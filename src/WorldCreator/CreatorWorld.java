package WorldCreator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
	private int posY = 0;
	private int posX = 0;

	//Scrolling variables
	private boolean up = false;
	private boolean down = false;
	private boolean right = false;
	private boolean left = false;
	private Timer scrollTimer;
	/**
	 * Table to reference objects by character
	 */
	private CreatorObject[] tiles = new CreatorObject[256];

	public CreatorWorld() throws NumberFormatException, IOException
	{
		setDoubleBuffered(true);
		setBackground(Color.black);

		setFocusable(true);
		requestFocusInWindow();
		setSize(WIDTH,HEIGHT);

		clearGrid();
		Images.importImages();
		readImages();
		
		addKeyListener(this);
		scrollTimer = new Timer(15,this);
		scrollTimer.start();
	}

	public void clearGrid()
	{
		for(int row = 0; row < grid.length;row++)
			for(int col = 0; col < grid[row].length;col++)
				grid[row][col] = ' ';
	}

	public void readImages() throws NumberFormatException, IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("WorldCreator.cfg"));
		int numTiles = Integer.parseInt(br.readLine());
		for(int tile = 0; tile < numTiles;tile++)
		{
			String line = br.readLine();
			tiles[line.charAt(0)] = new CreatorObject(line.charAt(0),line.substring(2));
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
	}
	@Override
	public void keyPressed(KeyEvent event) {
		System.out.println("working");
		if(event.getKeyCode() == KeyEvent.VK_UP)
			up = true;
		else if(event.getKeyCode() == KeyEvent.VK_DOWN)
			down = true;
		else if (event.getKeyCode() == KeyEvent.VK_RIGHT)
			right = true;
		else if(event.getKeyCode() == KeyEvent.VK_LEFT)
			left = true;
	}

	@Override
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
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		update();
		System.out.println(posX+" "+posY);
		
	}
}

