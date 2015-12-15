package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Creates a new world and accepts new client connections
 * 
 * @author William Xu & Alex Raita
 * 
 */
public class Server implements Runnable
{
	private ServerSocket socket;
	private Engine engine;
	private int port;

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
		engine = new Engine();
		Thread newEngine = new Thread(engine);
		newEngine.start();
		
		// Accept players into the server
		System.out.println("Waiting for clients to connect");
		while (true)
		{
			try
			{
				Socket newClient = socket.accept();
				Player newPlayer = new Player(newClient,this,engine);
				engine.addPlayer(newPlayer);
				
				System.out.println("A new client has connected");
			}
			catch (IOException e)
			{
				System.out.println("Error connecting to client");
				e.printStackTrace();
			}
		}
	}

}
