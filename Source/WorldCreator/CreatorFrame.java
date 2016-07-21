package WorldCreator;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import Client.Client;
import Client.ClientInventory;
import Server.Creatures.ServerPlayer;

/**
 * Frame for the world creator
 * 
 * @author Alex Raita & William Xu
 *
 */
public class CreatorFrame extends JFrame {

	/**
	 * Constructor
	 */
	public CreatorFrame() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		DisplayMode dm = gs.getDisplayMode();

		// Match to the screen size
		ClientInventory.INVENTORY_WIDTH = (int) (300 * (dm.getWidth() / 1920.0));

		Client.SCREEN_WIDTH = dm.getWidth() - ClientInventory.INVENTORY_WIDTH;
		if (Client.SCREEN_WIDTH > 1920 - ClientInventory.INVENTORY_WIDTH) {
			Client.SCREEN_WIDTH = 1920 - ClientInventory.INVENTORY_WIDTH;
		}
		Client.SCREEN_HEIGHT = dm.getHeight();
		if (Client.SCREEN_HEIGHT > 1080) {
			Client.SCREEN_HEIGHT = 1080;
		}

		setPreferredSize(new Dimension(Client.SCREEN_WIDTH + CreatorItems.WIDTH, Client.SCREEN_HEIGHT));
		setResizable(false);
		setTitle("WorldQuest Online");
		setLocationRelativeTo(null);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setAlwaysOnTop(true);
		setVisible(true);
	}
}
