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
		case ServerWorld.BASIC_BARRACKS_TYPE:
			setImage("BARRACKS_VALID");
			goodImageIndex = Images.getImageIndex("BARRACKS_VALID");
			badImageIndex = Images.getImageIndex("BARRACKS_INVALID");
			break;
		case ServerWorld.ADV_BARRACKS_TYPE:
			setImage("ADV_BARRACKS_VALID");
			goodImageIndex = Images.getImageIndex("ADV_BARRACKS_VALID");
			badImageIndex = Images.getImageIndex("ADV_BARRACKS_INVALID");
			break;
		case ServerWorld.GIANT_FACTORY_TYPE:
			setImage("GIANT_FACTORY_VALID");
			goodImageIndex = Images.getImageIndex("GIANT_FACTORY_VALID");
			badImageIndex = Images.getImageIndex("GIANT_FACTORY_INVALID");
			break;
		case ServerWorld.WOOD_HOUSE_TYPE:
			setImage("WOOD_HOUSE_VALID");
			goodImageIndex = Images.getImageIndex("WOOD_HOUSE_VALID");
			badImageIndex = Images.getImageIndex("WOOD_HOUSE_INVALID");
			break;
		case ServerWorld.INN_TYPE:
			setImage("INN_VALID");
			goodImageIndex = Images.getImageIndex("INN_VALID");
			badImageIndex = Images.getImageIndex("INN_INVALID");
			break;
		case ServerWorld.TOWER_TYPE:
			setImage("TOWER_INVALID");
			goodImageIndex = Images.getImageIndex("TOWER_VALID");
			badImageIndex = Images.getImageIndex("TOWER_INVALID");
			break;
		case ServerWorld.GOLD_MINE_TYPE:
			setImage("GOLD_MINE_INVALID");
			goodImageIndex = Images.getImageIndex("GOLD_MINE_VALID");
			badImageIndex = Images.getImageIndex("GOLD_MINE_INVALID");
			break;
		}
		
		setWidth(Images.getGameImage(getImage()).getWidth());
		setHeight(Images.getGameImage(getImage()).getHeight());
	}
	
	public ServerBuilding toBuilding(int team)
	{
		// -6 with hologram location
		return ServerBuilding.getNewBuilding(getX(), getY()+6, imageType, team, world);
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
