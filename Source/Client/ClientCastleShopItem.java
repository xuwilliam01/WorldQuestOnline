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
import Server.ServerWorld;
import Server.Buildings.ServerHouse;
import Server.Items.ServerBuildingItem;

@SuppressWarnings("serial")
public class ClientCastleShopItem extends JButton implements ActionListener{

	public static final int WIDTH = 75;
	public static final int HEIGHT = 75;
	private int cost;
	private String type;
	private ClientCastleShop shop;
	private Image coinImage = Images.getImage("COIN");
	
	public ClientCastleShopItem(String type, ClientCastleShop shop)
	{
		this.type = type;
		this.shop = shop;
		setSize(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT));
		
		//Add tooltips and set location:
		switch(type) {
		case ServerWorld.BASIC_BARRACKS_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("BARRACKS_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(ClientFrame.getScaledWidth(100),ClientFrame.getScaledHeight(50));
			cost = ServerBuildingItem.BASIC_BARRACKS_COST;
			setToolTipText("Barracks (Spawns three soldiers and two archers each tick)");
			break;
		case ServerWorld.ADV_BARRACKS_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("ADV_BARRACKS_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(ClientFrame.getScaledWidth(200),ClientFrame.getScaledHeight(50));
			cost = ServerBuildingItem.ADV_BARRACKS_COST;
			setToolTipText("Advanced Barracks (Spawns three knights and two wizards each tick)");
			break;
		case ServerWorld.GIANT_FACTORY_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("GIANT_FACTORY_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(ClientFrame.getScaledWidth(300),ClientFrame.getScaledHeight(50));
			cost = ServerBuildingItem.GIANT_FACTORY_COST;
			setToolTipText("Giant Factory (Spawns three giants each tick)");
			break;
		case ServerWorld.WOOD_HOUSE_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("WOOD_HOUSE_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(ClientFrame.getScaledWidth(100),ClientFrame.getScaledHeight(150));
			cost = ServerBuildingItem.WOOD_HOUSE_COST;
			setToolTipText(String.format("Wooden house (+%d housing space)", ServerHouse.WOOD_HOUSE_POP));
			break;
		case ServerWorld.INN_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("INN_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(ClientFrame.getScaledWidth(200),ClientFrame.getScaledHeight(150));
			cost = ServerBuildingItem.INN_COST;
			setToolTipText(String.format("Inn (+%d housing space)", ServerHouse.INN_POP));
			break;
		case ServerWorld.TOWER_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("TOWER_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(ClientFrame.getScaledWidth(100),ClientFrame.getScaledHeight(250));
			cost = ServerBuildingItem.TOWER_COST;
			setToolTipText("Arrow Tower (Defends against enemy units)");
			break;
		case ServerWorld.GOLD_MINE_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("GOLD_MINE_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(ClientFrame.getScaledWidth(100),ClientFrame.getScaledHeight(350));
			cost = ServerBuildingItem.GOLD_MINE_COST;
			setToolTipText("Gold Mine (Produces gold that players can collect for the team by walking by the mine)");
			break;
		case ServerWorld.MERC_TYPE:
			setIcon(new ImageIcon(Images.getImage("MERC")));
			setLocation(ClientFrame.getScaledWidth(550), ClientFrame.getScaledHeight(50));
			cost = ServerBuildingItem.MERC_COST;
			setToolTipText("Hire 20 Mercenaries to fight for you");
			break;
		}
		setVisible(true);
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusable(false);
		addActionListener(this);
	}
	
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		String price = cost+"";
		graphics.setColor(Color.white);
		graphics.drawString(price,ClientFrame.getScaledWidth(WIDTH)- graphics.getFontMetrics().stringWidth(price)-3 - 10, graphics.getFontMetrics().getHeight()/2+3);
		graphics.drawImage(coinImage, ClientFrame.getScaledWidth(WIDTH)-10, 1, this);
	}
	public void actionPerformed(ActionEvent arg0) {
		if(type.equals(ServerWorld.MERC_TYPE))
			shop.getClient().printToServer("m");
		else if(shop.getMoney() >= cost)
			shop.buy(type);
	}
}
