package Server.Buildings;

import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;
import Server.Effects.ServerSmoke;

public abstract class ServerBuilding extends ServerCreature {

	public final static int BARRACKS_HP = 1000;
	public final static int ADV_BARRACKS_HP = 2000;
	public final static int GIANT_FACTORY_HP = 3000;
	public final static int WOOD_HOUSE_HP = 500;
	public final static int INN_HP = 500;
	public final static int TOWER_HP = 700;
	public final static int CASTLE_HP = 1000;
	public final static int GOLD_MINE_HP = 600;
	
	public ServerBuilding(double x, double y, String type, int team, ServerWorld world) {
		super(x, y, 0, 0, 0, 0, 0, "SERVERBUILDING", type, 0, world, true);
		switch (type)
		{
		case ServerWorld.CASTLE_TYPE:
			if (team == ServerCreature.RED_TEAM) {
				setImage("RED_CASTLE");
			}
			else
			{
				setImage("BLUE_CASTLE");
			}
			setMaxHP(CASTLE_HP);
			setHP(CASTLE_HP);
			break;
		case ServerWorld.BASIC_BARRACKS_TYPE:
			setImage("BARRACKS");
			setMaxHP(BARRACKS_HP);
			setHP(BARRACKS_HP);
			break;
		case ServerWorld.ADV_BARRACKS_TYPE:
			setImage("ADV_BARRACKS");
			setMaxHP(ADV_BARRACKS_HP);
			setHP(ADV_BARRACKS_HP);
			break;
		case ServerWorld.GIANT_FACTORY_TYPE:
			setImage("GIANT_FACTORY");
			setMaxHP(GIANT_FACTORY_HP);
			setHP(GIANT_FACTORY_HP);
			break;
		case ServerWorld.WOOD_HOUSE_TYPE:
			setImage("WOOD_HOUSE");
			setMaxHP(WOOD_HOUSE_HP);
			setHP(WOOD_HOUSE_HP);
			break;
		case ServerWorld.INN_TYPE:
			setImage("INN");
			setMaxHP(INN_HP);
			setHP(INN_HP);
			break;
		case ServerWorld.TOWER_TYPE:
			setImage("TOWER");
			setMaxHP(TOWER_HP);
			setHP(TOWER_HP);
			break;
		case ServerWorld.GOLD_MINE_TYPE:
			setImage("GOLD_MINE");
			setMaxHP(GOLD_MINE_HP);
			setHP(GOLD_MINE_HP);
			break;
		}
		setTeam(team);
		setWidth(Images.getGameImage(getImage()).getWidth());
		setHeight(Images.getGameImage(getImage()).getHeight());
	}

	public static ServerBuilding getNewBuilding(double x, double y, String type, int team, ServerWorld world)
	{
		switch(type)
		{
		//Different type of barrack exist. Will all be put in the same statement
		case ServerWorld.BASIC_BARRACKS_TYPE:
		case ServerWorld.ADV_BARRACKS_TYPE:
		case ServerWorld.GIANT_FACTORY_TYPE:
			return new ServerBarracks(x,y,type, team, world);
		case ServerWorld.WOOD_HOUSE_TYPE:
		case ServerWorld.INN_TYPE:
			return new ServerHouse(x,y,type, team, world);
		case ServerWorld.TOWER_TYPE:
			return new ServerDefense(x,y,type, team, world);
		case ServerWorld.GOLD_MINE_TYPE:
			return new ServerMine(x,y,type, team, world);
		}
		return null;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void destroy()
	{
		super.destroy();
		getWorld().add(new ServerSmoke(getX(),getY()+getHeight()-180,true,getWorld()));
		getWorld().add(new ServerSmoke(getX()+getWidth()-320,getY()+getHeight()-180,false,getWorld()));
	}

}
