package Server.Items;

import Server.ServerWorld;

public class ServerBuildingItem extends ServerItem{

	public static final int BASIC_BARRACKS_COST = 10;
	public static final int ADV_BARRACKS_COST = 20;
	public static final int GIANT_FACTORY_COST = 25;
	public static final int WOOD_HOUSE_COST = 5;
	public static final int INN_COST = 10;
	public static final int TOWER_COST = 7;
	public static final int GOLD_MINE_COST = 15;
	
	private String buildingType;
	
	public ServerBuildingItem(String type, ServerWorld world) {
		super(0,0, type, world);
		switch (type)
		{
		case ServerWorld.BASIC_BARRACKS_ITEM_TYPE:
			buildingType = ServerWorld.BASIC_BARRACKS_TYPE;
			break;
		case ServerWorld.ADV_BARRACKS_ITEM_TYPE:
			buildingType = ServerWorld.ADV_BARRACKS_TYPE;
			break;
		case ServerWorld.GIANT_FACTORY_ITEM_TYPE:
			buildingType = ServerWorld.GIANT_FACTORY_TYPE;
			break;
		case ServerWorld.WOOD_HOUSE_ITEM_TYPE:
			buildingType = ServerWorld.WOOD_HOUSE_TYPE;
			break;
		case ServerWorld.INN_ITEM_TYPE:
			buildingType = ServerWorld.INN_TYPE;
			break;
		case ServerWorld.TOWER_ITEM_TYPE:
			buildingType = ServerWorld.TOWER_TYPE;
			break;
		case ServerWorld.GOLD_MINE_ITEM_TYPE:
			buildingType = ServerWorld.GOLD_MINE_TYPE;
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
