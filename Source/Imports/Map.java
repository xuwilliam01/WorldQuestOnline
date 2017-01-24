package Imports;

import java.util.ArrayList;
import java.util.LinkedList;

import Server.ServerObject;

/**
 * A map object
 * @author William
 *
 */
public class Map
{
	private String name;
	private char[][]tileGrid;
	private char[][]collisionGrid;
	private ArrayList<ServerObject>[][]objectGrid;
	private ArrayList<String> startingObjects;
	public Map(String name, char[][] tileGrid, char[][] collisionGrid, ArrayList<ServerObject>[][]objectGrid, ArrayList<String> startingObjects)
	{
		this.name=name;
		this.tileGrid = tileGrid;
		this.collisionGrid = collisionGrid;
		this.objectGrid = objectGrid;
		this.startingObjects = startingObjects;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public char[][] getTileGrid() {
		return tileGrid;
	}
	public void setTileGrid(char[][] tileGrid) {
		this.tileGrid = tileGrid;
	}
	public char[][] getCollisionGrid() {
		return collisionGrid;
	}
	public void setCollisionGrid(char[][] collisionGrid) {
		this.collisionGrid = collisionGrid;
	}
	public LinkedList<ServerObject>[][] getObjectGrid() {
		@SuppressWarnings("unchecked")
		LinkedList<ServerObject>[][]newObjectGrid = new LinkedList[objectGrid.length][objectGrid[0].length];
		for (int row = 0; row < objectGrid.length; row++)
		{
			for (int col = 0; col < objectGrid[0].length; col++)
			{
				newObjectGrid[row][col] = new LinkedList<ServerObject>();
				for (ServerObject object:objectGrid[row][col])
				{
					newObjectGrid[row][col].add(object);
				}
			}
		}
		return newObjectGrid;
	}
	public void setObjectGrid(ArrayList<ServerObject>[][] objectGrid) {
		this.objectGrid = objectGrid;
	}
	public ArrayList<String> getStartingObjects() {
		return startingObjects;
	}
	public void setStartingObjects(ArrayList<String> startingObjects) {
		this.startingObjects = startingObjects;
	}

}