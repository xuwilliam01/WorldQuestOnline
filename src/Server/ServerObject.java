package Server;

import Imports.Images;

/**
 * A generic object existing somewhere in the world with a unique ID, x,y
 * coordinate, and height and width
 * @author William Xu & Alex Raita
 *
 */
public class ServerObject
{
	/**
	 * Unique identifier for the object
	 */
	private int id;

	/**
	 * Y-coordinate of the object (left)
	 */
	private int x;

	/**
	 * Y-coordinate of the object (top)
	 */
	private int y;

	/**
	 * Width of the object in pixels
	 */
	private int width;

	/**
	 * Whether or not to scale the hitbox to the image
	 */
	private boolean useImageDimensions;

	/**
	 * Height of the object in pixels
	 */
	private int height;

	/**
	 * The image name for the object (ex. pie.png)
	 */
	private String image;

	/**
	 * Constructor for an object
	 * @param x
	 * @param y
	 * @param height
	 * @param width
	 * @param ID
	 */
	public ServerObject(int x, int y, int width, int height, int ID,
			String image)
	{
		useImageDimensions = false;
		this.x = x;
		this.y = y;
		this.id = ID;
		this.image = image;
		this.width = width;
		this.height = height;
	}

	/**
	 * Constructor for an object, using the image dimensions as the height and
	 * width
	 * @param x
	 * @param y
	 * @param ID
	 * @param image
	 */
	public ServerObject(int x, int y, int ID, String image)
	{
		useImageDimensions = true;
		this.x = x;
		this.y = y;
		this.id = ID;
		this.image = image;
		width = Images.getGameImage(image).getWidth();
		height = Images.getGameImage(image).getHeight();
	}

	/**
	 * Get the base image for the object (ex. the base name for player right.png
	 * is player) This is useful for changing the direction/animation of the
	 * object but not the image itself
	 * @return the base image
	 */
	public String getBaseImage()
	{
		// The first word is always the base image
		String[] tokens = image.split(" ");

		return tokens[0];
	}

	/**
	 * Check for a collision between the two objects
	 * @param other
	 * @return whether or not the two objects are colliding
	 */
	public boolean checkCollision(ServerObject other)
	{
		if (x <= other.getX() + other.getWidth() && (x + width) >= other.getX()
				&& y <= other.getY() + other.getHeight()
				&& (y + height) >= other.getY())
		{
			return true;
		}
		return false;
	}

	/**
	 * Set the image for the object, and update the height and width if using
	 * image dimensions
	 * @param image
	 */
	public void setImage(String image)
	{
		this.image = image;
		if (useImageDimensions)
		{
			width = Images.getGameImage(image).getWidth();
			height = Images.getGameImage(image).getHeight();
		}
	}

	public int getID()
	{
		return id;
	}

	public void setID(int iD)
	{
		id = iD;
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

	public void setHeight(int height)
	{
		this.height = height;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public String getImage()
	{
		return image;
	}

}
