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
import java.lang.reflect.Field;
import java.net.Socket;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.Timer;

import Imports.Images;

public class Client extends JPanel implements KeyListener
{
	// Width and height of the screen
	public final static int SCREEN_WIDTH = 1024;
	public final static int SCREEN_HEIGHT = 768;

	private Socket mySocket;
	private PrintWriter output;
	private BufferedReader input;

	private long ping;
	private String pingString = "LATENCY: (PRESS P)";

	/**
	 * The current message that the client is sending to the server
	 */
	private String currentMessage;

	/**
	 * Object storing all player data
	 */
	private ClientObject player;

	/**
	 * Stores the visible world of the client
	 */
	private ClientWorld world;

	/**
	 * The framerate of the client
	 */
	public final static int FRAME_DELAY = 0;

	/**
	 * Constructor for the client
	 */
	public Client(Socket socket)
	{
		Images.importImages();
		mySocket = socket;
		currentMessage = "";
	}

	/**
	 * Call when the server closes (Add more later)
	 */
	private void serverClosed()
	{
		System.out.println("Server was closed");
	}

	/**
	 * Start the client
	 */
	public void initialize()
	{
		// Create the screen
		setDoubleBuffered(true);
		setBackground(Color.white);

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
			// System.out.println("Error creating buffered reader");
			e.printStackTrace();
		}

		// Set up the output
		try
		{
			output = new PrintWriter(mySocket.getOutputStream());
		}
		catch (IOException e)
		{
			// System.out.println("Error creating print writer");
			e.printStackTrace();
		}

		// Import the map from the server
		importMap();

		// Get the user's player
		try
		{
			String message = input.readLine();
			String[] tokens = message.split(" ");

			int id = Integer.parseInt(tokens[0]);
			int x = Integer.parseInt(tokens[1]);
			int y = Integer.parseInt(tokens[2]);
			String image = tokens[3];

			player = new ClientObject(id, x, y, image);

			world.add(player);
		}
		catch (IOException e)
		{
			System.out.println("Error getting player from server");
			e.printStackTrace();
		}

		// Start the actual game
		Thread gameThread = new Thread(new runGame());
		gameThread.start();

		System.out.println("Game started");
	}

	/**
	 * Thread for running the actual game
	 * @author William Xu && Alex Raita
	 *
	 */
	class runGame implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				while (true)
				{
					if (input.ready())
					{
						String message = input.readLine();
						String[] tokens = message.split(" ");

						for(int token = 0; token < tokens.length;token++)
						{
							// If our player has moved
							if (tokens[token].equals("U"))
							{
								repaint();
							}
							// If there is a player to be updated
							else if (tokens[token].equals("P"))
							{
								int id = Integer.parseInt(tokens[++token]);
								if (id == player.getID())
								{
									player.setX(Integer.parseInt(tokens[++token]));
									player.setY(Integer.parseInt(tokens[++token]));
									player.setImage(tokens[++token]);
								}
								else if (world.contains(id))
								{
									ClientObject otherPlayer = world.get(id);
									otherPlayer.setX(Integer.parseInt(tokens[++token]));
									otherPlayer.setY(Integer.parseInt(tokens[++token]));
									otherPlayer.setImage(tokens[++token]);
								}
								else
								{
									ClientObject otherPlayer = new ClientObject(id,
											Integer.parseInt(tokens[++token]),
											Integer.parseInt(tokens[++token]), tokens[++token]);
									world.add(otherPlayer);
								}
							}
							else if (tokens[token].equals("REPING"))
							{
								pingString = "LATENCY: "
										+ (System.currentTimeMillis() - ping);
							}

							// Remove a player after disconnecting
							else if (tokens[token].equals("R"))
							{
								world.remove(Integer.parseInt(tokens[++token]));
							}
						}
					}
				}
			}
			catch (NumberFormatException e1)
			{
				e1.printStackTrace();
			}
			catch (IOException e1)
			{
				System.out.println("Server has closed");
			}
		}

	}

	/**
	 * Import the map
	 */
	private void importMap()
	{
		System.out.println("Importing the map from the server...");

		// Get the 2D grid from the server
		String gridSize;

		try
		{
			gridSize = input.readLine();
			String dimensions[] = gridSize.split(" ");
			int height = Integer.parseInt(dimensions[0]);
			int width = Integer.parseInt(dimensions[1]);
			int tileSize = Integer.parseInt(dimensions[2]);

			char grid[][] = new char[height][width];

			for (int row = 0; row < height; row++)
			{
				String gridRow = input.readLine();
				for (int column = 0; column < width; column++)
				{
					grid[row][column] = gridRow.charAt(column);
				}
			}

			world = new ClientWorld(grid, tileSize);
		}
		catch (IOException e)
		{
			serverClosed();
		}

		System.out.println("Map import has finished");
	}

	/**
	 * Draw everything
	 */
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		world.draw(graphics, player.getX(), player.getY(), player.getWidth(),
				player.getHeight());

		graphics.setColor(Color.black);
		graphics.drawString(pingString, 20, 20);
	}

	@Override
	public void keyPressed(KeyEvent key)
	{
		if (key.getKeyCode() == KeyEvent.VK_RIGHT
				&& !currentMessage.equals("RIGHT"))
		{
			currentMessage = "RIGHT";
			output.println(currentMessage);
			output.flush();
		}
		else if (key.getKeyCode() == KeyEvent.VK_LEFT
				&& !currentMessage.equals("LEFT"))
		{
			currentMessage = "LEFT";
			output.println(currentMessage);
			output.flush();
		}
		else if (key.getKeyCode() == KeyEvent.VK_UP
				&& !currentMessage.equals("UP"))
		{
			currentMessage = "UP";
			output.println(currentMessage);
			output.flush();
		}
		else if (key.getKeyCode() == KeyEvent.VK_DOWN
				&& !currentMessage.equals("DOWN"))
		{
			currentMessage = "DOWN";
			output.println(currentMessage);
			output.flush();
		}
		else if (key.getKeyCode() == KeyEvent.VK_P)
		{
			ping = System.currentTimeMillis();
			output.println("PING");
			output.flush();
		}
	}

	@Override
	public void keyReleased(KeyEvent key)
	{
		if (key.getKeyCode() == KeyEvent.VK_RIGHT
				&& !currentMessage.equals("STOP RIGHT"))
		{
			currentMessage = "STOP RIGHT";
		}
		else if (key.getKeyCode() == KeyEvent.VK_LEFT
				&& !currentMessage.equals("STOP LEFT"))
		{
			currentMessage = "STOP LEFT";
		}
		else if (key.getKeyCode() == KeyEvent.VK_UP
				&& !currentMessage.equals("STOP UP"))
		{
			currentMessage = "STOP UP";
		}
		else if (key.getKeyCode() == KeyEvent.VK_DOWN
				&& !currentMessage.equals("STOP DOWN"))
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

}
