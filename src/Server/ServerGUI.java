package Server;

import Server.Items.ServerWeaponSwing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ServerGUI extends JPanel implements KeyListener,
		MouseWheelListener, MouseListener, MouseMotionListener
{

	private ServerWorld world;
	private ServerEngine engine;
	private char[][] grid;
	private int posX = 200;
	private int posY = 300;

	/**
	 * A color that java doesn't provide
	 */
	public static final Color SKY = new Color(51, 255, 255);

	/**
	 * A color that java doesn't provide
	 */
	public static final Color BLOCK = new Color(112, 112, 112);

	/**
	 * The color of a player
	 */
	public static final Color PLAYER = Color.black;

	/**
	 * The color of an NPC
	 */
	public static final Color NPC = Color.darkGray;

	/**
	 * The color of a projectile
	 */
	public static final Color PROJECTILE = Color.blue;

	/**
	 * The color of an item
	 */
	public static final Color ITEM = Color.yellow;

	/**
	 * The color of other objects
	 */
	public static final Color OTHER = Color.WHITE;

	/**
	 * The factor of the scale of the object on the map compared to its actual
	 * height and width (can be changed by scrolling mouse wheel)
	 */
	private double objectFactor;

	/**
	 * X-value of the centre of the screen
	 */
	public static final int CENTRE_X = Client.Client.SCREEN_WIDTH
			/ ServerFrame.FRAME_FACTOR / 2;

	/**
	 * Y-value of the centre of the screen
	 */
	public static final int CENTRE_Y = Client.Client.SCREEN_HEIGHT
			/ ServerFrame.FRAME_FACTOR / 2;

	/**
	 * The x-coordinate of where the mouse began to be dragged from
	 */
	private int dragSourceX;

	/**
	 * The y-coordinate of where the mouse began to be dragged from
	 */
	private int dragSourceY;

	// Movement booleans
	private boolean up = false;
	private boolean down = false;
	private boolean left = false;
	private boolean right = false;

	public ServerGUI(ServerWorld world, ServerEngine engine)
	{
		// Set the scale of objects
		objectFactor = ServerFrame.FRAME_FACTOR * 8;

		// Create the screen
		setDoubleBuffered(true);
		setBackground(Color.white);
		setSize(Client.Client.SCREEN_WIDTH, Client.Client.SCREEN_HEIGHT);

		setFocusable(true);
		requestFocusInWindow();

		// Set world, engine and grid
		this.world = world;
		this.engine = engine;

		grid = world.getGrid();

		// Add key, mouse wheel listener and repaint timer
		addKeyListener(this);
		addMouseWheelListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

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
					graphics.setColor(SKY);
				}
				else
				{
					graphics.setColor(BLOCK);
				}

				graphics.fillRect(
						(int) (CENTRE_X + column
								* (ServerWorld.TILE_SIZE / objectFactor) - posX) + 1,
						(int) (CENTRE_Y + row
								* (ServerWorld.TILE_SIZE / objectFactor) - posY) + 1,
						(int) (ServerWorld.TILE_SIZE / objectFactor) + 1,
						(int) (ServerWorld.TILE_SIZE / objectFactor) + 1);
			}
		}

		// Draw each object on the gui if it's inside the screen
		for (ServerObject object : world.getObjects())
		{
			if (object.getType().contains(ServerWorld.WEAPON_SWING_TYPE))
			{
				Line2D.Double hitbox = ((ServerWeaponSwing) object).getHitbox();
				graphics.setColor(Color.black);
				graphics.drawLine((int) hitbox.getX1(), (int) hitbox.getY1(),
						(int) hitbox.getX2(), (int) hitbox.getY2());
			}

			if (object.isMapVisible()
					&& ((CENTRE_X + object.getX() / objectFactor - posX)
							+ 1
							+ (object.getWidth() / objectFactor) + 1) > 0
					&& ((CENTRE_X + object.getX() / objectFactor - posX) + 1) < Client.Client.SCREEN_WIDTH
					&& ((CENTRE_Y + object.getY() / objectFactor - posY)
							+ 1
							+ (object.getHeight() / objectFactor) + 1) > 0
					&& ((CENTRE_Y + object.getY() / objectFactor - posY) + 1) < Client.Client.SCREEN_HEIGHT)
			{
				if (object.getType().charAt(0) == ServerWorld.PROJECTILE_TYPE)
				{
					graphics.setColor(PROJECTILE);
				}
				else if (object.getType().charAt(0) == ServerWorld.ITEM_TYPE)
				{
					graphics.setColor(ITEM);
				}
				else if (object.getType().contains(ServerWorld.NPC_TYPE))
				{
					graphics.setColor(NPC);
				}
				else if (object.getType().contains(ServerWorld.PLAYER_TYPE))
				{
					graphics.setColor(PLAYER);
				}
				else
				{
					graphics.setColor(OTHER);
				}
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
				"Use mouse or arrows keys to move around the map, zoom with the mouse wheel",
				10, 25);
		graphics.drawString(
				"FPS: " + engine.getCurrentFPS(),
				10, 40);
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

		if (notches > 0)
		{
			if (objectFactor * (1.1 * (notches)) < ServerFrame.FRAME_FACTOR * 16)
			{
				objectFactor *= (1.1 * (notches));
				posX /= 1.1;
				posY /= 1.1;
			}
			else
			{
				posX /= ServerFrame.FRAME_FACTOR * 16 / objectFactor;
				posY /= ServerFrame.FRAME_FACTOR * 16 / objectFactor;
				objectFactor = ServerFrame.FRAME_FACTOR * 16;
			}
		}
		else if (notches < 0)
		{
			if (objectFactor / (1.1 * (-notches)) >= 1)
			{
				objectFactor /= (1.1 * (-notches));
				posX *= 1.1;
				posY *= 1.1;
			}
			else
			{
				posX *= objectFactor;
				posY *= objectFactor;
				objectFactor = 1;
			}

		}
	}

	@Override
	public void mouseClicked(MouseEvent event)
	{

	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent event)
	{
		if (event.getButton() == MouseEvent.BUTTON1)
		{
			dragSourceX = event.getX();
			dragSourceY = event.getY();
		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent event)
	{
		posX -= event.getX() - dragSourceX;
		posY -= event.getY() - dragSourceY;
		dragSourceX = event.getX();
		dragSourceY = event.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	public double getObjectFactor()
	{
		return objectFactor;
	}

	public void setObjectFactor(double objectFactor)
	{
		this.objectFactor = objectFactor;
	}

}
