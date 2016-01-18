package Server.Creatures;

import Server.ServerWorld;
import Server.Items.ServerPotion;
import Server.Items.ServerItem;

public class ServerSlime extends ServerEnemy
{

	/**
	 * The default HP of a slime
	 */
	public final static int SLIME_HP = 50;

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


	public ServerSlime(double x, double y, ServerWorld world)
	{
		super(x, y, -1, -1, 0, 0, ServerWorld.GRAVITY, "GREENSLIME_0.png", SLIME_HP,
				ServerWorld.SLIME_TYPE, world, ServerPlayer.NEUTRAL);

		// Set a random counter to start so not every slime does the exact same
		// thing
		setCounter((int) (Math.random() * 200));

		// Set a random direction
		if ((int) (Math.random() * 2) == 0)
		{
			direction *= -1;
		}
		landCounter = 0;
		
		
		setDamage(2);
		
		int slimeType = (int)(Math.random()*30);
		
		if (slimeType <15)
		{
		setImage("GREENSLIME_6.png");
		}
		else if (slimeType >= 15 && slimeType <= 18)
		{
			setImage("BLUESLIME_6.png");
		}
		else if (slimeType >= 19 && slimeType <= 22)
		{
			setImage("REDSLIME_6.png");
		}
		else if (slimeType >= 23 && slimeType <= 26)
		{
			setImage("YELLOWSLIME_6.png");
		}
		else if (slimeType >= 27 && slimeType <= 28)
		{
			setImage("GIANTSLIME_6.png");
			setDamage(5);
			setHP(150);
			setWidth(-1);
			setHeight(-1);
		}
		else if (slimeType == 29)
		{
			setImage("DARKSLIME_6.png");
			setDamage(10);
			setDamage(300);
		}
		
		landed = true;

		addItem(ServerItem.randomItem(getX(), getY()));
	}

	/**
	 * Move the slime according to its A.I.
	 */
	public void update()
	{
		// Targeting and following the player
		if (getTarget() == null)
		{
			if (getWorld().getWorldCounter() % 30 == 0)
			{
				findTarget();
			}
			if (!isOnSurface() && getHSpeed() == 0)
			{
				if (getCounter() >= changeDirectionCounter)
				{
					direction *= -1;
					changeDirectionCounter = getCounter()
							+ (int) (Math.random() * 6000);
				}
				setHSpeed(direction * speed);
			}
		}
		else if (getTarget().getHP() <= 0 || getTarget().isDisconnected()
				|| !quickInRange(getTarget(), getTargetRange()))
		{
			setTarget(null);
		}
		else
		{
			if ((getX() + getWidth())
					- (getTarget().getX() + getTarget().getWidth() / 2) < 0
					&& !isOnSurface())
			{
				setHSpeed(speed);
			}
			else if (getX() - (getTarget().getX() + getTarget().getWidth() / 2) > 0
					&& !isOnSurface())
			{
				setHSpeed(-speed);
			}
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

				setImage( getBaseImage() +"_6.png");

				landed = true;
			}
			else if (getCounter() - landCounter <= 15)
			{
				if (getCounter() - landCounter > 10)
				{
					setImage(getBaseImage() +"_7.png");
				}
			}
			else if (getCounter() - landCounter <= 25)
			{
				setImage(getBaseImage() +"_0.png");
			}
			else if (getCounter() - landCounter <= 45)
			{
				setImage(getBaseImage() +"_1.png");
			}
			else if (getCounter() - landCounter <= 65)
			{
				setImage(getBaseImage() +"_0.png");
			}
			else if (getCounter() - landCounter <= 85)
			{
				setImage(getBaseImage() +"_1.png");
			}
			else
			{
				setVSpeed(-15);
				setOnSurface(false);
				setImage(getBaseImage() +"_2.png");
			}
		}
		else
		{
			landed = false;
			if (Math.abs(getVSpeed()) < 8)
			{
				setImage(getBaseImage() +"_4.png");
			}
			else
			{
				setImage(getBaseImage() +"_2.png");
			}
		}

		setCounter(getCounter() + 1);

	}
	
}
