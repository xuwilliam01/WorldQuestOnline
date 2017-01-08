package Server;
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.Timer;

import Client.ClientFrame;
import Imports.Maps;
import Server.Creatures.ServerPlayer;

public class ServerManager implements Runnable, ActionListener{

	private ServerSocket socket;
	private static ArrayList<Server> rooms = new ArrayList<Server>();
	private int maxRooms;
	private ClientFrame mainFrame;
	public static boolean HAS_FRAME = true;
	private Timer updateCentral = new Timer(500,this);
	private String name = "Default";

	//Variables for central server comm
	private DatagramSocket centralSocket;
	private DatagramPacket send;
	private DatagramPacket receive;
	private byte[] sendData;
	private byte[] receiveData;

	private Socket centralServer;
	private BufferedReader in;
	private PrintWriter out;

	private int thisPort;
	
	private ArrayList<AddNewPlayer> listOfNewPlayers;
	/**
	 * 
	 * @param port
	 * @param maxRooms
	 * @param mainFrame
	 * @throws SocketException 
	 */
	public ServerManager(String name, int port, int maxRooms, ClientFrame mainFrame) throws SocketException {
		this.name = name;
		this.maxRooms = maxRooms;
		this.mainFrame = mainFrame;	
		thisPort = port;
		addNewRoom();
		centralSocket = new DatagramSocket(port);
		sendData = new byte[1024];
		PingReceiver ping = new PingReceiver();
		Thread pingThread = new Thread(ping);
		pingThread.start();

		try {
			this.socket = new ServerSocket(port);
			centralServer = new Socket(CentralServer.CentralServer.IP, CentralServer.CentralServer.PORT);
			out = new PrintWriter(centralServer.getOutputStream());
			in = new BufferedReader(new InputStreamReader(centralServer.getInputStream()));
		} catch (IOException e) {
			System.out.println("Server cannot be created with given port");
			e.printStackTrace();
		}

		updateCentral.start();
		Thread centralServerThread = new Thread(new CentralServerReceive());
		centralServerThread.start();
	}

	/**
	 * 
	 * @param port
	 * @param maxRooms
	 * @param mainFrame
	 * @throws SocketException 
	 */
	public ServerManager(String name, int port, int maxRooms) throws SocketException {
		this.name = name;
		this.maxRooms = maxRooms;
		thisPort = port;
		HAS_FRAME = false;
		addNewRoom();
		updateCentral.start();
		centralSocket = new DatagramSocket(port);
		sendData = new byte[1024];
		PingReceiver ping = new PingReceiver();
		Thread pingThread = new Thread(ping);
		pingThread.start();
		try {
			this.socket = new ServerSocket(port);
			centralServer = new Socket(CentralServer.CentralServer.IP, CentralServer.CentralServer.PORT);
			out = new PrintWriter(centralServer.getOutputStream());
			in = new BufferedReader(new InputStreamReader(centralServer.getInputStream()));
		} catch (IOException e) {
			System.out.println("Server cannot be created with given port");
			e.printStackTrace();
		}

		Thread centralServerThread = new Thread(new CentralServerReceive());
		centralServerThread.start();
		Maps.importMaps();
	}

