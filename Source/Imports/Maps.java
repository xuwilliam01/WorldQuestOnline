package Imports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import Server.ServerObject;
import Server.ServerWorld;

public class Maps 
{
	/**
	 * ArrayList of maps
	 */
	public static ArrayList<Map> maps = new ArrayList<Map>();
	
	public static void importMaps()
	{
		BufferedReader mapCheck = null;
		try {
			mapCheck = new BufferedReader(new FileReader(new File("Resources","Maps")));
		} catch (FileNotFoundException e3) {
			e3.printStackTrace();
		}
		int noOfMaps = 0;
		try {
			noOfMaps = Integer.parseInt(mapCheck.readLine());
		} catch (NumberFormatException | IOException e2) {
			e2.printStackTrace();
		}
		String mapFile = "";
		BufferedReader worldInput = null;
		
		for (int no = 0; no < noOfMaps; no++)
		{
			try {
				mapFile = mapCheck.readLine().trim().toLowerCase();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				worldInput = new BufferedReader(new FileReader(new File(
						"Resources", mapFile)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			
			StringTokenizer tokenizer = null;
			try {
				tokenizer = new StringTokenizer(worldInput.readLine());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Add to both sides to make room for the invisible walls
			char [][] tileGrid = new char[Integer.parseInt(tokenizer.nextToken()) + 6][Integer
					.parseInt(tokenizer.nextToken()) + 6];

			// Collision grid mirrors tileGrid but is simpler
			char [][] collisionGrid = new char[tileGrid.length][tileGrid[0].length];

			// Make object grid
			@SuppressWarnings("unchecked")
			ArrayList<ServerObject>[][]objectGrid = new ArrayList[tileGrid.length
					/ (ServerWorld.OBJECT_TILE_SIZE / ServerWorld.TILE_SIZE) + 1][tileGrid[0].length
					/ (ServerWorld.OBJECT_TILE_SIZE / ServerWorld.TILE_SIZE) + 1];

			// Initialize each arraylist of objects in the objectGridWo
			for (int row = 0; row < objectGrid.length; row++) {
				for (int column = 0; column < objectGrid[0].length; column++) {
					objectGrid[row][column] = new ArrayList<ServerObject>();
				}
			}

			String line=null;
			for (int row = 3; row < tileGrid.length - 3; row++) {
				try {
					line = worldInput.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				for (int col = 3; col < tileGrid[row].length - 3; col++)
					tileGrid[row][col] = line.charAt(col - 3);
			}

			// Make a border around the grid
			for (int col = 0; col < tileGrid[0].length; col++) {
				tileGrid[0][col] = '_';
				tileGrid[1][col] = '_';
				tileGrid[2][col] = '_';
				tileGrid[tileGrid.length - 1][col] = '_';
				tileGrid[tileGrid.length - 2][col] = '_';
				tileGrid[tileGrid.length - 3][col] = '_';
			}

			// Make a border around the grid
			for (int row = 0; row < tileGrid.length; row++) {
				tileGrid[row][0] = '_';
				tileGrid[row][1] = '_';
				tileGrid[row][2] = '_';
				tileGrid[row][tileGrid[0].length - 1] = '_';
				tileGrid[row][tileGrid[0].length - 2] = '_';
				tileGrid[row][tileGrid[0].length - 3] = '_';
			}

			// Copy tileGrid to collision grid for easier collision detection
			// '#' is a solid tile
			// '_' is a platform tile
			// ' ' is a background tile
			for (int row = 0; row < tileGrid.length; row++) {
				for (int col = 0; col < tileGrid[0].length; col++) {
					if (tileGrid[row][col] >= 'A') {
						collisionGrid[row][col] = ServerWorld.SOLID_TILE;
					} else if (tileGrid[row][col] >= '0'
							|| tileGrid[row][col] == ' ') {
						collisionGrid[row][col] = ServerWorld.BACKGROUND_TILE;
					} else {
						collisionGrid[row][col] = ServerWorld.PLATFORM_TILE;
					}
				}
			}

			ArrayList<String> startingObjects = new ArrayList<String>();
			int numObjects;
			try {
				numObjects = Integer.parseInt(worldInput.readLine());
				for (int object = 0; object < numObjects; object++) {
					startingObjects.add(worldInput.readLine());
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			maps.add(new Map(mapFile, tileGrid, collisionGrid, objectGrid, startingObjects));
		}
		
		try {
			mapCheck.close();
			worldInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Get a map using its name
	 * @param name
	 * @return
	 */
	public static Map getMapWithName(String name)
	{
		for (Map map:maps)
		{
			if (map.getName().equalsIgnoreCase(name))
			{
				return map;
			}
		}
		return null;
	}
}
