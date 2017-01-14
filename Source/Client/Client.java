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
import java.io.StringWriter;
import java.net.Socket;
import java.net.SocketException;
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

import com.sun.corba.se.spi.activation.Server;

import ClientUDP.ClientAccountWindow;
import Imports.Audio;
import Imports.Images;
import Server.ServerEngine;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerPlayer;
import Server.Items.ServerPotion;

@SuppressWarnings("serial")
/**
 * The main client class that deals with server communication and outsources
 * graphics to the client world
 * 
 * @author Alex Raita & William Xu
 *
 */
public class Client extends JPanel implements KeyListener, MouseListener, ActionListener, MouseMotionListener {
	// Width and height of the screen
	public static int SCREEN_WIDTH = 1620;
	public static int SCREEN_HEIGHT = 1080;

	// Constant used for sound
	public static float distanceConstant;

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
	 * Current hSpeed of the player
	 */
	private double hSpeed = 0;

	/**
	 * Current vSpeed of the player
	 */
	private double vSpeed = 0;

	/**
	 * Current PRECISE x coordinate of the player
	 */
	private double playerX = 0;

	/**
	 * Current PRECISE y coordinate of the player
	 */
	private double playerY = 0;

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
	private int redCastleXP;
	private int redPop;
	private int redPopLimit;

	private int blueCastleHP;
	private int blueCastleTier;
	private int blueCastleMoney;
	private int blueCastleMaxHP;
	private int blueCastleXP;
	private int bluePop;
	private int bluePopLimit;

	// Chat Components
	public final static int MAX_MESSAGES = 15;
	public final static int MAX_CHARACTERS = 100;
	private JTextField chat;
	private JButton enter;
	private ArrayList<String> chatQueue = new ArrayList<String>();

	// Scoreboard
	ClientScoreBoard scoreboard = new ClientScoreBoard();
	/**
	 * Mouse's x coordinate
	 */
	private int mouseX = 0;
	/**
	 * Mouse's y coordinate
	 */
	private int mouseY = 0;

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
	 * The startTime for checking FPS (for server reading)
	 */
	private long startTime2 = 0;

	/**
	 * A counter updating every repaint and reseting at the expected FPS (for
	 * server reading)
	 */
	private int FPScounter2 = 0;

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
	private ClientCastleShop castleShop = null;

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

	private boolean writingMessage = false;

	public boolean leaveGame;

	private int deathTime = 0;
	private float fillAmount = 0;

	private boolean isDropping = false;
	private boolean inAction = false;
	private boolean onSurface = false;

	private static long packetNo = 0;

	private int respawnTime = 10;
	
	public static boolean inGame = false;

	/**
	 * Constructor for the client
	 */
	public Client(Socket socket, PrintWriter output, ClientInventory inventory, JLayeredPane frame) {
		System.out.println("PlayerName: " + ClientAccountWindow.savedUser);
		setBackground(Color.BLACK);
		Images.importImages();
		Audio.importAudio();
		mySocket = socket;
		this.output = output;
		currentMessage = " ";
		this.playerName = ClientAccountWindow.savedUser;
		this.inventory = inventory;
		this.frame = frame;
		redPop = 0;
		redPopLimit = 0;
		bluePop = 0;
		bluePopLimit = 0;
		inGame = true;

		leaveGame = false;

		chat = new JTextField();
		chat.setLocation(1, 1);
		chat.setSize(200, 20);
		chat.addKeyListener(new JTextFieldEnter());
		chat.setVisible(true);
		chat.setFocusable(true);
		chat.setDocument(new JTextFieldLimit(MAX_CHARACTERS));
		chat.setForeground(Color.GRAY);
		chat.setText("Press enter to chat");
		chat.setToolTipText("Press 'enter' to chat. Type '/t ' before a message to send it only to your team");

		enter = new JButton("Chat");
		enter.setLocation(201, 1);
		enter.setSize(60, 20);
		enter.setVisible(true);
		enter.addActionListener(this);
		enter.setBackground(new Color(240, 240, 240));

		scoreboard.setVisible(false);
		setFocusTraversalKeysEnabled(false);

		setLayout(null);
		add(chat);
		add(enter);

		distanceConstant = 80.0f / (SCREEN_HEIGHT + SCREEN_WIDTH);

	}

	/**
	 * Call when the server closes (Add more later)
	 */
	private void serverClosed() {
		if (!leaveGame) {
			System.out.println("Server was closed");
			world.clear();
			JOptionPane.showMessageDialog(null, "Server was closed", "Server", JOptionPane.ERROR_MESSAGE);
			inventory.getMenuButton().doClick();
			leaveGame = true;
		}
	}

