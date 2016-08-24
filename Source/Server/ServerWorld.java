package Server;

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.StringTokenizer;

import Imports.ImageReferencePair;
import Server.Creatures.ServerCastle;
import Server.Creatures.ServerChest;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerEnemy;
import Server.Creatures.ServerGoblin;
import Server.Creatures.ServerPlayer;
import Server.Creatures.ServerSlime;
import Server.Creatures.ServerVendor;
import Server.Effects.ServerDamageIndicator;
import Server.Items.ServerItem;
import Server.Items.ServerMoney;
import Server.Items.ServerPotion;
import Server.Items.ServerProjectile;
import Server.Items.ServerWeaponSwing;
import Server.Spawners.ServerSpawner;
import Tools.RowCol;

/**
 * Stores all the data about the world
 * @author William Xu and Alex Raita
 *
 */
public class ServerWorld
{
	// String definitions for each type of object to identify them when in the
	// super class "ServerObject" state
	public final static String BACKGROUND_OBJECT_TYPE = "B";

	public final static String WEAPON_HOLD_TYPE = "H";

	public final static char PROJECTILE_TYPE = 'P';
	public final static String BULLET_TYPE = PROJECTILE_TYPE + "B";

	public final static String PIERCING_TYPE = PROJECTILE_TYPE + "P";

	public final static String WOODARROW_TYPE = PIERCING_TYPE + "W";
	public final static String STEELARROW_TYPE = PIERCING_TYPE + "S";
	public final static String MEGAARROW_TYPE = PIERCING_TYPE + "M";
	public final static String NINJASTAR_TYPE = PIERCING_TYPE + "N";

	public final static String FIREBALL_TYPE = PROJECTILE_TYPE + "F";
	public final static String ICEBALL_TYPE = PROJECTILE_TYPE + "I";
	public final static String DARKBALL_TYPE = PROJECTILE_TYPE + "D";

	public final static char CREATURE_TYPE = 'C';
	public final static String PLAYER_TYPE = CREATURE_TYPE + "C";
	public final static String PLAYER_GHOST_TYPE = PLAYER_TYPE + "G";
	public final static String NPC_TYPE = CREATURE_TYPE + "N";

	public final static String SLIME_TYPE = NPC_TYPE + "S";
	public final static String BAT_TYPE = NPC_TYPE + "B";

	
	public final static String GOBLIN_TYPE = NPC_TYPE + "G";

	public final static String NAKED_GOBLIN_TYPE = GOBLIN_TYPE + "N";
	public final static String GOBLIN_GENERAL_TYPE = GOBLIN_TYPE + "G";
	public final static String GOBLIN_GUARD_TYPE = GOBLIN_TYPE + "g";
	public final static String GOBLIN_GIANT_TYPE = GOBLIN_TYPE + "J";
	public final static String GOBLIN_KING_TYPE = GOBLIN_TYPE + "K";
	public final static String GOBLIN_KNIGHT_TYPE = GOBLIN_TYPE + "k";
	public final static String GOBLIN_LORD_TYPE = GOBLIN_TYPE + "L";
	public final static String GOBLIN_NINJA_TYPE = GOBLIN_TYPE + "N";
	public final static String GOBLIN_PEASANT_TYPE = GOBLIN_TYPE + "P";
	public final static String GOBLIN_SOLDIER_TYPE = GOBLIN_TYPE + "S";
	public final static String GOBLIN_WIZARD_TYPE = GOBLIN_TYPE + "W";
	public final static String GOBLIN_WORKER_TYPE = GOBLIN_TYPE + "w";

	public final static String CHEST_TYPE = CREATURE_TYPE + "M";
	public final static String CASTLE_TYPE = CREATURE_TYPE + "T";
	public final static String VENDOR_TYPE = CREATURE_TYPE + "V";

