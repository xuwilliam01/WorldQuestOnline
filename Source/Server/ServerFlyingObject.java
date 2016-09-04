package Server;

/**
 * A more advanced object with absolute speed and angle of movement
 * @author William Xu && Alex Raita
 *
 */
public abstract class ServerFlyingObject extends ServerObject
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
	 * Constructor for flying object
	 * @param x the x coordinate
	 * @param y the y coordinate 
	 * @param width the width of the object
	 * @param height the height of the object
	 * @param ID the identifier of the object
	 * @param image the image
	 * @param speed the speed of the object towards its angle
	 * @param angle the angle the object is moving towards
	 */
	public ServerFlyingObject(double x, double y, int width, int height,
			double gravity, String image, double speed, double angle,
			String type, ServerEngine engine)
	{
		super(x, y, width, height, gravity, image, type, engine);
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
