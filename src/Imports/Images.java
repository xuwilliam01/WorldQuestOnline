package Imports;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;

import Server.ServerWorld;

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
	
	/**
	 * Array list of the game images
	 */
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
			images.add(new GameImage("PLAYER_RIGHT.png",34,64));
			images.add(new GameImage("PLAYER_LEFT.png",34,64));
			images.add(new GameImage("BRICK.png",ServerWorld.TILE_SIZE,ServerWorld.TILE_SIZE));
			images.add(new GameImage("GRASS.png",ServerWorld.TILE_SIZE,ServerWorld.TILE_SIZE));
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
