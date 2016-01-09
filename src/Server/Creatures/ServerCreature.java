package Server.Creatures;

import java.util.ArrayList;

import Server.ServerObject;
import Server.ServerWorld;
import Server.Items.ServerHPPotion;
import Server.Items.ServerItem;

public abstract class ServerCreature extends ServerObject
{
	/**
	 * Teams
	 */
	public final static int NEUTRAL = 0;
	public final static int RED = 1;
	public final static int BLUE = 2;

	/**
	 * Maximum possible HP of the creature
	 */
	private int maxHP;

	/**
	 * Current HP of the creature
	 */
	private int HP;

	/**
	 * Team of the creature
	 */
	private int team = NEUTRAL;

	/**
	 * Stores the inventory of the creature
	 */
	private ArrayList<ServerItem>  inventory = new ArrayList<ServerItem>();

	/**
	 * World that the creature is in
	 */
	private ServerWorld world;

	/**
	 * The amount of resistance to a knockback by a weapon (normally based on size of the creature)
	 */
	private double knockBackResistance;

	/**
	 * The accessory worn on the head
	 */
	private ServerAccessory head;

	/**
	 * The accessory worn on the body
	 */
	private ServerAccessory body;

	/**
	 * The horizontal direction the creature is facing
	 */
	private String direction;

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
		direction = "RIGHT";

		// Calculate the resistance to knockback based on weight
		knockBackResistance = Math.sqrt((getWidth() * getHeight()))/16;
	}

	public void setTeam(int team)
	{
		this.team = team;
	}

	public int getTeam()
	{
		return team;
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
	public void inflictDamage(int amount, double knockBack)
	{
		HP -= amount;
		if (HP <= 0)
		{
			destroy();
			dropInventory();
		}
		else
		{
			//Only for NPC and PLAYER
			// Knock back the creature based on the knockback force
			//			if (Math.abs(knockBack) - knockBackResistance > 0)
			//			{
			//				setVSpeed(-(Math.abs(knockBack) - knockBackResistance));
			//				if (knockBack > 0)
			//				{
			//					setHSpeed(getHSpeed()+(knockBack-knockBackResistance)/2);
			//				}
			//				else
			//				{
			//					setHSpeed(getHSpeed()-(knockBack+knockBackResistance)/2);
			//				}
			//			}
			
			//If a castle dies, end the game
		}
	}

	/**
	 * Drop every item in the creature's inventory
	 */
	public void dropInventory()
	{
		ServerItem money = null;
		for(ServerItem item : inventory)
		{
			if(item.getType().equals(ServerWorld.MONEY_TYPE) && getType().substring(0,2).equals(ServerWorld.PLAYER_TYPE))
				money = item;
			else
				dropItem(item);
		}

		inventory.clear();
		if(money != null)
			inventory.add(money);
	}

	public ArrayList<ServerItem> getInventory()
	{
		return inventory;
	}

	public void addItem(ServerItem item)
	{
		if(item.getType().charAt(1) == ServerWorld.STACK_TYPE.charAt(1))
			for(ServerItem sItem : inventory)
			{
				if(item.getType().equals(sItem.getType()))
				{
					sItem.increaseAmount();
					return;
				}
			}
		inventory.add(item);
	}

	public void dropItem(ServerItem item)
	{

		item.setX(getX() + getWidth()/2);		
		item.setY(getY() + getHeight()/2);
		item.makeExist();
		item.startCoolDown();
		item.setSource(this);
		world.add(item);
		item.setOnSurface(false);
		item.setVSpeed(-Math.random()*15-5);

		if(HP <= 0)
		{
			int direction = Math.random() < 0.5 ? -1 : 1;
			item.setHSpeed(direction*(Math.random()*5 + 3));
		}
		else
			item.setHSpeed(Math.random()*5 + 3);

	}

	public void use(String item)
	{
		ServerItem toRemove = null;
		for(ServerItem sItem : inventory)
		{
			if(sItem.getType().equals(item))
			{
				toRemove = sItem;

				//If we have a potion
				if(item.charAt(0) == ServerWorld.ITEM_TYPE && item.charAt(1) == ServerWorld.POTION_TYPE.charAt(1))
				{
					if(item.charAt(2) == ServerWorld.HP_POTION_TYPE.charAt(2))
					{
						HP = Math.min(maxHP,HP+((ServerHPPotion)sItem).getHealAmount());
					}

				}
			}
		}

		if(toRemove.getAmount() > 1)
			toRemove.decreaseAmount();
		else
			inventory.remove(toRemove);
	}
	public void drop(String item)
	{
		ServerItem toRemove = null;
		for(ServerItem sItem : inventory)
		{
			if(sItem.getType().equals(item))
			{
				toRemove = sItem;
				if(toRemove.getAmount() > 1)
					dropItem(ServerItem.newItem(sItem.getX(), sItem.getY(), sItem.getType()));
				else
					dropItem(sItem);
				break;
			}
		}


		if(toRemove.getAmount() > 1)
			toRemove.decreaseAmount();
		else
			inventory.remove(toRemove);
	}

	public double getKnockBackResistance()
	{
		return knockBackResistance;
	}

	public void setKnockBackResistance(double knockBackResistance)
	{
		this.knockBackResistance = knockBackResistance;
	}

	public ServerWorld getWorld()
	{
		return world;
	}

	public void setWorld(ServerWorld world)
	{
		this.world = world;
	}

	public ServerAccessory getHead()
	{
		return head;
	}

	public void setHead(ServerAccessory head)
	{
		this.head = head;
	}

	public ServerAccessory getBody()
	{
		return body;
	}

	public void setBody(ServerAccessory body)
	{
		this.body = body;
	}

	public String getDirection()
	{
		return direction;
	}

	public void setDirection(String direction)
	{
		this.direction = direction;
	}


}
