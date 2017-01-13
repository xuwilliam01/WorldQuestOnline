package CentralServer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

import javax.swing.Timer;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import ClientUDP.ClientAccountWindow;
import ClientUDP.ServerInfo;
import Server.Creatures.ServerCreature;

public class CentralServer implements Runnable, ActionListener {

	public final static int BASE_ELO = 1000;
	
	//Not currently used
	public final static int LEADERBOARD_SIZE = 20;
	
	private DatagramSocket socket;
	private DatagramPacket receive;
	private DatagramPacket send;

	byte[] receiveData;
	byte[] sendData;

	private ArrayList<ServerInfo> servers = new ArrayList<ServerInfo>();
	private String listServers;
	private String delayedListServers;

	private Element root;
	private Document document;
	private SAXBuilder builder;
	private String FILE_NAME = "Resources//Accounts.xml";

	// Clear servers after a period of time
	private Timer reset;

	private ServerSocket TCPSocket;

	private PriorityQueue<LeaderboardPlayer> leaderboard = new PriorityQueue<LeaderboardPlayer>();

	private String leaderboardS = "";

	public CentralServer() throws IOException, JDOMException {
		socket = new DatagramSocket(ClientAccountWindow.PORT);
		receiveData = new byte[1024];
		sendData = new byte[1024];
		listServers = "";
		delayedListServers = "";
		reset = new Timer(200, this);

		// Build XML
		builder = new SAXBuilder();
		document = builder.build(new File(FILE_NAME));
		root = document.getRootElement();

		TCPSocket = new ServerSocket(ClientAccountWindow.PORT);
		// System.out.println(login("Alex","uhoh"));
		// System.out.println(login("Alex","12313ibsdfibsbfhskjdvbfjhs1234234"));
		// System.out.println(createAccount("Alex3","EasyPass"));
		// System.out.println(createAccount("Alex3","EasyPass"));
		// System.out.println(login("Alex3","EasyPass"));

		// Create the leaderboard
		createLeaderboard();
	}

	//No currently using this method
	public void createLeaderboard2() {
		leaderboard.clear();
		for (Element user : root.getChildren("User")) {
			int elo = Integer.parseInt(user.getChild("Elo").getValue());
			int wins = Integer.parseInt(user.getChild("Wins").getValue());
			int losses = Integer.parseInt(user.getChild("Losses").getValue());
			if (leaderboard.size() >= LEADERBOARD_SIZE && elo <= leaderboard.peek().getRating())
				continue;
			leaderboard.add(new LeaderboardPlayer(user.getAttributeValue("name"), elo, wins, losses, leaderboard.size()+1));
			if (leaderboard.size() > LEADERBOARD_SIZE)
				leaderboard.poll();
		}
		synchronized (leaderboardS) {
			leaderboardS = "";
			while (!leaderboard.isEmpty()) {
				LeaderboardPlayer next = leaderboard.poll();
				leaderboardS = next.getName().split(" ").length + " " + next.getRating() + " " + next.getWins() + " "
						+ next.getLosses() + " " + next.getName() + " " + leaderboardS;
			}
			leaderboardS.trim();
		}

	}

	public void createLeaderboard()
	{
		leaderboard.clear();
		for (Element user : root.getChildren("User")) {
			int elo = Integer.parseInt(user.getChild("Elo").getValue());
			int wins = Integer.parseInt(user.getChild("Wins").getValue());
			int losses = Integer.parseInt(user.getChild("Losses").getValue());
			leaderboard.add(new LeaderboardPlayer(user.getAttributeValue("name"), elo, wins, losses, leaderboard.size()+1));
		}
		synchronized (leaderboardS) {
			leaderboardS = "";
			while (!leaderboard.isEmpty()) {
				LeaderboardPlayer next = leaderboard.poll();
				leaderboardS = next.getName().split(" ").length + " " + next.getRating() + " " + next.getWins() + " "
						+ next.getLosses() + " " + next.getName() + " " + leaderboardS;
			}
			leaderboardS.trim();
		}
	}
	
