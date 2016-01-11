package Server;

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.StringTokenizer;

import Effects.ServerDamageIndicator;
import Server.Creatures.ServerCastle;
import Server.Creatures.ServerChest;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerEnemy;
import Server.Creatures.ServerPlayer;
import Server.Creatures.ServerSlime;
import Server.Creatures.ServerVendor;
import Server.Items.ServerItem;
import Server.Items.ServerMoney;
import Server.Items.ServerPotion;
import Server.Items.ServerWeaponSwing;
import Server.Projectile.ServerProjectile;
import Tools.RowCol;

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
	public final static String EXPLOSION_TYPE = PROJECTILE_TYPE + "E";

	public final static char CREATURE_TYPE = 'C';
	public final static String PLAYER_TYPE = CREATURE_TYPE + "C";
	public final static String PLAYER_GHOST_TYPE = PLAYER_TYPE + "G";
	public final static String NPC_TYPE = CREATURE_TYPE + "N";
	public final static String SLIME_TYPE = NPC_TYPE + "S";
	public final static String GHOUL_TYPE = NPC_TYPE + "G";
	public final static String CHEST_TYPE = CREATURE_TYPE + "M";
	public final static String CASTLE_TYPE = CREATURE_TYPE + "T";
	public final static String VENDOR_TYPE = CREATURE_TYPE + "V";

	public final static char ITEM_TYPE = 'I';
	public final static String EQUIP_TYPE = ITEM_TYPE + "E";
	public final static String STACK_TYPE = ITEM_TYPE + "S";
	public final static String POTION_TYPE = STACK_TYPE + "P";
	public final static String HP_POTION_TYPE = POTION_TYPE + "H";
	public final static String WEAPON_TYPE = EQUIP_TYPE + "W";
	public final static String MELEE_TYPE = WEAPON_TYPE + "M";
	public final static String RANGED_TYPE = WEAPON_TYPE + "R";
	public final static String ARMOUR_TYPE = EQUIP_TYPE + "A";
	public final static String SHIELD_TYPE = EQUIP_TYPE + "S";

	public final static String DAGGER_TYPE = MELEE_TYPE + "D";
	public final static String AX_TYPE = MELEE_TYPE + "A";
	public final static String SWORD_TYPE = MELEE_TYPE + "S";
	public final static String HALBERD_TYPE = MELEE_TYPE + "H";

	public final static char WOOD_TIER = 'W';
	public final static char STONE_TIER = 'S';
	public final static char IRON_TIER = 'I';
	public final static char GOLD_TIER = 'G';
	public final static char DIAMOND_TIER = 'D';

	public final static String MONEY_TYPE = STACK_TYPE + "M";

	public final static String ACCESSORY_TYPE = EQUIP_TYPE + "A";

	public final static char ANIMATION_TYPE = 'A';
	public final static String WEAPON_SWING_TYPE = ANIMATION_TYPE + "S";

	public final static char TEXT_TYPE = 'T';
	public final static String DAMAGE_INDICATOR_TYPE = TEXT_TYPE + "D";

	public final static String HP_50 = HP_POTION_TYPE + "50";

	public final static String GRID_FILE = "NewWorld.txt";

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
	 * Array of different types of creatures, buildings, background objects, and
	 * spawners possible in the world (for the purpose of the creator world)
	 * 
	 * (MAKE SURE TO SET WORLD WHEN CREATING CREATURES)
	 */
	private final ServerObject[] objectTypes = { new ServerSlime(0, 0, -1, -1,
			ServerWorld.GRAVITY, "SLIME_0.png", null) };

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
	 * The next time (in milliseconds) to spawn another enemy
	 */
	private long spawnTimer = 0;

	/**
	 * The counter showing how many frames the server has run
	 */
	private long worldCounter = 0;

	/**
	 * Constructor for server
	 * @throws IOException
	 */
	public ServerWorld() throws IOException
	{
		newWorld();
		objects = new ArrayList<ServerObject>();
		objectsToAdd = new ArrayDeque<ServerObject>();

		// These methods should reference a list to figure out where everything
		// goes
		// The list will be created using the map file
		addEnemies();
		addChests();
		addCastles();
		addVendors();
	}

	/**
	 * Spawn enemies in the world
	 */
	public void addEnemies()
	{
		for (int no = 0; no < 10; no++)
		{
			ServerEnemy newEnemy = new ServerSlime(50 * no + 50, 50, -1, -1,
					GRAVITY,
					"SLIME_0.png", this);
			// newEnemy.addItem(ServerItem.randomItem(newEnemy.getX(),
			// newEnemy.getY()));
			newEnemy.addItem(ServerPotion.randomPotion(newEnemy.getX(),
					newEnemy.getY()));
			add(newEnemy);
		}
	}

	/**
	 * Spawn enemies if the current time passes the spawn timer, then reset the
	 * timer
	 */
	public void spawnEnemies()
	{
		if (System.currentTimeMillis() >= spawnTimer)
		{
			int spawnLocation = (int) (Math.random() * 9 + 9);

			ServerEnemy newEnemy = new ServerSlime(400 * spawnLocation + 50,
					50, -1, -1,
					GRAVITY,
					"SLIME_0.png", this);
			add(newEnemy);
			newEnemy.addItem(ServerItem.randomItem(newEnemy.getX(),
					newEnemy.getY()));
			spawnTimer = System.currentTimeMillis()
					+ (int) (Math.random() * 15000 + 5000);
		}
	}

	/**
	 * Add chests
	 */
	public void addChests()
	{
		ServerChest newChest = new ServerChest(1000, 100, this);
		add(newChest);
	}

	public void addCastles()
	{
		ServerCastle newCastle = new ServerCastle(400, 100,
				ServerCreature.RED_TEAM,
				this);
		add(newCastle);
		newCastle = new ServerCastle(5000, 100, ServerCreature.BLUE_TEAM, this);
		add(newCastle);
	}

	public void addVendors()
	{
		ServerVendor newVendor = new ServerVendor(1500, 100, this, 3);
		add(newVendor);
	}

	/**
	 * Create a new world
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void newWorld() throws IOException
	{
		BufferedReader worldInput = new BufferedReader(new FileReader(new File(
				"Resources", GRID_FILE)));
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

		worldInput.close();

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
	}

	/**
	 * Update all the objects, doing whatever needs to be done
	 */
	public synchronized void update()
	{
		spawnEnemies();

		// Remove all the objects that no longer exist
		for (ServerObject object : objectsToRemove)
		{
			removeFromObjectTiles(object);
			objects.remove(object);

		}
		objectsToRemove.clear();

		while (!objectsToAdd.isEmpty())
		{
			objects.add(objectsToAdd.poll());
		}

		try
		{
			for (ServerObject object : objects)
			{

				// This will remove the object a frame after it stops existing
				if (object.exists())
				{
					if (object.getType().charAt(0) == ITEM_TYPE
							&& object.isOnSurface())
					{
						object.setHSpeed(0);
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

					// Check collisions with other objects in tiles that they
					// both
					// touch
					for (int row = startRow; row <= endRow; row++)
					{
						for (int column = startColumn; column <= endColumn; column++)
						{
							for (ServerObject otherObject : objectGrid[row][column])
							{
								if (otherObject.exists())
								{
									if (object.getType().equals(BULLET_TYPE))
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
														.collidesWith(otherObject))
										{
											double knockBack = ((ServerProjectile) object)
													.getKnockBack();

											if (object.getHSpeed() < 0)
											{
												knockBack *= -1;
											}
											((ServerCreature) otherObject)
													.inflictDamage(
															((ServerProjectile) object)
																	.getDamage(),
															knockBack);
											((ServerProjectile) object)
													.destroy();
										}
										// Don't need this
										// else if
										// (otherObject.getType().contains(
										// NPC_TYPE)
										// && object.collidesWith(otherObject)
										// &&
										// ((ServerCreature)otherObject).getTeam()
										// != ((ServerProjectile)
										// object).getOwner().getTeam())
										// {
										// double knockBack =
										// ((ServerProjectile) object)
										// .getKnockBack();
										//
										// if (object.getHSpeed() < 0)
										// {
										// knockBack *= -1;
										// }
										//
										// ((ServerEnemy) otherObject)
										// .inflictDamage(
										// ((ServerProjectile) object)
										// .getDamage(),
										// knockBack);
										// ((ServerProjectile)
										// object).destroy();
										// }
									}
									// If a player collided with an item
									else if (otherObject.getType().charAt(0) == ITEM_TYPE
											&& object.getType().equals(
													PLAYER_TYPE)
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
											double knockBack = ((ServerWeaponSwing) object)
													.getKnockBack();

											if (!((ServerWeaponSwing) object)
													.isClockwise())
											{
												knockBack *= -1;
											}
											((ServerCreature) otherObject)
													.inflictDamage(
															((ServerWeaponSwing) object)
																	.getDamage(),
															knockBack);
											((ServerWeaponSwing) object)
													.addCollided(otherObject);
										}
										// Don't need this
										// else if
										// (otherObject.getType().contains(
										// NPC_TYPE) &&
										// ((ServerCreature)otherObject).getTeam()
										// != ((ServerWeaponSwing)
										// object).getWielder().getTeam()
										// && ((ServerWeaponSwing) object)
										// .collidesWith(otherObject)
										// && !((ServerWeaponSwing) object)
										// .hasCollided(otherObject))
										// {
										// double knockBack =
										// ((ServerWeaponSwing) object)
										// .getKnockBack();
										//
										// if (!((ServerWeaponSwing) object)
										// .isClockwise())
										// {
										// knockBack *= -1;
										// }
										//
										// ((ServerEnemy) otherObject)
										// .inflictDamage(
										// ((ServerWeaponSwing) object)
										// .getDamage(),
										// knockBack);
										// ((ServerWeaponSwing) object)
										// .addCollided(otherObject);
										//
										// System.out.println(((ServerWeaponSwing)
										// object)
										// .getDamage());
										// }
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

						if (vSpeed > 0)
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
								object.setY(collideRow * TILE_SIZE + TILE_SIZE);
								object.setVSpeed(0);
							}
						}

						if (hSpeed >= 0)
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
						}
						else if (hSpeed <= 0)
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
														+ TILE_SIZE
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
								object.setX(collideColumn * TILE_SIZE
										+ TILE_SIZE);
								object.setHSpeed(0);
							}
						}
					}

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

					// Update specific objects
					if (object.getType().equals(SLIME_TYPE))
					{
						((ServerSlime) object).update();
					}
					else if (object.getType().equals(DAMAGE_INDICATOR_TYPE))
					{
						((ServerDamageIndicator) object).update();
					}
					else if (object.getType().charAt(0) == PROJECTILE_TYPE)
					{
						if ((!moveHorizontal || !moveVertical)
								&& object.getType().equals(BULLET_TYPE))
						{
							((ServerProjectile) object).destroy();
						}
						else if (object.getType().equals(EXPLOSION_TYPE))
						{
							((ServerProjectile) object).updateExplosion();
						}
					}
					else if (object.getType().charAt(0) == ANIMATION_TYPE)
					{
						if (object.getType().equals(WEAPON_SWING_TYPE))
						{
							((ServerWeaponSwing) object).update();
						}
					}
					else if (object.getType().equals(PLAYER_TYPE))
					{
						if (((ServerPlayer) object).isPerformingAction())
						{
							((ServerPlayer) object).performAction(
									((ServerPlayer) object).getNewMouseX(),
									((ServerPlayer) object).getNewMouseY());
							((ServerPlayer) object).setPerformingAction(false);
						}
					}

				}
				else
				{
					objectsToRemove.add(object);
				}
			}

		}
		catch (ConcurrentModificationException e)
		{
			System.out.println("Concurrent Modification Exception");
		}

		worldCounter++;
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
	 * @param object
	 * @param startRow
	 * @param endRow
	 * @param startColumn
	 * @param endColumn
	 */
	public void updateObjectTiles(ServerObject object, int startRow,
			int endRow, int startColumn, int endColumn)
	{
		ArrayList<RowCol> indexesToRemove = new ArrayList<RowCol>();
		ArrayList<RowCol> objectTiles = object.getObjectTiles();

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
	 * Just remove
	 * @param object
	 */
	public void removeFromObjectTiles(ServerObject object)
	{
		ArrayList<RowCol> indexesToRemove = new ArrayList<RowCol>();
		ArrayList<RowCol> objectTiles = object.getObjectTiles();

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

}
