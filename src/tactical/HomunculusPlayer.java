package tactical;

import gameTheory.GameAction;
import gameTheory.MixedStrategy;
import gameTheory.ZeroSumNormalFormGame;

import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import ec.util.MersenneTwisterFast;

public class HomunculusPlayer 
{
	private int maxdepth;
	private PlayerType player;
	private MersenneTwisterFast luck;
	
	public HomunculusPlayer(int maxdepth, PlayerType player) 
	{
		this.maxdepth = maxdepth;
		this.player = player;
		luck = new MersenneTwisterFast();	
	}
	
	public HomunculusPlayer(int maxdepth, PlayerType player, MersenneTwisterFast randomizer) 
	{
		this.maxdepth = maxdepth;
		this.player = player;
		luck = randomizer;	
	}
	
	//Returns the best action to play given a game state.
	public GameAction Think(GameState rootstate) 
	{
		//TODO Check to see if there are any buggy corner cases here. :(
		
		//Generate a tree with possible future game states
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		root.setUserObject(rootstate);
		DefaultTreeModel gametree = GenerateTree(root);		
		
		//Index the tree by depth
		LinkedList<DefaultMutableTreeNode>[] TreeIndex; 
		TreeIndex = (LinkedList<DefaultMutableTreeNode>[]) new LinkedList[maxdepth+1];
		for (int i = 0; i <= maxdepth; i++)
		{
			TreeIndex[i] = new LinkedList<DefaultMutableTreeNode>();
		}

		Enumeration<DefaultMutableTreeNode> tree_enum = root.preorderEnumeration();		
		while(tree_enum.hasMoreElements())
		{
			DefaultMutableTreeNode currentNode = tree_enum.nextElement();
			int level = currentNode.getLevel();
			TreeIndex[level].add(currentNode);			
		}
		
		
		
		
		MixedStrategy eq = null;
		
		//Traverse the tree starting from the bottom level and going towards the root (using the index)
		for (int d = maxdepth; d >= 0; d--)
		{
			for (DefaultMutableTreeNode n : TreeIndex[d])
			{
				GameState state = (GameState) n.getUserObject();
				//If the node is a leaf, it gets its value from the state evalaution function
				if (n.isLeaf())
				{
					state.setPropagatedValue(player);
				}
				//if both player play at the same time, then the node gets its value from a normal form game in which each pair of actions is rewarded with the value of the state children's value.
				else if (state.whoPlaysNext().equals(PlayerType.BOTH_PLAYERS))
				{
					ZeroSumNormalFormGame g = new ZeroSumNormalFormGame();
					Enumeration<DefaultMutableTreeNode> children = n.children();
					//For each child, add a pair of actions and the reward
					while(children.hasMoreElements())
					{
						DefaultMutableTreeNode child_node = children.nextElement();
						GameState child_state = (GameState) child_node.getUserObject();
						
						GameAction last_move1 = child_state.getLastMove(PlayerType.PLAYER_1);	
						g.addAction(last_move1, PlayerType.PLAYER_1);
					
						GameAction last_move2 = child_state.getLastMove(PlayerType.PLAYER_2);
						g.addAction(last_move2, PlayerType.PLAYER_2);										
						
						g.addResult(last_move1, last_move2, child_state.getPropagatedValue());						
					}
					//The node's value is set to its expected payoff.
					eq = g.GetEquilibrium();
					state.setPropagatedValue(eq.payoff); 
				}
				//if player 1 is next, then he chooses the maximum value from his options
				else if (state.whoPlaysNext().equals(PlayerType.PLAYER_1))
				{
					double max = Double.NEGATIVE_INFINITY;
					Enumeration<DefaultMutableTreeNode> children = n.children();
					while(children.hasMoreElements())
					{
						DefaultMutableTreeNode child_node = children.nextElement();
						GameState child_state = (GameState) child_node.getUserObject();
						
						if (child_state.getPropagatedValue() > max)
						{
							max = child_state.getPropagatedValue();
						}
					}
					state.setPropagatedValue(max);
				}
				//if player 2 is next, then he chooses the minimum value from his options
				else if (state.whoPlaysNext().equals(PlayerType.PLAYER_2))
				{
					double min = Double.POSITIVE_INFINITY;
					Enumeration<DefaultMutableTreeNode> children = n.children();
					while(children.hasMoreElements())
					{
						DefaultMutableTreeNode child_node = children.nextElement();
						GameState child_state = (GameState) child_node.getUserObject();
						
						if (child_state.getPropagatedValue() < min)
						{
							min = child_state.getPropagatedValue();
						}
					}
					state.setPropagatedValue(min);
				}
				//if it is a random move, then we calculate an expected payoff
				else if (state.whoPlaysNext().equals(PlayerType.RANDOM_MOVE))
				{
					double expected_payoff = 0;
					Enumeration<DefaultMutableTreeNode> children = n.children();
					while(children.hasMoreElements())
					{
						DefaultMutableTreeNode child_node = children.nextElement();
						GameState child_state = (GameState) child_node.getUserObject();
						
						expected_payoff += child_state.getProbability() * child_state.getPropagatedValue();
					}
					state.setPropagatedValue(expected_payoff);
				}
			}
		}
		
		GameAction bestaction = null;
		double threshold = luck.nextDouble();
		
		if (player.equals(PlayerType.BOTH_PLAYERS))
		{
			//The last eq is the mixed strategy of the root (see the way the iteration works above)
			//Roll a die to decide which single action should be played in this instance.
			for (int i = 0; i < eq.probabilities.length; i++)
			{
				if (i != 0)
				{
					eq.probabilities[i] += eq.probabilities[i-1];
				}
				
				if (threshold <= eq.probabilities[i])
				{
					bestaction = eq.actions[i];
					break;
				}
			}
		}
		else if (player.equals(PlayerType.PLAYER_1))
		{
			//go through root's children, return the last move of the one with the maximum payoff
			double max = Double.NEGATIVE_INFINITY;
			Enumeration<DefaultMutableTreeNode> children = root.children();
			while(children.hasMoreElements())
			{
				DefaultMutableTreeNode child_node = children.nextElement();
				GameState child_state = (GameState) child_node.getUserObject();
				
				if (child_state.getPropagatedValue() > max)
				{
					max = child_state.getPropagatedValue();
					bestaction = child_state.getLastMove(PlayerType.PLAYER_1);
				}
			}			
		}
		else if (player.equals(PlayerType.PLAYER_2))
		{
			double min = Double.POSITIVE_INFINITY;
			Enumeration<DefaultMutableTreeNode> children = root.children();
			while(children.hasMoreElements())
			{
				DefaultMutableTreeNode child_node = children.nextElement();
				GameState child_state = (GameState) child_node.getUserObject();
				
				if (child_state.getPropagatedValue() < min)
				{
					min = child_state.getPropagatedValue();
					bestaction = child_state.getLastMove(PlayerType.PLAYER_2);
				}
			}
		}
		else
		{
			//TODO: Should never happen. Throw an exception or something!
		}
				
		return bestaction;
	}
	

