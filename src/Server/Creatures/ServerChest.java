package Server.Creatures;

import Server.ServerWorld;
import Server.Items.ServerItem;
import Server.Items.ServerPotion;

public class ServerChest extends ServerCreature{

	public final static int CHEST_HP = 100;

	private int numItems = 5;
	
	public ServerChest(double x, double y, int numItems,
			ServerWorld world) {
		super(x, y,-1,-1, 0,0,ServerWorld.GRAVITY, "CHEST.png",ServerWorld.CHEST_TYPE,CHEST_HP, world,true);
		this.numItems = numItems;
		addItems();
	}

	public ServerChest(double x, double y,
			ServerWorld world) {
		super(x, y,-1,-1,0,0, ServerWorld.GRAVITY, "CHEST.png",ServerWorld.CHEST_TYPE,CHEST_HP, world,true);
		addItems();
	}
	
	public void addItems()
	{
		for(int item = 0; item < numItems; item++)
			addItem(ServerItem.randomItem(getX(),getY()));
	}

	public void inflictDamage(int amount, double knockBack)
	{
		setHP(getHP()-amount);
		if (getHP() <= 0)
		{
			destroy();
			dropInventory();
		}
	}
}
