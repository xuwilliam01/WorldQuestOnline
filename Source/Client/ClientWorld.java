package Client;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import Imports.ImageReferencePair;
import Imports.Images;
import Server.ServerEngine;
import Server.ServerWorld;
import Server.Buildings.ServerCastle;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerPlayer;

public class ClientWorld {

	public static int NO_OF_CLOUDS = 8;
	public static int MAX_NO_OF_STARS = 500;

	/**
	 * The grid of background tiles
	 */
	private char[][] backgroundGrid;

	/**
	 * The grid of foreground tiles
	 */
	private char[][] foregroundGrid;

	/**
	 * Array of client objects, where the index of the object in the array is
	 * its ID
	 */
	private ClientObject[] objects=new ClientObject[ServerEngine.NUMBER_OF_IDS];
	
	/**
	 * List of objects to remove
	 */
	private ArrayList<ClientObject> objectsToRemove = new ArrayList<ClientObject>();

	/**
	 * Number of objects in the client
	 */
	private int noOfObjects;

	/**
	 * Arraylist of clouds on the client side
	 */
	private ArrayList<ClientCloud> clouds;

	/**
	 * The distance a cloud travels before teleporting back
	 */
	public final static int CLOUD_DISTANCE = Client.SCREEN_WIDTH * 5;

	/**
	 * The direction the clouds move in
	 */
	private int cloudDirection = 0;

	/**
	 * The font for damage indicators
	 */
	public final static Font DAMAGE_FONT = new Font("Courier", Font.BOLD, 18);

	/**
	 * The font for damage indicators
	 */
	public final static Font PLAYER_TEXT_FONT = new Font("Consolas", Font.BOLD,
			16);

	/**
	 * The font for messages
	 */
	public final static Font MESSAGE_FONT = new Font("Arial", Font.BOLD, 16);

	// /**
	// * The font for messages
	// */
	// public final static Font BOLD_MESSAGE_FONT = new Font("Arial", Font.BOLD,
	// 16);

