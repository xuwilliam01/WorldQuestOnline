package Server.Items;

import Server.ServerWorld;

public class ServerSword extends ServerWeapon{

	public ServerSword(double x, double y, char type, int damage) {
		super(x, y, ServerWorld.SWORD_TYPE+type);
	}

}
