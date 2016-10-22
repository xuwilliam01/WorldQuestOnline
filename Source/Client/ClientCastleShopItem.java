package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import Imports.Images;
import Server.ServerWorld;

public class ClientCastleShopItem extends JButton implements ActionListener{

	public static final int WIDTH = 50;
	public static final int HEIGHT = 50;
	private int cost;
	private String type;
	private ClientCastleShop shop;

	public ClientCastleShopItem(String imageName, int cost, String type, ClientCastleShop shop)
	{
		super(new ImageIcon(Images.getImage(imageName).getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
		this.cost = cost;
		this.type = type;
		this.shop = shop;
		setSize(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT));
		//Add tooltips and set location:
		switch(type) {
		case ServerWorld.UPG_CASTLE_BUTT:
			setLocation(100,100);
			setToolTipText("Upgrade castle tier");
			break;
		}	
		setVisible(true);
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusable(false);
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent arg0) {
		switch(type) {
		case ServerWorld.UPG_CASTLE_BUTT:
			if(shop.getMoney() >= cost)
			{
				shop.buy(type, cost);
				
			}
			break;
		}


	}
}
