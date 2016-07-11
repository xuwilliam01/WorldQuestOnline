
package Client;

import java.awt.Color; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Timer;
import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import Imports.Images;
import Server.ServerEngine;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerPlayer;

@SuppressWarnings("serial")
/**
 * The main client class that deals with server communication and outsources graphics to the client world
 * @author Alex Raita & William Xu
 *
 */
public class Client extends JPanel implements KeyListener, MouseListener,
ActionListener,
MouseMotionListener
{
	// Width and height of the screen
	public static int SCREEN_WIDTH = 1620;
	public static int SCREEN_HEIGHT = 1080;

	private Socket mySocket;
	private PrintWriter output;
	private BufferedReader input;

	private Thread gameThread;
	private long ping;
	private String pingString = "Ping:";

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

	// Stores the HP, mana, jump,and speed of the player
	private int HP;
	private int maxHP;
	private int mana;
	private int maxMana;
	private int speed;
	private int jump;
	private double armour;

	// Variables for damage
	private int damage = 0;
	private int baseDamage = 0;

	// Stats of each castle
	private int redCastleHP;
	private int redCastleTier;
	private int redCastleMoney;
	private int redCastleMaxHP;

	private int blueCastleHP;
	private int blueCastleTier;
	private int blueCastleMoney;
	private int blueCastleMaxHP;

	// Chat Components
	public final static int MAX_MESSAGES = 15;
	public final static int MAX_CHARACTERS = 100;
	private JTextField chat;
	private JButton enter;
	private ArrayList<String> chatQueue = new ArrayList<String>();
	
	/**
	 * Mouse's x coordinate
	 */
	private int mouseX=0;
	/**
	 * Mouse's y coordinate
	 */
	private int mouseY=0;

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
	 * All the leftover lines to read in to the client
	 */
	private ArrayList<String> lines = new ArrayList<String>();
	
	

	/**
	 * The name of the player
	 */
	private String playerName;

	/**
	 * A timer for the start
	 */
	private long startTimer = 0;

	/**
	 * Constructor for the client
	 */
	public Client(Socket socket, ClientInventory inventory, JLayeredPane frame,
			String playerName)
	{
		setBackground(Color.BLACK);
		Images.importImages();
		mySocket = socket;
		currentMessage = " ";
		this.playerName = playerName;
		this.inventory = inventory;
		this.frame = frame;

		chat = new JTextField();
		chat.setLocation(0, 0);
		chat.setSize(200, 20);
		chat.addKeyListener(new JTextFieldEnter());
		chat.setVisible(true);
		chat.setFocusable(true);
		chat.setDocument(new JTextFieldLimit(MAX_CHARACTERS));
		chat.setForeground(Color.GRAY);
		chat.setToolTipText("Press 'enter' as a shortcut to chat. Type '/t ' before a message to send it only to your team");

		enter = new JButton("Chat");
		enter.setLocation(200, 0);
		enter.setSize(60, 20);
		enter.setVisible(true);
		enter.addActionListener(this);
		enter.setBackground(new Color(240,240,240));

		setLayout(null);
		add(chat);
		add(enter);
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
		
		// Set the cursor transparent
//		Toolkit toolkit = Toolkit.getDefaultToolkit();
//		Cursor cursor = toolkit.createCustomCursor(Images.getImage("NOTHING"), new Point(
//				getX(), getY()), "img");
//		setCursor(cursor);
		
		
		setDoubleBuffered(true);
		setFocusable(true);
		requestFocusInWindow();
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		HP = ServerPlayer.PLAYER_START_HP;
		maxHP = HP;
		mana = ServerPlayer.PLAYER_START_MANA;
		maxMana = mana;

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
			output.println(playerName);
			output.flush();
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

			player = new ClientObject(id, x, y, image, team, 
					ServerWorld.PLAYER_TYPE);
		}
		catch (IOException e)
		{
			System.out.println("Error getting player from server");
			e.printStackTrace();
		}

		// Start the actual game
		gameThread = new Thread(new RunGame());
		gameThread.start();

		// Start the actual game
		gameThread = new Thread(new ReadServer());
		gameThread.start();

		System.out.println("Game started");

		direction = 'R';

		printToServer("s " + SCREEN_WIDTH + " " + SCREEN_HEIGHT);

		// Get the ping
		printToServer("P");
		
		// Add listeners AT THE END
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
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
	public void printToServer(String message)
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
	class ReadServer implements Runnable
	{
		@Override
		public void run()
		{

			while (true)
			{
				while (!lines.isEmpty())
				{
					String message = lines.remove(0);

					if (message != null)
					{
						String[] tokens = message.split(" ");
						
						for (int token = 1; token < tokens.length; token++)
						{
							try
							{
								// If our player has moved
								if (tokens[token].equals("L"))
								{
									HP = Integer.parseInt(tokens[++token]);
								}
								else if (tokens[token].equals("M"))
								{
									maxHP = Integer.parseInt(tokens[++token]);
								}
								else if (tokens[token].equals("Q"))
								{
									mana = Integer.parseInt(tokens[++token]);
								}
								else if (tokens[token].equals("B"))
								{
									// End the game
									int team = Integer
											.parseInt(tokens[++token]);
									String winner = "Red Team";
									String loser = "Blue Team";
									if (team == ServerPlayer.RED_TEAM)
									{
										winner = "Blue Team";
										loser = "Red Team";
									}

									JOptionPane
									.showMessageDialog(
											Client.this,
											String.format(
													"The %s castle has been destroyed, the winner is the %s!",
													loser, winner));
									input.close();
									output.close();
									if (inventory.getMenuButton() != null)
									{
										inventory.getMenuButton().doClick();
									}
									break;

								}
								else if (tokens[token].equals("K"))
								{
									maxMana = Integer.parseInt(tokens[++token]);
								}
								else if (tokens[token].equals("SI"))
								{
									String type = tokens[++token];
									inventory.removeThis(type);
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
									int x = Integer
											.parseInt(tokens[++token]);
									int y = Integer
											.parseInt(tokens[++token]);
									if (id == player.getID())
									{
										player.setX(x);
										player.setY(y);
									}
									if(tokens[token+4].equals("{"))
									{
										if (tokens[token+1].contains("ODARROW"))
										{
											System.out.println(tokens[token+1]);
										}
										
										world.setObject(id, x, y,
												tokens[++token], Integer
												.parseInt(tokens[++token]),
												tokens[++token], tokens[++token]);
									}
									else
									{
										int len = 0;
										try{
											len = Integer.parseInt(tokens[token+4]);
										}
										catch(NumberFormatException E)
										{
											System.out.println("Bug with {");
											token += 4;
											continue;
										}
										String name = "";
										for(int i = 0; i < len;i++)
										{
											name+= tokens[token+5+i]+" ";
										}
										world.setObject(id, x, y,
												tokens[++token], Integer
												.parseInt(tokens[++token]),
												tokens[++token], name.trim());
										token+= len;
									}

								}
								else if (tokens[token].equals("P"))
								{
									pingString = "Ping: "
											+ (System.currentTimeMillis() - ping);
									startTimer = System.currentTimeMillis();
								}

								// Remove an object after
								// disconnection/destruction
								else if (tokens[token].equals("R"))
								{
									int ID = Integer.parseInt(tokens[++token]);
									world.remove(ID);
								}
								else if (tokens[token].equals("I"))
								{
									System.out.println("Received an item");
									inventory.addItem(tokens[++token],
											tokens[++token],
											Integer.parseInt(tokens[++token]),
											Integer.parseInt(tokens[++token]));
									inventory.repaint();
								}
								else if (tokens[token].equals("D"))
								{
									damage = Integer.parseInt(tokens[++token]);
									baseDamage = Integer
											.parseInt(tokens[++token]);
								}
								else if (tokens[token].equals("S"))
								{
									speed = Integer.parseInt(tokens[++token]);
								}
								else if (tokens[token].equals("J"))
								{
									jump = Integer.parseInt(tokens[++token]);
								}
								else if (tokens[token].equals("A"))
								{
									armour = Double
											.parseDouble(tokens[++token]);
								}
								else if (tokens[token].equals("V"))
								{
									if (Character.isDigit(tokens[token + 1]
											.charAt(0)))
									{
										if (shop != null)
										{
											shop.setVisible(false);
											frame.remove(shop);
											frame.invalidate();
											shop = null;
										}
										shop = new ClientShop(Client.this);
										int numItems = Integer
												.parseInt(tokens[++token]);
										for (int item = 0; item < numItems; item++)
											shop.addItem(
													tokens[++token],
													tokens[++token],
													Integer.parseInt(tokens[++token]),
													Integer.parseInt(tokens[++token]));
										frame.add(shop,
												JLayeredPane.PALETTE_LAYER);
										shop.revalidate();
										frame.setVisible(true);
									}
									else if (shop != null)
										shop.addItem(
												tokens[++token],
												tokens[++token],
												Integer.parseInt(tokens[++token]),
												Integer.parseInt(tokens[++token]));

								}
								else if (tokens[token].equals("C"))
								{
									if (shop != null)
										closeShop();
								}
								else if (tokens[token].equals("CH"))
								{
									char who = tokens[++token].charAt(0);
									int nameLen = Integer.parseInt(tokens[++token]);
									String name = tokens[++token];

									for(int i = 1; i < nameLen;i++)
									{
										name+=" "+tokens[++token];
									}
									int numWords = Integer
											.parseInt(tokens[++token]);
									String text = "";
									for (int i = 0; i < numWords; i++)
									{
										text += tokens[++token] + " ";
									}
									if (chatQueue.size() >= MAX_MESSAGES)
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
									if (chatQueue.size() >= MAX_MESSAGES)
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
								else if(tokens[token].equals("JO"))
								{
									int len = Integer.parseInt(tokens[++token]);
									String name = "";

									for(int i = 0; i < len;i++)
										name += tokens[++token]+" ";
									chatQueue.add("JO "+name.trim());
								}
								else if(tokens[token].equals("RO"))
								{
									int len = Integer.parseInt(tokens[++token]);
									String name = "";

									for(int i = 0; i < len;i++)
										name += tokens[++token]+" ";
									chatQueue.add("RO "+name.trim());
								}
								else if (tokens[token].equals("XR"))
								{
									redCastleHP = Integer
											.parseInt(tokens[++token]);
									redCastleTier = Integer
											.parseInt(tokens[++token]);
									redCastleMoney = Integer
											.parseInt(tokens[++token]);
									redCastleMaxHP = Integer
											.parseInt(tokens[++token]);
								}
								else if (tokens[token].equals("XB"))
								{
									blueCastleHP = Integer
											.parseInt(tokens[++token]);
									blueCastleTier = Integer
											.parseInt(tokens[++token]);
									blueCastleMoney = Integer
											.parseInt(tokens[++token]);
									blueCastleMaxHP = Integer
											.parseInt(tokens[++token]);
								}
								else if (tokens[token].equals("T"))
								{
									world.setWorldTime(Integer
											.parseInt(tokens[++token]));
								}
							}
							catch (NumberFormatException e)
							{
								System.out.println("Java can't parse integers");
								e.printStackTrace();
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}

						}
					}
				}
				try
				{
					Thread.sleep(1);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Thread for running the actual game
	 * 
	 * @author William Xu && Alex Raita
	 *
	 */
	class RunGame implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				startTime = System.currentTimeMillis();

				while (true)
				{
					String message = System.currentTimeMillis()+" " + input.readLine();

					
					
					lines.add(message);

					// Update the ping after half a second
					if (startTimer >= 0
							&& System.currentTimeMillis() - startTimer >= 500)
					{
						ping = System.currentTimeMillis();
						printToServer("P");
						startTimer = -1;
					}

					try
					{
						Thread.sleep(1);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
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
	 * Close the shop
	 */
	public void closeShop()
	{
		shop.setVisible(false);
		frame.remove(shop);
		frame.invalidate();
		shop = null;
	}

	/**
	 * Get the shop
	 */
	public ClientShop getShop()
	{
		return shop;
	}

	/**
	 * Import the map
	 */
	private void importMap()
	{
		System.out.println("Importing the map from the server...");

		// Get the 2D grid from the server
		String gridSize="";

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

			world = new ClientWorld(grid, tileSize, this);
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

	/**
	 * Sets the weapon selected
	 * @param weaponSelected
	 */
	public void setWeaponSelected(int weaponSelected)
	{
		if (this.weaponSelected != 9
				&& inventory.getEquippedWeapons()[this.weaponSelected] != null)
			inventory.getEquippedWeapons()[this.weaponSelected]
					.setBorder(BorderFactory.createEmptyBorder());

		if (weaponSelected != 9)
			inventory.getEquippedWeapons()[weaponSelected]
					.setBorder(BorderFactory
							.createLineBorder(new Color(240,240,240)));
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
		
		// Update the map
				try {
					getWorld().update(graphics, getPlayer());
				} catch (NullPointerException e) {
					System.out.println("Null Pointer Exception for world.update");
				}

				// Draw the ping and the FPS
				graphics.setFont(ClientWorld.NORMAL_FONT);
				graphics.setColor(new Color(240, 240, 240));
				graphics.drawString(getPingString(), Client.SCREEN_WIDTH - 60, 20);
				graphics.drawString("FPS: " + getCurrentFPS(), Client.SCREEN_WIDTH - 60, 40);

				if (getWorld().getWorldTime()%60==0)
				{
					requestFocusInWindow();
				}
					
				
				// Set the time of day to be displayed
				// DAWN: 5AM - 9AM
				// DAY: 9AM - 5PM
				// DUSK: 5PM - 9PM
				// NIGHT: 9PM - 5AM

				if (getWorld() != null) {

					String timeOfDay = "DAY";

					if (getWorld().getWorldTime() >= ServerWorld.DAY_COUNTERS / 6 * 5) {
						timeOfDay = "DAWN";
					} else if (getWorld().getWorldTime() >= ServerWorld.DAY_COUNTERS / 2) {
						timeOfDay = "NIGHT";
					} else if (getWorld().getWorldTime() >= ServerWorld.DAY_COUNTERS / 3) {
						timeOfDay = "DUSK";
					}

					int hour = (getWorld().getWorldTime() / 60) + 9;
					if (hour >= 24) {
						hour -= 24;
					}
					int minute = getWorld().getWorldTime() % 60;

					String amPm = "AM";

					if (hour >= 12) {
						hour -= 12;
						amPm = "PM";
					}

					if (hour == 0) {
						hour = 12;
					}

					String hourString = "";
					String minuteString = "";

					if (hour < 10) {
						hourString = "0";
					}
					if (minute < 10) {
						minuteString = "0";
					}
					hourString += hour;
					minuteString += minute;

					graphics.drawString(hourString + ":" + minuteString + " " + amPm, Client.SCREEN_WIDTH - 60, 60);
					graphics.drawString(timeOfDay, Client.SCREEN_WIDTH - 60, 80);
				}

				// Draw the chat
				graphics.setFont(ClientWorld.NORMAL_FONT);

				while (true) {
					try {
						int textY = 40;
						for (String str : getChatQueue()) {
							if (str.substring(0, 2).equals("CH")) {
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
								graphics.drawString(mssg, 10 + graphics.getFontMetrics().stringWidth(coloured + " "), textY);
							} else if (str.substring(0, 2).equals("JO")) {
								if (str.charAt(3) - '0' == ServerCreature.RED_TEAM)
									graphics.setColor(Color.RED);
								else if (str.charAt(3) - '0' == ServerCreature.BLUE_TEAM)
									graphics.setColor(Color.BLUE);
								else
									graphics.setColor(Color.GRAY);
								graphics.drawString(str.substring(4) + " ", 10, textY);
								graphics.setColor(Color.ORANGE);
								graphics.drawString("joined the game",
										10 + graphics.getFontMetrics().stringWidth(str.substring(4) + " "), textY);
							} else if (str.substring(0, 2).equals("RO")) {
								if (str.charAt(3) - '0' == ServerCreature.RED_TEAM)
									graphics.setColor(Color.RED);
								else if (str.charAt(3) - '0' == ServerCreature.BLUE_TEAM)
									graphics.setColor(Color.BLUE);
								else
									graphics.setColor(Color.GRAY);
								graphics.drawString(str.substring(4) + " ", 10, textY);
								graphics.setColor(Color.ORANGE);
								graphics.drawString("left the game",
										10 + graphics.getFontMetrics().stringWidth(str.substring(4) + " "), textY);
							} else {
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
									graphics.drawString("was " + killWord + " by a ",
											5 + graphics.getFontMetrics().stringWidth(firstName), textY);
								else
									graphics.drawString(secondKillWord + " ",
											5 + graphics.getFontMetrics().stringWidth(firstName), textY);

								if (lastName.charAt(0) - '0' == ServerCreature.RED_TEAM)
									graphics.setColor(Color.RED);
								else if (lastName.charAt(0) - '0' == ServerCreature.BLUE_TEAM)
									graphics.setColor(Color.BLUE);
								else
									graphics.setColor(Color.GREEN);

								if (str.substring(0, 3).equals("KF1"))
									graphics.drawString(lastName.substring(1),
											8 + graphics.getFontMetrics().stringWidth(firstName + "was " + killWord + " by a "),
											textY);
								else
									graphics.drawString(lastName.substring(1),
											8 + graphics.getFontMetrics().stringWidth(firstName + secondKillWord + " "), textY);
							}
							textY += 20;
						}
						break;
					} catch (ConcurrentModificationException E) {

					}
				}

				if (getHP() > 0) {
					setJustDied(true);
				} else {
					if (isJustDied()) {
						getInventory().clear();
						setJustDied(false);
					}
					graphics.setColor(Color.black);
					graphics.setFont(ClientWorld.MESSAGE_FONT);
					graphics.drawString("YOU ARE DEAD. Please wait 10 seconds to respawn", 300, 20);
				}

				// Repaint the inventory
				getInventory().repaint();
				if (!getChat().hasFocus()) {
					requestFocusInWindow();
				}
				
				//graphics.drawImage(Images.getImage("Cursor"),mouseX,mouseY,null);
				
	}

	@Override
	public void keyPressed(KeyEvent key)
	{

		if ((key.getKeyCode() == KeyEvent.VK_D || key.getKeyCode() == KeyEvent.VK_RIGHT)
				&& !currentMessage.equals("R"))
		{
			// R for right
			currentMessage = "R";
			printToServer(currentMessage);
			System.out.println("Go right");
		}
		else if ((key.getKeyCode() == KeyEvent.VK_A || key.getKeyCode() == KeyEvent.VK_LEFT)
				&& !currentMessage.equals("L"))
		{
			// L for left
			currentMessage = "L";
			printToServer(currentMessage);
		}
		else if ((key.getKeyCode() == KeyEvent.VK_W
				|| key.getKeyCode() == KeyEvent.VK_UP
				|| key.getKeyCode() == KeyEvent.VK_SPACE)
				&& !currentMessage.equals("U"))
		{
			// U for up
			currentMessage = "U";
			printToServer(currentMessage);
		}
		else if ((key.getKeyCode() == KeyEvent.VK_S || key.getKeyCode() == KeyEvent.VK_DOWN)
				&& !currentMessage.equals("D"))
		{
			// D for down
			currentMessage = "D";
			printToServer(currentMessage);
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
		else if (key.getKeyCode() == KeyEvent.VK_E)
		{
			printToServer("E");
			if (shop != null)
			{
				closeShop();
			}
		}
		else if (key.getKeyCode() == KeyEvent.VK_ENTER)
		{
			chat.requestFocus();
		}
	}

	@Override
	public void keyReleased(KeyEvent key)
	{

		if ((key.getKeyCode() == KeyEvent.VK_D || key.getKeyCode() == KeyEvent.VK_RIGHT)
				&& !currentMessage.equals("!R"))
		{
			currentMessage = "!R";
			System.out.println("Leave right");
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
		printToServer(currentMessage);

	}

	@Override
	public void mousePressed(MouseEvent event)
	{
		// Make sure the player changes direction
		if (event.getX() > SCREEN_WIDTH / 2 + ServerPlayer.DEFAULT_HEIGHT / 2)
		{
			printToServer("DR");
			direction = 'R';
		}
		else if (event.getX() < SCREEN_WIDTH / 2 + ServerPlayer.DEFAULT_WIDTH
				/ 2)
		{
			printToServer("DL");
			direction = 'L';
		}

		if (event.getButton() == MouseEvent.BUTTON1
				&& currentMessage.charAt(0) != 'A')
		{
			// A for action
			currentMessage = "A " + event.getX() + " " + event.getY();

			printToServer(currentMessage);
			
			System.out.println("Pressed");
		}
		else if (event.getButton() == MouseEvent.BUTTON3
				&& currentMessage.charAt(0) != 'a')
		{
			// A for action
			currentMessage = "a " + event.getX() + " " + event.getY();

			printToServer(currentMessage);
		}
	}

	@Override
	public void mouseReleased(MouseEvent event)
	{
		if (event.getButton() == MouseEvent.BUTTON1
				&& !currentMessage.equals("!A"))
		{
			currentMessage = "!A";

			printToServer(currentMessage);
		}
		else if (event.getButton() == MouseEvent.BUTTON3
				&& !currentMessage.equals("!a"))
		{
			currentMessage = "!a";

			printToServer(currentMessage);
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
		mouseX = event.getX();
		mouseY = event.getY();
		// Make the player face the direction of the mouse
		if (event.getX() > SCREEN_WIDTH / 2 + ServerPlayer.DEFAULT_WIDTH / 2
				&& direction != 'R')
		{
			printToServer("DR");
			direction = 'R';
		}
		else if (event.getX() < SCREEN_WIDTH / 2 + ServerPlayer.DEFAULT_WIDTH
				/ 2
				&& direction != 'L')
		{
			printToServer("DL");
			direction = 'L';
		}

	}

	@Override
	public void mouseMoved(MouseEvent event)
	{
		mouseX = event.getX();
		mouseY = event.getY();
		// Make the player face the direction of the mouse
		if (event.getX() > SCREEN_WIDTH / 2 + ServerPlayer.DEFAULT_WIDTH / 2
				&& direction != 'R')
		{
			printToServer("DR");
			direction = 'R';
		}
		else if (event.getX() < SCREEN_WIDTH / 2 + ServerPlayer.DEFAULT_WIDTH
				/ 2
				&& direction != 'L')
		{
			printToServer("DL");
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

	public int getHP()
	{
		return HP;
	}

	public void setHP(int hP)
	{
		HP = hP;
	}

	public int getMaxHP()
	{
		return maxHP;
	}

	public void setMaxHP(int maxHP)
	{
		this.maxHP = maxHP;
	}

	public int getMana()
	{
		return mana;
	}

	public void setMana(int mana)
	{
		this.mana = mana;
	}

	public int getMaxMana()
	{
		return maxMana;
	}

	public void setMaxMana(int maxMana)
	{
		this.maxMana = maxMana;
	}

	public int getSpeed()
	{
		return speed;
	}

	public int getJump()
	{
		return jump;
	}

	public BufferedReader getInput()
	{
		return input;
	}

	public PrintWriter getOutput()
	{
		return output;
	}

	public int getDamage()
	{
		return damage;
	}

	public int getBaseDamage()
	{
		return baseDamage;
	}

	public double getArmour()
	{
		return armour;
	}

	public int getRedCastleHP()
	{
		return redCastleHP;
	}

	public int getBlueCastleHP()
	{
		return blueCastleHP;
	}

	public int getRedCastleTier()
	{
		return redCastleTier;
	}

	public void setRedCastleTier(int redCastleTier)
	{
		this.redCastleTier = redCastleTier;
	}

	public int getRedCastleMoney()
	{
		return redCastleMoney;
	}

	public void setRedCastleMoney(int redCastleMoney)
	{
		this.redCastleMoney = redCastleMoney;
	}

	public int getBlueCastleTier()
	{
		return blueCastleTier;
	}

	public void setBlueCastleTier(int blueCastleTier)
	{
		this.blueCastleTier = blueCastleTier;
	}

	public int getBlueCastleMoney()
	{
		return blueCastleMoney;
	}

	public void setBlueCastleMoney(int blueCastleMoney)
	{
		this.blueCastleMoney = blueCastleMoney;
	}

	public int getRedCastleMaxHP()
	{
		return redCastleMaxHP;
	}

	public void setRedCastleMaxHP(int redCastleMaxHP)
	{
		this.redCastleMaxHP = redCastleMaxHP;
	}

	public int getBlueCastleMaxHP()
	{
		return blueCastleMaxHP;
	}

	public void setBlueCastleMaxHP(int blueCastleMaxHP)
	{
		this.blueCastleMaxHP = blueCastleMaxHP;
	}

	/**
	 * Class to limit the number of characters in a JTextField
	 */
	public static class JTextFieldLimit extends PlainDocument
	{
		private int limit;

		public JTextFieldLimit(int limit)
		{
			super();
			this.limit = limit;
		}

		public void insertString(int offset, String str, AttributeSet attr)
				throws BadLocationException
		{
			if (str == null)
				return;

			if ((getLength() + str.length()) <= limit)
			{
				super.insertString(offset, str, attr);
			}
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		// Send the message
		String message = chat.getText();
		if (message.length() > 0)
		{
			printToServer("C " + message);
		}
		chat.setForeground(Color.GRAY);
		chat.setText("");
		requestFocusInWindow();

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

	public Socket getMySocket()
	{
		return mySocket;
	}

	public void setMySocket(Socket mySocket)
	{
		this.mySocket = mySocket;
	}

	public Thread getGameThread()
	{
		return gameThread;
	}

	public void setGameThread(Thread gameThread)
	{
		this.gameThread = gameThread;
	}

	public long getPing()
	{
		return ping;
	}

	public void setPing(long ping)
	{
		this.ping = ping;
	}

	public String getPingString()
	{
		return pingString;
	}

	public void setPingString(String pingString)
	{
		this.pingString = pingString;
	}

	public String getCurrentMessage()
	{
		return currentMessage;
	}

	public void setCurrentMessage(String currentMessage)
	{
		this.currentMessage = currentMessage;
	}

	public ClientObject getPlayer()
	{
		return player;
	}

	public void setPlayer(ClientObject player)
	{
		this.player = player;
	}

	public ClientWorld getWorld()
	{
		return world;
	}

	public void setWorld(ClientWorld world)
	{
		this.world = world;
	}

	public JTextField getChat()
	{
		return chat;
	}

	public void setChat(JTextField chat)
	{
		this.chat = chat;
	}

	public JButton getEnter()
	{
		return enter;
	}

	public void setEnter(JButton enter)
	{
		this.enter = enter;
	}

	public ArrayList<String> getChatQueue()
	{
		return chatQueue;
	}

	public void setChatQueue(ArrayList<String> chatQueue)
	{
		this.chatQueue = chatQueue;
	}

	public ClientInventory getInventory()
	{
		return inventory;
	}

	public void setInventory(ClientInventory inventory)
	{
		this.inventory = inventory;
	}

	public boolean isJustDied()
	{
		return justDied;
	}

	public void setJustDied(boolean justDied)
	{
		this.justDied = justDied;
	}

	public char getDirection()
	{
		return direction;
	}

	public void setDirection(char direction)
	{
		this.direction = direction;
	}

	public long getStartTime()
	{
		return startTime;
	}

	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}

	public int getFPScounter()
	{
		return FPScounter;
	}

	public void setFPScounter(int fPScounter)
	{
		FPScounter = fPScounter;
	}

	public JLayeredPane getFrame()
	{
		return frame;
	}

	public void setFrame(JLayeredPane frame)
	{
		this.frame = frame;
	}

	public ArrayList<String> getLines()
	{
		return lines;
	}

	public void setLines(ArrayList<String> lines)
	{
		this.lines = lines;
	}

	public String getPlayerName()
	{
		return playerName;
	}

	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}

	public long getStartTimer()
	{
		return startTimer;
	}

	public void setStartTimer(long startTimer)
	{
		this.startTimer = startTimer;
	}

	public void setOutput(PrintWriter output)
	{
		this.output = output;
	}

	public void setInput(BufferedReader input)
	{
		this.input = input;
	}

	public void setSpeed(int speed)
	{
		this.speed = speed;
	}

	public void setJump(int jump)
	{
		this.jump = jump;
	}

	public void setArmour(double armour)
	{
		this.armour = armour;
	}

	public void setDamage(int damage)
	{
		this.damage = damage;
	}

	public void setBaseDamage(int baseDamage)
	{
		this.baseDamage = baseDamage;
	}

	public void setRedCastleHP(int redCastleHP)
	{
		this.redCastleHP = redCastleHP;
	}

	public void setBlueCastleHP(int blueCastleHP)
	{
		this.blueCastleHP = blueCastleHP;
	}

	public void setShop(ClientShop shop)
	{
		this.shop = shop;
	}

	
}
