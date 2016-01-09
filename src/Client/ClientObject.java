package Client;

import java.awt.Image;

import Imports.Images;

/**
 * A class that acts as a blueprint for all objects in the game
 * @author Alex Raita & William Xu
 */
public class ClientObject implements Comparable<ClientObject>
{

	/**
	 * The team of the object
	 * Objects with neutral teams won't have number displayed above them
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
	 * Constructor
	 */
	public ClientObject(int id, int x, int y, String image, int team)
	{
		this.team = team;
		this.x = x;
		this.y = y;
		this.id = id;
		this.imageName = image;
		this.image = Images.getImage(image);
		height = this.image.getHeight(null);
		width = this.image.getWidth(null);
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
	 * @param image
	 */
	public void setImage(String image)
	{
		if (!image.equals(imageName))
		{
			imageName = image;
			this.image = Images.getImage(image);
			height = this.image.getHeight(null);
			width = this.image.getWidth(null);
		}
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

}
