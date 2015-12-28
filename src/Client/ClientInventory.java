package Client;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class ClientInventory extends JPanel{

	public final static int INVENTORY_WIDTH = 300;
	public final static int WIDTH = 5;
	public final static int HEIGHT = 10;
	ClientItem[][] items = new ClientItem[HEIGHT][WIDTH];

	public ClientInventory()
	{
		setDoubleBuffered(true);
		setBackground(Color.black);

		setFocusable(true);
		requestFocusInWindow();
		setLayout(null);
		setSize(INVENTORY_WIDTH,Client.SCREEN_HEIGHT);
	}
	/**
	 * Adds an item to the array given its image
	 * @param image
	 */
	public void addItem(String image)
	{
		//Find first empty space
		for(int row = 0; row < items.length;row++)
			for(int col = 0;col < items[row].length;col++)
				if(items[row][col] == null)
				{
					items[row][col] = new ClientItem(image,row,col);
					add(items[row][col]);
					repaint();
					return;
				}
		//Inventory full if the method does not exit
		//This shouldn't happen
		System.out.println("Full Inventory");
	}

	public void removeItem(int row, int col)
	{
		items[row][col] = null;
	}

	public void clear()
	{
		for(int row = 0; row < items.length;row++)
			for(int col = 0;col < items[row].length;col++)
				if(items[row][col] != null)
					remove(items[row][col]);
		invalidate();
		items = new ClientItem[HEIGHT][WIDTH];
		repaint();
	}
	
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		graphics.setColor(Color.RED);
		graphics.drawString("Inventory", 120, 20);
	}
}
