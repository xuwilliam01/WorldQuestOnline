package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import Imports.Images;

public class ClientShopItem extends JButton implements ActionListener{
	
	private String imageName;
	private String type;
	private int amount;
	private int cost;
	private int row;
	private int col;
	private ClientShop inventory;
	private Image image;

	public ClientShopItem(String imageName, String type,int amount,int cost,int row, int col, ClientShop inventory)
	{
		super(new ImageIcon(Images.getImage(imageName)));
		this.imageName = imageName;
		image = Images.getImage(imageName);
		this.type = type;
		this.amount = amount;
		this.cost = cost;
		this.row = row;
		this.col = col;
		this.inventory = inventory;

		setSize(Images.INVENTORY_IMAGE_SIDELENGTH,Images.INVENTORY_IMAGE_SIDELENGTH);
		setLocation(col*Images.INVENTORY_IMAGE_SIDELENGTH+(col+1)*20,60 + row*(Images.INVENTORY_IMAGE_SIDELENGTH+20));
		setVisible(true);
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusable(false);
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent event) 
	{
		System.out.println("clicked");
		if(inventory.getClient().getMoney() >= cost)
		{
			inventory.getClient().print("B "+type);
			inventory.getClient().decreaseMoney(cost);

			if(amount > 1)
			{
				amount--;
			}
			else 
				inventory.removeItem(this);
		}	
		repaint();
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		graphics.setColor(Color.white);
		if(amount >= 10)
			graphics.drawString(amount+"", getWidth()-16, 10);
		else if(amount > 1)
			graphics.drawString(amount+"", getWidth()-8, 10);
	}

	public int getRow()
	{
		return row;
	}
	
	public int getCol()
	{
		return col;
	}
	
	public int getCost()
	{
		return cost;
	}
	
	public void increaseAmount(int amount)
	{
		this.amount += amount;
	}
	
	public String getType()
	{
		return type;
	}
}
