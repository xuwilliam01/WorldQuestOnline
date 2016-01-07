package Client;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ClientInventory extends JPanel{

	public final static int INVENTORY_WIDTH = 300;
	public final static int WIDTH = 5;
	public final static int HEIGHT = 10;
	
	private ClientItem[][] items = new ClientItem[HEIGHT][WIDTH];
	private Client client;

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
	public void addItem(String image, String type)
	{
		//Find first empty space
		for(int row = 0; row < items.length;row++)
			for(int col = 0;col < items[row].length;col++)
				if(items[row][col] == null)
				{
					items[row][col] = new ClientItem(image,type,row,col,this);
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
		remove(items[row][col]);
		items[row][col] = null;
	}

	public void removeItem(ClientItem item)
	{
		client.print("Dr "+item.getType());
		item.setVisible(false);
		remove(item);
		invalidate();
		for(int row = 0; row < items.length;row++)
			for(int col = 0; col < items[row].length;col++)
				if(items[row][col] == item)
				{
					items[row][col] = null;
					return;
				}
		repaint();
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

	public void setClient(Client client)
	{
		this.client = client;
	}
	
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		graphics.setColor(Color.RED);
		graphics.drawString("Inventory", 120, 20);
	}
}
