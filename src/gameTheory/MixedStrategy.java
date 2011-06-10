package gameTheory;

import java.util.LinkedHashSet;
import org.apache.commons.math.optimization.RealPointValuePair;

public class MixedStrategy
{
	public GameAction[] actions;
	public double probabilities[];
	public double payoff;
	
	public MixedStrategy(RealPointValuePair solution, LinkedHashSet<GameAction> newactions) 
	{
		probabilities = solution.getPoint();
		actions = newactions.toArray(new GameAction[0]);		
		payoff = solution.getValue();
	}
}
