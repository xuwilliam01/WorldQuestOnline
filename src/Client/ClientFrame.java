package Client;
import java.awt.Dimension;

import javax.swing.JFrame;


@SuppressWarnings("serial")
public class ClientFrame extends JFrame
{

	/**
	 * Constructor for the game frame
	 */
	public ClientFrame()
	{
		setPreferredSize(new Dimension(Client.SCREEN_WIDTH+ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT));
		setResizable(false);
		setTitle("WorldQuest Online");
		setLocationRelativeTo(null);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		
	}
}
