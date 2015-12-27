package Server;

public class ServerSword extends ServerWeapon{

	private int damage;
	
	public ServerSword(double x, double y, int damage) {
		super(x, y, "IWS");
		this.damage = damage;
	}

}
