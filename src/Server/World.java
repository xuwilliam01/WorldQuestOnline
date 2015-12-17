package Server;

import java.io.*;
import java.util.StringTokenizer;

/**
 * Stores all the data about the world
 * @author William Xu and Alex Raita
 *
 */
public class World
{
	private char[][] grid;

	public World() throws IOException
	{
		newWorld();
	}
	
	public void newWorld() throws IOException
	{
		BufferedReader worldInput = new BufferedReader(new FileReader("World"));
		StringTokenizer tokenizer = new StringTokenizer(worldInput.readLine());
		
		grid = new char[Integer.parseInt(tokenizer.nextToken())][Integer.parseInt(tokenizer.nextToken())];
		String line;
		for(int row = 0; row < grid.length;row++)
		{
			line = worldInput.readLine();
			for(int col = 0; col < grid[row].length;col++)
				grid[row][col] = line.charAt(col);
		}
		
		worldInput.close();
	}
	
	public char[][] getGrid() {
		return grid;
	}

	public void setGrid(char[][] grid) {
		this.grid = grid;
	}
	
}
