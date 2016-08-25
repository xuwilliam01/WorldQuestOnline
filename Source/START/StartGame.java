package START;
import javax.swing.JFrame;
import Menu.MainMenu;

/**
 * Starts the game
 * @author Alex Raita & William Xu
 *
 */
public class StartGame {

	/**
	 * Start the game
	 * @param args
	 */
	public static void main(String[] args) {
		MainMenu menu = new MainMenu();

	}
	
	/**
	 * Restarts the game on a new frame
	 * @param oldFrame the frame that should be closed
	 */
	public static void restart(JFrame oldFrame)
	{	
		oldFrame.setVisible(false);
		oldFrame.dispose();
		oldFrame.removeAll();
		MainMenu menu = new MainMenu();
	}
}
