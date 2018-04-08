package Menu;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import Client.Client;
import Client.ClientCloud;
import Client.ClientFrame;
import Client.ClientInventory;
import Client.ClientLobby;
import ClientUDP.ClientAccountWindow;
import ClientUDP.ClientServerSelection;
import ClientUDP.Leaderboard;
import Imports.Audio;
import Imports.Images;
import Imports.GameMaps;
import START.StartGame;
import Server.ServerManager;
import WorldCreator.CreatorItems;
import WorldCreator.CreatorWorld;

/**
 * The main menu for the game
 * 
 * @author Alex Raita & William Xu
 *
 */
public class MainMenu implements KeyListener {
	static MainMenu main;
	/**
	 * Default port number
	 */
	public final static int DEF_PORT = 9988;
	public final static int DEF_UDP_PORT = 9989;
	public final static int LOBBY_UDP_PORT = 9987;
	public final static int CONFIRM_UDP_PORT = 9990;
	public final static int STATS_UDP_PORT = 9991;

	// All the panels
	public static ClientFrame mainFrame;
	public static MainPanel mainMenu;
	private static CreatorPanel creatorPanel;
	private static GamePanel gamePanel;
	private static InstructionPanel instructionPanel;

	// Cloud variables
	private static ArrayList<ClientCloud> clouds;
	public final static int CLOUD_DISTANCE = Client.SCREEN_WIDTH * 3;
	public static int cloudDirection = 0;

	private static Client client;
	public static ClientLobby lobby;

	private static String playerName;

	private boolean imagesAudioLoaded = false;
	private boolean mapsLoaded = false;

	private static ClientServerSelection serverList = null;
	private static ClientAccountWindow newLogin = null;
	private static Leaderboard leaderboard = null;

	/**
	 * Whether or not image loading has failed
	 */
	public static boolean imageLoadFailed = false;

	private static boolean canConnect = false;

	// private static JButton login;
	private static JButton loginLogout;

	public static boolean tooLarge;
	private static boolean checkedSettingsAlready = false;
	
	public static boolean isMac = false;

	private static Font mainFont = null;

	//Number of times you tried to get stats
	static int numTries = 0;

	/**
	 * Create the initial clouds for the main menu screen
	 */
	public void generateClouds() {
		// Generate clouds
		if ((int) (Math.random() * 2) == 0) {
			cloudDirection = 1;
		} else {
			cloudDirection = -1;
		}

		clouds = new ArrayList<ClientCloud>();
		for (int no = 0; no < 20; no++) {
			double x = Client.SCREEN_WIDTH / 2 + Math.random() * CLOUD_DISTANCE - (CLOUD_DISTANCE / 2);
			double y = Math.random() * (Client.SCREEN_HEIGHT * 1) - (2 * Client.SCREEN_HEIGHT / 3);

			double hSpeed = 0;

			hSpeed = (Math.random() * 0.9 + 0.1) * cloudDirection;

			int imageNo = no;

			while (imageNo >= 6) {
				imageNo -= 6;
			}

			String image = "CLOUD_" + imageNo + "";

			clouds.add(new ClientCloud(x, y, hSpeed, 0, image));
		}
	}

