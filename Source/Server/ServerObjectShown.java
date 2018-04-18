package Server;

public class ServerObjectShown extends ServerObject
{
	private long startCounter;
	private ServerWorld world;
	public ServerObjectShown(double x, double y, int width, int height,
			double gravity, String image, String type, ServerEngine engine)
	{
		super(x, y, width, height, gravity, image, type, engine);
		this.world = engine.getWorld();
		this.startCounter = this.world.getWorldCounter();
	}

	@Override
	public void update()
	{
		if (this.world.getWorldCounter() - this.startCounter > 120)
		{
			this.destroy();
		}
	}

}
