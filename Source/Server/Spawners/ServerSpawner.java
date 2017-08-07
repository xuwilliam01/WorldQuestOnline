package Server.Spawners;

import Server.ServerObject;
import Server.ServerWorld;

/**
 * Spawns a given creature to the world
 * @author Alex Raita & William Xu
 *
 */
public abstract class ServerSpawner extends ServerObject
{

	/**
	 * The delay between spawning creatures
	 */
	private int delay;
	
	/**
	 * A reference to the main world
	 */
	private ServerWorld world;
	

	/**
	 * Constructor
	 * @param x the x-coordinate of the spawner
	 * @param y the y-coordinate of the spawner
	 * @param creatureType the type of creature that will be spawned
	 * @param world the world the creature will be added in
	 */
	public ServerSpawner(double x, double y, ServerWorld world, String type)
	{
		super(x, y, ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE,
				0, type,world.getEngine());
		this.world = world;
		makeExist();
		setSolid(false);
		setMapVisible(false);
	}

	
	public int getDelay()
	{
		return delay;
	}


	public void setDelay(int delay)
	{
		this.delay = delay;
	}

	public ServerWorld getWorld()
	{
		return world;
	}

	
}
