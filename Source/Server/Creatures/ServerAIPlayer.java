package Server.Creatures;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import Server.ServerObject;
import Server.ServerObjectShown;
import Server.ServerWorld;
import Server.Buildings.ServerCastle;
import Server.Effects.ServerText;
import Server.Items.ServerAccessory;
import Server.Items.ServerArmour;
import Server.Items.ServerItem;
import Server.Items.ServerProjectile;
import Server.Items.ServerWeapon;
import Server.Items.ServerWeaponSwing;

/**
 * The player (Type 'P')
 * 
 * @author William Xu & Alex Raita
 *
 */
public class ServerAIPlayer extends ServerCreature{

	public static String[] botNames = 
		{
			"Bot George", 
			"Bot Nick", 
			"Bot Wilson",
			"Bot Manuel",
			"Bot Colin",
			"Bot Sam",
			"Bot Brenton",
			"Bot Vytas",
			"Bot Clive",
			"Bot Aiden",
			"Bot Daniel",
			"Bot Peter",
			"Bot Michael",
			"Bot Yulong",
			"Bot Alex",
			"Bot Jerry",
			"Bot Andrew",
			"Bot Brian",
			"Bot Jesse",
		};
	
	public static LinkedList<String> namesList;
	
	private boolean disconnected = false;

	private int respawnXSpeed;
	private int respawnYSpeed;

	private ServerCastle castle = null;

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
	 * The speed the player moves horizontally
	 */
	private int horizontalMovement;

	/**
	 * The speed the player moves vertically
	 */
	private int verticalMovement;

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
	private int action = ServerCreature.NO_ACTION;

	/**
	 * The counter that plays the death animation
	 */
	private long deathCounter = -1;


	/**
	 * Stores the equipped weapons
	 */
	private ServerItem[] equippedWeapons = new ServerItem[ServerPlayer.MAX_WEAPONS];

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
	 * The weapon being held by the player during action (ex. bows and wands)
	 */
	private ServerObject heldWeapon;

	/**
	 * Stores the mana the player currently has
	 */
	private int mana;

	/**
	 * Stores the maximum possible mana for the player
	 */
	private int maxMana;

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

	private int kills = 0;
	private int deaths = 0;
	private int totalDamageDealt = 0;
	private int totalMoneySpent = 0;

	public final static int RELATIVE_X = -14;
	public final static int RELATIVE_Y = -38;

	private boolean ignoreClient = false;

	private String skinColour;
	private String hair;
	
	public final static int MELEE_TYPE = 0;
	public final static int MELEE_RANGE = ServerWorld.TILE_SIZE * 2;
	public final static int BOW_TYPE = 1;
	public final static int BOW_RANGE = 650;
	public final static int WAND_TYPE = 2;
	public final static int WAND_RANGE = 400;
	private int weaponType;
	private int myRangedType;
	
	private ServerWeapon meleeWeapon;
	private ServerWeapon rangedWeapon;
	
	/**
	 * The target for the a.i. to follow and attack
	 */
	private ServerCreature target;

	/**
	 * Whether or not the a.i. is actually at the target
	 */
	private boolean onTarget = false;

	/**
	 * The distance before the a.i. starts attacking
	 */
	private int fightingRange = MELEE_RANGE;
	
	/**
	 * The range to lock on to an enemy
	 */
	private int targetRange = 750;
	
	/**
	 * Whether or not to jump again
	 */
	private boolean jumpAgain = false;
	
