package ClientUDP;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import Client.Client;
import Client.ClientInventory;
import Menu.MainMenu;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class ClientServerSelection extends JFrame implements Runnable, WindowListener, ActionListener{

	DatagramSocket socket;
	DatagramPacket receive;
	DatagramPacket send;

	byte[] receiveData;
	byte[] sendData;

	private ArrayList<ServerInfo> servers = new ArrayList<ServerInfo>();
	private JButton refresh = new JButton("Refresh");
	private JButton connect = new JButton("Connect");
	private JTable table;
	private JScrollPane scrollTable;
	private int NUM_ROWS = 24;
	private final static String[] columns = {"Name", "Capacity", "Ping"};
	/**
	 * Name, capacity, ping
	 */
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

		initTable();

		refresh.addActionListener(this);
		refresh.setSize(100,50);
		refresh.setLocation((Client.SCREEN_WIDTH
				+ ClientInventory.INVENTORY_WIDTH)/2-200,75);
		add(refresh);

		connect.addActionListener(this);
		connect.setSize(100,50);
		connect.setLocation((Client.SCREEN_WIDTH
				+ ClientInventory.INVENTORY_WIDTH)/2-200,150);
		add(connect);

		socket = new DatagramSocket(port);
		receiveData = new byte[1024];
		sendData = new byte[1024];
		refresh();
		repaint();
	}

	public void initTable()
	{
		table = new JTable(serversData, columns)
		{
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {                
				return false; 
			}
		};
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		table.addMouseListener(new TableListener(table));
		scrollTable = new JScrollPane(table);
		scrollTable.setSize(3*(Client.SCREEN_WIDTH
				+ ClientInventory.INVENTORY_WIDTH)/8,(table.getRowHeight()+1)*NUM_ROWS - 1);
		scrollTable.setLocation(10,50);
		add(scrollTable);
	}

	public void refresh()
	{
		send("G");
	}

	public void send(String out)
	{
		sendData = out.getBytes();
		try {
			send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(CentralServer.CentralServer.IP), CentralServer.CentralServer.PORT);	
			socket.send(send);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void send(String out, String destIP, int destPort)
	{
		sendData = out.getBytes();
		String IP = destIP;
		if(!Character.isDigit(IP.charAt(0)))
				IP = IP.substring(1);
		try {
			send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(IP), destPort);	
			socket.send(send);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void connect()
	{
		int row = table.getSelectedRow();
		if(row > -1 && row < servers.size())
		{
			ServerInfo destination = servers.get(row);
			if(destination.getNumPlayers() >= 10)
				return;
			open = false;
			String IP = destination.getIP();
			//Checks if server IP is the same as your external IP
			//Must use 127.0.0.1 in this case
			if(IP.equals(destination.getOrigIP()))
				IP = "127.0.0.1";
			if(!Character.isDigit(IP.charAt(0)))
				IP = IP.substring(1);
			MainMenu.joinLobby(IP, destination.getPort());
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	public void run() {
		ArrayList<Ping> pings = new ArrayList<Ping>();
		while(true)
		{
			receiveData = new byte[1024];
			receive = new DatagramPacket(receiveData, receiveData.length);
			try {
				socket.receive(receive);
			} catch (IOException e) {
				return;
			}
			//Get input
			String input = new String(receive.getData()).trim();
			switch(input.charAt(0))
			{
			case 'S':
				input = input.substring(1);
				String[] tokens = input.split(" ");
				servers.clear();
				pings.clear();
				int numInputs = 5;
				serversData = new Object[tokens.length/numInputs][3];
				//System.out.println("Received Servers\n"+input);
				if(input.length() > 0)
					for(int i = 0; i < tokens.length;i+=numInputs)
					{
						serversData[i/numInputs][0] = tokens[i];
						serversData[i/numInputs][1] = tokens[i+3] +"/10";
						serversData[i/numInputs][2] = "200+";
						
						int port = Integer.parseInt(tokens[i+2]);
						pings.add(new Ping(tokens[i+1],port, System.currentTimeMillis()));
						send("P", tokens[i+1], port);
						
						servers.add(new ServerInfo(tokens[i], tokens[i+1], port, Integer.parseInt(tokens[i+3]), tokens[i+4]));
					}			
				remove(table);
				remove(scrollTable);
				revalidate();
				initTable();
				revalidate();
				repaint();
				break;
			case 'P':
				long end = System.currentTimeMillis();
				String IP = receive.getAddress().toString();
				int port = receive.getPort();
				int index = 0;
				for(Ping ping : pings)
				{
					if(ping.getPort() == port && ping.getIP().equals(IP))
					{
						serversData[index][2] = end - ping.getStart();
						table.setValueAt(end - ping.getStart(), index, 2);
						break;
					}
					index++;
				}
				break;
			}
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
		else if(button.getSource() == connect)
			connect();

	}

	private class TableListener implements MouseListener {

		private JTable table;

		public TableListener(JTable table)
		{
			this.table = table;
		}


		@Override
		public void mouseClicked(MouseEvent arg0) {
			int row = table.getSelectedRow();
			table.setRowSelectionAllowed(false);
			table.setRowSelectionAllowed(true);
			if ((row > -1)) {
				table.setRowSelectionInterval(row, row);
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			int row = table.getSelectedRow();
			table.setRowSelectionAllowed(false);
			table.setRowSelectionAllowed(true);
			if ((row > -1)) {
				table.setRowSelectionInterval(row, row);
			}	
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

	}


}

