package Server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Timer;

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
	 * The world the engine works with
	 */
	private World world;

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
		listOfPlayers = new ArrayList<ServerPlayer>();
		world = new World();
	}

	@Override
	/**
	 * Constantly update the game
	 */
	public void run()
	{
		updateTimer = new Timer(UPDATE_RATE,this);
		updateTimer.start();
	}

	/**
	 * Send messages to all the clients updating their player's data
	 */
	public synchronized void updatePlayers()
	{
		for(ServerPlayer player : listOfPlayers)
			player.update();
	}

	/**
	 * Move objects around by updating their x and y coordinates
	 */
	public void moveObjects()
	{
		// Move players around (will be changed once scrolling is implemented)
		for (ServerPlayer player : listOfPlayers)
		{
			player.setX(player.getX()+player.getHSpeed());
			player.setY(player.getY()+player.getVSpeed());
		}
	}

	/**
	 * Add a new player to the game
	 * @param newPlayer the new player
	 */
	public void addPlayer(ServerPlayer newPlayer)
	{
		listOfPlayers.add(newPlayer);
	}


	public World getWorld() {
		return world;
	}

	
	public ArrayList<ServerPlayer> getListOfPlayers() {
		return listOfPlayers;
	}

	public void setListOfPlayers(ArrayList<ServerPlayer> listOfPlayers) {
		this.listOfPlayers = listOfPlayers;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ArrayList <ServerPlayer> listOfRemovedPlayers = new ArrayList<ServerPlayer>();
		
		// Remove disconnected players
		for(ServerPlayer player : listOfPlayers)
		{
			if (player.isDisconnected())
			{
				listOfRemovedPlayers.add(player);
			}
		}
		
		for(ServerPlayer player : listOfRemovedPlayers)
		{
			listOfPlayers.remove(player);
		}
		
		
		// Move all the objects around
		moveObjects();

		// Update all the clients with the new player data
		updatePlayers();
		
	}
}