	/**
	 * The normal font for text
	 */
	public final static Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 12);

	/**
	 * The bold normal font for text
	 */
	public final static Font BOLD_NORMAL_FONT = new Font("Arial", Font.BOLD, 12);

	/**
	 * Bigger normal font
	 */
	public final static Font BIG_NORMAL_FONT = new Font("Arial", Font.PLAIN,
			ClientFrame.getScaledWidth(20));

	/**
	 * Team Title normal font
	 */
	public final static Font TEAM_TITLE_FONT = new Font("Arial", Font.BOLD,
			ClientFrame.getScaledWidth(36));

	/**
	 * Player name font
	 */
	public final static Font PLAYER_NAME_FONT = new Font("Arial", Font.PLAIN,
			ClientFrame.getScaledWidth(20));

	/**
	 * Font for displaying stats
	 */
	public final static Font STATS_FONT = new Font("Courier", Font.PLAIN,
			ClientFrame.getScaledWidth(15));

	/**
	 * Object for figuring out size of font
	 */
	private FontMetrics fontMetrics;

	/**
	 * The width in pixels of one character for the damage font
	 */
	public static double DAMAGE_FONT_WIDTH = 0;

	/**
	 * The width in pixels of one character for the player text font
	 */
	public static double PLAYER_TEXT_FONT_WIDTH = 0;

	/**
	 * The width in pixels of one character for the message font
	 */
	public static double MESSAGE_FONT_WIDTH = 0;

	/**
	 * Background colour image
	 */
	private Image backgroundColour;

	/**
	 * The time in the world
	 */
	public static int worldTime;

	/**
	 * Center of the screen
	 */
	int centreX;
	int centreY;

	private ClientHologram hologram = null;
	
	public final static int MAX_NO_OF_TEXT = Integer.MAX_VALUE;

	/**
	 * Adjusts the alpha of the darkness
	 */
	private double alphaMultiplier;

	private ArrayList<ClientStar> stars;

	private Client client;

	public static final int corner0 = 0;
	public static final int corner0WithSky = 1;
	public static final int corner1 = 2;
	public static final int corner1WithSky = 3;
	public static final int corner2 = 4;
	public static final int corner3 = 5;
	public static final int bottom = 6;
	public static final int top = 7;
	public static final int topWithSky = 8;
	public static final int right = 9;
	public static final int left = 10;
	public static final int middle = 11;
	public static final int edgeright = 12;
	public static final int edgeleft = 13;

	/**
	 * Constructor for the client's side of the world
	 * 
	 * @param rows
	 *            the number of rows in the tile grid
	 * @param columns
	 *            the number of columns in the tile grid
	 * @param grid
	 *            the tile grid
	 * @throws IOException
	 */
	public ClientWorld(char[][] grid, int tileSize, Client client)
			throws IOException {
		
		backgroundGrid = new char[grid.length][grid[0].length];
		foregroundGrid = new char[grid.length][grid[0].length];
		
		// Create a background and foreground grid
		for (int row = 0; row < grid.length; row++) {
			for (int column = 0; column < grid[0].length; column++) {
				
				// Invisible solid tiles
				if (grid[row][column] == '_') {
					grid[row][column] = ' ';
				}
				
				if (grid[row][column] < 'A') {
					backgroundGrid[row][column] = grid[row][column];
					foregroundGrid[row][column] = ' ';

					switch (backgroundGrid[row][column]) {
					case '0':
						backgroundGrid[row][column] = (char) (250 + (int) (Math
								.random() * 5));
						break;
					case '+':
						backgroundGrid[row][column] = (char) (255 + (int) (Math
								.random() * 3));
					}

				} else {
					foregroundGrid[row][column] = grid[row][column];
					backgroundGrid[row][column] = ' ';
				}
			}
		}

		// Change the tiles to the modified ones (based on location)
		char newGrid[][] = new char[grid.length][grid[0].length];

		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				newGrid[row][col] = foregroundGrid[row][col];

				switch (newGrid[row][col]) {

				// Dirt tile
				case 'D':
					int situation = checkTileSituation(row, col, grid);

					switch (situation) {
					case corner0:
						newGrid[row][col] = (char) 135; // dirt_corner0
						if (Math.random() > 0.9) {
							newGrid[row - 1][col] = (char) (193 + (int) (Math
									.random() * 6)); // drocks
						}
						break;
					case corner0WithSky:
						newGrid[row][col] = (char) 135; // dirt_corner0
						newGrid[row - 1][col] = (char) 178; // grass_5
						break;
					case corner1:
						newGrid[row][col] = (char) 136; // dirt_corner1
						if (Math.random() > 0.9) {
							newGrid[row - 1][col] = (char) (193 + (int) (Math
									.random() * 6)); // drocks
						}
						break;
					case corner1WithSky:
						newGrid[row][col] = (char) 136; // dirt_corner1
						newGrid[row - 1][col] = (char) 177; // grass_4
						break;
					case corner2:
						newGrid[row][col] = (char) 137; // dirt_corner2
						break;
					case corner3:
						newGrid[row][col] = (char) 138; // dirt_corner3
						break;
					case edgeright:
						newGrid[row - 1][col] = (char) 177; // grass_4
						newGrid[row][col] = (char) (302 + (int) (Math.random() * 2)); // dirt_edgeright
						break;
					case edgeleft:
						newGrid[row - 1][col] = (char) 178; // grass_5
						newGrid[row][col] = (char) (304 + (int) (Math.random() * 2)); // dirt_edgeleft
						break;
					case top:
						if (Math.random() > 0.05) {
							newGrid[row][col] = (char) (141 + (int) (Math
									.random() * 2)); // dirt_top 0-1
							if (Math.random() > 0.9) {
								newGrid[row - 1][col] = (char) (193 + (int) (Math
										.random() * 6)); // drocks
							}
						} else {
							newGrid[row][col] = (char) (143 + (int) (Math
									.random() * 2)); // dirt_top 2-3
						}

						break;
					case topWithSky:
						if (Math.random() > 0.05) {
							newGrid[row][col] = (char) (141 + (int) (Math
									.random() * 2)); // dirt_top 0-1
							double random = Math.random();
							if (random > 0.10) {
								newGrid[row - 1][col] = (char) (173 + (int) (Math
										.random() * 4)); // grass 0-3
							} else if (random > 0.05) {
								newGrid[row - 1][col] = (char) (179 + (int) (Math
										.random() * 3)); // grass 6-8
							} else if (random > 0.025) {
								newGrid[row - 1][col] = (char) (182 + (int) (Math
										.random() * 4)); // grass 9-12
							} else {
								newGrid[row - 1][col] = (char) (202 + (int) (Math
										.random() * 5)); // tree
							}
						} else {
							newGrid[row][col] = (char) (143 + (int) (Math
									.random() * 2)); // dirt_top 2-3
						}
						break;
					case bottom:
						if (Math.random() > 0.5) {
							newGrid[row][col] = (char) (133 + (int) (Math
									.random() * 2)); // dirt_bottom 0-1
						} else {
							newGrid[row][col] = (char) (300 + (int) (Math
									.random() * 2)); // dirt_bottom 2-3
						}
						break;
					case right:
						newGrid[row][col] = (char) 140; // dirt_right
						break;
					case left:
						newGrid[row][col] = (char) 139; // dirt_left
						break;
					case middle:
						newGrid[row][col] = (char) (128 + (int) (Math.random() * 5)); // dirt_middle
						break;
					}
					break;

				// Sand tile
				case 'S':
					int situation2 = checkTileSituation(row, col, grid);

					switch (situation2) {
					case corner0:
						newGrid[row][col] = (char) 150; // sand_corner0
						if (Math.random() > 0.99) {
							newGrid[row - 1][col] = (char) 209; // skull
						}
						break;
					case corner0WithSky:
						newGrid[row][col] = (char) 150; // sand_corner0

						double random = Math.random();
						if (random > 0.97) {
							if (random < 0.98) {
								newGrid[row - 1][col] = (char) 209; // skull
							} else {
								newGrid[row - 1][col] = (char) (207 + (int) (Math
										.random() * 2)); // tree 5-6
							}
						}
						break;
					case corner1:
						newGrid[row][col] = (char) 151; // sand_corner1
						if (Math.random() > 0.99) {
							newGrid[row - 1][col] = (char) 209; // skull
						}
						break;
					case corner1WithSky:
						newGrid[row][col] = (char) 151; // sand_corner1
						double random1 = Math.random();
						if (random1 > 0.97) {
							if (random1 < 0.98) {
								newGrid[row - 1][col] = (char) 209; // skull
							} else {
								newGrid[row - 1][col] = (char) (207 + (int) (Math
										.random() * 2)); // tree 5-6
							}
						}
						break;
					case corner2:
						newGrid[row][col] = (char) 152; // sand_corner2
						break;
					case corner3:
						newGrid[row][col] = (char) 153; // sand_corner3
						break;
					case edgeright:
						if (Math.random() > 0.25) {
							newGrid[row][col] = (char) 400; // sand_edgeright0
						} else {
							newGrid[row][col] = (char) 401; // sand_edgeright1
						}
						break;
					case edgeleft:
						if (Math.random() > 0.25) {
							newGrid[row][col] = (char) 402; // sand_edgeleft0
						} else {
							newGrid[row][col] = (char) 403; // sand_edgeleft1
						}
						break;
					case top:
						if (Math.random() > 0.02) {
							newGrid[row][col] = (char) (156 + (int) (Math
									.random() * 2)); // sand_top 0-1
							if (Math.random() > 0.99) {
								newGrid[row - 1][col] = (char) 209; // skull
							}
						} else {
							newGrid[row][col] = (char) 158; // sand_top 2
						}

						break;
					case topWithSky:
						if (Math.random() > 0.02) {
							newGrid[row][col] = (char) (156 + (int) (Math
									.random() * 2)); // sand_top 0-1
							double random2 = Math.random();
							if (random2 > 0.97) {
								if (random2 < 0.98) {
									newGrid[row - 1][col] = (char) 209; // skull
								} else {
									newGrid[row - 1][col] = (char) (207 + (int) (Math
											.random() * 2)); // tree 5-6
								}
							}
						} else {
							newGrid[row][col] = (char) 158; // sand_top 2
						}

						break;
					case bottom:
						newGrid[row][col] = (char) (148 + (int) (Math.random() * 2)); // sand_bottom
						break;
					case right:
						newGrid[row][col] = (char) 155; // sand_right
						break;
					case left:
						newGrid[row][col] = (char) 154; // sand_left
						break;
					case middle:
						newGrid[row][col] = (char) (145 + (int) (Math.random() * 3)); // sand_middle
						break;
					}

					break;

				// Stone tile
				case 'E':
					int situation3 = checkTileSituation(row, col, grid);

					switch (situation3) {
					case corner0:
					case corner0WithSky:
						newGrid[row][col] = (char) 164; // stone_corner0
						if (Math.random() > 0.92) {
							newGrid[row - 1][col] = (char) (189 + (int) (Math
									.random() * 4)); // srocks
						}
						break;
					case corner1:
					case corner1WithSky:
						newGrid[row][col] = (char) 165; // stone_corner1
						if (Math.random() > 0.92) {
							newGrid[row - 1][col] = (char) (189 + (int) (Math
									.random() * 4)); // srocks
						}
						break;
					case corner2:
						newGrid[row][col] = (char) 166; // stone_corner2
						break;
					case corner3:
						newGrid[row][col] = (char) 167; // stone_corner3
						break;
					case edgeright:
						if (Math.random() > 0.25) {
							newGrid[row][col] = (char) 500; // stone_edgeright0
						} else {
							newGrid[row][col] = (char) 501; // stone_edgeright1
						}
						break;
					case edgeleft:
						if (Math.random() > 0.25) {
							newGrid[row][col] = (char) 502; // stone_edgeleft0
						} else {
							newGrid[row][col] = (char) 503; // stone_edgeleft1
						}
						break;
					case top:
					case topWithSky:
						if (Math.random() > 0.02) {
							newGrid[row][col] = (char) (170 + (int) (Math
									.random() * 2)); // stone_top 0-1
							if (Math.random() > 0.92) {
								newGrid[row - 1][col] = (char) (189 + (int) (Math
										.random() * 4)); // srocks
							}
						} else {
							newGrid[row][col] = (char) 172; // stone_top 2
						}
						break;
					case bottom:
						newGrid[row][col] = (char) (162 + (int) (Math.random() * 2)); // stone_bottom
						break;
					case right:
						newGrid[row][col] = (char) 169; // stone_right
						break;
					case left:
						newGrid[row][col] = (char) 168; // stone_left
						break;
					case middle:
						newGrid[row][col] = (char) (159 + (int) (Math.random() * 3)); // stone_middle
						break;
					}

					break;
				}
			}
		}

		// Copy the new and improved tiles to the foreground grid
		foregroundGrid = newGrid;

		this.client = client;
		alphaMultiplier = 0;
		worldTime = 0;

		// Import tile drawing referenes
		ImageReferencePair.importReferences();

		backgroundColour = Images.getImage("BACKGROUND");

		// Generate clouds
		if ((int) (Math.random() * 2) == 0) {
			cloudDirection = 1;
		} else {
			cloudDirection = -1;
		}

		clouds = new ArrayList<ClientCloud>();
		for (int no = 0; no < NO_OF_CLOUDS; no++) {
			double x = Client.SCREEN_WIDTH / 2 + Math.random() * CLOUD_DISTANCE
					- (CLOUD_DISTANCE / 2);
			double y = Math.random() * (Client.SCREEN_HEIGHT)
					- (Client.SCREEN_HEIGHT / 3);

			double hSpeed = 0;

			hSpeed = (Math.random() * 0.9 + 0.1) * cloudDirection;

			int imageNo = no;

			while (imageNo >= 6) {
				imageNo -= 6;
			}

			String image = "CLOUD_" + imageNo + "";

			clouds.add(new ClientCloud(x, y, hSpeed, 0, image));
		}

		stars = new ArrayList<ClientStar>();

		centreX = Client.SCREEN_WIDTH / 2 - ServerPlayer.DEFAULT_WIDTH / 2;
		centreY = Client.SCREEN_HEIGHT / 2 - ServerPlayer.DEFAULT_HEIGHT / 2;

	}
	
	/**
	 * Check the neighbours of a tile on the grid
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	private int checkTileSituation(int row, int col, char[][] grid) {
		if (row + 1 < grid.length && foregroundGrid[row + 1][col] == ' ') {

			// Fill in background if there are empty spaces in foreground
			backgroundGrid[row][col] = backgroundGrid[row + 1][col];
			if (col + 1 < grid[0].length && foregroundGrid[row][col + 1] == ' ') {

				if (backgroundGrid[row][col + 1] != ' ') {
					backgroundGrid[row][col] = backgroundGrid[row][col + 1];
				}

				if (row - 1 >= 0 && foregroundGrid[row - 1][col] == ' ') {
					return edgeright;
				}

				return corner2;
			} else if (col - 1 >= 0 && foregroundGrid[row][col - 1] == ' ') {

				if (backgroundGrid[row][col - 1] != ' ') {
					backgroundGrid[row][col] = backgroundGrid[row][col - 1];
				}

				if (row - 1 >= 0 && foregroundGrid[row - 1][col] == ' ') {
					return edgeleft;
				}

				return corner3;
			} else {
				return bottom;
			}
		} else if (row - 1 >= 0 && foregroundGrid[row - 1][col] == ' ') {

			// Fill in background if there are empty spaces in foreground
			backgroundGrid[row][col] = backgroundGrid[row - 1][col];

			if (col + 1 < grid[0].length && foregroundGrid[row][col + 1] == ' ') {

				if (backgroundGrid[row][col + 1] != ' ') {
					backgroundGrid[row][col] = backgroundGrid[row][col + 1];
				}

				if (backgroundGrid[row - 1][col] == ' ') {
					return corner1WithSky;
				}

				return corner1;
			} else if (col - 1 >= 0 && foregroundGrid[row][col - 1] == ' ') {

				if (backgroundGrid[row][col - 1] != ' ') {
					backgroundGrid[row][col] = backgroundGrid[row][col - 1];
				}

				if (backgroundGrid[row - 1][col] == ' ') {
					return corner0WithSky;
				}

				return corner0;
			} else {
				if (backgroundGrid[row - 1][col] == ' ') {
					return topWithSky;
				}

				return top;
			}
		} else {
			if (col + 1 < grid[0].length && foregroundGrid[row][col + 1] == ' ') {

				// Fill in background if there are empty spaces in foreground
				backgroundGrid[row][col] = backgroundGrid[row][col + 1];

				return right;
			} else if (col - 1 >= 0 && foregroundGrid[row][col - 1] == ' ') {
				// Fill in background if there are empty spaces in foreground
				backgroundGrid[row][col] = backgroundGrid[row][col - 1];

				return left;
			} else {
				return middle;
			}
		}
	}

	

	/**
	 * Get a specific object from the list
	 * 
	 * @return the desired object
	 */
	public ClientObject get(int id) {
		return objects[id];
	}

	/**
	 * Add an object (not a tile) to the client, or update it if it already
	 * exists
	 * 
	 * @param type
	 * @param object
	 *            the object to add
	 */
	public void setObject(int id, int x, int y, String image, int team,
			String type, String name) {
		try {
			if (objects[id] == null) {
				if (name.equals("{")) {
					objects[id] = new ClientObject(id, x, y, image, team, type);
				} else {
					objects[id] = new ClientObject(id, x, y, image, team, type,
							name);
				}
				addObjectNo();
			} else {
				objects[id].setX(x);
				objects[id].setY(y);
				objects[id].setTeam(team);
				objects[id].setImage(image);
				if (name != null && name.length()>0) {
					objects[id].setName(name);
				}
				objects[id].setLastCounter(worldTime);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			System.out.println(id + " " + name + " " + type + " " + image);
		} catch (NullPointerException e2)
		{
			e2.printStackTrace();
			System.out.println(id + " " + name + " " + type + " " + image);
		}
	}

	/**
	 * Add an object (not a tile) to the client, or update it if it already
	 * exists
	 * 
	 * @param object
	 *            the object to add
	 */
	public void setObject(ClientObject object) {
		if (objects[object.getID()] == null) {
			addObjectNo();
		}
		objects[object.getID()] = object;

	}

	/**
	 * Remove an object from the client's side
	 * 
	 * @param object
	 *            the object to remove
	 */
	public void remove(int id) {
		try
		{
		if (objects[id]!=null)
		{
			subtractObjectNo();
		}
		objects[id] = null;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Draws the world
	 * 
	 * @param graphics
	 *            the graphics component to be drawn on
	 * @param playerX
	 *            the position of the player
	 * @param playerY
	 *            the position of the player
	 */
	public void update(Graphics graphics, ClientObject player) {

		int playerX = player.getX();
		int playerY = player.getY();
		// Get font metrics
		if (fontMetrics == null) {
			fontMetrics = graphics.getFontMetrics(DAMAGE_FONT);
			DAMAGE_FONT_WIDTH = fontMetrics.stringWidth("0");
			fontMetrics = graphics.getFontMetrics(MESSAGE_FONT);
			MESSAGE_FONT_WIDTH = fontMetrics.stringWidth("0");
			fontMetrics = graphics.getFontMetrics(PLAYER_TEXT_FONT);
			PLAYER_TEXT_FONT_WIDTH = fontMetrics.stringWidth("0");
		}

		// Draw the background
		graphics.drawImage(backgroundColour, 0, 0, null);

		// Adjust the time of day
		if (worldTime >= 0 && worldTime < ServerWorld.DAY_COUNTERS / 3) {
			alphaMultiplier = 0;
		} else if (worldTime >= ServerWorld.DAY_COUNTERS / 3
				&& worldTime < ServerWorld.DAY_COUNTERS / 2) {
			alphaMultiplier = (worldTime - ServerWorld.DAY_COUNTERS / 3)
					* 0.92
					/ (ServerWorld.DAY_COUNTERS / 2 - ServerWorld.DAY_COUNTERS / 3.0);
		} else if (worldTime >= ServerWorld.DAY_COUNTERS / 2
				&& worldTime < ServerWorld.DAY_COUNTERS / 6 * 5) {
			alphaMultiplier = 0.92;
		} else if (worldTime >= ServerWorld.DAY_COUNTERS / 6 * 5) {
			alphaMultiplier = 0.92
					- (worldTime - ServerWorld.DAY_COUNTERS / 6 * 5)
					* 0.92
					/ (ServerWorld.DAY_COUNTERS - ServerWorld.DAY_COUNTERS / 6 * 5.0);
		}

		graphics.setColor(Images.blacks[Math.min(254, (int)(alphaMultiplier*255))]);
		graphics.fillRect(0, 0, Client.SCREEN_WIDTH, Client.SCREEN_HEIGHT);

		// Add stars when dusk begins
		if (stars.isEmpty() && worldTime >= ServerWorld.DAY_COUNTERS / 3
				&& worldTime < ServerWorld.DAY_COUNTERS / 2) {

			int noOfStars = (int) (Math.random() * MAX_NO_OF_STARS);
			for (int no = 0; no < noOfStars; no++) {
				stars.add(new ClientStar());
			}
		}

		ArrayList<ClientStar> removeStars = new ArrayList<ClientStar>();

		for (ClientStar star : stars) {
			if (star.exists()) {
				if (star.getAlpha() > 0) {

					graphics.setColor(Images.whites[Math.min(254,(int)(star.getAlpha()*255))]);
					graphics.fillRect(star.getX(), star.getY(), star.getSize(),
							star.getSize());
				}
				star.update();
			} else {
				removeStars.add(star);
			}
		}

		for (ClientStar star : removeStars) {
			stars.remove(star);
		}

		// Draw and move the clouds
		for (ClientCloud cloud : clouds) {
			if (cloud.getX() <= Client.SCREEN_WIDTH
					&& cloud.getX() + cloud.getWidth() >= 0
					&& cloud.getY() <= Client.SCREEN_HEIGHT
					&& cloud.getY() + cloud.getHeight() >= 0) {
				graphics.drawImage(cloud.getImage(), (int) cloud.getX(),
						(int) cloud.getY(), null);
			}

			if (cloud.getX() < Client.SCREEN_WIDTH / 2 - CLOUD_DISTANCE / 2) {
				cloud.setX(Client.SCREEN_WIDTH / 2 + CLOUD_DISTANCE / 2);
				cloud.setY(Math.random() * (Client.SCREEN_HEIGHT)
						- (Client.SCREEN_HEIGHT / 3));
				cloud.sethSpeed((Math.random() * 0.8 + 0.2) * cloudDirection);

			} else if (cloud.getX() > Client.SCREEN_WIDTH / 2 + CLOUD_DISTANCE
					/ 2) {
				cloud.setX(Client.SCREEN_WIDTH / 2 - CLOUD_DISTANCE / 2);
				cloud.setY(Math.random() * (Client.SCREEN_HEIGHT)
						- (Client.SCREEN_HEIGHT / 3));
				cloud.sethSpeed((Math.random() * 0.8 + 0.2) * cloudDirection);
			}
			cloud.setX(cloud.getX() + cloud.gethSpeed());

			// System.out.println(cloud.getX());
		}

		// Draw tiles (draw based on player's position later)
		int startRow = (int) ((playerY - Client.SCREEN_HEIGHT / 2 - ServerPlayer.DEFAULT_HEIGHT) / ServerWorld.TILE_SIZE);
		if (startRow < 0) {
			startRow = 0;
		}
		int endRow = (int) ((Client.SCREEN_HEIGHT / 2 + playerY + ServerPlayer.DEFAULT_HEIGHT) / ServerWorld.TILE_SIZE);
		if (endRow >= backgroundGrid.length) {
			endRow = backgroundGrid.length - 1;
		}
		int startColumn = (int) ((playerX - Client.SCREEN_WIDTH / 2 - ServerPlayer.DEFAULT_WIDTH) / ServerWorld.TILE_SIZE);
		if (startColumn < 0) {
			startColumn = 0;
		}
		int endColumn = (int) ((Client.SCREEN_WIDTH / 2 + playerX + ServerPlayer.DEFAULT_WIDTH) / ServerWorld.TILE_SIZE);
		if (endColumn >= backgroundGrid[0].length) {
			endColumn = backgroundGrid[0].length - 1;
		}

		// Draw background tiles
		for (int row = startRow; row <= endRow; row++) {
			for (int column = startColumn; column <= endColumn; column++) {

				if (backgroundGrid[row][column] != ' ') {
					graphics.drawImage(
							ImageReferencePair.getImages()[(int) (backgroundGrid[row][column])]
									.getImage(), centreX + column
									* ServerWorld.TILE_SIZE - playerX, centreY
									+ row * ServerWorld.TILE_SIZE - playerY,
							null);
				}
			}

		}

		// The text to display to the player in the centre of the screen
		String displayedText = null;

		int objectNo = 0;

		// Go through each object in the world and draw it relative to the
		// player's position. If it is outside of the screen, don't draw it just
		// remove it
		try {
			for (ClientObject object : objects) {
				if (object == null) {
					continue;
				}
				if (objectNo == noOfObjects) {
					break;
				}
				objectNo++;

				int x = centreX + object.getX() - playerX;
				int y = centreY + object.getY() - playerY;

				if (object.getID() == player.getID()) {
					player.setX(object.getX());
					player.setY(object.getY());
					x = centreX;
					y = centreY;
				}
				else if (object.getX() == player.getX() && object.getY() == player.getY())
				{
					x = centreX;
					y = centreY;
				}
				

				if (object.getType().equals(ServerWorld.TEXT_TYPE + "")) {
					ClientText textObject = (ClientText) object;

					textObject.updateText();
					if (textObject.exists())
					{
					graphics.setColor(textObject.getColor());
					graphics.setFont(DAMAGE_FONT);
					graphics.drawString(textObject.getText(), x, y);
					}
					// System.out.println("Drawing floating text");
				} else {
					
					if (x > Client.SCREEN_WIDTH || x + object.getWidth() < 0
							|| y > Client.SCREEN_HEIGHT
							|| y + object.getHeight() < 0 || Math.abs(object.getLastCounter()-worldTime)>2) // If the object wasn't present in the last update
					{
						objectsToRemove.add(object);
						continue;
					}
					
					Image image = object.getImage();

					switch (object.getTeam()) {
					case ServerCreature.RED_TEAM:
						graphics.setColor(Color.red);
						break;
					case ServerCreature.BLUE_TEAM:
						graphics.setColor(Color.blue);
						break;
					}
					graphics.setFont(DAMAGE_FONT);

					if (object.getTeam() != ServerCreature.NEUTRAL) {
						if (object.getName().equals("")) {
							graphics.fillRect(x + object.getWidth() / 2 - 5, y
									+ object.getHeight() / 4, 10, 10);
						} else {
							if (object.getType()
									.equals(ServerWorld.PLAYER_TYPE)) {

								// System.out.println(object.getName());
								String[] tokens = object.getName().split("`");
								String name = tokens[0];
								graphics.drawString(name,
										(int) (x + object.getWidth() / 2 - name
												.trim().length()
												* DAMAGE_FONT_WIDTH / 2),
										y + 15);

								if (tokens.length > 1) {
									String currentText = tokens[1];

									graphics.setColor(Color.YELLOW);

									graphics.drawString(
											currentText,
											(int) (x + object.getWidth() / 2 - currentText
													.length()
													* DAMAGE_FONT_WIDTH / 2),
											y - 7);
								}
							} else {
								graphics.drawString(object.getName(), (int) (x
										+ object.getWidth() / 2 - object
										.getName().trim().length()
										* DAMAGE_FONT_WIDTH / 2), y + 15);
							}
						}
					}

					graphics.drawImage(image, x, y, null);

					// Draw a hint if necessary
					// DOES NOT DRAW OVER SOLID TILES
					if (player.collidesWith(object)
							&& !object.getHint().equals("")) {
						displayedText = object.getHint();
					}
				}
			}

			for (ClientObject object : objectsToRemove) {
				object.destroy();
				remove(object.getID());
			}
			objectsToRemove.clear();
		} catch (ConcurrentModificationException E) {
			System.out
					.println("Tried to access the object list while it was being used");
		}

		// Draw solid tiles at the very front
		for (int row = startRow; row <= endRow; row++) {
			for (int column = startColumn; column <= endColumn; column++) {

				if (foregroundGrid[row][column] != ' ') {
					graphics.drawImage(
							ImageReferencePair.getImages()[(int) (foregroundGrid[row][column])]
									.getImage(), centreX + column
									* ServerWorld.TILE_SIZE - playerX, centreY
									+ row * ServerWorld.TILE_SIZE - playerY,
							null);
				}
			}

		}

		if (displayedText != null) {
			graphics.setColor(Images.PURPLE);
			graphics.setFont(MESSAGE_FONT);
			graphics.drawString(displayedText,
					(int) (Client.SCREEN_WIDTH / 2
							- (displayedText.length() * MESSAGE_FONT_WIDTH)
							* (2.0 / 5) + 0.5), Client.SCREEN_HEIGHT / 3);
		}

		//Draw the hologram if it exists
		if(hologram != null)
			graphics.drawImage(hologram.getImage(), hologram.getX() - hologram.getImage().getWidth(null)/2, hologram.getY() - hologram.getImage().getHeight(null)/2, null);
		
		// Draw the castle hp bars
		graphics.setFont(NORMAL_FONT);
		graphics.setColor(Color.CYAN);
		graphics.fillRect(ClientFrame.getScaledWidth(100), ClientFrame
				.getScaledHeight(980),
				ClientFrame.getScaledWidth((int) (500.0 * client
						.getBlueCastleHP() / (client.getBlueCastleMaxHP()))),
				ClientFrame.getScaledHeight(20));
		graphics.setColor(Color.PINK);
		graphics.fillRect(ClientFrame.getScaledWidth(1050), ClientFrame
				.getScaledHeight(980),
				ClientFrame.getScaledWidth((int) (500.0 * client
						.getRedCastleHP() / (client.getRedCastleMaxHP()))),
				ClientFrame.getScaledHeight(20));

		graphics.setColor(Images.PURPLE);
		graphics.drawRect(ClientFrame.getScaledWidth(100),
				ClientFrame.getScaledHeight(980),
				ClientFrame.getScaledWidth(500),
				ClientFrame.getScaledHeight(20));
		graphics.drawString(
				String.format("%d/%d", client.getBlueCastleHP(),
						client.getBlueCastleMaxHP()),
				ClientFrame.getScaledWidth(325),
				ClientFrame.getScaledHeight(995));
		graphics.drawRect(ClientFrame.getScaledWidth(1050),
				ClientFrame.getScaledHeight(980),
				ClientFrame.getScaledWidth(500),
				ClientFrame.getScaledHeight(20));
		graphics.drawString(
				String.format("%d/%d", client.getRedCastleHP(),
						client.getRedCastleMaxHP()),
				ClientFrame.getScaledWidth(1275),
				ClientFrame.getScaledHeight(995));

		graphics.setFont(BIG_NORMAL_FONT);

		graphics.setColor(Color.red);
		if (client.getRedCastleTier() == 5)
			graphics.drawString(
					String.format("Red Castle Tier %d (Max)",
							client.getRedCastleTier() + 1),
					ClientFrame.getScaledWidth(1050),
					ClientFrame.getScaledHeight(975));
		else
			graphics.drawString(String.format(
					"Red Castle Tier %d (Money For Next Tier  %d/%d)",
					client.getRedCastleTier() + 1, client.getRedCastleMoney(),
					ServerCastle.CASTLE_TIER_PRICE[client.getRedCastleTier()]),
					ClientFrame.getScaledWidth(1050), ClientFrame
							.getScaledHeight(975));

		graphics.setColor(Color.blue);
		if (client.getBlueCastleTier() == 5)
			graphics.drawString(
					String.format("Blue Castle Tier %d (Max)",
							client.getBlueCastleTier() + 1),
					ClientFrame.getScaledWidth(100),
					ClientFrame.getScaledHeight(975));
		else
			graphics.drawString(
					String.format(
							"Blue Castle Tier %d (Money For Next Tier  %d/%d)",
							client.getBlueCastleTier() + 1, client
									.getBlueCastleMoney(),
							ServerCastle.CASTLE_TIER_PRICE[client
									.getBlueCastleTier()]), ClientFrame
							.getScaledWidth(100), ClientFrame
							.getScaledHeight(975));

		// for (int row = 0; row < Client.SCREEN_HEIGHT/16; row++)
		// {
		// for (int column = 0; column < Client.SCREEN_WIDTH*2/16; column++)
		// {
		// graphics.setColor(new Color(0, 0, 0, (float)(1f *0.5)));
		// graphics.fillRect(row*16, column*16, 16,16);
		// }
		// }
		
		//System.out.println(noOfObjects);
	}

	public void clear() {
		objects = new ClientObject[ServerEngine.NUMBER_OF_IDS];
	}

	public ClientObject[] getObjects() {
		return objects;
	}

	public double getAlphaMultiplier() {
		return alphaMultiplier;
	}

	public void setAlphaMultiplier(double alphaMultiplier) {
		this.alphaMultiplier = alphaMultiplier;
	}

	public int getWorldTime() {
		return worldTime;
	}

	public void setWorldTime(int worldTime) {
		this.worldTime = worldTime;
	}

	public void addToRemove(ClientObject object)
	{
		objectsToRemove.add(object);
	}
	
	public synchronized void addObjectNo()
	{
		noOfObjects++;
	}
	
	public synchronized void subtractObjectNo()
	{
		noOfObjects--;
	}

	public int getNoOfObjects() {
		return noOfObjects;
	}

	public void setNoOfObjects(int noOfObjects) {
		this.noOfObjects = noOfObjects;
	}
	
	public ClientHologram getHologram()
	{
		return hologram;
	}
	
	public void newHologram(int image, int x, int y)
	{
		hologram = new ClientHologram(image, x, y);
	}
	
	public void removeHologram()
	{
		hologram = null;
	}
	
	
}
