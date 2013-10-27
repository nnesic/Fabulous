package org.ggp.base.player.gamer.statemachine.sample;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.commons.collections4.map.AbstractReferenceMap.ReferenceStrength;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

/**
 * Fabulous Multiplayer
 * 
 * @author Nera, Nicolai
 *
 */
final class FabulousMultiPlayer2 extends SampleGamer {

	private static final int MAX_SCORE = 100;

	private static final int MIN_SCORE = 0;
	
	private static final int TIME_MULT = 1;
	
	private static final int TIME_DIV = 4;

	private static final ReferenceStrength SOFT = AbstractReferenceMap.ReferenceStrength.SOFT;

	/**
	 * Minimax internal node return value.
	 * Holds a score and information about completeness of exploration.
	 */
	private class Tuple {
		protected int score;
		protected boolean complete;
		protected boolean pruned;

		protected Tuple (int score, boolean complete, boolean pruned){
			this.complete = complete;
			this.score = score;
			this.pruned = pruned;
		}
	}

	/**
	 * Thrown if a timeout occurs during search.
	 */
	private class TimeoutException extends Throwable {
		private static final long serialVersionUID = 7485356568086889532L;

		public TimeoutException(){
			super();
		}
	}

	private final TimeoutException timeoutException = new TimeoutException();

	private ReferenceMap<MachineState, Tuple> transposition;

	private Role role;

	private StateMachine theMachine;

	private MachineState currentState;
	
	private long timeout;
	
	private long turnpoint;
	
	private boolean prune;

	@Override
	public void setState(MachineState state){
		currentState = state;
	}

