package gameTheory;

public abstract class GameAction 
{
	protected String actionName = new String();

	public String GetName()
	{
		return actionName;
	}
	
	public String toString()
	{
		return actionName;
	}
}
