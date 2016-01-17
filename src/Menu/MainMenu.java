package Menu;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Client.ClientFrame;

public class MainMenu {

	public static void main(String[] args) {
		ClientFrame mainFrame = new ClientFrame();
		

	}
	
	private class MainPanel extends JPanel
	{
		public MainPanel()
		{
			setDoubleBuffered(true);
			setBackground(Color.white);

			setFocusable(true);
			requestFocusInWindow();
			setSize(Client.Client.SCREEN_WIDTH,Client.Client.SCREEN_HEIGHT);
			
		}
	}

}
