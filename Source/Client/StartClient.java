package Client;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

/**
 * Run the client here
 * @author William Xu and Alex Raita
 *aw
 */
public class StartClient
{
	public static void main(String[] args)
	{
		boolean connected = false;
		String serverIP;
		int port;
		String playerName;

		serverIP = JOptionPane
				.showInputDialog("Please enter the IP address of the server");
		if(serverIP == null)
			return;
		if (serverIP.equals(""))
		{
			serverIP = "192.168.0.16";
			port = 5000;
			playerName = "Player";
		}
		else
		{
			while(true)
			{
				try
				{
					String portNum = JOptionPane
							.showInputDialog("Please enter the port of the server");
					if(portNum == null)
						return;

					port = Integer.parseInt(portNum);

					playerName = JOptionPane
							.showInputDialog("Please enter your name");
					if(playerName == null)
						return;
					break;
				}
				catch(NumberFormatException E)
				{

				}
			}
		}

		Socket mySocket = null;

		while (!connected)
		{
			try
			{
				mySocket = new Socket(serverIP, port);
				connected = true;
			}
			catch (IOException e)
			{
				serverIP = JOptionPane
						.showInputDialog("Connection Failed. Please a new IP");
				port = Integer
						.parseInt(JOptionPane
								.showInputDialog("Please enter the port of the server"));
			}
		}
		ClientFrame myFrame = new ClientFrame();
		JLayeredPane pane = new JLayeredPane();
		pane.setLocation(0, 0);
		pane.setLayout(null);
		pane.setSize(Client.SCREEN_WIDTH, Client.SCREEN_HEIGHT);
		pane.setDoubleBuffered(true);
		myFrame.add(pane);
		pane.setVisible(true);

		ClientInventory inventory = new ClientInventory(null);
		Client client = new Client(mySocket, inventory, pane,playerName);
		inventory.setClient(client);

		client.setLocation(0, 0);
		inventory.setLocation(Client.SCREEN_WIDTH, 0);

		pane.add(client);
		myFrame.add(inventory);
		client.initialize();
		client.revalidate();
		inventory.revalidate();
		pane.revalidate();
		pane.setVisible(true);
		myFrame.setVisible(true);
		inventory.repaint();
	}
}
