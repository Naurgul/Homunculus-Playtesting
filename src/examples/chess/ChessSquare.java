package examples.chess;

public class ChessSquare
{
	private int file;
	private int rank;
	private ChessPiece piece;
	
	ChessSquare(int file, int rank, ChessPiece piece) throws IllegalArgumentException
	{
		if (file >= 0 && file < Consts.NUM_OF_FILES && rank >= 0 && rank < Consts.NUM_OF_RANKS)
		{
			this.file = file;
			this.rank = rank;
			this.piece = piece;
		}
		else
		{
			throw new IllegalArgumentException();
		}
		
	}

	public int getFile()
	{
		return file;
	}

	public int getRank()
	{
		return rank;
	}
	
	public ChessPiece getPiece()
	{
		return piece;
	}
	
	public void setPiece(ChessPiece piece)
	{
		this.piece = piece;
	}
	
	public String getName()
	{
		char fileChar = (char) ('a' + file);
		char rankChar = (char) ('1' + rank);
		return new String(new char[] {fileChar,rankChar});	
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + file;
		result = prime * result + ((piece == null) ? 0 : piece.hashCode());
		result = prime * result + rank;
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
		ChessSquare other = (ChessSquare) obj;
		if (file != other.file)
			return false;
		if (piece == null)
		{
			if (other.piece != null)
				return false;
		} else if (!piece.equals(other.piece))
			return false;
		if (rank != other.rank)
			return false;
		return true;
	}
	
	//same as equals except that it only checks file and rank, not piece
	public boolean equalsPosition(ChessSquare otherSquare)
	{
		if (otherSquare == null)
		{
			return false;
		}
		else if (otherSquare.file == this.file && otherSquare.rank == this.rank)
		{
			return true;
		}
		else
		{
			return false;	
		}		
	}
	
	@Override
	public ChessSquare clone()
	{		
		if (piece != null)
		{
			return new ChessSquare(this.file, this.rank, this.piece.clone());	
		}
		else
		{
			return new ChessSquare(this.file, this.rank, null);
		}
				
	}

	

}