	/**
	 * Constructor
	 */
	public MainMenu(Point pos) {
		Client.inGame = false;
		main = this;
		numTries = 0;

		Thread loadImages = new Thread(new LoadImagesAudio());
		loadImages.start();

		// Set up the dimensions of the screen
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		DisplayMode dm = gs.getDisplayMode();

		ClientInventory.INVENTORY_WIDTH = (int) (300 * (Math.min(dm.getWidth(), 1920) / 1920.0));

		Client.SCREEN_WIDTH = dm.getWidth() - ClientInventory.INVENTORY_WIDTH;

		tooLarge = false;

		if (Client.SCREEN_WIDTH > 1920 - ClientInventory.INVENTORY_WIDTH) {
			Client.SCREEN_WIDTH = 1920 - ClientInventory.INVENTORY_WIDTH;
			tooLarge = true;
		}
		Client.SCREEN_HEIGHT = dm.getHeight();
		
		if (System.getProperty("os.name").toLowerCase().contains("mac"))
		{
			Client.SCREEN_HEIGHT -= 97;
			isMac = true;
		}
		
		if (Client.SCREEN_HEIGHT > 1080) {
			Client.SCREEN_HEIGHT = 1080;
			tooLarge = true;
		}



		if (!checkedSettingsAlready) {
			checkedSettingsAlready = true;

			// Display results
			System.out.println(dm.getWidth());
			System.out.println(dm.getHeight());

			if (tooLarge) {
				JOptionPane.showMessageDialog(null,
						"Please set your monitor to 1920x1080 or smaller for an optimized experience");
			}
		}
		if(tooLarge)
		{
			Client.SCREEN_HEIGHT +=60;
		}
		mainFrame = new ClientFrame(tooLarge, isMac, pos);
		mainFrame.addKeyListener(this);
		mainFrame.requestFocus();

		if(tooLarge)
		{
			Client.SCREEN_HEIGHT -=60;
		}

		while (!(imagesAudioLoaded)) {
			try {
				Thread.sleep(10);
				if (imageLoadFailed) {
					JOptionPane.showMessageDialog(null,
							"Failed to load images. Perhaps you are running the jar directly from Eclipse? Or perhaps you need to extract the zip first",
							"Error", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mainFrame.setLayout(null);
		mainMenu = new MainPanel();
		mainFrame.add(mainMenu);
		mainMenu.revalidate();
		mainFrame.setVisible(true);
		mainFrame.setLocationRelativeTo(null);
		mainMenu.repaint();
		generateClouds();

	}

	private class LoadImagesAudio implements Runnable {

		@Override
		public void run() {
			Images.importImages();
			Audio.importAudio(true);
			imagesAudioLoaded = true;

			// load font
			try {
				mainFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("Catamaran-Light.ttf"));
			} catch (FontFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("font not found");
				e.printStackTrace();
			}
			mainFont = mainFont.deriveFont(20f);

		}
	}

	/**
	 * The main JPanel
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class MainPanel extends JPanel implements ActionListener, MouseListener, Runnable {

		int middle = (Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH) / 2;
		Image titleImage = Images.getImage("WorldQuestOnline");
		Image profileBackgroundImage = Images.getImage("ProfileBackground");
		Image background = Images.getImage("SKY");
		JButton playOnline;
		JButton createServer;
		JButton createMap;
		JButton instructions;
		// JButton loginLogout;

		JButton directConnect;
		JButton leaderb;
		JButton exitButton;

		// Image buttonTrayImage = Images.getImage("ButtonTray");

		Image createMapImage = Images.getImage("CreateAMap");
		Image createMapOver = Images.getImage("CreateAMapClicked");

		Image instructionsImage = Images.getImage("Instructions");
		Image instructionsOver = Images.getImage("InstructionsClicked");

		Image leaderbImage = Images.getImage("Leaderboards");
		Image leaderbOver = Images.getImage("LeaderboardsClicked");

		Image playGameImage = Images.getImage("FindAGame");
		Image playGameOver = Images.getImage("FindAGameClicked");

		Image createServerImage = Images.getImage("CreateAServer");
		Image createServerOver = Images.getImage("CreateAServerClicked");

		Image exitImage = Images.getImage("Exit");
		Image exitOver = Images.getImage("ExitClicked");

		Image loginImage = Images.getImage("Login");
		Image loginOver = Images.getImage("LoginClicked");
		Image logoutImage = Images.getImage("Logout");
		Image logoutOver = Images.getImage("LogoutClicked");

		Image nameGlowImage = Images.getImage("nameGlow");

		private Timer repaintTimer = new Timer(15, this);

		private DatagramSocket socket;
		private DatagramPacket receive;
		private DatagramPacket send;

		private byte[] receiveData;
		private byte[] sendData;

		long statsCounter = 0;
		boolean displayed = true;
		String rating;
		String wins;
		String losses;

		/**
		 * Constructor
		 */
		public MainPanel() {
			// Set the Icon
			mainFrame.setIconImage(Images.getImage("WorldQuestIcon"));
			numTries = 0;
			setDoubleBuffered(true);
			// setBackground(Color.white);

			// setBackground(Color.BLACK);
			setFocusable(true);
			setLayout(null);
			setLocation(0, 0);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT);

			mainFrame.requestFocus();

			repaintTimer.start();

			int currentButtonY = (int) (Client.SCREEN_HEIGHT * (0.35));

			playOnline = new JButton(new ImageIcon(playGameImage));
			playOnline.setSize(playGameImage.getWidth(null), playGameImage.getHeight(null));
			playOnline.setLocation(0, currentButtonY);
			playOnline.setBorder(BorderFactory.createEmptyBorder());
			playOnline.setContentAreaFilled(false);
			playOnline.setOpaque(false);
			playOnline.addActionListener(new OnlineButton());
			playOnline.addMouseListener(this);
			add(playOnline);
			currentButtonY += playGameImage.getHeight(null);

			createMap = new JButton(new ImageIcon(createMapImage));
			createMap.setSize(createMapImage.getWidth(null), createMapImage.getHeight(null));
			createMap.setLocation(0, currentButtonY);
			createMap.setBorder(BorderFactory.createEmptyBorder());
			createMap.setContentAreaFilled(false);
			createMap.setOpaque(false);
			createMap.addActionListener(new StartCreator());
			createMap.addMouseListener(this);
			add(createMap);
			currentButtonY += createMapImage.getHeight(null);

			instructions = new JButton(new ImageIcon(instructionsImage));
			instructions.setSize(instructionsImage.getWidth(null), instructionsImage.getHeight(null));
			instructions.setLocation(0, currentButtonY);
			instructions.setBorder(BorderFactory.createEmptyBorder());
			instructions.setContentAreaFilled(false);
			instructions.setOpaque(false);
			instructions.addActionListener(new OpenInstructions());
			instructions.addMouseListener(this);
			add(instructions);
			currentButtonY += instructionsImage.getHeight(null);

			leaderb = new JButton(new ImageIcon(leaderbImage));
			leaderb.setSize(leaderbImage.getWidth(null), leaderbImage.getHeight(null));
			leaderb.setLocation(0, currentButtonY);
			leaderb.setBorder(BorderFactory.createEmptyBorder());
			leaderb.setContentAreaFilled(false);
			leaderb.setOpaque(false);
			leaderb.addActionListener(new LeaderboardButton());
			leaderb.addMouseListener(this);
			add(leaderb);

			exitButton = new JButton(new ImageIcon(exitImage));
			exitButton.setSize(exitImage.getWidth(null), exitImage.getHeight(null));
			exitButton.setLocation(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH - exitImage.getWidth(null),
					currentButtonY - 75);
			exitButton.setBorder(BorderFactory.createEmptyBorder());
			exitButton.setContentAreaFilled(false);
			exitButton.setOpaque(false);
			exitButton.addActionListener(new ExitGame());
			exitButton.addMouseListener(this);
			add(exitButton);

			// directConnect = new JButton("Direct IP Connect");
			// directConnect.setSize(createServerImage.getWidth(null),
			// createServerImage.getHeight(null));
			// directConnect.setLocation(middle -
			// instructionsImage.getWidth(null)
			// / 2, (int) (990 * (Client.SCREEN_HEIGHT / 1080.0)));
			// directConnect.addActionListener(new GameStart());
			// directConnect.addMouseListener(this);
			// add(directConnect);

			/*
			createServer = new JButton(new ImageIcon(createServerImage));
			createServer.setSize(createServerImage.getWidth(null), createServerImage.getHeight(null));
			createServer.setLocation(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH - exitImage.getWidth(null),
					currentButtonY + 30);
			createServer.setBorder(BorderFactory.createEmptyBorder());
			createServer.setContentAreaFilled(false);
			createServer.setOpaque(false);
			createServer.addActionListener(new StartServer());
			createServer.addMouseListener(this);
			add(createServer);
			*/

			ClientAccountWindow.checkLogin();
			if (ClientAccountWindow.loggedIn) {
				// login = new JButton(String.format("Logout(%s)",
				// ClientAccountWindow.savedUser));
				loginLogout = new JButton(new ImageIcon(logoutImage));
			} else {

				loginLogout = new JButton(new ImageIcon(loginImage));

			}

			loginLogout.setSize(loginImage.getWidth(null), loginImage.getHeight(null));
			loginLogout.setLocation(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH - loginImage.getWidth(null),
					Client.SCREEN_HEIGHT / 2 - profileBackgroundImage.getHeight(null) / 2 - loginImage.getHeight(null)
					- 80);
			loginLogout.setBorder(BorderFactory.createEmptyBorder());
			loginLogout.setContentAreaFilled(false);
			loginLogout.setOpaque(false);
			loginLogout.addActionListener(new LoginButton());
			loginLogout.addMouseListener(this);
			add(loginLogout);

			/*
			 * login.setSize(200, 50); login.setLocation(Client.SCREEN_WIDTH -
			 * 200,50); login.setContentAreaFilled(false);
			 * login.setOpaque(false); login.addActionListener(new
			 * LoginButton()); login.addMouseListener(this); add(login);
			 */

			try {
				socket = new DatagramSocket(STATS_UDP_PORT);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			receiveData = new byte[1024];
			sendData = new byte[1024];
			resetStats();

			Thread thisThread = new Thread(this);
			thisThread.start();

			setVisible(true);
			repaint();
		}

		public void close() {
			socket.close();
			repaintTimer.stop();
			displayed = false;
		}

		public void resetStats() {
			rating = "-";
			wins = "-";
			losses = "-";
		}

		/**
		 * Draw the clouds
		 */
		public void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);

			Graphics2D g2d = (Graphics2D) graphics;
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

			graphics.drawImage(background, 0, 0, Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH,
					Client.SCREEN_HEIGHT, null);

			// Draw and move the clouds
			for (ClientCloud cloud : clouds) {
				if (cloud.getX() <= Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH
						&& cloud.getX() + cloud.getWidth() >= -64 && cloud.getY() <= Client.SCREEN_HEIGHT
						&& cloud.getY() + cloud.getHeight() >= 0) {
					graphics.drawImage(cloud.getImage(), (int) cloud.getX(), (int) cloud.getY(), null);
				}

				if (cloud.getX() < middle - CLOUD_DISTANCE / 2) {
					cloud.setX(middle + CLOUD_DISTANCE / 2);
					cloud.setY(Math.random() * (Client.SCREEN_HEIGHT) - (2 * Client.SCREEN_HEIGHT / 3));
					cloud.sethSpeed((Math.random() * 0.8 + 0.2) * cloudDirection);

				} else if (cloud.getX() > middle + CLOUD_DISTANCE / 2) {
					cloud.setX(middle - CLOUD_DISTANCE / 2);
					cloud.setY(Math.random() * (Client.SCREEN_HEIGHT) - (2 * Client.SCREEN_HEIGHT / 3));
					cloud.sethSpeed((Math.random() * 0.8 + 0.2) * cloudDirection);
				}
				cloud.setX(cloud.getX() + cloud.gethSpeed());

			}

			if (Client.SCREEN_HEIGHT==1080)
			{
				graphics.drawImage(Images.getImage("menuBackground"), 0, 0, null);
			}

			// Draw the title image
			graphics.drawImage(titleImage, middle - titleImage.getWidth(null) / 2 - 25,
					(int) (75 * (Client.SCREEN_HEIGHT / 1080.0)), null);

			graphics.drawImage(profileBackgroundImage,
					Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH - profileBackgroundImage.getWidth(null),
					(int) (Client.SCREEN_HEIGHT * 0.4 - profileBackgroundImage.getHeight(null) / 2 + 25), null);

			g2d.setFont(mainFont);

			//g2d.drawString("William Xu, Alex Raita, Tony Wu", 15, 20);

			Font nameFont = mainFont.deriveFont(36.0f);
			g2d.setFont(nameFont);

			String displayName = ClientAccountWindow.savedUser;

			if (ClientAccountWindow.loggedIn) {
				displayName = ClientAccountWindow.savedUser;
			} else {
				displayName = "Guest";
			}

			int textWidth = graphics.getFontMetrics().stringWidth(displayName);
			BufferedImage nameGlowBuffered = toBufferedImage(nameGlowImage);
			Image nameGlowScaled = nameGlowBuffered.getScaledInstance(textWidth + 40, 120,
					Image.SCALE_SMOOTH);
			g2d.drawImage(nameGlowScaled,
					Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH - profileBackgroundImage.getWidth(null) + 70,
					Client.SCREEN_HEIGHT / 2 - profileBackgroundImage.getHeight(null) / 2 - 35 - 20, null);
			g2d.setColor(Color.WHITE);
			g2d.drawString(displayName,
					Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH - profileBackgroundImage.getWidth(null) + 90,
					Client.SCREEN_HEIGHT / 2 - profileBackgroundImage.getHeight(null) / 2 + 35 - 20);
			mainFont = mainFont.deriveFont(22f);
			g2d.setFont(mainFont);
			g2d.drawString("Rating: " + rating,
					Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH - profileBackgroundImage.getWidth(null) + 80,
					Client.SCREEN_HEIGHT / 2 - profileBackgroundImage.getHeight(null) / 2 + 95 - 20);
			g2d.drawString("Wins: " + wins + "  Losses: " + losses,
					Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH - profileBackgroundImage.getWidth(null) + 70,
					Client.SCREEN_HEIGHT / 2 - profileBackgroundImage.getHeight(null) / 2 + 155 - 20);

			// graphics.drawImage(buttonTrayImage,
			// middle - buttonTrayImage.getWidth(null) / 2,
			// (int) (605 * (Client.SCREEN_HEIGHT / 1080.0)), null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			statsCounter++;
			if (statsCounter % 60 == 0 && numTries < 3)
			{
				if (ClientAccountWindow.loggedIn && rating.equals("-")) {
					sendData = ("S " + ClientAccountWindow.savedUser).getBytes();
					try {
						send = new DatagramPacket(sendData, sendData.length,
								InetAddress.getByName(ClientUDP.ClientAccountWindow.Domain), ClientAccountWindow.PORT);
						//System.out.println(InetAddress.getByName(ClientUDP.ClientAccountWindow.Domain));
						socket.send(send);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				sendData = "P".getBytes();
				try {
					send = new DatagramPacket(sendData, sendData.length,
							InetAddress.getByName(ClientUDP.ClientAccountWindow.Domain), ClientAccountWindow.PORT);
					socket.send(send);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				numTries++;
				if(numTries == 3)
				{
					JOptionPane.showMessageDialog(this, "Could not connect to official servers\n(Apologies! You can still host your own or manually connect to a friend's)");
				}
			}
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
			if (e.getSource() == createMap) {
				createMap.setIcon(new ImageIcon(createMapOver));
			} else if (e.getSource() == instructions) {
				instructions.setIcon(new ImageIcon(instructionsOver));
			} else if (e.getSource() == playOnline) {
				playOnline.setIcon(new ImageIcon(playGameOver));
			} else if (e.getSource() == createServer) {
				createServer.setIcon(new ImageIcon(createServerOver));
			} else if (e.getSource() == leaderb) {
				leaderb.setIcon(new ImageIcon(leaderbOver));
			} else if (e.getSource() == exitButton) {
				exitButton.setIcon(new ImageIcon(exitOver));
			} else if (e.getSource() == loginLogout) {
				if (ClientAccountWindow.loggedIn) {
					loginLogout.setIcon(new ImageIcon(logoutOver));
				} else {
					loginLogout.setIcon(new ImageIcon(loginOver));
				}
			}

		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (e.getSource() == createMap) {
				createMap.setIcon(new ImageIcon(createMapImage));
			} else if (e.getSource() == instructions) {
				instructions.setIcon(new ImageIcon(instructionsImage));
			} else if (e.getSource() == playOnline) {
				playOnline.setIcon(new ImageIcon(playGameImage));
			} else if (e.getSource() == createServer) {
				createServer.setIcon(new ImageIcon(createServerImage));
			} else if (e.getSource() == leaderb) {
				leaderb.setIcon(new ImageIcon(leaderbImage));
			} else if (e.getSource() == exitButton) {
				exitButton.setIcon(new ImageIcon(exitImage));
			} else if (e.getSource() == loginLogout) {
				if (ClientAccountWindow.loggedIn) {
					loginLogout.setIcon(new ImageIcon(logoutImage));
				} else {
					loginLogout.setIcon(new ImageIcon(loginImage));
				}
			}

		}

		@Override
		public void run() {
			while (displayed) {
				receiveData = new byte[1024];
				receive = new DatagramPacket(receiveData, receiveData.length);
				try {
					socket.receive(receive);
				} catch (Exception e) {
					return;
				}
				String[] input = (new String(receive.getData()).trim()).split(" ");
				if(input[0].equals("S"))
				{
					rating = input[1];
					wins = input[2];
					losses = input[3];
				}
				else if(input[0].equals("p"))
				{
					numTries = 0;
				}
			}

		}

	}

	/**
	 * The Panel that displays the map creator
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class CreatorPanel extends JPanel {
		/**
		 * Constructor
		 * 
		 * @param fileName
		 *            the file to be used
		 */
		public CreatorPanel(String fileName) {
			setDoubleBuffered(true);
			setBackground(Color.black);
			setFocusable(true);
			setLayout(null);
			setLocation(0, 0);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT);

			StringBuilder newFileName = new StringBuilder(fileName);
			while (newFileName.indexOf(" ") >= 0)
				newFileName.setCharAt(newFileName.indexOf(" "), '_');

			CreatorWorld world = null;
			try {
				world = new CreatorWorld(newFileName.toString());
			} catch (NumberFormatException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

			JButton menu = new JButton("Main Menu");
			menu.addActionListener(new CreatorMenuButton(world));
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

	private static class ConnectionTimer implements Runnable {
		DatagramSocket socket;
		DatagramPacket receive;
		DatagramPacket send;

		byte[] receiveData;
		byte[] sendData;

		private int port;
		private String IP;

		public ConnectionTimer(String IP, int port) {
			this.IP = IP;
			this.port = port;
			canConnect = false;
		}

		public void close() {
			if (socket != null)
				socket.close();
		}

		@Override
		public void run() {
			while (true) {
				try {
					socket = new DatagramSocket(CONFIRM_UDP_PORT);
					receiveData = new byte[1024];
					sendData = "C".getBytes();
					send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(IP), port);
					socket.send(send);

					receiveData = new byte[1024];
					receive = new DatagramPacket(receiveData, receiveData.length);

					socket.receive(receive);

					// Get input
					String input = new String(receive.getData()).trim();
					if (input.length() == 1 && input.charAt(0) == 'C') {
						canConnect = true;
						socket.close();
						return;
					}
				} catch (Exception e) {
					return;
				}
			}

		}

	}

	/**
	 * The panel to run the actual game
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	public static class GamePanel extends JPanel {
		static ClientInventory inventory;
		static Socket mySocket = null;

		String serverIP;
		int port;
		BufferedReader input = null;
		PrintWriter output = null;

		/**
		 * Constructor
		 * 
		 * @param serverIP
		 *            the server IP
		 * @param port
		 *            the port number
		 * @param playerName
		 *            the player's name
		 * @throws IOException
		 * @throws NumberFormatException
		 */
		public GamePanel(String serverIPIn, int portIn) throws NumberFormatException, IOException {
			serverIP = serverIPIn;
			this.port = portIn;
			boolean connected = false;
			boolean exit = false;
			boolean full = false;

			while (!connected) {
				ConnectionTimer con = new ConnectionTimer(serverIP, port);
				Thread conThread = new Thread(con);
				conThread.start();
				try {
					Thread.sleep(300);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (!canConnect) {
					serverIP = JOptionPane.showInputDialog("Connection Failed. Please re-enter the IP.");
					if (serverIP == null)
						exit = true;
					else {
						con.close();
						continue;
					}
				}
				con.close();
				if (exit)
					break;
				try {
					mySocket = new Socket(serverIP, port);

					// Set up the output
					output = new PrintWriter(mySocket.getOutputStream());
					output.println("Na " + ClientAccountWindow.savedKey + " " + ClientAccountWindow.savedUser);
					output.flush();

					input = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));

					String line = input.readLine();

					if (line.equals("FULL")) {
						System.out.println("ROOMS ARE FULL MESSAGE RECEIVED");
						exit = true;
						full = true;
						input.close();
						output.close();
						mySocket.close();
					} else if (line.equals("DOUBLEACC")) {
						JOptionPane.showMessageDialog(this, "You are already playing a game on this account!");
						exit = true;
						input.close();
						output.close();
						mySocket.close();
					/*
					} else if (line.equals("INVALID")) {
						JOptionPane.showMessageDialog(this,
								"Invalid login credentials. Try to logout and login again.");
						exit = true;
						input.close();
						output.close();
						mySocket.close();
						*/
					} else if (line.equals("ERROR")) {
						JOptionPane.showMessageDialog(this, "Error Connecting");
						exit = true;
						input.close();
						output.close();
						mySocket.close();
					}

					connected = true;
				} catch (IOException e) {
					serverIP = JOptionPane.showInputDialog("Connection Failed. Please re-enter the IP.");
					if (serverIP == null)
						exit = true;
				}
				if (exit)
					break;
			}

			if (exit) {
				if (full)
					JOptionPane.showMessageDialog(null, "This game is full");

				setVisible(false);
				mainFrame.remove(this);
				mainFrame.invalidate();
				mainFrame.validate();

				mainMenu = new MainPanel();
				mainMenu.setVisible(true);
				mainFrame.add(mainMenu);

				mainFrame.setVisible(true);
				mainMenu.revalidate();
			} else {
				JButton menu = new JButton("Main Menu");
				menu.addActionListener(new LobbyMenuButton());

				lobby = new ClientLobby(mySocket, input, output, this, clouds, menu);
				if (lobby.cancelled) {
					menu.doClick();
				} else {
					lobby.setLocation(0, 0);
					lobby.setLayout(null);
					lobby.setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT);
					lobby.setDoubleBuffered(true);
					mainFrame.add(lobby);
					lobby.repaint();
					lobby.requestFocusInWindow();
				}

			}
		}

		public void startGame(ClientLobby lobby) throws UnknownHostException, IOException {
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

			JButton menu = new JButton("Quit");
			menu.addActionListener(new GameMenuButton());
			inventory = new ClientInventory(menu);
			mySocket.close();
			mySocket = new Socket(serverIP, port);

			try {
				mySocket.setReceiveBufferSize(1024 * 16);
				mySocket.setSendBufferSize(1024);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Set up the output
			output = new PrintWriter(mySocket.getOutputStream());
			output.println("Na " + ClientAccountWindow.savedKey + " " + ClientAccountWindow.savedUser);
			output.flush();

			client = new Client(mySocket, output, inventory, pane);
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
	 * Opens the leaderboard
	 */
	private static class LeaderboardButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ClientAccountWindow.open) {
				newLogin.setVisible(true);
				newLogin.toFront();
				return;
			}

			if (ClientServerSelection.open) {
				serverList.setVisible(true);
				serverList.toFront();
				return;
			}

			if (Leaderboard.open) {
				leaderboard.setVisible(true);
				leaderboard.toFront();
				return;
			}

			leaderboard = null;
			try {
				leaderboard = new Leaderboard(DEF_UDP_PORT);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread leaderboardThread = new Thread(leaderboard);
			leaderboardThread.start();
		}

	}

	/**
	 * Opens the menu to select a server
	 */
	private static class OnlineButton implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			if (ClientAccountWindow.open) {
				newLogin.setVisible(true);
				newLogin.toFront();
				return;
			}

			if (ClientServerSelection.open) {
				serverList.setVisible(true);
				serverList.toFront();
				return;
			}
			if (Leaderboard.open) {
				leaderboard.setVisible(true);
				leaderboard.toFront();
				return;
			}
			/*
			if (!ClientAccountWindow.loggedIn) {
				JOptionPane.showMessageDialog(MainMenu.mainFrame, "You are not logged in!");
				return;
			}
			*/
			serverList = null;
			try {
				serverList = new ClientServerSelection(DEF_UDP_PORT);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread listThread = new Thread(serverList);
			listThread.start();

		}

	}

