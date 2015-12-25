package Server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;

/**
 * Class to control enemy AIs
 * @author Alex Raita & William Xu
 *
 */
public class ServerNPC extends ServerObject implements ActionListener{

	private Timer movementTimer;
	private boolean right = true;
	private int counter = 0;
	private Random random;
	
	
	/**
	 * The range for the A.I. to follow the player. Change later
	 */
	private int targetRange = 1000;
	
	//Max hp and current hp
	private int maxHP;
	private int HP;
	
	//Players in server
	private ArrayList<ServerPlayer> players;
	private ServerPlayer target = null;
	
	public ServerNPC(double x, double y, int width, int height, double gravity, int ID, String image, int maxHP) {
		super(x, y, width, height, gravity,ID, image);
		movementTimer = new Timer(ServerEngine.UPDATE_RATE,this);
		movementTimer.start();
		random = new Random();
		this.maxHP = maxHP;
		HP = maxHP;
	}

	//All this should be overridden by other AI classes
	/**
	 * Moves the AI and makes decisions for it, i.e. whether to attack or not
	 */
	public void move()
	{
		if(right)
			setHSpeed(5);
		else
			setHSpeed(-5);
		counter++;
		
		if(counter  % 100 == 0 )
		{
			right = random.nextBoolean();
		}
	}
	
	public void findTarget()
	{
		for(ServerPlayer player : ServerEngine.getListOfPlayers())
		{
			if(player.getHP() > 0 && findDistanceBetween(player) <= targetRange)
			{
				setTarget(player);
				break;
			}
		}
	}
	
	
	
	public int getTargetRange()
	{
		return targetRange;
	}

	public void setTargetRange(int targetRange)
	{
		this.targetRange = targetRange;
	}

	public ServerPlayer getTarget()
	{
		return target;
	}
	
	public void setTarget(ServerPlayer target)
	{
		this.target = target;
	}
	
	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public void actionPerformed(ActionEvent arg0) {
		move();
	}

	public int getMaxHP()
	{
		return maxHP;
	}
	
	public int getHP()
	{
		return HP;
	}
	
	public void addDamage(int amount)
	{
		HP -= amount;
	}
}
