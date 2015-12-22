package Server;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Stores all the data about the world
 * @author William Xu and Alex Raita
 *
 */
public class ServerWorld
{
	/**
	 * Grid of tiles
	 */
	private char[][] grid;

	/**
	 * The size of each tile
	 */
	public static final int TILE_SIZE = 16;
	
	/**
	 * Number of pixels that the collision may be off by that we need to adjust for
	 */
	public static final int MARGIN_OF_ERROR = TILE_SIZE;

	/**
	 * Max speed (or game may glitch out)
	 */
	private static final int MAX_SPEED = TILE_SIZE * 2;

	/**
	 * The amount of gravity per refresh
	 */
	private int gravity = 1;

	/**
	 * List of all the non-tile objects in the world (for movement and collision
	 * detection)
	 */
	private ArrayList<ServerObject> objects;

	/**
	 * References the engine that contains the world
	 */
	Engine engine;
	
	/**
	 * Constructor for server
	 * @throws IOException
	 */
	public ServerWorld(Engine engine) throws IOException
	{
		this.engine = engine;
		newWorld();
		objects = new ArrayList<ServerObject>();
		addEnemies();
	}

	public void addEnemies()
	{
		EnemyAI newEnemy = new EnemyAI(50,50,60,105,engine.useNextID(),"ENEMY.png",100);
		add(newEnemy);
	}
	public void newWorld() throws IOException
	{
		BufferedReader worldInput = new BufferedReader(new FileReader(new File("Resources","World.txt")));
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
	 * Move objects around by updating their x and y coordinates
	 */
	public void moveObjects()
	{

		char[][] grid = getGrid();

		// Move objects around (will be changed once scrolling is implemented)
		for (ServerObject object : objects)
		{
			// Apply gravity first (DEFINITELY BEFORE CHECKING VSPEED)
			if (object.getVSpeed() < MAX_SPEED)
			{
				object.setVSpeed(object.getVSpeed()+gravity);
			}
			
			int vSpeed = object.getVSpeed();
			int hSpeed = object.getHSpeed();
			
			int absVSpeed = Math.abs(vSpeed);
			int absHSpeed = Math.abs(hSpeed);

			int x1 = object.getX();
			int x2 = object.getX() + object.getWidth();
			int y1 = object.getY();
			int y2 = object.getY() + object.getHeight();

			int startRow = (y1 - absVSpeed) / TILE_SIZE - 1;
			if (startRow < 0)
			{
				startRow = 0;
			}
			int endRow = (y2 + absVSpeed) / TILE_SIZE + 1;
			if (endRow >= grid.length)
			{
				endRow = grid.length - 1;
			}
			int startColumn = (x1 - absHSpeed) / TILE_SIZE
					- 1;
			if (startColumn < 0)
			{
				startColumn = 0;
			}
			int endColumn = (x2 + absHSpeed) / TILE_SIZE + 1;
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
									&& x2 <= column * TILE_SIZE + MARGIN_OF_ERROR)
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
					object.setX(collideColumn * TILE_SIZE - object.getWidth());
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
							if (x1 + hSpeed <= column * TILE_SIZE + TILE_SIZE
									&& x1 >= column * TILE_SIZE + TILE_SIZE - MARGIN_OF_ERROR)
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
		}
	}

	/**
	 * Add a new object to the list of objects in the world
	 * @param object
	 */
	public void add(ServerObject object)
	{
		objects.add(object);
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

	public int getGravity()
	{
		return gravity;
	}

	public void setGravity(int gravity)
	{
		this.gravity = gravity;
	}

	public ArrayList<ServerObject> getObjects()
	{
		return objects;
	}
}
