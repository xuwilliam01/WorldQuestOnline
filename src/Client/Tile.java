package Client;

/**
 * An extension of the object class that pertains to tiles
 * @author Alex Raita & William Xu
 */
public class Tile extends Object{

	private int type;
	
	public Tile(String description, int type, int x, int y) {
		super(description, x, y);
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int compareTo(Object o) {
		if(super.compareTo(o) == 0 && type == ((Tile)o).type)
			return 0;
		return -1;
	}

}
