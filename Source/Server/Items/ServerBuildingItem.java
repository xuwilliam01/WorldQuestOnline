package Server.Items;

import Server.ServerWorld;

public class ServerBuildingItem extends ServerItem{

	public static final int BARRACK_COST = 10;
	public static final int WOOD_HOUSE_COST = 5;
	
	private String buildingType;
	
	public ServerBuildingItem(String type, ServerWorld world) {
		super(0,0, type, world);
		switch (type)
		{
		case ServerWorld.BARRACK_ITEM_TYPE:
			buildingType = ServerWorld.BARRACK_TYPE;
			break;
		case ServerWorld.WOOD_HOUSE_ITEM_TYPE:
			buildingType = ServerWorld.WOOD_HOUSE_TYPE;
			break;
		}
	}

	public String getBuildingType()
	{
		return buildingType;
	}

	@Override
	public void update() {
		
	}
	

}
