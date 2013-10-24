package org.ggp.base.player.gamer.statemachine.sample;

import org.ggp.base.player.gamer.exception.MetaGamingException;
import org.ggp.base.util.statemachine.MachineState;
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
		System.out.println(roles);
		System.out.println("OHAI");
	}
	
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		/*
		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
		Move selection = moves.get(0);
		*/
		
		//notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
		player.setState(getCurrentState());
		return player.stateMachineSelectMove(timeout);
	}
	
}
