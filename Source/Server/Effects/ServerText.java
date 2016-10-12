package Server.Effects;

import Server.ServerObject;
import Server.ServerWorld;

public class ServerText extends ServerObject {

	ServerWorld world;
	
	public final static char RED_TEXT = 'r';
	public final static char YELLOW_TEXT = 'y';
	public final static char BLUE_TEXT = 'b';
	public final static char PURPLE_TEXT = 'p';
	
	// Send the text object only once
	private final int aliveTime = 2;
	
	private String text;
	
	private long startCounter;
	

	/**
	 * Constructor for a piece of text
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param gravity
	 *            the gravity of the text
	 * @param text
	 *            the actual text
	 * @param colour
	 *            the colour of the text
	 * @param type
	 *            the text type
	 */
	public ServerText(double x, double y, String text, char colour, ServerWorld world) {
		super(x, y, 20, 20, 0, colour + text, ServerWorld.TEXT_TYPE +"",world.getEngine());
		if (colour == ServerText.YELLOW_TEXT)
		{
			//System.out.println("Yellow");
		}
		setSolid(false);
		this.world = world;
		this.text = text;
		this.startCounter = world.getWorldCounter();
	}
	
	@Override
	public void update()
	{
		if (world.getWorldCounter()-startCounter>= aliveTime)
		{
			destroy();
		}
	}

	// ///////////////////////
	// GETTERS AND SETTERS //
	// ///////////////////////

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	
}
