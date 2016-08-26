package Server.Creatures;

import Server.ServerObject;
import Server.ServerWorld;
import Server.Items.ServerPotion;
import Server.Items.ServerItem;
import Server.Spawners.ServerBatSpawner;
import Server.Spawners.ServerSlimeSpawner;
import Server.Spawners.ServerSpawner;

/**
 * A bat enemy
 * @author Alex Raita & William Xu
 *
 */
public class ServerBat extends ServerEnemy
{

	/**
	 * The default HP of a bat
	 */
	public final static int BAT_HP = 35;

	/**
	 * Flying speed of the bat
	 */
	private double speed;

	/**
	 * The counter keeping track of when the bat will next change direction
	 */
	private int changeDirectionCounter = 0;

	/**
	 * Angle in RADIANS
	 */
	private double angle;

	/**
	 * 
	 */
	private boolean atRest = false;

	/**
	 * 
	 * @param x
	 * @param y
	 * @param world
	 */
	public ServerBat(double x, double y, ServerWorld world)
	{
		super(x, y, -1, -1, 0, 0, 0, "BATG_RIGHT_0",
				BAT_HP,
				ServerWorld.BAT_TYPE, world, ServerPlayer.NEUTRAL);

		// Set a random counter to start so not every bat does the exact same
		// thing
		setCounter((int) (Math.random() * 200));

		// Set a random direction
		if ((int) (Math.random() * 2) == 0)
		{
			setDirection("RIGHT");
		}

		setDamage((int) (Math.random() * 2) + 2);
		speed = (int) (Math.random() * 3 + 4);
		setTargetRange(2000);
		
		int batType = (int) (Math.random() * 31);

		if (batType < 10)
		{
			setName("Grey bat");
		}
		else if (batType < 20)
		{
			setImage("BATB_RIGHT_0");
			setName("Brown bat");
		}
		else if (batType == 20)
		{
			setImage("BATD_RIGHT_0");
			setDamage(15);
			setHP(100);
			setName("Dark bat");
			addItem(ServerItem.randomItem(getX(), getY()));
			addItem(ServerItem.randomItem(getX(), getY()));

			speed = (int) (Math.random() * 3 + 8);
		}

		if (Math.random() < 0.75)
		{
			addItem(ServerItem.randomItem(getX(), getY()));
		}
		
		setHeight(46);
		setWidth(28);
	}

	/**
	 * Move the bat according to its A.I.
	 */
	public void update()
	{
		// Targeting and following the player
		if (getTarget() == null)
		{
			findTarget();

		}
		else if (getTarget().getHP() <= 0 || getTarget().isDisconnected()
				|| !quickInRange(getTarget(), getTargetRange()))
		{
			setTarget(null);

			setHSpeed(0);
			setVSpeed(0);
		}
		else
		{
			setMovement(getTarget());
		}

		if (getTarget()==null)
		{
		if (getHSpeed() > 0)
		{
			setDirection("RIGHT");
			setRelativeDrawX(-36);
		}
		else if (getHSpeed() < 0)
		{
			setDirection("LEFT");
		}
		}
		else
		{
			if (getX()+getWidth()/2 < getTarget().getX()+getWidth()/2)
			{
				setDirection("RIGHT");
				setRelativeDrawX(-36);
			}
			else
			{
				setDirection("LEFT");
			}
		}

		int imageCounter = getCounter() % 35;
		if (imageCounter < 5)
		{
			setImage(getBaseImage() + "_" + getDirection() + "_0");
		}
		else if (imageCounter < 10)
		{
			setImage(getBaseImage() + "_" + getDirection() + "_1");
		}
		else if (imageCounter < 15)
		{
			setImage(getBaseImage() + "_" + getDirection() + "_2");
		}
		else if (imageCounter < 25)
		{
			setImage(getBaseImage() + "_" + getDirection() + "_3");
		}
		else if (imageCounter < 35)
		{
			setImage(getBaseImage() + "_" + getDirection() + "_4");
		}

		setCounter(getCounter() + 1);
		System.out.println("x:" + getX() + " y:" + getY());

	}

	@Override
	public void inflictDamage(int amount, ServerCreature source)
	{
		super.inflictDamage(amount, source);
		if (source.getType().equals(ServerWorld.PLAYER_TYPE)
				&& source != getTarget())
		{
			setTarget((ServerPlayer) source);
		}
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
	public void setMovement(ServerObject other)
	{

		if (collidesWith(other))
		{
			setHSpeed(0);
			setVSpeed(0);
		}
		else
		{
			double xDiffOne = (other.getX() + other.getWidth() / 2)
					- (getX() + getWidth());
			double xDiffTwo = (other.getX() + other.getWidth() / 2) - getX();
			double xDiff = Math.min(Math.abs(xDiffOne), Math.abs(xDiffTwo))
					* (xDiffOne / Math.abs(xDiffOne));

			double yDiffOne = (other.getY() + other.getHeight() / 2)
					- (getY() + getHeight());
			double yDiffTwo = (other.getY() + other.getHeight() / 2) - getY();
			double yDiff = Math.min(Math.abs(yDiffOne), Math.abs(yDiffTwo))
					* (yDiffOne / Math.abs(yDiffOne));

			this.angle = Math.atan2(yDiff, xDiff);

			setHSpeed((speed * Math.cos(angle)));

//			while (!(getHSpeed() <= speed && getHSpeed() >= -speed))
//			{
//				angle += 0.0001;
//				setHSpeed((speed * Math.cos(angle)));
//			}

			setVSpeed((speed * Math.sin(angle)));
		}

	}

	/**
	 * Destroy the bat
	 */
	@Override
	public void destroy()
	{
		System.out.println("Destroyed");
		super.destroy();
		((ServerBatSpawner) (getSpawner())).removeBat();
	}

}
