package Client;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Menu.MainMenu.GamePanel;
import Server.Creatures.ServerCreature;
import Client.Client.JTextFieldLimit;

import java.awt.Color;
import java.awt.Graphics;
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
	private boolean isLeader = false;

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
		start.setLocation(400,400);
		start.setSize(200,40);
		start.setVisible(true);
		start.addActionListener(this);

		setLayout(null);
		add(chat);
		add(enter);
		add(start);

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
					
					if (tokens[token].equals("CH"))
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
						repaint();
					}
					else if(tokens[token].equals("JO"))
					{
						int len = Integer.parseInt(tokens[++token]);
						String name = "";

						for(int i = 0; i < len;i++)
							name += tokens[++token]+" ";
						chatQueue.add("JO "+name.trim());
						repaint();
					}
					else if(tokens[token].equals("RO"))
					{
						int len = Integer.parseInt(tokens[++token]);
						String name = "";

						for(int i = 0; i < len;i++)
							name += tokens[++token]+" ";
						chatQueue.add("RO "+name.trim());
						repaint();
					}
					else if(tokens[token].equals("Start"))
					{
						socket.close();
						output.close();
						input.close();
						System.out.println("Disconnceted");
						GamePanel.startGame();
						
						break;
					}
					else if(tokens[token].equals("L"))
					{
						isLeader = true;
					}
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
