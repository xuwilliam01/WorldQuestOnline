package Server.Creatures;

import Server.ServerWorld;
import Server.Items.ServerItem;
import Server.Items.ServerPotion;

/**
 * A chest that contains items
 * @author Alex Raita & William Xu
 *
 */
public class ServerChest extends ServerCreature
{
	/**
	 * The default HP of a chest
	 */
	public final static int CHEST_HP = 100;

	/**
	 * The default number of items the chest will store, subject to change later
	 */
	private int numItems = 5;

	/**
	 * Constructor
	 * @param x the x-coorindate
	 * @param y the y-coordinate
	 * @param numItems the number of items this chest will contain
	 * @param world the world that contains this chest
	 */
	public ServerChest(double x, double y, int numItems,
			ServerWorld world)
	{
		super(x, y, -1, -1, 0, 0, ServerWorld.GRAVITY, "CHEST.png",
				ServerWorld.CHEST_TYPE, CHEST_HP, world, true);
		this.numItems = numItems;
		addItems();
	}

	/**
	 * Constructor
	 * @param x the x-coorindate
	 * @param y the y-coordinate
	 * @param world the world that contains this chest
	 */
	public ServerChest(double x, double y,
			ServerWorld world)
	{
		super(x, y, -1, -1, 0, 0, ServerWorld.GRAVITY, "CHEST.png",
				ServerWorld.CHEST_TYPE, CHEST_HP, world, true);
		// Default 5 items
		addItems();
	}

	/**
	 * Add items to the chest
	 */
	public void addItems()
	{
		for (int item = 0; item < numItems; item++)
			addItem(ServerItem.randomItem(getX(), getY()));
	}
}
