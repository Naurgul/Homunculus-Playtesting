package examples.chess;

import java.util.LinkedList;

import ec.EvolutionState;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;
import tactical.GameState;
import tactical.PlayerType;
import gameTheory.GameAction;
import genetics.PlayStyle;

public class ChessStyle extends DoubleVectorIndividual implements PlayStyle
{	
	

/*
  	OPTIMIZABLE PARAMETERS: 
 
	mobility versus material
	MOBILITY_WEIGHT	[0,1] (implied: MATERIAL_WEIGHT = 1 - MOBILITY_WEIGHT)		 
	
	mobility balancing
	MOBILITY_BALANCE_GAUSSIAN_B	[0,20)
	MOBILITY_BALANCE_GAUSSIAN_C	(0,20) (MOBILITY_BALANCE_GAUSSIAN_B * [0,1) + very small number)
	(note: set [0,1) in the config file)
	
	material balancing
	MATERIAL_BALANCE_GAUSSIAN_B	[0,10)
	MATERIAL_BALANCE_GAUSSIAN_C (0,10) (MATERIAL_BALANCE_GAUSSIAN_B * [0,1) + very small number)
	(note: set [0,1) in the config file)
	
	piece values (must have sensible values, eg pawn is the least valuable etc)
	PAWN_VALUE [0,1)
	BISHOP_VALUE [0,1)
	KNIGHT_VALUE [0,1)
	ROOK_VALUE [0,1)
	QUEEN_VALUE [0,1)
	
	mobility parameters (must add up to 1)
	CENTRE_WEIGHT [0,1)
	THREATEN_WEIGHT [0,1)
	DEFEND_WEIGHT [0,1)
	PROMOTION_WEIGHT [0,1)
	TRAVEL_WEIGHT [0,1)
	
	mobility - centre control
	CENTRE_GAUSSIAN_C [1,10)	
	mobility - threatened pieces
	THREATEN_GAUSSIAN_C [0.25,2)
	mobility - defended pieces
	DEFEND_GAUSSIAN_C [0.5,3)
	mobility - proximity to promotion
	PROMOTION_DISTANCE_GAUSSIAN_C [0.5,3)
	PROMOTION_NOTPASSED_MULTIPLIER [0,1)
	mobility - travel distance
	TRAVEL_GAUSSIAN_C [5,15)
*/
	
	@Override
	public void reset(EvolutionState state, int thread)
	{
		super.reset(state, thread);
		MersenneTwisterFast randomiser = new MersenneTwisterFast();		
		 
		//make sure the piece values are sensible (e.g. pawns never more valuable than queen etc)
		while (genome[Consts.PAWN_VALUE] >= genome[Consts.BISHOP_VALUE] || genome[Consts.PAWN_VALUE] >= genome[Consts.KNIGHT_VALUE] || genome[Consts.BISHOP_VALUE] >= genome[Consts.ROOK_VALUE] || genome[Consts.KNIGHT_VALUE] >= genome[Consts.ROOK_VALUE] || genome[Consts.ROOK_VALUE] >= genome[Consts.QUEEN_VALUE])	
		{
			genome[Consts.PAWN_VALUE] = randomiser.nextDouble();
			genome[Consts.BISHOP_VALUE] = randomiser.nextDouble();
			genome[Consts.KNIGHT_VALUE] = randomiser.nextDouble();
			genome[Consts.ROOK_VALUE] = randomiser.nextDouble();
			genome[Consts.QUEEN_VALUE] = randomiser.nextDouble();	
		}
		
		
		//mobility parameters must add up to 1
		double totalMobilityWeight = genome[Consts.CENTRE_WEIGHT] + genome[Consts.THREATEN_WEIGHT] + genome[Consts.DEFEND_WEIGHT] + genome[Consts.PROMOTION_WEIGHT] + genome[Consts.TRAVEL_WEIGHT];
		genome[Consts.CENTRE_WEIGHT] /= totalMobilityWeight;
		genome[Consts.THREATEN_WEIGHT] /= totalMobilityWeight;
		genome[Consts.DEFEND_WEIGHT] /= totalMobilityWeight;
		genome[Consts.PROMOTION_WEIGHT] /= totalMobilityWeight;
		genome[Consts.TRAVEL_WEIGHT] /= totalMobilityWeight;
		
		
		//the C parameters for the gaussian functions are a function of their corresponding B parameter
		genome[Consts.MOBILITY_BALANCE_GAUSSIAN_C] = genome[Consts.MOBILITY_BALANCE_GAUSSIAN_C] * genome[Consts.MOBILITY_BALANCE_GAUSSIAN_B] + Double.MIN_VALUE; //	(0,20]
		genome[Consts.MATERIAL_BALANCE_GAUSSIAN_C] = genome[Consts.MATERIAL_BALANCE_GAUSSIAN_C] * genome[Consts.MATERIAL_BALANCE_GAUSSIAN_B] + Double.MIN_VALUE; //	(0,20]
	}



