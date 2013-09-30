package org.ggp.base.player.gamer.statemachine.sample;

import java.util.LinkedList;
import java.util.List;
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
	
	private Stack<Move> best = null;
	
	private Stack<Move> current = new Stack<Move>();
	
	private int bestScore = -1;
	
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		if(best == null || best.isEmpty()){
			System.err.println("No moves.");
			getStateMachine().getRandomMove(getCurrentState(), getRole());
		}
		return best.pop();
	}
	
	@Override
	public void stateMachineMetaGame(long timeout){
		timeout -= 500;
		int depth = 4;
		long now = System.currentTimeMillis();
		long estimate;
		StateMachine theMachine = getStateMachine();
		while(! search(theMachine, theMachine.getInitialState(), depth)){
			estimate = 2 * (System.currentTimeMillis() - now);
			now = System.currentTimeMillis();
			if(now + estimate > timeout){
				break;
			}
			depth++;
			//System.out.println("Searching with depth " + depth);
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
	 * @return True if further search is pointless
	 */
	private boolean search(StateMachine theMachine, MachineState state, int depth){
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
				if(search(theMachine, theMachine.getNextState(state, list), depth - 1)){
					return true;
				}
			} catch (TransitionDefinitionException e) {
				System.err.println("Something went horribly wrong!");
				System.exit(-1);
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
