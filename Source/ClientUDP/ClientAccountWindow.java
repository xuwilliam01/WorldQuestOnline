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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import Client.Client;
import Client.ClientInventory;

public class ClientAccountWindow extends JFrame implements Runnable, ActionListener, WindowListener {

	private DatagramSocket socket;
	private DatagramPacket receive;
	private DatagramPacket send;

	private byte[] receiveData;
	private byte[] sendData;

	private JButton create = new JButton("Create");
	private JButton login = new JButton("Login");
	private JLabel usernameL = new JLabel("Username:");
	private JLabel passwordL = new JLabel("Password: ");
	private JLabel confirmL = new JLabel("Confirm Password: ");
	private JTextField username = new JTextField();
	private JTextField password = new JTextField();
	private JTextField confirm  = new JTextField();
	
	public static boolean open = false;
	
	public ClientAccountWindow(int port) throws SocketException
	{
		setBackground(Color.BLACK);
		setSize((Client.SCREEN_WIDTH
				+ ClientInventory.INVENTORY_WIDTH)/3, Client.SCREEN_HEIGHT/3);
		setResizable(false);
		setTitle("Account Login");
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
		
		int deltay = 50;
		int y = 75;
		int x = 10;
		int deltax = 140;
		usernameL.setSize(150,50);
		usernameL.setLocation(x,y);
		add(usernameL);
		
		passwordL.setSize(150,50);
		passwordL.setLocation(x,y+deltay);
		add(passwordL);
		
		confirmL.setSize(150,50);
		confirmL.setLocation(x,y+2*deltay);
		add(confirmL);
		
		username.setSize(200,40);
		username.setLocation(x+deltax,y);
		add(username);
		
		password.setSize(200,40);
		password.setLocation(x+deltax,y+deltay);
		add(password);
		
		confirm.setSize(200,40);
		confirm.setLocation(x+deltax,y+2*deltay);
		add(confirm);
		
		create.addActionListener(this);
		create.setSize(100,50);
		create.setLocation(x,250);
		add(create);
		
		login.addActionListener(this);
		login.setSize(100,50);
		login.setLocation(x+deltax,250);
		add(login);
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
			switch(input)
			{
			case "LY":
				dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
				JOptionPane.showMessageDialog(this, "Login Successful!");
				break;
			case "LN":
				JOptionPane.showMessageDialog(this, "Login Failed! Either your username or password is incorrect!");
				break;
			case "CY":
				dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
				JOptionPane.showMessageDialog(this, "Created Account! You are logged in.");
				break;
			case "CN":
				JOptionPane.showMessageDialog(this, "Username already in use. Please pick another one.");
				break;
			}
			
		}

	}

	public static String hash(String user, String pass)
	{
		return (user+pass).replaceAll(" ","_");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == login)
		{
			if(!password.getText().equals(confirm.getText()))
			{
				JOptionPane.showMessageDialog(this, "Passwords do not match");
				return;
			}
			String out = "L "+hash(username.getText(),password.getText())+" "+username.getText();
			sendData = out.getBytes();
			try {
				send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(CentralServer.CentralServer.IP), CentralServer.CentralServer.PORT);
				socket.send(send);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(arg0.getSource() == create)
		{
			String out = "C "+hash(username.getText(),password.getText())+" "+username.getText();
			sendData = out.getBytes();
			try {
				send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(CentralServer.CentralServer.IP), CentralServer.CentralServer.PORT);
				socket.send(send);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

}