	public ServerAIPlayer(double x, double y, int width, int height, double gravity, ServerWorld world, int team) {
		super(x, y, width, height, RELATIVE_X, RELATIVE_Y, gravity, "BASE_"
				+ world.getEngine().getServer().getPlayerColours()[(int) (Math.random() * world.getEngine().getServer().getPlayerColours().length)] 
						+ "_RIGHT_0_0", ServerWorld.PLAYER_AI_TYPE, world.getBluePlayerStartHP(), world, true); 
		
		if (namesList == null)
		{
			namesList = new LinkedList<String>(Arrays.asList(botNames));
			Collections.shuffle(namesList);
		}
		
		setName(namesList.removeFirst());
		setTeam(team);
		
		// Set the initial variables
		actionDelay = 20;
		actionSpeed = 13;
		canPerformAction = true;
		action = ServerCreature.NO_ACTION;
		performingAction = false;
		newMouseX = 0;
		newMouseY = 0;

		// Set the action counter to -1 when not used
		actionCounter = -1;

		xUpdated = true;
		yUpdated = true;
		
		this.skinColour = world.getEngine().getServer().getPlayerColours()[(int) (Math.random() * world.getEngine().getServer().getPlayerColours().length)];
		baseImage = "BASE_" + skinColour;

		// Set a random hair style for the player
		this.hair = world.getEngine().getServer().getPlayerHairs()[(int)(Math.random() * world.getEngine().getServer().getPlayerHairs().length)];
		
		if (castle == null)
		{
			if (getTeam() == RED_TEAM)
			{
				castle = getWorld().getRedCastle();
			}
			else
			{
				castle = getWorld().getBlueCastle();
			}
		}
		
		this.initPlayer();
	}
	
	@Override
	public void destroy()
	{
		super.destroy();
		// Destroy all the accessories the player has
		if (getHead() != null) {
			getHead().destroy();
			setHead(null);
		}
		if (getBody() != null) {
			getBody().destroy();
			setBody(null);
		}
		namesList.add(getName());
	}

	public void initPlayer()
	{	
		int randomStartMelee = (int) (Math.random() * (2 + Math.min(castle.getTier(), 5)));
		switch (randomStartMelee) {
		case 0:
			meleeWeapon = new ServerWeapon(0, 0, ServerWorld.SWORD_TYPE
					+ ServerWorld.STONE_TIER, getWorld());
			break;
		case 1:
			meleeWeapon = new ServerWeapon(0, 0, ServerWorld.AX_TYPE
					+ ServerWorld.STONE_TIER, getWorld());
			break;
		case 2:
			meleeWeapon = new ServerWeapon(0, 0, ServerWorld.SWORD_TYPE
					+ ServerWorld.IRON_TIER, getWorld());
			break;
		case 3:
			meleeWeapon = new ServerWeapon(0, 0, ServerWorld.AX_TYPE
					+ ServerWorld.IRON_TIER, getWorld());
			break;
		case 4:
			meleeWeapon = new ServerWeapon(0, 0, ServerWorld.SWORD_TYPE
					+ ServerWorld.GOLD_TIER, getWorld());
			break;
		case 5:
			meleeWeapon = new ServerWeapon(0, 0, ServerWorld.AX_TYPE
					+ ServerWorld.GOLD_TIER, getWorld());
			break;
		case 6:
			meleeWeapon = new ServerWeapon(0, 0, ServerWorld.SWORD_TYPE
					+ ServerWorld.DIAMOND_TIER, getWorld());
			break;
		}
		
		int randomStartRanged = (int) (Math.random() * (3 + Math.min(castle.getTier(), 5)));
		switch (randomStartRanged) {
		case 0:
		case 1:
			rangedWeapon = new ServerWeapon(0, 0, ServerWorld.SLINGSHOT_TYPE, getWorld());
			myRangedType = ServerAIPlayer.BOW_TYPE;
			break;
		case 2:
			rangedWeapon = new ServerWeapon(0, 0, ServerWorld.WOODBOW_TYPE, getWorld());
			myRangedType = ServerAIPlayer.BOW_TYPE;
			break;
		case 3:
			rangedWeapon = new ServerWeapon(0, 0, ServerWorld.ICEWAND_TYPE, getWorld());
			myRangedType = ServerAIPlayer.WAND_TYPE;
			break;
		case 4:
			rangedWeapon = new ServerWeapon(0, 0, ServerWorld.STEELBOW_TYPE, getWorld());
			myRangedType = ServerAIPlayer.BOW_TYPE;
			break;
		case 5:
			rangedWeapon = new ServerWeapon(0, 0, ServerWorld.FIREWAND_TYPE, getWorld());
			myRangedType = ServerAIPlayer.WAND_TYPE;
			break;
		case 6:
			rangedWeapon = new ServerWeapon(0, 0, ServerWorld.MEGABOW_TYPE, getWorld());
			myRangedType = ServerAIPlayer.BOW_TYPE;
			break;
		case 7:
			rangedWeapon = new ServerWeapon(0, 0, ServerWorld.DARKWAND_TYPE, getWorld());
			myRangedType = ServerAIPlayer.WAND_TYPE;
			break;
		}
		
		int randomStartArmor = (int) (Math.random() * (2 + Math.min(castle.getTier(), 4)));
		switch (randomStartArmor) {
		case 0:
			break;
		case 1:
			addItem(new ServerArmour(0, 0, ServerWorld.GREY_NINJA_ARMOUR, getWorld()));
			this.equipArmour(ServerWorld.GREY_NINJA_ARMOUR);
			break;
		case 2:
			addItem(new ServerArmour(0, 0, ServerWorld.BLUE_NINJA_ARMOUR, getWorld()));
			this.equipArmour(ServerWorld.BLUE_NINJA_ARMOUR);
			break;
		case 3:
			addItem(new ServerArmour(0, 0, ServerWorld.RED_NINJA_ARMOUR, getWorld()));
			this.equipArmour(ServerWorld.RED_NINJA_ARMOUR);
			break;
		case 4:
		case 5:
			addItem(new ServerArmour(0, 0, ServerWorld.STEEL_ARMOUR, getWorld()));
			this.equipArmour(ServerWorld.STEEL_ARMOUR);
			break;
		}
		
		setHair(hair);
		
		this.setWeaponType(myRangedType);
	}
	
