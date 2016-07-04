package Server.Items;

import java.awt.geom.Line2D;
import java.util.ArrayList;

import Imports.Images;
import Server.ServerFlyingObject;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;

/**
 * Class for projectiles
 * @author Alex Raita & William Xu
 *
 */
public class ServerProjectile extends ServerFlyingObject
{
	/**
	 * The default damage for a bullet
	 */
	public static final int BULLET_DAMAGE = 5;

	/**
	 * Counter
	 */
	private int counter = 0;

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
	 * Whether or not the image should rotate to face the angle
	 */
	private boolean faceAngle;

	/**
	 * Whether or not the projectile is animated
	 */
	private boolean animated;

	/**
	 * The frame of animation for the projectile
	 */
	private int animationCounter;

	/**
	 * Number of frames the explosion lasts
	 */
	private int noOfExplosionFrames;

	/**
	 * Number of frames for the animation
	 */
	private int animationFrames = 2;

	/**
	 * The ID's of the objects that have already collided with this projectile
	 */
	private ArrayList<Integer> objectsCollided;

	/**
	 * Constructor for a projectile
	 */
	public ServerProjectile(double x, double y, ServerCreature owner,
			double angle, String type)
	{
		super(x, y, 0, 0, 0, "", 0, angle, type);
		this.owner = owner;
		ownerID = owner.getID();

		setAngle(angle, true);

		objectsCollided = new ArrayList<Integer>();

		switch (type)
		{
		case ServerWorld.NINJASTAR_TYPE:
			if (Math.toDegrees(angle) >= 90 && Math.toDegrees(angle) < 90)
			{
				setImage("STAR0_0");
			}
			else
			{
				setImage("STAR1_0");
			}
			setGravity(0);
			setDamage(ServerWeapon.STAR_DMG);
			setSpeed(7);
			animated = true;
			animationFrames = 4;
			faceAngle = false;
			break;
		case ServerWorld.WOODARROW_TYPE:
			setImage("WOODARROW_0");
			setGravity(0.25);
			setDamage((int)Math.ceil(ServerWeapon.WOODBOW_DMG * (1+owner.getBaseDamage()/100.0)));
			setSpeed(20);
			animated = false;
			faceAngle = true;
			break;
		case ServerWorld.STEELARROW_TYPE:
			setImage("STEELARROW_0");
			setGravity(0.25);
			setDamage((int)Math.ceil(ServerWeapon.STEELBOW_DMG * (1+owner.getBaseDamage()/100.0)));
			setSpeed(20);
			animated = false;
			faceAngle = true;
			break;
		case ServerWorld.MEGAARROW_TYPE:
			setImage("MEGAARROW_0");
			setGravity(0);
			setDamage((int)Math.ceil(ServerWeapon.MEGABOW_DMG * (1+owner.getBaseDamage()/100.0)));
			setSpeed(30);
			animated = false;
			faceAngle = true;
			break;
		case ServerWorld.BULLET_TYPE:
			setImage("BULLET_0");
			setGravity(0.4);
			setDamage((int)Math.ceil(ServerWeapon.SLING_DMG * (1+owner.getBaseDamage()/100.0)));
			setSpeed(15);
			animated = false;
			faceAngle = false;
			break;
		case ServerWorld.FIREBALL_TYPE:
			setImage("FIREBALL_0_0");
			setGravity(0);
			setDamage((int)Math.ceil(ServerWeapon.FIREWAND_DMG * (1+owner.getBaseDamage()/100.0)));
			setSpeed(15);
			animated = true;
			faceAngle = true;
			break;
		case ServerWorld.ICEBALL_TYPE:
			setImage("ICEBALL_0_0");
			setGravity(0);
			setDamage((int)Math.ceil(ServerWeapon.ICEWAND_DMG * (1+owner.getBaseDamage()/100.0)));
			setSpeed(6);
			animated = true;
			faceAngle = true;
			break;
		case ServerWorld.DARKBALL_TYPE:
			setImage("DARKBALL_0_0");
			setGravity(0);
			setDamage((int)Math.ceil(ServerWeapon.ICEWAND_DMG * (1+owner.getBaseDamage()/100.0)));
			setSpeed(12);
			animated = true;
			faceAngle = true;
			break;
		}
		if (faceAngle)
		{
			length = Images.getGameImage(getImage()).getWidth();
			hitbox = new Line2D.Double(getX(), getY(), getX()
					+ ((length / 2) * Math.cos(getAngle())), getY()
					+ ((length / 2) * Math.sin(getAngle())));
		}
	}

	/**
	 * Update the projectile every game tick
	 */
	public void update()
	{
		int imageAngle = 0;
		if (faceAngle)

		{
			setAngle(Math.atan2(getVSpeed(), getHSpeed()), false);

			hitbox.setLine(getX(), getY(),
					getX() + ((length / 2) * Math.cos(getAngle())), getY()
							+ ((length / 2) * Math.sin(getAngle())));

			imageAngle = (int) (Math
					.round(Math.toDegrees(getAngle()) / 15.0) * 15);

			if (imageAngle <= -180)
			{
				imageAngle = 180 - (-180 - imageAngle);
			}
		}

		if (animated)
		{
			animationCounter++;

			int imageNo = 0;

			if (animationFrames == 2)
			{
				if (animationCounter >= 10)
				{
					animationCounter = 0;
				}

				if (animationCounter >= 5)
				{
					imageNo = 1;
				}
			}
			else if (animationFrames ==4)
			{
				if (animationCounter >= 12)
				{
					animationCounter = 0;
				}

				if (animationCounter < 3)
				{
					imageNo = 1;
				}
				else if (animationCounter<6)
				{
					imageNo = 2;
				}
				else if (animationCounter <9)
				{
					imageNo = 3;
				}
			}
			if (faceAngle)
			{
				setImage(getBaseImage() + "_" + imageNo + "_" + imageAngle
						+ "");
			}
			else
			{
				setImage(getBaseImage() + "_" + imageNo + "");
			}
		}
		else
		{
			if (faceAngle)
			{
				setImage(getBaseImage() + "_" + imageAngle + "");
			}
		}
		
		// Update the projectile's counter
		counter++;
		if (counter >= (2400/getSpeed()))
		{
			destroy();
		}
	}