	public void run() {
		reset.start();
		Thread TCPThread = new Thread(new TCPIn());
		TCPThread.start();
		while (true) {
			receiveData = new byte[1024];
			receive = new DatagramPacket(receiveData, receiveData.length);
			try {
				socket.receive(receive);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Get input
			String input = new String(receive.getData()).trim();

			try {
				switch (input.charAt(0)) {
				// Send clients the list of servers
				case 'G':
					String serversOut = "S " + receive.getAddress().toString() + " " + delayedListServers;
					serversOut.trim();
					sendData = serversOut.getBytes();
					send = new DatagramPacket(sendData, sendData.length, receive.getAddress(), receive.getPort());
					socket.send(send);
					break;
				case 'L':
					String[] tokens = input.split(" ");
					String key = tokens[1];
					String name = tokens[2];
					for (int i = 3; i < tokens.length; i++)
						name += " " + tokens[i];
					String out;
					if (login(name, key))
						out = "LY";
					else
						out = "LN";
					sendData = out.getBytes();
					send = new DatagramPacket(sendData, sendData.length, receive.getAddress(), receive.getPort());
					socket.send(send);
					break;
				case 'C':
					tokens = input.split(" ");
					key = tokens[1];
					name = tokens[2];
					for (int i = 3; i < tokens.length; i++)
						name += " " + tokens[i];
					if (createAccount(name, key)) {
						out = "CY";
					} else
						out = "CN";
					sendData = out.getBytes();
					send = new DatagramPacket(sendData, sendData.length, receive.getAddress(), receive.getPort());
					socket.send(send);
					break;
				case 'B':
					sendData = leaderboardS.getBytes();
					send = new DatagramPacket(sendData, sendData.length, receive.getAddress(), receive.getPort());
					socket.send(send);
					break;
				// Get your own player stats
				case 'S':
					name = input.substring(2);
					int[] stats = getStats(name);
					if (stats == null)
						break;
					sendData = ("S " + stats[0] + " " + stats[1] + " " + stats[2] + " "+name).getBytes();
					send = new DatagramPacket(sendData, sendData.length, receive.getAddress(), receive.getPort());
					socket.send(send);
					break;
				//Ping the central server to see if it's up
				case 'P':
					sendData = "p".getBytes();
					send = new DatagramPacket(sendData, sendData.length, receive.getAddress(), receive.getPort());
					socket.send(send);
					break;
				}
			} catch (Exception e) {
				System.out.println("Bad Input");
				e.printStackTrace();
			}
		}

	}

	public void updateRating(GameResult[] red, GameResult[] blue, int winner) {
		double avgEloR = 0;
		double avgKillsR = 0;
		double avgEloB = 0;
		double avgKillsB = 0;
		for (GameResult acc : red) {
			int elo = getElo(acc.getName());
			acc.setElo(elo);
			avgEloR += elo;
			avgKillsR += acc.getKills();
		}
		avgEloR /= 1.0 * red.length;
		avgKillsR /= 1.0 * red.length;
		for (GameResult acc : blue) {
			int elo = getElo(acc.getName());
			acc.setElo(elo);
			avgEloB += elo;
			avgKillsB += acc.getKills();
		}
		avgEloB /= 1.0 * blue.length;
		avgKillsB /= 1.0 * blue.length;

		int actualR = 0;
		int actualB = 1;
		if (winner == ServerCreature.RED_TEAM) {
			actualR = 1;
			actualB = 0;
		}

		for (GameResult acc : red) {
			int k;
			if (acc.getElo() <= 2100)
				k = 32;
			else if (acc.getElo() <= 2400)
				k = 24;
			else
				k = 16;
			double expected = 1 / (1 + Math.pow(10, (avgEloB - acc.getElo()) / 400.0));
			boolean win = false;
			if (actualR == 1)
				win = true;
			int newElo = Math.max((int) (acc.getElo() + k * (actualR - expected) + acc.getKills() - avgKillsR),
					BASE_ELO);
			setElo(acc.getName(), newElo, win);
		}
		for (GameResult acc : blue) {
			int k;
			if (acc.getElo() <= 2100)
				k = 32;
			else if (acc.getElo() <= 2400)
				k = 24;
			else
				k = 16;
			double expected = 1 / (1 + Math.pow(10, (avgEloR - acc.getElo()) / 400.0));
			boolean win = false;
			if (actualB == 1)
				win = true;
			// Yes ranking can go up even if you lose
			int newElo = Math.max((int) (acc.getElo() + k * (actualB - expected) + acc.getKills() - avgKillsB),
					BASE_ELO);
			setElo(acc.getName(), newElo, win);
		}
		saveXML();

	}

	public int getElo(String name) {
		for (Element username : root.getChildren()) {
			if (username.getAttribute("name").getValue().equals(name)) {
				return Integer.parseInt(username.getChild("Elo").getValue());
			}
		}
		return -1;
	}

	public void setElo(String name, int value, boolean win) {
		for (Element username : root.getChildren()) {
			if (username.getAttribute("name").getValue().equals(name)) {
				username.getChild("Elo").setText(Integer.toString(value));
				if (win)
					username.getChild("Wins").setText(Integer.parseInt(username.getChild("Wins").getValue()) + 1 + "");
				else
					username.getChild("Losses")
							.setText(Integer.parseInt(username.getChild("Losses").getValue()) + 1 + "");
			}
		}
	}

	public int[] getStats(String name) {
		for (Element username : root.getChildren()) {
			if (username.getAttribute("name").getValue().equals(name)) {
				return new int[] { Integer.parseInt(username.getChild("Elo").getValue()),
						Integer.parseInt(username.getChild("Wins").getValue()),
						Integer.parseInt(username.getChild("Losses").getValue()) };
			}
		}
		return null;
	}

	public boolean login(String user, String key) {
		for (Element username : root.getChildren()) {
			if (username.getAttribute("name").getValue().equals(user)) {
				return key.equals(username.getChild("Key").getValue());
			}
		}
		return false;
	}

	public void saveXML() {
		// Save with new username + key
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		try {
			xmlOutputter.output(document, new FileOutputStream(new File(FILE_NAME)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean createAccount(String user, String key) {
		for (Element username : root.getChildren()) {
			if (username.getAttribute("name").getValue().equals(user)) {
				// If account already exists, but the username and password
				// match, then pretend we made a new account
				if (key.equals(username.getChild("Key").getValue()))
					return true;

				return false;
			}
		}

		Element newUser = new Element("User");
		Attribute username = new Attribute("name", user);
		Element newKey = new Element("Key");
		newKey.setText(key);
		newUser.addContent(newKey);
		Element newElo = new Element("Elo");
		newElo.setText(Integer.toString(BASE_ELO));
		Element newWins = new Element("Wins");
		newWins.setText("0");
		Element newLosses = new Element("Losses");
		newLosses.setText("0");
		newUser.addContent(newElo);
		newUser.addContent(newWins);
		newUser.addContent(newLosses);
		newUser.setAttribute(username);
		root.addContent(newUser);

		saveXML();

		createLeaderboard();

		return true;
	}

	public void actionPerformed(ActionEvent arg0) {
		// Remove servers when necessary
		synchronized (servers) {
			// System.out.println("Clearing Servers");
			delayedListServers = listServers;
			servers.clear();
			listServers = "";
		}
	}

	private class TCPIn implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					Socket newServer = TCPSocket.accept();
					
					//Make sure you can create two servers from the same IP
					String IP = newServer.getInetAddress().toString();
					synchronized(servers)
					{
						for(ServerInfo server : servers)
						{
							if(server.getIP().equals(IP))
							{
								newServer.close();
								continue;
							}
						}
					}
					
					Thread inputThread = new Thread(new ReadIn(newServer));
					inputThread.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	LinkedList<String> pastServers = new LinkedList<String>();

	private class ReadIn implements Runnable {
		Socket server;
		BufferedReader input;
		PrintWriter output;

		public ReadIn(Socket server) {
			this.server = server;
		}

		public void run() {
			try {
				input = new BufferedReader(new InputStreamReader(server.getInputStream()));
				output = new PrintWriter(server.getOutputStream());
				while (true) {
					try {
						String command = input.readLine();
						// System.out.println(command);
						switch (command.charAt(0)) {
						// Get info from servers
						case 'A':
							String[] tokens = command.split(" ");
							ServerInfo newServer = new ServerInfo(tokens[1], server.getInetAddress().toString(),
									Integer.parseInt(tokens[3]), Integer.parseInt(tokens[2]));
							synchronized (servers) {
								if (!servers.contains(newServer)) {
									// System.out.println(newServer.getIP() + "
									// " + newServer.getPort());
									servers.add(newServer);
									listServers += newServer.getName() + " " + newServer.getIP() + " "
											+ newServer.getPort() + " " + newServer.getNumPlayers() + " ";

									if (!pastServers.contains(newServer.getName())) {
										pastServers.add(newServer.getName());
										System.out.println(
												"Server added: " + newServer.getName() + " " + newServer.getIP() + " "
														+ newServer.getPort() + " " + newServer.getNumPlayers());
									}

								}
							}
							break;
						case 'l':
							tokens = command.split(" ");
							String key = tokens[1];
							String name = tokens[2];
							for (int i = 3; i < tokens.length; i++)
								name += " " + tokens[i];
							String out;
							if (login(name, key)) {
								out = "L " + key + " " + name;
								send(out);
							}
							break;
						case 'E':
							System.out.println("A Game ended");
							tokens = command.split(" ");
							int winner = Integer.parseInt(tokens[1]);
							int rTeam = Integer.parseInt(tokens[2]);
							int bTeam = Integer.parseInt(tokens[3]);
							if (bTeam <= 0 || rTeam <= 0)
								break;

							GameResult[] red = new GameResult[rTeam];
							GameResult[] blue = new GameResult[bTeam];

							int index = 4;
							for (int i = 0; i < rTeam; i++) {
								int len = Integer.parseInt(tokens[index++]);
								String playerName = tokens[index++];
								for (int j = 1; j < len; j++) {
									playerName += " " + tokens[index++];
								}
								int kills = Integer.parseInt(tokens[index++]);
								red[i] = new GameResult(playerName, kills);
							}
							for (int i = 0; i < bTeam; i++) {
								int len = Integer.parseInt(tokens[index++]);
								String playerName = tokens[index++];
								for (int j = 1; j < len; j++) {
									playerName += " " + tokens[index++];
								}
								int kills = Integer.parseInt(tokens[index++]);
								blue[i] = new GameResult(playerName, kills);
							}
							updateRating(red, blue, winner);
							createLeaderboard();
							break;
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				// System.out.println("Server Disconnected");
				return;
			}
		}

		public void send(String s) {
			output.println(s);
			output.flush();
		}
	}

	// Do not use this method
	public void resetPassowrd() {
		for (Element username : root.getChildren()) {
			String name = username.getAttribute("name").getValue();
			String pass = username.getChild("Key").getValue().substring(name.length());
			username.getChild("Key").setText(ClientAccountWindow.hash(name, pass));
		}
		saveXML();
	}
}
