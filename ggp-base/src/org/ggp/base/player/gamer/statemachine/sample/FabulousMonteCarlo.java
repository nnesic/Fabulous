package org.ggp.base.player.gamer.statemachine.sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ggp.base.util.statemachine.*;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

/**
 * 
 * @author Irme, Nicolai
 *
 */

final class FabulousMonteCarlo extends SampleGamer {
	
	private static final int MAX_SCORE = 100;
	
	private static final int MIN_SCORE = 0;
	
	private static final double C = 1.0;
	
	private abstract class Node{
		
	}
	
	private class NonTerminalNode extends Node{
		final MachineState state;
		final List<List<Move>> legal;
		Map<int[], Node> successors;
		int n;
		int [][] n_action;
		double [][] q_action;
		
		protected NonTerminalNode(MachineState state){
			this.state = state;
			List<List<Move>> temp;
			try {
				temp = theMachine.getLegalJointMoves(state);
			} catch (MoveDefinitionException e) {
				System.err.println("Couldn't compute legal moves.");
				temp = null;
			}
			legal = temp;
			n = 0;
			n_action = new int[theMachine.getRoles().size()][];
			q_action = new double [n_action.length][];
			for (int i = 0; i < n_action.length; i++) {
				n_action[i] = new int[legal.get(i).size()];	
				q_action[i] = new double [n_action[i].length];
				Arrays.fill(n_action[i], 0);
				Arrays.fill(q_action[i], 0);
			}
			
			successors = new HashMap<int [], Node>();
		}
		
	}
	private class TerminalNode extends Node{
		final int[] goal;
		protected TerminalNode(MachineState state){
			List <Integer> temp;
			 try {
				temp = theMachine.getGoals(state);
			} catch (GoalDefinitionException e) {
				System.err.println("No goals");
				temp = new ArrayList<Integer>();
			}
			 goal = new int [temp.size()];
			 for(Integer i : temp){
				 goal[i] = temp.get(i);
			 }
		}
		
		
	}
	
	/**
	 * Thrown if a timeout occurs during search.
	 */
	private class TimeoutException extends Throwable {;
	
		private static final long serialVersionUID = -5034297695141082527L;

		public TimeoutException(){
			super();
		}
	}

	private final TimeoutException timeoutException = new TimeoutException();
	
	private StateMachine theMachine;
	
	private Node root;
	
	private MachineState currentState;
	
	private int role;
	
	@Override
	public void setState(MachineState state){
		currentState = state;
	}

	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		
		timeout -= 500;
		
		
		return mcts(timeout);
	}
	
	
	@Override
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		theMachine = getStateMachine();
		root = new NonTerminalNode(currentState);
		role = theMachine.getRoleIndices().get(getRole());
		timeout -= 500;
		
	}	
	
	/**
	 * 
	 * @param timeout time limit.
	 * @return the best move
	 */
	private Move mcts(long timeout){
		Move bestMove = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		
		
		return null;
	}
	
	private void mctsStep(long timeout) throws TimeoutException{
		
		
	}
	
	/**
	 * Calculates UCT for MCTS
	 * @param n total number of simulations
	 * @param na number of simulations for this action
	 * @return confidence bound
	 */
	public static double uct(int n, int na){
		return C * Math.sqrt(Math.log(n)/na);
	}

}
