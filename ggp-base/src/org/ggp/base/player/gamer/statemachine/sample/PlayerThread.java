package org.ggp.base.player.gamer.statemachine.sample;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

/**
 * Encapsulates a player for running in a thread.
 * 
 * @author Irme, Nicolai
 *
 */
public class PlayerThread implements Runnable {
	
	private SampleGamer player;
	
	private boolean metagame;
	
	private long timeout;
	
	private Result result = new Result();
	
	/**
	 * @param s Instantiated Player
	 */
	public PlayerThread(SampleGamer s){
		player = s;
	}
	
	/**
	 * Pass the current game state.
	 * 
	 * @param s MachineState
	 */
	public void setState(MachineState s){
		player.setState(s);
	}
	
	/**
	 * Select whether to run stateMachineMetagame or stateMachineSelectMove in the player class.
	 * 
	 * @param m True for metagame, false for play phase
	 */
	public void setMetaGame(boolean m){
		metagame = m;

	}
	
	/**
	 * Set up the time limit for the next action.
	 * 
	 * @param t Time limit
	 */
	public void setTimeout(long t){
		timeout = t;
	}
	
	@Override
	public void run(){
		try {
			if(metagame){
				result = new Result();
				player.stateMachineMetaGame(timeout);
			} else{
				result = new Result();
				result = new Result(player.stateMachineSelectMove(timeout), player.getConfidence());
			} 
		} catch (TransitionDefinitionException e) {
			System.err.println("Could not compute state update.");
		} catch (MoveDefinitionException e) {
			System.err.println("Could not compute legal moves.");
		} catch (GoalDefinitionException e) {
			System.err.println("Could not find goal values.");
		}
	}
	
	/**
	 * Retrieve the result of the last stateMachineSelectMove call.
	 * 
	 * @return Move
	 */
	public Result getResult(){
		return result;
	}
	
	/**
	 * @return Player's class name
	 */
	public String getName(){
		return player.getClass().getName();
	}
	
}
