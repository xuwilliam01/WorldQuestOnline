package CentralServer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import javax.swing.Timer;

import ClientUDP.ServerInfo;

public class CentralServer implements Runnable, ActionListener{

	public final static int PORT = 5000;
	public final static String IP = "127.0.0.1";

	DatagramSocket socket;
	DatagramPacket receive;
	DatagramPacket send;

	byte[] receiveData;
	byte[] sendData;

	private ArrayList<ServerInfo> servers = new ArrayList<ServerInfo>();
	private String listServers;
	boolean clearServers = false;
	
	//Clear servers after a period of time
	Timer reset;

	public CentralServer() throws IOException
	{
		socket = new DatagramSocket(PORT);
		receiveData = new byte[1024];
		sendData = new byte[1024];
		listServers = "S";
		reset = new Timer(2000, this);
	}
	public void run() {
		reset.start();
		while(true)
		{
			receiveData = new byte[1024];
			receive = new DatagramPacket(receiveData, receiveData.length);
			try {
				socket.receive(receive);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Get input
			String input = new String(receive.getData());
			
			try{
				switch(input.charAt(0))
				{
				//Get info from servers
				case 'A':
					String[] tokens = input.trim().split(" ");
					String orig = receive.getAddress().toString();
					ServerInfo newServer = new ServerInfo(tokens[1],orig, receive.getPort(), 
							Integer.parseInt(tokens[2]));
					if(!servers.contains(newServer))
					{
						//System.out.println(newServer.getIP() + " " + newServer.getPort());
						servers.add(newServer);
						listServers += newServer.getName() + " " + newServer.getIP() + " " + newServer.getPort() + " " + newServer.getNumPlayers() + " " + orig + " ";
					}
					break;
				//Send clients the list of servers
				case 'G':
					//System.out.println(listServers);
					listServers.trim();
					sendData = listServers.getBytes();
					send = new DatagramPacket(sendData, sendData.length, receive.getAddress(), receive.getPort());
					socket.send(send);
					break;	
				}
			}
			catch(Exception e)
			{
				System.out.println("Bad Input");
				e.printStackTrace();
			}
			
			// Remove servers when necessary
			if(clearServers)
			{
				//System.out.println("Clearing Servers");
				servers.clear();
				listServers = "S";
				clearServers = false;
			}
		}

	}

	public void actionPerformed(ActionEvent arg0) {
		clearServers = true;
	}

}
