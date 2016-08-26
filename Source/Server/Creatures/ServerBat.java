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
	public final static int BAT_CHANGE_DIRECTION_TIME = 300;

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
		maxSpeed =(int) (Math.random() * 3 + 4);
		
		setTargetRange(500);

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

			maxSpeed =(int) (Math.random() * 3 + 8);
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
					restCounter = (int) (getCounter() + (Math.random() + 3)
							* BAT_NEXT_REST_TIME);
				}
				setVSpeed(0);
				setHSpeed(0);
				speed = 0;
			}
			else
			{
				speed = maxSpeed/1.5;
				
				// Try to rest
				if (getY() == lastY && (lastVSpeed<0)
						&& getCounter() >= restCounter && getY() > ServerWorld.TILE_SIZE*5)
				{
					atRest = true;
					restCounter = (int) (getCounter() + (Math.random() * 2 + 1)
							* BAT_REST_TIME);
					
				}
				// Determine if and which direction to change to
				else if (getCounter() >= changeDirectionCounter
						|| (getX() == lastX && getY() == lastY))
				{
					 int newDirection = (int) (Math.random() *6);
					 switch (newDirection)
					 {
					 case 0:
					 moveInDirection(0);
					 break;
					 case 1:
					 moveInDirection(Math.PI/4);
					 break;
					 case 2:
					 moveInDirection(Math.PI/4*3);
					 break;
					 case 3:
					 moveInDirection(Math.PI);
					 break;
					 case 4:
					 moveInDirection(Math.PI/4*5);
					 break;
					 case 5:
					 moveInDirection(Math.PI/4*7);
					 break;
					 }

					//moveInDirection(Math.round(Math.random() * (Math.PI * 2)*10)/10.0);

					changeDirectionCounter = (int) (getCounter() + (Math
							.random() * 5 + 1) * BAT_CHANGE_DIRECTION_TIME);
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

			this.angle = Math.round(Math.atan2(yDiff, xDiff)*360)/360.0;

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
