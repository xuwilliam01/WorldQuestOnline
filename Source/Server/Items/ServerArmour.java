package Server.Items;

import Server.ServerWorld;

/**
 * The actual item for an armour set
 * 
 * @author William and Alex
 *
 */
public class ServerArmour extends ServerItem {

	// The number of existing armour sets
	public final static int NUM_ARMOURS = 4;
	
	// The armour settings for each type of armour
	public final static double STEEL_DEF = 0.5;
	public final static double RED_DEF = 0.3;
	public final static double BLUE_DEF = 0.2;
	public final static double GREY_DEF = 0.1;
	
	/**
	 * The percentage of damage this armour absorbs
	 */
	private double armour;

	/**
	 * The image to use when the player actually uses the weapon
	 */
	private String armourImage;

	/**
	 * Constructor for an armour
	 * @param x the x position
	 * @param y the y position
	 * @param type the type of armour
	 */
	public ServerArmour(double x, double y, String type, ServerWorld world) {
		super(x, y, type,world);

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

	/**
	 * Return a random armour type
	 * @param x the x position of the armour
	 * @param y the y position of the armour
	 * @return the new armour
	 */
	public static ServerArmour randomArmour(double x, double y, ServerWorld world) {
		int randType = (int) (Math.random() * 10) + 1;
		
		if(randType <= 1)
			return new ServerArmour(x,y,ServerWorld.STEEL_ARMOUR,world);
		if(randType <= 3)
			return new ServerArmour(x,y,ServerWorld.RED_NINJA_ARMOUR,world);
		if(randType <= 6)
			return new ServerArmour(x,y,ServerWorld.BLUE_NINJA_ARMOUR,world);
		if(randType <= 10)
			return new ServerArmour(x,y,ServerWorld.GREY_NINJA_ARMOUR,world);
		return null;
	}

	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////
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

	@Override
	public void update()
	{
		// TODO Auto-generated method stub
		
	}


}
