package Server.Buildings;

import java.util.LinkedList;

import Server.ServerWorld;
import Server.Creatures.ServerGoblin;
import Server.Creatures.ServerPlayer;

public class ServerBarracks extends ServerBuilding {

	private LinkedList<Integer> goblins;
	private ServerCastle castle = null;
	
	public ServerBarracks(double x, double y, String type, int team, ServerWorld world) {
		super(x, y, type, team, world);
		goblins = new LinkedList<Integer>();
		switch (type)
		{
		case ServerWorld.BASIC_BARRACKS_TYPE:
			goblins.add(ServerGoblin.GOBLIN_ARCHER_NO);
			goblins.add(ServerGoblin.GOBLIN_SOLDIER_NO);
			goblins.add(ServerGoblin.GOBLIN_SOLDIER_NO);
			break;
		case ServerWorld.ADV_BARRACKS_TYPE:
			goblins.add(ServerGoblin.GOBLIN_KNIGHT_NO);
			goblins.add(ServerGoblin.GOBLIN_KNIGHT_NO);
			goblins.add(ServerGoblin.GOBLIN_WIZARD_NO);
			break;
		case ServerWorld.GIANT_FACTORY_TYPE:
			goblins.add(ServerGoblin.GOBLIN_GIANT_NO);
			break;
		}
	}

	@Override
	public void update()
	{
		super.update();
		if (castle==null)
		{
			if (getTeam() == ServerPlayer.BLUE_TEAM)
			{
				castle = getWorld().getBlueCastle();
			}
			else
			{
				castle = getWorld().getRedCastle();
			}
			castle.addBarracks(this);
		}	
	}
	
	@Override
	public void destroy()
	{
		super.destroy();
		castle.removeBarracks(this);
	}

	public LinkedList<Integer> getGoblins()
	{
		return goblins;
	}
	
}
