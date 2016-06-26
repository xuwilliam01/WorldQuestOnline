package Client;

import Server.ServerWorld;

public class ClientStar
{
	private int x;
	private int y;
	private double alpha;
	private double maxAlpha;
	
	public ClientStar ()
	{
		x = (int)(Math.random()*Client.SCREEN_WIDTH);
		y = (int)(Math.random()*Client.SCREEN_HEIGHT);
		alpha = 0;
		maxAlpha = Math.random();
	}

	public void update()
	{
		if (alpha <= maxAlpha - 1/ServerWorld.COUNTER_TIME*60)
		alpha += 1/ServerWorld.COUNTER_TIME*60;
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
	
	
}