	//returns a single number that represent's a state's utility
	//a positive number means PLAYER_1 is winning, a negative number means PLAYER_2 is winning
	public double Evaluate(ChessState state)
	{		
		//evaluate the mobility of PLAYER_1's position
		double whiteMobility = 0;		
		ChessState whiteState  = (ChessState) state.clone(false, PlayerType.PLAYER_1, true);
		LinkedList<GameAction> whiteMoves = whiteState.getActions(PlayerType.PLAYER_1);
		for (GameAction move : whiteMoves)
		{
			whiteMobility += getMoveMobilityWorth((ChessMove)move, whiteState.getBoard());
		}			
		//evaluate the mobility of PLAYER_2's position
		double blackMobility = 0;		
		ChessState blackState  = (ChessState) state.clone(false, PlayerType.PLAYER_2, true);
		LinkedList<GameAction> blackMoves = blackState.getActions(PlayerType.PLAYER_2);
		for (GameAction move : blackMoves)
		{
			blackMobility += getMoveMobilityWorth((ChessMove)move, blackState.getBoard());
		}				
		//add those up and normalise to a single state mobility utility score
		double mobility = mobilityBalance(whiteMobility, blackMobility);		
		
		//evaluate the material of PLAYER_1
		double material1 = getStateMaterialWorth(whiteState.getBoard(), PlayerType.PLAYER_1);
		//evaluate the material of PLAYER_2
		double material2 = getStateMaterialWorth(blackState.getBoard(), PlayerType.PLAYER_2);
		//add those up and normalise to a single state material utility score
		double material = materialBalance(material1, material2);
		
		//the total state utility is a weighted average of mobility and material utility 
		return (genome[Consts.MOBILITY_WEIGHT] * mobility + (1 - genome[Consts.MOBILITY_WEIGHT]) * material);
	}	
	
	

	//given the worth of both player's mobility, it returns a single value that represents who's winning in mobility
	//a positive number means PLAYER_1 is winning, a negative number means PLAYER_2 is winning
	private double mobilityBalance(double mobility1, double mobility2)
	{		
		//Use a gaussian ramp to make mobility balance smoother 
		double sign = 0;
		if (mobility1 > mobility2)
		{
			sign = 1;
		}
		else if (mobility1 < mobility2)
		{
			sign = -1;
		}
		
		double dif = Math.abs(mobility1 - mobility2); 
		if (dif > genome[Consts.MOBILITY_BALANCE_GAUSSIAN_B])
		{
			return sign;
		}
		else
		{
			double util = Math.exp(-Math.pow(dif - genome[Consts.MOBILITY_BALANCE_GAUSSIAN_B],2)/Math.pow(genome[Consts.MOBILITY_BALANCE_GAUSSIAN_C],2));
			return (sign * util);
		}		
		
	}
	
	//given the worth of both player's material, it returns a single value that represents who's winning in material
	//a positive number means PLAYER_1 is winning, a negative number means PLAYER_2 is winning
	private double materialBalance(double material1, double material2)
	{
		//Use a gaussian ramp to make material balance smoother
		double sign = 0;
		if (material1 > material2)
		{
			sign = 1;
		}
		else if (material1 < material2)
		{
			sign = -1;
		}
		
		double dif = Math.abs(material1 - material2); 
		if (dif > genome[Consts.MATERIAL_BALANCE_GAUSSIAN_B])
		{
			return sign;
		}
		else
		{
			double util = Math.exp(-Math.pow(dif - genome[Consts.MATERIAL_BALANCE_GAUSSIAN_B],2)/Math.pow(genome[Consts.MATERIAL_BALANCE_GAUSSIAN_C],2));
			return (sign * util);
		}		
	}
	
