package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

import Imports.Images;

/**
 * The player (Type 'P')
 * 
 * @author William Xu & Alex Raita
 *
 */
public class ServerPlayer extends ServerObject implements Runnable
{
	// Width and height of the screen
	public final static int SCREEN_WIDTH = 1024;
	public final static int SCREEN_HEIGHT = 768;

	// The starting locations of the player, to change later on
	public final static int PLAYER_X = 50;
	public final static int PLAYER_Y = 50;

	private String message = "";

	private boolean disconnected = false;

	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;
	private ServerEngine engine;
	private ServerWorld world;

	// ////////////////////////////////////////////////////////////////////
	// X and Y coordinates will be changed once scrolling is implemented//
	// ////////////////////////////////////////////////////////////////////

	/**
	 * Boolean describing whether or not the x coordinate has changed since the
	 * last flush
	 */
	private boolean xUpdated;

	/**
	 * Boolean describing whether or not the x coordinate has changed since the
	 * last flush
	 */
	private boolean yUpdated;

	/**
	 * The horizontal direction the player is facing ('R' is right, 'L' is left)
	 */
	private char direction;

	/**
	 * The direction the player is trying to move (so player continues to move
	 * in that direction even after collision, until release of the key) (1 is
	 * right, -1 is left)
	 */
	private int movingDirection = 0;

	/**
	 * The speed at which the player moves
	 */
	private int movementSpeed = 5;

	/**
	 * The initial speed the player jumps at
	 */
	private int jumpSpeed = 20;

	/**
	 * The speed the player moves horizontally
	 */
	private int horizontalMovement = movementSpeed;

	/**
	 * The speed the player moves vertically
	 */
	private int verticalMovement = jumpSpeed;

	/**
	 * HP of the player
	 */
	private int HP = 100;

	/**
	 * Whether or not the player is alive
	 */
	private boolean alive = true;

	/**
	 * Stores the inventory of the player 
	 */
	private ArrayList<ServerItem>  inventory = new ArrayList<ServerItem>();
	
	
	/**
	 * Constructor for a player in the server
	 * @param socket
	 * @param engine
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param ID
	 * @param image
	 */
	public ServerPlayer(Socket socket, ServerEngine engine, double x, double y,
			int width,
			int height, double gravity, int ID, String image)
	{
		super(x, y, width, height, gravity, ID, image, ServerWorld.PLAYER_TYPE);

		if (image.contains("CYCLOPS"))
		{
			HP = 125;
		}
		else if (image.contains("GIRL"))
		{
			HP = 80;
		}
		else if (image.contains("KNIGHT"))
		{
			HP = 100;
		}

		// Import the socket, server, and world
		this.socket = socket;
		this.engine = engine;
		xUpdated = true;
		yUpdated = true;

		// Set up the output
		try
		{
			output = new PrintWriter(this.socket.getOutputStream());
		}
		catch (IOException e)
		{
			System.out.println("Error getting client's output stream");
			e.printStackTrace();
		}

		// Set up the input
		try
		{
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		}
		catch (IOException e)
		{
			System.out.println("Error getting client's input stream");
			e.printStackTrace();
		}

		// Send the 2D grid of the world to the client
		sendMap();

		// Send the player's information
		sendMessage(ID + " " + (int) (x + 0.5) + " " + (int) (y + 0.5) + " "
				+ image);
	}

	/**
	 * Send the player the entire map
	 */
	public void sendMap()
	{
		world = engine.getWorld();
		char[][] grid = world.getGrid();

		// Send to the client the height and width of the grid, the starting x
		// and y position of the grid (top left) and the side length of each
		// tile
		printMessage(grid.length + " " + grid[0].length + " "
				+ ServerWorld.TILE_SIZE);
		for (int row = 0; row < grid.length; row++)
		{
			String message = "";
			for (int column = 0; column < grid[0].length; column++)
			{
				message += grid[row][column];
			}
			printMessage(message);
		}
		flush();
	}

	/**
	 * Send to the client all the updated values (x and y must be rounded to
	 * closest integer)
	 */
	public void update()
	{
		// Update object locations
		for (ServerObject object : world.getObjects())
		{
			// Send the object's updated location if the player can see it
			// within their screen
			if (object.getX() < getX() + getWidth() + SCREEN_WIDTH
					&& object.getX() + object.getWidth() > getX()
							- SCREEN_WIDTH
					&& object.getY() < getY() + getHeight() + SCREEN_HEIGHT
					&& object.getY() + object.getHeight() > getY()
							- SCREEN_HEIGHT)
			{
				if (object.exists())
				{
					queueMessage("O " + object.getID() + " "
							+ ((int) (object.getX() + 0.5))
							+ " " + ((int) (object.getY() + 0.5)) + " "
							+ object.getImage());
				}
				else
				{
					queueMessage("R " + object.getID());
				}
			}
		}

		// Try to move the player in the direction that the key is holding
		if (movingDirection != 0)
		{
			setHSpeed(movingDirection * horizontalMovement);
		}

		// Tell the user what hp he has
		queueMessage("L " + HP);

		// Signal a repaint
		queueMessage("U");
		flushWriter();
	}

