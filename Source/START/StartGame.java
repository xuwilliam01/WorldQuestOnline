package START;
import java.awt.Point;

import javax.swing.JFrame;
import Menu.MainMenu;

/**
 * Starts the game
 * @author Alex Raita & William Xu
 * hi
 *
 */
public class StartGame {

	/**
	 * Start the game
	 * @param args
	 */
	public static void main(String[] args) {
		MainMenu menu = new MainMenu(null);

	}
	
	/**
	 * Restarts the game on a new frame
	 * @param oldFrame the frame that should be closed
	 */
	public static void restart(JFrame oldFrame)
	{	
		Point pos = oldFrame.getLocationOnScreen();
		oldFrame.setVisible(false);
		oldFrame.dispose();
		oldFrame.removeAll();
		MainMenu menu = new MainMenu(pos);
	}
}
