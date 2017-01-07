package Server.Items;

import Imports.Images;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;

/**
 * The basic item superclass
 * @author Alex Raita & William Xu
 *
 */
public abstract class ServerItem extends ServerObject
{

	/**
	 * Whether or not the item has a cooldown when dropped
	 */
	private boolean hasCoolDown = false;
	
	/**
	 * The creature who owns or previously owned the item
	 */
	private ServerCreature source;


	/**
	 * The money value of the item
	 */
	private int value = 1;

	/**
	 * Amount of this item. Will only be used for potions
	 */
	private int amount = 1;

	/**
	 * Items despawn after 30 seconds, so store a variable for when the item was
	 * dropped
	 */
	private long dropTime;
	
	/**
	 * When the cooldown was started
	 */
	private long coolDownStart;

	private ServerWorld world;
	
	/**
	 * Constructor that assigns and image and a value to every item
	 */
	public ServerItem(double x, double y, String type, ServerWorld world)
	{
		super(x, y, 0, 0, ServerWorld.GRAVITY, "SERVERITEM", type,world.getEngine());
		this.world = world;
		switch (type)
		{
		case ServerWorld.HP_POTION_TYPE:
			setImage("HP_POTION");
			value = 3;
			break;
		case ServerWorld.MAX_HP_TYPE:
			setImage("MAX_HP_POTION");
			value = 8;
			break;
		case ServerWorld.MANA_POTION_TYPE:
			setImage("MANA_POTION");
			value = 3;
			break;
		case ServerWorld.MAX_MANA_TYPE:
			setImage("MAX_MANA_POTION");
			value = 7;
			break;
		case ServerWorld.DMG_POTION_TYPE:
			setImage("DMG_POTION");
			value = 12;
			break;
		case ServerWorld.SPEED_POTION_TYPE:
			setImage("SPEED_POTION");
			value = 10;
			break;
		case ServerWorld.JUMP_POTION_TYPE:
			setImage("JUMP_POTION");
			value = 7;
			break;
		case ServerWorld.MONEY_TYPE:
			setImage("MONEY");
			break;
		case ServerWorld.STEEL_ARMOUR:
			setImage("OUTFITARMOR_ICON");
			value = 30;
			break;
		case ServerWorld.RED_NINJA_ARMOUR:
			setImage("OUTFITNINJARED_ICON");
			value = 15;
			break;
		case ServerWorld.BLUE_NINJA_ARMOUR:
			setImage("OUTFITNINJABLUE_ICON");
			value = 8;
			break;
		case ServerWorld.GREY_NINJA_ARMOUR:
			setImage("OUTFITNINJAGREY_ICON");
			value = 3;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.DIAMOND_TIER:
			setImage("DADIAMOND_ICON");
			value = 25;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.GOLD_TIER:
			setImage("DAGOLD_ICON");
			value = 15;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.IRON_TIER:
			setImage("DAIRON_ICON");
			value = 6;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.STONE_TIER:
			setImage("DASTONE_ICON");
			value = 3;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.WOOD_TIER:
			setImage("DAWOOD_ICON");
			value = 1;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.DIAMOND_TIER:
			setImage("AXDIAMOND_ICON");
			value = 30;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.GOLD_TIER:
			setImage("AXGOLD_ICON");
			value = 18;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.IRON_TIER:
			setImage("AXIRON_ICON");
			value = 8;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.STONE_TIER:
			setImage("AXSTONE_ICON");
			value = 3;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.WOOD_TIER:
			setImage("AXWOOD_ICON");
			value = 1;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.DIAMOND_TIER:
			setImage("SWDIAMOND_ICON");
			value = 35;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.GOLD_TIER:
			setImage("SWGOLD_ICON");
			value = 25;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.IRON_TIER:
			setImage("SWIRON_ICON");
			value = 10;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.STONE_TIER:
			setImage("SWSTONE_ICON");
			value = 4;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.WOOD_TIER:
			setImage("SWWOOD_ICON");
			value = 2;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.DIAMOND_TIER:
			setImage("HADIAMOND_ICON");
			value = 40;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.GOLD_TIER:
			setImage("HAGOLD_ICON");
			value = 28;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.IRON_TIER:
			setImage("HAIRON_ICON");
			value = 11;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.STONE_TIER:
			setImage("HASTONE_ICON");
			value = 4;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.WOOD_TIER:
			setImage("HAWOOD_ICON");
			value = 2;
			break;
		case ServerWorld.SLINGSHOT_TYPE:
			setImage("SLINGSHOT_ICON");
			value = 2;
			break;
		case ServerWorld.WOODBOW_TYPE:
			setImage("WOODBOW_ICON");
			value = 15;
			break;
		case ServerWorld.STEELBOW_TYPE:
			setImage("STEELBOW_ICON");
			value = 30;
			break;
		case ServerWorld.MEGABOW_TYPE:
			setImage("MEGABOW_ICON");
			value = 50;
			break;
		case ServerWorld.FIREWAND_TYPE:
			setImage("FIREWAND_ICON");
			value = 30;
			break;
		case ServerWorld.ICEWAND_TYPE:
			setImage("ICEWAND_ICON");
			value = 15;
			break;
		case ServerWorld.DARKWAND_TYPE:
			setImage("DARKWAND_ICON");
			value = 60;
			break;
		case ServerWorld.BARRACK_ITEM_TYPE:
			setImage("BARRACKS_ICON");
			value = ServerBuildingItem.BARRACK_COST; //Change?
			break;
		case ServerWorld.WOOD_HOUSE_ITEM_TYPE:
			setImage("WOOD_HOUSE_ICON");
			value = ServerBuildingItem.WOOD_HOUSE_COST; //Change?
			break;
		case ServerWorld.TOWER_ITEM_TYPE:
			setImage("TOWER_ICON");
			value = ServerBuildingItem.TOWER_COST; //Change?
			break;
		case ServerWorld.GOLD_MINE_ITEM_TYPE:
			setImage("GOLD_MINE_ICON");
			value = ServerBuildingItem.GOLD_MINE_COST; //Change?
			break;
		}

		setWidth(Images.getGameImage(getImage()).getWidth());
		setHeight(Images.getGameImage(getImage()).getHeight());

	}

