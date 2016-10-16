package Server.Creatures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import Imports.Images;
import Server.ServerEngine;
import Server.ServerObject;
import Server.ServerObjectShown;
import Server.ServerWorld;
import Server.Effects.ServerText;
import Server.Items.ServerAccessory;
import Server.Items.ServerArmour;
import Server.Items.ServerItem;
import Server.Items.ServerMoney;
import Server.Items.ServerPotion;
import Server.Items.ServerProjectile;
import Server.Items.ServerWeapon;
import Server.Items.ServerWeaponSwing;
import Tools.RowCol;

/**
 * The player (Type 'P')
 * 
 * @author William Xu & Alex Raita
 *
 */
public class ServerPlayer extends ServerCreature implements Runnable {
	// The starting locations of the player, to change later on
	public final static int PLAYER_X = 50;
	public final static int PLAYER_Y = 50;
	public final static int MAX_INVENTORY = 32;
	public static final int MAX_WEAPONS = 4;

	public final static int DEFAULT_WIDTH = 34;
	public final static int DEFAULT_HEIGHT = 90;

	public static final int DEFAULT_WEAPON_SLOT = 9;
	public static final int DEFAULT_ARMOUR_SLOT = -1;

	// The starting mana and hp for the player
	public final static int PLAYER_START_HP = 100;
	public final static int PLAYER_START_MANA = 100;

	// Initial jump and move speeds of the player
	public final static int MOVE_SPEED = 5;
	public final static int JUMP_SPEED = 20;

	public final static int MAX_HSPEED = 8;
	public final static int MAX_VSPEED = 24;
	public final static int MAX_DMGADD = 50;
	public final static int PLAYER_MAX_HP = 250;
	public final static int PLAYER_MAX_MANA = 250;

	private StringBuilder message = new StringBuilder();

	private boolean disconnected = false;

	private PrintWriter output;
	private BufferedReader input;
	private ServerEngine engine;
	private ServerWorld world;

	private int respawnXSpeed = MOVE_SPEED;
	private int respawnYSpeed = JUMP_SPEED;

	// The width and height of the screen of this specific player
	private int playerScreenWidth = 1620;
	private int playerScreenHeight = 1080;

	private ServerCastle castle;
	
	/**
	 * Whether the game is over or not
	 */
	private boolean endGame = false;
	private boolean closeWriter = false;
	private int losingTeam;

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
	private int actionSpeed = 13;

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
	 * The string for the base image not including the specific animation frame
	 */
	private String baseImage;

	/**
	 * Whether or not the action was a right click
	 */
	private boolean rightClick = false;

	/**
	 * The weapon being held by the player during action (ex. bows and wands)
	 */
	private ServerObject heldWeapon;

	/**
	 * Signaling the writer thread to flush
	 */
	private boolean flushWriterNow = false;

	/**
	 * Whether or not the player is trying to drop from a platform
	 */
	private boolean isDropping = false;

	/**
	 * Stores the mana the player currently has
	 */
	private int mana = PLAYER_START_MANA;

	/**
	 * Stores the maximum possible mana for the player
	 */
	private int maxMana = PLAYER_START_MANA;

	/**
	 * When the player joined the server
	 */
	private long joinTime;

	/**
	 * The current text floating on top of the player
	 */
	private String currentText = "";

	/**
	 * The time when the player last sent a message
	 */
	private long textStartTime = 0;

	/**
	 * The amount of time the text is allowed to be shown
	 */
	private int textDuration = 0;

	/**
	 * Constructor for a player in the server
	 * 
	 * @param socket
	 *            the connection between the client and the server
	 * @param engine
	 *            the engine the server is running on
	 * @param x
	 *            the x coordinate of the player
	 * @param y
	 *            the y coordinate of the player
	 * @param width
	 *            the width of the player
	 * @param height
	 *            the height of the player
	 * @param ID
	 *            the identifier of the player
	 * @param image
	 *            the image of the player
	 */
	public ServerPlayer(double x, double y, int width, int height,
			double relativeDrawX, double relativeDrawY, double gravity,
			String skinColour, Socket socket, ServerEngine engine,
			ServerWorld world, BufferedReader input, PrintWriter output) {
		super(x, y, width, height, relativeDrawX, relativeDrawY, gravity,
				"BASE_" + skinColour + "_RIGHT_0_0", ServerWorld.PLAYER_TYPE,
				PLAYER_START_HP, world, true);

		// Set default name of the player
		setName("Player");

		// Set a random hair style for the player
		String hair = "HAIR0BEIGE";
		int randomHair = (int) (Math.random() * 8);
		switch (randomHair) {
		case 1:
			hair = "HAIR1BEIGE";
			break;
		case 2:
			hair = "HAIR0BLACK";
			break;
		case 3:
			hair = "HAIR1BLACK";
			break;
		case 4:
			hair = "HAIR0BLOND";
			break;
		case 5:
			hair = "HAIR1BLOND";
			break;
		case 6:
			hair = "HAIR0GREY";
			break;
		case 7:
			hair = "HAIR1GREY";
			break;
		}
		ServerAccessory newHair = new ServerAccessory(this, hair, 0, world);
		setHead(newHair);
		world.add(newHair);
	
		
		// Set the initial variables
		actionDelay = 20;
		actionSpeed = 13;
		canPerformAction = true;
		action = "NOTHING";
		performingAction = false;
		newMouseX = 0;
		newMouseY = 0;

		// Set the action counter to -1 when not used
		actionCounter = -1;

		// Import the socket, server, and world
		this.engine = engine;
		xUpdated = true;
		yUpdated = true;
		this.joinTime = world.getWorldCounter();

		this.output = output;
		this.input = input;
		// Send the 2D grid of the world to the client
		sendMap();

		// Send the player's information
		sendMessage(toChars(getID()) + " " + toChars((int) (x + 0.5)) + " " + toChars((int) (y + 0.5))
				+ " "
				+ Images.getImageIndex("BASE_" + skinColour + "_RIGHT_0_0")
				+ " " + getTeam());

		if(getTeam() == RED_TEAM)
			castle = world.getRedCastle();
		else
			castle = world.getBlueCastle();
		
		baseImage = "BASE_" + skinColour;

		// Give the player random start weapon(s)
		int randomStartWeapon = (int) (Math.random() * 3);

		switch (randomStartWeapon) {
		case 0:
			addItem(new ServerWeapon(0, 0, ServerWorld.SWORD_TYPE
					+ ServerWorld.STONE_TIER, world));
			break;
		case 1:
			addItem(new ServerWeapon(0, 0, ServerWorld.AX_TYPE
					+ ServerWorld.STONE_TIER, world));
			break;
		case 2:
			addItem(new ServerWeapon(0, 0, ServerWorld.SLINGSHOT_TYPE, world));
			break;
		}

		// Start the player off with some gold
		addItem(new ServerMoney(0, 0, 10, world));

		// Use a separate thread to print to the client to prevent the client
		// from lagging the server itself
		Thread writer = new Thread(new WriterThread());
		writer.start();
	}

