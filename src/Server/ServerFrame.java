package Server;
import java.awt.Dimension;

import javax.swing.JFrame;

import Server.Creatures.ServerPlayer;


public class ServerFrame extends JFrame
{

	public static final int FRAME_FACTOR = 1;
	/**
	 * Constructor for the game frame
	 */
	public ServerFrame()
	{
		setPreferredSize(new Dimension(Client.Client.SCREEN_WIDTH/FRAME_FACTOR, Client.Client.SCREEN_HEIGHT/FRAME_FACTOR));
		setResizable(false);
		setTitle("WorldQuest Online - Server Map");
		setLocationRelativeTo(null);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		
	}
}