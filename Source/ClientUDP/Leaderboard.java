package ClientUDP;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import CentralServer.LeaderboardPlayer;
import Menu.MainMenu;

@SuppressWarnings("serial")
public class Leaderboard extends JFrame implements Runnable, ActionListener, WindowListener, KeyListener{

	public static final int LEADERBOARD_DISPLAY_SIZE = 20;

	private DatagramSocket socket;
	private DatagramPacket receive;
	private DatagramPacket send;

	private byte[] receiveData;
	private byte[] sendData;

	public static boolean open = false;

	private JButton refresh = new JButton("Refresh");

	private ArrayList<LeaderboardPlayer> leaderboard = new ArrayList<LeaderboardPlayer>();
	private ArrayList<LeaderboardPlayer> displayboard = new ArrayList<LeaderboardPlayer>();

	private JTextField search = new JTextField();
	private JLabel searchL = new JLabel("Search");

	public Leaderboard(int port) throws SocketException
	{
		setBackground(Color.BLACK);
		setSize(1200, 500);
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
		if(arg0.getSource() == refresh)
		{
			sendData = "B".getBytes();
			try {
				send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ClientUDP.ClientAccountWindow.IP), ClientAccountWindow.PORT);
				socket.send(send);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		while(true)
		{
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
			try
			{
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
			} catch(Exception e)
			{

			}
			repaint();
		}
	}

	private class Panel extends JPanel
	{
		public Panel()
		{
			setLayout(null);

			refresh.setSize(100,20);
			refresh.setLocation(380,5);
			refresh.addActionListener(Leaderboard.this);
			add(refresh);
			refresh.doClick();

			search.setSize(300,20);
			search.setLocation(70,5);
			search.addKeyListener(Leaderboard.this);
			add(search);

			searchL.setSize(60,20);
			searchL.setLocation(10,5);
			add(searchL);
		}

		@Override
		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			graphics.setColor(Color.white);
			graphics.fillRect(25, 28, 1140, 416);
			graphics.setColor(Color.black);
			graphics.setFont(new Font("Courier", Font.PLAIN, 11)); //Monospace font
			synchronized(leaderboard)
			{
				int y = 30;
				int delta = 20;
				for(int i = 0; i < 3*LEADERBOARD_DISPLAY_SIZE && i < displayboard.size();i++)
				{
					if(i%LEADERBOARD_DISPLAY_SIZE == 0)
						y = 30;
					graphics.drawString(String.format("%3d. %-25s R:%-4d W:%-3d L:%-3d", displayboard.get(i).getRank(), displayboard.get(i).getName(), displayboard.get(i).getRating(),displayboard.get(i).getWins(), displayboard.get(i).getLosses()), 50 + 375*(i/LEADERBOARD_DISPLAY_SIZE), y+=delta);
				}
			}
		}
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
			if(displayboard.size() >= 3*LEADERBOARD_DISPLAY_SIZE)
				break;
		}
		repaint();	

	}

	@Override
	public void keyTyped(KeyEvent arg0) {	
	}
}