package Server;

public abstract class ServerWeapon extends ServerItem{

	public final static int NUM_WEAPONS = 1;

	public ServerWeapon(double x, double y, String type) {
		super(x, y, type);
	}

	/**
	 * Creates a random weapon at specified location
	 * @param x
	 * @param y
	 * @param min the minimum grade of the weapon
	 * @param max the maximum grade of the weapon
	 * @return
	 */
	public static ServerWeapon randomWeapon(double x, double y, int min, int max)
	{
		//Choose a type between the max and min grade
		int randType = (int) (Math.random() * (max - min +1)) + min;

		switch (randType) {
		case 1:
			return new ServerSword(x, y,'L',20);
		}
		// This won't happen
		return null;
	}

}
