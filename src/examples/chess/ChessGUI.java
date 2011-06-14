package examples.chess;

import gameTheory.GameAction;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;

import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

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
	


	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{

		double[][] params = 
		{
				{0.3354472693493306, 19.830235475480293, 18.530561700335078, 1.7768081862068685, 1.3039733808137042, 0.05649225395344648, 0.1407910544120916, 0.19034547826104664, 0.20868354505293008, 0.7338611746203383, 0.1520271878441486, 0.41378453978254975, 0.0845769669613337, 0.19843608945987432, 0.15117521595209354, 7.726749199055515, 0.347278098569012, 0.5557058392069201, 0.8102714323373518, 0.12464370936166658, 14.085159070322069},
				{0.17581757244876295, 12.688419958067424, 2.3938188914826766, 3.722173293753225, 2.601580315200016, 0.024368045772347413, 0.48071143706894204, 0.3348896146504394, 0.5027717163976494, 0.6342193158559463, 0.05408402443499783, 0.1929228344465623, 0.05242233889157571, 0.5864320751062897, 0.11413872712057442, 3.9066232244068955, 0.9613385434093868, 0.7815537357406848, 1.5339902531348255, 0.290061840092954, 6.041655079064958},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
		};		
		
		ChessStyle strat = new ChessStyle();
		strat.init(params[1]);
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
		UIManager.put("ToggleButton.select", new Color(100,100,150));
		
		
		frame = new JFrame();
		frame.setBounds(100, 100, 648, 648);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(Consts.NUM_OF_RANKS, Consts.NUM_OF_FILES, 0, 0));

		
		boardButtons = new JToggleButton[Consts.NUM_OF_RANKS][Consts.NUM_OF_FILES];
		
		for (int rank = Consts.NUM_OF_RANKS - 1; rank >= 0; rank--)
		{
			for (int file = 0; file < Consts.NUM_OF_FILES; file++)
			{
				boardButtons[rank][file] = new JToggleButton(Consts.getPieceChar(game.getBoard()[rank][file]));
				boardButtons[rank][file].setFont(new Font("Dialog", Font.PLAIN, 40));
				boardButtons[rank][file].setDisabledIcon(new ImageIcon("img.gif"));
				
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
				
				frame.getContentPane().add(boardButtons[rank][file]);
			}
		}
		
		updateBoard();
		logicState = GuiState.HUMAN_THINKING;

	}
	

	
	
	public void updateBoard()
	{
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
					shouldBeOn = true;
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
				}
				else if (logicState.equals(GuiState.HUMAN_CHOSE_PIECE))
				{
					if (event.getStateChange() == event.SELECTED)
					{
						squareTo = new ChessSquare(file, rank, null);
						ChessMove move = new ChessMove(squareFrom, squareTo);
						LinkedList<GameAction> moveList = game.getActions(PlayerType.PLAYER_1);
						if (moveList.contains(move))
						{
							boardButtons[rank][file].setCursor(new Cursor(Cursor.WAIT_CURSOR));
							setAllButtonsEnabled(false);
							game.Play(move);
							deselectAllButtons();
							updateBoard();							
							logicState = GuiState.AI_THINKING;
							HomunculusPlayer bot = new HomunculusPlayer(3, PlayerType.PLAYER_2);
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
	
	
	private enum GuiState
	{
		HUMAN_THINKING,
		HUMAN_CHOSE_PIECE,
		AI_THINKING;
	}


	

}
