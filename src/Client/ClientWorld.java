package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

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
		objectIDs = new boolean [100000];
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
	 * Draws the world
	 * @param graphics the graphics component to be drawn on
	 * @param playerX the position of the player
	 * @param playerY the position of the player
	 */
	public void draw(Graphics graphics, int playerX, int playerY,
			int playerWidth, int playerHeight)
	{
		// Go through each object in the world and draw it relative to the
		// player's position
		try
		{
			for (ClientObject object : objects)
			{
				int x = object.getX() - playerX + Client.SCREEN_WIDTH / 2
						+ playerWidth / 2;
				int y = object.getY() - playerY + Client.SCREEN_HEIGHT / 2
						+ playerHeight / 2;

				graphics.setColor(Color.blue);
				//graphics.fillRect(playerX, playerY, 20,20);
				graphics.drawImage(object.getImage(), playerX, playerY, null);
			}
		}
		// this might cause some problems in the future
		catch (ConcurrentModificationException E)
		{

		}

	}

	public ArrayList<ClientObject> getObjects()
	{
		return objects;
	}

	public void clear()
	{
		objects.clear();
	}

	/**
	 * Checks if the world contains a given object
	 * @param object the object to be checked
	 * @return true if the object is found in the world, false if not
	 */
	public boolean contains(ClientObject object)
	{
		if (objectIDs[object.getID()])
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