	public final static char ITEM_TYPE = 'I';
	public final static String EQUIP_TYPE = ITEM_TYPE + "E";
	public final static String STACK_TYPE = ITEM_TYPE + "S";
	public final static String POTION_TYPE = STACK_TYPE + "P";
	public final static String HP_POTION_TYPE = POTION_TYPE + "H";
	public final static String MAX_HP_TYPE = POTION_TYPE + "h";
	public final static String MANA_POTION_TYPE = POTION_TYPE + "M";
	public final static String MAX_MANA_TYPE = POTION_TYPE + "m";
	public final static String DMG_POTION_TYPE = POTION_TYPE + "D";
	public final static String SPEED_POTION_TYPE = POTION_TYPE + "S";
	public final static String JUMP_POTION_TYPE = POTION_TYPE + "J";

	public final static String WEAPON_TYPE = EQUIP_TYPE + "W";
	public final static String MELEE_TYPE = WEAPON_TYPE + "M";
	public final static String RANGED_TYPE = WEAPON_TYPE + "R";
	public final static String ARMOUR_TYPE = EQUIP_TYPE + "A";

	public final static String STEEL_ARMOUR = ARMOUR_TYPE + "I";
	public final static String RED_NINJA_ARMOUR = ARMOUR_TYPE + "R";
	public final static String BLUE_NINJA_ARMOUR = ARMOUR_TYPE + "B";
	public final static String GREY_NINJA_ARMOUR = ARMOUR_TYPE + "G";

	public final static String DAGGER_TYPE = MELEE_TYPE + "D";
	public final static String AX_TYPE = MELEE_TYPE + "A";
	public final static String SWORD_TYPE = MELEE_TYPE + "S";
	public final static String HALBERD_TYPE = MELEE_TYPE + "H";

	public final static String BOW_TYPE = RANGED_TYPE + "B";
	public final static String WAND_TYPE = RANGED_TYPE + "W";

	public final static String SLINGSHOT_TYPE = BOW_TYPE + "s";
	public final static String WOODBOW_TYPE = BOW_TYPE + "W";
	public final static String STEELBOW_TYPE = BOW_TYPE + "S";
	public final static String MEGABOW_TYPE = BOW_TYPE + "M";

	public final static String FIREWAND_TYPE = WAND_TYPE + "F";
	public final static String ICEWAND_TYPE = WAND_TYPE + "I";
	public final static String DARKWAND_TYPE = WAND_TYPE + "D";

	public final static char WOOD_TIER = 'W';
	public final static char STONE_TIER = 'S';
	public final static char IRON_TIER = 'I';
	public final static char GOLD_TIER = 'G';
	public final static char DIAMOND_TIER = 'D';

	public final static String MONEY_TYPE = STACK_TYPE + "M";

	public final static String SPAWN_TYPE = "S";
	public final static char ANIMATION_TYPE = 'A';
	public final static String WEAPON_SWING_TYPE = ANIMATION_TYPE + "S";
	public final static String ACCESSORY_TYPE = ANIMATION_TYPE + "A";

	public final static String EXPLOSION_TYPE = ANIMATION_TYPE + "E";

	public final static char TEXT_TYPE = 'T';
	public final static String DAMAGE_INDICATOR_TYPE = TEXT_TYPE + "D";

	/**
	 * Map name
	 */
	private String mapFile;

	private int redCastleX;
	private int blueCastleX;
	private int redCastleY;
	private int blueCastleY;

	/**
	 * Grid of tiles
	 */
	private char[][] tileGrid;

	/**
	 * The size of each tile
	 */
	public static final int TILE_SIZE = 16;

	/**
	 * Grid of objects
	 */
	private ArrayList<ServerObject>[][] objectGrid;

	/**
	 * All the creatures in red team
	 */
	private ArrayList<ServerCreature> redTeam = new ArrayList<ServerCreature>();

	/**
	 * All the creatures in blue team
	 */
	private ArrayList<ServerCreature> blueTeam = new ArrayList<ServerCreature>();

