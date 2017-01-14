package Server.Effects;

import Server.ServerObject;
import Server.ServerWorld;

public class ServerDemolition extends ServerObject{

	private long counter;
	private ServerWorld world;
	int moveAmount;
	
	public ServerDemolition(double x, double y, int width, int height, String building, ServerWorld world) {
		super(x, y, width, height, 0, "INN_0", ServerWorld.DEMOLITION_TYPE, world.getEngine());
		
		switch(building)
		{
		case ServerWorld.INN_TYPE:
			setImage("INN_0");
			break;
		case ServerWorld.TOWER_TYPE:
			setImage("TOWER_0");
			break;
		case ServerWorld.BASIC_BARRACKS_TYPE:
			setImage("BARRACKS_0");
			break;
		case ServerWorld.ADV_BARRACKS_TYPE:
			setImage("ADV_BARRACKS_0");
			break;
		case ServerWorld.GIANT_FACTORY_TYPE:
			setImage("GIANT_FACTORY_0");
			break;
		case ServerWorld.GOLD_MINE_TYPE:
			setImage("GOLD_MINE_0");
			break;
		case ServerWorld.WOOD_HOUSE_TYPE:
			setImage("WOOD_HOUSE_0");
			break;
		}
		
		this.world = world;
		counter = world.getWorldCounter();
		
		moveAmount = height/20;
		setY(getY()+moveAmount);
		setHeight(getHeight()-moveAmount);
	}
	
	@Override
	public void update() {
		
		switch((int)(world.getWorldCounter()-counter))
		{
		case 2:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"1");
			setY(getY()+moveAmount);
			setHeight(getHeight()-moveAmount);
			break;
		case 4:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"2");
			setY(getY()+moveAmount);
			setHeight(getHeight()-moveAmount);
			break;
		case 6:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"3");
			setY(getY()+moveAmount);
			setHeight(getHeight()-moveAmount);
			break;
		case 8:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"4");
			setY(getY()+moveAmount);
			setHeight(getHeight()-moveAmount);
			world.add(new ServerSmoke(getX()+getWidth()+getWidth()/2-320,getY()+getHeight()-180,true,world));
			world.add(new ServerSmoke(getX()-getWidth()/2,getY()+getHeight()-180,false,world));
			break;
		case 10:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"5");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 12:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"6");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 14:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"7");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 16:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"8");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 18:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"9");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 20:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"10");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 22:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"11");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 24:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"12");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 26:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"13");
			setY(getY()+moveAmount);
			break;
		case 28:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"14");
			setY(getY()+moveAmount);
			break;
		case 30:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"15");
			setY(getY()+moveAmount);
			break;
		case 32:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"16");
			setY(getY()+moveAmount);
			break;
		case 34:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"17");
			setY(getY()+moveAmount);
			break;
		case 36:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"18");
			setY(getY()+moveAmount);
			break;
		case 38:
			setImage(getImage().substring(0,getImage().indexOf('_')+1)+"19");
			setY(getY()+moveAmount);
			break;
		case 40:
			destroy();
			break;
		}
		
	}
	
	@Override
	public void destroy()
	{
		super.destroy();
	}

}
