package examples.tictactoe;

import ec.Individual;
import ec.util.Parameter;
import genetics.PlayStyle;

public class TictacStyle extends Individual implements PlayStyle
{

	@Override
	public Parameter defaultBase()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getStateClass()
	{
		try
		{
			return Class.forName("examples.tictactoe.TictacState");
		} 
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public double distance(PlayStyle other)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean equals(Object ind)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode()
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
