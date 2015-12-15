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

	//////////////////////////////////////////////////////////////////////
	// X and Y coordinates will be changed once scrolling is implemented//
	//////////////////////////////////////////////////////////////////////
	
	/**
	 * The x-coordinate of the player (left)
	 */
	private int x;

	/**
	 * Boolean describing whether or not the x coordinate has changed since the
	 * last flush
	 */
	private boolean xUpdated;

	/**
	 * The y-coordinate of the player (bottom)
	 */
	private int y;

	/**
	 * Boolean describing whether or not the x coordinate has changed since the
	 * last flush
	 */
	private boolean yUpdated;

	/**
	 * The horizontal speed of the player (negative -- left, positive -- right)
	 */
	private int hSpeed;

	/**
	 * The vertical speed of the player (negative -- down, positive -- up)
	 */
	private int vSpeed;

	/**
	 * The horizontal direction the player is facing (negative -- left, positive
	 * -- right)
	 */
	private int direction;

	public Player(Socket socket, Server server, Engine world)
	{
		// Import the socket, server, and world
		this.socket = socket;
		this.server = server;
		this.world = world;

		// Set initial x and y coordinates
		x = 0;
		y = 0;
		xUpdated = true;
		yUpdated = true;
		
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

				if (command.equals("RIGHT"))
				{
					hSpeed = 5;
					direction = 1;
				}
				else if (command.equals("STOP RIGHT"))
				{
					if (hSpeed > 0)
					{
						hSpeed = 0;
					}
				}
				else if (command.equals("LEFT"))
				{
					hSpeed = -5;
					direction = -1;
				}
				else if (command.equals("STOP LEFT"))
				{
					if (hSpeed < 0)
					{
						hSpeed = 0;
					}
				}
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

	/**
	 * Send a message to the client (flushing included)
	 * 
	 * @param message the string command to send to the client
	 */
	public void sendMessage(String message)
	{
		output.println(message);
		output.flush();
	}

	/**
	 * Queue a message without flushing
	 * 
	 * @param message the string command to send to the client
	 */
	public void queueMessage(String message)
	{
		output.println(message);
	}

	/**
	 * Flush all queued messages to the client
	 */
	public void flushWriter()
	{
		output.flush();
	}

	/**
	 * Send to the client all the updated values
	 */
	public void update()
	{
		if (xUpdated)
		{
			queueMessage("x " + x);
			xUpdated = false;
		}
		if (yUpdated)
		{
			queueMessage("y " + y);
			yUpdated = false;
		}
		
		flushWriter();
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		if (this.x != x)
		{
			this.x = x;
			xUpdated = true;
		}
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		if (this.y != y)
		{
			this.y = y;
			yUpdated = true;
		}
	}

	public int getHSpeed()
	{
		return hSpeed;
	}

	public void setHSpeed(int hSpeed)
	{
		this.hSpeed = hSpeed;
	}

	public int getVSpeed()
	{
		return vSpeed;
	}

	public void setVSpeed(int vSpeed)
	{
		this.vSpeed = vSpeed;
	}

}
