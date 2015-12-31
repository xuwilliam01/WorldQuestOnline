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
		setPreferredSize(new Dimension(CreatorWorld.WIDTH + CreatorItems.WIDTH, CreatorWorld.HEIGHT));
		setResizable(false);
		setTitle("WorldQuest Online");
		setLocationRelativeTo(null);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
}
