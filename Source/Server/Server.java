package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

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
	public final static int MAX_PLAYERS = 2;

	private ServerEngine engine;
	private int port;
	private String map;
	private ServerGUI gui;
	private boolean start = false;

	private PrintWriter output = null;

	private ArrayList<Socket> newPlayerWaiting = new ArrayList<Socket>();
	private int sizeIndex = 0;
	public static String defaultMap;

	private String[] playerColours = { "DARK", "LIGHT", "TAN" };

	// Lobby variables
	private boolean needLeader = true;
	private ArrayList<ServerLobbyPlayer> lobbyPlayers = new ArrayList<ServerLobbyPlayer>();

	int noOfPlayers = 0;

	public boolean isFull()
	{
		if(!start)
		{
			return lobbyPlayers.size() >= MAX_PLAYERS;
		}
		else
			return noOfPlayers>= MAX_PLAYERS;
	}

	public void decreaseNumPlayer()
	{
		noOfPlayers--;
	}
	public boolean started()
	{
		return start;
	}

	public void addClient(Socket newClient,PrintWriter output)
	{
		while(true)
		{
			try{
				newPlayerWaiting.add(newClient);
				break;
			}
			catch(ConcurrentModificationException E)
			{
				System.out.println("Concurrnet modification adding players to game");
			}
		}
	}

	public Socket nextClient() throws Exception
	{
		while(newPlayerWaiting.size() == sizeIndex)
		{
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(start)
			{
				throw new Exception();
			}
		}
		sizeIndex++;
		return newPlayerWaiting.get(sizeIndex-1);
	}

	public Socket nextGameClient() 
	{
		while(newPlayerWaiting.size() == sizeIndex)
		{
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//System.out.printf("%s %d %d", "New player", newPlayerWaiting.size(), sizeIndex);
		sizeIndex++;
		return newPlayerWaiting.get(sizeIndex-1);
	}


	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				Socket newClient = nextClient();
				output = new PrintWriter(newClient.getOutputStream());
				BufferedReader input = new BufferedReader(
						new InputStreamReader(
								newClient.getInputStream()));

				ServerLobbyPlayer newPlayer = new ServerLobbyPlayer(newClient,input,output,
						this);




				if (needLeader)
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
			catch (Exception E)
			{
				System.out.println("Exited the lobby");
				newPlayerWaiting.clear();
				sizeIndex = 0;
				break;
			}
		}

		if (map == null)
		{
			map = defaultMap;
		}

		// Construct the new world
		System.out.println("Creating world...");
		try
		{
			engine = new ServerEngine(map,this);
			gui.setMap(map);
		}
		catch (IOException e1)
		{
			System.out.println("Error with Creating World and/or Engine");
		}

		engine.setGui(gui);
		gui.startGame(engine.getWorld(), engine);

		Thread newEngine = new Thread(engine);
		newEngine.start();
		broadcast("Start");

		// Accept players into the server
		System.out.println("Waiting for clients to connect");
		ArrayList<ServerLobbyPlayer> lobbyPlayersToAdd = new ArrayList<ServerLobbyPlayer>();
		for (ServerLobbyPlayer player : lobbyPlayers)
		{
			lobbyPlayersToAdd.add(player);
		}
		while (true)
		{
			try
			{
				Socket newClient = nextGameClient();
				output = new PrintWriter(newClient.getOutputStream());
				noOfPlayers++;

				BufferedReader input = new BufferedReader(
						new InputStreamReader(
								newClient.getInputStream()));


				String IP = newClient.getInetAddress().toString();
				int team = -1;
				String name = null;
				ServerLobbyPlayer playerToRemove = null;


				String message = input.readLine();
				String nameCheck;

				// If someone connects late, get them to start the client
				if (message.equals("Lobby"))
				{
					System.out.println("Checkpoint 2");
					output.println("Start");
					output.flush();
					output.close();
					input.close();
					noOfPlayers--;

					//Close input and socket
					continue;
				}
				else
				{
					nameCheck = message;
				}


				boolean inServer = false;
				for (ServerLobbyPlayer player : lobbyPlayersToAdd)
				{
					if (player.getIP().equals(IP))
					{
						if (nameCheck.equals(player.getName()))
						{
							team = player.getTeam();
							name = player.getName();
							engine.broadcast("JO "
									+ player.getName().split(" ").length + " "
									+ player.getTeam() + player.getName());
							playerToRemove = player;
							inServer = true;
							break;
						}
					}
				}

				if (!inServer)
				{
					team = engine.getNextTeam();
					name = nameCheck;
					engine.broadcast("JO "
							+ name.split(" ").length + " "
							+ team + name);
				}

				if (team == -1)
				{
					newClient.close();
					continue;

				}
				if (playerToRemove != null)
				{
					lobbyPlayersToAdd.remove(playerToRemove);
				}

				int x;
				int y;
				if (team == ServerPlayer.RED_TEAM)
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
						newClient, engine, engine.getWorld(), input,output);

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
			catch(NullPointerException e)
			{
				System.out.println("Client has disconnected");
			}
		}
	}

	public void setGUI(ServerGUI gui)
	{
		this.gui = gui;
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
		if (player.getTeam() == ServerCreature.RED_TEAM)
		{
			ServerLobbyPlayer.nextTeam = ServerCreature.RED_TEAM - 1;
			ServerLobbyPlayer.numRed--;
		}
		else
		{
			ServerLobbyPlayer.nextTeam = ServerCreature.BLUE_TEAM - 1;
			ServerLobbyPlayer.numBlue--;
		}

		if (player.isLeader())
		{
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

}