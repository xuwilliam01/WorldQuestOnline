package Server;

import Imports.Images;

public abstract class ServerItem extends ServerObject {

	public ServerItem(double x, double y,String type) {
		super(x, y, 0,0, ServerWorld.GRAVITY, ServerEngine.useNextID(), "", type);

		switch (type) {
		case ServerWorld.HP_TYPE:
			setImage("HP_POTION.png");
			break;
		case ServerWorld.SWORD_TYPE:
			setImage("SWORD.png");
			break;
		}
		
		setWidth(Images.getGameImage(getImage()).getWidth());
		setHeight(Images.getGameImage(getImage()).getHeight());
		
	}
}