	/**
	 * Creates a random item
	 */
	public static ServerItem randomItem(double x, double y, ServerWorld world)
	{
		int randType = (int) (Math.random() * 13 + 1);

		if (randType <= 7)
			return new ServerMoney(x, y,world);
		if (randType <= 8)
			return ServerArmour.randomArmour(x, y,world);
		if (randType <= 10)
			return ServerPotion.randomPotion(x, y,world);
		if (randType <= 13)
			return ServerWeapon.randomWeapon(x, y,world);

		// This won't happen
		return null;

	}

	/**
	 * Copies a given item
	 * @param item the item to be copied
	 * @return a copy of the item
	 */
	public static ServerItem copy(ServerItem item)
	{
		switch (item.getType())
		{
		case ServerWorld.HP_POTION_TYPE:
		case ServerWorld.MAX_HP_TYPE:
		case ServerWorld.MANA_POTION_TYPE:
		case ServerWorld.MAX_MANA_TYPE:
		case ServerWorld.DMG_POTION_TYPE:
		case ServerWorld.SPEED_POTION_TYPE:
		case ServerWorld.JUMP_POTION_TYPE:
			return new ServerPotion(item.getX(), item.getY(), item.getType(), item.getWorld());
		case ServerWorld.MONEY_TYPE:
			return new ServerMoney(item.getX(), item.getY(),item.getWorld());
		case ServerWorld.STEEL_ARMOUR:
		case ServerWorld.BLUE_NINJA_ARMOUR:
		case ServerWorld.RED_NINJA_ARMOUR:
		case ServerWorld.GREY_NINJA_ARMOUR:
			return new ServerArmour(item.getX(), item.getY(), item.getType(),item.getWorld());
		case ServerWorld.HALBERD_TYPE + ServerWorld.DIAMOND_TIER:
		case ServerWorld.HALBERD_TYPE + ServerWorld.GOLD_TIER:
		case ServerWorld.HALBERD_TYPE + ServerWorld.IRON_TIER:
		case ServerWorld.HALBERD_TYPE + ServerWorld.STONE_TIER:
		case ServerWorld.HALBERD_TYPE + ServerWorld.WOOD_TIER:
		case ServerWorld.SWORD_TYPE + ServerWorld.DIAMOND_TIER:
		case ServerWorld.SWORD_TYPE + ServerWorld.GOLD_TIER:
		case ServerWorld.SWORD_TYPE + ServerWorld.IRON_TIER:
		case ServerWorld.SWORD_TYPE + ServerWorld.STONE_TIER:
		case ServerWorld.SWORD_TYPE + ServerWorld.WOOD_TIER:
		case ServerWorld.AX_TYPE + ServerWorld.DIAMOND_TIER:
		case ServerWorld.AX_TYPE + ServerWorld.GOLD_TIER:
		case ServerWorld.AX_TYPE + ServerWorld.IRON_TIER:
		case ServerWorld.AX_TYPE + ServerWorld.STONE_TIER:
		case ServerWorld.AX_TYPE + ServerWorld.WOOD_TIER:
		case ServerWorld.DAGGER_TYPE + ServerWorld.DIAMOND_TIER:
		case ServerWorld.DAGGER_TYPE + ServerWorld.GOLD_TIER:
		case ServerWorld.DAGGER_TYPE + ServerWorld.IRON_TIER:
		case ServerWorld.DAGGER_TYPE + ServerWorld.STONE_TIER:
		case ServerWorld.DAGGER_TYPE + ServerWorld.WOOD_TIER:
		case ServerWorld.SLINGSHOT_TYPE:
		case ServerWorld.WOODBOW_TYPE:
		case ServerWorld.STEELBOW_TYPE:
		case ServerWorld.MEGABOW_TYPE:
		case ServerWorld.FIREWAND_TYPE:
		case ServerWorld.ICEWAND_TYPE:
		case ServerWorld.DARKWAND_TYPE:
			return new ServerWeapon(item.getX(), item.getY(), item.getType(),item.getWorld());
		case ServerWorld.BARRACK_ITEM_TYPE:
		case ServerWorld.WOOD_HOUSE_ITEM_TYPE:
			return new ServerBuildingItem(item.getType(),item.getWorld());

		}
		return null;
	}
	
