package Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.awt.Graphics;

import javax.swing.JPanel;

public class Client extends JPanel implements KeyListener
{
	// Width and height of the screen
	public final static int SCREEN_WIDTH = 1024;
	public final static int SCREEN_HEIGHT = 768;

	
	private Socket mySocket;
	private PrintWriter output;
	private BufferedReader input;
	
	/**
	 * The current message that the client is sending to the server
	 */
	private String currentMessage;
	
	/**
	 * Object storing all player data
	 */
	ClientPlayer player;

	/**
	 * The framerate of the client
	 */
	public final static int FRAME_DELAY = 1;

	/**
	 * Constructor for the client
	 */
	public Client(Socket socket)
	{
		mySocket = socket;
		currentMessage = "";
	}
	
	
	private class ServerInput implements Runnable
	{

		@Override
		public void run()
		{
			while (true)
			{
				try
				{
					String message = input.readLine();
					String[] tokens = message.split(" ");
					if (tokens[0].equals("x"))
					{
						player.setX(Integer.parseInt(tokens[1]));
					}
					else if (tokens[0].equals("y"))
					{
						player.setY(Integer.parseInt(tokens[1]));
					}
				}
				catch (IOException e)
				{
					System.out.println("You have disconnected");
					break;
				}
			}
		}
	}

	/**
	 * Start the client
	 */
	public void initialize()
	{	
		// Create the player object
		player = new ClientPlayer();
		
		// Create the screen
		setDoubleBuffered(true);
		setBackground(Color.DARK_GRAY);

		// Add listeners
		addKeyListener(this);
		
		setFocusable(true);
		requestFocusInWindow();
		
		// Set up the input
		try
		{
			input = new BufferedReader(new InputStreamReader(
					mySocket.getInputStream()));
		}
		catch (IOException e)
		{
			//System.out.println("Error creating buffered reader");
			e.printStackTrace();
		}
		
		// Set up the output
		try
		{
			output = new PrintWriter(mySocket.getOutputStream());
		}
		catch (IOException e)
		{
			//System.out.println("Error creating print writer");
			e.printStackTrace();
		}
		
		// Thread constantly getting input from the server
		ServerInput serverInput = new ServerInput();
		Thread inputThread = new Thread (serverInput);
		inputThread.start();
		
		// Start the game
		run();
	}

	/**
	 * Method that runs the game constantly at a fixed framerate
	 */
	private void run()
	{
		while (true)
		{
			try
			{
				repaint();
				Thread.sleep(FRAME_DELAY);
			}
			catch (InterruptedException e)
			{
				//System.out.println("Error delaying the thread");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Draw everything
	 */
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		
		graphics.setColor(Color.GREEN);
		graphics.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
		graphics.drawRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
	}

	@Override
	public void keyPressed(KeyEvent key)
	{
		if (key.getKeyCode()==KeyEvent.VK_RIGHT && !currentMessage.equals("RIGHT"))
		{
			currentMessage = "RIGHT";
			output.println(currentMessage);
			output.flush();
		}
		else if (key.getKeyCode()==KeyEvent.VK_LEFT && !currentMessage.equals("LEFT"))
		{
			currentMessage = "LEFT";
			output.println(currentMessage);
			output.flush();
		}
		else if (key.getKeyCode()==KeyEvent.VK_UP && !currentMessage.equals("UP"))
		{
			currentMessage = "UP";
			output.println(currentMessage);
			output.flush();
		}
		else if (key.getKeyCode()==KeyEvent.VK_DOWN && !currentMessage.equals("DOWN"))
		{
			currentMessage = "DOWN";
			output.println(currentMessage);
			output.flush();
		}
	}

	@Override
	public void keyReleased(KeyEvent key)
	{
		if (key.getKeyCode()==KeyEvent.VK_RIGHT && !currentMessage.equals("STOP RIGHT"))
		{
			currentMessage = "STOP RIGHT";
			output.println(currentMessage);
			output.flush();
		}
		else if (key.getKeyCode()==KeyEvent.VK_LEFT && !currentMessage.equals("STOP LEFT"))
		{
			currentMessage = "STOP LEFT";
			output.println(currentMessage);
			output.flush();
		}
		else if (key.getKeyCode()==KeyEvent.VK_UP && !currentMessage.equals("STOP UP"))
		{
			currentMessage = "STOP UP";
			output.println(currentMessage);
			output.flush();
		}
		else if (key.getKeyCode()==KeyEvent.VK_DOWN && !currentMessage.equals("STOP DOWN"))
		{
			currentMessage = "STOP DOWN";
			output.println(currentMessage);
			output.flush();
		}
	}

	@Override
	public void keyTyped(KeyEvent key)
	{

	}

}
