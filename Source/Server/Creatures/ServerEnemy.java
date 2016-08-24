package Server.Creatures;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Server.ServerEngine;
import Server.ServerWorld;
import Server.Spawners.ServerSpawner;

/**
 * Class to control enemy AIs
 * @author Alex Raita & William Xu
 *
 */
public abstract class ServerEnemy extends ServerCreature implements
		ActionListener
{

	/**
	 * Timer for the NPC in server frames
	 */
	private int counter;

	/**
	 * The range for the A.I. to follow the player. Change later
	 */
	private int targetRange = 500;


	private ServerPlayer target = null;

	/**
	 * The damage the enemy inflicts
	 */
	private int damage;
	
	/**
	 * Spawner for this creature
	 */
	private ServerSpawner spawner;

	/**
	 * Constructor
	 */
	public ServerEnemy(double x, double y, int width, int height,
			double relativeDrawX, double relativeDrawY, double gravity,
			String image, int maxHP, String type, ServerWorld world, int team)
	{
		super(x, y, width, height, relativeDrawX, relativeDrawY, gravity,
				image, type, maxHP, world, true);
		setTeam(team);
	}

	/**
	 * Moves the AI and makes decisions for it, i.e. whether to attack or not
	 */
	public void update()
	{
		counter++;
	}

	/**
	 * Find the nearest player and target it, if within range
	 */
	public void findTarget()
	{
		for (ServerPlayer player : ServerEngine.getListOfPlayers())
		{
			if (player.isAlive() && quickInRange(player, targetRange))
			{
				setTarget(player);
				break;
			}
		}
	}

	
	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////
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
	public int getCounter()
	{
		return counter;
	}
	public void setCounter(int counter)
	{
		this.counter = counter;
	}
	public void actionPerformed(ActionEvent arg0)
	{
		update();
	}
	public int getDamage()
	{
		return damage;
	}
	public void setDamage(int damage)
	{
		this.damage = damage;
	}

	public ServerSpawner getSpawner()
	{
		return spawner;
	}

	public void setSpawner(ServerSpawner spawner)
	{
		this.spawner = spawner;
	}
	
}
