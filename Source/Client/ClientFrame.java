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
	public ClientFrame(boolean tooLarge)
	{

		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(Client.SCREEN_WIDTH
				+ ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT));

		setResizable(false);
		setTitle("WorldQuest Online");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(null);
		setUndecorated(false);
		if (!tooLarge)
		{
			setUndecorated(true);
		}
		pack();
		if (!tooLarge)
		{
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		setVisible(true);

	}

	/**
	 * Have the player choose their account
	 */
	private void chooseAccount()
	{

	}

	/**
	 * Scale a number based on the screen width
	 * @param number
	 * @return
	 */
	public static int getScaledWidth(int number)
	{
		return (int) (number * (Client.SCREEN_WIDTH / (1920.0 - ClientInventory.INVENTORY_WIDTH)));
	}

	/**
	 * Scale a number based on the screen height
	 * @param number
	 * @return
	 */
	public static int getScaledHeight(int number)
	{
		return (int) (number * (Client.SCREEN_HEIGHT / 1080.0));
	}
}
