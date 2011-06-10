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

		double params[] = { 0.6589076169973126, 0.3449654035448546, 0.15053506299879083, 3.5885142342127594, 2.3949425912146727, 0.2364870179628754, 0.32192252950304834, 0.31891185908532826, 0.8298638636699257, 0.85286146620178, 0.22700618267180914, 0.3518945381714559, 0.06428710459213835, 0.12331610118138077, 0.2334960733832158, 2.5709880132138445, 0.42052423461774396, 2.96582474932872, 1.210687340615451, 0.43951873079741277, 5.5291090851146265};
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
