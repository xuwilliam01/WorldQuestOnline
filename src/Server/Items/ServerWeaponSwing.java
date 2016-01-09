package Server.Items;

import java.awt.geom.Line2D;
import java.util.ArrayList;

import Imports.Images;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;

public class ServerWeaponSwing extends ServerObject
{

	/**
	 * Counter for timing update
	 */
	private int counter;

	/**
	 * The current angle in degrees
	 */
	private int currentAngle;

	/**
	 * Whether the sword swings clockwise or counterclockwise
	 */
	private boolean isClockwise;

	/**
	 * The time in server frames the animation will last
	 */
	private int timeInFrames;

	/**
	 * The object that this animation will centre on
	 */
	private ServerCreature wielder;

	/**
	 * The width of the initial image before rotation
	 */
	private int width;

	/**
	 * The height of the initial image before rotation
	 */
	private int height;

	/**
	 * The hitbox line for the weapon
	 */
	private Line2D.Double hitbox;
	
	/**
	 * The id of the wielder
	 */
	private int ownerID;

	/**
	 * The amount of damage the weapon does
	 */
	private int damage;

	/**
	 * The change in vSpeed and hSpeed of the other object when it collides with
	 * this
	 */
	private double knockBack;

	/**
	 * The ID's of the objects that have already collided with this weapon swing
	 */
	private ArrayList<Integer> objectsCollided;

	/**
	 * Constructor for the item swing animation
	 * @param wielder
	 * @param image
	 * @param timeInMilliseconds
	 */
	public ServerWeaponSwing(ServerCreature wielder, String image, int angle,
			int timeInFrames, int damage, double knockBack)
	{
		super(wielder.getX(), wielder.getY(), -1, -1, 0, image,
				ServerWorld.WEAPON_SWING_TYPE);
		this.wielder = wielder;
		counter = 0;
		this.timeInFrames = timeInFrames;
		ownerID = wielder.getID();
		this.knockBack = knockBack;

		objectsCollided = new ArrayList<Integer>();

		setMapVisible(false);
		setSolid(false);

		this.damage = damage;

		if (angle > -90 && angle <= 90)
		{
			currentAngle = -120;
			isClockwise = true;
		}
		else
		{
			currentAngle = -60;
			isClockwise = false;
		}

		width = Images.getGameImage(image).getWidth();
		height = Images.getGameImage(image).getHeight();

		setX(wielder.getX() + wielder.getWidth() / 2 - width / 2);
		setY(wielder.getY() + wielder.getHeight() / 2 - height / 2 + 10);

		hitbox = new Line2D.Double(
				getX() + width
						/ 2
						+ ((width / 6) * Math.cos(Math.toRadians(currentAngle))),
				getY() + height
						/ 2
						+ ((width / 6) * Math.sin(Math.toRadians(currentAngle))),
				getX() + width
						/ 2
						+ ((width / 2) * Math.cos(Math.toRadians(currentAngle))),
				getY() + height
						/ 2
						+ ((width / 2) * Math.sin(Math.toRadians(currentAngle))));

		setImage(getBaseImage() + "_" + currentAngle + ".png");

	}

	public ServerCreature getWielder()
	{
		return wielder;
	}
	
	/**
	 * Update counter for the object
	 */
	public void update()
	{
		if (counter >= (int) (timeInFrames / 11.0 + 1) * 11)
		{
			destroy();
		}
		else if (counter % (int) (timeInFrames / 11.0 + 1) == 0 && counter != 0)
		{
			if (isClockwise)
			{
				currentAngle += 15;
				if (currentAngle > 180)
				{
					currentAngle = -180 - (180 - currentAngle);
				}
			}
			else
			{
				currentAngle -= 15;
				if (currentAngle <= -180)
				{
					currentAngle = 180 - (-180 - currentAngle);
				}
			}
			setImage(getBaseImage() + "_" + currentAngle + ".png");
		}

		setX(wielder.getX() + wielder.getWidth() / 2 - width / 2);
		setY(wielder.getY() + wielder.getHeight() / 2 - height / 2 + 10);
		hitbox.setLine(
				getX()
						+ width
						/ 2
						+ ((width / 6) * Math.cos(Math.toRadians(currentAngle))),
				getY()
						+ height
						/ 2
						+ ((width / 6) * Math.sin(Math.toRadians(currentAngle))),
				getX()
						+ width
						/ 2
						+ ((width / 2) * Math.cos(Math.toRadians(currentAngle))),
				getY()
						+ height
						/ 2
						+ ((width / 2) * Math.sin(Math.toRadians(currentAngle))));

		// System.out.println(hitbox.getX1() + " " + hitbox.getX2() + " " +
		// hitbox.getY1() + " " + hitbox.getY2());

		counter++;
	}

	/**
	 * Check for a collision between the weapon and a rectangular object
	 * @param other
	 * @return whether or not the two objects are colliding
	 */
	public boolean collidesWith(ServerObject other)
	{
		return hitbox.intersects(other.getX(), other.getY(), other.getWidth(),
				other.getHeight());
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
		objectsCollided.add(other.getID());
	}

	public int getDamage()
	{
		return damage;
	}

	public void setDamage(int damage)
	{
		this.damage = damage;
	}

	public boolean isClockwise()
	{
		return isClockwise;
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

	public Line2D.Double getHitbox()
	{
		return hitbox;
	}

	public void setHitbox(Line2D.Double hitbox)
	{
		this.hitbox = hitbox;
	}

}
