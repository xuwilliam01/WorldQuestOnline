package WorldCreator;

import java.awt.Image;

import Imports.ImageReferencePair;
import Server.ServerWorld;

/**
 * An object in the creator that will be drawn
 * @author Alex Raita & William Xu
 *
 */
public class CreatorWorldObject
{
	/**
	 * The column that the object sits in
	 */
	private int col;
	
	/**
	 * The row that the object sits in
	 */
	private int row;
	private int width;
	private int height;
	private Image image;
	private char ref;

	/**
	 * Constructor
	 */
	public CreatorWorldObject(int row, int col, char ref)
	{
		this.col = col;
		this.row = row;
		this.ref = ref;
		this.image = ImageReferencePair.getImages()[ref].getImage();
		this.width = image.getWidth(null) / ServerWorld.TILE_SIZE;
		this.height = image.getHeight(null) / ServerWorld.TILE_SIZE;
	}

	/**
	 * Checks collisions with another object
	 */
	public boolean collidesWith(int x1, int y1, int x2, int y2)
	{
		if (col < x2 && (col + width) > x1 && row < y2 && (row + height) > y1)
		{
			return true;
		}
		return false;
	}

	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////
	public char getRef()
	{
		return ref;
	}
	public int getCol()
	{
		return col;
	}
	public void setCol(int x)
	{
		this.col = x;
	}
	public int getRow()
	{
		return row;
	}
	public void setRow(int y)
	{
		this.row = y;
	}
	public int getWidth()
	{
		return width;
	}
	public void setWidth(int width)
	{
		this.width = width;
	}
	public int getHeight()
	{
		return height;
	}
	public void setHeight(int height)
	{
		this.height = height;
	}
	public Image getImage()
	{
		return image;
	}
	public void setImage(Image image)
	{
		this.image = image;
	}
}
