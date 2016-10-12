package Client;

import java.awt.Color;

import Imports.Images;
import Server.ServerWorld;
import Server.Effects.ServerText;

public class ClientText extends ClientObject {

	public final static int TOTAL_ALIVE_TIME = 60;

	/**
	 * Number of frames that the text is alive for
	 */
	private int aliveTime;

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
	 * Whether or not the object exists
	 */
	private boolean exists = false;
	
	/**
	 * Color array used for this text
	 */
	private Color[] colorArray;

	/**
	 * 
	 * @param id
	 * @param x
	 * @param y
	 * @param image
	 * @param team
	 * @param type
	 */
	public ClientText(int id, int x, int y, String image, int team,
			ClientWorld world) {
		super(id, x, y, image, team, ServerWorld.TEXT_TYPE + "");
		this.exists = true;
		this.y = y;
		this.world = world;
		char colour = image.charAt(0);
		this.text = image.substring(1);

		switch (colour) {
		case ServerText.PURPLE_TEXT:
			this.text = "NOT ENOUGH MANA";
			colorArray = Images.purples;
			color = Images.PURPLE_TEXT;
			break;
		case ServerText.BLUE_TEXT:
			this.text = "0";
			colorArray = Images.blues;
			color = Images.BLUE_TEXT;
			break;
		case ServerText.RED_TEXT:
			colorArray = Images.reds;
			color = Images.RED_TEXT;
			break;
		case ServerText.YELLOW_TEXT:
			colorArray = Images.yellows;
			System.out.println(colorArray[99].getRed());
			color = Images.YELLOW_TEXT;
			break;
		}

		this.aliveTime = TOTAL_ALIVE_TIME;
		
		setX(getX()- (int) ((this.text.length() * ClientWorld.DAMAGE_FONT_WIDTH + 0.5) / 2));

	}

	@Override
	public void update() {
		if (exists) {
			if (aliveTime-- <= 0) {
				world.addToRemove(this);
				destroy();
				return;
			}
			color = colorArray[(int)((1.0*aliveTime/TOTAL_ALIVE_TIME)*100.0)];
			
			y += vSpeed;
			setY((int) y);
		}
	}

	@Override
	public void destroy() {
		this.exists = false;
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

	public boolean exists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}
	

}
