package Client;

import java.net.Socket;

public class StartClient
{
	public static void main(String[] args)
	{
		Connection newConnection = new Connection();
		Thread connectThread = new Thread (newConnection);
		connectThread.start();
		
		while (!newConnection.isConnected())
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		Socket mySocket = newConnection.getSocket();
		
		Frame myFrame = new Frame();
		Client client = new Client(mySocket);
		client.initialize();
	}
}
