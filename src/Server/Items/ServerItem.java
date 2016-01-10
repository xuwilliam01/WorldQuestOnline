package Server.Items;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import Imports.Images;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;

public abstract class ServerItem extends ServerObject
{

	private final static int NUM_ITEMS = 3;
	private boolean hasCoolDown = false;
	private ServerCreature source;
	private Timer coolDownTimer = new Timer(2000, new CoolDownTimer());

	// Amount of this item. Will only be used for potions
	private int amount = 1;

	public ServerItem(double x, double y, String type)
	{
		super(x, y, 0, 0, ServerWorld.GRAVITY, "", type);

		String image = "";

		boolean isTiered = true;
		if (type.contains(ServerWorld.DAGGER_TYPE))
		{
			image += "DA";
		}
		else if (type.contains(ServerWorld.AX_TYPE))
		{
			image += "AX";
		}
		else if (type.contains(ServerWorld.SWORD_TYPE))
		{
			image += "SW";
		}
		else if (type.contains(ServerWorld.HALBERD_TYPE))
		{
			image += "HA";
		}
		else
		{
			isTiered = false;
		}

		if (isTiered)
		{
			char tier = type.charAt(type.length() - 1);
			if (tier == ServerWorld.WOOD_TIER)
			{
				image += "WOOD";
			}
			else if (tier == ServerWorld.STONE_TIER)
			{
				image += "STONE";
			}
			else if (tier == ServerWorld.IRON_TIER)
			{
				image += "IRON";
			}
			else if (tier == ServerWorld.GOLD_TIER)
			{
				image += "GOLD";
			}
			else if (tier == ServerWorld.DIAMOND_TIER)
			{
				image += "DIAMOND";
			}
			setImage(image + "_ICON.png");
		}
		else
		{
			switch (type)
			{
			case ServerWorld.HP_50:
				setImage("HP_POTION.png");
				break;
			case ServerWorld.MONEY_TYPE:
				setImage("MONEY.png");
				break;
			}
		}

		setWidth(Images.getGameImage(getImage()).getWidth());
		setHeight(Images.getGameImage(getImage()).getHeight());

	}

	public static ServerItem randomItem(double x, double y)
	{
		int randType = (int) (Math.random() * NUM_ITEMS + 1);

		switch (randType)
		{
		case 1:
			return new ServerHPPotion(x, y);
		case 2:
			return new ServerSword(x, y, 'L', 20);
		case 3:
			return new ServerMoney(x, y);
		}
		// This won't happen
		return null;

	}

	public static ServerItem copy(ServerItem item)
	{
		switch (item.getType())
		{
		case ServerWorld.HP_50:
			return new ServerHPPotion(item.getX(), item.getY());
		case ServerWorld.LONG_SWORD:
			return new ServerSword(item.getX(), item.getY(), 'L', 20);
		case ServerWorld.MONEY_TYPE:
			return new ServerMoney(item.getX(), item.getY());
		}
		return null;

	}

	public void startCoolDown()
	{
		hasCoolDown = true;

		// Start a timer for when to set cooldown to false
		coolDownTimer.start();

	}

	public void setSource(ServerCreature source)
	{
		this.source = source;
	}

	public boolean hasCoolDown()
	{
		return hasCoolDown;
	}

	public ServerCreature getSource()
	{
		return source;
	}

	private class CoolDownTimer implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			hasCoolDown = false;
			coolDownTimer.stop();
		}

	}

	public void increaseAmount()
	{
		amount++;
	}

	public void increaseAmount(int value)
	{
		amount += value;
	}

	public int getAmount()
	{
		return amount;
	}

	public void setAmount(int amount)
	{
		this.amount = amount;
	}

	public void decreaseAmount()
	{
		amount--;
	}

}
