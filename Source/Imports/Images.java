package Imports;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import Client.ClientCastleShopItem;
import Client.ClientFrame;
import Client.ClientInventory;
import Client.ClientShop;
import Server.ServerWorld;
import Server.Creatures.ServerPlayer;
import Tools.BinaryTree;
import WorldCreator.CreatorObject;

/**
 * Class that imports and stores all possible images in the game
 * 
 * @author William Xu & Alex Raita
 *
 */
public class Images {
	
	public final static Color YELLOW = new Color(204, 153, 0);
	public final static Color LIGHT_YELLOW = new Color(255, 255, 0);
	public final static Color LIGHT_GREEN = new Color(0, 204, 0);
	public final static Color RED = new Color(153, 0, 38);
	public final static Color DARK_RED = new Color(102,0,0);
	public final static Color BLUE = new Color(0, 161, 230);
	public final static Color GREEN = new Color(0, 153, 0);
	public final static Color PURPLE = new Color(82, 42, 122);
	public final static Color GRAY = Color.gray;
	public final static Color DARK_GRAY = Color.darkGray;
	public final static Color BLACK = Color.black;
	public final static Color WHITE = Color.white;

	/**
	 * Array of yellow starting from lowest opacity to highest opacity
	 */
	public static Color[] yellows = new Color[100];
	
	/**
	 * Array of yellow starting from lowest opacity to highest opacity
	 */
	public static Color[] lightYellows = new Color[100];
	
	/**
	 * Array of green starting from lowest opacity to highest opacity
	 */
	public static Color[] lightGreens = new Color[100];
	
	/**
	 * Array of red starting from lowest opacity to highest opacity
	 */
	public static Color[] reds = new Color[100];
	
	/**
	 * Array of darkReds starting from lowest opacity to highest opacity
	 */
	public static Color[] darkReds = new Color[255];
	
	/**
	 * Array of blue starting from lowest opacity to highest opacity
	 */
	public static Color[] blues = new Color[100];
	
	/**
	 * Array of purple starting from lowest opacity to highest opacity
	 */
	public static Color[] purples = new Color[100];
	
	/**
	 * Array of blacks starting from lowest opacity to highest opacity
	 */
	public static Color[] blacks = new Color[255];
	
	/**
	 * Array of whites starting from lowest opacity to highest opacity
	 */
	public static Color[] whites = new Color[255];
	
	/**
	 * Array of darkGrays starting from lowest opacity to highest opacity
	 */
	public static Color[] darkGrays = new Color[255];
	
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
	private static GameImage[] imageArray;
	private static Map<String, Integer> imageMap = new HashMap<String, Integer>();

	private static int noOfImages = 0;

	/**
	 * Determines whether images were already imported or not
	 */
	private static boolean imported = false;

