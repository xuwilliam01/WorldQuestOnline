package Server;

import Server.Creatures.ServerCreature;
import Server.Creatures.ServerPlayer;

public class ServerSpawner extends ServerObject{

	private ServerCreature creature;
	private int delay;
	private ServerWorld world;
	private int maxSpawn;

	public ServerSpawner(double x, double y, ServerCreature creatureType, ServerWorld world) {
		super(x, y, ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE, ServerWorld.GRAVITY, "NOTHING.png", ServerWorld.SPAWN_TYPE);
		this.creature = creatureType;
		this.world = world;

		makeExist();
		setSolid(false);

		//Since there are many types of goblins, they have special cases
		if(creature.getType().contains(ServerWorld.GOBLIN_TYPE))
		{
			if(creature.getTeam() == ServerPlayer.RED_TEAM)
				setImage("RED_GOBLIN_SPAWN.png");
			else
				setImage("BLUE_GOBLIN_SPAWN.png");
			maxSpawn = 1000;
			delay = 10000;
		}
		else
			switch(creature.getType())
			{
			case ServerWorld.SLIME_TYPE:
				setImage("SLIME_SPAWN.png");
				maxSpawn = 20;
				delay = 10000;
				break;
			}
	}

	public void update(long worldCounter)
	{
		if(worldCounter % (delay/ServerEngine.UPDATE_RATE) == 0 && maxSpawn > 0)
		{
			ServerObject newCreature = ServerObject.copy(creature);
			newCreature.setX(getX());
			newCreature.setY(getY()-getHeight()-ServerWorld.TILE_SIZE);
			world.add(newCreature);
			maxSpawn--;
			if(maxSpawn <= 0)
				destroy();
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
