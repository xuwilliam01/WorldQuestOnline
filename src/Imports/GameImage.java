package Imports;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Object that stores images with strings to identify them
 * @author William Xu && Alex Raita
 *
 */
public class GameImage implements Comparable<GameImage>
{
	private String name;
	private BufferedImage bufferedImage;
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
	 * The color in the middle of the image
	 */
	private Color color;

	/**
	 * Create a fake GameImage to compare in the binary tree
	 * @param name
	 * @param fake
	 */
	public GameImage(String name, boolean fake)
	{
		this.name = name;
	}

	/**
	 * Constructor for a game image with just the name (to load from file)
	 * @param name
	 */
	public GameImage(String name)
	{
		this.name = name;
		try
		{
			bufferedImage = ImageIO.read(new File(name));
		}
		catch (IOException e)
		{
			System.out.println("Error loading image: " + name);
			e.printStackTrace();
		}
		
		image = bufferedImage;
		width = image.getWidth(null);
		height = image.getHeight(null);
		color = new Color(bufferedImage.getRGB(bufferedImage.getWidth()/3, bufferedImage.getHeight()/3));
	}

	/**
	 * Constructor for a game image with scaling
	 * @param name
	 */
	public GameImage(String name, int width, int height)
	{
		this.name = name;
		try
		{
			bufferedImage = ImageIO.read(new File(name));
		}
		catch (IOException e)
		{
			System.out.println("Error loading image: " + name);
			e.printStackTrace();
		}

		image = bufferedImage.getScaledInstance(width,
				height, 0);
		this.width = width;
		this.height = height;
		color = new Color(bufferedImage.getRGB(width / 3, height / 3));
	}

	/**
	 * Constructor for a game image with the image and a name
	 * @param name
	 */
	public GameImage(String name, BufferedImage image)
	{
		this.name = name;
		this.image = image;
		width = image.getWidth(null);
		height = image.getHeight(null);
		color = new Color(image.getRGB(width / 3, height / 3));
	}

	/**
	 * Constructor for a game image with scaling with a predetermined image
	 * @param name
	 */
	public GameImage(String name, BufferedImage image, int width, int height)
	{
		this.name = name;
		this.image = image.getScaledInstance(width,
				height, 0);
		this.width = width;
		this.height = height;
		color = new Color(image.getRGB(image.getWidth() / 3,
				image.getHeight() / 3));
	}

	
	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////
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
	@Override
	public int compareTo(GameImage other)
	{
		return name.compareTo(other.getName());
	}
	public Color getCentreColor()
	{
		return color;
	}
	public void setCentreColor(Color centreColor)
	{
		this.color = centreColor;
	}
}
