package examples.chess;

import gameTheory.GameAction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import tactical.HomunculusPlayer;
import tactical.PlayerType;

public class ChessGUI
{

	private JFrame frame;
	private JToggleButton[][] boardButtons;
	private static ChessState game;
	private static GuiState logicState;
	private ChessSquare squareFrom; 
	private ChessSquare squareTo;
	private JProgressBar gameValue;
	private JSlider tacticsSlider;
	private JSlider strategySlider; 
	


	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{

		ChessStyle strat = new ChessStyle();
		strat.init(Consts.DEFAULT_STYLES[Consts.DEFAULT_STRATEGY]);
		game = new ChessState(strat, strat);
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				try
				{
					ChessGUI window = new ChessGUI();
					window.frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ChessGUI()
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		UIManager.put("ToggleButton.disabledText", Color.BLACK);
		UIManager.put("ToggleButton.select", new Color(150,150,200));
		UIManager.put("ToggleButton.foreground", Color.BLACK);		
		
		frame = new JFrame();
		frame.setBounds(100, 100, 700, 740);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel toolbarPanel = new JPanel();
		frame.getContentPane().add(toolbarPanel, BorderLayout.NORTH);
		toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.X_AXIS));

		
		JButton newGameButton = new JButton("new game");
		newGameButton.addActionListener(new NewGameListener());
		toolbarPanel.add(newGameButton);
		
		
		JLabel lblValue = new JLabel("     value: ");
		toolbarPanel.add(lblValue);
		
		gameValue = new JProgressBar();
		gameValue.setMaximum(1000);
		toolbarPanel.add(gameValue);
		
		JLabel lblTactics = new JLabel("     tactics: ");
		toolbarPanel.add(lblTactics);
		
		tacticsSlider = new JSlider();
		tacticsSlider.setPaintTicks(true);
		tacticsSlider.setSnapToTicks(true);
		tacticsSlider.setMinimum(1);
		tacticsSlider.setMajorTickSpacing(1);
		tacticsSlider.setValue(Consts.GAMETREE_SEARCH_DEPTH);
		tacticsSlider.setMinorTickSpacing(1);
		tacticsSlider.setMaximum(4);
		tacticsSlider.setForeground(Color.LIGHT_GRAY);
		toolbarPanel.add(tacticsSlider);
		
		JLabel lblStrategy = new JLabel("     strategy: ");
		toolbarPanel.add(lblStrategy);
		
		strategySlider = new JSlider();
		strategySlider.setPaintTicks(true);
		strategySlider.setSnapToTicks(true);
		strategySlider.setMajorTickSpacing(1);
		strategySlider.setValue(Consts.DEFAULT_STRATEGY);
		strategySlider.setMinorTickSpacing(1);
		strategySlider.setMaximum(9);
		strategySlider.setForeground(Color.LIGHT_GRAY);
		toolbarPanel.add(strategySlider);
		
		
		
		JPanel boardPanel = new JPanel();
		boardPanel.setLayout(new GridLayout(Consts.NUM_OF_RANKS, Consts.NUM_OF_FILES, 0, 0));
		frame.getContentPane().add(boardPanel, BorderLayout.CENTER);
		
	
		boardButtons = new JToggleButton[Consts.NUM_OF_RANKS][Consts.NUM_OF_FILES];
		
		for (int rank = Consts.NUM_OF_RANKS - 1; rank >= 0; rank--)
		{
			for (int file = 0; file < Consts.NUM_OF_FILES; file++)
			{
				boardButtons[rank][file] = new JToggleButton(Consts.getPieceChar(game.getBoard()[rank][file]));
				boardButtons[rank][file].setFont(new Font("Dialog", Font.PLAIN, 40));

				
				if (rank % 2 == 0)
				{
					if (file % 2 == 0)
					{
						boardButtons[rank][file].setBackground(new Color(Consts.BLACK_COLOUR, Consts.BLACK_COLOUR, Consts.BLACK_COLOUR));	
					}
					else
					{
						boardButtons[rank][file].setBackground(new Color(Consts.WHITE_COLOUR, Consts.WHITE_COLOUR, Consts.WHITE_COLOUR));
					}
				}
				else
				{					
					if (file % 2 == 0)
					{
						boardButtons[rank][file].setBackground(new Color(Consts.WHITE_COLOUR, Consts.WHITE_COLOUR, Consts.WHITE_COLOUR));
					}
					else
					{
						boardButtons[rank][file].setBackground(new Color(Consts.BLACK_COLOUR, Consts.BLACK_COLOUR, Consts.BLACK_COLOUR));
					}
				}
				
				boardButtons[rank][file].addItemListener(new BoardSquareListener(rank, file));
				
				boardPanel.add(boardButtons[rank][file]);
			}
		}
		
