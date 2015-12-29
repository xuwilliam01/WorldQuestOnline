package Client;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

/**
 * Run the client here
 * @author William Xu and Alex Raita
 *
 */
public class StartClient
{
	public static void main(String[] args)
	{

		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				boolean connected = false;
				int port;

				String serverIP = JOptionPane
						.showInputDialog("Please enter the IP address of the server");
				if (serverIP.equals(""))
				{
					serverIP = "192.168.0.10";
					port = 5000;
				}
				else
				{
					port = Integer.parseInt(JOptionPane
							.showInputDialog("Please enter the port of the server"));
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
						port = Integer.parseInt(JOptionPane
								.showInputDialog("Please enter the port of the server"));
					}
				}
				ClientFrame myFrame = new ClientFrame();
				
				ClientInventory inventory = new ClientInventory();
				Client client = new Client(mySocket,inventory);
				client.setLocation(0,0);
				inventory.setLocation(Client.SCREEN_WIDTH,0);
				
				myFrame.add(client);
				myFrame.add(inventory);
				client.initialize();
				client.revalidate();
				inventory.revalidate();
				myFrame.setVisible(true);
			}
		});

	}
}
