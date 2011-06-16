package examples.tictactoe;

import ec.Individual;
import examples.chess.ChessSquare;
import examples.chess.ChessState;
import examples.chess.Consts;
import gameTheory.GameAction;

import java.util.Arrays;
import java.util.LinkedList;

import tactical.GameState;
import tactical.PlayerType;

public class TictacState extends GameState
{
	private char[][] board;
	private double value;
	private static final int BOARD_SIZE = 3;
	private static final int GOAL = 3;
	private static final int EMPTY_SQUARE = ' ';
	private static final int X_SQUARE = 'X';
	private static final int O_SQUARE = 'O';
	private int numTurns;
	

	public TictacState(Individual strat1, Individual strat2)
	{
		super(strat1, strat2);
		
		nextPlayer = PlayerType.PLAYER_1;
		value = 0;
		board = new char[BOARD_SIZE][BOARD_SIZE];

		for (int i = 0; i < BOARD_SIZE; i++)
		{
			for (int j = 0; j < BOARD_SIZE; j++)
			{
				board[i][j] = EMPTY_SQUARE;
			}
		}
		
		numTurns = 0;
	}

	@Override
	public boolean isOver()
	{
		int lines;
		int columns;
		for (int i = 0; i < BOARD_SIZE; i++)
		{
			lines = 0;
			columns = 0;
			for (int j = 0; j < BOARD_SIZE; j++)
			{
				if (board[i][j]==X_SQUARE)
				{
					lines++;
				}
				else if (board[i][j]==O_SQUARE)
				{
					lines--;
				}	
				
				if (board[j][i]==X_SQUARE)
				{
					columns++;
				}
				else if (board[j][i]==O_SQUARE)
				{
					columns--;
				}	
			}
			
			if (lines >= GOAL || columns >= GOAL)
			{
				value = 1;
				return true;
			}
			else if (lines <= -GOAL || columns <= -GOAL)
			{
				value = -1;
				return true;
			}
		}
		
		int diagonal1 = 0;
		int diagonal2 = 0;
		for (int i = 0; i < BOARD_SIZE; i++)
		{
			if (board[i][i] == X_SQUARE)
			{
				diagonal1++;
			}
			else if (board[i][i] == O_SQUARE)
			{
				diagonal1--;
			}
			
			if (board[i][BOARD_SIZE-i-1] == X_SQUARE)
			{
				diagonal2++;
			}
			else if ((board[i][BOARD_SIZE-i-1] == O_SQUARE))
			{
				diagonal2--;
			}
		}
		
		if (diagonal1 >= GOAL || diagonal2 >= GOAL)
		{
			value = 1;
			return true;
		}
		else if (diagonal1 <= -GOAL || diagonal2 <= -GOAL)
		{
			value = -1;
			return true;
		}
		
		return false;
	}

	@Override
	public LinkedList<GameAction> getActions(PlayerType player)
	{
		LinkedList<GameAction> actions = new LinkedList<GameAction>();
		for (int i = 0; i < BOARD_SIZE; i++)
		{
			for (int j = 0; j < BOARD_SIZE; j++)
			{
				if (board[i][j] == EMPTY_SQUARE)
				{
					actions.add(new TictacAction(i,j));
				}
			}
		}
		return actions;
	}

	@Override
	public void Play(GameAction action1, GameAction action2)
	{
		if (action1 != null)
		{
			board[((TictacAction)action1).getX()][((TictacAction)action1).getY()] = X_SQUARE;
			lastMove1 = action1;
			nextPlayer = PlayerType.PLAYER_2;
		}
		else if (action2 != null)
		{
			board[((TictacAction)action2).getX()][((TictacAction)action2).getY()] = O_SQUARE;
			lastMove2 = action2;
			nextPlayer = PlayerType.PLAYER_1;
		}		
		numTurns++;
	}

	@Override
	public double GetValue(PlayerType player)
	{
		if (!isOver())
		{
			return 0;
		}
		return value;
	}

	@Override
	public GameState clone()
	{
		TictacState newState = new TictacState(strategy1, strategy2);
		for (int i = 0; i < BOARD_SIZE; i++)
		{
			for (int j = 0; j < BOARD_SIZE; j++)
			{
				newState.board[i][j] = board[i][j];
			}
		}
		newState.nextPlayer = this.nextPlayer;
		newState.propagatedValue = this.propagatedValue;
		newState.probability = this.probability;
		newState.lastMove1 = this.lastMove1;
		newState.lastMove2 = this.lastMove2;
		return newState;
	}

	@Override
	public LinkedList<GameState> getAllRandomChildren()
	{
		// No random moves in this game!
		return null;
	}

	@Override
	public Class<?> getStyleClass()
	{
		try
		{
			return Class.forName("examples.tictactoe.TictacStyle");
		} 
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void print()
	{
		//TODO: Make this work for arbitrary action name length
		for (int i = 0; i < BOARD_SIZE; i++)
		{
			System.out.println();
			for (int j = 0; j < BOARD_SIZE; j++)
			{
				char output;
				if (board[i][j] == EMPTY_SQUARE)
				{
					output = new TictacAction(i,j).GetName().charAt(0);
				}
				else
				{
					output = board[i][j];
				}
				System.out.print(output);
			}
		}
		System.out.println("\n");
		
	}

	@Override
	public int compareTo(GameState o)
	{
		return this.numTurns - ((TictacState)o).numTurns;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(board);
		result = prime * result + numTurns;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TictacState other = (TictacState) obj;
		if (!Arrays.equals(board, other.board))
			return false;
		if (numTurns != other.numTurns)
			return false;
		return true;
	}

}
