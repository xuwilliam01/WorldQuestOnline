package START;

import java.net.SocketException;
import java.util.Scanner;

import Client.ClientFrame;
import Imports.Audio;
import Imports.GameImage;
import Imports.Images;
import Imports.Maps;
import Menu.MainMenu;
import Server.ServerManager;

public class StartServer {

	final static int MAX_ROOMS = 1;

	public static void main(String[] args) throws SocketException {
		GameImage.hostingServer=true;
		Images.importImages();
		Audio.importAudio();
		Maps.importMaps();
		
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter server name: ");
		String name = scan.nextLine();
		name = name.replace(' ', '_');

		ServerManager server = new ServerManager(name, MainMenu.DEF_PORT, MAX_ROOMS);

		Thread serverThread = new Thread(server);

		serverThread.start();

		

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
