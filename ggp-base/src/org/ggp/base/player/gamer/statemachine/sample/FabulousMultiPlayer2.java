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
 * 
 * @author Nera
 *
 */
public class FabulousMultiPlayer2 extends SampleGamer {

	private class Tuple {
		int score;
		boolean complete;

		public Tuple (int score, boolean complete){
			this.complete = complete;
			this.score = score;
		}
	}
	private static final ReferenceStrength soft = AbstractReferenceMap.ReferenceStrength.SOFT;
	private ReferenceMap<MachineState, Tuple> transposition;
	
	private static final int MAX_SCORE = 100;

	private static final int MIN_SCORE = 0;

	private Role role;

	private StateMachine theMachine;

	//private boolean done;

	private MachineState currentState;

	@Override
	public void setState(MachineState state){
		currentState = state;
	}

	@Override
	public void stateMachineMetaGame(long timeout){
		timeout -= 2000;
		theMachine = getStateMachine();
		role = getRole();
		transposition = new ReferenceMap<MachineState, Tuple>(soft, soft);
		//step = 0;
		minimax(currentState, timeout);
	}


	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{

		timeout -= 2000;
		Move move = minimax(currentState, timeout);
		if(move != null){
			return move;
		}
		System.out.println("played Random");
		return theMachine.getRandomMove(currentState, role);
	}

	private Move minimax(MachineState state, long timeout){

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
		//done = false;
		notDone = true;
		while( notDone){
			//bestMove =  null;
			if(System.currentTimeMillis() > timeout){
				System.out.println("Ran out of time!");
				//done = false;
				break;
			}
			notDone = false;
			//bestScore = MIN_SCORE - 1;
			depth ++;
			for (Move move: moves){
				Tuple tempScore = new Tuple(bestScore, false);
				try {
					tempScore = minPlayer (state, move, depth, timeout, 0, 0);
				} catch (MoveDefinitionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!tempScore.complete){
					notDone = true;
				}
				if (tempScore.score > bestScore && tempScore.complete ){
					bestScore = tempScore.score;
					bestMove = move;
				}			
			}		
		}
		System.out.println("done. Depth: " + depth);
		return bestMove;
	}

	private Tuple maxPlayer(MachineState state, int depth, long timeout, int alpha, int beta) throws MoveDefinitionException, GoalDefinitionException{


		if(theMachine.isTerminal(state)){
			//System.out.println("Found a goal of value " + theMachine.getGoal(state, role));
			transposition.put(state, new Tuple (theMachine.getGoal(state, role), true));
			return new Tuple (theMachine.getGoal(state, role), true);
		}

		if (transposition.containsKey(state) && transposition.get(state).complete){
			return transposition.get(state);
			
		}
		if(depth == 0 || System.currentTimeMillis() > timeout){
			return new Tuple (Integer.MIN_VALUE, false);
		}

		int bestScore = MIN_SCORE - 1;
		Move bestMove = null;
		boolean pruned = false;
		boolean complete = true;
		boolean foundOne = false;
		
		List<Move> moves = theMachine.getLegalMoves(state, role);
		for(Move move : moves){

			Tuple s = minPlayer(state, move, depth, timeout, alpha, beta);
			if (!s.complete){
				complete = false;
				//return new Tuple (Integer.MIN_VALUE, false);
			}	
			
			if (s.score != Integer.MIN_VALUE){
				foundOne = true;
			}
			if(s.score > bestScore && s.score != Integer.MIN_VALUE){
				bestScore = s.score;
				bestMove = move;
			}
		}
		if (! foundOne){
			bestScore = Integer.MIN_VALUE;
		}
		
		if (complete){
			transposition.put(state, new Tuple (bestScore, complete));
		}
		return new Tuple (bestScore, complete);
	}

	
	
	
	
	
	private Tuple minPlayer(MachineState state, Move move, int depth,
			long timeout, int alpha, int beta) throws MoveDefinitionException {

		if( System.currentTimeMillis() > timeout){
			return new Tuple (Integer.MIN_VALUE, false);
		}



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
		boolean completed = true;
		boolean foundOne = false;
		for(List<Move> moves : next){
			//System.out.println("Expanding " + moves.get(0).toString());
			moves.add(fabulous, move);
			try {
				nextState = theMachine.getNextState(state, moves);
			} catch (TransitionDefinitionException e) {
				System.err.println("Attempted bad moves!");				
				continue;
			}
			Tuple s = new Tuple (Integer.MIN_VALUE, false);
			try {
				s = maxPlayer(nextState, depth - 1, timeout, alpha, beta);
			} catch (GoalDefinitionException e) {
				System.err.println("Bad goal definition!");				
				continue;
			}
			if(!s.complete){
				completed = false;
				//return new Tuple (Integer.MIN_VALUE, false);
			}
			
			if (s.score != Integer.MIN_VALUE){
				foundOne = true;
			}
			if(s.score < worstScore && s.score != Integer.MIN_VALUE){
				worstScore = s.score;
			}

		}
		if (!foundOne){
			worstScore = Integer.MIN_VALUE;
		}
		return new Tuple (worstScore, completed);
	}






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



