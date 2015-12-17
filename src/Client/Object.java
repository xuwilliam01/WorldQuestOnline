package Client;

/**
 * A class that acts as a blueprint for all objects in the game
 * @author Alex Raita & William Xu
 */
public class Object implements Comparable<Object> {

	private int x;
	private int y;
	private String description;

	/**
	 * Constructor
	 */
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


	public int compareTo(Object o) {
		if(x == o.x && y == o.y && description.equals(o.description))
			return 0;
		return -1;
	}


}
