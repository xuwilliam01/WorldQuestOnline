package Client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerVendor;

/**
 * The shop for the client. Works very similar to the inventory
 * @author Alex Raita & William Xu
 *
 */
public class ClientShop extends JPanel{

	public final static int SHOP_WIDTH = 560;
	public final static int SHOP_HEIGHT = 875;
	public final static int WIDTH = 10;
	public final static int HEIGHT = 15;

	private ClientShopItem[][] shopItems = new ClientShopItem[HEIGHT][WIDTH];
	private Client client;
	private Image coinImage = Images.getImage("COIN");
	private Image shopImage = Images.getImage("Shop");
	
	private Font shopFont;

	/**
	 * Constructor
	 * @param client the client that is using the shop
	 */
	public ClientShop(Client client)
	{
		this.client = client;
		setDoubleBuffered(true);
		setBackground(Color.black);

		setFocusable(true);
		requestFocusInWindow();
		setLayout(null);
		setSize(ClientFrame.getScaledWidth(SHOP_WIDTH), ClientFrame.getScaledHeight(SHOP_HEIGHT));
		setLocation(Client.SCREEN_WIDTH - SHOP_WIDTH - 50,25);
		
		shopFont = new Font("Courier",Font.PLAIN,ClientFrame.getScaledWidth(16));
	}

	/**
	 * Add an item to the shop
	 * @param imageName
	 * @param type
	 * @param amount
	 * @param cost
	 */
	public void addItem(String imageName, String type, int amount, int cost)
	{
		//Check if it can be stacked
		if(type.charAt(1)==ServerWorld.STACK_TYPE.charAt(1))
			for(int row = 0; row < shopItems.length;row++)
				for(int col = 0;col < shopItems[row].length;col++)
					if(shopItems[row][col] != null && shopItems[row][col].getType().equals(type))
					{
						shopItems[row][col].increaseAmount(amount);
						return;
					}

		//Add it to the shop
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

	/**
	 * Remove an item from the shop
	 * @param item the item to be removed
	 */
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

	/**
	 * Checks if the shop is full given an object to be added
	 * @param type the object that wants to be added
	 * @return whether the shop is full or not
	 */
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

	/**
	 * Paint the number of items and the cost of each item
	 */
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		graphics.drawImage(shopImage, 0, 0, null);

		//For each item write the price under it
		graphics.setColor(new Color(218,165,32));
		
		graphics.setFont(shopFont);
		
		for(int row = 0; row < shopItems.length;row++)
			for(int col = 0; col < shopItems[0].length;col++)
			{
				if(shopItems[row][col] != null)
				{
					if(shopItems[row][col].getCost() > 9)
						graphics.drawString(shopItems[row][col].getCost()+"", shopItems[row][col].getX(), shopItems[row][col].getY()+ClientFrame.getScaledHeight(15+Images.INVENTORY_IMAGE_SIDELENGTH));
					else
						graphics.drawString(shopItems[row][col].getCost()+"", shopItems[row][col].getX()+ClientFrame.getScaledWidth(7), shopItems[row][col].getY()+ClientFrame.getScaledHeight(15+Images.INVENTORY_IMAGE_SIDELENGTH));
					graphics.drawImage(coinImage, shopItems[row][col].getX()+ClientFrame.getScaledWidth(18),  shopItems[row][col].getY()+ClientFrame.getScaledHeight(5+Images.INVENTORY_IMAGE_SIDELENGTH),this);
				}
			}
	}
	
	
}
