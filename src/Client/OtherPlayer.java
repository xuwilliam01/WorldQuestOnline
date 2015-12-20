package Client;

import java.awt.Color;

/**
 * An extension of the object class that deals with other players in the game
 * @author Alex Raita & William Xu
 */
public class OtherPlayer extends ClientObject{
	private Color colour;
	private int playerNum;
	
	//Players will have more variables as game is developed
	public OtherPlayer(String description, int x, int y, Color colour, int playerNum, int identifier) {
		super(description, x, y, identifier);
		this.colour = colour;
		this.playerNum = playerNum;
	}

	public Color getColour() {
		return colour;
	}

	public void setColour(Color colour) {
		this.colour = colour;
	}

}
