package Server.Spawners;

import Server.ServerWorld;
import Server.Creatures.ServerEnemy;
import Server.Creatures.ServerSlime;

/**
 * Spawns a given creature to the world
 * @author Alex Raita & William Xu
 *
 */
public class ServerSlimeSpawner extends ServerSpawner
{
	/**
	 * Number of slimes spawned by this spawner
	 */
	private int slimeCount = 0;

	/**
	 * Max number of slimes per slime spawner
	 */
	public final static int maxSlimes = 7;

	/**
	 * Constructor
	 * @param x the x-coordinate of the spawner
	 * @param y the y-coordinate of the spawner
	 * @param creatureType the type of creature that will be spawned
	 * @param world the world the creature will be added in
	 */
	public ServerSlimeSpawner(double x, double y, ServerWorld world)
	{
		super(x, y, world, ServerWorld.SLIME_SPAWN_TYPE);
		setImage("SLIME_SPAWN");
		setDelay(60);
	}

	/**
	 * Update the spawner
	 * @param worldCounter
	 */
	@Override
	public void update()
	{
		long worldCounter = getWorld().getWorldCounter();
		if (worldCounter % getDelay() == 0 && slimeCount < maxSlimes)
		{

			ServerSlime newSlime = new ServerSlime(getX(), getY()
					- getHeight()
					- ServerWorld.TILE_SIZE, getWorld());
			((ServerEnemy) newSlime).setSpawner(this);
			slimeCount++;

			getWorld().add(newSlime);

		}
	}

	/**
	 * Subtract one from the slime count of this spawner
	 */
	public void removeSlime()
	{
		slimeCount--;
	}

}
