package Server.Effects;

import Server.ServerObject;
import Server.ServerWorld;

public abstract class ServerText extends ServerObject {

	ServerWorld world;
	/**
	 * The time the Damage indicator started
	 */
	private long startCounter;

	/**
	 * How long the damage indicator lasts for
	 */
	private int framesAlive;
	
	public final static char RED_TEXT = 'r';
	public final static char YELLOW_TEXT = 'y';
	public final static char PURPLE_TEXT = 'p';
	
	private String text;

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
	public ServerText(double x, double y, double gravity, String text,
			char colour, String type, ServerWorld world, int framesAlive) {
		super(x, y, 20, 20, gravity, "t" + colour + text, type);
		setSolid(false);
		this.world = world;
		startCounter = world.getWorldCounter();
		this.framesAlive = framesAlive;
		this.text = text;
	}

	/**
	 * Waiting for the moment it disappears
	 */
	public void update() {
		if (framesAlive >= 0) {
			if (world.getWorldCounter() - startCounter >= framesAlive) {
				destroy();
			}
		}
	}

	// ///////////////////////
	// GETTERS AND SETTERS //
	// ///////////////////////
	public int getFramesAlive() {
		return framesAlive;
	}

	public void setFramesAlive(int framesAlive) {
		this.framesAlive = framesAlive;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	
}
