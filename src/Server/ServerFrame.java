package Server;
import java.awt.Dimension;

import javax.swing.JFrame;


public class ServerFrame extends JFrame
{

	/**
	 * Constructor for the game frame
	 */
	public ServerFrame()
	{
		setPreferredSize(new Dimension(ServerPlayer.SCREEN_WIDTH, ServerPlayer.SCREEN_HEIGHT));
		setResizable(false);
		setTitle("WorldQuest Online - Server Map");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		
	}
}