	@Override
	public void run() {
		listOfNewPlayers = new ArrayList<AddNewPlayer>();
		while (true) {
			try {
				Socket newClient = socket.accept();
				System.out.println("New player JOINED");
				AddNewPlayer toAdd = new AddNewPlayer(newClient);
				synchronized(listOfNewPlayers)
				{
					listOfNewPlayers.add(toAdd);
				}
				Thread newPlayerAccept = new Thread(toAdd);
				newPlayerAccept.start();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private class CentralServerReceive implements Runnable
	{

		@Override
		public void run() {
			while(true)
			{
				try {
					String input = in.readLine();
					switch(input.charAt(0))
					{
					case 'L':
						String[] tokens = input.split(" ");
						String key = tokens[1];
						String name = tokens[2];
						for(int i = 3; i < tokens.length;i++)
						{
							name += " "+tokens[i];
						}
						AddNewPlayer toRemove = null;
						synchronized(listOfNewPlayers){
							for(AddNewPlayer player : listOfNewPlayers)
							{
								if(player.key != null && player.name != null && player.key.equals(key) && player.name.equals(name))
								{
									player.validCredentials = true;
									toRemove = player;
								}
							}
							listOfNewPlayers.remove(toRemove);
						}
						break;
					}
				}
				catch (IOException e) {
					System.out.println("Central Server Closed");
					e.printStackTrace();
					return;
				}
			}
		}

	}

	private class AddNewPlayer implements Runnable, ActionListener
	{
		Socket newClientSocket;
		boolean timeout = false;
		boolean validCredentials = false;
		String key = null;
		String name = null;
		public AddNewPlayer(Socket socket)
		{
			this.newClientSocket = socket;
		}

		@SuppressWarnings("resource")
		@Override
		public void run() {
			try{
				PrintWriter output = new PrintWriter(
						newClientSocket.getOutputStream());
				BufferedReader input = new BufferedReader(
						new InputStreamReader(newClientSocket.getInputStream()));
				Timer inputTimer = new Timer(400,this);
				inputTimer.start();
				while(!input.ready())
				{
					//System.out.println("Stuck on input.ready");
					if(timeout)
					{
						output.println("ERROR");
						output.flush();
						input.close();
						output.close();
						newClientSocket.close();
						return;
					}
				}
				String command = input.readLine();
				String[] tokens = command.split(" ");
				try{
					if(tokens[0].equals("Na"))
					{
						key = tokens[1];
						name = tokens[2];
						for(int i = 3; i < tokens.length;i++)
						{
							name += " "+tokens[i];
						}

						//Check to make sure the player isn't already logged in
						synchronized(rooms){
							for(Server room : rooms)
							{
								if(!room.started())
								{
									for(ServerLobbyPlayer player : room.getPlayers())
									{
										if(player.getName().equals(name))
										{
											output.println("DOUBLEACC");
											output.flush();
											input.close();
											output.close();
											newClientSocket.close();
											return;
										}
									}
								}
								else
								{
									for(ServerPlayer player : room.getEngine().getListOfPlayers())
									{
										if(player.getName().equals(name))
										{
											output.println("DOUBLEACC");
											output.flush();
											input.close();
											output.close();
											newClientSocket.close();
											return;
										}
									}
								}
							}
						}
						//Check with the central server to validate the account info
						validCredentials = false;
						String data = "l "+ key+" "+name;
						send(data);
						Thread.sleep(200);
						if(!validCredentials)
						{
							throw new Exception();
						}
						validCredentials = false;

					}
					else throw new Exception();	
				}
				catch(Exception e)
				{
					output.println("INVALID");
					output.flush();
					input.close();
					output.close();
					newClientSocket.close();
					return;
				}

				synchronized(rooms){
					for (Server room : rooms) {
						if (!room.isFull()) {
							if (room.started()) {
								output.println("CONNECTED");
								System.out.println("CONNECTED GAME STARTED");
								output.flush();
								room.addClient(newClientSocket, input, name);
							} else {
								output.println("CONNECTED");
								System.out.println("CONNECTED TO LOBBY");
								output.flush();
								room.addClient(newClientSocket, input, name);
							}
							return; // Once the client is added wait for
							// a new one
						}
					}

					if (rooms.size() < maxRooms) {
						output.println("CONNECTED");
						System.out.println("CONNECTED NEW ROOM");
						output.flush();
						addNewRoom();
						rooms.get(rooms.size() - 1).addClient(newClientSocket, input, name);
					} else // No More Space
					{
						System.out.println("Sent full message to client");
						output.println("FULL");
						output.flush();
						output.close();
						newClientSocket.close();
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			((Timer)arg0.getSource()).stop();
			timeout = true;

		}

	}

	public void send(String s)
	{
		out.println(s);
		out.flush();
	}

	public void addNewRoom() {
		Server newServer = new Server(this);
		rooms.add(newServer);
		Thread serverThread = new Thread(newServer);
		serverThread.start();

		if (HAS_FRAME)
		{
			ServerGUI gui = new ServerGUI(newServer);
			mainFrame.dispose();
			ServerFrame myFrame = new ServerFrame();
			gui.setLocation(0, 0);
			myFrame.add(gui);
			gui.revalidate();
			newServer.setGUI(gui);
		}
	}
	
	public void removeRoom(Server remove)
	{
		rooms.remove(remove);
	}

	public void actionPerformed(ActionEvent arg0) {
		synchronized(rooms)
		{
			int numPlayers = 0;
			if(rooms.size() != 0)
			{
				numPlayers = rooms.get(0).noOfPlayers;
				if(!rooms.get(0).started())
					numPlayers = rooms.get(0).getPlayers().size();
			}
			String data = "A "+ name + " " + numPlayers+" "+thisPort;
			send(data);
		}

	}

	private class PingReceiver implements Runnable
	{
		@Override
		public void run() {
			while(true)
			{
				receiveData = new byte[1024];
				receive = new DatagramPacket(receiveData, receiveData.length);
				try {
					centralSocket.receive(receive);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String input = new String(receive.getData()).trim();
				if(input.length() > 0)
				{
					switch(input.charAt(0))
					{
					case 'P':
						sendData = "P".getBytes();
						send = new DatagramPacket(sendData, sendData.length, receive.getAddress(), receive.getPort());
						try {
							centralSocket.send(send);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case 'C':
						sendData = "C".getBytes();
						send = new DatagramPacket(sendData, sendData.length, receive.getAddress(), receive.getPort());
						try {
							centralSocket.send(send);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}
				}

			}

		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

}
