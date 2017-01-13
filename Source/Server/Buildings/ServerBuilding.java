package Server.Buildings;

import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;
import Server.Effects.ServerSmoke;

public abstract class ServerBuilding extends ServerCreature {

	public final static int BARRACKS_HP = 1000;
	public final static int WOOD_HOUSE_HP = 500;
	public final static int TOWER_HP = 700;
	public final static int CASTLE_HP = 1;
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
		case ServerWorld.BARRACK_TYPE:
			setImage("BARRACKS");
			setMaxHP(BARRACKS_HP);
			setHP(BARRACKS_HP);
			break;
		case ServerWorld.WOOD_HOUSE_TYPE:
			setImage("WOOD_HOUSE");
			setMaxHP(WOOD_HOUSE_HP);
			setHP(WOOD_HOUSE_HP);
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
		case ServerWorld.BARRACK_TYPE:
			return new ServerBarracks(x,y,type, team, world);
		case ServerWorld.WOOD_HOUSE_TYPE:
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
		getWorld().add(new ServerSmoke(getX()+getWidth()/3,getY(),true,getWorld()));
		getWorld().add(new ServerSmoke(getX()+2*getWidth()/3-320,getY(),false,getWorld()));
	}

}
