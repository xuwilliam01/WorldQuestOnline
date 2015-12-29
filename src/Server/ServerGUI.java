package Server;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.Timer;

public class ServerGUI extends JPanel implements KeyListener,
		MouseWheelListener
{

	private ServerWorld world;
	private char[][] grid;
	private int posX = 300;
	private int posY = 200;

	/**
	 * A color that java doesn't provide
	 */
	public static final Color GRASS = new Color(0, 224, 0);

	/**
	 * A color that java doesn't provide
	 */
	public static final Color BRICK = new Color(214, 36, 0);

	/**
	 * The color of a player
	 */
	public static final Color PLAYER = Color.black;

	/**
	 * The factor of the scale of the object on the map compared to its actual
	 * height and width (can be changed by scrolling mouse wheel)
	 */
	private double objectFactor;

	/**
	 * X-value of the centre of the screen
	 */
	public static final int CENTRE_X = ServerPlayer.SCREEN_WIDTH
			/ ServerFrame.FRAME_FACTOR / 2;

	/**
	 * Y-value of the centre of the screen
	 */
	public static final int CENTRE_Y = ServerPlayer.SCREEN_HEIGHT
			/ ServerFrame.FRAME_FACTOR / 2;

	// Movement booleans
	private boolean up = false;
	private boolean down = false;
	private boolean left = false;
	private boolean right = false;

	public ServerGUI(ServerWorld world)
	{
		// Set the scale of objects
		objectFactor = ServerFrame.FRAME_FACTOR * 4;

		// Create the screen
		setDoubleBuffered(true);
		setBackground(Color.white);

		setFocusable(true);
		requestFocusInWindow();

		// Set world and grid
		this.world = world;
		grid = world.getGrid();

		// Add key, mouse wheel listener and repaint timer
		addKeyListener(this);
		addMouseWheelListener(this);
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		// Draw tiles (draw based on player's position later)
		int startRow = (int) ((posY - CENTRE_Y - 5) / (ServerWorld.TILE_SIZE / objectFactor));
		if (startRow < 0)
		{
			startRow = 0;
		}
		int endRow = (int) ((CENTRE_Y + posY + 5) / (ServerWorld.TILE_SIZE / objectFactor));
		if (endRow >= grid.length)
		{
			endRow = grid.length - 1;
		}
		int startColumn = (int) ((posX - CENTRE_X - 5) / (ServerWorld.TILE_SIZE / objectFactor));
		if (startColumn < 0)
		{
			startColumn = 0;
		}
		int endColumn = (int) ((CENTRE_X + posX + 5) / (ServerWorld.TILE_SIZE / objectFactor));
		if (endColumn >= grid[0].length)
		{
			endColumn = grid[0].length - 1;
		}
		for (int row = startRow; row <= endRow; row++)
		{
			for (int column = startColumn; column <= endColumn; column++)
			{
				if (grid[row][column] == ' ')
				{
					graphics.setColor(GRASS);
					graphics.fillRect(
							(int) (CENTRE_X + column
									* (ServerWorld.TILE_SIZE / objectFactor) - posX) + 1,
							(int) (CENTRE_Y + row
									* (ServerWorld.TILE_SIZE / objectFactor) - posY) + 1,
							(int) (ServerWorld.TILE_SIZE / objectFactor) + 1,
							(int) (ServerWorld.TILE_SIZE / objectFactor) + 1);
				}
				else if (grid[row][column] == '1')
				{
					graphics.setColor(BRICK);
					graphics.fillRect(
							(int) (CENTRE_X + column
									* (ServerWorld.TILE_SIZE / objectFactor) - posX) + 1,
							(int) (CENTRE_Y + row
									* (ServerWorld.TILE_SIZE / objectFactor) - posY) + 1,
							(int) (ServerWorld.TILE_SIZE / objectFactor) + 1,
							(int) (ServerWorld.TILE_SIZE / objectFactor) + 1);
				}
			}
		}

		// Go through each object in the world and draw it relative to the
		// player's position
		for (ServerObject object : world.getObjects())
		{
			if (object.isMapVisible())
			{
				graphics.setColor(PLAYER);
				graphics.fillRect(
						(int) (CENTRE_X + object.getX() / objectFactor - posX) + 1,
						(int) (CENTRE_Y + object.getY() / objectFactor - posY) + 1,
						(int) (object.getWidth() / objectFactor) + 1,
						(int) (object.getHeight() / objectFactor) + 1);
			}
		}

		// Tell the user to scroll with arrow keys
		graphics.setColor(Color.black);
		graphics.drawString(
				"Scroll with the arrow keys, zoom with the mouse wheel", 10, 25);
	}

	public void keyPressed(KeyEvent key)
	{
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

	public void keyReleased(KeyEvent key)
	{

		if (key.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			right = false;
		}
		else if (key.getKeyCode() == KeyEvent.VK_LEFT)
		{
			left = false;
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

	public void keyTyped(KeyEvent arg0)
	{
	}

	/**
	 * Scroll the minimap
	 */
	public void movePos()
	{
		if (right)
		{
			posX += 10;
		}
		else if (left)
		{
			posX -= 10;
		}
		if (up)
		{
			posY -= 10;
		}
		else if (down)
		{
			posY += 10;
		}
	}

	/**
	 * Update the map
	 */
	public void update()
	{
		// Move and repaint
		requestFocusInWindow();
		movePos();
		repaint();
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent scroll)
	{
		int notches = scroll.getWheelRotation();

		if (notches > 0 && objectFactor < ServerFrame.FRAME_FACTOR * 16)
		{
			objectFactor *= (1.1 * notches);
		}
		else if (notches < 0 && objectFactor > ServerFrame.FRAME_FACTOR)
		{
			objectFactor /= (1.1 * (-notches));
		}
	}
}
