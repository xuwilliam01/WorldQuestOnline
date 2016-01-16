package Server.Creatures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import Effects.ServerDamageIndicator;
import Server.ServerEngine;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Items.ServerAccessory;
import Server.Items.ServerArmour;
import Server.Items.ServerItem;
import Server.Items.ServerMoney;
import Server.Items.ServerWeapon;
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

	public final static int DEFAULT_WIDTH = 34;
	public final static int DEFAULT_HEIGHT = 90;

	public static final int DEFAULT_WEAPON_SLOT = 9;
	public static final int DEFAULT_ARMOUR_SLOT = -1;
	public static final int DEFAULT_SHIELD_SLOT = -2;

	// The starting mana and hp for the player
	public final static int PLAYER_START_HP = 100;
	public final static int PLAYER_START_MANA = 100;

	// Initial jump and move speeds of the player
	public final static int MOVE_SPEED = 5;
	public final static int JUMP_SPEED = 20;

	public final static int MAX_HSPEED = 10;
	public final static int MAX_VSPEED = 28;

	private StringBuilder message = new StringBuilder();

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
	 * The speed the player moves horizontally
	 */
	private int horizontalMovement = MOVE_SPEED;

	/**
	 * The speed the player moves vertically
	 */
	private int verticalMovement = JUMP_SPEED;

	/**
	 * The current weapon selected (change later to actual inventory slot)
	 */
	private char weaponSelected = '9';

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
	 * The specific action being performed alongside the action counter
	 */
	private String action;

	/**
	 * The counter that plays the death animation
	 */
	private long deathCounter = -1;

	/**
	 * The vendor that player is currently interacting with
	 */
	private ServerVendor vendor = null;

	/**
	 * Stores the equipped weapons
	 */
	private ServerWeapon[] equippedWeapons = new ServerWeapon[MAX_WEAPONS];

	/**
	 * The equipped armor
	 */
	private ServerArmour equippedArmour = null;

	/**
	 * The accessory worn on the head
	 */
	private ServerAccessory head;

	/**
	 * The accessory worn on the body
	 */
	private ServerAccessory body;

	/**
	 * The damage the player inflicts from just punching
	 */
	public final static int PUNCHING_DAMAGE = 5;

	/**
	 * Will make the player perform the action in the next game loop
	 */
	private boolean performingAction;

	/**
	 * The mouse's x position when the player last wanted to perform an action
	 */
	private int newMouseX;

	/**
	 * The mouse's y position when the player last wanted to perform an action
	 */
	private int newMouseY;

	/**
	 * The skin colour for the base image of the player
	 */
	private String skinColour;

	/**
	 * The string for the base image not including the specific animation frame
	 */
	private String baseImage;

	/**
	 * Whether or not the action was a right click
	 */
	private boolean rightClick = false;

	/**
	 * An accessory exclusive to the player
	 */
	private ServerAccessory shield;

	private int mana = PLAYER_START_MANA;
	private int maxMana = PLAYER_START_MANA;

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
			int height, double relativeDrawX, double relativeDrawY,
			double gravity, String skinColour, Socket socket,
			ServerEngine engine, ServerWorld world)
	{
		super(x, y, width, height, relativeDrawX, relativeDrawY, gravity,
				"BASE_" + skinColour
						+ "_RIGHT_0_0.png", ServerWorld.PLAYER_TYPE,
				PLAYER_START_HP, world, true);

		this.skinColour = skinColour;

		actionDelay = 20;

		canPerformAction = true;

		action = "NOTHING";

		performingAction = false;

		newMouseX = 0;
		newMouseY = 0;

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
				+ "BASE_" + skinColour + "_RIGHT_0_0.png " + getTeam());

		baseImage = "BASE_" + skinColour;

		// Start the player off with some weapons
		addItem(new ServerWeapon(0, 0, ServerWorld.AX_TYPE
				+ ServerWorld.WOOD_TIER));
		addItem(new ServerWeapon(0, 0, ServerWorld.SWORD_TYPE
				+ ServerWorld.WOOD_TIER));
		addItem(new ServerWeapon(0, 0, ServerWorld.HALBERD_TYPE
				+ ServerWorld.WOOD_TIER));
		addItem(new ServerWeapon(0, 0, ServerWorld.DAGGER_TYPE
				+ ServerWorld.WOOD_TIER));

		addItem(new ServerMoney(0, 0, 5));
		addItem(new ServerArmour(0, 0, ServerWorld.BLUE_NINJA_ARMOUR));
		addItem(new ServerArmour(0, 0, ServerWorld.RED_NINJA_ARMOUR));
		addItem(new ServerArmour(0, 0, ServerWorld.GREY_NINJA_ARMOUR));
		addItem(new ServerArmour(0, 0, ServerWorld.STEEL_ARMOUR));
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

	public void updatePlayer()
	{
		if (exists())
		{

			if (actionCounter < 0)
			{
				super.setDirection(getNextDirection());
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
				action = "NOTHING";
				canPerformAction = true;
			}

			// Update the animation of the player and its accessories
			// The row and column of the frame in the sprite sheet for the image
			setRowCol(new RowCol(0, 0));
			if (actionCounter >= 0)
			{
				if (action.equals("SWING"))
				{
					if (actionCounter < 1.0 * actionDelay / 4.0)
					{
						setRowCol(new RowCol(2, 0));
					}
					else if (actionCounter < 1.0 * actionDelay / 2.0)
					{
						setRowCol(new RowCol(2, 1));
					}
					else if (actionCounter < 1.0 * actionDelay / 4.0 * 3)
					{
						setRowCol(new RowCol(2, 2));
					}
					else if (actionCounter < actionDelay)
					{
						setRowCol(new RowCol(2, 3));
					}
				}
				else if (action.equals("PUNCH"))
				{
					if (actionCounter < 5)
					{
						setRowCol(new RowCol(2, 7));
					}
					else if (actionCounter < 16)
					{
						setRowCol(new RowCol(2, 8));
					}
				}
				else if (action.equals("FIRE"))
				{
					setRowCol(new RowCol(2, 7));
				}
				else if (action.equals("BLOCK"))
				{
					setRowCol(new RowCol(2, 9));
				}
			}
			else if (getHSpeed() != 0 && isOnSurface())
			{
				int checkFrame = (int) (world.getWorldCounter() % 30);
				if (checkFrame < 5)
				{
					setRowCol(new RowCol(0, 1));
				}
				else if (checkFrame < 10)
				{
					setRowCol(new RowCol(0, 2));
				}
				else if (checkFrame < 15)
				{
					setRowCol(new RowCol(0, 3));
				}
				else if (checkFrame < 20)
				{
					setRowCol(new RowCol(0, 4));
				}
				else if (checkFrame < 25)
				{
					setRowCol(new RowCol(0, 5));
				}
				else
				{
					setRowCol(new RowCol(0, 6));
				}
			}
			else if (!isAlive())
			{
				if (deathCounter < 0)
				{
					deathCounter = world.getWorldCounter();
					setRowCol(new RowCol(5, 1));
				}
				else if (world.getWorldCounter() - deathCounter < 10)
				{
					setRowCol(new RowCol(5, 1));
				}
				else if (world.getWorldCounter() - deathCounter < 20)
				{
					setRowCol(new RowCol(5, 2));
				}
				else if (world.getWorldCounter() - deathCounter < 300)
				{
					setRowCol(new RowCol(5, 4));
				}
				else
				{
					// Respawn the player
					addItem(new ServerWeapon(0, 0, ServerWorld.AX_TYPE
							+ ServerWorld.WOOD_TIER));
					addItem(new ServerWeapon(0, 0, ServerWorld.SWORD_TYPE
							+ ServerWorld.WOOD_TIER));
					addItem(new ServerWeapon(0, 0, ServerWorld.HALBERD_TYPE
							+ ServerWorld.WOOD_TIER));
					addItem(new ServerWeapon(0, 0, ServerWorld.DAGGER_TYPE
							+ ServerWorld.WOOD_TIER));

					addItem(new ServerMoney(0, 0, 5));
					addItem(new ServerArmour(0, 0,
							ServerWorld.BLUE_NINJA_ARMOUR));
					addItem(new ServerArmour(0, 0, ServerWorld.RED_NINJA_ARMOUR));
					addItem(new ServerArmour(0, 0,
							ServerWorld.GREY_NINJA_ARMOUR));
					addItem(new ServerArmour(0, 0, ServerWorld.STEEL_ARMOUR));

					setAlive(true);

					verticalMovement = JUMP_SPEED;
					horizontalMovement = MOVE_SPEED;

					setX(Math.random() * 1500 + 500);
					setY(300);

					setHP(100);

					setAttackable(true);
					deathCounter = -1;
				}
			}
			else if (Math.abs(getVSpeed()) < 5 && !isOnSurface())
			{
				setRowCol(new RowCol(1, 8));
			}
			else if (getVSpeed() < 0)
			{
				setRowCol(new RowCol(1, 7));
			}
			else if (getVSpeed() > 0)
			{
				setRowCol(new RowCol(1, 9));
			}

			// Update the player's image
			setImage(baseImage + "_" + getDirection() + "_"
					+ getRowCol().getRow()
					+ "_"
					+ getRowCol().getColumn()
					+ ".png");

			if (getHead() != null)
			{
				getHead().update(
						getDirection(),
						getRowCol());
			}

			if (getBody() != null)
			{
				getBody().update(
						getDirection(),
						getRowCol());
			}
		}
	}

	/**
	 * Send to the client all the updated values (x and y must be rounded to
	 * closest integer)
	 */
	public void updateClient()
	{
		if (exists())
		{
			// Send all the objects within all the object tiles in the player's
			// screen
			int startRow = (int) ((getY() + getHeight() / 2 - Client.Client.SCREEN_HEIGHT) / ServerWorld.OBJECT_TILE_SIZE);
			int endRow = (int) ((getY() + getHeight() / 2 + Client.Client.SCREEN_HEIGHT) / ServerWorld.OBJECT_TILE_SIZE);
			int startColumn = (int) ((getX() + getWidth() / 2 - Client.Client.SCREEN_WIDTH) / ServerWorld.OBJECT_TILE_SIZE);
			int endColumn = (int) ((getX() + getWidth() / 2 + Client.Client.SCREEN_WIDTH) / ServerWorld.OBJECT_TILE_SIZE);

			if (startRow < 0)
			{
				startRow = 0;
			}
			else if (endRow > world.getObjectGrid().length - 1)
			{
				endRow = world.getObjectGrid().length - 1;
			}

			if (startColumn < 0)
			{
				startColumn = 0;
			}
			else if (endColumn > world.getObjectGrid()[0].length - 1)
			{
				endColumn = world.getObjectGrid()[0].length - 1;
			}

			for (int row = startRow; row <= endRow; row++)
			{
				for (int column = startColumn; column <= endColumn; column++)
				{
					for (ServerObject object : world.getObjectGrid()[row][column])
					{
						if (object.exists()
								&& !object.getType().equals(
										ServerWorld.SPAWN_TYPE))
						{
							int x = (int) (object.getX() + 0.5);
							int y = (int) (object.getY() + 0.5);
							int team = ServerCreature.NEUTRAL;

							if (object.getType().charAt(0) == ServerWorld.CREATURE_TYPE)
							{
								x = ((ServerCreature) object).getDrawX();
								y = ((ServerCreature) object).getDrawY();
								team = ((ServerCreature) object).getTeam();
							}
							else if (object.getType().charAt(0) == ServerWorld.PROJECTILE_TYPE)

							{
								x = ((ServerProjectile) object).getDrawX();
								y = ((ServerProjectile) object).getDrawY();
							}

							queueMessage("O " + object.getID() + " " + x
									+ " " + y + " " + object.getImage()
									+ " " + team);
						}
						else
						{
							queueMessage("R " + object.getID());
						}
					}
				}
			}

			// Try to move the player in the direction that the key is holding
			if (movingDirection != 0)
			{
				setHSpeed(movingDirection * horizontalMovement);
			}

			// Check if the vendor is out of range
			if (vendor != null
					&& (!collidesWith(vendor) || getHP() <= 0 || isDisconnected()))
			{
				vendor.setIsBusy(false);
				vendor = null;
				queueMessage("C");
			}

			// Send the player's HP, Mana, and speed
			queueMessage("Q " + mana);
			queueMessage("K " + maxMana);
			queueMessage("L " + getHP());
			queueMessage("M " + getMaxHP());
			queueMessage("S " + horizontalMovement);
			queueMessage("J " + verticalMovement);

			// Send the player's current damage
			int currentDamage = PUNCHING_DAMAGE;
			int weaponNo = weaponSelected - '0';
			if (weaponNo != DEFAULT_WEAPON_SLOT
					&& equippedWeapons[weaponNo] != null)
				currentDamage = equippedWeapons[weaponNo].getDamage();
			queueMessage("D " + currentDamage + " " + getBaseDamage());

			while (message.length() < 4000)
			{
				queueMessage("L " + getHP());
			}

			// Signal a repaint
			queueMessage("U");
			flushWriter();
		}
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

				if ((command.charAt(0) == 'A' || command.charAt(0) == 'a')
						&& isOnSurface()
						&& !performingAction && isAlive())
				{
					String[] tokens = command.split(" ");
					performingAction = true;
					newMouseX = Integer.parseInt(tokens[1]);
					newMouseY = Integer.parseInt(tokens[2]);
					if (command.charAt(0) == 'a')
					{
						rightClick = true;
					}
				}
				else if (command.equals("!a") && isAlive())
				{
					actionCounter = actionDelay;
				}
				else if (command.equals("R") && isAlive())
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
				else if (command.equals("L") && isAlive())
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
				else if (command.equals("U") && isOnSurface() && isAlive()
						&& !inAction())
				{
					setVSpeed(-verticalMovement);
					// setVSpeed(-(ServerWorld.GRAVITY+Math.sqrt((ServerWorld.GRAVITY)*((ServerWorld.GRAVITY)+8*128.0)))/2.0);
					setOnSurface(false);
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
						equipWeapon(command.substring(3));
					}
					else if (command.charAt(1) == 'A')
					{
						equipArmour(command.substring(3));
					}
					else if (command.charAt(1) == 'S')
					{
						equipShield(command.substring(3));
					}
				}
				// This is temporary for selecting a gun or a sword
				else if (command.charAt(0) == 'W')
				{
					weaponSelected = command.charAt(1);
				}
				else if (command.charAt(0) == 'B' && vendor != null)
				{
					ServerItem vendorItem = null;
					for (ServerItem item : vendor.getInventory())
						if (item.getType().equals(command.substring(2)))
							vendorItem = item;

					if (vendorItem != null
							&& getMoney() >= vendorItem.getCost())
					{
						decreaseMoney(vendorItem.getCost());
						vendor.drop(vendorItem.getType());
					}
				}
				else if (command.charAt(0) == 'E')
				{
					interact();
				}
				else if (command.charAt(0) == 'S' && vendor != null)
				{
					String type = command.substring(2);
					if (!type.equals(ServerWorld.MONEY_TYPE))
						sell(type);
				}
			}
			catch (IOException e)
			{
				break;
			}
		}

		// If the buffered reader breaks, the player has disconnected
		if (vendor != null)
		{
			vendor.setIsBusy(false);
			vendor = null;
		}
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
		if (getHead() != null)
		{
			getHead().destroy();
			setHead(null);
		}

		if (getBody() != null)
		{
			getBody().destroy();
			setBody(null);
		}
		setX(0);
		setY(0);
		destroy();
		engine.removePlayer(this);
	}

	public void sell(String type)
	{
		ServerItem toRemove = null;
		for (ServerItem item : getInventory())
			if (item.getType().equals(type))
			{

				if (item.getAmount() > 1)
				{
					item.decreaseAmount();
					vendor.addItem(ServerItem.copy(item));
				}
				else
				{
					toRemove = item;
					vendor.addItem(item);
				}

				String newMessage = String.format("V %s %s %d %d",
						item.getImage(), item.getType(), 1, item.getCost());
				queueMessage(newMessage);

				increaseMoney((item.getCost() + 1) / 2);
				break;
			}
		if (toRemove != null)
			getInventory().remove(toRemove);

	}

	public void increaseMoney(int amount)
	{
		ServerMoney newMoney = new ServerMoney(getX(), getY(), amount);
		newMoney.makeExist();
		newMoney.setSource(this);
		world.add(newMoney);
	}

	public int getMoney()
	{
		for (ServerItem item : getInventory())
			if (item.getType().equals(ServerWorld.MONEY_TYPE))
				return item.getAmount();
		return 0;

	}

	public void decreaseMoney(int amount)
	{
		ServerItem toRemove = null;
		for (ServerItem item : getInventory())
			if (item.getType().equals(ServerWorld.MONEY_TYPE))
			{
				item.decreaseAmount(amount);
				if (item.getAmount() <= 0)
					toRemove = item;
			}

		if (toRemove != null)
			getInventory().remove(toRemove);
	}

	public void drop(int slot)
	{
		if (slot == DEFAULT_ARMOUR_SLOT)
		{
			dropItem(equippedArmour);
			equippedArmour = null;
			getBody().destroy();
			setBody(null);
		}
		else if (slot == DEFAULT_SHIELD_SLOT)
		{
			// dropItem(equippedShield);
			// equippedShield = null;
		}
		else
		{
			dropItem(equippedWeapons[slot]);
			equippedWeapons[slot] = null;
		}
	}

	/**
	 * Do a specific action when the action button is pressed
	 */
	public void performAction(int mouseX, int mouseY)
	{

		int xDist = mouseX - Client.Client.SCREEN_WIDTH / 2;
		int yDist = mouseY
				- (Client.Client.SCREEN_HEIGHT / 2 - getHeight() / 4);

		System.out.println("X/Y " + xDist + " " + yDist);

		double angle = Math.atan2(yDist, xDist);

		System.out.println("Angle: " + angle);

		if (isAlive() && canPerformAction)
		{
			if (rightClick)
			{
				actionDelay = 3600;
				action = "BLOCK";
				rightClick = false;
			}
			else
			{
				int random = (int) (Math.random() * 3);
				if (random == 0)
				{
					world.add(new ServerProjectile(getX() + getWidth() / 2,
							getY()
									+ getHeight() / 4, this, angle,
							ServerWorld.FIREBALL_TYPE));
				}
				else if (random == 1)
				{

					world.add(new ServerProjectile(getX() + getWidth() / 2,
							getY()
									+ getHeight() / 4, this, angle,
							ServerWorld.ICEBALL_TYPE));
				}
				else if (random == 2)
				{
					world.add(new ServerProjectile(getX() + getWidth() / 2,
							getY()
									+ getHeight() / 4, this, angle,
							ServerWorld.ARROW_TYPE));
				}
				actionDelay = 15;
				action = "FIRE";

			}

			// else if (weaponNo == 9 || equippedWeapons[weaponNo] == null)
			// {
			// action = "PUNCH";
			// actionDelay = 16;
			//
			// // List of creatures we've already punched so we dont hit them
			// // twice
			// ArrayList<ServerCreature> alreadyPunched = new
			// ArrayList<ServerCreature>();
			//
			// int startRow = (int) (getY() / ServerWorld.OBJECT_TILE_SIZE);
			// int endRow = (int) ((getY() + getHeight()) /
			// ServerWorld.OBJECT_TILE_SIZE);
			// int startColumn = (int) (getX() / ServerWorld.OBJECT_TILE_SIZE);
			// int endColumn = (int) ((getX() + getWidth()) /
			// ServerWorld.OBJECT_TILE_SIZE);
			//
			// for (int row = startRow; row <= endRow; row++)
			// {
			// for (int column = startColumn; column <= endColumn; column++)
			// {
			// for (ServerObject otherObject :
			// world.getObjectGrid()[row][column])
			// {
			// if (otherObject.getType().charAt(0) == ServerWorld.CREATURE_TYPE
			// && ((ServerCreature) otherObject)
			// .isAttackable()
			// && ((ServerCreature) otherObject)
			// .getTeam() != getTeam()
			// && collidesWith(otherObject)
			// && !alreadyPunched.contains(otherObject))
			// {
			// ((ServerCreature) otherObject).inflictDamage(
			// PUNCHING_DAMAGE + getBaseDamage(), 5);
			// alreadyPunched
			// .add((ServerCreature) otherObject);
			// }
			// }
			// }
			// }
			// }
			// else if (equippedWeapons[weaponNo].getType().contains(
			// ServerWorld.MELEE_TYPE))
			// {
			// actionDelay = equippedWeapons[weaponNo].getSwingSpeed()
			// + (int) (equippedWeapons[weaponNo].getSwingSpeed() / 4.0);
			// world.add(new ServerWeaponSwing(this, 0, -25,
			// equippedWeapons[weaponNo].getActionImage(),
			// (int) (Math.toDegrees(angle) + 0.5),
			// equippedWeapons[weaponNo].getSwingSpeed(),
			// equippedWeapons[weaponNo].getDamage()+getBaseDamage()));
			// action = "SWING";
			// }
			// else if (equippedWeapons[weaponNo].getType().contains(
			// ServerWorld.RANGED_TYPE))
			// {
			// action = "FIRE";
			// }
		}

		// if (weaponSelected == '0')
		// {
		// world.add(new ServerWeaponSwing(this, 0,-25, "DAIRON_0.png",
		// (int) (Math.toDegrees(angle) + 0.5), 7, 10, 10));
		// }
		// else if (weaponSelected == '1')
		// {
		// // Get the width and height of the image
		// int bulletWidth = Images.getGameImage("BULLET.png").getWidth();
		// int bulletHeight = Images.getGameImage("BULLET.png")
		// .getHeight();
		//
		// // Shoot the projectile for testing
		// double speed = 30;
		// double x = getX() + getWidth() / 2.0 - bulletWidth / 2.0;
		// double y = getY() + getHeight() / 2.0 - bulletHeight / 2.0;
		// double inaccuracy = 0;
		//
		// if (getHSpeed() != 0)
		// {
		// inaccuracy += Math.PI / 6;
		// }
		//
		// if (Math.abs(getVSpeed()) >= 3)
		// {
		// inaccuracy += Math.PI / 3;
		// }
		//
		// world.add(new ServerProjectile(x, y, -1, -1, 0,
		// this, "BULLET.png", speed, angle, inaccuracy,
		// ServerWorld.BULLET_TYPE));
		// }

		canPerformAction = false;
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
		equippedWeapons = new ServerWeapon[MAX_WEAPONS];

		if (equippedArmour != null)
			dropItem(equippedArmour);
		equippedArmour = null;
	}

	/**
	 * Damage the player a certain amount, and destroy if hp is 0 or below
	 * @param amount
	 */
	public void inflictDamage(int amount, double knockBack)
	{
		if (action.equals("BLOCK"))
		{
			amount = 0;
		}

		if (equippedArmour != null)
		{
			amount -= amount * equippedArmour.getArmour();
		}

		setHP(getHP() - amount);

		double damageX = Math.random() * getWidth() + getX();
		double damageY = Math.random() * getHeight() / 2 + getY() - getHeight()
				/ 3;
		world.add(new ServerDamageIndicator(damageX, damageY, Integer
				.toString(amount), ServerDamageIndicator.RED_TEXT, world));

		if (getHP() <= 0 && isAlive())
		{
			setAlive(false);
			// setHeight(getWidth());
			dropInventory();

			verticalMovement = 0;
			horizontalMovement = 0;

			if (getHead() != null)
			{
				getHead().destroy();
				setHead(null);
			}

			if (getBody() != null)
			{
				getBody().destroy();
				setBody(null);
			}

			setHSpeed(0);
			setVSpeed(0);

			setAttackable(false);

		}
		else
		{
			// Knock back the creature based on the knockback force
			// if (Math.abs(knockBack) - getKnockBackResistance() > 0)
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
	 * Player interacts with the environment
	 */
	public synchronized void interact()
	{
		// Send all the objects within all the object tiles in the player's
		// screen
		int startRow = (int) (getY() / ServerWorld.OBJECT_TILE_SIZE);
		int endRow = (int) (getY() / ServerWorld.OBJECT_TILE_SIZE);
		int startColumn = (int) (getX() / ServerWorld.OBJECT_TILE_SIZE);
		int endColumn = (int) (getX() / ServerWorld.OBJECT_TILE_SIZE);

		if (startRow < 0)
		{
			startRow = 0;
		}
		else if (endRow > world.getObjectGrid().length - 1)
		{
			endRow = world.getObjectGrid().length - 1;
		}

		if (startColumn < 0)
		{
			startColumn = 0;
		}
		else if (endColumn > world.getObjectGrid()[0].length - 1)
		{
			endColumn = world.getObjectGrid()[0].length - 1;
		}

		while (true)
		{
			try
			{
				for (int row = startRow; row <= endRow; row++)
				{
					for (int column = startColumn; column <= endColumn; column++)
					{
						for (ServerObject object : world.getObjectGrid()[row][column])
						{
							if (object.exists() && object.collidesWith(this))
							{
								// If vendor send shop to client
								if (object.getType().equals(
										ServerWorld.VENDOR_TYPE))
								{
									if (vendor == null
											&& !((ServerVendor) object)
													.isBusy())
									{
										vendor = (ServerVendor) object;
										vendor.setIsBusy(true);
										String newMessage = "V "
												+ vendor.getInventory().size();
										for (ServerItem item : vendor
												.getInventory())
											newMessage += String.format(
													" %s %s %d %d",
													item.getImage(),
													item.getType(),
													item.getAmount(),
													item.getCost());
										queueMessage(newMessage);
									}
									else if (vendor != null)
									{
										vendor.setIsBusy(false);
										vendor = null;
									}
									return;
								}

							}

						}

					}
				}
				break;
			}
			catch (ConcurrentModificationException e)
			{
				System.out.println("ConcurrentModificationException");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Set the direction while also changing the player's image
	 * @param newDirection
	 */
	public void setDirection(String newDirection)
	{
		if (isAlive())
		{
			setNextDirection(newDirection);
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
			this.message.append(" " + message);
		else
			this.message.append(message);
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
		message = new StringBuilder();
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
				+ item.getAmount() + " " + item.getCost());
	}

	public void equipWeapon(String itemType)
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

		equippedWeapons[pos] = (ServerWeapon) toRemove;
		getInventory().remove(toRemove);
	}

	public void equipShield(String itemType)
	{
		// First replace the shield in the inventory with the current shield, if
		// it exists
		// UNCOMMENT
		// if(equippedShield != null)
		{
			// getInventory().add(equippedShield)
		}

		ServerItem toRemove = null;
		for (ServerItem item : getInventory())
			if (item.getType().equals(itemType))
			{
				toRemove = item;
			}
		if (toRemove != null)
			getInventory().remove(toRemove);
		// UNCOMMENT
		// equippedShield = (ServerShield)toRemove;
	}

	public void equipArmour(String itemType)
	{
		// First replace the shield in the inventory with the current shield, if
		// it exists
		// UNCOMMENT
		if (equippedArmour != null)
		{
			getInventory().add(equippedArmour);
		}

		ServerItem toRemove = null;
		for (ServerItem item : getInventory())
		{
			if (item.getType().equals(itemType))
			{
				toRemove = item;
			}
		}
		if (toRemove != null)
		{
			getInventory().remove(toRemove);
		}
		// UNCOMMENT
		equippedArmour = (ServerArmour) toRemove;

		ServerAccessory newArmour = new ServerAccessory(this,
				equippedArmour.getArmourImage(), equippedArmour.getArmour());
		if (getBody() != null)
		{
			getBody().destroy();
		}
		setBody(newArmour);
		world.add(newArmour);

	}

	public void unequip(int slot)
	{
		if (getInventory().size() < MAX_INVENTORY)
		{
			if (slot == DEFAULT_ARMOUR_SLOT)
			{
				getInventory().add(equippedArmour);
				getBody().destroy();
				setBody(null);
				equippedArmour = null;
			}
			else if (slot == DEFAULT_SHIELD_SLOT)
			{
				// UNCOMMENT
				// getInventory().add(equippedShield);
				// equippedShield = null;
			}
			else
			{
				getInventory().add(equippedWeapons[slot]);
				equippedWeapons[slot] = null;
			}
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

	/**
	 * Whether or not the player is currently performing an action
	 * @return
	 */
	public boolean inAction()
	{
		return actionCounter >= 0;
	}

	public boolean isPerformingAction()
	{
		return performingAction;
	}

	public void setPerformingAction(boolean performingAction)
	{
		this.performingAction = performingAction;
	}

	public int getNewMouseX()
	{
		return newMouseX;
	}

	public void setNewMouseX(int newMouseX)
	{
		this.newMouseX = newMouseX;
	}

	public int getNewMouseY()
	{
		return newMouseY;
	}

	public void setNewMouseY(int newMouseY)
	{
		this.newMouseY = newMouseY;
	}

	public ServerAccessory getShield()
	{
		return shield;
	}

	public void setShield(ServerAccessory shield)
	{
		this.shield = shield;
	}

	public ServerAccessory getHead()
	{
		return head;
	}

	public void setHead(ServerAccessory head)
	{
		this.head = head;
	}

	public ServerAccessory getBody()
	{
		return body;
	}

	public void setBody(ServerAccessory body)
	{
		this.body = body;
	}

	public int getMana()
	{
		return mana;
	}

	public void setMana(int mana)
	{
		this.mana = mana;
	}

	public int getMaxMana()
	{
		return maxMana;
	}

	public void setMaxMana(int maxMana)
	{
		this.maxMana = maxMana;
	}

	public int getHorizontalMovement()
	{
		return horizontalMovement;
	}

	public void setHorizontalMovement(int horizontalMovement)
	{

		this.horizontalMovement = Math.min(horizontalMovement, MAX_HSPEED);
	}

	public int getVerticalMovement()
	{
		return verticalMovement;
	}

	public void setVerticalMovement(int verticalMovement)
	{
		this.verticalMovement = Math.min(verticalMovement, MAX_VSPEED);
	}

}
