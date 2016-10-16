package Client;

import javax.swing.JButton;

public class ClientCastleShopItem extends JButton{

	private int cost;
	private String type;
	
	public ClientCastleShopItem(String imageName, int cost, String type)
	{
		this.cost = cost;
		this.type = type;
	}
}
