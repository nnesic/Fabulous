package org.ggp.base.player.gamer.statemachine.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.management.relation.Role;

import org.ggp.base.util.gdl.factory.GdlFactory;
import org.ggp.base.util.gdl.factory.exceptions.GdlFormatException;
import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlConstant;
import org.ggp.base.util.gdl.grammar.GdlDistinct;
import org.ggp.base.util.gdl.grammar.GdlLiteral;
import org.ggp.base.util.gdl.grammar.GdlNot;
import org.ggp.base.util.gdl.grammar.GdlOr;
import org.ggp.base.util.gdl.grammar.GdlPool;
import org.ggp.base.util.gdl.grammar.GdlRule;
import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.prover.Prover;
import org.ggp.base.util.prover.aima.cache.ProverCache;
import org.ggp.base.util.prover.aima.knowledge.KnowledgeBase;
import org.ggp.base.util.prover.aima.renamer.VariableRenamer;
import org.ggp.base.util.prover.aima.substituter.Substituter;
import org.ggp.base.util.prover.aima.substitution.Substitution;
import org.ggp.base.util.prover.aima.unifier.Unifier;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.query.ProverQueryBuilder;
import org.ggp.base.util.symbol.factory.exceptions.SymbolFormatException;

//comment here
public final class EvaluationProver extends Prover
{
	class Tuple{
		int count, succeeded, size;
		double avgProb;
		HashMap<GdlSentence, Integer> instances;

		public Tuple(int c, int su, int sz){
			count = c;
			succeeded = su;
			size = sz;
			instances = new HashMap<GdlSentence, Integer>();
		}

		public void setAvgProb (){
			double sum = 0;
			for (GdlSentence s : instances.keySet()){
				sum += instances.get(s)*1.0/count;
			}
			avgProb = sum / instances.size();
		}
	}


	private KnowledgeBase knowledgeBase;
	public int count = 0;

	public HashMap < GdlSentence, Tuple > stats;

	public ArrayList<MachineState> trainStates;
	private boolean evaluate = false;
	public KnowledgeBase reorderedKnowledgeBase;
	public Set <Gdl> gameDescription;

	public EvaluationProver(Set<Gdl> description)
	{
		gameDescription = description;
		knowledgeBase = new KnowledgeBase(description);
		stats = new HashMap<GdlSentence, Tuple >();
		for (Gdl rule : description){
			if (rule instanceof GdlRule){	

				GdlRule r = (GdlRule) rule;
				//stats.put(r.getHead().getName(), new HashMap<GdlLiteral, Tuple>());
				List <GdlLiteral> body = r.getBody();
				for (GdlLiteral l : body){
					if (!stats.containsKey(l) && !(l instanceof GdlNot) && !(l instanceof GdlDistinct) && !(l instanceof GdlOr) ){
						stats.put((GdlSentence)l, new Tuple(0, 0, 0));
					}
				}

			}
		}
	}

	public int getLitCount (GdlSentence s){
		return stats.get(s).count;
	}

	public int getLitSuccess (GdlSentence s){
		return stats.get(s).succeeded;
	}

	public int getLitSize (GdlSentence s){
		return stats.get(s).size;
	}

	public HashMap<GdlSentence, Integer> getLitInstances (GdlSentence s){
		return stats.get(s).instances;
	}

	public void setTrainStates (ArrayList<MachineState> s){
		trainStates = s;
	}



