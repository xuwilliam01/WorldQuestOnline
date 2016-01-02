package Server.Projectile;

import Server.ServerFlyingObject;
import Server.ServerWorld;

public class ServerProjectile extends ServerFlyingObject
{
	/**
	 * The default damage for a bullet
	 */
	public static final int BULLET_DAMAGE = 5;
	
	/**
	 * Counter
	 */
	private int counter;
	
	/**
	 * The damage this projectile does
	 */
	private int damage;
	
	/**
	 * The object that shot the projectile
	 */
	private int ownerID;
	
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
	public ServerProjectile(double x, double y, int width, int height, double gravity, int ownerID, String image,double speed, double angle, double inaccuracy, String type)
	{
		super (x,y,width,height,gravity,image, speed, angle+ Math.random()*(inaccuracy) - inaccuracy/2,type);
		this.ownerID = ownerID;
		
		if (type.equals(ServerWorld.BULLET_TYPE))
		{
			damage = BULLET_DAMAGE;
		}
		
	}
	
	/**
	 * Destroy the projectile
	 */
	public void destroy()
	{
		setType(ServerWorld.EXPLOSION_TYPE + "");
		setSpeed(0);
		setSolid(false);
		setMapVisible(false);
		damage = 0;
		counter = 0;
	}
	
	/**
	 * World timer keeps calling this method after exploding
	 */
	public void updateExplosion()
	{
		if (counter<=2)
		{
			setImage("EXPLOSION_0.png");
		}
		else if (counter<= 4)
		{
			setImage("EXPLOSION_1.png");
		}
		else if (counter<= 6)
		{
			setImage("EXPLOSION_2.png");
		}
		else if (counter<= 8)
		{
			setImage("EXPLOSION_3.png");
		}
		else if (counter<= 10)
		{
			setImage("EXPLOSION_4.png");
		}
		else if (counter<= 12)
		{
			setImage("EXPLOSION_5.png");
		}
		else if (counter<= 14)
		{
			setImage("EXPLOSION_6.png");
		}
		else
		{
			super.destroy();
		}
		counter++;
		
	}

	public int getDamage()
	{
		return damage;
	}

	public void setDamage(int damage)
	{
		this.damage = damage;
	}

	public int getOwnerID()
	{
		return ownerID;
	}

	public void setOwnerID(int ownerID)
	{
		this.ownerID = ownerID;
	}
	
}