	/**
	 * Start the client
	 */
	public void initialize() {

		// Set the cursor transparent
		// Toolkit toolkit = Toolkit.getDefaultToolkit();
		// Cursor cursor =
		// toolkit.createCustomCursor(Images.getImage("NOTHING"), new Point(
		// getX(), getY()), "img");
		// setCursor(cursor);

		setDoubleBuffered(true);
		setFocusable(true);
		requestFocusInWindow();
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		// Set up the input
		try {
			input = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
			System.out.println("Skipped: " + input.readLine());
		} catch (IOException e) {
			// System.out.println("Error creating buffered reader");
			e.printStackTrace();
		}

		// Set up the output
		try {
			output = new PrintWriter(mySocket.getOutputStream());
			output.println(playerName);
			output.flush();
		} catch (IOException e) {
			// System.out.println("Error creating print writer");
			e.printStackTrace();
		}

		// Import the map from the server
		importMap();

		// Get the user's player
		try {
			String message = input.readLine();
			String[] tokens = message.split(" ");

			int id = toInt(tokens[0]);
			int x = toInt(tokens[1]);
			int y = toInt(tokens[2]);
			String image = Images.getImageName(Integer.parseInt(tokens[3]));
			int team = Integer.parseInt(tokens[4]);

			HP = ServerPlayer.PLAYER_BASE_HP;
			mana = ServerPlayer.PLAYER_BASE_MANA;

			maxHP = HP;
			maxMana = mana;

			player = new ClientObject(id, x, y, image, team, ServerWorld.PLAYER_TYPE);
		} catch (IOException e) {
			System.out.println("Error getting player from server");
			e.printStackTrace();
		}

		// Start the actual game
		gameThread = new Thread(new RunGame());
		gameThread.start();

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
	public int getMoney() {
		return inventory.getMoney();
	}

	public void decreaseMoney(int amount) {
		inventory.decreaseMoney(amount);
	}

	/**
	 * Print to the server
	 */
	public void printToServer(String message) {
		output.println(message);
		output.flush();
	}

	long start = 0;
	int noOfTicks = 0;
	long startPaint = 0;

	/**
	 * Thread for running the actual game
	 * 
	 * @author William Xu && Alex Raita
	 *
	 */
	class ReadServer implements Runnable {
		@Override
		public void run() {
			while (!leaveGame) {
				if (!lines.isEmpty()) {
					String message = lines.remove(0);

					// Update the FPS counter
					if (FPScounter2 >= (1000.0 / ServerEngine.UPDATE_RATE + 0.5)) {
						FPScounter2 = 0;
						startTime2 = System.currentTimeMillis();
					}

					FPScounter2++;

					if (message != null) {
						addPacketNo();
						String[] tokens = message.split(" ");

						for (int token = 1; token < tokens.length && !leaveGame; token++) {
							if(tokens[token].equals(""))
								continue;
							try {
								switch (tokens[token].charAt(0)) {
								case 'L':
									HP = Integer.parseInt(tokens[++token]);
									break;
								case 'A':
									armour = Double.parseDouble(tokens[++token]);
									break;
								case 'M':
									maxHP = Integer.parseInt(tokens[++token]);
									break;
								case 'Q':
									mana = Integer.parseInt(tokens[++token]);
									break;
								case 'K':
									maxMana = Integer.parseInt(tokens[++token]);
									break;
								case 'B':
									// End the game
									leaveGame = true;
									int team = Integer.parseInt(tokens[++token]);
									String winner = "Red Team";
									String loser = "Blue Team";
									if (team == ServerPlayer.RED_TEAM) {
										winner = "Blue Team";
										loser = "Red Team";
									}

									JOptionPane.showMessageDialog(Client.this, String.format(
											"The %s castle has been destroyed, the winner is the %s!", loser, winner));
									input.close();
									output.close();
									if (inventory.getMenuButton() != null) {
										inventory.getMenuButton().doClick();
									}
									break;
								case 'U':
									// startPaint = System.currentTimeMillis();
									// repaint();
									break;
								case 'H':
									if (world.getHologram() == null) {
										int image = Integer.parseInt(tokens[++token]);
										int y = Integer.parseInt(tokens[++token]);
										if (y == 0) {
											world.newHologram(image, mouseX, mouseY);
										} else {
											world.newHologram(image, mouseX, y);
										}

									} else {
										world.getHologram().setImage(Integer.parseInt(tokens[++token]));
										world.getHologram().setX(mouseX);
										int y = Integer.parseInt(tokens[++token]);
										if (y == 0) {
											world.getHologram().setY(mouseY);
										} else {
											world.getHologram().setY(y);
										}
									}
									break;
								case 'h':
									world.removeHologram();
									break;
								case 'p':
									playerX = Double.parseDouble(tokens[++token]);
									playerY = Double.parseDouble(tokens[++token]);
									setPos((int) playerX, (int) playerY);
									break;
								case 'e':
									try {
										ClientObject object = world.get(toInt(tokens[++token]));
										// System.out.println("CHECK: "+(object
										// == null));
										object.setX(player.getX());
										object.setY(player.getY());
									} catch (NullPointerException e) {
										e.printStackTrace();
									}

									break;
								case '*':
									hSpeed = Double.parseDouble(tokens[++token]);
									vSpeed = Double.parseDouble(tokens[++token]);
									break;
								case 'O':
									int id = toInt(tokens[++token]);
									int x = toInt(tokens[++token]);
									int y = toInt(tokens[++token]);
									if (id == player.getID()) {
										player.setTeam(Integer.parseInt(tokens[token + 3]));
										inAction = tokens[++token].charAt(0) == '1';
										if (inAction) {
											onSurface = true;
											isDropping = false;
										}
									}
									if (tokens[token + 4].equals("{")) {
										try {
											world.setObject(id, x, y,
													Images.getImageName(Integer.parseInt(tokens[++token])),
													Integer.parseInt(tokens[++token]), tokens[++token], tokens[++token],
													Integer.parseInt(tokens[++token]));
										} catch (ArrayIndexOutOfBoundsException e) {
											e.printStackTrace();
											// System.out.printf("%d %d %d %d %d
											// %s %s %d%n", id, x, y, Integer
											// .parseInt(tokens[token-1]),Integer
											// .parseInt(tokens[token]),tokens[token+1],
											// tokens[token+2], Integer
											// .parseInt(tokens[token+3]));
										}
									} else {
										int len = 0;
										try {
											len = Integer.parseInt(tokens[token + 4]);
										} catch (NumberFormatException E) {
											System.out.println("Bug with {");
											token += 4;
											continue;
										}
										String name = "";
										for (int i = 0; i < len; i++) {
											name += tokens[token + 5 + i] + " ";
										}
										if (id == player.getID()) {
											x = player.getX();
											y = player.getY();
										}
										world.setObject(id, x, y,
												Images.getImageName(Integer.parseInt(tokens[++token])),
												Integer.parseInt(tokens[++token]), tokens[++token], name.trim(),
												Integer.parseInt(tokens[token + len + 2]));
										token += len + 2;
									}

									break;
								case 't':
									world.setObject(new ClientText(toInt(tokens[++token]), toInt(tokens[++token]),
											toInt(tokens[++token]), tokens[++token], ServerPlayer.NEUTRAL, world));
									break;
								case 'P':
									long calcPing = System.currentTimeMillis() - ping;
									pingString = "Ping: " + calcPing;
									printToServer("y " + calcPing);
									startTimer = System.currentTimeMillis();
									break;
								case 'R':
									int ID = toInt(tokens[++token]);
									world.remove(ID);
									break;
								case 'I':
									System.out.println("Received an item");
									inventory.addItem(Images.getImageName(Integer.parseInt(tokens[++token])),
											tokens[++token], Integer.parseInt(tokens[++token]),
											Integer.parseInt(tokens[++token]));
									inventory.repaint();
									break;
								case 'D':
									damage = Integer.parseInt(tokens[++token]);
									baseDamage = Integer.parseInt(tokens[++token]);
									break;
								case 'S':
									speed = Integer.parseInt(tokens[++token]);
									break;
								case 'J':
									jump = Integer.parseInt(tokens[++token]);
									break;
								case 'a':

									Audio.playAudio(Integer.parseInt(tokens[++token]),
											(float)(Math.sqrt((toInt(tokens[++token]) - playerX)
													* (toInt(tokens[token]) - playerX)
													+ (toInt(tokens[++token]) - playerY)
													*(toInt(tokens[token]) - playerY))));

									break;
								case 'V':
									switch(tokens[token].charAt(1))
									{
									case 'B':
										if (shop != null) {
											shop.setVisible(false);
											frame.remove(shop);
											frame.invalidate();
											shop = null;
										}
										shop = new ClientShop(Client.this);
										int numItems = Integer.parseInt(tokens[++token]);
										for (int item = 0; item < numItems; item++)
											shop.addItem(Images.getImageName(Integer.parseInt(tokens[++token])),
													tokens[++token], Integer.parseInt(tokens[++token]),
													Integer.parseInt(tokens[++token]));
										frame.add(shop, JLayeredPane.PALETTE_LAYER);
										shop.revalidate();
										frame.setVisible(true);
										break;
									case 'S':
										if (shop != null)
											shop.addItem(Images.getImageName(Integer.parseInt(tokens[++token])),
													tokens[++token], Integer.parseInt(tokens[++token]),
													Integer.parseInt(tokens[++token]));
										break;
									case 'b':
										if (shop != null)
										{
											int row = Integer.parseInt(tokens[++token]);
											int col = Integer.parseInt(tokens[++token]);
											shop.getItems()[row][col].sell();
										}
										break;
									}
									break;
								case 'c':
									if (castleShop != null) {
										castleShop.setVisible(false);
										frame.remove(castleShop);
										frame.invalidate();
										castleShop = null;
									}
									if (player.getTeam() == ServerCreature.RED_TEAM) {
										castleShop = new ClientCastleShop(Client.this, redCastleMoney);
									} else
										castleShop = new ClientCastleShop(Client.this, blueCastleMoney);
									frame.add(castleShop, JLayeredPane.PALETTE_LAYER);
									castleShop.revalidate();
									frame.setVisible(true);
									break;
								case 'C':
									if (shop != null)
										closeShop();
									if (castleShop != null)
										closeCastleShop();
									break;
								case 'T':
									world.setWorldTime(toInt(tokens[++token]));
									break;
								case 'i':
									String type = tokens[++token];
									inventory.removeThis(type);
									break;
								case '^':
									int l = Integer.parseInt(tokens[++token]);
									String n = tokens[++token];
									for (int i = 1; i < l; i++)
										n += " " + tokens[++token];
									scoreboard.addPlayer(n, toInt(tokens[++token]), Integer.parseInt(tokens[++token]),
											Integer.parseInt(tokens[++token]), Integer.parseInt(tokens[++token]),
											toInt(tokens[++token]), Integer.parseInt(tokens[++token]));
									scoreboard.repaint();
									break;
								case '@':
									scoreboard.addKill(toInt(tokens[++token]), Integer.parseInt(tokens[++token]));
									scoreboard.repaint();
									break;
								case '!':
									scoreboard.addDeath(toInt(tokens[++token]), Integer.parseInt(tokens[++token]));
									scoreboard.repaint();
									break;
								case 's':
									scoreboard.update(toInt(tokens[++token]), toInt(tokens[++token]),
											Integer.parseInt(tokens[++token]), Integer.parseInt(tokens[++token]));
									break;
								case 'n':
									scoreboard.removePlayer(toInt(tokens[++token]), Integer.parseInt(tokens[++token]));
									scoreboard.repaint();
									break;
								case 'l':
									char who = tokens[++token].charAt(0);
									int nameLen = Integer.parseInt(tokens[++token]);
									String name = tokens[++token];

									for (int i = 1; i < nameLen; i++) {
										name += " " + tokens[++token];
									}
									int numWords = Integer.parseInt(tokens[++token]);
									String text = "";
									for (int i = 0; i < numWords; i++) {
										text += tokens[++token] + " ";
									}
									if (chatQueue.size() >= MAX_MESSAGES)
										chatQueue.remove(0);
									if (who == 'E')
										chatQueue.add("CH " + name + ": " + text.trim());
									else
										chatQueue.add("CH " + name + "[TEAM]: " + text.substring(2).trim());

									break;
								case 'k':
								case 'f':
									if (chatQueue.size() >= MAX_MESSAGES)
										chatQueue.remove(0);
									String text2 = "";
									int amount = Integer.parseInt(tokens[token + 1]) + 2;
									for (int i = 0; i < amount; i++, token++) {
										text2 += tokens[token] + " ";
									}

									amount = Integer.parseInt(tokens[token]) + 1;
									for (int i = 0; i < amount; i++, token++) {
										text2 += tokens[token] + " ";
									}
									chatQueue.add(text2.trim());
									break;
								case 'd':
									int len = Integer.parseInt(tokens[++token]);
									String name2 = "";

									for (int i = 0; i < len; i++)
										name2 += tokens[++token] + " ";
									chatQueue.add("JO " + name2.trim());
									break;
								case 'o':
									int len2 = Integer.parseInt(tokens[++token]);
									String name3 = "";

									for (int i = 0; i < len2; i++)
										name3 += tokens[++token] + " ";
									chatQueue.add("RO " + name3.trim());
									break;
								case 'X':
									redCastleHP = Integer.parseInt(tokens[++token]);
									redCastleTier = Integer.parseInt(tokens[++token]);
									redCastleMoney = Integer.parseInt(tokens[++token]);
									if (castleShop != null && player.getTeam() == ServerCreature.RED_TEAM)
										castleShop.setMoney(redCastleMoney);
									redCastleMaxHP = Integer.parseInt(tokens[++token]);
									redCastleXP = toInt(tokens[++token]);
									break;
								case 'x':
									blueCastleHP = Integer.parseInt(tokens[++token]);
									blueCastleTier = Integer.parseInt(tokens[++token]);
									blueCastleMoney = Integer.parseInt(tokens[++token]);
									if (castleShop != null && player.getTeam() == ServerCreature.BLUE_TEAM)
										castleShop.setMoney(blueCastleMoney);
									blueCastleMaxHP = Integer.parseInt(tokens[++token]);
									blueCastleXP = toInt(tokens[++token]);
									break;
								case 'b':
									for (int weap = 0; weap < inventory.getEquippedWeapons().length; weap++)
										if (inventory.getEquippedWeapons()[weap] != null
										&& inventory.getEquippedWeapons()[weap].getType()
										.contains(ServerWorld.BUILDING_ITEM_TYPE)) {
											inventory.removeItem(inventory.getEquippedWeapons()[weap], weap);
											break;
										}
									break;
								case 'w':
									redPop = Integer.parseInt(tokens[++token]);
									redPopLimit = Integer.parseInt(tokens[++token]);
									break;
								case 'u':
									bluePop = Integer.parseInt(tokens[++token]);
									bluePopLimit = Integer.parseInt(tokens[++token]);
									break;
								case 'r':
									respawnTime = 10 - Integer.parseInt(tokens[++token]) / 60;
									break;
								case '-':
									JOptionPane.showMessageDialog(null, "Kicked from server (cheating or high ping)", "Sorry", JOptionPane.ERROR_MESSAGE);
									inventory.mainMenu.doClick();
									break;
								}

							} catch (NumberFormatException e) {
								System.out.println(message);
								e.printStackTrace();
								break;
							} catch (IOException e) {
								e.printStackTrace();
								break;
							} catch (Exception e){
								e.printStackTrace();
								System.out.println("Something got fucked in the client input comms");
								break;
							}
						}
					}
				}

				if (System.nanoTime() - startPaint > 14 * 1000000) {
					clientUpdatePlayer(System.nanoTime() - startPaint);
					startPaint = System.nanoTime();
					repaint();
				}

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Move the player on the client side if the server side hasn't yet
	 * responded
	 */
	public void clientUpdatePlayer(long timeForTick) {
		if (startPaint == 0) {
			return;
		}
		double currHSpeed = 0;
		double currVSpeed = 0;
		if (!inAction) {
			double gravity = ServerWorld.GRAVITY * (timeForTick / (ServerEngine.UPDATE_RATE * 1000000.0));

			// Apply gravity first (DEFINITELY BEFORE CHECKING
			// VSPEED)
			if (vSpeed + gravity < ServerWorld.MAX_SPEED) {
				vSpeed += gravity;
			} else {
				vSpeed = ServerWorld.MAX_SPEED;
			}

			currHSpeed = hSpeed * (timeForTick / (ServerEngine.UPDATE_RATE * 1000000.0));
			currVSpeed = vSpeed * (timeForTick / (ServerEngine.UPDATE_RATE * 1000000.0));
		}
		// System.out.println(currHSpeed);

		// System.out.println("vSpeed " + currVSpeed + " timeForTick " +
		// timeForTick + " multiplier " +
		// (timeForTick/(ServerEngine.UPDATE_RATE*1000000.0)));

		// Add the object to all the object tiles that it collides
		// with
		// currently
		int startRow = 0;
		int endRow = 0;
		int startColumn = 0;
		int endColumn = 0;

		double x1 = playerX;
		double x2 = x1 + ServerPlayer.DEFAULT_WIDTH;
		double y1 = playerY;
		double y2 = playerY + ServerPlayer.DEFAULT_HEIGHT;

		// Detect the rows and columns of the tiles that the
		// object collides with in this tick
		if (currVSpeed > 0) {
			startRow = (int) (y1 / ServerWorld.TILE_SIZE - 1);
			endRow = (int) ((y2 + currVSpeed) / ServerWorld.TILE_SIZE + 1);
		} else if (currVSpeed < 0) {
			startRow = (int) ((y1 + currVSpeed) / ServerWorld.TILE_SIZE - 1);
			endRow = (int) (y2 / ServerWorld.TILE_SIZE + 1);
		} else {
			startRow = (int) (y1 / ServerWorld.TILE_SIZE);
			endRow = (int) (y2 / ServerWorld.TILE_SIZE + 1);
		}
		if (currHSpeed > 0) {
			startColumn = (int) (x1 / ServerWorld.TILE_SIZE - 1);
			endColumn = (int) ((x2 + currHSpeed) / ServerWorld.TILE_SIZE + 1);
		} else if (currHSpeed < 0) {
			startColumn = (int) ((x1 + currHSpeed) / ServerWorld.TILE_SIZE - 1);
			endColumn = (int) (x2 / ServerWorld.TILE_SIZE + 1);
		} else {
			startColumn = (int) (x1 / ServerWorld.TILE_SIZE - 1);
			endColumn = (int) (x2 / ServerWorld.TILE_SIZE + 1);
		}
		if (startRow < 0) {
			startRow = 0;
		} else if (endRow > world.getCollisionGrid().length - 1) {
			endRow = world.getCollisionGrid().length - 1;
		}
		if (startColumn < 0) {
			startColumn = 0;
		} else if (endColumn > world.getCollisionGrid()[0].length - 1) {
			endColumn = world.getCollisionGrid()[0].length - 1;
		}

		boolean moveVertical = true;
		boolean moveHorizontal = true;

		// Check for collisions with the tiles determined above
		if (currVSpeed > 0) {
			// The row and column of the tile that was collided
			// with
			int collideRow = 0;

			for (int row = startRow; row <= endRow; row++) {
				for (int column = startColumn; column <= endColumn; column++) {
					// System.out.println(row + " " + column + " " + endRow);
					if (((world.getCollisionGrid()[row][column] == ServerWorld.SOLID_TILE
							|| (world.getCollisionGrid()[row][column] == ServerWorld.PLATFORM_TILE && !isDropping))
							&& column * ServerWorld.TILE_SIZE < x2
							&& column * ServerWorld.TILE_SIZE + ServerWorld.TILE_SIZE > x1)) {
						if (y2 + currVSpeed >= row * ServerWorld.TILE_SIZE && y2 <= row * ServerWorld.TILE_SIZE) {
							moveVertical = false;
							collideRow = row;
							break;
						}
					}
					if (!moveVertical) {
						break;
					}
				}
			}
			if (!moveVertical) {
				// Snap the object to the colliding tile
				playerY = collideRow * ServerWorld.TILE_SIZE - (ServerPlayer.DEFAULT_HEIGHT);
				onSurface = true;
				vSpeed = 0;
				currVSpeed = 0;
			} else {
				onSurface = false;
			}
		} else if (currVSpeed < 0) {
			// The row and column of the tile that was collided
			// with
			int collideRow = 0;

			for (int row = endRow; row >= startRow; row--) {
				for (int column = startColumn; column <= endColumn; column++) {
					if (world.getCollisionGrid()[row][column] == ServerWorld.SOLID_TILE
							&& column * ServerWorld.TILE_SIZE < x2
							&& column * ServerWorld.TILE_SIZE + ServerWorld.TILE_SIZE > x1) {
						if (y1 + currVSpeed <= row * ServerWorld.TILE_SIZE + ServerWorld.TILE_SIZE
								&& y1 >= row * ServerWorld.TILE_SIZE + ServerWorld.TILE_SIZE) {
							moveVertical = false;
							collideRow = row;
							break;
						}
					}
					if (!moveVertical) {
						break;
					}
				}
			}
			if (!moveVertical) {
				// Snap the object to the colliding tile
				playerY = collideRow * ServerWorld.TILE_SIZE + ServerWorld.TILE_SIZE + 1;
				vSpeed = 0;
				currVSpeed = 0;
			}
		}

		if (currHSpeed > 0) {
			// The row and column of the tile that was collided
			// with
			int collideColumn = 0;

			for (int row = startRow; row <= endRow; row++) {
				for (int column = startColumn; column <= endColumn; column++) {
					if (world.getCollisionGrid()[row][column] == ServerWorld.SOLID_TILE
							&& row * ServerWorld.TILE_SIZE < y2
							&& row * ServerWorld.TILE_SIZE + ServerWorld.TILE_SIZE > y1) {
						if (x2 + currHSpeed >= column * ServerWorld.TILE_SIZE && x2 <= column * ServerWorld.TILE_SIZE) {
							moveHorizontal = false;
							collideColumn = column;
							break;
						}
					}
					if (!moveHorizontal) {
						break;
					}
				}
			}
			if (!moveHorizontal) {
				// Snap the object to the colliding tile
				playerX = collideColumn * ServerWorld.TILE_SIZE - (ServerPlayer.DEFAULT_WIDTH);
			}
		} else if (currHSpeed < 0) {
			// The row and column of the tile that was collided
			// with
			int collideColumn = 0;

			for (int row = startRow; row <= endRow; row++) {
				for (int column = endColumn; column >= startColumn; column--) {
					if (world.getCollisionGrid()[row][column] == ServerWorld.SOLID_TILE
							&& row * ServerWorld.TILE_SIZE < y2
							&& row * ServerWorld.TILE_SIZE + ServerWorld.TILE_SIZE > y1) {
						if (x1 + currHSpeed <= column * ServerWorld.TILE_SIZE + ServerWorld.TILE_SIZE
								&& x1 >= column * ServerWorld.TILE_SIZE + ServerWorld.TILE_SIZE) {
							moveHorizontal = false;
							collideColumn = column;
							break;
						}
					}
					if (!moveHorizontal) {
						break;
					}
				}
			}
			if (!moveHorizontal) {
				// Snap the object to the colliding tile
				playerX = collideColumn * ServerWorld.TILE_SIZE + ServerWorld.TILE_SIZE;
			}
		}

		// Move this object based on its vertical speed and
		// horizontal speed
		if (moveHorizontal) {

			// Don't let the player move when trying to swing a
			// sword
			// if (!inAction)
			{
				playerX += currHSpeed;
			}
		}
		if (moveVertical) {
			playerY += currVSpeed;
		}

		setPos((int) playerX, (int) playerY);
		printToServer("p " + playerX + " " + playerY);

		char surface = '0';
		if (onSurface) {
			surface = '1';
		}
		printToServer("& " + hSpeed + " " + vSpeed + " " + surface);
	}

	public void setPos(int x, int y) {
		player.setX(x + ServerPlayer.RELATIVE_X);
		player.setY(y + ServerPlayer.RELATIVE_Y);
	}

	/**
	 * Thread for running the actual game
	 * 
	 * @author William Xu && Alex Raita
	 *
	 */
	class RunGame implements Runnable {
		@Override
		public void run() {
			try {
				startTime = System.currentTimeMillis();

				while (!leaveGame) {
					String message = System.currentTimeMillis() + " " + input.readLine();

					lines.add(message);

					// Update the ping after half a second
					if (startTimer >= 0 && System.currentTimeMillis() - startTimer >= 500) {
						ping = System.currentTimeMillis();
						printToServer("P");
						startTimer = -1;

						// System.out.println("Current in-game fps: " +
						// currentFPS + " /// Current server reading fps: " +
						// currentFPS2 + " /// noOfLines: " + lines.size() +"
						// /// noOfObjects: " + world.getNoOfObjects());
					}

					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (IOException e2) {
				serverClosed();
			}

		}
	}

	/**
	 * Close the shop
	 */
	public void closeShop() {
		shop.setVisible(false);
		frame.remove(shop);
		frame.invalidate();
		shop = null;
	}

	public void closeCastleShop() {
		castleShop.setVisible(false);
		frame.remove(castleShop);
		frame.invalidate();
		castleShop = null;
	}

	/**
	 * Get the shop
	 */
	public ClientShop getShop() {
		return shop;
	}

	/**
	 * Import the map
	 */
	private void importMap() {
		System.out.println("Importing the map from the server...");

		// Get the 2D grid from the server
		String gridSize = null;

		try {
			while (gridSize == null) {
				gridSize = input.readLine();
			}
			System.out.println("gridsize " + gridSize);
			String dimensions[] = gridSize.split(" ");
			int height = Integer.parseInt(dimensions[0]);
			int width = Integer.parseInt(dimensions[1]);
			int tileSize = Integer.parseInt(dimensions[2]);

			char grid[][] = new char[height][width];

			for (int row = 0; row < height; row++) {
				String gridRow = input.readLine();
				for (int column = 0; column < width; column++) {
					grid[row][column] = gridRow.charAt(column);
				}
			}

			world = new ClientWorld(grid, tileSize, this);
		} catch (IOException e) {
			serverClosed();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		System.out.println("Map import has finished");
	}

	public int getWeaponSelected() {
		return weaponSelected;
	}

	public boolean isShopOpen() {
		return shop != null;
	}

	/**
	 * Sets the weapon selected
	 * 
	 * @param weaponSelected
	 */
	public void setWeaponSelected(int weaponSelected) {
		if (this.weaponSelected != 9 && inventory.getEquippedWeapons()[this.weaponSelected] != null)
			inventory.getEquippedWeapons()[this.weaponSelected].setBorder(BorderFactory.createEmptyBorder());

		if (weaponSelected != 9)
			inventory.getEquippedWeapons()[weaponSelected]
					.setBorder(BorderFactory.createLineBorder(new Color(244, 244, 244)));
		output.println("W " + weaponSelected);
		output.flush();
		this.weaponSelected = weaponSelected;
	}

	/**
	 * Draw everything
	 */
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		// Update the map
		if(getWorld() != null)
			getWorld().update(graphics, getPlayer());

		// Draw death message if applicable
		if (getHP() > 0) {
			setJustDied(true);
			deathTime = 1;
			fillAmount = 0;
		} else {
			if (isJustDied()) {
				getInventory().clear();
				setJustDied(false);
			}
			// deathTime++;
			// fillAmount += Math.max(0.5, 1.5 - deathTime / 15.0);

			// Causes lag
			// graphics.setColor(Images.darkReds[(int) Math.min(100,
			// fillAmount)]);
			// graphics.fillRect(0, 0, Client.SCREEN_WIDTH,
			// Client.SCREEN_HEIGHT);

			graphics.setColor(Color.white);
			graphics.setFont(ClientWorld.MESSAGE_FONT);
			graphics.drawString(String.format("YOU ARE DEAD. Wait %d seconds to respawn", respawnTime), ClientFrame.getScaledWidth(600), ClientFrame.getScaledHeight(450));
		}

		graphics.setFont(ClientWorld.NORMAL_FONT);
		if (getWorld() != null && getWorld().getBackgroundChoice() == 1) {
			graphics.setColor(Color.BLUE);
		} else {
			graphics.setColor(Color.WHITE);
		}

		graphics.drawString(getPingString(), Client.SCREEN_WIDTH - 60, 20);
		graphics.drawString("FPS: " + Math.min(60, getCurrentFPS()), Client.SCREEN_WIDTH - 60, 40);

		graphics.drawImage(Images.getImage("InventoryShadow"), Client.SCREEN_WIDTH - ClientFrame.getScaledWidth(100), 0, null);

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

		while (!leaveGame) {
			try {
				int textY = 40;
				for (String str : getChatQueue()) {
					boolean done = false;
					switch (str.substring(0, 2)) {
					case "CH":
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
						done = true;
						break;
					case "JO":
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
						done = true;
						break;
					case "RO":
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
						done = true;
						break;

					}
					if (!done) {
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
						String secondKillWord = "defeated";

						if (str.charAt(0) == 'k')
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

						if (str.charAt(0) == 'k')
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

		// Repaint the inventory
		getInventory().repaint();

		if (!writingMessage) {
			requestFocusInWindow();

		} else {
			chat.requestFocus();
		}

		// Update the FPS counter
		if (FPScounter >= (1000.0 / ServerEngine.UPDATE_RATE + 0.5)) {
			FPScounter = 0;
			currentFPS = Math
					.min((int) ((1000.0 / (System.currentTimeMillis() - startTime) * (1000.0 / ServerEngine.UPDATE_RATE)
							+ 0.5)), 120);
			startTime = System.currentTimeMillis();
		}

		FPScounter++;
		// graphics.drawImage(Images.getImage("Cursor"),mouseX,mouseY,null);

		if ((++noOfTicks) > 60) {
			// System.out.println("Repaints per second: " +
			// (int)(noOfTicks/(1.0*System.currentTimeMillis()-start)*1000.0));
			start = System.currentTimeMillis();
			noOfTicks = 0;
		}
	}

	@Override
	public void keyPressed(KeyEvent key) {

		switch (key.getKeyCode()) {
		case KeyEvent.VK_D:
			if (!currentMessage.equals("R") && !inAction) {
				// R for right
				currentMessage = "R";
				printToServer(currentMessage);
				System.out.println("Go right");
				hSpeed = speed;
			}
			break;
		case KeyEvent.VK_A:
			if (!currentMessage.equals("L") && !inAction) {
				// L for left
				currentMessage = "L";
				printToServer(currentMessage);
				hSpeed = -speed;
			}
			break;
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_W:
			if (!currentMessage.equals("U") && !inAction) {
				// U for up
				currentMessage = "U";
				printToServer(currentMessage);
				if (onSurface) {
					vSpeed = -jump;
					onSurface = false;
				}
			}
			break;
		case KeyEvent.VK_S:

			if (!currentMessage.equals("D") && !inAction) {
				// D for down
				currentMessage = "D";
				printToServer(currentMessage);
				isDropping = true;
			}

			break;
		case KeyEvent.VK_1:
			if (!currentMessage.equals("W0") && inventory.getEquippedWeapons()[0] != null) {
				setWeaponSelected(0);
			}
			break;
		case KeyEvent.VK_2:
			if (!currentMessage.equals("W1") && inventory.getEquippedWeapons()[1] != null) {
				setWeaponSelected(1);
			}
			break;
		case KeyEvent.VK_3:
			if (!currentMessage.equals("W1") && inventory.getEquippedWeapons()[2] != null) {
				setWeaponSelected(2);
			}
			break;
		case KeyEvent.VK_4:
			if (!currentMessage.equals("W1") && inventory.getEquippedWeapons()[3] != null) {
				setWeaponSelected(3);
			}
			break;
		case KeyEvent.VK_E:
			printToServer("E");
			if (shop != null) {
				closeShop();
			}
			if (castleShop != null) {
				closeCastleShop();
			}
			break;
		case KeyEvent.VK_ENTER:
			if (!writingMessage) {
				chat.requestFocus();
				writingMessage = true;
				chat.setText("");
				chat.setForeground(Color.black);
			} else if (writingMessage) {
				requestFocusInWindow();
				writingMessage = false;

			}
			break;
		case KeyEvent.VK_TAB:
			if (!scoreboard.isVisible()) {
				scoreboard.setVisible(true);
				add(scoreboard);
				revalidate();
			}
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent key) {

		switch (key.getKeyCode()) {
		case KeyEvent.VK_TAB:
			scoreboard.setVisible(false);
			remove(scoreboard);
			revalidate();
			break;
		case KeyEvent.VK_D:
			if (!currentMessage.equals("r")) {
				currentMessage = "r";
				if (hSpeed > 0) {
					hSpeed = 0;
				}
			}
			break;
		case KeyEvent.VK_A:
			if (!currentMessage.equals("l")) {
				currentMessage = "l";
				if (hSpeed < 0) {
					hSpeed = 0;
				}
			}
			break;
		case KeyEvent.VK_W:
		case KeyEvent.VK_SPACE:
			currentMessage = "!U";
			break;
		case KeyEvent.VK_S:
			currentMessage = "d";
			isDropping = false;
			break;
		}
		if (!currentMessage.isEmpty()) {
			printToServer(currentMessage);
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		// Make sure the player changes direction
		if (event.getX() > SCREEN_WIDTH / 2 + ServerPlayer.DEFAULT_HEIGHT / 2) {
			printToServer("Q");
			direction = 'R';
		} else if (event.getX() < SCREEN_WIDTH / 2 + ServerPlayer.DEFAULT_WIDTH / 2) {
			printToServer("q");
			direction = 'L';
		}

		if (event.getButton() == MouseEvent.BUTTON1 && currentMessage.charAt(0) != 'A') {
			// A for action
			currentMessage = "A " + event.getX() + " " + event.getY() + " t";
			printToServer(currentMessage);
			inAction = true;

			// System.out.println("Pressed");
		} else if (event.getButton() == MouseEvent.BUTTON3 && currentMessage.charAt(0) != 'a') {
			// A for action
			currentMessage = "a " + event.getX() + " " + event.getY();

			inAction = true;
			printToServer(currentMessage);
		}

		if (!writingMessage) {
			requestFocusInWindow();
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1 && !currentMessage.equals("!A")) {
			currentMessage = "!A";

			printToServer(currentMessage);
		} else if (event.getButton() == MouseEvent.BUTTON3 && !currentMessage.equals("c")) {
			currentMessage = "c";

			printToServer(currentMessage);
		}
	}

	@Override
	public void keyTyped(KeyEvent key) {

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mouseDragged(MouseEvent event) {
		mouseX = event.getX();
		mouseY = event.getY();
		printToServer("A " + mouseX + " " + mouseY + " f");
		// Make the player face the direction of the mouse
		if (event.getX() > SCREEN_WIDTH / 2 + ServerPlayer.DEFAULT_WIDTH / 2 && direction != 'R') {
			printToServer("Q");
			direction = 'R';
		} else if (event.getX() < SCREEN_WIDTH / 2 + ServerPlayer.DEFAULT_WIDTH / 2 && direction != 'L') {
			printToServer("q");
			direction = 'L';
		}

	}

	@Override
	public void mouseMoved(MouseEvent event) {
		mouseX = event.getX();
		mouseY = event.getY();
		printToServer("A " + mouseX + " " + mouseY + " f");
		// Make the player face the direction of the mouse
		if (event.getX() > SCREEN_WIDTH / 2 + ServerPlayer.DEFAULT_WIDTH / 2 && direction != 'R') {
			printToServer("Q");
			direction = 'R';
		} else if (event.getX() < SCREEN_WIDTH / 2 + ServerPlayer.DEFAULT_WIDTH / 2 && direction != 'L') {
			printToServer("q");
			direction = 'L';
		}
	}

	public int getCurrentFPS() {
		return currentFPS;
	}

	public void setCurrentFPS(int currentFPS) {
		this.currentFPS = currentFPS;
	}

	public int getHP() {
		return HP;
	}

	public void setHP(int hP) {
		HP = hP;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public int getMaxMana() {
		return maxMana;
	}

	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}

	public int getSpeed() {
		return speed;
	}

	public int getJump() {
		return jump;
	}

	public BufferedReader getInput() {
		return input;
	}

	public PrintWriter getOutput() {
		return output;
	}

	public int getDamage() {
		return damage;
	}

	public int getBaseDamage() {
		return baseDamage;
	}

	public double getArmour() {
		return armour;
	}

	public int getRedCastleHP() {
		return redCastleHP;
	}

	public int getBlueCastleHP() {
		return blueCastleHP;
	}

	public int getRedCastleTier() {
		return redCastleTier;
	}

	public void setRedCastleTier(int redCastleTier) {
		this.redCastleTier = redCastleTier;
	}

	public int getRedCastleMoney() {
		return redCastleMoney;
	}

	public void setRedCastleMoney(int redCastleMoney) {
		this.redCastleMoney = redCastleMoney;
	}

	public int getBlueCastleTier() {
		return blueCastleTier;
	}

	public void setBlueCastleTier(int blueCastleTier) {
		this.blueCastleTier = blueCastleTier;
	}

	public int getBlueCastleMoney() {
		return blueCastleMoney;
	}

	public void setBlueCastleMoney(int blueCastleMoney) {
		this.blueCastleMoney = blueCastleMoney;
	}

	public int getRedCastleMaxHP() {
		return redCastleMaxHP;
	}

	public void setRedCastleMaxHP(int redCastleMaxHP) {
		this.redCastleMaxHP = redCastleMaxHP;
	}

	public int getBlueCastleMaxHP() {
		return blueCastleMaxHP;
	}

	public void setBlueCastleMaxHP(int blueCastleMaxHP) {
		this.blueCastleMaxHP = blueCastleMaxHP;
	}

	/**
	 * Class to limit the number of characters in a JTextField
	 */
	public static class JTextFieldLimit extends PlainDocument {
		private int limit;

		public JTextFieldLimit(int limit) {
			super();
			this.limit = limit;
		}

		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			if (str == null)
				return;

			if ((getLength() + str.length()) <= limit) {
				super.insertString(offset, str, attr);
			}
		}
	}

	/**
	 * When sending a message
	 */
	public void actionPerformed(ActionEvent e) {
		// Send the message
		String message = chat.getText();
		if (message.length() > 0) {
			printToServer("C " + message);
		}
		chat.setForeground(Color.GRAY);
		chat.setText("Press Enter to chat");
		writingMessage = false;
		requestFocusInWindow();

	}

	private class JTextFieldEnter implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				enter.doClick();

			}

		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub

		}

	}

	public Socket getMySocket() {
		return mySocket;
	}

	public void setMySocket(Socket mySocket) {
		this.mySocket = mySocket;
	}

	public Thread getGameThread() {
		return gameThread;
	}

	public void setGameThread(Thread gameThread) {
		this.gameThread = gameThread;
	}

	public long getPing() {
		return ping;
	}

	public void setPing(long ping) {
		this.ping = ping;
	}

	public String getPingString() {
		return pingString;
	}

	public void setPingString(String pingString) {
		this.pingString = pingString;
	}

	public String getCurrentMessage() {
		return currentMessage;
	}

	public void setCurrentMessage(String currentMessage) {
		this.currentMessage = currentMessage;
	}

	public ClientObject getPlayer() {
		return player;
	}

	public void setPlayer(ClientObject player) {
		this.player = player;
	}

	public ClientWorld getWorld() {
		return world;
	}

	public void setWorld(ClientWorld world) {
		this.world = world;
	}

	public JTextField getChat() {
		return chat;
	}

	public void setChat(JTextField chat) {
		this.chat = chat;
	}

	public JButton getEnter() {
		return enter;
	}

	public void setEnter(JButton enter) {
		this.enter = enter;
	}

	public ArrayList<String> getChatQueue() {
		return chatQueue;
	}

	public void setChatQueue(ArrayList<String> chatQueue) {
		this.chatQueue = chatQueue;
	}

	public ClientInventory getInventory() {
		return inventory;
	}

	public void setInventory(ClientInventory inventory) {
		this.inventory = inventory;
	}

	public boolean isJustDied() {
		return justDied;
	}

	public void setJustDied(boolean justDied) {
		this.justDied = justDied;
	}

	public char getDirection() {
		return direction;
	}

	public void setDirection(char direction) {
		this.direction = direction;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public int getFPScounter() {
		return FPScounter;
	}

	public void setFPScounter(int fPScounter) {
		FPScounter = fPScounter;
	}

	public JLayeredPane getFrame() {
		return frame;
	}

	public void setFrame(JLayeredPane frame) {
		this.frame = frame;
	}

	public ArrayList<String> getLines() {
		return lines;
	}

	public void setLines(ArrayList<String> lines) {
		this.lines = lines;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public long getStartTimer() {
		return startTimer;
	}

	public void setStartTimer(long startTimer) {
		this.startTimer = startTimer;
	}

	public void setOutput(PrintWriter output) {
		this.output = output;
	}

	public void setInput(BufferedReader input) {
		this.input = input;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setArmour(double armour) {
		this.armour = armour;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public void setBaseDamage(int baseDamage) {
		this.baseDamage = baseDamage;
	}

	public void setRedCastleHP(int redCastleHP) {
		this.redCastleHP = redCastleHP;
	}

	public void setBlueCastleHP(int blueCastleHP) {
		this.blueCastleHP = blueCastleHP;
	}

	public int getRedCastleXP() {
		return redCastleXP;
	}

	public int getBlueCastleXP() {
		return blueCastleXP;
	}

	public void setShop(ClientShop shop) {
		this.shop = shop;
	}

	public int getRedPop() {
		return redPop;
	}

	public void setRedPop(int redPop) {
		this.redPop = redPop;
	}

	public int getRedPopLimit() {
		return redPopLimit;
	}

	public void setRedPopLimit(int redPopLimit) {
		this.redPopLimit = redPopLimit;
	}

	public int getBluePop() {
		return bluePop;
	}

	public void setBluePop(int bluePop) {
		this.bluePop = bluePop;
	}

	public int getBluePopLimit() {
		return bluePopLimit;
	}

	public void setBluePopLimit(int bluePopLimit) {
		this.bluePopLimit = bluePopLimit;
	}

	public int toInt(String base94) {
		int ret = 0;
		int pow = 1;
		for (int i = 0; i < base94.length(); i++) {
			int b = (int) base94.charAt(i);
			if (b > 92)
				b--;
			int num = b - 33;
			ret += num * pow;
			pow *= 94;
		}
		return ret;
	}

	public static synchronized long getPacketNo() {
		return packetNo;
	}

	public static synchronized void addPacketNo() {
		setPacketNo(getPacketNo() + 1);
	}

	public static synchronized void setPacketNo(long no) {
		packetNo = no;
	}

}
