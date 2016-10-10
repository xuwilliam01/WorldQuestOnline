package Client;

import java.awt.Image;

import Imports.GameImage;
import Imports.Images;
import Server.ServerWorld;

/**
 * A class that acts as a blueprint for all objects in the game
 * 
 * @author Alex Raita & William Xu
 */
public class ClientObject implements Comparable<ClientObject>
{

	/**
	 * The team of the object Objects with neutral teams won't have number
	 * displayed above them
	 */
	private int team;

	/**
	 * The x coordinate of the object (left)
	 */
	private int x;

	/**
	 * The y coordinate of the object (top)
	 */
	private int y;

	/**
	 * The image of the object to draw
	 */
	private Image image;

	/**
	 * The name of the image of the object to draw
	 */
	private String imageName;

	/**
	 * The height of the image
	 */
	private int height;

	/**
	 * The width of the image
	 */
	private int width;

	/**
	 * The unique ID of the object
	 */
	private int id;

	/**
	 * The type of object, related directly to the ServerWorld
	 */
	private String type;

	/**
	 * The hint that will be displayed when a player is over this object
	 */
	private String hint = "";

	private String name = "";

	/**
	 * Constructor
	 */
	public ClientObject(int id, int x, int y, String image, int team,
			String type)
	{
		this.team = team;
		this.x = x;
		this.y = y;
		this.id = id;
		this.imageName = image;
		this.type = type;

		try
		{
			// All objects that are just text have lower case names
			// Don't import an actual image if it is just text
			if (type.charAt(0)!= ServerWorld.TEXT_TYPE)
			{
				GameImage gameImage = Images.getGameImage(image);
				this.image = gameImage.getImage();
				height = gameImage.getHeight();
				width = gameImage.getWidth();
			}
			else
			{
				height = 0;
				width = 0;
			}
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			System.out.println("Image: " + image);
		}

		// Select a hint for this object, if it has one
		switch (type)
		{
		case ServerWorld.VENDOR_TYPE:
			hint = "Press 'E' to open/close the shop";
			break;
		case ServerWorld.CHEST_TYPE:
			hint = "Destroy the chest and it will drop items";
			break;
		case ServerWorld.CASTLE_TYPE:
			hint = "Drop money on the castle to upgrade your goblins";
			break;
		}
	}

	/**
	 * Constructor
	 */
	public ClientObject(int id, int x, int y, String image, int team,
			String type, String name)
	{
		this.team = team;
		this.x = x;
		this.y = y;
		this.id = id;
		this.imageName = image;
		this.type = type;
		this.name = name;

		// All objects that are just text have lower case names
		// Don't import an actual image if it is just text
		if (image.charAt(0) != 't')
		{
			GameImage gameImage = Images.getGameImage(image);
			this.image = gameImage.getImage();
			height = gameImage.getHeight();
			width = gameImage.getWidth();
		}
		else
		{
			height = 0;
			width = 0;
		}

		// Select a hint for this object, if it has one
		switch (type)
		{
		case ServerWorld.VENDOR_TYPE:
			hint = "Press 'E' to open/close the shop";
			break;
		case ServerWorld.CHEST_TYPE:
			hint = "Destroy the chest and it will drop items";
			break;
		case ServerWorld.CASTLE_TYPE:
			hint = "Drop money on the castle to upgrade your goblins";
			break;
		}
	}

	public int getTeam()
	{
		return team;
	}

	public void setTeam(int team)
	{
		this.team = team;
	}

	public Image getImage()
	{
		return image;
	}

	/**
	 * Change the image of the object
	 * 
	 * @param image
	 */
	public void setImage(String image)
	{
		if (!image.equals(imageName))
		{
			imageName = image;

			// If the image of the object is text, don't import an image
			if (image.charAt(0) != 't')
			{
				GameImage gameImage = Images.getGameImage(image);
				try{
				this.image = gameImage.getImage();
				}
				catch(NullPointerException e)
				{
					e.printStackTrace();
					System.out.println(image);
				}
				height = gameImage.getHeight();
				width = gameImage.getWidth();
			}
		}
	}

	public boolean collidesWith(ClientObject other)
	{
		if (x <= other.getX() + other.getWidth() && (x + width) >= other.getX()
				&& y <= other.getY() + other.getHeight()
				&& (y + height) >= other.getY())
		{
			return true;
		}
		return false;
	}
	
	public void update()
	{
		
	}

	public int getID()
	{
		return id;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}

	@Override
	public int compareTo(ClientObject other)
	{
		return id - other.getID();
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public String getImageName()
	{
		return imageName;
	}

	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}

	public String getHint()
	{
		return hint;
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name) {
		this.name = name.replace('_', ' ').replace('{', ' ').trim();
	}

	
}
