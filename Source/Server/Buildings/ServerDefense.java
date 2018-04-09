package Server.Buildings;

import java.util.ArrayList;

import Server.ServerObject;
import Server.ServerWorld;

public class ServerDefense extends ServerBuilding {

	public final static int TOWER_RANGE = 600;
	
	private ArrayList<ServerObject> arrowSources;
	
	public ServerDefense(double x, double y, String type, int team, ServerWorld world) {
		super(x, y, type, team, world);
		
		arrowSources = new ArrayList<ServerObject>();
		
		switch(type)
		{
		case ServerWorld.TOWER_TYPE:
			// if x and y are 0 then this is a dummy tower
			if (x > 1 || y > 1)
			{
				arrowSources.add(world.add(new ServerArrowSource(x + getWidth()/2, y + getHeight()/4, team, 20,
						ServerWorld.WOODARROW_TYPE, TOWER_RANGE, this, world)));
			}
			setName("An Arrow Tower");
			break;
		}
	}
	
	@Override
	public void setX(double x)
	{
		super.setX(x);
		for (ServerObject object:arrowSources)
		{
			object.setX(x + getWidth()/2);
		}
	}
	
	@Override
	public void setY(double y)
	{
		super.setY(y);
		for (ServerObject object:arrowSources)
		{
			object.setY(y + getHeight()/4);
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