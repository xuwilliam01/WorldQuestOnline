package Server;

import java.util.ArrayList;

/**
 * Runs the actual game
 * @author William Xu & Alex Raita
 *
 */
public class Engine implements Runnable
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

	/**
	 * Constructor for the engine
	 */
	public Engine()
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
		while (true)
		{
			// Move all the objects around
			moveObjects();
			
			// Update all the clients with the new player data
			updatePlayers();

			try
			{
				Thread.sleep(UPDATE_RATE);
			}
			catch (InterruptedException e)
			{
				System.out.println("Error with the update timer");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Send messages to all the clients updating their player's data
	 */
	public void updatePlayers()
	{
		for (int playerNo = 0; playerNo < listOfPlayers.size(); playerNo++)
		{
			listOfPlayers.get(playerNo).update();
		}
	}

	/**
	 * Move objects around by updating their x and y coordinates
	 */
	public void moveObjects()
	{
		// Move players around (will be changed once scrolling is implemented)
		for (int playerNo = 0; playerNo < listOfPlayers.size(); playerNo++)
		{
			ServerPlayer player = listOfPlayers.get(playerNo);
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
}
