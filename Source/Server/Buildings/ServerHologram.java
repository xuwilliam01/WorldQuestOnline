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
	private boolean wantToPlace = false;
	
	private int goodImageIndex;
	private int badImageIndex;
	
	private ServerWorld world;
	
	public ServerHologram(double x, double y, String imageType, ServerPlayer owner,
			ServerEngine engine) {
		super(x, y, 0,0, 0, "ServerHologram", ServerWorld.HOLOGRAM_TYPE, engine);
		
		this.owner = owner;
		this.world = engine.getWorld();
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
	
	public ServerBuilding toBuilding(int team)
	{
		// -6 with hologram location
		return ServerBuilding.getNewBuilding(getY(), getY()+6, imageType, team, world);
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

	public void place()
	{
		wantToPlace = true;
	}
	
	public void dontPlace()
	{
		wantToPlace = false;
	}
	
	public boolean wantToPlace()
	{
		return wantToPlace;
	}
}
