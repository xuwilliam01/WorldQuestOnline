package Client;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;


public class Frame extends JFrame
{

	/**
	 * Constructor for the game frame
	 */
	public Frame()
	{
		
		setPreferredSize(new Dimension(Client.SCREEN_WIDTH, Client.SCREEN_HEIGHT));
		setResizable(false);
		setTitle("WorldQuest Online");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		pack();
		
	}
}
