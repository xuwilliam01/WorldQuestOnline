package START;


import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import Menu.MainMenu;

/**
 * Starts the game
 * @author Alex Raita & William Xu
 *
 */
public class StartGame {

	public static void main(String[] args) {
		MainMenu menu = new MainMenu();

	}
	
	/**
	 * Restarts the game on a new frame
	 * @param oldFrame the frame that should be closed
	 */
	public static void restart(JFrame oldFrame)
	{
		//oldFrame.dispatchEvent(new WindowEvent(oldFrame,WindowEvent.WINDOW_CLOSING));		
		oldFrame.setVisible(false);
		oldFrame.dispose();
		MainMenu menu = new MainMenu();
	}

}
