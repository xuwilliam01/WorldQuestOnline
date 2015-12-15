package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The player
 * 
 * @author William Xu & Alex Raita
 *
 */
public class Player implements Runnable
{

	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;
	private Server server;
	private Engine world;

	public Player(Socket socket, Server server, Engine world)
	{
		// Import the socket, server, and world
		this.socket = socket;
		this.server = server;
		this.world = world;
		
		// Set up the output
		try
		{
			output = new PrintWriter(this.socket.getOutputStream());
		}
		catch (IOException e)
		{
			System.err.println("Error getting client's output stream");
			e.printStackTrace();
		}

		// Set up the input
		try
		{
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		}
		catch (IOException e)
		{
			System.err.println("Error getting client's input stream");
			e.printStackTrace();
		}
		
	}

	@Override
	public void run()
	{
		// Get input from the player
		while (true)
		{
			try
			{
				String command = input.readLine();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				break;
			}
		}
		
		// If the buffered reader breaks, the player has disconnected
		System.out.println("A client has disconnected");
		try
		{
			input.close();
		}
		catch (IOException e)
		{
			System.out.println("Error closing buffered reader");
			e.printStackTrace();
		}
		output.close();
	}
	
	/** Send a message to the client
	 * 
	 * @param message the string command to send to the client
	 */
	public void sendMessage(String message)
	{
		output.println(message);
		output.flush();
	}

}
