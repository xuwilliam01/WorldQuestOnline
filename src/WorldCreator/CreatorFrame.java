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
 * @author Alex Raita & William Xu
 *
 */
public class CreatorFrame extends JFrame{

	/**
	 * Constructor
	 */
	public CreatorFrame()
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
	    DisplayMode dm = gs.getDisplayMode();
	    
	    //Match to the screen size
	    Client.SCREEN_WIDTH = dm.getWidth()-300;
	    Client.SCREEN_HEIGHT = dm.getHeight();
	    
	    System.out.println(dm.getWidth()-300);
	    System.out.println(dm.getHeight());
		
		setPreferredSize(new Dimension(Client.SCREEN_WIDTH+CreatorItems.WIDTH, Client.SCREEN_HEIGHT));
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
