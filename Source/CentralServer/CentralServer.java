package CentralServer;

import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import javax.swing.Timer;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import ClientUDP.ServerInfo;

public class CentralServer implements Runnable, ActionListener{

	public final static int PORT = 5000;
	//public final static String IP = "138.197.138.125";
	public final static String IP = "127.0.0.1";

	private DatagramSocket socket;
	private DatagramPacket receive;
	private DatagramPacket send;

	byte[] receiveData;
	byte[] sendData;

	private ArrayList<ServerInfo> servers = new ArrayList<ServerInfo>();
	private String listServers;
	private String delayedListServers;
	boolean clearServers = false;
	
	private Element root;
	private Document document;
	private SAXBuilder builder;
	private String FILE_NAME = "Resources//Accounts.xml";
	
	//Clear servers after a period of time
	private Timer reset;

	public CentralServer() throws IOException, JDOMException
	{
		socket = new DatagramSocket(PORT);
		receiveData = new byte[1024];
		sendData = new byte[1024];
		listServers = "";
		delayedListServers = "";
		reset = new Timer(2000, this);
		
		//Build XML
		builder = new SAXBuilder();
		document = builder.build(new File(FILE_NAME));
		root = document.getRootElement();
		
//		System.out.println(login("Alex","uhoh"));
//		System.out.println(login("Alex","12313ibsdfibsbfhskjdvbfjhs1234234"));
//		System.out.println(createAccount("Alex3","EasyPass"));
//		System.out.println(createAccount("Alex3","EasyPass"));
//		System.out.println(login("Alex3","EasyPass"));
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
			String input = new String(receive.getData()).trim();
			
			try{
				switch(input.charAt(0))
				{
				//Get info from servers
				case 'A':
					String[] tokens = input.split(" ");
					ServerInfo newServer = new ServerInfo(tokens[1], receive.getAddress().toString(), receive.getPort(), 
							Integer.parseInt(tokens[2]));
					if(!servers.contains(newServer))
					{
						//System.out.println(newServer.getIP() + " " + newServer.getPort());
						servers.add(newServer);
						listServers += newServer.getName() + " " + newServer.getIP() + " " + newServer.getPort() + " " + newServer.getNumPlayers() + " ";
					}
					break;
				//Send clients the list of servers
				case 'G':
					String serversOut = "S "+receive.getAddress().toString()+" "+delayedListServers;
					serversOut.trim();
					sendData = serversOut.getBytes();
					send = new DatagramPacket(sendData, sendData.length, receive.getAddress(), receive.getPort());
					socket.send(send);
					break;
				case 'L':
					tokens = input.split(" ");
					String key = tokens[1];
					String name = tokens[2];
					for(int i = 3; i < tokens.length;i++)
						name+=" "+tokens[i];
					String out;
					if(login(name, key))
						out = "LY";
					else
						out = "LN";
					sendData = out.getBytes();
					send = new DatagramPacket(sendData, sendData.length, receive.getAddress(), receive.getPort());
					socket.send(send);
					break;
				case 'C':
					tokens = input.split(" ");
					key = tokens[1];
					name = tokens[2];
					for(int i = 3; i < tokens.length;i++)
						name+=" "+tokens[i];
					if(createAccount(name, key))
					{
						out = "CY";
					}
					else
						out = "CN";
					sendData = out.getBytes();
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
				delayedListServers = listServers;
				servers.clear();
				listServers = "";
				clearServers = false;
			}
		}

	}

	public boolean login(String user, String key)
	{
		for(Element username : root.getChildren())
		{
			if(username.getAttribute("name").getValue().equals(user))
			{
				return key.equals(username.getChild("Key").getValue());
			}
		}
		return false;
	}
	
	public boolean createAccount(String user, String key)
	{
		boolean contains = false;
		for(Element username : root.getChildren())
		{
			if(username.getAttribute("name").getValue().equals(user))
			{
				//If account already exists, but the username and password match, then pretend we made a new account
				if(key.equals(username.getChild("Key").getValue()))
					return true;
				
				contains = true;
				break;
			}
		}
		if(contains)
			return false;
		
		Element newUser = new Element("User");
		Attribute username = new Attribute("name", user);
		Element newKey = new Element("Key");
		newKey.setText(key);
		newUser.addContent(newKey);
		newUser.setAttribute(username);
		root.addContent(newUser);
		
		//Save with new username + key
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		try {
			xmlOutputter.output(document, new FileOutputStream(new File(FILE_NAME)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public void actionPerformed(ActionEvent arg0) {
		clearServers = true;
	}

}
