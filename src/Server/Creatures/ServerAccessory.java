package Server.Creatures;

import Server.ServerObject;
import Server.ServerWorld;
import Tools.RowCol;

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
	public ServerAccessory(ServerCreature wielder, String baseImage)
	{
		super(wielder.getX(), wielder.getY(), -1, -1, 0,
				baseImage + "_0_0.png", ServerWorld.ACCESSORY_TYPE);
		this.baseImage = baseImage;
		this.wielder = wielder;
	}

	/**
	 * Update the x and y of this accessory to be on the wielder, and change the
	 * animation frame to fit the wielder
	 * @param rowCol the specific row and column of the frame in the sprite
	 *            sheet
	 */
	public void update(String direction, RowCol rowCol)
	{
		setX(wielder.getX());
		setY(wielder.getY());
		setImage(baseImage + "_" + direction + "_" + rowCol.getRow() + "_"
				+ rowCol.getColumn()
				+ ".png");
	}
}
