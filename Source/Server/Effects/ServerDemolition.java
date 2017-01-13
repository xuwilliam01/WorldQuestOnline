package Server.Effects;

import Server.ServerEngine;
import Server.ServerObject;
import Server.ServerWorld;

public class ServerDemolition extends ServerObject{

	private long counter;
	private ServerWorld world;
	
	public ServerDemolition(double x, double y, int width, int height, String building, ServerWorld world) {
		super(x, y, width, height, 0, "INN_0", ServerWorld.DEMOLITION_TYPE, world.getEngine());
		
		switch(building)
		{
		case ServerWorld.INN_TYPE:
			setImage("INN_0");
			break;
		}
		
		this.world = world;
		counter = world.getWorldCounter();
	}

	@Override
	public void update() {
		
		switch((int)(world.getWorldCounter()-counter))
		{
		case 4:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"1");
			break;
		case 8:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"2");
			break;
		case 12:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"3");
			break;
		case 16:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"4");
			break;
		case 20:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"5");
			break;
		case 24:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"6");
			break;
		case 28:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"7");
			break;
		case 32:
			destroy();
			break;
		}
		
	}
	
	@Override
	public void destroy()
	{
		super.destroy();
		world.add(new ServerSmoke(getX()+getWidth()+getWidth()/2-320,getY()+getHeight()-180,true,world));
		world.add(new ServerSmoke(getX()-getWidth()/2,getY()+getHeight()-180,false,world));
	}

}
