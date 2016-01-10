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
	private int pos;
	private ClientShop inventory;
	private Image image;

	public ClientShopItem(String imageName, String type,int amount,int cost,int pos, ClientShop inventory)
	{
		super(new ImageIcon(Images.getImage(imageName)));
		this.imageName = imageName;
		image = Images.getImage(imageName);
		this.type = type;
		this.amount = amount;
		this.cost = cost;
		this.pos = pos;
		this.inventory = inventory;

		setSize(Images.INVENTORY_IMAGE_SIDELENGTH,Images.INVENTORY_IMAGE_SIDELENGTH);
		setLocation(pos*Images.INVENTORY_IMAGE_SIDELENGTH+(pos+1)*20,60);
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

	public int getPos()
	{
		return pos;
	}
}
