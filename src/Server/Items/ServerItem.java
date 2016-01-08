package Server.Items;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import Imports.Images;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;

public abstract class ServerItem extends ServerObject{

	private final static int NUM_ITEMS = 2;
	private boolean hasCoolDown = false;
	private ServerCreature source;
	private Timer coolDownTimer = new Timer(2000,new CoolDownTimer());

	//Amount of this item. Will only be used for potions
	private int amount = 1;

	public ServerItem(double x, double y,String type) {
		super(x, y, 0,0, ServerWorld.GRAVITY, "", type);

		switch (type) {
		case ServerWorld.HP_50:
			setImage("HP_POTION.png");
			break;
		case ServerWorld.LONG_SWORD:
			setImage("SWORD.png");
			break;
		}

		setWidth(Images.getGameImage(getImage()).getWidth());
		setHeight(Images.getGameImage(getImage()).getHeight());

	}

	public static ServerItem randomItem(double x, double y)
	{
		int randType = (int) (Math.random() * NUM_ITEMS + 1);

		switch (randType) {
		case 1:
			return new ServerHPPotion(x, y);
		case 2:
			return new ServerSword(x, y,'L',20);
		}
		// This won't happen
		return null;

	}

	public static ServerItem newItem(double x, double y, String type)
	{
		switch (type) {		
		case ServerWorld.HP_50:
			return new ServerHPPotion(x,y);					
		case ServerWorld.LONG_SWORD:
			return new ServerSword(x,y,'L',20);
		}
		return null;

	}
	public void startCoolDown ()
	{
		hasCoolDown = true;

		//Start a timer for when to set cooldown to false
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

	public ServerCreature getSource() {
		return source;
	}

	private class CoolDownTimer implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			hasCoolDown = false;
			coolDownTimer.stop();
		}

	}

	public void increaseAmount()
	{
		amount++;
	}

	public int getAmount()
	{
		return amount;
	}

	public void decreaseAmount()
	{
		amount--;
	}

}
