package Server.Effects;

import Server.ServerWorld;

/**
 * A small floating number indicating the damage dealt in an attack
 * @author William
 *
 */
public class ServerDamageIndicator extends ServerText
{
	/**
	 * Constructor for a damage indicator
	 * @param x
	 * @param y
	 * @param text
	 * @param colour
	 */
	public ServerDamageIndicator(double x, double y, String text, char colour, ServerWorld world)
	{
		super(x, y,0, text, colour, ServerWorld.DAMAGE_INDICATOR_TYPE,world,45);
		setVSpeed(-0.5);
		this.world = world;
	}
	
}
