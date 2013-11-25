package org.ggp.base.player.gamer.statemachine.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 * @author Nera, Nicolai, Irme
 *
 */
final class FabulousMultiPlayer2 extends SampleGamer {

	private static final int MAX_SCORE = 100;

	private static final int MIN_SCORE = 0;

	private static final int TIME_MULT = 1;

	private static final int TIME_DIV = 10;

	private static final ReferenceStrength SOFT = AbstractReferenceMap.ReferenceStrength.SOFT;

	
	/**
	 * Minimax internal node return value.
	 * Holds a score and information about completeness of exploration.
	 */
	private class Tuple {
		final int score;
		final boolean complete;
		final boolean pruned;
		final int alpha;
		final int beta;
		final Move move;
		//protected final List <Move> moves;

		protected Tuple(int score, boolean complete, boolean pruned, int alpha, int beta, Move move){
			this.complete = complete;
			this.score = score;
			this.pruned = pruned;
			this.alpha = alpha;
			this.beta = beta;
			this.move = move;
			//this.moves = moves;
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
	
	private ReferenceMap<MachineState, Object> seen;

	private ReferenceMap <MachineState, Map <Move, Tuple>> transpositionMin;

	private Role role;

	private StateMachine theMachine;

	private MachineState currentState;

	private long timeout;

	private long turnpoint;

	private boolean prune;
	
	private boolean useHeuristic = false;
	
	private Heuristics heuristic;
	
	@Override
	public void setMachine(StateMachine m) {
		theMachine = m;
	}
	
	@Override
	public void setState(MachineState state){
		currentState = state;
	}

	@Override
	public void stateMachineMetaGame(long timeout){
		timeout -= 1000;
		this.timeout = timeout;
		this.turnpoint = ((TIME_DIV - TIME_MULT) * System.currentTimeMillis() + TIME_MULT * timeout) / TIME_DIV;
		//theMachine = getStateMachine();
		role = getRole();
		transposition = new ReferenceMap<MachineState, Tuple>(SOFT, SOFT);
		transpositionMin = new ReferenceMap <MachineState, Map <Move, Tuple>> (SOFT, SOFT);
		heuristic = new Heuristics(theMachine);
		prune = false;
		useHeuristic = false;
		minimax(currentState);
		heuristic.computeWeights();
	}

	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		timeout -= 1000;
		this.timeout = timeout;
		this.turnpoint = 0;
		prune = false;
		useHeuristic = true;
		Move move = minimax(currentState);
		if(move != null){
			return move;
		}
		System.out.println("Playing random move.");
		return theMachine.getRandomMove(currentState, role);
	}

	/**
	 * Performs minimax search in a state.
	 * 
	 * @param state Game state
	 * @param heuristic If true, the combined heuristics function is used, if false, values are used to train the heuristic function
	 * @return Best move
	 */
	private Move minimax(MachineState state){
		seen = new ReferenceMap<MachineState, Object>(SOFT, SOFT);
		if(transposition.containsKey(state) && transposition.get(state).complete){
			Tuple lookup = transposition.get(state);
			if(!lookup.pruned || (lookup.alpha == MIN_SCORE - 1 && lookup.beta == MAX_SCORE + 1)){
				return lookup.move;
			}
		}
		
		List<Move> moves = new ArrayList<Move>();
		List<List<Move>> minMoves = new ArrayList<List<Move>>();
		int fabulous = 0;
		for(int i = 0; i < theMachine.getRoles().size(); i++){
			Role r = theMachine.getRoles().get(i);
			if(r.equals(role)){
				fabulous = i;
				try {
					moves = theMachine.getLegalMoves(state, role);
				} catch (MoveDefinitionException e) {
					System.err.println("No legal moves for player!");
					return null;
				}
				continue;
			}
			try {
				minMoves.add(theMachine.getLegalMoves(state, r));
			} catch (MoveDefinitionException e) {
				System.err.println("No legal moves for opponent!");
				minMoves.add(new ArrayList<Move>());
			}
		}
		
		boolean notDone = true;
		boolean pruned = false;
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
				pruned = false;
				for(Move move: moves){
					Tuple tempScore = new Tuple(bestScore, false, false, alpha, beta, move);
					try {
						tempScore = minPlayer (state, move, minMoves, fabulous, depth, alpha, beta);
					} catch (TimeoutException e){
						System.out.println("Ran out of time!");
						notDone = true;
						break Search;
					}
					if(!tempScore.complete){
						notDone = true;
					}
					if(tempScore.pruned){
						pruned = true;
					}
					if(tempScore.score != Integer.MIN_VALUE){
						if(tempScore.score > bestScore){
							bestScore = tempScore.score;
							bestMove = move;
						}
						if(tempScore.score > alpha){
							alpha = tempScore.score;
						}
					}
				}
			}
		if(bestScore != Integer.MIN_VALUE){
			if(! useHeuristic){
				heuristic.addValue(heuristic.evaluate_mobility(moves), heuristic.evaluate_novelty(state, seen), heuristic.evaluate_opponentMobility(minMoves), bestScore);
			}
			transposition.put(state, new Tuple(bestScore, ! notDone, pruned, MIN_SCORE - 1, MAX_SCORE + 1, bestMove));
		}
		else if(useHeuristic){
			int value = heuristic.evaluate_combined(moves, minMoves, state, seen);
			Tuple ret = new Tuple(value, false, false, MIN_SCORE - 1, MAX_SCORE + 1, null);
			transposition.put(state, ret);
		}
		//System.out.println("Done. Depth: " + depth);
		return bestMove;
	}

