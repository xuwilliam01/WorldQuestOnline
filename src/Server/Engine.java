package Server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Timer;

import Imports.Images;

/**
 * Runs the actual game
 * @author William Xu & Alex Raita
 *
 */
public class Engine implements Runnable, ActionListener
{

	/**
	 * A list of all the players in the server
	 */
	private ArrayList<ServerPlayer> listOfPlayers;

	
	/**
	 * A list of IDs currently used in the game (index is the ID, true means
	 * used, false means unused). Note that IDs can be freed when the object is
	 * deleted and re-assigned to another object
	 */
	private boolean[] objectIDs;

	/**
	 * The number of possible ID's for any objects. The number of objects
	 * existing the game (aside from tiles) cannot exceed this limit
	 */
	public final int NUMBER_OF_IDS = 100000;

	/**
	 * The world the engine works with
	 */
	private ServerWorld world;

	/**
	 * The rate at which the game runs at, in milliseconds
	 */
	public final int UPDATE_RATE = 15;

	private Timer updateTimer;

	/**
	 * Constructor for the engine
	 */
	public Engine() throws IOException
	{
		// Start importing the images from the file (place in a loading screen
		// or something later)
		Images.importImages();

		listOfPlayers = new ArrayList<ServerPlayer>();
		world = new ServerWorld();
		objectIDs = new boolean[NUMBER_OF_IDS];

	}

	@Override
	/**
	 * Constantly update the game
	 */
	public void run()
	{
		updateTimer = new Timer(UPDATE_RATE, this);
		updateTimer.start();
	}

	/**
	 * Send messages to all the clients updating their player's data
	 */
	public synchronized void updatePlayers()
	{
		for (ServerPlayer player : listOfPlayers)
		{
			player.update();
		}
	}

	/**
	 * Send an instant message to all clients
	 */
	public void broadcast(String message)
	{
		for (ServerPlayer player : listOfPlayers)
		{
			player.sendMessage(message);
		}
	}

	/**
	 * Remove a player from the array list
	 * @param remove
	 */
	public synchronized void removePlayer(ServerPlayer remove)
	{
		listOfPlayers.remove(remove);
		world.remove(remove);
		broadcast("R " + remove.getID());
	}

	/**
	 * Add a new player to the game
	 * @param newPlayer the new player
	 */
	public void addPlayer(ServerPlayer newPlayer)
	{
		listOfPlayers.add(newPlayer);
		world.add(newPlayer);
	}

	public ServerWorld getWorld()
	{
		return world;
	}

	public ArrayList<ServerPlayer> getListOfPlayers()
	{
		return listOfPlayers;
	}

	public void setListOfPlayers(ArrayList<ServerPlayer> listOfPlayers)
	{
		this.listOfPlayers = listOfPlayers;
	}

	/**
	 * Use and reserve the next available ID in the list of booleans
	 * @return the id
	 */
	public int useNextID()
	{
		for (int id = 0; id < objectIDs.length; id++)
		{
			if (!objectIDs[id])
			{
				objectIDs[id] = true;
				return id;
			}
		}
		return -1;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		ArrayList<ServerPlayer> listOfRemovedPlayers = new ArrayList<ServerPlayer>();

		// Remove disconnected players
		for (ServerPlayer player : listOfPlayers)
		{
			if (player.isDisconnected())
			{
				listOfRemovedPlayers.add(player);
			}
		}

		for (ServerPlayer player : listOfRemovedPlayers)
		{
			listOfPlayers.remove(player);
		}

		// Move all the objects around
		world.moveObjects();

		// Update all the clients with the new player data
		updatePlayers();
	}
}
