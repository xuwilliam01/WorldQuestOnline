package Server;

public class RunServer
{

	public static void main(String[] args)
	{
		Server server = new Server();

		Thread serverThread = new Thread(server);

		serverThread.start();
	}

}
