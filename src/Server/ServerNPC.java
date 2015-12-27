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
public abstract class ServerNPC extends ServerObject implements ActionListener{
	
	/**
	 * Timer for the NPC in server frames
	 */
	private int counter;
	
	
	/**
	 * The range for the A.I. to follow the player. Change later
	 */
	private int targetRange = 500;
	
	//Max hp and current hp
	private int maxHP;
	private int HP;
	
	//Players in server
	private ServerPlayer target = null;
	
	//World that the object is in
	private ServerWorld world;
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param gravity
	 * @param ID
	 * @param image
	 * @param maxHP
	 * @param world
	 */
	public ServerNPC(double x, double y, int width, int height, double gravity, int ID, String image, int maxHP, String type, ServerWorld world) {
		super(x, y, width, height, gravity,ID, image,type);
		this.maxHP = maxHP;
		HP = maxHP;
		this.world = world;
	}

	//All this should be overridden by other AI classes
	/**
	 * Moves the AI and makes decisions for it, i.e. whether to attack or not
	 */
	public void update()
	{
		counter++;
	}
	
	public void findTarget()
	{
		for(ServerPlayer player : ServerEngine.getListOfPlayers())
		{
			if(player.isAlive() && findDistanceBetween(player) <= targetRange)
			{
				setTarget(player);
				break;
			}
		}
	}
	
	public void dropItem()
	{
		world.add(ServerItem.randomItem(getX(), getY()));
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
		update();
	}

	public int getMaxHP()
	{
		return maxHP;
	}
	
	public int getHP()
	{
		return HP;
	}
	
	/**
	 * Inflict a certain amount of damage to the npc and destroy if less than 0 hp
	 * @param amount
	 */
	public void inflictDamage(int amount)
	{
		HP -= amount;
		if (HP <= 0)
		{
			destroy();
			dropItem();
		}
	}
}
