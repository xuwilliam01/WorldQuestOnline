package Client;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.Graphics;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Imports.Images;

public class Client extends JPanel implements KeyListener, MouseListener
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
	 * The player's current HP
	 */
	private int HP;

	/**
	 * Constructor for the client
	 */
	public Client(Socket socket)
	{
		Images.importImages();
		mySocket = socket;
		currentMessage = " ";
	}

	/**
	 * Call when the server closes (Add more later)
	 */
	private void serverClosed()
	{
		System.out.println("Server was closed");
		JOptionPane.showMessageDialog(null, "The Server was Closed");
	}

	/**
	 * Start the client
	 */
	public void initialize()
	{
		// Create the screen
		setDoubleBuffered(true);
		setBackground(Color.white);

		setFocusable(true);
		requestFocusInWindow();
		
		HP = 100;

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

		// Add listeners AT THE END
		addKeyListener(this);
		addMouseListener(this);
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

					String message = input.readLine();
					String[] tokens = message.split(" ");

					for (int token = 0; token < tokens.length; token++)
					{
						// If our player has moved
						if (tokens[token].equals("L"))
						{
							HP = Integer.parseInt(tokens[++token]);
						}
						else if (tokens[token].equals("U"))
						{
							repaint();
						}
						// If there is a player to be updated
						else if (tokens[token].equals("O"))
						{
							int id = Integer.parseInt(tokens[++token]);
							if (id == player.getID())
							{
								player.setX(Integer
										.parseInt(tokens[++token]));
								player.setY(Integer
										.parseInt(tokens[++token]));
								player.setImage(tokens[++token]);
							}
							else if (world.contains(id))
							{
								ClientObject otherObject = world.get(id);
								otherObject.setX(Integer
										.parseInt(tokens[++token]));
								otherObject.setY(Integer
										.parseInt(tokens[++token]));
								otherObject.setImage(tokens[++token]);
							}
							else
							{
								ClientObject otherObject = new ClientObject(id,
										Integer.parseInt(tokens[++token]),
										Integer.parseInt(tokens[++token]),
										tokens[++token]);
								world.add(otherObject);
							}
						}
						else if (tokens[token].equals("P"))
						{
							pingString = "LATENCY: "
									+ (System.currentTimeMillis() - ping);
						}

						// Remove an object after disconnection/destruction
						else if (tokens[token].equals("R"))
						{
							world.remove(Integer.parseInt(tokens[++token]));
						}
					}
				}
			}
			catch (NumberFormatException e1)
			{
				e1.printStackTrace();
			}
			catch (IOException e2)
			{
				serverClosed();
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
		world.update(graphics, player.getX(), player.getY(), player.getWidth(),
				player.getHeight());

		graphics.setColor(Color.black);
		graphics.drawString(pingString, 20, 20);
		if (HP > 0)
		{
		graphics.setColor(Color.red);
		graphics.drawString("Health: " + HP, 20, 40);
		}
		else
		{
			graphics.setColor(Color.black);
			graphics.drawString("YOU ARE DEAD. You may now fly around", 20, 40);
		}
	}

	@Override
	public void keyPressed(KeyEvent key)
	{

		if (key.getKeyCode() == KeyEvent.VK_D
				&& !currentMessage.equals("R"))
		{
			// R for right
			currentMessage = "R";
			output.println(currentMessage);
			output.flush();
		}
		else if (key.getKeyCode() == KeyEvent.VK_A
				&& !currentMessage.equals("L"))
		{
			// L for left
			currentMessage = "L";
			output.println(currentMessage);
			output.flush();
		}
		else if (key.getKeyCode() == KeyEvent.VK_W
				&& !currentMessage.equals("U"))
		{
			// U for up
			currentMessage = "U";
			output.println(currentMessage);
			output.flush();
		}
		else if (key.getKeyCode() == KeyEvent.VK_S
				&& !currentMessage.equals("D"))
		{
			// D for down
			currentMessage = "D";
			output.println(currentMessage);
			output.flush();
		}
		else if (key.getKeyCode() == KeyEvent.VK_P)
		{
			// P for ping
			ping = System.currentTimeMillis();
			output.println("P");
			output.flush();
		}
	}

	@Override
	public void keyReleased(KeyEvent key)
	{

		if (key.getKeyCode() == KeyEvent.VK_D
				&& !currentMessage.equals("!R"))
		{
			currentMessage = "!R";
		}
		else if (key.getKeyCode() == KeyEvent.VK_A
				&& !currentMessage.equals("!L"))
		{
			currentMessage = "!L";
		}
		else if (key.getKeyCode() == KeyEvent.VK_W
				&& !currentMessage.equals("!U"))
		{
			currentMessage = "!U";
		}
		else if (key.getKeyCode() == KeyEvent.VK_S
				&& !currentMessage.equals("!D"))
		{
			currentMessage = "!D";
		}
		else if (key.getKeyCode() == KeyEvent.VK_P)
		{
			pingString = "LATENCY: (PRESS P)";
		}
		output.println(currentMessage);
		output.flush();

	}

	@Override
	public void mousePressed(MouseEvent event)
	{
		if (event.getButton() == MouseEvent.BUTTON1
				&& currentMessage.charAt(0) != 'A')
		{
			// A for action
			currentMessage = "A";
			int xDist = event.getX() - SCREEN_WIDTH/2;
			int yDist = event.getY() - SCREEN_HEIGHT/2;
			
			double angle = Math.atan2(yDist, xDist);
			
			currentMessage += " " + angle;
			
			output.println(currentMessage);
			output.flush();
		}
	}

	@Override
	public void mouseReleased(MouseEvent event)
	{
		if (event.getButton() == MouseEvent.BUTTON1
				&& !currentMessage.equals("!A"))
		{
			currentMessage = "!A";
			
			output.println(currentMessage);
			output.flush();
		}
	}

	@Override
	public void keyTyped(KeyEvent key)
	{

	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{

	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{

	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{

	}

}
