package examples.tictactoe;

import gameTheory.GameAction;

public class TictacAction extends GameAction
{

	private int num;
	private int x;
	private int y;
	
	public TictacAction(int num)
	{
		this.num = num;
		x = (num-1) / 3;
		y = (num-1) % 3;
		actionName = Integer.toString(num);
	}
	
	public TictacAction(int x, int y)
	{
		this.x = x;
		this.y = y;
		num = x*3+y+1;
		actionName = String.valueOf(num);
	}

	public TictacAction(String actionName)
	{
		this.actionName = actionName;
		num = Integer.parseInt(actionName);
		x = (num-1) / 3;
		y = (num-1) % 3;
	}

	public int getNum()
	{
		return num;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TictacAction other = (TictacAction) obj;
		if (num != other.num)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
