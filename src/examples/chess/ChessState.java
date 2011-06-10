package examples.chess;

import ec.Individual;
import gameTheory.GameAction;

import java.util.Iterator;
import java.util.LinkedList;

import tactical.GameState;
import tactical.PlayerType;

public class ChessState extends GameState
{
	private ChessSquare[][] board;
	
	//The following fields change the way the state works for specific purposes
	//They are all turned off by default and should only be modified via custom cloning.
	private boolean disabledKingThreat;	//don't check if the king is threatened
	private boolean showDefendingMoves; //allow moves that end on pieces of the same player
	 

	public ChessState(Individual strat1, Individual strat2)
	{
		super(strat1, strat2);
		
		//set up the board
		board = new ChessSquare[Consts.NUM_OF_RANKS][Consts.NUM_OF_FILES];
		for (int rankCounter = 0; rankCounter < Consts.NUM_OF_RANKS; rankCounter++)
		{
			for (int fileCounter = 0; fileCounter < Consts.NUM_OF_FILES; fileCounter++)
			{
				board[rankCounter][fileCounter] = new ChessSquare(fileCounter, rankCounter, null);
				board[rankCounter][fileCounter].setPiece(Consts.INITIAL_PIECE_POSITIONS.get(board[rankCounter][fileCounter]));
			}
		}
		
		//White plays first
		nextPlayer = PlayerType.PLAYER_1;
		
		//Turn off special settings, this is a normal state
		disabledKingThreat = false;
		showDefendingMoves = false;
	}
	

	@Override
	public boolean isOver()
	{
		if(getActions(nextPlayer).isEmpty())
		{
			return true;
		}
		else
		{
			return false;	
		}		
	}

