package Server.Items;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import Imports.Images;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;

public abstract class ServerItem extends ServerObject {

	private final static int NUM_ITEMS = 39;
	
	private boolean hasCoolDown = false;
	private ServerCreature source;
	private Timer coolDownTimer = new Timer(2000, new CoolDownTimer());

	/**
	 * The money value of the item
	 */
	private int value = 1;

	// Amount of this item. Will only be used for potions
	private int amount = 1;

	public ServerItem(double x, double y, String type) {
		super(x, y, 0, 0, ServerWorld.GRAVITY, "SERVERITEM", type);

		
		
		switch (type) {
		case ServerWorld.HP_POTION_TYPE:
			setImage("HP_POTION.png");
			value = 2;
			break;
		case ServerWorld.MAX_HP_TYPE:
			setImage("MAX_HP_POTION.png");
			value = 5;
			break;
		case ServerWorld.MANA_POTION_TYPE:
			setImage("MANA_POTION.png");
			value = 2;
			break;
		case ServerWorld.MAX_MANA_TYPE:
			setImage("MAX_MANA_POTION.png");
			value = 5;
			break;
		case ServerWorld.DMG_POTION_TYPE:
			setImage("DMG_POTION.png");
			value = 8;
			break;
		case ServerWorld.SPEED_POTION_TYPE:
			setImage("SPEED_POTION.png");
			value = 8;
			break;
		case ServerWorld.JUMP_POTION_TYPE:
			setImage("JUMP_POTION.png");
			value = 8;
			break;
		case ServerWorld.MONEY_TYPE:
			setImage("MONEY.png");
			break;
		case ServerWorld.STEEL_ARMOUR:
			setImage("OUTFITARMOR_ICON.png");
			value = 10;
			break;
		case ServerWorld.BLUE_NINJA_ARMOUR:
			setImage("OUTFITNINJABLUE_ICON.png");
			value = 6;
			break;
		case ServerWorld.RED_NINJA_ARMOUR:
			setImage("OUTFITNINJARED_ICON.png");
			value = 3;
			break;
		case ServerWorld.GREY_NINJA_ARMOUR:
			setImage("OUTFITNINJAGREY_ICON.png");
			value = 1;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.DIAMOND_TIER:
			setImage("DADIAMOND_ICON.png");
			value = 15;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.GOLD_TIER:
			setImage("DAGOLD_ICON.png");
			value = 10;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.IRON_TIER:
			setImage("DAIRON_ICON.png");
			value = 6;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.STONE_TIER:
			setImage("DASTONE_ICON.png");
			value = 3;
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.WOOD_TIER:
			setImage("DAWOOD_ICON.png");
			value = 1;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.DIAMOND_TIER:
			setImage("AXDIAMOND_ICON.png");
			value = 15;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.GOLD_TIER:
			setImage("AXGOLD_ICON.png");
			value = 10;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.IRON_TIER:
			setImage("AXIRON_ICON.png");
			value = 6;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.STONE_TIER:
			setImage("AXSTONE_ICON.png");
			value = 3;
			break;
		case ServerWorld.AX_TYPE + ServerWorld.WOOD_TIER:
			setImage("AXWOOD_ICON.png");
			value = 1;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.DIAMOND_TIER:
			setImage("SWDIAMOND_ICON.png");
			value = 16;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.GOLD_TIER:
			setImage("SWGOLD_ICON.png");
			value = 11;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.IRON_TIER:
			setImage("SWIRON_ICON.png");
			value = 7;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.STONE_TIER:
			setImage("SWSTONE_ICON.png");
			value = 4;
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.WOOD_TIER:
			setImage("SWWOOD_ICON.png");
			value = 2;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.DIAMOND_TIER:
			setImage("HADIAMOND_ICON.png");
			value = 16;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.GOLD_TIER:
			setImage("HAGOLD_ICON.png");
			value = 11;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.IRON_TIER:
			setImage("HAIRON_ICON.png");
			value = 7;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.STONE_TIER:
			setImage("HASTONE_ICON.png");
			value = 4;
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.WOOD_TIER:
			setImage("HAWOOD_ICON.png");
			value = 2;
			break;
		case ServerWorld.SLINGSHOT_TYPE:
			setImage("SLINGSHOT_ICON.png");
			value = 1;
			break;
		case ServerWorld.WOODBOW_TYPE:
			setImage("WOODBOW_ICON.png");
			value = 5;
			break;
		case ServerWorld.STEELBOW_TYPE:
			setImage("STEELBOW_ICON.png");
			value = 10;
			break;
		case ServerWorld.MEGABOW_TYPE:
			setImage("MEGABOW_ICON.png");
			value = 20;
			break;
		case ServerWorld.FIREWAND_TYPE:
			setImage("FIREWAND_ICON.png");
			value = 10;
			break;
		case ServerWorld.ICEWAND_TYPE:
			setImage("ICEWAND_ICON.png");
			value = 20;
			break;
		case ServerWorld.DARKWAND_TYPE:
			setImage("DARKWAND_ICON.png");
			value = 30;
			break;
		}

		setWidth(Images.getGameImage(getImage()).getWidth());
		setHeight(Images.getGameImage(getImage()).getHeight());

	}

	public static ServerItem randomItem(double x, double y) {
		int randType = (int) (Math.random() * 7 + 1);

		if(randType <= 2)
			return new ServerMoney(x,y);
		if(randType <= 3)
			return ServerArmour.randomArmour(x, y);
		if(randType <= 5)
			return ServerPotion.randomPotion(x, y);
		if(randType <= 7)
			return ServerWeapon.randomWeapon(x,y);
		
		
		// This won't happen
		return null;

	}

	public static ServerItem copy(ServerItem item) {
		switch (item.getType()) {
		case ServerWorld.HP_POTION_TYPE:
		case ServerWorld.MAX_HP_TYPE:
		case ServerWorld.MANA_POTION_TYPE:
		case ServerWorld.MAX_MANA_TYPE:
		case ServerWorld.DMG_POTION_TYPE:
		case ServerWorld.SPEED_POTION_TYPE:
		case ServerWorld.JUMP_POTION_TYPE:
			return new ServerPotion(item.getX(), item.getY(), item.getType());
		case ServerWorld.MONEY_TYPE:
			return new ServerMoney(item.getX(), item.getY());
		case ServerWorld.STEEL_ARMOUR:
		case ServerWorld.BLUE_NINJA_ARMOUR:
		case ServerWorld.RED_NINJA_ARMOUR:
		case ServerWorld.GREY_NINJA_ARMOUR:
			return new ServerArmour(item.getX(), item.getY(), item.getType());
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
			return new ServerWeapon(item.getX(), item.getY(), item.getType());

		}
		return null;

	}

	public void startCoolDown() {
		hasCoolDown = true;

		// Start a timer for when to set cooldown to false
		coolDownTimer.start();

	}

	public void setSource(ServerCreature source) {
		this.source = source;
	}

	public boolean hasCoolDown() {
		return hasCoolDown;
	}

	public ServerCreature getSource() {
		return source;
	}

	private class CoolDownTimer implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			hasCoolDown = false;
			coolDownTimer.stop();
		}

	}

	public void increaseAmount() {
		amount++;
	}

	public void increaseAmount(int value) {
		amount += value;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void decreaseAmount() {
		amount--;
	}

	public void decreaseAmount(int amount) {
		this.amount -= amount;
	}

	public int getCost() {
		return value;
	}

}
