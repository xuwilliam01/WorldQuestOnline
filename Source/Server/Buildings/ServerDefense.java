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
			setName("An Arrow Tower");
			break;
		}
	}
	
	@Override
	public void setX(double x)
	{
		super.setX(x);
		if (arrowSources.size() == 0 && getX() > 0 && getY() > 0)
		{
			arrowSources.add(getWorld().add(new ServerArrowSource(getX() + getWidth()/2, getY() + getHeight()/4, getTeam(), 20,
					ServerWorld.WOODARROW_TYPE, TOWER_RANGE, this, getWorld())));
		}
		for (ServerObject object:arrowSources)
		{
			object.setX(x + getWidth()/2);
		}
	}
	
	@Override
	public void setY(double y)
	{
		super.setY(y);
		if (arrowSources.size() == 0 && getX() > 0 && getY() > 0)
		{
			arrowSources.add(getWorld().add(new ServerArrowSource(getX() + getWidth()/2, getY() + getHeight()/4, getTeam(), 20,
					ServerWorld.WOODARROW_TYPE, TOWER_RANGE, this, getWorld())));
		}
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