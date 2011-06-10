package examples.chess;

import java.util.LinkedList;
import java.util.Scanner;

import tactical.HomunculusPlayer;
import tactical.PlayerType;
import util.Logger;
import gameTheory.GameAction;


public class TestChess
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		//Logger log = new Logger("C:\\Users\\Νικόλας\\Desktop\\timelogs\\4.txt");
				
		ChessStyle strat1 = new ChessStyle();
		ChessStyle strat2 = new ChessStyle();
		
		ChessState game = new ChessState(strat1, strat2);
		
		PlayerType nextUp = PlayerType.PLAYER_1;
		
		System.out.println("\t" + strat1.toString());
		System.out.println("\t" + strat2.toString());
		
		String[] moveSequence = {};
		int sequencePosition = 0;
		


		for (int turn = 1; turn <= 100; turn++)
		{
			GameAction move = null;
			LinkedList<GameAction> moveList = game.getActions(nextUp);
			if (moveList == null || moveList.isEmpty())
			{
				break;
			}
			
			if (sequencePosition < moveSequence.length)
			{
				move = extractMove(moveSequence[sequencePosition], moveList);
				System.out.println(turn + ". " + nextUp + ": " + move);
				sequencePosition++;
			}
			else
			{
				double time = 0;
				double totalTime = 0;
				int depth = 0;
				for(depth = 2; time <= 0.1; depth++)
				{
					long startTime = System.currentTimeMillis();
					HomunculusPlayer bot = new HomunculusPlayer(depth, nextUp);
					move = bot.AlphaBetaPruning(game);	
					long endTime = System.currentTimeMillis();
					time = (double)((endTime - startTime)) / 1000;
					totalTime += time;
				}				
				System.out.println(turn + ". " + nextUp + ": " + move + " (time=" + totalTime + "'', depth=" + (depth-1) + ")");
				//log.Log(String.valueOf(totalTime));
			}				
			
			game.Play(move);	
			System.out.println("\tUtil1: " + game.GetValue(PlayerType.PLAYER_1));
			System.out.println("\tUtil2: " + game.GetValue(PlayerType.PLAYER_2));
			
			
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
		//log.Commit();
		
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