	/**
	 * Update the player after each tick
	 */
	@Override
	public void update() {
		if (exists()) {
			if (isAlive())
			{
				// On average, will change weapons every 20 seconds
				if (getWorld().getWorldCounter() % 60 == 0 && Math.random() < 0.05)
				{
					if (this.getWeaponType() == MELEE_TYPE)
					{
						this.setWeaponType(this.myRangedType);
					}
					else
					{
						this.setWeaponType(MELEE_TYPE);
					}
				}
				
				// Update the player's direction or try to jump over tiles
				if (getHSpeed() > 0) {
					setDirection("RIGHT");
				} else if (getHSpeed() < 0) {
					setDirection("LEFT");
				}
	
				if (getHSpeed() == 0 && isOnSurface() && !onTarget && action == ServerCreature.NO_ACTION) {
					setVSpeed(-verticalMovement);
					setOnSurface(false);
				}
				
				// Have the bot move towards the enemy base when it has no target
				if (getTarget() == null) {
					if (getWorld().getWorldCounter() % 15 == 0) {
						setTarget(findTarget(targetRange));
					}
	
					onTarget = false;
					if (getTarget() == null && action == ServerCreature.NO_ACTION) {
						if (getTeam() == ServerPlayer.BLUE_TEAM) {
							if (quickInRange(getWorld().getRedCastle(), (double) targetRange)) {
								setTarget(getWorld().getRedCastle());
							} else if (getX() - getWorld().getRedCastleX() < 0) {
								setHSpeed(horizontalMovement);
							} else if (getX() - getWorld().getRedCastleX() > 0) {
								setHSpeed(-horizontalMovement);
							}
	
						} else if (getTeam() == ServerPlayer.RED_TEAM) {
							if (quickInRange(getWorld().getBlueCastle(), (double) targetRange)) {
								setTarget(getWorld().getBlueCastle());
							} else if (getX() - getWorld().getBlueCastleX() < 0) {
								setHSpeed(horizontalMovement);
							} else {
								setHSpeed(-horizontalMovement);
							}
						}
					}
				}
				// Remove the target when it is out of range or dies
				else if (!getTarget().isAlive() || !getTarget().exists() || !quickInRange(getTarget(), targetRange)) {
					setTarget(null);
				}
				// Follow and attack the target
				else if (action == ServerCreature.NO_ACTION)
				{
					// Attack the target with the weapon the goblin uses.
					if (quickInRange(getTarget(), fightingRange)) {
						// System.out.println(getTarget().getImage() + " " +
						// getTarget().getX());
						onTarget = true;
						if (isOnSurface() && getWorld().getWorldCounter() % 5 == 0) {
							int actionChoice = (int) (Math.random() * 36);
							canPerformAction = false;
							
							if (jumpAgain)
							{
								actionChoice = 0;
								jumpAgain = false;
							}
							
							// Jump occasionally
							if (actionChoice < 2) {
								setTarget(null);
								action = ServerCreature.HOP;
								actionDelay = 30;
								setVSpeed(-verticalMovement/1.5);
								setOnSurface(false);
								if (actionChoice == 0)
								{
									setDirection("LEFT");
								}
								else
								{
									setDirection("RIGHT");
								}
								
								if (getDirection().equals("RIGHT")) {
									setHSpeed(this.horizontalMovement);
								} else {
									setHSpeed(-this.horizontalMovement);
								}
								if ((int)(Math.random() * 2) == 0)
								{
									jumpAgain = true;
								}
							}
							// Block occasionally
							else if (actionChoice == 3) {
								action = ServerCreature.BLOCK;
								actionDelay = 55;
							} else {
								if (weaponType == MELEE_TYPE) {
									int angle = 180;
									if (getDirection().equals("RIGHT")) {
										angle = 0;
									}
									actionDelay = meleeWeapon.getActionDelay() + meleeWeapon.getActionDelay()/2;
									actionSpeed = meleeWeapon.getActionSpeed();
									if (heldWeapon != null) {
										heldWeapon.destroy();
									}
									heldWeapon = new ServerWeaponSwing(this,
											0, -20, meleeWeapon.getActionImage(),
											(int) (Math.toDegrees(angle) + 0.5),
											meleeWeapon.getActionSpeed(),
											(int) Math.ceil((meleeWeapon).getDamage()
											* (1 + getBaseDamage() / 100.0)));
									getWorld().add(heldWeapon);
									action = ServerCreature.SWING;
								} else {
	
									int x = getDrawX();
									int y = getDrawY();
	
									String arrowType = "";
									String image = "";
									
									boolean canAttack = true;
	
									int xDist = (int) (getTarget().getX() + getTarget().getWidth() / 2
											- (getX() + getWidth() / 2));
		
									if (xDist > 0) {
										setDirection("RIGHT");
									} else if (xDist < 0) {
										setDirection("LEFT");
									}
		
									int yDist;
		
									double angle = 0;
									double targetHeightFactor = 5;
									
									if (getTarget().getType().equals(ServerWorld.CASTLE_TYPE)) {
										targetHeightFactor = 1.3;
									}
									
									// Vary the player's attack based on the weapon currently
									// equipped
									switch (rangedWeapon.getType()) {
									case ServerWorld.SLINGSHOT_TYPE:
									case ServerWorld.WOODBOW_TYPE:
									case ServerWorld.STEELBOW_TYPE:
										int projectileSpeed = ServerProjectile.ARROW_SPEED;
										double projectileGravity = ServerProjectile.ARROW_GRAVITY;
										
										if (rangedWeapon.getType().equals(ServerWorld.SLINGSHOT_TYPE))
										{
											projectileSpeed = ServerProjectile.BULLET_SPEED;
											projectileGravity = ServerProjectile.BULLET_GRAVITY;
											arrowType = ServerWorld.BULLET_TYPE;
											image = "SLINGSHOT";
										}
										else if (rangedWeapon.getType().equals(ServerWorld.WOODBOW_TYPE))
										{
											arrowType = ServerWorld.WOODARROW_TYPE;
											image = "WOODBOW";
										}
										else
										{
											arrowType = ServerWorld.STEELARROW_TYPE;
											image = "STEELBOW";
										}
										action = ServerCreature.BOW;
										actionDelay = 24;
										
										yDist = (int) ((getY() + getHeight() / 3.0)
												- (getTarget().getY() + getTarget().getHeight() / targetHeightFactor));
		
										int sign = -1;
		
										angle = Math.atan(((projectileSpeed * projectileSpeed)
												+ sign * Math.sqrt(Math.pow(projectileSpeed, 4) - projectileGravity
													* (projectileGravity * xDist * xDist + 2 * yDist * projectileSpeed
													* projectileSpeed))) / (projectileGravity * xDist));
		
										if (!(angle <= Math.PI && angle >= -Math.PI)) {
											fightingRange = (int) (BOW_RANGE / 1.5);
										}
		
										if (xDist <= 0) {
											angle = Math.PI - angle;
										} else {
											angle *= -1;
										}
										
										break;
									case ServerWorld.MEGABOW_TYPE:
										action = ServerCreature.BOW;
										arrowType = ServerWorld.MEGAARROW_TYPE;
										image = "MEGABOW";
										actionDelay = 37;
										yDist = (int) (getTarget().getY() + getTarget().getHeight() / 2
												- (getY() + getHeight() / targetHeightFactor));
										angle = Math.atan2(yDist, xDist);
										break;
									case ServerWorld.FIREWAND_TYPE:
										action = ServerCreature.WAND;
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
										actionDelay = 37;
										yDist = (int) (getTarget().getY() + getTarget().getHeight() / 2
												- (getY() + getHeight() / targetHeightFactor));
										angle = Math.atan2(yDist, xDist);
										break;
									case ServerWorld.ICEWAND_TYPE:
										action = ServerCreature.WAND;
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
										actionDelay = 45;
										yDist = (int) (getTarget().getY() + getTarget().getHeight() / 2
												- (getY() + getHeight() / targetHeightFactor));
										angle = Math.atan2(yDist, xDist);
										break;
									case ServerWorld.DARKWAND_TYPE:
										action = ServerCreature.WAND;
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
										actionDelay = 15;
										yDist = (int) (getTarget().getY() + getTarget().getHeight() / 2
												- (getY() + getHeight() / targetHeightFactor));
										angle = Math.atan2(yDist, xDist);
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
										if (heldWeapon != null) {
											heldWeapon.destroy();
										}
										
										heldWeapon = new ServerObjectShown(x, y, 0, 0, 0, image,
												ServerWorld.WEAPON_HOLD_TYPE, getWorld()
												.getEngine());
										heldWeapon.setSolid(false);
										getWorld().add(heldWeapon);
									} else {
										action = ServerCreature.OUT_OF_MANA;
										actionDelay = 0;
										ServerText message = new ServerText(
												getX() + getWidth() / 2, getY() - getHeight() / 2,
												"!M", ServerText.PURPLE_TEXT, getWorld());
										this.setWeaponType(MELEE_TYPE);
										getWorld().add(message);
									}
								}
							}
						}
					} else {
						onTarget = false;
					}
	
					if (action != ServerCreature.HOP)
					{
						if (getTarget() != null && !onTarget) {
							if ((getX() + getWidth() / 2 < getTarget().getX())) {
								if (getHSpeed() == 0 || getWorld().getWorldCounter() % 20 == 0) {
									setHSpeed(this.horizontalMovement);
								}
							} else {
								if (getHSpeed() == 0 || getWorld().getWorldCounter() % 20 == 0) {
									setHSpeed(-this.horizontalMovement);
								}
							}
						} else {
							setHSpeed(0);
						}
					}
				}
			}
			else
			{
				setHSpeed(0);
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
				setRowCol(0, 0);
				actionCounter = -1;
				action = ServerPlayer.NO_ACTION;
				canPerformAction = true;
			}

			// Update the animation of the player and its accessories
			// The row and column of the frame in the sprite sheet for the image
			if (!isAlive()) {
				if (deathCounter < 0) {
					deathCounter = getWorld().getWorldCounter();
					setRowCol(5, 1);
				} else if (getWorld().getWorldCounter() - deathCounter < 10) {
					setRowCol(5, 1);
				} else if (getWorld().getWorldCounter() - deathCounter < 20) {
					setRowCol(5, 2);
				} else if (getWorld().getWorldCounter() - deathCounter < 300) { // Respawn time here
					setRowCol(5, 4);
				} else {
					if (getBody() != null) {
						getBody().destroy();
						setBody(null);
					}
					
					if (getHead() != null)
					{
						getHead().destroy();
						setHead(null);
					}
					
					initPlayer();
					setAlive(true);

					verticalMovement = respawnYSpeed;
					horizontalMovement = respawnXSpeed;

					if (getTeam() == RED_TEAM) {
						setX(getWorld().getRedCastleX() + 
								getWorld().getRedCastle().getWidth()/2 +
								Math.random() * 200 - 100);
						setY(getWorld().getRedCastleY());

					} else {
						setX(getWorld().getBlueCastleX() + 
								getWorld().getBlueCastle().getWidth()/2 +
								Math.random() * 200 - 100);
						setY(getWorld().getBlueCastleY());
					}

					setHP(getMaxHP());
					mana = maxMana;

					setAttackable(true);
					deathCounter = -1;
				}
			}
			else if (!isOnSurface()) {
				if (Math.abs(getVSpeed()) <= 5) {
					setRowCol(1, 8);
				} else if (getVSpeed() < -5) {
					setRowCol(1, 7);
				} else if (getVSpeed() > 5) {
					setRowCol(1, 9);
				}
			}
			else if (getHSpeed() != 0 && getVSpeed() == 0) {
				int checkFrame = (int) (getWorld().getWorldCounter() % 30);
				if (checkFrame < 5) {
					setRowCol(0, 1);
				} else if (checkFrame < 10) {
					setRowCol(0, 2);
				} else if (checkFrame < 15) {
					setRowCol(0, 3);
				} else if (checkFrame < 20) {
					setRowCol(0, 4);
				} else if (checkFrame < 25) {
					setRowCol(0, 5);
				} else {
					setRowCol(0, 6);
				}
			} 
			else if (actionCounter >= 0) {
				if (action == ServerPlayer.SWING) {
					if (actionCounter < 1.0 * actionSpeed / 4.0) {
						setRowCol(2, 0);
					} else if (actionCounter < 1.0 * actionSpeed / 2.0) {
						setRowCol(2, 1);
					} else if (actionCounter < 1.0 * actionSpeed / 4.0 * 3) {
						setRowCol(2, 2);
					} else if (actionCounter < actionSpeed) {
						setRowCol(2, 3);
					} else {
						setRowCol(0, 0);
					}
				} else if (action == ServerPlayer.BOW) {
					setRowCol(2, 7);
					if (heldWeapon != null) {
						heldWeapon.setX(getDrawX());
						heldWeapon.setY(getDrawY());
					}
				} else if (action == ServerPlayer.WAND) {
					setRowCol(2, 5);
					if (heldWeapon != null) {
						if (getDirection().equals("LEFT")) {
							heldWeapon.setX(getDrawX() - (90 - 64));
						} else {
							heldWeapon.setX(getDrawX());
						}
						heldWeapon.setY(getDrawY());
					}
				} else if (action == ServerPlayer.BLOCK) {
					setRowCol(2, 9);
				} else if (action == ServerPlayer.PUNCH) {
					if (actionCounter < 5) {
						setRowCol(2, 7);
					} else if (actionCounter < 16) {
						setRowCol(2, 8);
						if (!isHasPunched()) {
							punch((int) Math.ceil(PUNCHING_DAMAGE
									* (1 + getBaseDamage() / 100.0)));
							setHasPunched(true);
						}
					}
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
	
	/**
	 * Equip a weapon onto the player
	 * 
	 * @param itemType
	 *            the weapon to equip
	 */
	public void equipWeapon(String itemType) {
		// Find next open spot in equipped
		int pos = 0;
		for (; pos < ServerPlayer.MAX_WEAPONS; pos++) {
			if (equippedWeapons[pos] == null)
				break;
		}

		// If there are no equip slots left
		if (pos == ServerPlayer.MAX_WEAPONS)
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
		ServerItem toRemove = null;
		for (ServerItem item : getInventory()) {
			if (item.getType().equals(itemType)) {
				toRemove = item;
			}
		}
		
		if (toRemove == null)
		{
			System.out.println("Couldn't find armour to equip in inventory!");
			return;
		}
		
		// First replace the armor in the inventory with the current armor, if
		// it exists
		if (equippedArmour != null) {
			getInventory().add(equippedArmour);
		}
		
		equippedArmour = (ServerArmour) toRemove;
		
		if (toRemove != null) {
			getInventory().remove(toRemove);
		}

		ServerAccessory newArmour = new ServerAccessory(this,
				equippedArmour.getArmourImage(), equippedArmour.getArmour(),
				getWorld());
		if (getBody() != null) {
			getBody().destroy();
		}
		setBody(newArmour);
		getWorld().add(newArmour);
	}
	
	public void setHair(String hair)
	{
		ServerAccessory newHair = new ServerAccessory(this, hair, 0, getWorld());
		setHead(newHair);
		getWorld().add(newHair);
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
			if (action == ServerCreature.BLOCK) {
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
						getWorld().getEngine().broadcast("@ " + ServerPlayer.toChars(source.getID()) + " "
								+ source.getTeam());
						((ServerPlayer) source).addKill();
					}
					getWorld().getEngine().broadcast("! " + ServerPlayer.toChars(getID()) + " " + getTeam());

					if (source.getTeam() == ServerCreature.NEUTRAL) {
						String firstName = getTeam() + getName();
						String secondName = ServerCreature.NEUTRAL
								+ source.getName();
						getWorld().getEngine().broadcast("k " + firstName.split(" ").length + " "
								+ firstName + " " + secondName.split(" ").length
								+ " " + secondName);
					} else {
						String firstName = source.getTeam() + source.getName();
						String secondName = getTeam() + getName();
						getWorld().getEngine().broadcast("f " + firstName.split(" ").length + " "
								+ firstName + " " + secondName.split(" ").length
								+ " " + secondName);
					}
				}
				setAlive(false);
				if (heldWeapon != null)
				{
					heldWeapon.destroy();
				}

				dropInventory();

				verticalMovement = 0;
				horizontalMovement = 0;

				setHSpeed(0);
				setVSpeed(0);
				setAttackable(false);
			}
			else
			{
				if (source.getType().equals(ServerWorld.PLAYER_TYPE) && (getTarget() == null 
						|| !getTarget().getType().equals(ServerWorld.PLAYER_TYPE)))
				{
					setTarget(source);
				}
			}
		}
	}
	
	/**
	 * Don't actually drop anything, just destroy it
	 */
	@Override
	public void dropInventory() {
		getInventory().clear();
		equippedWeapons = new ServerItem[ServerPlayer.MAX_WEAPONS];
		equippedArmour = null;
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
		this.maxMana = Math.min(ServerPlayer.PLAYER_MAX_MANA, maxMana);
	}

	public int getHorizontalMovement() {
		return horizontalMovement;
	}

	public void addHorizontalMovement(int amount) {

		if(isAlive())
		{
			this.horizontalMovement = Math.min(horizontalMovement + amount, ServerPlayer.MAX_HSPEED);
			respawnXSpeed = this.horizontalMovement;
		}
		else
			respawnXSpeed = Math.min(respawnXSpeed + amount, ServerPlayer.MAX_HSPEED);
	}

	public int getVerticalMovement() {
		return verticalMovement;
	}

	public void addVerticalMovement(int amount) {
		if(isAlive())
		{
			this.verticalMovement = Math.min(verticalMovement+amount, ServerPlayer.MAX_VSPEED);
			respawnYSpeed = this.verticalMovement;
		}
		else
			respawnYSpeed = Math.min(respawnYSpeed + amount, ServerPlayer.MAX_VSPEED);
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
		return totalDamageDealt + kills * ServerPlayer.PLAYER_BASE_HP + totalMoneySpent * 10;
	}

	public int getPing() {
		return 0;
	}

	public void setDeathCounter(long amount)
	{
		deathCounter = amount;
	}

	public int getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(int weaponType) {
		this.weaponType = weaponType;
		if (weaponType == MELEE_TYPE)
		{
			fightingRange = MELEE_RANGE;
		}
		else if (weaponType == BOW_TYPE)
		{
			fightingRange = BOW_RANGE;
		}
		else if (weaponType == WAND_TYPE)
		{
			fightingRange = WAND_RANGE;
		}
	}

	public ServerCreature getTarget() {
		return target;
	}

	public void setTarget(ServerCreature target) {
		this.target = target;
	}
}