	/**
	 * Check whether or not the other object collides with the hitbox (while
	 * also remaking it)
	 * @return
	 */
	public boolean collidesWith(ServerObject other)
	{
		if (faceAngle)
		{
			return hitbox.intersects(other.getX(), other.getY(),
					other.getWidth(),
					other.getHeight());
		}
		return super.collidesWith(other);
	}

	/**
	 * Checks whether the other object has already collided with this weapon
	 * @param other
	 * @return
	 */
	public boolean hasCollided(ServerObject other)
	{
		return objectsCollided.contains(other.getID());
	}

	/**
	 * Adds the other object to the list of objects that have already collided
	 * with this weapon swing
	 * @param other
	 * @return
	 */
	public void addCollided(ServerObject other)
	{
		if (objectsCollided.size()<3)
		{
			objectsCollided.add(other.getID());
		}
		else
		{
			destroy();
		}
	}

	/**
	 * Destroy the projectile and possibly set an explosion
	 */
	@Override
	public void destroy()
	{
		if (getType() == ServerWorld.BULLET_TYPE)
		{
			setType(ServerWorld.EXPLOSION_TYPE);
			setSpeed(0);
			setSolid(false);
			setMapVisible(false);
			counter = 0;
			setWidth(32);
			setHeight(32);
			setX(getX() - 16);
			setY(getY() - 16);
			setImage("EXPLOSION0_0");
			noOfExplosionFrames = 7;
		}
		else if (getType() == ServerWorld.FIREBALL_TYPE)
		{
			setType(ServerWorld.EXPLOSION_TYPE);
			setSpeed(0);
			setSolid(false);
			setMapVisible(false);
			counter = 0;
			setWidth(64);
			setHeight(64);
			setX(getX() - 32);
			setY(getY() - 32);
			setImage("EXPLOSION2_0");
			noOfExplosionFrames = 5;
		}
		else if (getType() == ServerWorld.ICEBALL_TYPE)
		{
			setType(ServerWorld.EXPLOSION_TYPE);
			setSpeed(0);
			setSolid(false);
			setMapVisible(false);
			counter = 0;
			setWidth(82);
			setHeight(82);
			setX(getX() - 41);
			setY(getY() - 41);
			setImage("EXPLOSION3_0");
			noOfExplosionFrames = 4;
		}
		else if (getType() == ServerWorld.MEGAARROW_TYPE)
		{
			setType(ServerWorld.EXPLOSION_TYPE);
			setSpeed(0);
			setSolid(false);
			setMapVisible(false);
			counter = 0;
			setWidth(82);
			setHeight(82);
			setX(getX() - 41);
			setY(getY() - 41);
			setImage("EXPLOSION4_0");
			noOfExplosionFrames = 4;
		}
		else if (getType() == ServerWorld.DARKBALL_TYPE)
		{
			setType(ServerWorld.EXPLOSION_TYPE);
			setSpeed(0);
			setSolid(false);
			setMapVisible(false);
			counter = 0;
			setWidth(100);
			setHeight(100);
			setX(getX() - 50);
			setY(getY() - 50);
			setImage("EXPLOSION1_0");
			noOfExplosionFrames = 10;
		}
		else
		{
			super.destroy();
		}
	}

	/**
	 * World timer keeps calling this method after exploding
	 */
	public void updateExplosion()
	{
		// Update the explosion animation
		if (counter <= 4)
		{
			setImage(getBaseImage() + "_1");
		}
		else if (counter <= 6 && noOfExplosionFrames >= 3)
		{
			setImage(getBaseImage() + "_2");
		}
		else if (counter <= 8 && noOfExplosionFrames >= 4)
		{
			setImage(getBaseImage() + "_3");
		}
		else if (counter <= 10 && noOfExplosionFrames >= 5)
		{
			setImage(getBaseImage() + "_4");
		}
		else if (counter <= 12 && noOfExplosionFrames >= 6)
		{
			setImage(getBaseImage() + "_5");
		}
		else if (counter <= 14 && noOfExplosionFrames >= 7)
		{
			setImage(getBaseImage() + "_6");
		}
		else if (counter <= 16 && noOfExplosionFrames >= 8)
		{
			setImage(getBaseImage() + "_7");
		}
		else if (counter <= 18 && noOfExplosionFrames >= 9)
		{
			setImage(getBaseImage() + "_8");
		}
		else if (counter <= 20 && noOfExplosionFrames >= 10)
		{
			setImage(getBaseImage() + "_9");
		}
		else
		{
			super.destroy();
		}

		counter++;

	}

	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////
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
