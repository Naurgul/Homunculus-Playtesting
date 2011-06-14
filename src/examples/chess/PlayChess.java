package examples.chess;

import gameTheory.GameAction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import tactical.HomunculusPlayer;
import tactical.PlayerType;

public class PlayChess
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		
		double params[] = new double[Consts.CHESSSTYLE_PARAMETERS_LENGTH];
		try 
		{
			for (int i = 0; i < Consts.CHESSSTYLE_PARAMETERS_LENGTH; i++)
			{
				params[i] = Double.parseDouble(args[i]);	
			}
	        
	    } 
		catch (Exception e) 
		{
	        System.err.println("Incorrect arguments. This application requires " + Consts.CHESSSTYLE_PARAMETERS_LENGTH + " arguments of type double to generate a valid chess playstyle.");
	        System.exit(1);
	    }


		
		ChessStyle strat = new ChessStyle();
		strat.init(params);
		
		ChessState game = new ChessState(strat, strat);
		PlayerType nextUp = PlayerType.PLAYER_1;	
		Scanner read = new Scanner(System.in);
		
		System.out.println("\t" + strat.toString());
			
		for (int turn = 1; turn <= 200; turn++)
		{
			GameAction move = null;
			LinkedList<GameAction> moveList = game.getActions(nextUp);
			if (moveList == null || moveList.isEmpty())
			{
				break;
			}
			
			if (nextUp.equals(PlayerType.PLAYER_2))
			{
				double time = 0;
				double totalTime = 0;
				int depth;
				for(depth = 3; time <= 1.0; depth++)
				{
					long startTime = System.currentTimeMillis();
					HomunculusPlayer bot = new HomunculusPlayer(depth, nextUp);
					move = bot.AlphaBetaPruning(game);	
					long endTime = System.currentTimeMillis();
					time = (double)((endTime - startTime)) / 1000;
					totalTime += time;
				}				
				System.out.println(turn + ". " + nextUp + ": " + move + " (time=" + String.format("%.1f", totalTime) + "'', depth=" + (depth-1) + ")");	
				
			}
			else
			{
				String textMove = new String();
				do
				{
					System.out.print(turn + ". " + nextUp + ": ");
					textMove = read.nextLine();	
					move = extractMove(textMove, moveList);
				}while (move == null);				
			}

		
			
			game.Play(move);	
			System.out.println("\tUtil1: " + String.format("%.3f", game.GetValue(PlayerType.PLAYER_1)));
			
			
			if (nextUp.equals(PlayerType.PLAYER_1))
			{
				nextUp = PlayerType.PLAYER_2;
			}
			else
			{
				nextUp = PlayerType.PLAYER_1;
			}
		}
		game.printBoard();		
		
	}

	public static GameAction extractMove(String text, LinkedList<GameAction> list)
	{
		ChessMove move = null;
		for (GameAction action : list)
		{
			move = (ChessMove) action;
			if (move.toString().equals(text))
			{
				break;
			}
			else
			{
				move = null;
			}
		}
		
		return move;
	}

}
