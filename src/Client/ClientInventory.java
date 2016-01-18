package Client;

import java.awt.Color; 
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerPlayer;

@SuppressWarnings("serial")
public class ClientInventory extends JPanel{

	public final static int INVENTORY_WIDTH = 300;
	public final static int WIDTH = 4;
	public final static int HEIGHT = 8;

	private ClientItem[][] inventory = new ClientItem[HEIGHT][WIDTH];
	private ClientItem[] equippedWeapons = new ClientItem[ServerPlayer.MAX_WEAPONS];
	private ClientItem equippedArmour = null;
	private ClientItem equippedShield = null;

	private Client client;

	private JButton mainMenu;

	public ClientInventory(JButton menu)
	{
		setDoubleBuffered(true);
		//setBackground(Color.black);

		setFocusable(true);
		requestFocusInWindow();
		setLayout(null);
		setSize(INVENTORY_WIDTH,Client.SCREEN_HEIGHT);

		if(menu != null)
		{
			mainMenu = menu;
			mainMenu.setSize(225,50);
			mainMenu.setLocation(INVENTORY_WIDTH-250,Client.SCREEN_HEIGHT-100);
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
			for(int col = 0; col < inventory[0].length;col++)
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
			inventory[0][0].setLocation(29,375);
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
		graphics.drawImage(Images.getImage("Inventory.png"),0,0,null);

		//Draw stats
		graphics.setFont(ClientWorld.STATS_FONT);
		if(client.getHP() > 0)
		{
			graphics.setColor(Color.red);
			graphics.fillRect(100,95,(int)(client.getHP()*180.0/client.getMaxHP()),20);
			graphics.setColor(Color.white);
			graphics.drawString(String.format("%d/%d", client.getHP(), client.getMaxHP()), 153,
					110);
		}
		
		graphics.setColor(Color.blue);
		graphics.fillRect(100,135,(int)(client.getMana()*180.0/client.getMaxMana()),20);
		graphics.setColor(Color.white);
		graphics.drawString(String.format("%d/%d",client.getMana(),client.getMaxMana()),153,150);
		
		graphics.setColor(new Color(253,83,83));
		graphics.drawString(String.format("%d(+%d)", client.getDamage()
				+ client.getBaseDamage(), client.getBaseDamage()), 103, 215);
		
		graphics.drawString(String.format("%.0f%%",client.getArmour()*100),115,255);
		
		if (client.getSpeed() == ServerPlayer.MAX_HSPEED)
			graphics.drawString(
					String.format("%d(Max)", client.getSpeed()
							- ServerPlayer.MOVE_SPEED + 1), 240, 215);
		else
			graphics.drawString(
					String.format("%d", client.getSpeed() - ServerPlayer.MOVE_SPEED
							+ 1), 240, 215);

		if (client.getJump() == ServerPlayer.MAX_VSPEED)
			graphics.drawString(
					String.format("%d(Max)", client.getJump()
							- ServerPlayer.JUMP_SPEED + 1), 240, 255);
		else
			graphics.drawString(
					String.format("%d", client.getJump() - ServerPlayer.JUMP_SPEED
							+ 1), 240, 255);
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

	public JButton getMenuButton()
	{
		return mainMenu;
	}


}
