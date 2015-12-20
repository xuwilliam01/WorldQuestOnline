package Client;
import java.awt.Image;

import Imports.Images;

/**
 * A class that acts as a blueprint for all objects in the game
 * @author Alex Raita & William Xu
 */
public class ClientObject {

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
	public ClientObject(int x, int y, int id, String image)
	{
		this.x = x;
		this.y = y;
		this.id=id;
		this.image = Images.getImage(image);
		height = this.image.getHeight(null);
		width = this.image.getWidth(null);
	}
	
	public Image getImage()
	{
		return image;
	}

	public void setImage(Image image)
	{
		this.image = image;
		height = this.image.getHeight(null);
		width = this.image.getWidth(null);
	}

	public int getID()
	{
		return id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
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


}
