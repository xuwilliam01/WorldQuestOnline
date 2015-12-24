package Server;

public class Slime extends EnemyAI
{

	private int speed = 5;
	private int startCounter = 0;


	public Slime(int x, int y, int width, int height, int ID, String image,
			int maxHP)
	{
		super(x, y, width, height, ID, image, maxHP);

	}

	public void move()
	{
		if (getTarget() == null)
			findTarget();
		else if (getTarget().getHP() <= 0 || getTarget().isDisconnected() || findDistanceBetween(getTarget()) > getTargetRange())
			setTarget(null);
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

			if (isOnSurface())
			{
				if (getCounter() - startCounter >= 90)
				{
					setVSpeed(-15);
					setOnSurface(false);
					startCounter = getCounter();
				}
			}

		}
		setCounter(getCounter()+1);
	}

}
