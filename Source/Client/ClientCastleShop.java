package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import Imports.Images;
import Server.ServerWorld;
import Server.Buildings.ServerCastle;
import Server.Creatures.ServerCreature;
import Server.Items.ServerBuildingItem;

public class ClientCastleShop extends JPanel implements ActionListener{
	
	public final static int SHOP_WIDTH = 1000;
	public final static int SHOP_HEIGHT = 500;

	private Client client; //Maybe not needed
	private int money;
	
	private JButton hireMerc;
	private Image coinImage = Images.getImage("COIN");
	
	public ClientCastleShop(Client client, int money)
	{
		this.client = client;
		this.money = money;
		setDoubleBuffered(true);
		setBackground(Color.black);
		setFocusable(true);
		requestFocusInWindow();
		setLayout(null);
		setSize(ClientFrame.getScaledWidth(SHOP_WIDTH), ClientFrame.getScaledHeight(SHOP_HEIGHT));
		setLocation(200,200);
		
		add(new ClientCastleShopItem(ServerWorld.BASIC_BARRACKS_ITEM_TYPE,this));
		add(new ClientCastleShopItem(ServerWorld.ADV_BARRACKS_ITEM_TYPE,this));
		add(new ClientCastleShopItem(ServerWorld.GIANT_FACTORY_ITEM_TYPE,this));
		add(new ClientCastleShopItem(ServerWorld.WOOD_HOUSE_ITEM_TYPE,this));
		add(new ClientCastleShopItem(ServerWorld.INN_ITEM_TYPE,this));
		add(new ClientCastleShopItem(ServerWorld.TOWER_ITEM_TYPE,this));
		add(new ClientCastleShopItem(ServerWorld.GOLD_MINE_ITEM_TYPE,this));
		
		hireMerc = new JButton(new ImageIcon(Images.getImage("MERC")));
		hireMerc.setToolTipText(String.format("Hire Mercenaries (Cost: %d)", ServerBuildingItem.MERC_COST));
		hireMerc.setLocation(ClientFrame.getScaledWidth(600),ClientFrame.getScaledHeight(400));
		hireMerc.setSize(ClientFrame.getScaledWidth(75),ClientFrame.getScaledHeight(75));
		hireMerc.addActionListener(this);
		add(hireMerc);
		
	}
	
	public Client getClient()
	{
		return client;
	}
	
	public void setMoney(int money)
	{
		this.money = money;
		repaint();
	}
	
	public void buy(String type)
	{
		client.printToServer("b "+ type);
		repaint();
	}
	
	public int getMoney()
	{
		return money;
	}
	
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		graphics.setColor(Color.white);
		graphics.drawString("Barracks", 20, 100);
		graphics.drawString("Housing", 20, 200);
		graphics.drawString("Defence", 20, 300);
		graphics.drawString("Resource", 20, 400);
		graphics.drawString("Money: "+money, ClientFrame.getScaledWidth(400), ClientFrame.getScaledHeight(450));
		int delta = 0;
		if(money >= 10)
			delta = 5;
		graphics.drawImage(coinImage, ClientFrame.getScaledWidth(400) + 55+delta, ClientFrame.getScaledHeight(450)-10,this);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		client.printToServer("m");
	}
}