	//returns how much a player's potential move is worth
	private double getMoveMobilityWorth(ChessMove move, ChessSquare[][] board)
	{
		PlayerType player = board[move.getFromSquare().getRank()][move.getFromSquare().getFile()].getPiece().getOwner();
		ChessSquare fromSquare = move.getFromSquare();
		ChessSquare targetSquare = move.getToSquare();
		ChessPiece myPiece = board[fromSquare.getRank()][fromSquare.getFile()].getPiece();
		ChessPiece targetPiece = board[targetSquare.getRank()][targetSquare.getFile()].getPiece();
		
		//CENTRE CONTROL
		//the closer the target square to the centre of the board, the better
		double centre; //	[0,1]
		double centre_distance = Math.abs((double)(targetSquare.getFile()) - 3.5) + Math.abs((double)(targetSquare.getRank()) - 3.5); // [1,7]
		centre = Math.exp(-Math.pow(centre_distance,2)/Math.pow(genome[Consts.CENTRE_GAUSSIAN_C],2));
		
		//ATTACK ENEMY PIECE OR DEFEND OWN PIECE
		//attacking an enemy piece is good
		//defending a piece is also good
		double threaten = 0; //	[0,1]
		double defend = 0; //	[0,1]
		if (targetPiece != null)
		{
			//if the target square has an enemy piece on it 
			if (!targetPiece.getOwner().equals(player))
			{
				//the utility of threatening is based on the difference in value between the threatened piece and the threatening piece
				double difference = getPieceWorth(targetPiece.getPieceType()) - getPieceWorth(myPiece.getPieceType()); // [-1,1]
				threaten = Math.exp(-Math.pow(difference-1,2)/Math.pow(genome[Consts.THREATEN_GAUSSIAN_C],2));
			}
			else //if the target square has one of our own pieces on it
			{
				//the utility of defending is based on the total value of the defended piece and the defending piece
				double total = getPieceWorth(targetPiece.getPieceType()) + getPieceWorth(myPiece.getPieceType()); // [0,2]
				defend =  Math.exp(-Math.pow(total,2)/Math.pow(genome[Consts.DEFEND_GAUSSIAN_C],2));
			}
		}
		
		//PROXIMITY TO PROMOTION
		//having a pawn close to the last rank is good
		//having a passed pawn (ie no blocking pieces in its path) is even better
		double promotion = 0; //  [0,1]
		if (myPiece.getPieceType().equals(ChessPieceType.PAWN))
		{
			double promotion_distance = 5;
			boolean passed_pawn = true;
			if (player.equals(PlayerType.PLAYER_1))
			{
				promotion_distance = Consts.NUM_OF_RANKS-1 - targetSquare.getRank(); // [0,5]
				for (int rankCounter = targetSquare.getRank()+1; rankCounter < Consts.NUM_OF_RANKS; rankCounter++)
				{
					if (board[rankCounter][targetSquare.getFile()].getPiece() != null)
					{
						passed_pawn = false;
						break;
					}
				}
			}
			else if (player.equals(PlayerType.PLAYER_2))
			{
				promotion_distance = targetSquare.getRank(); // [0,5]
				for (int rankCounter = targetSquare.getRank()-1; rankCounter >= 0; rankCounter--)
				{
					if (board[rankCounter][targetSquare.getFile()].getPiece() != null)
					{
						passed_pawn = false;
						break;
					}
				}
			}
			promotion = Math.exp(-Math.pow(promotion_distance,2)/Math.pow(genome[Consts.PROMOTION_DISTANCE_GAUSSIAN_C],2));
			if (!passed_pawn)
			{
				promotion *= genome[Consts.PROMOTION_NOTPASSED_MULTIPLIER];
			}
		}
		
		//TRAVEL DISTANCE
		//the more distance a piece covers on the board, the better
		double travel; //	[0,1]
		int travel_distance = Math.abs(targetSquare.getFile() - fromSquare.getFile()) + Math.abs(targetSquare.getRank() - fromSquare.getRank()); // [1,14]
		travel = Math.exp(-Math.pow(travel_distance-14,2)/Math.pow(genome[Consts.PROMOTION_DISTANCE_GAUSSIAN_C],2));
		
		return (genome[Consts.CENTRE_WEIGHT] * centre) + (genome[Consts.THREATEN_WEIGHT] * threaten) + (genome[Consts.DEFEND_WEIGHT] * defend) + (genome[Consts.PROMOTION_WEIGHT] * promotion) + (genome[Consts.TRAVEL_WEIGHT] * travel);
	}
	
