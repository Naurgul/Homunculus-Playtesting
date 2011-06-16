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
		
	public static final int CHESSSTYLE_PARAMETERS_LENGTH = 21;
		
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
	public static final int MAX_TURNS = 100;
	
	//GUI
	
	public static final int WHITE_COLOUR = 230;
	public static final int BLACK_COLOUR = 190;
	
	public static final int DEFAULT_STRATEGY = 5;
	
	public static final double[][] DEFAULT_STYLES = 
	{
		{0.3354472693493306, 19.830235475480293, 18.530561700335078, 1.7768081862068685, 1.3039733808137042, 0.05649225395344648, 0.1407910544120916, 0.19034547826104664, 0.20868354505293008, 0.7338611746203383, 0.1520271878441486, 0.41378453978254975, 0.0845769669613337, 0.19843608945987432, 0.15117521595209354, 7.726749199055515, 0.347278098569012, 0.5557058392069201, 0.8102714323373518, 0.12464370936166658, 14.085159070322069},
		{0.17581757244876295, 12.688419958067424, 2.3938188914826766, 3.722173293753225, 2.601580315200016, 0.024368045772347413, 0.48071143706894204, 0.3348896146504394, 0.5027717163976494, 0.6342193158559463, 0.05408402443499783, 0.1929228344465623, 0.05242233889157571, 0.5864320751062897, 0.11413872712057442, 3.9066232244068955, 0.9613385434093868, 0.7815537357406848, 1.5339902531348255, 0.290061840092954, 6.041655079064958},
		{0.17581757244876295, 12.688419958067424, 2.3938188914826766, 3.722173293753225, 2.601580315200016, 0.024368045772347413, 0.48071143706894204, 0.3348896146504394, 0.5027717163976494, 0.6342193158559463, 0.05408402443499783, 0.1929228344465623, 0.05242233889157571, 0.5864320751062897, 0.11413872712057442, 3.9066232244068955, 0.9613385434093868, 0.7815537357406848, 1.5339902531348255, 0.290061840092954, 6.041655079064958},
		{0.3354472693493306, 19.830235475480293, 18.530561700335078, 1.7768081862068685, 1.3039733808137042, 0.05649225395344648, 0.1407910544120916, 0.19034547826104664, 0.20868354505293008, 0.7338611746203383, 0.1520271878441486, 0.41378453978254975, 0.0845769669613337, 0.19843608945987432, 0.15117521595209354, 7.200572815478438, 0.6724199450285671, 2.55671290728699, 0.5314364287242739, 0.6595823815155798, 8.753928700985448},
		{0.3734545034258138, 14.225689504141148, 3.3522036297840474, 9.756997948808541, 8.517838087211032, 0.19960680693205057, 0.664573601840583, 0.21082425713057096, 0.6919309933225061, 0.9728139942537101, 0.13222868500647453, 0.25193908495846473, 0.06452937538181917, 0.055271471905367964, 0.24527262846300993, 2.1511356486594018, 0.7936286866043931, 1.2404515380300334, 0.5514518737182859, 0.23504263797686697, 11.371151254188353},
		{0.3354472693493306, 19.830235475480293, 18.530561700335078, 1.7768081862068685, 1.3039733808137042, 0.05649225395344648, 0.1407910544120916, 0.19034547826104664, 0.20868354505293008, 0.6342193158559463, 0.05408402443499783, 0.1929228344465623, 0.05242233889157571, 0.2376207431284701, 0.11860186580243387, 9.333017518429136, 1.3498025538666838, 2.3491153294592433, 2.4724793805571306, 0.20622819253023472, 9.924628059459124},
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
	};	

	public static String getPieceChar(ChessSquare chessSquare)
	{		
		if (chessSquare.getPiece() != null)
		{
			if (chessSquare.getPiece().getOwner().equals(PlayerType.PLAYER_1))
			{
				if (chessSquare.getPiece().getPieceType().equals(ChessPieceType.KING))
				{
					return "\u2654";
				}
				else if (chessSquare.getPiece().getPieceType().equals(ChessPieceType.QUEEN))
				{
					return "\u2655";
				}
				else if (chessSquare.getPiece().getPieceType().equals(ChessPieceType.ROOK))
				{
					return "\u2656";
				}
				else if (chessSquare.getPiece().getPieceType().equals(ChessPieceType.BISHOP))
				{
					return "\u2657";
				}
				else if (chessSquare.getPiece().getPieceType().equals(ChessPieceType.KNIGHT))
				{
					return "\u2658";
				}
				else if (chessSquare.getPiece().getPieceType().equals(ChessPieceType.PAWN))
				{
					return "\u2659";
				}
			}
			else
			{
				if (chessSquare.getPiece().getPieceType().equals(ChessPieceType.KING))
				{
					return "\u265A";
				}
				else if (chessSquare.getPiece().getPieceType().equals(ChessPieceType.QUEEN))
				{
					return "\u265B";
				}
				else if (chessSquare.getPiece().getPieceType().equals(ChessPieceType.ROOK))
				{
					return "\u265C";
				}
				else if (chessSquare.getPiece().getPieceType().equals(ChessPieceType.BISHOP))
				{
					return "\u265D";
				}
				else if (chessSquare.getPiece().getPieceType().equals(ChessPieceType.KNIGHT))
				{
					return "\u265E";
				}
				else if (chessSquare.getPiece().getPieceType().equals(ChessPieceType.PAWN))
				{
					return "\u265F";
				}
			}
		}
		return "";
	}
		
}
