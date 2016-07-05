package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import Server.Creatures.ServerCreature;
import Server.Creatures.ServerPlayer;

/**
 * Creates a new world and accepts new client connections
 * 
 * @author William Xu & Alex Raita
 * 
 */
public class Server implements Runnable
{
	private ServerSocket socket;
	private ServerEngine engine;
	private int port;
	private String map;
	private ServerGUI gui;
	private boolean start = false;

	private String[] playerColours = { "DARK", "LIGHT", "TAN" };

	//Lobby variables
	private boolean needLeader = true;
	private ArrayList<ServerLobbyPlayer> lobbyPlayers = new ArrayList<ServerLobbyPlayer>();

	int noOfPlayers = 0;

	public Server(String map, int port)
	{
		this.map = map;
		this.port = port;

		try
		{
			this.socket = new ServerSocket(port);
		}
		catch (IOException e)
		{
			System.out.println("Server cannot be created with given port");
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		Close closeSocket = new Close();
		Thread closeThead = new Thread(closeSocket);
		closeThead.start();

		while(true)
		{
			try{
				Socket newClient = socket.accept();
				ServerLobbyPlayer newPlayer = new ServerLobbyPlayer(newClient, this);
				if(needLeader)
				{
					newPlayer.setLeader();
					needLeader = false;
				}
				lobbyPlayers.add(newPlayer);
				
				Thread playerThread = new Thread(newPlayer);
				playerThread.start();
				System.out.println("A new client has connected");
				gui.repaint();
			}
			catch(Exception E)
			{
				break;
			}
		}

		System.out.println("Exited the lobby");
		try {
			socket = new ServerSocket(port);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		// Construct the new world
		System.out.println("Creating world...");
		try
		{
			if(map.equals(""))
				engine = new ServerEngine();
			else
				engine = new ServerEngine(map);
		}
		catch (IOException e1)
		{
			System.out.println("Error with Creating World and/or Engine");
		}

		engine.setGui(gui);
		gui.startGame(engine.getWorld(),engine);

		Thread newEngine = new Thread(engine);
		newEngine.start();
		broadcast("Start");

		// Accept players into the server
		System.out.println("Waiting for clients to connect");
		ArrayList<ServerLobbyPlayer> lobbyPlayersToAdd = new ArrayList<ServerLobbyPlayer>();
		for(ServerLobbyPlayer player : lobbyPlayers)
		{
			lobbyPlayersToAdd.add(player);
		}

		while (true)
		{
			try
			{
				Socket newClient = socket.accept();
				noOfPlayers++;

				String IP = newClient.getInetAddress().toString();
				int team = -1;
				String name = null;
				ServerLobbyPlayer playerToRemove = null;
				BufferedReader input = null;

				for(ServerLobbyPlayer player : lobbyPlayersToAdd)
				{
					if(player.getIP().equals(IP))
					{
						input = new BufferedReader(new InputStreamReader(newClient.getInputStream()));
						String nameCheck = input.readLine();
						if(nameCheck.equals(player.getName()))
						{
							team = player.getTeam();
							name = player.getName();
							engine.broadcast("JO " + player.getName().split(" ").length + " "
									+ player.getTeam() + player.getName());
							playerToRemove = player;
							break;
						}
					}
				}
				if(team == -1)
				{
					newClient.close();
					continue;

				}
				lobbyPlayersToAdd.remove(playerToRemove);

				int x;
				int y;
				if(team == ServerPlayer.RED_TEAM)
				{
					x = engine.getWorld().getRedCastleX();
					y = engine.getWorld().getRedCastleY();
				}
				else
				{
					x = engine.getWorld().getBlueCastleX();
					y = engine.getWorld().getBlueCastleY();
				}

				int characterSelection = (int) (Math.random() * playerColours.length);
				ServerPlayer newPlayer = new ServerPlayer(x, y,
						ServerPlayer.DEFAULT_WIDTH,
						ServerPlayer.DEFAULT_HEIGHT, -14, -38,
						ServerWorld.GRAVITY, playerColours[characterSelection],
						newClient, engine, engine.getWorld(),input);

				newPlayer.setName(name);
				newPlayer.setTeam(team);
				engine.addPlayer(newPlayer);
				Thread playerThread = new Thread(newPlayer);
				playerThread.start();

				System.out.println("A new client has connected");
			}
			catch (IOException e)
			{
				System.out.println("Error connecting to client");
				e.printStackTrace();
			}
		}
	}

	public void setGUI (ServerGUI gui)
	{
		this.gui=gui;
		gui.setMap(map);
	}
	public ServerEngine getEngine()
	{
		return engine;
	}

	public ArrayList<ServerLobbyPlayer> getPlayers()
	{
		return lobbyPlayers;
	}

	/**
	 * Remove a player from the lobby and update info
	 */
	public void remove(ServerLobbyPlayer player)
	{
		lobbyPlayers.remove(player);
		if(player.getTeam() == ServerCreature.RED_TEAM)
		{
			ServerLobbyPlayer.nextTeam = ServerCreature.RED_TEAM-1;
			ServerLobbyPlayer.numRed--;
		}
		else
		{
			ServerLobbyPlayer.nextTeam = ServerCreature.BLUE_TEAM-1;
			ServerLobbyPlayer.numBlue--;
		}

		if(player.isLeader())
		{
			if(lobbyPlayers.size() > 0)
				lobbyPlayers.get(0).setLeader();
			else needLeader = true;
		}
		broadcast("RO " + player.getName().split(" ").length + " "
				+ player.getTeam() + player.getName());
	}

	/**
	 * Send an instant message to all clients
	 */
	public void broadcast(String message)
	{
		for (ServerLobbyPlayer player : lobbyPlayers)
		{
			player.sendMessage(message);
		}
		gui.addToChat(message);
		gui.repaint();
	}

	public void start()
	{
		start = true;
	}
	
	public String getMap()
	{
		return map;
	}
	
	public void setMap(String map)
	{
		this.map = map;
		gui.setMap(map);
	}
	
	private class Close implements Runnable
	{	
		public void run() {
			while(true)
			{
				if(start)
					try {
						socket.close();
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
}
