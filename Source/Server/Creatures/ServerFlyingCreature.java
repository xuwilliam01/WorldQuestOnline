package Server.Creatures;

import Server.ServerWorld;

/**
 * A more advanced object with absolute speed and angle of movement
 * @author William Xu && Alex Raita
 *
 */
public abstract class ServerFlyingCreature extends ServerCreature
{
	/**
	 * Absolute speed of the object in pixels per frame
	 */
	private double speed;

	/**
	 * Angle in RADIANS
	 */
	private double angle;

	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param relativeDrawX
	 * @param relativeDrawY
	 * @param gravity
	 * @param image
	 * @param type
	 * @param maxHP
	 * @param world
	 * @param attackable
	 * @param speed
	 * @param angle
	 */
	public ServerFlyingCreature(double x, double y, int width, int height,
			double relativeDrawX, double relativeDrawY,
			double gravity, String image, String type,
			int maxHP, ServerWorld world, boolean attackable,
			double speed, double angle)
	{
		super(x, y, width, height, relativeDrawX, relativeDrawY, gravity,
				image, type, maxHP, world, attackable);
		setHSpeed((Math.round(speed * Math.cos(angle) * 100)) / 100);
		setVSpeed((Math.round(speed * Math.sin(angle) * 100)) / 100);
	}

	/**
	 * Get the absolute speed of the flying object
	 * @return
	 */
	public double getSpeed()
	{
		return speed;
	}

	/**
	 * Set the speed of the object and update the horizontal and vertical speeds
	 * @param speed
	 */
	public void setSpeed(double speed)
	{
		this.speed = speed;
		setHSpeed((speed * Math.cos(angle)));
		setVSpeed((speed * Math.sin(angle)));
	}

	/**
	 * Get the angle in radians of the object
	 * @return
	 */
	public double getAngle()
	{
		return angle;
	}

	/**
	 * Set the angle of the object and update the horizontal and vertical speeds
	 * @param angle
	 */
	public void setAngle(double angle, boolean updateSpeeds)
	{
		this.angle = angle;
		if (updateSpeeds)
		{
			setHSpeed((speed * Math.cos(angle)));
			setVSpeed((speed * Math.sin(angle)));
		}
	}


}
