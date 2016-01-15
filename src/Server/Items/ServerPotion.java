package Server.Items;


public abstract class ServerPotion extends ServerItem {

	private final static int NUM_POTIONS = 3;

	public ServerPotion(double x, double y, String type) {
		super(x, y, type);
	}

	public static ServerPotion randomPotion(double x, double y) {
		int randType = (int) (Math.random() * NUM_POTIONS + 1);

		switch (randType) {
		case 1:
			return new ServerHPPotion(x, y);
		case 2:
			return new ServerMaxHPPotion(x,y);
		case 3:
			return new ServerManaPotion(x,y);
		}
		// This won't happen
		return null;
	}


}
