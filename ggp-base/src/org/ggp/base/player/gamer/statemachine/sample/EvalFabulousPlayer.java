package org.ggp.base.player.gamer.statemachine.sample;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

/**
 * The fabulous player.
 * 
 * @author Nera, Nicolai, Irme
 *
 */
public final class EvalFabulousPlayer extends SampleGamer {
	
	private static final int MIN_SCORE = 0;
	
	//private static final int MAX_SCORE = 100;
	
	/*{
		System.out.println("Free: " + Runtime.getRuntime().freeMemory() + "\nTotal: " + Runtime.getRuntime().totalMemory() + "\nMax: " + Runtime.getRuntime().maxMemory());
	}*/
	
	//private final SampleGamer singlePlayer = new FabulousSinglePlayer2();
	
	//private final SampleGamer multiPlayer = new FabulousMultiPlayer2();
	
	//private final SampleGamer montecarlo = new FabulousMonteCarlo();
	
	private List<PlayerThread> currentPlayers;
	
	private EvaluationProverStateMachine theMachine;
	
	
	public ArrayList<MachineState> generateRandomStates (int n){
		LinkedList<MachineState> temp = new LinkedList<MachineState>();
		ArrayList<MachineState> ret = new ArrayList<MachineState>();
		MachineState init = theMachine.getInitialState();
		temp.add(init);
		ret.add(init);
		MachineState current = init;
		while (ret.size() < n){



			if (theMachine.isTerminal(current)){
				System.out.println("terminal");
				current = theMachine.getInitialState();
				continue;
			}
			else {
				try {
					current = theMachine.getRandomNextState(current);
					theMachine.getRandomNextState(current);
					if (! ret.contains(current)){
						ret.add(current);
					}
				} catch (MoveDefinitionException e) {				
					e.printStackTrace();
					current = theMachine.getInitialState();
				} catch (TransitionDefinitionException e) {			
					e.printStackTrace();
					current = theMachine.getInitialState();
				}
			}
		}
		return ret;
	}

	
	@Override
	public void stateMachineMetaGame(long timeout){
		long total = System.currentTimeMillis();
		System.out.println();
		theMachine = (EvaluationProverStateMachine) getStateMachine();
		int roles = theMachine.getRoles().size();
		currentPlayers = new ArrayList<PlayerThread>();
		List<SampleGamer> players = new ArrayList<SampleGamer>();
		/*
		
		ArrayList<MachineState> randomStates = generateRandomStates(10);
		ArrayList<MachineState> randomStates2 = generateRandomStates(10);
		theMachine.reorder(randomStates, theMachine);
		theMachine.checkCorrect(randomStates2, theMachine);
		*/
		if(roles == 1){
			players.add(new FabulousSinglePlayer2());
		}
		else{
			players.add(new FabulousMultiPlayer2());
			players.add(new FabulousMonteCarlo());
		}
		for(SampleGamer p : players){
			p.setMatch(getMatch());
			p.setMachine(theMachine);
			p.setRole(getRole());
			PlayerThread pThread = new PlayerThread(p);
			currentPlayers.add(pThread);
		}
		List<Thread> active = new ArrayList<Thread>();
		for(PlayerThread p : currentPlayers){
			p.setState(theMachine.getInitialState());
			p.setMetaGame(true);
			p.setTimeout(timeout);
			Thread t = new Thread(p);
			t.start();
			active.add(t);
		}
		timeout -= 250;
		try{
			for(Thread t : active){
				t.join(timeout - System.currentTimeMillis());
			}
		} catch(InterruptedException e){
			System.err.println("Interrupted while waiter for player threads.");
		}
		total = System.currentTimeMillis() - total;
		System.out.println("Completed metagaming in " + total + "ms.");
	}
	
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		long total = System.currentTimeMillis();
		List<Thread> active = new ArrayList<Thread>();
		for(PlayerThread p : currentPlayers){
			p.setState(getCurrentState());
			p.setMetaGame(false);
			p.setTimeout(timeout);
			Thread t = new Thread(p);
			t.start();
			active.add(t);
		}
		timeout -= 250;
		try{
			for(Thread t : active){
				t.join(timeout - System.currentTimeMillis());
			}
		} catch(InterruptedException e){
			System.err.println("Interrupted while waiter for player threads.");
		}
		Move best = null;
		int confidence = MIN_SCORE - 1;
		PlayerThread winner = null;
		for(PlayerThread p : currentPlayers){
			Result r = p.getResult();
			if(r.confidence > confidence){
				confidence = r.confidence;
				best = r.move;
				winner = p;
			}
		}
		if(winner == null){
			System.out.println("Player random move.");
			best = theMachine.getRandomMove(getCurrentState(), getRole());
		}
		else{
			System.out.println("Decision by " + winner.getName() + ". (Confidence " + confidence + ")");
		}
		total = System.currentTimeMillis() - total;
		System.out.println("Selected move in " + total + "ms.");
		return best;
	}

	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedStateMachine(new EvaluationProverStateMachine());
	} 
	
	@Override
	public void setState(MachineState state) {
		System.err.println("You should not be calling this.");
	}

	@Override
	public void setMachine(StateMachine m) {
		System.err.println("You should not be calling this.");
		theMachine = (EvaluationProverStateMachine)m;
	}

	@Override
	public int getConfidence() {
		System.err.println("You should not be calling this.");
		return 0;
	}
	
}
