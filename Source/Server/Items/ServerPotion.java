package Server.Items;

import Server.ServerWorld;

/**
 * Potion class
 * @author Alex Raita & William Xu
 *
 */
public class ServerPotion extends ServerItem
{

	// Variables for each type of potion
	public final static int HEAL_AMOUNT = 25;
	public final static int MAX_HP_INCREASE = 25;
	public final static int MANA_AMOUNT = 50;
	public final static int MAX_MANA_INCREASE = 25;
	public final static int DMG_AMOUNT = 5;
	public final static int SPEED_AMOUNT = 1;
	public final static int JUMP_AMOUNT = 1;

	/**
	 * Constructor
	 */
	public ServerPotion(double x, double y, String type)
	{
		super(x, y, type);
	}

	/**
	 * Returns a random potion with given probabilities
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public static ServerPotion randomPotion(double x, double y)
	{
		int randType = (int) (Math.random() * 21 + 1);

		if (randType <= 1)
			return new ServerPotion(x, y, ServerWorld.DMG_POTION_TYPE);
		if (randType <= 3)
			return new ServerPotion(x, y, ServerWorld.SPEED_POTION_TYPE);
		if (randType <= 5)
			return new ServerPotion(x, y, ServerWorld.JUMP_POTION_TYPE);
		if (randType <= 7)
			return new ServerPotion(x, y, ServerWorld.MAX_MANA_TYPE);
		if (randType <= 9)
			return new ServerPotion(x, y, ServerWorld.MAX_HP_TYPE);
		if (randType <= 15)
			return new ServerPotion(x, y, ServerWorld.HP_POTION_TYPE);
		if (randType <= 21)
			return new ServerPotion(x, y, ServerWorld.MANA_POTION_TYPE);

		// This won't happen
		return null;
	}

	@Override
	public void update()
	{
		// TODO Auto-generated method stub
		
	}

}
