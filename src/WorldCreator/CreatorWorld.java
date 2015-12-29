package WorldCreator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import Imports.Images;

public class CreatorWorld extends JPanel implements KeyListener, ActionListener, MouseListener{

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

	//Adding/removing tile variables
	private char selectedTile = '-';
	private int[] selectedBlock = null;
	private boolean addingTile = false;
	private boolean removingTile = false;

	//Variables for changing grid size
	private boolean isNewHeight = false;
	private boolean isNewWidth = false;
	private int newHeight;
	private int newWidth;

	/**
	 * Table to reference objects by character
	 */
	private CreatorObject[] tiles = new CreatorObject[256];

	public CreatorWorld() throws NumberFormatException, IOException
	{
		addKeyListener(this);
		addMouseListener(this);
		setDoubleBuffered(true);
		setBackground(Color.black);

		setLayout(null);
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
	}

	public char[][] getGrid() {
		return grid;
	}

	public void setGrid(char[][] grid) {
		this.grid = grid;
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

		//Create new height and width if necessary
		if(isNewHeight)
		{
			setHeight(newHeight);
			isNewHeight = false;
		}

		if(isNewWidth)
		{
			setWidth(newWidth);
			isNewWidth = false;
		}

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
		if (endColumn >= grid[0].length)
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

		graphics.setColor(Color.white);
		graphics.drawString("Use arrow keys to scroll", 10, 20);
		graphics.drawString("Select a tile using the mouse", 10, 30);
		graphics.drawString("Place tiles using left click", 10, 40);
		graphics.drawString("Delete tiles using right click", 10, 50);
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

	public void save() throws FileNotFoundException
	{
		PrintWriter output = new PrintWriter(new File("Resources","NewWorld.txt"));
		output.println(grid.length+" "+grid[0].length);
		for(int row = 0; row < grid.length;row++)
		{
			for(int col = 0; col < grid[0].length;col++)
				output.print(grid[row][col]);
			output.println();
		}
		output.close();
	}

	public void setHeight(int height)
	{
		char[][] currentGrid = grid.clone();
		grid = new char[height][grid[0].length];

		for(int row = 0; row < grid.length;row++)
			for(int col = 0; col < grid[0].length;col++)
			{
				if(row < currentGrid.length)
					grid[row][col] = currentGrid[row][col];
				else
					grid[row][col] = ' ';
			}
	}

	public void setWidth(int width)
	{
		char[][] currentGrid = grid.clone();
		grid = new char[grid.length][width];

		for(int row = 0; row < grid.length;row++)
			for(int col = 0; col < grid[0].length;col++)
			{
				if(col < currentGrid[0].length)
					grid[row][col] = currentGrid[row][col];
				else
					grid[row][col] = ' ';
			}
	}

	public void setNewHeight(int newHeight) {
		isNewHeight = true;
		this.newHeight = newHeight;
	}

	public void setNewWidth(int newWidth) {
		isNewWidth = true;
		this.newWidth = newWidth;
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
		if(this.hasFocus())
			selectedBlock = getRowCol((int)(MouseInfo.getPointerInfo().getLocation().getX()- this.getLocationOnScreen().getX()),(int)(MouseInfo.getPointerInfo().getLocation().getY()-this.getLocationOnScreen().getY()));

		if(selectedBlock != null && selectedBlock[0] >= 0 && selectedBlock[0] < grid.length && selectedBlock[1] >= 0 && selectedBlock[1] < grid[0].length)
			if(addingTile && grid[selectedBlock[0]][selectedBlock[1]] == ' ')
				grid[selectedBlock[0]][selectedBlock[1]] = selectedTile;
			else if(removingTile)
				grid[selectedBlock[0]][selectedBlock[1]] = ' ';

		update();
		requestFocusInWindow();		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent event) {
		if(event.getButton() == MouseEvent.BUTTON1 && selectedTile != '-')
			addingTile = true;
		else if(event.getButton() == MouseEvent.BUTTON3)
			removingTile = true;

		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if(event.getButton() == MouseEvent.BUTTON1)
			addingTile = false;
		else if(event.getButton() == MouseEvent.BUTTON3)
			removingTile = false;

	}
}