	//returns how much a player's material on the board is worth in total
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
	
	//returns the value of a single piece
	private double getPieceWorth(ChessPieceType pieceType)
	{
		if (pieceType.equals(ChessPieceType.PAWN))
		{
			return genome[Consts.PAWN_VALUE];
		}
		else if (pieceType.equals(ChessPieceType.BISHOP))
		{
			return genome[Consts.BISHOP_VALUE];
		}
		else if (pieceType.equals(ChessPieceType.KNIGHT))
		{
			return genome[Consts.KNIGHT_VALUE];
		}
		else if (pieceType.equals(ChessPieceType.ROOK))
		{
			return genome[Consts.ROOK_VALUE];
		}
		else if (pieceType.equals(ChessPieceType.QUEEN))
		{
			return genome[Consts.QUEEN_VALUE];
		}
		else if (pieceType.equals(ChessPieceType.KING))
		{
			return 1; //king has the maximum value by default
		}
		else //TODO: This should never happen, add an exception or something...
		{
			return -1;
		}
	}
	
	@Override
	public String toString()
	{
		return "ChessStyle " +
				"[mobility_weight=" + genome[Consts.MOBILITY_WEIGHT]
				+ ", mobility_balance_gaussian_b=" + genome[Consts.MOBILITY_BALANCE_GAUSSIAN_B]
				+ ", mobility_balance_gaussian_c=" + genome[Consts.MOBILITY_BALANCE_GAUSSIAN_C]
				+ ", material_balance_gaussian_b=" + genome[Consts.MATERIAL_BALANCE_GAUSSIAN_B]
				+ ", material_balance_gaussian_c=" + genome[Consts.MATERIAL_BALANCE_GAUSSIAN_C] 
				+ ", pawn_value=" + genome[Consts.PAWN_VALUE]
				+ ", bishop_value=" + genome[Consts.BISHOP_VALUE] 
				+ ", knight_value="	+ genome[Consts.KNIGHT_VALUE] 
				+ ", rook_value=" + genome[Consts.ROOK_VALUE]
				+ ", queen_value=" + genome[Consts.QUEEN_VALUE] 
				+ ", centre_weight=" + genome[Consts.CENTRE_WEIGHT] 
				+ ", threaten_weight=" + genome[Consts.THREATEN_WEIGHT]
				+ ", defend_weight=" + genome[Consts.DEFEND_WEIGHT]
				+ ", promotion_weight=" + genome[Consts.PROMOTION_WEIGHT] 
				+ ", travel_weight=" + genome[Consts.TRAVEL_WEIGHT]
				+ ", centre_gaussian_c=" + genome[Consts.CENTRE_GAUSSIAN_C]
				+ ", threaten_gaussian_c=" + genome[Consts.THREATEN_GAUSSIAN_C]
				+ ", defend_gaussian_c=" + genome[Consts.DEFEND_GAUSSIAN_C]
				+ ", promotion_distance_gaussian_c=" + genome[Consts.PROMOTION_DISTANCE_GAUSSIAN_C]
				+ ", promotion_notpassed_multiplier=" + genome[Consts.PROMOTION_NOTPASSED_MULTIPLIER]
				+ ", travel_gaussian_c=" + genome[Consts.TRAVEL_GAUSSIAN_C] + "]";
	}

	@Override
	public Class<?> getStateClass()
	{
		
		try
		{
			return Class.forName("examples.chess.ChessState");
		} 
		catch (ClassNotFoundException e)
		{
			System.err.println("Incorrect gamestate class specified at" + this.getClass());
			e.printStackTrace();
		}
		return null;
	}	
	
}