	/**
	 * Array of different types of creatures, buildings, background objects, and
	 * spawners possible in the world (for the purpose of the creator world)
	 * 
	 * (MAKE SURE TO SET WORLD WHEN CREATING CREATURES)
	 */
	public final ServerObject[] objectTypes = {
			new ServerCastle(0, 0, ServerPlayer.RED_TEAM, this),
			new ServerCastle(0, 0, ServerPlayer.BLUE_TEAM, this),
			new ServerChest(0, 0, this),
			new ServerVendor(0, 0, this, "VENDOR_RIGHT"),
			new ServerVendor(0, 0, this, "VENDOR_LEFT"),
			new ServerSpawner(0, 0, new ServerSlime(0, 0, this), this),
			new ServerSpawner(0, 0, new ServerGoblin(0, 0, this,
					ServerPlayer.RED_TEAM, 1), this),
			new ServerSpawner(0, 0, new ServerGoblin(0, 0, this,
					ServerPlayer.BLUE_TEAM, 1), this) };

	// Store the goblin spawners so the castles can access them and change their
	// settings
	ArrayList<ServerSpawner> redSpawners = new ArrayList<ServerSpawner>();
	ArrayList<ServerSpawner> blueSpawners = new ArrayList<ServerSpawner>();

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
	 * Number of counters (minutes) in a day
	 */
	public final static int DAY_COUNTERS = 1440;

	/**
	 * Number of frames in a day counter
	 */
	public final static int COUNTER_TIME = 15;

	/**
	 * List of all the non-tile objects in the world (for movement and collision
	 * detection)
	 */
	private ArrayList<ServerObject> objects;

	/**
	 * List of objects to add to the world next refresh (based on the user's
	 * input)
	 */
	private ArrayDeque<ServerObject> objectsToAdd;

	/**
	 * List of objects to remove that doesn't exist anymore
	 */
	private ArrayList<ServerObject> objectsToRemove = new ArrayList<ServerObject>();

	/**
	 * The counter showing how many frames the server has run
	 */
	private long worldCounter = 0;

	/**
	 * The current time in the world
	 */
	private int worldTime = 0;

	/**
	 * The number of slimes in the world
	 */
	public static int slimeCount = 0;

	private ServerEngine engine;

	private ServerCastle blueCastle;
	private ServerCastle redCastle;

	/**
	 * Constructor for server
	 * @throws IOException
	 */
	public ServerWorld(ServerEngine engine) throws IOException
	{
		objects = new ArrayList<ServerObject>();
		objectsToAdd = new ArrayDeque<ServerObject>();

		this.engine = engine;
		newWorld();
	}

	/**
	 * Constructor for server
	 * @throws IOException
	 */
	public ServerWorld(ServerEngine engine, String map) throws IOException
	{
		objects = new ArrayList<ServerObject>();
		objectsToAdd = new ArrayDeque<ServerObject>();
		mapFile = map;
		this.engine = engine;
		newWorld();
	}

