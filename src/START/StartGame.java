package START;


import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import Menu.MainMenu;

public class StartGame {

	public static void main(String[] args) {
		MainMenu menu = new MainMenu();

	}
	
	public static void restart(JFrame oldFrame)
	{
		//oldFrame.dispatchEvent(new WindowEvent(oldFrame,WindowEvent.WINDOW_CLOSING));		
		oldFrame.setVisible(false);
		oldFrame.dispose();
		MainMenu menu = new MainMenu();
	}

}
