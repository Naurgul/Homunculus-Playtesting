package examples.chess;

import tactical.PlayerType;

public class ChessPiece
{
	private ChessPieceType pieceType;
	private PlayerType owner;
	
	public ChessPiece(ChessPieceType pieceType, PlayerType owner)
	{
		this.pieceType = pieceType;
		this.owner = owner;
	}

	public ChessPieceType getPieceType()
	{
		return pieceType;
	}

	public PlayerType getOwner()
	{
		return owner;
	}
	
	@Override
	public ChessPiece clone()
	{
		return new ChessPiece(this.pieceType, this.owner);
	}
	
	public String print()
	{
		String c = new String();
		if (pieceType.equals(ChessPieceType.PAWN))
		{
			c = "p";
		}
		else if (pieceType.equals(ChessPieceType.BISHOP))
		{
			c = "b";
		}
		else if (pieceType.equals(ChessPieceType.KNIGHT))
		{
			c = "n";
		}
		else if (pieceType.equals(ChessPieceType.ROOK))
		{
			c = "r";
		}
		else if (pieceType.equals(ChessPieceType.QUEEN))
		{
			c = "q";
		}
		else if (pieceType.equals(ChessPieceType.KING))
		{
			c = "k";
		}
		
		if (owner.equals(PlayerType.PLAYER_1))
		{
			return c.toUpperCase();
		}
		else
		{
			return c;
		}
	}
}