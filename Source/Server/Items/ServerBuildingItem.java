package Server.Items;

import Server.ServerWorld;

public class ServerBuildingItem extends ServerItem{

	public static final int BARRACK_COST = 10;
	
	public ServerBuildingItem(String type, ServerWorld world) {
		super(0,0, type, world);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	

}
