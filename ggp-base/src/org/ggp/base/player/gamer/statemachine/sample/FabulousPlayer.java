package org.ggp.base.player.gamer.statemachine.sample;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

/**
 * The fabulous player.
 * 
 * @author Nera, Nicolai, Irme
 *
 */
public final class FabulousPlayer extends SampleGamer {
	
	/*{
		System.out.println("Free: " + Runtime.getRuntime().freeMemory() + "\nTotal: " + Runtime.getRuntime().totalMemory() + "\nMax: " + Runtime.getRuntime().maxMemory());
	}*/
	
	//private final SampleGamer singlePlayer = new FabulousSinglePlayer2();
	
	//private final SampleGamer multiPlayer = new FabulousMultiPlayer2();
	
	//private final SampleGamer montecarlo = new FabulousMonteCarlo();
	
	private PlayerThread currentPlayer;
	
	private StateMachine theMachine;
	
	@Override
	public void stateMachineMetaGame(long timeout){
		long total = System.currentTimeMillis();
		System.out.println();
		theMachine = getStateMachine();
		int roles = theMachine.getRoles().size();
		SampleGamer p;
		if(roles == 1){
			p = new FabulousMonteCarlo();
		}
		else{
			if (theMachine.getRoleIndices().get(getRole()) == 0){
				p = new FabulousMonteCarlo();
			}
			else{
				p = new FabulousMonteCarlo();
			}
		}
		p.setMatch(getMatch());
		p.setMachine(theMachine);
		p.setRole(getRole());
		currentPlayer = new PlayerThread(p);
		currentPlayer.setState(theMachine.getInitialState());
		currentPlayer.setMetaGame(true);
		currentPlayer.setTimeout(timeout);
		Thread t = new Thread(currentPlayer);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			System.err.println("Thread interrupted.");
		}
		total = System.currentTimeMillis() - total;
		System.out.println("Completed metagaming in " + total + "ms.");
	}
	
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		long total = System.currentTimeMillis();
		currentPlayer.setState(getCurrentState());
		currentPlayer.setMetaGame(false);
		currentPlayer.setTimeout(timeout);
		Thread t = new Thread(currentPlayer);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			System.err.println("Thread interrupted.");
		}
		Move move = currentPlayer.getResult();
		total = System.currentTimeMillis() - total;
		System.out.println("Selected move in " + total + "ms.");
		return move;
	}

	@Override
	public void setState(MachineState state) {
		
	}

	@Override
	public void setMachine(StateMachine m) {
		theMachine = m;
	}
	
}