	/**
	 * Create a new world
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void newWorld() throws IOException
	{
		BufferedReader worldInput = new BufferedReader(new FileReader(new File(
				"Resources", mapFile)));

		StringTokenizer tokenizer = new StringTokenizer(worldInput.readLine());

		// Add to both sides to make room for the invisible walls
		tileGrid = new char[Integer.parseInt(tokenizer.nextToken()) + 6][Integer
				.parseInt(tokenizer.nextToken()) + 6];

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
		for (int row = 3; row < tileGrid.length - 3; row++)
		{
			line = worldInput.readLine();
			for (int col = 3; col < tileGrid[row].length - 3; col++)
				tileGrid[row][col] = line.charAt(col - 3);
		}

		// Make a border around the grid
		for (int col = 0; col < tileGrid[0].length; col++)
		{
			tileGrid[0][col] = '_';
			tileGrid[1][col] = '_';
			tileGrid[2][col] = '_';
			tileGrid[tileGrid.length - 1][col] = '_';
			tileGrid[tileGrid.length - 2][col] = '_';
			tileGrid[tileGrid.length - 3][col] = '_';
		}

		// Make a border around the grid
		for (int row = 0; row < tileGrid.length; row++)
		{
			tileGrid[row][0] = '_';
			tileGrid[row][1] = '_';
			tileGrid[row][2] = '_';
			tileGrid[row][tileGrid[0].length - 1] = '_';
			tileGrid[row][tileGrid[0].length - 2] = '_';
			tileGrid[row][tileGrid[0].length - 3] = '_';
		}

		// Add objects to the grid
		int numObjects = Integer.parseInt(worldInput.readLine());
		for (int object = 0; object < numObjects; object++)
		{
			tokenizer = new StringTokenizer(worldInput.readLine());
			int row = Integer.parseInt(tokenizer.nextToken()) + 3;
			int col = Integer.parseInt(tokenizer.nextToken()) + 3;
			char ref = tokenizer.nextToken().charAt(0);
			for (ServerObject obj : objectTypes)
				if (obj.getImage().equals(
						ImageReferencePair.getImages()[ref].getImageName()))
				{
					ServerObject newObject = ServerObject.copy(obj);
					newObject.setX(col * ServerWorld.TILE_SIZE);
					newObject.setY(row * ServerWorld.TILE_SIZE);
					

					if (obj.getType().equals(ServerWorld.CASTLE_TYPE))
					{
						((ServerCastle)newObject).setTeam(((ServerCastle)newObject).getTeam());
						if (((ServerCastle) newObject).getTeam() == ServerPlayer.RED_TEAM)
						{
							redCastleX = (int) newObject.getX() + 50;
							redCastleY = (int) newObject.getY() + 100;
							redCastle = (ServerCastle) newObject;
						}
						else
						{
							blueCastleX = (int) newObject.getX() + 50;
							blueCastleY = (int) newObject.getY() + 100;
							blueCastle = (ServerCastle) newObject;
						}
					}
					else if (obj.getType().equals(ServerWorld.SPAWN_TYPE)
							&& obj.getImage().equals("RED_GOBLIN_SPAWN"))
					{
						redSpawners.add((ServerSpawner) newObject);
					}
					else if (obj.getType().equals(ServerWorld.SPAWN_TYPE)
							&& obj.getImage().equals("BLUE_GOBLIN_SPAWN"))
					{
						blueSpawners.add((ServerSpawner) newObject);
					}
					// else if (obj.getType().equals(ServerWorld.CHEST_TYPE))
					// {
					// // Chests don't spawn immediately
					// newObject.destroy();
					// }
					add(newObject);
				}
		}
		worldInput.close();

		worldTime= DAY_COUNTERS / 12 * 11 + (int) (Math.random() * (DAY_COUNTERS / 6));
		if (worldTime >= DAY_COUNTERS)
		{
			worldTime -= DAY_COUNTERS;
		}
	}

	/**
	 * Update all the objects, doing whatever needs to be done
	 */
	public synchronized void update()
	{
		// Remove all the objects that no longer exist
		for (ServerObject object : objectsToRemove)
		{
			removeFromObjectTiles(object);
			objects.remove(object);

		}
		objectsToRemove.clear();

		// Add an object to the game
		while (!objectsToAdd.isEmpty())
		{
			ServerObject newObject = objectsToAdd.poll();
			if (newObject.getType().charAt(0) == ServerWorld.ITEM_TYPE)
				((ServerItem) newObject).setDropTime(worldCounter);
			objects.add(newObject);
		}

		try
		{
			// Go through and update each object in the game
			for (ServerObject object : objects)
			{
				if (object.getType().equals(ServerWorld.CASTLE_TYPE)
						&& ((ServerCreature) object).getHP() <= 0)
					engine.endGame(((ServerCreature) object).getTeam());

				// This will remove the object a frame after it stops existing
				if (object.exists())
				{

					if (object.getType().charAt(0) == ITEM_TYPE
							&& object.isOnSurface())
					{
						object.setHSpeed(0);
						((ServerItem) object).update(worldCounter);
					}

					// Add the object to all the object tiles that it collides
					// with
					// currently
					int startRow = (int) (object.getY() / OBJECT_TILE_SIZE);
					int endRow = (int) ((object.getY() + object.getHeight()) / OBJECT_TILE_SIZE);
					int startColumn = (int) (object.getX() / OBJECT_TILE_SIZE);
					int endColumn = (int) ((object.getX() + object.getWidth()) / OBJECT_TILE_SIZE);

					// Destroy the object if it is not in the world
					if (startRow < 0 || endRow > objectGrid.length - 1
							|| startColumn < 0
							|| endColumn > objectGrid[0].length - 1)
					{
						object.destroy();
						continue;
					}

					// Update all locations of objects based on the object grid
					// (for
					// collisions)
					updateObjectTiles(object, startRow, endRow, startColumn,
							endColumn);

					if (object.getType().equals(SPAWN_TYPE))
					{
						((ServerSpawner) object).update(worldCounter);
						continue;
					}

					// Store the objects that the tile has already collided with
					// so there are no repeats when checking in tiles
					ArrayList<ServerObject> collidedAlready = new ArrayList<ServerObject>();

					// Check collisions with other objects in tiles that they
					// both touch
					for (int row = startRow; row <= endRow; row++)
					{
						for (int column = startColumn; column <= endColumn; column++)
						{
							for (ServerObject otherObject : objectGrid[row][column])
							{
								if (otherObject.exists()
										&& otherObject.getID() != object
												.getID()
										&& !collidedAlready
												.contains(otherObject))
								{
									collidedAlready.add(otherObject);
									if (object.getType().charAt(0) == PROJECTILE_TYPE)
									{
										if (otherObject.getType().charAt(0) == CREATURE_TYPE
												&& ((ServerCreature) otherObject)
														.isAttackable()
												&& otherObject.getID() != ((ServerProjectile) object)
														.getOwnerID()
												&& ((ServerCreature) otherObject)
														.getTeam() != ((ServerProjectile) object)
														.getOwner().getTeam()
												&& ((ServerProjectile) object)
														.collidesWith(otherObject))
										{
											if (object.getType().contains(
													PIERCING_TYPE))
											{
												if (!((ServerProjectile) object)
														.hasCollided(otherObject))
												{
													((ServerCreature) otherObject)
															.inflictDamage(
																	((ServerProjectile) object)
																			.getDamage(),
																	((ServerProjectile) object)
																			.getOwner());
													((ServerProjectile) object)
															.addCollided(otherObject);
												}
											}
											else
											{

												((ServerProjectile) object)
														.destroy();
											}
										}
									}
									else if (object.getType().equals(
											EXPLOSION_TYPE))
									{
										if (otherObject.getType().charAt(0) == CREATURE_TYPE
												&& ((ServerCreature) otherObject)
														.isAttackable()
												&& otherObject.getID() != ((ServerProjectile) object)
														.getOwnerID()
												&& ((ServerCreature) otherObject)
														.getTeam() != ((ServerProjectile) object)
														.getOwner().getTeam()
												&& object
														.collidesWith(otherObject)
												&& !((ServerProjectile) object)
														.hasCollided(otherObject))
										{
											((ServerCreature) otherObject)
													.inflictDamage(
															((ServerProjectile) object)
																	.getDamage(),
															((ServerProjectile) object)
																	.getOwner());
											((ServerProjectile) object)
													.addCollided(otherObject);

										}
									}
									else if (object.getType()
											.equals(SLIME_TYPE)
											&& otherObject.getType().equals(
													PLAYER_TYPE)
											&& object.collidesWith(otherObject)
											&& getWorldCounter() % 20 == 0)
									{

										((ServerCreature) otherObject)
												.inflictDamage(
														((ServerSlime) object)
																.getDamage(),
														(ServerCreature) object);

									}
									// If a player collided with an item
									else if (otherObject.getType().charAt(0) == ITEM_TYPE
											&& object.getType().equals(
													PLAYER_TYPE)
											&& ((ServerCreature) object)
													.isAlive()
											&& object.collidesWith(otherObject))
									{
										ServerItem item = (ServerItem) otherObject;
										ServerCreature player = (ServerCreature) object;
										if (!(item.hasCoolDown() && item
												.getSource().getID() == player
												.getID())
												&& player.getInventory().size() <= ServerPlayer.MAX_INVENTORY)
										{
											if (player.getInventory().size() < ServerPlayer.MAX_INVENTORY)
											{
												player.addItem(item);
												item.setSource(player);
												item.destroy();
											}
											else if (player.getInventory()
													.size() == ServerPlayer.MAX_INVENTORY
													&& item.getType().charAt(1) == STACK_TYPE
															.charAt(1))
											{
												// Only if the potion already
												// exists, add it
												for (ServerItem sItem : player
														.getInventory())
													if (sItem.getType().equals(
															item.getType()))
													{
														player.addItem(item);
														item.setSource(player);
														item.destroy();
														break;
													}
											}
										}
									}
									// If stackable items collide
									else if (object.collidesWith(otherObject)
											&& object.getType().charAt(0) == ITEM_TYPE
											&& otherObject.getType().charAt(0) == ITEM_TYPE
											&& object.getType().charAt(1) == STACK_TYPE
													.charAt(1)
											&& otherObject.getType().charAt(1) == STACK_TYPE
													.charAt(1))
									{
										if (object.getType().equals(
												otherObject.getType())
												&& object.getID() != otherObject
														.getID())
										{
											((ServerItem) object)
													.increaseAmount(((ServerItem) otherObject)
															.getAmount());
											otherObject.destroy();
										}
									}
									// Collision of weapons and creatures
									else if (object.getType().charAt(0) == ANIMATION_TYPE
											&& object.getType().charAt(1) == WEAPON_SWING_TYPE
													.charAt(1))
									{
										if (otherObject.getType().charAt(0) == CREATURE_TYPE
												&& ((ServerCreature) otherObject)
														.isAttackable()
												&& otherObject.getID() != ((ServerWeaponSwing) object)
														.getOwnerID()
												&& ((ServerCreature) otherObject)
														.getTeam() != ((ServerWeaponSwing) object)
														.getWielder().getTeam()
												&& ((ServerWeaponSwing) object)
														.collidesWith(otherObject)
												&& !((ServerWeaponSwing) object)
														.hasCollided(otherObject))
										{
											((ServerCreature) otherObject)
													.inflictDamage(
															((ServerWeaponSwing) object)
																	.getDamage(),
															((ServerWeaponSwing) object)
																	.getOwner());
											((ServerWeaponSwing) object)
													.addCollided(otherObject);
										}
									}
									else if (object.getType().equals(
											CASTLE_TYPE)
											&& otherObject.getType().equals(
													MONEY_TYPE)
											&& !((ServerMoney) otherObject)
													.hasCoolDown()
											&& ((ServerCastle) object)
													.getCurrentGoblinTier() < 5)
									{
										((ServerCastle) object)
												.addMoney(((ServerMoney) otherObject)
														.getAmount());
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
						// Apply gravity first (DEFINITELY BEFORE CHECKING
						// VSPEED)
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

						// Detect the rows and columns of the tiles that the
						// object collides with in this tick
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
							startColumn = (int) (x1 / TILE_SIZE - 1);
							endColumn = (int) ((x2 + hSpeed) / TILE_SIZE + 1);
						}
						else if (hSpeed < 0)
						{
							startColumn = (int) ((x1 + hSpeed) / TILE_SIZE - 1);
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

						// Check for collions with the tiles determined above
						if (vSpeed > 0)
						{
							// The row and column of the tile that was collided
							// with
							int collideRow = 0;

							for (int row = startRow; row <= endRow; row++)
							{
								for (int column = startColumn; column <= endColumn; column++)
								{
									if (((tileGrid[row][column] >= 'A' || ((tileGrid[row][column] > ' ' && tileGrid[row][column] <= '/') && !((((object
											.getType().equals(PLAYER_TYPE) && ((ServerPlayer) object)
											.isDropping()) || object.getType()
											.charAt(0) == PROJECTILE_TYPE)))))
											&& column * TILE_SIZE < x2
											&& column * TILE_SIZE + TILE_SIZE > x1))
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
								// Snap the object to the colliding tile
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
							// The row and column of the tile that was collided
							// with
							int collideRow = 0;

							for (int row = startRow; row <= endRow; row++)
							{
								for (int column = startColumn; column <= endColumn; column++)
								{
									if (tileGrid[row][column] >= 'A'
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
								// Snap the object to the colliding tile
								object.setY(collideRow * TILE_SIZE + TILE_SIZE
										+ 1);
								object.setVSpeed(0);
							}
						}

						if (hSpeed > 0)
						{
							// The row and column of the tile that was collided
							// with
							int collideColumn = 0;

							for (int row = startRow; row <= endRow; row++)
							{
								for (int column = startColumn; column <= endColumn; column++)
								{
									if (tileGrid[row][column] >= 'A'
											&& row * TILE_SIZE < y2
											&& row * TILE_SIZE + TILE_SIZE > y1)
									{
										if (x2 + hSpeed >= column * TILE_SIZE
												&& x2 <= column * TILE_SIZE)
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
								// Snap the object to the colliding tile
								object.setX(collideColumn * TILE_SIZE
										- object.getWidth());
								object.setHSpeed(0);
							}
						}
						else if (hSpeed < 0)
						{
							// The row and column of the tile that was collided
							// with
							int collideColumn = 0;

							for (int row = startRow; row <= endRow; row++)
							{
								for (int column = startColumn; column <= endColumn; column++)
								{
									if (tileGrid[row][column] >= 'A'
											&& row * TILE_SIZE < y2
											&& row * TILE_SIZE + TILE_SIZE > y1)
									{
										if (x1 + hSpeed <= column * TILE_SIZE
												+ TILE_SIZE
												&& x1 >= column * TILE_SIZE
														+ TILE_SIZE)
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
								// Snap the object to the colliding tile
								object.setX(collideColumn * TILE_SIZE
										+ TILE_SIZE);
								object.setHSpeed(0);
							}
						}
					}

					// Move this object based on its vertical speed and
					// horizontal speed
					if (moveHorizontal)
					{
						// Don't let the player move when trying to swing a
						// sword
						if (!(object.getType().contains(PLAYER_TYPE))
								|| !((ServerPlayer) object).inAction())
						{
							object.setX(object.getX() + object.getHSpeed());
						}
					}
					if (moveVertical)
					{
						object.setY(object.getY() + object.getVSpeed());
					}

					// Update this object based on its type
					if (object.getType().equals(DAMAGE_INDICATOR_TYPE))
					{
						((ServerDamageIndicator) object).update();
					}
					else if (object.getType().charAt(0) == PROJECTILE_TYPE)
					{
						if ((!moveHorizontal || !moveVertical))
						{
							((ServerProjectile) object).destroy();
						}
						else
						{
							((ServerProjectile) object).update();
						}
					}
					else if (object.getType().charAt(0) == ANIMATION_TYPE)
					{
						if (object.getType().equals(WEAPON_SWING_TYPE))
						{
							((ServerWeaponSwing) object).update();
						}
						else if (object.getType().equals(EXPLOSION_TYPE))
						{
							((ServerProjectile) object).updateExplosion();
						}
					}
					else if (object.getType().charAt(0) == CREATURE_TYPE)
					{
						if (object.getType().equals(PLAYER_TYPE))
						{
							if (((ServerPlayer) object).isPerformingAction())
							{
								((ServerPlayer) object).performAction(
										((ServerPlayer) object).getNewMouseX(),
										((ServerPlayer) object).getNewMouseY());
								((ServerPlayer) object)
										.setPerformingAction(false);
							}
							((ServerPlayer) object).updatePlayer();
						}
						else if (object.getType().equals(SLIME_TYPE))
						{
							((ServerSlime) object).update();
						}
						else if (object.getType().contains(GOBLIN_TYPE))
						{
							((ServerGoblin) object).update();
						}
						else if (object.getType().equals(CASTLE_TYPE))
						{
							((ServerCastle) object).update();
						}
					}

				}
				// Remove this object from the game if its 'exists' variable is
				// false, unless it's a castle or a chest
				else if (!object.getType().equals(CASTLE_TYPE)
						&& !object.getType().equals(CHEST_TYPE))
				{
					objectsToRemove.add(object);
				}
				else if (object.getType().equals(CHEST_TYPE))
				{
					((ServerChest) object).update();
				}
			}

		}
		catch (ConcurrentModificationException e)
		{
			System.out.println("Concurrent Modification Exception");
		}

		// Increase the world counter by 1 after this game tick
		worldCounter++;

		// Update game time
		if (worldCounter % COUNTER_TIME == 0)
		{
			worldTime++;

			if (worldTime >= DAY_COUNTERS)
			{
				worldTime = 0;
			}
		}
	}

	/**
	 * Add a new object to the list of objects in the world
	 * @param object
	 */
	public synchronized void add(ServerObject object)
	{
		objectsToAdd.add(object);
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
	 * @param object the object to object
	 * @param startRow the row to start checking for the object
	 * @param endRow the row to end checking for the object
	 * @param startColumn the column to start checking for the object
	 * @param endColumn the column to end checking for the object
	 */
	public void updateObjectTiles(ServerObject object, int startRow,
			int endRow, int startColumn, int endColumn)
	{
		ArrayList<RowCol> indexesToRemove = new ArrayList<RowCol>();
		ArrayList<RowCol> objectTiles = object.getObjectTiles();

		// Choose the object tiles to remove the object from
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

		// Add the object to the tiles that the object enters
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
	 * Just remove an object from
	 * @param object
	 */
	public void removeFromObjectTiles(ServerObject object)
	{
		ArrayList<RowCol> indexesToRemove = new ArrayList<RowCol>();
		ArrayList<RowCol> objectTiles = object.getObjectTiles();

		// Choose the object tiles to remove the object from
		for (RowCol tile : objectTiles)
		{
			if (!object.exists()
					|| !(object
							.collidesWith(tile.getColumn() * OBJECT_TILE_SIZE,
									tile.getRow() * OBJECT_TILE_SIZE,
									tile.getColumn() * OBJECT_TILE_SIZE
											+ OBJECT_TILE_SIZE,
									tile.getRow() * OBJECT_TILE_SIZE
											+ OBJECT_TILE_SIZE)))
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

	/**
	 * Add a creature to blue team
	 * @param creature
	 */
	public void addToBlue(ServerCreature creature)
	{
		blueTeam.add(creature);
	}

	/**
	 * Remove a creature from blue team
	 * @param creature
	 */
	public void removeFromBlue(ServerCreature creature)
	{
		blueTeam.remove(creature);
	}

	/**
	 * Add a creature to red team
	 * @param creature
	 */
	public void addToRed(ServerCreature creature)
	{
		redTeam.add(creature);
	}

	/**
	 * Remove a creature from red team
	 * @param creature
	 */
	public void removeFromRed(ServerCreature creature)
	{
		redTeam.remove(creature);
	}

	// //////////////////////
	// GETTERS AND SETTERS//
	// //////////////////////
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

	public long getWorldCounter()
	{
		return worldCounter;
	}

	public ArrayList<ServerObject>[][] getObjectGrid()
	{
		return objectGrid;
	}

	public void setObjectGrid(ArrayList<ServerObject>[][] objectGrid)
	{
		this.objectGrid = objectGrid;
	}

	public ArrayList<ServerCreature> getRedTeam()
	{
		return redTeam;
	}

	public void setRedTeam(ArrayList<ServerCreature> redTeam)
	{
		this.redTeam = redTeam;
	}

	public ArrayList<ServerCreature> getBlueTeam()
	{
		return blueTeam;
	}

	public void setBlueTeam(ArrayList<ServerCreature> blueTeam)
	{
		this.blueTeam = blueTeam;
	}

	public int getRedCastleX()
	{
		return redCastleX;
	}

	public int getBlueCastleX()
	{
		return blueCastleX;
	}

	public int getRedCastleY()
	{
		return redCastleY;
	}

	public int getBlueCastleY()
	{
		return blueCastleY;
	}

	public ArrayList<ServerSpawner> getRedSpawners()
	{
		return redSpawners;
	}

	public ArrayList<ServerSpawner> getBlueSpawners()
	{
		return blueSpawners;
	}

	public ServerCastle getBlueCastle()
	{
		return blueCastle;
	}

	public ServerCastle getRedCastle()
	{
		return redCastle;
	}

	public int getWorldTime()
	{
		return worldTime;
	}

	public void setWorldTime(int worldTime)
	{
		this.worldTime = worldTime;
	}

	public void setWorldCounter(long worldCounter)
	{
		this.worldCounter = worldCounter;
	}

}
