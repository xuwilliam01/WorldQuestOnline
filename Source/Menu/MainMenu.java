package Menu;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import Client.Client;
import Client.ClientCloud;
import Client.ClientFrame;
import Client.ClientInventory;
import Client.ClientLobby;
import Client.ClientWorld;
import Imports.Images;
import START.StartGame;
import Server.Server;
import Server.ServerFrame;
import Server.ServerGUI;
import WorldCreator.CreatorItems;
import WorldCreator.CreatorWorld;

/**
 * The main menu for the game
 * @author Alex Raita & William Xu
 *
 */
public class MainMenu
{

	/**
	 * Default port number
	 */
	final private static int DEF_PORT = 9988;

	// All the panels
	public static ClientFrame mainFrame;
	private static MainPanel mainMenu;
	private static CreatorPanel creatorPanel;
	private static GamePanel gamePanel;
	private static InstructionPanel instructionPanel;

	// Cloud variables
	private static ArrayList<ClientCloud> clouds;
	public final static int CLOUD_DISTANCE = Client.SCREEN_WIDTH * 3;
	public static int cloudDirection = 0;

	private static Client client;
	private static ClientLobby lobby;

	private static String playerName;
	
	private boolean imagesLoaded = false;
	
	/**
	 * Whether or not image loading has failed
	 */
	public static boolean imageLoadFailed = false;

	/**
	 * Create the initial clouds for the main menu screen
	 */
	public void generateClouds()
	{
		// Generate clouds
		if ((int) (Math.random() * 2) == 0)
		{
			cloudDirection = 1;
		}
		else
		{
			cloudDirection = -1;
		}

		clouds = new ArrayList<ClientCloud>();
		for (int no = 0; no < ClientWorld.NO_OF_CLOUDS*2; no++)
		{
			double x = Client.SCREEN_WIDTH / 2 + Math.random() * CLOUD_DISTANCE
					- (CLOUD_DISTANCE / 2);
			double y = Math.random() * (Client.SCREEN_HEIGHT * 1.5)
					- (2 * Client.SCREEN_HEIGHT / 3);

			double hSpeed = 0;

			hSpeed = (Math.random() * 0.9 + 0.1) * cloudDirection;

			int imageNo = no;

			while (imageNo >= 6)
			{
				imageNo -= 6;
			}

			String image = "CLOUD_" + imageNo + "";

			clouds.add(new ClientCloud(x, y, hSpeed, 0, image));
		}
	}

