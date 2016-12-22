package Server.Items;

import java.awt.geom.Line2D;
import java.util.ArrayList;

import Imports.Images;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;

/**
 * Class for displaying weapon swing
 * @author Alex Raita & William Xu
 *
 */
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
	 * Number of pixels difference from the original x position
	 */
	private double relativeX;

	/**
	 * Number of pixels difference from the original y position
	 */
	private double relativeY;

	/**
	 * The one who swung the weapon
	 */
	private ServerCreature owner;

	/**
	 * Constructor for the item swing animation
	 * @param owner the one who swung the weapon
	 * @param image the initial image for the weapon
	 * @param timeInMilliseconds the time that the swinging will last
	 */
	public ServerWeaponSwing(ServerCreature owner, double relativeX,
			double relativeY, String image, int angle,
			int timeInFrames, int damage)
	{
		super(owner.getX(), owner.getY(), -1, -1, 0, image,
				ServerWorld.WEAPON_SWING_TYPE, owner.getWorld().getEngine());

		this.relativeX = relativeX;
		this.relativeY = relativeY;

		this.wielder = owner;
		counter = 0;
		this.timeInFrames = timeInFrames;
		this.owner = owner;
		ownerID = owner.getID();

		objectsCollided = new ArrayList<Integer>();

		setMapVisible(false);
		setSolid(false);

		this.damage = damage;

		if (owner.getDirection().equals("RIGHT"))
		{
			currentAngle = -135;
			isClockwise = true;
		}
		else
		{
			currentAngle = -45;
			isClockwise = false;
		}

		width = Images.getGameImage(image).getWidth();
		height = Images.getGameImage(image).getHeight();

		// Lock the weapon onto the owner
		setX(owner.getX() + owner.getWidth() / 2 - width / 2 + relativeX);
		setY(owner.getY() + owner.getHeight() / 2 - height / 2 + 10 + relativeY);

		// Set the hitbox for the weapon based on the angle
		hitbox = new Line2D.Double(
				getX()
						+ width
						/ 2
						+ ((width / 8) * Math.cos(Math.toRadians(currentAngle))),
				getY()
						+ height
						/ 2
						+ ((width / 8) * Math.sin(Math.toRadians(currentAngle))),
				getX()
						+ width
						/ 2
						+ ((width / 2) * Math.cos(Math.toRadians(currentAngle))),
				getY()
						+ height
						/ 2
						+ ((width / 2) * Math.sin(Math.toRadians(currentAngle))));

		setImage(getBaseImage() + "_" + currentAngle + "");

		int soundNo = (int) (Math.random() * 11);
		owner.getWorld().playSound("cut_air" + soundNo,
				getX() + getWidth() / 2, getY() + getHeight() / 2);

	}

	/**
	 * Update counter for the object
	 */
	public void update()
	{
		// Destroy the weapon when the animation ends
		if (counter >= (int) (timeInFrames / 13.0 + 0.5) * 13)
		{
			destroy();
		}
		// Display a certain animation for the weapon
		else if (counter % (int) (timeInFrames / 13.0 + 0.5) == 0
				&& counter != 0)
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
			setImage(getBaseImage() + "_" + currentAngle + "");
		}

		// Lock the weapon onto the player
		setX(wielder.getX() + wielder.getWidth() / 2 - width / 2 + relativeX);
		setY(wielder.getY() + wielder.getHeight() / 2 - height / 2 + relativeY);

		// Rotate the hitbox for the weapon
		hitbox.setLine(
				getX()
						+ width
						/ 2
						+ ((width / 8) * Math.cos(Math.toRadians(currentAngle))),
				getY()
						+ height
						/ 2
						+ ((width / 8) * Math.sin(Math.toRadians(currentAngle))),
				getX()
						+ width
						/ 2
						+ ((width / 2) * Math.cos(Math.toRadians(currentAngle))),
				getY()
						+ height
						/ 2
						+ ((width / 2) * Math.sin(Math.toRadians(currentAngle))));

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
//		owner.getWorld().playSound("cut", other.getX() + other.getWidth() / 2,
//				other.getY() + other.getHeight() / 2, this,
//				other);
	}

	// ///////////////////////
	// GETTERS AND SETTERS //
	// ///////////////////////

	public ServerCreature getWielder()
	{
		return wielder;
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

	public ServerCreature getOwner()
	{
		return owner;
	}

	public void setOwner(ServerCreature owner)
	{
		this.owner = owner;
	}
}
