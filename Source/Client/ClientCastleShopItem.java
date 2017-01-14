package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import Imports.Images;
import Server.ServerWorld;
import Server.Buildings.ServerCastle;
import Server.Items.ServerBuildingItem;

public class ClientCastleShopItem extends JButton implements ActionListener{

	public static final int WIDTH = 50;
	public static final int HEIGHT = 50;
	private int cost;
	private String type;
	private ClientCastleShop shop;

	public ClientCastleShopItem(String type, ClientCastleShop shop)
	{
		this.type = type;
		this.shop = shop;
		setSize(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT));
		
		//Add tooltips and set location:
		switch(type) {
		case ServerWorld.BASIC_BARRACKS_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("BARRACKS_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(ClientFrame.getScaledWidth(100),ClientFrame.getScaledHeight(100));
			cost = ServerBuildingItem.BASIC_BARRACKS_COST;
			setToolTipText("Barracks (Spawns two soldiers and one archer each tick) Cost: "+ ServerBuildingItem.BASIC_BARRACKS_COST);
			break;
		case ServerWorld.ADV_BARRACKS_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("ADV_BARRACKS_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(ClientFrame.getScaledWidth(100),ClientFrame.getScaledHeight(200));
			cost = ServerBuildingItem.ADV_BARRACKS_COST;
			setToolTipText("Advanced Barracks (Spawns two knights and one wizard each tick) Cost: "+ ServerBuildingItem.ADV_BARRACKS_COST);
			break;
		case ServerWorld.GIANT_FACTORY_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("GIANT_FACTORY_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(ClientFrame.getScaledWidth(100),ClientFrame.getScaledHeight(300));
			cost = ServerBuildingItem.GIANT_FACTORY_COST;
			setToolTipText("Giant Factory (Spawns a giant each tick) Cost: "+ ServerBuildingItem.GIANT_FACTORY_COST);
			break;
		case ServerWorld.WOOD_HOUSE_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("WOOD_HOUSE_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(ClientFrame.getScaledWidth(200),ClientFrame.getScaledHeight(100));
			cost = ServerBuildingItem.WOOD_HOUSE_COST;
			setToolTipText("Wooden house (+10 housing space) Cost: "+ ServerBuildingItem.WOOD_HOUSE_COST);
			break;
		case ServerWorld.INN_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("INN_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(ClientFrame.getScaledWidth(200),ClientFrame.getScaledHeight(200));
			cost = ServerBuildingItem.INN_COST;
			setToolTipText("Inn (+25 housing space) Cost: "+ ServerBuildingItem.INN_COST);
			break;
		case ServerWorld.TOWER_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("TOWER_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(ClientFrame.getScaledWidth(300),ClientFrame.getScaledHeight(100));
			cost = ServerBuildingItem.TOWER_COST;
			setToolTipText("Arrow Tower (Defends against enemy units) Cost: "+ ServerBuildingItem.TOWER_COST);
			break;
		case ServerWorld.GOLD_MINE_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("GOLD_MINE_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(ClientFrame.getScaledWidth(400),ClientFrame.getScaledHeight(100));
			cost = ServerBuildingItem.GOLD_MINE_COST;
			setToolTipText("Gold Mine (Produces gold that players can collect for the team by passing by) Cost: "+ ServerBuildingItem.GOLD_MINE_COST);
			break;
		}
		setVisible(true);
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusable(false);
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent arg0) {
		if(shop.getMoney() >= cost)
			shop.buy(type);
	}
}
