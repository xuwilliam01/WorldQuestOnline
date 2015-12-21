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
	private int posX = 500;
	private int posY = 500;
	Timer repaintTimer;

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
		int centreX = ServerPlayer.SCREEN_WIDTH/2;
		int centreY = ServerPlayer.SCREEN_HEIGHT/2;

		// Draw tiles (draw based on player's position later)
		int startRow = (int)((posY - centreY-5)/world.TILE_SIZE);
		if (startRow < 0)
		{
			startRow = 0;
		}
		int endRow = (int)((centreY+posY+5)/world.TILE_SIZE);
		if (endRow >= grid.length)
		{
			endRow = grid.length-1;
		}
		int startColumn = (int)((posX - centreX-5)/world.TILE_SIZE);
		if (startColumn < 0)
		{
			startColumn = 0;
		}
		int endColumn = (int)((centreX+posX+5)/world.TILE_SIZE);
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
					graphics.fillRect(centreX + column* world.TILE_SIZE - posX, centreY + row*world.TILE_SIZE - posY,world.TILE_SIZE,world.TILE_SIZE);
				}
				else if (grid[row][column]=='1')
				{
					graphics.setColor(Color.RED);
					graphics.fillRect(centreX + column* world.TILE_SIZE - posX, centreY + row*world.TILE_SIZE - posY,world.TILE_SIZE,world.TILE_SIZE);
				}
			}
		}

		// Go through each object in the world and draw it relative to the
		// player's position
		for (ServerObject object : world.getObjects())
		{			
			//graphics.drawImage(object.getImage(), centreX + object.getX() - posX, centreY + object.getY() - posY,
				//	null);
		}

	}

	public void keyPressed(KeyEvent key) {
		System.out.println("working");
		if (key.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			posX += 20;		
		}
		else if (key.getKeyCode() == KeyEvent.VK_LEFT)
		{
			posX -= 20;
		}
		else if (key.getKeyCode() == KeyEvent.VK_UP)
		{
			posY -= 20;
		}
		else if (key.getKeyCode() == KeyEvent.VK_DOWN)
		{
			posY += 20;
		}	
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}

	public void actionPerformed(ActionEvent arg0) {
		repaint();

	}
}
