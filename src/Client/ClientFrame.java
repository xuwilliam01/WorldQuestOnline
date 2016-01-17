package Client;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;


@SuppressWarnings("serial")
public class ClientFrame extends JFrame
{

	/**
	 * Constructor for the game frame
	 */
	public ClientFrame()
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
	    DisplayMode dm = gs.getDisplayMode();
	    Client.SCREEN_WIDTH = dm.getWidth()-300;
	    Client.SCREEN_HEIGHT = dm.getHeight();
	    
	    System.out.println(dm.getWidth()-300);
	    System.out.println(dm.getHeight());
		
		setPreferredSize(new Dimension(Client.SCREEN_WIDTH+ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT));
		setResizable(false);
		setTitle("WorldQuest Online");
		setLocationRelativeTo(null);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		setVisible(true);
	}
}
