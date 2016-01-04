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
	private double x;

	/**
	 * Y-coordinate of the object (top)
	 */
	private double y;

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
	private double hSpeed;

	/**
	 * The vertical speed of the player (negative -- up, positive -- down)
	 */
	private double vSpeed;

	/**
	 * The specific object's gravity (usually the universal gravity)
	 */
	private double gravity;

	/**
	 * Whether or not the object is on top of a surface
	 */
	private boolean onSurface;

	/**
	 * Whether or not the object exists
	 */
	private boolean exists;

	/**
	 * The type of object this is (subclass)
	 */
	private String type;

	/**
	 * Whether or not the MAP can see the object
	 */
	private boolean mapVisible;

	/**
	 * Whether or not the object will collide with tiles
	 */
	private boolean solid;

	/**
	 * Constructor for an object
	 * @param x
	 * @param y
	 * @param height
	 * @param width
	 * @param ID
	 */
	public ServerObject(double x, double y, int width, int height,
			double gravity, String image, String type)
	{
		solid = true;
		mapVisible = true;
		this.type = type;
		exists = true;
		onSurface = false;
		useImageDimensions = false;
		this.gravity = gravity;
		this.x = x;
		this.y = y;
		this.id = ServerEngine.useNextID();
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
	public boolean collidesWith(ServerObject other)
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
	 * Checks whether or not the other object is in range of this object
	 * @param other
	 * @param distance
	 * @return
	 */
	public boolean inRange(ServerObject other, double distance)
	{
		double distanceBetween = distanceBetween(other);
		
		if (distanceBetween <= distance)
		{
			return true;
		}
		return false;
	}
	

	/**
	 * Quickly finds whether or not the other object is vertically or
	 * horizontally within range without actually calculating the distance between
	 * @param other
	 * @param distance
	 * @return
	 */
	public boolean quickInRange(ServerObject other, double distance)
	{
		// Create a big hitbox and see if the other object touches it,
		// essentially
		if (other.getX()<=x+width+distance && other.getX()+other.getWidth() >= x - distance && other.getY() <= y + height + distance && other.getY() + other.getHeight() >= y - distance)
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

	/**
	 * Find the minimum distance between two objects
	 * @param other the other object
	 * @return the distance between this and the other object
	 */
	public double distanceBetween(ServerObject other)
	{
		// The specific sides of each object to calculate distance between (top,
		// bottom, left, right)
		double thisX = 0;
		double otherX = 0;
		double thisY = 0;
		double otherY = 0;

		if (x - (other.getX() + other.getWidth()) > 0)
		{
			otherX = other.getX() + other.getWidth();
		}
		else if (other.getX() - (x + width) > 0)
		{
			thisX = x + width;
		}

		if (y - (other.getY() + other.getHeight()) > 0)
		{
			otherY = other.getY() + other.getHeight();
		}
		else if (other.getY() - (y + height) > 0)
		{
			thisY = y + height;
		}

		return Math.sqrt((thisX - otherX)
				* (thisX - otherX) + (thisY - otherY)
				* (thisY - otherY));
	}

	public boolean exists()
	{
		return exists;
	}

	public void destroy()
	{
		exists = false;
	}

	public double getGravity()
	{
		return gravity;
	}

	public void makeExist()
	{
		exists = true;
	}

	public void setGravity(double gravity)
	{
		this.gravity = gravity;
	}

	public boolean isOnSurface()
	{
		return onSurface;
	}

	public void setOnSurface(boolean onSurface)
	{
		this.onSurface = onSurface;
	}

	public double getHSpeed()
	{
		return hSpeed;
	}

	public void setHSpeed(double hSpeed)
	{
		this.hSpeed = hSpeed;
	}

	public double getVSpeed()
	{
		return vSpeed;
	}

	public void setVSpeed(double vSpeed)
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

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
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

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public boolean isMapVisible()
	{
		return mapVisible;
	}

	public void setMapVisible(boolean mapVisible)
	{
		this.mapVisible = mapVisible;
	}

	public boolean isSolid()
	{
		return solid;
	}

	public void setSolid(boolean solid)
	{
		this.solid = solid;
	}

}
