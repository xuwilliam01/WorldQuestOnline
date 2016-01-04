package Server;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

import Server.Creatures.ServerEnemy;
import Server.Creatures.ServerPlayer;
import Server.Creatures.ServerSlime;
import Server.Items.ServerItem;
import Server.Items.ServerWeaponSwing;
import Server.Projectile.ServerProjectile;
import Tools.RowCol;
import sun.misc.Queue;

/**
 * Stores all the data about the world
 * @author William Xu and Alex Raita
 *
 */
public class ServerWorld
{
	// Character definitions for each type of object
	public final static char PROJECTILE_TYPE = 'P';
	public final static String BULLET_TYPE = PROJECTILE_TYPE + "B";
	public final static String EXPLOSION_TYPE = PROJECTILE_TYPE + "E";

	public final static String PLAYER_TYPE = "C";
	public final static String PLAYER_GHOST_TYPE = "CG";

	public final static char NPC_TYPE = 'N';
	public final static String SLIME_TYPE = NPC_TYPE + "S";
	public final static String GHOUL_TYPE = NPC_TYPE + "G";

	public final static char ITEM_TYPE = 'I';
	public final static String POTION_TYPE = ITEM_TYPE + "P";
	public final static String HP_POTION_TYPE = POTION_TYPE + "H";

	public final static String WEAPON_TYPE = ITEM_TYPE + "W";
	public final static String SWORD_TYPE = WEAPON_TYPE + "S";

	public final static char ANIMATION_TYPE = 'A';
	public final static String ITEM_SWING_TYPE = ANIMATION_TYPE + "S";

	// These are so we can find the right images
	public final static String LONG_SWORD = SWORD_TYPE + "L";

	public final static String HP_25 = HP_POTION_TYPE + "25";
	public final static String HP_50 = HP_POTION_TYPE + "50";
	public final static String HP_75 = HP_POTION_TYPE + "75";
	public final static String HP_100 = HP_POTION_TYPE + "100";

	public final static String GRID_FILE = "World.txt";

	/**
	 * Grid of tiles
	 */
	private char[][] tileGrid;

	/**
	 * The size of each tile
	 */
	public static final int TILE_SIZE = 24;

	/**
	 * Grid of objects
	 */
	private ArrayList<ServerObject>[][] objectGrid;

	/**
	 * The size of each object tile
	 */
	public static final int OBJECT_TILE_SIZE = TILE_SIZE * 4;

	/**
	 * Number of pixels that the collision may be off by that we need to adjust
	 * for
	 */
	public static final int MARGIN_OF_ERROR = TILE_SIZE;

	/**
	 * Max speed (or game may glitch out)
	 */
	private static final int MAX_SPEED = TILE_SIZE * 4;

	/**
	 * The amount of gravity per refresh
	 */
	public final static double GRAVITY = 1;

	/**
	 * List of all the non-tile objects in the world (for movement and collision
	 * detection)
	 */
	private ArrayList<ServerObject> objects;

	/**
	 * List of objects to add to the world next refresh (based on the user's
	 * input)
	 */
	private Queue<ServerObject> objectsToAdd;

	/**
	 * List of objects to remove that doesn't exist anymore
	 */
	private ArrayList<ServerObject> objectsToRemove = new ArrayList<ServerObject>();

	/**
	 * The next time (in milliseconds) to spawn another enemy
	 */
	private long spawnTimer = 0;

	/**
	 * Constructor for server
	 * @throws IOException
	 */
	public ServerWorld() throws IOException
	{
		newWorld();
		objects = new ArrayList<ServerObject>();
		objectsToAdd = new Queue<ServerObject>();
		addEnemies();
	}

	/**
	 * Spawn enemies in the world
	 */
	public void addEnemies()
	{
		for (int no = 0; no < 4; no++)
		{
			ServerEnemy newEnemy = new ServerSlime(400 * no + 50, 50, -1, -1,
					GRAVITY,
					"SLIME_0.png", this);
			newEnemy.addItem(ServerItem.randomItem(newEnemy.getX(),
					newEnemy.getY()));
			add(newEnemy);
		}
	}

