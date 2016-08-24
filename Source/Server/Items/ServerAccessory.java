package Server.Items;

import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerCreature;
import Tools.RowCol;

/**
 * Accessories for the player
 * @author Alex Raita & William Xu
 *
 */
public class ServerAccessory extends ServerObject
{

	/**
	 * The creature that wields this accessory
	 */
	private ServerCreature wielder;

	/**
	 * The base image string of the accessory without the specific frame
	 */
	private String baseImage;

	/**
	 * Constructor for an accessory on a creature
	 * @param wielder
	 * @param image
	 */
	public ServerAccessory(ServerCreature wielder, String baseImage, double armourPercentage)
	{
		super(wielder.getDrawX(), wielder.getDrawY(), wielder.getWidth(), wielder.getHeight(), 0,
				baseImage + "_RIGHT_0_0", ServerWorld.ACCESSORY_TYPE);
		this.baseImage = baseImage;
		this.wielder = wielder;
		setSolid(false);
	}

	/**
	 * Update the x and y of this accessory to be on the wielder, and change the
	 * animation frame to fit the wielder
	 * @param rowCol the specific row and column of the frame in the sprite
	 *            sheet
	 */
	public void update(String direction, RowCol rowCol)
	{
		setX(wielder.getDrawX());
		setY(wielder.getDrawY());
		setImage(baseImage + "_" + direction + "_" + rowCol.getRow() + "_"
				+ rowCol.getColumn()
				+ "");
	}

	@Override
	public void update()
	{
		// TODO Auto-generated method stub
		
	}
}
