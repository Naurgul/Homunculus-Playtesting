package examples.chess;

import tactical.PlayerType;
import ec.vector.DoubleVectorIndividual;
import genetics.PlayStyle;

public class ChessSimpleStyle extends ChessStyle
{

	@Override
	public double Evaluate(ChessState state)
	{
		ChessState whiteState  = (ChessState) state.clone(false, PlayerType.PLAYER_1, true);
		ChessState blackState  = (ChessState) state.clone(false, PlayerType.PLAYER_2, true);
		return getStateMaterialWorth(whiteState.getBoard(), PlayerType.PLAYER_1) - getStateMaterialWorth(blackState.getBoard(), PlayerType.PLAYER_2);
	}
	
	private double getStateMaterialWorth(ChessSquare[][] board, PlayerType player)
	{
		double value = 0;
		
		//go through the board
		for (int rankCounter = 0; rankCounter < Consts.NUM_OF_RANKS; rankCounter++)
		{
			for (int fileCounter = 0; fileCounter < Consts.NUM_OF_FILES; fileCounter++)
			{
				ChessSquare square = board[rankCounter][fileCounter];
				ChessPiece piece = square.getPiece();
				//for every piece that belongs to the player whose material we're evaluating...					
				if (piece != null && piece.getOwner().equals(player))
				{
					//add the appropriate value
					value += getPieceWorth(piece.getPieceType());
				}
			}
		}
		
		return value;
	}
	
	private double getPieceWorth(ChessPieceType pieceType)
	{
		if (pieceType.equals(ChessPieceType.PAWN))
		{
			return 0.03;
		}
		else if (pieceType.equals(ChessPieceType.BISHOP))
		{
			return 0.1;
		}
		else if (pieceType.equals(ChessPieceType.KNIGHT))
		{
			return 0.1;
		}
		else if (pieceType.equals(ChessPieceType.ROOK))
		{
			return 0.12;
		}
		else if (pieceType.equals(ChessPieceType.QUEEN))
		{
			return 0.15;
		}
		else if (pieceType.equals(ChessPieceType.KING))
		{
			return 0; 
		}
		else //TODO: This should never happen, add an exception or something...
		{
			return -1;
		}
	}
	
	@Override
	public 	Object clone()
	{
		return new ChessSimpleStyle();	
	}
	
	@Override
	public String toString()
	{
		return "Only normal piece values contribute to value.";
	}


	
	
}