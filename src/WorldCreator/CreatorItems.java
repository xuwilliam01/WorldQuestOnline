package WorldCreator;

import java.awt.Color;

import javax.swing.JPanel;

public class CreatorItems extends JPanel {

	public static final int WIDTH = 300;
	
	public CreatorItems()
	{
		setDoubleBuffered(true);
		setBackground(Color.red);

	//	setFocusable(true);
	//	requestFocusInWindow();
		setSize(WIDTH,CreatorWorld.HEIGHT);
	}
}
