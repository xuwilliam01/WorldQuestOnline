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
		for(int weapon = 0; weapon < 20; weapon++)
			addItem(ServerWeapon.randomWeapon(getX(), getY()));

		int randWeapon = (int)(Math.random() * 6+1);
		switch(randWeapon)
		{
		case 1:
			addItem(new ServerWeapon(getX(),getY(),ServerWorld.DARKWAND_TYPE));
			break;
		case 2:
			addItem(new ServerWeapon(getX(),getY(),ServerWorld.MEGABOW_TYPE));
			break;
		case 3:
			addItem(new ServerWeapon(getX(),getY(),ServerWorld.SWORD_TYPE + ServerWorld.DIAMOND_TIER));
			break;
		case 4:
			addItem(new ServerWeapon(getX(),getY(),ServerWorld.HALBERD_TYPE + ServerWorld.DIAMOND_TIER));
			break;
		case 5:
			addItem(new ServerWeapon(getX(),getY(),ServerWorld.FIREWAND_TYPE));
		case 6:
			addItem(new ServerWeapon(getX(),getY(),ServerWorld.STEELBOW_TYPE));
			break;
		}

		for(int armour = 0; armour < 8; armour++)
			addItem(ServerArmour.randomArmour(getX(), getY()));

		//Always have the steel armour
		addItem(new ServerArmour(getX(),getY(),ServerWorld.STEEL_ARMOUR));
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
