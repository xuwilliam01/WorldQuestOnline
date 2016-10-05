package START;

import Client.ClientFrame;
import Imports.Images;
import Menu.MainMenu;
import Server.ServerManager;

public class StartServer {

	final static int MAX_ROOMS = 100;
	
	public static void main(String[] args) {
		Images.importImages();
		
		ServerManager server = new ServerManager(MainMenu.DEF_PORT, MAX_ROOMS);

		Thread serverThread = new Thread(server);

		serverThread.start();

	}

}