	@Override
	public LinkedList<GameAction> getActions(PlayerType player)
	{
		if (player.equals(nextPlayer))
		{
			LinkedList<GameAction> moveList = new LinkedList<GameAction>();
			//go through the board
			for (int rankCounter = 0; rankCounter < Consts.NUM_OF_RANKS; rankCounter++)
			{
				for (int fileCounter = 0; fileCounter < Consts.NUM_OF_FILES; fileCounter++)
				{
					ChessSquare square = board[rankCounter][fileCounter];
					ChessPiece piece = square.getPiece();
					//for every piece that belongs to the player whose turn it is...					
					if (piece != null && piece.getOwner().equals(nextPlayer))
					{
						//add its possible moves to the list!
						//treat each piece type individually
						
						if (piece.getPieceType().equals(ChessPieceType.PAWN))
						{
							int pawnInitRank;
							int pawnDirection;
							
							if (nextPlayer.equals(PlayerType.PLAYER_1))
							{
								pawnInitRank = Consts.PAWN_INIT_RANK_PLAYER_1;
								pawnDirection = 1;
							}
							else
							{
								pawnInitRank = Consts.PAWN_INIT_RANK_PLAYER_2;
								pawnDirection = -1;
							}
							
							
							if (square.getRank() + pawnDirection >= 0 && square.getRank() + pawnDirection < Consts.NUM_OF_RANKS)
							{
								//pawns can move one square forward, if not blocked
								if (board[square.getRank() + pawnDirection][square.getFile()].getPiece() == null)
								{
									moveList.add(new ChessMove(square.getFile(), square.getRank(), square.getFile(), square.getRank() + pawnDirection));
									//also two squares if it's the first move (and not blocked!) 
									if(square.getRank() == pawnInitRank && board[square.getRank() + pawnDirection * 2][square.getFile()].getPiece() == null)
									{
										moveList.add(new ChessMove(square.getFile(), square.getRank(), square.getFile(), square.getRank() + pawnDirection * 2));
									}
								}
								//pawns may move diagonally one square, if there is an enemy piece to capture there
								if (square.getFile() + 1 < Consts.NUM_OF_FILES)
								{
									ChessPiece targetPiece = board[square.getRank() + pawnDirection][square.getFile() + 1].getPiece();
									if (targetPiece != null && (!targetPiece.getOwner().equals(nextPlayer) || showDefendingMoves))
									{
										moveList.add(new ChessMove(square.getFile(), square.getRank(), square.getFile() + 1, square.getRank() + pawnDirection));
									}
								}
								if (square.getFile() - 1 >= 0)
								{
									ChessPiece targetPiece = board[square.getRank() + pawnDirection][square.getFile() - 1].getPiece();
									if (targetPiece != null && (!targetPiece.getOwner().equals(nextPlayer) || showDefendingMoves))
									{
										moveList.add(new ChessMove(square.getFile(), square.getRank(), square.getFile() - 1, square.getRank() + pawnDirection));
									}
								}	
								//TODO: En passant
							}							
						}
						else if (piece.getPieceType().equals(ChessPieceType.BISHOP))
						{
							//try all diagonals
							//to do that, use all combinations of bishopDirectionRank and bishopDirectionFile for  the values +1 and -1
							for (int bishopDirectionRank = 1; bishopDirectionRank >= -1; bishopDirectionRank -= 2)
							{ 
								for (int bishopDirectionFile = 1; bishopDirectionFile >= -1; bishopDirectionFile -= 2)
								{
							
									//continue until you get out of bounds or reach a piece
									int targetRank = square.getRank();
									int targetFile = square.getFile();
									while (targetRank + bishopDirectionRank >= 0 && targetRank + bishopDirectionRank < Consts.NUM_OF_RANKS && targetFile + bishopDirectionFile >= 0 && targetFile + bishopDirectionFile < Consts.NUM_OF_FILES)
									{
										targetRank += bishopDirectionRank;
										targetFile += bishopDirectionFile;
										
										if (board[targetRank][targetFile].getPiece() == null)
										{
											moveList.add(new ChessMove(square.getFile(), square.getRank(), targetFile, targetRank));
										}
										else
										{
											if (!board[targetRank][targetFile].getPiece().getOwner().equals(nextPlayer) || showDefendingMoves)
											{
												moveList.add(new ChessMove(square.getFile(), square.getRank(), targetFile, targetRank));
											}
											break;
										}
									}								
								}
							}
						}
						else if (piece.getPieceType().equals(ChessPieceType.KNIGHT))
						{							
							//try all 8 knight moves
							//to do that, use all combinations of knightDirectionSmall for the values +1 and -1 and knightDirectionBig for the values -2 and +2 AND their inverse too
							for (int knightDirectionSmall = 1; knightDirectionSmall >= -1; knightDirectionSmall -= 2)
							{
								for (int knightDirectionBig = 2; knightDirectionBig >= -2; knightDirectionBig -= 4)
								{									
									//we need to try adding knightDirectionBig to the file and knightDirectionSmall to the rank
									//but also the inverse: adding knightDirectionBig to the rank and knightDirectionSmall to the file
									for (int comboMode = 0; comboMode <= 1; comboMode++)
									{
										int targetRank = square.getRank(); 
										int targetFile = square.getFile(); 
										
										if (comboMode == 0)
										{
											//try current knightDirectionSmall-knightDirectionBig combination
											targetRank += knightDirectionSmall;
											targetFile += knightDirectionBig;
										}
										else if (comboMode == 1)
										{
											//now try the inverse knightDirectionSmall-knightDirectionBig combination
											targetRank += knightDirectionBig;
											targetFile += knightDirectionSmall;
										}
										
										//if it's the target square is not out of bounds
										if (targetRank >= 0 && targetRank < Consts.NUM_OF_RANKS && targetFile >= 0 && targetFile < Consts.NUM_OF_FILES)
										{
											//and if it's empty or occupied by an enemy piece
											if (board[targetRank][targetFile].getPiece() == null || !board[targetRank][targetFile].getPiece().getOwner().equals(nextPlayer) || showDefendingMoves)
											{
												//then it's a valid move
												moveList.add(new ChessMove(square.getFile(), square.getRank(), targetFile, targetRank));
											}
										}
										
									}
								}
							}
							
						}
						else if (piece.getPieceType().equals(ChessPieceType.ROOK))
						{
							//try all vertical and horizontal directions
							//to do that, use all combinations of rookDirection for the values +1 and -1 AND their inverse too
							for (int rookDirection = 1; rookDirection >= -1; rookDirection -= 2)
							{ 
								//we need to try adding rookDirection to the file BUT ALSO to the rank
								for (int comboMode = 0; comboMode <= 1; comboMode++)
								{
									int rookDirectionRank = 0; 
									int rookDirectionFile = 0; 
									
									if (comboMode == 0)
									{
										//try adding rookDirection to the rank
										rookDirectionRank = rookDirection;
									}
									else if (comboMode == 1)
									{
										//now try adding rookDirection to the file
										rookDirectionFile = rookDirection;
									}
									
									//continue until you get out of bounds or reach a piece
									int targetRank = square.getRank();
									int targetFile = square.getFile();
									while (targetRank + rookDirectionRank >= 0 && targetRank + rookDirectionRank < Consts.NUM_OF_RANKS && targetFile + rookDirectionFile >= 0 && targetFile + rookDirectionFile < Consts.NUM_OF_FILES)
									{
										targetRank += rookDirectionRank;
										targetFile += rookDirectionFile;
										
										if (board[targetRank][targetFile].getPiece() == null)
										{
											moveList.add(new ChessMove(square.getFile(), square.getRank(), targetFile, targetRank));
										}
										else
										{
											if (!board[targetRank][targetFile].getPiece().getOwner().equals(nextPlayer) || showDefendingMoves)
											{
												moveList.add(new ChessMove(square.getFile(), square.getRank(), targetFile, targetRank));
											}
											break;
										}
									}
								}	
							}
						}
						else if (piece.getPieceType().equals(ChessPieceType.QUEEN))
						{
							//try all horizontal and vertical directions as well as diagonals
							//to do that, use all combinations of queenDirectionRank and queenDirectionFile for  the values -1, 0, +1
							for (int queenDirectionRank = -1; queenDirectionRank <= 1; queenDirectionRank++)
							{ 
								for (int queenDirectionFile = -1; queenDirectionFile <= 1; queenDirectionFile++)
								{
							
									//continue until you get out of bounds or reach a piece
									int targetRank = square.getRank();
									int targetFile = square.getFile();
									while (targetRank + queenDirectionRank >= 0 && targetRank + queenDirectionRank < Consts.NUM_OF_RANKS && targetFile + queenDirectionFile >= 0 && targetFile + queenDirectionFile < Consts.NUM_OF_FILES)
									{
										targetRank += queenDirectionRank;
										targetFile += queenDirectionFile;
										
										if (board[targetRank][targetFile].getPiece() == null)
										{
											moveList.add(new ChessMove(square.getFile(), square.getRank(), targetFile, targetRank));
										}
										else
										{
											if (!board[targetRank][targetFile].getPiece().getOwner().equals(nextPlayer) || showDefendingMoves)
											{
												moveList.add(new ChessMove(square.getFile(), square.getRank(), targetFile, targetRank));
											}
											break;
										}
									}								
								}
							}
						}
						else if (piece.getPieceType().equals(ChessPieceType.KING))
						{			
							//try all horizontal and vertical directions as well as diagonals
							//to do that, use all combinations of kingDirectionRank and kingDirectionFile for  the values -1, 0, +1
							for (int kingDirectionRank = -1; kingDirectionRank <= 1; kingDirectionRank++)
							{ 
								for (int kingDirectionFile = -1; kingDirectionFile <= 1; kingDirectionFile++)
								{
									int targetRank = square.getRank() + kingDirectionRank; 
									int targetFile = square.getFile() + kingDirectionFile; 
									
									//if it's the target square is not out of bounds
									if (targetRank >= 0 && targetRank < Consts.NUM_OF_RANKS && targetFile >= 0 && targetFile < Consts.NUM_OF_FILES)
									{
										//and if it's empty or occupied by an enemy piece
										if (board[targetRank][targetFile].getPiece() == null || !board[targetRank][targetFile].getPiece().getOwner().equals(nextPlayer) || showDefendingMoves)
										{
											//then it's a valid move
											moveList.add(new ChessMove(square.getFile(), square.getRank(), targetFile, targetRank));
										}
									}								
								}
							}		
							
							//TODO: Castling
						}
					}
				}
			}
			
			//normally, you have to look ahead one move and remove any moves that end up with a threatened king
			//note that this filtering won't be applied to the virtual look-ahead state
			if (!disabledKingThreat)
			{
				//for every move we can make
				Iterator<GameAction> moveIterator = moveList.iterator();				
				while (moveIterator.hasNext())
				{
					GameAction thisMove = moveIterator.next();
					//play it out in a virtual look-ahead state
					ChessState virtualState = (ChessState) this.clone(true, nextPlayer, false);
					virtualState.Play(thisMove);
					
					//find out the position of our king
					ChessSquare kingSquare = null;
					for (int rankCounter = 0; rankCounter < Consts.NUM_OF_RANKS; rankCounter++)
					{
						for (int fileCounter = 0; fileCounter < Consts.NUM_OF_FILES; fileCounter++)
						{
							ChessSquare square = virtualState.board[rankCounter][fileCounter];
							ChessPiece piece = square.getPiece();
							
							if (piece != null && piece.getOwner().equals(nextPlayer) && piece.getPieceType().equals(ChessPieceType.KING))
							{
								kingSquare = square;
								break;
							}
						}
						if (kingSquare != null)
						{
							break;
						}
					}					
					//and see what moves the other player has got
					LinkedList<GameAction> nextMoves = virtualState.getActions(virtualState.nextPlayer);
					
					//if any of these next moves attacks the square where the king is positioned
					//then remove our move from the list
					for (GameAction nMove : nextMoves)
					{
						if (((ChessMove)nMove).getToSquare().equalsPosition(kingSquare))
						{
							moveIterator.remove();
							break;
						}
					}
				}
			}
			
			return moveList;
		}
		else
		{
			return null;	
		}
	}

