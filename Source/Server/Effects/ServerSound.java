package Server.Effects;

import java.util.ArrayList;

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
		
		int startRow = Math.max((int) (getY() / ServerWorld.OBJECT_TILE_SIZE)-5, 0);
		int endRow = Math.min((int) (getY() / ServerWorld.OBJECT_TILE_SIZE)+5, world.getObjectGrid().length-1);
		int startColumn = Math.max((int) (getX() / ServerWorld.OBJECT_TILE_SIZE)-5, 0);
		int endColumn = Math.min((int) (getX() / ServerWorld.OBJECT_TILE_SIZE)+5, world.getObjectGrid()[0].length-1);
		
		for (int row = startRow; row <= endRow; row++)
		{
			for (int column = startColumn; column <= endColumn; column++)
			{
				for (ServerObject otherObject : world.getObjectGrid()[row][column])
				{
					if (otherObject.getType().charAt(0)==ServerWorld.SOUND_TYPE)
					{
						destroy();
						break;
					}
				}
			}
			
		}
		
	}

	public void update() {
		if (world.getWorldCounter()-startCounter>= ALIVE_TIME)
		{
			destroy();
		}
		
	}
}
