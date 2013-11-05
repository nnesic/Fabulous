package org.ggp.base.player.gamer.statemachine.sample;

import java.util.List;
import java.util.Map;

/**
 * Class containing the evaluation functions used by FabulousPlayer.
 * 
 * @author Nicolai, Irme
 *
 */
public class Heuristics {
	
	/**
	 * Class holding a state's heuristic values and actual value.
	 */
	private class Record {
		int[] heuristics;
		int value;
		
		protected Record(int[] heuristics, int value){
			this.heuristics = heuristics;
			this.value = value;
		}
	}
	
	private static final int MAX_HEURISTIC = 99;
	
	private static final int MIN_HEURISTIC = 1;
	
	private static final ReferenceStrength SOFT = AbstractReferenceMap.ReferenceStrength.SOFT;
	
	private int maxMoves = 1;
	
	private int maxOppMoves;
	
	private ReferenceMap<Object, Record> values;
	
	private double[] weights = {0.34, 0.33, 0.33};
	
	/**
	 * @param machine State machine of the game
	 */
	public Heuristics(StateMachine machine){
		maxOppMoves = machine.getRoles().size();
		values = new ReferenceMap<Object, Record>(SOFT, SOFT);
	}
	
	/**
	 * Combined heuristic function.
	 * 
	 * @param maxMove Max-player's possible moves
	 * @param minMoves Min-players' possible moves
	 * @param state Game state
	 * @param transposition Transposition table
	 * @return Heuristic value
	 */
	public int evaluate_combined(List<Move> maxMove, List<List<Move>> minMoves, MachineState state, Map<MachineState, ?> transposition){
		double ret = 0;
		ret += weights[0] * evaluate_mobility(maxMove);
		ret += weights[1] * evaluate_novelty(state, transposition);
		ret += weights[2] * inverse(evaluate_opponentMobility(minMoves));
		return (int)ret;
	}
	
	/**
	 * Inverts the result of an evaluation function.
	 * 
	 * @param value Evaluation function output
	 * @return Inverted heuristic value
	 */
	public int inverse(int value){
		return MIN_HEURISTIC + MAX_HEURISTIC - value;
	}
	
	/**
	 * Dummy evaluation function.
	 * 
	 * @param state Current state
	 * @param role Role to evaluate for
	 * @return Heuristic value
	 */
	public int evaluate_dummy(){
		return (MAX_HEURISTIC + MIN_HEURISTIC) / 2;
	}
	
	/**
	 * Evaluation function using the mobility metric.
	 * 
	 * @param moves Number of available moves
	 * @return Heuristic value
	 */
	public int evaluate_mobility(List<Move> moves){
		int c = moves.size();
		if(c > maxMoves){
			maxMoves = c;
			return MAX_HEURISTIC;
		}
		c *= (MAX_HEURISTIC - MIN_HEURISTIC);
		c /= maxMoves;
		return MIN_HEURISTIC + c;
	}
	
	/**
	 * Evaluation function using the novelty metric.
	 * Basic implementation, only checks for exact matches.
	 * 
	 * @param state Current state
	 * @param transposition Transposition table
	 * @return Heuristic value
	 */
	public int evaluate_novelty(MachineState state, Map<MachineState, ?> transposition){
		if(transposition.containsKey(state)){
			return MIN_HEURISTIC;
		}
		else{
			return MAX_HEURISTIC;
		}
	}
	
	/**
	 * Evaluation function using opponents' mobility.
	 * 
	 * @param moves List of opponents' possible moves
	 * @return Heuristic value
	 */
	public int evaluate_opponentMobility(List<List<Move>> moves){
		int c = 0;
		for(List<Move> l : moves){
			c += l.size();
		}
		if(c > maxOppMoves){
			maxOppMoves = c;
			return MAX_HEURISTIC;
		}
		c *= (MAX_HEURISTIC - MIN_HEURISTIC);
		c /= maxOppMoves;
		return MIN_HEURISTIC + c;
	}
	
	/**
	 * Adds a state's heuristic values to the table.
	 * 
	 * @param mobility Mobility heuristic value
	 * @param novelty Novelty heuristic value
	 * @param invOppMobility Inverted opponents' mobility heuristic value
	 * @param value State's actual value
	 */
	public void addValue(int mobility, int novelty, int invOppMobility, int value){
		int[] val = new int[3];
		val[0] = mobility;
		val[1] = novelty;
		val[2] = invOppMobility;
		values.put(new Object(), new Record(val, value));
	}
	
	/**
	 * Computes the weights for the combined heuristic function.
	 */
	public void computeWeights(){
		double mult = 0.0;
		double[] add = {0.0, 0.0, 0.0};
		for(Record r : values.values()){
			for(int i = 0; i < 3; i++){
				mult += r.heuristics[i] * r.heuristics[i];
				add[i] += r.heuristics[i] * r.value;
			}
		}
		for(int i = 0; i < 3; i++){
			weights[i] = (int)(add[i] / mult);
		}
	}
	
}	 * @param moves Number of available moves
	 * @return Heuristic value
	 */
	public int evaluate_mobility(List<Move> moves){
		int c = moves.size();
		if(c > maxMoves){
			maxMoves = c;
			return MAX_HEURISTIC;
		}
		c *= (MAX_HEURISTIC - MIN_HEURISTIC);
		c /= maxMoves;
		return MIN_HEURISTIC + c;
	}
	
	/**
	 * Evaluation function using the novelty metric.
	 * Basic implementation, only checks for exact matches.
	 * 
	 * @param state Current state
	 * @param transposition Transposition table
	 * @return Heuristic value
	 */
	public int evaluate_novelty(MachineState state, Map<MachineState, ?> transposition){
		if(transposition.containsKey(state)){
			return MIN_HEURISTIC;
		}
		else{
			return MAX_HEURISTIC;
		}
	}
	
	/**
	 * Evaluation function using opponents' mobility.
	 * 
	 * @param moves List of opponents' possible moves
	 * @return Heuristic value
	 */
	public int evaluate_opponentMobility(List<List<Move>> moves){
		int c = 0;
		for(List<Move> l : moves){
			c += l.size();
		}
		if(c > maxOppMoves){
			maxOppMoves = c;
			return MAX_HEURISTIC;
		}
		c *= (MAX_HEURISTIC - MIN_HEURISTIC);
		c /= maxOppMoves;
		return MIN_HEURISTIC + c;
	}
	
	/**
	 * Adds a state's heuristic values to the table.
	 * 
	 * @param mobility Mobility heuristic value
	 * @param novelty Novelty heuristic value
	 * @param invOppMobility Inverted opponents' mobility heuristic value
	 * @param value State's actual value
	 */
	public void addValue(int mobility, int novelty, int invOppMobility, int value){
		int[] val = new int[3];
		val[0] = mobility;
		val[1] = novelty;
		val[2] = invOppMobility;
		values.put(new Object(), new Record(val, value));
	}
	
	/**
	 * Computes the weights for the combined heuristic function.
	 */
	public void computeWeights(){
		double mult = 0.0;
		double[] add = {0.0, 0.0, 0.0};
		for(Record r : values.values()){
			for(int i = 0; i < 3; i++){
				mult += r.heuristics[i] * r.heuristics[i];
				add[i] += r.heuristics[i] * r.value;
			}
		}
		for(int i = 0; i < 3; i++){
			weights[i] = (int)(add[i] / mult);
		}
	}
	
}
