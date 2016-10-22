package Client;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import Server.ServerWorld;
import Server.Creatures.ServerCastle;
import Server.Creatures.ServerCreature;

public class ClientCastleShop extends JPanel{
	
	public final static int SHOP_WIDTH = 1000;
	public final static int SHOP_HEIGHT = 500;

	private Client client; //Maybe not needed
	private int money;
	
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

		if (client.getPlayer().getTeam() == ServerCreature.RED_TEAM)
			add(new ClientCastleShopItem(ServerWorld.UPG_CASTLER_BUTT,this));
		else
			add(new ClientCastleShopItem(ServerWorld.UPG_CASTLEB_BUTT,this));
		
		add(new ClientCastleShopItem(ServerWorld.BARRACK_TYPE,this));
		
		
		
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
	
	public void buy(String type, int cost)
	{
		money -= cost;
		client.printToServer("BC "+ type);
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
}
