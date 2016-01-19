package Server.Creatures;

import java.util.ArrayList;

import Server.ServerSpawner;
import Server.ServerWorld;
import Server.Items.ServerProjectile;

public class ServerCastle extends ServerCreature
{

	public final static int CASTLE_HP = 10000;

	private int targetRange = 1000;
	private int money = 0;
	private int currentGoblinTier = 0;

	/**
	 * The target for the castle to attack
	 */
	private ServerCreature target;

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
		if(currentGoblinTier < ServerGoblin.NUM_TYPES-1 && money >= ServerGoblin.GOBLIN_TIER_PRICE[currentGoblinTier])
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
	 * Find the nearest enemy creature and attack it
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
}
