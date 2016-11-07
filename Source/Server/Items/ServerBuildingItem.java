package Server.Items;

import Server.ServerWorld;

public class ServerBuildingItem extends ServerItem{

	public static final int BARRACK_COST = 10;
	
	private String buildingType;
	
	public ServerBuildingItem(String type, ServerWorld world) {
		super(0,0, type, world);
		switch (type)
		{
		case ServerWorld.BARRACK_ITEM_TYPE:
			buildingType = ServerWorld.BARRACK_TYPE;
			break;
		}
	}

	public String getBuildingType()
	{
		return buildingType;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	

}
