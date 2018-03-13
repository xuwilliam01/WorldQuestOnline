package Server.Spawners;

import Server.ServerWorld;
import Server.Creatures.ServerBat;
import Server.Creatures.ServerEnemy;

/**
 * Spawns a given creature to the world
 * @author Alex Raita & William Xu
 *
 */
public class ServerBatSpawner extends ServerSpawner
{
	/**
	 * Number of bats spawned by this spawner
	 */
	private int batCount = 0;

	/**
	 * Max number of bats per bat spawner
	 */
	public final static int MAX_BATS = 5;

	/**
	 * Constructor
	 * @param x the x-coordinate of the spawner
	 * @param y the y-coordinate of the spawner
	 * @param creatureType the type of creature that will be spawned
	 * @param world the world the creature will be added in
	 */
	public ServerBatSpawner(double x, double y, ServerWorld world)
	{
		super(x, y, world, ServerWorld.BAT_SPAWN_TYPE);
		setImage("BAT_SPAWN");
		setDelay(3000);
	}

	/**
	 * Update the spawner
	 * @param worldCounter
	 */
	@Override
	public void update()
	{
		long worldCounter = getWorld().getWorldCounter();
		if (worldCounter % getDelay() == 0 && batCount < MAX_BATS)
		{

			ServerBat newbat = new ServerBat(getX(), getY()
					- getHeight()
					- ServerWorld.TILE_SIZE, getWorld());
			((ServerEnemy) newbat).setSpawner(this);
			batCount++;

			getWorld().add(newbat);
		}
	}

	/**
	 * Subtract one from the bat count of this spawner
	 */
	public void removeBat()
	{
		batCount--;
	}

}
