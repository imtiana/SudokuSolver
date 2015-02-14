package sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
public class SudokuSolver
{
	/**
	 * @return names of the authors and their student IDs (1 per line).
	 */
	public String authors()
	{
		return " Sae Young Kim    30172092    \n Jihyung Im       60411089";
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
				
				final int firstBoxSize = 3;
				final int secondBoxSize = 6;
				
				int boxRowNumber;
				int boxColNumber;
				if (i < firstBoxSize)
				{
					boxRowNumber = 0;
				}
				else if (i < secondBoxSize)
				{
					boxRowNumber = 1;
				}
				else
				{
					boxRowNumber = 2;
				}
				
				if (j < firstBoxSize)
				{
					boxColNumber = 0;
				}
				else if (j < secondBoxSize)
				{
					boxColNumber = 1;
				}
				else
				{
					boxColNumber = 2;
				}
				
				for (int r = firstBoxSize * boxRowNumber; r < firstBoxSize * boxRowNumber + firstBoxSize; r++)
				{
					for (int c = firstBoxSize * boxColNumber; c < firstBoxSize * boxColNumber + firstBoxSize; c++)
					{
						if (r == i && c == j) continue;
						
						toDoArcs.add(new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(new Pair<Integer, Integer>(i, j),
																							  new Pair<Integer, Integer>(r, c)));
					}
				}
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
				otherNodes.add(otherNode);
			}
			else
			{
				ArrayList<Pair<Integer, Integer>> otherNodes = new ArrayList<Pair<Integer, Integer>>();
				otherNodes.add(otherNode);
				arcMap.put(thisNode, otherNodes);
			}
		}
		
		List<Integer> isFound = new ArrayList<Integer>();	// Need some object that is mutable to check if solution is found
		List<List<List<Integer>>> resultDomains = new ArrayList<List<List<Integer>>>();
		for (int i = 0; i < 9; i++)
		{
			resultDomains.add(new ArrayList<List<Integer>>());
			for (int j = 0; j < 9; j++)
			{
				resultDomains.get(i).add(new ArrayList<Integer>());
			}
		}
		solveHelper(arcMap, toDoArcs, domains, resultDomains, isFound);
		
		for (int i = 0; i < resultDomains.size(); i++)
		{
			for (int j = 0; j < resultDomains.get(i).size(); j++)
			{
				List<Integer> domainsTemp = resultDomains.get(i).get(j);
				for (int k = 0; k < domainsTemp.size(); k++)
				{
					if (domainsTemp.size() == 1)
						board[i][j] = domainsTemp.get(k);
				}
			}
		}
		
		return board;
	}
	
	private void solveHelper(Map<Pair<Integer, Integer>, ArrayList<Pair<Integer, Integer>>> arcMap, 
							Set<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> toDoArcs, 
							List<List<List<Integer>>> domains,
							List<List<List<Integer>>> resultDomains,
							List<Integer> isFound)
	{		
		while (!toDoArcs.isEmpty())
		{
			@SuppressWarnings("unchecked")
			Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> arc = 
					(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>) toDoArcs.iterator().next();
			toDoArcs.remove(arc);
			
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
				addArcsToReconsider(arcMap, toDoArcs, thisNode);
			}
		}
		
		for (int i = 0; i < domains.size(); i++)
		{
			for (int j = 0; j < domains.get(i).size(); j++)
			{
				int numberOfDomainValues = domains.get(i).get(j).size();
				//System.out.println("Solver i: " + i + "    j: " + j + "     Number of values: " + numberOfDomainValues);
				if (numberOfDomainValues == 0)
					return;
				
				//printDomains(domains);
				if (numberOfDomainValues > 1)
				{
					//System.out.println("Splitting Domain");
					int midIndex = (int) Math.ceil((double)numberOfDomainValues / 2);
					List<List<List<Integer>>> copyOfDomains = getCopyOfDomains(domains);
					List<Integer> valuesOfDomain = copyOfDomains.get(i).get(j);
					List<Integer> copyOfValueOfDomain = new ArrayList<Integer>(valuesOfDomain);

					List<Integer> splitDomain1 = copyOfValueOfDomain.subList(0, midIndex);
					valuesOfDomain.clear();
					valuesOfDomain.addAll(splitDomain1);
					addArcsToReconsider(arcMap, toDoArcs, new Pair<Integer, Integer>(i, j));
					solveHelper(arcMap, toDoArcs, copyOfDomains, resultDomains, isFound);
					if (!isFound.isEmpty())
					{
						return;
					}

					copyOfDomains = getCopyOfDomains(domains);
					valuesOfDomain = copyOfDomains.get(i).get(j);
					List<Integer> splitDomain2 = copyOfValueOfDomain.subList(midIndex, numberOfDomainValues);
					valuesOfDomain.clear();
					valuesOfDomain.addAll(splitDomain2);
					addArcsToReconsider(arcMap, toDoArcs, new Pair<Integer, Integer>(i, j));	// area of performance improvement
					solveHelper(arcMap, toDoArcs, copyOfDomains, resultDomains, isFound);
					if (!isFound.isEmpty())
					{
						return;
					}
				}
			}
		}
		
		isFound.add(new Integer(0));
		if (!isFound.isEmpty()) 
		{
			for (int i = 0; i < domains.size(); i++)
			{
				for (int j = 0; j < domains.get(i).size(); j++)
				{
					for (int k = 0; k < domains.get(i).get(j).size(); k++)
					{
						resultDomains.get(i).get(j).addAll(domains.get(i).get(j));
					}
				}
			}
			System.out.println("Found Solution");
			printDomains(resultDomains);
		}
	}
	
	private void addArcsToReconsider(Map<Pair<Integer, Integer>, ArrayList<Pair<Integer, Integer>>> arcMap, 
									 Set<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> toDoArcs,
									 Pair<Integer, Integer> nodeInWhichItsDomainIsSplitted)
	{
		ArrayList<Pair<Integer, Integer>> otherNodesThatAreConnectedToTheConstraintsThatNeedToBeReconsidered = 
				arcMap.get(nodeInWhichItsDomainIsSplitted);
		
		for (int i = 0; i < otherNodesThatAreConnectedToTheConstraintsThatNeedToBeReconsidered.size(); i++)
		{
			Pair<Integer, Integer> otherNodeToReconsider = otherNodesThatAreConnectedToTheConstraintsThatNeedToBeReconsidered.get(i);
			Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> arcToReconsider = 
					new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(otherNodeToReconsider, nodeInWhichItsDomainIsSplitted);
			
			toDoArcs.add(arcToReconsider);
		}
	}
	
	private List<List<List<Integer>>> getCopyOfDomains(List<List<List<Integer>>> domains)
	{
		List<List<List<Integer>>> copyOfDomain = new ArrayList<List<List<Integer>>>();
		for (int i = 0; i < domains.size(); i++)
		{
			copyOfDomain.add(new ArrayList<List<Integer>>());
			for (int j = 0; j < domains.get(i).size(); ++j)
			{
				copyOfDomain.get(i).add(new ArrayList<Integer>(domains.get(i).get(j)));
			}
		}
		
		return copyOfDomain;
	}
	
	private boolean doesOtherNodeContainAnyIntOtherThanThisInt(int thisInt, List<Integer> otherNodeDomain)
	{
		for (int i = 0; i < otherNodeDomain.size(); ++i)
		{
			int otherInt = otherNodeDomain.get(i);
			if (thisInt != otherInt)
				return true;
		}
		
		return false;
	}
	
	private void printDomains(List<List<List<Integer>>> domains)
	{
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
