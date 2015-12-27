package Server;

public class ServerSlime extends ServerNPC
{

	private int speed = 5;

	/**
	 * The point in time when the slime lands
	 */
	private int landCounter = 0;

	/**
	 * The counter keeping track of when the slime will next change direction
	 */
	private int changeDirectionCounter = 0;

	/**
	 * The direction the slime intends to go (1 means right, -1 means left)
	 */
	int direction = 1;

	/**
	 * Whether or not the slime has landed, for purposes of changing the image
	 */
	private boolean landed;

	public ServerSlime(double x, double y, int width, int height,
			double gravity, int ID, String image,
			int maxHP)
	{
		super(x, y, width, height, gravity, ID, image, maxHP,
				ServerWorld.SLIME_TYPE);

		// Set a random counter to start so not every slime does the exact same
		// thing
		setCounter((int) (Math.random() * 200));

		// Set a random direction
		if ((int) (Math.random() * 2) == 0)
		{
			direction *= -1;
		}

		landCounter = 0;
		setImage("SLIME_6.png");
		landed = true;
	}

	/**
	 * Move the slime according to its A.I.
	 */
	public void update()
	{
		// Targeting and following the player
		if (getTarget() == null)
		{
			findTarget();
			if (!isOnSurface() && getHSpeed() == 0)
			{
				if (getCounter() >= changeDirectionCounter)
				{
					direction *= -1;
					changeDirectionCounter = getCounter()
							+ (int) (Math.random() * 900);
				}
				setHSpeed(direction * speed);
			}
		}
		else if (getTarget().getHP() <= 0 || getTarget().isDisconnected()
				|| findDistanceBetween(getTarget()) > getTargetRange())
		{
			setTarget(null);
		}
		else
		{
			if (getX() - getTarget().getX() < -getTarget().getWidth()
					&& !isOnSurface())
				setHSpeed(speed);
			else if (getX() - getTarget().getX() > getTarget().getWidth()
					&& !isOnSurface())
				setHSpeed(-speed);
			else
			{
				setHSpeed(0);
			}
		}

		// Base the A.I. around the moment the slime lands
		if (isOnSurface())
		{
			setHSpeed(0);
			if (!landed)
			{
				landCounter = getCounter();

				setImage("SLIME_6.png");

				landed = true;
			}
			else if (getCounter() - landCounter <= 15)
			{
				if (getCounter() - landCounter > 10)
				{
					setImage("SLIME_7.png");
				}
			}
			else if (getCounter() - landCounter <= 25)
			{
				setImage("SLIME_0.png");
			}
			else if (getCounter() - landCounter <= 45)
			{
				setImage("SLIME_1.png");
			}
			else if (getCounter() - landCounter <= 65)
			{
				setImage("SLIME_0.png");
			}
			else if (getCounter() - landCounter <= 85)
			{
				setImage("SLIME_1.png");
			}
			else
			{
				setVSpeed(-15);
				setOnSurface(false);
				setImage("SLIME_2.png");
			}
		}
		else
		{
			landed = false;
			if (Math.abs(getVSpeed()) < 8)
			{
				setImage("SLIME_4.png");
			}
			else
			{
				setImage("SLIME_2.png");
			}
		}

		setCounter(getCounter() + 1);

	}
}
