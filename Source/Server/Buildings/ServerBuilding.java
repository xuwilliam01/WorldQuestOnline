package Server.Buildings;

import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;

public abstract class ServerBuilding extends ServerCreature {

	public final static int BARRACKS_HP = 1000;
	public final static int WOOD_HOUSE_HP = 1000;
	public final static int CASTLE_HP = 20000;
	
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
			return new ServerBarrack(x,y,type, team, world);
		case ServerWorld.WOOD_HOUSE_TYPE:
			return new ServerHouse(x,y,type, team, world);
		}
		return null;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
