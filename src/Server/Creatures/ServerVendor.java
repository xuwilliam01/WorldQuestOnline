package Server.Creatures;

import Server.ServerWorld;
import Server.Items.ServerArmour;
import Server.Items.ServerPotion;
import Server.Items.ServerWeapon;

public class ServerVendor extends ServerCreature {

	public final static int MAX_INVENTORY = 60;
	private boolean isBusy = false;

	public ServerVendor(double x, double y, ServerWorld world) {
		super(x, y, -1,-1,0,0, ServerWorld.GRAVITY, "VENDOR_RIGHT.png", ServerWorld.VENDOR_TYPE, Integer.MAX_VALUE, world,false);
		
		if ((int)(Math.random()*2)==1)
		{
			setImage("VENDOR_LEFT.png");
		}
		
		makeShop();

	}
	
	public ServerVendor(double x, double y, ServerWorld world, String image) {
		super(x, y, -1,-1,0,0, ServerWorld.GRAVITY, image, ServerWorld.VENDOR_TYPE, Integer.MAX_VALUE, world,false);
	
		makeShop();

	}
	

	public void makeShop()
	{
		for(int potion = 0; potion < 10; potion++)
			addItem(ServerPotion.randomPotion(getX(),getY()));
		for(int weapon = 0; weapon < 15; weapon++)
			addItem(ServerWeapon.randomWeapon(getX(), getY()));
		for(int armour = 0; armour < 8; armour++)
			addItem(ServerArmour.randomArmour(getX(), getY()));
	}
	
	public boolean isBusy()
	{
		return isBusy;
	}

	public void setIsBusy(boolean isBusy)
	{
		this.isBusy = isBusy;
	}

}
