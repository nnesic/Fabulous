package org.ggp.base.player.gamer.statemachine.sample;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ggp.base.util.gdl.factory.GdlFactory;
import org.ggp.base.util.gdl.factory.exceptions.GdlFormatException;
import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.prover.aima.EvaluationProver;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.query.ProverQueryBuilder;
import org.ggp.base.util.symbol.factory.exceptions.SymbolFormatException;



public class Test {

	EvaluationProverStateMachine machine;

	public Test (){
		machine = new EvaluationProverStateMachine();
	}

	
	public List<Gdl> createTicTacToe (){
		List <Gdl> description = new LinkedList<Gdl>();

		try {
			description.add(GdlFactory.create("(role player1)"));
			description.add(GdlFactory.create("(role player2)"));
			description.add(GdlFactory.create("(init (heap a 2))"));
			description.add(GdlFactory.create("(init (heap b 2))"));
			description.add(GdlFactory.create("(init (heap c 10))"));
			//description.add(GdlFactory.create("( <= (smaller ?X ?Y)(successor ?X ?Y)))(init (heap d 10))"));
			description.add(GdlFactory.create("(init (control player1))"));
			description.add(GdlFactory.create("(<= (legal ?p noop)(role ?p)(true (control ?x))(distinct ?x ?p))"));
			description.add(GdlFactory.create("(<= (legal ?p (reduce ?x ?n))(true (control ?p))(true (heap ?x ?m))(smaller ?n ?m))"));
			description.add(GdlFactory.create("(<= (next (heap ?x ?n))(does ?p (reduce ?x ?n)))"));
			description.add(GdlFactory.create("(<= (next (heap ?x ?n))(true (heap ?x ?n))(does ?p (reduce ?y ?m))(distinct ?x ?y))"));
			description.add(GdlFactory.create("(<= (next (control ?p2))(true (control ?p1))(next_player ?p1 ?p2))"));
			description.add(GdlFactory.create("(<= terminal(true (heap a 0))(true (heap b 0))(true (heap c 0))(true (heap d 0)))"));
			description.add(GdlFactory.create("(<= (goal ?p 0)(true (control ?p)))"));
			description.add(GdlFactory.create("(<= (goal ?p 100)(true (control ?p1))(next_player ?p ?p1))"));
			description.add(GdlFactory.create("(<= (smaller ?x ?y)(succ ?x ?y))"));
			description.add(GdlFactory.create("(<= (smaller ?x ?y)(succ ?x ?z)(smaller ?z ?y))"));
			description.add(GdlFactory.create("(next_player player1 player2)"));
			description.add(GdlFactory.create("(next_player player2 player1)"));
			description.add(GdlFactory.create("(succ 0 1)"));
			description.add(GdlFactory.create("(succ 1 2)"));
			description.add(GdlFactory.create("(succ 2 3)"));
			description.add(GdlFactory.create("(succ 3 4)"));
			description.add(GdlFactory.create("(succ 4 5)"));
			description.add(GdlFactory.create("(succ 5 6)"));
			description.add(GdlFactory.create("(succ 6 7)"));
			description.add(GdlFactory.create("(succ 7 8)"));
			description.add(GdlFactory.create("(succ 8 9)"));
			description.add(GdlFactory.create("(succ 9 10)"));
			description.add(GdlFactory.create("(succ 10 11)"));
			description.add(GdlFactory.create("(succ 11 12)"));
			description.add(GdlFactory.create("(succ 12 13)"));
			description.add(GdlFactory.create("(succ 13 14)"));
			description.add(GdlFactory.create("(succ 14 15)"));
			description.add(GdlFactory.create("(succ 15 16)"));
			description.add(GdlFactory.create("(succ 16 17)"));
			description.add(GdlFactory.create("(succ 17 18)"));
			description.add(GdlFactory.create("(succ 18 19)"));
			description.add(GdlFactory.create("(succ 19 20)"));
			description.add(GdlFactory.create("(succ 20 21)"));
			description.add(GdlFactory.create("(succ 21 22)"));
			description.add(GdlFactory.create("(succ 22 23)"));
			description.add(GdlFactory.create("(succ 23 24)"));
			description.add(GdlFactory.create("(succ 24 25)"));
			description.add(GdlFactory.create("(succ 25 26)"));
			description.add(GdlFactory.create("(succ 26 27)"));

		} catch (GdlFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SymbolFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return description;


	}

	public ArrayList<MachineState> generateRandomStates (int n){
		LinkedList<MachineState> temp = new LinkedList<MachineState>();
		ArrayList<MachineState> ret = new ArrayList<MachineState>();
		MachineState init = machine.getInitialState();
		temp.add(init);
		ret.add(init);
		MachineState current = init;
		while (ret.size() < n){



			if (machine.isTerminal(current)){
				System.out.println("terminal");
				current = machine.getInitialState();
				continue;
			}
			else {
				try {
					current = machine.getRandomNextState(current);
					machine.getRandomNextState(current);
					if (! ret.contains(current)){
						ret.add(current);
					}
				} catch (MoveDefinitionException e) {				
					e.printStackTrace();
					current = machine.getInitialState();
				} catch (TransitionDefinitionException e) {			
					e.printStackTrace();
					current = machine.getInitialState();
				}
			}
		}
		return ret;
	}



	public static void main(String[] args) throws GdlFormatException, SymbolFormatException, MoveDefinitionException, TransitionDefinitionException {
		Test t = new Test ();
		//t.createCheckers();
		
		List <Gdl> game = t.createTicTacToe();
		t.machine.initialize(game);
		//t.machine.initialize(t.createTicTacToe());

		Set <GdlSentence> s = ProverQueryBuilder.getContext(t.machine.getInitialState());
		System.out.println(s.toString());
		System.out.println((t.machine.getRandomNextState(t.machine.getInitialState()).toString()));
		EvaluationProver p = new EvaluationProver(new HashSet<Gdl>(game));
		ArrayList<MachineState> randomStates = t.generateRandomStates(10);
		p.setTrainStates(randomStates);

		p.evaluate(randomStates, t.machine);
		ArrayList<MachineState> randomStates2 = t.generateRandomStates(10);
		p.results(randomStates2, t.machine);



		/*for (MachineState m : randomStates){
			System.out.println(m.toString());
		}*/




		/*	for (GdlSentence g :p.stats.keySet()){

			System.out.println(g.toString() + "       " + p.getLitCount(g) + " " + p.getLitSize(g) + " " + p.getLitSuccess(g) + " " + p.getLitInstances(g) );
		}
		try {
			System.out.println("\n\n\n");
			t.machine.getRandomJointMove(t.machine.getInitialState());

		} catch (MoveDefinitionException e) {
			e.printStackTrace();
		}*/
		System.out.println("\n\n\n");

		/*for (MachineState state : randomStates){
			System.out.println(state.toString());
		}*/
		/*s = ProverQueryBuilder.getContext(t.machine.getRandomNextState(randomStates.get(4)), t.machine.getRoles(), t.machine.getRandomJointMove(randomStates.get(4)));
		System.out.println(s.toString());
		Set <GdlSentence> a = p.askAll((GdlSentence)GdlFactory.create("(next ?x)"), s);*/
		//Set <GdlSentence> a = p.askAll(ProverQueryBuilder.getTerminalQuery(), s);
		//System.out.println(p.count);
		/*	for (GdlSentence sen : a)
			System.out.println(sen.toString());*/

		//Set <Gdl> thingy = p.order(0);
		/*for (Gdl g : thingy){
			System.out.println(g.toString());
		}*/
		/*ArrayList<MachineState> randomStates2 = t.generateRandomStates(20);
		p.results(randomStates2, t.machine);

		System.out.println("\n\n\n");
		/*for (MachineState m : randomStates)
			System.out.println(m.toString());
		System.out.println();
		for (MachineState m : randomStates2)
			System.out.println(m.toString());*/
		System.out.println("done");



	}

	public List<Gdl> createConnect3 (){
		List <Gdl> description = new LinkedList<Gdl>();

		try {
			description.add(GdlFactory.create("(role white)")); 
			description.add(GdlFactory.create("(role black)"));
			description.add(GdlFactory.create("(init (cell 1 1 b))"));
			description.add(GdlFactory.create("(init (cell 1 2 b))"));
			description.add(GdlFactory.create("(init (cell 1 3 b))"));
			description.add(GdlFactory.create("(init (cell 1 4 b))"));
			description.add(GdlFactory.create("(init (cell 1 5 b))"));
			description.add(GdlFactory.create("(init (cell 1 6 b))"));
			description.add(GdlFactory.create("(init (cell 2 1 b))"));
			description.add(GdlFactory.create("(init (cell 2 2 b))"));
			description.add(GdlFactory.create("(init (cell 2 3 b))"));
			description.add(GdlFactory.create("(init (cell 2 4 b))"));
			description.add(GdlFactory.create("(init (cell 2 5 b))"));
			description.add(GdlFactory.create("(init (cell 2 6 b))"));
			description.add(GdlFactory.create("(init (cell 3 1 b))"));
			description.add(GdlFactory.create("(init (cell 3 2 b))"));
			description.add(GdlFactory.create("(init (cell 3 3 b))"));
			description.add(GdlFactory.create("(init (cell 3 4 b))"));
			description.add(GdlFactory.create("(init (cell 3 5 b))"));
			description.add(GdlFactory.create("(init (cell 3 6 b))"));
			description.add(GdlFactory.create("(init (cell 4 1 b))"));
			description.add(GdlFactory.create("(init (cell 4 2 b))"));
			description.add(GdlFactory.create("(init (cell 4 3 b))"));
			description.add(GdlFactory.create("(init (cell 4 4 b))"));
			description.add(GdlFactory.create("(init (cell 4 5 b))"));
			description.add(GdlFactory.create("(init (cell 4 6 b))"));
			description.add(GdlFactory.create("(init (cell 5 1 b))"));
			description.add(GdlFactory.create("(init (cell 5 2 b))"));
			description.add(GdlFactory.create("(init (cell 5 3 b))"));
			description.add(GdlFactory.create("(init (cell 5 4 b))"));
			description.add(GdlFactory.create("(init (cell 5 5 b))"));
			description.add(GdlFactory.create("(init (cell 5 6 b))"));
			description.add(GdlFactory.create("(init (cell 6 1 b))"));
			description.add(GdlFactory.create("(init (cell 6 2 b))"));
			description.add(GdlFactory.create("(init (cell 6 3 b))"));
			description.add(GdlFactory.create("(init (cell 6 4 b))"));
			description.add(GdlFactory.create("(init (cell 6 5 b))"));
			description.add(GdlFactory.create("(init (cell 6 6 b))"));
			description.add(GdlFactory.create("(init (cell 7 1 b))"));
			description.add(GdlFactory.create("(init (cell 7 2 b))"));
			description.add(GdlFactory.create("(init (cell 7 3 b))"));
			description.add(GdlFactory.create("(init (cell 7 4 b))"));
			description.add(GdlFactory.create("(init (cell 7 5 b))"));
			description.add(GdlFactory.create("(init (cell 7 6 b))"));
			description.add(GdlFactory.create("(init (control white))"));
			description.add(GdlFactory.create("(succ 1 2)"));
			description.add(GdlFactory.create("(succ 2 3)"));
			description.add(GdlFactory.create("(succ 3 4)"));
			description.add(GdlFactory.create("(succ 4 5)"));
			description.add(GdlFactory.create("(succ 5 6)"));
			description.add(GdlFactory.create("(succ 6 7)"));
			description.add(GdlFactory.create("(<= (cm ?c ?r)(or (true (cell ?c ?r x))(true (cell ?c ?r o))))"));
			description.add(GdlFactory.create("(<= (sequential ?a ?b ?c ?d)(succ ?a ?b)(succ ?b ?c)(succ ?c ?d))"));
			description.add(GdlFactory.create("(<= (top-unused ?c ?r)(true (cell ?c ?r b))(cm ?c ?s)(succ ?s ?r))"));   
			description.add(GdlFactory.create("(<= (top-unused ?c 1)(true (cell ?c 1 b)))"));
			description.add(GdlFactory.create("(<= (plays-on ?c ?r)(does ?x (drop ?c))(top-unused ?c ?r))"));
			description.add(GdlFactory.create("(<= (next (cell ?c ?r ?x))(true (cell ?c ?r ?x))(not (plays-on ?c ?r)))"));
			description.add(GdlFactory.create("(<= (next (control white))(true (control black)))"));
			description.add(GdlFactory.create("(<= (next (control black))(true (control white)))"));
			description.add(GdlFactory.create("(<= (legal ?x (drop ?c))(true (cell ?c 6 b))(true (control ?x)))"));
			description.add(GdlFactory.create("(<= (legal white noop)(true (control black)))"));
			description.add(GdlFactory.create("(<= (legal black noop)(true (control white)))"));
			description.add(GdlFactory.create("(<= (next (cell ?c ?r x))(does white (drop ?c))(top-unused ?c ?r))"));
			description.add(GdlFactory.create("(<= (next (cell ?c ?r o))(does black (drop ?c))(top-unused ?c ?r))"));
			description.add(GdlFactory.create("(<= (row ?x)(sequential ?a ?b ?c ?d)(true (cell ?a ?r ?x))(true (cell ?b ?r ?x))(true (cell ?c ?r ?x))(true (cell ?d ?r ?x)))"));
			description.add(GdlFactory.create("(<= (col  ?x)(sequential ?a ?b ?c ?d)(true (cell ?e ?a ?x))(true (cell ?e ?b ?x))(true (cell ?e ?c ?x))(true (cell ?e ?d ?x)))"));
			description.add(GdlFactory.create("(<= (diag1 ?x)(sequential ?a ?b ?c ?d)(sequential ?e ?f ?g ?h)(true (cell ?a ?e ?x))(true (cell ?b ?f ?x))(true (cell ?c ?g ?x))(true (cell ?d ?h ?x)))"));
			description.add(GdlFactory.create("(<= (diag2 ?x)(sequential ?a ?b ?c ?d)(sequential ?e ?f ?g ?h) (true (cell ?a ?h ?x))(true (cell ?b ?g ?x))(true (cell ?c ?f ?x))(true (cell ?d ?e ?x)))"));
			description.add(GdlFactory.create("(<= (connfour ?x)(or (col ?x)(row ?x)(diag1 ?x)(diag2 ?x)))"));
			description.add(GdlFactory.create("(<= (goal ?x 50)(not (connfour x))(not (connfour o))(role ?x))"));
			description.add(GdlFactory.create("(<= (goal white 100)(connfour x))"));
			description.add(GdlFactory.create("(<= (goal black 0)(connfour x))"));
			description.add(GdlFactory.create("(<= (goal white 0)(connfour o))"));
			description.add(GdlFactory.create("(<= (goal black 100)(connfour o))"));
			description.add(GdlFactory.create("(<= terminal(or (connfour x)(connfour o)))"));
			description.add(GdlFactory.create("(<= (not-filled)(true (cell ?c 6 b)))"));
			description.add(GdlFactory.create("(<= terminal(not (not-filled)))"));
		} catch (GdlFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SymbolFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return description;

	}

	public List<Gdl> createAsteroids (){
		List <Gdl> description = new LinkedList<Gdl>();

		try {

			description.add(GdlFactory.create("(role ship)"));
			description.add(GdlFactory.create("(init (x 10))"));
			description.add(GdlFactory.create("(init (y 10))"));
			description.add(GdlFactory.create("(init (heading north))"));
			description.add(GdlFactory.create("(init (north-speed 3))"));
			description.add(GdlFactory.create("(init (east-speed 2))"));
			description.add(GdlFactory.create("(init (step 1))"));
			description.add(GdlFactory.create("(<= (legal ship thrust))"));
			description.add(GdlFactory.create("(<= (legal ship (turn clock)))"));
			description.add(GdlFactory.create("(<= (legal ship (turn counter)))"));
			description.add(GdlFactory.create("(<= (next (heading ?h))(true (heading ?h))(does ship thrust))"));
			description.add(GdlFactory.create("(<= (next (heading west))(true (heading north))(does ship (turn counter)))"));
			description.add(GdlFactory.create("(<= (next (heading south))(true (heading west))(does ship (turn counter)))"));
			description.add(GdlFactory.create("(<= (next (heading east))(true (heading south))(does ship (turn counter)))"));
			description.add(GdlFactory.create("(<= (next (heading north))(true (heading east))(does ship (turn counter)))"));
			description.add(GdlFactory.create("(<= (next (heading east))(true (heading north))(does ship (turn clock)))"));
			description.add(GdlFactory.create("(<= (next (heading south))(true (heading east))(does ship (turn clock)))"));
			description.add(GdlFactory.create("(<= (next (heading west))(true (heading south))(does ship (turn clock)))"));
			description.add(GdlFactory.create("(<= (next (heading north))(true (heading west))(does ship (turn clock)))"));
			description.add(GdlFactory.create("(<= (next (north-speed ?s))(true (north-speed ?s))(does ship (turn clock)))"));
			description.add(GdlFactory.create("(<= (next (north-speed ?s))(true (north-speed ?s))(does ship (turn counter)))"));
			description.add(GdlFactory.create("(<= (next (north-speed ?s))(true (north-speed ?s))(true (heading east)))"));
			description.add(GdlFactory.create("(<= (next (north-speed ?s))(true (north-speed ?s))(true (heading west)))"));
			description.add(GdlFactory.create("(<= (next (north-speed ?s2))(true (north-speed ?s1))(true (heading north))(does ship thrust)(speed+ ?s1 ?s2))"));
			description.add(GdlFactory.create("(<= (next (north-speed ?s2))(true (north-speed ?s1))(true (heading south))(does ship thrust)(speed- ?s1 ?s2))"));
			description.add(GdlFactory.create("(<= (next (east-speed ?s))(true (east-speed ?s))(does ship (turn clock)))"));
			description.add(GdlFactory.create("(<= (next (east-speed ?s))(true (east-speed ?s))(does ship (turn counter)))"));
			description.add(GdlFactory.create("(<= (next (east-speed ?s))(true (east-speed ?s))(true (heading north)))"));
			description.add(GdlFactory.create("(<= (next (east-speed ?s))(true (east-speed ?s))(true (heading south)))"));
			description.add(GdlFactory.create("(<= (next (east-speed ?s2))(true (east-speed ?s1))(true (heading east))(does ship thrust)(speed+ ?s1 ?s2))"));
			description.add(GdlFactory.create("(<= (next (east-speed ?s2))(true (east-speed ?s1))(true (heading west))(does ship thrust)(speed- ?s1 ?s2))"));
			description.add(GdlFactory.create("(<= (next (x ?new))(true (x ?old))(true (east-speed ?s))(map+ ?old ?s ?new))"));
			description.add(GdlFactory.create("(<= (next (y ?new))(true (y ?old))(true (north-speed ?s))(map+ ?old ?s ?new))"));
			description.add(GdlFactory.create("(<= (next (step ?n++))(true (step ?n))(succ ?n ?n++))"));
			description.add(GdlFactory.create("(<= terminal stopped)"));
			description.add(GdlFactory.create("(<= terminal timeout)"));
			description.add(GdlFactory.create("(<= (goal ship 100) stopped atplanet)"));
			description.add(GdlFactory.create("(<= (goal ship 50) stopped(not atplanet))"));
			description.add(GdlFactory.create("(<= (goal ship 0)(not stopped))"));
			description.add(GdlFactory.create("(<= stopped (true (north-speed 0))(true (east-speed 0)))"));
			description.add(GdlFactory.create("(<= atplanet(true (x 15))(true (y 5)))"));
			description.add(GdlFactory.create("(<= timeout(true (step 50)))"));
			description.add(GdlFactory.create("(map+ 20 -3 17)"));
			description.add(GdlFactory.create("(map+ 20 -2 18)"));
			description.add(GdlFactory.create("(map+ 20 -1 19)"));
			description.add(GdlFactory.create("(map+ 20 0 20)"));
			description.add(GdlFactory.create("(map+ 20 1 1)"));
			description.add(GdlFactory.create("(map+ 20 2 2)"));
			description.add(GdlFactory.create("(map+ 20 3 3)"));
			description.add(GdlFactory.create("(map+ 19 -3 16)"));
			description.add(GdlFactory.create("(map+ 19 -2 17)"));
			description.add(GdlFactory.create("(map+ 19 -1 18)"));
			description.add(GdlFactory.create("(map+ 19 0 19)"));
			description.add(GdlFactory.create("(map+ 19 1 20)"));
			description.add(GdlFactory.create("(map+ 19 2 1)"));
			description.add(GdlFactory.create("(map+ 19 3 2)"));
			description.add(GdlFactory.create("(map+ 18 -3 15)"));
			description.add(GdlFactory.create("(map+ 18 -2 16)"));
			description.add(GdlFactory.create("(map+ 18 -1 17)"));
			description.add(GdlFactory.create("(map+ 18 0 18)"));
			description.add(GdlFactory.create("(map+ 18 1 19)"));
			description.add(GdlFactory.create("(map+ 18 2 20)"));
			description.add(GdlFactory.create("(map+ 18 3 1)"));
			description.add(GdlFactory.create("(map+ 17 -3 14)"));
			description.add(GdlFactory.create("(map+ 17 -2 15)"));
			description.add(GdlFactory.create("(map+ 17 -1 16)"));
			description.add(GdlFactory.create("(map+ 17 0 17)"));
			description.add(GdlFactory.create("(map+ 17 1 18)"));
			description.add(GdlFactory.create("(map+ 17 2 19)"));
			description.add(GdlFactory.create("(map+ 17 3 20)"));
			description.add(GdlFactory.create("(map+ 16 -3 13)"));
			description.add(GdlFactory.create("(map+ 16 -2 14)"));
			description.add(GdlFactory.create("(map+ 16 -1 15)"));
			description.add(GdlFactory.create("(map+ 16 0 16)"));
			description.add(GdlFactory.create("(map+ 16 1 17)"));
			description.add(GdlFactory.create("(map+ 16 2 18)"));
			description.add(GdlFactory.create("(map+ 16 3 19)"));
			description.add(GdlFactory.create("(map+ 15 -3 12)"));
			description.add(GdlFactory.create("(map+ 15 -2 13)"));
			description.add(GdlFactory.create("(map+ 15 -1 14)"));
			description.add(GdlFactory.create("(map+ 15 0 15)"));
			description.add(GdlFactory.create("(map+ 15 1 16)"));
			description.add(GdlFactory.create("(map+ 15 2 17)"));
			description.add(GdlFactory.create("(map+ 15 3 18)"));
			description.add(GdlFactory.create("(map+ 14 -3 11)"));
			description.add(GdlFactory.create("(map+ 14 -2 12)"));
			description.add(GdlFactory.create("(map+ 14 -1 13)"));
			description.add(GdlFactory.create("(map+ 14 0 14)"));
			description.add(GdlFactory.create("(map+ 14 1 15)"));
			description.add(GdlFactory.create("(map+ 14 2 16)"));
			description.add(GdlFactory.create("(map+ 14 3 17)"));
			description.add(GdlFactory.create("(map+ 13 -3 10)"));
			description.add(GdlFactory.create("(map+ 13 -2 11)"));
			description.add(GdlFactory.create("(map+ 13 -1 12)"));
			description.add(GdlFactory.create("(map+ 13 0 13)"));
			description.add(GdlFactory.create("(map+ 13 1 14)"));
			description.add(GdlFactory.create("(map+ 13 2 15)"));
			description.add(GdlFactory.create("(map+ 13 3 16)"));
			description.add(GdlFactory.create("(map+ 12 -3 9)"));
			description.add(GdlFactory.create("(map+ 12 -2 10)"));
			description.add(GdlFactory.create("(map+ 12 -1 11)"));
			description.add(GdlFactory.create("(map+ 12 0 12)"));
			description.add(GdlFactory.create("(map+ 12 1 13)"));
			description.add(GdlFactory.create("(map+ 12 2 14)"));
			description.add(GdlFactory.create("(map+ 12 3 15)"));
			description.add(GdlFactory.create("(map+ 11 -3 8)"));
			description.add(GdlFactory.create("(map+ 11 -2 9)"));
			description.add(GdlFactory.create("(map+ 11 -1 10)"));
			description.add(GdlFactory.create("(map+ 11 0 11)"));
			description.add(GdlFactory.create("(map+ 11 1 12)"));
			description.add(GdlFactory.create("(map+ 11 2 13)"));
			description.add(GdlFactory.create("(map+ 11 3 14)"));
			description.add(GdlFactory.create("(map+ 10 -3 7)"));
			description.add(GdlFactory.create("(map+ 10 -2 8)"));
			description.add(GdlFactory.create("(map+ 10 -1 9)"));
			description.add(GdlFactory.create("(map+ 10 0 10)"));
			description.add(GdlFactory.create("(map+ 10 1 11)"));
			description.add(GdlFactory.create("(map+ 10 2 12)"));
			description.add(GdlFactory.create("(map+ 10 3 13)"));
			description.add(GdlFactory.create("(map+ 9 -3 6)"));
			description.add(GdlFactory.create("(map+ 9 -2 7)"));
			description.add(GdlFactory.create("(map+ 9 -1 8)"));
			description.add(GdlFactory.create("(map+ 9 0 9)"));
			description.add(GdlFactory.create("(map+ 9 1 10)"));
			description.add(GdlFactory.create("(map+ 9 2 11)"));
			description.add(GdlFactory.create("(map+ 9 3 12)"));
			description.add(GdlFactory.create("(map+ 8 -3 5)"));
			description.add(GdlFactory.create("(map+ 8 -2 6)"));
			description.add(GdlFactory.create("(map+ 8 -1 7)"));
			description.add(GdlFactory.create("(map+ 8 0 8)"));
			description.add(GdlFactory.create("(map+ 8 1 9)"));
			description.add(GdlFactory.create("(map+ 8 2 10)"));
			description.add(GdlFactory.create("(map+ 8 3 11)"));
			description.add(GdlFactory.create("(map+ 7 -3 4)"));
			description.add(GdlFactory.create("(map+ 7 -2 5)"));
			description.add(GdlFactory.create("(map+ 7 -1 6)"));
			description.add(GdlFactory.create("(map+ 7 0 7)"));
			description.add(GdlFactory.create("(map+ 7 1 8)"));
			description.add(GdlFactory.create("(map+ 7 2 9)"));
			description.add(GdlFactory.create("(map+ 7 3 10)"));
			description.add(GdlFactory.create("(map+ 6 -3 3)"));
			description.add(GdlFactory.create("(map+ 6 -2 4)"));
			description.add(GdlFactory.create("(map+ 6 -1 5)"));
			description.add(GdlFactory.create("(map+ 6 0 6)"));
			description.add(GdlFactory.create("(map+ 6 1 7)"));
			description.add(GdlFactory.create("(map+ 6 2 8)"));
			description.add(GdlFactory.create("(map+ 6 3 9)"));
			description.add(GdlFactory.create("(map+ 5 -3 2)"));
			description.add(GdlFactory.create("(map+ 5 -2 3)"));
			description.add(GdlFactory.create("(map+ 5 -1 4)"));
			description.add(GdlFactory.create("(map+ 5 0 5)"));
			description.add(GdlFactory.create("(map+ 5 1 6)"));
			description.add(GdlFactory.create("(map+ 5 2 7)"));
			description.add(GdlFactory.create("(map+ 5 3 8)"));
			description.add(GdlFactory.create("(map+ 4 -3 1)"));
			description.add(GdlFactory.create("(map+ 4 -2 2)"));
			description.add(GdlFactory.create("(map+ 4 -1 3)"));
			description.add(GdlFactory.create("(map+ 4 0 4)"));
			description.add(GdlFactory.create("(map+ 4 1 5)"));
			description.add(GdlFactory.create("(map+ 4 2 6)"));
			description.add(GdlFactory.create("(map+ 4 3 7)"));
			description.add(GdlFactory.create("(map+ 3 -3 20)"));
			description.add(GdlFactory.create("(map+ 3 -2 1)"));
			description.add(GdlFactory.create("(map+ 3 -1 2)"));
			description.add(GdlFactory.create("(map+ 3 0 3)"));
			description.add(GdlFactory.create("(map+ 3 1 4)"));
			description.add(GdlFactory.create("(map+ 3 2 5)"));
			description.add(GdlFactory.create("(map+ 3 3 6)"));
			description.add(GdlFactory.create("(map+ 2 -3 19)"));
			description.add(GdlFactory.create("(map+ 2 -2 20)"));
			description.add(GdlFactory.create("(map+ 2 -1 1)"));
			description.add(GdlFactory.create("(map+ 2 0 2)"));
			description.add(GdlFactory.create("(map+ 2 1 3)"));
			description.add(GdlFactory.create("(map+ 2 2 4)"));
			description.add(GdlFactory.create("(map+ 2 3 5)"));
			description.add(GdlFactory.create("(map+ 1 -3 18)"));
			description.add(GdlFactory.create("(map+ 1 -2 19)"));
			description.add(GdlFactory.create("(map+ 1 -1 20)"));
			description.add(GdlFactory.create("(map+ 1 0 1)"));
			description.add(GdlFactory.create("(map+ 1 1 2)"));
			description.add(GdlFactory.create("(map+ 1 2 3)"));
			description.add(GdlFactory.create("(map+ 1 3 4)"));
			description.add(GdlFactory.create("(speed+ -3 -2)"));
			description.add(GdlFactory.create("(speed+ -2 -1)"));
			description.add(GdlFactory.create("(speed+ -1 0)"));
			description.add(GdlFactory.create("(speed+ 0 1)"));
			description.add(GdlFactory.create("(speed+ 1 2)"));
			description.add(GdlFactory.create("(speed+ 2 3)"));
			description.add(GdlFactory.create("(speed+ 3 3)"));
			description.add(GdlFactory.create("(speed- 3 2)"));
			description.add(GdlFactory.create("(speed- 2 1)"));
			description.add(GdlFactory.create("(speed- 1 0)"));
			description.add(GdlFactory.create("(speed- 0 -1)"));
			description.add(GdlFactory.create("(speed- -1 -2)"));
			description.add(GdlFactory.create("(speed- -2 -3)"));
			description.add(GdlFactory.create("(speed- -3 -3)"));
			description.add(GdlFactory.create("(succ 1 2)"));
			description.add(GdlFactory.create("(succ 2 3)"));
			description.add(GdlFactory.create("(succ 3 4)"));
			description.add(GdlFactory.create("(succ 4 5)"));
			description.add(GdlFactory.create("(succ 5 6)"));
			description.add(GdlFactory.create("(succ 6 7)"));
			description.add(GdlFactory.create("(succ 7 8)"));
			description.add(GdlFactory.create("(succ 8 9)"));
			description.add(GdlFactory.create("(succ 9 10)"));
			description.add(GdlFactory.create("(succ 10 11)"));
			description.add(GdlFactory.create("(succ 11 12)"));
			description.add(GdlFactory.create("(succ 12 13)"));
			description.add(GdlFactory.create("(succ 13 14)"));
			description.add(GdlFactory.create("(succ 14 15)"));
			description.add(GdlFactory.create("(succ 15 16)"));
			description.add(GdlFactory.create("(succ 16 17)"));
			description.add(GdlFactory.create("(succ 17 18)"));
			description.add(GdlFactory.create("(succ 18 19)"));
			description.add(GdlFactory.create("(succ 19 20)"));
			description.add(GdlFactory.create("(succ 20 21)"));
			description.add(GdlFactory.create("(succ 21 22)"));
			description.add(GdlFactory.create("(succ 22 23)"));
			description.add(GdlFactory.create("(succ 23 24)"));
			description.add(GdlFactory.create("(succ 24 25)"));
			description.add(GdlFactory.create("(succ 25 26)"));
			description.add(GdlFactory.create("(succ 26 27)"));
			description.add(GdlFactory.create("(succ 27 28)"));
			description.add(GdlFactory.create("(succ 28 29)"));
			description.add(GdlFactory.create("(succ 29 30)"));
			description.add(GdlFactory.create("(succ 30 31)"));
			description.add(GdlFactory.create("(succ 31 32)"));
			description.add(GdlFactory.create("(succ 32 33)"));
			description.add(GdlFactory.create("(succ 33 34)"));
			description.add(GdlFactory.create("(succ 34 35)"));
			description.add(GdlFactory.create("(succ 35 36)"));
			description.add(GdlFactory.create("(succ 36 37)"));
			description.add(GdlFactory.create("(succ 37 38)"));
			description.add(GdlFactory.create("(succ 38 39)"));
			description.add(GdlFactory.create("(succ 39 40)"));
			description.add(GdlFactory.create("(succ 40 41)"));
			description.add(GdlFactory.create("(succ 41 42)"));
			description.add(GdlFactory.create("(succ 42 43)"));
			description.add(GdlFactory.create("(succ 43 44)"));
			description.add(GdlFactory.create("(succ 44 45)"));
			description.add(GdlFactory.create("(succ 45 46)"));
			description.add(GdlFactory.create("(succ 46 47)"));
			description.add(GdlFactory.create("(succ 47 48)"));
			description.add(GdlFactory.create("(succ 48 49)"));
			description.add(GdlFactory.create("(succ 49 50)")); 
		} catch (GdlFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SymbolFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return description;

	}
	
	public List<Gdl> createCheckers(){

		List <Gdl> description = new LinkedList<Gdl>();

		try {

			description.add(GdlFactory.create("role white)"));
			description.add(GdlFactory.create("(role black)"));
			description.add(GdlFactory.create("(init (cell a 1 b))"));
			description.add(GdlFactory.create("(init (cell a 3 b))"));
			description.add(GdlFactory.create("(init (cell a 4 b))"));
			description.add(GdlFactory.create("(init (cell a 5 b))"));
			description.add(GdlFactory.create("(init (cell a 7 b))"));
			description.add(GdlFactory.create("(init (cell b 2 b))"));
			description.add(GdlFactory.create("(init (cell b 4 b))"));
			description.add(GdlFactory.create("(init (cell b 5 b))"));
			description.add(GdlFactory.create("(init (cell b 6 b))"));
			description.add(GdlFactory.create("(init (cell b 8 b))"));
			description.add(GdlFactory.create("(init (cell c 1 b))"));
			description.add(GdlFactory.create("(init (cell c 3 b))"));
			description.add(GdlFactory.create("(init (cell c 4 b))"));
			description.add(GdlFactory.create("(init (cell c 5 b))"));
			description.add(GdlFactory.create("(init (cell c 7 b))"));
			description.add(GdlFactory.create("(init (cell d 2 b))"));
			description.add(GdlFactory.create("(init (cell d 4 b))"));
			description.add(GdlFactory.create("(init (cell d 5 b))"));
			description.add(GdlFactory.create("(init (cell d 6 b))"));
			description.add(GdlFactory.create("(init (cell d 8 b))"));
			description.add(GdlFactory.create("(init (cell e 1 b))"));
			description.add(GdlFactory.create("(init (cell e 3 b))"));
			description.add(GdlFactory.create("(init (cell e 4 b))"));
			description.add(GdlFactory.create("(init (cell e 5 b))"));
			description.add(GdlFactory.create("(init (cell e 7 b))"));
			description.add(GdlFactory.create("(init (cell f 2 b))"));
			description.add(GdlFactory.create("(init (cell f 4 b))"));
			description.add(GdlFactory.create("(init (cell f 5 b))"));
			description.add(GdlFactory.create("(init (cell f 6 b))"));
			description.add(GdlFactory.create("(init (cell f 8 b))"));
			description.add(GdlFactory.create("(init (cell g 1 b))"));
			description.add(GdlFactory.create("(init (cell g 3 b))"));
			description.add(GdlFactory.create("(init (cell g 4 b))"));
			description.add(GdlFactory.create("(init (cell g 5 b))"));
			description.add(GdlFactory.create("(init (cell g 7 b))"));
			description.add(GdlFactory.create("(init (cell h 2 b))"));
			description.add(GdlFactory.create("(init (cell h 4 b))"));
			description.add(GdlFactory.create("(init (cell h 5 b))"));
			description.add(GdlFactory.create("(init (cell h 6 b))"));
			description.add(GdlFactory.create("(init (cell h 8 b))"));
			description.add(GdlFactory.create("(init (cell a 2 wp))"));
			description.add(GdlFactory.create("(init (cell b 1 wp))"));
			description.add(GdlFactory.create("(init (cell c 2 wp))"));
			description.add(GdlFactory.create("(init (cell d 1 wp))"));
			description.add(GdlFactory.create("(init (cell e 2 wp))"));
			description.add(GdlFactory.create("(init (cell f 1 wp))"));
			description.add(GdlFactory.create("(init (cell g 2 wp))"));
			description.add(GdlFactory.create("(init (cell h 1 wp))"));
			description.add(GdlFactory.create("(init (cell b 3 wp))"));
			description.add(GdlFactory.create("(init (cell d 3 wp))"));
			description.add(GdlFactory.create("(init (cell f 3 wp))"));
			description.add(GdlFactory.create("(init (cell h 3 wp))"));
			description.add(GdlFactory.create("(init (cell a 8 bp))"));
			description.add(GdlFactory.create("(init (cell c 8 bp))"));
			description.add(GdlFactory.create("(init (cell e 8 bp))"));
			description.add(GdlFactory.create("(init (cell g 8 bp))"));
			description.add(GdlFactory.create("(init (cell h 7 bp))"));
			description.add(GdlFactory.create("(init (cell f 7 bp))"));
			description.add(GdlFactory.create("(init (cell d 7 bp))"));
			description.add(GdlFactory.create("(init (cell b 7 bp))"));
			description.add(GdlFactory.create("(init (cell a 6 bp))"));
			description.add(GdlFactory.create("(init (cell c 6 bp))"));
			description.add(GdlFactory.create("(init (cell e 6 bp))"));
			description.add(GdlFactory.create("(init (cell g 6 bp))"));
			description.add(GdlFactory.create("(init (control white))"));
			description.add(GdlFactory.create("(init (step 1))"));
			description.add(GdlFactory.create("(init (piece_count white 12))"));
			description.add(GdlFactory.create("(init (piece_count black 12))"));
			description.add(GdlFactory.create("(<= (next (cell ?u ?v b))(does ?player (move ?p ?u ?v ?x ?y)))"));
			description.add(GdlFactory.create("(<= (next (cell ?u ?v b))(does ?player (doublejump ?p ?u ?v ?x ?y ?x3 ?y3)))"));
			description.add(GdlFactory.create("(<= (next (cell ?u ?v b))(does ?player (triplejump ?p ?u ?v ?x ?y ?x3 ?y3 ?x4 ?y4)))"));
			description.add(GdlFactory.create("(<= (next (cell ?x ?y ?p))(does ?player (move ?p ?u ?v ?x ?y))(or (distinct ?p wp) (distinct ?y 8))(or (distinct ?p bp) (distinct ?y 1)))"));
			description.add(GdlFactory.create("(<= (next (cell ?x ?y ?p))(does ?player (doublejump ?p ?u ?v ?x3 ?y3 ?x ?y))(or (distinct ?p wp) (distinct ?y 8))(or (distinct ?p bp) (distinct ?y 1)))"));
			description.add(GdlFactory.create("(<= (next (cell ?x ?y ?p))(does ?player (triplejump ?p ?u ?v ?x3 ?y3 ?x4 ?y4 ?x ?y))(or (distinct ?p wp) (distinct ?y 8))(or (distinct ?p bp) (distinct ?y 1)))"));
			description.add(GdlFactory.create("(<= (next (cell ?x ?y ?p))(does ?player (move ?piece ?x1 ?y1 ?x2 ?y2))(true (cell ?x ?y ?p))(not (single_jump_capture ?player ?x1 ?y1 ?x ?y ?x2 ?y2))(different_cells ?x ?y ?x1 ?y1)(different_cells ?x ?y ?x2 ?y2))"));
			description.add(GdlFactory.create("(<= (next (cell ?x ?y ?p))(does ?player (doublejump ?piece ?x1 ?y1 ?x2 ?y2 ?x3 ?y3))(true (cell ?x ?y ?p))(not (single_jump_capture ?player ?x1 ?y1 ?x ?y ?x2 ?y2))(not (single_jump_capture ?player ?x2 ?y2 ?x ?y ?x3 ?y3))(different_cells ?x ?y ?x1 ?y1)(different_cells ?x ?y ?x3 ?y3))"));
			description.add(GdlFactory.create("(<= (next (cell ?x ?y ?p))(does ?player (triplejump ?piece ?x1 ?y1 ?x2 ?y2 ?x3 ?y3 ?x4 ?y4))(true (cell ?x ?y ?p))(not (single_jump_capture ?player ?x1 ?y1 ?x ?y ?x2 ?y2))(not (single_jump_capture ?player ?x2 ?y2 ?x ?y ?x3 ?y3))(not (single_jump_capture ?player ?x3 ?y3 ?x ?y ?x4 ?y4))(different_cells ?x ?y ?x1 ?y1)(different_cells ?x ?y ?x4 ?y4))"));
			description.add(GdlFactory.create("(<= (next (cell ?x ?y b))(does ?player (move ?piece ?x1 ?y1 ?x2 ?y2))(single_jump_capture ?player ?x1 ?y1 ?x ?y ?x2 ?y2))"));
			description.add(GdlFactory.create("(<= (next (cell ?x ?y b))(does ?player (doublejump ?piece ?x1 ?y1 ?x2 ?y2 ?x3 ?y3))(or (single_jump_capture ?player ?x1 ?y1 ?x ?y ?x2 ?y2)(single_jump_capture ?player ?x2 ?y2 ?x ?y ?x3 ?y3)))"));
			description.add(GdlFactory.create("(<= (next (cell ?x ?y b))(does ?player (triplejump ?piece ?x1 ?y1 ?x2 ?y2 ?x3 ?y3 ?x4 ?y4))(or (single_jump_capture ?player ?x1 ?y1 ?x ?y ?x2 ?y2)(single_jump_capture ?player ?x2 ?y2 ?x ?y ?x3 ?y3)(single_jump_capture ?player ?x3 ?y3 ?x ?y ?x4 ?y4)))"));
			description.add(GdlFactory.create("(<= (next (control white))(true (control black)))"));
			description.add(GdlFactory.create("(<= (next (control black))(true (control white)))"));
			description.add(GdlFactory.create("(<= (next (step ?y))(true (step ?x))(succ ?x ?y))"));
			description.add(GdlFactory.create("(<= (next (cell ?x 8 wk))(does white (move wp ?u ?v ?x 8)))"));
			description.add(GdlFactory.create("(<= (next (cell ?x 1 bk))(does black (move bp ?u ?v ?x 1)))"));
			description.add(GdlFactory.create("(<= (next (cell ?x 8 wk))(does white (doublejump wp ?u ?v ?x3 ?y3 ?x 8)))"));
			description.add(GdlFactory.create("(<= (next (cell ?x 1 bk))(does black (doublejump bp ?u ?v ?x3 ?y3 ?x 1)))"));
			description.add(GdlFactory.create("(<= (next (cell ?x 8 wk))(does white (triplejump ?p ?u ?v ?x3 ?y3 ?x4 ?y4 ?x 8)))"));
			description.add(GdlFactory.create("(<= (next (cell ?x 1 bk))(does black (triplejump ?p ?u ?v ?x3 ?y3 ?x4 ?y4 ?x 1)))"));
			description.add(GdlFactory.create("(<= (next (piece_count ?player ?n))(or (does ?player (move ?p ?u ?v ?x ?y))(does ?player (doublejump ?p ?u ?v ?x3 ?y3 ?x ?y))(does ?player (triplejump ?p ?u ?v ?x3 ?y3 ?x4 ?y4 ?x ?y)))(true (piece_count ?player ?n)))"));
			description.add(GdlFactory.create("(<= (next (piece_count white ?n))(does black (move ?p ?x1 ?y1 ?x2 ?y2))(kingmove black ?x1 ?y1 ?x2 ?y2)(true (piece_count white ?n)))"));
			description.add(GdlFactory.create("(<= (next (piece_count white ?lower))(does black (move ?p ?x1 ?y1 ?x2 ?y2))(single_jump_capture black ?x1 ?y1 ?x ?y ?x2 ?y2)(true (piece_count white ?higher))(minus1 ?higher ?lower))"));
			description.add(GdlFactory.create("(<= (next (piece_count white ?lower))(does black (doublejump ?p ?u ?v ?x3 ?y3 ?x ?y))(true (piece_count white ?higher))(minus2 ?higher ?lower))"));
			description.add(GdlFactory.create("(<= (next (piece_count white ?lower))(does black (triplejump ?p ?u ?v ?x3 ?y3 ?x4 ?y4 ?x ?y))(true (piece_count white ?higher))(minus3 ?higher ?lower))"));
			description.add(GdlFactory.create("(<= (next (piece_count black ?n))(does white (move ?p ?x1 ?y1 ?x2 ?y2))(kingmove white ?x1 ?y1 ?x2 ?y2)(true (piece_count black ?n)))"));
			description.add(GdlFactory.create("(<= (next (piece_count black ?lower))(does white (move ?p ?x1 ?y1 ?x2 ?y2))(single_jump_capture white ?x1 ?y1 ?x ?y ?x2 ?y2)(true (piece_count black ?higher))(minus1 ?higher ?lower))"));
			description.add(GdlFactory.create("(<= (next (piece_count black ?lower))(does white (doublejump ?p ?u ?v ?x3 ?y3 ?x ?y))(true (piece_count black ?higher))(minus2 ?higher ?lower))"));
			description.add(GdlFactory.create("(<= (next (piece_count black ?lower))(does white (triplejump ?p ?u ?v ?x3 ?y3 ?x4 ?y4 ?x ?y))(true (piece_count black ?higher))(minus3 ?higher ?lower))"));
			description.add(GdlFactory.create("(<= (legal ?player (move ?piece ?u ?v ?x ?y))(true (control ?player))(true (cell ?u ?v ?piece))(piece_owner_type ?piece ?player pawn)(pawnmove ?player ?u ?v ?x ?y)(true (cell ?x ?y b)))"));
			description.add(GdlFactory.create("(<= (legal ?player (move ?piece ?u ?v ?x ?y))(true (control ?player))(true (cell ?u ?v ?piece))(piece_owner_type ?piece ?player king)(kingmove ?player ?u ?v ?x ?y)(true (cell ?x ?y b)))"));
			description.add(GdlFactory.create("(<= (legal ?player (move ?piece ?u ?v ?x ?y))(true (control ?player))(true (cell ?u ?v ?piece))(piece_owner_type ?piece ?player pawn)(pawnjump ?player ?u ?v ?x ?y)(true (cell ?x ?y b))(single_jump_capture ?player ?u ?v ?c ?d ?x ?y))"));
			description.add(GdlFactory.create("(<= (legal ?player (move ?piece ?u ?v ?x ?y))(true (control ?player))(true (cell ?u ?v ?piece))(piece_owner_type ?piece ?player king)(kingjump ?player ?u ?v ?x ?y)(true (cell ?x ?y b))(single_jump_capture ?player ?u ?v ?c ?d ?x ?y))"));
			description.add(GdlFactory.create("(<= (legal ?player (doublejump ?piece ?u ?v ?x1 ?y1 ?x2 ?y2))(true (control ?player))(true (cell ?u ?v ?piece))(piece_owner_type ?piece ?player pawn)(pawnjump ?player ?u ?v ?x1 ?y1)(true (cell ?x1 ?y1 b))(pawnjump ?player ?x1 ?y1 ?x2 ?y2)(true (cell ?x2 ?y2 b))(different_cells ?u ?v ?x2 ?y2)(single_jump_capture ?player ?u ?v ?c ?d ?x1 ?y1)(single_jump_capture ?player ?x1 ?y1 ?c1 ?d1 ?x2 ?y2))"));
			description.add(GdlFactory.create("(<= (legal ?player (doublejump ?piece ?u ?v ?x1 ?y1 ?x2 ?y2))(true (control ?player))(true (cell ?u ?v ?piece))(piece_owner_type ?piece ?player king)(kingjump ?player ?u ?v ?x1 ?y1)(true (cell ?x1 ?y1 b))(kingjump ?player ?x1 ?y1 ?x2 ?y2)(true (cell ?x2 ?y2 b))(different_cells ?u ?v ?x2 ?y2)(single_jump_capture ?player ?u ?v ?c ?d ?x1 ?y1)(single_jump_capture ?player ?x1 ?y1 ?c1 ?d1 ?x2 ?y2))"));
			description.add(GdlFactory.create("(<= (legal ?player (triplejump ?piece ?u ?v ?x1 ?y1 ?x2 ?y2 ?x3 ?y3))(true (control ?player))(true (cell ?u ?v ?piece))(piece_owner_type ?piece ?player pawn)(pawnjump ?player ?u ?v ?x1 ?y1)(true (cell ?x1 ?y1 b))(pawnjump ?player ?x1 ?y1 ?x2 ?y2)(true (cell ?x2 ?y2 b))(different_cells ?u ?v ?x2 ?y2)(pawnjump ?player ?x2 ?y2 ?x3 ?y3)(true (cell ?x3 ?y3 b))(different_cells ?x1 ?y1 ?x3 ?y3)(single_jump_capture ?player ?u ?v ?c ?d ?x1 ?y1)(single_jump_capture ?player ?x1 ?y1 ?c1 ?d1 ?x2 ?y2)(single_jump_capture ?player ?x2 ?y2 ?c2 ?d2 ?x3 ?y3))"));
			description.add(GdlFactory.create("(<= (legal ?player (triplejump ?piece ?u ?v ?x1 ?y1 ?x2 ?y2 ?x3 ?y3))(true (control ?player))(true (cell ?u ?v ?piece))(piece_owner_type ?piece ?player king)(kingjump ?player ?u ?v ?x1 ?y1)(true (cell ?x1 ?y1 b))(kingjump ?player ?x1 ?y1 ?x2 ?y2)(true (cell ?x2 ?y2 b))(different_cells ?u ?v ?x2 ?y2)(kingjump ?player ?x2 ?y2 ?x3 ?y3)(true (cell ?x3 ?y3 b))(different_cells ?x1 ?y1 ?x3 ?y3)(single_jump_capture ?player ?u ?v ?c ?d ?x1 ?y1)(single_jump_capture ?player ?x1 ?y1 ?c1 ?d1 ?x2 ?y2)(single_jump_capture ?player ?x2 ?y2 ?c2 ?d2 ?x3 ?y3))"));
			description.add(GdlFactory.create("(<= (legal white noop)(true (control black)))"));
			description.add(GdlFactory.create("(<= (legal black noop)(true (control white)))"));
			description.add(GdlFactory.create("(<= (pawnmove white ?u ?v ?x ?y)(next_rank ?v ?y)(or (next_file ?u ?x) (next_file ?x ?u)))"));
			description.add(GdlFactory.create("(<= (pawnmove black ?u ?v ?x ?y)(next_rank ?y ?v)(or (next_file ?u ?x) (next_file ?x ?u)))"));
			description.add(GdlFactory.create("(<= (kingmove ?player ?u ?v ?x ?y)(role ?player)(role ?player2)(pawnmove ?player2 ?u ?v ?x ?y))"));
			description.add(GdlFactory.create("(<= (pawnjump white ?u ?v ?x ?y)(next_rank ?v ?v1)(next_rank ?v1 ?y)(next_file ?u ?x1)(next_file ?x1 ?x))"));
			description.add(GdlFactory.create("(<= (pawnjump white ?u ?v ?x ?y)(next_rank ?v ?v1)(next_rank ?v1 ?y)(next_file ?x ?x1)(next_file ?x1 ?u))"));
			description.add(GdlFactory.create("(<= (pawnjump black ?u ?v ?x ?y)(next_rank ?y ?v1)(next_rank ?v1 ?v)(next_file ?u ?x1)(next_file ?x1 ?x))"));
			description.add(GdlFactory.create("(<= (pawnjump black ?u ?v ?x ?y)(next_rank ?y ?v1)(next_rank ?v1 ?v)(next_file ?x ?x1)(next_file ?x1 ?u))"));
			description.add(GdlFactory.create("(<= (kingjump ?player ?u ?v ?x ?y)(role ?player)(role ?player2)(pawnjump ?player2 ?u ?v ?x ?y))"));
			description.add(GdlFactory.create("(<= (single_jump_capture ?player ?u ?v ?c ?d ?x ?y)(kingjump ?player ?u ?v ?x ?y)(kingmove ?player ?u ?v ?c ?d)(kingmove ?player ?c ?d ?x ?y)(true (cell ?c ?d ?piece))(opponent ?player ?opponent)(piece_owner_type ?piece ?opponent ?type))"));
			description.add(GdlFactory.create("(<= (has_legal_move ?player)(piece_owner_type ?piece ?player ?type)(or (legal ?player (move ?piece ?u ?v ?x ?y))(legal ?player (doublejump ?piece ?u ?v ?x1 ?y1 ?x ?y))(legal ?player (triplejump ?piece ?u ?v ?x1 ?y1 ?x2 ?y2 ?x ?y))))"));
			description.add(GdlFactory.create("(<= (stuck ?player)(role ?player)(not (has_legal_move ?player)))"));
			description.add(GdlFactory.create("(<= terminal(true (control ?player))(stuck ?player))"));
			description.add(GdlFactory.create("(<= terminal(true (piece_count ?player 0)))"));
			description.add(GdlFactory.create("(<= terminal(true (step 102)))"));
			description.add(GdlFactory.create("(<= (goal white 100)(true (piece_count white ?rc))(true (piece_count black ?bc))(greater ?rc ?bc))"));
			description.add(GdlFactory.create("(<= (goal white 50)(true (piece_count white ?x))(true (piece_count black ?x)))"));
			description.add(GdlFactory.create("(<= (goal white 0)(true (piece_count white ?rc))(true (piece_count black ?bc))(greater ?bc ?rc))"));
			description.add(GdlFactory.create("(<= (goal black 100)(true (piece_count white ?rc))(true (piece_count black ?bc))(greater ?bc ?rc))"));
			description.add(GdlFactory.create("(<= (goal black 50)(true (piece_count white ?x))(true (piece_count black ?x)))"));
			description.add(GdlFactory.create("(<= (goal black 0)(true (piece_count white ?rc))(true (piece_count black ?bc))(greater ?rc ?bc))"));
			description.add(GdlFactory.create("(<= (adjacent ?x1 ?x2)(next_file ?x1 ?x2))"));
			description.add(GdlFactory.create("(<= (adjacent ?x1 ?x2)(next_file ?x2 ?x1))"));
			description.add(GdlFactory.create("(<= (adjacent ?y1 ?y2)(next_rank ?y1 ?y2))"));
			description.add(GdlFactory.create("(<= (adjacent ?y1 ?y2)(next_rank ?y2 ?y1))"));
			description.add(GdlFactory.create("(<= (different_cells ?x1 ?y1 ?x2 ?y2)(distinct ?x1 ?x2)(coordinate ?x1)(coordinate ?x2)(coordinate ?y1) (coordinate ?y2))"));
			description.add(GdlFactory.create("(<= (different_cells ?x1 ?y1 ?x2 ?y2)(distinct ?y1 ?y2)(coordinate ?x1)(coordinate ?x2)(coordinate ?y1)(coordinate ?y2))"));
			description.add(GdlFactory.create("(opponent white black)"));
			description.add(GdlFactory.create("(opponent black white)"));
			description.add(GdlFactory.create("(piece_owner_type wk white king)"));
			description.add(GdlFactory.create("(piece_owner_type wp white pawn)"));
			description.add(GdlFactory.create("(piece_owner_type bk black king)"));
			description.add(GdlFactory.create("(piece_owner_type bp black pawn)"));
			description.add(GdlFactory.create("(next_rank 1 2)"));
			description.add(GdlFactory.create("(next_rank 2 3)"));
			description.add(GdlFactory.create("(next_rank 3 4)"));
			description.add(GdlFactory.create("(next_rank 4 5)"));
			description.add(GdlFactory.create("(next_rank 5 6)"));
			description.add(GdlFactory.create("(next_rank 6 7)"));
			description.add(GdlFactory.create("(next_rank 7 8)"));
			description.add(GdlFactory.create("(next_file a b)"));
			description.add(GdlFactory.create("(next_file b c)"));
			description.add(GdlFactory.create("(next_file c d)"));
			description.add(GdlFactory.create("(next_file d e)"));
			description.add(GdlFactory.create("(next_file e f)"));
			description.add(GdlFactory.create("(next_file f g)"));
			description.add(GdlFactory.create("(next_file g h)"));
			description.add(GdlFactory.create("(coordinate 1)"));
			description.add(GdlFactory.create("(coordinate 2)"));
			description.add(GdlFactory.create("(coordinate 3)"));
			description.add(GdlFactory.create("(coordinate 4)"));
			description.add(GdlFactory.create("(coordinate 5)"));
			description.add(GdlFactory.create("(coordinate 6)"));
			description.add(GdlFactory.create("(coordinate 7)"));
			description.add(GdlFactory.create("(coordinate 8)"));
			description.add(GdlFactory.create("(coordinate a)"));
			description.add(GdlFactory.create("(coordinate b)"));
			description.add(GdlFactory.create("(coordinate c)"));
			description.add(GdlFactory.create("(coordinate d)"));
			description.add(GdlFactory.create("(coordinate e)"));
			description.add(GdlFactory.create("(coordinate f)"));
			description.add(GdlFactory.create("(coordinate g)"));
			description.add(GdlFactory.create("(coordinate h)"));
			description.add(GdlFactory.create("(<= (greater ?a ?b)(succ ?b ?a))"));
			description.add(GdlFactory.create("(<= (greater ?a ?b)(distinct ?a ?b)(succ ?c ?a)(greater ?c ?b))"));
			description.add(GdlFactory.create("(minus3 12 9)"));
			description.add(GdlFactory.create("(minus3 11 8)"));
			description.add(GdlFactory.create("(minus3 10 7)"));
			description.add(GdlFactory.create("(minus3 9 6)"));
			description.add(GdlFactory.create("(minus3 8 5)"));
			description.add(GdlFactory.create("(minus3 7 4)"));
			description.add(GdlFactory.create("(minus3 6 3)"));
			description.add(GdlFactory.create("(minus3 5 2)"));
			description.add(GdlFactory.create("(minus3 4 1)"));
			description.add(GdlFactory.create("(minus3 3 0)"));
			description.add(GdlFactory.create("(minus2 12 10)"));
			description.add(GdlFactory.create("(minus2 11 9)"));
			description.add(GdlFactory.create("(minus2 10 8)"));
			description.add(GdlFactory.create("(minus2 9 7)"));
			description.add(GdlFactory.create("(minus2 8 6)"));
			description.add(GdlFactory.create("(minus2 7 5)"));
			description.add(GdlFactory.create("(minus2 6 4)"));
			description.add(GdlFactory.create("(minus2 5 3)"));
			description.add(GdlFactory.create("(minus2 4 2)"));
			description.add(GdlFactory.create("(minus2 3 1)"));
			description.add(GdlFactory.create("(minus2 2 0)"));
			description.add(GdlFactory.create("(minus1 12 11)"));
			description.add(GdlFactory.create("(minus1 11 10)"));
			description.add(GdlFactory.create("(minus1 10 9)"));
			description.add(GdlFactory.create("(minus1 9 8)"));
			description.add(GdlFactory.create("(minus1 8 7)"));
			description.add(GdlFactory.create("(minus1 7 6)"));
			description.add(GdlFactory.create("(minus1 6 5)"));
			description.add(GdlFactory.create("(minus1 5 4)"));
			description.add(GdlFactory.create("(minus1 4 3)"));
			description.add(GdlFactory.create("(minus1 3 2)"));
			description.add(GdlFactory.create("(minus1 2 1)"));
			description.add(GdlFactory.create("(minus1 1 0)"));
			description.add(GdlFactory.create("(succ 0 1)"));
			description.add(GdlFactory.create("(succ 1 2)"));
			description.add(GdlFactory.create("(succ 2 3)"));
			description.add(GdlFactory.create("(succ 3 4)"));
			description.add(GdlFactory.create("(succ 4 5)"));
			description.add(GdlFactory.create("(succ 5 6)"));
			description.add(GdlFactory.create("(succ 6 7)"));
			description.add(GdlFactory.create("(succ 7 8)"));
			description.add(GdlFactory.create("(succ 8 9)"));
			description.add(GdlFactory.create("(succ 9 10)"));
			description.add(GdlFactory.create("(succ 10 11)"));
			description.add(GdlFactory.create("(succ 11 12)"));
			description.add(GdlFactory.create("(succ 12 13)"));
			description.add(GdlFactory.create("(succ 13 14)"));
			description.add(GdlFactory.create("(succ 14 15)"));
			description.add(GdlFactory.create("(succ 15 16)"));
			description.add(GdlFactory.create("(succ 16 17)"));
			description.add(GdlFactory.create("(succ 17 18)"));
			description.add(GdlFactory.create("(succ 18 19)"));
			description.add(GdlFactory.create("(succ 19 20)"));
			description.add(GdlFactory.create("(succ 20 21)"));
			description.add(GdlFactory.create("(succ 21 22)"));
			description.add(GdlFactory.create("(succ 22 23)"));
			description.add(GdlFactory.create("(succ 23 24)"));
			description.add(GdlFactory.create("(succ 24 25)"));
			description.add(GdlFactory.create("(succ 25 26)"));
			description.add(GdlFactory.create("(succ 26 27)"));
			description.add(GdlFactory.create("(succ 27 28)"));
			description.add(GdlFactory.create("(succ 28 29)"));
			description.add(GdlFactory.create("(succ 29 30)"));
			description.add(GdlFactory.create("(succ 30 31)"));
			description.add(GdlFactory.create("(succ 31 32)"));
			description.add(GdlFactory.create("(succ 32 33)"));
			description.add(GdlFactory.create("(succ 33 34)"));
			description.add(GdlFactory.create("(succ 34 35)"));
			description.add(GdlFactory.create("(succ 35 36)"));
			description.add(GdlFactory.create("(succ 36 37)"));
			description.add(GdlFactory.create("(succ 37 38)"));
			description.add(GdlFactory.create("(succ 38 39)"));
			description.add(GdlFactory.create("(succ 39 40)"));
			description.add(GdlFactory.create("(succ 40 41)"));
			description.add(GdlFactory.create("(succ 41 42)"));
			description.add(GdlFactory.create("(succ 42 43)"));
			description.add(GdlFactory.create("(succ 43 44)"));
			description.add(GdlFactory.create("(succ 44 45)"));
			description.add(GdlFactory.create("(succ 45 46)"));
			description.add(GdlFactory.create("(succ 46 47)"));
			description.add(GdlFactory.create("(succ 47 48)"));
			description.add(GdlFactory.create("(succ 48 49)"));
			description.add(GdlFactory.create("(succ 49 50)"));
			description.add(GdlFactory.create("(succ 50 51)"));
			description.add(GdlFactory.create("(succ 51 52)"));
			description.add(GdlFactory.create("(succ 52 53)"));
			description.add(GdlFactory.create("(succ 53 54)"));
			description.add(GdlFactory.create("(succ 54 55)"));
			description.add(GdlFactory.create("(succ 55 56)"));
			description.add(GdlFactory.create("(succ 56 57)"));
			description.add(GdlFactory.create("(succ 57 58)"));
			description.add(GdlFactory.create("(succ 58 59)"));
			description.add(GdlFactory.create("(succ 59 60)"));
			description.add(GdlFactory.create("(succ 60 61)"));
			description.add(GdlFactory.create("(succ 61 62)"));
			description.add(GdlFactory.create("(succ 62 63)"));
			description.add(GdlFactory.create("(succ 63 64)"));
			description.add(GdlFactory.create("(succ 64 65)"));
			description.add(GdlFactory.create("(succ 65 66)"));
			description.add(GdlFactory.create("(succ 66 67)"));
			description.add(GdlFactory.create("(succ 67 68)"));
			description.add(GdlFactory.create("(succ 68 69)"));
			description.add(GdlFactory.create("(succ 69 70)"));
			description.add(GdlFactory.create("(succ 70 71)"));
			description.add(GdlFactory.create("(succ 71 72)"));
			description.add(GdlFactory.create("(succ 72 73)"));
			description.add(GdlFactory.create("(succ 73 74)"));
			description.add(GdlFactory.create("(succ 74 75)"));
			description.add(GdlFactory.create("(succ 75 76)"));
			description.add(GdlFactory.create("(succ 76 77)"));
			description.add(GdlFactory.create("(succ 77 78)"));
			description.add(GdlFactory.create("(succ 78 79)"));
			description.add(GdlFactory.create("(succ 79 80)"));
			description.add(GdlFactory.create("(succ 80 81)"));
			description.add(GdlFactory.create("(succ 81 82)"));
			description.add(GdlFactory.create("(succ 82 83)"));
			description.add(GdlFactory.create("(succ 83 84)"));
			description.add(GdlFactory.create("(succ 84 85)"));
			description.add(GdlFactory.create("(succ 85 86)"));
			description.add(GdlFactory.create("(succ 86 87)"));
			description.add(GdlFactory.create("(succ 87 88)"));
			description.add(GdlFactory.create("(succ 88 89)"));
			description.add(GdlFactory.create("(succ 89 90)"));
			description.add(GdlFactory.create("(succ 90 91)"));
			description.add(GdlFactory.create("(succ 91 92)"));
			description.add(GdlFactory.create("(succ 92 93)"));
			description.add(GdlFactory.create("(succ 93 94)"));
			description.add(GdlFactory.create("(succ 94 95)"));
			description.add(GdlFactory.create("(succ 95 96)"));
			description.add(GdlFactory.create("(succ 96 97)"));
			description.add(GdlFactory.create("(succ 97 98)"));
			description.add(GdlFactory.create("(succ 98 99)"));
			description.add(GdlFactory.create("(succ 99 100)"));
			description.add(GdlFactory.create("(succ 100 101)"));
			description.add(GdlFactory.create("(succ 101 102)"));
			description.add(GdlFactory.create("(succ 102 103)"));
			description.add(GdlFactory.create("(succ 103 104)"));
			description.add(GdlFactory.create("(succ 104 105)"));
			description.add(GdlFactory.create("(succ 105 106)"));
			description.add(GdlFactory.create("(succ 106 107)"));
			description.add(GdlFactory.create("(succ 107 108)"));
			description.add(GdlFactory.create("(succ 108 109)"));
			description.add(GdlFactory.create("(succ 109 110)"));
			description.add(GdlFactory.create("(succ 110 111)"));
			description.add(GdlFactory.create("(succ 111 112)"));
			description.add(GdlFactory.create("(succ 112 113)"));
			description.add(GdlFactory.create("(succ 113 114)"));
			description.add(GdlFactory.create("(succ 114 115)"));
			description.add(GdlFactory.create("(succ 115 116)"));
			description.add(GdlFactory.create("(succ 116 117)"));
			description.add(GdlFactory.create("(succ 117 118)"));
			description.add(GdlFactory.create("(succ 118 119)"));
			description.add(GdlFactory.create("(succ 119 120)"));
			description.add(GdlFactory.create("(succ 120 121)"));
			description.add(GdlFactory.create("(succ 121 122)"));
			description.add(GdlFactory.create("(succ 122 123)"));
			description.add(GdlFactory.create("(succ 123 124)"));
			description.add(GdlFactory.create("(succ 124 125)"));
			description.add(GdlFactory.create("(succ 125 126)"));
			description.add(GdlFactory.create("(succ 126 127)"));
			description.add(GdlFactory.create("(succ 127 128)"));
			description.add(GdlFactory.create("(succ 128 129)"));
			description.add(GdlFactory.create("(succ 129 130)"));
			description.add(GdlFactory.create("(succ 130 131)"));
			description.add(GdlFactory.create("(succ 131 132)"));
			description.add(GdlFactory.create("(succ 132 133)"));
			description.add(GdlFactory.create("(succ 133 134)"));
			description.add(GdlFactory.create("(succ 134 135)"));
			description.add(GdlFactory.create("(succ 135 136)"));
			description.add(GdlFactory.create("(succ 136 137)"));
			description.add(GdlFactory.create("(succ 137 138)"));
			description.add(GdlFactory.create("(succ 138 139)"));
			description.add(GdlFactory.create("(succ 139 140)"));
			description.add(GdlFactory.create("(succ 140 141)"));
			description.add(GdlFactory.create("(succ 141 142)"));
			description.add(GdlFactory.create("(succ 142 143)"));
			description.add(GdlFactory.create("(succ 143 144)"));
			description.add(GdlFactory.create("(succ 144 145)"));
			description.add(GdlFactory.create("(succ 145 146)"));
			description.add(GdlFactory.create("(succ 146 147)"));
			description.add(GdlFactory.create("(succ 147 148)"));
			description.add(GdlFactory.create("(succ 148 149)"));
			description.add(GdlFactory.create("(succ 149 150)"));
			description.add(GdlFactory.create("(succ 150 151)"));
			description.add(GdlFactory.create("(succ 151 152)"));
			description.add(GdlFactory.create("(succ 152 153)"));
			description.add(GdlFactory.create("(succ 153 154)"));
			description.add(GdlFactory.create("(succ 154 155)"));
			description.add(GdlFactory.create("(succ 155 156)"));
			description.add(GdlFactory.create("(succ 156 157)"));
			description.add(GdlFactory.create("(succ 157 158)"));
			description.add(GdlFactory.create("(succ 158 159)"));
			description.add(GdlFactory.create("(succ 159 160)"));
			description.add(GdlFactory.create("(succ 160 161)"));
			description.add(GdlFactory.create("(succ 161 162)"));
			description.add(GdlFactory.create("(succ 162 163)"));
			description.add(GdlFactory.create("(succ 163 164)"));
			description.add(GdlFactory.create("(succ 164 165)"));
			description.add(GdlFactory.create("(succ 165 166)"));
			description.add(GdlFactory.create("(succ 166 167)"));
			description.add(GdlFactory.create("(succ 167 168)"));
			description.add(GdlFactory.create("(succ 168 169)"));
			description.add(GdlFactory.create("(succ 169 170)"));
			description.add(GdlFactory.create("(succ 170 171)"));
			description.add(GdlFactory.create("(succ 171 172)"));
			description.add(GdlFactory.create("(succ 172 173)"));
			description.add(GdlFactory.create("(succ 173 174)"));
			description.add(GdlFactory.create("(succ 174 175)"));
			description.add(GdlFactory.create("(succ 175 176)"));
			description.add(GdlFactory.create("(succ 176 177)"));
			description.add(GdlFactory.create("(succ 177 178)"));
			description.add(GdlFactory.create("(succ 178 179)"));
			description.add(GdlFactory.create("(succ 179 180)"));
			description.add(GdlFactory.create("(succ 180 181)"));
			description.add(GdlFactory.create("(succ 181 182)"));
			description.add(GdlFactory.create("(succ 182 183)"));
			description.add(GdlFactory.create("(succ 183 184)"));
			description.add(GdlFactory.create("(succ 184 185)"));
			description.add(GdlFactory.create("(succ 185 186)"));
			description.add(GdlFactory.create("(succ 186 187)"));
			description.add(GdlFactory.create("(succ 187 188)"));
			description.add(GdlFactory.create("(succ 188 189)"));
			description.add(GdlFactory.create("(succ 189 190)"));
			description.add(GdlFactory.create("(succ 190 191)"));
			description.add(GdlFactory.create("(succ 191 192)"));
			description.add(GdlFactory.create("(succ 192 193)"));
			description.add(GdlFactory.create("(succ 193 194)"));
			description.add(GdlFactory.create("(succ 194 195)"));
			description.add(GdlFactory.create("(succ 195 196)"));
			description.add(GdlFactory.create("(succ 196 197)"));
			description.add(GdlFactory.create("(succ 197 198)"));
			description.add(GdlFactory.create("(succ 198 199)"));
			description.add(GdlFactory.create("(succ 199 200)"));
			description.add(GdlFactory.create("(succ 200 201)"));
		} catch (GdlFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SymbolFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return description;

	}
}
