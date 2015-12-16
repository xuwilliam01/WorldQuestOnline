package Client;

public class ClientPlayer
{
	private int x;
	private int y;
	private int width;
	private int height;

	/**
	 * Constructor for a client player
	 */
	public ClientPlayer()
	{
		this.x = 0;
		this.y = 0;
		this.width = Client.TILE_SIZE;
		this.height = Client.TILE_SIZE;
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
	
	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}
}
