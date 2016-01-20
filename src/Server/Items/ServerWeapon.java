package Server.Items;

import Server.ServerWorld;

/**
 * Class for each weapon
 * @author Alex Raita & William Xu
 *
 */
public class ServerWeapon extends ServerItem
{

	public final static int NUM_WEAPONS = 27;
	public final static int NUM_TIERS = 5;

	//Weapons
	public final static int DADIAMOND_DMG = 35;
	public final static int DAGOLD_DMG = 25;
	public final static int DAIRON_DMG = 18;
	public final static int DASTONE_DMG = 12;
	public final static int DAWOOD_DMG = 8;

	public final static int AXDIAMOND_DMG = 50;
	public final static int AXGOLD_DMG = 35;
	public final static int AXIRON_DMG = 24;
	public final static int AXSTONE_DMG = 18;
	public final static int AXWOOD_DMG = 13;

	public final static int SWDIAMOND_DMG = 40;
	public final static int SWGOLD_DMG = 27;
	public final static int SWIRON_DMG = 21;
	public final static int SWSTONE_DMG = 13;
	public final static int SWWOOD_DMG = 9;

	public final static int HADIAMOND_DMG = 60;
	public final static int HAGOLD_DMG = 45;
	public final static int HAIRON_DMG = 30;
	public final static int HASTONE_DMG = 22;
	public final static int HAWOOD_DMG =14;

	public final static int STAR_DMG = 10;
	public final static int SLING_DMG = 7;
	public final static int WOODBOW_DMG = 10;
	public final static int STEELBOW_DMG = 14;
	public final static int MEGABOW_DMG = 25;

	public final static int FIREWAND_DMG = 40;
	public final static int ICEWAND_DMG = 50;
	public final static int DARKWAND_DMG = 80;
	
	public final static int FIREWAND_MANA = 7;
	public final static int ICEWAND_MANA = 8;
	public final static int DARKWAND_MANA = 15;


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

	/**
	 * Constructor builds a weapon
	 */
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
			break;
		case ServerWorld.ICEWAND_TYPE:
			damage = ICEWAND_DMG;
			break;
		case ServerWorld.DARKWAND_TYPE:
			damage = DARKWAND_DMG;
			break;
		}
	}

	/**
	 * Creates a random weapon at specified location
	 * Better weapons are more rare
	 * @return a new weapon
	 */
	public static ServerWeapon randomWeapon(double x, double y)
	{
		int randType = (int) (Math.random() * 202+1);
		
		if(randType == 1) return new ServerWeapon(x,y,ServerWorld.DARKWAND_TYPE);
		else if(randType == 2) return new ServerWeapon(x,y,ServerWorld.MEGABOW_TYPE);
		else if(randType <= 6) return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
				+ ServerWorld.DIAMOND_TIER);
		else if(randType <= 10) return new ServerWeapon(x, y, ServerWorld.AX_TYPE
				+ ServerWorld.DIAMOND_TIER);
		else if(randType <= 12) return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
				+ ServerWorld.DIAMOND_TIER);
		else if(randType <= 14) return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
				+ ServerWorld.DIAMOND_TIER);
		else if(randType<= 20)
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.GOLD_TIER);
		else if(randType <= 28)
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.IRON_TIER);
		else if(randType <= 38)
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.STONE_TIER);
		else if(randType <= 50)
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.WOOD_TIER);
		else if(randType <= 56)
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.GOLD_TIER);
		else if(randType <= 64)
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.IRON_TIER);
		else if(randType <= 74)
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.STONE_TIER);
		else if(randType <= 86)
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.WOOD_TIER);
		else if(randType <= 94)
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.GOLD_TIER);
		else if(randType <= 104)
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.IRON_TIER);
		else if(randType <= 116)
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.STONE_TIER);
		else if(randType <= 130)
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.WOOD_TIER);
		else if(randType <= 138)
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.GOLD_TIER);
		else if(randType <= 148)
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.IRON_TIER);
		else if(randType <= 160)
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.STONE_TIER);
		else if(randType <= 174)
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.WOOD_TIER);
		else if(randType <= 186)
			return new ServerWeapon(x,y,ServerWorld.SLINGSHOT_TYPE);
		else if(randType <= 192)
			return new ServerWeapon(x,y,ServerWorld.WOODBOW_TYPE);
		else if(randType <= 194)
			return new ServerWeapon(x,y,ServerWorld.STEELBOW_TYPE);
		else if(randType <= 196)
			return new ServerWeapon(x,y,ServerWorld.FIREWAND_TYPE);
		else if(randType <= 202)
			return new ServerWeapon(x,y,ServerWorld.ICEWAND_TYPE);


		// This won't happen
		return null;
	}

	/**
	 * Creates a random weapon for the shop, meaning equal probabilities for each weapon
	 */
	public static ServerWeapon randomShopWeapon(double x, double y)
	{
		// Choose a type between the max and min grade
		int randType = (int) (Math.random() * NUM_WEAPONS+1);
		
		if(randType == 1) return new ServerWeapon(x,y,ServerWorld.DARKWAND_TYPE);
		else if(randType == 2) return new ServerWeapon(x,y,ServerWorld.MEGABOW_TYPE);
		else if(randType <= 3) return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
				+ ServerWorld.DIAMOND_TIER);
		else if(randType <= 4) return new ServerWeapon(x, y, ServerWorld.AX_TYPE
				+ ServerWorld.DIAMOND_TIER);
		else if(randType <= 5) return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
				+ ServerWorld.DIAMOND_TIER);
		else if(randType <= 6) return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
				+ ServerWorld.DIAMOND_TIER);
		else if(randType<= 7)
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.GOLD_TIER);
		else if(randType <= 8)
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.IRON_TIER);
		else if(randType <= 9)
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.STONE_TIER);
		else if(randType <= 10)
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE
					+ ServerWorld.WOOD_TIER);
		else if(randType <= 11)
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.GOLD_TIER);
		else if(randType <= 12)
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.IRON_TIER);
		else if(randType <= 13)
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.STONE_TIER);
		else if(randType <= 14)
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE
					+ ServerWorld.WOOD_TIER);
		else if(randType <= 15)
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.GOLD_TIER);
		else if(randType <= 16)
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.IRON_TIER);
		else if(randType <= 17)
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.STONE_TIER);
		else if(randType <= 18)
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE
					+ ServerWorld.WOOD_TIER);
		else if(randType <= 19)
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.GOLD_TIER);
		else if(randType <= 20)
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.IRON_TIER);
		else if(randType <= 21)
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.STONE_TIER);
		else if(randType <= 22)
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE
					+ ServerWorld.WOOD_TIER);
		else if(randType <= 23)
			return new ServerWeapon(x,y,ServerWorld.SLINGSHOT_TYPE);
		else if(randType <= 24)
			return new ServerWeapon(x,y,ServerWorld.WOODBOW_TYPE);
		else if(randType <= 25)
			return new ServerWeapon(x,y,ServerWorld.STEELBOW_TYPE);
		else if(randType <= 26)
			return new ServerWeapon(x,y,ServerWorld.FIREWAND_TYPE);
		else if(randType <= 27)
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
