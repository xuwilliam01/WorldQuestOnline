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
import Server.ServerFrame;
import Server.Creatures.ServerCreature;
import Client.Client.JTextFieldLimit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

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

	private ArrayList<String> redTeam = new ArrayList<String>();
	private ArrayList<String> blueTeam = new ArrayList<String>();

	private GamePanel panel;

	private ClientLobby lobby = this;

	/**
	 * The clouds flying in the background
	 */
	private ArrayList<ClientCloud> clouds;

	private int middle;

	/**
	 * 
	 * @param socket
	 * @param playerName
	 * @param panel
	 * @param clouds
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public ClientLobby(Socket socket, String playerName, GamePanel panel,
			ArrayList<ClientCloud> clouds)
			throws NumberFormatException, IOException
	{
		lobbyImage = Images.getImage("Lobby");
		background = Images.getImage("BACKGROUND");
		middle = (Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH) / 2;

		this.clouds = clouds;

		// Set up the input
		try
		{
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		}
		catch (IOException e)
		{
			// System.out.println("Error creating buffered reader");
			e.printStackTrace();
		}

		// Set up the output
		try
		{
			output = new PrintWriter(socket.getOutputStream());
			printToServer("Lobby");

		}
		catch (IOException e)
		{
			// System.out.println("Error creating print writer");
			e.printStackTrace();
		}

		String message = input.readLine();
		if (message.equals("Start"))
		{
			socket.close();
			output.close();
			input.close();
			panel.startGame(this);
		}
		else
		{
			this.socket = socket;
			name = playerName;
			this.panel = panel;

			chat = new JTextField();
			chat.setLocation(1, 0);
			chat.setSize(200, 20);
			chat.addKeyListener(new JTextFieldEnter());
			chat.setVisible(true);
			chat.setFocusable(true);
			chat.setDocument(new JTextFieldLimit(Client.MAX_CHARACTERS));
			chat.setForeground(Color.GRAY);
			chat.setToolTipText("Press 'enter' as a shortcut to chat");

			enter = new JButton("Chat");
			enter.setLocation(200, 0);
			enter.setSize(60, 20);
			enter.setVisible(true);
			enter.addActionListener(this);
			enter.setBackground(new Color(240, 240, 240));

			start = new JButton("Start Game");
			start.setForeground(Color.GRAY);
			start.setBackground(Color.LIGHT_GRAY);
			start.setOpaque(true);
			start.setBorderPainted(false);
			start.setLocation((int) (ClientFrame.getScaledWidth(427)),
					(int) (ClientFrame.getScaledHeight(280)));
			start.setSize(270, 20);
			start.setVisible(true);
			start.addActionListener(this);

			switchTeams = new JButton("Switch Teams");
			switchTeams.setLocation((int) (ClientFrame.getScaledWidth(427)),
					(int) (ClientFrame.getScaledHeight(240)));
			switchTeams.setSize(270, 20);
			switchTeams.setVisible(true);
			switchTeams.addActionListener(this);
			switchTeams.setBackground(new Color(240, 240, 240));

			setLayout(null);
			add(chat);
			add(enter);
			add(start);
			add(switchTeams);

			setDoubleBuffered(true);
			setFocusable(true);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH,
					Client.SCREEN_HEIGHT);
			addKeyListener(this);
			this.setBackground(Color.BLACK);

			// Add maps on server (first thing read)
			String[] readMaps = input.readLine().split(" ");
			maps = new String[readMaps.length];
			for (int i = 0; i < readMaps.length; i++)
			{
				maps[i] = Character.toUpperCase(readMaps[i].charAt(0))
						+ readMaps[i].substring(1);
				System.out.println(Character.toUpperCase(readMaps[i].charAt(0))
						+ readMaps[i].substring(1));
			}

			// Create the map box
			mapBox = new JComboBox<String>(maps);
			mapBox.setSize(230, 25);
			mapBox.setLocation((int) (ClientFrame.getScaledWidth(480)),
					(int) (ClientFrame.getScaledHeight(160)));
			mapBox.addActionListener(this);
			mapBox.setFocusable(true);
			mapBox.setVisible(true);
			mapBox.setEnabled(false);

			add(mapBox);

			ReadServer reader = new ReadServer();
			Thread startReader = new Thread(reader);
			startReader.start();

			printToServer("Na " + name);

			Timer repaintTimer = new Timer(15, this);
			repaintTimer.start();

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
			while (true)
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

						if (team == ServerCreature.RED_TEAM)
						{
							if (!isNew)
								blueTeam.remove(name);
							redTeam.add(name);
						}
						else
						{
							if (!isNew)
								redTeam.remove(name);
							blueTeam.add(name);
						}
					}
					else if (tokens[token].equals("CH"))
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
						if (chatQueue.size() >= Client.MAX_MESSAGES)
							chatQueue.remove(0);

						chatQueue.add("CH " + name + ": "
								+ text.trim());
					}
					else if (tokens[token].equals("JO"))
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
						name = name.trim();
						chatQueue.add("RO " + name);

						if (name.charAt(0) - '0' == ServerCreature.RED_TEAM)
						{
							redTeam.remove(name.substring(1));
						}
						else
						{
							blueTeam.remove(name.substring(1));
						}
					}
					else if (tokens[token].equals("Start"))
					{
						socket.close();
						output.close();
						input.close();
						panel.startGame(lobby);

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
							System.out.println(map);
						}
					}
					repaint();
				}
				catch (Exception E)
				{
					System.out.println("Lost connection to server");
					JOptionPane.showMessageDialog(null,
							"Lost connection to the Server", "Uh-oh",
							JOptionPane.ERROR_MESSAGE);
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
					textY += 20;
				}
				break;
			}
			catch (ConcurrentModificationException E)
			{

			}
		}

		// Write the map name in the top right
		graphics.setFont(ClientWorld.BIG_NORMAL_FONT);
		graphics.setColor(Color.GRAY);
		graphics.drawString("Map:", (int) (ClientFrame.getScaledWidth(420)),
				(int) (ClientFrame.getScaledHeight(180)));

		// Write the players on each team
		graphics.setFont(ClientWorld.TEAM_TITLE_FONT);
		graphics.setColor(Color.BLUE);
		int blueX = (int) (ClientFrame.getScaledWidth(930));
		int blueY = (int) (ClientFrame.getScaledHeight(120));
		int redX = (int) (ClientFrame.getScaledWidth(1480));
		int redY = (int) (ClientFrame.getScaledHeight(120));
		graphics.drawString("Blue Team", blueX, blueY);
		graphics.setColor(Color.RED);
		graphics.drawString("Red Team", redX, redY);
		int redStart = redY + 60;
		int blueStart = blueY + 60;
		graphics.setFont(ClientWorld.PLAYER_NAME_FONT);

		graphics.setColor(Color.RED);
		for (String player : redTeam)
		{
			if (leaderTeam == ServerCreature.RED_TEAM
					&& leaderName.equals(player))
			{
				graphics.setColor(Color.GREEN);
				graphics.fillOval(redX - 30, redStart - 20, 20, 20);
				graphics.setColor(Color.RED);
			}

			graphics.drawString(player, redX + 5,
					redStart);
			redStart += 40;
		}

		graphics.setColor(Color.BLUE);
		for (String player : blueTeam)
		{
			if (leaderTeam == ServerCreature.BLUE_TEAM
					&& leaderName.equals(player))
			{
				graphics.setColor(Color.GREEN);
				graphics.fillOval(blueX - 30, blueStart - 20, 20, 20);
				graphics.setColor(Color.BLUE);
			}

			graphics.drawString(player, blueX + 5,
					blueStart);
			blueStart += 40;
		}

		// if(!chat.hasFocus() && !mapBox.hasFocus())
		// requestFocusInWindow();
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == enter)
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
		else if (e.getSource() == start && isLeader)
		{
			printToServer("S");
		}
		else if (e.getSource() == switchTeams)
		{
			printToServer("X");
		}
		else if (e.getSource() == mapBox)
		{
			printToServer("M " + maps[mapBox.getSelectedIndex()]);
			System.out.println("M " + maps[mapBox.getSelectedIndex()]);
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
				enter.doClick();

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
		// TODO Auto-generated method stub

	}
}