		updateBoard();
		logicState = GuiState.HUMAN_THINKING;

	}
	

	
	
	public void updateBoard()
	{
		int val = (int)(game.GetValue(PlayerType.PLAYER_1) * 1000);
		if (val > 0)
		{
			gameValue.setForeground(Color.GREEN);
		}
		else
		{
			gameValue.setForeground(Color.RED);
		}
		gameValue.setValue(Math.abs(val));		
		
		for (int rank = Consts.NUM_OF_RANKS - 1; rank >= 0; rank--)
		{
			for (int file = 0; file < Consts.NUM_OF_FILES; file++)
			{
				boolean shouldBeOn = false;
				ChessSquare square = game.getBoard()[rank][file];
				String text = Consts.getPieceChar(square);
				boardButtons[rank][file].setText(text);
				if (square.getPiece() != null && square.getPiece().getOwner().equals(PlayerType.PLAYER_1))
				{
					LinkedList<GameAction> moveList = game.getActions(PlayerType.PLAYER_1);
					if (moveList != null)
					{
						for (GameAction move : moveList)
						{						
							if ( ( ((ChessMove)move).getFromSquare().getFile() == square.getFile() ) && ( ((ChessMove)move).getFromSquare().getRank() == square.getRank() ) )
							{
								shouldBeOn = true;
								break;
							}						
						}
					}				
				}
				boardButtons[rank][file].setEnabled(shouldBeOn);
			}
		}		
	}
	
	public void setAllButtonsEnabled(boolean b)
	{
		for (int rank = Consts.NUM_OF_RANKS - 1; rank >= 0; rank--)
		{
			for (int file = 0; file < Consts.NUM_OF_FILES; file++)
			{
				boardButtons[rank][file].setEnabled(b);
			}
		}
	}
	
	public void deselectAllButtons()
	{
		for (int rank = Consts.NUM_OF_RANKS - 1; rank >= 0; rank--)
		{
			for (int file = 0; file < Consts.NUM_OF_FILES; file++)
			{
				if (!boardButtons[rank][file].isEnabled())
				{
					boardButtons[rank][file].setSelected(false);	
				}				
			}
		}
		
	}
	
		
	
	class BoardSquareListener implements ItemListener
	{		
		private int rank;
		private int file;

		BoardSquareListener(int rank, int file)
		{
			super();
			this.rank = rank;
			this.file = file;
		}

		@Override
		public void itemStateChanged(ItemEvent event)
		{
			if (boardButtons[rank][file].isEnabled())
			{
				if (logicState.equals(GuiState.HUMAN_THINKING))
				{
					if (event.getStateChange() == event.SELECTED)
					{
						if (game.getBoard()[rank][file].getPiece() != null && game.getBoard()[rank][file].getPiece().getOwner().equals(PlayerType.PLAYER_1))
						{
							deselectAllButtons();
							squareFrom = new ChessSquare(file, rank, null);
							logicState = GuiState.HUMAN_CHOSE_PIECE;
							setAllButtonsEnabled(true);
						}						
					}
					else
					{
						setAllButtonsEnabled(false);
						deselectAllButtons();
						updateBoard();
					}
				}
				else if (logicState.equals(GuiState.HUMAN_CHOSE_PIECE))
				{
					if (event.getStateChange() == event.SELECTED)
					{
						squareTo = new ChessSquare(file, rank, null);
						ChessMove move = new ChessMove(squareFrom, squareTo);
						LinkedList<GameAction> moveList = game.getActions(PlayerType.PLAYER_1);
						if (moveList != null && moveList.contains(move))
						{
							boardButtons[rank][file].setCursor(new Cursor(Cursor.WAIT_CURSOR));
							setAllButtonsEnabled(false);
							game.Play(move);
							deselectAllButtons();
							updateBoard();							
							logicState = GuiState.AI_THINKING;
							HomunculusPlayer bot = new HomunculusPlayer(tacticsSlider.getValue(), PlayerType.PLAYER_2);
							move = (ChessMove) bot.AlphaBetaPruning(game);	
							Toolkit.getDefaultToolkit().beep();
							game.Play(move);						
							updateBoard();
							boardButtons[move.getFromSquare().getRank()][move.getFromSquare().getFile()].setSelected(true);
							boardButtons[move.getToSquare().getRank()][move.getToSquare().getFile()].setSelected(true);
							logicState = GuiState.HUMAN_THINKING;
							boardButtons[rank][file].setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						}
						else
						{
							setAllButtonsEnabled(false);
							deselectAllButtons();
							updateBoard();
							logicState = GuiState.HUMAN_THINKING;
						}
					}
					else if (event.getStateChange() == event.DESELECTED)
					{
						updateBoard();
						deselectAllButtons();
						logicState = GuiState.HUMAN_THINKING;
					}
				}
			}			
		}	
	}
	
	class NewGameListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			ChessStyle strat = new ChessStyle();
			strat.init(Consts.DEFAULT_STYLES[strategySlider.getValue()]);
			game = new ChessState(strat, strat);
			setAllButtonsEnabled(false);
			deselectAllButtons();
			updateBoard();
			logicState = GuiState.HUMAN_THINKING;			
		}	
	}
	
	
	private enum GuiState
	{
		HUMAN_THINKING,
		HUMAN_CHOSE_PIECE,
		AI_THINKING;
	}


	

}
