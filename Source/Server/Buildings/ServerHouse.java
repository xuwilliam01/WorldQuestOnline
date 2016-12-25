package Server.Buildings;

import Server.ServerWorld;

public class ServerHouse extends ServerBuilding {

	private int population;
	public static final int WOOD_HOUSE_POP = 10;
	private ServerCastle castle = null;

	public ServerHouse(double x, double y, String type, int team, ServerWorld world) {
		super(x, y, type, team, world);

		switch (type) {
		case ServerWorld.WOOD_HOUSE_TYPE:
			population = WOOD_HOUSE_POP;
			break;
		}
		
		if (team==ServerCastle.BLUE_TEAM)
		{
			castle = world.getBlueCastle();;
		}
		else
		{
			castle = world.getRedCastle();
		}
		
		castle.increasePopLimit(population);
	}
	
	@Override
	public void destroy()
	{
		super.destroy();
		castle.decreasePopLimit(population);
	}

}
