package tactical;

import ec.Individual;
import gameTheory.GameAction;
import java.util.LinkedList;

public abstract class GameState implements Comparable<GameState>
{
	protected Individual strategy1;
	protected Individual strategy2;
	
	protected double propagatedValue;
	protected double probability;
	protected PlayerType nextPlayer;
	protected GameAction lastMove1;
	protected GameAction lastMove2;
	
	
	public  GameState(Individual strat1, Individual strat2) 
	{
		if (getStyleClass().isInstance(strat1) && getStyleClass().isInstance(strat2))
		{
			strategy1 = (Individual) strat1.clone();
			strategy2 = (Individual) strat2.clone();	
		}
		else
		{
			//TODO: should never happen, throw exception 
		}
		
	}
	
	//Returns true if the game is over.
	public abstract boolean isOver();
		
	//returns a list of available actions for a player
	//each player may have a separate fitness function, so the player must be specified in the parameters
	//the parameter must be either PLAYER_1 or PLAYER_2, the rest of the values are not valid
	public abstract LinkedList<GameAction> getActions(PlayerType player);
	
	//returns the last move a player did
	public GameAction getLastMove(PlayerType player)
	{
		if (player.equals(PlayerType.PLAYER_1))
		{
			return lastMove1;	
		}
		else if (player.equals(PlayerType.PLAYER_2))
		{
			return lastMove2;	
		}
		else
		{
			//TODO: Exception
			return null;
		}
		
	}
	
	//changes the state to reflect a move played
	//leave parameter null if one or both players don't get an action at this state
	public abstract void Play(GameAction action1, GameAction action2); 
	
	//returns an object that indicates who gets to choose an action at this state
	public PlayerType whoPlaysNext()
	{
		return nextPlayer;
	}
	
	//Calculates and returns a double to indicate how good the state is for each player.
	//Positive means it's good for player 1, negative is good for player 2.
	//each player may have a separate fitness function, so the player must be specified in the parameters
	//the parameter must be either PLAYER_1 or PLAYER_2, the rest of the values are not valid
	public abstract double GetValue(PlayerType player);
	
	//Returns the value of this state, regardless of how it has gotten it.
	public double getPropagatedValue()
	{
		return propagatedValue;
	}
	
	//Set the value of this state to a specific number.
	public void setPropagatedValue(double value)
	{
		propagatedValue = value;
	}
	
	//calculates the state's value using GetValue() and sets it as the state's propagated value
	//each player may have a separate fitness function, so the player must be specified in the parameters
	//the parameter must be either PLAYER_1 or PLAYER_2, the rest of the values are not valid
	public double setPropagatedValue(PlayerType player)
	{
		propagatedValue = GetValue(player);
		return propagatedValue;
	}
	
	//returns a copy of this state
	public abstract GameState clone();

	//should not work unless previous state returns RANDOM_MOVE for whoPlaysNext()
	public double getProbability() 
	{
		return probability;
	}

	//should only work when whoPlaysNext() returns RANDOM_MOVE
	//should be used inside the implementation of the Play method, for when Play(null,null) is called
	public abstract LinkedList<GameState> getAllRandomChildren();
	
	//returns the type of PlayStyle/Individual class this game accepts 
	public abstract Class<?> getStyleClass();

}
