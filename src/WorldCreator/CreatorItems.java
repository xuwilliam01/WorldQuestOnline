package WorldCreator;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class CreatorItems extends JPanel {

	public static final int WIDTH = 300;
	public static final int NUM_ROWS = 1;
	private static final int NUM_COLS = 2;
	
	private CreatorObject[] buttons;
	
	public CreatorItems(CreatorObject[] buttons)
	{
		setDoubleBuffered(true);
		setBackground(Color.red);

		setFocusable(true);
		requestFocusInWindow();
		setSize(WIDTH,CreatorWorld.HEIGHT);
		setLayout(null);
		
		this.buttons = buttons;
		addButtons();
	}
	
	public void addButtons()
	{
		int row = 0;
		int col = 0;
		
		for(int button = 0; button < buttons.length;button++)
			if(buttons[button] != null)
			{
				buttons[button].setPosition(row, col++ % NUM_COLS);
				row = col/NUM_COLS;
				
				add(buttons[button]);
			}
		repaint();
	}
	
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		graphics.drawString("TILES", 140, 20);
		
	}
}
