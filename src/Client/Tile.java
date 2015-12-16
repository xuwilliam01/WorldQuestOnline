package Client;

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


}
