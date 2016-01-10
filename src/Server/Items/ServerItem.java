package Server.Items;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import Imports.Images;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;

public abstract class ServerItem extends ServerObject {

	private final static int NUM_ITEMS = 22;
	private boolean hasCoolDown = false;
	private ServerCreature source;
	private Timer coolDownTimer = new Timer(2000, new CoolDownTimer());

	// Amount of this item. Will only be used for potions
	private int amount = 1;

	public ServerItem(double x, double y, String type) {
		super(x, y, 0, 0, ServerWorld.GRAVITY, "", type);

		switch (type) {
		case ServerWorld.HP_50:
			setImage("HP_POTION.png");
			break;
		case ServerWorld.MONEY_TYPE:
			setImage("MONEY.png");
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.DIAMOND_TIER:
			setImage("DADIAMOND_ICON.png");
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.GOLD_TIER:
			setImage("DAGOLD_ICON.png");
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.IRON_TIER:
			setImage("DAIRON_ICON.png");
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.STONE_TIER:
			setImage("DASTONE_ICON.png");
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.WOOD_TIER:
			setImage("DAWOOD_ICON.png");
			break;
		case ServerWorld.AX_TYPE + ServerWorld.DIAMOND_TIER:
			setImage("AXDIAMOND_ICON.png");
			break;
		case ServerWorld.AX_TYPE + ServerWorld.GOLD_TIER:
			setImage("AXGOLD_ICON.png");
			break;
		case ServerWorld.AX_TYPE + ServerWorld.IRON_TIER:
			setImage("AXIRON_ICON.png");
			break;
		case ServerWorld.AX_TYPE + ServerWorld.STONE_TIER:
			setImage("AXSTONE_ICON.png");
			break;
		case ServerWorld.AX_TYPE + ServerWorld.WOOD_TIER:
			setImage("AXWOOD_ICON.png");
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.DIAMOND_TIER:
			setImage("SWDIAMOND_ICON.png");
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.GOLD_TIER:
			setImage("SWGOLD_ICON.png");
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.IRON_TIER:
			setImage("SWIRON_ICON.png");
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.STONE_TIER:
			setImage("SWSTONE_ICON.png");
			break;
		case ServerWorld.SWORD_TYPE + ServerWorld.WOOD_TIER:
			setImage("SWWOOD_ICON.png");
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.DIAMOND_TIER:
			setImage("HADIAMOND_ICON.png");
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.GOLD_TIER:
			setImage("HAGOLD_ICON.png");
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.IRON_TIER:
			setImage("HAIRON_ICON.png");
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.STONE_TIER:
			setImage("HASTONE_ICON.png");
			break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.WOOD_TIER:
			setImage("HAWOOD_ICON.png");
			break;
		}

		setWidth(Images.getGameImage(getImage()).getWidth());
		setHeight(Images.getGameImage(getImage()).getHeight());

	}

