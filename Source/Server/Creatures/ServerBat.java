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
	 * The minimum amount of time before the bat manually changes direction
	 */
	public final static int BAT_CHANGE_DIRECTION_TIME = 60;

	/**
	 * The minimum amount of time before the bat needs rest again
	 */
	public final static int BAT_NEXT_REST_TIME = 600;

	/**
	 * The minimum amount of time the bat will rest
	 */
	public final static int BAT_REST_TIME = 3600;

	/**
	 * Flying speed of the bat
	 */
	private double speed;

	/**
	 * The maximum speed of the bat
	 */
	private double maxSpeed;

	/**
	 * The counter keeping track of when the bat will next change direction
	 */
	private int changeDirectionCounter = 300;

	/**
	 * The counter keeping track of when the next available rest time is or when
	 * to stop resting
	 */
	private int restCounter = 100;

	/**
	 * Whether the bat is at rest
	 */
	private boolean atRest = false;

	/**
	 * Angle of the bat in RADIANS
	 */
	private double angle;

	/**
	 * The last x-coordinate the object was in
	 */
	private double lastX = -999;

	/**
	 * The last y-coordinate the object was in
	 */
	private double lastY = -999;

	/**
	 * The last vSpeed the object had
	 */
	private double lastVSpeed = 999;

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
		maxSpeed = (int) (Math.random() * 3 + 4);

		setTargetRange(500);

		int batType = (int) (Math.random() * 31);

		if (batType < 15)
		{
			setName("Grey Bat");
		}
		else if (batType < 30)
		{
			setImage("BATB_RIGHT_0");
			setName("Brown Bat");
		}
		else if (batType == 30)
		{
			setImage("BATD_RIGHT_0");
			setDamage((int) (Math.random() * 2) + 13);
			setHP(80);
			setName("Dark Bat");
			addItem(ServerItem.randomItem(getX(), getY()));
			addItem(ServerItem.randomItem(getX(), getY()));

			maxSpeed = (int) (Math.random() * 3 + 6);
		}

		if (Math.random() < 0.75)
		{
			addItem(ServerItem.randomItem(getX(), getY()));
		}

		// Set the actual bounds of the creature
		setHeight(46);
		setWidth(28);

		speed = maxSpeed;
	}

	/**
	 * Move the bat according to its A.I.
	 */
	public void update()
	{
		// Targeting and following the player
		if (getTarget() == null)
		{

			// Change image direction
			if (getHSpeed() > 0)
			{
				setDirection("RIGHT");
				setRelativeDrawX(-36);
			}
			else if (getHSpeed() < 0)
			{
				setDirection("LEFT");
				setRelativeDrawX(0);
			}

			// If at rest then determine if needs to wake up
			if (atRest)
			{
				if (getCounter() >= restCounter)
				{
					atRest = false;
					restCounter = (int) (getCounter() + (Math.random() * 3 + 3)
							* BAT_NEXT_REST_TIME);
				}
				setVSpeed(0);
				setHSpeed(0);
				speed = 0;
			}
			else
			{
				speed = maxSpeed / 1.5;

				// Try to rest
				if (getY() == lastY && (lastVSpeed < 0)
						&& getCounter() >= restCounter
						&& getY() > ServerWorld.TILE_SIZE * 5)
				{
					atRest = true;
					restCounter = (int) (getCounter() + (Math.random() * 2 + 1)
							* BAT_REST_TIME);

				}
				// Determine if and which direction to change to
				else if (getCounter() >= changeDirectionCounter
						|| (getX() == lastX && getY() == lastY))
				{
					moveInRandomDirection();
					
					changeDirectionCounter = (int) (getCounter() + (Math
							.random() * 30 + 1) * BAT_CHANGE_DIRECTION_TIME);
				}

			}
			findTarget();

		}
		else if (getTarget().getHP() <= 0 || getTarget().isDisconnected()
				|| !quickInRange(getTarget(), getTargetRange()))
		{
			setTarget(null);
		}
		else
		{
			speed = maxSpeed;
			if (atRest)
			{
				atRest = false;
				restCounter = (int) (getCounter() + (Math.random())
						* BAT_NEXT_REST_TIME);
			}
			
			
			setMovement(getTarget());

			// Change image direction
			if (getX() + getWidth() / 2 < getTarget().getX() + getWidth() / 2)
			{
				setDirection("RIGHT");
				setRelativeDrawX(-36);
			}
			else
			{
				setDirection("LEFT");
				setRelativeDrawX(0);
			}
		}

		if (!atRest)
		{
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
		}
		else
		{
			setImage(getBaseImage() + "_" + getDirection() + "_5");
		}

		setCounter(getCounter() + 1);
		lastX = getX();
		lastY = getY();
		lastVSpeed = getVSpeed();
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

		// If the bat collides with 1/3 or more of the player
		if (collidesWith(other)
				&& (getX() + getWidth() >= other.getX() + other.getWidth() / 4 && getX() <= other
						.getX() + other.getWidth() / 4 * 3))
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

			this.angle = Math.round(Math.atan2(yDiff, xDiff) * 100) / 100.0;

			setHSpeed((speed * Math.cos(angle)));
			setVSpeed((speed * Math.sin(angle)));
		}
	}

	/**
	 * Set the angle of the object and update the horizontal and vertical speeds
	 * @param angle
	 */
	public void moveInDirection(double angle)
	{
		this.angle = angle;
		setHSpeed((speed * Math.cos(angle)));
		setVSpeed((speed * Math.sin(angle)));
	}
	
	/**
	 * Move in a random direction (Other than straight vertically)
	 */
	public void moveInRandomDirection()
	{
		double moveDirection;

		do
		{
			moveDirection = ((int) (Math.random() * 16))
					* (Math.PI / 8);
		}
		while (moveDirection == Math.PI / 2
				|| moveDirection == Math.PI / 2 * 3);

		moveInDirection(moveDirection);
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
