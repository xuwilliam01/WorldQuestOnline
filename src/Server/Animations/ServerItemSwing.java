package Server.Animations;

import Imports.Images;
import Server.ServerObject;
import Server.ServerWorld;

public class ServerItemSwing extends ServerObject
{

	/**
	 * Counter for timing update
	 */
	private int counter;

	/**
	 * The current angle in degrees
	 */
	private int currentAngle;

	/**
	 * Whether the sword swings clockwise or counterclockwise
	 */
	private boolean isClockwise;

	/**
	 * The time in server frames the animation will last
	 */
	private int timeInFrames;

	/**
	 * The object that this animation will centre on
	 */
	private ServerObject wielder;
	
	private int originalWidth;
	private int originalHeight;

	/**
	 * Constructor for the item swing animation
	 * @param wielder
	 * @param image
	 * @param timeInMilliseconds
	 */
	public ServerItemSwing(ServerObject wielder, String image, int angle,
			int timeInFrames)
	{
		super(wielder.getX(), wielder.getY(), -1, -1, 0, image,
				ServerWorld.ITEM_SWING_TYPE);
		this.wielder = wielder;
		counter = 0;
		this.timeInFrames = timeInFrames;
		
		setMapVisible(false);
		setSolid(false);
		
		if (angle > -90 && angle <= 15)
		{
			currentAngle = -90;
			isClockwise = true;
		}
		else if(angle <= 165 && angle > 90)
		{
			currentAngle = 90;
			isClockwise = true;
		}
		else if(angle > 15 && angle <= 90)
		{
			currentAngle = 90;
			isClockwise = false;
		}
		else
		{
			currentAngle = -90;
			isClockwise = false;
		}
		
		originalWidth = Images.getGameImage(image).getWidth();
		originalHeight = Images.getGameImage(image).getHeight();
		
		setImage(getBaseImage() + "_" + currentAngle + ".png");
		
	}

	/**
	 * Update counter for the object
	 */
	public void update()
	{
		if (counter >= (int)(timeInFrames / 8.0 + 1)*8)
		{
			destroy();
		}
		else if (counter % (int) (timeInFrames / 8.0 + 1) == 0 && counter!=0)
		{
			if (isClockwise)
			{
				currentAngle += 15;
				if (currentAngle > 180)
				{
					currentAngle = -180 - (180 - currentAngle);
				}
			}
			else
			{
				currentAngle -= 15;
				if (currentAngle <= -180)
				{
					currentAngle = 180 - (-180 - currentAngle);
				}
			}
			setImage(getBaseImage() + "_" + currentAngle + ".png");
		}

		setX(wielder.getX() + wielder.getWidth() / 2 - originalWidth / 2);
		setY(wielder.getY() + wielder.getHeight() / 2 - originalHeight / 2);
		counter++;
	}

}