	/**
	 * Send the player the entire map
	 */
	public void sendMap() {
		world = engine.getWorld();
		char[][] grid = world.getGrid();

		// Send to the client the height and width of the grid, the starting x
		// and y position of the grid (top left) and the side length of each
		// tile
		printMessage(grid.length + " " + grid[0].length + " "
				+ ServerWorld.TILE_SIZE);
		for (int row = 0; row < grid.length; row++) {
			String message = "";
			for (int column = 0; column < grid[0].length; column++) {
				message += grid[row][column];
			}
			printMessage(message);
		}
		flush();
	}

	/**
	 * Update the player after each tick
	 */
	@Override
	public void update() {
		if (exists()) {
			// Change the player's facing direction after its current action
			if (actionCounter < 0) {
				super.setDirection(getNextDirection());
			}

			// Update the counter for weapon delay
			if (actionCounter < actionDelay) {
				if (!canPerformAction) {
					actionCounter++;
				}
			} else {
				if (heldWeapon != null) {
					heldWeapon.destroy();
					heldWeapon = null;
				}
				actionCounter = -1;
				action = "NOTHING";
				canPerformAction = true;
			}

			// Update the animation of the player and its accessories
			// The row and column of the frame in the sprite sheet for the image
			setRowCol(new RowCol(0, 0));
			if (actionCounter >= 0) {
				if (action.equals("SWING")) {
					if (actionCounter < 1.0 * actionSpeed / 4.0) {
						setRowCol(new RowCol(2, 0));
					} else if (actionCounter < 1.0 * actionSpeed / 2.0) {
						setRowCol(new RowCol(2, 1));
					} else if (actionCounter < 1.0 * actionSpeed / 4.0 * 3) {
						setRowCol(new RowCol(2, 2));
					} else if (actionCounter < actionSpeed) {
						setRowCol(new RowCol(2, 3));
					}
				} else if (action.equals("PUNCH")) {
					if (actionCounter < 5) {
						setRowCol(new RowCol(2, 7));
					} else if (actionCounter < 16) {
						setRowCol(new RowCol(2, 8));
						if (!isHasPunched()) {
							punch((int) Math.ceil(PUNCHING_DAMAGE
									* (1 + getBaseDamage() / 100.0)));
							setHasPunched(true);
						}
					}
				} else if (action.equals("BOW")) {
					setRowCol(new RowCol(2, 7));
					if (heldWeapon != null) {
						heldWeapon.setX(getDrawX());
						heldWeapon.setY(getDrawY());
					}
				} else if (action.equals("WAND")) {
					setRowCol(new RowCol(2, 5));
					if (heldWeapon != null) {
						if (getDirection().equals("LEFT")) {
							heldWeapon.setX(getDrawX() - (90 - 64));
						} else {
							heldWeapon.setX(getDrawX());
						}
						heldWeapon.setY(getDrawY());
					}
				} else if (action.equals("BLOCK")) {
					setRowCol(new RowCol(2, 9));
				}
			} else if (getHSpeed() != 0 && isOnSurface()) {
				int checkFrame = (int) (world.getWorldCounter() % 30);
				if (checkFrame < 5) {
					setRowCol(new RowCol(0, 1));
				} else if (checkFrame < 10) {
					setRowCol(new RowCol(0, 2));
				} else if (checkFrame < 15) {
					setRowCol(new RowCol(0, 3));
				} else if (checkFrame < 20) {
					setRowCol(new RowCol(0, 4));
				} else if (checkFrame < 25) {
					setRowCol(new RowCol(0, 5));
				} else {
					setRowCol(new RowCol(0, 6));
				}
			} else if (!isAlive()) {
				if (deathCounter < 0) {
					deathCounter = world.getWorldCounter();
					setRowCol(new RowCol(5, 1));
				} else if (world.getWorldCounter() - deathCounter < 10) {
					setRowCol(new RowCol(5, 1));
				} else if (world.getWorldCounter() - deathCounter < 20) {
					setRowCol(new RowCol(5, 2));
				} else if (world.getWorldCounter() - deathCounter < 600) {
					setRowCol(new RowCol(5, 4));
				} else {

					int randomStartWeapon = (int) (Math.random() * 3);

					switch (randomStartWeapon) {
					case 0:
						addItem(new ServerWeapon(0, 0, ServerWorld.SWORD_TYPE
								+ ServerWorld.STONE_TIER, world));
						break;
					case 1:
						addItem(new ServerWeapon(0, 0, ServerWorld.AX_TYPE
								+ ServerWorld.STONE_TIER, world));
						break;
					case 2:
						addItem(new ServerWeapon(0, 0,
								ServerWorld.SLINGSHOT_TYPE, world));
						break;
					}

					setAlive(true);

					verticalMovement = respawnYSpeed;
					horizontalMovement = respawnXSpeed;

					if (getTeam() == RED_TEAM) {
						setX(world.getRedCastleX());
						setY(world.getRedCastleY());

					} else {
						setX(world.getBlueCastleX());
						setY(world.getBlueCastleY());
					}

					setHP(getMaxHP());
					mana = maxMana;

					setAttackable(true);
					deathCounter = -1;
				}
			} else if (Math.abs(getVSpeed()) < 5 && !isOnSurface()) {
				setRowCol(new RowCol(1, 8));
			} else if (getVSpeed() < 0) {
				setRowCol(new RowCol(1, 7));
			} else if (getVSpeed() > 0) {
				setRowCol(new RowCol(1, 9));
			}

			// Update the player's image
			setImage(baseImage + "_" + getDirection() + "_"
					+ getRowCol().getRow() + "_" + getRowCol().getColumn() + "");
			if (getHead() != null) {
				getHead().update(getDirection(), getRowCol());
			}
			if (getBody() != null) {
				getBody().update(getDirection(), getRowCol());
			}
		}
	}