	@Override
	public void stateMachineMetaGame(long timeout){
		timeout -= 500;
		this.timeout = timeout;
		this.turnpoint = ((TIME_DIV - TIME_MULT) * System.currentTimeMillis() + TIME_MULT * timeout) / TIME_DIV;
		theMachine = getStateMachine();
		role = getRole();
		transposition = new ReferenceMap<MachineState, Tuple>(SOFT, SOFT);
		prune = false;
		minimax(currentState);
	}

	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		timeout -= 500;
		this.timeout = timeout;
		this.turnpoint = ((TIME_DIV - TIME_MULT) * System.currentTimeMillis() + TIME_MULT * timeout) / TIME_DIV;
		prune = false;
		Move move = minimax(currentState);
		if(move != null){
			return move;
		}
		System.out.println("Playing random move.");
		return theMachine.getRandomMove(currentState, role);
	}

	/**
	 * Performs exhaustive minimax search in a state.
	 * 
	 * @param state Game state
	 * @param timeout Time limit
	 * @return Ideal move
	 */
	private Move minimax(MachineState state){
		List<Move> moves;
		try {
			moves = theMachine.getLegalMoves(state, role);
		} catch (MoveDefinitionException e) {
			System.err.println("No legal moves!");
			return null;
		}
		boolean notDone = true;
		int depth = 1;	//Change to something high for testing with output!
		int bestScore = MIN_SCORE - 1;
		Move bestMove =  null;
		Search:
			while(notDone){
				if(System.currentTimeMillis() > timeout){
					System.out.println("Ran out of time!");
					break;
				}
				depth++;
				if(System.currentTimeMillis() > turnpoint){
					prune = true;
				}
				int alpha = MIN_SCORE - 1;
				int beta = MAX_SCORE + 1;
				notDone = false;
				for (Move move: moves){
					Tuple tempScore = new Tuple(bestScore, false, false);
					try {
						tempScore = minPlayer (state, move, depth, alpha, beta);
					} catch (TimeoutException e){
						System.out.println("Ran out of time!");
						break Search;
					}
					if(!tempScore.complete){
						notDone = true;
					}
					else{
						if(tempScore.score > bestScore){
							bestScore = tempScore.score;
							bestMove = move;
						}
						if(prune && tempScore.score > alpha){
							alpha = tempScore.score;
						}
					}
				}
			}
		//System.out.println("Done. Depth: " + depth);
		return bestMove;
	}

	/**
	 * Recursively performs minimax search (max-player move)
	 * 
	 * @param state Game state
	 * @param depth Depth limit
	 * @param timeout Time limit
	 * @param alpha Alpha value
	 * @param beta Beta value
	 * @return Best score
	 * @throws TimeoutException Time limit exceeded
	 */
	private Tuple maxPlayer(MachineState state, int depth, int alpha, int beta) throws TimeoutException{
		if(System.currentTimeMillis() > timeout){
			throw timeoutException;
		}
		if(theMachine.isTerminal(state)){
			Tuple ret;
			try {
				ret = new Tuple(theMachine.getGoal(state, role), true, false);
			} catch (GoalDefinitionException e) {
				System.err.println("Bad goal description!");
				ret = new Tuple(Integer.MIN_VALUE, false, false);
			}
			//System.out.println("Found a goal of value " + ret.score);
			transposition.put(state, ret);
			return ret;
		}
		if (transposition.containsKey(state) && transposition.get(state).complete){
			return transposition.get(state);
		}
		if(depth == 0){
			return new Tuple(Integer.MIN_VALUE, false, false);
		}

		if(! prune){
			alpha = MIN_SCORE - 1;
		}
		//int bestScore = MIN_SCORE - 1;
		//Move bestMove = null;
		//boolean pruned = false;
		boolean complete = true;
		boolean foundOne = false;
		boolean pruned = false;
		List<Move> moves;
		try {
			moves = theMachine.getLegalMoves(state, role);
		} catch (MoveDefinitionException e) {
			System.err.println("No legal moves!");
			return new Tuple(Integer.MIN_VALUE, false, false);
		}
		for(Move move : moves){
			Tuple s = minPlayer(state, move, depth, alpha, beta);
			if(!s.complete){
				complete = false;
				//return new Tuple (Integer.MIN_VALUE, false);
			}	
			if(s.score != Integer.MIN_VALUE){
				foundOne = true;
				/*
				if(s.score > bestScore){
					bestScore = s.score;
					//bestMove = move;
				}
				 */
				if(s.complete){
					if(s.score > alpha){
						alpha = s.score;
					}
					if(prune && alpha >= beta){
						pruned = true;
						break;
					}
				}
			}

			if(s.pruned){
				pruned = true;
			}
		}

		if(! foundOne){
			alpha = Integer.MIN_VALUE;
		}
		Tuple ret = new Tuple(alpha, complete, pruned);
		if(complete && !pruned){
			transposition.put(state, ret);
		}
		return ret;
	}

	/**
	 * Recursively performs minimax search (min-player move)
	 * 
	 * @param state Game state
	 * @param move Max-player's move
	 * @param depth Depth limit
	 * @param timeout Time limit
	 * @param alpha Alpha value
	 * @param beta Beta value
	 * @return Worst score
	 * @throws TimeoutException Time limit exceeded
	 */
	private Tuple minPlayer(MachineState state, Move move, int depth, int alpha, int beta) throws TimeoutException{
		if( System.currentTimeMillis() > timeout){
			throw timeoutException;
		}

		List<List<Move>> options = new ArrayList<List<Move>>();
		List<Role> roles = theMachine.getRoles();
		int fabulous = 0;
		for(int i = 0; i < roles.size(); i++){
			Role player = roles.get(i);
			if(player.equals(role)){
				fabulous = i;
				continue;
			}
			List<Move> moves;
			try {
				moves = theMachine.getLegalMoves(state, player);
			} catch (MoveDefinitionException e) {
				System.err.println("No legal moves!");
				moves = new ArrayList<Move>();
			}
			options.add(moves);
		}
		Set<List<Move>> next = combinations(options);

		if(! prune){
			beta = MAX_SCORE + 1;
		}
		MachineState nextState;
		//int worstScore = MAX_SCORE + 1;
		boolean complete = true;
		boolean foundOne = false;
		boolean pruned = false;
		for(List<Move> moves : next){
			//System.out.println("Expanding " + moves.get(0).toString());
			moves.add(fabulous, move);
			try {
				nextState = theMachine.getNextState(state, moves);
			} catch (TransitionDefinitionException e) {
				System.err.println("Attempted bad moves!");
				complete = false;
				continue;
			}
			Tuple s = maxPlayer(nextState, depth - 1, alpha, beta);
			if(!s.complete){
				complete = false;
				//return new Tuple (Integer.MIN_VALUE, false);
			}
			if(s.score != Integer.MIN_VALUE){
				foundOne = true;
				/*
				if(s.score < worstScore){
					worstScore = s.score;
				}
				 */
				if(s.complete){
					if(s.score <= beta){
						beta = s.score;
					}
					if(prune && alpha >= beta){
						pruned = true;
						break;
					}
				}
			}

			if(s.pruned){
				pruned = true;
			}
		}

		if(!foundOne){
			beta = Integer.MIN_VALUE;
		}
		return new Tuple (beta, complete, pruned);
	}

	/**
	 * Creates all combinations of moves for opposing players.
	 * 
	 * @param moves List of all possible moves for all opposing player (in order)
	 * @return Set of combinations of one move per opposing player (maintains order)
	 */
	private Set<List<Move>> combinations(List<List<Move>> moves){
		Set<List<Move>> ret = new HashSet<List<Move>>();
		if(moves.size() == 0){
			return ret;
		}
		int num = 1;
		for(List<Move> l : moves){
			num *= l.size();
		}
		for(int i = 0; i < num; i++){
			int tmp = i;
			List<Move> combination = new ArrayList<Move>();
			for(int r = 0; r < moves.size(); r++){
				List<Move> l = moves.get(r);
				if(l.size() == 0){
					combination.add(null);
					continue;
				}
				combination.add(l.get(tmp % l.size()));
				tmp /= l.size();
			}
			ret.add(combination);
		}
		return ret;
	}

}



