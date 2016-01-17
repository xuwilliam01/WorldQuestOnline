package Server.Creatures;

import java.util.ArrayList;

import Effects.ServerDamageIndicator;
import Server.ServerWorld;
import Server.Items.ServerWeapon;
import Server.Items.ServerWeaponSwing;
import Tools.RowCol;

public class ServerGoblin extends ServerCreature
{

	//Types of goblins
	public final static int NUM_TYPES = 2;
	
	/**
	 * The default HP of a goblin of a certain type
	 */
	public final static int GOBLIN_HP = 80;
	public final static int GOBLIN_PEASANT_HP = 80;
	public final static int GOBLIN_WIZARD_HP = 80;
	public final static int GOBLIN_WORKER_HP = 80;
	public final static int GOBLIN_NINJA_HP = 80;
	public final static int GOBLIN_LORD_HP = 80;
	public final static int GOBLIN_SOLDIER_HP = 80;
	public final static int GOBLIN_GUARD_HP = 80;
	public final static int GOBLIN_KNIGHT_HP = 80;
	public final static int GOBLIN_GENERAL_HP = 80;
	public final static int GOBLIN_KING_HP = 80;

	/**
	 * The speed at which the goblin walks
	 */
	private int movementSpeed = 3;

	/**
	 * The initial speed the goblin jumps at
	 */
	private int jumpSpeed = 20;

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
	private int targetRange = 500;

	/**
	 * The amount of armour the goblin has (reducing the total damage taken)
	 */
	private double armour = 0;

	/**
	 * Constructor
	 * @param x
	 * @param y
	 * @param world
	 * @param team
	 * @param type
	 */
	public ServerGoblin(double x, double y, ServerWorld world, int team)
	{
		super(x, y, 16, 64, -24, -64, ServerWorld.GRAVITY, "GOB_RIGHT_0_0.png",
				"", GOBLIN_HP, world, true);
		
		int numTypes = (int) (Math.random()*NUM_TYPES+1);
		switch (numTypes)
		{
		case 1:
			setType(ServerWorld.NAKED_GOBLIN_TYPE);
			setImage("GOB_RIGHT_0_0.png");
			break;
		case 2:
			setType(ServerWorld.GOBLIN_GENERAL_TYPE);
			setImage("GOBGENERAL_RIGHT_0_0.png");
			armour = 0.5;
			break;
		}

		setTeam(team);
	}

	/**
	 * Update the goblin behavior
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
			else if (getHSpeed() == 0
					&& isOnSurface() && !onTarget)
			{
				setVSpeed(-jumpSpeed);
				setOnSurface(false);
			}

			if (action != null && actionCounter < actionDelay)
			{
				actionCounter++;
			}
			else
			{
				action = null;
				actionCounter = -1;
			}

			if (getTarget() == null)
			{
				if (getWorld().getWorldCounter() % 30 == 0)
				{
					setTarget(findTarget());
				}

				if (getTarget() == null)
				{
					if (getTeam() == ServerPlayer.BLUE_TEAM)
					{
						setHSpeed(movementSpeed);
					}
					else
					{
						setHSpeed(-movementSpeed);
					}
				}

			}
			else if (!getTarget().isAlive() || !getTarget().exists()
					|| !quickInRange(getTarget(), targetRange))
			{
				setTarget(null);
				onTarget = false;
			}
			else
			{
				if ((getX() + getWidth())
						- (getTarget().getX()) < -ServerWorld.TILE_SIZE
						&& getX()
								- (getTarget().getX() + getTarget().getWidth()) < -ServerWorld.TILE_SIZE)
				{
					setHSpeed(movementSpeed);
					onTarget = false;
				}
				else if (getX()
						- (getTarget().getX() + getTarget().getWidth()) > ServerWorld.TILE_SIZE
						&& (getX() + getWidth())
								- (getTarget().getX()) > ServerWorld.TILE_SIZE)
				{
					setHSpeed(-movementSpeed);
					onTarget = false;
				}
				else
				{
					onTarget = true;
					setHSpeed(0);
				}

				if (quickInRange(getTarget(), ServerWorld.TILE_SIZE))
				{
					if (action == null
							&& getWorld().getWorldCounter() % 45 == 0)
					{
						action = "SWING";
						actionDelay = 16;
						
						int angle = 180;
						if (getDirection().equals("RIGHT"))
						{
							angle = 0;
						}
						
						if (getType().equals(ServerWorld.GOBLIN_GENERAL_TYPE))
						{
						getWorld().add(new ServerWeaponSwing(this, 0, -25,
								"SWIRON_0.png",angle,
								actionDelay, 4));
						}
						else
						{
							getWorld().add(new ServerWeaponSwing(this, 0, -25,
									"DAWOOD_0.png",angle,
									actionDelay, 2));
						}
					}
				}
			}
		}

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
			else if (getWorld().getWorldCounter() - deathCounter < 10)
			{
				setRowCol(new RowCol(1, 3));
			}
			else if (getWorld().getWorldCounter() - deathCounter < 20)
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
	 * Find the nearest enemy creature and attack it
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
	public void inflictDamage(int amount)
	{
		amount -= amount * armour;

		if (amount <= 0)
		{
			amount = 1;
		}

		setHP(getHP() - amount);

		double damageX = Math.random() * getWidth() + getX();
		double damageY = Math.random() * getHeight() / 2 + getY() - getHeight()
				/ 3;
		getWorld().add(
				new ServerDamageIndicator(damageX, damageY, Integer
						.toString(amount), ServerDamageIndicator.YELLOW_TEXT,
						getWorld()));

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

	public ServerCreature getTarget()
	{
		return target;
	}

	public void setTarget(ServerCreature target)
	{
		this.target = target;
	}

}
