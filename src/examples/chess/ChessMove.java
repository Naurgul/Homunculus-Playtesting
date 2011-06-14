package examples.chess;

import gameTheory.GameAction;

public class ChessMove extends GameAction
{
	

	private ChessSquare fromSquare;
	private ChessSquare toSquare;

	public ChessMove(int fromFile, int fromRank, int toFile, int toRank)
	{
			this.fromSquare = new ChessSquare(fromFile, fromRank, null);
			this.toSquare = new ChessSquare(toFile, toRank, null);
			
			actionName = fromSquare.getName() + toSquare.getName();		
	}
	
	public ChessMove(ChessSquare squareFrom, ChessSquare squareTo)
	{
		this.fromSquare = squareFrom.clone();
		this.toSquare = squareTo.clone();
	}

	public ChessSquare getFromSquare()
	{
		return fromSquare;
	}

	public ChessSquare getToSquare()
	{
		return toSquare;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fromSquare == null) ? 0 : fromSquare.hashCode());
		result = prime * result
				+ ((toSquare == null) ? 0 : toSquare.hashCode());
		return result;
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
		ChessMove other = (ChessMove) obj;
		if (fromSquare == null)
		{
			if (other.fromSquare != null)
				return false;
		} else if (!fromSquare.equals(other.fromSquare))
			return false;
		if (toSquare == null)
		{
			if (other.toSquare != null)
				return false;
		} else if (!toSquare.equals(other.toSquare))
			return false;
		return true;
	}
}
