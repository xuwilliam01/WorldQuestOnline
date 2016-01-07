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

	public ServerItem(double x, double y,String type) {
		super(x, y, 0,0, ServerWorld.GRAVITY, "", type);

		switch (type) {
		case ServerWorld.HP_25:
			setImage("HP_POTION.png");
			break;
		case ServerWorld.HP_50:
			setImage("HP_POTION.png");
			break;
		case ServerWorld.HP_75:
			setImage("HP_POTION.png");
			break;
		case ServerWorld.HP_100:
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

	public void startCoolDown ()
	{
		hasCoolDown = true;

		//Start a timer for when to set cooldown to false
		System.out.println("start");
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
			System.out.println("working");
			hasCoolDown = false;
			coolDownTimer.stop();
		}
		
	}
	
}
