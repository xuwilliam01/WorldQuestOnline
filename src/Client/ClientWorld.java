package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class ClientWorld {

	private ArrayList<Object> objects = new ArrayList<Object>();

	public void add(Object object)
	{
		objects.add(object);
	}

	public void draw(Graphics graphics)
	{
		synchronized(objects){
			for(Object object : objects)
			{
				if(object.getDesc().equals("TILE"))
				{
					Tile tile = (Tile)object;
					if(tile.getType() == 1)
						graphics.setColor(Color.BLACK);
					else if(tile.getType() == 0)
						graphics.setColor(Color.RED);
					graphics.fillRect(tile.getX(), tile.getY(), Client.TILE_SIZE, Client.TILE_SIZE);
				}
			}
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
