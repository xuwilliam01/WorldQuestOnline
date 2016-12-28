package Server.Buildings;

import java.util.ArrayList;

import Server.ServerEngine;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerPlayer;
import Server.Items.ServerProjectile;

public class ServerArrowSource extends ServerObject
{
	
	/**
	 * The target for the source to attack
	 */
	private ServerCreature target;

	/**
	 * The type of arrows the source shoots
	 */
	private String arrowType;
	
	/**
	 * Team the source fights for
	 */
	private int team;
	
	/**
	 * The range in pixels
	 */
	private int range;
	
	/**
	 * The delay in frames
	 */
	private int delay;
	
	private ServerWorld world;
	
	private ServerCreature owner;

	public ServerArrowSource(double x, double y, int team, int delay, String arrowType, int range, ServerCreature owner, ServerWorld world)
	{
		super(x, y, 1, 1, 0, ServerWorld.ARROW_SOURCE_TYPE+"", world.getEngine());
		this.arrowType = arrowType;
		this.team = team;
		this.range = range;
		this.delay = delay;
		this.world = world;
		this.owner = owner;
		
		System.out.println("Added arrow source " + x + " " + y + team);
	}

	@Override
	public void update()
	{
		if (getTarget() == null) {
			if (getWorld().getWorldCounter() % 15 == 0) {
				setTarget(findTarget(range));
			}
		} else if (!getTarget().isAlive() || !getTarget().exists()
				|| !quickInRange(getTarget(), range)) {
//			System.out.println(getTarget().getName());
//			System.out.println(distanceBetween(getTarget()));
//			System.out.println(range);
//			System.out.println(getX() + " " + getY());
//			System.out.println(getTarget().getX() + " " + getTarget().getY());
			setTarget(null);
		} else {
			// Every second and a half calculate the angle to shoot the target
			// from and launch a projectile at it
			if (getWorld().getWorldCounter() % delay == 0) {
				int xDist = (int) (getTarget().getX() + getTarget().getWidth()
						/ 2 - (getX()));

				int yDist = (int) ((getY()) - (getTarget().getY() + getTarget()
				.getHeight() / 2));

				double angle = getAngle(xDist, yDist, ServerProjectile.ARROW_SPEED, ServerProjectile.ARROW_GRAVITY);

				ServerProjectile arrow = new ServerProjectile(getX(),
						getY(), owner, angle, arrowType,getWorld());

				getWorld().add(arrow);
			}
		}
		
	}
	
	/**
	 * Find the nearest enemy creature and attack it (in this case any creature
	 * from the enemy team)
	 */
	public ServerCreature findTarget(int range) {
		ArrayList<ServerCreature> enemyTeam = null;

		if (team == ServerPlayer.BLUE_TEAM) {
			enemyTeam = getWorld().getRedTeam();
		} else if (team == ServerPlayer.RED_TEAM) {
			enemyTeam = getWorld().getBlueTeam();
		}
		for (ServerCreature enemy : enemyTeam) {
			if (enemy.isAlive() && quickInRange(enemy, range)) 
			{
				return enemy;
			}
		}
		return null;
	}
	
	/**
	 * Get angle based on target
	 * @param xDist
	 * @param yDist
	 * @param speed
	 * @param gravity
	 * @return
	 */
	public static double getAngle(int xDist, int yDist, double speed, double gravity)
	{
		int sign = -1;

		double angle = Math
				.atan(((speed * speed) + sign
						* Math.sqrt(Math.pow(
								speed, 4)
								- gravity
								* (gravity
										* xDist * xDist + 2 * yDist
										* speed
										* speed)))
						/ (gravity * xDist));

		if (xDist <= 0) {
			return Math.PI - angle;
		} 
		return angle * -1;
		
	}
	
	public ServerCreature getTarget()
	{
		return target;
	}

	public void setTarget(ServerCreature target)
	{
		this.target = target;
	}

	public ServerWorld getWorld()
	{
		return world;
	}

	public void setWorld(ServerWorld world)
	{
		this.world = world;
	}
	
	
	

}
