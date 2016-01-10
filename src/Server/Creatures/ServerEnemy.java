package Server.Creatures;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Server.ServerEngine;
import Server.ServerWorld;

/**
 * Class to control enemy AIs
 * @author Alex Raita & William Xu
 *
 */
public abstract class ServerEnemy extends ServerCreature implements ActionListener{
	
	/**
	 * Timer for the NPC in server frames
	 */
	private int counter;
	
	/**
	 * The range for the A.I. to follow the player. Change later
	 */
	private int targetRange = 500;
	
	//Players in server
	private ServerPlayer target = null;
	
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
	public ServerEnemy(double x, double y, int width, int height, double relativeDrawX, double relativeDrawY,double gravity, String image, int maxHP, String type, ServerWorld world, int team) {
		super(x, y, width, height, relativeDrawX, relativeDrawY,gravity, image,type, maxHP, world,true);
		setTeam(team);
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
			if(player.isAlive() && inRange(player,targetRange))
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
		update();
	}
}
