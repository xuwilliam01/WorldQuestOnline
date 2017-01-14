package Server.Creatures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import Imports.Audio;
import Imports.Images;
import Server.ServerEngine;
import Server.SavedPlayer;
import Server.ServerObject;
import Server.ServerObjectShown;
import Server.ServerWorld;
import Server.Buildings.*;
import Server.Effects.ServerText;
import Server.Items.ServerAccessory;
import Server.Items.ServerArmour;
import Server.Items.ServerBuildingItem;
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
	public final static String NOTHING = "0";

	// The starting locations of the player, to change later on
	public final static int PLAYER_X = 50;
	public final static int PLAYER_Y = 50;
	public final static int MAX_INVENTORY = 32;
	public static final int MAX_WEAPONS = 4;

	public final static int DEFAULT_WIDTH = 34;
	public final static int DEFAULT_HEIGHT = 90;

	public static final int DEFAULT_WEAPON_SLOT = 9;
	public static final int DEFAULT_ARMOUR_SLOT = -1;

	public static final int PLAYER_BASE_HP = 100;
	public static final int PLAYER_BASE_MANA = 100;

	public static final int MAX_HP_POTS = 5;
	public static final int MAX_MANA_POTS = 5;

	private int numHPPots = 0;
	private int numManaPots = 0;


	// Initial jump and move speeds of the player
	public final static int DEFAULT_MOVE_SPEED = 5;
	public final static int DEFAULT_JUMP_SPEED = 20;

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

	private int respawnXSpeed;
	private int respawnYSpeed;

	// The width and height of the screen of this specific player
	private int playerScreenWidth = 1620;
	private int playerScreenHeight = 1080;

	private ServerCastle castle = null;

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
	private int horizontalMovement;

	/**
	 * The speed the player moves vertically
	 */
	private int verticalMovement;

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
	private String action = NOTHING;

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
	private ServerItem[] equippedWeapons = new ServerItem[MAX_WEAPONS];

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
	private int mana;

	/**
	 * Stores the maximum possible mana for the player
	 */
	private int maxMana;

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
	 * Stores the hologram for placing a building
	 */
	private ServerHologram hologram = null;

	private int kills = 0;
	private int deaths = 0;
	private int totalDamageDealt = 0;
	private int totalMoneySpent = 0;
	private int ping = 0;

	public final static int RELATIVE_X = -14;
	public final static int RELATIVE_Y = -38;

	private boolean ignoreClient = false;

	private String skinColour;
	private String hair;

	private ServerItem bestWeapon = null;
	private ServerItem bestArmour = null;
	
	private boolean castleOpen = false;
	
	private boolean disconnect = false;
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
	public ServerPlayer(String name, double x, double y, int width, int height,
			double gravity, String skinColour, String hair, Socket socket,
			ServerEngine engine, ServerWorld world, BufferedReader input,
			PrintWriter output) {
		super(x, y, width, height, RELATIVE_X, RELATIVE_Y, gravity, "BASE_"
				+ skinColour + "_RIGHT_0_0", ServerWorld.PLAYER_TYPE,
				world.getBluePlayerStartHP(), world, true); // player start HP doesn't
		// matter since it will
		// change
		disconnect = false;
		// Set the name of the player
		setName(name);

		// Set the initial variables
		actionDelay = 20;
		actionSpeed = 13;
		canPerformAction = true;
		action = NOTHING;
		performingAction = false;
		newMouseX = 0;
		newMouseY = 0;

		// Set the action counter to -1 when not used
		actionCounter = -1;

		// Import the socket, server, and world
		this.engine = engine;
		xUpdated = true;
		yUpdated = true;
		this.joinTime = getWorld().getWorldCounter();

		this.output = output;
		this.input = input;
		// Send the 2D grid of the world to the client
		sendMap();

		// Send the player's information
		sendMessage(toChars(getID()) + " " + toChars((int) (x + 0.5)) + " "
				+ toChars((int) (y + 0.5)) + " "
				+ Images.getImageIndex("BASE_" + skinColour + "_RIGHT_0_0")
				+ " " + getTeam());

		// System.out.printf("%.2f %.2f x, y%n", castle.getX(), castle.getY());
		this.skinColour = skinColour;
		baseImage = "BASE_" + skinColour;

		// Set a random hair style for the player
		this.hair = hair;
		setHair(hair);

		forcePlayerPos(x, y);
		forcePlayerSpeed(getHSpeed(), getVSpeed());

		// Use a separate thread to print to the client to prevent the client
		// from lagging the server itself
		Thread writer = new Thread(new WriterThread());
		writer.start();

	}

	public void initPlayer()
	{
		// Start the player off with some gold
		addItem(new ServerMoney(0, 0, 10, getWorld()));

		// Give the player random start weapon(s)
		int randomStartWeapon = (int) (Math.random() * 3);
		switch (randomStartWeapon) {
		case 0:
			addItem(new ServerWeapon(0, 0, ServerWorld.SWORD_TYPE
					+ ServerWorld.STONE_TIER, getWorld()));
			break;
		case 1:
			addItem(new ServerWeapon(0, 0, ServerWorld.AX_TYPE
					+ ServerWorld.STONE_TIER, getWorld()));
			break;
		case 2:
			addItem(new ServerWeapon(0, 0, ServerWorld.SLINGSHOT_TYPE, getWorld()));
			break;
		}

		addItem(new ServerWeapon(0, 0, ServerWorld.SWORD_TYPE
				+ ServerWorld.IRON_TIER, getWorld()));
		addItem(new ServerArmour(0, 0, ServerWorld.GREY_NINJA_ARMOUR, getWorld()));
		addItem(new ServerWeapon(0, 0, ServerWorld.WOODBOW_TYPE, getWorld()));
		addItem(new ServerWeapon(0, 0, ServerWorld.SLINGSHOT_TYPE, getWorld()));
		addItem(new ServerPotion(0,0, ServerWorld.HP_POTION_TYPE, getWorld()));
		addItem(new ServerPotion(0,0, ServerWorld.MANA_POTION_TYPE, getWorld()));
	}

	public void setHair(String hair)
	{
		ServerAccessory newHair = new ServerAccessory(this, hair, 0, getWorld());
		setHead(newHair);
		getWorld().add(newHair);
	}

	/**
	 * Send the player the entire map
	 */
	public void sendMap() {
		char[][] grid = getWorld().getGrid();

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

	private long lastCheck = 0;
	private double lastX = 0;
	private double lastY = 0;
	
	
	/**
	 * Update the player after each tick
	 */
	@Override
	public void update() {
		
		long time = 0;
		if (lastCheck == 0)
		{
			lastCheck = System.currentTimeMillis();
			lastX = getX();
			lastY = getY();
		}
		else if ((time=System.currentTimeMillis()-lastCheck) >= 3000)
		{
			if (lastX != -1 && isAlive() && Math.abs(lastX-getX())>1.5* horizontalMovement*((1.0*time)/ServerEngine.UPDATE_RATE))
			{
				disconnect = true;
			}
			lastX = getX();
			lastY = getY();
			lastCheck = System.currentTimeMillis();
		}
		
		
		if (exists()) {
			// Change the player's facing direction after its current action
			if (actionCounter < 0 && action == NOTHING) {
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
				action = NOTHING;
				canPerformAction = true;

				// Inform that the action is finished
				// queueMessage("^");
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
			} else if (!isAlive()) {
				if (deathCounter < 0) {
					deathCounter = getWorld().getWorldCounter();
					setRowCol(new RowCol(5, 1));
				} else if (getWorld().getWorldCounter() - deathCounter < 10) {
					setRowCol(new RowCol(5, 1));
				} else if (getWorld().getWorldCounter() - deathCounter < 20) {
					setRowCol(new RowCol(5, 2));
				} else if (getWorld().getWorldCounter() - deathCounter < 600) {
					setRowCol(new RowCol(5, 4));
				} else {
					int randomStartWeapon = (int) (Math.random() * 3);

					//If we already have a weapon, don't add a random one
					for(ServerItem sItem : getInventory())
					{
						if(sItem.getType().contains(ServerWorld.WEAPON_TYPE))
						{
							randomStartWeapon = -1;
							break;
						}
					}

					switch (randomStartWeapon) {
					case 0:
						addItem(new ServerWeapon(0, 0, ServerWorld.SWORD_TYPE
								+ ServerWorld.STONE_TIER, getWorld()));
						break;
					case 1:
						addItem(new ServerWeapon(0, 0, ServerWorld.AX_TYPE
								+ ServerWorld.STONE_TIER, getWorld()));
						break;
					case 2:
						addItem(new ServerWeapon(0, 0,
								ServerWorld.SLINGSHOT_TYPE, getWorld()));
						break;
					}

					setAlive(true);

					verticalMovement = respawnYSpeed;
					horizontalMovement = respawnXSpeed;

					if (getTeam() == RED_TEAM) {
						setX(getWorld().getRedCastleX());
						setY(getWorld().getRedCastleY());
						forcePlayerPos(getX(), getY());
						forcePlayerSpeed(getHSpeed(), getVSpeed());

					} else {
						setX(getWorld().getBlueCastleX());
						setY(getWorld().getBlueCastleY());
						forcePlayerPos(getX(), getY());
						forcePlayerSpeed(getHSpeed(), getVSpeed());
					}

					setHP(getMaxHP());
					mana = maxMana;

					setAttackable(true);
					deathCounter = -1;
					lastX = -1;
				}
			} else if (getHSpeed() != 0 && getVSpeed() == 0) {
				int checkFrame = (int) (getWorld().getWorldCounter() % 30);
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
			} else if (!isOnSurface()) {
				if (Math.abs(getVSpeed()) < 5) {
					setRowCol(new RowCol(1, 8));
				} else if (getVSpeed() < -5) {
					setRowCol(new RowCol(1, 7));
				} else if (getVSpeed() > 5) {
					setRowCol(new RowCol(1, 9));
				}
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
	
	

	public void setTeam(int team) {
		super.setTeam(team);

		// Set default player stats
		if (getTeam() == RED_TEAM) {
			respawnXSpeed = getWorld().getRedMoveSpeed();
			respawnYSpeed = getWorld().getRedJumpSpeed();
			horizontalMovement = getWorld().getRedMoveSpeed();
			verticalMovement = getWorld().getRedJumpSpeed();
			mana = getWorld().getRedPlayerStartMana();
			maxMana = getWorld().getRedPlayerStartMana();
			setMaxHP(getWorld().getRedPlayerStartHP());
			setHP(getMaxHP());
			setBaseDamage(getWorld().getRedStartBaseDamage());
		} else {
			respawnXSpeed = getWorld().getBlueMoveSpeed();
			respawnYSpeed = getWorld().getBlueJumpSpeed();
			horizontalMovement = getWorld().getBlueMoveSpeed();
			verticalMovement = getWorld().getBlueJumpSpeed();
			mana = getWorld().getBluePlayerStartMana();
			maxMana = getWorld().getBluePlayerStartMana();
			setMaxHP(getWorld().getBluePlayerStartHP());
			setHP(getMaxHP());
			setBaseDamage(getWorld().getBlueStartBaseDamage());
		}
	}

	/**
	 * Force a change in the player position
	 * 
	 * @param x
	 * @param y
	 */
	public void forcePlayerPos(double x, double y) {
		queueMessage("p " + x + " " + y);
	}

	/**
	 * Force a change in the player speed
	 * 
	 * @param hSpeed
	 * @param vSpeed
	 */
	public void forcePlayerSpeed(double hSpeed, double vSpeed) {
		queueMessage("* " + hSpeed + " " + vSpeed);
	}

	/**
	 * Send to the client all the updated values (x and y must be rounded to
	 * closest integer)
	 */
	public void updateClient() {
		// Slowly regenerate the player's mana and hp, and send it to the client
		if (getWorld().getWorldCounter() % 40 == 0 && mana < maxMana) {
			mana++;
		}
		if (getWorld().getWorldCounter() % 80 == 0 && getHP() < getMaxHP()
				&& getHP() > 0) {
			setHP(getHP() + 1);
		}

		if (getWorld().getWorldCounter() - textStartTime > textDuration) {
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
			if (endRow > getWorld().getObjectGrid().length - 1) {
				endRow = getWorld().getObjectGrid().length - 1;
			}
			if (startColumn < 0) {
				startColumn = 0;
			}
			if (endColumn > getWorld().getObjectGrid()[0].length - 1) {
				endColumn = getWorld().getObjectGrid()[0].length - 1;
			}

			// Only checks collisions for the hologram once
			boolean holoChecked = false;

			// Send information to the client about all the objects
			for (int row = startRow; row <= endRow; row++) {
				for (int column = startColumn; column <= endColumn; column++) {
					for (ServerObject object : getWorld().getObjectGrid()[row][column]) {
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
									if (object.getID() == getID()) {
										char inAction = '0';
										if (inAction() || !isAlive()) {
											inAction = '1';
										}

										queueMessage("O "
												+ toChars(object.getID())
												+ " "
												+ toChars(x)
												+ " "
												+ toChars(y)
												+ " "
												// + object.getX()
												// + " "
												// + object.getY()
												// + " "
												// + object.getHSpeed()
												// + " "
												// + object.getVSpeed()
												// + " "
												+ inAction
												+ " "
												+ object.getImageIndex()
												+ " "
												+ team
												+ " "
												+ object.getType()
												+ " "
												+ ((ServerPlayer) object)
												.getName().split(" ").length
												+ " "
												+ ((ServerPlayer) object)
												.getName()
												+ '`'
												+ ((ServerPlayer) object)
												.getCurrentText()
												+ " "
												+ Math.max(
														0,
														Math.round(100.0
																* ((ServerPlayer) object)
																.getHP()
																/ ((ServerPlayer) object)
																.getMaxHP())));
									} else {
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
												+ ((ServerPlayer) object)
												.getName().split(" ").length
												+ " "
												+ ((ServerPlayer) object)
												.getName()
												+ '`'
												+ ((ServerPlayer) object)
												.getCurrentText()
												+ " "
												+ Math.max(
														0,
														Math.round(100.0
																* ((ServerPlayer) object)
																.getHP()
																/ ((ServerPlayer) object)
																.getMaxHP())));
									}
									continue;
								}

								break;
							case ServerWorld.PROJECTILE_TYPE:
								x = ((ServerProjectile) object).getDrawX();
								y = ((ServerProjectile) object).getDrawY();
								break;
							case ServerWorld.TEXT_TYPE:
								queueMessage("t " + toChars(object.getID())
								+ " " + toChars(x) + " " + toChars(y)
								+ " " + object.getImage());
								continue;
							case ServerWorld.SOUND_TYPE:
								queueMessage("a "
										+ Audio.getIndex(object.getImage())
										+ " " + toChars((int) object.getX())
										+ " " + toChars((int) object.getY()));
								continue;
							}

							// If it's any other object
							if (object.getType().contains(
									ServerWorld.BUILDING_TYPE)) {
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
										+ "{ "
										+ Math.max(0, Math.round(100.0
												* ((ServerBuilding) object)
												.getHP()
												/ ((ServerBuilding) object)
												.getMaxHP())));
							} else {
								queueMessage("O " + toChars(object.getID())
								+ " " + toChars(x) + " " + toChars(y)
								+ " " + object.getImageIndex() + " "
								+ team + " " + object.getType() + " "
								+ "{ 0");
							}

						} else if (object.exists()
								&& object.getType().contains(
										ServerWorld.HOLOGRAM_TYPE)
								&& ((ServerHologram) object).getOwner() == this) {
							int weap = weaponSelected - '0';
							if (weap == DEFAULT_WEAPON_SLOT
									|| equippedWeapons[weap] == null
									|| !equippedWeapons[weap]
											.getType()
											.contains(
													ServerWorld.BUILDING_ITEM_TYPE)) {
								if (hologram != null) {
									hologram.destroy();
									hologram = null;
								}
							} else {
								if (!holoChecked) {
									hologram = (ServerHologram) object;
									double x = getNewMouseX()
											- object.getWidth() / 2 + getX()
											- playerScreenWidth / 2;
									double y = getNewMouseY()
											- object.getHeight() / 2 + getY()
											- playerScreenHeight / 2;

									// Where the client will draw it on their
									// screen. If 0, use the mouse's y
									int clientY = 0;

									object.setX(x);

									// Snap to nearest solid tile below it
									int tileCol = (int) ((x) / ServerWorld.TILE_SIZE);
									for (int tileRow = (int) ((y + object
											.getHeight()) / ServerWorld.TILE_SIZE) + 1; tileRow <= (int) ((getY()
													+ getHeight() / 2 + playerScreenHeight / 2) / ServerWorld.TILE_SIZE); tileRow++) {
										if (getWorld().getCollisionGrid()[tileRow][tileCol] == ServerWorld.SOLID_TILE
												&& getWorld()
												.getCollisionGrid()[tileRow - 1][tileCol] == ServerWorld.BACKGROUND_TILE) {
											boolean canPlace = true;
											for (int tileCol2 = tileCol; tileCol2 <= tileCol
													+ object.getWidth()
													/ ServerWorld.TILE_SIZE; tileCol2++) {
												if (getWorld()
														.getCollisionGrid()[tileRow][tileCol2] != ServerWorld.SOLID_TILE
														|| getWorld()
														.getCollisionGrid()[tileRow - 1][tileCol2] == ServerWorld.SOLID_TILE) {
													canPlace = false;
													break;
												}
											}
											if (!canPlace) {
												continue;
											}
											// Modify this 6 later to see whats
											// going on
											y = tileRow * ServerWorld.TILE_SIZE
													- object.getHeight() - 6;
											clientY = (int) (y
													+ object.getHeight() / 2
													- (getY()) + playerScreenHeight / 2);
											break;
										}
									}

									if (clientY == 0) {
										// Snap to best good solid tile above it
										for (int tileRow = (int) ((y + object
												.getHeight()) / ServerWorld.TILE_SIZE); tileRow >= (int) ((getY()
														+ getHeight() / 2 - playerScreenHeight / 2) / ServerWorld.TILE_SIZE); tileRow--) {
											if (getWorld().getCollisionGrid()[tileRow][tileCol] == ServerWorld.SOLID_TILE
													&& getWorld()
													.getCollisionGrid()[tileRow - 1][tileCol] == ServerWorld.BACKGROUND_TILE) {
												boolean canPlace = true;
												for (int tileCol2 = tileCol; tileCol2 <= tileCol
														+ object.getWidth()
														/ ServerWorld.TILE_SIZE; tileCol2++) {
													if (getWorld()
															.getCollisionGrid()[tileRow][tileCol2] != ServerWorld.SOLID_TILE
															|| getWorld()
															.getCollisionGrid()[tileRow - 1][tileCol2] == ServerWorld.SOLID_TILE) {
														canPlace = false;
														break;
													}
												}
												if (!canPlace) {
													continue;
												}
												// Modify the 6 later to see
												// whats going on
												y = tileRow
														* ServerWorld.TILE_SIZE
														- object.getHeight()
														- 6;
												clientY = (int) (y
														+ object.getHeight()
														/ 2 - (getY()) + playerScreenHeight / 2);
											}
										}

									}

									object.setY(y);

									int imageIndex = -1;
									if (hologram.canPlace())
										imageIndex = hologram.getGoodImage();
									else
										imageIndex = hologram.getBadImage();

									if (hologram.wantToPlace()
											&& hologram.canPlace()) {
										getWorld().add(
												hologram.toBuilding(getTeam()));
										queueMessage("b");
										equippedWeapons[weaponSelected - '0'] = null;
										hologram.destroy();
										hologram = null;
									} else {
										hologram.dontPlace();

										hologram.setCanPlace(true);
										queueMessage("H " + imageIndex + " "
												+ clientY);
									}
									holoChecked = true;
								}
							}
						} else if (object.getType().charAt(0) != ServerWorld.TEXT_TYPE) {
							if (object.getType().equals(
									ServerWorld.HOLOGRAM_TYPE))
								queueMessage("h");
							else if (object.getType().charAt(0) != ServerWorld.SOUND_TYPE)
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

			if (castle != null
					&& castleOpen
					&& (!collidesWith(castle) || getHP() <= 0 || isDisconnected())) {
				castleOpen = false;
				System.out.println("closing: " + collidesWith(castle));
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
			queueMessage("x " + getWorld().getBlueCastle().getHP() + " "
					+ getWorld().getBlueCastle().getTier() + " "
					+ getWorld().getBlueCastle().getMoney() + " "
					+ getWorld().getBlueCastle().getMaxHP() + " "
					+ toChars(getWorld().getBlueCastle().getXP()));
			queueMessage("X " + getWorld().getRedCastle().getHP() + " "
					+ getWorld().getRedCastle().getTier() + " "
					+ getWorld().getRedCastle().getMoney() + " "
					+ getWorld().getRedCastle().getMaxHP() + " "
					+ toChars(getWorld().getRedCastle().getXP()));
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
				if (equippedWeapons[weaponNo].getType().charAt(2) == ServerWorld.WEAPON_TYPE
						.charAt(2))
					currentDamage = ((ServerWeapon) equippedWeapons[weaponNo])
					.getDamage();
				else
					currentDamage = 0;
			}
			queueMessage("D " + currentDamage + " " + getBaseDamage());

			// while (message.length() < 4000)
			// {
			// queueMessage("L " + getHP());
			// }
			// Send the current time in the world (Must be the last thing)
			queueMessage("T " + toChars(getWorld().getWorldTime()));

			// Send population for red and blue castles
			queueMessage("w " + getWorld().getRedCastle().getPopulation()
					+ " " + getWorld().getRedCastle().getPopLimit());
			queueMessage("u " + getWorld().getBlueCastle().getPopulation()
					+ " " + getWorld().getBlueCastle().getPopLimit());

			if (body != null) {
				queueMessage("e " + toChars(body.getID()));
			}

			if (head != null) {
				queueMessage("e " + toChars(head.getID()));
			}

			if(!isAlive())
				queueMessage("r "+(getWorld().getWorldCounter() - deathCounter));
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
			
			if (disconnect)
			{
				disconnect = false;
				sendMessage("-");
				break;
			}
			
			try {
				// Read the next line the player sent in
				String command = input.readLine();
				String[] tokens = command.split(" ");

				if (tokens.length == 0) {
					continue;
				}

				// Execute the player's action based on the command received
				// from the client
				switch (tokens[0].charAt(0)) {
				case '&':
					setHSpeed(Double.parseDouble(tokens[1]));
					setVSpeed(Double.parseDouble(tokens[2]));
					if (tokens[3].charAt(0) == '1') {
						setOnSurface(true);
					} else {
						setOnSurface(false);
					}
					break;
				case 'A':
				case 'a':
					try {
						newMouseX = Integer.parseInt(tokens[1]);
						newMouseY = Integer.parseInt(tokens[2]);
						if (isOnSurface() && !performingAction && isAlive()) {

							// If the client tries to send an invalid message
							if (command.charAt(0) == 'a'
									|| tokens[3].charAt(0) == 't')
								performingAction = true;
							if (command.charAt(0) == 'a') {
								rightClick = true;
							} else {
								rightClick = false;
							}
						}
					} catch (Exception e) {
					}
					break;
				case 'c':
					if (isAlive()) {
						actionCounter = actionDelay;
					}
					break;
				case 'D':
					if (isAlive()) {
						isDropping = true;
					}
					break;
				case 'd':
					if (isAlive()) {
						isDropping = false;
					}
					break;
				case 'R':
					if (isAlive()) {
						setHSpeed(horizontalMovement);
						movingDirection = 1;
					}
					break;
				case 'r':
					movingDirection = 0;
					if (getHSpeed() > 0) {
						setHSpeed(0);
					}
					break;
				case 'L':
					if (isAlive()) {
						movingDirection = -1;
						setHSpeed(-horizontalMovement);
					}
					break;
				case 'l':
					movingDirection = 0;
					if (getHSpeed() < 0) {
						setHSpeed(0);
					}
					break;
				case 'U':
					if (isOnSurface() && isAlive() && !inAction()) {
						setVSpeed(-verticalMovement);
						setOnSurface(false);
					}
					break;
				case 'Q':
					setDirection("RIGHT");
					break;
				case 'q':
					setDirection("LEFT");
					break;
				case 'p':
					if (!ignoreClient && isAlive()) {
						double x =Double.parseDouble(tokens[1]);
						double y = Double.parseDouble(tokens[2]);
						if (x > 0 && x < getWorld().getCollisionGrid()[0].length*ServerWorld.TILE_SIZE-50 && y > 0 && y < getWorld().getCollisionGrid().length*ServerWorld.TILE_SIZE-50)
						{
							setX(x);
							setY(y);
						}
					}
					break;
				case 'P':
					sendMessage("P");
					break;
				case 'y':
					ping = Integer.parseInt(command.substring(2));
					break;
				case 'C':
					// Player uses the chat
					if (command.length() >= 3) {
						String message = command.substring(2);
						String[] tokens2 = message.split(" ");
						if (tokens2[0].equals("/t")) {
							engine.broadCastTeam("l " + "T "
									+ (getTeam() + getName()).split(" ").length
									+ " " + getTeam() + getName() + " "
									+ tokens2.length + " " + message, getTeam());
						} else {
							engine.broadcast("l " + "E "
									+ (getTeam() + getName()).split(" ").length
									+ " " + getTeam() + getName() + " "
									+ tokens2.length + " " + message);

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
							textStartTime = getWorld().getWorldCounter();
							textDuration = (int) (60 * 3 + text.length() * 60 * 0.1);
						}
					}
					break;
				case 'v':
					try {
						switch (command.charAt(2)) {
						case 'I':
							// If dropping from inventory
							super.drop(command.substring(4));
							break;
						case 'W':
							// If dropping from equipped
							drop(Integer.parseInt(command.substring(4)));
							break;
						case 'U':
							// If using a potion
							super.use(command.substring(4));
							break;
						}
					}
					// If the player sends a bad message
					catch (Exception E) {
					}
					break;

				case 'M':
					try {
						switch (command.charAt(2)) {
						// Move to inventory
						case 'I':
							unequip(Integer.parseInt(command.substring(4)));
							break;
							// Move to equipped weapons
						case 'W':
							equipWeapon(command.substring(4));
							break;
							// Move to equipped armors
						case 'A':
							equipArmour(command.substring(4));
							break;
						}
					} catch (Exception E) {
					}
					break;

				case 'W':
					try {
						weaponSelected = command.charAt(2);
						int weap = weaponSelected - '0';
						System.out
						.println("Selected weapon: " + weaponSelected);
						if (weap != DEFAULT_WEAPON_SLOT
								&& equippedWeapons[weap] != null
								&& equippedWeapons[weap].getType().contains(
										ServerWorld.BUILDING_ITEM_TYPE)) {
							hologram = new ServerHologram(
									getNewMouseX() + getX() - playerScreenWidth
									/ 2,
									getNewMouseY() + getY()
									- playerScreenHeight / 2,
									((ServerBuildingItem) equippedWeapons[weap])
									.getBuildingType(), this, engine);
							getWorld().add(hologram);
							System.out.println("Added HOLOGRAM to world at "
									+ getNewMouseX() + " " + getNewMouseY());
						} else if (hologram != null) {
							hologram.destroy();
							System.out.println("Removed HOLOGRAM from world");
							hologram = null;
						}
					} catch (Exception E) {
						E.printStackTrace();
					}
					break;
				case 'B':
					if (vendor != null) {
						ServerItem vendorItem = null;
						tokens = command.split(" ");
						String itemName = tokens[1];
						for (ServerItem item : vendor.getInventory())
							if (item.getType().equals(itemName))
								vendorItem = item;

						if (vendorItem != null
								&& getMoney() >= vendorItem.getCost()) {						
							if ((vendorItem.getAmount() > 1 && addItem(ServerItem.copy(vendorItem))==1) || addItem(vendorItem) == 1)
							{
								decreaseMoney(vendorItem.getCost());
								vendor.drop(vendorItem.getType());
								queueMessage("Vb "+tokens[2]+" "+tokens[3]);
							}
						}
					}
					break;
				case 'b':
					try {
						buyCastleItem(command.substring(2));
					} catch (Exception e) {
						continue;
					}
					break;
				case 'E':
					interact();
					break;
				case 'S':
					if (vendor != null) {
						String type = "";
						try {
							type = command.substring(2);
						} catch (Exception E) {
							continue;
						}
						if (!type.equals(ServerWorld.MONEY_TYPE)
								&& !type.contains(ServerWorld.BUILDING_ITEM_TYPE)) {
							sell(type);
							queueMessage("i " + type);
						}
					}
					break;
				case 's':
					try {
						playerScreenWidth = Integer.parseInt(tokens[1]);
						playerScreenHeight = Integer.parseInt(tokens[2]);
					} catch (Exception E) {
						continue;
					}
					break;
				}
			} catch (IOException e) {
				//e.printStackTrace();
				break;
			} catch (NullPointerException e) {
				e.printStackTrace();
				break;
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Indexing problem caught");
				e.printStackTrace();
				break;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Something broke for this player");
				break;
			}
		}

		if (endGame)
		{
			return;
		}

		//Save the players money if they disconnect
		synchronized(engine.getSavedPlayers())
		{
			inflictDamage(1000, this);
			engine.getSavedPlayers().add(new SavedPlayer(getName(), getMoney(), kills, deaths, totalDamageDealt, totalMoneySpent, getTeam(),hair, skinColour, bestWeapon, bestArmour,getWorld().getWorldCounter()));
		}

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
				if(item.getType().equals(ServerWorld.HP_POTION_TYPE))
					numHPPots--;
				else if(item.getType().equals(ServerWorld.MANA_POTION_TYPE))
					numManaPots--;

				if (item.getAmount() > 1) {
					item.decreaseAmount();
					vendor.addItem(ServerItem.copy(item));
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
				+ getHeight() / 2, amount, getWorld());
		if (vendor != null) {
			newMoney.setSource(vendor);
		}
		addItem(newMoney);
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
				addTotalMoneySpent(amount);
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

	public void dropItem(ServerItem item)
	{
		if(item.getType().equals(ServerWorld.HP_POTION_TYPE))
			numHPPots--;
		else if(item.getType().equals(ServerWorld.MANA_POTION_TYPE))
			numManaPots--;
		super.dropItem(item);
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
				actionDelay = ((ServerWeapon) equippedWeapons[weaponNo])
						.getActionDelay();
				actionSpeed = ((ServerWeapon) equippedWeapons[weaponNo])
						.getActionSpeed();
				getWorld()
				.add(new ServerWeaponSwing(
						this,
						0,
						-20,
						((ServerWeapon) equippedWeapons[weaponNo])
						.getActionImage(),
						(int) (Math.toDegrees(angle) + 0.5),
						((ServerWeapon) equippedWeapons[weaponNo])
						.getActionSpeed(),
						(int) Math.ceil(((ServerWeapon) equippedWeapons[weaponNo])
								.getDamage()
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
					getWorld().add(
							new ServerProjectile(getX() + getWidth() / 2,
									getY() + getHeight() / 3, this, angle,
									arrowType, getWorld()));

					if (getDirection().equals("LEFT")) {

						image += "_LEFT";
					} else {
						image += "_RIGHT";
					}

					heldWeapon = new ServerObjectShown(x, y, 0, 0, 0, image,
							ServerWorld.WEAPON_HOLD_TYPE, getWorld()
							.getEngine());
					heldWeapon.setSolid(false);
					getWorld().add(heldWeapon);
				} else {
					action = "";
					actionDelay = 0;
					ServerText message = new ServerText(
							getX() + getWidth() / 2, getY() - getHeight() / 2,
							"!M", ServerText.PURPLE_TEXT, getWorld());
					getWorld().add(message);
				}
			} else if (equippedWeapons[weaponNo].getType().contains(
					ServerWorld.BUILDING_ITEM_TYPE)) {
				actionDelay = 0;
				// Place building
				if (hologram != null) {
					hologram.place();
				}
			}
		}
		canPerformAction = false;
	}

	/**
	 * Drop inventory and equipment
	 */
	public void dropInventory() {
		ServerItem money = null;
		bestWeapon = null;
		bestArmour = null;

		for (ServerItem item : getInventory())
		{
			if (item.getType().equals(ServerWorld.MONEY_TYPE)
					&& getType().substring(0, 2)
					.equals(ServerWorld.PLAYER_TYPE))
				money = item;
			else if (item.getType().contains(ServerWorld.WEAPON_TYPE))
			{
				if(bestWeapon == null)
				{
					bestWeapon = item;
				}
				else if(item.getCost() > bestWeapon.getCost())
				{
					dropItem(bestWeapon);
					bestWeapon = item;
				}
				else dropItem(item);
			}
			else if (item.getType().contains(ServerWorld.ARMOUR_TYPE))
			{
				if(bestArmour == null)
				{
					bestArmour = item;
				}
				else if(item.getCost() > bestArmour.getCost())
				{
					dropItem(bestArmour);
					bestArmour = item;
				}
				else dropItem(item);
			}
			else
				dropItem(item);
		}

		getInventory().clear();
		if (money != null)
			getInventory().add(money);

		for (int item = 0; item < equippedWeapons.length; item++)
			if (equippedWeapons[item] != null)
			{
				if(bestWeapon == null || equippedWeapons[item].getCost() > bestWeapon.getCost())
				{
					if(bestWeapon != null)
						dropItem(bestWeapon);
					bestWeapon = equippedWeapons[item];
				}
				else dropItem(equippedWeapons[item]);
			}
		equippedWeapons = new ServerItem[MAX_WEAPONS];

		if (equippedArmour != null)
		{
			if(bestArmour == null || equippedArmour.getCost() > bestArmour.getCost())
			{
				if(bestArmour!= null)
					dropItem(bestArmour);
				bestArmour = equippedArmour;
			}
			else dropItem(equippedArmour);
		}
		equippedArmour = null;

		if(bestWeapon != null)
		{
			System.out.println("BEST W: "+bestWeapon.getType());
			getInventory().add(bestWeapon);
		}
		if(bestArmour != null)
		{
			System.out.println("BEST A: "+bestArmour.getType());
			getInventory().add(bestArmour);
		}
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

			char textColour = ServerText.RED_TEXT;
			if (action == "BLOCK") {
				textColour = ServerText.BLUE_TEXT;
				amount = 0;
			}

			if(source != this)
				addCastleXP(Math.min(amount, getHP()), source);

			setHP(getHP() - amount);

			double damageX = Math.random() * getWidth() + getX();
			double damageY = Math.random() * getHeight() / 2 + getY()
			- getHeight() / 3;

			getWorld().add(
					new ServerText(damageX, damageY, Integer.toString(amount),
							textColour, getWorld()));

			// Play the death animation of the player when the HP drops to 0 or
			// below, and eventually respawn the player
			if (getHP() <= 0) {
				deaths++;

				// For the scoreboard
				if(source != this)
				{
					if (source.getType().equals(ServerWorld.PLAYER_TYPE)) {
						engine.broadcast("@ " + toChars(source.getID()) + " "
								+ source.getTeam());
						((ServerPlayer) source).kills++;
					}
					engine.broadcast("! " + toChars(getID()) + " " + getTeam());

					if (source.getTeam() == ServerCreature.NEUTRAL) {
						String firstName = getTeam() + getName();
						String secondName = ServerCreature.NEUTRAL
								+ source.getName();
						engine.broadcast("k " + firstName.split(" ").length + " "
								+ firstName + " " + secondName.split(" ").length
								+ " " + secondName);
					} else {
						String firstName = source.getTeam() + source.getName();
						String secondName = getTeam() + getName();
						engine.broadcast("f " + firstName.split(" ").length + " "
								+ firstName + " " + secondName.split(" ").length
								+ " " + secondName);
					}
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

				numHPPots = 0;
				numManaPots = 0;
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
		} else if (endRow > getWorld().getObjectGrid().length - 1) {
			endRow = getWorld().getObjectGrid().length - 1;
		}

		if (startColumn < 0) {
			startColumn = 0;
		} else if (endColumn > getWorld().getObjectGrid()[0].length - 1) {
			endColumn = getWorld().getObjectGrid()[0].length - 1;
		}

		while (true) {
			try {
				for (int row = startRow; row <= endRow; row++) {
					for (int column = startColumn; column <= endColumn; column++) {
						for (ServerObject object : getWorld().getObjectGrid()[row][column]) {
							if (object.exists() && object.collidesWith(this)) {
								// If vendor send shop to client
								if (object.getType().equals(
										ServerWorld.VENDOR_TYPE)) {
									if (vendor == null) {
										if(!((ServerVendor) object)
												.isBusy())
										{
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
										}
										else
										{
											getWorld().add(new ServerText(getX()+getWidth()/2, getY()-20, "Shop is being used", 'p', getWorld()));
										}
									} else if (vendor != null) {
										vendor.setIsBusy(false);
										vendor = null;
									}
									return;
								} else if (object.getType().equals(
										ServerWorld.CASTLE_TYPE)
										&& ((ServerCreature) object).getTeam() == getTeam()) {
									// Make a shop
									if (castle == null)
										if (getTeam() == RED_TEAM)
											castle = getWorld().getRedCastle();
										else
											castle = getWorld().getBlueCastle();
									if (!castleOpen) {
										queueMessage("c");
										castleOpen = true;
									} else if (castleOpen) {
										castleOpen = false;
									}
									return;
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
			sendMessage("B " + losingTeam);
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
	public int addItem(ServerItem item) {		
		if((item.getType().equals(ServerWorld.HP_POTION_TYPE) && (numHPPots == MAX_HP_POTS || item.getAmount() > MAX_HP_POTS))|| (item.getType().equals(ServerWorld.MANA_POTION_TYPE) && (numManaPots == MAX_MANA_POTS|| item.getAmount() > MAX_MANA_POTS)))
			return 0;

		if (item.getType().charAt(1) == ServerWorld.STACK_TYPE.charAt(1))
			for (ServerItem sItem : getInventory())
			{
				if (item.getType().equals(sItem.getType()))
				{
					if(item.getType().equals(ServerWorld.HP_POTION_TYPE))
					{
						if(numHPPots + item.getAmount() > MAX_HP_POTS)
						{
							queueMessage("I " + item.getImageIndex() + " " + item.getType() + " "
									+ (MAX_HP_POTS - numHPPots) + " " + item.getCost());
							item.decreaseAmount(MAX_HP_POTS - numHPPots);
							sItem.setAmount(MAX_HP_POTS);
							numHPPots = MAX_HP_POTS;
							return 2;
						}
						else
						{
							numHPPots += item.getAmount();
						}
					}
					else if(item.getType().equals(ServerWorld.MANA_POTION_TYPE))
					{
						if(numManaPots + item.getAmount() > MAX_MANA_POTS)
						{
							queueMessage("I " + item.getImageIndex() + " " + item.getType() + " "
									+ (MAX_MANA_POTS - numManaPots) + " " + item.getCost());
							item.decreaseAmount(MAX_MANA_POTS - numManaPots);
							sItem.setAmount(MAX_MANA_POTS);
							numManaPots = MAX_MANA_POTS;
							return 2;
						}
						else
						{
							numManaPots += item.getAmount();
						}
					}
					sItem.increaseAmount(item.getAmount());
					queueMessage("I " + item.getImageIndex() + " " + item.getType() + " "
							+ item.getAmount() + " " + item.getCost());
					return 1;
				}
			}
		if(item.getType().equals(ServerWorld.HP_POTION_TYPE))
			numHPPots+=item.getAmount();
		else if (item.getType().equals(ServerWorld.MANA_POTION_TYPE))
			numManaPots+=item.getAmount();

		getInventory().add(item);
		queueMessage("I " + item.getImageIndex() + " " + item.getType() + " "
				+ item.getAmount() + " " + item.getCost());
		return 1;
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
		equippedWeapons[pos] = toRemove;
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
				getWorld());
		if (getBody() != null) {
			getBody().destroy();
		}
		setBody(newArmour);
		getWorld().add(newArmour);

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

	public static String toChars(int y) {
		int x = y;
		String ret = "";

		while (x > 0) {
			int ch = x % 94 + 33;
			if (ch >= 92)
				ch++;
			ret += (char) ch;
			x /= 94;
		}
		// System.out.println("StringRep: " +y+" "+ret);
		return ret;
	}

	public void buyCastleItem(String type) {
		synchronized(castle)
		{
			switch (type) {
			case ServerWorld.BASIC_BARRACKS_ITEM_TYPE:
				if (castle != null
				&& castle.getMoney() >= ServerBuildingItem.BASIC_BARRACKS_COST) {
					castle.spendMoney(ServerBuildingItem.BASIC_BARRACKS_COST);
					addItem(new ServerBuildingItem(ServerWorld.BASIC_BARRACKS_ITEM_TYPE,
							getWorld()));
				}
				break;
			case ServerWorld.ADV_BARRACKS_ITEM_TYPE:
				if (castle != null
				&& castle.getMoney() >= ServerBuildingItem.ADV_BARRACKS_COST) {
					castle.spendMoney(ServerBuildingItem.ADV_BARRACKS_COST);
					addItem(new ServerBuildingItem(ServerWorld.ADV_BARRACKS_ITEM_TYPE,
							getWorld()));
				}
				break;
			case ServerWorld.GIANT_FACTORY_ITEM_TYPE:
				if (castle != null
				&& castle.getMoney() >= ServerBuildingItem.GIANT_FACTORY_COST) {
					castle.spendMoney(ServerBuildingItem.GIANT_FACTORY_COST);
					addItem(new ServerBuildingItem(ServerWorld.GIANT_FACTORY_ITEM_TYPE,
							getWorld()));
				}
				break;
			case ServerWorld.WOOD_HOUSE_ITEM_TYPE:
				if (castle != null
				&& castle.getMoney() >= ServerBuildingItem.WOOD_HOUSE_COST) {
					castle.spendMoney(ServerBuildingItem.WOOD_HOUSE_COST);
					addItem(new ServerBuildingItem(
							ServerWorld.WOOD_HOUSE_ITEM_TYPE, getWorld()));
				}
				break;
			case ServerWorld.INN_ITEM_TYPE:
				if (castle != null
				&& castle.getMoney() >= ServerBuildingItem.INN_COST) {
					castle.spendMoney(ServerBuildingItem.INN_COST);
					addItem(new ServerBuildingItem(
							ServerWorld.INN_ITEM_TYPE, getWorld()));
				}
				break;
			case ServerWorld.TOWER_ITEM_TYPE:
				if (castle != null
				&& castle.getMoney() >= ServerBuildingItem.TOWER_COST) {
					castle.spendMoney(ServerBuildingItem.TOWER_COST);
					addItem(new ServerBuildingItem(ServerWorld.TOWER_ITEM_TYPE,
							getWorld()));
				}
				break;
			case ServerWorld.GOLD_MINE_ITEM_TYPE:
				if (castle != null
				&& castle.getMoney() >= ServerBuildingItem.GOLD_MINE_COST) {
					castle.spendMoney(ServerBuildingItem.GOLD_MINE_COST);
					addItem(new ServerBuildingItem(ServerWorld.GOLD_MINE_ITEM_TYPE,
							getWorld()));
				}
				break;
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

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public void setMaxHP(int maxHP) {
		super.setMaxHP(Math.min(ServerPlayer.PLAYER_MAX_HP, maxHP));
	}

	public void setBaseDamage(int baseDamage) {
		super.setBaseDamage(Math.min(ServerPlayer.MAX_DMGADD, baseDamage));
	}

	public boolean isIgnoreClient() {
		return ignoreClient;
	}

	public void setIgnoreClient(boolean ignoreClient) {
		this.ignoreClient = ignoreClient;
	}

	public int getTotalDamageDealt() {
		return totalDamageDealt;
	}

	public void addTotalDamage(int amount) {
		totalDamageDealt += amount;
	}

	public int getTotalMoneySpent() {
		return totalMoneySpent;
	}

	public void addTotalMoneySpent(int amount) {
		totalMoneySpent += amount;
	}

	public int getScore() {
		return totalDamageDealt + kills * PLAYER_BASE_HP + totalMoneySpent * 10;
	}

	public int getPing() {
		return ping;
	}

	public void decreaseNumHPPots()
	{
		numHPPots--;
	}

	public void decreaseNumManaPots()
	{
		numManaPots--;
	}

	public void setDeathCounter(long amount)
	{
		deathCounter = amount;
	}
}
