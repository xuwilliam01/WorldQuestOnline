package Server;

public class StartServer
{

	public static void main(String[] args)
	{
		
		Server server = new Server();

		Thread serverThread = new Thread(server);

		serverThread.start();
		
		ServerFrame myFrame = new ServerFrame();
		ServerGUI gui = new ServerGUI(server.getEngine().getWorld());
		myFrame.add(gui);
		gui.revalidate();
		
	
	}

}
