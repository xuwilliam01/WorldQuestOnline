package Server.Items;

import Server.ServerWorld;

public class ServerWeapon extends ServerItem {

	public final static int NUM_WEAPONS = 20;
	public final static int NUM_TIERS = 5;
	
	private int damage;

	public ServerWeapon(double x, double y, String type) {
		super(x, y, type);

		switch (type) {
		case ServerWorld.DAGGER_TYPE + ServerWorld.DIAMOND_TIER:
			damage = 25;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.GOLD_TIER:
			damage = 20;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.IRON_TIER:
			damage = 15;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.STONE_TIER:
			damage = 10;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.WOOD_TIER:
			damage = 5;
		case ServerWorld.AX_TYPE + ServerWorld.DIAMOND_TIER:
			damage = 30;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.GOLD_TIER:
			damage = 25;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.IRON_TIER:
			damage = 20;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.STONE_TIER:
			damage = 15;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.WOOD_TIER:
			damage = 10;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.DIAMOND_TIER:
			damage = 35;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.GOLD_TIER:
			damage = 30;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.IRON_TIER:
			damage = 25;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.STONE_TIER:
			damage = 20;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.WOOD_TIER:
			damage = 15;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.DIAMOND_TIER:
			damage = 40;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.GOLD_TIER:
			damage = 35;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.IRON_TIER:
			damage = 30;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.STONE_TIER:
			damage = 25;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.WOOD_TIER:
			damage = 20;
			break;
		}
	}

	/**
	 * Creates a random weapon at specified location
	 * 
	 * @param x
	 * @param y
	 * @param min
	 *            the minimum grade of the weapon
	 * @param max
	 *            the maximum grade of the weapon
	 * @return
	 */
	public static ServerWeapon randomWeapon(double x, double y, int min, int max) {
		// Choose a type between the max and min grade
		int randType = (int) (Math.random() * (max - min + 1)) + min;
		switch (randType) {
		case 20:
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE + ServerWorld.DIAMOND_TIER);
		case 16:
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE + ServerWorld.GOLD_TIER);
		case 12:
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE + ServerWorld.IRON_TIER);
		case 8:
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE + ServerWorld.STONE_TIER);
		case 4:
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE + ServerWorld.WOOD_TIER);
		case 19:
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE + ServerWorld.DIAMOND_TIER);
		case 15:
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE + ServerWorld.GOLD_TIER);
		case 11:
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE + ServerWorld.IRON_TIER);
		case 7:
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE + ServerWorld.STONE_TIER);
		case 3:
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE + ServerWorld.WOOD_TIER);
		case 18:
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE + ServerWorld.DIAMOND_TIER);
		case 14:
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE + ServerWorld.GOLD_TIER);
		case 10:
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE + ServerWorld.IRON_TIER);
		case 6:
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE + ServerWorld.STONE_TIER);
		case 2:
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE + ServerWorld.WOOD_TIER);
		case 17:
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE + ServerWorld.DIAMOND_TIER);
		case 13:
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE + ServerWorld.GOLD_TIER);
		case 9:
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE + ServerWorld.IRON_TIER);
		case 5:
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE + ServerWorld.STONE_TIER);
		case 1:
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE + ServerWorld.WOOD_TIER);
		}
		// This won't happen
		return null;
	}

}
