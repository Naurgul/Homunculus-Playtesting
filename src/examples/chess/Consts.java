package examples.chess;

import java.util.HashMap;

import tactical.PlayerType;

public class Consts
{		
	//GAME RULES
	
	public static final int NUM_OF_FILES = 8;
	public static final int NUM_OF_RANKS = 8;
	public static final int PAWN_INIT_RANK_PLAYER_1 = 1;
	public static final int PAWN_INIT_RANK_PLAYER_2 = 6;
	
	public static final HashMap<ChessSquare, ChessPiece> INITIAL_PIECE_POSITIONS = new HashMap<ChessSquare, ChessPiece>()
		{{
			for (int fileCounter = 0; fileCounter < NUM_OF_FILES; fileCounter++)
			{
				put(new ChessSquare(fileCounter, PAWN_INIT_RANK_PLAYER_1, null), new ChessPiece(ChessPieceType.PAWN, PlayerType.PLAYER_1));	
			}
			put(new ChessSquare(0, 0, null), new ChessPiece(ChessPieceType.ROOK, PlayerType.PLAYER_1));
			put(new ChessSquare(1, 0, null), new ChessPiece(ChessPieceType.KNIGHT, PlayerType.PLAYER_1));
			put(new ChessSquare(2, 0, null), new ChessPiece(ChessPieceType.BISHOP, PlayerType.PLAYER_1));
			put(new ChessSquare(3, 0, null), new ChessPiece(ChessPieceType.QUEEN, PlayerType.PLAYER_1));
			put(new ChessSquare(4, 0, null), new ChessPiece(ChessPieceType.KING, PlayerType.PLAYER_1));
			put(new ChessSquare(5, 0, null), new ChessPiece(ChessPieceType.BISHOP, PlayerType.PLAYER_1));
			put(new ChessSquare(6, 0, null), new ChessPiece(ChessPieceType.KNIGHT, PlayerType.PLAYER_1));
			put(new ChessSquare(7, 0, null), new ChessPiece(ChessPieceType.ROOK, PlayerType.PLAYER_1));
			
			for (int fileCounter = 0; fileCounter < NUM_OF_FILES; fileCounter++)
			{
				put(new ChessSquare(fileCounter, PAWN_INIT_RANK_PLAYER_2, null), new ChessPiece(ChessPieceType.PAWN, PlayerType.PLAYER_2));	
			}
			put(new ChessSquare(0, 7, null), new ChessPiece(ChessPieceType.ROOK, PlayerType.PLAYER_2));
			put(new ChessSquare(1, 7, null), new ChessPiece(ChessPieceType.KNIGHT, PlayerType.PLAYER_2));
			put(new ChessSquare(2, 7, null), new ChessPiece(ChessPieceType.BISHOP, PlayerType.PLAYER_2));
			put(new ChessSquare(3, 7, null), new ChessPiece(ChessPieceType.QUEEN, PlayerType.PLAYER_2));
			put(new ChessSquare(4, 7, null), new ChessPiece(ChessPieceType.KING, PlayerType.PLAYER_2));
			put(new ChessSquare(5, 7, null), new ChessPiece(ChessPieceType.BISHOP, PlayerType.PLAYER_2));
			put(new ChessSquare(6, 7, null), new ChessPiece(ChessPieceType.KNIGHT, PlayerType.PLAYER_2));
			put(new ChessSquare(7, 7, null), new ChessPiece(ChessPieceType.ROOK, PlayerType.PLAYER_2));						
		}};
	
	//STYLE PARAMETERS
		
	//mobility versus material
	public static final int MOBILITY_WEIGHT = 0;	
	//mobility balancing
	public static final int MOBILITY_BALANCE_GAUSSIAN_B = 1;
	public static final int MOBILITY_BALANCE_GAUSSIAN_C = 2;
	//material balancing
	public static final int MATERIAL_BALANCE_GAUSSIAN_B = 3;
	public static final int MATERIAL_BALANCE_GAUSSIAN_C = 4;
	//piece values
	public static final int PAWN_VALUE = 5;
	public static final int BISHOP_VALUE = 6;
	public static final int KNIGHT_VALUE = 7;
	public static final int ROOK_VALUE = 8;
	public static final int QUEEN_VALUE = 9;
	//mobility parameters
	public static final int CENTRE_WEIGHT = 10;
	public static final int THREATEN_WEIGHT = 11;
	public static final int DEFEND_WEIGHT = 12;
	public static final int PROMOTION_WEIGHT = 13;
	public static final int TRAVEL_WEIGHT = 14;
	//mobility - centre control
	public static final int CENTRE_GAUSSIAN_C = 15;
	//mobility - threatened pieces
	public static final int THREATEN_GAUSSIAN_C = 16;
	//mobility - defended pieces
	public static final int DEFEND_GAUSSIAN_C = 17;
	//mobility - proximity to promotion
	public static final int PROMOTION_DISTANCE_GAUSSIAN_C = 18;
	public static final int PROMOTION_NOTPASSED_MULTIPLIER = 19;
	//mobility - travel distance
	public static final int TRAVEL_GAUSSIAN_C = 20;
	
	//MATCH CONFIG
	
	public static final int GAMETREE_SEARCH_DEPTH = 2;
		
}
