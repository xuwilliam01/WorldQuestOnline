package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import Imports.Images;
import Server.ServerWorld;
import Server.Buildings.ServerCastle;

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
		case ServerWorld.UPG_CASTLER_BUTT:
			setIcon(new ImageIcon(Images.getImage("Upgrade").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			cost = ServerCastle.CASTLE_TIER_PRICE[shop.getClient().getRedCastleTier()];
			setLocation(100,100);
			setToolTipText("Upgrade castle tier");
			break;
		case ServerWorld.UPG_CASTLEB_BUTT:
			setIcon(new ImageIcon(Images.getImage("Upgrade").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			cost = ServerCastle.CASTLE_TIER_PRICE[shop.getClient().getBlueCastleTier()];
			setLocation(100,100);
			setToolTipText("Upgrade castle tier");
			break;
		case ServerWorld.BARRACK_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("BARRACKS_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(200,100);
			//Set the cost as well
			setToolTipText("Barracks");
			break;
		}	
		setVisible(true);
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusable(false);
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent arg0) {
		if(shop.getMoney() < cost)
			return;
		shop.buy(type, cost);
	}
}
