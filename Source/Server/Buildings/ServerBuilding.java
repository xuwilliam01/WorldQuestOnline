package Server.Buildings;

import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;
import Server.Effects.ServerDemolition;
import Server.Effects.ServerSmoke;

public abstract class ServerBuilding extends ServerCreature {

	public final static int BARRACKS_HP = 700;
	public final static int ADV_BARRACKS_HP = 1200;
	public final static int GIANT_FACTORY_HP = 1500;
	public final static int WOOD_HOUSE_HP = 500;
	public final static int INN_HP = 700;
	public final static int TOWER_HP = 50;
	public final static int CASTLE_HP = 1;
	public final static int GOLD_MINE_HP = 1000;
	
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
			if(team == ServerCreature.RED_TEAM)
				setImage("RED_BARRACKS");
			else setImage("BLUE_BARRACKS");
			setMaxHP(BARRACKS_HP);
			setHP(BARRACKS_HP);
			break;
		case ServerWorld.ADV_BARRACKS_TYPE:
			if(team == ServerCreature.RED_TEAM)
				setImage("RED_ADV_BARRACKS");
			else setImage("BLUE_ADV_BARRACKS");
			setMaxHP(ADV_BARRACKS_HP);
			setHP(ADV_BARRACKS_HP);
			break;
		case ServerWorld.GIANT_FACTORY_TYPE:
			if(team == ServerCreature.RED_TEAM)
				setImage("RED_GIANT_FACTORY");
			else setImage("BLUE_GIANT_FACTORY");
			setMaxHP(GIANT_FACTORY_HP);
			setHP(GIANT_FACTORY_HP);
			break;
		case ServerWorld.WOOD_HOUSE_TYPE:
			if(team == ServerCreature.RED_TEAM)
				setImage("RED_WOOD_HOUSE");
			else setImage("BLUE_WOOD_HOUSE");
			setMaxHP(WOOD_HOUSE_HP);
			setHP(WOOD_HOUSE_HP);
			break;
		case ServerWorld.INN_TYPE:
			if(team == ServerCreature.RED_TEAM)
				setImage("RED_INN");
			else setImage("BLUE_INN");
			setMaxHP(INN_HP);
			setHP(INN_HP);
			break;
		case ServerWorld.TOWER_TYPE:
			if(team == ServerCreature.RED_TEAM)
				setImage("RED_TOWER");
			else setImage("BLUE_TOWER");
			setMaxHP(TOWER_HP);
			setHP(TOWER_HP);
			break;
		case ServerWorld.GOLD_MINE_TYPE:
			if(team == ServerCreature.RED_TEAM)
				setImage("RED_GOLD_MINE");
			else setImage("BLUE_GOLD_MINE");
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
		getWorld().add(new ServerDemolition(getX(),getY(),getWidth(),getHeight(),getType(),getWorld()));
	}

}
