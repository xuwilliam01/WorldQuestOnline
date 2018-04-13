package Client;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import Imports.Images;
import Menu.MainMenu;
import Menu.MainMenu.GamePanel;
import Server.Creatures.ServerCreature;
import Client.Client.JTextFieldLimit;
import ClientUDP.ClientAccountWindow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

@SuppressWarnings("serial")
public class ClientLobby extends JPanel implements ActionListener, KeyListener
{
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;

	private String name = "";

	private JTextField chat;
	private JButton enter;
	private ArrayList<String> chatQueue = new ArrayList<String>();

	private JButton start;
	private JButton switchTeams;
	private JComboBox<String> mapBox = new JComboBox<String>();

	private boolean isLeader = false;
	private int leaderTeam = -1;
	private String leaderName = "";

	private Image lobbyImage;
	private Image background;

	private String[] maps;

	private ArrayList<Pair> redTeam = new ArrayList<Pair>();
	private ArrayList<String> redTeamBots = new ArrayList<String>();
	private ArrayList<Pair> blueTeam = new ArrayList<Pair>();
	private ArrayList<String> blueTeamBots = new ArrayList<String>();

	private GamePanel panel;
	private JButton menu;
	private boolean goToMenu = false;

	private ClientLobby lobby = this;

	/**
	 * The clouds flying in the background
	 */
	private ArrayList<ClientCloud> clouds;

	private int middle;

	private DatagramSocket centralSocket;
	private DatagramPacket receive;
	private DatagramPacket send;

	private byte[] receiveData;
	private byte[] sendData;

	private Timer startTimer = new Timer(1000,new GameStartTimer());
	private int startCounter = 5;

	/**
	 * If the lobby creation get's cancelled during intialization
	 */
	public boolean cancelled;
	
	/**
	 * When you switched teams last
	 */
	private long switchTime = 0;
	
	private ArrayList<String> botNames = new ArrayList<String>();
	
