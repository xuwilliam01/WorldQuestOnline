package Server.Items;

import Server.ServerWorld;

public class ServerMaxHPPotion extends ServerPotion{

	public final static int MAX_HP_INCREASE = 50;
	
	public ServerMaxHPPotion(double x, double y) {
		super(x, y, ServerWorld.MAX_HP_TYPE);
	}
}
