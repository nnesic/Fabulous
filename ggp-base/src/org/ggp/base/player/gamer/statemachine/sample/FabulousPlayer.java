package org.ggp.base.player.gamer.statemachine.sample;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

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
	
	private Stack<Move> best;
	
	private Stack<Move> current;
	
	private int bestScore;
	
	private int nodeCount;
	
	private Set<MachineState> seen;
	
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		if(best == null || best.isEmpty()){
			System.err.println("No best solution, performing random move.");
			return getStateMachine().getRandomMove(getCurrentState(), getRole());
		}
		return best.pop();
	}
	
	@Override
	public void stateMachineMetaGame(long timeout){
		best = null;
		bestScore = -1;
		current = new Stack<Move>();
		timeout -= 200;
		int depth = 4;
		//long now = System.currentTimeMillis();
		//long estimate;
		StateMachine theMachine = getStateMachine();
		seen = new HashSet<MachineState>();
		nodeCount = 0;
		while(! search(theMachine, theMachine.getInitialState(), depth, timeout)){
			//estimate = 2 * (System.currentTimeMillis() - now);
			//now = System.currentTimeMillis();
			if(System.currentTimeMillis() > timeout){
				break;
			}
			depth++;
			seen = new HashSet<MachineState>();
			nodeCount = 0;
		}
		if(best == null){
			return;
		}
		Stack<Move> copy = new Stack<Move>();
		while(! best.isEmpty()){
			copy.push(best.pop());
		}
		best = copy;
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
		if(seen.contains(state)){
			return false;
		}
		seen.add(state);
		if(theMachine.isTerminal(state)){
			int score = -1;
			try {
				score = theMachine.getGoal(state, getRole());
			} catch (GoalDefinitionException e) {
				System.err.println("Bad goal description!");
				return true;
			}
			if(score > bestScore){
				best = new Stack<Move>();
				best.addAll(current);
			}
			return (score == 100);
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
			current.push(m);
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
			current.pop();
		}
		return false;
	}
	
	@Override
	public void stateMachineStop(){
		//TODO: Implement something or remove
	}
	
}