	@Override
	public void run()
	{
		// Get input from the player
		while (true)
		{
			try
			{
				String command = input.readLine();

				if (command.charAt(0) == 'A')
				{
					performAction(Double.parseDouble((command.split(" "))[1]));
				}
				else if (command.equals("R"))
				{
					if (direction == 'R')
					{
					setHSpeed(horizontalMovement);
					}
					else
					{
						setHSpeed(2*horizontalMovement/3.0);
					}
					movingDirection = 1;
					
				}
				else if (command.equals("!R"))
				{
					movingDirection = 0;
					if (getHSpeed() > 0)
					{
						setHSpeed(0);
					}
				}
				else if (command.equals("L"))
				{
					if (direction == 'L')
					{
					setHSpeed(-horizontalMovement);
					}
					else
					{
						setHSpeed(-2*horizontalMovement/3);
					}
					
					movingDirection = -1;
				}
				else if (command.equals("!L"))
				{
					movingDirection = 0;
					if (getHSpeed() < 0)
					{
						setHSpeed(0);
					}
				}
				else if (command.equals("U") && (isOnSurface() || !alive))
				{
					setVSpeed(-verticalMovement);
					setOnSurface(false);
				}
				else if (command.equals("!U") && !alive)
				{
					if (getVSpeed() < 0)
					{
						setVSpeed(0);
					}
				}
				else if (command.equals("D") && !alive)
				{
					setVSpeed(verticalMovement);
				}
				else if (command.equals("!D") && !alive)
				{
					if (getVSpeed() > 0)
					{
						setVSpeed(0);
					}
				}
				else if (command.equals("DR"))
				{
					setDirection('R');
				}
				else if (command.equals("DL"))
				{
					setDirection('L');
				}
				else if (command.equals("P"))
				{
					sendMessage("P");
				}
			}
			catch (IOException e)
			{
				break;
			}
		}

		// If the buffered reader breaks, the player has disconnected
		System.out.println("A client has disconnected");
		try
		{
			input.close();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			System.out.println("Error closing buffered reader");
		}

		output.close();
		disconnected = true;
		engine.removePlayer(this);
	}

	/**
	 * Do a specific action when the action button is pressed
	 */
	public void performAction(double angle)
	{
		if (alive)
		{
			// Get the width and height of the image
			int bulletWidth = Images.getGameImage("BULLET.png").getWidth();
			int bulletHeight = Images.getGameImage("BULLET.png").getHeight();

			// Shoot the projectile for testing
			double speed = 30;
			double x = getX() + getWidth() / 2.0 - bulletWidth / 2.0;
			double y = getY() + getHeight() / 2.0 - bulletHeight / 2.0;
			double inaccuracy = 0;

			if (getHSpeed() != 0)
			{
				inaccuracy += Math.PI / 6;
			}

			if (Math.abs(getVSpeed()) >= 3)
			{
				inaccuracy += Math.PI / 3;
			}

			world.add(new ServerProjectile(x, y, -1, -1, 0, ServerEngine.useNextID(),
					getID(), "BULLET.png", speed, angle, inaccuracy,
					ServerWorld.BULLET_TYPE));
		}
	}

	/**
	 * Damage the player a certain amount, and destroy if hp is 0 or below
	 * @param amount
	 */
	public void inflictDamage(int amount)
	{
		HP -= amount;
		if (HP <= 0)
		{
			for(ServerItem item : inventory)
			{
				item.setX(getX());
				item.setY(getY()+5);
				item.makeExist();
				world.add(item);
				item.setOnSurface(false);
				item.setVSpeed(-Math.random()*30-5);

				int direction = Math.random() < 0.5 ? -1 : 1;
				item.setHSpeed(direction*(Math.random()*5 + 3));
			}
			
			inventory.clear();
			
			setSolid(false);
			setGravity(0);
			alive = false;
			setType(ServerWorld.PLAYER_GHOST_TYPE);
			setImage("PLAYERGHOST_RIGHT.png");
			setWidth(59);
			setHeight(64);

			verticalMovement = movementSpeed;
			
			
		}
	}

	/**
	 * Set the direction while also changing the player's image
	 * @param newDirection
	 */
	public void setDirection(char newDirection)
	{
		direction = newDirection;
		if (direction == 'R')
		{
			setImage(getBaseImage() + "_RIGHT" + Images.IMAGE_FORMAT);
		}
		else if (direction == 'L')
		{
			setImage(getBaseImage() + "_LEFT" + Images.IMAGE_FORMAT);
		}
	}

	public boolean isDisconnected()
	{
		return disconnected;
	}

	public void setDisconnected(boolean disconnected)
	{
		this.disconnected = disconnected;
	}

	/**
	 * Send a message to the client (flushing included)
	 * 
	 * @param message the string command to send to the client
	 */
	public void sendMessage(String message)
	{
		output.println(message);
		output.flush();
	}

	/**
	 * Queue a message without flushing
	 * 
	 * @param message the string command to send to the client
	 */
	public void queueMessage(String message)
	{
		if (message.length() != 0)
			this.message += " " + message;
		else
			this.message += message;
	}

	/**
	 * Prints a message to the client
	 * @param message the message to be printed
	 */
	public void printMessage(String message)
	{
		output.println(message);
	}

	/**
	 * Flush all queued messages to the client
	 */
	public void flushWriter()
	{
		output.println(message);
		output.flush();
		message = "";
	}

	/**
	 * Flushes all messages
	 */
	public void flush()
	{
		output.flush();
	}

	public void setX(double x)
	{
		if (x != super.getX())
		{
			xUpdated = true;
			super.setX(x);
		}

	}

	public void setY(double y)
	{
		if (y != super.getY())
		{
			super.setY(y);
			yUpdated = true;
		}
	}

	public boolean isxUpdated()
	{
		return xUpdated;
	}

	public boolean isyUpdated()
	{
		return yUpdated;
	}

	public int getHP()
	{
		return HP;
	}

	public boolean isAlive()
	{
		return alive;
	}

	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}

	public ArrayList<ServerItem> getInventory()
	{
		return inventory;
	}
	
	public void addItem(ServerItem item)
	{
		inventory.add(item);
		queueMessage("I "+item.getImage());
	}
}