	//Returns a tree of possible future game states
	private DefaultTreeModel GenerateTree(DefaultMutableTreeNode root) 
	{
		DefaultTreeModel t = new DefaultTreeModel(root);
		
		//nodesToVisit keeps track of the nodes that we will visit to generate their children.
		LinkedList<DefaultMutableTreeNode> nodesToVisit = new LinkedList<DefaultMutableTreeNode>();
		nodesToVisit.add(root);
		
		while(!nodesToVisit.isEmpty())
		{
			DefaultMutableTreeNode currentNode = nodesToVisit.pollFirst();
			
			//Only generate children if we haven't reached the maximum depth for this tree
			if (currentNode.getLevel() < maxdepth)
			{				
				//Each child state is generated from a pair of actions, one for each player
				//unless the state is followed by a random move
				
				GameState currentState = (GameState) currentNode.getUserObject();
				LinkedList<ActionSet> availableMoves = GetAvailableMoves(currentState);
				
				if (!currentState.whoPlaysNext().equals(PlayerType.RANDOM_MOVE))
				{
					for (ActionSet movepair : availableMoves)
					{
						GameState newChildState = currentState.clone();			
						newChildState.Play(movepair.move1, movepair.move2);
									
						DefaultMutableTreeNode newChildNode = new DefaultMutableTreeNode();
						newChildNode.setUserObject(newChildState);
						currentNode.add(newChildNode);
						
						nodesToVisit.add(newChildNode);
					}
				}
				else
				{
					//Play(null,null) would return a single random child, but we need them all
					LinkedList<GameState> children_states = currentState.getAllRandomChildren();
					for (GameState newChildState : children_states)
					{
						DefaultMutableTreeNode newChildNode = new DefaultMutableTreeNode();
						newChildNode.setUserObject(newChildState);
						currentNode.add(newChildNode);
						
						nodesToVisit.add(newChildNode);
					}
				}
				
			}			
		}
		return t;
	}
	
