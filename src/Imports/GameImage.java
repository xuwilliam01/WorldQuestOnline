package Imports;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Object that stores images with strings to identify them
 * @author William Xu && Alex Raita
 *
 */
public class GameImage
{
	private String name;
	private Image image;
	
	/**
	 * The width of the image in pixels
	 */
	private int width;
	
	/**
	 * The height of the image in pixels
	 */
	private int height;
	
	
	/**
	 * Constructor for a game image
	 * @param name
	 */
	public GameImage(String name)
	{
		this.name = name;
		try
		{
			image = ImageIO.read(new File(name));
		}
		catch (IOException e)
		{
			System.out.println("Error loading image: " + name);
			e.printStackTrace();
		}
		width = image.getWidth(null);
		height = image.getHeight(null);
	}
	
	/**
	 * Constructor for a game image with scaling
	 * @param name
	 */
	public GameImage(String name,int width, int height)
	{
		this.name = name;
		try
		{
			image = ImageIO.read(new File(name)).getScaledInstance(width, height, 0);
		}
		catch (IOException e)
		{
			System.out.println("Error loading image: " + name);
			e.printStackTrace();
		}
		this.width = width;
		this.height = height;
	}

	public String getName()
	{
		return name;
	}

	public Image getImage()
	{
		return image;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}
}
