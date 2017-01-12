package Client;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerPlayer;

@SuppressWarnings("serial")
/**
 * Stores and displays the inventory for the client
 * @author Alex Raita & William Xu
 *
 */
public class ClientInventory extends JPanel implements ActionListener
{

	// Pixel width, and inventory dimensions for capacity
	public static int INVENTORY_WIDTH = 300;
	public final static int WIDTH = 4;
	public final static int HEIGHT = 8;
	public final static Color RED = new Color(253, 83, 83);

	private ClientItem[][] inventory = new ClientItem[HEIGHT][WIDTH];

	/**
	 * Image for the inventory
	 */
	private Image inventoryImage;

	private ClientItem[] equippedWeapons = new ClientItem[ServerPlayer.MAX_WEAPONS];
	private ClientItem equippedArmour = null;

	private Client client;

	private JButton mainMenu;
	private JButton switchTeams;

	private ArrayList<ClientItem> removeList = new ArrayList<ClientItem>();

	private Font inventoryFont;

	/**
	 * Constructor
	 * 
	 * @param menu the button that will take the player back to the main menu
	 */
	public ClientInventory(JButton menu)
	{
		ToolTipManager.sharedInstance().setInitialDelay(100);
		setDoubleBuffered(true);
		setBackground(Color.BLACK);
		setFocusable(true);
		requestFocusInWindow();
		setLayout(null);
		setSize(INVENTORY_WIDTH, Client.SCREEN_HEIGHT);

		if (menu != null)
		{
			mainMenu = menu;
			mainMenu.setSize(ClientFrame.getScaledWidth(70), ClientFrame.getScaledHeight(50));
			mainMenu.setLocation(ClientFrame.getScaledWidth(215), ClientFrame.getScaledHeight(16));
			mainMenu.setBackground(new Color(240, 240, 240));
			mainMenu.setForeground(Color.black);
			add(mainMenu);
		}

		inventoryImage = Images.getImage("Inventory");
		
		try {
			inventoryFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("Catamaran-Light.ttf"));
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("font not found");
			e.printStackTrace();
		}
		inventoryFont = inventoryFont.deriveFont(18f);
		
