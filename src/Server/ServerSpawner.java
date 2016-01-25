package Server;

import Server.Creatures.ServerCreature;
import Server.Creatures.ServerPlayer;

/**
 * Spawns a given creature to the world
 * @author Alex Raita & William Xu
 *
 */
public class ServerSpawner extends ServerObject
{

	/**
	 * The creature to spawn
	 */
	private ServerCreature creature;
	
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
	public ServerSpawner(double x, double y, ServerCreature creatureType,
			ServerWorld world)
	{
		super(x, y, ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE,
				ServerWorld.GRAVITY, "NOTHING.png", ServerWorld.SPAWN_TYPE);
		this.creature = creatureType;
		this.world = world;
		makeExist();
		setSolid(false);

		// Since there are many types of goblins, they have special cases
		if (creature.getType().contains(ServerWorld.GOBLIN_TYPE))
		{
			if (creature.getTeam() == ServerPlayer.RED_TEAM)
				setImage("RED_GOBLIN_SPAWN.png");
			else
				setImage("BLUE_GOBLIN_SPAWN.png");
			delay = 2000;
		}
		else
			switch (creature.getType())
			{
			case ServerWorld.SLIME_TYPE:
				setImage("SLIME_SPAWN.png");
				delay = 2000;
				break;
			}
	}

	public void update(long worldCounter)
	{
		if (worldCounter % (delay) == 0)
		{
			if (!creature.getType().equals(ServerWorld.SLIME_TYPE)
					|| ServerWorld.slimeCount < ServerWorld.maxSlimes)
			{
				ServerObject newCreature = ServerObject.copy(creature);
				newCreature.setX(getX());
				newCreature.setY(getY() - getHeight() - ServerWorld.TILE_SIZE);
				world.add(newCreature);

				if (creature.getType().equals(ServerWorld.SLIME_TYPE))
				{
					ServerWorld.slimeCount++;
				}
			}
		}
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
