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

		double params[] = {0.6169228677247973, 15.043718728437366, 6.639907999686099, 0.8348754239121514, 0.1167784942154325, 0.23972010377140407, 0.6842678554624262, 0.4867228003180374, 0.7199942367632433, 0.9948140532086048, 0.055518805679279026, 0.32084045517314624, 0.03510134437133858, 0.15863663932876051, 0.14429773324194153, 8.437935158162173, 1.7328697407048304, 0.7616819991609922, 1.756012128521803, 0.7520517131875623, 7.416795294727195};
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
