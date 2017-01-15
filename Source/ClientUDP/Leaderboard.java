package ClientUDP;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

import CentralServer.LeaderboardPlayer;
import Client.Client;
import Client.ClientInventory;
import Menu.MainMenu;

public class Leaderboard extends JFrame implements Runnable, ActionListener, WindowListener, KeyListener, FocusListener{

	public static final int LEADERBOARD_DISPLAY_SIZE = 10;

	private DatagramSocket socket;
	private DatagramPacket receive;
	private DatagramPacket send;

	private byte[] receiveData;
	private byte[] sendData;

	public static boolean open = false;

	private JButton refresh;
	private JButton back;

	private ArrayList<LeaderboardPlayer> leaderboard = new ArrayList<LeaderboardPlayer>();
	private ArrayList<LeaderboardPlayer> displayboard = new ArrayList<LeaderboardPlayer>();

	private JTextField search;
	private JLabel searchL;
	private JLabel title;
	
	private JTable table;
	
	
	JScrollPane scrollPane;
	
	private String[] columnNames = {"Rank",
			"Name",
            "Rating",
            "Wins",
            "Losses"};
	Object[][] data;

	public Leaderboard(int port) throws SocketException
	{
		setBackground(Color.BLACK);
		setSize(960, 540);
		setResizable(false);
		setTitle("Leaderboard");
		setLocationRelativeTo(null);
		setLayout(null);
		setUndecorated(false);
		setVisible(true);
		revalidate();
		addWindowListener(this);
		
		

		socket = new DatagramSocket(port);
		receiveData = new byte[1024];
		sendData = new byte[1024];

		open = true;

		Panel panel = new Panel();
		panel.setSize(getWidth(),getHeight());
		panel.setLocation(0,0);
		add(panel);
		
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
		if(event.getSource() == refresh)
		{
			sendData = "B".getBytes();
			try {
				send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ClientUDP.ClientAccountWindow.IP), ClientAccountWindow.PORT);
				socket.send(send);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			updateTable();
		}
		if(event.getSource() == back)
		{
			setVisible(false);
			dispose();
		}
	}

	@Override
	public void run() {
		receiveData = new byte[1024];
		receive = new DatagramPacket(receiveData, receiveData.length);
		try {
			socket.receive(receive);
		} catch (Exception e) {
			return;
		}
		//Get input
		String input = new String(receive.getData()).trim();
		leaderboard.clear();
		displayboard.clear();
		search.setText("");
		String[] tokens = input.split(" ");
		for(int i = 0, rank = 1; i < tokens.length; rank++)
		{
			int len = Integer.parseInt(tokens[i++]);
			int rating = Integer.parseInt(tokens[i++]);
			int wins = Integer.parseInt(tokens[i++]);
			int losses = Integer.parseInt(tokens[i++]);
			String name = tokens[i++];
			for(int j = 1; j < len;j++)
				name += " "+tokens[i++];
			leaderboard.add(new LeaderboardPlayer(name, rating, wins, losses,rank));
			displayboard.add(new LeaderboardPlayer(name, rating, wins, losses, rank));
		}
		updateTable();
	}

	private class Panel extends JPanel
	{
		public Panel()
		{

			
			GroupLayout layout = new GroupLayout(this);
			setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			
			title = new JLabel("Leaderboard");
			
			refresh = new JButton("Refresh");
			//refresh.setSize(150,50);
			//refresh.setLocation(Leaderboard.this.getWidth()-200,Leaderboard.this.getHeight()-100);
			refresh.addActionListener(Leaderboard.this);
			add(refresh);
			refresh.doClick();
			
			search = new JTextField();
			//search.setSize(300,20);
			//search.setLocation(70,5);
			search.addKeyListener(Leaderboard.this);
			search.addFocusListener(Leaderboard.this);
			add(search);
			
			back = new JButton("Back");
			back.addActionListener(Leaderboard.this);
			

			table = new JTable(data, columnNames);
			
			table.getColumnModel().getColumn(0).setWidth(60);
			table.getColumnModel().getColumn(1).setWidth(300);
			table.getColumnModel().getColumn(2).setWidth(100);
			table.getColumnModel().getColumn(3).setWidth(100);
			table.getColumnModel().getColumn(4).setWidth(100);
			table.setRowSelectionAllowed(true);
			scrollPane = new JScrollPane(table);
			table.getTableHeader().setReorderingAllowed(false);
			table.setFillsViewportHeight(true);

			add(scrollPane);
			
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addGroup(layout.createSequentialGroup()
								.addComponent(title)
								.addGap(600)
								.addComponent(search))
							.addComponent(scrollPane)
							.addGroup(layout.createSequentialGroup()
								.addComponent(refresh)
								.addGap(700)
								.addComponent(back)))
			);
			layout.setVerticalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(title)
							.addComponent(search))
						.addComponent(scrollPane,400,400,400)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(refresh)
							.addComponent(back))
			);
		}

		//@Override
		/*public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			graphics.setColor(Color.black);
			synchronized(leaderboard)
			{
				int y = 30;
				int delta = 20;
				for(int i = 0; i < LEADERBOARD_DISPLAY_SIZE && i < displayboard.size();i++)
				{
					graphics.drawString(String.format("%3d. %-25s R:%4d %3dW %3dL", displayboard.get(i).getRank(), displayboard.get(i).getName(), displayboard.get(i).getRating(),displayboard.get(i).getWins(), displayboard.get(i).getLosses()), 50, y+=delta);
				}
			}
		}*/
	}
	
	public void updateTable(){
		int tableHeight = Math.min(LEADERBOARD_DISPLAY_SIZE, displayboard.size());
		data = new Object[tableHeight][5];

		synchronized(leaderboard)
		{
			for(int i = 0; i < tableHeight;i++)
			{
				data[i][0] = new Integer(displayboard.get(i).getRank());
				data[i][1] = displayboard.get(i).getName();
				data[i][2] = new Integer(displayboard.get(i).getRating());
				data[i][3] = new Integer(displayboard.get(i).getWins());
				data[i][4] = new Integer(displayboard.get(i).getLosses());
				
				/*model.setValueAt(new Integer(displayboard.get(i).getRank()), i, 0);
				model.setValueAt(displayboard.get(i).getName(), i, 1);
				model.setValueAt(new Integer(displayboard.get(i).getRating()), i, 2);
				model.setValueAt(new Integer(displayboard.get(i).getWins()), i, 3);
				model.setValueAt(new Integer(displayboard.get(i).getLosses()), i, 4);*/
				
				System.out.println(displayboard.get(i).getRank());
				System.out.println(displayboard.get(i).getName());
				//graphics.drawString(String.format("%3d. %-25s R:%4d %3dW %3dL", displayboard.get(i).getRank(), displayboard.get(i).getName(), displayboard.get(i).getRating(),displayboard.get(i).getWins(), displayboard.get(i).getLosses()), 50, y+=delta);
			}
		}
		
		table = new JTable(data, columnNames);
		
		revalidate();
		repaint();

		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		String text = search.getText().trim().toLowerCase();
		System.out.println(text);
		displayboard.clear();
		for(LeaderboardPlayer player : leaderboard)
		{
			if(player.getName().toLowerCase().contains(text))
				displayboard.add(player);
			if(displayboard.size() > LEADERBOARD_DISPLAY_SIZE)
				break;
		}
		updateTable();
		
	}
	public void focusLost(FocusEvent e) {
        if(search.getText().trim().equals(""))
           search.setText("Search Player Names");
    }
	
	public void focusGained(FocusEvent e) {
        if(search.getText().trim().equals("Search Player Names"))
           search.setText("");
    }

	@Override
	public void keyTyped(KeyEvent arg0) {	
	}
}
