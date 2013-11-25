package org.ggp.base.player.gamer.statemachine.sample;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class PlayerThread extends Thread {

	private SampleGamer player;

	private boolean metagame;
	private long timeout;

	private Move result = null;

	public PlayerThread(SampleGamer s){
		player = s;


	}
	
	public void setState(MachineState s){
		player.setState(s);
	}
	
	public void setMetaGame(boolean m){
		metagame = m;

	}

	public void setTimeout(long t){
		timeout = t;

	}

	@Override
	public void run(){
		try {
			if(metagame){
				result = null;
				player.stateMachineMetaGame(timeout);
			} else{
				result = null;
				result = player.stateMachineSelectMove(timeout);
			} 
		} catch (TransitionDefinitionException e) {
			System.err.println("Could not compute state update.");
		} catch (MoveDefinitionException e) {
			System.err.println("Could not compute legal moves.");
		} catch (GoalDefinitionException e) {
			System.err.println("Could not find goal values.");
		}
	}
	
	public Move getResult(){
		return result;
	}
	


}
