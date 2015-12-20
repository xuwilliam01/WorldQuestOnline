package Imports;

import java.awt.Image;
import java.util.ArrayList;

/**
 * Class that imports and stores all possible images in the game
 * @author William Xu & Alex Raita
 *
 */
public class Images
{
	/**
	 * The format of the images (.jpg, .png, .gif, etc.)
	 */
	public static final String IMAGE_FORMAT = ".png";
	public static ArrayList<GameImage> images;

	/**
	 * Import the images from the folder
	 */
	public static void importImages()
	{
		// Only import if the images haven't been imported already
		if (images == null)
		{
			images = new ArrayList<GameImage>();
			images.add(new GameImage("player_right.png"));
			images.add(new GameImage("player_left.png"));
		}
	}

	/**
	 * Get a specific image from the list using the name of the image
	 * @param name the name of the image
	 * @return the image (inside a game image) from the list
	 */
	public static Image getImage(String name)
	{
		for (GameImage image : images)
		{
			if (image.getName().equals(name))
			{
				return image.getImage();
			}
		}
		return null;
	}
	
	/**
	 * Get a specific game image from the list using the name of the image
	 * @param name the name of the image
	 * @return the game image from the list
	 */
	public static GameImage getGameImage(String name)
	{
		for (GameImage image : images)
		{
			if (image.getName().equals(name))
			{
				return image;
			}
		}
		return null;
	}
}