	@Deprecated
	//TODO: This should eventually be merged with Think(GameState)
	public GameAction AlphaBetaPruning(GameState rootstate)
	{
		//assume nextPlayer in gamestates can only have the values PLAYER_1 or PLAYER_2		
		GameState best = AlphaBetaPruning(rootstate, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
		PlayerType nextPlayer = rootstate.whoPlaysNext();
		return best.getLastMove(nextPlayer);
	}

	private GameState AlphaBetaPruning(GameState node, double alpha, double beta, int depth)
	{	
		if (depth > maxdepth)
		{
			node.setPropagatedValue(player);
			return node;
		}		
		
		PlayerType nextPlayer = node.whoPlaysNext();
		
		double bestUtil;		
		if (nextPlayer.equals(PlayerType.PLAYER_1))
		{
			bestUtil = Double.NEGATIVE_INFINITY;
		}
		else if (nextPlayer.equals(PlayerType.PLAYER_2))
		{
			bestUtil = Double.POSITIVE_INFINITY;
		}
		else
		{
			//TODO: Should never happen, throw exception
			bestUtil = 0;
		}
		
		GameState bestChild = null;
		
		LinkedList<GameAction> actionList = node.getActions(nextPlayer);
		if (actionList == null || actionList.isEmpty())
		{
			node.setPropagatedValue(player);
			return node;
		}
		
		for (GameAction action : actionList)
		{
			if (nextPlayer.equals(PlayerType.PLAYER_1))
			{
				GameState child = node.clone();
				child.Play(action, null);
				GameState grandChild = AlphaBetaPruning(child, alpha, beta, depth+1);
				child.setPropagatedValue(grandChild.getPropagatedValue());
				if (bestChild == null)
				{
					bestChild = child;
				}
				if (child.getPropagatedValue() > alpha)
				{
					bestChild = child;
					alpha = child.getPropagatedValue();
					bestUtil = child.getPropagatedValue();
				}
				if (alpha >= beta)
				{
					return bestChild;
				}
			}
			else if (nextPlayer.equals(PlayerType.PLAYER_2))
			{
				GameState child = node.clone();
				child.Play(null, action);
				GameState grandChild = AlphaBetaPruning(child, alpha, beta, depth+1);
				child.setPropagatedValue(grandChild.getPropagatedValue());
				if (bestChild == null)
				{
					bestChild = child;
				}
				if (child.getPropagatedValue() < beta)
				{
					bestChild = child;
					beta = child.getPropagatedValue();
					bestUtil = child.getPropagatedValue();
				}
				if (alpha >= beta)
				{
					return bestChild;
				}
			}		
			else
			{
				//TODO: Should never happen, throw exception
			}
		}
		
		return bestChild;
	}

	//Gets all possible action pairs (one for each player) from a specific game state
	private LinkedList<ActionSet> GetAvailableMoves(GameState state) 
	{
		LinkedList<ActionSet> move_pairs = new LinkedList<ActionSet>();
		LinkedList<GameAction> moves1 = null;
		LinkedList<GameAction> moves2 = null;		

		if (state.whoPlaysNext().equals(PlayerType.BOTH_PLAYERS))
		{
			moves1 = state.getActions(PlayerType.PLAYER_1);
			moves2 = state.getActions(PlayerType.PLAYER_2);	
			for (GameAction m1 : moves1)
			{
				for (GameAction m2 : moves2)
				{
					move_pairs.add(new ActionSet(m1, m2));
				}
			}
		}
		else if (state.whoPlaysNext().equals(PlayerType.PLAYER_1))
		{
			moves1 = state.getActions(PlayerType.PLAYER_1);
			for (GameAction m1 : moves1)
			{
				move_pairs.add(new ActionSet(m1, null));
			}
		}
		else if (state.whoPlaysNext().equals(PlayerType.PLAYER_2))
		{
			moves2 = state.getActions(PlayerType.PLAYER_2);
			for (GameAction m2 : moves2)
			{
				move_pairs.add(new ActionSet(null, m2));
			}
		}
			
		return move_pairs;
	}
	
	//A simple class that holds two actios in one structure.
	class ActionSet
	{
		public GameAction move1;
		public GameAction move2;
		
		public ActionSet()
		{
			move1 = null;
			move2 = null;
		}
		
		public ActionSet(GameAction m1, GameAction m2)
		{
			move1 = m1;
			move2 = m2;
		}
	}

}