	public static ServerItem randomItem(double x, double y) {
		int randType = (int) (Math.random() * NUM_ITEMS + 1);

		switch (randType) {
		case 1:
			return new ServerHPPotion(x, y);	
		case 2:
			return new ServerMoney(x, y);
		case 3:
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE+ServerWorld.DIAMOND_TIER);
		case 4:
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE+ServerWorld.GOLD_TIER);
		case 5:
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE+ServerWorld.IRON_TIER);
		case 6:
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE+ServerWorld.STONE_TIER);
		case 7:
			return new ServerWeapon(x, y, ServerWorld.HALBERD_TYPE+ServerWorld.WOOD_TIER);
		case 8:
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE+ServerWorld.DIAMOND_TIER);
		case 9:
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE+ServerWorld.GOLD_TIER);
		case 10:
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE+ServerWorld.IRON_TIER);
		case 11:
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE+ServerWorld.STONE_TIER);
		case 12:
			return new ServerWeapon(x, y, ServerWorld.SWORD_TYPE+ServerWorld.WOOD_TIER);
		case 13:
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE+ServerWorld.DIAMOND_TIER);
		case 14:
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE+ServerWorld.GOLD_TIER);
		case 15:
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE+ServerWorld.IRON_TIER);
		case 16:
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE+ServerWorld.STONE_TIER);
		case 17:
			return new ServerWeapon(x, y, ServerWorld.AX_TYPE+ServerWorld.WOOD_TIER);
		case 18:
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE+ServerWorld.DIAMOND_TIER);
		case 19:
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE+ServerWorld.GOLD_TIER);
		case 20:
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE+ServerWorld.IRON_TIER);
		case 21:
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE+ServerWorld.STONE_TIER);
		case 22:
			return new ServerWeapon(x, y, ServerWorld.DAGGER_TYPE+ServerWorld.WOOD_TIER);
		}
		// This won't happen
		return null;

	}

	public static ServerItem copy(ServerItem item) {
		switch (item.getType()) {
		case ServerWorld.HP_50:
			return new ServerHPPotion(item.getX(), item.getY());
		case ServerWorld.MONEY_TYPE:
			return new ServerMoney(item.getX(), item.getY());
		case ServerWorld.HALBERD_TYPE+ServerWorld.DIAMOND_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.HALBERD_TYPE+ServerWorld.DIAMOND_TIER);
		case ServerWorld.HALBERD_TYPE+ServerWorld.GOLD_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.HALBERD_TYPE+ServerWorld.GOLD_TIER);
		case ServerWorld.HALBERD_TYPE+ServerWorld.IRON_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.HALBERD_TYPE+ServerWorld.IRON_TIER);
		case ServerWorld.HALBERD_TYPE+ServerWorld.STONE_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.HALBERD_TYPE+ServerWorld.STONE_TIER);
		case ServerWorld.HALBERD_TYPE+ServerWorld.WOOD_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.HALBERD_TYPE+ServerWorld.WOOD_TIER);
		case ServerWorld.SWORD_TYPE+ServerWorld.DIAMOND_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.SWORD_TYPE+ServerWorld.DIAMOND_TIER);
		case ServerWorld.SWORD_TYPE+ServerWorld.GOLD_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.SWORD_TYPE+ServerWorld.GOLD_TIER);
		case ServerWorld.SWORD_TYPE+ServerWorld.IRON_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.SWORD_TYPE+ServerWorld.IRON_TIER);
		case ServerWorld.SWORD_TYPE+ServerWorld.STONE_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.SWORD_TYPE+ServerWorld.STONE_TIER);
		case ServerWorld.SWORD_TYPE+ServerWorld.WOOD_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.SWORD_TYPE+ServerWorld.WOOD_TIER);
		case ServerWorld.AX_TYPE+ServerWorld.DIAMOND_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.AX_TYPE+ServerWorld.DIAMOND_TIER);
		case ServerWorld.AX_TYPE+ServerWorld.GOLD_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.AX_TYPE+ServerWorld.GOLD_TIER);
		case ServerWorld.AX_TYPE+ServerWorld.IRON_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.AX_TYPE+ServerWorld.IRON_TIER);
		case ServerWorld.AX_TYPE+ServerWorld.STONE_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.AX_TYPE+ServerWorld.STONE_TIER);
		case ServerWorld.AX_TYPE+ServerWorld.WOOD_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.AX_TYPE+ServerWorld.WOOD_TIER);
		case ServerWorld.DAGGER_TYPE+ServerWorld.DIAMOND_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.DAGGER_TYPE+ServerWorld.DIAMOND_TIER);
		case ServerWorld.DAGGER_TYPE+ServerWorld.GOLD_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.DAGGER_TYPE+ServerWorld.GOLD_TIER);
		case ServerWorld.DAGGER_TYPE+ServerWorld.IRON_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.DAGGER_TYPE+ServerWorld.IRON_TIER);
		case ServerWorld.DAGGER_TYPE+ServerWorld.STONE_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.DAGGER_TYPE+ServerWorld.STONE_TIER);
		case ServerWorld.DAGGER_TYPE+ServerWorld.WOOD_TIER:
			return new ServerWeapon(item.getX(), item.getY(), ServerWorld.DAGGER_TYPE+ServerWorld.WOOD_TIER);
			
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

}
