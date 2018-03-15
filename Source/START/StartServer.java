package START;

import java.net.SocketException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import ClientUDP.ClientAccountWindow;
import Imports.Audio;
import Imports.GameImage;
import Imports.Images;
import Imports.GameMaps;
import Menu.MainMenu;
import Server.ServerManager;

public class StartServer {

	final static int MAX_ROOMS = 1;
	public static boolean autoStart = false;

	public static void main(String[] args) throws SocketException {
		String name = "";
		if (args.length == 0)
		{
			Scanner scan = new Scanner(System.in);
			System.out.print("Enter server name: ");
			name = scan.nextLine().replace(' ', '_');
			System.out.print("Enter central server IP: ");
			ClientAccountWindow.Domain = scan.nextLine();
			scan.close();
		}
		else if (args.length == 1)
		{
			name = args[0];
			System.out.println("Server name set to: " + name);
		}
		else
		{
			StringBuilder fullName = new StringBuilder();
			fullName.append(args[0]);
			for (int i = 1; i < args.length - 1; i++)
			{
				fullName.append(" " + args[i]);
			}
			name = fullName.toString().replace(' ', '_');
			System.out.println("Server name set to: " + name);
			ClientAccountWindow.Domain = args[args.length-1];
			autoStart = true;
		}
		
		Imports.Audio.isServer=true;
		GameImage.hostingServer=true;
		Images.importImages();
		Audio.importAudio(false);
		GameMaps.importMaps();

		ServerManager server = new ServerManager(name, MainMenu.DEF_PORT, MAX_ROOMS, false, true);
		Thread serverThread = new Thread(server);
		serverThread.start();

		System.out.println("Done loading");
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
		System.out.println(dtf.format(now));

//		while (true) {
//			if (scan.nextLine().equalsIgnoreCase("exit")) {
//				System.out.println("Server closed");
//				scan.close();
//				System.exit(0);
//			}
//		}
	}

}
