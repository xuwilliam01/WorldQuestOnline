package Server;

public class ServerProjectile extends ServerFlyingObject
{

	/**
	 * Constructor for a projectile
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param gravity
	 * @param ID
	 * @param image
	 * @param speed
	 * @param angle
	 */
	public ServerProjectile(double x, double y, int width, int height, double gravity, int ID, String image,double speed, double angle, double inaccuracy, String type)
	{
		super (x,y,width,height,gravity,ID,image, speed, angle+ Math.random()*(inaccuracy) - inaccuracy/2,type);
	}
}
