package Server;

import Server.Creatures.ServerCreature;

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
