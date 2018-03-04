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
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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

import Menu.MainMenu;

@SuppressWarnings("serial")
public class ClientAccountWindow extends JFrame implements Runnable, ActionListener, WindowListener, KeyListener {

	public static final String CREDS_PATH = "Resources//savedata";
	private DatagramSocket socket;
	private DatagramPacket receive;
	private DatagramPacket send;

	private byte[] receiveData;
	private byte[] sendData;

	private JButton create = new JButton("New User");
	private JButton login = new JButton("Login");
	private JLabel usernameL = new JLabel("Username:");
	private JLabel passwordL = new JLabel("Password: ");
	private JLabel confirmL = new JLabel("Confirm Password: ");
	private JTextField username = new JTextField();
	private JPasswordField password = new JPasswordField();
	private JPasswordField confirm  = new JPasswordField();
	private JButton menuLoginButton;
	private Image logoutOver;
	
	public static String Domain = "http://www.worldquest.online/";
	public final static int PORT = 9977;

	public static boolean open = false;
	public static boolean loggedIn = false;

	public static String savedUser;
	private static String savedPassword;
	public static String savedKey;
	
	int deltay = 45;
	int y = 25;
	int x = 15;
	int deltax = 140;

	public static final int MAX_NAME_LEN = 20;
	public ClientAccountWindow(int port, JButton menuLoginButton, Image logoutOver) throws SocketException
	{
		this.getContentPane().setBackground(Color.BLACK);
		setSize(400, 270);
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

		usernameL.setLocation(x + 3,y - 5);
		usernameL.setSize(150,50);
		usernameL.setForeground(Color.white);
		add(usernameL);

		passwordL.setLocation(x + 3,y+deltay - 5);
		passwordL.setSize(150,50);
		passwordL.setForeground(Color.white);
		add(passwordL);

		confirmL.setLocation(x + 3,y+2*deltay - 5);
		confirmL.setSize(150,50);
		confirmL.setVisible(false);
		confirmL.setForeground(Color.white);
		add(confirmL);

		username.setLocation(x+deltax,y);
		username.setSize(this.getWidth() - username.getX() - x,40);
		username.setDocument(new JTextFieldLimit(MAX_NAME_LEN));
		username.addKeyListener(this);
		username.setBackground(Color.DARK_GRAY);
		username.setForeground(Color.white);
		add(username);

		password.setLocation(x+deltax,y+deltay);
		password.setSize(this.getWidth() - password.getX() - x,40);
		password.setDocument(new JTextFieldLimit(MAX_NAME_LEN));
		password.addKeyListener(this);
		password.setBackground(Color.DARK_GRAY);
		password.setForeground(Color.white);
		add(password);

		confirm.setLocation(x+deltax,y+2*deltay);
		confirm.setSize(this.getWidth() - confirm.getX() - x,40);
		confirm.setVisible(false);
		confirm.setDocument(new JTextFieldLimit(MAX_NAME_LEN));
		confirm.addKeyListener(this);
		confirm.setBackground(Color.DARK_GRAY);
		confirm.setForeground(Color.white);
		add(confirm);

		create.addActionListener(this);
		create.setSize(120,50);
		create.setLocation(x,y+(int)(3.5*deltay));
		create.setBackground(Color.gray);
		add(create);

		login.addActionListener(this);
		login.setSize(100,50);
		login.setLocation(this.getWidth() - login.getWidth() - x,y+(int)(3.5*deltay));
		login.setBackground(Color.gray);
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
				JOptionPane.showMessageDialog(this, "Login Successful!");
				dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
				break;
			case "LN":
				JOptionPane.showMessageDialog(this, "Login Failed! Either your username or password is incorrect!");
				break;
			case "CY":
				saveCredentials();
				JOptionPane.showMessageDialog(this, "Created Account! You are now logged in.");
				dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
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
			repaint();
			if(username.getText().length() == 0 || new String(password.getPassword()).length() == 0)
			{
				JOptionPane.showMessageDialog(this, "Username and/or password is too short");
				return;
			}
			String out = "L "+hash(username.getText(),new String(password.getPassword()))+" "+username.getText();
			sendData = out.getBytes();
			try {
				send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ClientUDP.ClientAccountWindow.Domain), PORT);
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
				login.setVisible(false);
				create.setText("Create");
				create.setSize(100, 50);
				create.setLocation(this.getWidth()/2 - create.getWidth()/2, create.getY());
				repaint();
			}
			else
			{
				if(username.getText().length() == 0 || new String(password.getPassword()).length() == 0)
				{
					JOptionPane.showMessageDialog(this, "Username and password are too short", "", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(!new String(password.getPassword()).equals(new String(confirm.getPassword())))
				{
					JOptionPane.showMessageDialog(this, "Passwords do not match", "", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String out = "C "+hash(username.getText(),new String(password.getPassword()))+" "+username.getText();
				sendData = out.getBytes();
				try {
					send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ClientUDP.ClientAccountWindow.Domain), PORT);
					socket.send(send);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

		private static final long serialVersionUID = 1L;
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
			if(confirm.isVisible())
			{
				create.doClick();
			}
			else
			{
				login.doClick();
			}
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
