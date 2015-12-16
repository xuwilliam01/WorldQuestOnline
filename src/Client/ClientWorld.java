package Client;

import java.awt.Graphics;

public class ClientWorld {

	private int[][] visibleGrid;
	
	public void update(int[][] newGrid)
	{
		visibleGrid = newGrid;
	}

	public void draw(Graphics graphics, int xStart, int yStart)
	{
		int x = xStart;
		int y = yStart;
		for(int row = 0; row < visibleGrid.length;row++)
		{
			for(int col = 0; col < visibleGrid[row].length;col++)
			{
				graphics.fillRect(x,y,x+Client.TILE_SIZE*(col+1),y+Client.TILE_SIZE*(row+1));
			}
		}
	}
	public int[][] getVisibleGrid() {
		return visibleGrid;
	}

	
}
