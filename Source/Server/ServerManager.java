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

	private final Timer inputTimer = new Timer(400,this);
	private boolean timeout = false;
	private boolean validCredentials = false;

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
		addNewRoom();
		updateCentral.start();
		centralSocket = new DatagramSocket(port);
		sendData = new byte[1024];
		PingReceiver ping = new PingReceiver();
		Thread pingThread = new Thread(ping);
		pingThread.start();

		try {
			this.socket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Server cannot be created with given port");
			e.printStackTrace();
		}
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
		} catch (IOException e) {
			System.out.println("Server cannot be created with given port");
			e.printStackTrace();
		}

		Maps.importMaps();
	}

	@Override
	public void run() {
		outerloop: while (true) {
			try {
				Socket newClient = socket.accept();
				System.out.println("New player JOINED");
				PrintWriter output = new PrintWriter(
						newClient.getOutputStream());
				BufferedReader input = new BufferedReader(
						new InputStreamReader(newClient.getInputStream()));
				timeout = false;
				inputTimer.restart();
				while(!input.ready())
				{
					//System.out.println("Stuck on input.ready");
					if(timeout)
					{
						output.println("ERROR");
						output.flush();
						input.close();
						output.close();
						newClient.close();
						continue outerloop;
					}
				}
				String command = input.readLine();
				String[] tokens = command.split(" ");
				try{
					if(tokens[0].equals("Na"))
					{
						String key = tokens[1];
						String name = tokens[2];
						for(int i = 3; i < tokens.length;i++)
						{
							name += " "+tokens[i];
						}

						//Check to make sure the player isn't already logged in
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
										newClient.close();
										continue outerloop;
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
										newClient.close();
										continue outerloop;
									}
								}
							}
						}

						//Check with the central server to validate the account info
						validCredentials = false;
						String data = "L "+ key+" "+name;
						sendData = data.getBytes();
						try {
							send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(CentralServer.CentralServer.IP), CentralServer.CentralServer.PORT);
							centralSocket.send(send);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
					newClient.close();
					continue outerloop;
				}
				for (Server room : rooms) {
					if (!room.isFull()) {
						if (room.started()) {
							output.println("CONNECTED");
							System.out.println("CONNECTED GAME STARTED");
							output.flush();
							room.addClient(newClient, input);
						} else {
							output.println("CONNECTED");
							System.out.println("CONNECTED TO LOBBY");
							output.flush();
							room.addClient(newClient, input);
						}
						continue outerloop; // Once the client is added wait for
						// a new one
					}
				}

				if (rooms.size() < maxRooms) {
					output.println("CONNECTED");
					System.out.println("CONNECTED NEW ROOM");
					output.flush();
					addNewRoom();
					rooms.get(rooms.size() - 1).addClient(newClient, input);
				} else // No More Space
				{
					System.out.println("Sent full message to client");
					output.println("FULL");
					output.flush();
					output.close();
					newClient.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void addNewRoom() {
		Server newServer = new Server();
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

	public static void removeRoom(Server remove)
	{
		rooms.remove(remove);
	}

	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == inputTimer)
		{
			timeout = true;
			((Timer)arg0.getSource()).stop();
		}
		else
		{
			int numPlayers = 0;
			if(rooms.size() != 0)
			{
				numPlayers = rooms.get(0).noOfPlayers;
				if(!rooms.get(0).started())
					numPlayers = rooms.get(0).getPlayers().size();
			}
			String data = "A "+ name + " " + numPlayers;
			sendData = data.getBytes();
			try {
				send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(CentralServer.CentralServer.IP), CentralServer.CentralServer.PORT);
				centralSocket.send(send);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
					case 'L':
						if(input.length() == 2)
						{
							if(input.charAt(1) == 'Y')
							{
								validCredentials = true;
							}
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
