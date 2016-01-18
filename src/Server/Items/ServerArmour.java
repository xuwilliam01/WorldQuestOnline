package Server.Items;

import Server.ServerWorld;

/**
 * The actual item for an armour set
 * 
 * @author William
 *
 */
public class ServerArmour extends ServerItem {

	public static int NUM_ARMOURS = 4;

	public static double STEEL_DEF = 0.5;
	public static double RED_DEF = 0.3;
	public static double BLUE_DEF = 0.2;
	public static double GREY_DEF = 0.1;
	
	/**
	 * The percentage of damage this armour absorbs
	 */
	private double armour;

	/**
	 * The image to use when the player actually uses the weapon
	 */
	private String armourImage;

	public ServerArmour(double x, double y, String type) {
		super(x, y, type);

		switch (type) {
		case ServerWorld.STEEL_ARMOUR:
			armour = STEEL_DEF;
			armourImage = "OUTFITARMOR";
			break;
		case ServerWorld.GREY_NINJA_ARMOUR:
			armour = GREY_DEF;
			armourImage = "OUTFITNINJAGREY";
			break;
		case ServerWorld.BLUE_NINJA_ARMOUR:
			armour = BLUE_DEF;
			armourImage = "OUTFITNINJABLUE";
			break;
		case ServerWorld.RED_NINJA_ARMOUR:
			armour = RED_DEF;
			armourImage = "OUTFITNINJARED";
			break;
		}
	}

	public static ServerArmour randomArmour(double x, double y) {
		int randType = (int) (Math.random() * 10) + 1;
		
		if(randType <= 1)
			return new ServerArmour(x,y,ServerWorld.STEEL_ARMOUR);
		if(randType <= 3)
			return new ServerArmour(x,y,ServerWorld.RED_NINJA_ARMOUR);
		if(randType <= 6)
			return new ServerArmour(x,y,ServerWorld.BLUE_NINJA_ARMOUR);
		if(randType <= 10)
			return new ServerArmour(x,y,ServerWorld.GREY_NINJA_ARMOUR);
		return null;
	}

	public double getArmour() {
		return armour;
	}

	public void setArmour(double armour) {
		this.armour = armour;
	}

	public String getArmourImage() {
		return armourImage;
	}

	public void setArmourImage(String armourImage) {
		this.armourImage = armourImage;
	}

}
