package START;

import java.util.Scanner;

import Client.ClientFrame;
import Imports.GameImage;
import Imports.Images;
import Imports.Maps;
import Menu.MainMenu;
import Server.ServerManager;

public class StartServer {

	final static int MAX_ROOMS = 1;

	public static void main(String[] args) {
		GameImage.hostingServer=true;
		Images.importImages();
		Maps.importMaps();

		ServerManager server = new ServerManager(MainMenu.DEF_PORT, MAX_ROOMS);

		Thread serverThread = new Thread(server);

		serverThread.start();

		Scanner scan = new Scanner(System.in);
		
		System.out.println("Done loading");

		while (true) {
			if (scan.nextLine().equalsIgnoreCase("exit")) {
				System.out.println("Server closed");
				scan.close();
				System.exit(0);
			}
		}
	}

}
