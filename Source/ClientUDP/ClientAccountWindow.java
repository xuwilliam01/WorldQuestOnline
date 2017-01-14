package ClientUDP;

import java.awt.Color; 
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import Client.Client;
import Client.ClientInventory;
import Menu.MainMenu;

public class ClientAccountWindow extends JFrame implements Runnable, ActionListener, WindowListener, KeyListener {

	public static final String CREDS_PATH = "Resources//savedata";
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
	private JPasswordField password = new JPasswordField();
	private JPasswordField confirm  = new JPasswordField();
	private JButton menuLoginButton;
	private Image logoutOver;
	
	public final static String IP = "52.14.41.226";
	
	//public final static String IP = "74.12.138.159";
	//public final static String IP = "127.0.0.1";
	public final static int PORT = 9977;

	public static boolean open = false;
	public static boolean loggedIn = false;

	public static String savedUser;
	private static String savedPassword;
	public static String savedKey;

	public ClientAccountWindow(int port, JButton menuLoginButton, Image logoutOver) throws SocketException
	{
		setBackground(Color.BLACK);
		setSize(480, 308);
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
		this.menuLoginButton = menuLoginButton;
		this.logoutOver = logoutOver;

		int deltay = 45;
		int y = 25;
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
		confirmL.setVisible(false);
		add(confirmL);

		username.setSize(200,40);
		username.setLocation(x+deltax,y);
		username.setDocument(new JTextFieldLimit(25));
		username.addKeyListener(this);
		add(username);

		password.setSize(200,40);
		password.setLocation(x+deltax,y+deltay);
		password.setDocument(new JTextFieldLimit(25));
		password.addKeyListener(this);
		add(password);

		confirm.setSize(200,40);
		confirm.setLocation(x+deltax,y+2*deltay);
		confirm.setVisible(false);
		confirm.setDocument(new JTextFieldLimit(25));
		confirm.addKeyListener(this);
		add(confirm);

		create.addActionListener(this);
		create.setSize(100,50);
		create.setLocation(x,y+(int)(3.5*deltay));
		add(create);

		login.addActionListener(this);
		login.setSize(100,50);
		login.setLocation(x+deltax,y+(int)(3.5*deltay));
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
				saveCredentials();
				dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
				JOptionPane.showMessageDialog(this, "Login Successful!");
				break;
			case "LN":
				JOptionPane.showMessageDialog(this, "Login Failed! Either your username or password is incorrect!");
				break;
			case "CY":
				saveCredentials();
				dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
				JOptionPane.showMessageDialog(this, "Created Account! You are now logged in.");
				break;
			case "CN":
				JOptionPane.showMessageDialog(this, "Username already in use. Please pick another one.");
				break;
			}

		}

	}

	public static boolean checkLogin()
	{
		File f = new File(CREDS_PATH);
		if(f.exists() && !f.isDirectory()) {
			loggedIn = true;
			try {
				BufferedReader br = new BufferedReader(new FileReader(CREDS_PATH));
				savedUser = br.readLine();
				savedPassword = br.readLine();
				savedKey = br.readLine();
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	public static void logout()
	{
		File f = new File(CREDS_PATH);
		loggedIn = false;
		savedUser = null;
		savedPassword = null;
		savedKey = null;
		if(!f.exists() || f.isDirectory()) { 
			return;
		}
		f.delete();
	}

	public void saveCredentials()
	{
		savedUser = username.getText();
		savedPassword = new String(password.getPassword());
		savedKey = hash(username.getText(),new String(password.getPassword()));
		loggedIn = true;
		menuLoginButton.setIcon(new ImageIcon(logoutOver));

		PrintWriter out = null;
		try {
			out = new PrintWriter(new File(CREDS_PATH));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println(savedUser);
		out.println(savedPassword);
		out.println(savedKey);
		out.close();
	}
	public static String hash(String user, String pass)
	{
		String toHash = (user+pass).replaceAll(" ","_");
		int result = 0;
		for(int i = 0; i < toHash.length();i++)
		{
			result += (toHash.charAt(i))*(int)(Math.pow(31, toHash.length()-i-1));
		}
		return Integer.toString(result);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == login)
		{
			confirm.setVisible(false);
			confirmL.setVisible(false);
			repaint();
			if(username.getText().length() == 0 || new String(password.getPassword()).length() == 0)
			{
				JOptionPane.showMessageDialog(this, "Username and password are too short");
				return;
			}
			String out = "L "+hash(username.getText(),new String(password.getPassword()))+" "+username.getText();
			sendData = out.getBytes();
			try {
				send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ClientUDP.ClientAccountWindow.IP), PORT);
				socket.send(send);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(arg0.getSource() == create)
		{
			if(!confirm.isVisible())
			{
				confirm.setVisible(true);
				confirmL.setVisible(true);
				repaint();
				return;
			}
			if(username.getText().length() == 0 || new String(password.getPassword()).length() == 0)
			{
				JOptionPane.showMessageDialog(this, "Username and password are too short");
				return;
			}
			if(!new String(password.getPassword()).equals(new String(confirm.getPassword())))
			{
				JOptionPane.showMessageDialog(this, "Passwords do not match");
				return;
			}
			String out = "C "+hash(username.getText(),new String(password.getPassword()))+" "+username.getText();
			sendData = out.getBytes();
			try {
				send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ClientUDP.ClientAccountWindow.IP), PORT);
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
	
	public class JTextFieldLimit extends PlainDocument {
		  private int limit;

		  JTextFieldLimit(int limit) {
		   super();
		   this.limit = limit;
		   }

		  public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
		    if (str == null) return;

		    if ((getLength() + str.length()) <= limit) {
		      super.insertString(offset, str, attr);
		    }
		  }
		}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER)
		{
			if(arg0.getSource() == confirm)
			{
				create.doClick();
			}
			else login.doClick();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
