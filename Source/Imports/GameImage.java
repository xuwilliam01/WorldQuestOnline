package Imports;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Menu.MainMenu;

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
		
		try
		{
			bufferedImage = ImageIO.read(new File("Images//"+name));
		}
		catch (IOException e)
		{
			System.out.println("Error loading image: " + name);
			MainMenu.imageLoadFailed=true;
			e.printStackTrace();
		}
		this.name = name.substring(0,name.indexOf('.'));
		image = bufferedImage;
		width = image.getWidth(null);
		height = image.getHeight(null);
		color = new Color(bufferedImage.getRGB(bufferedImage.getWidth()/2, bufferedImage.getHeight()/2));
	}

	/**
	 * Constructor for a game image with scaling
	 * @param name
	 */
	public GameImage(String name, int width, int height)
	{
		
		try
		{
			bufferedImage = ImageIO.read(new File("Images//"+name));
		}
		catch (IOException e)
		{
			System.out.println("Error loading image: " + name);
			MainMenu.imageLoadFailed=true;
			e.printStackTrace();
		}
		this.name = name.substring(0,name.indexOf('.'));
		if (bufferedImage.getWidth()!= width || bufferedImage.getHeight()!= height)
		{
		image = bufferedImage.getScaledInstance(width,
				height, 0);
		}
		else
		{
			image = bufferedImage;
		}
		this.width = width;
		this.height = height;
		if (bufferedImage.getWidth(null) >= 3)
		{
		color = new Color(bufferedImage.getRGB(bufferedImage.getWidth() / 2, bufferedImage.getHeight()/ 2));
		}
		else
		{
			color = Color.white;
		}
	}

	/**
	 * Constructor for a game image with the image and a name
	 * @param name
	 */
	public GameImage(String name, BufferedImage image)
	{
		this.name = name.substring(0,name.indexOf('.'));
		this.image = image;
		width = image.getWidth(null);
		height = image.getHeight(null);
		color = new Color(image.getRGB(width / 2, height / 2));
	}

	/**
	 * Constructor for a game image with scaling with a predetermined image
	 * @param name
	 */
	public GameImage(String name, BufferedImage image, int width, int height)
	{
		this.name = name.substring(0,name.indexOf('.'));
		if (image.getWidth(null)!= width || image.getHeight(null)!= height)
		{
		this.image = image.getScaledInstance(width,
				height, 0);
		}
		else
		{
			this.image = image;
		}
		this.width = width;
		this.height = height;
		color = new Color(image.getRGB(image.getWidth() / 2,
				image.getHeight() / 2));
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

	public void setName(String name) {
		this.name = name;
	}
	
}
