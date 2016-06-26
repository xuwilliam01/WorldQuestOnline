package Server.Creatures;

import java.util.ArrayList;

import Effects.ServerDamageIndicator;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Items.ServerItem;
import Server.Items.ServerProjectile;
import Server.Items.ServerWeapon;
import Server.Items.ServerWeaponSwing;
import Tools.RowCol;

/**
 * A goblin class
 * @author alex
 *
 */
public class ServerGoblin extends ServerCreature
{

	// Types of goblins
	public final static int NUM_TYPES = 12;

	/**
	 * The default HP of a goblin of a certain type
	 */
	public final static int GOBLIN_HP = 50;
	public final static int GOBLIN_PEASANT_HP = 50;
	public final static int GOBLIN_WIZARD_HP = 60;
	public final static int GOBLIN_WORKER_HP = 70;
	public final static int GOBLIN_NINJA_HP = 60;
	public final static int GOBLIN_LORD_HP = 150;
	public final static int GOBLIN_SOLDIER_HP = 80;
	public final static int GOBLIN_GUARD_HP = 90;
	public final static int GOBLIN_KNIGHT_HP = 125;
	public final static int GOBLIN_GIANT_HP = 500;
	public final static int GOBLIN_GENERAL_HP = 150;
	public final static int GOBLIN_KING_HP = 250;

	public final static int[] GOBLIN_TIER_PRICE = {10, 25, 50, 75, 100};

	/**
	 * The speed at which the goblin walks
	 */
	private double movementSpeed = 3;

	/**
	 * The initial speed the goblin jumps at
	 */
	private int jumpSpeed = 12;

	/**
	 * The action currently performed by the goblin
	 */
	private String action;

	/**
	 * The number of frames before the goblin can perform another action
	 */
	private int actionDelay;

	/**
	 * The number of frames that has passed after the goblin's action is
	 * disabled / the goblin just recently performed an action
	 */
	private int actionCounter;

	/**
	 * The counter that plays the death animation
	 */
	private long deathCounter = -1;

	/**
	 * The target for the goblin to follow and attack
	 */
	private ServerCreature target;

	/**
	 * Whether or not the goblin is actually at the target
	 */
	private boolean onTarget = false;

	/**
	 * The range to lock on to an enemy
	 */
	private int targetRange = 250;

	/**
	 * Whether or not the goblin is a melee user or ranged
	 */
	private boolean isMelee = true;

	/**
	 * The range for the goblin to start fighting the target
	 */
	private int fightingRange;

	/**
	 * The amount of armour the goblin has (reducing the total damage taken)
	 */
	private double armour = 0;

	/**
	 * The damage the goblin does
	 */
	private int damage;

	/**
	 * The weapon the goblin uses
	 */
	private String weapon;

	/**
	 * The max goblin level that this goblin can be
	 */
	private int maxGoblinLevel;

	/**
	 * Constructor
	 */
	public ServerGoblin(double x, double y, ServerWorld world, int team,
			int maxGoblinLevel)
	{
		super(x, y, 20, 64, -24, -64, ServerWorld.GRAVITY, "GOB_RIGHT_0_0.png",
				"", GOBLIN_HP, world, true);

		int numTypes = (int) (Math.random()
				* Math.min(NUM_TYPES, maxGoblinLevel * 2) + 1);

		this.maxGoblinLevel = maxGoblinLevel;

		switch (numTypes)
		{
		case 1:
			setType(ServerWorld.NAKED_GOBLIN_TYPE);
			setImage("GOB_RIGHT_0_0.png");
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE)
					+ ServerWorld.TILE_SIZE / 2;
			setMaxHP(GOBLIN_HP);
			setHP(GOBLIN_HP);

			weapon = "DAWOOD_0.png";
			damage = 3;
			setName("A Naked Goblin");
			break;
		case 2:
			setType(ServerWorld.GOBLIN_PEASANT_TYPE);
			setImage("GOBPEASANT_RIGHT_0_0.png");
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE)
					+ ServerWorld.TILE_SIZE * 3;
			setMaxHP(GOBLIN_PEASANT_HP);
			setHP(GOBLIN_PEASANT_HP);

