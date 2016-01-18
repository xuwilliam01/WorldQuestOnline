package Server.Items;

import Server.ServerWorld;

public class ServerWeapon extends ServerItem
{

	public final static int NUM_WEAPONS = 27;
	public final static int NUM_TIERS = 5;

	public final static int DADIAMOND_DMG = 25;
	public final static int DAGOLD_DMG = 17;
	public final static int DAIRON_DMG = 11;
	public final static int DASTONE_DMG = 8;
	public final static int DAWOOD_DMG = 5;

	public final static int AXDIAMOND_DMG = 50;
	public final static int AXGOLD_DMG = 34;
	public final static int AXIRON_DMG = 22;
	public final static int AXSTONE_DMG = 16;
	public final static int AXWOOD_DMG = 10;

	public final static int SWDIAMOND_DMG = 40;
	public final static int SWGOLD_DMG = 27;
	public final static int SWIRON_DMG = 18;
	public final static int SWSTONE_DMG = 12;
	public final static int SWWOOD_DMG = 8;

	public final static int HADIAMOND_DMG = 80;
	public final static int HAGOLD_DMG = 54;
	public final static int HAIRON_DMG = 36;
	public final static int HASTONE_DMG = 24;
	public final static int HAWOOD_DMG =16;

	public final static int SLING_DMG = 10;
	public final static int WOODBOW_DMG = 18;
	public final static int STEELBOW_DMG = 25;
	public final static int MEGABOW_DMG = 30;

	public final static int FIREWAND_DMG = 30;
	public final static int ICEWAND_DMG = 50;
	public final static int DARKWAND_DMG = 80;
	
	public final static int FIREWAND_MANA = 10;
	public final static int ICEWAND_MANA = 15;
	public final static int DARKWAND_MANA = 20;


	/**
	 * The damage the weapon will do when you use it
	 */
	private int damage = -5;

	private int manaCost = 0;
	
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
		case ServerWorld.SLINGSHOT_TYPE:
			damage = SLING_DMG;
			break;
		case ServerWorld.WOODBOW_TYPE:
			damage = WOODBOW_DMG;
			break;
		case ServerWorld.STEELBOW_TYPE:
			damage = STEELBOW_DMG;
			break;
		case ServerWorld.MEGABOW_TYPE:
			damage = MEGABOW_DMG;
			break;
		case ServerWorld.FIREWAND_TYPE:
			damage = FIREWAND_DMG;
			manaCost = FIREWAND_MANA;
			break;
		case ServerWorld.ICEWAND_TYPE:
			damage = ICEWAND_DMG;
			manaCost = ICEWAND_MANA;
			break;
		case ServerWorld.DARKWAND_TYPE:
			damage = DARKWAND_DMG;
			manaCost = DARKWAND_MANA;
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
	public static ServerWeapon randomWeapon(double x, double y)
	{
		// Choose a type between the max and min grade
		int randType = (int) (Math.random() * 135+1);

		if(randType == 1) return new ServerWeapon(x,y,ServerWorld.DARKWAND_TYPE);
		else if(randType == 2) return new ServerWeapon(x,y,ServerWorld.MEGABOW_TYPE);
		else if(randType <= 5) return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
				+ ServerWorld.DIAMOND_TIER);
		else if(randType <= 8) return new ServerWeapon(x, y, ServerWorld.AX_TYPE
				+ ServerWorld.DIAMOND_TIER);
		else if(randType <= 10) return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
				+ ServerWorld.DIAMOND_TIER);
		else if(randType <= 12) return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
				+ ServerWorld.DIAMOND_TIER);
		else if(randType<= 16)
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.GOLD_TIER);
		else if(randType <= 21)
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.IRON_TIER);
		else if(randType <= 27)
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.STONE_TIER);
		else if(randType <= 34)
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.WOOD_TIER);
		else if(randType <= 38)
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.GOLD_TIER);
		else if(randType <= 43)
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.IRON_TIER);
		else if(randType <= 49)
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.STONE_TIER);
		else if(randType <= 56)
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.WOOD_TIER);
		else if(randType <= 61)
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.GOLD_TIER);
		else if(randType <= 67)
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.IRON_TIER);
		else if(randType <= 74)
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.STONE_TIER);
		else if(randType <= 82)
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.WOOD_TIER);
		else if(randType <= 87)
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.GOLD_TIER);
		else if(randType <= 93)
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.IRON_TIER);
		else if(randType <= 100)
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.STONE_TIER);
		else if(randType <= 108)
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.WOOD_TIER);
		else if(randType <= 118)
			return new ServerWeapon(x,y,ServerWorld.SLINGSHOT_TYPE);
		else if(randType <= 124)
			return new ServerWeapon(x,y,ServerWorld.WOODBOW_TYPE);
		else if(randType <= 128)
			return new ServerWeapon(x,y,ServerWorld.STEELBOW_TYPE);
		else if(randType <= 132)
			return new ServerWeapon(x,y,ServerWorld.FIREWAND_TYPE);
		else if(randType <= 135)
			return new ServerWeapon(x,y,ServerWorld.ICEWAND_TYPE);


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
