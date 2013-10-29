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
 * Class for storing information in the transition table.
 * 
 * @author Nicolai
 *
 */
class TableEntry{
	protected int score;
	protected boolean complete;
	protected Move best;
	
	protected TableEntry(int score, boolean complete, Move best){
		this.score = score;
		this.complete = complete;
		this.best = best;
	}
}

/**
 * Fabulous Multiplayer with Minimax search.
 * 
 * @author Nicolai
 *
 */
final class FabulousMultiPlayer extends SampleGamer {
	
	private static final int MAX_SCORE = 100;
	
	private static final int MIN_SCORE = 0;
	
	private static final ReferenceStrength soft = AbstractReferenceMap.ReferenceStrength.SOFT;
	
	private Role role;
	
	private StateMachine theMachine;
	
	private boolean done;
	
	private MachineState currentState;
	
	private ReferenceMap<MachineState, TableEntry> transposition;
	
	//private int step;
	
	@Override
	public void setState(MachineState state){
		currentState = state;
	}
	
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		/*
		step++;
		if(step != 5){
			return theMachine.getRandomMove(currentState, role);
		}
		*/
		timeout -= 2000;
		Move move = minimax(currentState, timeout);
		if(move != null){
			return move;
		}
		return theMachine.getRandomMove(currentState, role);
	}
	
	@Override
	public void stateMachineMetaGame(long timeout){
		timeout -= 2000;
		theMachine = getStateMachine();
		role = getRole();
		transposition = new ReferenceMap<MachineState, TableEntry>(soft, soft);
		//step = 0;
		minimax(currentState, timeout);
	}
	
	/**
	 * Perform minimax search in a state.
	 * 
	 * @param state Game state
	 * @param timeout Time limit
	 * @return Best move
	 */
	private Move minimax(MachineState state, long timeout){
		//System.out.println("Exploring the root.");
		List<Move> moves;
		try {
			moves = theMachine.getLegalMoves(state, role);
		} catch (MoveDefinitionException e) {
			System.err.println("No legal moves!");
			return null;
		}
		int depth = 1;	//Change to something high for testing with output!
		int bestScore = MIN_SCORE - 1;
		Move best =  null;
		done = false;
		while(! done){
			if(System.currentTimeMillis() > timeout){
				System.out.println("Ran out of time!");
				done = false;
				break;
			}
			depth++;
			done = true;
			int alpha = MIN_SCORE - 1;
			int beta = MAX_SCORE + 1;
			bestScore = MIN_SCORE - 1;  /////Added this here
			for(Move move : moves){
				//System.out.println("Expanding " + move.toString());
				int s;
				try {
					s = minPlayer(state, move, depth, timeout, alpha, beta);
				} catch (MoveDefinitionException e) {
					System.err.println("No legal moves!");
					done = false;
					continue;
				}
				//System.out.println("Returned score of " + s);
				if(s != MAX_SCORE + 1){
					if(s > bestScore){
						bestScore = s;
						best = move;
					}
					if(s > alpha){
						alpha = s;
					}
				}
			}
		}
		if(done){
			transposition.put(state, new TableEntry(bestScore, true, best));
		}
		else{
			transposition.put(state, new TableEntry(bestScore, false, best));
		}
		//System.out.println("Choose " + best.toString());
		return best;
	}
	
	/**
	 * Recursively performs minimax search (min-player move)
	 * 
	 * @param state Game state
	 * @param move Move done by the max player
	 * @param depth Depth limit
	 * @param timeout Time limit
	 * @param alpha Alpha value
	 * @param beta Beta value
	 * @return Score value
	 * @throws MoveDefinitionException Found no legal moves
	 */
	private int minPlayer(MachineState state, Move move, int depth, long timeout, int alpha, int beta) throws MoveDefinitionException{
		//System.out.println("Exploring a min-player node.");
		List<Move[]> options = new ArrayList<Move[]>();
		List<Role> roles = theMachine.getRoles();
		int fabulous = 0;
		for(int i = 0; i < roles.size(); i++){
			Role player = roles.get(i);
			if(player.equals(role)){
				fabulous = i;
				continue;
			}
			List<Move> moves = theMachine.getLegalMoves(state, player);
			Move[] pmoves = new Move[moves.size()];
			for(int j = 0; j < moves.size(); j++){
				pmoves[j] = moves.get(j);
			}
			options.add(pmoves);
		}
		Set<List<Move>> next = combinations(options);
		MachineState nextState;
		int worstScore = MAX_SCORE + 1;
		for(List<Move> moves : next){
			//System.out.println("Expanding " + moves.get(0).toString());
			moves.add(fabulous, move);
			try {
				nextState = theMachine.getNextState(state, moves);
			} catch (TransitionDefinitionException e) {
				System.err.println("Attempted bad moves!");
				done = false;
				continue;
			}
			int s;
			try {
				s = maxPlayer(nextState, depth - 1, timeout, alpha, beta);
			} catch (GoalDefinitionException e) {
				System.err.println("Bad goal definition!");
				done = false;
				continue;
			}
			if(s == Integer.MIN_VALUE){
				break;
			}
			if(s != MIN_SCORE - 1){
				if(s < worstScore){
					worstScore = s;
				}
				if(s < beta){
					beta = s;
					if(alpha >= beta){
						//System.out.println("Pruning.");
						break;
					}
				}
			}
		}
		//System.out.println("Returning a score of " + worstScore);
		return worstScore;
	}
	
	/**
	 * Recursively performs minimax search (max-player move)
	 * 
	 * @param state Game state
	 * @param depth Depth limit
	 * @param timeout Time limit
	 * @param alpha Alpha value
	 * @param beta Beta value
	 * @return Score value
	 * @throws MoveDefinitionException Found no legal moves
	 * @throws GoalDefinitionException Bad goal definition
	 */
	private int maxPlayer(MachineState state, int depth, long timeout, int alpha, int beta) throws MoveDefinitionException, GoalDefinitionException{
		//System.out.println("Exploring a max-player node.");
		if(theMachine.isTerminal(state)){
			//System.out.println("Found a goal of value " + theMachine.getGoal(state, role));
			return theMachine.getGoal(state, role);
		}
		if(transposition.containsKey(state) && transposition.get(state).complete){
			//System.out.println("Found score of " + transposition.get(state).score + " in the transposition table.");
			return transposition.get(state).score;
		}
		if(depth == 0 || System.currentTimeMillis() > timeout){
			done = false;
			return Integer.MIN_VALUE;
		}
		int bestScore = MIN_SCORE - 1;
		Move best = null;
		boolean pruned = false;
		/*if(transposition.containsKey(state) && transposition.get(state).best != null){
			best = transposition.get(state).best;
			//System.out.println("Expanding " + best.toString());
			int s = minPlayer(state, best, depth, timeout, alpha, beta);
			if(s != MAX_SCORE + 1){
				bestScore = s;
				if(s > alpha){
					alpha = s;
					if(alpha >= beta){
						//System.out.println("Pruning.");
						pruned = true;
					}
				}
			}
		}*/
		if(! pruned){
			List<Move> moves = theMachine.getLegalMoves(state, role);
			for(Move move : moves){
				if(move.equals(best)){
					continue;
				}
				//System.out.println("Expanding " + move.toString());
				int s = minPlayer(state, move, depth, timeout, alpha, beta);
				if(s != MAX_SCORE + 1){
					if(s > bestScore){
						bestScore = s;
						best = move;
					}
					if(s > alpha){
						alpha = s;
						if(alpha >= beta){
							//System.out.println("Pruning.");
							pruned = true;
							break;
						}
					}
				}
			}
		}
		if(done && ! pruned){
			transposition.put(state, new TableEntry(bestScore, true, best));
		}
		else{
			transposition.put(state, new TableEntry(bestScore, false, best));
		}
		//System.out.println("Returning a score of " + bestScore);
		return bestScore;
	}
	
	/**
	 * Creates all combinations of moves for the opposing players.
	 * 
	 * @param moves List of arrays of possible moves for each opponent (in the order of the roles)
	 * @return Set containing all possible combinations of moves (in the order of the roles)
	 */
	private Set<List<Move>> combinations(List<Move[]> moves){
		Set<List<Move>> ret = new HashSet<List<Move>>();
		if(moves.size() == 0){
			return ret;
		}
		int num = 1;
		for(Move[] m : moves){
			num *= m.length;
		}
		for(int i = 0; i < num; i++){
			int tmp = i;
			List<Move> combination = new ArrayList<Move>();
			for(int r = 0; r < moves.size(); r++){
				Move[] m = moves.get(r);
				combination.add(m[tmp % m.length]);
				tmp /= m.length;
			}
			ret.add(combination);
		}
		return ret;
	}
	
}
