package Server.Effects;

import Server.ServerEngine;
import Server.ServerObject;
import Server.ServerWorld;

public class ServerSound extends ServerObject{

	private final int ALIVE_TIME = 2;
	private long startCounter = 0;
	private ServerWorld world;
	
	public ServerSound(double x, double y, String name, ServerEngine engine) {
		super(x, y, 0,0,0, name, ServerWorld.SOUND_TYPE+"", engine);
		this.startCounter = engine.getWorld().getWorldCounter();
		world = engine.getWorld();
	}

	public void update() {
		if (world.getWorldCounter()-startCounter>= ALIVE_TIME)
		{
			destroy();
		}
		
	}
}
