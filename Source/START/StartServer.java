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
	private final static String[] roomNames = {"ROOM1", "ROOM2", "ROOM3", "ROOM4", "ROOM5"};
	private static int currRoomName = 0;

	public static void main(String[] args) throws SocketException {
		GameImage.hostingServer=true;
		Images.importImages();
		Audio.importAudio();
		Maps.importMaps();

		ServerManager server = new ServerManager(roomNames[currRoomName++], MainMenu.DEF_PORT, MAX_ROOMS);

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