			armour = 0.1;
			damage = 4;
			weapon = "HASTONE_0.png";
			setName("A Goblin Peasant");
			break;
		case 3:
			setType(ServerWorld.GOBLIN_SOLDIER_TYPE);
			setImage("GOBSOLDIER_RIGHT_0_0.png");
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE)
					+ ServerWorld.TILE_SIZE;

			setMaxHP(GOBLIN_SOLDIER_HP);
			setHP(GOBLIN_SOLDIER_HP);

			armour = 0.4;
			weapon = "AXIRON_0.png";
			damage = 6;
			setName("A Goblin Soldier");
			break;
		case 4:
			setType(ServerWorld.GOBLIN_NINJA_TYPE);
			setImage("GOBNINJA_RIGHT_0_0.png");
			fightingRange = (int) (Math.random() * 100 + 250);
			setMaxHP(GOBLIN_NINJA_HP);
			setHP(GOBLIN_NINJA_HP);

			weapon = ServerWorld.NINJASTAR_TYPE;
			isMelee = false;
			movementSpeed = 5;
			jumpSpeed = 16;
			setName("A Ninja Goblin");
			break;
		case 5:
			setType(ServerWorld.GOBLIN_WORKER_TYPE);
			setImage("GOBWORKER_RIGHT_0_0.png");
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE)
					+ ServerWorld.TILE_SIZE * 2;
			setMaxHP(GOBLIN_WORKER_HP);
			setHP(GOBLIN_WORKER_HP);
			
			armour = 0.1;
			damage = 7;
			movementSpeed = 4;
			jumpSpeed = 14;
			weapon = "HAIRON_0.png";
			setName("A Goblin Warrior");
			break;
		case 6:
			setType(ServerWorld.GOBLIN_GUARD_TYPE);
			setImage("GOBGUARD_RIGHT_0_0.png");
			setMaxHP(GOBLIN_GUARD_HP);
			setHP(GOBLIN_GUARD_HP);
			
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE)
					+ ServerWorld.TILE_SIZE;
			armour = 0.2;
			damage = 8;
			weapon = "SWIRON_0.png";
			setName("A Goblin Guard");
			break;
		case 7:
			setType(ServerWorld.GOBLIN_KNIGHT_TYPE);
			setImage("GOBKNIGHT_RIGHT_0_0.png");
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE)
					+ ServerWorld.TILE_SIZE;
			setMaxHP(GOBLIN_KNIGHT_HP);
			setHP(GOBLIN_KNIGHT_HP);
			
			armour = 0.4;
			weapon = "SWIRON_0.png";
			damage = 8;
			setName("A Goblin Knight");
			break;
		case 8:
			setType(ServerWorld.GOBLIN_LORD_TYPE);
			setImage("GOBLORD_RIGHT_0_0.png");
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE)
					+ ServerWorld.TILE_SIZE;
			setMaxHP(GOBLIN_LORD_HP);
			setHP(GOBLIN_LORD_HP);
			
			armour = 0.3;
			weapon = "SWGOLD_0.png";
			damage = 12;
			setName("A Goblin Lord");
			break;
		case 9:
			setType(ServerWorld.GOBLIN_WIZARD_TYPE);
			setImage("GOBWIZARD_RIGHT_0_0.png");
			fightingRange = (int) (Math.random() * 250 + 250);
			setMaxHP(GOBLIN_WIZARD_HP);
			setHP(GOBLIN_WIZARD_HP);
			
			armour = 0.2;
			int weaponChoice = (int) (Math.random() * 5);
			weapon = ServerWorld.ICEBALL_TYPE;
			actionDelay = 240;
			if (weaponChoice == 4)
			{
				weapon = ServerWorld.FIREBALL_TYPE;
				actionDelay = 180;
			}
			isMelee = false;
			setName("A Goblin Wizard");
			break;
		case 10:
			setType(ServerWorld.GOBLIN_GIANT_TYPE);
			setImage("GOBGIANT_RIGHT_0_0.png");
			fightingRange = (int) (Math.random() * 16 + 4);
			setMaxHP(GOBLIN_GIANT_HP);
			setHP(GOBLIN_GIANT_HP);
			
			movementSpeed = 2;
			armour = 0;
			damage = 15;
			setWidth(getWidth() * 2);
			setHeight(getHeight() * 2);
			setRelativeDrawX(getRelativeDrawX() * 2);
			setRelativeDrawY(getRelativeDrawY() * 2);
			setName("A Goblin Giant");
			break;
		case 11:
			setType(ServerWorld.GOBLIN_GENERAL_TYPE);
			setImage("GOBGENERAL_RIGHT_0_0.png");
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE)
					+ ServerWorld.TILE_SIZE;
			setMaxHP(GOBLIN_GENERAL_HP);
			setHP(GOBLIN_GENERAL_HP);
			
			armour = 0.7;
			weapon = "SWDIAMOND_0.png";
			damage = 16;
			setName("A Goblin General");
			break;
		case 12:
			setType(ServerWorld.GOBLIN_KING_TYPE);
			setImage("GOBKING_RIGHT_0_0.png");
			fightingRange = (int) (Math.random() * ServerWorld.TILE_SIZE)
					+ ServerWorld.TILE_SIZE;
			setMaxHP(GOBLIN_KING_HP);
			setHP(GOBLIN_KING_HP);
			
			armour = 0.7;
			weapon = "AXDIAMOND_0.png";
			damage = 20;
			setName("A Goblin King");
			break;
		}

		// Randomize the movement speed for each goblin so they don't stack as
		// much
		movementSpeed = Math.random() * 1.0 * (movementSpeed / 2)
				+ movementSpeed * 3.0 / 4;

		if (Math.random() < 0.2)
			addItem(ServerItem.randomItem(getX(), getY()));
		setTeam(team);
	}

	/**
	 * Update the goblin behavior every game tick
	 */
	public void update()
	{
		if (isAlive())
		{
			// Update the goblin's direction or try to jump over tiles
			if (getHSpeed() > 0)
			{
				setDirection("RIGHT");
			}
			else if (getHSpeed() < 0)
			{
				setDirection("LEFT");
			}

			if (Math.abs(getHSpeed()) <= 1
					&& isOnSurface() && !onTarget)
			{
				setVSpeed(-jumpSpeed);
				setOnSurface(false);
			}

			// Update the action counter for the goblin
			if (action != null && actionCounter < actionDelay)
			{
				actionCounter++;
			}
			else
			{
				action = null;
				actionCounter = -1;
			}

			// Have the goblin move towards the enemy base when it has no target
			if (getTarget() == null)
			{
				onTarget = false;
				if (getWorld().getWorldCounter() % 15 == 0)
				{
					setTarget(findTarget());
				}

				if (getTarget() == null)
				{
					if (getTeam() == ServerPlayer.BLUE_TEAM && action == null)
					{
						setHSpeed(movementSpeed);
					}
					else if (action == null)
					{
						setHSpeed(-movementSpeed);
					}
				}
			}
			// Remove the target when it is out of range or dies
			else if (!getTarget().isAlive() || !getTarget().exists()
					|| !quickInRange(getTarget(), targetRange))
			{
				setTarget(null);
			}

			// Follow and attack the target
			else
			{
				if ((getX() + getWidth() / 2 < getTarget().getX())
						&& !onTarget && action == null)
				{
					setHSpeed(movementSpeed);
				}
				else if ((getX() + getWidth() / 2 > getTarget().getX()
						+ getTarget().getWidth())
						&& !onTarget && action == null)
				{
					setHSpeed(-movementSpeed);
				}
				else
				{
					setHSpeed(0);
				}

				// Attack the target with the weapon the goblin uses.
				if (quickInRange(getTarget(), fightingRange))
				{
					onTarget = true;
					if (action == null
							&& getWorld().getWorldCounter() % 30 == 0)
					{
						int actionChoice = (int) (Math.random() * 12);

						// Jump occasionally
						if (actionChoice == 0)
						{
							setTarget(null);
							setVSpeed(-jumpSpeed);
							setOnSurface(false);
							if (getDirection().equals("RIGHT"))
							{
								setHSpeed(movementSpeed);
							}
							else
							{
								setHSpeed(-movementSpeed);
							}
						}
						// Block occasionally
						else if (actionChoice == 1 || actionChoice == 2)
						{
							action = "BLOCK";
							actionDelay = 55;
						}
						else
						{
							if (isMelee)
							{
								if (getType().equals(
										ServerWorld.GOBLIN_GIANT_TYPE))
								{
									action = "PUNCH";
									setHasPunched(false);
									actionDelay = 60;
								}
								else
								{
									action = "SWING";
									actionDelay = 16;

									int angle = 180;
									if (getDirection().equals("RIGHT"))
									{
										angle = 0;
									}

									getWorld().add(
											new ServerWeaponSwing(this, 0, -25,
													weapon, angle,
													actionDelay, damage));
								}
							}
							else
							{
								action = "SHOOT";
								actionDelay = 60;

								int xDist = (int) (getTarget().getX()
										+ getTarget().getWidth() / 2
										- (getX() + getWidth() / 2));

								if (xDist > 0)
								{
									setDirection("RIGHT");
								}
								else if (xDist < 0)
								{
									setDirection("LEFT");
								}

								int yDist = (int) (getTarget().getY()
										+ getTarget().getHeight() / 2
										- (getY() + getHeight() / 3));

								double angle = Math.atan2(yDist, xDist);

								getWorld().add(
										new ServerProjectile(getX()
												+ getWidth() / 2, getY()
												+ getHeight() / 3, this, angle,
												weapon));

							}
						}
					}
				}
				else
				{
					onTarget = false;
				}
			}
		}

		// Update the animation of the goblin
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

			else if (action.equals("SHOOT"))
			{
				if (actionCounter < 4)
				{
					setRowCol(new RowCol(2, 4));
				}
				else if (actionCounter < 8)
				{
					setRowCol(new RowCol(2, 5));
				}
				else if (actionCounter < 2 * actionDelay / 3)
				{
					setRowCol(new RowCol(2, 6));
				}
				else
				{
					setRowCol(new RowCol(0, 0));
				}
			}
			else if (action.equals("BLOCK"))
			{
				setRowCol(new RowCol(2, 9));
			}
			else if (action.equals("PUNCH"))
			{
				if (actionCounter < 10)
				{
					setRowCol(new RowCol(2, 7));
				}
				else if (actionCounter < 30)
				{
					setRowCol(new RowCol(2, 8));

					if (!isHasPunched())
					{
						punch(damage);
						setHasPunched(true);
					}
				}
			}
		}
		else if (getHSpeed() != 0 && isOnSurface())
		{
			int checkFrame = (int) (getWorld().getWorldCounter() % 30);
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
				deathCounter = getWorld().getWorldCounter();
				setRowCol(new RowCol(1, 2));
			}
			else if (getWorld().getWorldCounter() - deathCounter < 20)
			{
				setRowCol(new RowCol(1, 3));
			}
			else if (getWorld().getWorldCounter() - deathCounter < 40)
			{
				setRowCol(new RowCol(1, 4));
			}
			else if (getWorld().getWorldCounter() - deathCounter < 300)
			{
				setRowCol(new RowCol(1, 6));
			}
			else
			{
				destroy();
			}
		}
		else if (Math.abs(getVSpeed()) < 4 && !isOnSurface())
		{
			setRowCol(new RowCol(0, 8));
		}
		else if (getVSpeed() < 0)
		{
			setRowCol(new RowCol(0, 9));
		}
		else if (getVSpeed() > 0)
		{
			setRowCol(new RowCol(0, 7));
		}
		setImage(getBaseImage() + "_" + getDirection() + "_"
				+ getRowCol().getRow()
				+ "_"
				+ getRowCol().getColumn()
				+ ".png");
	}

	/**
	 * Find the nearest enemy creature and attack it, in this case something
	 * from the other team
	 */
	public ServerCreature findTarget()
	{
		ArrayList<ServerCreature> enemyTeam = getWorld().getBlueTeam();

		if (getTeam() == ServerPlayer.BLUE_TEAM)
		{
			enemyTeam = getWorld().getRedTeam();
		}

		for (ServerCreature enemy : enemyTeam)
		{
			if (enemy.isAlive() && quickInRange(enemy, targetRange))
			{
				return enemy;
			}
		}
		return null;
	}

	@Override
	/**
	 * Inflict damage to the goblin, with specific behavior (such as armour or blocking)
	 */
	public void inflictDamage(int amount, ServerCreature source)
	{
		if (!onTarget && source != getTarget())
		{
			setTarget(source);
		}

		amount -= amount * armour;

		if (amount <= 0)
		{
			amount = 1;
		}

		if (action == "BLOCK")
		{
			amount = 0;
		}

		setHP(getHP() - amount);

		double damageX = Math.random() * getWidth() + getX();
		double damageY = Math.random() * getHeight() / 2 + getY() - getHeight()
				/ 3;
		getWorld().add(
				new ServerDamageIndicator(damageX, damageY, Integer
						.toString(amount), ServerDamageIndicator.YELLOW_TEXT,
						getWorld()));

		// Play the death animation for a goblin when it dies
		if (getHP() <= 0 && isAlive())
		{
			setAlive(false);

			dropInventory();
			setHSpeed(0);
			setVSpeed(0);

			setAttackable(false);

			actionCounter = -1;
			action = null;
		}
	}

	// ///////////////////////
	// GETTERS AND SETTERS //
	// ///////////////////////
	public ServerCreature getTarget()
	{
		return target;
	}

	public void setTarget(ServerCreature target)
	{
		this.target = target;
	}

	public int getMaxGoblinLevel()
	{
		return maxGoblinLevel;
	}

	public void increaseMaxGoblinLevel()
	{
		maxGoblinLevel++;
	}
}
