package org.ggp.base.player.gamer.statemachine.sample;

import org.ggp.base.util.statemachine.Move;

/**
 * Search result for FabulousPlayers
 * 
 * @author Nicolai
 *
 */
public class Result {
	
	protected final Move move;
	
	protected final int confidence;
	
	/**
	 * Null result constructor.
	 */
	protected Result(){
		move = null;
		confidence = 0;
	}
	
	/**
	 * Real constructor for actual results.
	 * 
	 * @param move Best move
	 * @param confidence Confidence in quality of the move (0 - 100)
	 */
	protected Result(Move move, int confidence){
		this.move = move;
		this.confidence = confidence;
	}
	
}
