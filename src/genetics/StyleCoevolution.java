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
		long startTime = System.currentTimeMillis();
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
			
			//Play a match between the two styles:
			PlayerType nextUp = PlayerType.PLAYER_1;
			HomunculusPlayer bot1 = new HomunculusPlayer(Consts.GAMETREE_SEARCH_DEPTH, PlayerType.PLAYER_1);
			HomunculusPlayer bot2 = new HomunculusPlayer(Consts.GAMETREE_SEARCH_DEPTH, PlayerType.PLAYER_2);
			GameAction move = null;
			int turn;
			for (turn = 1; turn <= 100; turn++)
			{
				if (game.isOver())
				{

					if (game.GetValue(null) > 0)
					{
						System.out.print("+");
						if (updateFitness[0])
						{
							fit1.setFitness(state, fit1.fitness() + 1, false);
							fit1.trials++;
						}
					}
					else
					{
						System.out.print("-");
						if (updateFitness[1])
						{
							fit2.setFitness(state, fit2.fitness() + 1, false);
							fit2.trials++;
						}
					}
					
					break;
				}						
				
				
				if (nextUp.equals(PlayerType.PLAYER_1))
				{
					move = bot1.AlphaBetaPruning(game);
					game.Play(move, null);
					nextUp = PlayerType.PLAYER_2;
				}
				else
				{
					move = bot2.AlphaBetaPruning(game);	
					game.Play(null, move);
					nextUp = PlayerType.PLAYER_1;
				}
				
				
					
			}
			if(turn>=100)
			{
				System.out.print(".");
			}
			//System.out.println(("\t" + (System.currentTimeMillis()-startTime)/1000));
			
		
		}
		
		
	}

}
