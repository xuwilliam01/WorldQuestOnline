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
	public final static int MAX_INVENTORY = 25;
	public static final int MAX_WEAPONS = 4;
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
	 * The counter that plays the death animation
	 */
	private long deathCounter = -1;

	/**
	 * Stores the equipped items
	 */
	private ServerItem[] equippedWeapons = new ServerItem[MAX_WEAPONS];

	/**
	 * The skin colour for the base image of the player
	 */
	private String skinColour;

	/**
	 * The string for the base image not including the specific animation frame
	 */
	private String baseImage;

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
			int height, double gravity, String skinColour, Socket socket,
			ServerEngine engine, ServerWorld world)
	{
		super(x, y, width, height, gravity, "BASE_" + skinColour
				+ "_RIGHT_0_0.png", ServerWorld.PLAYER_TYPE,
				PLAYER_START_HP, world);

		this.skinColour = skinColour;

		weaponSelected = '0';
		actionDelay = 20;

		canPerformAction = true;

		// Set to -1 when not used
		actionCounter = -1;

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
				+ "BASE_" + skinColour + "_RIGHT_0_0.png");

		baseImage = "BASE_" + skinColour + "_RIGHT";
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
		if (actionCounter < 0)
		{
			baseImage = "BASE_" + skinColour + "_" + getDirection();
		}

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
			actionCounter = -1;
			canPerformAction = true;
		}

		// Update the animation of the player and its accessories
		// The row and column of the frame in the sprite sheet for the image
		RowCol rowCol = new RowCol(0, 0);

		if (Math.abs(getVSpeed()) < 5 && !isOnSurface())
		{
			rowCol = new RowCol(1, 8);
		}
		else if (getVSpeed() < 0)
		{
			rowCol = new RowCol(1, 7);
		}
		else if (getVSpeed() > 0)
		{
			rowCol = new RowCol(1, 9);
		}
		else if (actionCounter >= 0)
		{
			if (actionCounter < 4)
			{
				rowCol = new RowCol(2, 0);
			}
			else if (actionCounter < 8)
			{
				rowCol = new RowCol(2, 1);
			}
			else if (actionCounter < 12)
			{
				rowCol = new RowCol(2, 2);
			}
			else if (actionCounter < 16)
			{
				rowCol = new RowCol(2, 3);
			}
		}
		else if (getHSpeed() != 0 && isOnSurface())
		{
			int checkFrame = (int) (world.getWorldCounter() % 30);
			if (checkFrame < 5)
			{
				rowCol = new RowCol(0, 1);
			}
			else if (checkFrame < 10)
			{
				rowCol = new RowCol(0, 2);
			}
			else if (checkFrame < 15)
			{
				rowCol = new RowCol(0, 3);
			}
			else if (checkFrame < 20)
			{
				rowCol = new RowCol(0, 4);
			}
			else if (checkFrame < 25)
			{
				rowCol = new RowCol(0, 5);
			}
			else
			{
				rowCol = new RowCol(0, 6);
			}
		}
		else if (!isAlive())
		{
			if (deathCounter < 0)
			{
				deathCounter = world.getWorldCounter();
				rowCol = new RowCol(5,1);
			}
			else if (world.getWorldCounter() - deathCounter < 10)
			{
				rowCol = new RowCol(5,1);
			}
			else if (world.getWorldCounter() - deathCounter < 20)
			{
				rowCol = new RowCol(5,2);
			}
			else
			{
				rowCol = new RowCol(5,4);
			}
		}

		// Update the player's image
		setImage(baseImage + "_" + rowCol.getRow() + "_" + rowCol.getColumn()
				+ ".png");

		// Update the accessories on the player
		if (getHead() != null)
		{
			getHead().update(getDirection(), rowCol);
		}

		if (getBody() != null)
		{
			getHead().update(getDirection(), rowCol);
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

		while (message.length() < 4000)
		{
			queueMessage("L " + getHP());
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

				if (command.charAt(0) == 'A' && isOnSurface())
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
				else if (command.equals("DR"))
				{
					setDirection("RIGHT");
				}
				else if (command.equals("DL"))
				{
					setDirection("LEFT");
				}
				else if (command.equals("P"))
				{
					sendMessage("P");
				}
				else if (command.length() >= 2
						&& command.substring(0, 2).equals("Dr"))
				{
					// If dropping from inventory
					if (command.charAt(2) == 'I')
						super.drop(command.substring(4));
					// If dropping from equipped
					else if (command.charAt(2) == 'W')
						drop(Integer.parseInt(command.substring(4)));
					// If using a potion
					else if (command.charAt(2) == 'U')
						super.use(command.substring(4));
				}
				else if (command.charAt(0) == 'M')
				{
					// Move to inventory
					if (command.charAt(1) == 'I')
					{
						unequip(Integer.parseInt(command.substring(3)));
					}
					// Move to equipped weapons
					else if (command.charAt(1) == 'W')
					{
						equip(command.substring(3));
					}
				}
				// This is temporary for selecting a gun or a sword
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
		destroy();
		engine.removePlayer(this);
	}

	public void drop(int slot)
	{
		dropItem(equippedWeapons[slot]);
		equippedWeapons[slot] = null;
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

			if (weaponSelected == '0')
			{
				world.add(new ServerWeaponSwing(this, "SWORD_0.png",
						(int) (Math.toDegrees(angle) + 0.5), 7, 10, 10));
			}
			else if (weaponSelected == '1')
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
	 * Drop inventory and equipment
	 */
	public void dropInventory()
	{
		super.dropInventory();
		for (int item = 0; item < equippedWeapons.length; item++)
			if (equippedWeapons[item] != null)
				dropItem(equippedWeapons[item]);
		equippedWeapons = new ServerItem[MAX_WEAPONS];
	}

	/**
	 * Damage the player a certain amount, and destroy if hp is 0 or below
	 * @param amount
	 */
	public void inflictDamage(int amount, double knockBack)
	{
		setHP(getHP() - amount);
		if (getHP() <= 0)
		{
			movementSpeed = movementSpeed * 3;
			dropInventory();
			setSolid(false);
			setGravity(0);
			alive = false;
			setWidth(59);
			setHeight(64);

			verticalMovement = movementSpeed;
			horizontalMovement = movementSpeed;
		}
		else
		{
			// Knock back the creature based on the knockback force
			// if (Math.abs(knockBack) - getKnockBackResistance() > 0)
			// {
			// setVSpeed(-(Math.abs(knockBack) - getKnockBackResistance()));
			// if (knockBack > 0)
			// {
			// setHSpeed(getHSpeed()
			// + (knockBack - getKnockBackResistance()) / 2);
			// }
			// else
			// {
			// setHSpeed(getHSpeed()
			// - (knockBack + getKnockBackResistance()) / 2);
			// }
			// }
		}
	}

	/**
	 * Set the direction while also changing the player's image
	 * @param newDirection
	 */
	public void setDirection(String newDirection)
	{
		super.setDirection(newDirection);
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
		queueMessage("I " + item.getImage() + " " + item.getType() + " "
				+ item.getAmount());
	}

	public void equip(String itemType)
	{
		// Find next open spot in equipped
		int pos = 0;
		for (; pos < MAX_WEAPONS; pos++)
		{
			if (equippedWeapons[pos] == null)
				break;
		}

		// If there are no equip slots left
		if (pos == MAX_WEAPONS)
			return;

		// Find the item in the inventory
		ServerItem toRemove = null;
		for (ServerItem item : getInventory())
		{
			if (item.getType().equals(itemType))
			{
				toRemove = item;
				break;
			}
		}

		equippedWeapons[pos] = toRemove;
		getInventory().remove(toRemove);
	}

	public void unequip(int slot)
	{
		if (getInventory().size() <= MAX_INVENTORY)
		{
			getInventory().add(equippedWeapons[slot]);
			equippedWeapons[slot] = null;
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

	public boolean isAlive()
	{
		return alive;
	}

	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}

	/**
	 * Whether or not the player is currently performing an action
	 * @return
	 */
	public boolean inAction()
	{
		return actionCounter >= 0;
	}
}
