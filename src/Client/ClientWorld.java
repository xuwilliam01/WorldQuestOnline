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
	public void draw(Graphics graphics, int playerX, int playerY)
	{
		// Go through each object in the world and draw it relative to the
		// player's position
		try
		{
			for (ClientObject object : objects)
			{
				int x = object.getX() - playerX + Client.SCREEN_WIDTH / 2
						- Client.TILE_SIZE / 2;
				int y = object.getY() - playerY + Client.SCREEN_HEIGHT / 2
						- Client.TILE_SIZE / 2;

				if (object.getDesc().equals("PLAYER"))
				{
					OtherPlayer player = (OtherPlayer) object;
					graphics.setColor(player.getColour());
					graphics.fillRect(x, y, Client.TILE_SIZE, Client.TILE_SIZE);
				}
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
		objectIDs[id]=true;
	}
	
	/**
	 * Set an ID's usage to false
	 * @param id
	 */
	public void removeID(int id)
	{
		objectIDs[id]=false;
	}

	/**
	 * Gets the actual object in the world
	 * @param object the object to be fetched
	 * @return the desired object
	 */
	public ClientObject get(ClientObject object)
	{
		for (ClientObject obj : objects)
			if (obj.compareTo(object) == 0)
				return obj;
		return null;
	}
}
