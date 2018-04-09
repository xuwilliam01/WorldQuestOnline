package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import Server.Creatures.ServerAIPlayer;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerPlayer;

/**
 * Creates a new world and accepts new client connections
 * 
 * @author William Xu & Alex Raita
 * 
 */
public class Server implements Runnable {
	public static int MAX_PLAYERS = 12;

	private ServerEngine engine;
	private String map;
	private ServerGUI gui;
	private boolean start = false;

	private PrintWriter output = null;

	private ArrayList<Triple> newPlayerWaiting = new ArrayList<Triple>();
	private int sizeIndex = 0;
	public static String defaultMap;

	private String[] playerColours = { "DARK", "LIGHT", "TAN" };
	private String[] playerHairs = {"HAIR0BEIGE", "HAIR1BEIGE", "HAIR0BLACK", "HAIR1BLACK", "HAIR0BLOND", "HAIR1BLOND", "HAIR0GREY","HAIR1GREY"};

	// Lobby variables
	private boolean needLeader = true;
	private ArrayList<ServerLobbyPlayer> lobbyPlayers = new ArrayList<ServerLobbyPlayer>();

	int noOfPlayers = 0;

	private boolean closeServer = false;

	private ServerManager manager;

	//Use this to keep track of all the players who joined
	private ArrayList<ServerPlayer> allConnectedPlayers = new ArrayList<ServerPlayer>();
	private ArrayList<String> allowedPlayers = new ArrayList<String>();
	
	private boolean running;

	public Server(ServerManager manager)
	{
		this.manager = manager;
		System.out.println("Server opened");
	}

	public ServerManager getManager() {
		return manager;
	}

	public void setManager(ServerManager manager) {
		this.manager = manager;
	}

	public boolean isFull() {
		if (!start) {
			return lobbyPlayers.size() >= MAX_PLAYERS;
		} else
			return noOfPlayers >= MAX_PLAYERS;
	}

	public void decreaseNumPlayer() {
		noOfPlayers--;
	}

	public boolean started() {
		return start;
	}

	public void addClient(Socket newClient, BufferedReader input, String name) {
		while (true) {
			try {
				newPlayerWaiting.add(new Triple(newClient, input, name));
				break;
			} catch (ConcurrentModificationException E) {
				System.out
				.println("Concurrnet modification adding players to game");
			}
		}
	}

