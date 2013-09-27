package org.ggp.base.player.gamer.statemachine.sample;

import org.ggp.base.util.statemachine.Move;
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
	
	private static Move best;
	
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		//TODO: Implement
		return null;
	}
	
	@Override
	public void stateMachineMetaGame(long timeout){
		//TODO: Implement
	}
	
	@Override
	public void stateMachineStop(){
		//TODO: Implement something or remove
	}
	
}
