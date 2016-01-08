package Client;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import Server.ServerWorld;
import Server.Creatures.ServerPlayer;

@SuppressWarnings("serial")
public class ClientInventory extends JPanel{

	public final static int INVENTORY_WIDTH = 300;
	public final static int WIDTH = 5;
	public final static int HEIGHT = 5;

	private ClientItem[][] inventory = new ClientItem[HEIGHT][WIDTH];
	private ClientItem[] equippedWeapons = new ClientItem[ServerPlayer.MAX_WEAPONS];

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
	public void addItem(String image, String type, int amount)
	{
		//If the item is a potion, check if it already exists and if it does, increase the amount
		if(type.charAt(1)==ServerWorld.STACK_TYPE.charAt(1))
			for(int row = 0; row < inventory.length;row++)
				for(int col = 0;col < inventory[row].length;col++)
					if(inventory[row][col] != null && inventory[row][col].getType().equals(type))
					{
						inventory[row][col].increaseAmount();
						return;
					}

		//Find first empty space and add item
		for(int row = 0; row < inventory.length;row++)
			for(int col = 0;col < inventory[row].length;col++)
				if(inventory[row][col] == null)
				{
					inventory[row][col] = new ClientItem(image,type,amount,row,col,this);
					add(inventory[row][col]);
					repaint();
					return;
				}

		//Inventory full if the method does not exit
		//This shouldn't happen
		System.out.println("Full Inventory");
	}

	//	public void removeItem(int row, int col)
	//	{
	//		remove(items[row][col]);
	//		items[row][col] = null;
	//	}

	public void removeItem(ClientItem item, int pos)
	{
		if(item.getAmount()  > 1)
		{
			item.decreaseAmount();
			client.print("DrI "+item.getType());
		}
		else
		{
			item.setVisible(false);
			remove(item);
			invalidate();
			if(pos == -1)
				for(int row = 0; row < inventory.length;row++)
					for(int col = 0; col < inventory[row].length;col++)
					{
						if(inventory[row][col] == item)
						{
							inventory[row][col] = null;
							client.print("DrI "+item.getType());
							repaint();
							return;
						}
					}
			else
			{
				equippedWeapons[pos] = null;
				client.print("DrW "+item.getEquipSlot());
				
				//If we dropped the weapon we selected, select a new weapon
				if(client.getWeaponSelected() == pos)
				{
					for(int spot = 0; spot < equippedWeapons.length;spot++)
						if(equippedWeapons[spot] != null)
						{
							equippedWeapons[spot].setBorder(BorderFactory.createLineBorder(Color.white));
							client.setWeaponSelected(spot);
						}
				}
				
			}
		}
		repaint();
	}

	public void use(ClientItem item)
	{
		if(item.getAmount()  > 1)
		{
			item.decreaseAmount();
			client.print("DrU "+item.getType());
		}
		else
		{
			item.setVisible(false);
			remove(item);
			invalidate();

			for(int row = 0; row < inventory.length;row++)
				for(int col = 0; col < inventory[row].length;col++)
				{
					if(inventory[row][col] == item)
					{
						inventory[row][col] = null;
						client.print("DrU "+item.getType());
						repaint();
						return;
					}
				}
		}
	}

	public void clear()
	{
		for(int row = 0; row < inventory.length;row++)
			for(int col = 0;col < inventory[row].length;col++)
				if(inventory[row][col] != null)
					remove(inventory[row][col]);
		invalidate();
		inventory = new ClientItem[HEIGHT][WIDTH];
		repaint();
	}


	public Client getClient()
	{
		return client;
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
		graphics.drawString("Equipped Items", 115, 480);
		graphics.drawString("Weapons",10,520);
	}

	public ClientItem[] getEquippedWeapons() {
		return equippedWeapons;
	}

	public void setEquippedWeapons(ClientItem[] equippedWeapons) {
		this.equippedWeapons = equippedWeapons;
	}
	public ClientItem[][] getInventory() {
		return inventory;
	}
	public void setInventory(ClientItem[][] inventory) {
		this.inventory = inventory;
	}


}
