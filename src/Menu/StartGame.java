package Menu;

import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class StartGame {

	public static void main(String[] args) {
		MainMenu menu = new MainMenu();

	}
	
	public static void restart(JFrame oldFrame)
	{
		oldFrame.dispatchEvent(new WindowEvent(oldFrame,WindowEvent.WINDOW_CLOSING));		
		MainMenu menu = new MainMenu();
	}

}
