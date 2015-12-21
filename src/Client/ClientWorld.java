package Client;
import java.awt.Graphics;
import java.util.ArrayList;

import Imports.Images;

public class ClientWorld
{

	/**
	 * The grid of tiles
	 */
	private char[][] grid;

	/**
	 * Long boolean array where the index of an object is its ID, while true or
	 * false represents whether it already exists in the array list of objects
	 */
	private boolean[] objectIDs;

	/**
	 * List of objects stored in the client. Instead of calling a .contains when
	 * checking whether an object exists in the list, just check the index in
	 * the objectIDs boolean array
	 */
	private ArrayList<ClientObject> objects;

	/**
	 * The side length of one square tile in pixels
	 */
	private int tileSize;

	/**
	 * Constructor for the client's side of the world
	 * @param rows the number of rows in the tile grid
	 * @param columns the number of columns in the tile grid
	 * @param grid the tile grid
	 */
	public ClientWorld(char[][] grid, int tileSize)
	{
		this.tileSize = tileSize;
		this.grid = grid;
		objects = new ArrayList<ClientObject>();
		objectIDs = new boolean[100000];
	}

	/**
	 * Add an object (not a tile) to the client
	 * @param object the object to add
	 */
	public void add(ClientObject object)
	{
		objects.add(object);
		addID(object.getID());
	}
	
	/**
	 * Remove an object from the client's side
	 * @param object the object to remove
	 */
	public void remove(int id)
	{
		int removeIndex=-1;
		int currentIndex = 0;
		for (ClientObject object:objects)
		{
			if (object.getID()==id)
			{
				removeIndex = currentIndex;
				break;
			}
			currentIndex++;
		}
		
		if (removeIndex!=-1)
		{
			objects.remove(removeIndex);
			removeID(id);
		}
	}

	/**
	 * Draws the world
	 * @param graphics the graphics component to be drawn on
	 * @param playerX the position of the player
	 * @param playerY the position of the player
	 */
	public void draw(Graphics graphics, int playerX, int playerY,
			int playerWidth, int playerHeight)
	{
		//Center of the screen
		int centreX = Client.SCREEN_WIDTH/2 - playerWidth/2;
		int centreY = Client.SCREEN_HEIGHT/2 - playerHeight/2;
		
		// Draw tiles (draw based on player's position later)
		int startRow = (int)((playerY - Client.SCREEN_HEIGHT/2-5)/tileSize);
		if (startRow < 0)
		{
			startRow = 0;
		}
		int endRow = (int)((Client.SCREEN_HEIGHT/2+playerY+5)/tileSize);
		if (endRow >= grid.length)
		{
			endRow = grid.length-1;
		}
		int startColumn = (int)((playerX - Client.SCREEN_WIDTH/2-5)/tileSize);
		if (startColumn < 0)
		{
			startColumn = 0;
		}
		int endColumn = (int)((Client.SCREEN_WIDTH/2+playerX+5)/tileSize);
		if (endColumn >= grid.length)
		{
			endColumn= grid[0].length-1;
		}
		for (int row = startRow; row <= endRow; row++)
		{
			for (int column = startColumn; column <= endColumn; column++)
			{
				if (grid[row][column]=='0')
				{
					graphics.drawImage(Images.getImage("GRASS.png"), centreX + column* tileSize - playerX, centreY + row*tileSize - playerY,
							null);
				}
				else if (grid[row][column]=='1')
				{
					graphics.drawImage(Images.getImage("BRICK.png"),  centreX + column* tileSize - playerX, centreY + row*tileSize - playerY,
							null);
				}
			}
		}
		
		// Go through each object in the world and draw it relative to the
		// player's position
		for (ClientObject object : objects)
		{			
			graphics.drawImage(object.getImage(), centreX + object.getX() - playerX, centreY + object.getY() - playerY,
					null);
		}
	}

	/**
	 * Get a specific object from the list
	 * @return the desired object
	 */
	public ClientObject get(int id)
	{
		for (ClientObject object : objects)
		{
			if (object.getID() == id)
			{
				return object;
			}
		}
		return null;
	}

	public void clear()
	{
		objects.clear();
	}

	/**
	 * Checks if the world contains a given object (id)
	 * @param object the object to be checked
	 * @return true if the object is found in the world, false if not
	 */
	public boolean contains(int id)
	{
		if (objectIDs[id])
		{
			return true;
		}
		return false;
	}

	/**
	 * Set an ID's usage to true
	 * @param id
	 */
	public void addID(int id)
	{
		objectIDs[id] = true;
	}

	/**
	 * Set an ID's usage to false
	 * @param id
	 */
	public void removeID(int id)
	{
		objectIDs[id] = false;
	}

	public char[][] getGrid()
	{
		return grid;
	}

	public void setGrid(char[][] grid)
	{
		this.grid = grid;
	}

	public boolean[] getObjectIDs()
	{
		return objectIDs;
	}

	public void setObjectIDs(boolean[] objectIDs)
	{
		this.objectIDs = objectIDs;
	}

	public int getTileSize()
	{
		return tileSize;
	}

	public void setTileSize(int tileSize)
	{
		this.tileSize = tileSize;
	}

	public void setObjects(ArrayList<ClientObject> objects)
	{
		this.objects = objects;
	}

}