	/**
	 * Recursively performs minimax search (max-player move)
	 * 
	 * @param state Game state
	 * @param depth Depth limit
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
				ret = new Tuple(theMachine.getGoal(state, role), true, false, alpha, beta, null);
				transposition.put(state, ret);
			} catch (GoalDefinitionException e) {
				System.err.println("Bad goal description!");
				ret = new Tuple(Integer.MIN_VALUE, false, false, alpha, beta, null);
			}
			//System.out.println("Found a goal of value " + ret.score);
			return ret;
		}
		if(transposition.containsKey(state) && transposition.get(state).complete){
			Tuple lookup = transposition.get(state);
			/*if( lookup.pruned && (lookup.score  >= beta)){
				return lookup;
			}*/

			if(lookup.alpha <= alpha && lookup.beta >= beta){
				return lookup;
			}
		}
		
		List<Move> moves = new ArrayList<Move>();
		List<List<Move>> minMoves = new ArrayList<List<Move>>();
		int fabulous = 0;
		for(int i = 0; i < theMachine.getRoles().size(); i++){
			Role r = theMachine.getRoles().get(i);
			if(r.equals(role)){
				fabulous = i;
				try {
					moves = theMachine.getLegalMoves(state, role);
				} catch (MoveDefinitionException e) {
					System.err.println("No legal moves for player!");
					return new Tuple(Integer.MIN_VALUE, false, false, alpha, beta, null);
				}
				continue;
			}
			try {
				minMoves.add(theMachine.getLegalMoves(state, r));
			} catch (MoveDefinitionException e) {
				System.err.println("No legal moves for opponent!");
				minMoves.add(new ArrayList<Move>());
			}
		}
		
		if(depth == 0){
			if(useHeuristic){
				int value = heuristic.evaluate_combined(moves, minMoves, state, seen);
				Tuple ret = new Tuple(value, false, false, alpha, beta, null);
				transposition.put(state, ret);
				return ret;
			}
			return new Tuple(Integer.MIN_VALUE, false, false, alpha, beta, null);
		}

		int bestScore = MIN_SCORE - 1;
		//Move bestMove = null;
		//boolean pruned = false;
		boolean complete = true;
		boolean foundOne = false;
		boolean pruned = false;
		Move bestMove = null;
		int alpha0 = alpha;

		Move firstTry = null;
		if(transposition.containsKey(state) && transposition.get(state).move != null){
			firstTry = transposition.get(state).move;
			Tuple s = minPlayer(state, firstTry, minMoves, fabulous, depth, alpha, beta);
			if(!s.complete){
				complete = false;
				//return new Tuple (Integer.MIN_VALUE, false);
			}	
			if(s.pruned){
				pruned = true;
			}
			if(s.score != Integer.MIN_VALUE){
				foundOne = true;
				/*
				if(s.score > bestScore){
					bestScore = s.score;
					//bestMove = move;
				}
				 */
				//if(s.complete){
				if(s.score > bestScore){
					bestScore = s.score;
					bestMove = firstTry;
				}
				if(bestScore > alpha){
					alpha = bestScore;
				}
				if(prune && alpha >= beta){
					pruned = true;
				}
				//}
			}
		}

		
		for(Move move : moves){
			if(move.equals(firstTry)){
				continue;
			}
			Tuple s = minPlayer(state, move, minMoves, fabulous, depth, alpha, beta);
			if(!s.complete){
				complete = false;
				//return new Tuple (Integer.MIN_VALUE, false);
			}	
			if(s.pruned){
				pruned = true;
			}
			if(s.score != Integer.MIN_VALUE){
				foundOne = true;
				/*
				if(s.score > bestScore){
					bestScore = s.score;
					//bestMove = move;
				}
				 */
				//if(s.complete){
				if(s.score > bestScore){
					bestScore = s.score;
					bestMove = move;
				}
				if(bestScore > alpha){
					alpha = bestScore;
				}
				if(prune && alpha >= beta){
					pruned = true;
					break;
				}
				//}
			}
		}

		if(! foundOne){
			bestScore = Integer.MIN_VALUE;
		}
		Tuple ret = new Tuple(bestScore, complete, pruned, alpha0, beta, bestMove);
		if(bestScore != Integer.MIN_VALUE){
			if(! useHeuristic){
				heuristic.addValue(heuristic.evaluate_mobility(moves), heuristic.evaluate_novelty(state, seen), heuristic.evaluate_opponentMobility(minMoves), bestScore);
			}
			transposition.put(state, ret);
		}
		else if(useHeuristic){
			int value = heuristic.evaluate_combined(moves, minMoves, state, seen);
			ret = new Tuple(value, false, pruned, alpha, beta, null);
			transposition.put(state, ret);
		}
		addTable(state);
		return ret;
	}

	/**
	 * Recursively performs minimax search (min-player move)
	 * 
	 * @param state Game state
	 * @param move Max-player's move
	 * @param options Min-players' possible moves
	 * @param fabulous Player's index in the role list
	 * @param depth Depth limit
	 * @param alpha Alpha value
	 * @param beta Beta value
	 * @return Worst score
	 * @throws TimeoutException Time limit exceeded
	 */
	private Tuple minPlayer(MachineState state, Move move, List<List<Move>> options, int fabulous, int depth, int alpha, int beta) throws TimeoutException{
		if(System.currentTimeMillis() > timeout){
			throw timeoutException;
		}

		if(transpositionMin.containsKey(state) && transpositionMin.get(state).containsKey(move) && transpositionMin.get(state).get(move).complete){
			Tuple lookup = transpositionMin.get(state).get(move);
			/*if( lookup.pruned && (alpha >=lookup.score )){
				return lookup;
			}	*/			
			if (lookup.alpha <= alpha && lookup.beta >= beta){
				return lookup;				
			}
		}
		
		List<List<Move>> next = combinations(options);

		MachineState nextState;
		int worstScore = MAX_SCORE + 1;
		boolean complete = true;
		boolean foundOne = false;
		boolean pruned = false;
		int beta0 = beta;
		//List <Move> bestMoves = null;
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
			if(s.pruned){
				pruned = true;
			}
			if(s.score != Integer.MIN_VALUE){
				foundOne = true;
				/*
				if(s.score < worstScore){
					worstScore = s.score;
				}
				 */
				//if(s.complete){
				if(s.score < worstScore){
					worstScore = s.score;

				}
				if(worstScore < beta){
					beta = worstScore;
					//bestMoves = moves;
				}
				if(prune && alpha >= beta){
					pruned = true;
					break;
				}
				//}
			}
		}

		if(!foundOne){
			worstScore = Integer.MIN_VALUE;
		}
		
		Tuple ret = new Tuple(worstScore, complete, pruned, alpha, beta0, null);
		if (! transpositionMin.containsKey(state)){
			transpositionMin.put(state, new ReferenceMap<Move, FabulousMultiPlayer2.Tuple>());		
		}
		transpositionMin.get(state).put(move, ret);
		/*
		else {
			if (transpositionMin.get(state).containsKey(move)){
				if (transpositionMin.get(state).get(move).beta > beta0){
					

				}
			}
			else{
				transpositionMin.get(state).put(move, ret);
			}

		}*/
		//transpositionMin.get(state).put(move, ret);
		return ret;
	}

	/**
	 * Creates all combinations of moves for opposing players.
	 * 
	 * @param moves List of all possible moves for all opposing player (in order)
	 * @return List of combinations of one move per opposing player (maintains order)
	 */
	public static List<List<Move>> combinations(List<List<Move>> moves){
		List<List<Move>> ret = new ArrayList<List<Move>>();
		if(moves.size() == 0){
			return ret;
		}
		int num = 1;
		for(List<Move> l : moves){
			num *= (l.size() == 0) ? 1 : l.size();
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
	/**
	 * 
	 * @param state the Machine state
	 */
	private void addTable(MachineState state){
		if(!seen.containsKey(state)){
			seen.put(state, new Object());
		}
		
	}

}
