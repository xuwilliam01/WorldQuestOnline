package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Imports.Images;

/**
 * The player
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
	private Engine engine;
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
	 * The speed at which the player moves
	 */
	private int movementSpeed = 5;

	/**
	 * The initial speed the player jumps at
	 */
	private int jumpSpeed = -15;

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
	public ServerPlayer(Socket socket, Engine engine, int x, int y, int width,
			int height, int ID, String image)
	{
		super(x, y, width, height, ID, image);
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
		sendMessage(ID + " " + x + " " + y + " " + image);
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
	 * Send to the client all the updated values
	 */
	public void update()
	{
		// Update object locations
		for (ServerObject object : world.getObjects())
		{
			// Send the object's updated location if the player can see it within their screen
			if (object.getX() < getX() + getWidth() + SCREEN_WIDTH
					&& object.getX() + object.getWidth() > getX()
							- SCREEN_WIDTH
					&& object.getY() < getY() + getHeight() + SCREEN_HEIGHT
					&& object.getY() + object.getHeight() > getY()
							- SCREEN_HEIGHT)
				queueMessage("O " + object.getID() + " " + object.getX()
						+ " " + object.getY() + " " + object.getImage());
		}
		
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
				// System.out.println(command);

				if (command.equals("RIGHT"))
				{
					setHSpeed(movementSpeed);
					setDirection('R');
				}
				else if (command.equals("STOP RIGHT"))
				{
					if (getHSpeed() > 0)
					{
						setHSpeed(0);
					}
				}
				else if (command.equals("LEFT"))
				{
					setHSpeed(-movementSpeed);
					setDirection('L');
				}
				else if (command.equals("STOP LEFT"))
				{
					if (getHSpeed() < 0)
					{
						setHSpeed(0);
					}
				}
				else if (command.equals("UP") && isOnSurface())
				{
					setVSpeed(jumpSpeed);
					setOnSurface(false);
				}
				else if (command.equals("PING"))
				{
					sendMessage("REPING");
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

	public void setX(int x)
	{
		if (x != super.getX())
		{
			xUpdated = true;
			super.setX(x);
		}

	}

	public void setY(int y)
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

	public int[] getPlayerOnGrid()
	{
		return new int[] { getY() / ServerWorld.TILE_SIZE,
				getX() / ServerWorld.TILE_SIZE };
	}

	public int[] getObjectOnGrid(int x, int y)
	{
		return new int[] { y / ServerWorld.TILE_SIZE, x / ServerWorld.TILE_SIZE };
	}

}
