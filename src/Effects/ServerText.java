package Effects;

import Server.ServerObject;

public class ServerText extends ServerObject
{

	public ServerText(double x, double y, double gravity, String text, char colour, String type)
	{
		super(x, y, 20, 20, gravity, "t" + colour + text, type);
		setSolid(false);
	}

}
