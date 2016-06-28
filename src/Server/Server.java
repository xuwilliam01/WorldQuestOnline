package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import Server.Creatures.ServerPlayer;

/**
 * Creates a new world and accepts new client connections
 * 
 * @author William Xu & Alex Raita
 * 
 */
public class Server implements Runnable
{
	private ServerSocket socket;
	private ServerEngine engine;
	private int port;
	private String map;

	private String[] playerColours = { "DARK", "LIGHT", "TAN" };

	int noOfPlayers = 0;

	public Server(String map, int port)
	{
		this.map = map;
		try
		{
			this.socket = new ServerSocket(port);
		}
		catch (IOException e)
		{
			System.out.println("Server cannot be created with given port");
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		// Construct the new world
		System.out.println("Creating world...");
		try
		{
			if(map.equals(""))
				engine = new ServerEngine();
			else
				engine = new ServerEngine(map);
		}
		catch (IOException e1)
		{
			System.out.println("Error with Creating World and/or Engine");
		}
		Thread newEngine = new Thread(engine);
		newEngine.start();

		// Accept players into the server
		System.out.println("Waiting for clients to connect");

		while (true)
		{
			try
			{
				Socket newClient = socket.accept();

				noOfPlayers++;

				int team = engine.nextTeam() % 2 + 1;

				int x = 2000;
				int y = 2000;
				if(team == ServerPlayer.RED_TEAM)
				{
					x = engine.getWorld().getRedCastleX();
					y = engine.getWorld().getRedCastleY();
				}
				else
				{
					x = engine.getWorld().getBlueCastleX();
					y = engine.getWorld().getBlueCastleY();
				}

				int characterSelection = (int) (Math.random() * playerColours.length);
				ServerPlayer newPlayer = new ServerPlayer(x, y,
						ServerPlayer.DEFAULT_WIDTH,
						ServerPlayer.DEFAULT_HEIGHT, -14, -38,
						ServerWorld.GRAVITY, playerColours[characterSelection],
						newClient, engine, engine.getWorld());

				newPlayer.setTeam(team);

				engine.addPlayer(newPlayer);
				Thread playerThread = new Thread(newPlayer);
				playerThread.start();

				System.out.println("A new client has connected");
			}
			catch (IOException e)
			{
				System.out.println("Error connecting to client");
				e.printStackTrace();
			}
		}
	}

	public ServerEngine getEngine()
	{
		return engine;
	}

}
