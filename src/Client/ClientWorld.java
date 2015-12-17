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
			Iterator<Object> itr = objects.iterator();
			//while(itr.hasNext())
			for(Object object : objects)
			{
				//Object object = itr.next();
				//if(object == null)
					//break;
				if(object.getDesc().equals("TILE"))
				{
					Tile tile = (Tile)object;
					//If tile is not on the grid, remove it from the list
					int x = tile.getX()-playerX + Client.SCREEN_WIDTH/2 - Client.TILE_SIZE/2;
					int y = tile.getY()-playerY + Client.SCREEN_HEIGHT/2 - Client.TILE_SIZE/2;
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
	
	public boolean contains(Object object)
	{
		for(Object obj : objects)
			if(obj.getX() == object.getX() && obj.getY() == object.getY() && obj.getDesc().equals(object.getDesc()))
				return true;
		return false;
	}
}
