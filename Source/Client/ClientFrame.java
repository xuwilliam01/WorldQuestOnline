package Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

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
	 * Constructor for the game frame
	 */
	public ClientFrame(boolean tooLarge, boolean isMac, Point pos)
	{
		this.getContentPane().setBackground(Color.BLACK);
		setPreferredSize(new Dimension(Client.SCREEN_WIDTH
				+ ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT));

		setResizable(false);
		setTitle("WorldQuest Online");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		if(tooLarge && pos != null)
			setLocation(pos.x, pos.y);
		else
			setLocation(0,0);
		setLayout(null);
		if (!tooLarge)
		{
			setUndecorated(true);
		}
		else
		{
			setUndecorated(false);
		}
		pack();
		if (!tooLarge)
		{
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		setVisible(true);
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
