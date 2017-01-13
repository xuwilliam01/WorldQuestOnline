package ClientUDP;

import java.awt.Color;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import CentralServer.LeaderboardPlayer;
import Client.Client;
import Client.ClientInventory;
import Menu.MainMenu;

public class Leaderboard extends JFrame implements Runnable, ActionListener, WindowListener{

	private DatagramSocket socket;
	private DatagramPacket receive;
	private DatagramPacket send;

	private byte[] receiveData;
	private byte[] sendData;

	public static boolean open = false;

	private JButton refresh = new JButton("Refresh");

	private ArrayList<LeaderboardPlayer> leaderboard = new ArrayList<LeaderboardPlayer>();

	public Leaderboard(int port) throws SocketException
	{
		setBackground(Color.BLACK);
		setSize((Client.SCREEN_WIDTH
				+ ClientInventory.INVENTORY_WIDTH)/2, Client.SCREEN_HEIGHT/2);
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
	public void actionPerformed(ActionEvent arg0) {
		sendData = "B".getBytes();
		try {
			send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ClientUDP.ClientAccountWindow.IP), CentralServer.CentralServer.PORT);
			socket.send(send);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		String[] tokens = input.split(" ");
		for(int i = 0; i < tokens.length;)
		{
			int len = Integer.parseInt(tokens[i++]);
			int rating = Integer.parseInt(tokens[i++]);
			int wins = Integer.parseInt(tokens[i++]);
			int losses = Integer.parseInt(tokens[i++]);
			String name = tokens[i++];
			for(int j = 1; j < len;j++)
				name += " "+tokens[i++];
			leaderboard.add(new LeaderboardPlayer(name, rating, wins, losses));
		}
		repaint();
	}

	private class Panel extends JPanel
	{
		public Panel()
		{
			setLayout(null);
			
			refresh.setSize(150,50);
			refresh.setLocation(Leaderboard.this.getWidth()-200,Leaderboard.this.getHeight()-100);
			refresh.addActionListener(Leaderboard.this);
			add(refresh);
			refresh.doClick();
		}
		
		@Override
		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			graphics.setColor(Color.black);
			synchronized(leaderboard)
			{
				int y = 30;
				int delta = 30;
				for(LeaderboardPlayer player : leaderboard)
				{
					graphics.drawString(String.format("%-25s R:%4d %3dW %3dL", player.getName(), player.getRating(), player.getWins(), player.getLosses()), 50, y+=delta);
				}
			}
		}
	}
}
