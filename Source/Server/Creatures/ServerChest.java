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
	public final static int CHEST_HP = 200;

	/**
	 * Max items a chest can have
	 */
	private final static int NUM_ITEMS = 4;

	/**
	 * The default number of items the chest will store, subject to change later
	 */
	private int numItems = NUM_ITEMS;

	/**
	 * When this chest was destroyed
	 */
	private long destroyTime;

	/**
	 * How long the chest waits to respawn
	 */
	private int respawnTime;

	/**
	 * Constructor
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param numItems the number of items this chest will contain
	 * @param world the world that contains this chest
	 */
	public ServerChest(double x, double y, int numItems,
			ServerWorld world)
	{
		super(x, y, -1, -1, 0, 0, ServerWorld.GRAVITY, "CHEST",
				ServerWorld.CHEST_TYPE, CHEST_HP, world, true);
		this.numItems = numItems;
		addItems();
	}

	/**
	 * Constructor
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param world the world that contains this chest
	 */
	public ServerChest(double x, double y,
			ServerWorld world)
	{
		super(x, y, -1, -1, 0, 0, ServerWorld.GRAVITY, "CHEST",
				ServerWorld.CHEST_TYPE, CHEST_HP, world, true);
		addItems();
		setName("Chest");
	}

	/**
	 * Add items to the chest
	 */
	public void addItems()
	{
		numItems = (int) (Math.random() * (NUM_ITEMS - 1)) + 2;
		for (int item = 0; item < numItems; item++)
			addItem(ServerItem.randomItem(getX(), getY(),getWorld()));
	}

	public void destroy()
	{
		super.destroy();
		destroyTime = getWorld().getWorldCounter();
		respawnTime = (int) (Math.random() * 18000 + 10800);
	}

	public void update()
	{
		if (!exists())
		{
			if (getWorld().getWorldCounter() - destroyTime > respawnTime)
			{
				makeExist();
				setHP(CHEST_HP);
				addItems();
			}
		}
	}
}