	@Override
	public void Play(GameAction action1, GameAction action2)
	{
		//make sure that we only get one non-null move and that it is consistent with who the next player is
		ChessMove a = null;
		if (nextPlayer.equals(PlayerType.PLAYER_1) && action1 != null && action2 == null)
		{
			a = (ChessMove) action1;
		}
		else if (nextPlayer.equals(PlayerType.PLAYER_2) && action1 == null && action2 != null)
		{
			a = (ChessMove) action2;
		}		
		if (a != null)
		{
			//if this is a valid move (ignore this check if we're simulating for king threats)
			LinkedList<GameAction> validMoves = null;
			if (!disabledKingThreat)
			{
				validMoves = getActions(nextPlayer);
			}
			if (validMoves == null || validMoves.contains(a))
			{
				ChessPiece piece = board[a.getFromSquare().getRank()][a.getFromSquare().getFile()].getPiece();
				
				//promotion
				if (piece.getPieceType().equals(ChessPieceType.PAWN) && (a.getToSquare().getRank() == 0 || a.getToSquare().getRank() == Consts.NUM_OF_RANKS-1))
				{
					piece = new ChessPiece(ChessPieceType.QUEEN, piece.getOwner());
				}
				
				//TODO: en passant, castling
				
				//place piece on end square
				board[a.getToSquare().getRank()][a.getToSquare().getFile()].setPiece(piece);
				//remove it from start square
				board[a.getFromSquare().getRank()][a.getFromSquare().getFile()].setPiece(null);
								
				//note down the last move and change next player
				if (nextPlayer.equals(PlayerType.PLAYER_1))
				{
					lastMove1 = action1;
					nextPlayer = PlayerType.PLAYER_2;
				}
				else if (nextPlayer.equals(PlayerType.PLAYER_2))
				{
					lastMove2 = (GameAction) action2;
					nextPlayer = PlayerType.PLAYER_1;
				}
			}			
		}
	}
	
