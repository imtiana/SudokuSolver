package sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.lang.Object;

/**
 * Place for your code.
 */
public class SudokuSolver {

	/**
	 * @return names of the authors and their student IDs (1 per line).
	 */
	public String authors() {
		// TODO write it;
		return "NAMES OF THE AUTHORS AND THEIR STUDENT IDs (1 PER LINE)";
	}

	/**
	 * Performs constraint satisfaction on the given Sudoku board using Arc Consistency and Domain Splitting.
	 * 
	 * @param board the 2d int array representing the Sudoku board. Zeros indicate unfilled cells.
	 * @return the solved Sudoku board
	 */
	public int[][] solve(int[][] board)
	{
		List<List<List<Integer>>> domains = new ArrayList<List<List<Integer>>>();
		for (int i = 0; i < 9; i++)
		{
			domains.add(new ArrayList<List<Integer>>());
			for (int j = 0; j < 9; j++)
			{
				domains.get(i).add(new ArrayList<Integer>());
			}
		}
		Set<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> toDoArcs = 
				new HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>();
		
		for (int i = 0; i < board.length; i++)
		{
			for (int j = 0; j < board[i].length; j++)
			{
				int valueAtBoard = board[i][j];
				if (valueAtBoard == 0)
				{
					domains.get(i).get(j).addAll(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}));
				}
				else
				{
					domains.get(i).get(j).add(valueAtBoard);
				}
				
				for (int k = 0; k < 9; k++)
				{
					if (k == j) continue;
					
					toDoArcs.add(new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(new Pair<Integer, Integer>(i, j),
																						  new Pair<Integer, Integer>(i, k)));
				}
				
				for (int k = 0; k < 9; k++)
				{
					if (k == i) continue;
					
					toDoArcs.add(new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(new Pair<Integer, Integer>(i, j),
																						  new Pair<Integer, Integer>(k, j)));
				}
				
				// TODO: ToDoArcs for Box
			}
		}
		
		Map<Pair<Integer, Integer>, ArrayList<Pair<Integer, Integer>>> arcMap = 
				new HashMap<Pair<Integer, Integer>, ArrayList<Pair<Integer, Integer>>>();
		
		for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> arc : toDoArcs)
		{
			Pair<Integer, Integer> thisNode = arc.first;
			Pair<Integer, Integer> otherNode = arc.second;
			
			if (arcMap.containsKey(thisNode))
			{
				ArrayList<Pair<Integer, Integer>> otherNodes = arcMap.get(thisNode);
				otherNodes.add(otherNode);	// check if this is actually added ot the arcMap
			}
			else
			{
				ArrayList<Pair<Integer, Integer>> otherNodes = new ArrayList<Pair<Integer, Integer>>();
				otherNodes.add(otherNode);
				arcMap.put(thisNode, otherNodes);
			}
		}
		
		while (!toDoArcs.isEmpty())
		{
			@SuppressWarnings("unchecked")
			Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> arc = 
					(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>) toDoArcs.toArray()[0];
			toDoArcs.remove(arc);	// make sure arc's reference is not removed at this point
			
			Pair<Integer, Integer> thisNode = arc.first;
			Pair<Integer, Integer> otherNode = arc.second;
			List<Integer> domainOfOtherNode = domains.get(otherNode.first).get(otherNode.second);
			
			List<Integer> domainOfThisNodeThatSatisfiesConstraint = domains.get(thisNode.first).get(thisNode.second);
			int countOfDomainOfThisNodeBeforePruning = domainOfThisNodeThatSatisfiesConstraint.size();
			for (int i = domainOfThisNodeThatSatisfiesConstraint.size() - 1; i >= 0; --i)
			{
				int thisDomainInt = domainOfThisNodeThatSatisfiesConstraint.get(i);
				if (!doesOtherNodeContainAnyIntOtherThanThisInt(thisDomainInt, domainOfOtherNode))
				{
					domainOfThisNodeThatSatisfiesConstraint.remove(i);
				}
			}
			
			int countOfDomainOfThisNodeAfterPruning = domainOfThisNodeThatSatisfiesConstraint.size();
			
			if (countOfDomainOfThisNodeAfterPruning != countOfDomainOfThisNodeBeforePruning)
			{				
				ArrayList<Pair<Integer, Integer>> otherNodesThatAreConnectedToTheConstraintsThatNeedToBeReconsidered = 
						arcMap.get(thisNode);
				
				for (int i = 0; i < otherNodesThatAreConnectedToTheConstraintsThatNeedToBeReconsidered.size(); i++)
				{
					Pair<Integer, Integer> otherNodeToReconsider = otherNodesThatAreConnectedToTheConstraintsThatNeedToBeReconsidered.get(i);
					Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> arcToReconsider = 
							new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(otherNodeToReconsider, thisNode);
					
					if (!toDoArcs.contains(arcToReconsider))
					{
						toDoArcs.add(arcToReconsider);
					}
				}
			}
		}
		
		System.out.println();
		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				List<Integer> domainsTemp = domains.get(i).get(j);
				for (int k = 0; k < domainsTemp.size(); k++)
				{
					System.out.print(domainsTemp.get(k));
				}
				
				System.out.print(" ");
			}
			System.out.println();
		}
		
		return board;
	}
	
	public boolean doesOtherNodeContainAnyIntOtherThanThisInt(int thisInt, List<Integer> otherNodeDomain)
	{
		for (int i = 0; i < otherNodeDomain.size(); ++i)
		{
			int otherInt = otherNodeDomain.get(i);
			if (thisInt != otherInt)
				return true;
		}
		
		return false;
	}
	
	public class Pair<A, B>
	{
	    A first = null;
	    B second = null;

	    Pair(A first, B second)
	    {
	        this.first = first;
	        this.second = second;
	    }
	    
	    @Override
	    public int hashCode()
	    {
	        return Objects.hash(first, second);
	    }

	    @Override
	    public boolean equals(Object o)
	    {
	        if (o instanceof Pair)
	        {
	        	Pair<?, ?> pair = (Pair<?, ?>) o;
	        	return first.equals(pair.first) && second.equals(pair.second);
	        }
	        
	        return false;
	    }
	}
}
