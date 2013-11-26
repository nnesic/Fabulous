package org.ggp.base.player.gamer.statemachine.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.management.relation.Role;

import org.ggp.base.util.game.CloudGameRepository;
import org.ggp.base.util.game.Game;
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
import org.ggp.base.util.gdl.grammar.GdlTerm;
import org.ggp.base.util.prover.Prover;
import org.ggp.base.util.prover.aima.cache.ProverCache;
import org.ggp.base.util.prover.aima.knowledge.KnowledgeBase;
import org.ggp.base.util.prover.aima.renamer.VariableRenamer;
import org.ggp.base.util.prover.aima.substituter.Substituter;
import org.ggp.base.util.prover.aima.substitution.Substitution;
import org.ggp.base.util.prover.aima.unifier.Unifier;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
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
	boolean tooLong = false;
	int controlCount = Integer.MAX_VALUE;
	boolean eval;
	public HashMap < GdlSentence, Tuple > stats;

	public HashMap<GdlRule, GdlRule> oldToNew;
	public HashMap<GdlRule, GdlRule> newToOld;

	public KnowledgeBase reorderedKnowledgeBase;
	public KnowledgeBase originalKnowledgeBase;
	public Set <Gdl> gameDescription;
	public Set <Gdl> reorderedGameDescription;

	int [] origScores;

	public HashMap <GdlRule, Integer> scores;


	public EvaluationProver(Set<Gdl> description)

	{
		gameDescription = description;
		knowledgeBase = new KnowledgeBase(description);
		stats = new HashMap<GdlSentence, Tuple >();
		newToOld = new HashMap<GdlRule, GdlRule> ();
		oldToNew = new HashMap<GdlRule, GdlRule> ();
		reorderedKnowledgeBase = new KnowledgeBase(gameDescription);
		originalKnowledgeBase = new KnowledgeBase(description);
		reorderedGameDescription = new HashSet <Gdl> ();
		reorderedGameDescription.addAll(description);
		scores = new HashMap<GdlRule, Integer>();
		eval = false;

	}


	public void reorder (ArrayList <MachineState> trainStates, StateMachine machine){
		eval = true;
		ArrayList <List <Move>> moves = new ArrayList<List<Move>>();

		//calculate query length to test the old rule over all states
		for (Gdl item : gameDescription){

			if(item instanceof GdlRule){
				GdlRule rule = (GdlRule) item;
				//here do the re-ordering ot literals in the tail

				//dakle, prvi re-arrangement je onaj pravi. 
				reorderedGameDescription.remove(rule);
				GdlRule bestRuleVersion = rule;

				List tail = rule.getBody();

				
				ArrayList <GdlRule> rulesToTry = new ArrayList<GdlRule> ();
				rulesToTry.add(rule);
				
				int bestReorderingIndex = 0;
				for (int j = 0; j < tail.size(); j++){

					
					 rulesToTry = rearrangeRule(rulesToTry.get(bestReorderingIndex), j);
					


					Set origResults = null;
					int currentBestScore = Integer.MAX_VALUE;
					
					for (int i = 0; i < rulesToTry.size(); i++){
						controlCount = Integer.MAX_VALUE;

						GdlRule currentRule = rulesToTry.get(i);	
						int countOld = 0;
						int countNew = 0;

						reorderedGameDescription.add(currentRule);

						knowledgeBase = new KnowledgeBase(reorderedGameDescription);
						origScores = new int [trainStates.size()];
						tooLong = false;

						for (int machineIndex = 0; machineIndex<trainStates.size(); machineIndex++){
							MachineState m = trainStates.get(machineIndex);
							GdlSentence head = rule.getHead();

							try {

								List <Move> doThis = machine.getRandomJointMove(m);
								if ( i ==0) {
									eval = true;
									moves.add(machineIndex,  machine.getRandomJointMove(m));
									origResults = askAll(head, ProverQueryBuilder.getContext(m, machine.getRoles(), doThis ));
									eval = false;
									origScores [machineIndex] = count;
									controlCount = 10000 ;
									countNew += count;

								}

								else{
									//controlCount = 10000 + origScores [machineIndex] ;
									//System.out.println(currentRule.toString());
									controlCount = 1000+3*origScores [machineIndex] ;
									count = 0;
									eval = true;
									Set reorderedResults = askAll(head, ProverQueryBuilder.getContext(m, machine.getRoles(), doThis));
									eval = false;
									if ( !(reorderedResults.equals(origResults)) || tooLong){
										//if the original rule and the rearranged rule dont give same result discard this 
										//rearrangement and try anoter one	
										//another reacon to break is if we hit an infinite loop somewhere so tooLong is true
										//System.out.println("ORIG: " + origResults.toString());
										//System.out.println("NEW: " + reorderedResults.toString());
										tooLong = true; //using this as a general 'woops something went wrong'


										break;
									}
									else {
										countNew +=count;
									}
								}
							} catch (MoveDefinitionException e) {
								e.printStackTrace();
							}
						}

						if (tooLong)
							scores.put(currentRule, -1);
						else 
							scores.put(currentRule, countNew);

						if (!(tooLong)){
							//if ( (countNew < countOld)){

							if (countNew < currentBestScore){

								//bestRuleVersion = currentRule;
								bestReorderingIndex = i;
								currentBestScore = countNew;
							}

							//	}


						}
						reorderedGameDescription.remove(currentRule);
					}
					reorderedGameDescription.add(rulesToTry.get(bestReorderingIndex));

				}
				controlCount = Integer.MAX_VALUE;
				eval = false;
			}
		}
		
		
	}




	public ArrayList <GdlRule> rearrangeRuleComplete (GdlRule rule){
		ArrayList<GdlRule> ret = new ArrayList<GdlRule>();
		List <GdlLiteral> tail = rule.getBody();
		ret.add(rule);
		for (int i = 0; i < tail.size(); i++){
			ArrayList <GdlRule> temp = new ArrayList<GdlRule>();
			for (GdlRule r : ret){
				temp.addAll(rearrangeRule(r, i));
			}
			ret.addAll(temp);

		}
		return ret;
	}
	public ArrayList <GdlRule> rearrangeRule(GdlRule rule, int index){
		ArrayList<GdlRule> ret = new ArrayList<GdlRule>();
		List <GdlLiteral> tail = rule.getBody();
		GdlLiteral old = tail.get(index);
		for (int i = index; i<tail.size(); i++){
			ArrayList<GdlLiteral> newTail = new ArrayList<GdlLiteral> ();
			newTail.addAll(tail);
			newTail.set(index, tail.get(i));
			newTail.set(i, old);
			ret.add(GdlPool.getRule(rule.getHead(), newTail));
		}
		return ret;
	}



	public static void main(String[] args) throws GdlFormatException, SymbolFormatException {

		CloudGameRepository repo = new CloudGameRepository("games.ggp.org/dresden");
		Game g = repo.getGame("tictactoe");
		List <Gdl> origRules = g.getRules();
		Set <Gdl> rules = new HashSet<Gdl>();
		rules.addAll(origRules);
		EvaluationProver prover = new EvaluationProver(rules);
		ArrayList <GdlRule> res = prover.rearrangeRuleComplete((GdlRule)GdlFactory.create("(<= (column ?z) (true (cell ?x ?y1 ?z))(distinct ?z b)(succ ?y1 ?y2)(true (cell ?x ?y2 ?z))(succ ?y2 ?y3)(true (cell ?x ?y3 ?z))(succ ?y3 ?y4)(true (cell ?x ?y4 ?z)))"));

		System.out.println(res.size());
		//for (GdlRule r : res)
		//System.out.println(r.toString());
	}
	//	newSet.add(GdlPool.getRule(r.getHead(), newBody));



	public boolean checkCorrect (ArrayList <MachineState> machineStates, StateMachine machine){


		System.out.println(reorderedGameDescription.size()+  " " + gameDescription.size() );
		boolean ret = true;
		HashMap<MachineState, List<Move>> moves = new HashMap<MachineState, List<Move>> ();

		for (MachineState m : machineStates){
			try {
				List <Move> temp = machine.getRandomJointMove(m);
				moves.put(m, temp);
			} catch (MoveDefinitionException e) {
				e.printStackTrace();
			}	
		}


		reorderedKnowledgeBase = new KnowledgeBase(reorderedGameDescription);
		for (MachineState m : machineStates){
			try {
				eval = false;
				knowledgeBase = new KnowledgeBase(reorderedGameDescription);
				Set<GdlSentence> solNew = askAll((GdlSentence)GdlFactory.create("(legal ?p ?x)"), ProverQueryBuilder.getContext(m, machine.getRoles(),moves.get(m)));;
				knowledgeBase = new KnowledgeBase(gameDescription);
				Set <GdlSentence> solOrig = askAll((GdlSentence)GdlFactory.create("(legal ?p ?x)"), ProverQueryBuilder.getContext(m, machine.getRoles(),moves.get(m)));
				if (!solNew.equals(solOrig)){
					ret = false;
				}
				knowledgeBase = new KnowledgeBase(reorderedGameDescription);
				 solNew = askAll((GdlSentence)GdlFactory.create("(next ?x)"), ProverQueryBuilder.getContext(m, machine.getRoles(),moves.get(m)));;
				knowledgeBase = new KnowledgeBase(gameDescription);
				 solOrig = askAll((GdlSentence)GdlFactory.create("(next ?x)"), ProverQueryBuilder.getContext(m, machine.getRoles(),moves.get(m)));
				if (!solNew.equals(solOrig)){
					ret = false;
				}
				knowledgeBase = new KnowledgeBase(reorderedGameDescription);
				solNew = askAll((GdlSentence)GdlFactory.create("(goal ?p ?x)"), ProverQueryBuilder.getContext(m, machine.getRoles(),moves.get(m)));;
				knowledgeBase = new KnowledgeBase(gameDescription);
				 solOrig = askAll((GdlSentence)GdlFactory.create("(goal ?p ?x)"), ProverQueryBuilder.getContext(m, machine.getRoles(),moves.get(m)));
				if (!solNew.equals(solOrig)){
					ret = false;
				}
				knowledgeBase = new KnowledgeBase(reorderedGameDescription);
				solNew = askAll((GdlSentence)GdlFactory.create("(terminal ?x)"), ProverQueryBuilder.getContext(m, machine.getRoles(),moves.get(m)));;
				knowledgeBase = new KnowledgeBase(gameDescription);
				 solOrig = askAll((GdlSentence)GdlFactory.create("(terminal ?x)"), ProverQueryBuilder.getContext(m, machine.getRoles(),moves.get(m)));
				if (!solNew.equals(solOrig)){
					ret = false;
				}
				
			} catch (GdlFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SymbolFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		}
		
		if (ret)
			knowledgeBase = new KnowledgeBase(reorderedGameDescription);
		else

			knowledgeBase = new KnowledgeBase(gameDescription);
		return ret;
	}


	public void results (ArrayList <MachineState> m, StateMachine machine){
		System.out.println("RUNNING ORIGINAL RULE SET");
		knowledgeBase = new KnowledgeBase(gameDescription);
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

		knowledgeBase = new KnowledgeBase(reorderedGameDescription);
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


	}












	private Set<GdlSentence> ask(GdlSentence query, Set<GdlSentence> context, boolean askOne)
	{
		if (eval){
			//System.out.println("eval");
			if (tooLong)
				return null;
		}

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
		if (eval){
			//	System.out.println("eval");
			if (count > controlCount){
				tooLong = true;
				//System.out.println("check control count: " + controlCount);
				return;
			}

		}

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
		tooLong = false;
		return ask(query, context, false);

	}

	private void askDistinct(GdlDistinct distinct, LinkedList<GdlLiteral> goals, KnowledgeBase context, Substitution theta, ProverCache cache, VariableRenamer renamer, boolean askOne, Set<Substitution> results, Set<GdlSentence> alreadyAsking)
	{
		if (eval){
			if (tooLong)
				return;
		}
		if (!distinct.getArg1().equals(distinct.getArg2()))
		{
			ask(goals, context, theta, cache, renamer, askOne, results, alreadyAsking);
		}
	}

	private void askNot(GdlNot not, LinkedList<GdlLiteral> goals, KnowledgeBase context, Substitution theta, ProverCache cache, VariableRenamer renamer, boolean askOne, Set<Substitution> results, Set<GdlSentence> alreadyAsking)
	{
		if (eval){
			if (tooLong)
				return;
		}

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
		if (eval){
			if (tooLong)
				return null;
		}
		Set<GdlSentence> results = ask(query, context, true);
		return (results.size() > 0) ? results.iterator().next() : null;
	}

	private void askOr(GdlOr or, LinkedList<GdlLiteral> goals, KnowledgeBase context, Substitution theta, ProverCache cache, VariableRenamer renamer, boolean askOne, Set<Substitution> results, Set<GdlSentence> alreadyAsking)
	{
		if (eval){
			if (tooLong)
				return;
		}
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
		if (eval){
			if (tooLong)
				return;
		}
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
