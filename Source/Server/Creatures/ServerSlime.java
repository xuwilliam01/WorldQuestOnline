package Server.Creatures;

import Server.ServerWorld;
import Server.Items.ServerItem;
import Server.Spawners.ServerSlimeSpawner;

/**
 * A slime enemy
 * @author Alex Raita & William Xu
 *
 */
public class ServerSlime extends ServerEnemy
{

	/**
	 * The default HP of a slime
	 */
	public final static int SLIME_HP = 50;

	/**
	 * Horizontal speed of the slime
	 */
	private int speed;

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
	 * The jump height
	 */
	int jumpHeight;

	/**
	 * Whether or not the slime has landed, for purposes of changing the image
	 */
	private boolean landed;

	/**
	 * The x-coordinate of the last landing position the slime was in
	 */
	private double lastX;

	/**
	 * 
	 * @param x
	 * @param y
	 * @param world
	 */
	public ServerSlime(double x, double y, ServerWorld world)
	{
		super(x, y, -1, -1, 0, 0, ServerWorld.GRAVITY, "GREENSLIME_0",
				SLIME_HP,
				ServerWorld.SLIME_TYPE, world, ServerPlayer.NEUTRAL);

		lastX = 0;

		// Set a random counter to start so not every slime does the exact same
		// thing
		setCounter((int) (Math.random() * 200));

		// Set a random direction
		if ((int) (Math.random() * 2) == 0)
		{
			direction *= -1;
		}
		landCounter = 0;

		setDamage((int) (Math.random() * 3) + 3);
		jumpHeight = (int) (Math.random() * 3 + 14);
		speed = (int) (Math.random() * 3 + 4);

		int slimeType = (int) (Math.random() * 31);

		if (slimeType < 15)
		{
			setImage("GREENSLIME_6");
			setName("Green Slime");
		}
		else if (slimeType <= 18)
		{
			setImage("BLUESLIME_6");
			setName("Blue Slime");
		}
		else if (slimeType <= 22)
		{
			setImage("REDSLIME_6");
			setName("Red Slime");
		}
		else if (slimeType <= 26)
		{
			setImage("YELLOWSLIME_6");
			setName("Yellow Slime");
		}
		else if (slimeType <= 29)
		{
			setImage("GIANTSLIME_6");
			setDamage(15);
			setHP(150);
			setWidth(106);
			setRelativeDrawX(-24);
			setHeight(-1);
			setName("Giant Slime");
			addItem(ServerItem.randomItem(getX(), getY(),world));
		}
		else if (slimeType <= 30)
		{
			setImage("DARKSLIME_6");
			setWidth(-1);
			setHeight(-1);
			setDamage(18);
			setHP(50);
			setName("Dark Slime");
			addItem(ServerItem.randomItem(getX(), getY(),world));
			addItem(ServerItem.randomItem(getX(), getY(),world));
			jumpHeight = (int) (Math.random() * 3 + 20);
			speed = (int) (Math.random() * 3 + 8);
		}
		landed = true;
		if (Math.random() < 0.75)
		{
			addItem(ServerItem.randomItem(getX(), getY(),world));
		}
	}

	/**
	 * Move the slime according to its A.I.
	 */
	public void update()
	{
		// Targeting and following the player
		if (getTarget() == null)
		{
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

			// if (getWorld().getWorldCounter() % 10 == 0)
			// {
			findPlayerTarget();
			// }

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

				setImage(getBaseImage() + "_6");

				landed = true;
			}
			else if (getCounter() - landCounter <= 15)
			{
				if (getCounter() - landCounter > 10)
				{
					setImage(getBaseImage() + "_7");
				}
			}
			else if (getCounter() - landCounter <= 25)
			{
				setImage(getBaseImage() + "_0");
			}
			else if (getCounter() - landCounter <= 45)
			{
				setImage(getBaseImage() + "_1");
			}
			else if (getCounter() - landCounter <= 65)
			{
				setImage(getBaseImage() + "_0");
			}
			else if (getCounter() - landCounter <= 85)
			{
				setImage(getBaseImage() + "_1");
			}
			else
			{
				if (Math.abs(getX() - lastX) < 1)
				{
					direction *= -1;
				}
				lastX = getX();
				setVSpeed(-jumpHeight);
				setOnSurface(false);
				setImage(getBaseImage() + "_2");
			}
		}
		else
		{
			landed = false;
			if (Math.abs(getVSpeed()) < 8)
			{
				setImage(getBaseImage() + "_4");
			}
			else
			{
				setImage(getBaseImage() + "_2");
			}
		}

		setCounter(getCounter() + 1);

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
	 * Destroy the slime
	 */
	@Override
	public void destroy()
	{
		((ServerSlimeSpawner) (getSpawner())).removeSlime();
		super.destroy();
	}
}
