package Server.Creatures;

import java.util.ArrayList;

import Server.ServerWorld;
import Server.Items.ServerProjectile;

public class ServerCastle extends ServerCreature
{

	public final static int CASTLE_HP = 10000;

	private int targetRange = 2000;

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
			if (getWorld().getWorldCounter() % 30 == 0)
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
						ServerWorld.STEELARROW_TYPE);
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
		ServerCreature target = null;

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
			if (enemy.isAlive() && quickInRange(enemy, targetRange))
			{
				target = enemy;
			}
		}

		return target;
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