	public Socket nextClient() throws Exception {
		while (newPlayerWaiting.size() == sizeIndex) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (start) {
				throw new Exception();
			}
		}
		sizeIndex++;
		return newPlayerWaiting.get(sizeIndex - 1).socket;
	}

	public Triple nextGameClient() {
		while (newPlayerWaiting.size() == sizeIndex && !closeServer) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// System.out.printf("%s %d %d", "New player", newPlayerWaiting.size(),
		// sizeIndex);
		if(closeServer)
			return null;

		sizeIndex++;
		return newPlayerWaiting.get(sizeIndex - 1);
	}

	public void terminate()
	{
		running = false;
		close();
		System.out.println("Server closed");
	}
	
	@Override
	public void run() {
		running = true;
		
		while (running && !Thread.interrupted()) {
			try {
				Socket newClient = nextClient();
				output = new PrintWriter(newClient.getOutputStream());
				ServerManager.trackService(output);
				BufferedReader input = new BufferedReader(
						new InputStreamReader(newClient.getInputStream()));
				ServerManager.trackService(input);
				ServerLobbyPlayer newPlayer = new ServerLobbyPlayer(newClient,
						input, output, this);

				if (needLeader) {
					newPlayer.setLeader();
					needLeader = false;
				}
				lobbyPlayers.add(newPlayer);
				
				Thread playerThread = new Thread(newPlayer);
				ServerManager.trackService(playerThread);
				playerThread.start();
				System.out.println("A new client has connected");

				if (ServerManager.HAS_FRAME) {
					gui.repaint();
				}
				
				if(lobbyPlayers.size() >= MAX_PLAYERS)
				{
					if(Math.abs(ServerLobbyPlayer.numBlue - ServerLobbyPlayer.numRed) < 2)
						start();
					else broadcast("CH E 1 " + ServerCreature.NEUTRAL
							+ "Server " + 5 + " "
							+ "Balance the teams to start");
				}
			} catch (Exception E) {
				System.out.println("Exited the lobby");
				newPlayerWaiting.clear();
				sizeIndex = 0;
				break;
			}
		}
		
		if (!running || Thread.interrupted())
		{
			return;
		}

		long startTime = System.currentTimeMillis();
		ServerLobbyPlayer.numRed = 0;
		ServerLobbyPlayer.numBlue = 0;
		
		//Set up the allowed players
		for(ServerLobbyPlayer p : lobbyPlayers)
		{
			allowedPlayers.add(p.getName());
		}
		if (map == null) {
			map = defaultMap;
		}

		// Construct the new world
		System.out.println("Creating world...");
		try {
			engine = new ServerEngine(map, this);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("Error with Creating World and/or Engine");
		}

		if (ServerManager.HAS_FRAME) {
			gui.setMap(map);
			engine.setGui(gui);
			gui.startGame(engine.getWorld(), engine);
		}

		broadcast("Start");

		// Accept players into the server
		System.out.println("Waiting for clients to connect");
		ArrayList<ServerLobbyPlayer> lobbyPlayersToAdd = new ArrayList<ServerLobbyPlayer>();
		for (ServerLobbyPlayer player : lobbyPlayers) {
			lobbyPlayersToAdd.add(player);
		}
		while (running && !Thread.interrupted()) {
			try {
				Triple next = nextGameClient();
				Socket newClient = next.socket;
				String name = next.name;
				if(closeServer)
					return;

				try {
					newClient.setReceiveBufferSize(1024);
					newClient.setSendBufferSize(1024*16);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				output = new PrintWriter(newClient.getOutputStream());
				ServerManager.trackService(output);
				noOfPlayers++;

				BufferedReader input = new BufferedReader(
						new InputStreamReader(newClient.getInputStream()));
				ServerManager.trackService(input);
				String IP = newClient.getInetAddress().toString();
				ServerLobbyPlayer playerToRemove = null;

				String message = input.readLine();

				// If someone connects late, get them to start the client
				if (message.equals("Lobby")) {
					if(System.currentTimeMillis() - startTime < 5000)
					{
						boolean isNew = true;
						for(String s : allowedPlayers)
						{
							if(s.equals(name))
							{
								isNew = false;
							}
						}
						if(allowedPlayers.size() >= MAX_PLAYERS && isNew)
							output.println("Full");
						else
						{
							if(isNew)
								allowedPlayers.add(name);
							output.println("Start");
						}
					}
					else
						output.println("Start");
					output.flush();
					output.close();
					input.close();
					noOfPlayers--;

					// Close input and socket
					continue;
				}

				ServerPlayer newPlayer = null;
				boolean inServer = false;
				
				
				//Check if the player already joined
				synchronized(engine.getSavedPlayers())
				{
					SavedPlayer toRemove = null;
					for(SavedPlayer p : engine.getSavedPlayers())
					{
						if(name.equals(p.name))
						{
							int x = engine.getWorld().getBlueCastleX();
							int y = engine.getWorld().getBlueCastleY();
							if (p.team == ServerPlayer.RED_TEAM) {
								x = engine.getWorld().getRedCastleX();
								y = engine.getWorld().getRedCastleY();
							}

							newPlayer = new ServerPlayer(name, x, y,
									ServerPlayer.DEFAULT_WIDTH,
									ServerPlayer.DEFAULT_HEIGHT,
									ServerWorld.GRAVITY, p.skinColour, p.hair,
									newClient, engine, engine.getWorld(), input, output);

							newPlayer.increaseMoney(p.money);
							newPlayer.setKills(p.kills);
							newPlayer.setDeaths(p.deaths);
							newPlayer.addTotalDamage(p.totalDmg);
							newPlayer.addTotalMoneySpent(p.totalMoney);
							newPlayer.setTeam(p.team);
							if(p.bestWeapon != null)
								newPlayer.addItem(p.bestWeapon);
							if(p.bestArmour != null)
								newPlayer.addItem(p.bestArmour);
							if(engine.getWorld().getWorldCounter() - p.leaveTime < 600)
							{
								newPlayer.setAlive(false);
								newPlayer.setDeathCounter(p.leaveTime);
								newPlayer.setHP(0);
							}	
							toRemove = p;
							engine.broadcast("d "
									+ newPlayer.getName().split(" ").length + " "
									+ newPlayer.getTeam() + newPlayer.getName());
							break;
						}
					}
					if(toRemove != null)
					{
						engine.getSavedPlayers().remove(toRemove);
					}
					//If a new player, initialize them
					else 
					{
						int characterSelection = (int) (Math.random() * playerColours.length);
						int randomHair = (int)(Math.random() * playerHairs.length);

						int team = -1;
						
						for (ServerLobbyPlayer player : lobbyPlayersToAdd) {
							if (player.getIP().equals(IP)) {
								if (name.equals(player.getName())) {
									team = player.getTeam();
									name = player.getName();
									engine.broadcast("d "
											+ player.getName().split(" ").length + " "
											+ player.getTeam() + player.getName());
									playerToRemove = player;
									inServer = true;
									break;
								}
							}
						}
						
						if (!inServer) {
							team = engine.getNextTeam();
							engine.broadcast("d " + name.split(" ").length + " "
									+ team + name);
						}

						if (team == -1) {
							newClient.close();
							continue;

						}
						if (playerToRemove != null) {
							lobbyPlayersToAdd.remove(playerToRemove);
						}

						int x = engine.getWorld().getBlueCastleX();
						int y = engine.getWorld().getBlueCastleY();
						if (team == ServerPlayer.RED_TEAM) {
							x = engine.getWorld().getRedCastleX();
							y = engine.getWorld().getRedCastleY();
						}

						newPlayer = new ServerPlayer(name, x, y,
								ServerPlayer.DEFAULT_WIDTH,
								ServerPlayer.DEFAULT_HEIGHT,
								ServerWorld.GRAVITY, playerColours[characterSelection], playerHairs[randomHair],
								newClient, engine, engine.getWorld(), input, output);
						newPlayer.setTeam(team);
						newPlayer.initPlayer();
					}
				}

				engine.addPlayer(newPlayer);

				if (!inServer || lobbyPlayersToAdd.isEmpty())
				{
					engine.rebalanceAIPlayers();
				}
				
				//This is to keep track of all players who joined
				ServerPlayer toRemove = null;
				for(ServerPlayer player : allConnectedPlayers)
				{
					if(player.getName().equals(newPlayer.getName()))
					{
						toRemove = player;
					}
				}
				if(toRemove != null)
				{
					allConnectedPlayers.remove(toRemove);
				}
				allConnectedPlayers.add(newPlayer);

				Thread playerThread = new Thread(newPlayer);
				ServerManager.trackService(playerThread);
				playerThread.start();

				System.out.println("A new client has connected");
			} catch (IOException e) {
				System.out.println("Error connecting to client");
				e.printStackTrace();
			} catch (NullPointerException e) {
				return;
			}
		}
	}

	public ArrayList<ServerPlayer> getAllConnectedPlayers()
	{
		return allConnectedPlayers;
	}

	public void setGUI(ServerGUI gui) {
		this.gui = gui;
		gui.setMap(map);
	}

	public ServerEngine getEngine() {
		return engine;
	}

	public ArrayList<ServerLobbyPlayer> getPlayers() {
		return lobbyPlayers;
	}

	/**
	 * Remove a player from the lobby and update info
	 */
	public void remove(ServerLobbyPlayer player) {
		lobbyPlayers.remove(player);
		if (player.getTeam() == ServerCreature.RED_TEAM) {
			ServerLobbyPlayer.nextTeam = ServerCreature.RED_TEAM - 1;
			ServerLobbyPlayer.numRed--;
		} else {
			ServerLobbyPlayer.nextTeam = ServerCreature.BLUE_TEAM - 1;
			ServerLobbyPlayer.numBlue--;
		}

		if (player.isLeader()) {
			if (lobbyPlayers.size() > 0)
				lobbyPlayers.get(0).setLeader();
			else
				needLeader = true;
		}
		broadcast("RO " + player.getName().split(" ").length + " "
				+ player.getTeam() + player.getName());
	}

	/**
	 * Send an instant message to all clients
	 */
	public void broadcast(String message) {
		while(true)
		{
			try{
				for (ServerLobbyPlayer player : lobbyPlayers) {
					player.sendMessage(message);
				}
				break;
			}
			catch (ConcurrentModificationException e)
			{

			}
		}
		if (ServerManager.HAS_FRAME) {
			gui.addToChat(message);
			gui.repaint();
		}
	}

	public void start() {
		start = true;
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
		if (ServerManager.HAS_FRAME) {
			gui.setMap(map);
		}
	}

	public void close()
	{
		closeServer = true;
	}

	public class Triple{
		Socket socket;
		BufferedReader reader;
		String name;

		public Triple(Socket socket, BufferedReader reader, String name)
		{
			this.socket = socket;
			this.reader = reader;
			this.name = name;
		}
	}

	public ArrayList<String> getAllowedPlayers()
	{
		return allowedPlayers;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public String[] getPlayerColours() {
		return playerColours;
	}

	public void setPlayerColours(String[] playerColours) {
		this.playerColours = playerColours;
	}

	public String[] getPlayerHairs() {
		return playerHairs;
	}

	public void setPlayerHairs(String[] playerHairs) {
		this.playerHairs = playerHairs;
	}
}