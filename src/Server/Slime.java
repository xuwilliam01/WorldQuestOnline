package Server;

public class Slime extends EnemyAI
{

	private int speed = 5;
	private int previousX;
	private boolean targetFound = false;

	private int startCounter = 0;

	public Slime(int x, int y, int width, int height, int ID, String image,
			int maxHP)
	{
		super(x, y, width, height, ID, image, maxHP);
		previousX = x + 1;

	}

	public void move()
	{
		targetFound = false;
		if (getTarget() == null)
			findTarget();
		else if (getTarget().getHP() <= 0 || getTarget().isDisconnected())
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
				targetFound = true;
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
