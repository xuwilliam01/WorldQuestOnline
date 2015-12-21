package Server;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import Client.Client;
import Client.ClientObject;
import Imports.Images;

public class ServerGUI extends JPanel implements KeyListener, ActionListener{

	private ServerWorld world;
	private char[][] grid;
	private int posX = 200;
	private int posY = 200;
	Timer repaintTimer;

	//Movement booleans
	private boolean up = false;
	private boolean down = false;
	private boolean left = false;
	private boolean right = false;
	
	public ServerGUI(ServerWorld world)
	{
		// Create the screen
		setDoubleBuffered(true);
		setBackground(Color.white);

		setFocusable(true);
		requestFocusInWindow();

		//Set world and grid
		this.world = world;
		grid = world.getGrid();

		//Add key listener and repaint timer
		addKeyListener(this);
		repaintTimer = new Timer(Engine.UPDATE_RATE,this);
		repaintTimer.start();


	}
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		//Center of the screen
		int centreX = ServerPlayer.SCREEN_WIDTH/ServerFrame.FRAME_FACTOR/2;
		int centreY = ServerPlayer.SCREEN_HEIGHT/ServerFrame.FRAME_FACTOR/2;
		int tileSize = ServerWorld.TILE_SIZE/ServerFrame.FRAME_FACTOR/2;

		// Draw tiles (draw based on player's position later)
		int startRow = (int)((posY - centreY-5)/tileSize);
		if (startRow < 0)
		{
			startRow = 0;
		}
		int endRow = (int)((centreY+posY+5)/tileSize);
		if (endRow >= grid.length)
		{
			endRow = grid.length-1;
		}
		int startColumn = (int)((posX - centreX-5)/tileSize);
		if (startColumn < 0)
		{
			startColumn = 0;
		}
		int endColumn = (int)((centreX+posX+5)/tileSize);
		if (endColumn >= grid.length)
		{
			endColumn= grid[0].length-1;
		}
		for (int row = startRow; row <= endRow; row++)
		{
			for (int column = startColumn; column <= endColumn; column++)
			{
				if (grid[row][column]=='0')
				{
					graphics.setColor(Color.GREEN);
					graphics.fillRect(centreX + column* tileSize - posX, centreY + row*tileSize - posY,tileSize,tileSize);
				}
				else if (grid[row][column]=='1')
				{
					graphics.setColor(Color.RED);
					graphics.fillRect(centreX + column* tileSize - posX, centreY + row*tileSize - posY,tileSize,tileSize);
				}
			}
		}

		// Go through each object in the world and draw it relative to the
		// player's position
		for (ServerObject object : world.getObjects())
		{		
			graphics.setColor(Color.BLACK);
			graphics.fillRect(centreX + object.getX()/ServerFrame.FRAME_FACTOR/2 - posX, centreY + object.getY()/ServerFrame.FRAME_FACTOR/2 - posY,object.getWidth()/ServerFrame.FRAME_FACTOR/2,object.getHeight()/ServerFrame.FRAME_FACTOR/2);
				
		}

	}

	public void keyPressed(KeyEvent key) {
		System.out.println("working");
		if (key.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			right = true;	
		}
		else if (key.getKeyCode() == KeyEvent.VK_LEFT)
		{
			left = true;
		}
		else if (key.getKeyCode() == KeyEvent.VK_UP)
		{
			up = true;
		}
		else if (key.getKeyCode() == KeyEvent.VK_DOWN)
		{
			down = true;
		}	
	}

	public void keyReleased(KeyEvent key) {
		
		if (key.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			right = false;	
		}
		else if (key.getKeyCode() == KeyEvent.VK_LEFT)
		{
			left= false;	
		}
		else if (key.getKeyCode() == KeyEvent.VK_UP)
		{
			up = false;	
		}
		else if (key.getKeyCode() == KeyEvent.VK_DOWN)
		{
			down = false;	
		}	
		
	}

	public void keyTyped(KeyEvent arg0) {
	}

	public void movePos()
	{
		if(right)
			posX+=10;
		else if(left)
			posX-=10;
		if(up)
			posY-=10;
		else if(down)
			posY+=10;
	}
	
	public void actionPerformed(ActionEvent arg0) {
		//Move and repaint
		movePos();
		repaint();

	}
}
