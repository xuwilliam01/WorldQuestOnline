package Client;

import java.io.IOException;
import java.net.Socket;

public class Connection implements Runnable
{
	// IP address and port of the server
	private String serverIP = "127.0.0.1";
	private int port = 5000;
	
	
	private Socket socket;

	private boolean connected;
	
	@Override
	public void run()
	{
		connected = false;

		while (!connected)
		{
			try
			{
				socket = new Socket(serverIP, port);
				connected = true;
			}
			catch (IOException e)
			{
				System.out
						.println("Failed to connect to the server. Try a new one.");
			}
		}
	}
	
	public Socket getSocket()
	{
		return socket;
	}

	public void setSocket(Socket mySocket)
	{
		this.socket = mySocket;
	}

	public boolean isConnected()
	{
		return connected;
	}

	public void setConnected(boolean connected)
	{
		this.connected = connected;
	}
}