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

		double params[] = {0.26397043930078534, 0.9156589182662067, 0.7336267109207087, 1.6179456809294102, 6.021257131788403, 0.06080237542040434, 0.16161816604133727, 0.12392522929172112, 0.5836157933574644, 0.6964819080063572, 0.10155858796764046, 0.1752755856043969, 0.4084250247304054, 0.137955228582397, 0.21488340619501695, 8.443751810928905, 0.810456418163825, 1.6736325444571927, 0.8811193277548597, 0.928602258167742, 6.868074249107106};
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
				for(depth = 3; time <= 1.5; depth++)
				{
					long startTime = System.currentTimeMillis();
					HomunculusPlayer bot = new HomunculusPlayer(depth, nextUp);
					move = bot.AlphaBetaPruning(game);	
					long endTime = System.currentTimeMillis();
					time = (double)((endTime - startTime)) / 1000;
					totalTime += time;
				}				
				System.out.println(turn + ". " + nextUp + ": " + move + " (time=" + totalTime + "'', depth=" + (depth-1) + ")");	
				
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
			System.out.println("\tUtil1: " + game.GetValue(PlayerType.PLAYER_1));
			
			
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