	public void Play(GameAction action)
	{
		if (nextPlayer == PlayerType.PLAYER_1)
		{
			Play(action, null);
		}
		else if (nextPlayer == PlayerType.PLAYER_2)
		{
			Play(null, action);
		}
	}


	@Override
	public double GetValue(PlayerType player)
	{
		if (isOver())
		{
			if (nextPlayer.equals(PlayerType.PLAYER_1))
			{
				return Double.NEGATIVE_INFINITY;
			}
			else if (nextPlayer.equals(PlayerType.PLAYER_2))
			{
				return Double.POSITIVE_INFINITY;
			}
			else //TODO: Should never happen, add an exception or something
			{
				return 0;
			}
		}
		else if (player.equals(PlayerType.PLAYER_1))
		{
			return ((ChessStyle)strategy1).Evaluate(this);
		}
		else if (player.equals(PlayerType.PLAYER_2))
		{
			return ((ChessStyle)strategy2).Evaluate(this);
		}
		else	//TODO: Should never happen, add an exception or something
		{
			return 0;	
		}
	}

	@Override
	public GameState clone()
	{
		ChessState newState = new ChessState(strategy1, strategy2);
		newState.board = getBoard();
		newState.disabledKingThreat = this.disabledKingThreat;
		newState.nextPlayer = this.nextPlayer;
		newState.propagatedValue = this.propagatedValue;
		newState.probability = this.probability;
		newState.lastMove1 = this.lastMove1;
		newState.lastMove2 = this.lastMove2;
		return newState;
	}
	
