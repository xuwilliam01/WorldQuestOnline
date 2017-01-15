package Server;

import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

import javax.swing.Timer;

import Imports.Audio;
import Imports.ImageReferencePair;
import Imports.Images;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerPlayer;

/**
 * Runs the actual game
 * 
 * @author William Xu & Alex Raita
 *
 */
public class ServerEngine implements Runnable, ActionListener {

	/**
	 * A list of all the players in the server
	 */
	private ArrayList<ServerPlayer> listOfPlayers;

	/**
	 * Player to remove
	 */
	private ArrayList<ServerPlayer> toRemove = new ArrayList<ServerPlayer>();

	/**
	 * The number of possible ID's for any objects. The number of objects
	 * existing the game (aside from tiles) cannot exceed this limit
	 */
	public static final int NUMBER_OF_IDS = 100000;

	private final int normalIDStart = 500;

	/**
	 * The highest ID not used yet for normal objects
	 */
	private int nextID = normalIDStart;

	/**
	 * The highest ID not used yet for building objects
	 */
	private int nextBuildingID = 0;

	private boolean[] usedIDs= new boolean[NUMBER_OF_IDS];



	// /**
	// * Stack of freeIDs to use
	// */
	// private PriorityQueue<Integer> freeIDs = new PriorityQueue<Integer>();

	/**
	 * The world the engine works with
	 */
	private ServerWorld world;

	/**
	 * The rate at which the game runs at, in milliseconds
	 */
	public final static int UPDATE_RATE = 15;

	/**
	 * The startTime for checking FPS
	 */
	private long startTime = 0;

	/**
	 * The current FPS of the client
	 */
	private int currentFPS = 60;

	/**
	 * The team of the last player added
	 */
	private int lastTeam = 1;

	/**
	 * The game loop timer
	 */
	private Timer updateTimer;

	/**
	 * The map for the server
	 */
	private ServerGUI gui = null;

	private boolean endGame = false;
	private int losingTeam;
	private Server server;

	private ArrayList<SavedPlayer> savedPlayers = new ArrayList<SavedPlayer>();
	/**
	 * Constructor for the engine
	 */
	public ServerEngine(String map, Server server) throws IOException {
		// Start importing the images from the file (place in a loading screen
		// or something later)
		Images.importImages();
		Audio.importAudio();
		this.server = server;
		ImageReferencePair.importReferences();

		listOfPlayers = new ArrayList<ServerPlayer>();
		world = new ServerWorld(this, map);
	}

	/**
	 * Set the gui
	 * 
	 * @param gui
	 */
	public void setGui(ServerGUI gui) {
		this.gui = gui;
	}

	@Override
	/**
	 * Constantly update the game
	 */
	public void run() {
		updateTimer = new Timer(UPDATE_RATE, this);
		startTime = System.currentTimeMillis();
		updateTimer.start();
	}

	/**
	 * Send messages to all the clients updating their player's data
	 */
	public void updateClients() {
		synchronized(listOfPlayers)
		{
			if (!toRemove.isEmpty()) {
				for (ServerPlayer player : toRemove)
					listOfPlayers.remove(player);
				toRemove.clear();
			}

			//Everyone left the game, so end it
			if(listOfPlayers.isEmpty() && !savedPlayers.isEmpty())
			{
				server.getAllConnectedPlayers().clear();
				endGame(ServerCreature.RED_TEAM);
			}

			try{
				for (ServerPlayer player : listOfPlayers) {
					try {
						player.updateClient();
					} catch(Exception e) {
						System.out.println("Caught exception");
						e.printStackTrace();
					}
					if (endGame) {
						player.setEndGame(true, losingTeam);
					}
				}
				if (endGame) {
					String redPlayers ="";
					String bluePlayers ="";
					int winner = ServerCreature.BLUE_TEAM;
					if(losingTeam == ServerCreature.BLUE_TEAM)
						winner = ServerCreature.RED_TEAM;
					int numRed = 0;
					int numBlue = 0;
					for (ServerPlayer player : server.getAllConnectedPlayers()) {
						if(player.getTeam() == ServerCreature.RED_TEAM)
						{
							numRed++;
							redPlayers += " "+player.getName().split(" ").length+" "+player.getName()+" "+player.getKills();
						} else 
						{
							numBlue++;
							bluePlayers += " "+player.getName().split(" ").length+" "+player.getName()+" "+player.getKills();
						}
					}
					if(server.getAllConnectedPlayers().size() > 1)
						server.getManager().send("E "+winner+" "+numRed+" "+numBlue+redPlayers+bluePlayers);
					server.getManager().removeRoom(server);
					close();
					server.close();
				}

			} catch (ConcurrentModificationException e) {
				System.out.println("Concurrent modification occured");
				e.printStackTrace();
			}
		}
	}

