package Client;

import java.awt.Image;

import Imports.Images;

/**
 * A background object on the client side that doesn't affect the game
 * @author William Xu & Alex Raita
 */
public class ClientBackground
{

	/**
	 * The x coordinate of the object (left)
	 */
	private double x;

	/**
	 * The y coordinate of the object (top)
	 */
	private double y;
	
	/**
	 * The speed the object moves horizontally
	 */
	private double hSpeed;
	
	/**
	 * The speed the object moves vertically
	 */
	private double vSpeed;
	
	/**
	 * The height of the object image
	 */
	private int height;
	
	/**
	 * The width of the object image
	 */
	private int width;

	/**
	 * The image of the object to draw
	 */
	private Image image;

	
	public ClientBackground(double x, double y, double hSpeed, double vSpeed, String imageName)
	{
		this.x = x;
		this.y = y;
		this.hSpeed = hSpeed;
		this.vSpeed = vSpeed;
		this.image = Images.getImage(imageName);
		this.width = image.getWidth(null);
		this.height = image.getHeight(null);
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


	public double gethSpeed()
	{
		return hSpeed;
	}


	public void sethSpeed(double hSpeed)
	{
		this.hSpeed = hSpeed;
	}


	public double getvSpeed()
	{
		return vSpeed;
	}


	public void setvSpeed(double vSpeed)
	{
		this.vSpeed = vSpeed;
	}


	public Image getImage()
	{
		return image;
	}


	public void setImage(Image image)
	{
		this.image = image;
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
	
}
