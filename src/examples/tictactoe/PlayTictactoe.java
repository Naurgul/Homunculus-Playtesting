package examples.tictactoe;

import gameTheory.GameAction;

import java.util.Scanner;

import tactical.HomunculusPlayer;
import tactical.PlayerType;

public class PlayTictactoe
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		TictacState game = new TictacState(new TictacStyle(), new TictacStyle());
		PlayerType nextUp = PlayerType.PLAYER_1;	
		Scanner read = new Scanner(System.in);
				
		while (!game.isOver())
		{
			GameAction move = null;
			if (nextUp.equals(PlayerType.PLAYER_2))
			{
				HomunculusPlayer bot = new HomunculusPlayer(3, nextUp);
				move = bot.AlphaBetaPruning(game);					
				game.Play(null, move);
				nextUp = PlayerType.PLAYER_1;
			}
			else
			{
				String textMove = new String();
				do
				{
					System.out.print("Your move: ");
					textMove = read.nextLine();	
					move = new TictacAction(textMove);
				}while (((TictacAction)move).getNum() <= 0);	
				game.Play(move, null);
				nextUp = PlayerType.PLAYER_2;
			}
			
			game.print();

		}

	}

}