	public void endGame(int losingTeam) {
		endGame = true;
		this.losingTeam = losingTeam;
		usedIDs= new boolean[NUMBER_OF_IDS];
		nextID = normalIDStart;
		nextBuildingID = 0;
	}

	public void close() {
		updateTimer.stop();
		listOfPlayers.clear();
		toRemove.clear();
		// gui.close();
		world.close();
	}

	/**
	 * Send an instant message to all clients
	 */
	public void broadcast(String message) {
		synchronized(listOfPlayers)
		{
			for (ServerPlayer player : listOfPlayers) {
				player.sendMessage(message);
			}
		}
		if (ServerManager.HAS_FRAME) {
			gui.addToChat(message);
		}
	}

	/**
	 * Sends a message to the given team
	 */
	public void broadCastTeam(String message, int team) {
		synchronized(listOfPlayers)
		{
			for (ServerPlayer player : listOfPlayers) {
				if (player.getTeam() == team)
					player.sendMessage(message);
			}
		}
		if (ServerManager.HAS_FRAME) {
			gui.addToChat(message);
		}
	}

	/**
	 * Remove a player from the array list
	 * 
	 * @param remove
	 */
	public synchronized void removePlayer(ServerPlayer remove) {
		toRemove.add(remove);
		lastTeam--;
		synchronized(listOfPlayers)
		{
			listOfPlayers.remove(remove);
		}
		world.remove(remove);
		server.decreaseNumPlayer();
		broadcast("R " + ServerPlayer.toChars(remove.getID()));
		broadcast("o " + remove.getName().split(" ").length + " "
				+ remove.getTeam() + remove.getName());
		//Remove from the scoreboard
		broadcast("n " + ServerPlayer.toChars(remove.getID()) + " " + remove.getTeam());
	}

	/**
	 * Add a new player to the game
	 * 
	 * @param newPlayer
	 *            the new player
	 */
	public void addPlayer(ServerPlayer newPlayer) {
		synchronized(listOfPlayers)
		{
			listOfPlayers.add(newPlayer);

			//For the scoreboard
			broadcast("^ " + newPlayer.getName().split(" ").length + " " + newPlayer.getName() + " "
					+ ServerPlayer.toChars(newPlayer.getID()) + " " + newPlayer.getTeam()+" "+ newPlayer.getKills() +" "+ newPlayer.getDeaths() + " " + ServerPlayer.toChars(newPlayer.getScore()) + " " + newPlayer.getPing());
			for(ServerPlayer player : listOfPlayers)
			{
				if(player.getID() != newPlayer.getID())
					newPlayer.sendMessage("^ " + player.getName().split(" ").length + " " + player.getName() + " "
							+ ServerPlayer.toChars(player.getID()) + " " + player.getTeam()+" "+ player.getKills()+" "+ player.getDeaths() + " "+ServerPlayer.toChars(player.getScore()) + " " + player.getPing());
			}
		}
		world.add(newPlayer);
	}

	/**
	 * Use and reserve the next available ID in the list of booleans
	 * 
	 * @return the id
	 */
	public synchronized int useNextID() {

		while (true) {
			for (int no = nextID; no < NUMBER_OF_IDS; no++) {
				if (!usedIDs[no]) {
					usedIDs[no] = true;
					nextID = no+1;
					return no;
				}
			}
			nextID = normalIDStart;
		}

	}

