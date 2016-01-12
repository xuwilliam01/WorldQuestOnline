package Server.Items;

import Server.ServerWorld;

public class ServerWeapon extends ServerItem
{

	public final static int NUM_WEAPONS = 20;
	public final static int NUM_TIERS = 5;

	/**
	 * The damage the weapon will do when you use it
	 */
	private int damage;

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
			damage = 25;
			actionImage = "DADIAMOND_0.png";
			swingSpeed = DAGGER_SPEED;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.GOLD_TIER:
			damage = 20;
			actionImage = "DAGOLD_0.png";
			swingSpeed = DAGGER_SPEED;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.IRON_TIER:
			damage = 15;
			actionImage = "DAIRON_0.png";
			swingSpeed = DAGGER_SPEED;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.STONE_TIER:
			damage = 10;
			actionImage = "DASTONE_0.png";
			swingSpeed = DAGGER_SPEED;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.WOOD_TIER:
			damage = 5;
			actionImage = "DAWOOD_0.png";
			swingSpeed = DAGGER_SPEED;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.DIAMOND_TIER:
			damage = 30;
			actionImage = "AXDIAMOND_0.png";
			swingSpeed = AX_SPEED;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.GOLD_TIER:
			damage = 25;
			actionImage = "AXGOLD_0.png";
			swingSpeed = AX_SPEED;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.IRON_TIER:
			damage = 20;
			actionImage = "AXIRON_0.png";
			swingSpeed = AX_SPEED;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.STONE_TIER:
			damage = 15;
			actionImage = "AXSTONE_0.png";
			swingSpeed = AX_SPEED;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.WOOD_TIER:
			damage = 10;
			actionImage = "AXWOOD_0.png";
			swingSpeed = AX_SPEED;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.DIAMOND_TIER:
			damage = 35;
			actionImage = "SWDIAMOND_0.png";
			swingSpeed = SWORD_SPEED;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.GOLD_TIER:
			damage = 30;
			actionImage = "SWGOLD_0.png";
			swingSpeed = SWORD_SPEED;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.IRON_TIER:
			damage = 25;
			actionImage = "SWIRON_0.png";
			swingSpeed = SWORD_SPEED;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.STONE_TIER:
			damage = 20;
			actionImage = "SWSTONE_0.png";
			swingSpeed = SWORD_SPEED;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.WOOD_TIER:
			damage = 15;
			actionImage = "SWWOOD_0.png";
			swingSpeed = SWORD_SPEED;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.DIAMOND_TIER:
			damage = 40;
			actionImage = "HADIAMOND_0.png";
			swingSpeed = HALBERD_SPEED;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.GOLD_TIER:
			damage = 35;
			actionImage = "HAGOLD_0.png";
			swingSpeed = HALBERD_SPEED;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.IRON_TIER:
			damage = 30;
			actionImage = "HAIRON_0.png";
			swingSpeed = HALBERD_SPEED;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.STONE_TIER:
			damage = 25;
			actionImage = "HASTONE_0.png";
			swingSpeed = HALBERD_SPEED;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.WOOD_TIER:
			damage = 20;
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