	/**
	 * Spawn enemies if the current time passes the spawn timer, then reset the
	 * timer
	 */
	public void spawnEnemies()
	{
		if (System.currentTimeMillis() >= spawnTimer)
		{
			int spawnLocation = (int) (Math.random() * 9 + 9);

			ServerEnemy newEnemy = new ServerSlime(400 * spawnLocation + 50,
					50, -1, -1,
					GRAVITY,
					"SLIME_0.png", this);
			add(newEnemy);
			newEnemy.addItem(ServerItem.randomItem(newEnemy.getX(),
					newEnemy.getY()));
			spawnTimer = System.currentTimeMillis()
					+ (int) (Math.random() * 15000 + 5000);
		}
	}

	/**
	 * Create a new world
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void newWorld() throws IOException
	{
		BufferedReader worldInput = new BufferedReader(new FileReader(new File(
				"Resources", GRID_FILE)));
		StringTokenizer tokenizer = new StringTokenizer(worldInput.readLine());

		tileGrid = new char[Integer.parseInt(tokenizer.nextToken())][Integer
				.parseInt(tokenizer.nextToken())];

		objectGrid = new ArrayList[tileGrid.length
				/ (OBJECT_TILE_SIZE / TILE_SIZE) + 1][tileGrid[0].length
				/ (OBJECT_TILE_SIZE / TILE_SIZE) + 1];

		// Initialize each arraylist of objects in the objectGrid
		for (int row = 0; row < objectGrid.length; row++)
		{
			for (int column = 0; column < objectGrid[0].length; column++)
			{
				objectGrid[row][column] = new ArrayList<ServerObject>();
			}
		}

		String line;
		for (int row = 0; row < tileGrid.length; row++)
		{
			line = worldInput.readLine();
			for (int col = 0; col < tileGrid[row].length; col++)
				tileGrid[row][col] = line.charAt(col);
		}

		worldInput.close();
	}

	/**
	 * Update all the objects, doing whatever needs to be done
	 */
	public synchronized void updateObjects()
	{
		spawnEnemies();

		// Remove all the objects that no longer exist
		for (ServerObject object : objectsToRemove)
		{
			objects.remove(object);

		}
		objectsToRemove.clear();

		while (!objectsToAdd.isEmpty())
		{
			try
			{
				objects.add(objectsToAdd.dequeue());
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		for (ServerObject object : objects)
		{
			// This will remove the object a frame after it stops existing
			if (object.exists())
			{
				if (object.getType().charAt(0) == 'I' && object.isOnSurface())
					object.setHSpeed(0);

				// Make sure the object is within the world
				
				// Make sure the object is within the world
				if (object.getHSpeed() >= 0)
				{
					if (object.getX() < 0)
					{
						object.setX(0);
						object.setHSpeed(0);
					}
					else if (object.getX() + object.getWidth() + object.getHSpeed() > tileGrid[0].length
							* TILE_SIZE)
					{
						object.setX((tileGrid[0].length - 1) * TILE_SIZE
								- object.getWidth());
						object.setHSpeed(0);
					}
				}
				else
				{
					if (object.getX() + object.getHSpeed() < 0)
					{
						object.setX(0);
						object.setHSpeed(0);
					}
					else if (object.getX() + object.getWidth() > tileGrid[0].length
							* TILE_SIZE)
					{
						object.setX((tileGrid[0].length - 1) * TILE_SIZE
								- object.getWidth());
						object.setHSpeed(0);
					}
				}
				

				// Make sure the object is within the world
				if (object.getVSpeed() >= 0)
				{
					if (object.getY() < 0)
					{
						object.setY(0);
						object.setVSpeed(0);
					}
					else if (object.getY() + object.getHeight()
							+ object.getVSpeed() > tileGrid.length
							* TILE_SIZE)
					{
						object.setY((tileGrid.length - 1) * TILE_SIZE
								- object.getHeight());
						object.setVSpeed(0);
					}
				}
				else
				{
					if (object.getY() + object.getVSpeed() < 0)
					{
						object.setY(0);
						object.setVSpeed(0);
					}
					else if (object.getY() + object.getHeight() > tileGrid.length
							* TILE_SIZE)
					{
						object.setY((tileGrid.length - 1) * TILE_SIZE
								- object.getHeight());
						object.setVSpeed(0);
					}
				}
				

				// Add the object to all the object tiles that it collides with
				// currently
				int startRow = (int) (object.getY() / OBJECT_TILE_SIZE);
				int endRow = (int) ((object.getY() + object.getHeight()) / OBJECT_TILE_SIZE);
				int startColumn = (int) (object.getX() / OBJECT_TILE_SIZE);
				int endColumn = (int) ((object.getX() + object.getWidth()) / OBJECT_TILE_SIZE);

				// Update all locations of objects based on the object grid (for
				// collisions)
				updateObjectTiles(object, startRow, endRow, startColumn,
						endColumn);

				// Check collisions with other objects in tiles that they both
				// touch
				for (int row = startRow; row <= endRow; row++)
				{
					for (int column = startColumn; column <= endColumn; column++)
					{
						for (ServerObject otherObject : objectGrid[row][column])
						{
							if (otherObject.exists())
							{
								if (object.getType().equals(BULLET_TYPE))
								{
									if (otherObject.getType().equals(
											PLAYER_TYPE)
											&& otherObject.getID() != ((ServerProjectile) object)
													.getOwnerID()
											&& object.collidesWith(otherObject))
									{
										((ServerPlayer) otherObject)
												.inflictDamage(((ServerProjectile) object)
														.getDamage());
										((ServerProjectile) object).destroy();
									}
									else if (otherObject.getType().charAt(0) == NPC_TYPE
											&& object.collidesWith(otherObject))
									{
										((ServerEnemy) otherObject)
												.inflictDamage(((ServerProjectile) object)
														.getDamage());
										((ServerProjectile) object).destroy();
									}
								}
								// If a player collided with an item
								else if (otherObject.getType().charAt(0) == 'I'
										&& object.getType().equals(PLAYER_TYPE)
										&& object.collidesWith(otherObject))
								{
									((ServerPlayer) object)
											.addItem((ServerItem) otherObject);
									otherObject.destroy();
								}
							}
						}
					}
				}

				boolean moveVertical = true;
				boolean moveHorizontal = true;

				if (object.isSolid())
				{
					// Apply gravity first (DEFINITELY BEFORE CHECKING VSPEED)
					if (object.getVSpeed() < MAX_SPEED)
					{
						object.setVSpeed(object.getVSpeed()
								+ object.getGravity());
					}
					else
					{
						object.setVSpeed(MAX_SPEED);
					}

					double vSpeed = object.getVSpeed();
					double hSpeed = object.getHSpeed();
					double x1 = object.getX();
					double x2 = object.getX() + object.getWidth();
					double y1 = object.getY();
					double y2 = object.getY() + object.getHeight();

					if (vSpeed > 0)
					{
						startRow = (int) (y1 / TILE_SIZE - 1);
						endRow = (int) ((y2 + vSpeed) / TILE_SIZE + 1);
					}
					else if (vSpeed < 0)
					{
						startRow = (int) ((y1 + vSpeed) / TILE_SIZE - 1);
						endRow = (int) (y2 / TILE_SIZE + 1);
					}
					else
					{
						startRow = (int) (y1 / TILE_SIZE);
						endRow = (int) (y2 / TILE_SIZE + 1);
					}

					if (hSpeed > 0)
					{
						startColumn = (int) (x1 / TILE_SIZE- 1);
						endColumn = (int) ((x2 + hSpeed) / TILE_SIZE + 1);
					}
					else if (hSpeed < 0)
					{
						startColumn = (int) ((x1 + hSpeed) / TILE_SIZE- 1);
						endColumn = (int) (x2 / TILE_SIZE + 1);
					}
					else
					{
						startColumn = (int) (x1 / TILE_SIZE - 1);
						endColumn = (int) (x2 / TILE_SIZE + 1);
					}

					if (startRow < 0)
					{
						startRow = 0;
					}
					else if (endRow > tileGrid.length - 1)
					{
						endRow = tileGrid.length - 1;
					}

					if (startColumn < 0)
					{
						startColumn = 0;
					}
					else if (endColumn > tileGrid[0].length - 1)
					{
						endColumn = tileGrid[0].length - 1;
					}

					if (vSpeed > 0)
					{
						// The row and column of the tile that was collided with
						int collideRow = 0;

						for (int row = startRow; row <= endRow; row++)
						{
							for (int column = startColumn; column <= endColumn; column++)
							{
								if (tileGrid[row][column] != ' '
										&& column * TILE_SIZE < x2
										&& column * TILE_SIZE + TILE_SIZE > x1)
								{
									if (y2 + vSpeed >= row * TILE_SIZE
											&& y2 <= row * TILE_SIZE)
									{
										moveVertical = false;
										collideRow = row;
										break;
									}
								}
								if (!moveVertical)
								{
									break;
								}
							}
						}
						if (!moveVertical)
						{
							object.setY(collideRow * TILE_SIZE
									- object.getHeight());
							object.setOnSurface(true);
							object.setVSpeed(0);
						}
						else
						{
							object.setOnSurface(false);
						}
					}
					else if (vSpeed < 0)
					{
						// The row and column of the tile that was collided with
						int collideRow = 0;

						for (int row = startRow; row <= endRow; row++)
						{
							for (int column = startColumn; column <= endColumn; column++)
							{
								if (tileGrid[row][column] != ' '
										&& column * TILE_SIZE < x2
										&& column * TILE_SIZE + TILE_SIZE > x1)
								{
									if (y1 + vSpeed <= row * TILE_SIZE
											+ TILE_SIZE
											&& y1 >= row * TILE_SIZE
													+ TILE_SIZE)
									{
										moveVertical = false;
										collideRow = row;
										break;
									}
								}
								if (!moveVertical)
								{
									break;
								}
							}
						}
						if (!moveVertical)
						{
							object.setY(collideRow * TILE_SIZE + TILE_SIZE);
							object.setVSpeed(0);
						}
					}

					if (hSpeed >= 0)
					{
						// The row and column of the tile that was collided with
						int collideColumn = 0;

						for (int row = startRow; row <= endRow; row++)
						{
							for (int column = startColumn; column <= endColumn; column++)
							{
								if (tileGrid[row][column] != ' '
										&& row * TILE_SIZE < y2
										&& row * TILE_SIZE + TILE_SIZE > y1)
								{
									if (x2 + hSpeed >= column * TILE_SIZE
											&& x2 <= column * TILE_SIZE
													+ MARGIN_OF_ERROR)
									{
										moveHorizontal = false;
										collideColumn = column;
										break;
									}
								}
								if (!moveHorizontal)
								{
									break;
								}
							}
						}
						if (!moveHorizontal)
						{
							object.setX(collideColumn * TILE_SIZE
									- object.getWidth());
							object.setHSpeed(0);
						}
					}
					else if (hSpeed <= 0)
					{
						// The row and column of the tile that was collided with
						int collideColumn = 0;

						for (int row = startRow; row <= endRow; row++)
						{
							for (int column = startColumn; column <= endColumn; column++)
							{
								if (tileGrid[row][column] != ' '
										&& row * TILE_SIZE < y2
										&& row * TILE_SIZE + TILE_SIZE > y1)
								{
									if (x1 + hSpeed <= column * TILE_SIZE
											+ TILE_SIZE
											&& x1 >= column * TILE_SIZE
													+ TILE_SIZE
													- MARGIN_OF_ERROR)
									{
										moveHorizontal = false;
										collideColumn = column;
										break;
									}
								}
								if (!moveHorizontal)
								{
									break;
								}
							}
						}
						if (!moveHorizontal)
						{
							object.setX(collideColumn * TILE_SIZE + TILE_SIZE);
							object.setHSpeed(0);
						}
					}
				}

				if (moveHorizontal)
				{
					object.setX(object.getX() + object.getHSpeed());
				}
				if (moveVertical)
				{
					object.setY(object.getY() + object.getVSpeed());
				}

				// Update specific objects
				if (object.getType().equals(SLIME_TYPE))
				{
					((ServerSlime) object).update();
				}
				else if (object.getType().charAt(0) == PROJECTILE_TYPE)
				{
					if (!moveHorizontal || !moveVertical
							&& object.getType().equals(BULLET_TYPE))
					{
						((ServerProjectile) object).destroy();
					}
					else if (object.getType().equals(EXPLOSION_TYPE))
					{
						((ServerProjectile) object).updateExplosion();
					}
				}
				else if (object.getType().charAt(0) == ANIMATION_TYPE)
				{
					if (object.getType().equals(ITEM_SWING_TYPE))
					{
						((ServerWeaponSwing) object).update();
					}
				}

			}
			else
			{
				objectsToRemove.add(object);
			}
		}
	}

	/**
	 * Add a new object to the list of objects in the world
	 * @param object
	 */
	public synchronized void add(ServerObject object)
	{
		objectsToAdd.enqueue(object);
	}

	/**
	 * Remove an object from the list of objects in the world
	 * @param object
	 */
	public void remove(ServerObject object)
	{
		objects.remove(object);
	}

	/**
	 * Updates the object's location on the object grid
	 * @param object
	 * @param startRow
	 * @param endRow
	 * @param startColumn
	 * @param endColumn
	 */
	public void updateObjectTiles(ServerObject object, int startRow,
			int endRow, int startColumn, int endColumn)
	{
		ArrayList<RowCol> indexesToRemove = new ArrayList<RowCol>();
		ArrayList<RowCol> objectTiles = object.getObjectTiles();

		for (RowCol tile : objectTiles)
		{
			if (!(object.collidesWith(tile.getColumn() * OBJECT_TILE_SIZE,
					tile.getRow() * OBJECT_TILE_SIZE,
					tile.getColumn() * OBJECT_TILE_SIZE + OBJECT_TILE_SIZE,
					tile.getRow() * OBJECT_TILE_SIZE + OBJECT_TILE_SIZE)))
				;
			{
				indexesToRemove.add(tile);
			}
		}

		// Remove all the tiles that the object has left
		for (RowCol tile : indexesToRemove)
		{
			objectTiles.remove(tile);
			removeFromObjectTile(object, tile);
		}

		for (int row = startRow; row <= endRow; row++)
		{
			for (int column = startColumn; column <= endColumn; column++)
			{
				RowCol tile = new RowCol(row, column);

				if (!objectTiles.contains(tile))
				{
					objectTiles.add(tile);
					addToObjectTile(object, tile);
				}
			}
		}
	}

	/**
	 * Add an object to a certain object tile's arraylist
	 * @param object
	 * @param objectTile
	 */
	public void addToObjectTile(ServerObject object, RowCol objectTile)
	{
		objectGrid[objectTile.getRow()][objectTile.getColumn()].add(object);
	}

	/**
	 * Remove an object from a certain object tile's arraylist
	 * @param object
	 * @param objectTile
	 */
	public void removeFromObjectTile(ServerObject object, RowCol objectTile)
	{
		objectGrid[objectTile.getRow()][objectTile.getColumn()].remove(object);
	}

	public char[][] getGrid()
	{
		return tileGrid;
	}

	public void setGrid(char[][] grid)
	{
		this.tileGrid = grid;
	}

	public ArrayList<ServerObject> getObjects()
	{
		return objects;
	}
}
