package WorldCreator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.Timer;

import Imports.Images;

public class CreatorWorld extends JPanel implements KeyListener, ActionListener{

	public static final int WIDTH = 1024;
	public static final int HEIGHT = 768;
	public static final int TILE_SIZE = 16; 

	public static final int CENTRE_X = WIDTH/2;
	public static final int CENTRE_Y = HEIGHT/2;

	private final int SCROLL_SPEED = 20;

	private char[][] grid = new char[HEIGHT/TILE_SIZE + 1][WIDTH/TILE_SIZE+1];
	private int posY = 200;
	private int posX = 200;

	//Scrolling variables
	private boolean up = false;
	private boolean down = false;
	private boolean right = false;
	private boolean left = false;
	private Timer scrollTimer;
	private char selectedTile = '-';
	private int[] selectedBlock = null;

	/**
	 * Table to reference objects by character
	 */
	private CreatorObject[] tiles = new CreatorObject[256];

	public CreatorWorld() throws NumberFormatException, IOException
	{
		addKeyListener(this);
		setDoubleBuffered(true);
		setBackground(Color.black);

		setFocusable(true);
		requestFocusInWindow();
		setSize(WIDTH,HEIGHT);

		clearGrid();
		Images.importImages();
		readImages();


		scrollTimer = new Timer(15,this);
		scrollTimer.start();
	}

	public void clearGrid()
	{
		for(int row = 0; row < grid.length;row++)
			for(int col = 0; col < grid[row].length;col++)
				grid[row][col] = ' ';

		grid[5][5] = '1';
	}

	public void readImages() throws NumberFormatException, IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("WorldCreator.cfg"));
		int numTiles = Integer.parseInt(br.readLine());
		for(int tile = 0; tile < numTiles;tile++)
		{
			String line = br.readLine();
			tiles[line.charAt(0)] = new CreatorObject(line.charAt(0),line.substring(2),this);
		}
		br.close();
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		// Draw tiles (draw based on player's position later)
		int startRow = (int) ((posY - CENTRE_Y - 5) / TILE_SIZE);
		if (startRow < 0)
		{
			startRow = 0;
		}
		int endRow = (int) ((CENTRE_Y + posY + 5) / TILE_SIZE);
		if (endRow >= grid.length)
		{
			endRow = grid.length - 1;
		}
		int startColumn = (int) ((posX - CENTRE_X - 5) / TILE_SIZE);
		if (startColumn < 0)
		{
			startColumn = 0;
		}
		int endColumn = (int) ((CENTRE_X + posX + 5) / TILE_SIZE);
		if (endColumn >= grid.length)
		{
			endColumn = grid[0].length - 1;
		}
		for (int row = startRow; row <= endRow; row++)
		{
			for (int column = startColumn; column <= endColumn; column++)
			{		
				graphics.drawImage(tiles[grid[row][column]].getImage(),
						(int) (CENTRE_X + column
								* TILE_SIZE - posX) + 1,
						(int) (CENTRE_Y + row
								* TILE_SIZE - posY) + 1,
						null);
			}
		}

		if(selectedTile != '-' && selectedBlock != null && selectedBlock[0] >= startRow && selectedBlock[0] <= endRow && selectedBlock[1] >= startColumn && selectedBlock[1] <= endColumn)
		{
			graphics.setColor(Color.white);
			graphics.drawRect((int) (CENTRE_X + selectedBlock[1]
					* TILE_SIZE - posX) + 1,
			(int) (CENTRE_Y + selectedBlock[0]
					* TILE_SIZE - posY) + 1,TILE_SIZE,TILE_SIZE);

		}

	}

	public void update()
	{
		if(up)
			posY -= SCROLL_SPEED;
		else if(down)
			posY += SCROLL_SPEED;

		if(right)
			posX += SCROLL_SPEED;
		else if(left)
			posX -= SCROLL_SPEED;

		repaint();
	}

	public int[] getRowCol(int x, int y)
	{
		int col = (x - CENTRE_X + posX)/TILE_SIZE;
		int row = (y - CENTRE_Y + posY)/TILE_SIZE;
		return new int[]{row,col};
	}
	public CreatorObject[] getTiles() {
		return tiles;
	}

	public void setTiles(CreatorObject[] tiles) {
		this.tiles = tiles;
	}


	public char getSelectedTile() {
		return selectedTile;
	}

	public void setSelectedTile(char selectedTile) {

		if(this.selectedTile != '-')
			tiles[this.selectedTile].deselect();
		this.selectedTile = selectedTile;
	}

	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_UP)
			up = true;
		else if(event.getKeyCode() == KeyEvent.VK_DOWN)
			down = true;
		else if (event.getKeyCode() == KeyEvent.VK_RIGHT)
			right = true;
		else if(event.getKeyCode() == KeyEvent.VK_LEFT)
			left = true;
	}

	public void keyReleased(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_UP)
			up = false;
		else if(event.getKeyCode() == KeyEvent.VK_DOWN)
			down = false;
		else if(event.getKeyCode() == KeyEvent.VK_RIGHT)
			right = false;
		else if(event.getKeyCode() == KeyEvent.VK_LEFT)
			left = false;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		selectedBlock = getRowCol((int)(MouseInfo.getPointerInfo().getLocation().getX()- this.getLocationOnScreen().getX()),(int)(MouseInfo.getPointerInfo().getLocation().getY()-this.getLocationOnScreen().getY()));
		update();
		requestFocusInWindow();		
	}
}

