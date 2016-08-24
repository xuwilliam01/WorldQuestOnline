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
public class ServerGoblinSpawner extends ServerSpawner
{
	
	/**
	 * The delay between spawning creatures
	 */
	private int delay;
	
	/**
	 * Team of the spawner
	 */
	private int team;


	/**
	 * Constructor
	 * @param x the x-coordinate of the spawner
	 * @param y the y-coordinate of the spawner
	 * @param creatureType the type of creature that will be spawned
	 * @param world the world the creature will be added in
	 */
	public ServerGoblinSpawner(double x, double y, 
			ServerWorld world, String type, int team)
	{
		super(x, y, world,type);
		this.team = team;

			if (team == ServerPlayer.RED_TEAM)
			{
				setImage("RED_GOBLIN_SPAWN");
			}
			else
			{
				setImage("BLUE_GOBLIN_SPAWN");
			}
			delay = 2000;
		
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
