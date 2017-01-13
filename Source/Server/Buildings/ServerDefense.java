package Server.Buildings;

import java.util.ArrayList;

import Server.ServerObject;
import Server.ServerWorld;

public class ServerDefense extends ServerBuilding {

	public final static int TOWER_RANGE = 1000;
	
	private ArrayList<ServerObject> arrowSources;
	
	
	public ServerDefense(double x, double y, String type, int team, ServerWorld world) {
		super(x, y, type, team, world);
		arrowSources = new ArrayList<ServerObject>();
		
		switch(type)
		{
		case ServerWorld.TOWER_TYPE:
			arrowSources.add(world.add(new ServerArrowSource(x + getWidth()/2, y + getHeight()/4, team, 25,
					ServerWorld.STEELARROW_TYPE, TOWER_RANGE, this, world)));
			break;
		}
	}
	
	@Override
	public void destroy()
	{
		super.destroy();
		for (ServerObject object:arrowSources)
		{
			object.destroy();
		}
		arrowSources.clear();
	}
	
	
	
	}


