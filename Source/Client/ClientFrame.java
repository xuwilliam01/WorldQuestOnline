package Client;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;

import Imports.Images;


@SuppressWarnings("serial")
/**
 * The frame that the game will be displayed on
 * @author Alex Raita & William Xu
 *
 */
public class ClientFrame extends JFrame
{
	
	/**
	 * Constructor for the game frame
	 */
	public ClientFrame()
	{
	    
	    setBackground(Color.BLACK);
		setPreferredSize(new Dimension(Client.SCREEN_WIDTH+ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT));
		setResizable(false);
		setTitle("WorldQuest Online");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(null);
		pack();
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		setVisible(true);
		
	}
	
	/**
	 * Have the player choose their account
	 */
	private void chooseAccount()
	{
		
		
	}
}
