package START;

import java.net.SocketException;
import java.util.Scanner;

import Imports.Audio;
import Imports.GameImage;
import Imports.Images;
import Imports.GameMaps;
import Menu.MainMenu;
import Server.ServerManager;

public class StartServer {

	final static int MAX_ROOMS = 1;

	public static void main(String[] args) throws SocketException {
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter server name: ");
		String name = scan.nextLine();
		name = name.replace(' ', '_');
		Imports.Audio.isServer=true;
		
		GameImage.hostingServer=true;
		Images.importImages();
		Audio.importAudio(false);
		GameMaps.importMaps();

		ServerManager server = new ServerManager(name, MainMenu.DEF_PORT, MAX_ROOMS, false, true);

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
