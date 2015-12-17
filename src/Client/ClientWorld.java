package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class ClientWorld {

	private ArrayList<Object> objects = new ArrayList<Object>();

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
		//Go through each object in the wrold and draw it relative to the player's position
		try{
			Iterator<Object> itr = objects.iterator();
			//while(itr.hasNext())
			for(Object object : objects)
			{
				//Object object = itr.next();
				//if(object == null)
					//break;
				int x = object.getX()-playerX + Client.SCREEN_WIDTH/2 - Client.TILE_SIZE/2;
				int y = object.getY()-playerY + Client.SCREEN_HEIGHT/2 - Client.TILE_SIZE/2;
				
				if(object.getDesc().equals("TILE"))
				{
					Tile tile = (Tile)object;
					//If tile is not on the grid, remove it from the list
					
//					if(x-Client.TILE_SIZE < 0 || x > Client.SCREEN_WIDTH)
//					{
//						itr.remove();
//					}
//					else if(y-Client.TILE_SIZE < 0 || y > Client.SCREEN_HEIGHT)
//					{
//						itr.remove();
//					}
//					else
					{
						//Figure out tyep of tile and place it
						if(tile.getType() == 1)
							graphics.setColor(Color.BLACK);
						else if(tile.getType() == 0)
							graphics.setColor(Color.RED);
						graphics.fillRect(x,y, Client.TILE_SIZE, Client.TILE_SIZE);
					}
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
