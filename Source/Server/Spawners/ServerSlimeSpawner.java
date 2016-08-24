package Server.Spawners;

import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerEnemy;
import Server.Creatures.ServerPlayer;

/**
 * Spawns a given creature to the world
 * @author Alex Raita & William Xu
 *
 */
public class ServerSlimeSpawner extends ServerObject
{

	/**
	 * The creature to spawn
	 */
	private ServerCreature slimeType;
	
	
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
	public ServerSlimeSpawner(double x, double y, ServerCreature creatureType,
			ServerWorld world)
	{
		super(x, y, ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE,
				ServerWorld.GRAVITY, "NOTHING", ServerWorld.SPAWN_TYPE);
		this.creature = creatureType;
		this.world = world;
		makeExist();
		setSolid(false);

		// Since there are many types of goblins, they have special cases
		if (creature.getType().contains(ServerWorld.GOBLIN_TYPE))
		{
			if (creature.getTeam() == ServerPlayer.RED_TEAM)
				setImage("RED_GOBLIN_SPAWN");
			else
				setImage("BLUE_GOBLIN_SPAWN");
			delay = 2000;
		}
		else
			switch (creature.getType())
			{
			case ServerWorld.SLIME_TYPE:
				setImage("SLIME_SPAWN");
				delay = 2000;
				break;
			}
	}

	public void update(long worldCounter)
	{
		if (worldCounter % (delay) == 0)
		{
			if (!creature.getType().equals(ServerWorld.SLIME_TYPE)
					|| slimeCount < maxSlimes)
			{
				ServerCreature newCreature = (ServerCreature) ServerObject.copy(creature);
				newCreature.setX(getX());
				newCreature.setY(getY() - getHeight() - ServerWorld.TILE_SIZE);
				world.add(newCreature);
				

				if (creature.getType().equals(ServerWorld.SLIME_TYPE))
				{
					((ServerEnemy)newCreature).setSpawner(this);
					slimeCount++;
				}
			}
		}
	}

	/**
	 * Subtract one from the slime count of this spawner
	 */
	public void removeSlime()
	{
		slimeCount--;
	}
	
	public int getDelay()
	{
		return delay;
	}

	public ServerCreature getCreature()
	{
		return creature;
	}

	public ServerWorld getWorld()
	{
		return world;
	}
	
}
