package Server.Effects;

import Server.ServerEngine;
import Server.ServerObject;
import Server.ServerWorld;

public class ServerSmoke extends ServerObject{

	private String direction = "RIGHT";
	private long counter;
	private ServerWorld world;
	
	public ServerSmoke(double x, double y, boolean isRight, ServerWorld world) {
		super(x, y, 0, 0, 0, "SMOKE_RIGHT_0", ServerWorld.SMOKE_TYPE, world.getEngine());
		
		if (!isRight)
		{
			direction = "LEFT";
		}
		
		if (isRight)
		{
			setImage("SMOKE_LEFT_0");
		}
		this.world = world;
		counter = world.getWorldCounter();
	}

	@Override
	public void update() {
		
		switch((int)(world.getWorldCounter()-counter))
		{
		case 10:
			setImage("SMOKE_"+direction+"_1");
			break;
		case 20:
			setImage("SMOKE_"+direction+"_2");
			break;
		case 30:
			setImage("SMOKE_"+direction+"_3");
			break;
		case 40:
			setImage("SMOKE_"+direction+"_4");
			break;
		case 50:
			setImage("SMOKE_"+direction+"_5");
			break;
		case 60:
			destroy();
			break;
		}
		
	}

}
