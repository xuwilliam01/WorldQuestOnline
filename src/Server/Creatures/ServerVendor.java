package Server.Creatures;

import Server.ServerWorld;
import Server.Items.ServerArmour;
import Server.Items.ServerPotion;
import Server.Items.ServerWeapon;

public class ServerVendor extends ServerCreature {

	public final static int MAX_INVENTORY = 60;

	private int quality;
	private boolean isBusy = false;

	public ServerVendor(double x, double y, ServerWorld world, int quality) {
		super(x, y, -1,-1,0,0, ServerWorld.GRAVITY, "VENDOR.png", ServerWorld.VENDOR_TYPE, Integer.MAX_VALUE, world,false);
		
		this.quality = Math.min(quality, ServerWeapon.NUM_TIERS);
		makeShop();

	}

	public void makeShop()
	{
		for(int potion = 0; potion < 6; potion++)
			addItem(ServerPotion.randomPotion(getX(),getY()));
		for(int weapon = 0; weapon < 10; weapon++)
			addItem(ServerWeapon.randomWeapon(getX(), getY(), (quality-1)*ServerWeapon.NUM_WEAPONS/ServerWeapon.NUM_TIERS,(quality+1)*ServerWeapon.NUM_WEAPONS/ServerWeapon.NUM_TIERS));
		for(int armour = 0; armour < 3; armour++)
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
	
	public int getQuality()
	{
		return quality;
	}

}
