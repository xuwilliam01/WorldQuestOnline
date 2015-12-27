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
				JOptionPane.showMessageDialog(null, "You have joined #1");
				ClientFrame myFrame = new ClientFrame();
				Client client = new Client(mySocket);
				myFrame.add(client);
				client.initialize();
				client.revalidate();
				myFrame.setVisible(true);
				JOptionPane.showMessageDialog(null, "You have joined #5");
			}
		});

	}
}
