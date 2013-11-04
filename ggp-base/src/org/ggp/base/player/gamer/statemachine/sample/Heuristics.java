package org.ggp.base.player.gamer.statemachine.sample;

import org.ggp.base.util.statemachine.*;

/**
 * Class containing the evaluation functions used by FabulousPlayer.
 * 
 * @author Nicolai
 *
 */
public class Heuristics {
	
	private final StateMachine theMachine;
	
	/**
	 * @param machine State machine of the game
	 */
	public Heuristics(StateMachine machine){
		theMachine = machine;
	}
	
	/**
	 * Dummy evaluation function.
	 * 
	 * @param state Current state
	 * @param role Role to evaluate for
	 * @return Heuristic value
	 */
	public int evaluate_dummy(MachineState state, Role role){
		return Integer.MIN_VALUE;
	}
	
}
