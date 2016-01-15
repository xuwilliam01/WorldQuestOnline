package Server.Items;

import Server.ServerWorld;

public class ServerPotion extends ServerItem {

	private final static int NUM_POTIONS = 5;
	
	public final static int HEAL_AMOUNT = 50;
	public final static int MAX_HP_INCREASE = 50;
	public final static int MANA_AMOUNT = 50;
	public final static int MAX_MANA_INCREASE = 50;
	public final static int DMG_AMOUNT = 5;

	public ServerPotion(double x, double y, String type) {
		super(x, y, type);
	}

	public static ServerPotion randomPotion(double x, double y) {
		int randType = (int) (Math.random() * NUM_POTIONS + 1);

		switch (randType) {
		case 1:
			return new ServerPotion(x, y,ServerWorld.HP_POTION_TYPE);
		case 2:
			return new ServerPotion(x,y,ServerWorld.MAX_HP_TYPE);
		case 3:
			return new ServerPotion(x,y,ServerWorld.MANA_POTION_TYPE);
		case 4:
			return new ServerPotion(x,y,ServerWorld.MAX_MANA_TYPE);
		case 5:
			return new ServerPotion(x,y,ServerWorld.DMG_POTION_TYPE);
		}
		// This won't happen
		return null;
	}


}
