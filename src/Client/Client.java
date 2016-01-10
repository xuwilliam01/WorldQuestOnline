package Client;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Imports.Images;
import Server.ServerEngine;

@SuppressWarnings("serial")
public class Client extends JPanel implements KeyListener, MouseListener,
MouseMotionListener
{
	// Width and height of the screen
	public final static int SCREEN_WIDTH = 1620;
	public final static int SCREEN_HEIGHT = 1080;

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
	 * The player's inventory
	 */
	private ClientInventory inventory;

	/**
	 * Used to clear inventory only once when player dies
	 */
	private boolean justDied = true;

	/**
	 * The direction that the player is facing
	 */
	private char direction;

	/**
	 * The startTime for checking FPS
	 */
	private long startTime = 0;

	/**
	 * The current FPS of the client
	 */
	private int currentFPS = 60;

	/**
	 * A counter updating every repaint and reseting at the expected FPS
	 */
	private int FPScounter = 0;

	/**
	 * Store the selected weapon
	 */
	private int weaponSelected = 9;

	/**
	 * Frame this panel is located in
	 */
	private JLayeredPane frame;

	/**
	 * The shop
	 */
	private ClientShop shop = null;

	/**
	 * Constructor for the client
	 */
	public Client(Socket socket, ClientInventory inventory, JLayeredPane frame)
	{
		Images.importImages();
		mySocket = socket;
		currentMessage = " ";
		this.inventory = inventory;
		this.frame = frame;
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
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
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
			int team = Integer.parseInt(tokens[4]);

			player = new ClientObject(id, x, y, image, team);

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
		addMouseMotionListener(this);

		direction = 'R';
	}

	/**
	 * Gets the amount of money the client has
	 */
	public int getMoney()
	{
		return inventory.getMoney();
	}

	public void decreaseMoney(int amount)
	{
		inventory.decreaseMoney(amount);
	}

	/**
	 * Print to the server
	 */
	public void print(String message)
	{
		output.println(message);
		output.flush();
	}

	/**
	 * Thread for running the actual game
	 * 
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
				startTime = System.currentTimeMillis();

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

							// Update the FPS counter
							if (FPScounter >= (1000.0 / ServerEngine.UPDATE_RATE + 0.5))
							{
								FPScounter = 0;
								currentFPS = (int) ((1000.0
										/ (System.currentTimeMillis() - startTime)
										* (1000.0 / ServerEngine.UPDATE_RATE) + 0.5));
								startTime = System.currentTimeMillis();
							}

							FPScounter++;
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
								player.setTeam(Integer
										.parseInt(tokens[++token]));
							}
							else if (world.contains(id))
							{
								ClientObject otherObject = world.get(id);
								if (otherObject != null)
								{
									otherObject.setX(Integer
											.parseInt(tokens[++token]));
									otherObject.setY(Integer
											.parseInt(tokens[++token]));
									otherObject.setImage(tokens[++token]);
									otherObject.setTeam(Integer
											.parseInt(tokens[++token]));
								}
								else
								{
									world.removeID(id);
								}
							}
							else
							{
								ClientObject otherObject = new ClientObject(
										id,
										Integer.parseInt(tokens[++token]),
										Integer.parseInt(tokens[++token]),
										tokens[++token],
										Integer.parseInt(tokens[++token]));
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
						else if (tokens[token].equals("I"))
						{
							System.out.println("Received an item");
							inventory.addItem(tokens[++token], tokens[++token],
									Integer.parseInt(tokens[++token]),Integer.parseInt(tokens[++token]));
							inventory.repaint();
						}
						else if(tokens[token].equals("V"))
						{
							if(Character.isDigit(tokens[token+1].charAt(0)))
							{
								if(shop != null)
								{
									shop.setVisible(false);
									frame.remove(shop);
									frame.invalidate();
									shop = null;
								}
								shop = new ClientShop(Client.this);
								int numItems = Integer.parseInt(tokens[++token]);
								for(int item = 0; item < numItems;item++)
									shop.addItem(tokens[++token], tokens[++token], Integer.parseInt(tokens[++token]), Integer.parseInt(tokens[++token]));
								frame.add(shop,JLayeredPane.PALETTE_LAYER);
								shop.revalidate();
								frame.setVisible(true);
								System.out.println("Added shop");
							}
							else if(shop != null)
								shop.addItem(tokens[++token], tokens[++token], Integer.parseInt(tokens[++token]), Integer.parseInt(tokens[++token]));

						}
					}

					// long delay = System.currentTimeMillis()
					// - startTime;

					// if (delay < 15)
					// {
					// try {
					// Thread.sleep((15-delay)-2);
					// } catch (InterruptedException e) {
					//
					// e.printStackTrace();
					// }
					// }

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

	public int getWeaponSelected()
	{
		return weaponSelected;
	}

	public boolean isShopOpen()
	{
		return shop != null;
	}

	public void setWeaponSelected(int weaponSelected)
	{
		if (this.weaponSelected != 9 && inventory.getEquippedWeapons()[this.weaponSelected] != null)
			inventory.getEquippedWeapons()[this.weaponSelected]
					.setBorder(BorderFactory.createEmptyBorder());

		if(weaponSelected != 9)
			inventory.getEquippedWeapons()[weaponSelected].setBorder(BorderFactory
					.createLineBorder(Color.white));
		output.println("W" + weaponSelected);
		output.flush();
		this.weaponSelected = weaponSelected;
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
		graphics.drawString("FPS: " + currentFPS, 20, 40);
		if (HP > 0)
		{
			graphics.setColor(Color.red);
			graphics.drawString("Health: " + HP, 20, 60);
		}
		else
		{
			if (justDied)
			{
				inventory.clear();
				justDied = false;
			}
			graphics.setColor(Color.black);
			graphics.drawString("YOU ARE DEAD. You may now fly around", 20, 60);
		}
	}

	@Override
	public void keyPressed(KeyEvent key)
	{

		if ((key.getKeyCode() == KeyEvent.VK_D || key.getKeyCode() == KeyEvent.VK_RIGHT)
				&& !currentMessage.equals("R"))
		{
			// R for right
			currentMessage = "R";
			print(currentMessage);
		}
		else if ((key.getKeyCode() == KeyEvent.VK_A || key.getKeyCode() == KeyEvent.VK_LEFT)
				&& !currentMessage.equals("L"))
		{
			// L for left
			currentMessage = "L";
			print(currentMessage);
		}
		else if ((key.getKeyCode() == KeyEvent.VK_W
				|| key.getKeyCode() == KeyEvent.VK_UP
				|| key.getKeyCode() == KeyEvent.VK_SPACE)
				&& !currentMessage.equals("U"))
		{
			// U for up
			currentMessage = "U";
			print(currentMessage);
		}
		else if ((key.getKeyCode() == KeyEvent.VK_S || key.getKeyCode() == KeyEvent.VK_DOWN)
				&& !currentMessage.equals("D"))
		{
			// D for down
			currentMessage = "D";
			print(currentMessage);
		}
		else if (key.getKeyCode() == KeyEvent.VK_P)
		{
			// P for ping
			ping = System.currentTimeMillis();
			print("P");
		}
		else if (key.getKeyCode() == KeyEvent.VK_1
				&& !currentMessage.equals("W0")
				&& inventory.getEquippedWeapons()[0] != null)
		{
			setWeaponSelected(0);
		}
		else if (key.getKeyCode() == KeyEvent.VK_2
				&& !currentMessage.equals("W1")
				&& inventory.getEquippedWeapons()[1] != null)
		{
			setWeaponSelected(1);
		}
		// Use these later
		else if (key.getKeyCode() == KeyEvent.VK_3
				&& !currentMessage.equals("W1")
				&& inventory.getEquippedWeapons()[2] != null)
		{
			setWeaponSelected(2);
		}
		else if (key.getKeyCode() == KeyEvent.VK_4
				&& !currentMessage.equals("W1")
				&& inventory.getEquippedWeapons()[3] != null)
		{
			setWeaponSelected(3);
		}
		else if(key.getKeyCode() == KeyEvent.VK_E)
		{
			print("E");
			if(shop != null)
			{
				shop.setVisible(false);
				frame.remove(shop);
				frame.invalidate();
				shop = null;
				//close the window
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent key)
	{

		if ((key.getKeyCode() == KeyEvent.VK_D || key.getKeyCode() == KeyEvent.VK_RIGHT)
				&& !currentMessage.equals("!R"))
		{
			currentMessage = "!R";
		}
		else if ((key.getKeyCode() == KeyEvent.VK_A || key.getKeyCode() == KeyEvent.VK_LEFT)
				&& !currentMessage.equals("!L"))
		{
			currentMessage = "!L";
		}
		else if ((key.getKeyCode() == KeyEvent.VK_W
				|| key.getKeyCode() == KeyEvent.VK_UP
				|| key.getKeyCode() == KeyEvent.VK_SPACE)
				&& !currentMessage.equals("!U"))
		{
			currentMessage = "!U";
		}
		else if ((key.getKeyCode() == KeyEvent.VK_S || key.getKeyCode() == KeyEvent.VK_DOWN)
				&& !currentMessage.equals("!D"))
		{
			currentMessage = "!D";
		}
		else if (key.getKeyCode() == KeyEvent.VK_P)
		{
			pingString = "LATENCY: (PRESS P)";
		}
		print(currentMessage);

	}

	@Override
	public void mousePressed(MouseEvent event)
	{
		if (event.getButton() == MouseEvent.BUTTON1
				&& currentMessage.charAt(0) != 'A')
		{
			// A for action
			currentMessage = "A " + event.getX() + " " + event.getY();

			print(currentMessage);
		}
	}

	@Override
	public void mouseReleased(MouseEvent event)
	{
		if (event.getButton() == MouseEvent.BUTTON1
				&& !currentMessage.equals("!A"))
		{
			currentMessage = "!A";

			print(currentMessage);
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

	@Override
	public void mouseDragged(MouseEvent event)
	{
		// Make the player face the direction of the mouse
		if (event.getX() > SCREEN_WIDTH / 2 && direction != 'R')
		{
			print("DR");
			direction = 'R';
		}
		else if (event.getX() < SCREEN_WIDTH / 2 && direction != 'L')
		{
			print("DL");
			direction = 'L';
		}

	}

	@Override
	public void mouseMoved(MouseEvent event)
	{
		// Make the player face the direction of the mouse
		if (event.getX() > SCREEN_WIDTH / 2 && direction != 'R')
		{
			print("DR");
			direction = 'R';
		}
		else if (event.getX() < SCREEN_WIDTH / 2 && direction != 'L')
		{
			print("DL");
			direction = 'L';
		}
	}

	public int getCurrentFPS()
	{
		return currentFPS;
	}

	public void setCurrentFPS(int currentFPS)
	{
		this.currentFPS = currentFPS;
	}

}
