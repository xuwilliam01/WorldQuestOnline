package Server;

import Client.ClientWorld;
import Client.Client.JTextFieldLimit;
import Imports.ImageReferencePair;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerPlayer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
/**
 * Displays a scrollable map of the server
 * @author Alex Raita & William Xu
 *
 */
public class ServerGUI extends JPanel implements KeyListener,
MouseWheelListener, MouseListener, MouseMotionListener, ActionListener
{

	/**
	 * Reference to the game world
	 */
	private ServerWorld world;

	/**
	 * Reference to the game engine
	 */
	private ServerEngine engine;

	/**
	 * Reference to the server
	 */
	private Server server;

	/**
	 * If the map is being shown or not
	 */
	boolean visible = false;

	// Variables for chat
	private ArrayList<String> chatQueue = new ArrayList<String>();
	private JTextField chat;
	private JButton enter;

	// Button to show/hide map
	private JButton showHide = new JButton("Show Map");

	/**
	 * Grid of all the tiles
	 */
	private char[][] grid;
	
	/**
	 * Grid of the sidelength of all tiles
	 */
	private int[][] sideLengthGrid;
	
	/**
	 * Grid of the column to skip to if reaching a blank tile
	 * covered by a larger merged tile
	 */
	private int[][] columnSkipGrid;
	
	/**
	 * Position of the map when viewing the world
	 */
	private int posX = 200;

	/**
	 * Position of the map when viewing the world
	 */
	private int posY = 300;

	/**
	 * A color that java doesn't provide
	 */
	public static final Color SKY = new Color(51, 255, 255);

	/**
	 * A color that java doesn't provide
	 */
	public static final Color SOLID_BLOCK = new Color(112, 112, 112);

	/**
	 * A color that java doesn't provide
	 */
	public static final Color BACKGROUND_BLOCK = new Color(176, 176, 176);

	/**
	 * The color of a player
	 */
	public static final Color PLAYER = Color.black;

	/**
	 * The color of an NPC
	 */
	public static final Color NPC = Color.darkGray;

	/**
	 * The color of a projectile
	 */
	public static final Color PROJECTILE = Color.blue;

	/**
	 * The color of an item
	 */
	public static final Color ITEM = Color.yellow;

	/**
	 * The color of other objects
	 */
	public static final Color OTHER = Color.gray;

	/**
	 * The factor of the scale of the object on the map compared to its actual
	 * height and width (can be changed by scrolling mouse wheel)
	 */
	private double objectFactor;

	/**
	 * The name of the map
	 */
	private String map = "";

	/**
	 * X-value of the centre of the screen
	 */
	public static final int CENTRE_X = Client.Client.SCREEN_WIDTH
			/ ServerFrame.FRAME_FACTOR / 2;

	/**
	 * Y-value of the centre of the screen
	 */
	public static final int CENTRE_Y = Client.Client.SCREEN_HEIGHT
			/ ServerFrame.FRAME_FACTOR / 2;

	/**
	 * The x-coordinate of where the mouse began to be dragged from
	 */
	private int dragSourceX;

	/**
	 * The y-coordinate of where the mouse began to be dragged from
	 */
	private int dragSourceY;

	//private Image background = Images.getImage("SKY");

	// Movement booleans
	private boolean up = false;
	private boolean down = false;
	private boolean left = false;
	private boolean right = false;

	private boolean started = false;

	public ServerGUI(Server server)
	{
		this.server = server;

		// Set up chat components
		chat = new JTextField();
		chat.setLocation(0, 0);
		chat.setSize(200, 20);
		chat.setVisible(true);
		chat.setFocusable(true);
		chat.addKeyListener(new JTextFieldEnter());
		chat.setDocument(new JTextFieldLimit(Client.Client.MAX_CHARACTERS));
		chat.setForeground(Color.GRAY);

		enter = new JButton("Chat");
		enter.setLocation(200, 0);
		enter.setSize(60, 20);
		enter.setVisible(true);
		enter.addActionListener(this);

		setLayout(null);
		add(chat);
		add(enter);
		addKeyListener(this);

		// Set the scale of objects
		objectFactor = ServerFrame.FRAME_FACTOR * 8;

		// Create the screen
		setDoubleBuffered(true);
		setBackground(new Color(152,227,250));
		setSize(Client.Client.SCREEN_WIDTH / ServerFrame.FRAME_FACTOR,
				Client.Client.SCREEN_HEIGHT / ServerFrame.FRAME_FACTOR);

		setFocusable(true);
		requestFocusInWindow();

		// Show/hide map button
		showHide.setLocation(10, Client.Client.SCREEN_HEIGHT
				/ ServerFrame.FRAME_FACTOR - 75);
		showHide.setSize(200, 30);
		showHide.setVisible(true);
		add(showHide);
	}

	/**
	 * Start the game
	 */
	public void startGame(ServerWorld world, ServerEngine engine)
	{
		started = true;

		remove(showHide);
		showHide.addActionListener(this);
		add(showHide);

		// Set world, engine and grid
		this.world = world;
		this.engine = engine;

		//this.grid = world.getGrid();
		//this.columnSkipGrid = new int[grid.length][grid[0].length];
		//this.sideLengthGrid = new int[grid.length][grid[0].length];
		this.setUpTileGrids();
		
		// Add key, mouse wheel listener and repaint timer
		addMouseWheelListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	private void setUpTileGrids()
	{
		this.grid = world.getGrid();
		this.columnSkipGrid = new int[grid.length][grid[0].length];
		this.sideLengthGrid = new int[grid.length][grid[0].length];
		for (int row = 0; row < this.sideLengthGrid.length; row++)
		{
			for (int col = 0; col < this.sideLengthGrid[0].length; col++)
			{
				this.sideLengthGrid[row][col] = 1;
			}
		}
		
		// The column of the empty tile we want to add a skip to
		// Set to -1 when there's nothing currently
		int savedCol = -1;
		
		// Go through once to set up skips for rows of empty tiles
		for (int row = 0; row < grid.length; row++)
		{
			for (int col = 0; col < grid[0].length; col++)
			{
				if (this.grid[row][col] == ' ' && savedCol == -1)
				{
					savedCol = col;
				}
				else if (savedCol != -1)
				{
					// Skip to this tile from the saved empty tile
					// therefore skipping a row of empty tiles
					this.columnSkipGrid[row][savedCol] = col;
					savedCol = -1;
				}
			}
		}
		
		// Go through and merge squares of tiles into one
		// as well as skips for already merged tiles
		for (int row = 0; row < grid.length; row++)
		{
			for (int col = 0; col < grid[0].length; col++)
			{
				char tileType = grid[row][col];
				if (this.columnSkipGrid[row][col] != 0)
				{
					col = this.columnSkipGrid[row][col];
				}
				else if (tileType != ' ')
				{
					int sideLength = 1;
					
					checkLoop:
					while (true)
					{
						if (row + sideLength >= grid.length || col + sideLength >= grid[0].length)
						{
							break checkLoop;
						}
						
						for (int checkCol = col; checkCol <= col + sideLength; checkCol++)
						{
							// Check if the tile is the same as the one we're trying to expand
							// and also if the tile is already covered by an above merged tile
							if (grid[row + sideLength][checkCol] != tileType || 
									this.columnSkipGrid[row + sideLength][checkCol] != 0)
							{
								break checkLoop;
							}
						}
						
						for (int checkRow = row; checkRow <= row + sideLength; checkRow++)
						{
							// Don't need if the tile is covered because it won't be,
							// since it's below the one we're currently checking
							if (grid[checkRow][col + sideLength] != tileType || 
									this.columnSkipGrid[checkRow][col + sideLength] != 0)
							{
								break checkLoop;
							}
						}
						
						// Set up the skips
						this.columnSkipGrid[row][col + 1] = col + sideLength;
						this.columnSkipGrid[row + sideLength][col] = col + sideLength;
						
						sideLength++;
						
						// When it gets really big it glitches out for some reason
						if (sideLength >= 20)
						{
							break checkLoop;
						}
					}
					
					this.sideLengthGrid[row][col] = sideLength;
				}
			}
		}
	}

	/**
	 * Draw the world map
	 */
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		// graphics.drawImage(background, 0, -300, null);

		// Draw the map
		if (visible & started)
		{
			// Draw each tile on the screen
			int startRow = (int) ((posY - CENTRE_Y - 150) / (ServerWorld.TILE_SIZE / objectFactor));
			if (startRow < 0)
			{
				startRow = 0;
			}
			int endRow = (int) ((CENTRE_Y + posY + 5) / (ServerWorld.TILE_SIZE / objectFactor));
			if (endRow >= grid.length)
			{
				endRow = grid.length - 1;
			}
			int startCol = (int) ((posX - CENTRE_X - 150) / (ServerWorld.TILE_SIZE / objectFactor));
			if (startCol < 0)
			{
				startCol = 0;
			}
			int endCol = (int) ((CENTRE_X + posX + 5) / (ServerWorld.TILE_SIZE / objectFactor));
			if (endCol >= grid[0].length)
			{
				endCol = grid[0].length - 1;
			}
			for (int row = startRow; row <= endRow; row++)
			{
				for (int col = startCol; col <= endCol; col++)
				{
					if (this.columnSkipGrid[row][col] > 0)
					{
						col = this.columnSkipGrid[row][col];
					}
					else if (grid[row][col] != ' ')
					{
						Color color = ImageReferencePair.getImages()[grid[row][col]].getColor();
						graphics.setColor(color);
						
						int x = (int) (CENTRE_X + col * (ServerWorld.TILE_SIZE / objectFactor) - posX) + 1;
						int y = (int) (CENTRE_Y + row * (ServerWorld.TILE_SIZE / objectFactor) - posY) + 1;
						int width = ((int) (ServerWorld.TILE_SIZE / objectFactor) + 1) * (this.sideLengthGrid[row][col]);
						int height = ((int) (ServerWorld.TILE_SIZE / objectFactor) + 1) * (this.sideLengthGrid[row][col]);
						
						if (x + width >= 0 && y + height >= 0 && x <= Client.Client.SCREEN_WIDTH / ServerFrame.FRAME_FACTOR
								&& y <= Client.Client.SCREEN_HEIGHT / ServerFrame.FRAME_FACTOR)
						{
							graphics.fillRect(x, y, width, height);
						}
					}
				}
			}

			// Draw each object on the gui if it's inside the screen
			for (ServerObject object : world.getObjects().values())
			{
				if (object.isMapVisible()
						&&
						((CENTRE_X + object.getX() / objectFactor - posX)
								+ 1
								+ (object.getWidth() / objectFactor) + 1) > 0
								&& ((CENTRE_X + object.getX() / objectFactor - posX) + 1) < Client.Client.SCREEN_WIDTH
								&& ((CENTRE_Y + object.getY() / objectFactor - posY)
										+ 1
										+ (object.getHeight() / objectFactor) + 1) > 0
										&& ((CENTRE_Y + object.getY() / objectFactor - posY) + 1) < Client.Client.SCREEN_HEIGHT)
				{
					if (object.getType().charAt(0) == ServerWorld.PROJECTILE_TYPE)
					{
						graphics.setColor(PROJECTILE);
					}
					else if (object.getType().charAt(0) == ServerWorld.ITEM_TYPE)
					{
						graphics.setColor(ITEM);
					}
					else if (object.getType().contains(ServerWorld.NPC_TYPE))
					{
						graphics.setColor(NPC);
					}
					else if (object.getType().contains(ServerWorld.PLAYER_TYPE))
					{
						graphics.setColor(PLAYER);
					}
					else
					{
						graphics.setColor(OTHER);
					}
					graphics.fillRect(
							(int) (CENTRE_X + object.getX() / objectFactor - posX) + 1,
							(int) (CENTRE_Y + object.getY() / objectFactor - posY) + 1,
							(int) (object.getWidth() / objectFactor) + 1,
							(int) (object.getHeight() / objectFactor) + 1);
				}
			}
		}
		// else
		// {
		graphics.setColor(Color.BLACK);
		graphics.setFont(Client.ClientWorld.NORMAL_FONT);
		graphics.drawString("Showing the map may slow down your server!",
				250, Client.Client.SCREEN_HEIGHT / ServerFrame.FRAME_FACTOR
				- 55);
		// }
		// Draw the chat and the map name
		graphics.setFont(ClientWorld.NORMAL_FONT);
		graphics.setColor(Color.GRAY);
		graphics.drawString("Map: ", 270, 15);
		graphics.setColor(new Color(235, 117, 0));
		if (map != null)
		{
			graphics.drawString(map, 270 + graphics.getFontMetrics()
					.stringWidth("Map: "), 15);
		}
		else
		{
			graphics.drawString("In Lobby", 270 + graphics.getFontMetrics()
			.stringWidth("Map: "), 15);
		}
		while (true)
		{
			try
			{
				int textY = 40;
				for (String str : chatQueue)
				{
					if (str.substring(0, 2).equals("CH"))
					{
						String newStr = str.substring(3);
						int space = newStr.indexOf(':');
						String coloured = newStr.substring(1, space + 1);
						String mssg = newStr.substring(space + 2);
						if (newStr.charAt(0) - '0' == ServerCreature.RED_TEAM)
							graphics.setColor(Color.RED);
						else if (newStr.charAt(0) - '0' == ServerCreature.BLUE_TEAM)
							graphics.setColor(Color.BLUE);
						else
							graphics.setColor(Color.GRAY);
						graphics.drawString(coloured + " ", 10, textY);
						graphics.setColor(Color.YELLOW);
						graphics.drawString(mssg, 10 + graphics
								.getFontMetrics().stringWidth(coloured + " "),
								textY);
					}
					else if (str.substring(0, 2).equals("JO"))
					{
						if (str.charAt(3) - '0' == ServerCreature.RED_TEAM)
							graphics.setColor(Color.RED);
						else if (str.charAt(3) - '0' == ServerCreature.BLUE_TEAM)
							graphics.setColor(Color.BLUE);
						else
							graphics.setColor(Color.GRAY);
						graphics.drawString(str.substring(4) + " ", 10, textY);
						graphics.setColor(Color.ORANGE);
						graphics.drawString(
								"joined the game",
								10 + graphics.getFontMetrics().stringWidth(
										str.substring(4) + " "), textY);
					}
					else if (str.substring(0, 2).equals("RO"))
					{
						if (str.charAt(3) - '0' == ServerCreature.RED_TEAM)
							graphics.setColor(Color.RED);
						else if (str.charAt(3) - '0' == ServerCreature.BLUE_TEAM)
							graphics.setColor(Color.BLUE);
						else
							graphics.setColor(Color.GRAY);
						graphics.drawString(str.substring(4) + " ", 10, textY);
						graphics.setColor(Color.ORANGE);
						graphics.drawString(
								"left the game",
								10 + graphics.getFontMetrics().stringWidth(
										str.substring(4) + " "), textY);
					}
					else if (str.length() > 2
							&& str.substring(0, 3).equals("KF1")
							|| str.substring(0, 3).equals("KF2"))
					{
						String[] split = str.split(" ");
						int firstLen = Integer.parseInt(split[1]);
						String firstName = "";
						for (int i = 0; i < firstLen; i++)
							firstName += split[i + 2] + " ";

						int secondLen = Integer.parseInt(split[firstLen + 2]);
						String lastName = "";
						for (int i = 0; i < secondLen; i++)
							lastName += split[firstLen + 3 + i] + " ";

						if (firstName.charAt(0) - '0' == ServerCreature.RED_TEAM)
							graphics.setColor(Color.RED);
						else if (firstName.charAt(0) - '0' == ServerCreature.BLUE_TEAM)
							graphics.setColor(Color.BLUE);
						else
							graphics.setColor(Color.DARK_GRAY);
						graphics.drawString(firstName.substring(1), 10, textY);

						graphics.setColor(Color.ORANGE);

						String killWord = "slain";
						String secondKillWord = "killed";

						// int random = (int) (Math.random() * 5);

						// if (random == 0)
						// {
						// killWord = "slain";
						// secondKillWord = "slayed";
						// }
						// else if (random == 1)
						// {
						// killWord = "defeated";
						// secondKillWord = "defeated";
						// }
						// else if (random == 2)
						// {
						// killWord = "murdered";
						// secondKillWord = "murdered";
						// }
						// else if (random == 3)
						// {
						// killWord = "slaughtered";
						// secondKillWord = "slaughtered";
						// }
						// else if (random == 4)
						// {
						// killWord = "ended";
						// secondKillWord = "ended";
						// }

						if (str.substring(0, 3).equals("KF1"))
							graphics.drawString(
									"was " + killWord + " by a ",
									5 + graphics.getFontMetrics().stringWidth(
											firstName), textY);
						else
							graphics.drawString(
									secondKillWord + " ",
									5 + graphics.getFontMetrics().stringWidth(
											firstName), textY);

						if (lastName.charAt(0) - '0' == ServerCreature.RED_TEAM)
							graphics.setColor(Color.RED);
						else if (lastName.charAt(0) - '0' == ServerCreature.BLUE_TEAM)
							graphics.setColor(Color.BLUE);
						else
							graphics.setColor(Color.GREEN);

						if (str.substring(0, 3).equals("KF1"))
							graphics.drawString(
									lastName.substring(1),
									8 + graphics.getFontMetrics().stringWidth(
											firstName + "was " + killWord
											+ " by a "), textY);
						else
							graphics.drawString(
									lastName.substring(1),
									8 + graphics.getFontMetrics().stringWidth(
											firstName + secondKillWord + " "),
											textY);
					}
					textY += 20;
				}
				break;
			}
			catch (ConcurrentModificationException E)
			{
				System.out.println("concurrent modification");
			}
		}
		graphics.setColor(Color.gray);
		// Show the fps
		try
		{
			graphics.drawString("FPS: " + engine.getCurrentFPS(), 270, 35);
		}
		catch (NullPointerException e)
		{
		}

		// Write the player names for each team
		graphics.setFont(Client.ClientWorld.BIG_NORMAL_FONT);
		graphics.setColor(Color.BLUE);
		int blueX = Client.Client.SCREEN_WIDTH / ServerFrame.FRAME_FACTOR - 400;
		int redX = Client.Client.SCREEN_WIDTH / ServerFrame.FRAME_FACTOR - 200;
		graphics.drawString("Blue Team", blueX, 50);
		graphics.setColor(Color.RED);
		graphics.drawString("Red Team", redX, 50);
		int redStart = 80;
		int blueStart = 80;
		graphics.setFont(Client.ClientWorld.NORMAL_FONT);
		try
		{
			if (started)
				for (ServerPlayer player : engine.getListOfPlayers())
				{
					if (player.getTeam() == ServerCreature.RED_TEAM)
					{
						graphics.setColor(Color.RED);
						graphics.drawString(player.getName(), redX + 5,
								redStart);
						redStart += 20;
					}
					else
					{
						graphics.setColor(Color.BLUE);
						graphics.drawString(player.getName(), blueX + 5,
								blueStart);
						blueStart += 20;
					}
				}
			else
				for (ServerLobbyPlayer player : server.getPlayers())
				{
					if (player.getTeam() == ServerCreature.RED_TEAM)
					{
						graphics.setColor(Color.RED);
						graphics.drawString(player.getName(), redX + 5,
								redStart);
						redStart += 20;
					}
					else
					{
						graphics.setColor(Color.BLUE);
						graphics.drawString(player.getName(), blueX + 5,
								blueStart);
						blueStart += 20;
					}
				}
		}
		catch (ConcurrentModificationException E)
		{

		}
		// Tell the user to scroll with arrow keys
		graphics.setColor(Color.black);
		// graphics.drawString(
		// "Use mouse or arrows keys to move around the map, zoom with the mouse wheel",
		// 100, 25);
		// try
		// {
		// graphics.drawString(
		// "Server FPS: " + engine.getCurrentFPS(),
		// 120, 20);
		// }
		// catch (NullPointerException E)
		// {
		// System.out.println("Cannot get");
		// }
	}

	/**
	 * Adds a message to the chat
	 */
	public void addToChat(String message)
	{
		String[] tokens = message.split(" ");
		int token = 0;

		if (tokens[token].equals("CH"))
		{
			char who = tokens[++token].charAt(0);
			int nameLen = Integer.parseInt(tokens[++token]);
			String name = tokens[++token];

			for (int i = 1; i < nameLen; i++)
			{
				name += " " + tokens[++token];
			}
			int numWords = Integer
					.parseInt(tokens[++token]);
			String text = "";
			for (int i = 0; i < numWords; i++)
			{
				text += tokens[++token] + " ";
			}
			if (chatQueue.size() >= Client.Client.MAX_MESSAGES)
				chatQueue.remove(0);
			if (who == 'E')
				chatQueue.add("CH " + name + ": "
						+ text.trim());
			else
				chatQueue.add("CH " + name + "[TEAM]: "
						+ text.substring(2).trim());

		}
		else if (tokens[token].equals("KF1")
				|| tokens[token].equals("KF2"))
		{
			if (chatQueue.size() >= Client.Client.MAX_MESSAGES)
				chatQueue.remove(0);
			String text = "";
			int amount = Integer
					.parseInt(tokens[token + 1]) + 2;
			for (int i = 0; i < amount; i++, token++)
			{
				text += tokens[token] + " ";
			}

			amount = Integer.parseInt(tokens[token]) + 1;
			for (int i = 0; i < amount; i++, token++)
			{
				text += tokens[token] + " ";
			}
			chatQueue.add(text.trim());
		}
		else if (tokens[token].equals("JO") && !started)
		{
			int len = Integer.parseInt(tokens[++token]);
			String name = "";

			for (int i = 0; i < len; i++)
				name += tokens[++token] + " ";
			chatQueue.add("JO " + name.trim());
		}
		else if (tokens[token].equals("RO"))
		{
			int len = Integer.parseInt(tokens[++token]);
			String name = "";

			for (int i = 0; i < len; i++)
				name += tokens[++token] + " ";
			chatQueue.add("RO " + name.trim());
		}
	}

	/**
	 * Scroll the map around
	 */
	public void keyPressed(KeyEvent key)
	{
		if (visible)
		{
			if (key.getKeyCode() == KeyEvent.VK_RIGHT)
			{
				right = true;
			}
			else if (key.getKeyCode() == KeyEvent.VK_LEFT)
			{
				left = true;
			}
			else if (key.getKeyCode() == KeyEvent.VK_UP)
			{
				up = true;
			}
			else if (key.getKeyCode() == KeyEvent.VK_DOWN)
			{
				down = true;
			}
		}
		else if (key.getKeyCode() == KeyEvent.VK_ENTER)
		{
			chat.requestFocusInWindow();
		}
	}

	/**
	 * Stop scrolling the map around
	 */
	public void keyReleased(KeyEvent key)
	{
		if (key.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			right = false;
		}
		else if (key.getKeyCode() == KeyEvent.VK_LEFT)
		{
			left = false;
		}
		else if (key.getKeyCode() == KeyEvent.VK_UP)
		{
			up = false;
		}
		else if (key.getKeyCode() == KeyEvent.VK_DOWN)
		{
			down = false;
		}

	}

	/**
	 * Unused but required method
	 */
	public void keyTyped(KeyEvent arg0)
	{
	}

	/**
	 * Scroll the minimap
	 */
	public void movePos()
	{
		if (right)
		{
			posX += 10;
		}
		else if (left)
		{
			posX -= 10;
		}
		if (up)
		{
			posY -= 10;
		}
		else if (down)
		{
			posY += 10;
		}
	}

	/**
	 * Update the map
	 */
	public void update()
	{
		// Move and repaint
		if (!chat.hasFocus())
			requestFocusInWindow();
		movePos();
		if (world.getWorldCounter() < 10 || world.getWorldCounter() % 4 == 0)
		{
			repaint();
		}
	}

	@Override
	/**
	 * Zoom in and out of the map by changing the factor at which to scale objects
	 */
	public void mouseWheelMoved(MouseWheelEvent scroll)
	{
		if (visible)
		{
			int notches = scroll.getWheelRotation();

			if (notches > 0)
			{
				if (objectFactor * (1.1 * (notches)) < ServerFrame.FRAME_FACTOR * 16)
				{
					objectFactor *= (1.1 * (notches));
					posX /= (1.1 * notches);
					posY /= (1.1 * notches);
				}
				else
				{
					posX /= ServerFrame.FRAME_FACTOR * 16 / objectFactor;
					posY /= ServerFrame.FRAME_FACTOR * 16 / objectFactor;
					objectFactor = ServerFrame.FRAME_FACTOR * 16;
				}
			}
			else if (notches < 0)
			{
				if (objectFactor / (1.1 * (-notches)) > ServerFrame.FRAME_FACTOR * 3)
				{
					objectFactor /= (1.1 * (-notches));
					posX *= 1.1 * -notches;
					posY *= 1.1 * -notches;
				}
				else
				{
					posX /= ServerFrame.FRAME_FACTOR * 3 / objectFactor;
					posY /= ServerFrame.FRAME_FACTOR * 3 / objectFactor;
					objectFactor = ServerFrame.FRAME_FACTOR * 3;
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event)
	{
		if (event.getButton() == MouseEvent.BUTTON1 && visible)
		{
			dragSourceX = event.getX();
			dragSourceY = event.getY();
		}

	}

	// UNUSED BUT REQUIRED METHODS
	@Override
	public void mouseReleased(MouseEvent arg0)
	{
	}

	@Override
	public void mouseClicked(MouseEvent event)
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
	/**
	 * Drag the map around with the mouse
	 */
	public void mouseDragged(MouseEvent event)
	{
		if (visible)
		{
			posX -= event.getX() - dragSourceX;
			posY -= event.getY() - dragSourceY;
			dragSourceX = event.getX();
			dragSourceY = event.getY();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	public double getObjectFactor()
	{
		return objectFactor;
	}

	public void setObjectFactor(double objectFactor)
	{
		this.objectFactor = objectFactor;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{

		if (e.getSource() == enter)
		{
			// Send the message
			String message = chat.getText();
			if (message.length() > 0)
			{
				if(engine!= null)
					engine.broadcast("CH E 1 " + ServerCreature.NEUTRAL
							+ "Server " + message.split(" ").length + " "
							+ message);
				else
					server.broadcast("CH E 1 " + ServerCreature.NEUTRAL
							+ "Server " + message.split(" ").length + " "
							+ message);
			}
			chat.setForeground(Color.GRAY);
			chat.setText("");
			requestFocusInWindow();
		}
		else if (e.getSource() == showHide)
		{
			if (showHide.getText().equals("Show Map"))
				showHide.setText("Hide Map");
			else
				showHide.setText("Show Map");
			visible = !visible;
		}

	}

	private class JTextFieldEnter implements KeyListener
	{
		@Override
		public void keyTyped(KeyEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void keyPressed(KeyEvent e)
		{
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
				enter.doClick();

		}

		@Override
		public void keyReleased(KeyEvent e)
		{
			// TODO Auto-generated method stub

		}

	}

	public void setMap(String map)
	{
		this.map = map;
	}
}
