package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerVendor;

public class ClientShop extends JPanel{

	public final static int SHOP_WIDTH = 560;
	public final static int SHOP_HEIGHT = 440;
	public final static int WIDTH = 10;
	public final static int HEIGHT = 6;

	private ClientShopItem[][] shopItems = new ClientShopItem[HEIGHT][WIDTH];
	private Client client;
	private Image coinImage = Images.getImage("COIN.png");
	private Image shopImage = Images.getImage("Shop.png");

	public ClientShop(Client client)
	{
		this.client = client;
		setDoubleBuffered(true);
		setBackground(Color.black);

		setFocusable(true);
		requestFocusInWindow();
		setLayout(null);
		setSize(SHOP_WIDTH, SHOP_HEIGHT);
		setLocation(1000,50);
	}

	public void addItem(String imageName, String type, int amount, int cost)
	{
		if(type.charAt(1)==ServerWorld.STACK_TYPE.charAt(1))
			for(int row = 0; row < shopItems.length;row++)
				for(int col = 0;col < shopItems[row].length;col++)
					if(shopItems[row][col] != null && shopItems[row][col].getType().equals(type))
					{
						shopItems[row][col].increaseAmount(amount);
						return;
					}

		for(int row = 0; row < shopItems.length;row++)
			for(int col = 0; col < shopItems[0].length;col++)
				if(shopItems[row][col] == null)
				{
					shopItems[row][col]= new ClientShopItem(imageName,type,amount,cost,row,col,this);
					add(shopItems[row][col]);
					repaint();
					return;
				}


	}

	public void removeItem(ClientShopItem item)
	{

		item.setVisible(false);
		remove(item);
		invalidate();
		shopItems[item.getRow()][item.getCol()] = null;

	}

	public Client getClient()
	{
		return client;
	}

	public boolean isFull(String type)
	{
		for(int row = 0; row < shopItems.length;row++)
			for(int col = 0; col < shopItems[0].length;col++)
				if(shopItems[row][col] == null)
					return false;
				else if(shopItems[row][col].getType().equals(type) && type.charAt(1) == ServerWorld.STACK_TYPE.charAt(1))
					return false;
		return true;
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		graphics.drawImage(shopImage, 0, 0, null);

		//For each item write the price under it
		graphics.setColor(new Color(218,165,32));
		for(int row = 0; row < shopItems.length;row++)
			for(int col = 0; col < shopItems[0].length;col++)
			{
				if(shopItems[row][col] != null)
				{
					if(shopItems[row][col].getCost() > 9)
						graphics.drawString(shopItems[row][col].getCost()+"", shopItems[row][col].getX(), shopItems[row][col].getY()+15+Images.INVENTORY_IMAGE_SIDELENGTH);
					else
						graphics.drawString(shopItems[row][col].getCost()+"", shopItems[row][col].getX()+3, shopItems[row][col].getY()+15+Images.INVENTORY_IMAGE_SIDELENGTH);
					graphics.drawImage(coinImage, shopItems[row][col].getX()+18, shopItems[row][col].getY()+5+Images.INVENTORY_IMAGE_SIDELENGTH,this);
				}
			}
	}
}
