package Server;

public class Slime extends EnemyAI{

	private int speed = 5;
	private int previousX;
	private boolean targetFound = false;
	
	public Slime(int x, int y, int width, int height, int ID, String image, int maxHP) {
		super(x, y, width, height, ID, image, maxHP);
		previousX = x+1;

	}
	
	public void move()
	{
		targetFound = false;
		if(getTarget() == null)
			findTarget();
		else if(getTarget().getHP() <= 0 || getTarget().isDisconnected())
			setTarget(null);
		else
		{
			if(getX() - getTarget().getX() < -getTarget().getWidth() )
				setHSpeed(speed);
			else if(getX() - getTarget().getX() > getTarget().getWidth())
				setHSpeed(-speed);
			else
			{
				setHSpeed(0);
				targetFound = true;
			}

			if(getCounter()%4 == 0)
			{
				if(getTarget().isOnSurface()&& !targetFound && previousX == getX())
				{
					setVSpeed(-15);
					getTarget().setOnSurface(false);
					
				}
				previousX = getX();
			}

		}
	}

}
