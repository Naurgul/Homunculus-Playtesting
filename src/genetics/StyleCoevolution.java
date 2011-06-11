package genetics;

import tactical.GameState;
import tactical.HomunculusPlayer;
import tactical.PlayerType;
import ec.*;
import ec.simple.*;
import ec.vector.*;
import ec.coevolve.*;
import examples.chess.Consts;
import gameTheory.GameAction;


public class StyleCoevolution extends Problem implements GroupedProblemForm
{

	@Override
	public void preprocessPopulation(EvolutionState state, Population pop, boolean countVictoriesOnly)
	{
		// initialise population fitness
		
		//assume we only have one population!
		if (pop.subpops.length == 1)
		{
			for(int j = 0; j < pop.subpops[0].individuals.length; j++) 
			{
				SimpleFitness fit =	(SimpleFitness)(pop.subpops[0].individuals[j].fitness);
				fit.trials = 0;
				fit.setFitness(state, 0, false);
			}			
		}
		else
		{
			//TODO: should never happen, throw an exception or something! 
		}		

	}

	@Override
	public void postprocessPopulation(EvolutionState state, Population pop,	boolean countVictoriesOnly)
	{
		//assume we only have one population!
		if (pop.subpops.length == 1)
		{
			for(int j = 0; j < pop.subpops[0].individuals.length; j++) 
			{
				SimpleFitness fit =	(SimpleFitness)(pop.subpops[0].individuals[j].fitness);
				if (!countVictoriesOnly)
				{
					fit.setFitness(state, fit.fitness() / fit.trials, false);
				}
				
				pop.subpops[0].individuals[j].evaluated = true;
			}
		}
		else
		{
			//TODO: should never happen, throw exception! 
		}
	}

	@Override
	public void evaluate(EvolutionState state, Individual[] ind, boolean[] updateFitness, boolean countVictoriesOnly, int[] subpops, int threadnum)
	{
		//long startTime = System.currentTimeMillis();
		//First check if the individuals implement the PlayStyle interface
		if(PlayStyle.class.isInstance(ind[0]) && PlayStyle.class.isInstance(ind[1]))
		{
			//Use reflection to instantiate the correct type of GameState for the styles involved
			
			//Find what kind of GameState class we need
			Class gameClass = ((PlayStyle)ind[0]).getStateClass();			
			//Set up the parameters (the styles)
			Class[] parameterTypes = {Individual.class, Individual.class};
			Object[] parameters = {ind[0], ind[1]};
			GameState game = null;
			try
			{				
				//Find the constructor
				java.lang.reflect.Constructor gameConstructor = gameClass.getConstructor(parameterTypes);				
				//Create a new instance of the appropriate class, pass it the styles.
				game = (GameState) gameConstructor.newInstance(parameters);
			} 
			catch (Exception e)
			{
				System.err.println("Failed to dynamically instantiate " + gameClass);
				e.printStackTrace();
			}
			
			//Get the fitness object for each of the individual styles
			SimpleFitness fit1 = (SimpleFitness)(ind[0].fitness);
			SimpleFitness fit2 = (SimpleFitness)(ind[1].fitness);
			
			HomunculusPlayer bot1 = new HomunculusPlayer(Consts.GAMETREE_SEARCH_DEPTH, PlayerType.PLAYER_1, state.random[threadnum]);
			HomunculusPlayer bot2 = new HomunculusPlayer(Consts.GAMETREE_SEARCH_DEPTH, PlayerType.PLAYER_2, state.random[threadnum]);
			GameAction move1 = null;
			GameAction move2 = null;
			int turn;
			for (turn = 1; turn <= Consts.MAX_TURNS; turn++)
			{

				if (game.whoPlaysNext().equals(PlayerType.PLAYER_1))
				{
					move1 = bot1.Think(game);
					game.Play(move1, null);
				}
				else if (game.whoPlaysNext().equals(PlayerType.PLAYER_2))
				{
					move2 = bot2.Think(game);
					game.Play(null, move2);
				}
				else if (game.whoPlaysNext().equals(PlayerType.BOTH_PLAYERS))
				{
					move1 = bot1.Think(game);	
					move2 = bot2.Think(game);
					game.Play(move1, move2);
				}
				else if (game.whoPlaysNext().equals(PlayerType.RANDOM_MOVE))
				{
					game.Play(null, null);
				}
				else
				{
					//TODO: Should never happen, throw exception.
				}
				
				if (game.isOver())
				{

					if (game.GetValue(null) > 0)
					{
						if (updateFitness[0])
						{
							fit1.setFitness(state, fit1.fitness() + 1, false);
						}
					}
					else
					{
						if (updateFitness[1])
						{
							fit2.setFitness(state, fit2.fitness() + 1, false);
						}
					}
					
					break;
				}		
				
					
			}
			
			if (updateFitness[0])
			{
				fit1.trials++;
			}
			if (updateFitness[1])
			{
				fit2.trials++;
			}			
		
		}	
		
	}

}
