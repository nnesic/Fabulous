package org.ggp.base.player.gamer.statemachine.sample;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.AbstractReferenceMap.ReferenceStrength;
import org.apache.commons.collections4.map.ReferenceMap;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

/**
 * Fabulous Singleplayer.
 * 
 * @author Nicolai, Nera, Irme
 *
 */
final class FabulousSinglePlayer2 extends SampleGamer {
	
	private enum SearchResult{
		LIMIT, TERMINAL, DONE, ERROR, TIMEOUT
	}
	
	private static final int MAX_SCORE = 100;
	
	private static final int MIN_SCORE = 0;
	
	private static final ReferenceStrength soft = AbstractReferenceMap.ReferenceStrength.SOFT;
	
	private Deque<Move> best;
	
	private Deque<Move> current;
	
	private int bestScore;
	
	private ReferenceMap<MachineState, Integer> seen;
	
	private ReferenceMap<MachineState, Boolean> completed;
	
	private Heuristics heuristic;
	
	private boolean useHeuristic = true;
	
	private MachineState currentState;
	
	private StateMachine theMachine;
	
	private boolean foundSolution = false;
	
	@Override
	public void setState(MachineState state){
		currentState = state;
	}
	
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		if (!foundSolution){
		//if(best == null || best.isEmpty()){
			//System.out.println("No best solution, performing random move.");
			upperSearch(theMachine, currentState, timeout);
			//System.out.println("searching again");
			//return getStateMachine().getRandomMove(getCurrentState(), getRole());
		}
		if(best == null || best.isEmpty()){
			System.out.println("playing random");
			return getStateMachine().getRandomMove(currentState, getRole());
		}
		return best.removeFirst();
	}
	
	@Override
	public void stateMachineMetaGame(long timeout){
		best = null;
		bestScore = MIN_SCORE - 1;
		foundSolution = false;
		completed = new ReferenceMap<MachineState, Boolean>(soft, soft);
		seen = new ReferenceMap<MachineState, Integer>(soft, soft);
		theMachine = getStateMachine();
		heuristic = new Heuristics( theMachine);
		upperSearch(theMachine, theMachine.getInitialState(), timeout);
	}
	
	/**
	 * Performs iterative deepening search.
	 * 
	 * @param theMachine State machine of the game
	 * @param state Initial state
	 * @param timeout Time limit
	 */
	private void upperSearch(StateMachine theMachine, MachineState state, long timeout){
		best=null;
		current = new ArrayDeque<Move>();
		bestScore = MIN_SCORE - 1;
		timeout -= 1000;
		int depth = 1;
		SearchResult result = search(theMachine, state, depth, timeout);
		while(result == SearchResult.LIMIT || result == SearchResult.TERMINAL){
			//estimate = 2 * (System.currentTimeMillis() - now);
			//now = System.currentTimeMillis();
			if(System.currentTimeMillis() > timeout){
				break;
			}
			depth++;
			seen = new ReferenceMap<MachineState, Integer>(soft, soft);
			result = search(theMachine, state, depth, timeout);
		}
		if(result == SearchResult.ERROR){
			System.err.println("An error occured during search.");
		}
		else if(result == SearchResult.TIMEOUT){
			System.out.println("Search ran out of time.");
		}
		if(best == null || best.isEmpty()){
			System.out.println("Playing random moves.");
		}
		
	}
	
	/**
	 * Limited depth first search.
	 * 
	 * @param theMachine State Machine of the game
	 * @param state State to start the search from
	 * @param depth Depth limit
	 * @param timeout Time limit
	 * @return True if further search is pointless
	 */
	private SearchResult search(StateMachine theMachine, MachineState state, int depth, long timeout){
		if(System.currentTimeMillis() > timeout){
			return SearchResult.TIMEOUT;
		}
		if(theMachine.isTerminal(state)){
			int score = MIN_SCORE - 1;
			try {
				score = theMachine.getGoal(state, getRole());
			} catch (GoalDefinitionException e) {
				System.err.println("Bad goal description!");
				return SearchResult.ERROR;
			}
			if(score > bestScore){
				best = new ArrayDeque<Move>();
				best.addAll(current);
				bestScore = score;
			}
			if(score == MAX_SCORE){
				foundSolution = true;
				return SearchResult.DONE;
			}
			return SearchResult.TERMINAL;
		}
		List<Move> moves;
		try{
			moves = theMachine.getLegalMoves(state, getRole());
		} catch (MoveDefinitionException e){
			System.err.println("No legal moves!");
			return SearchResult.ERROR;
		}
		Map<MachineState, Move> next = new HashMap<MachineState, Move>();
		for(Move m : moves){
			List<Move> list = new LinkedList<Move>();
			list.add(m);
			MachineState s;
			try {
				s = theMachine.getNextState(state, list);
			} catch (TransitionDefinitionException e) {
				System.err.println("Something went horribly wrong!");
				return SearchResult.ERROR;
			}
			if(!(hashCheck(s, depth - 1) || (completed.keySet().contains(s) && completed.get(s)))){
				next.put(s, m);
			}
		}
		if(depth == 0){
			if(useHeuristic){
				int score = heuristic.evaluate_combinedSingle(moves, state, seen);
				if(score > bestScore){
					best = new ArrayDeque<Move>();
					best.addAll(current);
					bestScore = score;
				}
			}
			return SearchResult.LIMIT;
		}
		boolean done = true;
		for(MachineState s : next.keySet()){
			current.addLast(next.get(s));
			SearchResult ret = search(theMachine, s, depth - 1, timeout);
			if(ret != SearchResult.TERMINAL){
				if(ret != SearchResult.LIMIT){
					return ret;
				}
				done = false;
			}
			current.removeLast();
		}
		addTable(state, done);
		if(done){
			return SearchResult.TERMINAL;
		} 
		return SearchResult.LIMIT;
	}
	
	/**
	 * Check if a state has been seen further up in the tree and adds it if not.
	 * 
	 * @param state MachineState
	 * @param depth Depth value in the game tree (counting backwards)
	 * @return True if the state was already added in a more favorable position
	 */
	private boolean hashCheck(MachineState state, int depth){
		if(seen.containsKey(state) && seen.get(state) >= depth){
			return true;
		}
		seen.put(state, depth);
		return false;
	}
	
	/**
	 * 
	 * @param state MachineState
	 * @param done whether the search is done or not.
	 */
	private void addTable(MachineState state, boolean done){
		if(!completed.containsKey(state)){
			completed.put(state, done);
			
		}else if (done){
			completed.put(state, done);
		}
		
	}
	
}
