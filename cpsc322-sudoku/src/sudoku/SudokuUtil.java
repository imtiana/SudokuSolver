package sudoku;

import java.io.*;

/**
 * An utility class for creating a Sudoku solver Includes input and output functions for the game board as well as text formatting of the Sudoku board
 * 
 * @author Mark Crowley (original)
 * 
 * @author Jacek Kisynski (updates)
 * 
 * @version 1.1 February 2009
 */
public final class SudokuUtil {

	/**
	 * Don't let anyone instantiate this class.
	 */
	private SudokuUtil() {
		// Empty
	}

	/**
	 * Read a Sudoku board from the file given in boardInFile as a comma delimited file with new lines to indicate rows. The Board must be bdSize x
	 * bdSize large Any data after bdSize lines will not be read
	 * 
	 * @param boardInFile The full file name and location for the Sudoku board;
	 * @param bdSize The number of rows and columns in the Sudoku board.
	 * 
	 * @return board The int[][] array representing the board in boardInFile.
	 * 
	 * @throws IOException
	 */
	public static int[][] readInBoard(String boardInFile, int bdSize) throws IOException {
		int[][] board = new int[bdSize][bdSize];
		BufferedReader in = new BufferedReader(new FileReader(boardInFile));
		String str;
		int row = 0;
		String[] rowStr;
		while ((str = in.readLine()) != null && row < bdSize) {
			rowStr = str.split(",\\s*");
			for (int i = 0; i < rowStr.length; i++) {
				board[row][i] = Integer.parseInt(rowStr[i]);
			}
			row++;
		}
		in.close();
		return board;
	}

	/**
	 * Read a Sudoku board from the file given in boardInFile as a a string of bdSize^2 characters: digits from 1 to 9 and dots. Dots stand for empty
	 * cells. This format is widely used on the Internet to share Sudokus.
	 * 
	 * @param boardInFile The full file name and location for the Sudoku board;
	 * @param bdSize The number of rows and columns in the Sudoku board.
	 * 
	 * @return board The int[][] array representing the board in boardInFile.
	 * 
	 * @throws IOException
	 */
	public static int[][] readInBoardDots(String boardInFile, int bdSize) throws IOException {
		int[][] board = new int[bdSize][bdSize];
		BufferedReader in = new BufferedReader(new FileReader(boardInFile));
		String str = in.readLine();
		if (str == null)
			throw new IOException("Invalid input file.");
		str = str.trim(); // Get rid of leading and trailing white spaces
		if (str.length() != bdSize * bdSize)
			throw new IOException("Invalid input file.");
		int index = 0;
		char[] characters = str.toCharArray();
		for (int row = 0; row < bdSize; row++)
			for (int column = 0; column < bdSize; column++) {
				if (characters[index] == '.')
					board[row][column] = 0;
				else {
					try {
						board[row][column] = Integer.valueOf(characters[index]).intValue() - 48;
					} catch (NumberFormatException e) {
						throw new IOException(e.getMessage());
					}
					if ((board[row][column] < 1) || (board[row][column] > 9))
						throw new IOException("Invalid input file");
				}
				index++;
			}
		in.close();
		return board;
	}

	/**
	 * Output the Sudoku board to a text file in comma delimited format.
	 * 
	 * @param newBoard a 2d int array representing the Sudoku board to write out;
	 * @param boardOutFile the full file name and location for the board to be written to.
	 * 
	 * @throws IOException
	 */
	public static void writeOutBoard(int[][] newBoard, String boardOutFile) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(boardOutFile));
		out.write(formatBoardComma(newBoard));
		out.close();

		System.out.println("new board saved");
	}

	/**
	 * Produces a nicely formatted in text for the Sudoku board.
	 * 
	 * @param board the 2d int array representing the Sudoku board.
	 * 
	 * @return a formatted text view of the Sudoku board.
	 */
	public static String formatBoard(int[][] board) {
		String r = "";
		String hline = "";
		for (int s = 0; s < 24; s++) {
			hline += "-";
		}
		for (int i = 0; i < board.length; i++) {
			if (i > 0 && i % 3 == 0)
				r += hline + "\n";
			for (int j = 0; j < board[i].length; j++) {
				if (j > 0 && j % 3 == 0)
					r += " | ";
				r += board[i][j] + " ";
			}
			r += "\n";
		}
		return r;
	}

	/**
	 * Produces a formatted text out of the Sudoku board with numbers separated by commas only. This is the format used to read in and write out
	 * Sudoku boards.
	 * 
	 * @param board the 2d int array representing the Sudoku board.
	 * 
	 * @return a comma delimited text String of the Sudoku board.
	 */
	protected static String formatBoardComma(int[][] board) {
		String r = "";
		int i = 0, j = 0;
		for (i = 0; i < board.length; i++) {
			for (j = 0; j < board[i].length - 1; j++) {
				r += board[i][j] + ",";
			}
			r += board[i][j] + "\n";
		}
		return r;
	}
}