	/**
	 * Use and reserve the next available building ID (to make sure they underlap other objects) in the list of booleans
	 * 
	 * @return the id
	 */
	public synchronized int useNextBuildingID()
	{
		while (true) {
			for (int no = nextBuildingID; no < normalIDStart; no++) {
				if (!usedIDs[no]) {
					usedIDs[no] = true;
					nextBuildingID = no+1;
					return no;
				}
			}
			nextBuildingID = 1;
		}
	}

	/**
	 * Remove an object's id after it is destroyed
	 * 
	 * @return
	 */
	public void removeID(int id) {
		usedIDs[id] = false;
	}

	@Override
	/**
	 * Update the game after every game tick (15 milliseconds)
	 */
	public void actionPerformed(ActionEvent e) {

		// Remove disconnected players and update all player scores/pings
		synchronized(listOfPlayers)
		{
			ArrayList<ServerPlayer> listOfRemovedPlayers = new ArrayList<ServerPlayer>();
			for (ServerPlayer player : listOfPlayers) {
				broadcast("s "+ServerPlayer.toChars(player.getID())+" "+ServerPlayer.toChars(player.getScore())+" "+player.getPing()+" "+player.getTeam());
				if (player.isDisconnected()) {
					listOfRemovedPlayers.add(player);
				}
			}
			for (ServerPlayer player : listOfRemovedPlayers) {
				listOfPlayers.remove(player);
			}
		}

		// Move all the objects around and update them
		world.update();

		// Update all the clients with the new player data
		updateClients();

		// Update the gui
		if (ServerManager.HAS_FRAME) {
			if (gui != null) {
				gui.update();
			}
		}

		// Parameters to fix the lag spike
		if (world.getWorldCounter() > 1000 && getCurrentFPS() < 30 && !lagSpike) {
			System.out.println("~LAG DETECTED~");
			System.out.println("CURRENT SERVER FPS: " + getCurrentFPS());
			System.out.println("Number of objects in this world: "
					+ world.getObjects().size());
			lagSpike = true;
		}

		if (world.getWorldCounter() % 500 == 0) {
			lagSpike = false;
		}

		// Check how long a game loop took
		long loopTime = System.nanoTime() - startTime;

		if (world.getWorldCounter() % 60 == 0) {
			currentFPS = Math.min(60, (int) ((1000000000.0 / loopTime)
					/ (1000.0 / UPDATE_RATE) * 60 + 0.5));
		}

		startTime = System.nanoTime();
	}

	boolean lagSpike = false;
	boolean checkObjects = false;

	/**
	 * Get the emptiest team
	 * 
	 * @return
	 */
	public int getNextTeam() {
		int noOfBlue = 0;
		int noOfRed = 0;
		synchronized(listOfPlayers)
		{
			for (ServerPlayer player : listOfPlayers) {
				if (player.getTeam() == ServerCreature.RED_TEAM) {
					noOfRed++;
				} else {
					noOfBlue++;
				}
			}
		}
		if (noOfBlue == noOfRed) {
			if (Math.random() < 0.5) {
				return ServerCreature.BLUE_TEAM;
			}
			return ServerCreature.RED_TEAM;
		} else if (noOfBlue > noOfRed) {
			return ServerCreature.RED_TEAM;
		}
		return ServerCreature.BLUE_TEAM;
	}

	// ///////////////////////
	// GETTERS AND SETTERS //
	// ///////////////////////
	public int getCurrentFPS() {
		return currentFPS;
	}

	public void setCurrentFPS(int currentFPS) {
		this.currentFPS = currentFPS;
	}

	public int nextTeam() {
		return ++lastTeam;
	}

	public ServerWorld getWorld() {
		return world;
	}

	public ArrayList<ServerPlayer> getListOfPlayers() {
		return listOfPlayers;
	}

	public void setListOfPlayers(ArrayList<ServerPlayer> newListOfPlayers) {
		listOfPlayers = newListOfPlayers;
	}

	public ArrayList<SavedPlayer> getSavedPlayers()
	{
		return savedPlayers;
	}
}
