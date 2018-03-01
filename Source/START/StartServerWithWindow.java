package START;

import java.net.SocketException;
import java.util.Scanner;

import javax.swing.JOptionPane;

import Imports.Audio;
import Imports.GameImage;
import Imports.Images;
import Imports.GameMaps;
import Menu.MainMenu;
import Server.ServerManager;

public class StartServerWithWindow {

	final static int MAX_ROOMS = 1;
	static String name;

	public static void main(String[] args) throws SocketException {
		while (true) {
			try {
				name = JOptionPane.showInputDialog("What would you like to name the room? (No spaces)");
				
				if (name == null)
				{
					return;
				}
				
				if (name.contains(" ") || name.equals(""))
				{
					continue;
				}
				break;
			} catch (Exception e) {

			}
		}
		
		int listServer = JOptionPane.showOptionDialog(null, "Would you like to list your server on the global server list?", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, null, null);
		
		if (listServer == JOptionPane.CLOSED_OPTION) {
			return;
		}
		
		JOptionPane.showMessageDialog(null, "Remember to forward port 9988 to open public access to your server!");
		
		Imports.Audio.isServer=true;
		GameImage.hostingServer=true;
		Images.importImages();
		Audio.importAudio(false);
		GameMaps.importMaps();

		ServerManager server = new ServerManager(name, MainMenu.DEF_PORT, MAX_ROOMS, true, (listServer == JOptionPane.YES_OPTION));

		Thread serverThread = new Thread(server);

		serverThread.start();

		System.out.println("Done loading");
	}

}
