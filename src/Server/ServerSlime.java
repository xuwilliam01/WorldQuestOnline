package Server;

public class ServerSlime extends ServerNPC
{

	private int speed = 5;
	private int startCounter = 0;

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
		
		
		// Set a random counter to start so not every slime does the exact same thing
		setCounter((int)(Math.random()*100));
		
		startCounter = 0;
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
			if (!landed)
			{
				startCounter = getCounter();

				setImage("SLIME_6.png");
				
				landed = true;
			}
			else if (getCounter() - startCounter <= 15)
			{
				if (getCounter() - startCounter  > 10)
				{
				setImage("SLIME_7.png");
				}
			}
			else if (getCounter() - startCounter <= 25)
			{
				setImage("SLIME_0.png");
			}
			else if (getCounter() - startCounter <= 45)
			{
				setImage("SLIME_1.png");
			}
			else if (getCounter() - startCounter <= 65)
			{
				setImage("SLIME_0.png");
			}
			else if (getCounter() - startCounter <= 85)
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
