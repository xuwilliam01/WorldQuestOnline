package Client;

import java.awt.Color;

/**
 * An extension of the object class that deals with other players in the game
 * @author Alex Raita & William Xu
 */
public class OtherPlayer extends Object{
	private Color colour;
	private int playerNum;
	
	//Players will have more variables as game is developed
	public OtherPlayer(String description, int x, int y, Color colour, int playerNum) {
		super(description, x, y);
		this.colour = colour;
		this.playerNum = playerNum;
	}

	public Color getColour() {
		return colour;
	}

	public void setColour(Color colour) {
		this.colour = colour;
	}
	
	public int compareTo(Object o) {
		if(colour.equals(((OtherPlayer)o).colour) && playerNum == ((OtherPlayer)o).playerNum)
			return 0;
		return -1;
	}

}
