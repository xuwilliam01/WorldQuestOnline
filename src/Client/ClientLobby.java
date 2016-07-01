package Client;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Imports.Images;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class ClientLobby extends JPanel implements ActionListener,KeyListener{

	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;

	private String name = "";

	private JTextField chat;
	private JButton enter;
	private ArrayList<String> chatQueue = new ArrayList<String>();

	private JButton start;
	private JButton switchTeams;
	
	private boolean isLeader = false;
	private int leaderTeam = -1;
	private String leaderName ="";
	
	private Image background = Images.getImage("BACKGROUND.png");
	private String map ="";

	private ArrayList<String> redTeam = new ArrayList<String>();
	private ArrayList<String> blueTeam = new ArrayList<String>();

	public ClientLobby(Socket socket, String playerName)
	{
		this.socket = socket;
		name = playerName;

		chat = new JTextField();
		chat.setLocation(0, 0);
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

		start = new JButton("Start Game");
		start.setForeground(Color.GRAY);
		start.setBackground(Color.LIGHT_GRAY);
		start.setOpaque(true);
		start.setBorderPainted(false);
		start.setLocation(320,0);
		start.setSize(200,20);
		start.setVisible(true);
		start.addActionListener(this);

		switchTeams = new JButton("Switch Team");
		switchTeams.setLocation(580,0);
		switchTeams.setSize(200,20);
		switchTeams.setVisible(true);
		switchTeams.addActionListener(this);
		
		setLayout(null);
		add(chat);
		add(enter);
		add(start);
		add(switchTeams);

		setDoubleBuffered(true);
		setFocusable(true);
		requestFocusInWindow();
		setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT);
		addKeyListener(this);

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
			printToServer("Na "+name);
		}
		catch (IOException e)
		{
			// System.out.println("Error creating print writer");
			e.printStackTrace();
		} 

		ReadServer reader = new ReadServer();
		Thread startReader = new Thread(reader);
		startReader.start();
	}

	private class ReadServer implements Runnable
	{

		@Override
		public void run() {
			while(true)
			{
				try
				{
					String message = input.readLine();
					String[] tokens = message.split(" ");
					int token = 0;

					//Move a player to the other team
					if(tokens[token].equals("P"))
					{
						boolean isNew = Boolean.parseBoolean(tokens[++token]);
						int team = Integer.parseInt(tokens[++token]);
						String name ="";
						for(int i = 3; i < tokens.length;i++)
							name += tokens[i]+" ";
						name = name.trim();

						if(team == ServerCreature.RED_TEAM)
						{
							if(!isNew)
								blueTeam.remove(name);
							redTeam.add(name);
						}
						else
						{
							if(!isNew)
								redTeam.remove(name);
							blueTeam.add(name);
						}
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
						if (chatQueue.size() >= Client.MAX_MESSAGES)
							chatQueue.remove(0);

						chatQueue.add("CH " + name + ": "
								+ text.trim());						
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
						name = name.trim();
						chatQueue.add("RO "+name);
						
						if(name.charAt(0)-'0' == ServerCreature.RED_TEAM)
						{
							redTeam.remove(name.substring(1));
						}
						else
						{
							blueTeam.remove(name.substring(1));									
						}
					}
					else if(tokens[token].equals("Start"))
					{
						socket.close();
						output.close();
						input.close();
						GamePanel.startGame();

						break;
					}
					else if(tokens[token].equals("L"))
					{
						isLeader = true;
						start.setForeground(Color.BLACK);
						start.setBackground(Color.GRAY);
						start.setBorderPainted(true);
					}
					else if(tokens[token].equals("LE"))
					{
						leaderTeam = Integer.parseInt(tokens[++token]);
						String name ="";
						for(int i = 2; i < tokens.length;i++)
							name+= tokens[i]+" ";
						name = name.trim();
						
						leaderName = name;					
					}
					else if(tokens[token].equals("M"))
					{
						map = tokens[++token].substring(0,tokens[token].length()-4);
					}
					repaint();
				}
				catch(Exception E)
				{
					System.out.println("Lost connection to server");
					E.printStackTrace();
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


		graphics.drawImage(background,0,0,Client.SCREEN_WIDTH+ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT,null);
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
						String coloured = newStr.substring(1, space+1);
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
					else if(str.substring(0,2).equals("JO"))
					{
						if (str.charAt(3) - '0' == ServerCreature.RED_TEAM)
							graphics.setColor(Color.RED);
						else if (str.charAt(3) - '0' == ServerCreature.BLUE_TEAM)
							graphics.setColor(Color.BLUE);
						else
							graphics.setColor(Color.GRAY);
						graphics.drawString(str.substring(4) + " ", 10, textY);
						graphics.setColor(Color.ORANGE);
						graphics.drawString("joined the game", 10+graphics.getFontMetrics().stringWidth(str.substring(4)+" "), textY);
					}
					else if(str.substring(0,2).equals("RO"))
					{
						if (str.charAt(3) - '0' == ServerCreature.RED_TEAM)
							graphics.setColor(Color.RED);
						else if (str.charAt(3) - '0' == ServerCreature.BLUE_TEAM)
							graphics.setColor(Color.BLUE);
						else
							graphics.setColor(Color.GRAY);
						graphics.drawString(str.substring(4) + " ", 10, textY);
						graphics.setColor(Color.ORANGE);
						graphics.drawString("left the game", 10+graphics.getFontMetrics().stringWidth(str.substring(4)+" "), textY);
					}
					textY += 20;
				}
				break;
			}
			catch (ConcurrentModificationException E)
			{

			}
		}

		//Write the map name in the top right
		graphics.setFont(ClientWorld.BIG_NORMAL_FONT);
		int sizeName = graphics.getFontMetrics().stringWidth(map);
		int sizeMap = graphics.getFontMetrics().stringWidth("Map: ");
		graphics.setColor(Color.GRAY);
		graphics.drawString("Map:", Client.SCREEN_WIDTH+ClientInventory.INVENTORY_WIDTH-sizeName-sizeMap-50, 50);
		graphics.setColor(Color.ORANGE);
		graphics.drawString(map, Client.SCREEN_WIDTH+ClientInventory.INVENTORY_WIDTH-sizeName-50, 50);

		//Write the players on each team
		graphics.setFont(ClientWorld.BIG_NORMAL_FONT);
		graphics.setColor(Color.BLUE);
		int blueX = Client.SCREEN_WIDTH - 500;
		int redX = Client.SCREEN_WIDTH - 300;
		graphics.drawString("Blue Team", blueX, 50);
		graphics.setColor(Color.RED);
		graphics.drawString("Red Team", redX, 50);
		int redStart = 80;
		int blueStart = 80;
		graphics.setFont(ClientWorld.NORMAL_FONT);

		graphics.setColor(Color.RED);
		for(String player : redTeam)
		{
			if(leaderTeam == ServerCreature.RED_TEAM && leaderName.equals(player))
			{
				graphics.setColor(Color.GREEN);
				graphics.fillOval(redX-12 , redStart-10, 10, 10);
				graphics.setColor(Color.RED);
			}
				
			graphics.drawString(player, redX + 5,
					redStart);
			redStart += 20;
		}

		graphics.setColor(Color.BLUE);
		for(String player : blueTeam)
		{
			if(leaderTeam == ServerCreature.BLUE_TEAM && leaderName.equals(player))
			{
				graphics.setColor(Color.GREEN);
				graphics.fillOval(blueX-12 , blueStart-10, 10, 10);
				graphics.setColor(Color.BLUE);
			}
			
			graphics.drawString(player, blueX + 5,
					blueStart);
			blueStart += 20;
		}

		if(!chat.hasFocus())
			requestFocusInWindow();

	}


	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == enter)
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
		else if(e.getSource() == start && isLeader)
		{
			printToServer("S");
		}
		else if(e.getSource() == switchTeams)
		{
			printToServer("X");
		}

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
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}


	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			chat.requestFocus();
		}

	}


	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
