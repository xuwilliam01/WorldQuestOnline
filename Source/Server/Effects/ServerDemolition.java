package Server.Effects;

import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerPlayer;

public class ServerDemolition extends ServerObject{

	private long counter;
	private ServerWorld world;
	int moveAmount;
	private String base = "INN";
	private int team = -5;
	
	public ServerDemolition(double x, double y, int width, int height, String building, ServerWorld world, int team) {
		super(x, y, width, height, 0, "INN_0", ServerWorld.DEMOLITION_TYPE, world.getEngine());
		
		
		switch(building)
		{
		case ServerWorld.INN_TYPE:
			base = "INN";
			break;
		case ServerWorld.TOWER_TYPE:
			base = "TOWER";
			break;
		case ServerWorld.BASIC_BARRACKS_TYPE:
			base = "BARRACKS";
			break;
		case ServerWorld.ADV_BARRACKS_TYPE:
			base = "ADV_BARRACKS";
			break;
		case ServerWorld.GIANT_FACTORY_TYPE:
			base = "GIANT_FACTORY";
			break;
		case ServerWorld.GOLD_MINE_TYPE:
			base = "GOLD_MINE";
			break;
		case ServerWorld.WOOD_HOUSE_TYPE:
			base = "WOOD_HOUSE";
			break;
		case ServerWorld.CASTLE_TYPE:
			this.team = team;
			if (team==ServerPlayer.RED_TEAM)
			{
				base = "RED_CASTLE";
			}
			else
			{
				base = "BLUE_CASTLE";
			}
			break;
		}
		
		setImage(base+"_0");
		
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
			setImage(base + "_1");
			setY(getY()+moveAmount);
			setHeight(getHeight()-moveAmount);
			break;
		case 4:
			setImage(base + "_2");
			setY(getY()+moveAmount);
			setHeight(getHeight()-moveAmount);
			break;
		case 6:
			setImage(base + "_3");
			setY(getY()+moveAmount);
			setHeight(getHeight()-moveAmount);
			break;
		case 8:
			setImage(base + "_4");
			setY(getY()+moveAmount);
			setHeight(getHeight()-moveAmount);
			world.add(new ServerSmoke(getX()+getWidth()/2 + Math.max(0, getWidth()-500),getY()+getHeight()-180,true,world));
			world.add(new ServerSmoke(getX()+getWidth()/2-320 - + Math.max(0, getWidth()-500),getY()+getHeight()-180,false,world));
			break;
		case 10:
			setImage(base + "_5");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 12:
			setImage(base + "_6");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 14:
			setImage(base + "_7");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 16:
			setImage(base + "_8");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 18:
			setImage(base + "_9");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 20:
			setImage(base + "_10");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 22:
			setImage(base + "_11");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 24:
			setImage(base + "_12");
			setY(getY()+moveAmount);setHeight(getHeight()-moveAmount);
			break;
		case 26:
			setImage(base + "_13");
			setY(getY()+moveAmount);
			break;
		case 28:
			setImage(base + "_14");
			setY(getY()+moveAmount);
			break;
		case 30:
			setImage(base + "_15");
			setY(getY()+moveAmount);
			break;
		case 32:
			setImage(base + "_16");
			setY(getY()+moveAmount);
			break;
		case 34:
			setImage(base + "_17");
			setY(getY()+moveAmount);
			break;
		case 36:
			setImage(base + "_18");
			setY(getY()+moveAmount);
			break;
		case 38:
			setImage(base + "_19");
			setY(getY()+moveAmount);
			break;
		case 40:
			if (team == -5)
			{
				destroy();
			}
			break;
		}
		
		if (team>-5 && world.getWorldCounter()-counter > 90)
		{
			world.getEngine().endGame(team);
			destroy();
		}
		
	}
	
	@Override
	public void destroy()
	{
		super.destroy();
	}

}
