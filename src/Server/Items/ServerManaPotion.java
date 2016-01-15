package Server.Items;

import Server.ServerWorld;

public class ServerManaPotion extends ServerPotion {

	public final static int MANA_AMOUNT = 50;
	
	public ServerManaPotion(double x, double y) {
		super(x, y, ServerWorld.MANA_POTION_TYPE);
		
	}

}
