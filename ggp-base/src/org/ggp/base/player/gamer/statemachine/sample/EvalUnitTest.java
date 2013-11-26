package org.ggp.base.player.gamer.statemachine.sample;

import static org.junit.Assert.assertTrue;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ggp.base.util.game.CloudGameRepository;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.game.GameRepository;
import org.ggp.base.util.gdl.factory.GdlFactory;
import org.ggp.base.util.gdl.factory.exceptions.GdlFormatException;
import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.symbol.factory.exceptions.SymbolFormatException;
import org.junit.Test;

/**
 * Unit tests for the ProverImpl class
 * Author: Nero
 */
public class EvalUnitTest {


	HashSet <Gdl> gameDescription = new HashSet <Gdl> ();
	HashSet<GdlSentence> context = new HashSet<GdlSentence>();
	CloudGameRepository repo = new CloudGameRepository("games.ggp.org/dresden");

	public void createGameRules () throws GdlFormatException, SymbolFormatException{
		gameDescription = new HashSet <Gdl> ();
		context = new HashSet<GdlSentence>();
		//gameDescription.add(GdlFactory.create("(successor 0 1)"));
		//gameDescription.add(GdlFactory.create("(successor 1 2)"));
		gameDescription.add(GdlFactory.create("( <= (smaller ?X ?Y)(successor ?X ?Y))"));
		gameDescription.add(GdlFactory.create("( <= (smaller ?X ?Y)(successor ?Y1 ?Y)(smaller ?X ?Y1))"));
		gameDescription.add(GdlFactory.create("(role black)"));
		gameDescription.add(GdlFactory.create("(role white)"));

		
		gameDescription.add(GdlFactory.create("(foo 2 0)"));
		gameDescription.add(GdlFactory.create("(foo 3 1)"));
		gameDescription.add(GdlFactory.create("(<= (footable ?p )(or (foo ?p 2) (foo ?p 1)))"));
		
		
		gameDescription.add(GdlFactory.create("(foonot 2 0)"));
		gameDescription.add(GdlFactory.create("(foonot 3 1)"));
		gameDescription.add(GdlFactory.create("(<= (footableNot ?p )(foonot ?p 1) (not (foonot ?p 0)))"));
		
		
		
		gameDescription.add(GdlFactory.create("(foo2 2 0)"));
		gameDescription.add(GdlFactory.create("(foo2 3 1)"));
		gameDescription.add(GdlFactory.create("(<= (footable2 ?p )(or (foo ?p 0) (foo ?p 1)))"));
		
		gameDescription.add(GdlFactory.create("(foonot2 2 0)"));
		gameDescription.add(GdlFactory.create("(foonot2 3 1)"));
		gameDescription.add(GdlFactory.create("(<= (footableNot2 ?p )(foonot2 ?p 1) (not (foonot2 ?p 1)))"));
		
		//gameDescription.add(GdlFactory.create("(<= (footableDistint ?p )(foonot2 ?p 1) (distinct ( ?p )))"));
		
		
		gameDescription.add(GdlFactory.create("(foo3 2 0)"));
		gameDescription.add(GdlFactory.create("(foo3 3 1)"));
		gameDescription.add(GdlFactory.create("(<= (footable3 ?p )(or (foo ?p 3) (foo ?p 3)))"));
		
		gameDescription.add(GdlFactory.create("(<= (legal ?p foo)(role ?p)(true (control ?p)))"));
		gameDescription.add(GdlFactory.create("(<= (legal ?p noop)(role ?p)(not(true (control ?p))))"));
		gameDescription.add(GdlFactory.create("(<= (legal (move ?x ?y) )(role ?x)(true (control ?y)))"));
		
		gameDescription.add(GdlFactory.create("(successor 0 1)"));
		gameDescription.add(GdlFactory.create("(successor 1 2)"));
		gameDescription.add(GdlFactory.create("(successor 2 3)"));
		gameDescription.add(GdlFactory.create("(successor 3 4)"));
		gameDescription.add(GdlFactory.create("(<= (legal white (capture ?x1 ?y1 ?x2 ?y2 ?x3 ?y3))(true (control white))(true (cell ?x1 ?y1 white))(succ ?x1 ?x2)(succ ?x2 ?x3)(true (cell ?x2 ?y2 black))(true (cell ?x3 ?y3 blank))(succ ?y1 ?y2)(succ ?y2 ?y3))"));
		



		context.add((GdlSentence)GdlFactory.create("(cell 1 2 2)"));
		context.add((GdlSentence)GdlFactory.create("(cell 2 3 1)"));
		context.add((GdlSentence)GdlFactory.create("(cell 2 3 3)"));

		//context.add((GdlSentence)GdlFactory.create("(true (cell  1 1 black))"));
		//context.add((GdlSentence)GdlFactory.create("(true (control black))"));
		
		context.add((GdlSentence)GdlFactory.create("(true (cell 1 1 white))"));
		context.add((GdlSentence)GdlFactory.create("(true (cell 2 2 black))"));
		context.add((GdlSentence)GdlFactory.create("(true (cell 3 3 blank))"));
		context.add((GdlSentence)GdlFactory.create("(true (control white))"));

	}

	
	public void testTicTacToe (){
		Game g = repo.getGame("tictactoe");
		List <Gdl> origRuler = g.getRules();
		
	}
	/*@Test
	public void checkersTest() throws SymbolFormatException, GdlFormatException {
		createGameRules();
		ProverImpl p = new ProverImpl(gameDescription);
		GdlSentence query = (GdlSentence) GdlFactory.create("(legal white ?x)");
		GdlSentence result = p.askOne(query, context);				
		assertTrue(result.equals(((GdlSentence)GdlFactory.create("(legal white (capture 1 1 2 2 3 3))"))));
	}
	*/

}
