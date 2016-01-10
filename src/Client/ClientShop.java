package Client;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import Server.Creatures.ServerVendor;

public class ClientShop extends JPanel{

	public final static int SHOP_WIDTH = 700;
	public final static int SHOP_HEIGHT = 400;

	private ClientShopItem[] shopItems = new ClientShopItem[ServerVendor.MAX_INVENTORY];
	private Client client;

	public ClientShop(Client client)
	{
		this.client = client;
		setDoubleBuffered(true);
		setBackground(Color.black);

		setFocusable(true);
		requestFocusInWindow();
		setLayout(null);
		setSize(SHOP_WIDTH, SHOP_HEIGHT);
		setLocation(100,100);
	}

	public void addItem(String imageName, String type, int amount, int cost)
	{
		for(int spot = 0; spot < shopItems.length;spot++)
			if(shopItems[spot] == null)
			{
				shopItems[spot] = new ClientShopItem(imageName,type,amount,cost,spot,this);
				add(shopItems[spot]);
				repaint();
				return;
			}


	}

	public void removeItem(ClientShopItem item)
	{

		item.setVisible(false);
		remove(item);
		invalidate();
		shopItems[item.getPos()] = null;

	}

	public Client getClient()
	{
		return client;
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		graphics.setColor(Color.RED);
		graphics.drawString("Shop", 300, 20);
		
		//For each item write the price under it
		for(int item = 0; item < shopItems.length;item++)
		{
			///if(shopItems[item] != null)
		}
	}
}
