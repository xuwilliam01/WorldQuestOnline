package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class ClientWorld {

	private char [][] grid;
	private ArrayList<Object> objects;
	private int startX;
	private int startY;
	private int tileSize;

	/**
	 * Constructor for the client's side of the world
	 * @param rows the number of rows in the tile grid
	 * @param columns the number of columns in the tile grid
	 * @param grid the tile grid
	 */
	public ClientWorld(char[][]grid, int startX, int startY, int tileSize)
	{
		this.startX = startX;
		this.startY = startY;
		this.tileSize = tileSize;
		this.grid = grid;
	}
	
	public void add(Object object)
	{
		objects.add(object);
	}

	/**
	 * Draws the world
	 * @param graphics the graphics component to be drawn on
	 * @param playerX the position of the player
	 * @param playerY the position of the player
	 */
	public void draw(Graphics graphics, int playerX, int playerY)
	{
		//Go through each object in the world and draw it relative to the player's position
		try{
			for(Object object : objects)
			{
				int x = object.getX()-playerX + Client.SCREEN_WIDTH/2 - Client.TILE_SIZE/2;
				int y = object.getY()-playerY + Client.SCREEN_HEIGHT/2 - Client.TILE_SIZE/2;

				if(object.getDesc().equals("TILE"))
				{
					Tile tile = (Tile)object;

					// Figure out type of tile and place it
					if(tile.getType() == '1')
						graphics.setColor(Color.BLACK);
					else if(tile.getType() == '0')
						graphics.setColor(Color.RED);
					graphics.fillRect(x,y, Client.TILE_SIZE, Client.TILE_SIZE);

				}
				else if(object.getDesc().equals("PLAYER"))
				{
					OtherPlayer player = (OtherPlayer)object;
					graphics.setColor(player.getColour());
					graphics.fillRect(x,y, Client.TILE_SIZE, Client.TILE_SIZE);
				}
			}
		}
		//this might cause some problems in the future
		catch(ConcurrentModificationException E)
		{

		}

	}
	public ArrayList<Object> getObjects() {
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
	public boolean contains(Object object)
	{
		for(Object obj : objects)
			if(obj.compareTo(object) == 0)
				return true;
		return false;
	}

	/**
	 * Gets the actual object in the world
	 * @param object the object to be fetched
	 * @return the desired object
	 */
	public Object get(Object object)
	{
		for(Object obj : objects)
			if(obj.compareTo(object) == 0)
				return obj;
		return null;
	}
}
