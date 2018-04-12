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

	static MainMenu menu;
	
	/**
	 * Start the game
	 * @param args
	 */
	public static void main(String[] args) {
		menu = new MainMenu(null, false);

	}
	
	/**
	 * Restarts the game on a new frame
	 * @param oldFrame the frame that should be closed
	 */
	public static void restart(JFrame oldFrame, boolean openServerList)
	{	
		Point pos = oldFrame.getLocationOnScreen();
		oldFrame.setVisible(false);
		oldFrame.dispose();
		oldFrame.removeAll();
		menu = new MainMenu(pos, openServerList);
	}
}
