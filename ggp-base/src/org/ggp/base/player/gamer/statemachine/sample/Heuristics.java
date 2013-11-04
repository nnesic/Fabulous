package org.ggp.base.player.gamer.statemachine.sample;

import java.util.Map;

import org.ggp.base.util.statemachine.*;

/**
 * Class containing the evaluation functions used by FabulousPlayer.
 * 
 * @author Nicolai, Irme
 *
 */
public class Heuristics {
	
	private static final int MAX_HEURISTIC = 99;
	
	private static final int MIN_HEURISTIC = 1;
	
	private final StateMachine theMachine;
	
	/**
	 * @param machine State machine of the game
	 */
	public Heuristics(StateMachine machine){
		theMachine = machine;
	}
	
	/**
	 * Inverts the result of an evaluation function.
	 * 
	 * @param value Evaluation function output
	 * @return Inverted heuristic value
	 */
	public int inverse(int value){
		return MIN_HEURISTIC + MAX_HEURISTIC - value;
	}
	
	/**
	 * Dummy evaluation function.
	 * 
	 * @param state Current state
	 * @param role Role to evaluate for
	 * @return Heuristic value
	 */
	public int evaluate_dummy(){
		return (MAX_HEURISTIC + MIN_HEURISTIC) / 2;
	}
	
	private int maxMoves = 1;
	
	/**
	 * Evaluation function using the mobility metric.
	 * 
	 * @param moves Number of available moves
	 * @return Heuristic value
	 */
	public int evaluate_mobility(int moves){
		if(moves > maxMoves){
			maxMoves = moves;
			return MAX_HEURISTIC;
		}
		moves *= (MAX_HEURISTIC - MIN_HEURISTIC);
		moves /= maxMoves;
		return MIN_HEURISTIC + moves;
	}
	
	/**
	 * Evaluation function using the novelty metric.
	 * Basic implementation, only checks for exact matches.
	 * 
	 * @param state Current state
	 * @param transposition Transposition table
	 * @return Heuristic value
	 */
	public int evaluate_novelty(MachineState state,  Map<MachineState, ?> transposition ){
		if(transposition.containsKey(state)){
			return MIN_HEURISTIC;
		}
		else{
			return MAX_HEURISTIC;
		}
	}
	
}
