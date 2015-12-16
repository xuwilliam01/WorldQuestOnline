package Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Client extends JPanel implements KeyListener, ActionListener
{
	// Width and height of the screen
	public final static int SCREEN_WIDTH = 1024;
	public final static int SCREEN_HEIGHT = 768;
	public final static int TILE_SIZE = 20;

	private Socket mySocket;
	private PrintWriter output;
	private BufferedReader input;
	private Timer framerate;
	
	/**
	 * The current message that the client is sending to the server
	 */
	private String currentMessage;
	
	/**
	 * Object storing all player data
	 */
	private ClientPlayer player;

	/**
	 * Stores the visible world of the client
	 */
	private ClientWorld world;
	
	/**
	 * The framerate of the client
	 */
	public final static int FRAME_DELAY = 15;

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
					System.out.println(message);
					String[] tokens = message.split(" ");
					if (tokens[0].equals("x"))
					{
						player.setX(Integer.parseInt(tokens[1]));
					}
					else if (tokens[0].equals("y"))
					{
						player.setY(Integer.parseInt(tokens[1]));
					}
					else if(tokens[0].equals("TILE"))
					{
						Tile newTile = new Tile("TILE",Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]),Integer.parseInt(tokens[3]));
						if(!world.contains(newTile))
							world.add(newTile);
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
		framerate = new Timer(FRAME_DELAY,this);
		world = new ClientWorld();
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
		framerate.start();
	}

	/**
	 * Draw everything
	 */
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		world.draw(graphics);
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
		}
		else if (key.getKeyCode()==KeyEvent.VK_LEFT && !currentMessage.equals("STOP LEFT"))
		{
			currentMessage = "STOP LEFT";	
		}
		else if (key.getKeyCode()==KeyEvent.VK_UP && !currentMessage.equals("STOP UP"))
		{
			currentMessage = "STOP UP";	
		}
		else if (key.getKeyCode()==KeyEvent.VK_DOWN && !currentMessage.equals("STOP DOWN"))
		{
			currentMessage = "STOP DOWN";		
		}
		output.println(currentMessage);
		output.flush();
	
	}

	@Override
	public void keyTyped(KeyEvent key)
	{

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
		
	}

}