	/**
	 * Modify images using java and write them to the file
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		BufferedImage image = ImageIO.read(new File("Images//" + "Other//"
//				+ "Platform.png"));
//		BufferedImage[][] imageTiles = new BufferedImage[image.getHeight() / 32][image
//				.getWidth() / 32];
//		for (int row = 0; row < imageTiles.length; row++) {
//			for (int column = 0; column < imageTiles[0].length; column++) {
//				imageTiles[row][column] = image.getSubimage(column * 32,
//						row * 32, 32, 32);
//			}
//		}

		// double rotationRequired = Math.toRadians(135);
		// AffineTransform tx;
		//
		// tx = AffineTransform.getRotateInstance(
		// rotationRequired, image.getWidth()/2+1, image.getHeight()/2+1);
		//
		// AffineTransformOp op = new AffineTransformOp(tx,
		// AffineTransformOp.TYPE_BILINEAR);
		//
		// BufferedImage newImage = op
		// .filter(image, null);

//		for (int no = 0; no < 1; no++) {
//			image = imageTiles[10][7+no ];
//			ImageIO.write(image, "PNG", new File("Images//" + "Tiles//"
//					+ "stone_edgeright"  + (0 + no) + ".png"));
//		}
	
	}

	public static int getImageIndex(String image)
	{
		Integer no = imageMap.get(image);
		if (no!=null)
		{
			return no;
		}
		return -1;
	}
	
	public static String getImageName(int index)
	{
		//System.out.println(index);
		return imageArray[index].getName();
	}
	
	/**
	 * Import the images from the folder
	 * 
	 * @throws IOException
	 */
	public static void importImages() {
		
		if (imported)
			return;
		
		// Create array of yellows with varying opacity
		for (int no = 0; no < yellows.length;no++)
		{
			yellows[no] = new Color (YELLOW.getRed(), YELLOW.getGreen(), YELLOW.getBlue(), (int)((1.0*(no+1)/yellows.length)*255.0));
		}
		
		// Create array of lightYellows with varying opacity
		for (int no = 0; no < lightYellows.length;no++)
		{
			lightYellows[no] = new Color (LIGHT_YELLOW.getRed(), LIGHT_YELLOW.getGreen(), LIGHT_YELLOW.getBlue(), (int)((1.0*(no+1)/lightYellows.length)*255.0));
		}
		
		// Create array of lightgreens with varying opacity
		for (int no = 0; no < lightGreens.length;no++)
		{
			lightGreens[no] = new Color (LIGHT_GREEN.getRed(), LIGHT_GREEN.getGreen(), LIGHT_GREEN.getBlue(), (int)((1.0*(no+1)/lightGreens.length)*255.0));
		}
		
		// Create array of blues with varying opacity
		for (int no = 0; no < blues.length;no++)
		{
			blues[no] = new Color (BLUE.getRed(), BLUE.getGreen(), BLUE.getBlue(), (int)((1.0*(no+1)/blues.length)*255.0));
		}
		
		// Create array of PURPLEs with varying opacity
		for (int no = 0; no < purples.length;no++)
		{
			purples[no] = new Color (PURPLE.getRed(), PURPLE.getGreen(), PURPLE.getBlue(), (int)((1.0*(no+1)/purples.length)*255.0));
		}
		
		// Create array of reds with varying opacity
		for (int no = 0; no < reds.length;no++)
		{
			reds[no] = new Color (RED.getRed(), RED.getGreen(), RED.getBlue(), (int)((1.0*(no+1)/reds.length)*255.0));
		}
		
		// Create array of reds with varying opacity
		for (int no = 0; no < darkReds.length;no++)
		{
			darkReds[no] = new Color (DARK_RED.getRed(), DARK_RED.getGreen(), DARK_RED.getBlue(), (int)((1.0*(no+1)/darkReds.length)*255.0));
		}
		
		// Create array of blacks with varying opacity
		for (int no = 0; no < blacks.length;no++)
		{
			blacks[no] = new Color (BLACK.getRed(), BLACK.getGreen(), BLACK.getBlue(), (int)((1.0*(no+1)/blacks.length)*255.0));
		}
		
		// Create array of whites with varying opacity
		for (int no = 0; no < whites.length;no++)
		{
			whites[no] = new Color (WHITE.getRed(), WHITE.getGreen(), WHITE.getBlue(), (int)((1.0*(no+1)/whites.length)*255.0));
		}
		
		// Create array of darkGrays with varying opacity
		for (int no = 0; no < darkGrays.length;no++)
		{
			darkGrays[no] = new Color (DARK_GRAY.getRed(), DARK_GRAY.getGreen(), DARK_GRAY.getBlue(), (int)((1.0*(no+1)/darkGrays.length)*255.0));
		}
		
		imageArray = new GameImage[10000];

		imported = true;
		// Only import if the images haven't been imported already
		if (imageMap.isEmpty()) {
			BufferedImage image = null;

			// Import sprite sheets to create images with
			try {

				// Add all the pics from the player sprite sheets
				String[] slimeSheets = { "GREENSLIME", "REDSLIME", "BLUESLIME",
						"YELLOWSLIME" };
				for (int sheetNo = 0; sheetNo < slimeSheets.length; sheetNo++) {
					image = ImageIO.read(new File("Images//Creatures//"
							+ slimeSheets[sheetNo] + ".png"));

					for (int no = 0; no < 9; no++) {
						addtoImageArray(new GameImage(slimeSheets[sheetNo]
								+ "_" + no + IMAGE_FORMAT, image.getSubimage(
								no * 19, 0, 19, 17), 38, 34));
					}
				}

				image = ImageIO.read(new File("Images//Creatures//"
						+ "GREENSLIME.png"));

				for (int no = 0; no < 9; no++) {
					addtoImageArray(new GameImage("GIANTSLIME" + "_" + no
							+ IMAGE_FORMAT, image.getSubimage(no * 19, 0, 19,
							17), 144, 102));
				}

				image = ImageIO.read(new File("Images//Creatures//"
						+ "DARKSLIME.png"));

				for (int no = 0; no < 9; no++) {
					addtoImageArray(new GameImage("DARKSLIME" + "_" + no
							+ IMAGE_FORMAT, image.getSubimage(no * 19, 0, 19,
							17), 19, 17));
				}

				image = ImageIO.read(new File("Images//Effects//"
						+ "EXPLOSION0_SHEET.png"));
				for (int no = 0; no < image.getWidth() / 32; no++) {
					addtoImageArray(new GameImage("EXPLOSION0_" + no
							+ IMAGE_FORMAT, image.getSubimage(no * 32, 0, 32,
							32)));
				}

				image = ImageIO.read(new File("Images//Effects//"
						+ "EXPLOSION1_SHEET.png"));
				for (int no = 0; no < image.getWidth() / 160; no++) {
					addtoImageArray(new GameImage("EXPLOSION1_" + no
							+ IMAGE_FORMAT, image.getSubimage(no * 160, 0, 160,
							160), 100, 100));
				}
				for (int no = 0; no < image.getWidth() / 160; no++) {
					addtoImageArray(new GameImage("EXPLOSION1_" + (no + 5)
							+ IMAGE_FORMAT, image.getSubimage(no * 160, 160,
							160, 160), 100, 100));
				}

				image = ImageIO.read(new File("Images//Effects//"
						+ "EXPLOSION2_SHEET.png"));
				for (int no = 0; no < image.getWidth() / 64; no++) {
					addtoImageArray(new GameImage("EXPLOSION2_" + no
							+ IMAGE_FORMAT, image.getSubimage(no * 64, 0, 64,
							64), 64, 64));
				}

				image = ImageIO.read(new File("Images//Effects//"
						+ "EXPLOSION3_SHEET.png"));
				for (int no = 0; no < image.getWidth() / 82; no++) {
					addtoImageArray(new GameImage("EXPLOSION3_" + no
							+ IMAGE_FORMAT, image.getSubimage(no * 82, 0, 82,
							82), 82, 82));
				}

				image = ImageIO.read(new File("Images//Effects//"
						+ "EXPLOSION4_SHEET.png"));
				for (int no = 0; no < image.getWidth() / 82; no++) {
					addtoImageArray(new GameImage("EXPLOSION4_" + no
							+ IMAGE_FORMAT, image.getSubimage(no * 82, 0, 82,
							82), 82, 82));
				}
				
				for (int no = 0; no < 14; no++)
				{
					BufferedImage buffImage= ImageIO.read(new File("Images//Effects//"
							+ "SMOKE_" + no + ".png"));
					addtoImageArray(new GameImage("SMOKE_RIGHT_" + no
							+ IMAGE_FORMAT, buffImage,320,180));
					
					AffineTransform tx;
					tx = AffineTransform.getScaleInstance(-1, 1);
					tx.translate(-buffImage.getWidth(null), 0);
					AffineTransformOp op = new AffineTransformOp(tx,
							AffineTransformOp.TYPE_BILINEAR);
					addtoImageArray(new GameImage("SMOKE_LEFT_" + no
							+ IMAGE_FORMAT,
							op.filter(buffImage, null).getSubimage(
									0, 0, buffImage.getWidth(),
									buffImage.getHeight()),320,180));
					
				}
				
				



				image = ImageIO
						.read(new File("Images//Weapons//" + "STAR0.png"));
				for (int no = 0; no < image.getWidth() / 24; no++) {
					addtoImageArray(new GameImage("STAR0_" + no + IMAGE_FORMAT,
							image.getSubimage(no * 24, 0, 24, 24), 24, 24));
				}

				image = ImageIO
						.read(new File("Images//Weapons//" + "STAR1.png"));
				for (int no = 0; no < image.getWidth() / 24; no++) {
					addtoImageArray(new GameImage("STAR1_" + no + IMAGE_FORMAT,
							image.getSubimage(no * 24, 0, 24, 24), 24, 24));
				}

				// Bats
				// BATG = Grey bat
				// BATB = Brown bat
				// BATD = Dark bat
				String[] batSheets = { "BATG", "BATB", "BATD" };
				for (int sheetNo = 0; sheetNo < batSheets.length; sheetNo++) {
					image = ImageIO.read(new File("Images//Creatures//"
							+ batSheets[sheetNo] + ".png"));

					for (int no = 0; no < 6; no++) {
						BufferedImage currentImage = image.getSubimage(no * 32,
								0, 32, 32);
						addtoImageArray(new GameImage(batSheets[sheetNo]
								+ "_LEFT_" + no + IMAGE_FORMAT, currentImage,
								64, 64));

						AffineTransform tx;
						tx = AffineTransform.getScaleInstance(-1, 1);
						tx.translate(-currentImage.getWidth(null), 0);
						AffineTransformOp op = new AffineTransformOp(tx,
								AffineTransformOp.TYPE_BILINEAR);

						// Add a left version of this image
						addtoImageArray(new GameImage(batSheets[sheetNo]
								+ "_RIGHT_" + no + IMAGE_FORMAT, op.filter(
								currentImage, null).getSubimage(0, 0,
								currentImage.getWidth(),
								currentImage.getHeight()), 64, 64));
					}
				}

				// Add all the pics from the player sprite sheets
				String[] playerSheets = { "BASE_DARK", "BASE_LIGHT",
						"BASE_TAN", "HAIR0BEIGE", "HAIR1BEIGE", "HAIR0BLACK",
						"HAIR1BLACK", "HAIR0BLOND", "HAIR1BLOND", "HAIR0GREY",
						"HAIR1GREY", "OUTFITARMOR", "OUTFITNINJABLUE",
						"OUTFITNINJAGREY", "OUTFITNINJARED" };
				for (int no = 0; no < playerSheets.length; no++) {
					if (no <= 10) {
						image = ImageIO.read(new File("Images//Creatures//"
								+ playerSheets[no] + ".png"));
					} else {
						image = ImageIO.read(new File("Images//Items//"
								+ playerSheets[no] + ".png"));
					}
					BufferedImage[][] imageTiles = new BufferedImage[5][image
							.getWidth() / 32];
					for (int row = 0; row < imageTiles.length; row++) {
						for (int column = 0; column < imageTiles[0].length; column++) {
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
											0, 0, currentImage.getWidth(),
											currentImage.getHeight()), 64, 128));
						}
					}

					// Load death images (they are different sizes)
					BufferedImage currentImage = image.getSubimage(32, 5 * 64,
							32, 64);

					// Add a right version of this image
					addtoImageArray(new GameImage(playerSheets[no] + "_RIGHT_"
							+ 5 + "_" + 1 + ".png", currentImage, 64, 128));

					AffineTransform tx;
					tx = AffineTransform.getScaleInstance(-1, 1);
					tx.translate(-currentImage.getWidth(null), 0);
					AffineTransformOp op = new AffineTransformOp(tx,
							AffineTransformOp.TYPE_BILINEAR);

					// Add a left version of this image
					addtoImageArray(new GameImage(playerSheets[no] + "_LEFT_"
							+ 5 + "_" + 1 + ".png", op.filter(currentImage,
							null).getSubimage(0, 0, currentImage.getWidth(),
							currentImage.getHeight()), 64, 128));

					currentImage = image.getSubimage(2 * 32, 5 * 64, 40, 64);
					// Add a right version of this image
					addtoImageArray(new GameImage(playerSheets[no] + "_RIGHT_"
							+ 5 + "_" + 2 + ".png", currentImage, 80, 128));

					AffineTransform tx2;
					tx2 = AffineTransform.getScaleInstance(-1, 1);
					tx2.translate(-currentImage.getWidth(null), 0);
					AffineTransformOp op2 = new AffineTransformOp(tx2,
							AffineTransformOp.TYPE_BILINEAR);

					// Add a left version of this image
					addtoImageArray(new GameImage(playerSheets[no] + "_LEFT_"
							+ 5 + "_" + 2 + ".png", op2.filter(currentImage,
							null).getSubimage(0, 0, currentImage.getWidth(),
							currentImage.getHeight()), 80, 128));

					currentImage = image.getSubimage(4 * 32, 5 * 64, 52, 64);
					// Add a right version of this image
					addtoImageArray(new GameImage(playerSheets[no] + "_RIGHT_"
							+ 5 + "_" + 4 + ".png", currentImage, 104, 128));

					AffineTransform tx3;
					tx3 = AffineTransform.getScaleInstance(-1, 1);
					tx3.translate(-currentImage.getWidth(null), 0);
					AffineTransformOp op3 = new AffineTransformOp(tx3,
							AffineTransformOp.TYPE_BILINEAR);

					// Add a left version of this image
					addtoImageArray(new GameImage(playerSheets[no] + "_LEFT_"
							+ 5 + "_" + 4 + ".png", op3.filter(currentImage,
							null).getSubimage(0, 0, currentImage.getWidth(),
							currentImage.getHeight()), 104, 128));

				}

