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
	 * The horizontal speed of the player (negative -- left, positive -- right)
	 */
	private int hSpeed;

	/**
	 * The vertical speed of the player (negative -- up, positive -- down)
	 */
	private int vSpeed;
	
	/**
	 * Whether or not the object is on top of a surface
	 */
	private boolean onSurface;

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
		onSurface = false;
		useImageDimensions = false;
		this.x = x;
		this.y = y;
		this.id = ID;
		this.image = image;
		if (width == -1)
		{
			this.width = Images.getGameImage(image).getWidth();
			this.height = Images.getGameImage(image).getHeight();
			useImageDimensions = true;
		}
		else
		{
			this.width = width;
			this.height = height;
			useImageDimensions = false;
		}
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
		String[] tokens = image.split("_");
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
	
	public boolean isOnSurface()
	{
		return onSurface;
	}

	public void setOnSurface(boolean onSurface)
	{
		this.onSurface = onSurface;
	}
	
	public int getHSpeed()
	{
		return hSpeed;
	}

	public void setHSpeed(int hSpeed)
	{
		this.hSpeed = hSpeed;
	}

	public int getVSpeed()
	{
		return vSpeed;
	}

	public void setVSpeed(int vSpeed)
	{
		this.vSpeed = vSpeed;
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
