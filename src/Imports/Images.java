package Imports;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Client.ClientInventory;
import Server.ServerWorld;
import Tools.BinaryTree;
import WorldCreator.CreatorObject;

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
	public static final int INVENTORY_IMAGE_SIDELENGTH = ClientInventory.INVENTORY_WIDTH
			/ (ClientInventory.WIDTH + 1) - 20;

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
			BufferedImage image;

			// Import sprite sheets to create images with
			try
			{
				image = ImageIO.read(new File(
						"SLIME_SHEET.png"));
				for (int no = 0; no < 9; no++)
				{
					images.add(new GameImage("SLIME_" + no + IMAGE_FORMAT,
							image.getSubimage(no * 19, 0, 19, 17), 38, 34));
				}

				image = ImageIO.read(new File(
						"EXPLOSION_SHEET.png"));
				for (int no = 0; no < 7; no++)
				{
					images.add(new GameImage("EXPLOSION_" + no + IMAGE_FORMAT,
							image.getSubimage(no * 32, 0, 32, 32)));
				}
				
				image = ImageIO.read(new File(
						"SWORD_0.png"));

				double locationX = image.getWidth() / 2;
				double locationY = image.getHeight() / 2;

				for (int angle = 180; angle > -180; angle -= 15)
				{
					double rotationRequired = Math.toRadians(angle);
					AffineTransform tx;
					
					if (!(angle >= -90 && angle < 90))
					{
						tx = AffineTransform.getScaleInstance(-1, 1);
						tx.translate(-image.getWidth(null), 0);
					}
					
					tx = AffineTransform.getRotateInstance(
							rotationRequired, locationX, locationY);
					
					AffineTransformOp op = new AffineTransformOp(tx,
							AffineTransformOp.TYPE_BILINEAR);
					
					BufferedImage newImage = op
							.filter(image, null).getSubimage(0, 0, image.getWidth(), image.getHeight());
					
					images.add(new GameImage("SWORD_" + angle + ".png", newImage));
				}
				
				// Add images with rotations (the number in the name represents the angle rotated)
				
			}
			catch (IOException e)
			{
				System.out.println("Error loading sprite sheets");
				e.printStackTrace();
			}

			// Add images with rotations (the number in the name represents the angle rotated)
			images.add(new GameImage("SWORD.png", 70, 30));

			images.add(new GameImage("SWORD_ICON.png",
					INVENTORY_IMAGE_SIDELENGTH, INVENTORY_IMAGE_SIDELENGTH));

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
			images.add(new GameImage("WATER.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("DIRT.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("DIRTGRASS.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("COBBLESTONE.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("STONEBRICKS.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("NOTHING.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("BLACK.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));

			images.add(new GameImage("ENEMY.png", 60, 90));
			images.add(new GameImage("BULLET.png"));

			images.add(new GameImage("HP_POTION.png", 15, 15));
			images.add(new GameImage("HP_POTION_ICON.png",
					INVENTORY_IMAGE_SIDELENGTH, INVENTORY_IMAGE_SIDELENGTH));
			
			images.add(new GameImage("BACKGROUND.png"));
		}
	}

	/**
	 * Get a specific image from the list using the name of the image
	 * @param name the name of the image
	 * @return the image (inside a game image) from the list
	 */
	public static Image getImage(String name)
	{
		try{
			return images.get(new GameImage(name, true)).getImage();
		}
		catch(NullPointerException e)
		{
			System.out.println("Could not find image " + name);
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Get a specific image from the list using the name of the image
	 * @param name the name of the image
	 * @return the image (inside a game image) from the list
	 */
	public static Image getImage(String name, int width, int height)
	{
		try{
			return images.get(new GameImage(name, true)).getImage().getScaledInstance(width, height, 0);
		}
		catch(NullPointerException e)
		{
			System.out.println("Could not find image " + name);
			e.printStackTrace();
			return null;
		}
		
	}

	/**
	 * Get a specific game image from the list using the name of the image
	 * @param name the name of the image
	 * @return the game image from the list
	 */
	public static GameImage getGameImage(String name)
	{
		try{
			return images.get(new GameImage(name, true));
		}
		catch(NullPointerException e)
		{
			System.out.println("Could not find image " + name);
			e.printStackTrace();
			return null;
		}
	}
}
