package Server.Projectile;

import java.awt.geom.Line2D;

import Imports.Images;
import Server.ServerFlyingObject;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;

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
	 * A reference to the owner
	 */
	private ServerCreature owner;

	/**
	 * The object that shot the projectile
	 */
	private int ownerID;

	/**
	 * The knockback force of the projectile (change later)
	 */
	private double knockBack = 10;

	/**
	 * The hitbox line for the projectile
	 */
	private Line2D.Double hitbox;

	/**
	 * The length of the projectile
	 */
	private int length;

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
	public ServerProjectile(double x, double y, ServerCreature owner, double angle, String type)
	{
		super(x, y, 0, 0, 0, "", 0, angle, type);
		this.owner = owner;
		ownerID = owner.getID();
	
		
		
		setAngle(angle,true);

		switch (type)
		{
		case ServerWorld.ARROW_TYPE:
			setImage("ARROW_0.png");
			setGravity(0.3);
			setDamage(5);
			setSpeed(20);
			break;
		case ServerWorld.FIREBALL_TYPE:
			setImage("FIREBALL_0.png");
			setGravity(0);
			setDamage(10);
			setSpeed(15);
			break;
		}

		length = Images.getGameImage(getImage()).getWidth();

		hitbox = new Line2D.Double(getX()
				- ((length / 2) * Math.cos(getAngle())), getY()
				- ((length / 2) * Math.sin(getAngle())), getX()
				+ ((length / 2) * Math.cos(getAngle())), getY()
				+ ((length / 2) * Math.sin(getAngle())));
	}

	/**
	 * Update the projectile
	 */
	public void update()
	{
		setAngle(Math.atan2(getVSpeed(), getHSpeed()), false);
		
		hitbox.setLine(getX()
				- ((length / 2) * Math.cos(getAngle())), getY()
				- ((length / 2) * Math.sin(getAngle())), getX()
				+ ((length / 2) * Math.cos(getAngle())), getY()
				+ ((length / 2) * Math.sin(getAngle())));
		

		int imageAngle = (int) (Math.round(Math.toDegrees(getAngle()) / 15.0) * 15);
		
		if (imageAngle <= -180)
		{
			imageAngle = 180 - (-180 - imageAngle); 
		}
		
		setImage(getBaseImage() + "_" + imageAngle + ".png");
		
	}

	/**
	 * Check whether or not the other object collides with the hitbox (while
	 * also remaking it)
	 * @return
	 */
	public boolean collidesWith(ServerObject other)
	{
		return hitbox.intersects(other.getX(), other.getY(), other.getWidth(),
				other.getHeight());
	}

	/**
	 * Destroy the projectile
	 */
	public void destroy()
	{
		if (getType() == ServerWorld.FIREBALL_TYPE)
		{
			setType(ServerWorld.EXPLOSION_TYPE);
			setSpeed(0);
			setSolid(false);
			setMapVisible(false);
			damage = 0;
			counter = 0;
			setX(getX() - 5);
			setY(getY() - 5);
			setImage("EXPLOSION2_0.png");
		}
		super.destroy();

	}

	/**
	 * World timer keeps calling this method after exploding
	 */
	public void updateExplosion()
	{
		if (counter <= 4)
		{
			setImage(getBaseImage() + "_1.png");
		}
		else if (counter <= 6)
		{
			setImage(getBaseImage() + "_2.png");
		}
		else if (counter <= 8)
		{
			setImage(getBaseImage() + "_3.png");
		}
		else if (counter <= 10)
		{
			setImage(getBaseImage() + "_4.png");
		}
		else if (counter <= 12)
		{
			setImage(getBaseImage() + "_5.png");
		}
		else if (counter <= 14)
		{
			setImage(getBaseImage() + "_6.png");
		}
		else if (counter <= 16)
		{
			setImage(getBaseImage() + "_7.png");
		}
		else
		{
			super.destroy();
		}
		
		
		counter++;

	}

	public int getDrawX()
	{
		return (int) (getX() - length / 2);
	}

	public int getDrawY()
	{
		return (int) (getY() - length / 2);
	}

	public int getDamage()
	{
		return damage;
	}

	public void setDamage(int damage)
	{
		this.damage = damage;
	}

	public ServerCreature getOwner()
	{
		return owner;
	}

	public void setOwner(ServerCreature owner)
	{
		this.owner = owner;
	}

	public int getOwnerID()
	{
		return ownerID;
	}

	public void setOwnerID(int ownerID)
	{
		this.ownerID = ownerID;
	}

	public double getKnockBack()
	{
		return knockBack;
	}

	public void setKnockBack(double knockBack)
	{
		this.knockBack = knockBack;
	}

}
