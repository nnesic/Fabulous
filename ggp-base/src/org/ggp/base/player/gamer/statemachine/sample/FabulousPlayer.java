package org.ggp.base.player.gamer.statemachine.sample;

import org.ggp.base.player.gamer.exception.MetaGamingException;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

/**
 * The fabulous player.
 * 
 * @author Nera, Nicolai
 *
 */
public final class FabulousPlayer extends SampleGamer {
	
	private SampleGamer player;
	
	@Override
	public void stateMachineMetaGame(long timeout){
		long total = System.currentTimeMillis();
		System.out.println();
		StateMachine m = getStateMachine();
		int roles = m.getRoles().size();
		if (roles == 1){
			player = new FabulousSinglePlayer();
		}
		else{
			player = new FabulousMultiPlayer();
		}
		player.setMatch(this.getMatch());
		player.setRoleName(this.getRoleName());
		player.setState(m.getInitialState());
		try {
			player.metaGame(timeout);
		} catch (MetaGamingException e) {
			System.err.println("Metagaming failed!");
		}
		total = System.currentTimeMillis() - total;
		System.out.println("Completed metagaming in " + total + "ms.");
	}
	
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		long total = System.currentTimeMillis();
		player.setState(getCurrentState());
		Move move = player.stateMachineSelectMove(timeout);
		total = System.currentTimeMillis() - total;
		System.out.println("Selected move in " + total + "ms.");
		return move;
	}
	
}
