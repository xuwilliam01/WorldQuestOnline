package Server.Items;

import Server.ServerWorld;

public class ServerWeapon extends ServerItem
{

	public final static int NUM_WEAPONS = 20;
	public final static int NUM_TIERS = 5;

	public final static int DADIAMOND_DMG = 25;
	public final static int DAGOLD_DMG = 20;
	public final static int DAIRON_DMG = 15;
	public final static int DASTONE_DMG = 10;
	public final static int DAWOOD_DMG = 5;
	
	public final static int AXDIAMOND_DMG = 30;
	public final static int AXGOLD_DMG = 25;
	public final static int AXIRON_DMG = 20;
	public final static int AXSTONE_DMG = 15;
	public final static int AXWOOD_DMG = 10;
	
	public final static int SWDIAMOND_DMG = 35;
	public final static int SWGOLD_DMG = 30;
	public final static int SWIRON_DMG = 25;
	public final static int SWSTONE_DMG = 20;
	public final static int SWWOOD_DMG = 15;
	
	public final static int HADIAMOND_DMG = 40;
	public final static int HAGOLD_DMG = 35;
	public final static int HAIRON_DMG = 30;
	public final static int HASTONE_DMG = 25;
	public final static int HAWOOD_DMG =20;
	
	/**
	 * The damage the weapon will do when you use it
	 */
	private int damage = -5;

	/**
	 * The image to use when the player actually uses the weapon
	 */
	private String actionImage;
	
	/**
	 * Number of counters for the swing
	 */
	private int swingSpeed;
	
	public final static int DAGGER_SPEED = 13;
	
	public final static int AX_SPEED = 26;
	
	public final static int SWORD_SPEED = 13;
	
	public final static int HALBERD_SPEED = 26;

	public ServerWeapon(double x, double y, String type)
	{
		super(x, y, type);

		switch (type)
		{
		case ServerWorld.DAGGER_TYPE + ServerWorld.DIAMOND_TIER:
			damage = DADIAMOND_DMG;
			actionImage = "DADIAMOND_0.png";
			swingSpeed = DAGGER_SPEED;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.GOLD_TIER:
			damage = DAGOLD_DMG;
			actionImage = "DAGOLD_0.png";
			swingSpeed = DAGGER_SPEED;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.IRON_TIER:
			damage = DAIRON_DMG;
			actionImage = "DAIRON_0.png";
			swingSpeed = DAGGER_SPEED;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.STONE_TIER:
			damage = DASTONE_DMG;
			actionImage = "DASTONE_0.png";
			swingSpeed = DAGGER_SPEED;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.WOOD_TIER:
			damage = DAWOOD_DMG;
			actionImage = "DAWOOD_0.png";
			swingSpeed = DAGGER_SPEED;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.DIAMOND_TIER:
			damage = AXDIAMOND_DMG;
			actionImage = "AXDIAMOND_0.png";
			swingSpeed = AX_SPEED;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.GOLD_TIER:
			damage = AXGOLD_DMG;
			actionImage = "AXGOLD_0.png";
			swingSpeed = AX_SPEED;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.IRON_TIER:
			damage = AXIRON_DMG;
			actionImage = "AXIRON_0.png";
			swingSpeed = AX_SPEED;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.STONE_TIER:
			damage = AXSTONE_DMG;
			actionImage = "AXSTONE_0.png";
			swingSpeed = AX_SPEED;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.WOOD_TIER:
			damage = AXWOOD_DMG;
			actionImage = "AXWOOD_0.png";
			swingSpeed = AX_SPEED;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.DIAMOND_TIER:
			damage = SWDIAMOND_DMG;
			actionImage = "SWDIAMOND_0.png";
			swingSpeed = SWORD_SPEED;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.GOLD_TIER:
			damage = SWGOLD_DMG;
			actionImage = "SWGOLD_0.png";
			swingSpeed = SWORD_SPEED;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.IRON_TIER:
			damage = SWIRON_DMG;
			actionImage = "SWIRON_0.png";
			swingSpeed = SWORD_SPEED;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.STONE_TIER:
			damage = SWSTONE_DMG;
			actionImage = "SWSTONE_0.png";
			swingSpeed = SWORD_SPEED;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.WOOD_TIER:
			damage = SWWOOD_DMG;
			actionImage = "SWWOOD_0.png";
			swingSpeed = SWORD_SPEED;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.DIAMOND_TIER:
			damage = HADIAMOND_DMG;
			actionImage = "HADIAMOND_0.png";
			swingSpeed = HALBERD_SPEED;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.GOLD_TIER:
			damage = HAGOLD_DMG;
			actionImage = "HAGOLD_0.png";
			swingSpeed = HALBERD_SPEED;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.IRON_TIER:
			damage = HAIRON_DMG;
			actionImage = "HAIRON_0.png";
			swingSpeed = HALBERD_SPEED;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.STONE_TIER:
			damage = HASTONE_DMG;
			actionImage = "HASTONE_0.png";
			swingSpeed = HALBERD_SPEED;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.WOOD_TIER:
			damage = HAWOOD_DMG;
			actionImage = "HAWOOD_0.png";
			swingSpeed = HALBERD_SPEED;
			break;
		}
	}

	/**
	 * Creates a random weapon at specified location
	 * 
	 * @param x
	 * @param y
	 * @param min the minimum grade of the weapon
	 * @param max the maximum grade of the weapon
	 * @return
	 */
	public static ServerWeapon randomWeapon(double x, double y, int min, int max)
	{
		// Choose a type between the max and min grade
		int randType = (int) (Math.random() * (max - min + 1)) + min;
		switch (randType)
		{
		case 20:
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.DIAMOND_TIER);
		case 16:
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.GOLD_TIER);
		case 12:
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.IRON_TIER);
		case 8:
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.STONE_TIER);
		case 4:
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.WOOD_TIER);
		case 19:
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.DIAMOND_TIER);
		case 15:
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.GOLD_TIER);
		case 11:
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.IRON_TIER);
		case 7:
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.STONE_TIER);
		case 3:
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.WOOD_TIER);
		case 18:
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.DIAMOND_TIER);
		case 14:
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.GOLD_TIER);
		case 10:
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.IRON_TIER);
		case 6:
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.STONE_TIER);
		case 2:
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.WOOD_TIER);
		case 17:
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.DIAMOND_TIER);
		case 13:
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.GOLD_TIER);
		case 9:
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.IRON_TIER);
		case 5:
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.STONE_TIER);
		case 1:
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.WOOD_TIER);
		}
		// This won't happen
		return null;
	}

	public int getDamage()
	{
		return damage;
	}

	public void setDamage(int damage)
	{
		this.damage = damage;
	}

	public String getActionImage()
	{
		return actionImage;
	}

	public void setActionImage(String actionImage)
	{
		this.actionImage = actionImage;
	}

	public int getSwingSpeed()
	{
		return swingSpeed;
	}

	public void setSwingSpeed(int swingSpeed)
	{
		this.swingSpeed = swingSpeed;
	}

	
}
