package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

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
		
		hireMerc = new JButton("HIRE");
		hireMerc.setToolTipText(String.format("Hire Mercenaries (Cost: %d)", ServerBuildingItem.MERC_COST));
		hireMerc.setLocation(ClientFrame.getScaledWidth(200),ClientFrame.getScaledHeight(400));
		hireMerc.setSize(ClientFrame.getScaledWidth(100),ClientFrame.getScaledHeight(50));
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
		graphics.drawString("Money: "+money, 400, 400);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		client.printToServer("m");
	}
}
