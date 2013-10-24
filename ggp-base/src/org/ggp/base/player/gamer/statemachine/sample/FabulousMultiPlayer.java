package org.ggp.base.player.gamer.statemachine.sample;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

/**
 * Fabulous Multiplayer with Minimax search.
 * 
 * @author Nicolai
 *
 */
final class FabulousMultiPlayer extends SampleGamer {
	
	private static final int MAX_SCORE = 100;
	
	private static final int MIN_SCORE = 0;
	
	private Role role;
	
	private StateMachine theMachine;
	
	private boolean done;
	
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		timeout -= 1000;
		MachineState state = getCurrentState();
		Move move = minimax(state, timeout);
		if(move != null){
			return move;
		}
		return getStateMachine().getRandomMove(getCurrentState(), role);
	}
	
	@Override
	public void stateMachineMetaGame(long timeout){
		theMachine = getStateMachine();
		role = getRole();
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
				break;
			}
			depth++;
			done = true;
			for(Move move : moves){
				int s = bestScore;
				try {
					s = minPlayer(state, move, depth, timeout);
				} catch (MoveDefinitionException e) {
					System.err.println("No legal moves!");
				}
				if(s > bestScore){
					bestScore = s;
					best = move;
				}
			}
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
	 * @return Score value
	 * @throws MoveDefinitionException Found no legal moves
	 */
	private int minPlayer(MachineState state, Move move, int depth, long timeout) throws MoveDefinitionException{
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
				continue;
			}
			int s;
			try {
				s = maxPlayer(nextState, depth - 1, timeout);
			} catch (GoalDefinitionException e) {
				System.err.println("Bad goal definition!");
				continue;
			}
			if(s == Integer.MIN_VALUE){
				break;
			}
			if(s < worstScore){
				worstScore = s;
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
	 * @return Score value
	 * @throws MoveDefinitionException Found no legal moves
	 * @throws GoalDefinitionException Bad goal definition
	 */
	private int maxPlayer(MachineState state, int depth, long timeout) throws MoveDefinitionException, GoalDefinitionException{
		if(theMachine.isTerminal(state)){
			return theMachine.getGoal(state, role);
		}
		if(depth == 0 || System.currentTimeMillis() > timeout){
			done = false;
			return Integer.MIN_VALUE;
		}
		List<Move> moves;
		moves = theMachine.getLegalMoves(state, role);
		int bestScore = MIN_SCORE - 1;
		for(Move move : moves){
			int s = minPlayer(state, move, depth, timeout);
			if(s > bestScore){
				bestScore = s;
			}
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
