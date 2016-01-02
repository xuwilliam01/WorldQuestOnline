package Imports;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Client.ClientInventory;
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
	 * The size the image will be in the inventory
	 */
	public static final int INVENTORY_IMAGE_SIDELENGTH = ClientInventory.INVENTORY_WIDTH/(ClientInventory.WIDTH+1)-20;


	/**
	 * Array list of the game images
	 */
	public static BinaryTree<GameImage> images = null;

	/**
	 * Import the images from the folder
	 * @throws IOException
	 */
	public static void importImages()
	{
		// Only import if the images haven't been imported already
		if (images == null)
		{
			images = new BinaryTree<GameImage>();

			// Import sprite sheets to create images with
			try
			{
				BufferedImage sheet = ImageIO.read(new File(
						"SLIME_SHEET.png"));
				for (int no = 0; no < 9; no++)
				{
					images.add(new GameImage("SLIME_" + no + IMAGE_FORMAT,
							sheet.getSubimage(no*19, 0, 19, 17),38,34));
				}

				sheet = ImageIO.read(new File(
						"EXPLOSION_SHEET.png"));
				for (int no = 0; no < 7; no++)
				{
					images.add(new GameImage("EXPLOSION_" + no + IMAGE_FORMAT,
							sheet.getSubimage(no*32, 0, 32, 32)));
				}
			}
			catch (IOException e)
			{
				System.out.println("Error loading sprite sheets");
				e.printStackTrace();
			}

			// Add the rest of the images normally
			images.add(new GameImage("KNIGHT_RIGHT.png"));
			images.add(new GameImage("KNIGHT_LEFT.png"));

			images.add(new GameImage("CYCLOPS_RIGHT.png", 120, 122));
			images.add(new GameImage("CYCLOPS_LEFT.png", 120, 122));

			images.add(new GameImage("GIRL_RIGHT.png"));
			images.add(new GameImage("GIRL_LEFT.png"));


			images.add(new GameImage("PLAYERGHOST_RIGHT.png"));
			images.add(new GameImage("PLAYERGHOST_LEFT.png"));

			images.add(new GameImage("BRICK.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("GRASS.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("WATER.png",ServerWorld.TILE_SIZE,ServerWorld.TILE_SIZE));
			
			images.add(new GameImage("ENEMY.png", 60, 90));
			images.add(new GameImage("BULLET.png"));

			images.add(new GameImage("HP_POTION.png",15,15));
			images.add(new GameImage("HP_POTION_ICON.png",INVENTORY_IMAGE_SIDELENGTH,INVENTORY_IMAGE_SIDELENGTH));
			
			images.add(new GameImage("SWORD.png",70,30));
			images.add(new GameImage("SWORD_ICON.png",INVENTORY_IMAGE_SIDELENGTH,INVENTORY_IMAGE_SIDELENGTH));
		}
	}

	/**
	 * Get a specific image from the list using the name of the image
	 * @param name the name of the image
	 * @return the image (inside a game image) from the list
	 */
	public static Image getImage(String name)
	{
		return images.get(new GameImage(name,true)).getImage();
	}

	/**
	 * Get a specific game image from the list using the name of the image
	 * @param name the name of the image
	 * @return the game image from the list
	 */
	public static GameImage getGameImage(String name)
	{
		return images.get(new GameImage(name,true));
	}
}
