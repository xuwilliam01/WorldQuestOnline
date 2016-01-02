package WorldCreator;

import java.awt.Dimension;

import javax.swing.JFrame;

import Server.Creatures.ServerPlayer;

public class CreatorFrame extends JFrame{

	/**
	 * Constructor
	 */
	public CreatorFrame()
	{
		setPreferredSize(new Dimension(Client.Client.SCREEN_WIDTH, Client.Client.SCREEN_HEIGHT));
		setResizable(false);
		setTitle("WorldQuest Online");
		setLocationRelativeTo(null);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
}
