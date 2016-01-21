package Effects;

import Server.ServerWorld;

/**
 * A small floating number indicating the damage dealt in an attack
 * @author William
 *
 */
public class ServerDamageIndicator extends ServerText
{
	public final static char RED_TEXT = 'r';
	public final static char YELLOW_TEXT = 'y';
	public final static char PURPLE_TEXT = 'p';
	
	/**
	 * How long the damage indicator lasts for
	 */
	private int framesAlive = 45;
	
	/**
	 * The world
	 */
	private ServerWorld world;
	
	/**
	 * The time the Damage indicator started
	 */
	private long startCounter;
	
	/**
	 * Constructor for a damage indicator
	 * @param x
	 * @param y
	 * @param text
	 * @param colour
	 */
	public ServerDamageIndicator(double x, double y, String text, char colour, ServerWorld world)
	{
		super(x, y,0, text, colour, ServerWorld.DAMAGE_INDICATOR_TYPE);
		setVSpeed(-0.5);
		this.world = world;
		startCounter = world.getWorldCounter();
	}
	
	/**
	 * Waiting for the moment it disappears
	 */
	public void update()
	{
		if (world.getWorldCounter()-startCounter >= framesAlive)
		{
			destroy();
		}
	}
	
	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////
	public int getFramesAlive()
	{
		return framesAlive;
	}
	public void setFramesAlive(int framesAlive)
	{
		this.framesAlive = framesAlive;
	}
}
