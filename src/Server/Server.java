package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Imports.Images;

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

	private String[] playerImages = { "GIRL_RIGHT.png", "CYCLOPS_RIGHT.png",
			"KNIGHT_RIGHT.png" };

	int noOfPlayers = 0;

	public Server()
	{
		port = 5000;

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
			engine = new ServerEngine();
		}
		catch (IOException e1)
		{
			System.out.println("Error with Creating World and/or Engine");
		}
		Thread newEngine = new Thread(engine);
		newEngine.start();

		// Accept players into the server
		System.out.println("Waiting for clients to connect");

		while (noOfPlayers < 999)
		{
			try
			{
				Socket newClient = socket.accept();

				noOfPlayers++;
				int x = (int) (Math.random() * 1000 + 50);
				int y = ServerPlayer.PLAYER_Y;

				int characterSelection = (int) (Math.random() * 3);
				ServerPlayer newPlayer = new ServerPlayer(newClient, engine,
						x, y, -1, -1,
						ServerWorld.GRAVITY,
						engine.useNextID(), playerImages[characterSelection]);
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
