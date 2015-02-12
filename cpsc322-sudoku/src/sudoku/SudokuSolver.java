package sudoku;

import java.util.ArrayList;

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
		int[][][] domains = new int[9][9][];
		ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> toDoArcs = 
				new ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>();
		
		for (int i = 0; i < board.length; i++)
		{
			for (int j = 0; j < board[i].length; j++)
			{
				domains[i][j] = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
				
				
			}
		}
		
		return board;
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

	    public A getFirst()
	    {
	        return first;
	    }

	    public void setFirst(A first)
	    {
	        this.first = first;
	    }

	    public B getSecond()
	    {
	        return second;
	    }

	    public void setSecond(B second)
	    {
	        this.second = second;
	    }
	}
}