				// Add all the pics from the player sprite sheets
				String[] goblinSheets = { "GOB", "GOBGENERAL", "GOBGUARD",
						"GOBKING", "GOBKNIGHT", "GOBLORD", "GOBNINJA",
						"GOBARCHER", "GOBSOLDIER", "GOBWIZARD", "GOBWORKER",
						"GOBGIANT" };

				for (int no = 0; no < goblinSheets.length; no++) {
					double size = 1;
					if (!goblinSheets[no].equals("GOBGIANT")) {
						image = ImageIO.read(new File("Images//Creatures//"
								+ goblinSheets[no] + ".png"));
					} else {
						image = ImageIO.read(new File("Images//Creatures//"
								+ "GOB.png"));
						size = 2;
					}
					BufferedImage[][] imageTiles = new BufferedImage[image
							.getHeight() / 64][image.getWidth() / 32];
					for (int row = 0; row < imageTiles.length; row++) {
						for (int column = 0; column < imageTiles[0].length; column++) {
							if (row == 1 && column >= 4) {
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
											0, 0, currentImage.getWidth(),
											currentImage.getHeight()),
									(int) (64 * size + 0.5),
									(int) (128 * size + 0.5)));
						}

						// Add the death images
						BufferedImage currentImage = image.getSubimage(
								32 * 4 + 16, 64, 32, 64);

						// Add a right version of this image
						addtoImageArray(new GameImage(goblinSheets[no]
								+ "_RIGHT_" + 1 + "_" + 4 + ".png",
								currentImage, (int) (64 * size + 0.5),
								(int) (128 * size + 0.5)));

						AffineTransform tx;
						tx = AffineTransform.getScaleInstance(-1, 1);
						tx.translate(-currentImage.getWidth(null), 0);
						AffineTransformOp op = new AffineTransformOp(tx,
								AffineTransformOp.TYPE_BILINEAR);

						// Add a left version of this image
						addtoImageArray(new GameImage(goblinSheets[no]
								+ "_LEFT_" + 1 + "_" + 4 + ".png", op.filter(
								currentImage, null).getSubimage(0, 0,
								currentImage.getWidth(),
								currentImage.getHeight()),
								(int) (64 * size + 0.5),
								(int) (128 * size + 0.5)));

						currentImage = image
								.getSubimage(32 * 6 + 8, 64, 42, 64);

