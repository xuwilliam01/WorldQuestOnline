package Client;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import Imports.ImageReferencePair;
import Imports.Images;
import Server.ServerEngine;
import Server.ServerWorld;
import Server.Creatures.ServerCastle;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerGoblin;
import Server.Creatures.ServerPlayer;
import Server.Effects.ServerDamageIndicator;

public class ClientWorld {

	public final static Color YELLOW_TEXT = new Color(204, 153, 0);
	public final static Color RED_TEXT = new Color(153, 0, 38);
	public final static Color BLUE_TEXT = new Color(0, 161, 230);
	public final static Color GREEN_TEXT = new Color(0, 153, 0);
	public final static Color PURPLE_TEXT = new Color(82, 42, 122);
	public final static Color GRAY_TEXT = Color.gray;

	public static int NO_OF_CLOUDS = 8;
	public static int MAX_NO_OF_STARS = 500;

	/**
	 * The grid of tiles
	 */
	private char[][] grid;

	/**
	 * Grid of light of tiles (0 = full dark, 10 = full light)
	 */
	private int[][] lightGrid;

	/**
	 * Grid of original light sources
	 */
	private int[][] sourceGrid;

	/**
	 * Grid of sun-exposed tiles;
	 */
	private boolean[][] exposedGrid;

	/**
	 * Array of client objects, where the index of the object in the array is
	 * its ID
	 */
	private ClientObject[] objects;

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
	 * The side length of one square tile in pixels
	 */
	private int tileSize;

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
	private int worldTime;

	// Center of the screen
	int centreX;
	int centreY;

	/**
	 * Adjusts the alpha of the darkness
	 */
	private double alphaMultiplier;

	private ArrayList<ClientStar> stars;

	private Client client;

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
		this.tileSize = tileSize;
		this.grid = grid;
		this.client = client;

		alphaMultiplier = 0;
		worldTime = 0;

