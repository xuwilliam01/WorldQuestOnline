package Client;

import Server.ServerWorld;

public class ClientStar
{
	private int x;
	private int y;
	private double alpha;
	private double maxAlpha;
	private boolean exists;
	private int counter;
	private int lifeTime;
	private int size;

	public ClientStar()
	{
		x = (int) (Math.random() * Client.SCREEN_WIDTH);
		y = (int) (Math.random() * Client.SCREEN_HEIGHT);
		alpha = 0;
		maxAlpha = Math.random();
		exists = true;
		counter = -((int) (Math.random() * ServerWorld.COUNTER_TIME * 60 * 7)+ServerWorld.COUNTER_TIME * 60*1);
		lifeTime = (int) (Math.random() * ServerWorld.COUNTER_TIME * 60 * 6)
				+ ServerWorld.COUNTER_TIME * 60 * 4;
		
		
		size = (int)(Math.random()*4);
		if (size !=2)
		{
			size = 1;
		}
	}

	/**
	 * Update the opacity of the star
	 */
	public void update()
	{
		if (counter >= lifeTime - ServerWorld.COUNTER_TIME * 60.0 && alpha > 0)
		{
			if (alpha - 1 / (ServerWorld.COUNTER_TIME * 60.0) > 0)
			{
				alpha -= 1 / (ServerWorld.COUNTER_TIME * 60.0);
			}
			else
			{
				alpha = 0;
			}
		}
		else 
			if (counter >= 0 && alpha < maxAlpha)
		{
			if (alpha < maxAlpha - 1 / (ServerWorld.COUNTER_TIME * 60.0))
			{
				alpha += 1 / (ServerWorld.COUNTER_TIME * 60.0);;
			}
			else
			{
				alpha = maxAlpha;
			}
		}

		if (counter >= lifeTime)
		{
			exists = false;
		}
		else
		{
			counter++;
		}
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public double getAlpha()
	{
		return alpha;
	}

	public void setAlpha(double alpha)
	{
		this.alpha = alpha;
	}

	public boolean exists()
	{
		return exists;
	}

	public void setExists(boolean exists)
	{
		this.exists = exists;
	}

	public int getSize()
	{
		return size;
	}

	public void setSize(int size)
	{
		this.size = size;
	}
	

}
