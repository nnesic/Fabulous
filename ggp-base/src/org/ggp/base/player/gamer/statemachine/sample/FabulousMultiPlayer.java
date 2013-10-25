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
	
	@Override
	public void setState(MachineState state){
		currentState = state;
	}
	
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		timeout -= 1000;
		Move move = minimax(currentState, timeout);
		if(move != null){
			return move;
		}
		return getStateMachine().getRandomMove(currentState, role);
	}
	
	@Override
	public void stateMachineMetaGame(long timeout){
		theMachine = getStateMachine();
		role = getRole();
		transposition = new ReferenceMap<MachineState, TableEntry>(soft, soft);
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
		List<Move> moves;
		try {
			moves = theMachine.getLegalMoves(state, role);
		} catch (MoveDefinitionException e) {
			System.err.println("No legal moves!");
			return null;
		}
		int depth = 1;
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
			for(Move move : moves){
				int s;
				try {
					s = minPlayer(state, move, depth, timeout, alpha, beta);
				} catch (MoveDefinitionException e) {
					System.err.println("No legal moves!");
					done = false;
					continue;
				}
				if(s > bestScore && !(s == MAX_SCORE + 1)){
					bestScore = s;
					best = move;
				}
				if(s > alpha){
					alpha = s;
				}
			}
		}
		if(done){
			transposition.put(state, new TableEntry(bestScore, true, best));
		}
		else{
			transposition.put(state, new TableEntry(bestScore, false, best));
		}
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
			if(s < worstScore){
				worstScore = s;
			}
			if(s < beta){
				beta = s;
				if(alpha >= beta){
					break;
				}
			}
		}
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
		if(theMachine.isTerminal(state)){
			return theMachine.getGoal(state, role);
		}
		if(transposition.containsKey(state) && transposition.get(state).complete){
			//return transposition.get(state).score;
		}
		if(depth == 0 || System.currentTimeMillis() > timeout){
			done = false;
			return Integer.MIN_VALUE;
		}
		List<Move> moves;
		moves = theMachine.getLegalMoves(state, role);
		int bestScore = MIN_SCORE - 1;
		Move best = null;
		boolean pruned = false;
		for(Move move : moves){
			int s = minPlayer(state, move, depth, timeout, alpha, beta);
			if(s > bestScore && !(s == MAX_SCORE + 1)){
				bestScore = s;
				best = move;
			}
			if(s > alpha){
				alpha = s;
				if(alpha >= beta){
					pruned = true;
					break;
				}
			}
		}
		if(done && ! pruned){
			transposition.put(state, new TableEntry(bestScore, true, best));
		}
		else{
			transposition.put(state, new TableEntry(bestScore, false, best));
		}
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
