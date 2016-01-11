package Server.Items;

import Server.ServerWorld;

/**
 * The actual item for an armour set
 * @author William
 *
 */
public class ServerArmour extends ServerItem
{

	/**
	 * The percentage of damage this armour absorbs
	 */
	private double armour;

	/**
	 * The image to use when the player actually uses the weapon
	 */
	private String armourImage;

	public ServerArmour(double x, double y, String type)
	{
		super(x, y, type);

		switch (type)
		{
		case ServerWorld.STEEL_ARMOUR:
			armour = 0.5;
			armourImage = "OUTFITARMOR_0_0.png";
			break;
		case ServerWorld.GREY_NINJA_ARMOUR:
			armour = 0.1;
			armourImage = "OUTFITNINJAGREY_0_0.png";
			break;
		case ServerWorld.BLUE_NINJA_ARMOUR:
			armour = 0.2;
			armourImage = "OUTFITNINJABLUE_0_0.png";
			break;
		case ServerWorld.RED_NINJA_ARMOUR:
			armour = 0.3;
			armourImage = "OUTFITNINJARED_0_0.png";
			break;
		}
	}

	public double getArmour()
	{
		return armour;
	}

	public void setArmour(double armour)
	{
		this.armour = armour;
	}

	public String getArmourImage()
	{
		return armourImage;
	}

	public void setArmourImage(String armourImage)
	{
		this.armourImage = armourImage;
	}

	

	
}
