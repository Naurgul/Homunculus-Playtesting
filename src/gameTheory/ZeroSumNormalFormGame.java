package gameTheory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedHashSet;

import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;
import org.apache.commons.math.optimization.linear.Relationship;
import org.apache.commons.math.optimization.linear.SimplexSolver;

import tactical.PlayerType;

//Represents a 2-player zero-sum normal form game.
public class ZeroSumNormalFormGame 
{
	private LinkedHashSet<GameAction> Actions1;
	private LinkedHashSet<GameAction> Actions2;
	private Hashtable<String, Double> UtilityMatrix;
	
	public ZeroSumNormalFormGame()
	{
		Actions1 = new LinkedHashSet<GameAction>();
		Actions2 = new LinkedHashSet<GameAction>();
		UtilityMatrix = new Hashtable<String, Double>();
	}
	
	public boolean addAction(GameAction newaction, PlayerType newplayer)
	{
		boolean r = false;
		
		if (newplayer.equals(PlayerType.PLAYER_1))
		{
			r = Actions1.add(newaction);
		}
		else if (newplayer.equals(PlayerType.PLAYER_2))
		{
			r = Actions2.add(newaction);
		}

		//TODO: Throw exception if newplayer is BOTH_PLAYERS or RANDOM_MOVE
		
		return r;
	}
	
	public void addResult(GameAction action1, GameAction action2, double utility)
	{
		if (Actions1.contains(action1) && Actions2.contains(action2))
		{
			UtilityMatrix.put(action1.GetName()+"|"+action2.GetName(), utility);
		}		
	}
	
	private double getResult(GameAction a1, GameAction a2)
	{
		double u = 0;		
		if (Actions1.contains(a1) && Actions2.contains(a2) && UtilityMatrix.containsKey(a1.GetName()+"|"+a2.GetName()))
		{
			u = UtilityMatrix.get(a1.GetName()+"|"+a2.GetName());
		}
		return u;
	}
	
	//Generate the utility table for this game
	private double[][] MakeUtilityTable(PlayerType player)
	{
		double UtilityTable[][] = new double[Actions1.size()][Actions2.size()];
		
		int i = 0;
		int j = 0;
		int sign = 1;
		if (player.equals(PlayerType.PLAYER_2))
		{
			sign = -1;
		}
		
		
		for (GameAction a1 : Actions1)
		{
			j=0;
			for (GameAction a2 : Actions2)
			{
				double u = getResult(a1,a2);
				UtilityTable[i][j] = sign * u;
				j++;
			}
			i++;
		}
		return UtilityTable;
	}
	
	//Return the expected payoff vector given an action
	private double[] ExpectedPayoff(double[][] UtilityTable, int action)
	{
		double[] payoff = new double[Actions1.size()];
		int i = 0;
		 
		for (double[] v : UtilityTable)
		{
			payoff[i] = v[action];
			i++;
		}
		
		return payoff;		
	}
	
	//Subtrack tables. 
	private double[] TablesMinus(double[] a, double[] b)
	{
		double[] c = new double[a.length];
		if (a.length == b.length)
		{
			for (int i = 0; i < a.length; i++)
			{
				c[i] = a[i] - b[i];
			}
		}
		return c;
	}
	
	//Compute an equilibrium strategy for this game
	public MixedStrategy GetEquilibrium()
	{		
		//To get the equilibrium we have to minimize a linear function under a set of restrictions.
		//
		
		double U[][] = MakeUtilityTable(PlayerType.PLAYER_2);
		int actionToMin = 0;
		boolean haveEq = false;
		RealPointValuePair solution = null;
		
		while (!haveEq)
		{
			//The expected payoffs for one of the opponent's action is chosen as the function to minimize.
			LinearObjectiveFunction f = new LinearObjectiveFunction(ExpectedPayoff(U, actionToMin), 0);
			Collection constraints = new ArrayList();
			
			//For each other of the opponent's actions we need E(a)<= E(action-to-minimize)
			for (int i = 0; i < Actions2.size(); i++)
			{
				if (i != actionToMin)
				{
					constraints.add(new LinearConstraint(TablesMinus(ExpectedPayoff(U,i), ExpectedPayoff(U,actionToMin)), Relationship.LEQ, 0));
				}
			}
			//Another restriction is that the sum of the probabilities assigned to each of our actions must be 1.
			double[] ones = new double[Actions1.size()];
			for (int i = 0; i < Actions1.size(); i++)
			{
				ones[i] = 1;
			}
			constraints.add(new LinearConstraint(ones, Relationship.EQ, 1));
			
			//Also, each probability assigned to one of our actions must be greater or equal to 0.
			for (int i = 0; i < Actions1.size(); i++)
			{
				double[] singleOne = new double[Actions1.size()];
				singleOne[i] = 1;
				constraints.add(new LinearConstraint(singleOne, Relationship.GEQ, 0));
			}
			
			//Solve the system using the Simplex algorithm.
			haveEq = true;
			try {
				solution = new SimplexSolver().optimize(f, constraints, GoalType.MINIMIZE, false);
			} catch (OptimizationException e) {
				//System.err.println("Equilibrium not found. Let's try to minimize another action.");
				haveEq = false;
			}			
			//If it doesn't work, choose a different action whose expected payoff to minimize.
			actionToMin++;
		}		
				
		MixedStrategy strategy = new MixedStrategy(solution, Actions1);
		return strategy ;		
	}
}
