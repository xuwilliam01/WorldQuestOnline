package Client;
import java.net.Socket;

/**
 * Run the client here
 * @author William Xu and Alex Raita
 *
 */
public class StartClient
{
	public static void main(String[] args)
	{
		ClientConnection newConnection = new ClientConnection();
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
		
		ClientFrame myFrame = new ClientFrame();
		Client client = new Client(mySocket);
		myFrame.add(client);
		client.initialize();
		client.revalidate();
	}
}
