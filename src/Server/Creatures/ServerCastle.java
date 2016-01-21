package Server.Creatures;

import java.util.ArrayList;

import Server.ServerSpawner;
import Server.ServerWorld;
import Server.Items.ServerProjectile;

/**
 * A castle for a given team
 * @author Alex Raita & William Xu
 *
 */
public class ServerCastle extends ServerCreature
{

	/**
	 * The default HP of a castle
	 */
	public final static int CASTLE_HP = 15000;

	/**
	 * The number of pixels for a target to be in range for the castle to fire at it
	 */
	private int targetRange = 1000;
	
	/**
	 * The money invested in upgrading the castle
	 */
	private int money = 0;
	
	/**
	 * The current tier of the castle
	 */
	private int currentGoblinTier = 0;

	/**
	 * The target for the castle to attack
	 */
	private ServerCreature target;

	/**
	 * Constructor
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param team the team of the castle
	 * @param world the world of the castle
	 */
	public ServerCastle(double x, double y, int team, ServerWorld world)
	{
		super(x, y, -1, -1, 0, 0, ServerWorld.GRAVITY, "BLUE_CASTLE.png",
				ServerWorld.CASTLE_TYPE, CASTLE_HP, world, true);
		if (team == ServerCreature.RED_TEAM)
		{
			setImage("RED_CASTLE.png");
		}
		setTeam(team);
	}

	/**
	 * Update the castle behavior
	 */
	public void update()
	{
		// Try to purchase the next tier of goblin
		if(currentGoblinTier < 5 && money >= ServerGoblin.GOBLIN_TIER_PRICE[currentGoblinTier])
		{
			money -= ServerGoblin.GOBLIN_TIER_PRICE[currentGoblinTier];
			currentGoblinTier++;
			
			ArrayList<ServerSpawner> teamSpawners;
			if(getTeam() == RED_TEAM)
				teamSpawners = getWorld().getRedSpawners();
			else teamSpawners = getWorld().getBlueSpawners();

			if(teamSpawners.size() > 0)
				((ServerGoblin)teamSpawners.get(0).getCreature()).increaseMaxGoblinLevel();

		}

		// Attack a target
		if (getTarget() == null)
		{
			if (getWorld().getWorldCounter() % 15 == 0)
			{
				setTarget(findTarget());
			}
		}
		else if (!getTarget().isAlive() || !getTarget().exists()
				|| !quickInRange(getTarget(), targetRange))
		{
			setTarget(null);
		}
		else
		{
			// Every second and a half calculate the angle to shoot the target from and launch a projectile at it
			if (getWorld().getWorldCounter() % 90 == 0)
			{
				int xDist = (int) (getTarget().getX()
						+ getTarget().getWidth() / 2
						- (getX() + 270));

				int yDist = (int) (getTarget().getY()
						+ getTarget().getHeight() / 2
						- (getY() + 232));

				double angle = Math.atan2(yDist, xDist);

				ServerProjectile arrow = new ServerProjectile(getX()
						+ 270, getY()
						+ 232, this, angle,
						ServerWorld.WOODARROW_TYPE);
				arrow.setGravity(0);

				getWorld().add(arrow);
			}
		}
	}

	/**
	 * Find the nearest enemy creature and attack it (in this case any creature from the enemy team)
	 */
	public ServerCreature findTarget()
	{
		ArrayList<ServerCreature> enemyTeam = null;

		if (getTeam() == ServerPlayer.BLUE_TEAM)
		{
			enemyTeam = getWorld().getRedTeam();
		}
		else if (getTeam() == ServerPlayer.RED_TEAM)
		{
			enemyTeam = getWorld().getBlueTeam();
		}
		for (ServerCreature enemy : enemyTeam)
		{
			if (enemy.isAlive() && quickInRange(enemy, targetRange) && !enemy.getType().equals(ServerWorld.CASTLE_TYPE))
			{
				return enemy;
			}
		}
		return null;
	}

	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////
	public ServerCreature getTarget()
	{
		return target;
	}
	public void setTarget(ServerCreature target)
	{
		this.target = target;
	}
	public void addMoney(int money)
	{
		this.money += money;
	}
	public int getCurrentGoblinTier()
	{
		return currentGoblinTier;
	}
	public int getMoney()
	{
		return money;
	}
}
