package Client;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import Effects.ServerDamageIndicator;
import Imports.ImageReferencePair;
import Imports.Images;
import Server.ServerEngine;
import Server.Creatures.ServerCreature;

public class ClientWorld
{

	public final static Color YELLOW_TEXT = new Color(204, 153, 0);
	public final static Color RED_TEXT = new Color(153, 0, 38);

	/**
	 * The grid of tiles
	 */
	private char[][] grid;

	/**
	 * Array of client objects, where the index of the object in the array is
	 * its ID
	 */
	private ClientObject[] objects;

	/**
	 * Arraylist of clouds on the client side
	 */
	private ArrayList<ClientBackground> clouds;

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
	 * The normal font for text
	 */
	public final static Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 12);

	private FontMetrics damageFontMetrics;

	/**
	 * The width in pixels of one character for the damage font
	 */
	public static double DAMAGE_FONT_WIDTH = 0;

	/**
	 * Constructor for the client's side of the world
	 * @param rows the number of rows in the tile grid
	 * @param columns the number of columns in the tile grid
	 * @param grid the tile grid
	 * @throws IOException
	 */
	public ClientWorld(char[][] grid, int tileSize) throws IOException
	{
		this.tileSize = tileSize;
		this.grid = grid;
		objects = new ClientObject[ServerEngine.NUMBER_OF_IDS];

		// Import tile drawing referenes
		ImageReferencePair.importReferences();

		// Generate clouds
		if ((int) (Math.random() * 2) == 0)
		{
			cloudDirection = 1;
		}
		else
		{
			cloudDirection = -1;
		}

		clouds = new ArrayList<ClientBackground>();
		for (int no = 0; no < 8; no++)
		{
			double x = Client.SCREEN_WIDTH / 2 + Math.random() * CLOUD_DISTANCE
					- (CLOUD_DISTANCE / 2);
			double y = Math.random() * (Client.SCREEN_HEIGHT)
					- (2 * Client.SCREEN_HEIGHT / 3);

			double hSpeed = 0;

			hSpeed = (Math.random() * 0.8 + 0.2) * cloudDirection;

			int imageNo = no;

			while (imageNo >= 6)
			{
				imageNo -= 6;
			}

			String image = "CLOUD_" + imageNo + ".png";

			clouds.add(new ClientBackground(x, y, hSpeed, 0, image));
		}
	}

	/**
	 * Add an object (not a tile) to the client, or update it if it already
	 * exists
	 * @param object the object to add
	 */
	public void setObject(int id, int x, int y, String image, int team)
	{
		if (objects[id] == null)
		{
			objects[id] = new ClientObject(id, x, y, image, team);
		}
		else
		{
			objects[id].setX(x);
			objects[id].setY(y);
			objects[id].setImage(image);
		}
	}

	/**
	 * Add an object (not a tile) to the client, or update it if it already
	 * exists
	 * @param object the object to add
	 */
	public void setObject(ClientObject object)
	{
		objects[object.getID()] = object;
	}

	/**
	 * Remove an object from the client's side
	 * @param object the object to remove
	 */
	public void remove(int id)
	{
		objects[id] = null;
	}

	/**
	 * Draws the world
	 * @param graphics the graphics component to be drawn on
	 * @param playerX the position of the player
	 * @param playerY the position of the player
	 */
	public void update(Graphics graphics, int playerX, int playerY,
			int playerWidth, int playerHeight)
	{

		// Get font metrics
		if (damageFontMetrics == null)
		{
			damageFontMetrics = graphics.getFontMetrics(DAMAGE_FONT);
			DAMAGE_FONT_WIDTH = damageFontMetrics.stringWidth("0");
		}

		// Draw the background
		graphics.drawImage(Images.getImage("BACKGROUND.png"), 0, 0, null);

		// Draw and move the clouds
		for (ClientBackground cloud : clouds)
		{
			if (cloud.getX() <= Client.SCREEN_WIDTH
					&& cloud.getX() + cloud.getWidth() >= 0
					&& cloud.getY() <= Client.SCREEN_HEIGHT
					&& cloud.getY() + cloud.getHeight() >= 0)
			{
				graphics.drawImage(cloud.getImage(), (int) cloud.getX(),
						(int) cloud.getY(), null);
			}

			if (cloud.getX() < Client.SCREEN_WIDTH / 2 - CLOUD_DISTANCE / 2)
			{
				cloud.setX(Client.SCREEN_WIDTH / 2 + CLOUD_DISTANCE / 2);
				// String image = "CLOUD_" + (int) (Math.random() * 6) + ".png";
				// cloud.setImage(Images.getImage(image));
				cloud.setY(Math.random() * (Client.SCREEN_HEIGHT)
						- (2 * Client.SCREEN_HEIGHT / 3));
				cloud.sethSpeed((Math.random() * 0.8 + 0.2) * cloudDirection);

			}
			else if (cloud.getX() > Client.SCREEN_WIDTH / 2 + CLOUD_DISTANCE
					/ 2)
			{
				cloud.setX(Client.SCREEN_WIDTH / 2 - CLOUD_DISTANCE / 2);
				// String image = "CLOUD_" + (int) (Math.random() * 6) + ".png";
				// cloud.setImage(Images.getImage(image));
				cloud.setY(Math.random() * (Client.SCREEN_HEIGHT)
						- (2 * Client.SCREEN_HEIGHT / 3));
				cloud.sethSpeed((Math.random() * 0.8 + 0.2) * cloudDirection);
			}
			cloud.setX(cloud.getX() + cloud.gethSpeed());

			// System.out.println(cloud.getX());
		}
		
		

		// Center of the screen
		int centreX = Client.SCREEN_WIDTH / 2 - playerWidth / 2;
		int centreY = Client.SCREEN_HEIGHT / 2 - playerHeight / 2;

		// Draw tiles (draw based on player's position later)
		int startRow = (int) ((playerY - Client.SCREEN_HEIGHT / 2 - playerHeight) / tileSize);
		if (startRow < 0)
		{
			startRow = 0;
		}
		int endRow = (int) ((Client.SCREEN_HEIGHT / 2 + playerY + playerHeight) / tileSize);
		if (endRow >= grid.length)
		{
			endRow = grid.length - 1;
		}
		int startColumn = (int) ((playerX - Client.SCREEN_WIDTH / 2 - playerWidth) / tileSize);
		if (startColumn < 0)
		{
			startColumn = 0;
		}
		int endColumn = (int) ((Client.SCREEN_WIDTH / 2 + playerX + playerWidth) / tileSize);
		if (endColumn >= grid[0].length)
		{
			endColumn = grid[0].length - 1;
		}

		// Draw background tiles
		for (int row = startRow; row <= endRow; row++)
		{
			for (int column = startColumn; column <= endColumn; column++)
			{

				if (grid[row][column] < 'A' && grid[row][column] != ' ')
				{
					graphics.drawImage(
							ImageReferencePair.getImages()[(int) (grid[row][column])]
									.getImage(), centreX
									+ column * tileSize - playerX, centreY
									+ row
									* tileSize - playerY,
							null);
				}
			}

		}

		// Create a list of objects to remove after leaving the screen
		ArrayList<Integer> objectsToRemove = new ArrayList<Integer>();

		// Go through each object in the world and draw it relative to the
		// player's position. If it is outside of the screen, don't draw it just
		// remove it
		try
		{
			for (ClientObject object : objects)
			{
				if (object == null)
				{
					continue;
				}
				int x = centreX + object.getX() - playerX;
				int y = centreY + object.getY() - playerY;
				Image image = object.getImage();

				if (x > Client.SCREEN_WIDTH || x + object.getWidth() < 0
						|| y > Client.SCREEN_HEIGHT
						|| y + object.getHeight() < 0)
				{
					objectsToRemove.add(object.getID());
				}
				else
				{
					if (image != null)
					{
						if (object.getTeam() == ServerCreature.RED_TEAM)
						{
							graphics.setColor(Color.red);
							graphics.fillRect(x + image.getWidth(null) / 2 - 5,
									y - 15, 10, 10);
						}
						else if (object.getTeam() == ServerCreature.BLUE_TEAM)
						{
							graphics.setColor(Color.blue);
							graphics.fillRect(x + image.getWidth(null) / 2 - 5,
									y - 15, 10, 10);
						}
						graphics.drawImage(image, x, y,
								null);
					}
					// If there is no image, then the object is text/numbers
					else
					{
						String imageName = object.getImageName();
						char colour = imageName.charAt(1);
						if (colour == ServerDamageIndicator.RED_TEXT)
						{
							graphics.setColor(RED_TEXT);
						}
						else if (colour == ServerDamageIndicator.YELLOW_TEXT)
						{
							graphics.setColor(YELLOW_TEXT);
						}

						graphics.setFont(DAMAGE_FONT);
						String text = imageName
								.substring(2, imageName.length());

						if (object.getWidth() == 0)
						{
							object.setWidth((int) (text.length()
									* DAMAGE_FONT_WIDTH + 0.5));
							object.setX(object.getX() - object.getWidth() / 2);
						}
						x = centreX + object.getX() - playerX;

						graphics.drawString(text, x, y - 10);
					}
				}
			}

			for (Integer object : objectsToRemove)
			{
				remove(object);
			}
		}
		catch (ConcurrentModificationException E)
		{
			System.out
					.println("Tried to access the object list while it was being used");
		}

		// Draw solid tiles at the very front
		for (int row = startRow; row <= endRow; row++)
		{
			for (int column = startColumn; column <= endColumn; column++)
			{

				if (grid[row][column] >= 'A' && grid[row][column] != '_')
				{
					graphics.drawImage(
							ImageReferencePair.getImages()[(int) (grid[row][column])]
									.getImage(), centreX
									+ column * tileSize - playerX, centreY
									+ row
									* tileSize - playerY,
							null);
				}
			}

		}
	}

	public void clear()
	{
		objects = new ClientObject[ServerEngine.NUMBER_OF_IDS];
	}

	public char[][] getGrid()
	{
		return grid;
	}

	public void setGrid(char[][] grid)
	{
		this.grid = grid;
	}

	public int getTileSize()
	{
		return tileSize;
	}

	public void setTileSize(int tileSize)
	{
		this.tileSize = tileSize;
	}

}
