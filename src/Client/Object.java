package Client;

public class Object {
	
	private int x;
	private int y;
	private String description;
	
	public Object(String description, int x, int y)
	{
		this.x = x;
		this.y = y;
		this.description = description;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getDesc() {
		return description;
	}

	public void setDesc(String description) {
		this.description = description;
	}
	
	
}