	/**
	 * Start a cooldown before the item can be picked up again so it isn't
	 * instantly picked up by the creature that dropped it
	 * @param start the start time of the cooldown
	 */
	public void startCoolDown(long start)
	{
		hasCoolDown = true;
		coolDownStart = start;
		dropTime = start;
	}

	public ServerWorld getWorld()
	{
		return world;
	}
	
	/**
	 * Checks if the item has a cooldown and if it should be destroyed
	 * @param currentTime the current world time
	 */
	public void update(long currentTime)
	{
		if(hasCoolDown && currentTime - coolDownStart > 120)
		{
			hasCoolDown = false;
		}
		else if(currentTime - dropTime > 1800 && isOnSurface())
		{
			destroy();
		}
		if (isOnSurface())
		{
			setHSpeed(0);
		}
	}
	
	// ///////////////////////
	// GETTERS AND SETTERS //
	// ///////////////////////
	public void setSource(ServerCreature source)
	{
		this.source = source;
	}

	public boolean hasCoolDown()
	{
		return hasCoolDown;
	}

	public ServerCreature getSource()
	{
		return source;
	}

	public void increaseAmount()
	{
		amount++;
	}

	public void increaseAmount(int value)
	{
		amount += value;
	}

	public int getAmount()
	{
		return amount;
	}

	public void setAmount(int amount)
	{
		this.amount = amount;
	}

	public void decreaseAmount()
	{
		amount--;
	}

	public void decreaseAmount(int amount)
	{
		this.amount -= amount;
	}

	public int getCost()
	{
		return value;
	}

	public long getDropTime()
	{
		return dropTime;
	}

	public void setDropTime(long dropTime)
	{
		this.dropTime = dropTime;
	}
}