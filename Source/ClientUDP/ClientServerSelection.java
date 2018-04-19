package ClientUDP;

import java.awt.Color;
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
import Menu.MainMenu;
import Server.Server;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class ClientServerSelection extends JFrame implements Runnable, WindowListener, ActionListener{

	private DatagramSocket socket;
	private DatagramPacket receive;
	private DatagramPacket send;

	private byte[] receiveData;
	private byte[] sendData;

	private ArrayList<ServerInfo> servers = new ArrayList<ServerInfo>();
	private JButton refresh = new JButton("Refresh");
	private JButton connect = new JButton("Connect");
	private JButton manualConnect = new JButton("Manual Connect");
	private Timer timer;
	
	
	private JTable table;
	private JScrollPane scrollTable;
	private int NUM_ROWS = 12;
	private final static String[] columns = {"Name", "Status", "Ping"};
	
	// For checking if a server exists, using system elapsed time
	private long signalBegin = Long.MAX_VALUE / 2;
	private final long SIGNAL_TIME_LIMIT = 500;
	
	public static ServerInfo savedDestination;
	
	/**
	 * Name, capacity, ping
	 */
	private Object[][] serversData = new Object[0][3];
	public static boolean open = false;

	public ClientServerSelection(int port, boolean joinSavedLobby) throws SocketException
	{
		//this.getContentPane().setBackground(Color.BLACK);
		
		int height = 300;
		
		if (MainMenu.tooLarge)
		{
			height = 335;
		}
		
		setSize(515, height);
		setResizable(false);
		setTitle("Server Selection");
		setLocationRelativeTo(null);
		setLayout(null);
		setUndecorated(false);
		
		if (!joinSavedLobby)
		{
			setVisible(true);
		}
		else
		{
			setVisible(false);
		}
		revalidate();
		addWindowListener(this);
		requestFocus();
		open = true;

		initTable();

		refresh.addActionListener(this);
		refresh.setSize(100,50);
		refresh.setBackground(Color.white);
		refresh.setForeground(Color.black);
		refresh.setLocation(scrollTable.getX() + scrollTable.getWidth() + 10, scrollTable.getY());
		add(refresh);

		connect.addActionListener(this);
		connect.setSize(100,50);
		connect.setBackground(Color.white);
		connect.setForeground(Color.black);
		connect.setLocation(scrollTable.getX() + scrollTable.getWidth() + 10, scrollTable.getY() + 60);
		add(connect);
		
		manualConnect.addActionListener(this);
		manualConnect.setSize(150,50);
		manualConnect.setBackground(Color.white);
		manualConnect.setForeground(Color.black);
		manualConnect.setLocation(scrollTable.getX() + scrollTable.getWidth() + 10 , 
				scrollTable.getY() + scrollTable.getHeight() - manualConnect.getHeight());
		add(manualConnect);

		socket = new DatagramSocket(port);
		receiveData = new byte[1024];
		sendData = new byte[1024];
		
		refresh.doClick();
		repaint();

		timer = new Timer(200, this);
		timer.start();
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
		scrollTable.setSize(300,(table.getRowHeight()+1)*NUM_ROWS - 1);
		scrollTable.setLocation(30,30);
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
			send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ClientUDP.ClientAccountWindow.Domain), ClientAccountWindow.PORT);	
			socket.send(send);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
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
	
	public void connectToSaved()
	{
		if (savedDestination != null)
		{
			ServerInfo destination = savedDestination;
			savedDestination = null;
			
			int maxPlayers = Server.MAX_PLAYERS;
			if (destination.getName().contains("1v1"))
			{
				maxPlayers = 2;
			}
			else if (destination.getName().contains("2v2"))
			{
				maxPlayers = 2;
			}
			else if (destination.getName().contains("3v3"))
			{
				maxPlayers = 2;
			}
			else if (destination.getName().contains("4v4"))
			{
				maxPlayers = 2;
			}
			else if (destination.getName().contains("5v5"))
			{
				maxPlayers = 2;
			}
			
			if(destination.getNumPlayers() >= maxPlayers)
			{
				JOptionPane.showMessageDialog(null, "This server is full", "", JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			String IP = destination.getIP();
			//Checks if server IP is the same as your external IP
			//Must use 127.0.0.1 in this case
			send("C", IP, destination.getPort());
			this.setSignalBegin(System.currentTimeMillis());
			
		}
	}
	
	public void connect()
	{
		int row = table.getSelectedRow();
		if(row > -1 && row < servers.size())
		{
			ServerInfo destination = servers.get(row);
			int maxPlayers = Server.MAX_PLAYERS;
			if (destination.getName().contains("1v1"))
			{
				maxPlayers = 2;
			}
			else if (destination.getName().contains("2v2"))
			{
				maxPlayers = 2;
			}
			else if (destination.getName().contains("3v3"))
			{
				maxPlayers = 2;
			}
			else if (destination.getName().contains("4v4"))
			{
				maxPlayers = 2;
			}
			else if (destination.getName().contains("5v5"))
			{
				maxPlayers = 2;
			}
			
			if(destination.getNumPlayers() >= maxPlayers)
			{
				JOptionPane.showMessageDialog(null, "This server is full", "", JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			String IP = destination.getIP();
			//Checks if server IP is the same as your external IP
			//Must use 127.0.0.1 in this case
			send("C", IP, destination.getPort());
			this.setSignalBegin(System.currentTimeMillis());
			
			savedDestination = destination;
		}
	}
	
	public void manualConnect()
	{
		String IP = JOptionPane.showInputDialog(null, "Please enter the server's IP address", "Need IP", JOptionPane.PLAIN_MESSAGE);
		
		if (IP == null)
		{
			return;
		}
		else if (IP == "")
		{
			JOptionPane.showMessageDialog(null, "Could not connect to server", "Sorry!", JOptionPane.ERROR_MESSAGE);
			return;
		}

		//Checks if server IP is the same as your external IP
		//Must use 127.0.0.1 in this case
		try {
			send("C", IP, MainMenu.DEF_PORT);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "Could not connect to server", "Sorry!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		this.setSignalBegin(System.currentTimeMillis());
	}

	public void run() {
		ArrayList<Ping> pings = new ArrayList<Ping>();
		refresh();
		while(true)
		{
			try {
				receiveData = new byte[1024];
				receive = new DatagramPacket(receiveData, receiveData.length);
				
				try {
					socket.receive(receive);
				} catch (Exception e) {
					return;
				}
				//Get input
				String input = new String(receive.getData()).trim();
				switch(input.charAt(0))
				{
				case 'S':
					String[] tokens = input.split(" ");
					//System.out.println(input);
					servers.clear();
					pings.clear();
					int numInputs = 4;
					
					serversData = new Object[(tokens.length-2)/numInputs][6];
					String thisIP = tokens[1];
					//System.out.println("Received Servers\n"+input);
					if(tokens.length > 2)
					{
						for(int i = 0; i < tokens.length-2;i+=numInputs)
						{
							tokens[i+2] = tokens[i+2].replace('_', ' ');
							serversData[i/numInputs][0] = tokens[i+2];

							serversData[i/numInputs][1] = "Connecting...";
							serversData[i/numInputs][2] = "-";
							serversData[i/numInputs][3] = tokens[i+3];
							serversData[i/numInputs][4] = tokens[i+4];
							serversData[i/numInputs][5] = tokens[i+5];

							int port = Integer.parseInt(tokens[i+4]);
							String IP = tokens[i+3];
							if(IP.equals(thisIP))
							{
								System.out.println("Local server");
								IP = "/127.0.0.1";
							}
							send("P", IP, port);
						}			
					}
					
					for (int i = 0; i < serversData.length; i ++)
					{
						int k = i;
						for (int j = i+1; j < serversData.length; j++)
						{
							if (((String)serversData[j][0]).compareTo(((String)serversData[k][0]))<0)
							{
								k=j;
							}
						}
						
						for (int no = 0; no < 5; no++)
						{
							Object object = serversData[i][no];
							serversData[i][no]=serversData[k][no];
							serversData[k][no]=object;
						}
					}
					pings = new ArrayList<Ping>();;
					servers =  new ArrayList<ServerInfo>();;
					
					for (int i=0; i< serversData.length;i++)
					{
						String IP = (String)serversData[i][3];
						if(IP.equals(thisIP))
						{
							System.out.println("Local server");
							IP = "/127.0.0.1";
						}
						pings.add(new Ping(IP,Integer.parseInt((String)serversData[i][4]), System.currentTimeMillis()));
						servers.add(new ServerInfo((String)serversData[i][2], IP, Integer.parseInt((String)serversData[i][4]), Integer.parseInt((String) serversData[i][5])));
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
							int maxPlayers = Server.MAX_PLAYERS;
							if (((String)serversData[index][0]).contains("1v1"))
							{
								maxPlayers = 2;
							}
							else if (((String)serversData[index][0]).contains("2v2"))
							{
								maxPlayers = 4;
							}
							else if (((String)serversData[index][0]).contains("3v3"))
							{
								maxPlayers = 6;
							}
							else if (((String)serversData[index][0]).contains("4v4"))
							{
								maxPlayers = 8;
							}
							else if (((String)serversData[index][0]).contains("5v5"))
							{
								maxPlayers = 10;
							}
							
							if (Integer.parseInt((String)serversData[index][5]) >= maxPlayers)
							{
								serversData[index][1] = "Full";
							}
							else
							{
								serversData[index][1] = "Open";
							}
							
							table.setValueAt(serversData[index][1], index, 1);
							
							serversData[index][2] = end - ping.getStart();
							table.setValueAt(end - ping.getStart(), index, 2);
							
							break;
						}
						index++;
					}
					break;
				case 'C':
					open = false;
					this.setSignalBegin(Long.MAX_VALUE / 2);
					IP = receive.getAddress().toString();
					if(!Character.isDigit(IP.charAt(0)))
						IP = IP.substring(1);
					dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
					MainMenu.joinLobby(IP, receive.getPort());
					break;
				}
			}
			
			catch (Exception e)
			{
				e.printStackTrace();
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
		if(socket != null)
		{
			socket.close();
			socket = null;
		}
		MainMenu.mainFrame.requestFocus();
		open = false;
		dispose();
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		if(socket != null)
		{
			socket.close();
			socket = null;
		}
		MainMenu.mainFrame.requestFocus();
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
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == timer)
		{
			if (System.currentTimeMillis() >= this.getSignalBegin() + this.SIGNAL_TIME_LIMIT)
			{
				JOptionPane.showMessageDialog(null, "Could not connect to server", "Sorry!", JOptionPane.ERROR_MESSAGE);
				this.setSignalBegin(Long.MAX_VALUE / 2);
			}
		}
		else if(event.getSource() == refresh)
			refresh();
		else if(event.getSource() == connect)
			connect();
		else if (event.getSource() == manualConnect)
			manualConnect();
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

	public long getSignalBegin() {
		return signalBegin;
	}

	public void setSignalBegin(long signalBegin) {
		this.signalBegin = signalBegin;
	}

}

