package org.ggp.base.player.gamer.statemachine.sample;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

/**
 * The Fabulous Player.
 * 
 * @author Nicolai
 *
 */
public final class FabulousPlayer extends SampleGamer {
	
	private static final int MAX_SCORE = 100;
	
	private static final int MIN_SCORE = 0;
	
	private boolean useHash = true;
	
	private Deque<Move> best;
	
	private Deque<Move> current;
	
	private int bestScore;
	
	private int nodeCount;
	
	private Map<MachineState, Integer> seen;
	
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		if(best == null || best.isEmpty()){
			System.out.println("No best solution, performing random move.");
			return getStateMachine().getRandomMove(getCurrentState(), getRole());
		}
		return best.removeFirst();
	}
	
	@Override
	public void stateMachineMetaGame(long timeout){
		best = null;
		bestScore = MIN_SCORE - 1;
		current = new ArrayDeque<Move>();
		timeout -= 200;
		int depth = 4;
		//long now = System.currentTimeMillis();
		//long estimate;
		StateMachine theMachine = getStateMachine();
		seen = new HashMap<MachineState, Integer>();
		nodeCount = 0;
		while(! search(theMachine, theMachine.getInitialState(), depth, timeout)){
			//estimate = 2 * (System.currentTimeMillis() - now);
			//now = System.currentTimeMillis();
			if(System.currentTimeMillis() > timeout){
				break;
			}
			depth++;
			seen = new HashMap<MachineState, Integer>();
			nodeCount = 0;
		}
	}
	
	/**
	 * Limited depth first search.
	 * 
	 * @param theMachine State Machine of the game
	 * @param state State to start the search from
	 * @param depth Depth limit
	 * @param timeout Time limit
	 * @return True if further search is pointless
	 */
	private boolean search(StateMachine theMachine, MachineState state, int depth, long timeout){
		if(++nodeCount > 500){
			if(System.currentTimeMillis() > timeout){
				return true;
			}
			nodeCount = 0;
		}
		if(useHash && hashCheck(state, depth)){
			return false;
		}
		if(theMachine.isTerminal(state)){
			int score = MIN_SCORE - 1;
			try {
				score = theMachine.getGoal(state, getRole());
			} catch (GoalDefinitionException e) {
				System.err.println("Bad goal description!");
				return true;
			}
			if(score > bestScore){
				best = new ArrayDeque<Move>();
				best.addAll(current);
			}
			return (score == MAX_SCORE);
		}
		if(depth == 0){
			return false;
		}
		List<Move> moves;
		try{
			moves = theMachine.getLegalMoves(state, getRole());
		} catch (MoveDefinitionException e){
			System.err.println("No legal moves!");
			return true;
		}
		for(Move m : moves){
			current.addLast(m);
			List<Move> list = new LinkedList<Move>();
			list.add(m);
			try {
				if(search(theMachine, theMachine.getNextState(state, list), depth - 1, timeout)){
					return true;
				}
			} catch (TransitionDefinitionException e) {
				System.err.println("Something went horribly wrong!");
				return true;
			}
			current.removeLast();
		}
		return false;
	}
	
	/**
	 * Check if a state has been seen further up in the tree and adds it if not.
	 * 
	 * @param state MachineState
	 * @param depth Depth value in the game tree (counting backwards)
	 * @return True if the state was already added in a more favorable position
	 */
	private boolean hashCheck(MachineState state, int depth){
		if(seen.containsKey(state) && seen.get(state) >= depth){
			return true;
		}
		seen.put(state, depth);
		return false;
	}
	
}
