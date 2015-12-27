package Client;
import java.awt.Dimension;

import javax.swing.JFrame;


public class ClientFrame extends JFrame
{

	/**
	 * Constructor for the game frame
	 */
	public ClientFrame()
	{
		setPreferredSize(new Dimension(Client.SCREEN_WIDTH, Client.SCREEN_HEIGHT));
		setResizable(false);
		setTitle("WorldQuest Online");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		
	}
}
