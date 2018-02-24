package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;
import Server.Buildings.ServerCastle;

@SuppressWarnings("serial")
public class ClientCastleShop extends JPanel implements ActionListener{
	
	public final static int SHOP_WIDTH = 1000;
	public final static int SHOP_HEIGHT = 500;

	private Client client; //Maybe not needed
	private int money;
	
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
		add(new ClientCastleShopItem(ServerWorld.MERC_TYPE,this));
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
		graphics.drawString("Barracks", ClientFrame.getScaledWidth(20), ClientFrame.getScaledHeight(100));
		graphics.drawString("Housing", ClientFrame.getScaledWidth(20), ClientFrame.getScaledHeight(200));
		graphics.drawString("Defence", ClientFrame.getScaledWidth(20), ClientFrame.getScaledHeight(300));
		graphics.drawString("Resource", ClientFrame.getScaledWidth(20), ClientFrame.getScaledHeight(400));
		graphics.drawString("Mercenaries", ClientFrame.getScaledWidth(440), ClientFrame.getScaledHeight(80));
		graphics.drawString("Uses 20 housing", ClientFrame.getScaledWidth(440), ClientFrame.getScaledHeight(110));
		String mon = "Team Gold: "+ money;
		graphics.drawString(mon, ClientFrame.getScaledWidth(400), ClientFrame.getScaledHeight(450));
		graphics.drawImage(coinImage, ClientFrame.getScaledWidth(400) + graphics.getFontMetrics().stringWidth(mon)+5, ClientFrame.getScaledHeight(450)-8,this);
		graphics.setColor(Color.gray);
		String income =  " (+" + ServerCastle.CASTLE_TIER_INCOME
				[(client.getPlayer().getTeam()==ServerCreature.RED_TEAM)?client.getRedCastleTier():client.getBlueCastleTier()]
						+ " gold/min)";
		graphics.drawString(income,  ClientFrame.getScaledWidth(400) + graphics.getFontMetrics().stringWidth(mon)+30, ClientFrame.getScaledHeight(450));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}
}