	//customise clone
	public GameState clone(boolean disabledKingThreat, PlayerType nextPlayer, boolean showDefendingMoves)
	{
		GameState newState = this.clone();
		((ChessState)newState).disabledKingThreat = disabledKingThreat;
		((ChessState)newState).nextPlayer = nextPlayer;
		((ChessState)newState).showDefendingMoves = showDefendingMoves;
		return newState;
	}

	@Override
	public LinkedList<GameState> getAllRandomChildren()
	{
		//this game doesn't have random moves
		return null;
	}
	
	public ChessSquare[][] getBoard()
	{
		//create clone of the board so that whoever gets this won't be able to directly affect this state
		//to deep clone the board, we have to iterate through each of its elements
		ChessSquare[][] boardClone = new ChessSquare[Consts.NUM_OF_RANKS][Consts.NUM_OF_FILES];
		for (int rankCounter = 0; rankCounter < Consts.NUM_OF_RANKS; rankCounter++)
		{
			for (int fileCounter = 0; fileCounter < Consts.NUM_OF_FILES; fileCounter++)
			{
				boardClone[rankCounter][fileCounter] = board[rankCounter][fileCounter].clone();
			}
		}
		return boardClone;
	}

	public void printBoard()
	{
		System.out.print("\n");
		for (int rankCounter = Consts.NUM_OF_RANKS - 1; rankCounter >= 0; rankCounter--)
		{
			for (int fileCounter = 0; fileCounter < Consts.NUM_OF_FILES; fileCounter++)
			{
				ChessSquare square = board[rankCounter][fileCounter];
				ChessPiece piece = square.getPiece();
				String pieceChar = new String();
				if (piece == null)
				{
					pieceChar = "囗";
				}
				else 
				{
					pieceChar = piece.print();
				}
				System.out.print(pieceChar + "\t");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}


	@Override
	public Class<?> getStyleClass()
	{
		 try
		{
			return Class.forName("examples.chess.ChessStyle");
		} 
		catch (ClassNotFoundException e)
		{
			System.err.println("Incorrect gamestate class specified at" + this.getClass());
			e.printStackTrace();
		}
		return null;
	}

}