		objects = new ClientObject[ServerEngine.NUMBER_OF_IDS];

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
		if (objects[id] == null) {
			if (name.equals("{")) {
				objects[id] = new ClientObject(id, x, y, image, team, type);
			} else {
				objects[id] = new ClientObject(id, x, y, image, team, type,
						name);
			}
			noOfObjects++;
		} else {
			objects[id].setX(x);
			objects[id].setY(y);
			objects[id].setTeam(team);
			objects[id].setImage(image);
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
		if (objects[object.getID()] != null) {
			noOfObjects++;
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
		objects[id] = null;
		noOfObjects--;
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
					* 0.95
					/ (ServerWorld.DAY_COUNTERS / 2 - ServerWorld.DAY_COUNTERS / 3.0);
		} else if (worldTime >= ServerWorld.DAY_COUNTERS / 2
				&& worldTime < ServerWorld.DAY_COUNTERS / 6 * 5) {
			alphaMultiplier = 0.95;
		} else if (worldTime >= ServerWorld.DAY_COUNTERS / 6 * 5) {
			alphaMultiplier = 0.95
					- (worldTime - ServerWorld.DAY_COUNTERS / 6 * 5)
					* 0.95
					/ (ServerWorld.DAY_COUNTERS - ServerWorld.DAY_COUNTERS / 6 * 5.0);
		}

		graphics.setColor(new Color(0, 0, 0, (float) (1f * alphaMultiplier)));
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

					graphics.setColor(new Color(1f, 1f, 1f, (float) (1f * star
							.getAlpha())));
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
		int startRow = (int) ((playerY - Client.SCREEN_HEIGHT / 2 - ServerPlayer.DEFAULT_HEIGHT) / tileSize);
		if (startRow < 0) {
			startRow = 0;
		}
		int endRow = (int) ((Client.SCREEN_HEIGHT / 2 + playerY + ServerPlayer.DEFAULT_HEIGHT) / tileSize);
		if (endRow >= grid.length) {
			endRow = grid.length - 1;
		}
		int startColumn = (int) ((playerX - Client.SCREEN_WIDTH / 2 - ServerPlayer.DEFAULT_WIDTH) / tileSize);
		if (startColumn < 0) {
			startColumn = 0;
		}
		int endColumn = (int) ((Client.SCREEN_WIDTH / 2 + playerX + ServerPlayer.DEFAULT_WIDTH) / tileSize);
		if (endColumn >= grid[0].length) {
			endColumn = grid[0].length - 1;
		}

		// Draw background tiles
		for (int row = startRow; row <= endRow; row++) {
			for (int column = startColumn; column <= endColumn; column++) {

				if (grid[row][column] < 'A' && grid[row][column] != ' ') {
					graphics.drawImage(
							ImageReferencePair.getImages()[(int) (grid[row][column])]
									.getImage(), centreX + column * tileSize
									- playerX, centreY + row * tileSize
									- playerY, null);
				}
			}

		}

		// Create a list of objects to remove after leaving the screen
		ArrayList<Integer> objectsToRemove = new ArrayList<Integer>();

		// The text to display to the player in the centre of the screen
		String displayedText = null;

		int objectNo = 0;

		// Go through each object in the world and draw it relative to the
		// player's position. If it is outside of the screen, don't draw it just
		// remove it
		try {
			for (ClientObject object : objects) {
				if (objectNo == noOfObjects) {
					break;
				}
				if (object == null) {
					continue;
				}
				objectNo++;

				int x = centreX + object.getX() - playerX;
				int y = centreY + object.getY() - playerY;

				if (object.getX() == player.getX()
						&& object.getY() == player.getY()) {
					x = centreX;
					y = centreY;
				}

				Image image = object.getImage();

				if (x > Client.SCREEN_WIDTH || x + object.getWidth() < 0
						|| y > Client.SCREEN_HEIGHT
						|| y + object.getHeight() < 0) {
					objectsToRemove.add(object.getID());
				} else {
					if (image != null) {
						graphics.setFont(DAMAGE_FONT);
						if (object.getTeam() == ServerCreature.RED_TEAM) {
							graphics.setColor(Color.red);
							if (object.getName().equals("")) {
								graphics.fillRect(
										x + object.getWidth() / 2 - 5, y
												+ object.getHeight() / 4, 10,
										10);
							} else {
								graphics.drawString(object.getName(), (int) (x
										+ object.getWidth() / 2 - object
										.getName().trim().length()
										* DAMAGE_FONT_WIDTH / 2), y + 15);
							}
						} else if (object.getTeam() == ServerCreature.BLUE_TEAM) {
							graphics.setColor(Color.blue);
							if (object.getName().equals("")) {
								graphics.fillRect(
										x + object.getWidth() / 2 - 5, y
												+ object.getHeight() / 4, 10,
										10);
							} else {
								graphics.drawString(object.getName(), (int) (x
										+ object.getWidth() / 2 - object
										.getName().trim().length()
										* DAMAGE_FONT_WIDTH / 2), y + 15);
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
					// If there is no image, then the object is text/numbers
					else {
						String imageName = object.getImageName();
						char colour = imageName.charAt(1);
						String text = imageName
								.substring(2, imageName.length());

						if (!Character.isLetter(text.charAt(0))) {
							if (text.equals("!M")) {
								text = "NOT ENOUGH MANA";
								graphics.setColor(PURPLE_TEXT);
							} else if (Integer.parseInt(text) == 0) {
								graphics.setColor(BLUE_TEXT);
								text = "BLOCK";
							} else if (colour == ServerDamageIndicator.RED_TEXT) {
								graphics.setColor(RED_TEXT);
							} else if (colour == ServerDamageIndicator.YELLOW_TEXT) {
								graphics.setColor(YELLOW_TEXT);
							}

							graphics.setFont(DAMAGE_FONT);

							x -= ((int) (text.length() * DAMAGE_FONT_WIDTH + 0.5)) / 2;
						} else {
							graphics.setFont(PLAYER_TEXT_FONT);
							graphics.setColor(YELLOW_TEXT);

							String oldText = text;
							text = "";
							for (int no = 0; no < oldText.length(); no++) {
								if (oldText.charAt(no) == '_') {
									text += ' ';
								} else {
									text += oldText.charAt(no);
								}
							}
							x-= text.length() * PLAYER_TEXT_FONT_WIDTH / 2;
						}

						graphics.drawString(text, x, y - 10);
					}
				}
			}

			for (Integer object : objectsToRemove) {
				remove(object);
			}
		} catch (ConcurrentModificationException E) {
			System.out
					.println("Tried to access the object list while it was being used");
		}

		// Draw solid tiles at the very front
		for (int row = startRow; row <= endRow; row++) {
			for (int column = startColumn; column <= endColumn; column++) {

				if (grid[row][column] >= 'A' && grid[row][column] != '_') {
					graphics.drawImage(
							ImageReferencePair.getImages()[(int) (grid[row][column])]
									.getImage(), centreX + column * tileSize
									- playerX, centreY + row * tileSize
									- playerY, null);
				}
			}

		}

		if (displayedText != null) {
			graphics.setColor(PURPLE_TEXT);
			graphics.setFont(MESSAGE_FONT);
			graphics.drawString(displayedText,
					(int) (Client.SCREEN_WIDTH / 2
							- (displayedText.length() * MESSAGE_FONT_WIDTH)
							* (2.0 / 5) + 0.5), Client.SCREEN_HEIGHT / 3);
		}

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

		graphics.setColor(PURPLE_TEXT);
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

	}

	public void clear() {
		objects = new ClientObject[ServerEngine.NUMBER_OF_IDS];
	}

	public ClientObject[] getObjects() {
		return objects;
	}

	public char[][] getGrid() {
		return grid;
	}

	public void setGrid(char[][] grid) {
		this.grid = grid;
	}

	public int getTileSize() {
		return tileSize;
	}

	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
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

}
