package ClientUDP;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JFrame;

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
		
		refresh.setSize(150,50);
		refresh.setLocation(50,200);
		refresh.addActionListener(this);
		add(refresh);
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
			send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(CentralServer.CentralServer.IP), CentralServer.CentralServer.PORT);
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
		System.out.println(input);
		
	}

}
