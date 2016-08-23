package Server.Effects;

import Server.ServerObject;

public class ServerText extends ServerObject
{
	/**
	 * Constructor for a piece of text
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param gravity the gravity of the text
	 * @param text the actual text
	 * @param colour the colour of the text
	 * @param type the text type
	 */
	public ServerText(double x, double y, double gravity, String text, char colour, String type)
	{
		super(x, y, 20, 20, gravity, "t" + colour + text, type);
		setSolid(false);
	}

}
