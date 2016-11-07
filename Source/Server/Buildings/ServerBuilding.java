package Server.Buildings;

import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;

public class ServerBuilding extends ServerCreature {

	public final static int BARRACKS_HP = 1000;
	
	public ServerBuilding(double x, double y, String type, ServerWorld world) {
		super(x, y, 0, 0, 0, 0, 1, "SERVERBUILDING", type, 0, world, true);
		switch (type)
		{
		case ServerWorld.BARRACK_TYPE:
			setImage("BARRACKS.png");
			setMaxHP(BARRACKS_HP);
			break;
		}
		
		setWidth(Images.getGameImage(getImage()).getWidth());
		setHeight(Images.getGameImage(getImage()).getHeight());
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
