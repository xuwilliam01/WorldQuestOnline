package Client;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerPlayer;

@SuppressWarnings("serial")
public class ClientInventory extends JPanel{

	public final static int INVENTORY_WIDTH = 300;
	public final static int WIDTH = 5;
	public final static int HEIGHT = 5;

	private ClientItem[][] inventory = new ClientItem[HEIGHT][WIDTH];
	private ClientItem[] equippedWeapons = new ClientItem[ServerPlayer.MAX_WEAPONS];
	private ClientItem equippedArmour = null;
	private ClientItem equippedShield = null;

	private Client client;
	
	private JButton mainMenu;

	public ClientInventory(JButton menu)
	{
		setDoubleBuffered(true);
		setBackground(Color.black);

		setFocusable(true);
		requestFocusInWindow();
		setLayout(null);
		setSize(INVENTORY_WIDTH,Client.SCREEN_HEIGHT);
		
		if(menu != null)
		{
			mainMenu = menu;
			mainMenu.setSize(225,50);
			mainMenu.setLocation(INVENTORY_WIDTH-250,Client.SCREEN_HEIGHT-125);
			add(mainMenu);
		}
	}
	/**
	 * Adds an item to the array given its image
	 * @param image
	 */
	public void addItem(String image, String type, int amount, int cost)
	{
		//If the item is a potion, check if it already exists and if it does, increase the amount
		if(type.charAt(1)==ServerWorld.STACK_TYPE.charAt(1))
			for(int row = 0; row < inventory.length;row++)
				for(int col = 0;col < inventory[row].length;col++)
					if(inventory[row][col] != null && inventory[row][col].getType().equals(type))
					{
						inventory[row][col].increaseAmount(amount);
						return;
					}

		//Find first empty space and add item
		for(int row = 0; row < inventory.length;row++)
			for(int col = 0;col < inventory[row].length;col++)
				if(inventory[row][col] == null)
				{
					inventory[row][col] = new ClientItem(image,type,amount,cost,row,col,this);
					add(inventory[row][col]);
					repaint();
					return;
				}

		//Inventory full if the method does not exit
		//This shouldn't happen
		System.out.println("Full Inventory");
	}


	/**
	 * Gets the amount of money in the inventory
	 */
	public int getMoney()
	{
		for(int row = 0; row < inventory.length;row++)
			for(int col = 0; col < inventory.length;col++)
				if(inventory[row][col] != null && inventory[row][col].getType().equals(ServerWorld.MONEY_TYPE))
					return inventory[row][col].getAmount();
		return 0;
	}

	public void decreaseMoney(int amount)
	{
		for(int row = 0; row < inventory.length;row++)
			for(int col = 0; col < inventory.length;col++)
				if(inventory[row][col] != null && inventory[row][col].getType().equals(ServerWorld.MONEY_TYPE))
				{
					inventory[row][col].decreaseAmount(amount);
					if(inventory[row][col].getAmount() <= 0)
					{
						inventory[row][col].setVisible(false);
						remove(inventory[row][col]);
						invalidate();
						inventory[row][col] = null;	
					}
					else
						inventory[row][col].repaint();
				}
	}

	public void sellItem(ClientItem item, int pos)
	{
		if(item.getAmount() > 1)
		{
			item.decreaseAmount();	
			client.print("S "+item.getType());
		}
		else
		{
			item.setVisible(false);
			remove(item);
			invalidate();
			if(pos == ServerPlayer.DEFAULT_WEAPON_SLOT || !item.isSelected())
				for(int row = 0; row < inventory.length;row++)
					for(int col = 0; col < inventory[row].length;col++)
					{
						if(inventory[row][col] == item)
						{
							inventory[row][col] = null;
							client.print("S "+item.getType());
							repaint();
							return;
						}
					}
		}


	}
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
			if(pos == ServerPlayer.DEFAULT_WEAPON_SLOT || !item.isSelected())
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
			else if(pos == ServerPlayer.DEFAULT_ARMOUR_SLOT)
			{
				equippedArmour = null;
				client.print("DrW "+item.getEquipSlot());
				
			}
			else if(pos == ServerPlayer.DEFAULT_SHIELD_SLOT)
			{
				equippedShield = null;
				client.print("DrW "+item.getEquipSlot());
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
							repaint();
							return;
						}
					client.setWeaponSelected(ServerPlayer.DEFAULT_WEAPON_SLOT);
				}

			}
		}

	}

	public void use(ClientItem item)
	{
		if((item.getType().equals(ServerWorld.HP_POTION_TYPE) && client.getHP() == client.getMaxHP())||(item.getType().equals(ServerWorld.MANA_POTION_TYPE) && client.getMana() == client.getMaxMana())||(item.getType().equals(ServerWorld.SPEED_POTION_TYPE)&& client.getSpeed() == ServerPlayer.MAX_HSPEED) || item.getType().equals(ServerWorld.JUMP_POTION_TYPE)&&(client.getJump() == ServerPlayer.MAX_VSPEED))
			return;
		
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
		ClientItem money = null;
		for(int row = 0; row < inventory.length;row++)
			for(int col = 0;col < inventory[row].length;col++)
				if(inventory[row][col] != null)
				{
					if(inventory[row][col].getType().equals(ServerWorld.MONEY_TYPE))
					{
						money = inventory[row][col];
					}
					else
						remove(inventory[row][col]);
				}
		
		inventory = new ClientItem[HEIGHT][WIDTH];
		if(money != null)
		{
			inventory[0][0] = money;
			inventory[0][0].setLocation(25,50);
			inventory[0][0].setRow(0);
			inventory[0][0].setCol(0);
		}
		
		for(int weapon = 0; weapon < equippedWeapons.length;weapon++)
			if(equippedWeapons[weapon] != null)
				remove(equippedWeapons[weapon]);
		equippedWeapons = new ClientItem[ServerPlayer.MAX_WEAPONS];
		
		if(equippedArmour != null)
			remove(equippedArmour);
		equippedArmour= null;
		
		
		invalidate();
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
		graphics.drawString("Armour",10,600);
	}

	public ClientItem[] getEquippedWeapons() {
		return equippedWeapons;
	}

	public void setEquippedWeapons(ClientItem[] equippedWeapons) {
		this.equippedWeapons = equippedWeapons;

		// Make the border show up around the first weapon
		if (equippedWeapons[0]!=null)
		{
			equippedWeapons[0].setBorder(BorderFactory.createLineBorder(Color.white));
			client.setWeaponSelected(0);
		}
	}
	public ClientItem[][] getInventory() {
		return inventory;
	}
	public void setInventory(ClientItem[][] inventory) {
		this.inventory = inventory;
	}
	public ClientItem getEquippedArmour() {
		return equippedArmour;
	}
	public void setEquippedArmour(ClientItem equippedArmour) {
		this.equippedArmour = equippedArmour;
	}
	public ClientItem getEquippedShield() {
		return equippedShield;
	}
	public void setEquippedShield(ClientItem equippedShield) {
		this.equippedShield = equippedShield;
	}
	
	


}
