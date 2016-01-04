package Server.Creatures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import Imports.Images;
import Server.ServerEngine;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Items.ServerItem;
import Server.Items.ServerWeaponSwing;
import Server.Projectile.ServerProjectile;
import Tools.RowCol;

/**
 * The player (Type 'P')
 * 
 * @author William Xu & Alex Raita
 *
 */
public class ServerPlayer extends ServerCreature implements Runnable
{
	// The starting locations of the player, to change later on
	public final static int PLAYER_X = 50;
	public final static int PLAYER_Y = 50;

	/**
	 * The starting max hp of the player
	 */
	public final static int PLAYER_START_HP = 100;

	private String message = "";

	private boolean disconnected = false;

	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;
	private ServerEngine engine;
	private ServerWorld world;

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
	 * Whether or not the player is alive
	 */
	private boolean alive = true;

	/**
	 * The current weapon selected (change later to actual inventory slot)
	 */
	private char weaponSelected;

	/**
	 * Whether or not the player can use the item/perform the current action
	 * (used for delaying actions)
	 */
	private boolean canPerformAction;

	/**
	 * The number of frames before the player can perform another action
	 */
	private int actionDelay;

	/**
	 * The number of frames that has passed after the player's action is
	 * disabled
	 */
	private int actionCounter;

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
	public ServerPlayer(double x, double y,
			int width,
			int height, double gravity, String image, Socket socket,
			ServerEngine engine, ServerWorld world)
	{
		super(x, y, width, height, gravity, image, ServerWorld.PLAYER_TYPE,
				PLAYER_START_HP, world);

		weaponSelected = '1';
		actionDelay = 10;

		canPerformAction = true;
		actionCounter = 0;

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
		sendMessage(getID() + " " + (int) (x + 0.5) + " " + (int) (y + 0.5)
				+ " "
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
		// Update the counter for weapon delay
		if (actionCounter < actionDelay)
		{
			if (!canPerformAction)
			{
				actionCounter++;
			}
		}
		else
		{
			actionCounter = 0;
			canPerformAction = true;
		}

		// Update object locations
		for (ServerObject object : world.getObjects())
		{
			// Send the object's updated location if the player can see it
			// within their screen
			if (object.getX() < getX() + getWidth()
					+ Client.Client.SCREEN_WIDTH
					&& object.getX() + object.getWidth() > getX()
							- Client.Client.SCREEN_WIDTH
					&& object.getY() < getY() + getHeight()
							+ Client.Client.SCREEN_HEIGHT
					&& object.getY() + object.getHeight() > getY()
							- Client.Client.SCREEN_HEIGHT)
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
		queueMessage("L " + getHP());

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
					String[] tokens = command.split(" ");
					performAction(Integer.parseInt(tokens[1]),
							Integer.parseInt(tokens[2]));
				}
				else if (command.equals("R"))
				{
					setHSpeed(horizontalMovement);
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
					movingDirection = -1;
					setHSpeed(-horizontalMovement);
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
					// setVSpeed(-(ServerWorld.GRAVITY+Math.sqrt((ServerWorld.GRAVITY)*((ServerWorld.GRAVITY)+8*128.0)))/2.0);
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
				else if (command.charAt(0) == 'W')
				{
					weaponSelected = command.charAt(1);
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
	public void performAction(int mouseX, int mouseY)
	{
		if (alive && canPerformAction)
		{

			int xDist = mouseX - Client.Client.SCREEN_WIDTH / 2;
			int yDist = mouseY - Client.Client.SCREEN_HEIGHT / 2;

			double angle = Math.atan2(yDist, xDist);

			if (weaponSelected == '1')
			{
				world.add(new ServerWeaponSwing(this, "SWORD_0.png",
						(int) (Math.toDegrees(angle) + 0.5), 8));
			}
			else if (weaponSelected == '2')
			{
				// Get the width and height of the image
				int bulletWidth = Images.getGameImage("BULLET.png").getWidth();
				int bulletHeight = Images.getGameImage("BULLET.png")
						.getHeight();

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

				world.add(new ServerProjectile(x, y, -1, -1, 0,
						getID(), "BULLET.png", speed, angle, inaccuracy,
						ServerWorld.BULLET_TYPE));
			}
			
			canPerformAction = false;
		}
	}

	/**
	 * Damage the player a certain amount, and destroy if hp is 0 or below
	 * @param amount
	 */
	public void inflictDamage(int amount)
	{
		setHP(getHP() - amount);
		if (getHP() <= 0)
		{
			movementSpeed = movementSpeed * 3;
			dropInventory();
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

	/**
	 * Add an item to the player's inventory and also tell the client about it
	 */
	public void addItem(ServerItem item)
	{
		super.addItem(item);
		queueMessage("I " + item.getImage());
	}

	public boolean isxUpdated()
	{
		return xUpdated;
	}

	public boolean isyUpdated()
	{
		return yUpdated;
	}

	public boolean isAlive()
	{
		return alive;
	}

	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}
}