	/**
	 * 
	 * @param socket
	 * @param playerName
	 * @param panel
	 * @param clouds
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public ClientLobby(Socket socket, BufferedReader input, PrintWriter output, GamePanel panel,
			ArrayList<ClientCloud> clouds, JButton menu)
					throws NumberFormatException, IOException
	{
		System.out.println("INITIALIZING LOBBY");
		lobbyImage = Images.getImage("Lobby");
		background = Images.getImage("SKY");
		middle = (Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH) / 2;

		this.menu = menu;

		this.clouds = clouds;

		this.input = input;

		this.output = output;

		printToServer("Lobby");

		String message = input.readLine();
		if (message.equals("Start"))
		{
			socket.close();
			output.close();
			input.close();
			panel.startGame(this);
		}
		else if(message.equals("Full"))
		{
			JOptionPane.showMessageDialog(MainMenu.mainFrame, "This game is full", "Sorry", JOptionPane.PLAIN_MESSAGE);
			cancelled = true;
			return;
		}
		else
		{
			this.socket = socket;
			this.panel = panel;
			name = ClientAccountWindow.savedUser;

			menu.setLocation((int) (ClientFrame.getScaledWidth(337)),
					(int) (ClientFrame.getScaledHeight(320)));
			menu.setSize(ClientFrame.getScaledWidth(270),ClientFrame.getScaledHeight(20));
			menu.setBackground(new Color(240, 240, 240));
			menu.setForeground(Color.black);
			menu.setFocusable(false);
			menu.setVisible(true);

			chat = new JTextField();
			chat.setLocation(1, 0);
			chat.setSize(200, 20);
			chat.addKeyListener(new JTextFieldEnter());
			chat.setVisible(true);
			chat.setFocusable(true);
			chat.setDocument(new JTextFieldLimit(Client.MAX_CHARACTERS));
			chat.setForeground(Color.BLACK);
			chat.setToolTipText("Press 'enter' as a shortcut to chat");

			enter = new JButton("Chat");
			enter.setLocation(200, 0);
			enter.setSize(60, 20);
			enter.setVisible(true);
			enter.addActionListener(this);
			enter.setBackground(new Color(240, 240, 240));
			enter.setForeground(Color.black);

			start = new JButton("Start Game");
			start.setForeground(Color.GRAY);
			start.setBackground(Color.LIGHT_GRAY);
			start.setOpaque(true);
			start.setBorderPainted(false);
			start.setFocusable(false);
			start.setLocation((int) (ClientFrame.getScaledWidth(337)),
					(int) (ClientFrame.getScaledHeight(280)));
			start.setSize(ClientFrame.getScaledWidth(270),ClientFrame.getScaledHeight(20));
			start.setVisible(true);
			start.addActionListener(this);

			switchTeams = new JButton("Switch Teams");
			switchTeams.setLocation((int) (ClientFrame.getScaledWidth(337)),
					(int) (ClientFrame.getScaledHeight(240)));
			switchTeams.setSize(ClientFrame.getScaledWidth(270),ClientFrame.getScaledHeight(20));
			switchTeams.setVisible(true);
			switchTeams.addActionListener(this);
			switchTeams.setForeground(Color.black);
			switchTeams.setBackground(new Color(240, 240, 240));
			switchTeams.setFocusable(false);

			setLayout(null);
			add(chat);
			add(enter);
			add(start);
			add(switchTeams);
			add(menu);

			setDoubleBuffered(true);
			setFocusable(true);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH,
					Client.SCREEN_HEIGHT);
			
			this.setBackground(Color.BLACK);

			// Add maps on server (first thing read)
			String read = input.readLine();
			String[] readMaps = read.split(" ");
			maps = new String[readMaps.length];
			for (int i = 0; i < readMaps.length; i++)
			{
				maps[i] = (Character.toUpperCase(readMaps[i].charAt(0))
						+ readMaps[i].substring(1)).replace('_', ' ');
				System.out.println(maps[i]);
			}

			// Create the map box
			mapBox = new JComboBox<String>(maps);
			mapBox.setSize(230, 25);
			mapBox.setLocation((int) (ClientFrame.getScaledWidth(390)),
					(int) (ClientFrame.getScaledHeight(160)));
			mapBox.addActionListener(this);
			mapBox.setFocusable(true);
			mapBox.setVisible(true);
			mapBox.setEnabled(false);

			add(mapBox);

			printToServer("Na "+name);

			GetRating rating = new GetRating();
			Thread ratingThread = new Thread(rating);
			ratingThread.start();

			ReadServer reader = new ReadServer();
			Thread startReader = new Thread(reader);
			startReader.start();

			Timer repaintTimer = new Timer(15, this);
			repaintTimer.start();

			setFocusable(true);
			requestFocusInWindow();
			addKeyListener(this);
		}
	}
	
	public void addRed(Pair player, boolean rebalance)
	{
		redTeam.add(player);
		if (rebalance)
		{
			rebalanceAIPlayers();
		}
	}
	
	public void addBlue(Pair player, boolean rebalance)
	{
		blueTeam.add(player);
		if (rebalance)
		{
			rebalanceAIPlayers();
		}
	}
	
	public void removeRed(Pair player, boolean rebalance)
	{
		redTeam.remove(player);
		if (rebalance)
		{
			rebalanceAIPlayers();
		}
	}
	
	public void removeBlue(Pair player, boolean rebalance)
	{
		blueTeam.remove(player);
		if (rebalance)
		{
			rebalanceAIPlayers();
		}
	}
	
	public void rebalanceAIPlayers()
	{
		if (!botNames.isEmpty())
		{
			redTeamBots.clear();
			blueTeamBots.clear();
			
			Queue<String> namesQueue = new LinkedList<String>();
			
			for (String name : botNames)
			{
				namesQueue.add(name); // Just use the word BOT for now
			}
			
			if (redTeam.size() < 2 || redTeam.size() < blueTeam.size())
			{
				for (int i = 0; i < Math.max(2 - redTeam.size(), blueTeam.size() - redTeam.size()); i++)
				{
					redTeamBots.add(namesQueue.remove());
				}
			}
			
			if (blueTeam.size() < 2 || blueTeam.size() < redTeam.size())
			{
				for (int i = 0; i < Math.max(2 - blueTeam.size(), redTeam.size() - blueTeam.size()); i++)
				{
					blueTeamBots.add(namesQueue.remove());
				}
			}
		}
	}
	
	/**
	 * 
	 * @author William
	 *
	 */
	private class ReadServer implements Runnable
	{

