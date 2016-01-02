package Server.Creatures;

import java.util.ArrayList;

import Server.ServerObject;
import Server.ServerWorld;
import Server.Items.ServerItem;

public abstract class ServerCreature extends ServerObject
{
	/**
	 * Maximum possible HP of the creature
	 */
	private int maxHP;
	
	/**
	 * Current HP of the creature
	 */
	private int HP;
	
	/**
	 * Stores the inventory of the creature
	 */
	private ArrayList<ServerItem>  inventory = new ArrayList<ServerItem>();
	
	/**
	 * World that the creature is in
	 */
	private ServerWorld world;
	

	/**
	 * Constructor for a creature
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param gravity
	 * @param ID
	 * @param image
	 * @param type
	 * @param maxHP
	 * @param world
	 */
	public ServerCreature(double x, double y, int width, int height,
			double gravity, String image, String type, int maxHP, ServerWorld world)
	{
		super(x, y, width, height, gravity, image, type);
		this.maxHP = maxHP;
		HP = maxHP;
		this.world = world;
	}

	public int getMaxHP()
	{
		return maxHP;
	}
	
	public int getHP()
	{
		return HP;
	}
	
	/**
	 * Set HP to a certain amount
	 * @param HP
	 */
	public void setHP(int HP)
	{
		this.HP = HP;
	}
	
	/**
	 * Inflict a certain amount of damage to the npc and destroy if less than 0 hp
	 * @param amount
	 */
	public void inflictDamage(int amount)
	{
		HP -= amount;
		if (HP <= 0)
		{
			destroy();
			dropInventory();
		}
	}
	
	/**
	 * Drop every item in the creature's inventory
	 */
	public void dropInventory()
	{
		for(ServerItem item : inventory)
		{
			item.setX(getX() + getWidth()/2);
			item.setY(getY() + getHeight()/2);
			item.makeExist();
			world.add(item);
			item.setOnSurface(false);
			item.setVSpeed(-Math.random()*15-5);

			int direction = Math.random() < 0.5 ? -1 : 1;
			item.setHSpeed(direction*(Math.random()*5 + 3));
		}
		
		inventory.clear();
	}
	
	public ArrayList<ServerItem> getInventory()
	{
		return inventory;
	}
	
	public void addItem(ServerItem item)
	{
		inventory.add(item);
	}
}
