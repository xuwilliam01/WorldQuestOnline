package Server.Buildings;

import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;

public abstract class ServerBuilding extends ServerCreature {

	public final static int BARRACKS_HP = 1000;
	
	public ServerBuilding(double x, double y, String type, ServerWorld world) {
		super(x, y, 0, 0, 0, 0, 1, "SERVERBUILDING", type, 0, world, true);
		switch (type)
		{
		case ServerWorld.BARRACK_TYPE:
			setImage("BARRACKS");
			setMaxHP(BARRACKS_HP);
			break;
		}
		
		setWidth(Images.getGameImage(getImage()).getWidth());
		setHeight(Images.getGameImage(getImage()).getHeight());
	}

	public static ServerBuilding getNewBuilding(int x, int y, String type, ServerWorld world)
	{
		switch(type)
		{
		//Different type of barrack exist. Will all be put in the same statement
		case ServerWorld.BARRACK_TYPE:
			return new ServerBarrack(x,y,type, world);
		}
		return null;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}