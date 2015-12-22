package Server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Timer;

/**
 * Class to control enemy AIs
 * @author Alex Raita & William Xu
 *
 */
public class EnemyAI extends ServerObject implements ActionListener{

	private Timer movementTimer;
	private boolean right = true;
	private int counter = 0;
	private Random random;
	
	//Max hp and current hp
	private int maxHP;
	private int HP;
	
	
	public EnemyAI(int x, int y, int width, int height, int ID, String image, int maxHP) {
		super(x, y, width, height, ID, image);
		movementTimer = new Timer(Engine.UPDATE_RATE,this);
		movementTimer.start();
		random = new Random();
		this.maxHP = maxHP;
		HP = maxHP;
	}

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
