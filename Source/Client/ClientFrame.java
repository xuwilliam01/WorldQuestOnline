package Client;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;


@SuppressWarnings("serial")
/**
 * The frame that the game will be displayed on
 * @author Alex Raita & William Xu
 *
 */
public class ClientFrame extends JFrame
{
	/**
	 * Canvas
	 */
	public static ClientCanvas canvas = new ClientCanvas();
	
	/**
	 * Constructor for the game frame
	 */
	public ClientFrame()
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
	    DisplayMode dm = gs.getDisplayMode();
	    
	    //Set the window to the size of the screen
	    Client.SCREEN_WIDTH = dm.getWidth()-ClientInventory.INVENTORY_WIDTH;
	    if (Client.SCREEN_WIDTH > 1920-ClientInventory.INVENTORY_WIDTH)
	    {
	    	Client.SCREEN_WIDTH = 1920-ClientInventory.INVENTORY_WIDTH;
	    }
	    
	    Client.SCREEN_HEIGHT = dm.getHeight();
	    if (Client.SCREEN_HEIGHT > 1080)
	    {
	    	Client.SCREEN_HEIGHT=1080;
	    }
	    
	    setBackground(Color.BLACK);
	    System.out.println(dm.getWidth()-ClientInventory.INVENTORY_WIDTH);
	    System.out.println(dm.getHeight());
		setPreferredSize(new Dimension(Client.SCREEN_WIDTH+ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT));
		setResizable(false);
		setTitle("WorldQuest Online");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(null);
		pack();
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		
		getContentPane().add(canvas);
		setVisible(true);
	}
}