		@Override
		public void run()
		{
			while (!goToMenu)
			{
				try
				{
					String message = input.readLine();
					String[] tokens = message.split(" ");
					int token = 0;

					// Move a player to the other team
					if (tokens[token].equals("P"))
					{
						boolean isNew = Boolean.parseBoolean(tokens[++token]);
						int team = Integer.parseInt(tokens[++token]);
						String name = "";
						for (int i = 3; i < tokens.length; i++)
							name += tokens[i] + " ";
						name = name.trim();

						synchronized(redTeam)
						{
							synchronized(blueTeam)
							{
								if (team == ServerCreature.RED_TEAM)
								{
									if (!isNew)
									{
										Pair toRemove = null;
										for(Pair p : blueTeam)
										{
											if(p.name.equals(name))
												toRemove = p;
										}					
										removeBlue(toRemove, false);
									}
									addRed(new Pair(name), true);
								}
								else
								{
									if (!isNew)
									{
										Pair toRemove = null;
										for(Pair p : redTeam)
										{
											if(p.name.equals(name))
												toRemove = p;
										}					
										removeRed(toRemove, false);
									}
									addBlue(new Pair(name), true);
								}
							}
						}

						sendData = ("S "+name).getBytes();
						send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ClientAccountWindow.Domain), ClientAccountWindow.PORT);
						centralSocket.send(send);
					}
					else if (tokens[token].equals("CH"))
					{
						if (tokens[token+1].equals("E"))
						{
							token++;
						}
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
						if (chatQueue.size() >= Client.MAX_MESSAGES)
							chatQueue.remove(0);

						chatQueue.add("CH " + name + ": "
								+ text.trim());
					}
					else if (tokens[token].equals("JO"))
					{
						if (chatQueue.size() >= Client.MAX_MESSAGES)
							chatQueue.remove(0);
						
						int len = Integer.parseInt(tokens[++token]);
						String name = "";

						for (int i = 0; i < len; i++)
							name += tokens[++token] + " ";
						chatQueue.add("JO " + name.trim());
					}
					else if (tokens[token].equals("RO"))
					{
						if (chatQueue.size() >= Client.MAX_MESSAGES)
							chatQueue.remove(0);
						
						int len = Integer.parseInt(tokens[++token]);
						String name = "";

						for (int i = 0; i < len; i++)
							name += tokens[++token] + " ";
						name = name.trim();
						chatQueue.add("RO " + name);

						if (name.charAt(0) - '0' == ServerCreature.RED_TEAM)
						{
							Pair toRemove = null;
							for(Pair p : redTeam)
							{
								if(p.name.equals(name.substring(1)))
									toRemove = p;
							}					
							removeRed(toRemove, true);
						}
						else
						{
							Pair toRemove = null;
							for(Pair p : blueTeam)
							{
								if(p.name.equals(name.substring(1)))
									toRemove = p;
							}					
							removeBlue(toRemove, true);
						}
					}
					else if (tokens[token].equals("Start"))
					{
						System.out.println("Started");
						centralSocket.close();
						socket.close();
						output.close();
						input.close();
						mapBox.setEnabled(false);
						switchTeams.setEnabled(false);
						menu.setEnabled(false);
						start.setEnabled(false);
						enter.setEnabled(false);
						repaint();
						startTimer.start();
						break;
					}
					else if (tokens[token].equals("L"))
					{
						isLeader = true;
						start.setForeground(Color.BLACK);
						start.setBackground(new Color(240, 240, 240));
						start.setBorderPainted(true);

						mapBox.setEnabled(true);
						repaint();
					}
					else if (tokens[token].equals("LE"))
					{
						leaderTeam = Integer.parseInt(tokens[++token]);
						String name = "";
						for (int i = 2; i < tokens.length; i++)
							name += tokens[i] + " ";
						name = name.trim();

						leaderName = name;
					}
					else if (tokens[token].equals("M"))
					{
						if (!isLeader)
						{
							String map = "";
							token++;
							for (int no = 0; no < maps.length; no++)
							{
								if (maps[no].equalsIgnoreCase(Character
										.toUpperCase(tokens[token].charAt(0))
										+ tokens[token].substring(1)))
								{
									map = maps[no];
									break;
								}
							}

							mapBox.setSelectedItem(map);
							//System.out.println(map);
						}
					}
					else if (tokens[token].equals("N"))
					{
						int noOfBotNames = Integer.parseInt(tokens[++token]);
						for (int i = 0; i < noOfBotNames; i++)
						{
							String name = tokens[++token].replace('_', ' ');
							botNames.add(name);
						}
					}
					repaint();
				}
				catch (Exception E)
				{
					if(!goToMenu)
					{
						E.printStackTrace();
						System.out.println("Lost connection to server");
						JOptionPane.showMessageDialog(null,
								"Lost connection to the Server", "Uh-oh",
								JOptionPane.ERROR_MESSAGE);
						menu.doClick();					
					}
					break;
				}
			}

		}

	}

	/**
	 * Draw everything
	 */
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		graphics.drawImage(background, 0, 0, Client.SCREEN_WIDTH
				+ ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT, null);

		// Draw and move the clouds
		for (ClientCloud cloud : clouds)
		{
			if (cloud.getX() <= Client.SCREEN_WIDTH
					+ ClientInventory.INVENTORY_WIDTH
					&& cloud.getX() + cloud.getWidth() >= -64
					&& cloud.getY() <= Client.SCREEN_HEIGHT
					&& cloud.getY() + cloud.getHeight() >= 0)
			{
				graphics.drawImage(cloud.getImage(), (int) cloud.getX(),
						(int) cloud.getY(), null);
			}

			if (cloud.getX() < middle - MainMenu.CLOUD_DISTANCE / 2)
			{
				cloud.setX(middle + MainMenu.CLOUD_DISTANCE / 2);
				cloud.setY(Math.random() * (Client.SCREEN_HEIGHT)
						- (2 * Client.SCREEN_HEIGHT / 3));
				cloud.sethSpeed((Math.random() * 0.8 + 0.2)
						* MainMenu.cloudDirection);

			}
			else if (cloud.getX() > middle + MainMenu.CLOUD_DISTANCE
					/ 2)
			{
				cloud.setX(middle - MainMenu.CLOUD_DISTANCE / 2);
				cloud.setY(Math.random() * (Client.SCREEN_HEIGHT)
						- (2 * Client.SCREEN_HEIGHT / 3));
				cloud.sethSpeed((Math.random() * 0.8 + 0.2)
						* MainMenu.cloudDirection);
			}
			cloud.setX(cloud.getX() + cloud.gethSpeed());

		}

		graphics.drawImage(lobbyImage, 0, 0, null);

		// Draw the chat
		graphics.setFont(ClientWorld.NORMAL_FONT);

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
						graphics.setColor(Color.BLACK);
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
					textY += 20;
				}
				break;
			}
			catch (ConcurrentModificationException E)
			{

			}
		}

		graphics.setColor(Color.black);
		// Inform the player on how to quit
		//graphics.drawString("Press 'ESC' to quit", ClientFrame.getScaledWidth(1920)-120,
		//		20);

		// Write the map name in the top right
		graphics.setFont(ClientWorld.BIG_NORMAL_FONT);
		graphics.setColor(Color.GRAY);
		graphics.drawString("Map:", (int) (ClientFrame.getScaledWidth(330)),
				(int) (ClientFrame.getScaledHeight(180)));

		// Write the players on each team
		graphics.setFont(ClientWorld.TEAM_TITLE_FONT);
		graphics.setColor(Color.BLUE);
		int blueX = (int) (ClientFrame.getScaledWidth(1350));
		int blueY = (int) (ClientFrame.getScaledHeight(120));
		int redX = (int)(ClientFrame.getScaledWidth(820));
		int redY = (int) (ClientFrame.getScaledHeight(120));
		graphics.drawString("Blue Team", blueX, blueY);
		graphics.setColor(Color.RED);
		graphics.drawString("Red Team", redX, redY);
		int redStart = redY + 60;
		int blueStart = blueY + 60;
		graphics.setFont(ClientWorld.PLAYER_NAME_FONT);

		graphics.setColor(Color.RED);
		synchronized(redTeam)
		{
			for (Pair player : redTeam)
			{
				if (leaderTeam == ServerCreature.RED_TEAM
						&& leaderName.equals(player.name))
				{
					graphics.setColor(Color.GREEN);
					graphics.fillOval(redX - 20, redStart - 13, 15, 15);
					graphics.setColor(Color.RED);

					//Write the leader name under the menu button
					graphics.drawString("Lobby Leader: "+leaderName, (int) (ClientFrame.getScaledWidth(337)),
							(int) (ClientFrame.getScaledHeight(385)));
				}

				graphics.drawString(String.format("%s  [%s]", player.name, player.rating), redX + 5,
						redStart);
				redStart += 40;
			}
			
			for (String botName : redTeamBots)
			{
				graphics.drawString(String.format("%s", botName), redX + 5,
						redStart);
				redStart += 40;
			}
		}
		graphics.setColor(Color.BLUE);
		synchronized(blueTeam)
		{
			for (Pair player : blueTeam)
			{
				if (leaderTeam == ServerCreature.BLUE_TEAM
						&& leaderName.equals(player.name))
				{
					graphics.setColor(Color.GREEN);
					graphics.fillOval(blueX - 20, blueStart - 13, 15, 15);
					graphics.setColor(Color.BLUE);

					//Write the leader name under the menu button
					graphics.drawString("Lobby Leader: "+leaderName, (int) (ClientFrame.getScaledWidth(337)),
							(int) (ClientFrame.getScaledHeight(385)));
				}

				graphics.drawString(String.format("%s  [%s]", player.name, player.rating), blueX + 5,
						blueStart);
				blueStart += 40;
			}
			
			for (String botName : blueTeamBots)
			{
				graphics.drawString(String.format("%s", botName), blueX + 5,
						blueStart);
				blueStart += 40;
			}
		}

		graphics.setColor(Color.black);
		if(startTimer.isRunning())
		{
			graphics.drawString("Starting in: "+startCounter, (int) (ClientFrame.getScaledWidth(337)),
					(int) (ClientFrame.getScaledHeight(414)));
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == enter)
		{
			// Send the message
			if(!startTimer.isRunning())
			{
				String message = chat.getText();
				if (message.length() > 0)
				{
					printToServer("C " + message);
				}
				chat.setForeground(Color.GRAY);
				chat.setText("");
			}
			requestFocusInWindow();
		}
		else if (e.getSource() == start && isLeader)
		{
			printToServer("S");
		}
		else if (e.getSource() == switchTeams)
		{
			if(System.currentTimeMillis() - switchTime >= 1000)
			{
				printToServer("X");
				switchTime = System.currentTimeMillis();
			}
		}
		else if (e.getSource() == mapBox)
		{
			printToServer("M " + maps[mapBox.getSelectedIndex()].replace(' ', '_'));
			System.out.println("M " + maps[mapBox.getSelectedIndex()].replace(' ', '_'));
			requestFocusInWindow();
		}
		repaint();

	}

	public void printToServer(String message)
	{
		output.println(message);
		output.flush();
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
			{
				enter.doClick();
			}

		}

		@Override
		public void keyReleased(KeyEvent e)
		{
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			chat.requestFocus();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode()==KeyEvent.VK_ESCAPE)
		{
			System.exit(0);
		}

	}

	public void close()
	{
		try{
			startTimer.stop();
			if(centralSocket != null)
				centralSocket.close();
			goToMenu = true;
			if(socket != null)
			{
				socket.close();
				input.close();
				output.close();
			}
		}
		catch(Exception E)
		{
			E.printStackTrace();
		}
	}

	private class GetRating implements Runnable
	{
		public void run() {
			try {
				centralSocket = new DatagramSocket(MainMenu.LOBBY_UDP_PORT);
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			receiveData = new byte[1024];
			sendData = new byte[1024];

			try
			{
				while(true)
				{
					receiveData = new byte[1024];
					receive = new DatagramPacket(receiveData, receiveData.length);
					centralSocket.receive(receive);

					String input = new String(receive.getData()).trim();
					switch(input.charAt(0))
					{
					case 'S':
						//System.out.println(input);
						String tokens[] = input.split(" ");
						String rating = tokens[1];
						//tokens[2] and tokens[3] are wins and losses. We can ignore those
						String name = tokens[4];
						for(int i = 5; i < tokens.length;i++)
						{
							name += " " + tokens[i];
						}
						synchronized(redTeam)
						{
							for(Pair p : redTeam)
							{
								if(p.name.equals(name))
								{
									p.rating = rating;
									repaint();
									break;
								}
							}
						}
						synchronized(blueTeam)
						{
							for(Pair p : blueTeam)
							{
								if(p.name.equals(name))
								{
									p.rating = rating;
									repaint();
									break;
								}
							}
						}
						break;
					}
				}
			}
			catch(Exception E)
			{
				return;
			}
		}

	}

	private class Pair
	{
		String name;
		String rating = "0";

		public Pair(String name)
		{
			this.name = name;
		}
	}

	private class GameStartTimer implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0) {
			startCounter--;
			if(startCounter == 0)
			{
				startTimer.stop();
				try {
					panel.startGame(lobby);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}
