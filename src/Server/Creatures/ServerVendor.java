package Server.Creatures;

import Server.ServerWorld;
import Server.Items.ServerPotion;
import Server.Items.ServerWeapon;

public class ServerVendor extends ServerCreature {

	public final static int MAX_INVENTORY = 30;

	private int quality;

	public ServerVendor(double x, double y, ServerWorld world, int quality) {
		super(x, y, -1,-1,0,0, ServerWorld.GRAVITY, "VENDOR.png", ServerWorld.VENDOR_TYPE, Integer.MAX_VALUE, world,false);
		
		this.quality = Math.min(quality, ServerWeapon.NUM_TIERS);
		makeShop();

	}

	public void makeShop()
	{
		for(int potion = 0; potion < 4; potion++)
			addItem(ServerPotion.randomPotion(getX(),getY()));
		for(int weapon = 0; weapon < 21; weapon++)
			addItem(ServerWeapon.randomWeapon(getX(), getY(), (quality-1)*ServerWeapon.NUM_WEAPONS/ServerWeapon.NUM_TIERS,(quality+1)*ServerWeapon.NUM_WEAPONS/ServerWeapon.NUM_TIERS));
	}


}
