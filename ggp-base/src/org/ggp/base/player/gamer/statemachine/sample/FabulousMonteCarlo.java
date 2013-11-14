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
 * Fabulous Monte-Carlo Tree Search Player
 * 
 * @author Irme, Nicolai
 *
 */
final class FabulousMonteCarlo extends SampleGamer {
	
	private static final int MAX_SCORE = 100;
	
	private static final int MIN_SCORE = 0;
	
	private static final double C = 1.0;
	
	/**
	 * Class representing a node of the MCTS tree
	 */
	private abstract class Node{
		
	}
	
	/**
	 * Non-terminal node in the MCTS tree
	 */
	private class NonTerminalNode extends Node{
		
		final MachineState state;
		final List<List<Move>> legal;
		Map<int[], Node> successors;
		int n;
		int [][] n_action;
		double [][] q_action;
		
		/**
		 * @param state Game state
		 */
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
			q_action = new double[n_action.length][];
			for(int i = 0; i < n_action.length; i++) {
				n_action[i] = new int[legal.get(i).size()];	
				q_action[i] = new double[n_action[i].length];
				Arrays.fill(n_action[i], 0);
				Arrays.fill(q_action[i], 0);
			}
			successors = new HashMap<int[], Node>();
		}
	}
	
	/**
	 * Terminal node in the MCTS tree
	 */
	private class TerminalNode extends Node{
		
		final int[] goal;
		
		/**
		 * @param state Game state
		 */
		protected TerminalNode(MachineState state){
			List<Integer> temp;
			 try {
				temp = theMachine.getGoals(state);
			} catch (GoalDefinitionException e) {
				System.err.println("No goals");
				temp = new ArrayList<Integer>();
			}
			 goal = new int[temp.size()];
			 for(Integer i : temp){
				 goal[i] = temp.get(i);
			 }
		}
	}
	
	/**
	 * Thrown if a timeout occurs during search.
	 */
	private class TimeoutException extends Throwable{
	
		private static final long serialVersionUID = -5034297695141082527L;

		public TimeoutException(){
			super();
		}
	}
	
	private final TimeoutException timeoutException = new TimeoutException();
	
	private StateMachine theMachine;
	
	private NonTerminalNode root;
	
	private MachineState currentState;
	
	private int role;
	
	@Override
	public void setState(MachineState state){
		currentState = state;
	}
	
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		timeout -= 500;
		return mcts(timeout);
	}
	
	@Override
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		theMachine = getStateMachine();
		root = new NonTerminalNode(currentState);
		role = theMachine.getRoleIndices().get(getRole());
		timeout -= 500;
		mcts(timeout);
	
	}	
	
	/**
	 * Performs Monte-Carlo tree search.
	 * 
	 * @param timeout Time limit
	 * @return Best move in the root state
	 */
	private Move mcts(long timeout){
		Move bestMove = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		while(System.currentTimeMillis() < timeout){
			int next;
			try {
				next = mctsStep(timeout);
			} catch (TimeoutException e) {
				break;
			}
			double score = root.q_action[role][next] - uct(root.n, root.n_action[role][next]);
			if(score > bestScore){
				bestScore = score;
				bestMove = root.legal.get(role).get(next);
			}
			
		}
		
		return bestMove;
	}
	
	/**
	 * Performs one MCTS step.
	 * 
	 * @param timeout Time limit
	 * @return Index of the move performed by the player in the root state
	 * @throws TimeoutException Time limit reached
	 */
	private int mctsStep(long timeout) throws TimeoutException{
		List<Node> path = new ArrayList<Node>();
		int[] scores;
		Node current = root;
		NonTerminalNode c;
		int rootMove = -1;
		while(current != null){
			path.add(current);
			if(current instanceof TerminalNode){
				scores = ((TerminalNode)current).goal;
				break;
			}
			c = (NonTerminalNode)current;
			int[] select = new int[theMachine.getRoles().size()];
			for(int p = 0; p < theMachine.getRoles().size(); p++){
				List<Move> moves = c.legal.get(p);
				double bestval = Double.NEGATIVE_INFINITY;
				int bestmove = -1;
				for(int m = 0; m < moves.size(); m++){
					double value = c.q_action[p][m] + uct(c.n, c.n_action[p][m]);
					if(value > bestval){
						bestval = value;
						bestmove = m;
					}
				}
				select[p] = bestmove;
				//Continue here.
			}
		}
		
		return rootMove;
	}
	
	/**
	 * Recursively performs Monte-Carlo simulation.
	 * 
	 * @param node Node to start at
	 * @param timeout Time limit
	 * @return Scores for all players
	 */
	private int[] playout(Node node, long timeout){
		return null;
	}
	
	/**
	 * Calculates UCT for MCTS.
	 * 
	 * @param n Total number of simulations
	 * @param na Number of simulations for this action
	 * @return Confidence bound
	 */
	public static double uct(int n, int na){
		return C * Math.sqrt(Math.log(n) / na);
	}
	
}
