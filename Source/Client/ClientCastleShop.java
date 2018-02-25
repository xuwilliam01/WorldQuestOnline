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
	
	public final static int SHOP_WIDTH = 725;
	public final static int SHOP_HEIGHT = 475;

	private Client client; //Maybe not needed
	private int money;
	
	private Image coinImage = Images.getImage("COIN");
	
	public ClientCastleShop(Client client, int money)
	{
		this.client = client;
		this.money = money;
		setDoubleBuffered(true);
		setBackground(Color.darkGray);
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
		
		graphics.setColor(Color.BLACK);
		graphics.fillRect(ClientFrame.getScaledWidth(15), ClientFrame.getScaledHeight(15), ClientFrame.getScaledWidth(SHOP_WIDTH - 30), ClientFrame.getScaledHeight(SHOP_HEIGHT - 30));
		
		graphics.setColor(Color.white);
		
		int delta = 45;
		
		graphics.drawString("Barracks", ClientFrame.getScaledWidth(delta), ClientFrame.getScaledHeight(100));
		graphics.drawString("Housing", ClientFrame.getScaledWidth(delta), ClientFrame.getScaledHeight(200));
		graphics.drawString("Defence", ClientFrame.getScaledWidth(delta), ClientFrame.getScaledHeight(300));
		graphics.drawString("Resource", ClientFrame.getScaledWidth(delta), ClientFrame.getScaledHeight(400));
		graphics.drawString("Mercenaries", ClientFrame.getScaledWidth(335 + delta), ClientFrame.getScaledHeight(240));
		graphics.drawString("Uses 20 housing", ClientFrame.getScaledWidth(335 + delta), ClientFrame.getScaledHeight(270));
		String mon = "Team Gold: "+ money;
		graphics.drawString(mon, ClientFrame.getScaledWidth(220+ delta), ClientFrame.getScaledHeight(420));
		graphics.drawImage(coinImage, ClientFrame.getScaledWidth(220+ delta) + graphics.getFontMetrics().stringWidth(mon)+5, ClientFrame.getScaledHeight(420)-8,this);
		graphics.setColor(Color.gray);
		String income =  " (+" + ServerCastle.CASTLE_TIER_INCOME
				[(client.getPlayer().getTeam()==ServerCreature.RED_TEAM)?client.getRedCastleTier():client.getBlueCastleTier()]
						+ " gold/min until " + ServerCastle.MAX_MONEY_FOR_INCOME + " gold)";
		graphics.drawString(income,  ClientFrame.getScaledWidth(205 + delta) + graphics.getFontMetrics().stringWidth(mon)+30, ClientFrame.getScaledHeight(420));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}
}
