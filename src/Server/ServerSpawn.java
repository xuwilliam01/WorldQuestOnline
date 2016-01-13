package Server;

import Server.Creatures.ServerCreature;

public class ServerSpawn extends ServerObject{

	private ServerCreature creature;
	private long delay;
	private ServerWorld world;
	
	public ServerSpawn(double x, double y, ServerCreature creatureType, int delay, ServerWorld world) {
		super(x, y, ServerWorld.TILE_SIZE, ServerWorld.TILE_SIZE, ServerWorld.GRAVITY, "NOTHING.png", ServerWorld.SPAWN_TYPE+"");
		this.creature = creatureType;
		this.delay = delay/ServerEngine.UPDATE_RATE;
		this.world = world;
		
		setSolid(false);
		switch(creature.getType())
		{
		
		}
	}
	
	public void update(long worldCounter)
	{
		if(worldCounter % delay == 0)
		{
			ServerCreature newCreature = ServerCreature.copy(creature);
			newCreature.setX(getX());
			newCreature.setY(getY());
			world.add(newCreature);
		}
	}

}
