package WorldCreator;

import java.awt.Image;

import Server.ServerWorld;

public class CreatorWorldObject {

	private int col;
	private int row;
	private int width;
	private int height;
	private Image image;
	private char ref;
	
	public CreatorWorldObject(int row, int col, Image image, char ref)
	{
		this.col = col;
		this.row = row;
		this.ref = ref;
		this.width = image.getWidth(null)/ServerWorld.TILE_SIZE;
		this.height = image.getHeight(null)/ServerWorld.TILE_SIZE;
		this.image = image;
	}

	public boolean collidesWith(int x1, int y1, int x2, int y2)
	{
		if (col < x2 && (col + width) > x1 && row < y2 && (row + height) > y1)
		{
			return true;
		}
		return false;
	}
	
	public char getRef()
	{
		return ref;
	}
	
	public int getCol() {
		return col;
	}

	public void setCol(int x) {
		this.col = x;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int y) {
		this.row = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
	
	
	
}