	/**
	 * Constructor
	 */
	public MainMenu()
	{
		Thread loadImages = new Thread(new LoadImages());
		loadImages.start();
		
		// Set up the dimensions of the screen
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
	    DisplayMode dm = gs.getDisplayMode();
	    
	    ClientInventory.INVENTORY_WIDTH = (int)(300 * (dm.getWidth()/1920.0));
	    
	    Client.SCREEN_WIDTH = dm.getWidth()-ClientInventory.INVENTORY_WIDTH;
	    if (Client.SCREEN_WIDTH > 1920-ClientInventory.INVENTORY_WIDTH)
	    {
	    	Client.SCREEN_WIDTH = 1920-ClientInventory.INVENTORY_WIDTH;
	    }  
	    Client.SCREEN_HEIGHT = dm.getHeight();
	    if (Client.SCREEN_HEIGHT > 1080)
	    {
	    	Client.SCREEN_HEIGHT=1080;
	    }
	    
	    // Display results
	    System.out.println(dm.getWidth());
	    System.out.println(dm.getHeight());
		
		mainFrame = new ClientFrame();
		
		while (playerName == null)
		{
			try
			{
				playerName = JOptionPane
						.showInputDialog(null,
								"Please enter your name (max 25 characters)","Identification",JOptionPane.QUESTION_MESSAGE);
				if (playerName == null)
				{
					System.exit(0);
					break;
				}
				else if (playerName.equals("") || playerName.length() > 25)
				{
					
					playerName = null;
					continue;
				}
				
				playerName = playerName.trim();

				int enableCloudsAndStars =
						JOptionPane
								.showConfirmDialog(
										null,
										"Would you like to enable extra graphical features? (May reduce performance)",
										"Select Game Quality",
										JOptionPane.YES_NO_OPTION);
				if (enableCloudsAndStars != JOptionPane.YES_OPTION)
				{
					ClientWorld.NO_OF_CLOUDS = 0;
					ClientWorld.MAX_NO_OF_STARS = 0;
				}

				break;
			}
			catch (NumberFormatException E)
			{

			}
		}

		while (!imagesLoaded)
		{
			try
			{
				Thread.sleep(10);
				if (imageLoadFailed )
				{
					JOptionPane.showMessageDialog(null, "Failed to load images. Perhaps you are running the jar directly from Eclipse?", "Error", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
					break;
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		mainFrame.setLayout(null);
		mainMenu = new MainPanel();
		mainFrame.add(mainMenu);
		mainMenu.revalidate();
		mainFrame.setVisible(true);
		
		mainMenu.repaint();
		generateClouds();
	}
	
	private class LoadImages implements Runnable
	{

		@Override
		public void run()
		{
			Images.importImages();
			imagesLoaded = true;
		}
	}

	/**
	 * The main JPanel
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class MainPanel extends JPanel implements ActionListener,
			MouseListener
	{
		int middle = (Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH) / 2;
		Image titleImage = Images.getImage("WorldQuestOnline");
		Image background = Images.getImage("BACKGROUND");
		JButton playGame;
		JButton createServer;
		JButton createMap;
		JButton instructions;

		Image createMapImage = Images.getImage("CreateAMap");
		Image createMapOver = Images.getImage("CreateAMapClicked");

		Image instructionsImage = Images.getImage("Instructions");
		Image instructionsOver = Images.getImage("InstructionsClicked");

		Image playGameImage = Images.getImage("FindAGame");
		Image playGameOver = Images.getImage("FindAGameClicked");

		Image createServerImage = Images.getImage("CreateAServer");
		Image createServerOver = Images.getImage("CreateAServerClicked");

		private Timer repaintTimer = new Timer(15, this);

		/**
		 * Constructor
		 */
		public MainPanel()
		{
			// Set the Icon
			mainFrame.setIconImage(Images.getImage("WorldQuestIcon"));
			
			setDoubleBuffered(true);
			// setBackground(Color.white);

			setBackground(Color.BLACK);
			setFocusable(true);
			setLayout(null);
			setLocation(0, 0);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH,
					Client.SCREEN_HEIGHT);

			
			if (ClientWorld.NO_OF_CLOUDS==0)
			{
				repaintTimer.setDelay(999999999);
			}
			repaintTimer.start();

			playGame = new JButton(new ImageIcon(playGameImage));
			playGame.setSize(playGameImage.getWidth(null),
					playGameImage.getHeight(null));
			playGame.setLocation(middle - playGameImage.getWidth(null) / 2, (int)(375*(Client.SCREEN_HEIGHT/1080.0)));
			playGame.setBorder(BorderFactory.createEmptyBorder());
			playGame.setContentAreaFilled(false);
			playGame.setOpaque(false);
			playGame.addActionListener(new GameStart());
			playGame.addMouseListener(this);
			add(playGame);

			createServer = new JButton(new ImageIcon(createServerImage));
			createServer.setSize(createServerImage.getWidth(null),
					createServerImage.getHeight(null));
			createServer.setLocation(middle - createServerImage.getWidth(null)
					/ 2, (int)(525*(Client.SCREEN_HEIGHT/1080.0)));
			createServer.setBorder(BorderFactory.createEmptyBorder());
			createServer.setContentAreaFilled(false);
			createServer.setOpaque(false);
			createServer.addActionListener(new StartServer());
			createServer.addMouseListener(this);
			add(createServer);

			createMap = new JButton(new ImageIcon(createMapImage));
			createMap.setSize(createMapImage.getWidth(null),
					createMapImage.getHeight(null));
			createMap.setLocation(middle - createMapImage.getWidth(null) / 2,
					(int)(675*(Client.SCREEN_HEIGHT/1080.0)));
			createMap.setBorder(BorderFactory.createEmptyBorder());
			createMap.setContentAreaFilled(false);
			createMap.setOpaque(false);
			createMap.addActionListener(new StartCreator());
			createMap.addMouseListener(this);
			add(createMap);

			instructions = new JButton(new ImageIcon(instructionsImage));
			instructions.setSize(instructionsImage.getWidth(null),
					instructionsImage.getHeight(null));
			instructions.setLocation(middle - instructionsImage.getWidth(null)
					/ 2, (int)(825*(Client.SCREEN_HEIGHT/1080.0)));
			instructions.setBorder(BorderFactory.createEmptyBorder());
			instructions.setContentAreaFilled(false);
			instructions.setOpaque(false);
			instructions.addActionListener(new OpenInstructions());
			instructions.addMouseListener(this);
			add(instructions);

			setVisible(true);
			repaint();
		}

		/**
		 * Draw the clouds
		 */
		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			graphics.drawImage(background, 0, 0, Client.SCREEN_WIDTH
					+ ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT,
					null);

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

				if (cloud.getX() < middle - CLOUD_DISTANCE / 2)
				{
					cloud.setX(middle + CLOUD_DISTANCE / 2);
					cloud.setY(Math.random() * (Client.SCREEN_HEIGHT)
							- (2 * Client.SCREEN_HEIGHT / 3));
					cloud.sethSpeed((Math.random() * 0.8 + 0.2)
							* cloudDirection);

				}
				else if (cloud.getX() > middle + CLOUD_DISTANCE
						/ 2)
				{
					cloud.setX(middle - CLOUD_DISTANCE / 2);
					cloud.setY(Math.random() * (Client.SCREEN_HEIGHT)
							- (2 * Client.SCREEN_HEIGHT / 3));
					cloud.sethSpeed((Math.random() * 0.8 + 0.2)
							* cloudDirection);
				}
				cloud.setX(cloud.getX() + cloud.gethSpeed());

			}

			// Draw the title image
			graphics.drawImage(titleImage, middle - titleImage.getWidth(null)
					/ 2 - 20, (int)(75*(Client.SCREEN_HEIGHT/1080.0)), null);

			graphics.drawString("William Xu and Alex Raita", 15,
					20);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			repaint();

		}

		@Override
		public void mouseClicked(MouseEvent e)
		{

		}

		@Override
		public void mousePressed(MouseEvent e)
		{
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{

		}

		@Override
		/**
		 * When mouse hovers over a button, change the colour
		 */
		public void mouseEntered(MouseEvent e)
		{
			if (e.getSource() == createMap)
			{
				createMap.setIcon(new ImageIcon(createMapOver));
			}
			else if (e.getSource() == instructions)
			{
				instructions.setIcon(new ImageIcon(instructionsOver));
			}
			else if (e.getSource() == playGame)
			{
				playGame.setIcon(new ImageIcon(playGameOver));
			}
			else if (e.getSource() == createServer)
			{
				createServer.setIcon(new ImageIcon(createServerOver));
			}

		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			if (e.getSource() == createMap)
			{
				createMap.setIcon(new ImageIcon(createMapImage));
			}
			else if (e.getSource() == instructions)
			{
				instructions.setIcon(new ImageIcon(instructionsImage));
			}
			else if (e.getSource() == playGame)
			{
				playGame.setIcon(new ImageIcon(playGameImage));
			}
			else if (e.getSource() == createServer)
			{
				createServer.setIcon(new ImageIcon(createServerImage));
			}
		}

	}

	/**
	 * The Panel that displays the map creator
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class CreatorPanel extends JPanel
	{
		/**
		 * Constructor
		 * @param fileName the file to be used
		 */
		public CreatorPanel(String fileName)
		{
			setDoubleBuffered(true);
			setBackground(Color.black);
			setFocusable(true);
			setLayout(null);
			setLocation(0, 0);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH,
					Client.SCREEN_HEIGHT);

			StringBuilder newFileName = new StringBuilder(fileName);
			while (newFileName.indexOf(" ") >= 0)
				newFileName.setCharAt(newFileName.indexOf(" "), '_');

			CreatorWorld world = null;
			try
			{
				world = new CreatorWorld(newFileName.toString());
			}
			catch (NumberFormatException e)
			{

				e.printStackTrace();
			}
			catch (IOException e)
			{

				e.printStackTrace();
			}

			JButton menu = new JButton("Main Menu");
			menu.addActionListener(new CreatorMenuButton());
			CreatorItems items = new CreatorItems(world, menu);
			world.setLocation(0, 0);
			items.setLocation(Client.SCREEN_WIDTH, 0);

			add(world);
			add(items);
			world.revalidate();
			items.invalidate();
			items.revalidate();
			setVisible(true);
			items.repaint();
		}
	}

	/**
	 * The panel to run the actual game
	 * @author Alex Raita & William Xu
	 *
	 */
	public static class GamePanel extends JPanel
	{
		static ClientInventory inventory;
		static Socket mySocket = null;

		String serverIP;
		int port;

		/**
		 * Constructor
		 * @param serverIP the server IP
		 * @param port the port number
		 * @param playerName the player's name
		 * @throws IOException
		 * @throws NumberFormatException
		 */
		public GamePanel(String serverIPIn, int portIn)
				throws NumberFormatException, IOException
		{
			serverIP = serverIPIn;
			this.port = portIn;
			boolean connected = false;
			boolean exit = false;

			while (!connected)
			{
				try
				{
					mySocket = new Socket(serverIP, port);
					connected = true;
				}
				catch (IOException e)
				{
					serverIP = JOptionPane
							.showInputDialog("Connection Failed. Please try again.");
					if (serverIP == null)
						exit = true;
					else
						while (true)
						{
							try
							{
								String portNum = JOptionPane
										.showInputDialog("Please enter the port of the server (Default: "
												+ DEF_PORT + ")");
								if (portNum == null)
								{
									exit = true;
								}
								else if (portNum.equals(""))
								{
									port = DEF_PORT;
								}
								else if (Integer.parseInt(portNum) <= 65535)
									port = Integer.parseInt(portNum);
								else
									throw new NumberFormatException();

								break;
							}
							catch (NumberFormatException E)
							{
							}
						}
				}
				if (exit)
					break;
			}

			if (exit)
			{
				setVisible(false);
				mainFrame.remove(this);
				mainFrame.invalidate();
				mainFrame.validate();

				mainMenu = new MainPanel();
				mainMenu.setVisible(true);
				mainFrame.add(mainMenu);
				mainFrame.setVisible(true);
				mainMenu.revalidate();
			}
			else
			{
				lobby = new ClientLobby(mySocket, playerName, this,clouds);
				lobby.setLocation(0, 0);
				lobby.setLayout(null);
				lobby.setSize(Client.SCREEN_WIDTH
						+ ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT);
				lobby.setDoubleBuffered(true);
				mainFrame.add(lobby);
				lobby.repaint();

			}
		}

		public void startGame(ClientLobby lobby) throws UnknownHostException, IOException
		{
			lobby.setVisible(false);
			mainFrame.remove(lobby);
			mainFrame.invalidate();
			mainFrame.validate();
			lobby = null;

			JLayeredPane pane = new JLayeredPane();
			pane.setLocation(0, 0);
			pane.setLayout(null);
			pane.setSize(Client.SCREEN_WIDTH, Client.SCREEN_HEIGHT);
			pane.setDoubleBuffered(true);
			mainFrame.add(pane);
			pane.setVisible(true);

			JButton menu = new JButton("Main Menu");
			menu.addActionListener(new GameMenuButton());
			inventory = new ClientInventory(menu);
			mySocket.close();
			mySocket = new Socket(serverIP, port);
			client = new Client(mySocket, inventory, pane, playerName);
			inventory.setClient(client);
			inventory.setBackground(Color.BLACK);

			client.setLocation(0, 0);
			inventory.setLocation(Client.SCREEN_WIDTH, 0);

			pane.add(client);
			mainFrame.add(inventory);
			client.initialize();
			client.revalidate();
			inventory.revalidate();
			pane.revalidate();
			pane.setVisible(true);
			mainFrame.setVisible(true);
			inventory.repaint();
		}
	}

	/**
	 * Reacts when the menu button in the creator is pressed
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class CreatorMenuButton implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			creatorPanel.setVisible(false);
			mainFrame.remove(creatorPanel);
			mainFrame.invalidate();
			mainFrame.validate();
			creatorPanel = null;

			mainMenu = new MainPanel();
			mainFrame.add(mainMenu);
			mainFrame.setVisible(true);
			mainMenu.revalidate();
		}
	}

	/**
	 * The instruction menu
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class InstructionPanel extends JPanel implements
			ActionListener
	{
		int currentPanel = 0;
		JButton next;
		JButton previous;

		Image objective = Images.getImage("Objective");
		Image controls = Images.getImage("Controls");
		Image stats = Images.getImage("Stats");

		/**
		 * Constructor
		 */
		public InstructionPanel()
		{
			setDoubleBuffered(true);
			setBackground(Color.red);
			setFocusable(true);
			setLayout(null);
			setLocation(0, 0);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH,
					Client.SCREEN_HEIGHT);

			Image nextImage = Images.getImage("Next");
			next = new JButton(new ImageIcon(nextImage));
			next.setSize(nextImage.getWidth(null), nextImage.getHeight(null));
			next.setLocation(Client.SCREEN_WIDTH
					+ ClientInventory.INVENTORY_WIDTH - 350,
					Client.SCREEN_HEIGHT - 200);
			next.setBorder(BorderFactory.createEmptyBorder());
			next.setContentAreaFilled(false);
			next.setOpaque(false);
			next.addActionListener(this);
			add(next);

			setVisible(true);

			repaint();
		}

		/**
		 * Paints an image depending on the screen the user is on
		 */
		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);

			if (currentPanel == 0)
				graphics.drawImage(objective, 0, 0, null);
			else if (currentPanel == 1)
				graphics.drawImage(controls, 0, 0, null);
			else if (currentPanel == 2)
				graphics.drawImage(stats, 0, 0, null);

		}

		/**
		 * Increase the currentPanel until it reaches the main menu again
		 */
		public void actionPerformed(ActionEvent e)
		{
			currentPanel++;

			if (currentPanel == 3)
			{
				setVisible(false);
				mainFrame.remove(this);
				mainFrame.invalidate();
				mainFrame.validate();

				mainMenu = new MainPanel();
				mainFrame.add(mainMenu);
				mainFrame.setVisible(true);
				mainMenu.revalidate();
			}
			repaint();

		}
	}

	/**
	 * Reacts when the menu button in game is pressed
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class GameMenuButton implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			client.leaveGame=true;
			client.getOutput().close();
			StartGame.restart(mainFrame);
		}
	}

	/**
	 * Starts the game when this button is pressed
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class GameStart implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{

			// Get user info. If it invalid, then ask for it again or exit back
			// to the main menu
			String serverIP;
			int port = DEF_PORT;
			String playerName;

			serverIP = JOptionPane
					.showInputDialog("Server IP (Leave blank for a server on this computer)");
			if (serverIP == null)
				return;
			if (serverIP.equals(""))
			{
				serverIP = "127.0.0.1";
			}

			port = DEF_PORT;
			
//			while (true)
//			{
//				try
//				{
//					String portNum = JOptionPane
//							.showInputDialog(
//									"Please enter the port of the server (Default: "
//											+ DEF_PORT + ")").trim();
//					if (portNum == null)
//						return;
//					else if (portNum.equals(""))
//						portNum = "" + DEF_PORT;
//					else if (Integer.parseInt(portNum) <= 65535)
//						port = Integer.parseInt(portNum);
//					else
//						throw new NumberFormatException();
//
//					break;
//				}
//				catch (NumberFormatException E)
//				{
//
//				}
//
//			}

			mainFrame.remove(mainMenu);
			mainFrame.invalidate();
			mainFrame.validate();
			mainMenu = null;

			try
			{
				gamePanel = new GamePanel(serverIP, port);
			}
			catch (NumberFormatException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			mainFrame.add(gamePanel);
			mainFrame.setVisible(true);
			gamePanel.revalidate();
			
		}

	}

	/**
	 * Starts the server when this button is pressed
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class StartServer implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// while(true)
			// {
			// fileName = JOptionPane
			// .showInputDialog("Map name (Default: WORLD)");
			// if(fileName == null)
			// return;
			// else if(fileName.equals(""))
			// fileName = "World.txt";
			// else
			// fileName+=".txt";
			//
			// File f = new File("Resources",fileName);
			// if(f.exists() && !f.isDirectory()) {
			// break;
			// }
			// }

			int portNum = DEF_PORT;
//			while (true)
//			{
//				String port = JOptionPane
//						.showInputDialog("Please enter the port you want to use for the server (Default "
//								+ DEF_PORT + ")");
//				if (port == null)
//					return;
//				else if (port.equals(""))
//				{
//					port = "" + DEF_PORT;
//				}
//				try
//				{
//					portNum = Integer.parseInt(port);
//					break;
//				}
//				catch (NumberFormatException E)
//				{
//				}
//			}

			// Starts the server
			Server server = new Server(portNum);

			Thread serverThread = new Thread(server);

			serverThread.start();

			while (true)
			{
				try
				{
					ServerGUI gui = new ServerGUI(server);
					ServerFrame myFrame = new ServerFrame();
					gui.setLocation(0, 0);
					myFrame.add(gui);
					gui.revalidate();
					server.setGUI(gui);
					break;
				}
				catch (Exception E)
				{

				}
			}

		}

	}

	/**
	 * Starts the creator when this button is pressed
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class StartCreator implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{

			// Get filename. If it is invalid, exit
			String fileName = "";
			while (true)
			{
				fileName = (String) JOptionPane.showInputDialog(
						"File name (new or existing) (ex: Tunnels)").trim();
				if (fileName != null && !fileName.isEmpty())
				{
					break;
				}
			}

			mainFrame.remove(mainMenu);
			mainFrame.invalidate();
			mainFrame.validate();
			mainMenu = null;

			creatorPanel = new CreatorPanel(fileName);
			mainFrame.add(creatorPanel);
			mainFrame.setVisible(true);
			creatorPanel.revalidate();
		}

	}

	/**
	 * Open the instructions when this button is pressed
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class OpenInstructions implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JOptionPane.showMessageDialog(null, "We are updating the instructions! The controls are shown in the lobby.", "Sorry", JOptionPane.ERROR_MESSAGE);
			
//			mainFrame.remove(mainMenu);
//			mainFrame.invalidate();
//			mainFrame.validate();
//			mainMenu = null;
//
//			instructionPanel = new InstructionPanel();
//			mainFrame.add(instructionPanel);
//			mainFrame.setVisible(true);
//			instructionPanel.revalidate();
//			instructionPanel.repaint();

		}
	}

}