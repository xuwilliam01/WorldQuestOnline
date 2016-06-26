package Menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import Client.Client;
import Client.ClientBackground;
import Client.ClientFrame;
import Client.ClientInventory;
import Imports.Images;
import START.StartGame;
import Server.Server;
import Server.ServerFrame;
import Server.ServerGUI;
import WorldCreator.CreatorFrame;
import WorldCreator.CreatorItems;
import WorldCreator.CreatorWorld;

/**
 * The main menu for the game
 * @author Alex Raita & William Xu
 *
 */
public class MainMenu {

	//Default port number
	final private static int DEF_PORT = 9988;
	//All the panels
	private static ClientFrame mainFrame;
	private static MainPanel mainMenu;
	private static CreatorPanel creatorPanel;
	private static GamePanel gamePanel;
	private static InstructionPanel instructionPanel;

	//Cloud variables
	private static ArrayList<ClientBackground> clouds;
	public final static int CLOUD_DISTANCE = Client.SCREEN_WIDTH * 2;
	private static int cloudDirection = 0;

	private static Client client;

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

		clouds = new ArrayList<ClientBackground>();
		for (int no = 0; no < 20; no++)
		{
			double x = Client.SCREEN_WIDTH / 2 + Math.random() * CLOUD_DISTANCE
					- (CLOUD_DISTANCE / 2);
			double y = Math.random() * (Client.SCREEN_HEIGHT)
					- (2 * Client.SCREEN_HEIGHT / 3);

			double hSpeed = 0;

			hSpeed = (Math.random() * 0.9 + 0.1) * cloudDirection;

			int imageNo = no;

			while (imageNo >= 6)
			{
				imageNo -= 6;
			}

			String image = "CLOUD_" + imageNo + ".png";

			clouds.add(new ClientBackground(x, y, hSpeed, 0, image));
		}
	}

	/**
	 * Constructor
	 */
	public MainMenu()
	{
		Images.importImages();
		generateClouds();
		mainFrame = new ClientFrame();
		mainFrame.setLayout(null);
		mainMenu = new MainPanel();
		mainFrame.add(mainMenu);
		mainMenu.revalidate();
		mainFrame.setVisible(true);
		mainMenu.repaint();
	}

	/**
	 * The main JPanel
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class MainPanel extends JPanel implements ActionListener, MouseListener
	{
		int middle = (Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH)/2;
		Image titleImage = Images.getImage("WorldQuestOnline.png");
		Image background = Images.getImage("BACKGROUND.png");
		JButton playGame;
		JButton createServer;
		JButton createMap;
		JButton instructions;

		Image createMapImage = Images.getImage("CreateAMap.png");
		Image createMapOver = Images.getImage("CreateAMapClicked.png");

		Image instructionsImage = Images.getImage("Instructions.png");
		Image instructionsOver = Images.getImage("InstructionsClicked.png");

		Image playGameImage = Images.getImage("FindAGame.png");
		Image playGameOver = Images.getImage("FindAGameClicked.png");

		Image createServerImage = Images.getImage("CreateAServer.png");
		Image createServerOver = Images.getImage("CreateAServerClicked.png");

		private Timer repaintTimer = new Timer(15,this);

		/**
		 * Constructor
		 */
		public MainPanel()
		{
			setDoubleBuffered(true);
			//setBackground(Color.white);

			setFocusable(true);
			setLayout(null);
			setLocation(0,0);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH,Client.SCREEN_HEIGHT);
			repaintTimer.start();



			playGame = new JButton(new ImageIcon(playGameImage));
			playGame.setSize(playGameImage.getWidth(null),playGameImage.getHeight(null));
			playGame.setLocation(middle - playGameImage.getWidth(null)/2,375);
			playGame.setBorder(BorderFactory.createEmptyBorder());
			playGame.setContentAreaFilled(false);
			playGame.setOpaque(false);
			playGame.addActionListener(new GameStart());
			playGame.addMouseListener(this);
			add(playGame);	

			createServer = new JButton(new ImageIcon(createServerImage));
			createServer.setSize(createServerImage.getWidth(null),createServerImage.getHeight(null));
			createServer.setLocation(middle - createServerImage.getWidth(null)/2,525);
			createServer.setBorder(BorderFactory.createEmptyBorder());
			createServer.setContentAreaFilled(false);
			createServer.setOpaque(false);
			createServer.addActionListener(new StartServer());
			createServer.addMouseListener(this);
			add(createServer);	


			createMap = new JButton(new ImageIcon(createMapImage));
			createMap.setSize(createMapImage.getWidth(null),createMapImage.getHeight(null));
			createMap.setLocation(middle - createMapImage.getWidth(null)/2,675);
			createMap.setBorder(BorderFactory.createEmptyBorder());
			createMap.setContentAreaFilled(false);
			createMap.setOpaque(false);
			createMap.addActionListener(new StartCreator());
			createMap.addMouseListener(this);
			add(createMap);	


			instructions = new JButton(new ImageIcon(instructionsImage));
			instructions.setSize(instructionsImage.getWidth(null),instructionsImage.getHeight(null));
			instructions.setLocation(middle - instructionsImage.getWidth(null)/2,825);
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
			graphics.drawImage(background,0, 0, Client.SCREEN_WIDTH+ClientInventory.INVENTORY_WIDTH,Client.SCREEN_HEIGHT,null);

			// Draw and move the clouds
			for (ClientBackground cloud : clouds)
			{
				if (cloud.getX() <= Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH
						&& cloud.getX() + cloud.getWidth() >= 0
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
					cloud.sethSpeed((Math.random() * 0.8 + 0.2) * cloudDirection);

				}
				else if (cloud.getX() > middle + CLOUD_DISTANCE
						/ 2)
				{
					cloud.setX(middle - CLOUD_DISTANCE / 2);
					cloud.setY(Math.random() * (Client.SCREEN_HEIGHT)
							- (2 * Client.SCREEN_HEIGHT / 3));
					cloud.sethSpeed((Math.random() * 0.8 + 0.2) * cloudDirection);
				}
				cloud.setX(cloud.getX() + cloud.gethSpeed());

			}

			//Draw the title image
			graphics.drawImage(titleImage,middle - titleImage.getWidth(null)/2 - 20,75,null);

			graphics.drawString("William Xu and Alex Raita", 20, Client.SCREEN_HEIGHT - 30);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			repaint();

		}

		@Override
		public void mouseClicked(MouseEvent e) {


		}

		@Override
		public void mousePressed(MouseEvent e) {


		}

		@Override
		public void mouseReleased(MouseEvent e) {


		}

		@Override
		/**
		 * When mouse hovers over a button, change the colour
		 */
		public void mouseEntered(MouseEvent e) {
			if(e.getSource()== createMap)
			{
				createMap.setIcon(new ImageIcon(createMapOver));
			}
			else if(e.getSource() == instructions)
			{
				instructions.setIcon(new ImageIcon(instructionsOver));
			}
			else if(e.getSource() == playGame)
			{
				playGame.setIcon(new ImageIcon(playGameOver));
			}
			else if(e.getSource() == createServer)
			{
				createServer.setIcon(new ImageIcon(createServerOver));
			}

		}

		@Override
		public void mouseExited(MouseEvent e) {
			if(e.getSource()== createMap)
			{
				createMap.setIcon(new ImageIcon(createMapImage));
			}
			else if(e.getSource() == instructions)
			{
				instructions.setIcon(new ImageIcon(instructionsImage));
			}
			else if(e.getSource() == playGame)
			{
				playGame.setIcon(new ImageIcon(playGameImage));
			}
			else if(e.getSource() == createServer)
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
			setBackground(Color.red);
			setFocusable(true);
			setLayout(null);
			setLocation(0,0);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH,Client.SCREEN_HEIGHT);


			CreatorWorld world = null;
			try {
				world = new CreatorWorld(fileName);
			} catch (NumberFormatException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

			JButton menu = new JButton("Main Menu");
			menu.addActionListener(new CreatorMenuButton());
			CreatorItems items = new CreatorItems(world,menu);
			world.setLocation(0,0);
			items.setLocation(Client.SCREEN_WIDTH,0);

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
	private static class GamePanel extends JPanel
	{
		ClientInventory inventory;

		/**
		 * Constructor
		 * @param serverIP the server IP
		 * @param port the port number
		 * @param playerName the player's name
		 */
		public GamePanel(String serverIP, int port, String playerName)
		{
			boolean connected = false;
			boolean exit = false;
			Socket mySocket = null;

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
							.showInputDialog("Connection Failed. Please enter a new IP");
					if(serverIP == null)
						exit = true;
					else
						while(true)
						{
							try
							{
								String portNum = JOptionPane
										.showInputDialog("Please enter the port of the server (Default: " + DEF_PORT + ")");
								if(portNum == null)
								{
									exit = true;
								}
								else if(portNum.equals(""))
								{
									port = DEF_PORT;
								}
								else
									port = Integer.parseInt(portNum);
								break;
							}
							catch(NumberFormatException E)
							{
							}
						}
				}
				if(exit)
					break;
			}

			if(exit)
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
			else
			{
				JLayeredPane pane = new JLayeredPane();
				pane.setLocation(0,0);
				pane.setLayout(null);
				pane.setSize(Client.SCREEN_WIDTH, Client.SCREEN_HEIGHT);
				pane.setDoubleBuffered(true);
				mainFrame.add(pane);
				pane.setVisible(true);

				JButton menu = new JButton("Main Menu");
				menu.addActionListener(new GameMenuButton());
				inventory = new ClientInventory(menu);
				client = new Client(mySocket,inventory,pane,playerName);
				inventory.setClient(client);

				client.setLocation(0,0);
				inventory.setLocation(Client.SCREEN_WIDTH,0);

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
			creatorPanel= null;

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
	private static class InstructionPanel extends JPanel implements ActionListener
	{
		int currentPanel = 0;
		JButton next;
		JButton previous;

		Image objective = Images.getImage("Objective.png");
		Image controls = Images.getImage("Controls.png");
		Image stats = Images.getImage("Stats.png");

		/**
		 * Constructor
		 */
		public InstructionPanel()
		{
			setDoubleBuffered(true);
			setBackground(Color.red);
			setFocusable(true);
			setLayout(null);
			setLocation(0,0);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH,Client.SCREEN_HEIGHT);

			Image nextImage = Images.getImage("Next.png");
			next = new JButton(new ImageIcon(nextImage));
			next.setSize(nextImage.getWidth(null),nextImage.getHeight(null));
			next.setLocation(Client.SCREEN_WIDTH+ClientInventory.INVENTORY_WIDTH-350,Client.SCREEN_HEIGHT-200);
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

			if(currentPanel == 0)
				graphics.drawImage(objective, 0, 0, null);
			else if(currentPanel == 1)
				graphics.drawImage(controls, 0, 0, null);
			else if(currentPanel == 2)
				graphics.drawImage(stats, 0, 0, null);

		}

		/**
		 * Increase the currentPanel until it reaches the main menu again
		 */
		public void actionPerformed(ActionEvent e) {
			currentPanel++;

			if(currentPanel == 3)
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
		public void actionPerformed(ActionEvent e) {

			//Get user info. If it invalid, then ask for it again or exit back to the main menu
			String serverIP;
			int port;
			String playerName;

			serverIP = JOptionPane
					.showInputDialog("Server IP (Leave blank for this computer)");
			if(serverIP == null)
				return;
			if (serverIP.equals(""))
			{
				serverIP = "127.0.0.1";
			}

				while(true)
				{
					try
					{
						String portNum = JOptionPane
								.showInputDialog("Please enter the port of the server (Default: " + DEF_PORT + ")");
						if(portNum == null)
							return;
						else if(portNum.equals(""))
							portNum = ""+DEF_PORT;
						port = Integer.parseInt(portNum);

						playerName = JOptionPane
								.showInputDialog("Please enter your name");
						if(playerName == null)
							return;
						else if(playerName.equals(""))
							playerName = "Player";
						break;
					}
					catch(NumberFormatException E)
					{

					}
				
			}

			mainFrame.remove(mainMenu);
			mainFrame.invalidate();
			mainFrame.validate();
			mainMenu = null;

			gamePanel = new GamePanel(serverIP, port,playerName);
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
		public void actionPerformed(ActionEvent e) {
			//Get info for the server and exit if it is invalid
			String fileName;
			while(true)
			{
				fileName = JOptionPane
						.showInputDialog("Map name (Default: WORLD)");
				if(fileName == null)
					return;
				else if(fileName.equals(""))
					fileName = "world.txt";
				else
					fileName+=".txt";

				File f = new File("Resources",fileName);
				if(f.exists() && !f.isDirectory()) { 
					break;
				}
			}

			int portNum;
			while(true)
			{
				String port = JOptionPane
						.showInputDialog("Please enter the port you want to use for the server (Default "+DEF_PORT+")");
				if(port == null)
					return;
				else if(port.equals(""))
				{
					port = ""+DEF_PORT;
				}
				try{
					portNum = Integer.parseInt(port);
					break;
				}
				catch(NumberFormatException E)
				{		
				}
			}


			//Starts the server
			Server server = new Server(fileName,portNum);

			Thread serverThread = new Thread(server);

			serverThread.start();

			//Ask the user if they want to open the a map of the server, but warns them about lag
			int dialogResult = JOptionPane.showConfirmDialog (null, "A Server was opened! Would you like to see a map of the entire world?\nThis will cause server lag when zoomed out. ","Warning",0);
			if(dialogResult == JOptionPane.YES_OPTION){

				ServerFrame myFrame = new ServerFrame();
				ServerGUI gui = new ServerGUI(server.getEngine().getWorld(), server.getEngine());
				gui.setLocation(0,0);
				myFrame.add(gui);
				gui.revalidate();
				server.getEngine().setGui(gui);
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
		public void actionPerformed(ActionEvent e) {

			//Get filename. If it invalid, exit
			String fileName = "";
			while(true)
			{
				fileName = (String)JOptionPane.showInputDialog("File name (new or existing) (default: WORLD)").trim();
				if(fileName != null && !fileName.isEmpty())
				{
					fileName+=".txt";
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
		public void actionPerformed(ActionEvent e) {
			mainFrame.remove(mainMenu);
			mainFrame.invalidate();
			mainFrame.validate();
			mainMenu = null;

			instructionPanel = new InstructionPanel();
			mainFrame.add(instructionPanel);
			mainFrame.setVisible(true);
			instructionPanel.revalidate();
			instructionPanel.repaint();



		}

	}

}