	public void evaluate (ArrayList <MachineState> m, StateMachine machine){
		trainStates = m;
		evaluate = true;

		for (GdlLiteral l : stats.keySet()){
			for (MachineState state : trainStates){
				Set<GdlSentence> result = null;
				try {
					result = askAll((GdlSentence)l, ProverQueryBuilder.getContext(state, machine.getRoles(), machine.getRandomJointMove(state)));
				} catch (MoveDefinitionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Tuple temp = stats.get(l);
				temp.count ++;
				if (result.size() != 0){
					temp.succeeded ++;
					for (GdlSentence sen : result){
						if (!temp.instances.containsKey(sen)){
							temp.instances.put(sen, 0);
						}
						temp.instances.put(sen, temp.instances.get(sen)+1);
					}
				}
				temp.size += count;
				stats.put((GdlSentence) l, temp);
			}
		}

	}

	public void calculateProbs (){
		for (GdlSentence s : stats.keySet()){
			Tuple temp = stats.get(s);
			temp.setAvgProb();
			stats.put(s, temp);
		}
	}

	public Set <Gdl> order(int mode){
		calculateProbs();
		Set <Gdl> newSet = new HashSet<Gdl>(); 

		for (Gdl rule : gameDescription){
			if (rule instanceof GdlRule){	
				int start = 0, end = 0;
				GdlRule r = (GdlRule) rule;
				List<GdlLiteral> body = r.getBody();
				ArrayList <GdlLiteral> newBody = new ArrayList<GdlLiteral>();
				ArrayList <GdlLiteral> temp = new ArrayList<GdlLiteral>();

				for (int i = 0; i < body.size(); i++){
					if (! (body.get(i) instanceof GdlSentence)){
						end = i;
						if (start != end){
							/*	for (int j = start; j<end; j++){
								temp.add(body.get(j));
							}*/
							
								ArrayList<GdlLiteral> sorted = probHelper(start, end, temp);
								
								if (mode == 1){
									sorted = sizeHelper(start, end, temp);
								}
								if (mode == 2){
									sorted = smartHelper(start, end, temp);
								}
							
							for (int j = 0; j < sorted.size(); j++){
								newBody.add(sorted.get(j));
							}
						}
						newBody.add(body.get(i));
						start = end +1;
						temp.clear();
					}
					else {
						temp.add(body.get(i));
					}
				}
				if (end == 0){
					end = body.size();
					ArrayList<GdlLiteral> sorted = probHelper(start, end, temp);
					
					if (mode == 2){
						sorted = smartHelper(start, end, temp);
					}
					
					if (mode == 1){
						sorted = sizeHelper(start, end, temp);
					}
					
					for (int j = 0; j < sorted.size(); j++){
						newBody.add(sorted.get(j));
					}
				}
				newSet.add(GdlPool.getRule(r.getHead(), newBody));
				
				
				for (Gdl g : gameDescription){
					if (! (g instanceof GdlRule)){
						newSet.add(g);
					}
					
				}
			}



			//newSet.add(null);
		}

		return newSet;
	}

	private ArrayList<GdlLiteral > probHelper (int start, int end, ArrayList <GdlLiteral> toSort){

		ArrayList<GdlLiteral> ret = new ArrayList<GdlLiteral>();


		while (toSort.size() >0){
			double min = 100;
			int minIndex = 0;
			for (int i = 0; i < toSort.size(); i++){
				if (stats.get((GdlSentence)toSort.get(i)).avgProb <min){
					min = stats.get((GdlSentence)toSort.get(i)).avgProb;
					minIndex = i;
				}
			}
			ret.add(toSort.get(minIndex));
			toSort.remove(minIndex);
		}
		return ret;
	}
	

	private ArrayList<GdlLiteral > sizeHelper (int start, int end, ArrayList <GdlLiteral> toSort){

		ArrayList<GdlLiteral> ret = new ArrayList<GdlLiteral>();


		while (toSort.size() >0){
			double min = stats.get((GdlSentence)toSort.get(0)).size;
			int minIndex = 0;
			for (int i = 0; i < toSort.size(); i++){
				if (stats.get((GdlSentence)toSort.get(i)).size <min){
					min = stats.get((GdlSentence)toSort.get(i)).size;
					minIndex = i;
				}
			}
			ret.add(toSort.get(minIndex));
			toSort.remove(minIndex);
		}
		return ret;
	}

	
	private ArrayList<GdlLiteral > smartHelper (int start, int end, ArrayList <GdlLiteral> toSort){

		ArrayList<GdlLiteral> ret = new ArrayList<GdlLiteral>();


		while (toSort.size() >0){
			double min = stats.get((GdlSentence)toSort.get(0)).size;
			int minIndex = 0;
			for (int i = 0; i < toSort.size(); i++){
				if (stats.get((GdlSentence)toSort.get(i)).size * stats.get((GdlSentence)toSort.get(i)).avgProb <min){
					min = stats.get((GdlSentence)toSort.get(i)).size * stats.get((GdlSentence)toSort.get(i)).avgProb;
					minIndex = i;
				}
			}
			ret.add(toSort.get(minIndex));
			toSort.remove(minIndex);
		}
		return ret;
	}
	
	


	public void results (ArrayList <MachineState> m, StateMachine machine){
		System.out.println("RUNNING ORIGINAL RULE SET");
		int stepsLegal = 0;
		int stepsNext = 0;
		int stepsTerminal =0;
		int stepsGoal =0;
		for (MachineState state : m){
			try {
				askAll((GdlSentence)GdlFactory.create("(legal ?p ?x)"), ProverQueryBuilder.getContext(state, machine.getRoles(), machine.getRandomJointMove(state)));
				stepsLegal += count;
				askAll((GdlSentence)GdlFactory.create("(next ?x)"),  ProverQueryBuilder.getContext(state, machine.getRoles(), machine.getRandomJointMove(state)));
				stepsNext += count;
				askAll((GdlSentence)GdlFactory.create("(goal ?p ?x)"), ProverQueryBuilder.getContext(state, machine.getRoles(), machine.getRandomJointMove(state)));
				stepsGoal += count;
				askAll(ProverQueryBuilder.getTerminalQuery(), ProverQueryBuilder.getContext(state, machine.getRoles(), machine.getRandomJointMove(state)));
				stepsTerminal += count;
				
				
			} catch (GdlFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SymbolFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MoveDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Average tree size for legal: " + stepsLegal/m.size() );
		System.out.println("Average tree size for next: " + stepsNext/m.size() );
		System.out.println("Average tree size for goal: " + stepsGoal/m.size() );
		System.out.println("Average tree size for terminal: " + stepsTerminal/m.size() );
		
		knowledgeBase = new KnowledgeBase(order(0));
		 stepsLegal = 0;
		 stepsNext = 0;
		 stepsTerminal =0;
		 stepsGoal =0;
		for (MachineState state : m){
			try {
				askAll((GdlSentence)GdlFactory.create("(legal ?p ?x)"), ProverQueryBuilder.getContext(state, machine.getRoles(), machine.getRandomJointMove(state)));
				stepsLegal += count;
				askAll((GdlSentence)GdlFactory.create("(next ?x)"),  ProverQueryBuilder.getContext(state, machine.getRoles(), machine.getRandomJointMove(state)));
				stepsNext += count;
				askAll((GdlSentence)GdlFactory.create("(goal ?p ?x)"), ProverQueryBuilder.getContext(state, machine.getRoles(), machine.getRandomJointMove(state)));
				stepsGoal += count;
				askAll(ProverQueryBuilder.getTerminalQuery(), ProverQueryBuilder.getContext(state, machine.getRoles(), machine.getRandomJointMove(state)));
				stepsTerminal += count;
				
			} catch (GdlFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SymbolFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MoveDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("RUNNING PROB RULE SET");
		System.out.println("Average tree size for legal: " + stepsLegal/m.size() );
		System.out.println("Average tree size for next: " + stepsNext/m.size() );
		System.out.println("Average tree size for goal: " + stepsGoal/m.size() );
		System.out.println("Average tree size for terminal: " + stepsTerminal/m.size() );
		
		knowledgeBase = new KnowledgeBase(order(1));
		 stepsLegal = 0;
		 stepsNext = 0;
		 stepsTerminal =0;
		 stepsGoal =0;
		for (MachineState state : m){
			try {
				askAll((GdlSentence)GdlFactory.create("(legal ?p ?x)"), ProverQueryBuilder.getContext(state, machine.getRoles(), machine.getRandomJointMove(state)));
				stepsLegal += count;
				askAll((GdlSentence)GdlFactory.create("(next ?x)"),  ProverQueryBuilder.getContext(state, machine.getRoles(), machine.getRandomJointMove(state)));
				stepsNext += count;
				askAll((GdlSentence)GdlFactory.create("(goal ?p ?x)"), ProverQueryBuilder.getContext(state, machine.getRoles(), machine.getRandomJointMove(state)));
				stepsGoal += count;
				askAll(ProverQueryBuilder.getTerminalQuery(), ProverQueryBuilder.getContext(state, machine.getRoles(), machine.getRandomJointMove(state)));
				stepsTerminal += count;
				
				
			} catch (GdlFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SymbolFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MoveDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("RUNNING SIZE RULE SET");
		System.out.println("Average tree size for legal: " + stepsLegal/m.size() );
		System.out.println("Average tree size for next: " + stepsNext/m.size() );
		System.out.println("Average tree size for goal: " + stepsGoal/m.size() );
		System.out.println("Average tree size for terminal: " + stepsTerminal/m.size() );
		try {
			System.out.println(knowledgeBase.fetch((GdlSentence)GdlFactory.create("(connfour ?x)")).toString());
			System.out.println(knowledgeBase.fetch((GdlSentence)GdlFactory.create("(goal ?p ?x)")).toString());
			System.out.println(knowledgeBase.fetch((GdlSentence)GdlFactory.create("(diag1 ?x)")).toString());
		} catch (GdlFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SymbolFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}












	private Set<GdlSentence> ask(GdlSentence query, Set<GdlSentence> context, boolean askOne)
	{
		LinkedList<GdlLiteral> goals = new LinkedList<GdlLiteral>();
		goals.add(query);

		Set<Substitution> answers = new HashSet<Substitution>();
		Set<GdlSentence> alreadyAsking = new HashSet<GdlSentence>();
		ask(goals, new KnowledgeBase(context), new Substitution(), new ProverCache(), new VariableRenamer(), askOne, answers, alreadyAsking);

		Set<GdlSentence> results = new HashSet<GdlSentence>();
		for (Substitution theta : answers)
		{
			results.add(Substituter.substitute(query, theta));
		}

		return results;
	}

	private void ask(LinkedList<GdlLiteral> goals, KnowledgeBase context, Substitution theta, ProverCache cache, VariableRenamer renamer, boolean askOne, Set<Substitution> results, Set<GdlSentence> alreadyAsking)
	{
		count++;
		if (goals.size() == 0)
		{
			results.add(theta);
		}
		else
		{
			GdlLiteral literal = goals.removeFirst();
			//System.out.println(literal.toString());
			GdlLiteral qPrime = Substituter.substitute(literal, theta);

			if (qPrime instanceof GdlDistinct)
			{
				GdlDistinct distinct = (GdlDistinct) qPrime;
				askDistinct(distinct, goals, context, theta, cache, renamer, askOne, results, alreadyAsking);
			}
			else if (qPrime instanceof GdlNot)
			{
				GdlNot not = (GdlNot) qPrime;
				askNot(not, goals, context, theta, cache, renamer, askOne, results, alreadyAsking);
			}
			else if (qPrime instanceof GdlOr)
			{
				GdlOr or = (GdlOr) qPrime;
				askOr(or, goals, context, theta, cache, renamer, askOne, results, alreadyAsking);
			}
			else
			{
				GdlSentence sentence = (GdlSentence) qPrime;
				askSentence(sentence, goals, context, theta, cache, renamer, askOne, results, alreadyAsking);
			}

			goals.addFirst(literal);
		}
	}

	@Override
	public Set<GdlSentence> askAll(GdlSentence query, Set<GdlSentence> context)
	{
		count = 0;
		return ask(query, context, false);

	}

	private void askDistinct(GdlDistinct distinct, LinkedList<GdlLiteral> goals, KnowledgeBase context, Substitution theta, ProverCache cache, VariableRenamer renamer, boolean askOne, Set<Substitution> results, Set<GdlSentence> alreadyAsking)
	{
		if (!distinct.getArg1().equals(distinct.getArg2()))
		{
			ask(goals, context, theta, cache, renamer, askOne, results, alreadyAsking);
		}
	}

	private void askNot(GdlNot not, LinkedList<GdlLiteral> goals, KnowledgeBase context, Substitution theta, ProverCache cache, VariableRenamer renamer, boolean askOne, Set<Substitution> results, Set<GdlSentence> alreadyAsking)
	{
		LinkedList<GdlLiteral> notGoals = new LinkedList<GdlLiteral>();
		notGoals.add(not.getBody());

		Set<Substitution> notResults = new HashSet<Substitution>();
		ask(notGoals, context, theta, cache, renamer, true, notResults, alreadyAsking);

		if (notResults.size() == 0)
		{
			ask(goals, context, theta, cache, renamer, askOne, results, alreadyAsking);
		}
	}

	@Override
	public GdlSentence askOne(GdlSentence query, Set<GdlSentence> context)
	{
		Set<GdlSentence> results = ask(query, context, true);
		return (results.size() > 0) ? results.iterator().next() : null;
	}

	private void askOr(GdlOr or, LinkedList<GdlLiteral> goals, KnowledgeBase context, Substitution theta, ProverCache cache, VariableRenamer renamer, boolean askOne, Set<Substitution> results, Set<GdlSentence> alreadyAsking)
	{
		for (int i = 0; i < or.arity(); i++)
		{
			goals.addFirst(or.get(i));
			ask(goals, context, theta, cache, renamer, askOne, results, alreadyAsking);
			goals.removeFirst();

			if (askOne && (results.size() > 0))
			{
				break;
			}
		}
	}

	private void askSentence(GdlSentence sentence, LinkedList<GdlLiteral> goals, KnowledgeBase context, Substitution theta, ProverCache cache, VariableRenamer renamer, boolean askOne, Set<Substitution> results, Set<GdlSentence> alreadyAsking)
	{
		if (!cache.contains(sentence))
		{
			//Prevent infinite loops on certain recursive queries.
			if(alreadyAsking.contains(sentence)) {
				return;
			}
			alreadyAsking.add(sentence);
			List<GdlRule> candidates = new ArrayList<GdlRule>();
			candidates.addAll(knowledgeBase.fetch(sentence));
			candidates.addAll(context.fetch(sentence));

			Set<Substitution> sentenceResults = new HashSet<Substitution>();
			for (GdlRule rule : candidates)
			{
				GdlRule r = renamer.rename(rule);

				Substitution thetaPrime = Unifier.unify(r.getHead(), sentence);

				if (thetaPrime != null)
				{
					LinkedList<GdlLiteral> sentenceGoals = new LinkedList<GdlLiteral>();
					for (int i = 0; i < r.arity(); i++)
					{
						sentenceGoals.add(r.get(i));
					}

					ask(sentenceGoals, context, theta.compose(thetaPrime), cache, renamer, false, sentenceResults, alreadyAsking);
				}
			}

			cache.put(sentence, sentenceResults);
			alreadyAsking.remove(sentence);
		}

		for (Substitution thetaPrime : cache.get(sentence))
		{
			ask(goals, context, theta.compose(thetaPrime), cache, renamer, askOne, results, alreadyAsking);
			if (askOne && (results.size() > 0))
			{
				break;
			}
		}
	}

	@Override
	public boolean prove(GdlSentence query, Set<GdlSentence> context)
	{
		return askOne(query, context) != null;
	}

}
