package org.ggp.base.player.gamer.statemachine.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
		for(Role player : roles){
			if(player.equals(role)){
				continue;
			}
			List<Move> moves = theMachine.getLegalMoves(state, player);
			Move[] pmoves = new Move[moves.size()];
			for(int i = 0; i < moves.size(); i++){
				pmoves[i] = moves.get(i);
			}
			options.add(pmoves);
		}
		return 0;
	}
	
	/**
	 * Recursively performs minimax search (max-player move)
	 * 
	 * @param state Game state
	 * @param depth Depth limit
	 * @param timeout Time limit
	 * @return Score value
	 * @throws MoveDefinitionException Found no legal moves
	 */
	private int maxPlayer(MachineState state, int depth, long timeout) throws MoveDefinitionException{
		if(depth == 0 || System.currentTimeMillis() > timeout){
			return MIN_SCORE - 1;
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
	 * @param roles List of opponents
	 * @param moves List of arrays of possible moves for each opponent (in the same order as roles)
	 * @return Set containing all possible combinations of moves
	 */
	private Set<Map<Role, Move>> combinations(List<Role> roles, List<Move[]> moves){
		Set<Map<Role, Move>> ret = new HashSet<Map<Role, Move>>();
		if(roles.size() == 0){
			return ret;
		}
		long num = 1;
		for(Move[] m : moves){
			num *= m.length;
		}
		for(long i = 0; i < num; i++){
			
		}
		return null;
	}
	
}
