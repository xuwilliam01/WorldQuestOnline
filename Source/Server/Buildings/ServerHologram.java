package Server.Buildings;

import Imports.Images;
import Server.ServerEngine;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerPlayer;

public class ServerHologram extends ServerObject{

	private ServerPlayer owner;
	
	//The type of the image that this hologram represents
	private String imageType;
	private boolean canPlace = true;
	
	private int goodImageIndex;
	private int badImageIndex;
	
	public ServerHologram(double x, double y, String imageType, ServerPlayer owner,
			ServerEngine engine) {
		super(x, y, 0,0, 0, "ServerHologram", ServerWorld.HOLOGRAM_TYPE, engine);
		
		this.owner = owner;
		setVisible(false);
		setSolid(false);
		this.imageType = imageType;
		
		switch (imageType)
		{
		case ServerWorld.BARRACK_TYPE:
			setImage("BARRACKS");
			goodImageIndex = Images.getImageIndex("BARRACKS_GOOD");
			badImageIndex = Images.getImageIndex("BARRACKS_BAD");
			break;
		}
		
		setWidth(Images.getGameImage(getImage()).getWidth());
		setHeight(Images.getGameImage(getImage()).getHeight());
	}

	public int getGoodImage()
	{
		return goodImageIndex;
	}
	
	public int getBadImage()
	{
		return badImageIndex;
	}
	
	public void setCanPlace(boolean bool)
	{
		canPlace = bool;
	}
	
	public boolean canPlace()
	{
		return canPlace;
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	public ServerPlayer getOwner()
	{
		return owner;
	}

}
