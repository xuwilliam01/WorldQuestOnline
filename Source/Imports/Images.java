package Imports;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import Client.ClientFrame;
import Client.ClientInventory;
import Client.ClientShop;
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
			/ (ClientInventory.WIDTH + 2) - 20;

	/**
	 * Array list of the game images
	 */
	public static BinaryTree<GameImage> images = null;

	private static GameImage[] imageArray;

	private static int noOfImages = 0;

	/**
	 * Determines whether images were already imported or not
	 */
	private static boolean imported = false;

	/**
	 * Modify images using java and write them to the file
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
//		BufferedImage image = ImageIO.read(new File("Images//"
//				+ "RESOURCE_SHEET_5.png"));
//		BufferedImage[][] imageTiles = new BufferedImage[image
//				.getHeight() / 32][image.getWidth() / 32];
//		for (int row = 0; row < imageTiles.length; row++)
//		{
//			for (int column = 0; column < imageTiles[0].length; column++)
//			{
//				imageTiles[row][column] = image.getSubimage(
//						column * 32, row * 32, 32, 32);
//			}
//		}
//
//		image = imageTiles[2][6];
//
//		// double rotationRequired = Math.toRadians(135);
//		// AffineTransform tx;
//		//
//		// tx = AffineTransform.getRotateInstance(
//		// rotationRequired, image.getWidth()/2+1, image.getHeight()/2+1);
//		//
//		// AffineTransformOp op = new AffineTransformOp(tx,
//		// AffineTransformOp.TYPE_BILINEAR);
//		//
//		// BufferedImage newImage = op
//		// .filter(image, null);
//
//		ImageIO.write(image, "PNG", new File(
//				"Images//" + "STONEBRICKS.png"));
	}

	/**
	 * Import the images from the folder
	 * @throws IOException
	 */
	public static void importImages()
	{
		imageArray = new GameImage[10000];

		if (imported)
			return;

		imported = true;
		// Only import if the images haven't been imported already
		if (images == null)
		{
			images = new BinaryTree<GameImage>();
			BufferedImage image;

			// Import sprite sheets to create images with
			try
			{

				// Add all the pics from the player sprite sheets
				String[] slimeSheets = { "GREENSLIME", "REDSLIME", "BLUESLIME",
						"YELLOWSLIME" };
				for (int sheetNo = 0; sheetNo < slimeSheets.length; sheetNo++)
				{
					image = ImageIO.read(new File("Images//" +
							slimeSheets[sheetNo] + ".png"));

					for (int no = 0; no < 9; no++)
					{
						addtoImageArray(new GameImage(slimeSheets[sheetNo]
								+ "_" + no + IMAGE_FORMAT,
								image.getSubimage(no * 19, 0, 19, 17), 38, 34));
					}
				}

				image = ImageIO.read(new File("Images//" +
						"GREENSLIME.png"));

				for (int no = 0; no < 9; no++)
				{
					addtoImageArray(new GameImage("GIANTSLIME" + "_" + no
							+ IMAGE_FORMAT,
							image.getSubimage(no * 19, 0, 19, 17), 144, 102));
				}

				image = ImageIO.read(new File("Images//" +
						"DARKSLIME.png"));

				for (int no = 0; no < 9; no++)
				{
					addtoImageArray(new GameImage("DARKSLIME" + "_" + no
							+ IMAGE_FORMAT,
							image.getSubimage(no * 19, 0, 19, 17), 19, 17));
				}

				image = ImageIO.read(new File("Images//" +
						"EXPLOSION0_SHEET.png"));
				for (int no = 0; no < image.getWidth() / 32; no++)
				{
					addtoImageArray(new GameImage("EXPLOSION0_" + no
							+ IMAGE_FORMAT,
							image.getSubimage(no * 32, 0, 32, 32)));
				}

				image = ImageIO.read(new File("Images//" +
						"EXPLOSION1_SHEET.png"));
				for (int no = 0; no < image.getWidth() / 160; no++)
				{
					addtoImageArray(new GameImage("EXPLOSION1_" + no
							+ IMAGE_FORMAT,
							image.getSubimage(no * 160, 0, 160, 160), 100, 100));
				}
				for (int no = 0; no < image.getWidth() / 160; no++)
				{
					addtoImageArray(new GameImage("EXPLOSION1_" + (no + 5)
							+ IMAGE_FORMAT,
							image.getSubimage(no * 160, 160, 160, 160), 100,
							100));
				}

				image = ImageIO.read(new File("Images//" +
						"EXPLOSION2_SHEET.png"));
				for (int no = 0; no < image.getWidth() / 64; no++)
				{
					addtoImageArray(new GameImage("EXPLOSION2_" + no
							+ IMAGE_FORMAT,
							image.getSubimage(no * 64, 0, 64, 64)));
				}

				image = ImageIO.read(new File("Images//" +
						"EXPLOSION3_SHEET.png"));
				for (int no = 0; no < image.getWidth() / 82; no++)
				{
					addtoImageArray(new GameImage("EXPLOSION3_" + no
							+ IMAGE_FORMAT,
							image.getSubimage(no * 82, 0, 82, 82)));
				}

				image = ImageIO.read(new File("Images//" +
						"EXPLOSION4_SHEET.png"));
				for (int no = 0; no < image.getWidth() / 82; no++)
				{
					addtoImageArray(new GameImage("EXPLOSION4_" + no
							+ IMAGE_FORMAT,
							image.getSubimage(no * 82, 0, 82, 82)));
				}

				image = ImageIO.read(new File("Images//" +
						"STAR0.png"));
				for (int no = 0; no < image.getWidth() / 24; no++)
				{
					addtoImageArray(new GameImage("STAR0_" + no + IMAGE_FORMAT,
							image.getSubimage(no * 24, 0, 24, 24)));
				}

				image = ImageIO.read(new File("Images//" +
						"STAR1.png"));
				for (int no = 0; no < image.getWidth() / 24; no++)
				{
					addtoImageArray(new GameImage("STAR1_" + no + IMAGE_FORMAT,
							image.getSubimage(no * 24, 0, 24, 24)));
				}

				// Bats
				String[] batSheets = { "GBAT", "BBAT" };
				for (int sheetNo = 0; sheetNo < batSheets.length; sheetNo++)
				{
					image = ImageIO.read(new File("Images//" +
							batSheets[sheetNo] + ".png"));

					for (int no = 0; no < 5; no++)
					{
						BufferedImage currentImage = image.getSubimage(
								no * 32, 0, 32, 32);
						addtoImageArray(new GameImage(batSheets[sheetNo]
								+ "_RIGHT_" + no + IMAGE_FORMAT,
								currentImage));

						AffineTransform tx;
						tx = AffineTransform.getScaleInstance(-1, 1);
						tx.translate(-currentImage.getWidth(null), 0);
						AffineTransformOp op = new AffineTransformOp(tx,
								AffineTransformOp.TYPE_BILINEAR);

						// Add a left version of this image
						addtoImageArray(new GameImage(batSheets[sheetNo]
								+ "_LEFT_" + no + IMAGE_FORMAT,
								op.filter(currentImage, null).getSubimage(
										0, 0,
										currentImage.getWidth(),
										currentImage.getHeight())));
					}
				}

				// Add all the pics from the player sprite sheets
				String[] playerSheets = { "BASE_DARK", "BASE_LIGHT",
						"BASE_TAN", "HAIR0BEIGE", "HAIR1BEIGE",
						"HAIR0BLACK", "HAIR1BLACK", "HAIR0BLOND",
						"HAIR1BLOND", "HAIR0GREY", "HAIR1GREY",
						"OUTFITARMOR", "OUTFITNINJABLUE",
						"OUTFITNINJAGREY", "OUTFITNINJARED" };
				for (int no = 0; no < playerSheets.length; no++)
				{
					image = ImageIO.read(new File("Images//" + playerSheets[no]
							+ ".png"));
					BufferedImage[][] imageTiles = new BufferedImage[5][image
							.getWidth() / 32];
					for (int row = 0; row < imageTiles.length; row++)
					{
						for (int column = 0; column < imageTiles[0].length; column++)
						{
							BufferedImage currentImage = image.getSubimage(
									column * 32, row * 64, 32, 64);

							// Add a right version of this image
							addtoImageArray(new GameImage(playerSheets[no]
									+ "_RIGHT_" + row + "_" + column + ".png",
									currentImage, 64, 128));

							AffineTransform tx;
							tx = AffineTransform.getScaleInstance(-1, 1);
							tx.translate(-currentImage.getWidth(null), 0);
							AffineTransformOp op = new AffineTransformOp(tx,
									AffineTransformOp.TYPE_BILINEAR);

							// Add a left version of this image
							addtoImageArray(new GameImage(playerSheets[no]
									+ "_LEFT_" + row + "_" + column + ".png",
									op.filter(currentImage, null).getSubimage(
											0, 0,
											currentImage.getWidth(),
											currentImage.getHeight()), 64, 128));
						}
					}

					// Load death images (they are different sizes)
					BufferedImage currentImage = image.getSubimage(32, 5 * 64,
							32, 64);

					// Add a right version of this image
					addtoImageArray(new GameImage(playerSheets[no] + "_RIGHT_"
							+ 5
							+ "_" + 1 + ".png", currentImage, 64, 128));

					AffineTransform tx;
					tx = AffineTransform.getScaleInstance(-1, 1);
					tx.translate(-currentImage.getWidth(null), 0);
					AffineTransformOp op = new AffineTransformOp(tx,
							AffineTransformOp.TYPE_BILINEAR);

					// Add a left version of this image
					addtoImageArray(new GameImage(playerSheets[no] + "_LEFT_"
							+ 5
							+ "_" + 1 + ".png", op.filter(currentImage, null)
							.getSubimage(0, 0,
									currentImage.getWidth(),
									currentImage.getHeight()), 64, 128));

					currentImage = image.getSubimage(2 * 32, 5 * 64, 40, 64);
					// Add a right version of this image
					addtoImageArray(new GameImage(playerSheets[no] + "_RIGHT_"
							+ 5
							+ "_" + 2 + ".png", currentImage, 80, 128));

					AffineTransform tx2;
					tx2 = AffineTransform.getScaleInstance(-1, 1);
					tx2.translate(-currentImage.getWidth(null), 0);
					AffineTransformOp op2 = new AffineTransformOp(tx2,
							AffineTransformOp.TYPE_BILINEAR);

					// Add a left version of this image
					addtoImageArray(new GameImage(playerSheets[no] + "_LEFT_"
							+ 5
							+ "_" + 2 + ".png", op2.filter(currentImage, null)
							.getSubimage(0, 0,
									currentImage.getWidth(),
									currentImage.getHeight()), 80, 128));

					currentImage = image.getSubimage(4 * 32, 5 * 64, 52, 64);
					// Add a right version of this image
					addtoImageArray(new GameImage(playerSheets[no] + "_RIGHT_"
							+ 5
							+ "_" + 4 + ".png", currentImage, 104, 128));

					AffineTransform tx3;
					tx3 = AffineTransform.getScaleInstance(-1, 1);
					tx3.translate(-currentImage.getWidth(null), 0);
					AffineTransformOp op3 = new AffineTransformOp(tx3,
							AffineTransformOp.TYPE_BILINEAR);

					// Add a left version of this image
					addtoImageArray(new GameImage(playerSheets[no] + "_LEFT_"
							+ 5
							+ "_" + 4 + ".png", op3.filter(currentImage, null)
							.getSubimage(0, 0,
									currentImage.getWidth(),
									currentImage.getHeight()), 104, 128));

				}

				// Add all the pics from the player sprite sheets
				String[] goblinSheets = { "GOB", "GOBGENERAL", "GOBGUARD",
						"GOBKING", "GOBKNIGHT", "GOBLORD", "GOBNINJA",
						"GOBPEASANT", "GOBSOLDIER", "GOBWIZARD", "GOBWORKER",
						"GOBGIANT" };

				for (int no = 0; no < goblinSheets.length; no++)
				{
					double size = 1;
					if (!goblinSheets[no].equals("GOBGIANT"))
					{
						image = ImageIO.read(new File("Images//"
								+ goblinSheets[no] + ".png"));
					}
					else
					{
						image = ImageIO.read(new File("Images//" + "GOB.png"));
						size = 2;
					}
					BufferedImage[][] imageTiles = new BufferedImage[image
							.getHeight() / 64][image.getWidth() / 32];
					for (int row = 0; row < imageTiles.length; row++)
					{
						for (int column = 0; column < imageTiles[0].length; column++)
						{
							if (row == 1 && column >= 4)
							{
								continue;
							}
							BufferedImage currentImage = image.getSubimage(
									column * 32, row * 64, 32, 64);

							// Add a right version of this image
							addtoImageArray(new GameImage(goblinSheets[no]
									+ "_RIGHT_" + row + "_" + column + ".png",
									currentImage, (int) (64 * size + 0.5),
									(int) (128 * size + 0.5)));

							AffineTransform tx;
							tx = AffineTransform.getScaleInstance(-1, 1);
							tx.translate(-currentImage.getWidth(null), 0);
							AffineTransformOp op = new AffineTransformOp(tx,
									AffineTransformOp.TYPE_BILINEAR);

							// Add a left version of this image
							addtoImageArray(new GameImage(goblinSheets[no]
									+ "_LEFT_" + row + "_" + column + ".png",
									op.filter(currentImage, null).getSubimage(
											0, 0,
											currentImage.getWidth(),
											currentImage.getHeight()),
									(int) (64 * size + 0.5),
									(int) (128 * size + 0.5)));
						}

						// Add the death images
						BufferedImage currentImage = image.getSubimage(
								32 * 4 + 16, 64,
								32, 64);

						// Add a right version of this image
						addtoImageArray(new GameImage(goblinSheets[no]
								+ "_RIGHT_" + 1
								+ "_" + 4 + ".png", currentImage,
								(int) (64 * size + 0.5),
								(int) (128 * size + 0.5)));

						AffineTransform tx;
						tx = AffineTransform.getScaleInstance(-1, 1);
						tx.translate(-currentImage.getWidth(null), 0);
						AffineTransformOp op = new AffineTransformOp(tx,
								AffineTransformOp.TYPE_BILINEAR);

						// Add a left version of this image
						addtoImageArray(new GameImage(goblinSheets[no]
								+ "_LEFT_" + 1
								+ "_" + 4 + ".png", op.filter(currentImage,
								null)
								.getSubimage(0, 0,
										currentImage.getWidth(),
										currentImage.getHeight()),
								(int) (64 * size + 0.5),
								(int) (128 * size + 0.5)));

						currentImage = image.getSubimage(32 * 6 + 8, 64,
								42, 64);

						// Add a right version of this image
						addtoImageArray(new GameImage(goblinSheets[no]
								+ "_RIGHT_" + 1
								+ "_" + 6 + ".png", currentImage,
								(int) (84 * size + 0.5),
								(int) (128 * size + 0.5)));

						tx = AffineTransform.getScaleInstance(-1, 1);
						tx.translate(-currentImage.getWidth(null), 0);
						op = new AffineTransformOp(tx,
								AffineTransformOp.TYPE_BILINEAR);

						// Add a left version of this image
						addtoImageArray(new GameImage(goblinSheets[no]
								+ "_LEFT_" + 1
								+ "_" + 6 + ".png", op.filter(currentImage,
								null)
								.getSubimage(0, 0,
										currentImage.getWidth(),
										currentImage.getHeight()),
								(int) (84 * size + 0.5),
								(int) (128 * size + 0.5)));
					}
				}

				addtoImageArray(new GameImage("OUTFITARMOR_ICON.png", 32, 32));
				addtoImageArray(new GameImage("OUTFITNINJABLUE_ICON.png", 32,
						32));
				addtoImageArray(new GameImage("OUTFITNINJARED_ICON.png", 32, 32));
				addtoImageArray(new GameImage("OUTFITNINJAGREY_ICON.png", 32,
						32));

				String[] weapons = { "DAWOOD", "DASTONE", "DAIRON",
						"DAGOLD", "DADIAMOND", "AXWOOD", "AXSTONE", "AXIRON",
						"AXGOLD", "AXDIAMOND", "SWWOOD", "SWSTONE", "SWIRON",
						"SWGOLD", "SWDIAMOND", "HAWOOD", "HASTONE", "HAIRON",
						"HAGOLD", "HADIAMOND" };

				for (int no = 0; no < weapons.length; no++)
				{
					// Add the icon image to the game also
					addtoImageArray(new GameImage(weapons[no] + "_ICON.png"));

					image = ImageIO.read(new File(
							"Images//" + weapons[no] + ".png"));
					double locationX = image.getWidth() / 2;
					double locationY = image.getHeight() / 2;

					for (int angle = 180; angle > -180; angle -= 15)
					{
						double rotationRequired = Math.toRadians(angle);
						AffineTransform tx;

						tx = AffineTransform.getRotateInstance(
								rotationRequired, locationX, locationY);

						AffineTransformOp op = new AffineTransformOp(tx,
								AffineTransformOp.TYPE_BILINEAR);

						BufferedImage newImage = op
								.filter(image, null).getSubimage(0, 0,
										image.getWidth(), image.getHeight());

						if (weapons[no].contains("HA"))
						{
							addtoImageArray(new GameImage(
									weapons[no] + "_" + angle + ".png",
									newImage));
						}
						else
						{
							addtoImageArray(new GameImage(
									weapons[no] + "_" + angle + ".png",
									newImage));
						}

					}
				}

				addtoImageArray(new GameImage("OUTFITARMOR_ICON.png", 32, 32));
				addtoImageArray(new GameImage("OUTFITNINJABLUE_ICON.png", 32,
						32));
				addtoImageArray(new GameImage("OUTFITNINJARED_ICON.png", 32, 32));
				addtoImageArray(new GameImage("OUTFITNINJAGREY_ICON.png", 32,
						32));

				addtoImageArray(new GameImage("BULLET_0.png"));

				String[] projectiles = { "WOODARROW", "STEELARROW",
						"MEGAARROW", "FIREBALL_0", "FIREBALL_1", "ICEBALL_0",
						"ICEBALL_1", "DARKBALL_0", "DARKBALL_1" };

				for (int no = 0; no < projectiles.length; no++)
				{
					image = ImageIO.read(new File(
							"Images//" + projectiles[no] + ".png"));
					double locationX = image.getWidth() / 2;
					double locationY = image.getHeight() / 2;

					for (int angle = 180; angle > -180; angle -= 15)
					{
						double rotationRequired = Math.toRadians(angle);
						AffineTransform tx;
						BufferedImage newImage = image;

						if (angle < -90 || angle >= 90)
						{
							tx = AffineTransform.getScaleInstance(1, -1);
							tx.translate(0, -image.getHeight());
							AffineTransformOp op = new AffineTransformOp(tx,
									AffineTransformOp.TYPE_BILINEAR);
							newImage = op.filter(image, null);
							int cropWidth = Math.min(image.getWidth(),
									newImage.getWidth());
							int cropHeight = Math.min(image.getHeight(),
									newImage.getHeight());
							newImage = newImage.getSubimage(0, 0, cropWidth,
									cropHeight);

						}

						tx = AffineTransform.getRotateInstance(
								rotationRequired, locationX, locationY);

						AffineTransformOp op = new AffineTransformOp(tx,
								AffineTransformOp.TYPE_BILINEAR);

						newImage = op.filter(newImage, null);

						int cropWidth = Math.min(image.getWidth(),
								newImage.getWidth());
						int cropHeight = Math.min(image.getHeight(),
								newImage.getHeight());

						newImage = newImage.getSubimage(0, 0, cropWidth,
								cropHeight);

						int height = 42;
						int width = 42;

						if (no >= 7)
						{
							width = 58;
							height = 58;
						}
						else if (no >= 3 && no <= 6)
						{
							width = 48;
							height = 48;
						}
						else if (no == 2)
						{
							width = 81;
							height = 81;
						}

						addtoImageArray(new GameImage(
								projectiles[no] + "_" + (angle) + ".png",
								newImage, width, height));
					}
				}

			}
			catch (IOException e)
			{
				System.out.println("Error loading sprite sheets");
				e.printStackTrace();
			}

			
			
			/*
			 * (Implement later)
			 * Add the rest of the images through the text file First token
			 * protocol: 
			 * 0-Just the image name
			 * 1-Name + scale
			 * 2-Name + alias
			 */
			addtoImageArray(new GameImage("BRICK.jpg", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			GameImage newImage = new GameImage("BRICK.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("BRICK_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("GRASS.jpg", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("GRASS.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("GRASS_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("WATER.jpg", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("WATER.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("WATER_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("DIRT.jpg", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("DIRT.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("DIRT_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("DIRTGRASS.jpg",
					ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("DIRTGRASS.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("DIRTGRASS_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("COBBLESTONE.jpg",
					ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("COBBLESTONE.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("COBBLESTONE_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("STONEBRICKS.jpg",
					ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("STONEBRICKS.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("STONEBRICKS_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("NOTHING.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("NOTHING.png", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("NOTHING_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("BLACK.jpg", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("BLACK.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("BLACK_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("BDIRT.jpg", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("BDIRT.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("BDIRT_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("BDIRTPLAT.jpg",
					ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("BDIRTPLAT.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("BDIRTPLAT_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("PLAT.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("PLAT.png", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("PLAT_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("WATERPLAT.jpg",
					ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("WATERPLAT.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("WATERPLAT_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("SAND.jpg", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("SAND.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("SAND_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("BSAND.jpg", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("BSAND.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("BSAND_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("BSANDPLAT.jpg",
					ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("BSANDPLAT.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("BSANDPLAT_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("BSTONE.jpg", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("BSTONE.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("BSTONE_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("BSTONEPLAT.jpg",
					ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("BSTONEPLAT.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("BSTONEPLAT_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("PLANKS.jpg", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("PLANKS.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("PLANKS_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("STONE.jpg", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("STONE.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("STONE_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("SANDSTONE.jpg",
					ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			newImage = new GameImage("SANDSTONE.jpg", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE);
			newImage.setName("SANDSTONE_ICON");
			addtoImageArray(newImage);

			// Rest of the icons
			addtoImageArray(new GameImage("SLIME_6_ICON.png",
					CreatorObject.SCALE * ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE));
			addtoImageArray(new GameImage("RED_CASTLE_ICON.png",
					CreatorObject.SCALE * ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE));
			addtoImageArray(new GameImage("BLUE_CASTLE_ICON.png",
					CreatorObject.SCALE * ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE));
			addtoImageArray(new GameImage("CHEST_ICON.png", CreatorObject.SCALE
					* ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE));
			addtoImageArray(new GameImage("VENDOR_LEFT_ICON.png",
					CreatorObject.SCALE * ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE));
			addtoImageArray(new GameImage("SLIME_SPAWN.png",
					ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			addtoImageArray(new GameImage("SLIME_SPAWN_ICON.png",
					CreatorObject.SCALE * ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE));
			addtoImageArray(new GameImage("RED_GOBLIN_SPAWN.png",
					ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			addtoImageArray(new GameImage("RED_GOBLIN_SPAWN_ICON.png",
					CreatorObject.SCALE * ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE));
			addtoImageArray(new GameImage("BLUE_GOBLIN_SPAWN.png",
					ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			addtoImageArray(new GameImage("BLUE_GOBLIN_SPAWN_ICON.png",
					CreatorObject.SCALE * ServerWorld.TILE_SIZE,
					CreatorObject.SCALE * ServerWorld.TILE_SIZE));

			for (int no = 0; no < 6; no++)
			{
				addtoImageArray(new GameImage("CLOUD_" + no + ".png"));

			}

			addtoImageArray(new GameImage("VENDOR_RIGHT.png",
					4 * ServerWorld.TILE_SIZE, 5 * ServerWorld.TILE_SIZE));
			addtoImageArray(new GameImage("VENDOR_LEFT.png",
					4 * ServerWorld.TILE_SIZE, 5 * ServerWorld.TILE_SIZE));

			addtoImageArray(new GameImage("FIREWAND_RIGHT.png"));
			addtoImageArray(new GameImage("FIREWAND_LEFT.png"));
			addtoImageArray(new GameImage("FIREWAND_ICON.png"));

			addtoImageArray(new GameImage("ICEWAND_RIGHT.png"));
			addtoImageArray(new GameImage("ICEWAND_LEFT.png"));
			addtoImageArray(new GameImage("ICEWAND_ICON.png"));

			addtoImageArray(new GameImage("DARKWAND_RIGHT.png"));
			addtoImageArray(new GameImage("DARKWAND_LEFT.png"));
			addtoImageArray(new GameImage("DARKWAND_ICON.png"));

			addtoImageArray(new GameImage("SLINGSHOT_RIGHT.png"));
			addtoImageArray(new GameImage("SLINGSHOT_LEFT.png"));
			addtoImageArray(new GameImage("SLINGSHOT_ICON.png"));

			addtoImageArray(new GameImage("WOODBOW_RIGHT.png"));
			addtoImageArray(new GameImage("WOODBOW_LEFT.png"));
			addtoImageArray(new GameImage("WOODBOW_ICON.png"));

			addtoImageArray(new GameImage("STEELBOW_RIGHT.png"));
			addtoImageArray(new GameImage("STEELBOW_LEFT.png"));
			addtoImageArray(new GameImage("STEELBOW_ICON.png"));

			addtoImageArray(new GameImage("MEGABOW_RIGHT.png"));
			addtoImageArray(new GameImage("MEGABOW_LEFT.png"));
			addtoImageArray(new GameImage("MEGABOW_ICON.png"));

			addtoImageArray(new GameImage("CHEST.png",
					5 * ServerWorld.TILE_SIZE, 3 * ServerWorld.TILE_SIZE));
			addtoImageArray(new GameImage("RED_CASTLE.png",
					26 * ServerWorld.TILE_SIZE, 52 * ServerWorld.TILE_SIZE));
			addtoImageArray(new GameImage("BLUE_CASTLE.png",
					26 * ServerWorld.TILE_SIZE, 52 * ServerWorld.TILE_SIZE));
			addtoImageArray(new GameImage("COIN.png", ClientFrame.getScaledWidth(10), ClientFrame.getScaledHeight(10)));
			addtoImageArray(new GameImage("HP_POTION.png",
					Images.INVENTORY_IMAGE_SIDELENGTH,
					Images.INVENTORY_IMAGE_SIDELENGTH));
			addtoImageArray(new GameImage("MANA_POTION.png",
					Images.INVENTORY_IMAGE_SIDELENGTH,
					Images.INVENTORY_IMAGE_SIDELENGTH));
			addtoImageArray(new GameImage("MAX_HP_POTION.png",
					Images.INVENTORY_IMAGE_SIDELENGTH,
					Images.INVENTORY_IMAGE_SIDELENGTH));
			addtoImageArray(new GameImage("MAX_MANA_POTION.png",
					Images.INVENTORY_IMAGE_SIDELENGTH,
					Images.INVENTORY_IMAGE_SIDELENGTH));
			addtoImageArray(new GameImage("DMG_POTION.png",
					Images.INVENTORY_IMAGE_SIDELENGTH,
					Images.INVENTORY_IMAGE_SIDELENGTH));
			addtoImageArray(new GameImage("SPEED_POTION.png",
					Images.INVENTORY_IMAGE_SIDELENGTH,
					Images.INVENTORY_IMAGE_SIDELENGTH));
			addtoImageArray(new GameImage("JUMP_POTION.png",
					Images.INVENTORY_IMAGE_SIDELENGTH,
					Images.INVENTORY_IMAGE_SIDELENGTH));
			addtoImageArray(new GameImage("MONEY.png",
					INVENTORY_IMAGE_SIDELENGTH, INVENTORY_IMAGE_SIDELENGTH));
			addtoImageArray(new GameImage("MONEY_ICON.png",
					INVENTORY_IMAGE_SIDELENGTH, INVENTORY_IMAGE_SIDELENGTH));

			addtoImageArray(new GameImage("BACKGROUND.png",
					Client.Client.SCREEN_WIDTH, Client.Client.SCREEN_HEIGHT));
			
			addtoImageArray(new GameImage("Lobby.png",
					Client.Client.SCREEN_WIDTH + Client.ClientInventory.INVENTORY_WIDTH, Client.Client.SCREEN_HEIGHT));
			
			addtoImageArray(new GameImage("PLAINBLACK.png",
					Client.Client.SCREEN_WIDTH, Client.Client.SCREEN_HEIGHT));

			addtoImageArray(new GameImage("FindAGame.png",
					(int)(ClientFrame.getScaledWidth(300)),(int)(ClientFrame.getScaledHeight(100))));
			addtoImageArray(new GameImage("FindAGameClicked.png",
					(int)(ClientFrame.getScaledWidth(300)),(int)(ClientFrame.getScaledHeight(100))));
			addtoImageArray(new GameImage("CreateAServer.png",
					(int)(ClientFrame.getScaledWidth(400)),(int)(ClientFrame.getScaledHeight(100))));
			addtoImageArray(new GameImage("CreateAServerClicked.png",
					(int)(ClientFrame.getScaledWidth(400)),(int)(ClientFrame.getScaledHeight(100))));
			addtoImageArray(new GameImage("CreateAMap.png",
					(int)(ClientFrame.getScaledWidth(300)),(int)(ClientFrame.getScaledHeight(100))));
			addtoImageArray(new GameImage("CreateAMapClicked.png",
					(int)(ClientFrame.getScaledWidth(300)),(int)(ClientFrame.getScaledHeight(100))));
			addtoImageArray(new GameImage("WorldQuestOnline.png",(int)(ClientFrame.getScaledWidth(1238)),(int)(ClientFrame.getScaledHeight(248))));
			addtoImageArray(new GameImage("Instructions.png",
					(int)(ClientFrame.getScaledWidth(300)),(int)(ClientFrame.getScaledHeight(100))));
			addtoImageArray(new GameImage("InstructionsClicked.png",
					(int)(ClientFrame.getScaledWidth(300)),(int)(ClientFrame.getScaledHeight(100))));

			addtoImageArray(new GameImage("Inventory.png", (int)(ClientFrame.getScaledWidth(300)), Client.Client.SCREEN_HEIGHT));

			addtoImageArray(new GameImage("Objective.png"));
			addtoImageArray(new GameImage("Controls.png"));
			addtoImageArray(new GameImage("Stats.png"));

			addtoImageArray(new GameImage("Next.png"));

			addtoImageArray(new GameImage("Shop.png", (int)(ClientFrame.getScaledWidth(ClientShop.SHOP_WIDTH)),
					(int)(ClientFrame.getScaledHeight(ClientShop.SHOP_HEIGHT))));
			
			addtoImageArray(new GameImage("Cursor.png"));
			addtoImageArray(new GameImage("Cursorclick.png"));
			
			addtoImageArray(new GameImage("WorldQuestIcon.png"));
		}
		
	

			// Create a correctly sized array of the game images
			GameImage[] clone = imageArray;
			imageArray = new GameImage[noOfImages];
			for (int no = 0; no < noOfImages; no++)
			{
				imageArray[no] = clone[no];
			}

			Arrays.sort(imageArray);

			createBalancedTree(0, noOfImages);
			imageArray = null;
			clone = null;
	}

	/**
	 * Add a game image to the imageArray
	 * @param image
	 */
	private static void addtoImageArray(GameImage gameImage)
	{
		imageArray[noOfImages] = gameImage;
		noOfImages++;

	}

	/**
	 * Create the balanced tree of images
	 * @param low
	 * @param high
	 */
	private static void createBalancedTree(int low, int high)
	{

		if (low == high)
			return;

		int midpoint = (low + high) / 2;

		images.add(imageArray[midpoint]);

		createBalancedTree(midpoint + 1, high);
		createBalancedTree(low, midpoint);
	}

	/**
	 * Get a specific image from the list using the name of the image
	 * @param name the name of the image
	 * @return the image (inside a game image) from the list
	 */
	public static Image getImage(String name)
	{
		try
		{
			return images.get(new GameImage(name, true)).getImage();
		}
		catch (NullPointerException e)
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
		try
		{
			return images.get(new GameImage(name, true)).getImage()
					.getScaledInstance(width, height, 0);
		}
		catch (NullPointerException e)
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
		try
		{
			return images.get(new GameImage(name, true));
		}
		catch (NullPointerException e)
		{
			System.out.println("Could not find image " + name);
			e.printStackTrace();
			return null;
		}
	}
}