	/**
	 * Reacts to login/logout
	 */
	private static class LoginButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ClientServerSelection.open) {
				serverList.setVisible(true);
				serverList.toFront();
				return;
			}
			if (Leaderboard.open) {
				leaderboard.setVisible(true);
				leaderboard.toFront();
				return;
			}
			if (ClientAccountWindow.open) {
				newLogin.setVisible(true);
				newLogin.toFront();
				return;
			}
			if (ClientAccountWindow.loggedIn) {
				ClientAccountWindow.logout();
				if (mainMenu != null)
					mainMenu.resetStats();

				// login.setText("Login");
				Image logoutImage = Images.getImage("Login");
				loginLogout.setIcon(new ImageIcon(logoutImage));
				JOptionPane.showMessageDialog(mainFrame, "Successfully Logged Out!");
				mainFrame.requestFocus();
				return;
			}
			newLogin = null;
			try {
				newLogin = new ClientAccountWindow(DEF_UDP_PORT, loginLogout, Images.getImage("Logout"));
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread loginThread = new Thread(newLogin);
			loginThread.start();

			ClientAccountWindow.checkLogin();

		}

	}

	/**
	 * Reacts when the menu button in the creator is pressed
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class CreatorMenuButton implements ActionListener {
		private CreatorWorld creator;

		public CreatorMenuButton(CreatorWorld creator) {
			this.creator = creator;
		}

		public void actionPerformed(ActionEvent e) {
			int dialogResult = JOptionPane.NO_OPTION;
			if (!creator.justSaved())
				dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to save your map?", "Warning",
						JOptionPane.YES_NO_CANCEL_OPTION);
			if (dialogResult == JOptionPane.YES_OPTION)
				try {
					creator.save();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			if (dialogResult != JOptionPane.CANCEL_OPTION) {
				creatorPanel.setVisible(false);
				mainFrame.remove(creatorPanel);
				mainFrame.invalidate();
				mainFrame.validate();
				creatorPanel = null;

				mainMenu = new MainPanel();
				mainFrame.add(mainMenu);
				mainFrame.setVisible(true);
				mainMenu.revalidate();
				mainFrame.requestFocus();

			}
		}
	}

	/**
	 * Reacts when the menu button in the lobby is pressed
	 * 
	 * @author Alex Raita & William Xu
	 */
	private static class LobbyMenuButton implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.out.println("went to menu from lobby");
			lobby.setVisible(false);
			lobby.close();
			mainFrame.remove(lobby);
			mainFrame.invalidate();
			mainFrame.validate();
			lobby = null;

			mainMenu = new MainPanel();
			mainFrame.add(mainMenu);
			mainFrame.setVisible(true);
			mainFrame.requestFocus();
			mainMenu.revalidate();
		}
	}

	/**
	 * The instruction menu
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class InstructionPanel extends JPanel implements ActionListener {
		int currentPanel = 0;
		JButton next;
		JButton previous;

		Image objective = Images.getImage("Objective");
		Image controls = Images.getImage("Controls");
		Image stats = Images.getImage("Stats");

		/**
		 * Constructor
		 */
		public InstructionPanel() {
			setDoubleBuffered(true);
			setBackground(Color.red);
			setFocusable(true);
			setLayout(null);
			setLocation(0, 0);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT);

			Image nextImage = Images.getImage("Next");
			next = new JButton(new ImageIcon(nextImage));
			next.setSize(nextImage.getWidth(null), nextImage.getHeight(null));
			next.setLocation(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH - 350, Client.SCREEN_HEIGHT - 200);
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
		public void paintComponent(Graphics graphics) {
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
		public void actionPerformed(ActionEvent e) {
			currentPanel++;

			if (currentPanel == 3) {
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

	static boolean addedKeyListener = false;

	/**
	 * Reacts when the menu button in game is pressed
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class GameMenuButton implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			client.leaveGame = true;
			client.getOutput().close();
			StartGame.restart(mainFrame);
			addedKeyListener = false;
		}
	}

	/**
	 * Starts the game when this button is pressed
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class GameStart implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (ClientServerSelection.open) {
				serverList.setVisible(true);
				serverList.toFront();
				return;
			}
			if (ClientAccountWindow.open) {
				newLogin.setVisible(true);
				newLogin.toFront();
				return;
			}
			if (Leaderboard.open) {
				leaderboard.setVisible(true);
				leaderboard.toFront();
				return;
			}
			/*
			if (!ClientAccountWindow.loggedIn) {
				JOptionPane.showMessageDialog(MainMenu.mainFrame, "You are not logged in!");
				return;
			}
			*/
			// Get user info. If it invalid, then ask for it again or exit back
			// to the main menu
			String serverIP;
			int port = DEF_PORT;
			// String playerName;

			serverIP = JOptionPane.showInputDialog("Enter the server IP or leave blank for a server on this machine");
			if (serverIP == null) {
				mainFrame.requestFocus();
				return;
			} else if ((serverIP.trim()).equals("")) {
				serverIP = "127.0.0.1";
			}

			port = DEF_PORT;

			mainFrame.remove(mainMenu);
			mainMenu.close();
			mainFrame.invalidate();
			mainFrame.validate();
			mainMenu = null;

			try {
				gamePanel = new GamePanel(serverIP, port);
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			mainFrame.add(gamePanel);
			mainFrame.setVisible(true);
			gamePanel.revalidate();

		}

	}

	public static void joinLobby(String IP, int port) {
		mainFrame.remove(mainMenu);
		mainMenu.close();
		mainFrame.invalidate();
		mainFrame.validate();
		mainMenu = null;

		try {
			gamePanel = new GamePanel(IP, port);
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mainFrame.add(gamePanel);
		mainFrame.setVisible(true);
		gamePanel.revalidate();
	}

	/**
	 * Starts the server when this button is pressed
	 * Currently inactive, please fix constructor in server manager
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class StartServer implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (ClientServerSelection.open) {
				serverList.setVisible(true);
				serverList.toFront();
				return;
			}
			if (ClientAccountWindow.open) {
				newLogin.setVisible(true);
				newLogin.toFront();
				return;
			}
			if (Leaderboard.open) {
				leaderboard.setVisible(true);
				leaderboard.toFront();
				return;
			}
			int maxRooms;
			String name;

			GameMaps.importMaps();
			while (true) {
				try {
					String maxRoomsStr = JOptionPane
							.showInputDialog("What is the maximum number of gamerooms you would like?");
					if (maxRoomsStr == null) {
						mainFrame.requestFocus();
						return;
					}
					maxRooms = Integer.parseInt(maxRoomsStr);
					if (maxRooms < 1)
						maxRooms = 1;
					break;
				} catch (Exception E) {

				}
			}

			while (true) {
				try {
					name = JOptionPane.showInputDialog("What would you like to name the room? (No spaces)");
					if (name == null) {
						mainFrame.requestFocus();
						return;
					}
					if (name.contains(" ") || name.equals(""))
						throw new Exception();
					break;
				} catch (Exception E) {

				}
			}

			int portNum = DEF_PORT;
			// while (true)
			// {
			// String port = JOptionPane
			// .showInputDialog("Please enter the port you want to use for the
			// server (Default "
			// + DEF_PORT + ")");
			// if (port == null)
			// return;
			// else if (port.equals(""))
			// {
			// port = "" + DEF_PORT;
			// }
			// try
			// {
			// portNum = Integer.parseInt(port);
			// break;
			// }
			// catch (NumberFormatException E)
			// {
			// }
			// }

			// Starts the server
			System.out.println("rooms " + maxRooms);
			ServerManager server = null;
//			try {
//				server = new ServerManager(name, portNum, maxRooms, mainFrame, false);
//			} catch (SocketException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}

			Thread serverThread = new Thread(server);

			serverThread.start();

		}

	}

	/**
	 * Starts the creator when this button is pressed
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class StartCreator implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Get filename. If it is invalid, exit
			if (ClientServerSelection.open) {
				serverList.setVisible(true);
				serverList.toFront();
				return;
			}
			if (ClientAccountWindow.open) {
				newLogin.setVisible(true);
				newLogin.toFront();
				return;
			}
			if (Leaderboard.open) {
				leaderboard.setVisible(true);
				leaderboard.toFront();
				return;
			}
			String fileName = "";
			String[] mapNames = null;
			final String DEFAULT_MAP_NAME = "New Map Name";
			try {
				BufferedReader maps = new BufferedReader(new FileReader(new File("Resources", "Maps")));
				int numMaps = Integer.parseInt(maps.readLine());
				mapNames = new String[numMaps + 1];
				mapNames[0] = DEFAULT_MAP_NAME;
				for (int i = 0; i < numMaps; i++) {
					mapNames[i + 1] = maps.readLine();
				}
				maps.close();
			} catch (Exception E) {
				E.printStackTrace();
			}

			while (true) {
				try {
					final JComboBox<String> jcb = new JComboBox<String>(mapNames);
					jcb.setEditable(true);
					jcb.addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent e) {
							String selectedItem = (String) jcb.getSelectedItem();
							boolean editable = selectedItem instanceof String
									&& ((String) selectedItem).equals(DEFAULT_MAP_NAME);
							jcb.setEditable(editable);
						}
					});
					int result = JOptionPane.showOptionDialog(null, jcb, "Choose A Map To Edit",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
							new String[] { "Next", "Cancel" }, null);
					if (result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION)
						throw new NullPointerException();
					fileName = ((String) jcb.getSelectedItem()).trim();
				} catch (NullPointerException e2) {
					mainFrame.requestFocus();
					return;
				}

				if (fileName != null && !fileName.isEmpty()) {
					break;
				}
			}
			
			mainFrame.remove(mainMenu);
			mainMenu.close();
			mainFrame.invalidate();
			mainFrame.validate();
			mainMenu = null;

			creatorPanel = new CreatorPanel(fileName);
			mainFrame.add(creatorPanel);
			mainFrame.setVisible(true);
			creatorPanel.revalidate();
			JOptionPane.showMessageDialog(mainFrame, 
					"\nA playable map must have spawners and a castle for both teams as well"
					+ "\nas a clear path between the castles for goblins."
					+ "\n\nIf you save your own map, it'll be playable on your own server!"
					+ "\nIf you think you made a great map, we'd love to add it to our servers!"
					+ "\nYou'll find it in the Resources folder in your game directory."
					+ "\n\nContact us at worldquestdev@gmail.com!", "Creator instructions", JOptionPane.INFORMATION_MESSAGE);
		}

	}

	/**
	 * Open the instructions when this button is pressed
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class OpenInstructions implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (ClientServerSelection.open) {
				serverList.setVisible(true);
				serverList.toFront();
				return;
			}
			if (ClientAccountWindow.open) {
				newLogin.setVisible(true);
				newLogin.toFront();
				return;
			}
			if (Leaderboard.open) {
				leaderboard.setVisible(true);
				leaderboard.toFront();
				return;
			}
			JOptionPane.showMessageDialog(null,
					"We are updating the instructions! The controls are shown in the lobby.", "Sorry",
					JOptionPane.ERROR_MESSAGE);

			mainFrame.requestFocus();
			// mainFrame.remove(mainMenu);
			//mainMenu.close();
			// mainFrame.invalidate();
			// mainFrame.validate();
			// mainMenu = null;
			//
			// instructionPanel = new InstructionPanel();
			// mainFrame.add(instructionPanel);
			// mainFrame.setVisible(true);
			// instructionPanel.revalidate();
			// instructionPanel.repaint();

		}
	}

	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	private static class ExitGame implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object[] options = { "Exit", "Cancel" };
			Frame dialogueFrame = new Frame();
			int confirmExit = JOptionPane.showOptionDialog(dialogueFrame, "Exit the game?", "Confirm Exit",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]); // default
			// button
			// title
			mainFrame.requestFocus();

			if (confirmExit == 0) {
				System.exit(0);
			}

		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent event) {

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}
