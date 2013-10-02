package org.ggp.base.player.gamer.statemachine.sample;

import org.ggp.base.util.game.CloudGameRepository;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.gdl.factory.exceptions.GdlFormatException;
import org.ggp.base.util.match.Match;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.symbol.factory.exceptions.SymbolFormatException;
import org.junit.Test;

import external.JSON.JSONException;

/*
 * @Author: Irme, Nera
 */
public class FabulousTest {
	FabulousPlayer fab = new FabulousPlayer();
	CloudGameRepository cloud = new CloudGameRepository("games.ggp.org/base");
	boolean done = false;


	@Test
	public void test1(){
		Game gam = cloud.getGame("hanoi");
		Match mat = new Match(null, 0, 60, 15, gam);
		Role myRole = Role.computeRoles(gam.getRules()).get(0);
		fab.setRoleName(myRole.getName());
		fab.setMatch(mat);
		int i = -1;
		try {
			i = fab.getStateMachine().getGoal(fab.getCurrentState(), myRole);
		} catch (GoalDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(i);
		

		


	}

}