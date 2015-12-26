package Server;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

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

	public final static String PLAYER_TYPE = "C";

	public final static char NPC_TYPE = 'N';
	public final static String SLIME_TYPE = NPC_TYPE + "S";
	public final static String GHOUL_TYPE = NPC_TYPE + "G";

	/**
	 * Grid of tiles
	 */
	private char[][] grid;

	/**
	 * The size of each tile
	 */
	public static final int TILE_SIZE = 16;

	/**
	 * Number of pixels that the collision may be off by that we need to adjust
	 * for
	 */
	public static final int MARGIN_OF_ERROR = TILE_SIZE;

	/**
	 * Max speed (or game may glitch out)
	 */
	private static final int MAX_SPEED = TILE_SIZE * 2;

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
	 * References the engine that contains the world
	 */
	private ServerEngine engine;

	/**
	 * List of objects to add to the world next refresh (based on the user's
	 * input)
	 */
	private Queue<ServerObject> objectsToAdd;

	// Create list of objects to remove if doesn't exist anymore
	private ArrayList<ServerObject> objectsToRemove = new ArrayList<ServerObject>();

	/**
	 * Constructor for server
	 * @throws IOException
	 */
	public ServerWorld(ServerEngine engine) throws IOException
	{
		this.engine = engine;
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
		for (int no = 0; no < 5; no++)
		{
			ServerNPC newEnemy = new ServerSlime(200 * no + 50, 50, -1, -1,
					GRAVITY,
					engine.useNextID(),
					"SLIME_0.png",
					100);
			add(newEnemy);
		}
	}

	/**
	 * Create a new world
	 * @throws IOException
	 */
	public void newWorld() throws IOException
	{
		BufferedReader worldInput = new BufferedReader(new FileReader(new File(
				"Resources", "World.txt")));
		StringTokenizer tokenizer = new StringTokenizer(worldInput.readLine());

		grid = new char[Integer.parseInt(tokenizer.nextToken())][Integer
				.parseInt(tokenizer.nextToken())];
		String line;
		for (int row = 0; row < grid.length; row++)
		{
			line = worldInput.readLine();
			for (int col = 0; col < grid[row].length; col++)
				grid[row][col] = line.charAt(col);
		}

		worldInput.close();
	}

	/**
	 * Update all the objects, doing whatever needs to be done
	 */
	public synchronized void updateObjects()
	{

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
				// Apply gravity first (DEFINITELY BEFORE CHECKING VSPEED)
				if (object.getVSpeed() < MAX_SPEED)
				{
					object.setVSpeed(object.getVSpeed() + object.getGravity());
				}

				double vSpeed = object.getVSpeed();
				double hSpeed = object.getHSpeed();

				double absVSpeed = Math.abs(vSpeed);
				double absHSpeed = Math.abs(hSpeed);

				double x1 = object.getX();
				double x2 = object.getX() + object.getWidth();
				double y1 = object.getY();
				double y2 = object.getY() + object.getHeight();

				int startRow = (int) ((y1 - absVSpeed) / TILE_SIZE - 1);
				if (startRow < 0)
				{
					startRow = 0;
				}
				int endRow = (int) ((y2 + absVSpeed) / TILE_SIZE + 1);
				if (endRow >= grid.length)
				{
					endRow = grid.length - 1;
				}
				int startColumn = (int) ((x1 - absHSpeed) / TILE_SIZE - 1);
				if (startColumn < 0)
				{
					startColumn = 0;
				}
				int endColumn = (int) ((x2 + absHSpeed) / TILE_SIZE + 1);
				if (endColumn >= grid[0].length)
				{
					endColumn = grid[0].length - 1;
				}

				boolean moveVertical = true;
				boolean moveHorizontal = true;

				if (vSpeed > 0)
				{
					// The row and column of the tile that was collided with
					int collideRow = 0;

					for (int row = startRow; row <= endRow; row++)
					{
						for (int column = startColumn; column <= endColumn; column++)
						{
							if (grid[row][column] != '0'
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
						object.setY(collideRow * TILE_SIZE - object.getHeight());
						object.setOnSurface(true);
						object.setVSpeed(0);
					}
					else
					{
						object.setY(y1 + vSpeed);
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
							if (grid[row][column] != '0'
									&& column * TILE_SIZE < x2
									&& column * TILE_SIZE + TILE_SIZE > x1)
							{
								if (y1 + vSpeed <= row * TILE_SIZE + TILE_SIZE
										&& y1 >= row * TILE_SIZE + TILE_SIZE)
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
					else
					{
						object.setY(y1 + vSpeed);
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
							if (grid[row][column] != '0'
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
					else
					{
						object.setX(object.getX() + object.getHSpeed());
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
							if (grid[row][column] != '0'
									&& row * TILE_SIZE < y2
									&& row * TILE_SIZE + TILE_SIZE > y1)
							{
								if (x1 + hSpeed <= column * TILE_SIZE
										+ TILE_SIZE
										&& x1 >= column * TILE_SIZE + TILE_SIZE
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
					else
					{
						object.setX(object.getX() + object.getHSpeed());
					}
				}


				// Update specific objects
				if (object.getType() == SLIME_TYPE)
				{
					((ServerSlime) object).update();
				}
				else if (object.getType().charAt(0) == PROJECTILE_TYPE)
				{
					if (!moveHorizontal || !moveVertical)
					{
						objectsToRemove.add(object);
						object.setExists(false);
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

	public char[][] getGrid()
	{
		return grid;
	}

	public void setGrid(char[][] grid)
	{
		this.grid = grid;
	}

	public ArrayList<ServerObject> getObjects()
	{
		return objects;
	}
}