		//inventoryFont = new Font("Courier", Font.PLAIN, ClientFrame.getScaledWidth(15));

	}

	/**
	 * Adds an item to the array given its image
	 * 
	 * @param image
	 */
	public void addItem(String image, String type, int amount, int cost)
	{
		// If the item is a potion, check if it already exists and if it does,
		// increase the amount
		if (type.charAt(1) == ServerWorld.STACK_TYPE.charAt(1))
			for (int row = 0; row < inventory.length; row++)
				for (int col = 0; col < inventory[row].length; col++)
					if (inventory[row][col] != null
					&& inventory[row][col].getType().equals(type))
					{
						inventory[row][col].increaseAmount(amount);
						return;
					}

		// Find first empty space and add item
		for (int row = 0; row < inventory.length; row++)
			for (int col = 0; col < inventory[row].length; col++)
				if (inventory[row][col] == null)
				{
					inventory[row][col] = new ClientItem(image, type, amount,
							cost, row, col, this);
					add(inventory[row][col]);
					repaint();
					return;
				}

		// Inventory full if the method does not exit
		// This shouldn't happen
		System.out.println("Full Inventory");
	}

	/**
	 * Gets the amount of money in the inventory
	 */
	public int getMoney()
	{
		for (int row = 0; row < inventory.length; row++)
			for (int col = 0; col < inventory[0].length; col++)
				if (inventory[row][col] != null
				&& inventory[row][col].getType().equals(
						ServerWorld.MONEY_TYPE))
					return inventory[row][col].getAmount();
		return 0;
	}

	/**
	 * Decrease the amount of money in the inventory
	 * 
	 * @param amount the amount to decrease money by
	 */
	public void decreaseMoney(int amount)
	{
		for (int row = 0; row < inventory.length; row++)
			for (int col = 0; col < inventory[0].length; col++)
				if (inventory[row][col] != null
				&& inventory[row][col].getType().equals(
						ServerWorld.MONEY_TYPE))
				{
					inventory[row][col].decreaseAmount(amount);
					// If we have no money, remove the button
					if (inventory[row][col].getAmount() <= 0)
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

	/**
	 * Sell a given item
	 * 
	 * @param item the item to be sold
	 * @param pos where the item is located
	 */
	public void sellItem(ClientItem item, int pos)
	{
		// Tell the server you are selling the item
		if (pos == ServerPlayer.DEFAULT_WEAPON_SLOT || !item.isSelected())
		{
			client.printToServer("S " + item.getType());
			if (!removeList.contains(item))
				removeList.add(item);
		}
	}

	/**
	 * When an item is confirmed to be sold, get rid of it
	 */
	public void removeThis(String type)
	{
		ClientItem toRemove = null;
		for (ClientItem item : removeList)
		{
			if (item.getType().equals(type))
			{
				toRemove = item;
				if (item.getAmount() > 1)
				{
					item.decreaseAmount();
				}
				else
				{
					item.setVisible(false);
					remove(item);
					invalidate();
					for (int row = 0; row < inventory.length; row++)
						for (int col = 0; col < inventory[row].length; col++)
						{
							if (inventory[row][col] == item)
							{
								inventory[row][col] = null;
								repaint();
								return;
							}
						}
				}
			}
		}
		removeList.remove(toRemove);
	}

	/**
	 * Removes an item from the inventory
	 * 
	 * @param item the item to be removed
	 * @param pos where the item is located
	 */
	public void removeItem(ClientItem item, int pos)
	{
		if(client.getHP() <= 0)
			return;

		// Drop 1 item at a time and tell the server
		if (item.getAmount() > 1)
		{
			item.decreaseAmount();
			client.printToServer("Dr I " + item.getType());
		}
		else
		{
			// Remove the item completely
			item.setVisible(false);
			remove(item);
			invalidate();
			// If it is in the inventory
			if (pos == ServerPlayer.DEFAULT_WEAPON_SLOT || !item.isSelected())
				for (int row = 0; row < inventory.length; row++)
					for (int col = 0; col < inventory[row].length; col++)
					{
						if (inventory[row][col] == item)
						{
							inventory[row][col] = null;
							client.printToServer("Dr I " + item.getType());
							repaint();
							return;
						}
					}
			// If it's armour
			else if (pos == ServerPlayer.DEFAULT_ARMOUR_SLOT)
			{
				equippedArmour = null;
				client.printToServer("Dr W " + item.getEquipSlot());

			}
			// If it's a weapon
			else
			{
				equippedWeapons[pos] = null;
				if(!item.getType().contains(ServerWorld.BUILDING_ITEM_TYPE))
					client.printToServer("Dr W " + item.getEquipSlot());

				// If we dropped the weapon we selected, select a new weapon
				if (client.getWeaponSelected() == pos)
				{
					for (int spot = 0; spot < equippedWeapons.length; spot++)
						if (equippedWeapons[spot] != null)
						{
							equippedWeapons[spot].setBorder(BorderFactory
									.createLineBorder(Color.white));
							client.setWeaponSelected(spot);
							repaint();
							return;
						}
					client.setWeaponSelected(ServerPlayer.DEFAULT_WEAPON_SLOT);
				}

			}
		}

	}

	/**
	 * Use an item (potion)
	 * 
	 * @param item the item to be used
	 */
	public void use(ClientItem item)
	{
		// If we are trying to use a potion that we cannot use, return
		if ((item.getType().equals(ServerWorld.HP_POTION_TYPE) && client
				.getHP() == client.getMaxHP())
				|| (item.getType().equals(ServerWorld.MANA_POTION_TYPE) && client
						.getMana() == client.getMaxMana())
				|| (item.getType().equals(ServerWorld.SPEED_POTION_TYPE) && client
						.getSpeed() == ServerPlayer.MAX_HSPEED)
				|| (item.getType().equals(ServerWorld.JUMP_POTION_TYPE) && client
						.getJump() == ServerPlayer.MAX_VSPEED)
				|| (item.getType().equals(ServerWorld.DMG_POTION_TYPE) && client
						.getBaseDamage() == ServerPlayer.MAX_DMGADD)
				|| (item.getType().equals(ServerWorld.MAX_HP_TYPE) && client
						.getMaxHP() == ServerPlayer.PLAYER_MAX_HP)
				|| (item.getType().equals(ServerWorld.MAX_MANA_TYPE) && client
						.getMaxMana() == ServerPlayer.PLAYER_MAX_MANA))
			return;

		// Decrease the amonut of the potion
		if (item.getAmount() > 1)
		{
			item.decreaseAmount();
			client.printToServer("Dr U " + item.getType());
		}
		else
		{
			// Remove it from the inventory
			item.setVisible(false);
			remove(item);
			invalidate();

			for (int row = 0; row < inventory.length; row++)
				for (int col = 0; col < inventory[row].length; col++)
				{
					if (inventory[row][col] == item)
					{
						inventory[row][col] = null;
						client.printToServer("Dr U " + item.getType());
						repaint();
						return;
					}
				}
		}
	}

	/**
	 * Clear the inventory except for the money
	 */
	public void clear()
	{
		ClientItem money = null;
		ClientItem bestWeapon = null;
		ClientItem bestArmour = null;
		for (int row = 0; row < inventory.length; row++)
			for (int col = 0; col < inventory[row].length; col++)
				if (inventory[row][col] != null)
				{
					if (inventory[row][col].getType().equals(
							ServerWorld.MONEY_TYPE))
					{
						money = inventory[row][col];
					}
					else if(inventory[row][col].getType().contains(
							ServerWorld.WEAPON_TYPE))
					{
						if(bestWeapon == null)
							bestWeapon = inventory[row][col];
						else if(inventory[row][col].getCost() > bestWeapon.getCost())
						{
							remove(bestWeapon);
							bestWeapon = inventory[row][col];
						}
						else remove(inventory[row][col]);
					}
					else if(inventory[row][col].getType().contains(
							ServerWorld.ARMOUR_TYPE))
					{
						if(bestArmour == null)
							bestArmour = inventory[row][col];
						else if(inventory[row][col].getCost() > bestArmour.getCost())
						{
							remove(bestArmour);
							bestArmour = inventory[row][col];
						}
						else remove(inventory[row][col]);
					}
					else
						remove(inventory[row][col]);
				}

		inventory = new ClientItem[HEIGHT][WIDTH];
		// Reset the money
		if (money != null)
		{
			inventory[0][0] = money;
			inventory[0][0].setLocation(ClientFrame.getScaledWidth(29), ClientFrame.getScaledHeight(375));
			inventory[0][0].setRow(0);
			inventory[0][0].setCol(0);
		}

		// Remove all weapons
		for (int weapon = 0; weapon < equippedWeapons.length; weapon++)
			if (equippedWeapons[weapon] != null)
			{
				if(bestWeapon == null)
				{
					bestWeapon = equippedWeapons[weapon];
				}
				else if(equippedWeapons[weapon].getCost() > bestWeapon.getCost())
				{
					remove(bestWeapon);
					bestWeapon = equippedWeapons[weapon];
				}
				else remove(equippedWeapons[weapon]);
			}
		equippedWeapons = new ClientItem[ServerPlayer.MAX_WEAPONS];

		if (equippedArmour != null)
		{
			if(bestArmour == null)
			{
				bestArmour = equippedArmour;
			}
			else if(equippedArmour.getCost() > bestArmour.getCost())
			{
				remove(bestArmour);
				bestArmour = equippedArmour;
			}
			else remove(equippedArmour);
		}
		equippedArmour = null;

		// Reset armour and weapon
		if (bestWeapon != null)
		{
			inventory[0][1] = bestWeapon;
			bestWeapon.setSelected(false);
			bestWeapon.setBorder(BorderFactory.createEmptyBorder());
			inventory[0][1].setLocation(ClientFrame.getScaledWidth(1*Images.INVENTORY_IMAGE_SIDELENGTH+(2)*29 + 5), ClientFrame.getScaledHeight(375));
			inventory[0][1].setRow(0);
			inventory[0][1].setCol(1);
		}
		if (bestArmour != null)
		{
			inventory[0][2] = bestArmour;
			bestArmour.setSelected(false);
			bestArmour.setBorder(BorderFactory.createEmptyBorder());
			inventory[0][2].setLocation(ClientFrame.getScaledWidth(2*Images.INVENTORY_IMAGE_SIDELENGTH+(3)*29 + 5), ClientFrame.getScaledHeight(375));
			inventory[0][2].setRow(0);
			inventory[0][2].setCol(2);
		}

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

	/**
	 * Paint the inventory
	 */
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		// Background
		graphics.drawImage(inventoryImage, 0, 0, null);

		
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		
		// Draw stats
		g2d.setFont(inventoryFont);

		if (client.getHP() > 0)
		{
			g2d.setColor(new Color(210,37,114));
			g2d.fillRect(
					ClientFrame.getScaledWidth(95),
					ClientFrame.getScaledHeight(95),
					(int) (client.getHP() * ClientFrame.getScaledWidth(170) * 1.0 / client
							.getMaxHP()), ClientFrame.getScaledHeight(20));
			g2d.setColor(Color.white);
			if (client.getMaxHP() == ServerPlayer.PLAYER_MAX_HP)
				g2d.setColor(Color.green);
			g2d.drawString(
					String.format("%d/%d", client.getHP(), client.getMaxHP()),
					ClientFrame.getScaledWidth(153), ClientFrame.getScaledHeight(110));
		}

		g2d.setColor(new Color(35,162,199));
		g2d.fillRect(ClientFrame.getScaledWidth(95), ClientFrame.getScaledHeight(135),
				(int) (client.getMana() * ClientFrame.getScaledWidth(170) * 1.0 / client
						.getMaxMana()), ClientFrame.getScaledHeight(20));
		g2d.setColor(Color.white);
		if (client.getMaxMana() == ServerPlayer.PLAYER_MAX_MANA)
			g2d.setColor(Color.green);
		g2d.drawString(
				String.format("%d/%d", client.getMana(), client.getMaxMana()),
				ClientFrame.getScaledWidth(153), ClientFrame.getScaledHeight(150));

		g2d.setColor(RED);
		g2d.drawString(String.format("%.0f%%", client.getArmour() * 100),
				ClientFrame.getScaledWidth(105), ClientFrame.getScaledHeight(254));

		if (client.getBaseDamage() == ServerPlayer.MAX_DMGADD)
			g2d.setColor(Color.green);
		if (client.getBaseDamage() > 9)
			g2d.drawString(
					String.format(
							"%d(+%d%%)",
							(int) Math.ceil(client.getDamage()
									* (1 + client.getBaseDamage() / 100.0)),
							client.getBaseDamage()),
							ClientFrame.getScaledWidth(105),
							ClientFrame.getScaledHeight(219));
		else
			g2d.drawString(
					String.format(
							"%d(+%d%%)",
							(int) Math.ceil(client.getDamage()
									* (1 + client.getBaseDamage() / 100.0)),
							client.getBaseDamage()),
							ClientFrame.getScaledWidth(105),
							ClientFrame.getScaledHeight(219));

		g2d.setColor(RED);
		if (client.getSpeed() == ServerPlayer.MAX_HSPEED)
			g2d.setColor(Color.green);				
		g2d.drawString(
				String.format("%d", client.getSpeed() - ServerPlayer.DEFAULT_MOVE_SPEED
						+ 1), ClientFrame.getScaledWidth(225), ClientFrame.getScaledHeight(219));

		g2d.setColor(RED);
		if (client.getJump() == ServerPlayer.MAX_VSPEED)
			g2d.setColor(Color.green);		
		g2d.drawString(
				String.format("%d", client.getJump() - ServerPlayer.DEFAULT_JUMP_SPEED
						+ 1), ClientFrame.getScaledWidth(225), ClientFrame.getScaledHeight(254));
	}


	public ClientItem[] getEquippedWeapons()
	{
		return equippedWeapons;
	}

	public void setEquippedWeapons(ClientItem[] equippedWeapons)
	{
		this.equippedWeapons = equippedWeapons;

		// Make the border show up around the first weapon
		if (equippedWeapons[0] != null)
		{
			equippedWeapons[0].setBorder(BorderFactory
					.createLineBorder(Color.white));
			client.setWeaponSelected(0);
		}
	}

	public ClientItem[][] getInventory()
	{
		return inventory;
	}

	public void setInventory(ClientItem[][] inventory)
	{
		this.inventory = inventory;
	}

	public ClientItem getEquippedArmour()
	{
		return equippedArmour;
	}

	public void setEquippedArmour(ClientItem equippedArmour)
	{
		this.equippedArmour = equippedArmour;
	}

	public JButton getMenuButton()
	{
		return mainMenu;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		client.printToServer("X");
		client.leaveGame = true;
	}

	public int getNumHP()
	{
		for (int row = 0; row < inventory.length; row++)
			for (int col = 0; col < inventory[row].length; col++)
			{
				if(inventory[row][col] != null && inventory[row][col].getType().equals(ServerWorld.HP_POTION_TYPE))
				{
					return  inventory[row][col].getAmount();
				}
			}
		return 0;
	}

	public int getNumMana()
	{
		for (int row = 0; row < inventory.length; row++)
			for (int col = 0; col < inventory[row].length; col++)
			{
				if(inventory[row][col] != null && inventory[row][col].getType().equals(ServerWorld.MANA_POTION_TYPE))
				{
					return  inventory[row][col].getAmount();
				}
			}
		return 0;
	}

}
