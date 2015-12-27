package Server;

public class ServerSword extends ServerWeapon{

	public ServerSword(double x, double y, char type, int damage) {
		super(x, y, ServerWorld.SWORD_TYPE+type);
	}

}