	/**
	 * Send to the client all the updated values (x and y must be rounded to
	 * closest integer)
	 */
	public void updateClient() {
		// Slowly regenerate the player's mana and hp, and send it to the client
		if (world.getWorldCounter() % 40 == 0 && mana < maxMana) {
			mana++;
		}
		if (world.getWorldCounter() % 80 == 0 && getHP() < getMaxHP()
				&& getHP() > 0) {
			setHP(getHP() + 1);
		}

		if (world.getWorldCounter() - textStartTime > textDuration) {
			currentText = "";
		}

		if (exists()) {
			// Send all the objects within all the object tiles in the
			// player's screen
			int startRow = (int) ((getY() + getHeight() / 2 - playerScreenHeight) / ServerWorld.OBJECT_TILE_SIZE);
			int endRow = (int) ((getY() + getHeight() / 2 + playerScreenHeight) / ServerWorld.OBJECT_TILE_SIZE);
			int startColumn = (int) ((getX() + getWidth() / 2 - playerScreenWidth) / ServerWorld.OBJECT_TILE_SIZE);
			int endColumn = (int) ((getX() + getWidth() / 2 + playerScreenWidth) / ServerWorld.OBJECT_TILE_SIZE);

			if (startRow < 0) {
				startRow = 0;
			}
			if (endRow > world.getObjectGrid().length - 1) {
				endRow = world.getObjectGrid().length - 1;
			}
			if (startColumn < 0) {
				startColumn = 0;
			}
			if (endColumn > world.getObjectGrid()[0].length - 1) {
				endColumn = world.getObjectGrid()[0].length - 1;
			}

			// Send information to the client about all the objects
			for (int row = startRow; row <= endRow; row++) {
				for (int column = startColumn; column <= endColumn; column++) {
					for (ServerObject object : world.getObjectGrid()[row][column]) {
						if (object.exists() && object.isVisible()) {
							int x = (int) (object.getX() + 0.5);
							int y = (int) (object.getY() + 0.5);
							int team = ServerCreature.NEUTRAL;

							switch (object.getType().charAt(0)) {
							case ServerWorld.CREATURE_TYPE:
								x = ((ServerCreature) object).getDrawX();
								y = ((ServerCreature) object).getDrawY();
								team = ((ServerCreature) object).getTeam();
								if (object.getType().equals(
										ServerWorld.PLAYER_TYPE)) {
									queueMessage("O "
											+ toChars(object.getID())
											+ " "
											+ toChars(x)
											+ " "
											+ toChars(y)
											+ " "
											+ object.getImageIndex()
											+ " "
											+ team
											+ " "
											+ object.getType()
											+ " "
											+ ((ServerPlayer) object).getName()
													.split(" ").length
											+ " "
											+ ((ServerPlayer) object).getName()
											+ '`'
											+ ((ServerPlayer) object)
													.getCurrentText());
									continue;
								}

								break;
							case ServerWorld.PROJECTILE_TYPE:
								x = ((ServerProjectile) object).getDrawX();
								y = ((ServerProjectile) object).getDrawY();
								break;
							case ServerWorld.TEXT_TYPE:
								queueMessage("t " + toChars(object.getID()) + " " + toChars(x)
										+ " " + toChars(y) + " " + object.getImage());
								continue;
							}

							// If it's any other object
							queueMessage("O " + toChars(object.getID()) + " " + toChars(x) + " "
									+ toChars(y) + " " + object.getImageIndex() + " "
									+ team + " " + object.getType() + " " + "{");

						} else if (object.getType().charAt(0) != ServerWorld.TEXT_TYPE) {
							queueMessage("R " + toChars(object.getID()));
						}
					}
				}
			}

			// Try to move the player in the direction that the key is
			// holding
			if (movingDirection != 0) {
				setHSpeed(movingDirection * horizontalMovement);
			}

			// Check if the vendor is out of range
			if (vendor != null
					&& (!collidesWith(vendor) || getHP() <= 0 || isDisconnected())) {
				vendor.setIsBusy(false);
				vendor = null;
				queueMessage("C");
			}
			
			if(castle.isOpen() && (!collidesWith(castle) || getHP() <= 0 || isDisconnected())) {
				castle.close();
				queueMessage("C");
			}

			// Send the player's HP, Mana, and speed
			queueMessage("Q " + mana);
			queueMessage("K " + maxMana);
			queueMessage("L " + getHP());
			queueMessage("M " + getMaxHP());
			if (isAlive()) {
				queueMessage("S " + horizontalMovement);
				queueMessage("J " + verticalMovement);
			}
			queueMessage("XB " + world.getBlueCastle().getHP() + " "
					+ world.getBlueCastle().getTier() + " "
					+ world.getBlueCastle().getMoney() + " "
					+ world.getBlueCastle().getMaxHP());
			queueMessage("XR " + world.getRedCastle().getHP() + " "
					+ world.getRedCastle().getTier() + " "
					+ world.getRedCastle().getMoney() + " "
					+ world.getRedCastle().getMaxHP());
			if (equippedArmour != null)
				queueMessage(String
						.format("A %.2f", equippedArmour.getArmour()));
			else
				queueMessage(String.format("A 0"));

			// Send the player's current damage
			int currentDamage = PUNCHING_DAMAGE;
			int weaponNo = weaponSelected - '0';
			if (weaponNo != DEFAULT_WEAPON_SLOT
					&& equippedWeapons[weaponNo] != null) {
				currentDamage = equippedWeapons[weaponNo].getDamage();
			}
			queueMessage("D " + currentDamage + " " + getBaseDamage());

			while (message.length() < 4000) {
				queueMessage("L " + getHP());
			}
			// Send the current time in the world
			queueMessage("T " + toChars(world.getWorldTime()));

			// Signal a repaint
			queueMessage("U");
			flushWriterNow = true;
		}

	}

