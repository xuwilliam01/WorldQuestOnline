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

	public void draw(Graphics graphics, int playerX, int playerY)
	{
		try{
			for(Object object : objects)
			{
				if(object.getDesc().equals("TILE"))
				{
					Tile tile = (Tile)object;
					if(tile.getType() == 1)
						graphics.setColor(Color.BLACK);
					else if(tile.getType() == 0)
						graphics.setColor(Color.RED);
					graphics.fillRect(tile.getX()-playerX + Client.SCREEN_WIDTH/2 - Client.TILE_SIZE/2, tile.getY()-playerY + Client.SCREEN_HEIGHT/2 - Client.TILE_SIZE/2 , Client.TILE_SIZE, Client.TILE_SIZE);
				}
			}
		}
		catch(ConcurrentModificationException E)
		{

		}

	}
	public ArrayList<Object> getObjects() {
		return objects;
	}

	public boolean contains(Object object)
	{
		for(Object obj : objects)
			if(obj.getX() == object.getX() && obj.getY() == object.getY() && object.getDesc().equals(obj.getDesc()))
				return true;
		return false;
	}
}
