package Server.Items;

import Imports.Images;
import Server.ServerEngine;
import Server.ServerObject;
import Server.ServerWorld;

public abstract class ServerItem extends ServerObject {

	private final static int NUM_ITEMS = 2;
	
	public ServerItem(double x, double y,String type) {
		super(x, y, 0,0, ServerWorld.GRAVITY, ServerEngine.useNextID(), "", type);

		switch (type) {
		case ServerWorld.HP_25:
			setImage("HP_POTION.png");
			break;
		case ServerWorld.HP_50:
			setImage("HP_POTION.png");
			break;
		case ServerWorld.HP_75:
			setImage("HP_POTION.png");
			break;
		case ServerWorld.HP_100:
			setImage("HP_POTION.png");
			break;
		case ServerWorld.LONG_SWORD:
			setImage("SWORD.png");
			break;
		}
		
		setWidth(Images.getGameImage(getImage()).getWidth());
		setHeight(Images.getGameImage(getImage()).getHeight());
		
	}
	
	public static ServerItem randomItem(double x, double y)
	{
		int randType = (int) (Math.random() * NUM_ITEMS + 1);

		switch (randType) {
		case 1:
			return new ServerHPPotion(x, y);
		case 2:
			return new ServerSword(x, y,'L',20);
		}
		// This won't happen
		return null;
		
	}
}