						// Add a right version of this image
						addtoImageArray(new GameImage(goblinSheets[no]
								+ "_RIGHT_" + 1 + "_" + 6 + ".png",
								currentImage, (int) (84 * size + 0.5),
								(int) (128 * size + 0.5)));

						tx = AffineTransform.getScaleInstance(-1, 1);
						tx.translate(-currentImage.getWidth(null), 0);
						op = new AffineTransformOp(tx,
								AffineTransformOp.TYPE_BILINEAR);

						// Add a left version of this image
						addtoImageArray(new GameImage(goblinSheets[no]
								+ "_LEFT_" + 1 + "_" + 6 + ".png", op.filter(
								currentImage, null).getSubimage(0, 0,
								currentImage.getWidth(),
								currentImage.getHeight()),
								(int) (84 * size + 0.5),
								(int) (128 * size + 0.5)));
					}
				}

				addtoImageArray(new GameImage("OUTFITARMOR_ICON.png", 32, 32,
						"Items//"));
				addtoImageArray(new GameImage("OUTFITNINJABLUE_ICON.png", 32,
						32, "Items//"));
				addtoImageArray(new GameImage("OUTFITNINJARED_ICON.png", 32,
						32, "Items//"));
				addtoImageArray(new GameImage("OUTFITNINJAGREY_ICON.png", 32,
						32, "Items//"));

				String[] weapons = { "DAWOOD", "DASTONE", "DAIRON", "DAGOLD",
						"DADIAMOND", "AXWOOD", "AXSTONE", "AXIRON", "AXGOLD",
						"AXDIAMOND", "SWWOOD", "SWSTONE", "SWIRON", "SWGOLD",
						"SWDIAMOND", "HAWOOD", "HASTONE", "HAIRON", "HAGOLD",
						"HADIAMOND" };

				for (int no = 0; no < weapons.length; no++) {
					// Add the icon image to the game also
					addtoImageArray(new GameImage(weapons[no] + "_ICON.png",
							"Weapons//"));

					image = ImageIO.read(new File("Images//" + "Weapons//"
							+ weapons[no] + ".png"));
					int width = image.getWidth();
					int height = image.getHeight();

					double locationX = image.getWidth() / 2;
					double locationY = image.getHeight() / 2;

					for (int angle = 180; angle > -180; angle -= 15) {
						double rotationRequired = Math.toRadians(angle);
						AffineTransform tx;

						tx = AffineTransform.getRotateInstance(
								rotationRequired, locationX, locationY);

						AffineTransformOp op = new AffineTransformOp(tx,
								AffineTransformOp.TYPE_BILINEAR);

						BufferedImage newImage = op.filter(image, null)
								.getSubimage(0, 0, width, height);

						if (weapons[no].contains("HA")) {
							addtoImageArray(new GameImage(weapons[no] + "_"
									+ angle + ".png", newImage, width, height));
						} else {
							addtoImageArray(new GameImage(weapons[no] + "_"
									+ angle + ".png", newImage, width, height));
						}

					}
				}

				addtoImageArray(new GameImage("BULLET_0.png", "Weapons//"));

				String[] projectiles = { "WOODARROW", "STEELARROW",
						"MEGAARROW", "FIREBALL_0", "FIREBALL_1", "ICEBALL_0",
						"ICEBALL_1", "DARKBALL_0", "DARKBALL_1" };

				for (int no = 0; no < projectiles.length; no++) {
					image = ImageIO.read(new File("Images//" + "Weapons//"
							+ projectiles[no] + ".png"));
					double locationX = image.getWidth() / 2;
					double locationY = image.getHeight() / 2;

					for (int angle = 180; angle > -180; angle -= 15) {
						double rotationRequired = Math.toRadians(angle);
						AffineTransform tx;
						BufferedImage newImage = image;

						if (angle < -90 || angle >= 90) {
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

						if (no >= 7) {
							width = 58;
							height = 58;
						} else if (no >= 3 && no <= 6) {
							width = 48;
							height = 48;
						} else if (no == 2) {
							width = 81;
							height = 81;
						}

						addtoImageArray(new GameImage(projectiles[no] + "_"
								+ (angle) + ".png", newImage, width, height));
					}
				}

			} catch (IOException e) {
				System.out.println("Error loading sprite sheets");
				e.printStackTrace();
			}

			/*
			 * (Implement later) Add the rest of the images through the text
			 * file First token protocol: 0-Just the image name 1-Name + scale
			 * 2-Name + alias
			 */

			for (int no = 0; no < 6; no++) {
				addtoImageArray(new GameImage("CLOUD_" + no + ".png",
						"Background//"));

			}
			addtoImageArray(new GameImage("FIREWAND_RIGHT.png", "Weapons//"));
			addtoImageArray(new GameImage("FIREWAND_LEFT.png", "Weapons//"));
			addtoImageArray(new GameImage("FIREWAND_ICON.png", "Weapons//"));

			addtoImageArray(new GameImage("ICEWAND_RIGHT.png", "Weapons//"));
			addtoImageArray(new GameImage("ICEWAND_LEFT.png", "Weapons//"));
			addtoImageArray(new GameImage("ICEWAND_ICON.png", "Weapons//"));

			addtoImageArray(new GameImage("DARKWAND_RIGHT.png", "Weapons//"));
			addtoImageArray(new GameImage("DARKWAND_LEFT.png", "Weapons//"));
			addtoImageArray(new GameImage("DARKWAND_ICON.png", "Weapons//"));

			addtoImageArray(new GameImage("SLINGSHOT_RIGHT.png", "Weapons//"));
			addtoImageArray(new GameImage("SLINGSHOT_LEFT.png", "Weapons//"));
			addtoImageArray(new GameImage("SLINGSHOT_ICON.png", "Weapons//"));

			addtoImageArray(new GameImage("WOODBOW_RIGHT.png", "Weapons//"));
			addtoImageArray(new GameImage("WOODBOW_LEFT.png", "Weapons//"));
			addtoImageArray(new GameImage("WOODBOW_ICON.png", "Weapons//"));

			addtoImageArray(new GameImage("STEELBOW_RIGHT.png", "Weapons//"));
			addtoImageArray(new GameImage("STEELBOW_LEFT.png", "Weapons//"));
			addtoImageArray(new GameImage("STEELBOW_ICON.png", "Weapons//"));

			addtoImageArray(new GameImage("MEGABOW_RIGHT.png", "Weapons//"));
			addtoImageArray(new GameImage("MEGABOW_LEFT.png", "Weapons//"));
			addtoImageArray(new GameImage("MEGABOW_ICON.png", "Weapons//"));

			addtoImageArray(new GameImage("Objective.png", "Interface////"));
			addtoImageArray(new GameImage("Controls.png", "Interface////"));
			addtoImageArray(new GameImage("Stats.png", "Interface////"));

			addtoImageArray(new GameImage("Next.png", "Interface////"));

			addtoImageArray(new GameImage("Cursor.png", "Interface////"));
			addtoImageArray(new GameImage("Cursorclick.png", "Interface////"));

			addtoImageArray(new GameImage("WorldQuestIcon.png", "Interface////"));
			
			addtoImageArray(new GameImage("menuBackground.png", ClientFrame.getScaledWidth(1920), ClientFrame.getScaledHeight(1080), "Interface////"));

			addtoImageArray(new GameImage("BRICK.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			GameImage newImage = new GameImage("BRICK.png", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("BRICK_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("GRASS.jpg", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("GRASS.jpg", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("GRASS_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("WATER.jpg", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("WATER.jpg", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("WATER_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("DIRT.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("DIRT.png", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("DIRT_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("DIRTGRASS.jpg",
					ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("DIRTGRASS.jpg", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("DIRTGRASS_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("COBBLESTONE.jpg",
					ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("COBBLESTONE.jpg", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("COBBLESTONE_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("STONEBRICKS.jpg",
					ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("STONEBRICKS.jpg", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("STONEBRICKS_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("NOTHING.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("NOTHING.png", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("NOTHING_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("BLACK.jpg", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("BLACK.jpg", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("BLACK_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("BDIRT.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("BDIRT.png", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("BDIRT_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("BDIRTPLAT.png",
					ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("BDIRTPLAT.png", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("BDIRTPLAT_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("PLAT.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("PLAT.png", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("PLAT_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("WATERPLAT.jpg",
					ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("WATERPLAT.jpg", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("WATERPLAT_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("SAND.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("SAND.png", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("SAND_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("BSAND.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("BSAND.png", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("BSAND_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("BSANDPLAT.png",
					ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("BSANDPLAT.png", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("BSANDPLAT_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("BSTONE.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("BSTONE.png", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("BSTONE_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("BSTONEPLAT.png",
					ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("BSTONEPLAT.png", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("BSTONEPLAT_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("PLANKS.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("PLANKS.png", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("PLANKS_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("STONE.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("STONE.png", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("STONE_ICON");
			addtoImageArray(newImage);

			addtoImageArray(new GameImage("SANDSTONE.jpg",
					ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE, "Tiles//"));
			newImage = new GameImage("SANDSTONE.jpg", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Tiles//");
			newImage.setName("SANDSTONE_ICON");
			addtoImageArray(newImage);

			// Rest of the icons
			addtoImageArray(new GameImage("SLIME_6_ICON.png",
					(int)(CreatorObject.SCALE),
					(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE), "Creatures//"));
			addtoImageArray(new GameImage("RED_CASTLE_ICON.png",
					(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),
					(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE), "Creatures//"));
			addtoImageArray(new GameImage("BLUE_CASTLE_ICON.png",
					(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),
					(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE), "Creatures//"));
			addtoImageArray(new GameImage("CHEST_ICON.png", (int)(CreatorObject.SCALE
					* ServerWorld.TILE_SIZE), (int)(CreatorObject.SCALE
							* ServerWorld.TILE_SIZE), "Creatures//"));
			addtoImageArray(new GameImage("VENDOR_LEFT_ICON.png",
					(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),
					(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE), "Creatures//"));
			addtoImageArray(new GameImage("SLIME_SPAWN.png",
					ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE, "Creatures//"));
			addtoImageArray(new GameImage("SLIME_SPAWN_ICON.png",
					(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),
					(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE), "Creatures//"));
			addtoImageArray(new GameImage("BAT_SPAWN.png",
					ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE, "Creatures//"));
			addtoImageArray(new GameImage("BAT_SPAWN_ICON.png",
					(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),
					(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE), "Creatures//"));
			addtoImageArray(new GameImage("RED_GOBLIN_SPAWN.png",
					ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE, "Creatures//"));
			addtoImageArray(new GameImage("RED_GOBLIN_SPAWN_ICON.png",
					(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),
					(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE), "Creatures//"));
			addtoImageArray(new GameImage("BLUE_GOBLIN_SPAWN.png",
					ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE, "Creatures//"));
			addtoImageArray(new GameImage("BLUE_GOBLIN_SPAWN_ICON.png",
					(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),
					(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE), "Creatures//"));

			addtoImageArray(new GameImage("VENDOR_RIGHT.png",
					56, 72,
					"Creatures//"));
			addtoImageArray(new GameImage("VENDOR_LEFT.png",
					56, 72,
					"Creatures//"));

			addtoImageArray(new GameImage("CHEST.png",
					80, 48,
					"Creatures//"));
			addtoImageArray(new GameImage("COIN.png",
					ClientFrame.getScaledWidth(10),
					ClientFrame.getScaledHeight(10), "Items//"));
			addtoImageArray(new GameImage("HP_POTION.png",
					Images.INVENTORY_IMAGE_SIDELENGTH,
					Images.INVENTORY_IMAGE_SIDELENGTH, "Items//"));
			addtoImageArray(new GameImage("MANA_POTION.png",
					Images.INVENTORY_IMAGE_SIDELENGTH,
					Images.INVENTORY_IMAGE_SIDELENGTH, "Items//"));
			addtoImageArray(new GameImage("MAX_HP_POTION.png",
					Images.INVENTORY_IMAGE_SIDELENGTH,
					Images.INVENTORY_IMAGE_SIDELENGTH, "Items//"));
			addtoImageArray(new GameImage("MAX_MANA_POTION.png",
					Images.INVENTORY_IMAGE_SIDELENGTH,
					Images.INVENTORY_IMAGE_SIDELENGTH, "Items//"));
			addtoImageArray(new GameImage("DMG_POTION.png",
					Images.INVENTORY_IMAGE_SIDELENGTH,
					Images.INVENTORY_IMAGE_SIDELENGTH, "Items//"));
			addtoImageArray(new GameImage("SPEED_POTION.png",
					Images.INVENTORY_IMAGE_SIDELENGTH,
					Images.INVENTORY_IMAGE_SIDELENGTH, "Items//"));
			addtoImageArray(new GameImage("JUMP_POTION.png",
					Images.INVENTORY_IMAGE_SIDELENGTH,
					Images.INVENTORY_IMAGE_SIDELENGTH, "Items//"));
			addtoImageArray(new GameImage("MONEY.png",
					INVENTORY_IMAGE_SIDELENGTH, INVENTORY_IMAGE_SIDELENGTH,
					"Items//"));
			addtoImageArray(new GameImage("MONEY_ICON.png",
					INVENTORY_IMAGE_SIDELENGTH, INVENTORY_IMAGE_SIDELENGTH,
					"Items//"));

			addtoImageArray(new GameImage("SKY.jpg",Client.Client.SCREEN_WIDTH, Client.Client.SCREEN_HEIGHT,
					"Background//"));
			
			addtoImageArray(new GameImage("DUSK.jpg",
					Client.Client.SCREEN_WIDTH, Client.Client.SCREEN_HEIGHT,
					"Background//"));

			addtoImageArray(new GameImage("Lobby.png",
					Client.Client.SCREEN_WIDTH
							+ Client.ClientInventory.INVENTORY_WIDTH,
					Client.Client.SCREEN_HEIGHT, "Interface//"));

			addtoImageArray(new GameImage("PLAINBLACK.png",
					Client.Client.SCREEN_WIDTH, Client.Client.SCREEN_HEIGHT,
					"Interface//"));

			addtoImageArray(new GameImage("ButtonTray.png",
					(int) (ClientFrame.getScaledWidth(360)),
					(int) (ClientFrame.getScaledHeight(400)), "Interface//"));
			addtoImageArray(new GameImage("FindAGame.png",
					(int) (ClientFrame.getScaledWidth(360)),
					(int) (ClientFrame.getScaledHeight(120)), "Interface//"));
			addtoImageArray(new GameImage("FindAGameClicked.png",
					(int) (ClientFrame.getScaledWidth(360)),
					(int) (ClientFrame.getScaledHeight(120)), "Interface//"));
			addtoImageArray(new GameImage("CreateAServer.png",
					(int) (ClientFrame.getScaledWidth(268)),
					(int) (ClientFrame.getScaledHeight(77)), "Interface//"));
			addtoImageArray(new GameImage("CreateAServerClicked.png",
					(int) (ClientFrame.getScaledWidth(268)),
					(int) (ClientFrame.getScaledHeight(77)), "Interface//"));
			addtoImageArray(new GameImage("CreateAMap.png",
					(int) (ClientFrame.getScaledWidth(360)),
					(int) (ClientFrame.getScaledHeight(120)), "Interface//"));
			addtoImageArray(new GameImage("CreateAMapClicked.png",
					(int) (ClientFrame.getScaledWidth(360)),
					(int) (ClientFrame.getScaledHeight(120)), "Interface//"));
			addtoImageArray(new GameImage("Instructions.png",
					(int) (ClientFrame.getScaledWidth(360)),
					(int) (ClientFrame.getScaledHeight(120)), "Interface//"));
			addtoImageArray(new GameImage("Leaderboards.png",
					(int) (ClientFrame.getScaledWidth(360)),
					(int) (ClientFrame.getScaledHeight(120)), "Interface//"));
			addtoImageArray(new GameImage("LeaderboardsClicked.png",
					(int) (ClientFrame.getScaledWidth(360)),
					(int) (ClientFrame.getScaledHeight(120)), "Interface//"));
			addtoImageArray(new GameImage("InstructionsClicked.png",
					(int) (ClientFrame.getScaledWidth(360)),
					(int) (ClientFrame.getScaledHeight(120)), "Interface//"));
			addtoImageArray(new GameImage("Exit.png",
					(int) (ClientFrame.getScaledWidth(360)),
					(int) (ClientFrame.getScaledHeight(120)), "Interface//"));
			addtoImageArray(new GameImage("ExitClicked.png",
					(int) (ClientFrame.getScaledWidth(360)),
					(int) (ClientFrame.getScaledHeight(120)), "Interface//"));
			addtoImageArray(new GameImage("Login.png",
					(int) (ClientFrame.getScaledWidth(180)),
					(int) (ClientFrame.getScaledHeight(60)), "Interface//"));
			addtoImageArray(new GameImage("LoginClicked.png",
					(int) (ClientFrame.getScaledWidth(180)),
					(int) (ClientFrame.getScaledHeight(60)), "Interface//"));
			addtoImageArray(new GameImage("Logout.png",
					(int) (ClientFrame.getScaledWidth(180)),
					(int) (ClientFrame.getScaledHeight(60)), "Interface//"));
			addtoImageArray(new GameImage("LogoutClicked.png",
					(int) (ClientFrame.getScaledWidth(180)),
					(int) (ClientFrame.getScaledHeight(60)), "Interface//"));
			
			
			addtoImageArray(new GameImage("WorldQuestOnline.png",
					(int) (ClientFrame.getScaledWidth(1000)),
					(int) (ClientFrame.getScaledHeight(250)), "Interface//"));
			addtoImageArray(new GameImage("ProfileBackground.png",
					(int) (ClientFrame.getScaledWidth(360)),
					(int) (ClientFrame.getScaledHeight(300)), "Interface//"));
			addtoImageArray(new GameImage("nameGlow.png",
					(int) (ClientFrame.getScaledWidth(360)),
					(int) (ClientFrame.getScaledHeight(200)), "Interface//"));
			
			addtoImageArray(new GameImage("Inventory.png",
					(int) (ClientFrame.getScaledWidth(300)),
					Client.Client.SCREEN_HEIGHT, "Interface//"));
			addtoImageArray(new GameImage("InventoryShadow.png",
					(int) (ClientFrame.getScaledWidth(100)),
					Client.Client.SCREEN_HEIGHT, "Interface//"));
			
			addtoImageArray(new GameImage("castleBarRed.png",
					500,
					40, "Interface//"));
			addtoImageArray(new GameImage("castleBarBlue.png",
					500,
					40, "Interface//"));
			
			addtoImageArray(new GameImage("scoreboard.png",
					(int) (ClientFrame.getScaledWidth(1366)),
					(int) (ClientFrame.getScaledHeight(768)), "Interface//"));

			addtoImageArray(new GameImage(
					"Shop.png",
					(int) (ClientFrame.getScaledWidth(ClientShop.SHOP_WIDTH)),
					(int) (ClientFrame.getScaledHeight(ClientShop.SHOP_HEIGHT)),
					"Interface//"));

			// Add the better tile images
			for (int no=0; no < 5; no++)
			{
				addtoImageArray(new GameImage("dirt_" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			for (int no=0; no < 4; no++)
			{
				addtoImageArray(new GameImage("dirt_bottom" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			for (int no=0; no < 2; no++)
			{
				addtoImageArray(new GameImage("dirt_edgeright" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			for (int no=0; no < 2; no++)
			{
				addtoImageArray(new GameImage("dirt_edgeleft" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			for (int no=0; no < 4; no++)
			{
				addtoImageArray(new GameImage("dirt_corner" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			addtoImageArray(new GameImage("dirt_left" + ".png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			addtoImageArray(new GameImage("dirt_right" + ".png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			
			for (int no=0; no < 4; no++)
			{
				addtoImageArray(new GameImage("dirt_top" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			for (int no=0; no < 5; no++)
			{
				addtoImageArray(new GameImage("bdirt_" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			for (int no=0; no < 3; no++)
			{
				addtoImageArray(new GameImage("bdirt_plat" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			for (int no=0; no < 3; no++)
			{
				addtoImageArray(new GameImage("sand_" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			for (int no=0; no < 2; no++)
			{
				addtoImageArray(new GameImage("sand_bottom" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			addtoImageArray(new GameImage("sand_right" + ".png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			addtoImageArray(new GameImage("sand_left" +  ".png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			
			for (int no=0; no < 4; no++)
			{
				addtoImageArray(new GameImage("sand_corner" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			for (int no=0; no < 2; no++)
			{
				addtoImageArray(new GameImage("sand_edgeright" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			for (int no=0; no < 2; no++)
			{
				addtoImageArray(new GameImage("sand_edgeleft" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			for (int no=0; no < 3; no++)
			{
				addtoImageArray(new GameImage("sand_top" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			for (int no=0; no < 3; no++)
			{
				addtoImageArray(new GameImage("stone_" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			for (int no=0; no < 2; no++)
			{
				addtoImageArray(new GameImage("stone_bottom" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			for (int no=0; no < 4; no++)
			{
				addtoImageArray(new GameImage("stone_corner" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			for (int no=0; no < 2; no++)
			{
				addtoImageArray(new GameImage("stone_edgeright" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			for (int no=0; no < 2; no++)
			{
				addtoImageArray(new GameImage("stone_edgeleft" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			for (int no=0; no < 3; no++)
			{
				addtoImageArray(new GameImage("stone_top" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			addtoImageArray(new GameImage("stone_left" + ".png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			
			addtoImageArray(new GameImage("stone_right" + ".png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			
			for (int no=0; no < 13; no++)
			{
				addtoImageArray(new GameImage("grass_" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			for (int no=0; no < 4; no++)
			{
				addtoImageArray(new GameImage("srocks_" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			for (int no=0; no < 6; no++)
			{
				addtoImageArray(new GameImage("drocks_" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			for (int no=0; no < 3; no++)
			{
				addtoImageArray(new GameImage("barrel_" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			for (int no=0; no < 7; no++)
			{
				addtoImageArray(new GameImage("tree_" + no + ".png", ServerWorld.TILE_SIZE,
						ServerWorld.TILE_SIZE, "Tiles//"));
			}
			
			addtoImageArray(new GameImage("skull" +".png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE, "Tiles//"));
			
			addtoImageArray(new GameImage("Upgrade.png", ClientCastleShopItem.WIDTH,ClientCastleShopItem.HEIGHT,"Interface//"));
			
			try {
				image = ImageIO.read(new File("Images//Buildings//" + "BARRACKS.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			addtoImageArray(new GameImage("BLUE_BARRACKS.png", image));
			addtoImageArray(new GameImage("RED_BARRACKS.png", image));
			addtoImageArray(new GameImage("BLUE_BARRACKS_ICON.png", image,(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE)));
			addtoImageArray(new GameImage("RED_BARRACKS_ICON.png", image,(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE)));
			addtoImageArray(new GameImage("BARRACKS_ICON.png", image,ClientCastleShopItem.WIDTH,ClientCastleShopItem.HEIGHT));
			addtoImageArray(new GameImage("BARRACKS.png",image));
			addtoImageArray(new GameImage("BARRACKS_INVALID.png","Buildings//"));
			addtoImageArray(new GameImage("BARRACKS_VALID.png","Buildings//"));
			
			try {
				image = ImageIO.read(new File("Images//Buildings//" + "TOWER.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			addtoImageArray(new GameImage("BLUE_TOWER.png", image));
			addtoImageArray(new GameImage("RED_TOWER.png", image));
			addtoImageArray(new GameImage("BLUE_TOWER_ICON.png", image,(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE)));
			addtoImageArray(new GameImage("RED_TOWER_ICON.png", image,(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE)));
			addtoImageArray(new GameImage("TOWER_ICON.png", image,ClientCastleShopItem.WIDTH,ClientCastleShopItem.HEIGHT));
			addtoImageArray(new GameImage("TOWER.png",image));
			addtoImageArray(new GameImage("TOWER_INVALID.png","Buildings//"));
			addtoImageArray(new GameImage("TOWER_VALID.png","Buildings//"));
			
			
			int height = image.getHeight();
			for (int i = 0; i < 20; i++)
			{
				image = image.getSubimage(0,0,image.getWidth(),image.getHeight()-(height/20));
				addtoImageArray(new GameImage("TOWER_"+i+".png",image));
			}
			
			
			try {
				image = ImageIO.read(new File("Images//Buildings//" + "BARRACKS.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			addtoImageArray(new GameImage("BARRACKS_ICON.png", image,ClientCastleShopItem.WIDTH,ClientCastleShopItem.HEIGHT));
			addtoImageArray(new GameImage("BARRACKS.png",image));
			addtoImageArray(new GameImage("BARRACKS_INVALID.png","Buildings//"));
			addtoImageArray(new GameImage("BARRACKS_VALID.png","Buildings//"));
			
			height = image.getHeight();
			for (int i = 0; i < 20; i++)
			{
				image = image.getSubimage(0,0,image.getWidth(),image.getHeight()-(height/20));
				addtoImageArray(new GameImage("BARRACKS_"+i+".png",image));
			}

			try {
				image = ImageIO.read(new File("Images//Buildings//" + "WOOD_HOUSE.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			addtoImageArray(new GameImage("BLUE_WOOD_HOUSE.png", image));
			addtoImageArray(new GameImage("RED_WOOD_HOUSE.png", image));
			addtoImageArray(new GameImage("BLUE_WOOD_HOUSE_ICON.png", image,(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE)));
			addtoImageArray(new GameImage("RED_WOOD_HOUSE_ICON.png", image,(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE)));
			addtoImageArray(new GameImage("WOOD_HOUSE_ICON.png", image,ClientCastleShopItem.WIDTH,ClientCastleShopItem.HEIGHT));
			addtoImageArray(new GameImage("WOOD_HOUSE.png",image));
			addtoImageArray(new GameImage("WOOD_HOUSE_INVALID.png","Buildings//"));
			addtoImageArray(new GameImage("WOOD_HOUSE_VALID.png","Buildings//"));
			
			height = image.getHeight();
			for (int i = 0; i < 20; i++)
			{
				image = image.getSubimage(0,0,image.getWidth(),image.getHeight()-(height/20));
				addtoImageArray(new GameImage("WOOD_HOUSE_"+i+".png",image));
			}
			
			try {
				image = ImageIO.read(new File("Images//Buildings//" + "GOLD_MINE.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			addtoImageArray(new GameImage("BLUE_GOLD_MINE.png", image));
			addtoImageArray(new GameImage("RED_GOLD_MINE.png", image));
			addtoImageArray(new GameImage("BLUE_GOLD_MINE_ICON.png", image,(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE)));
			addtoImageArray(new GameImage("RED_GOLD_MINE_ICON.png", image,(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE)));
			addtoImageArray(new GameImage("GOLD_MINE_ICON.png",image,ClientCastleShopItem.WIDTH,ClientCastleShopItem.HEIGHT));
			addtoImageArray(new GameImage("GOLD_MINE.png",image));
			addtoImageArray(new GameImage("GOLD_MINE_INVALID.png","Buildings//"));
			addtoImageArray(new GameImage("GOLD_MINE_VALID.png","Buildings//"));
			
			height = image.getHeight();
			for (int i = 0; i < 20; i++)
			{
				image = image.getSubimage(0,0,image.getWidth(),image.getHeight()-(height/20));
				addtoImageArray(new GameImage("GOLD_MINE_"+i+".png",image));
			}
			
			try {
				image = ImageIO.read(new File("Images//Buildings//" + "ADV_BARRACKS.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			addtoImageArray(new GameImage("BLUE_ADV_BARRACKS.png", image));
			addtoImageArray(new GameImage("RED_ADV_BARRACKS.png", image));
			addtoImageArray(new GameImage("BLUE_ADV_BARRACKS_ICON.png", image,(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE)));
			addtoImageArray(new GameImage("RED_ADV_BARRACKS_ICON.png", image,(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE)));
			addtoImageArray(new GameImage("ADV_BARRACKS_ICON.png", image,ClientCastleShopItem.WIDTH,ClientCastleShopItem.HEIGHT));
			addtoImageArray(new GameImage("ADV_BARRACKS.png",image));
			addtoImageArray(new GameImage("ADV_BARRACKS_INVALID.png","Buildings//"));
			addtoImageArray(new GameImage("ADV_BARRACKS_VALID.png","Buildings//"));
			
			height = image.getHeight();
			for (int i = 0; i < 20; i++)
			{
				image = image.getSubimage(0,0,image.getWidth(),image.getHeight()-(height/20));
				addtoImageArray(new GameImage("ADV_BARRACKS_"+i+".png",image));
			}
			
			try {
				image = ImageIO.read(new File("Images//Buildings//" + "GIANT_FACTORY.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			addtoImageArray(new GameImage("BLUE_GIANT_FACTORY.png", image));
			addtoImageArray(new GameImage("RED_GIANT_FACTORY.png", image));
			addtoImageArray(new GameImage("BLUE_GIANT_FACTORY_ICON.png", image,(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE)));
			addtoImageArray(new GameImage("RED_GIANT_FACTORY_ICON.png", image,(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE)));
			addtoImageArray(new GameImage("GIANT_FACTORY_ICON.png", image,ClientCastleShopItem.WIDTH,ClientCastleShopItem.HEIGHT));
			addtoImageArray(new GameImage("GIANT_FACTORY.png",image));
			addtoImageArray(new GameImage("GIANT_FACTORY_INVALID.png","Buildings//"));
			addtoImageArray(new GameImage("GIANT_FACTORY_VALID.png","Buildings//"));
			
			height = image.getHeight();
			for (int i = 0; i < 20; i++)
			{
				image = image.getSubimage(0,0,image.getWidth(),image.getHeight()-(height/20));
				addtoImageArray(new GameImage("GIANT_FACTORY_"+i+".png",image));
			}
			
			
			try {
				image = ImageIO.read(new File("Images//Buildings//" + "INN.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			addtoImageArray(new GameImage("BLUE_INN.png", image));
			addtoImageArray(new GameImage("RED_INN.png", image));
			addtoImageArray(new GameImage("BLUE_INN_ICON.png", image,(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE)));
			addtoImageArray(new GameImage("RED_INN_ICON.png", image,(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE),(int)(CreatorObject.SCALE * ServerWorld.TILE_SIZE)));
			addtoImageArray(new GameImage("INN_ICON.png", image,ClientCastleShopItem.WIDTH,ClientCastleShopItem.HEIGHT));
			addtoImageArray(new GameImage("INN.png",image));
			addtoImageArray(new GameImage("INN_INVALID.png","Buildings//"));
			addtoImageArray(new GameImage("INN_VALID.png","Buildings//"));
			
			height = image.getHeight();
			for (int i = 0; i < 20; i++)
			{
				image = image.getSubimage(0,0,image.getWidth(null),image.getHeight(null)-(height/20));
				addtoImageArray(new GameImage("INN_"+i+".png",image));
			}
			
			try {
				image = ImageIO.read(new File("Images//Buildings//" + "RED_CASTLE.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			addtoImageArray(new GameImage("RED_CASTLE.png",
					image));
			height = image.getHeight();
			for (int i = 0; i < 20; i++)
			{
				image = image.getSubimage(0,0,image.getWidth(),image.getHeight()-(height/20));
				addtoImageArray(new GameImage("RED_CASTLE_"+i+".png",image));
			}
			
			try {
				image = ImageIO.read(new File("Images//Buildings//" + "BLUE_CASTLE.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			addtoImageArray(new GameImage("BLUE_CASTLE.png",
					image));
			height = image.getHeight();
			for (int i = 0; i < 20; i++)
			{
				image = image.getSubimage(0,0,image.getWidth(),image.getHeight()-(height/20));
				addtoImageArray(new GameImage("BLUE_CASTLE_"+i+".png",image));
			}

			image = null;
		}

		// Create a correctly sized array of the game images
		GameImage[] clone = imageArray;
		imageArray = new GameImage[noOfImages];
		for (int no = 0; no < noOfImages; no++) {
			imageArray[no] = clone[no];
			imageMap.put(imageArray[no].getName(), no);
		}
	}

	/**
	 * Add a game image to the imageArray
	 * 
	 * @param image
	 */
	private static void addtoImageArray(GameImage gameImage) {
		imageArray[noOfImages] = gameImage;
		noOfImages++;

	}

	/**
	 * Get a specific image from the list using the name of the image
	 * 
	 * @param name
	 *            the name of the image
	 * @return the image (inside a game image) from the list
	 */
	public static Image getImage(String name) {
		return imageArray[imageMap.get(name)].getImage();
	}

	/**
	 * Get a specific game image from the list using the name of the image
	 * 
	 * @param name
	 *            the name of the image
	 * @return the game image from the list
	 */
	public static GameImage getGameImage(String name) {
		return imageArray[imageMap.get(name)];
	}

}