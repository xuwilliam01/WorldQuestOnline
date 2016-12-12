package Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import CentralServer.ServerInfo;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ClientServerSelection extends JFrame implements Runnable, WindowListener, ActionListener{

	DatagramSocket socket;
	DatagramPacket receive;
	DatagramPacket send;

	byte[] receiveData;
	byte[] sendData;

	private ArrayList<ServerInfo> servers = new ArrayList<ServerInfo>();
	private JButton refresh = new JButton("Refresh");
	private JTable table;
	private JScrollPane scrollTable;
	private final static String[] columns = {"Name", "Capacity", "Ping"};
	private Object[][] serversData = new Object[0][3];
	public static boolean open = false;
	

	public ClientServerSelection(int port) throws SocketException
	{
		setBackground(Color.BLACK);
		setSize((Client.SCREEN_WIDTH
				+ ClientInventory.INVENTORY_WIDTH)/2, Client.SCREEN_HEIGHT/2);
		setResizable(false);
		setTitle("Server Selection");
		setLocationRelativeTo(null);
		setLayout(null);
		setUndecorated(false);
		setVisible(true);
		revalidate();
		addWindowListener(this);
		open = true;
		
		table = new JTable(serversData, columns);
		scrollTable = new JScrollPane(table);
		scrollTable.setSize(400,serversData.length*50);
		scrollTable.setLocation(10,50);
		add(scrollTable);
				
		refresh.addActionListener(this);
		refresh.setSize(100,50);
		refresh.setLocation(200,200);
		add(refresh);

		socket = new DatagramSocket(port);
		receiveData = new byte[1024];
		sendData = new byte[1024];
		refresh();
	}

	public void refresh()
	{
		sendData = "G".getBytes();
		try {
			send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(CentralServer.CentralServer.IP), CentralServer.CentralServer.PORT);	
			socket.send(send);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		while(true)
		{
			receive = new DatagramPacket(receiveData, receiveData.length);
			try {
				socket.receive(receive);
			} catch (IOException e) {
				return;
			}
			//Get input
			System.out.println("Received Servers");
			String input = new String(receive.getData()).trim();
			String[] tokens = input.split(" ");
			servers.clear();
			serversData = new Object[tokens.length/4][3];
			for(int i = 0; i < tokens.length;i+=4)
			{
				serversData[i/4][0] = tokens[i];
				serversData[i/4][1] = Integer.parseInt(tokens[i+3]);
				serversData[i/4][2] = 0;
				servers.add(new ServerInfo(tokens[i], tokens[i+1], Integer.parseInt(tokens[i+2]), Integer.parseInt(tokens[i+3])));
			}
			remove(table);
			revalidate();
			table = new JTable(serversData, columns);
			scrollTable = new JScrollPane(table);
			scrollTable.setSize(400,serversData.length*50);
			scrollTable.setLocation(10,50);
			add(scrollTable);
			revalidate();
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		socket.close();
		socket = null;
		open = false;
		dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent button) {
		if(button.getSource() == refresh)
			refresh();

	}

}