	/**
	 * Thread for printing the writer to the client
	 * 
	 * @author William Xu && Alex Raita
	 *
	 */
	class WriterThread implements Runnable {
		@Override
		public void run() {
			while (!closeWriter) {
				if (flushWriterNow) {
					flushWriter();
					flushWriterNow = false;
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	/**
	 * Main thread of the class receiving input from the player
	 */
	public void run() {
		while (!endGame) {
			try {
				// Read the next line the player sent in
				String command = input.readLine();

				// Execute the player's action based on the command received
				// from the client
				if ((command.charAt(0) == 'A' || command.charAt(0) == 'a')
						&& isOnSurface() && !performingAction && isAlive()) {
					// If the client tries to send an invalid message
					try {
						String[] tokens = command.split(" ");
						performingAction = true;
						newMouseX = Integer.parseInt(tokens[1]);
						newMouseY = Integer.parseInt(tokens[2]);
						if (command.charAt(0) == 'a') {
							rightClick = true;
						} else {
							rightClick = false;
						}
					} catch (Exception e) {

					}
				} else if (command.equals("!a") && isAlive()) {
					actionCounter = actionDelay;
				} else if (command.equals("D") && isAlive()) {
					isDropping = true;
				} else if (command.equals("!D") && isAlive()) {
					isDropping = false;
				} else if (command.equals("R") && isAlive()) {
					setHSpeed(horizontalMovement);
					movingDirection = 1;
				} else if (command.equals("!R")) {
					movingDirection = 0;
					if (getHSpeed() > 0) {
						setHSpeed(0);
					}
				} else if (command.equals("L") && isAlive()) {
					movingDirection = -1;
					setHSpeed(-horizontalMovement);
				} else if (command.equals("!L")) {
					movingDirection = 0;
					if (getHSpeed() < 0) {
						setHSpeed(0);
					}
				} else if (command.equals("U") && isOnSurface() && isAlive()
						&& !inAction()) {
					setVSpeed(-verticalMovement);
					setOnSurface(false);
				} else if (command.equalsIgnoreCase("X")
						&& world.getWorldCounter() - joinTime <= 1800) {
					inflictDamage(10000, this);
					if (getTeam() == RED_TEAM) {
						setTeam(BLUE_TEAM);
						while (true) {
							try {
								if (!world.getBlueTeam().contains(this))
									world.getBlueTeam().add(this);
								world.getRedTeam().remove(this);
								break;
							} catch (ConcurrentModificationException E) {
							}
						}
					} else {
						setTeam(RED_TEAM);
						while (true) {
							try {
								world.getBlueTeam().remove(this);
								if (!world.getRedTeam().contains(this))
									world.getRedTeam().add(this);
								break;
							} catch (ConcurrentModificationException E) {

							}
						}
					}

				} else if (command.equals("DR")) {
					setDirection("RIGHT");
				} else if (command.equals("DL")) {
					setDirection("LEFT");
				} else if (command.equals("P")) {
					sendMessage("P");
				}

				// Player uses the chat
				else if (command.length() >= 3 && command.charAt(0) == 'C') {
					String message = command.substring(2);
					String[] tokens = message.split(" ");
					if (tokens[0].equals("/t")) {
						engine.broadCastTeam("CH " + "T "
								+ (getTeam() + getName()).split(" ").length
								+ " " + getTeam() + getName() + " "
								+ tokens.length + " " + message, getTeam());
					} else {
						engine.broadcast("CH " + "E "
								+ (getTeam() + getName()).split(" ").length
								+ " " + getTeam() + getName() + " "
								+ tokens.length + " " + message);

					}

					String text = "";
					for (int no = 0; no < message.length(); no++) {
						if (message.charAt(no) == ' ') {
							text += '_';
						} else {
							text += message.charAt(no);
						}
					}

					if (text.length() > 0) {
						currentText = text;
						textStartTime = world.getWorldCounter();
						textDuration = (int) (60 * 3 + text.length() * 60 * 0.1);
					}

				} else if (command.length() >= 2
						&& command.substring(0, 2).equals("Dr")) {
					try {
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
					// If the player sends a bad message
					catch (Exception E) {

					}
				} else if (command.charAt(0) == 'M') {
					try {
						// Move to inventory
						if (command.charAt(1) == 'I') {
							unequip(Integer.parseInt(command.substring(3)));
						}
						// Move to equipped weapons
						else if (command.charAt(1) == 'W') {
							equipWeapon(command.substring(3));
						} else if (command.charAt(1) == 'A') {
							equipArmour(command.substring(3));
						}
					} catch (Exception E) {

					}
				}
				// This is temporary for selecting a gun or a sword
				else if (command.charAt(0) == 'W') {
					try {
						weaponSelected = command.charAt(1);
					} catch (Exception E) {

					}
				} else if (command.charAt(0) == 'B' && vendor != null) {
					ServerItem vendorItem = null;
					String itemName = "";
					try {
						itemName = command.substring(2);
					} catch (Exception E) {
						continue;
					}
					for (ServerItem item : vendor.getInventory())
						if (item.getType().equals(itemName))
							vendorItem = item;

					if (vendorItem != null
							&& getMoney() >= vendorItem.getCost()) {
						decreaseMoney(vendorItem.getCost());
						vendor.drop(vendorItem.getType());
					}
				} else if (command.charAt(0) == 'E') {
					interact();
				} else if (command.charAt(0) == 'S' && vendor != null) {
					String type = "";
					try {
						type = command.substring(2);
					} catch (Exception E) {
						continue;
					}
					if (!type.equals(ServerWorld.MONEY_TYPE)) {
						sell(type);
						queueMessage("SI " + type);
					}
				} else if (command.length() > 2
						&& command.substring(0, 2).equals("Na")) {
					try {
						setName(command.substring(3));
					} catch (Exception E) {
						continue;
					}
					// Maybe broadcast name change later
				}
				// Adjust the screen width and height for the player
				else if (command.charAt(0) == 's') {
					try {
						String[] tokens = command.split(" ");
						playerScreenWidth = Integer.parseInt(tokens[1]);
						playerScreenHeight = Integer.parseInt(tokens[2]);
					} catch (Exception E) {
						continue;
					}
				}
			} catch (IOException e) {
				break;
			} catch (NullPointerException e) {
				break;
			}
		}

		if (endGame)
			return;

		// If the buffered reader breaks, the player has disconnected
		if (vendor != null) {
			vendor.setIsBusy(false);
			vendor = null;
		}
		System.out.println("A client has disconnected");
		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("Error closing buffered reader");
		}

		output.close();
		disconnected = true;

		// Destroy all the accessories the player has
		if (getHead() != null) {
			getHead().destroy();
			setHead(null);
		}
		if (getBody() != null) {
			getBody().destroy();
			setBody(null);
		}
		setX(0);
		setY(0);
		destroy();
		engine.removePlayer(this);
	}

	/**
	 * Sell a specific item to the shop
	 * 
	 * @param type
	 *            the item to sell to the shop
	 */
	public void sell(String type) {
		ServerItem toRemove = null;
		for (ServerItem item : getInventory())
			if (item.getType().equals(type)) {
				if (item.getAmount() > 1) {
					item.decreaseAmount();
					vendor.addItem(ServerItem.copy(item, world));
				} else {
					toRemove = item;
					vendor.addItem(item);
				}

				String newMessage = String
						.format("VS %s %s %d %d", item.getImageIndex(),
								item.getType(), 1, item.getCost());
				queueMessage(newMessage);

				increaseMoney((item.getCost() + 1) / 2);
				break;
			}
		if (toRemove != null) {
			getInventory().remove(toRemove);
		}

	}

	/**
	 * Add money to the player's inventory
	 * 
	 * @param amount
	 */
	public void increaseMoney(int amount) {
		ServerMoney newMoney = new ServerMoney(getX() + getWidth() / 2, getY()
				+ getHeight() / 2, amount, world);
		newMoney.makeExist();
		if (vendor != null) {
			newMoney.setSource(vendor);
		}
		newMoney.startCoolDown(world.getWorldCounter());
		world.add(newMoney);
	}

	/**
	 * Get the amount of money the player has
	 * 
	 * @return the amount of money
	 */
	public int getMoney() {
		for (ServerItem item : getInventory()) {
			if (item.getType().equals(ServerWorld.MONEY_TYPE)) {
				return item.getAmount();
			}
		}
		return 0;
	}

	/**
	 * Remove money from the player's inventory
	 * 
	 * @param amount
	 *            the amount to decrease the player's money by
	 */
	public void decreaseMoney(int amount) {
		ServerItem toRemove = null;
		for (ServerItem item : getInventory()) {
			if (item.getType().equals(ServerWorld.MONEY_TYPE)) {
				item.decreaseAmount(amount);
				if (item.getAmount() <= 0) {
					toRemove = item;
				}
			}
		}
		if (toRemove != null) {
			getInventory().remove(toRemove);
		}
	}

	/**
	 * Drop an item from the player's inventory
	 * 
	 * @param slot
	 */
	public void drop(int slot) {
		if (slot == DEFAULT_ARMOUR_SLOT) {
			dropItem(equippedArmour);
			equippedArmour = null;
			getBody().destroy();
			setBody(null);
		} else {
			dropItem(equippedWeapons[slot]);
			equippedWeapons[slot] = null;
		}
	}

	/**
	 * Do a specific action when the action button is pressed
	 */
	public void performAction(int mouseX, int mouseY) {
		// Calculate the distance from the mouse to the player and the angle
		int xDist = mouseX - playerScreenWidth / 2 - getWidth() / 2;
		int yDist = mouseY
				- (playerScreenHeight / 2 - getHeight() / 2 + getHeight() / 3);

		double angle = Math.atan2(yDist, xDist);

		/**
		 * Perform a specific action based on the player's circumstances and the
		 * mouse button that was pressed
		 */
		if (isAlive() && canPerformAction) {
			int weaponNo = weaponSelected - '0';
			actionDelay = 15;
			if (rightClick) {
				actionDelay = 300;
				action = "BLOCK";
				rightClick = false;
			} else if (weaponNo == 9 || equippedWeapons[weaponNo] == null) {
				action = "PUNCH";
				actionDelay = 16;
				setHasPunched(false);
			} else if (equippedWeapons[weaponNo].getType().contains(
					ServerWorld.MELEE_TYPE)) {
				actionDelay = equippedWeapons[weaponNo].getActionDelay();
				actionSpeed = equippedWeapons[weaponNo].getActionSpeed();
				world.add(new ServerWeaponSwing(this, 0, -25,
						equippedWeapons[weaponNo].getActionImage(), (int) (Math
								.toDegrees(angle) + 0.5),
						equippedWeapons[weaponNo].getActionSpeed(), (int) Math
								.ceil(equippedWeapons[weaponNo].getDamage()
										* (1 + getBaseDamage() / 100.0))));
				action = "SWING";
			} else if (equippedWeapons[weaponNo].getType().contains(
					ServerWorld.RANGED_TYPE)) {
				int x = getDrawX();
				int y = getDrawY();

				String arrowType = "";
				String image = "";

				boolean canAttack = true;

				// Vary the player's attack based on the weapon currently
				// equipped
				switch (equippedWeapons[weaponNo].getType()) {
				case ServerWorld.SLINGSHOT_TYPE:
					action = "BOW";
					arrowType = ServerWorld.BULLET_TYPE;
					image = "SLINGSHOT";
					actionDelay = 16;
					break;
				case ServerWorld.WOODBOW_TYPE:
					action = "BOW";
					arrowType = ServerWorld.WOODARROW_TYPE;
					image = "WOODBOW";
					actionDelay = 16;
					break;
				case ServerWorld.STEELBOW_TYPE:
					action = "BOW";
					arrowType = ServerWorld.STEELARROW_TYPE;
					image = "STEELBOW";
					actionDelay = 16;
					break;
				case ServerWorld.MEGABOW_TYPE:
					action = "BOW";
					arrowType = ServerWorld.MEGAARROW_TYPE;
					image = "MEGABOW";
					actionDelay = 25;
					break;
				case ServerWorld.FIREWAND_TYPE:
					action = "WAND";
					if (mana >= ServerWeapon.FIREWAND_MANA) {
						mana -= ServerWeapon.FIREWAND_MANA;
					} else {
						canAttack = false;
					}
					arrowType = ServerWorld.FIREBALL_TYPE;
					image = "FIREWAND";
					if (getDirection().equals("LEFT")) {
						x -= 90 - 64;
					}
					actionDelay = 25;
					break;
				case ServerWorld.ICEWAND_TYPE:
					action = "WAND";
					if (mana >= ServerWeapon.ICEWAND_MANA) {
						mana -= ServerWeapon.ICEWAND_MANA;
					} else {
						canAttack = false;
					}
					arrowType = ServerWorld.ICEBALL_TYPE;
					image = "ICEWAND";
					if (getDirection().equals("LEFT")) {
						x -= 90 - 64;
					}
					actionDelay = 30;
					break;
				case ServerWorld.DARKWAND_TYPE:
					action = "WAND";
					if (mana >= ServerWeapon.DARKWAND_MANA) {
						mana -= ServerWeapon.DARKWAND_MANA;
					} else {
						canAttack = false;
					}
					arrowType = ServerWorld.DARKBALL_TYPE;
					image = "DARKWAND";
					if (getDirection().equals("LEFT")) {
						x -= 90 - 64;
					}
					actionDelay = 30;
					break;
				}

				if (canAttack) {
					world.add(new ServerProjectile(getX() + getWidth() / 2,
							getY() + getHeight() / 3, this, angle, arrowType,
							world));

					if (getDirection().equals("LEFT")) {

						image += "_LEFT";
					} else {
						image += "_RIGHT";
					}

					heldWeapon = new ServerObjectShown(x, y, 0, 0, 0, image,
							ServerWorld.WEAPON_HOLD_TYPE, world.getEngine());
					heldWeapon.setSolid(false);
					world.add(heldWeapon);
				} else {
					action = "";
					actionDelay = 0;
					ServerText message = new ServerText(
							getX() + getWidth() / 2, getY() - getHeight() / 2,
							"!M", ServerText.PURPLE_TEXT, world);
					world.add(message);
				}
			}
		}
		canPerformAction = false;
	}

	/**
	 * Drop inventory and equipment
	 */
	public void dropInventory() {
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
	 * Damage the player a certain amount, and destroy if hp is 0 or below (With
	 * specific differences from regular creatures)
	 * 
	 * @param amount
	 *            the amount of damage to inflict
	 * @param source
	 *            the source of the attack
	 */
	@Override
	public void inflictDamage(int amount, ServerCreature source) {
		if (isAlive()) {
			if (equippedArmour != null) {
				amount -= amount * equippedArmour.getArmour();
			}

			if (amount <= 0) {
				amount = 1;
			}

			if (action.equals("BLOCK")) {
				amount = 0;
			}

			setHP(getHP() - amount);

			double damageX = Math.random() * getWidth() + getX();
			double damageY = Math.random() * getHeight() / 2 + getY()
					- getHeight() / 3;
			world.add(new ServerText(damageX, damageY,
					Integer.toString(amount), ServerText.RED_TEXT, world));

			// Play the death animation of the player when the HP drops to 0 or
			// below, and eventually respawn the player
			if (getHP() <= 0) {
				if (source.getTeam() == ServerCreature.NEUTRAL) {
					String firstName = getTeam() + getName();
					String secondName = ServerCreature.NEUTRAL
							+ source.getName();
					engine.broadcast("KF1 " + firstName.split(" ").length + " "
							+ firstName + " " + secondName.split(" ").length
							+ " " + secondName);
				} else {
					String firstName = source.getTeam() + source.getName();
					String secondName = getTeam() + getName();
					engine.broadcast("KF2 " + firstName.split(" ").length + " "
							+ firstName + " " + secondName.split(" ").length
							+ " " + secondName);
				}
				setAlive(false);

				dropInventory();

				verticalMovement = 0;
				horizontalMovement = 0;

				if (getBody() != null) {
					getBody().destroy();
					setBody(null);
				}
				setHSpeed(0);
				setVSpeed(0);

				setAttackable(false);
				isDropping = false;
			}
		}
	}

	/**
	 * Player interacts with the environment
	 */
	public synchronized void interact() {
		// Send all the objects within all the object tiles in the player's
		// screen
		int startRow = (int) (getY() / ServerWorld.OBJECT_TILE_SIZE);
		int endRow = (int) ((getY() + getHeight()) / ServerWorld.OBJECT_TILE_SIZE);
		int startColumn = (int) (getX() / ServerWorld.OBJECT_TILE_SIZE);
		int endColumn = (int) ((getX() + getWidth()) / ServerWorld.OBJECT_TILE_SIZE);

		if (startRow < 0) {
			startRow = 0;
		} else if (endRow > world.getObjectGrid().length - 1) {
			endRow = world.getObjectGrid().length - 1;
		}

		if (startColumn < 0) {
			startColumn = 0;
		} else if (endColumn > world.getObjectGrid()[0].length - 1) {
			endColumn = world.getObjectGrid()[0].length - 1;
		}

		while (true) {
			try {
				for (int row = startRow; row <= endRow; row++) {
					for (int column = startColumn; column <= endColumn; column++) {
						for (ServerObject object : world.getObjectGrid()[row][column]) {
							if (object.exists() && object.collidesWith(this)) {
								// If vendor send shop to client
								if (object.getType().equals(
										ServerWorld.VENDOR_TYPE)) {
									if (vendor == null
											&& !((ServerVendor) object)
													.isBusy()) {
										vendor = (ServerVendor) object;
										vendor.setIsBusy(true);
										String newMessage = "VB "
												+ vendor.getInventory().size();
										for (ServerItem item : vendor
												.getInventory())
											newMessage += String.format(
													" %d %s %d %d",
													item.getImageIndex(),
													item.getType(),
													item.getAmount(),
													item.getCost());
										queueMessage(newMessage);
									} else if (vendor != null) {
										vendor.setIsBusy(false);
										vendor = null;
									}
									return;
								}
								else if (object.getType().equals(
										ServerWorld.CASTLE_TYPE)) {
									//Make a shop
									if(!castle.isOpen())
										queueMessage("CS");
									else if (castle.isOpen())
									{
										castle.close();
									}
									
								}

							}

						}

					}
				}
				break;
			} catch (ConcurrentModificationException e) {
				System.out.println("ConcurrentModificationException");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Set the direction while also changing the player's image
	 * 
	 * @param newDirection
	 */
	public void setDirection(String newDirection) {
		if (isAlive()) {
			setNextDirection(newDirection);
		}
	}

	/**
	 * Send a message to the client (flushing included)
	 * 
	 * @param message
	 *            the string command to send to the client
	 */
	public void sendMessage(String message) {
		output.println(message);
		output.flush();
	}

	/**
	 * Queue a message without flushing
	 * 
	 * @param message
	 *            the string command to send to the client
	 */
	public void queueMessage(String message) {
		while (true) {
			try {
				this.message.append(" " + message);
				break;
			}

			catch (ArrayIndexOutOfBoundsException e) {
				System.out
						.println("String builder queue lagged and out of bounds happened");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Prints a message to the client
	 * 
	 * @param message
	 *            the message to be printed
	 */
	public void printMessage(String message) {
		output.println(message);
	}

	/**
	 * Flush all queued messages to the client
	 */
	public void flushWriter() {
		output.println(message);
		output.flush();

		if (endGame) {
			queueMessage("B " + losingTeam);
			output.println(message);
			output.flush();
			output.close();
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			closeWriter = true;

		}
		message = new StringBuilder("Z");
	}

	/**
	 * Flushes all messages
	 */
	public void flush() {
		output.flush();
	}

	/**
	 * Add an item to the player's inventory and also tell the client about it
	 */
	public void addItem(ServerItem item) {
		super.addItem(item);
		System.out.println("Added item");
		queueMessage("I " + item.getImageIndex() + " " + item.getType() + " "
				+ item.getAmount() + " " + item.getCost());
	}

	/**
	 * Equip a weapon onto the player
	 * 
	 * @param itemType
	 *            the weapon to equip
	 */
	public void equipWeapon(String itemType) {
		// Find next open spot in equipped
		int pos = 0;
		for (; pos < MAX_WEAPONS; pos++) {
			if (equippedWeapons[pos] == null)
				break;
		}

		// If there are no equip slots left
		if (pos == MAX_WEAPONS)
			return;

		// Find the item in the inventory
		ServerItem toRemove = null;
		for (ServerItem item : getInventory()) {
			if (item.getType().equals(itemType)) {
				toRemove = item;
				break;
			}
		}

		equippedWeapons[pos] = (ServerWeapon) toRemove;
		getInventory().remove(toRemove);
	}

	/**
	 * Equip a certain armor
	 * 
	 * @param itemType
	 *            the type of armor to equip
	 */
	public void equipArmour(String itemType) {
		// First replace the shield in the inventory with the current shield, if
		// it exists
		// UNCOMMENT
		if (equippedArmour != null) {
			getInventory().add(equippedArmour);
		}

		ServerItem toRemove = null;
		for (ServerItem item : getInventory()) {
			if (item.getType().equals(itemType)) {
				toRemove = item;
			}
		}
		if (toRemove != null) {
			getInventory().remove(toRemove);
		}

		equippedArmour = (ServerArmour) toRemove;

		ServerAccessory newArmour = new ServerAccessory(this,
				equippedArmour.getArmourImage(), equippedArmour.getArmour(),
				world);
		if (getBody() != null) {
			getBody().destroy();
		}
		setBody(newArmour);
		world.add(newArmour);

	}

	/**
	 * Remove a weapon or armour from the weapon or armour slot
	 * 
	 * @param slot
	 *            the slot to remove from
	 */
	public void unequip(int slot) {
		if (getInventory().size() < MAX_INVENTORY) {
			if (slot == DEFAULT_ARMOUR_SLOT) {
				getInventory().add(equippedArmour);
				getBody().destroy();
				setBody(null);
				equippedArmour = null;
			} else {
				getInventory().add(equippedWeapons[slot]);
				equippedWeapons[slot] = null;
			}
		}

	}

	// ///////////////////////
	// GETTERS AND SETTERS //
	// ///////////////////////
	public boolean isDisconnected() {
		return disconnected;
	}

	public void setDisconnected(boolean disconnected) {
		this.disconnected = disconnected;
	}

	public void setX(double x) {
		if (x != super.getX()) {
			xUpdated = true;
			super.setX(x);
		}
	}

	public void setY(double y) {
		if (y != super.getY()) {
			super.setY(y);
			yUpdated = true;
		}
	}

	public boolean isxUpdated() {
		return xUpdated;
	}

	public boolean isyUpdated() {
		return yUpdated;
	}

	/**
	 * Whether or not the player is currently performing an action
	 * 
	 * @return
	 */
	public boolean inAction() {
		return actionCounter >= 0;
	}

	public boolean isPerformingAction() {
		return performingAction;
	}

	public void setPerformingAction(boolean performingAction) {
		this.performingAction = performingAction;
	}

	public int getNewMouseX() {
		return newMouseX;
	}

	public void setNewMouseX(int newMouseX) {
		this.newMouseX = newMouseX;
	}

	public int getNewMouseY() {
		return newMouseY;
	}

	public void setNewMouseY(int newMouseY) {
		this.newMouseY = newMouseY;
	}

	public ServerAccessory getHead() {
		return head;
	}

	public void setHead(ServerAccessory head) {
		this.head = head;
	}

	public ServerAccessory getBody() {
		return body;
	}

	public void setBody(ServerAccessory body) {
		this.body = body;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public int getMaxMana() {
		return maxMana;
	}

	public void setMaxMana(int maxMana) {
		this.maxMana = Math.min(PLAYER_MAX_MANA, maxMana);
	}

	public int getHorizontalMovement() {
		return horizontalMovement;
	}

	public void setHorizontalMovement(int horizontalMovement) {

		this.horizontalMovement = Math.min(horizontalMovement, MAX_HSPEED);
		respawnXSpeed = this.horizontalMovement;
	}

	public int getVerticalMovement() {
		return verticalMovement;
	}

	public void setVerticalMovement(int verticalMovement) {
		this.verticalMovement = Math.min(verticalMovement, MAX_VSPEED);
		respawnYSpeed = this.verticalMovement;
	}

	public boolean isDropping() {
		return isDropping;
	}

	public void setDropping(boolean isDropping) {
		this.isDropping = isDropping;
	}

	public void setEndGame(boolean endGame, int losingTeam) {
		this.endGame = endGame;
		this.losingTeam = losingTeam;
	}

	public int getPlayerScreenWidth() {
		return playerScreenWidth;
	}

	public void setPlayerScreenWidth(int playerScreenWidth) {
		this.playerScreenWidth = playerScreenWidth;
	}

	public int getPlayerScreenHeight() {
		return playerScreenHeight;
	}

	public void setPlayerScreenHeight(int playerScreenHeight) {
		this.playerScreenHeight = playerScreenHeight;
	}

	public String getCurrentText() {
		return currentText;
	}

	public void setCurrentText(String currentText) {
		this.currentText = currentText;
	}
	
	public String toChars(int y)
	{
		int x = y;
		String ret = "";
		
		while(x > 0)
		{
			ret += (char)(x%95 + 33);
			x /= 95;
		}
		//System.out.println("StringRep: " +y+" "+ret);
		return ret;
	}

}
