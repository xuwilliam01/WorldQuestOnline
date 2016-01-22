package Server.Creatures;

import Server.ServerWorld;
import Server.Items.ServerArmour;
import Server.Items.ServerPotion;
import Server.Items.ServerWeapon;

/**
 * A vendor that sells items
 * @author Alex Raita & William Xu
 *
 */
public class ServerVendor extends ServerCreature
{
	/**
	 * The maximum size of the vendor's inventory
	 */
	public final static int MAX_INVENTORY = 150;

	/**
	 * Whether or not a player is already using the vendor
	 */
	private boolean isBusy = false;

	/**
	 * Constructor
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param world the world
	 */
	public ServerVendor(double x, double y, ServerWorld world)
	{
		super(x, y, -1, -1, 0, 0, ServerWorld.GRAVITY, "VENDOR_RIGHT.png",
				ServerWorld.VENDOR_TYPE, Integer.MAX_VALUE, world, false);

		if ((int) (Math.random() * 2) == 1)
		{
			setImage("VENDOR_LEFT.png");
		}

		makeShop();

	}

	/**
	 * Constructor
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param world the world the vendor is in
	 * @param image the image for the vendor
	 */
	public ServerVendor(double x, double y, ServerWorld world, String image)
	{
		super(x, y, -1, -1, 0, 0, ServerWorld.GRAVITY, image,
				ServerWorld.VENDOR_TYPE, Integer.MAX_VALUE, world, false);

		makeShop();

	}

	/**
	 * Make the vendor's shop
	 */
	public void makeShop()
	{
		for (int potion = 0; potion < 5; potion++)
		{
			addItem(new ServerPotion(getX(), getY(), ServerWorld.HP_POTION_TYPE));
			addItem(new ServerPotion(getX(), getY(), ServerWorld.MANA_POTION_TYPE));
			addItem(new ServerPotion(getX(), getY(), ServerWorld.MAX_HP_TYPE));
			addItem(new ServerPotion(getX(), getY(), ServerWorld.MAX_MANA_TYPE));
			addItem(new ServerPotion(getX(), getY(), ServerWorld.JUMP_POTION_TYPE));
			addItem(new ServerPotion(getX(), getY(), ServerWorld.SPEED_POTION_TYPE));
			addItem(new ServerPotion(getX(), getY(), ServerWorld.DMG_POTION_TYPE));
		}

		for (int weapon = 0; weapon < 25; weapon++)
			addItem(ServerWeapon.randomShopWeapon(getX(), getY()));

		// Add a rare weapon
		int randWeapon = (int) (Math.random() * 6 + 1);
		switch (randWeapon)
		{
		case 1:
			addItem(new ServerWeapon(getX(), getY(), ServerWorld.DARKWAND_TYPE));
			break;
		case 2:
			addItem(new ServerWeapon(getX(), getY(), ServerWorld.MEGABOW_TYPE));
			break;
		case 3:
			addItem(new ServerWeapon(getX(), getY(), ServerWorld.SWORD_TYPE
					+ ServerWorld.DIAMOND_TIER));
			break;
		case 4:
			addItem(new ServerWeapon(getX(), getY(), ServerWorld.HALBERD_TYPE
					+ ServerWorld.DIAMOND_TIER));
			break;
		case 5:
			addItem(new ServerWeapon(getX(), getY(), ServerWorld.FIREWAND_TYPE));
		case 6:
			addItem(new ServerWeapon(getX(), getY(), ServerWorld.STEELBOW_TYPE));
			break;
		}

		for (int armour = 0; armour < 8; armour++)
			addItem(ServerArmour.randomArmour(getX(), getY()));

		// Always have the steel armour
		addItem(new ServerArmour(getX(), getY(), ServerWorld.STEEL_ARMOUR));

	}

	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////
	public boolean isBusy()
	{
		return isBusy;
	}
	public void setIsBusy(boolean isBusy)
	{
		this.isBusy = isBusy;
	}
}
