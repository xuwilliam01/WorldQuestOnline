package Client;

import java.awt.Color;

import Server.ServerWorld;
import Server.Effects.ServerText;

public class ClientFloatingText extends ClientObject {

	public final static int TOTAL_ALIVE_TIME = 45;

	/**
	 * Number of frames that the text is alive for
	 */
	private int aliveTime = TOTAL_ALIVE_TIME;

	/**
	 * Number of frames it moves every second
	 */
	private double vSpeed = -0.5;
	/**
	 * A personal y that's a double so it can move slower than 1 pixel/s
	 */
	private double y;

	/**
	 * The opacity of the text
	 */
	private float alpha = 1;

	/**
	 * Colour of the text
	 */
	private Color color;

	/**
	 * The actual text
	 */
	private String text;

	/**
	 * Reference to the world
	 */
	private ClientWorld world;

	/**
	 * 
	 * @param id
	 * @param x
	 * @param y
	 * @param image
	 * @param team
	 * @param type
	 */
	public ClientFloatingText(int id, int x, int y, String image, int team,
			ClientWorld world) {
		super(id, x, y, image, team, ServerWorld.TEXT_TYPE + "");
		this.y = y;
		this.world = world;
		char colour = image.charAt(0);
		
		this.text = image.substring(1);

		switch (colour) {
		case ServerText.PURPLE_TEXT:
			this.text = "NOT ENOUGH MANA";
			color = ClientWorld.PURPLE_TEXT;
			break;
		case ServerText.BLUE_TEXT:
			this.text = "BLOCK";
			color = ClientWorld.BLUE_TEXT;
			break;
		case ServerText.RED_TEXT:
			color = ClientWorld.RED_TEXT;
			break;
		case ServerText.YELLOW_TEXT:
			color = ClientWorld.YELLOW_TEXT;
			break;
		}

		x -= (int) (this.text.length() * ClientWorld.DAMAGE_FONT_WIDTH + 0.5 / 2);
	}

	@Override
	public void update() {
		if ((--aliveTime) <= 0) {
			world.remove(getID());
			return;
		}

		y += vSpeed;
		setY((int) y);
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
