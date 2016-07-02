package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Server.Creatures.ServerCreature;

public class ServerLobbyPlayer implements Runnable {
	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;
	private boolean isLeader = false;
	private Server server;

	public static int nextTeam = 0;
	private int team = 0;
	private String IP = "";
	private String name = "Player";
	private boolean started = false;

	public static int numRed = 0;
	public static int numBlue = 0;
	
	private String allMaps ="";

	public ServerLobbyPlayer(Socket socket, Server server)
	{
		this.socket = socket;
		this.IP = socket.getInetAddress().toString();
		this.server = server;
		this.team = nextTeam%2+1;
		nextTeam++;

		if(team == ServerCreature.RED_TEAM)
		{
			numRed++;
		}
		else
			numBlue++;

		// Set up the output
		try
		{
			output = new PrintWriter(this.socket.getOutputStream());
			sendMessage("M "+server.getMap());
			
			allMaps = "";
			BufferedReader inputMap = new BufferedReader(new FileReader(new File("Resources","Maps.txt")));
			int numMaps = Integer.parseInt(inputMap.readLine());
			for(int i = 0; i < numMaps;i++)
				allMaps+=inputMap.readLine()+" ";
			allMaps = allMaps.trim();
			inputMap.close();
		}
		catch (IOException e)
		{
			System.out.println("Error getting client's output stream");
			e.printStackTrace();
		}

		// Set up the input
		try
		{
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		}
		catch (IOException e)
		{
			System.out.println("Error getting client's input stream");
			e.printStackTrace();
		}

	}

	/**
	 * Input thread
	 */
	public void run() {
		while (true)
		{
			try
			{
				// Read the next line the player sent in
				String command = input.readLine();

				if(command.length() >= 3 && command.charAt(0) == 'C')
				{
					String message = command.substring(2);
					String[] tokens = message.split(" ");

					server.broadcast("CH " + "E "+(getTeam() + getName()).split(" ").length
							+ " " + getTeam() + getName() + " "
							+ tokens.length + " " + message);
				}
				else if(command.equals("S") && isLeader)
				{
					if(Math.abs(numBlue - numRed) < 2)
					{
						started = true;
						server.start();
					}
					else
					{
						server.broadcast("CH E 1 " + ServerCreature.NEUTRAL
						+ "Server " + 5 + " "
						+ "Balance the teams to start");
					}
				}
				else if(command.length() > 2 && command.substring(0,2).equals("Na"))
				{
					try
					{
						name = command.substring(3);
					}
					catch(Exception E)
					{
						continue;
					}
					server.broadcast("JO " + getName().split(" ").length + " "
							+ getTeam() + getName());

					for(ServerLobbyPlayer player : server.getPlayers())
					{
						//Send every player to this one and send all other players that this player just joined
						sendMessage("P true "+player.getTeam()+" "+player.getName());
						if(player != this)
							player.sendMessage("P true "+team+" "+name);

						if(player.isLeader())
							server.broadcast("LE "+player.getTeam()+" "+player.getName());
					}
				}
				else if(command.equals("X"))
				{
					if(team == ServerCreature.RED_TEAM)
					{
						team = ServerCreature.BLUE_TEAM;
						numRed--;
						numBlue++;
						server.broadcast("P false "+ team+" "+name);
						if(isLeader)
							setLeader();

					}
					else 
					{
						team = ServerCreature.RED_TEAM;
						numRed++;
						numBlue--;
						server.broadcast("P false "+ team+" "+name);
						if(isLeader)
							setLeader();
					}
				}
				else if(command.length() > 2 && command.charAt(0) =='M' && isLeader)
				{
					String map = command.substring(2);
					if(allMaps.contains(map))
					{
						server.broadcast("M "+map);
						server.setMap(map);
						System.out.println(map);
					}
				}
			}
			catch (IOException e)
			{
				break;
			}
			catch (NullPointerException e)
			{
				break;
			}

		}

		System.out.println("A client has disconnected");
		try
		{
			input.close();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			System.out.println("Error closing buffered reader");
		}

		if(!started)
			server.remove(this);

	}

	public void setLeader()
	{
		isLeader = true;
		sendMessage("L");
		server.broadcast("LE "+team+" "+name);
	}

	public boolean isLeader()
	{
		return isLeader;
	}

	public int getTeam()
	{
		return team;
	}

	public String getName()
	{
		return name;
	}

	public void sendMessage(String message)
	{
		output.println(message);
		output.flush();
	}

	public String getIP()
	{
		return IP;
	}

}
