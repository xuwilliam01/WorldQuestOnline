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
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import Imports.ImageReferencePair;
import Imports.Images;
import Server.ServerFrame;
import Server.ServerGUI;
import Server.ServerObject;
import Server.ServerWorld;
import Server.Creatures.ServerSlime;

public class CreatorWorld extends JPanel implements KeyListener,
ActionListener, MouseWheelListener, MouseListener, MouseMotionListener
{

	public final static int SCROLL_SPEED = 13;

	public final static Color LIGHT_GRAY = Color.LIGHT_GRAY;

	public final static double MIN_EDIT_ZOOM = ServerFrame.FRAME_FACTOR * 2.5;

	private char[][] grid = new char[Client.Client.SCREEN_HEIGHT
	                                 / ServerWorld.TILE_SIZE + 1][Client.Client.SCREEN_WIDTH
	                                                              / ServerWorld.TILE_SIZE + 1];
	private int posY = 200;
	private int posX = 200;

	//Objects in world
	ArrayList<CreatorWorldObject> objects = new ArrayList<CreatorWorldObject>();

	// Scrolling variables
	private boolean up = false;
	private boolean down = false;
	private boolean right = false;
	private boolean left = false;
	boolean ctrlPressed = false;
	private Timer scrollTimer;

	// Adding/removing tile variables
	private char selectedTile = '-';
	private int[] selectedBlock = null;
	private int[] startingBlock = null;
	private boolean rightClick = false;
	private boolean leftClick = false;
	private boolean addingTile = false;
	private boolean removingTile = false;
	private boolean highlightingArea = false;
	private boolean highlight = false;
	private boolean isEditable = true;
	private boolean canDrawObject = false;

	// Variables for changing grid size
	private boolean isNewHeight = false;
	private boolean isNewWidth = false;
	private int newHeight;
	private int newWidth;

	/**
	 * The factor of the scale of the object on the map compared to its actual
	 * height and width (can be changed by scrolling mouse wheel)
	 */
	private double objectFactor;

	/**
	 * The x-coordinate of where the mouse began to be dragged from
	 */
	private int dragSourceX;

	/**
	 * The y-coordinate of where the mouse began to be dragged from
	 */
	private int dragSourceY;

	// File
	String fileName;

	/**
	 * Table to reference objects by character
	 */
	private CreatorObject[] tiles = new CreatorObject[256];

	public CreatorWorld(String fileName) throws NumberFormatException,
	IOException
	{	
		Images.importImages();
		ImageReferencePair.importReferences();
		
		setDoubleBuffered(true);
		setBackground(Color.black);

		setLayout(null);
		setFocusable(true);
		requestFocusInWindow();
		setSize(Client.Client.SCREEN_WIDTH, Client.Client.SCREEN_HEIGHT);

		// Set the scale of objects
		objectFactor = ServerFrame.FRAME_FACTOR;

		this.fileName = fileName;

		// Check if the file already exists
		File file = new File("Resources",fileName);
		if (file.exists() && !file.isDirectory())
			importGrid();
		else
			clearGrid();

		readImages();

		scrollTimer = new Timer(10, this);
		scrollTimer.start();

		addKeyListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		addMouseMotionListener(this);
	}

	public void importGrid() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(new File(
				"Resources", fileName)));
		String line = br.readLine();
		String[] tokens = line.split(" ");
		grid = new char[Integer.parseInt(tokens[0])][Integer
		                                             .parseInt(tokens[1])];

		//Add tiles
		for (int row = 0; row < grid.length; row++)
		{
			line = br.readLine();
			for (int col = 0; col < grid[0].length; col++)
			{
				grid[row][col] = line.charAt(col);
				System.out.print(grid[row][col]);
			}
			System.out.println();

		}
		
		//Add objects
		int numObjects = Integer.parseInt(br.readLine());
		for(int obj = 0; obj < numObjects;obj++)
		{
			tokens = br.readLine().split(" ");
			int row = Integer.parseInt(tokens[0]);
			int col = Integer.parseInt(tokens[1]);
			
			objects.add(new CreatorWorldObject(row,col,tokens[2].charAt(0)));
		}
		br.close();
	}

	public void clearGrid()
	{
		for (int row = 0; row < grid.length; row++)
			for (int col = 0; col < grid[row].length; col++)
				grid[row][col] = ' ';
		objects.clear();
	}

	public char[][] getGrid()
	{
		return grid;
	}

	public void setGrid(char[][] grid)
	{
		this.grid = grid;
	}
	
	public void readImages() throws NumberFormatException, IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(new File("Resources",
				"WorldCreator.cfg")));
		int numTiles = Integer.parseInt(br.readLine());
		for (int tile = 0; tile < numTiles; tile++)
		{
			String fullLine = br.readLine();
			String[] line = fullLine.substring(2).split(" ");
			String toolTip = line[2];
			for(int word = 3; word < line.length;word++)
				toolTip+= " "+line[word];
			tiles[fullLine.charAt(0)] = new CreatorObject(fullLine.charAt(0),
					line[0],Boolean.parseBoolean(line[1]),toolTip, this);
		}
		br.close();
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		// Draw the background
		graphics.drawImage(Images.getImage("BACKGROUND.png"), 0, 0, null);

		// Create new height and width if necessary
		if (isNewHeight)
		{
			setHeight(newHeight);
			isNewHeight = false;
		}

		if (isNewWidth)
		{
			setWidth(newWidth);
			isNewWidth = false;
		}

		// Draw tiles
		int startRow = (int) ((posY - ServerGUI.CENTRE_Y - 5) / (ServerWorld.TILE_SIZE / objectFactor));
		if (startRow < 0)
		{
			startRow = 0;
		}
		int endRow = (int) ((ServerGUI.CENTRE_Y + posY + 5) / (ServerWorld.TILE_SIZE / objectFactor));
		if (endRow >= grid.length)
		{
			endRow = grid.length - 1;
		}
		int startColumn = (int) ((posX - ServerGUI.CENTRE_X - 5) / (ServerWorld.TILE_SIZE / objectFactor));
		if (startColumn < 0)
		{
			startColumn = 0;
		}
		int endColumn = (int) ((ServerGUI.CENTRE_X + posX + 5) / (ServerWorld.TILE_SIZE / objectFactor));
		if (endColumn >= grid[0].length)
		{
			endColumn = grid[0].length - 1;
		}

		if (isEditable)
		{
			for (int row = startRow; row <= endRow; row++)
			{
				for (int column = startColumn; column <= endColumn; column++)
				{
					//					if (grid[row][column] == ' ')
					//					{
					//						graphics.setColor(LIGHT_GRAY);
					//						graphics.fillRect(
					//								(int) (ServerGUI.CENTRE_X
					//										+ column
					//										* (ServerWorld.TILE_SIZE / objectFactor) - posX),
					//								(int) (ServerGUI.CENTRE_Y
					//										+ row
					//										* (ServerWorld.TILE_SIZE / objectFactor) - posY),
					//								(int) (ServerWorld.TILE_SIZE / objectFactor ),
					//								(int) (ServerWorld.TILE_SIZE / objectFactor ));
					//					}
					if (grid[row][column] != ' ')
					{
						graphics.drawImage(
								tiles[grid[row][column]].getImage(),
								(int) (ServerGUI.CENTRE_X
										+ column
										* (ServerWorld.TILE_SIZE / objectFactor) - posX + 0.5),
								(int) (ServerGUI.CENTRE_Y
										+ row
										* (ServerWorld.TILE_SIZE / objectFactor) - posY + 0.5),(int) (ServerWorld.TILE_SIZE / objectFactor +1),
								(int) (ServerWorld.TILE_SIZE / objectFactor + 1),
								null);
					}
				}
			}
		}
		else
		{
			for (int row = startRow; row <= endRow; row++)
			{
				for (int column = startColumn; column <= endColumn; column++)
				{
					//					if (grid[row][column] == ' ')
					//					{
					//						graphics.setColor(LIGHT_GRAY);
					//					}
					if (grid[row][column] != ' ')
					{
						graphics.setColor(tiles[grid[row][column]].getColor());

						graphics.fillRect(
								(int) (ServerGUI.CENTRE_X
										+ column
										* (ServerWorld.TILE_SIZE / objectFactor) - posX + 0.5),
								(int) (ServerGUI.CENTRE_Y
										+ row
										* (ServerWorld.TILE_SIZE / objectFactor) - posY + 0.5),
								(int) (ServerWorld.TILE_SIZE / objectFactor + 1),
								(int) (ServerWorld.TILE_SIZE / objectFactor + 1));
					}
					// graphics.setColor(Color.black);
					// graphics.drawRect((int) (ServerGUI.CENTRE_X + column*
					// (ServerWorld.TILE_SIZE / objectFactor) - posX),
					// (int) (ServerGUI.CENTRE_Y + row
					// * (ServerWorld.TILE_SIZE / objectFactor) - posY),
					// (int)(ServerWorld.TILE_SIZE /
					// objectFactor+0.5),(int)(ServerWorld.TILE_SIZE /
					// objectFactor+0.5));

				}
			}
		}

		//Draw all the objects
		for(CreatorWorldObject object : objects)
		{
			if(isEditable)
				graphics.drawImage(object.getImage(), (int) (ServerGUI.CENTRE_X + object.getCol()
				* (ServerWorld.TILE_SIZE / objectFactor) - posX),(int) (ServerGUI.CENTRE_Y + object.getRow()
				* (ServerWorld.TILE_SIZE / objectFactor) - posY),(int)(object.getWidth()*ServerWorld.TILE_SIZE/objectFactor),(int)(object.getHeight()*ServerWorld.TILE_SIZE/objectFactor), null);
			else
			{
				graphics.setColor(tiles[object.getRef()].getColor());
				graphics.fillRect((int) (ServerGUI.CENTRE_X + object.getCol()
				* (ServerWorld.TILE_SIZE / objectFactor) - posX),(int) (ServerGUI.CENTRE_Y + object.getRow()
				* (ServerWorld.TILE_SIZE / objectFactor) - posY),(int)(object.getWidth()*ServerWorld.TILE_SIZE/objectFactor),(int)(object.getHeight()*ServerWorld.TILE_SIZE/objectFactor));
			}
		}

		
		if (highlightingArea)
		{
			canDrawObject = false;
			graphics.setColor(Color.white);

			// If we are trying to selected an area that exceeds the grid, draw
			// a smaller grid
			if (selectedBlock[0] < startRow)
				selectedBlock[0] = startRow;
			else if (selectedBlock[0] > endRow + 1)
				selectedBlock[0] = endRow + 1;

			if (selectedBlock[1] < startColumn)
				selectedBlock[1] = startColumn;
			else if (selectedBlock[1] > endColumn + 1)
				selectedBlock[1] = endColumn + 1;

			// Since rectangles can only be drawn from top-left to bottom-right,
			// we need to figure out the starting location and width/height
			int width = (int) ((selectedBlock[1] - startingBlock[1]) * (ServerWorld.TILE_SIZE / objectFactor));
			int height = (int) ((selectedBlock[0] - startingBlock[0]) * (ServerWorld.TILE_SIZE / objectFactor));
			int startX = (int) (ServerGUI.CENTRE_X + startingBlock[1]
					* (ServerWorld.TILE_SIZE / objectFactor) - posX);
			int startY = (int) (ServerGUI.CENTRE_Y + startingBlock[0]
					* (ServerWorld.TILE_SIZE / objectFactor) - posY);

			// Variables for highlighting
			int startRowInt = startingBlock[0];
			int startColInt = startingBlock[1];
			int numRows = selectedBlock[0] - startingBlock[0];
			int numCols = selectedBlock[1] - startingBlock[1];

			if (width < 0)
			{
				startX += width;
				startColInt += numCols;
				numCols = -numCols;
				width = -width;
			}
			if (height < 0)
			{
				startRowInt += numRows;
				startY += height;
				numRows = -numRows;
				height = -height;
			}

			if (highlight)
			{
				highlight = false;
				highlightingArea = false;
				for (int row = startRowInt; row < startRowInt + numRows; row++)
					for (int col = startColInt; col < startColInt + numCols; col++)
						if(canFit(row,col,1,1,true) || selectedTile < 'A')
							grid[row][col] = selectedTile;
			}
			else
			{
				graphics.drawRect(startX, startY, width, height);
			}
		}
		else if (selectedTile != '-' && selectedBlock != null
				&& selectedBlock[0] >= startRow && selectedBlock[0] <= endRow
				&& selectedBlock[1] >= startColumn
				&& selectedBlock[1] <= endColumn
				&& isEditable)
		{
			graphics.setColor(Color.white);

			//if we are highlighting  single tile
			if(tiles[selectedTile].isTile())
			{			
				if(!canFit(selectedBlock[0],selectedBlock[1],1,1,true) && selectedTile >= 'A')
					graphics.setColor(Color.red);

				graphics.drawRect((int) (ServerGUI.CENTRE_X + selectedBlock[1]
						* (ServerWorld.TILE_SIZE / objectFactor) - posX),
						(int) (ServerGUI.CENTRE_Y + selectedBlock[0]
								* (ServerWorld.TILE_SIZE / objectFactor) - posY),
						(int) (ServerWorld.TILE_SIZE / objectFactor) ,
						(int) (ServerWorld.TILE_SIZE / objectFactor) );
				canDrawObject = false;
			}
			//Draw a box for the object if it can be added to the screen
			else 
			{
				int x = (int) (ServerGUI.CENTRE_X + selectedBlock[1]
						* (ServerWorld.TILE_SIZE / objectFactor) - posX);
				int y = (int) (ServerGUI.CENTRE_Y + selectedBlock[0]
						* (ServerWorld.TILE_SIZE / objectFactor) - posY);
				int width = (int) (tiles[selectedTile].getImage().getWidth(null)/ objectFactor) ;
				int height = (int) (tiles[selectedTile].getImage().getHeight(null)/ objectFactor) ;
		
				if(canFit(selectedBlock[0],selectedBlock[1],(int)(tiles[selectedTile].getImage().getWidth(null)/ ServerWorld.TILE_SIZE),(int) (tiles[selectedTile].getImage().getHeight(null)/ ServerWorld.TILE_SIZE),false))
				{
					canDrawObject = true;

				}
				else
				{
					canDrawObject = false;
					graphics.setColor(Color.red);
				}
				graphics.drawRect(x,y,width,height);
			}
		}
		else canDrawObject = false;

		graphics.setColor(Color.white);

		//Draw an outline
		graphics.drawRect((int) (ServerGUI.CENTRE_X - posX), (int) (ServerGUI.CENTRE_Y  - posY),(int)((grid[0].length)
				* (ServerWorld.TILE_SIZE / objectFactor)), (int)((grid.length)
						* (ServerWorld.TILE_SIZE / objectFactor)));

		//Draw instructions
		graphics.drawString(String.format("Map can only be edited when zoomed in %d%% or more (Current: %d%%)", (int)( 100/MIN_EDIT_ZOOM),(int)( 100/objectFactor)), 10,
				20);
		graphics.drawString("Select an object using the mouse", 10, 35);
		graphics.drawString("Place objects using left click", 10, 50);
		graphics.drawString("Delete objects using ctrl+right click", 10, 65);
		graphics.drawString(
				"Use arrow keys to scroll or ctrl + left click to drag the map (left click only when map is not editable)",
				10, 80);
		graphics.drawString(
				"Highlight and fill areas of the map with tiles using right click",
				10, 95);
		graphics.drawString(
				"Tip: Scroll in one direction and hold mouse down to create long straight lines and boxes",
				10, 110);
		graphics.drawString(
				"Tip: Zoom out and use mouse drags to quickly access other parts of the map",
				10, 125);


	}

	public boolean canFit(int startRow, int startCol, int width, int height, boolean isTile)
	{
		if(startCol + width >= grid[0].length ||startRow + height >= grid.length)
			return false;

		//System.out.printf("%d %d %d %d%n",startRow,startCol,width,height);
		for(CreatorWorldObject object : objects)
		{
			if(object.collidesWith(startCol, startRow, startCol+width, startRow+height))
				return false;
		}

		if(!isTile)
			for(int row = startRow; row < startRow + height;row++)
				for(int col = startCol; col < startCol+width;col++)
				{
					if(grid[row][col] >= 'A')
						return false;
				}

		return true;
	}

	public void update()
	{
		if (up)
			posY -= SCROLL_SPEED;
		else if (down)
			posY += SCROLL_SPEED;

		if (right)
			posX += SCROLL_SPEED;
		else if (left)
			posX -= SCROLL_SPEED;

		repaint();
	}

	public int[] getRowCol(int x, int y)
	{
		int col = (int) ((x - ServerGUI.CENTRE_X + posX) / (ServerWorld.TILE_SIZE / objectFactor));
		int row = (int) ((y - ServerGUI.CENTRE_Y + posY) / (ServerWorld.TILE_SIZE / objectFactor));
		return new int[] { row, col };
	}

	public void save() throws FileNotFoundException
	{	
		PrintWriter output = new PrintWriter(new File("Resources", fileName));
		
		//print the grid
		output.println(grid.length + " " + grid[0].length);
		for (int row = 0; row < grid.length; row++)
		{
			for (int col = 0; col < grid[0].length; col++)
				output.print(grid[row][col]);
			output.println();
		}
		
		//print the objects
		output.println(objects.size());
		for(CreatorWorldObject object : objects)
		{
			output.printf("%d %d %s%n", object.getRow(),object.getCol(),object.getRef());
		}
		output.close();
	}

	public void setHeight(int height)
	{
		char[][] currentGrid = grid.clone();
		grid = new char[height][grid[0].length];

		for (int row = 0; row < grid.length; row++)
			for (int col = 0; col < grid[0].length; col++)
			{
				if (row < currentGrid.length)
					grid[row][col] = currentGrid[row][col];
				else
					grid[row][col] = ' ';
			}
	}

	public void setWidth(int width)
	{
		char[][] currentGrid = grid.clone();
		grid = new char[grid.length][width];

		for (int row = 0; row < grid.length; row++)
			for (int col = 0; col < grid[0].length; col++)
			{
				if (col < currentGrid[0].length)
					grid[row][col] = currentGrid[row][col];
				else
					grid[row][col] = ' ';
			}
	}

	public void setNewHeight(int newHeight)
	{
		isNewHeight = true;
		this.newHeight = newHeight;
	}

	public void setNewWidth(int newWidth)
	{
		isNewWidth = true;
		this.newWidth = newWidth;
	}

	public CreatorObject[] getTiles()
	{
		return tiles;
	}

	public void setTiles(CreatorObject[] tiles)
	{
		this.tiles = tiles;
	}

	public char getSelectedTile()
	{
		return selectedTile;
	}

	public void setSelectedTile(char selectedTile)
	{

		if (this.selectedTile != '-')
			tiles[this.selectedTile].deselect();
		this.selectedTile = selectedTile;
	}

	public void keyPressed(KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.VK_W)
			up = true;
		else if (event.getKeyCode() == KeyEvent.VK_S)
			down = true;
		else if (event.getKeyCode() == KeyEvent.VK_D)
			right = true;
		else if (event.getKeyCode() == KeyEvent.VK_A)
			left = true;
		else if (event.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			ctrlPressed = true;
		}
	}

	public void keyReleased(KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.VK_W)
			up = false;
		else if (event.getKeyCode() == KeyEvent.VK_S)
			down = false;
		else if (event.getKeyCode() == KeyEvent.VK_D)
			right = false;
		else if (event.getKeyCode() == KeyEvent.VK_A)
			left = false;
		else if (event.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			ctrlPressed = false;
			highlight = true;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (this.hasFocus())
			selectedBlock = getRowCol(
					(int) (MouseInfo.getPointerInfo().getLocation().getX() - this
							.getLocationOnScreen().getX()),
					(int) (MouseInfo.getPointerInfo().getLocation().getY() - this
							.getLocationOnScreen().getY()));

		if (selectedBlock != null && selectedBlock[0] >= 0
				&& selectedBlock[0] < grid.length && selectedBlock[1] >= 0
				&& selectedBlock[1] < grid[0].length)
			if (addingTile && !ctrlPressed && (canFit(selectedBlock[0],selectedBlock[1],1,1,true) || selectedTile < 'A'))
				grid[selectedBlock[0]][selectedBlock[1]] = selectedTile;
			else if (removingTile && ctrlPressed)
			{
				CreatorWorldObject toRemove = null;
				for(CreatorWorldObject object : objects)
				{
					if(object.collidesWith(selectedBlock[1], selectedBlock[0], selectedBlock[1]+1, selectedBlock[0]+1))
					{
						toRemove = object;
					}
				}
				if(toRemove != null)
				{
					objects.remove(toRemove);
					update();
					requestFocusInWindow();
					return;
				}
				grid[selectedBlock[0]][selectedBlock[1]] = ' ';
			}

		update();
		requestFocusInWindow();
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

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
			leftClick = true;
		else if (event.getButton() == MouseEvent.BUTTON3)
			rightClick = true;

		if (leftClick
				&& (ctrlPressed || !isEditable || selectedTile == '-'))
		{
			dragSourceX = event.getX();
			dragSourceY = event.getY();
		}
		else if (leftClick && selectedTile != '-' && !ctrlPressed
				&& isEditable)
		{
			if(tiles[selectedTile].isTile())
			{
				addingTile = true;
			}
			else if(canDrawObject)
			{
				objects.add(new CreatorWorldObject(selectedBlock[0],selectedBlock[1],tiles[selectedTile].getReference()));
			}
		}
		else if (rightClick && isEditable
				&& ctrlPressed)
		{		
			removingTile = true;
		}
		else if (rightClick && isEditable
				&& !ctrlPressed && selectedTile != '-' && tiles[selectedTile].isTile() && selectedBlock[0] >= 0
				&& selectedBlock[0] < grid.length && selectedBlock[1] >= 0
				&& selectedBlock[1] < grid[0].length)
		{
			highlightingArea = true;
			highlight = false;
			startingBlock = selectedBlock.clone();
		}
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent event)
	{
		if (event.getButton() == MouseEvent.BUTTON1)
			leftClick = false;
		else if (event.getButton() == MouseEvent.BUTTON3)
			rightClick = false;

		addingTile = false;
		removingTile = false;
		highlight = true;
	}

	@Override
	public void mouseDragged(MouseEvent event)
	{
		if ((ctrlPressed || !isEditable || selectedTile =='-')
				&& leftClick)
		{
			// System.out.println(event.+" "+MouseEvent.BUTTON3);
			posX -= event.getX() - dragSourceX;
			posY -= event.getY() - dragSourceY;
			dragSourceX = event.getX();
			dragSourceY = event.getY();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent scroll)
	{
		int notches = scroll.getWheelRotation();


		if (notches > 0 )
		{
			if (objectFactor * (1.1 * (notches))< ServerFrame.FRAME_FACTOR * 16)
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


		if(objectFactor <= MIN_EDIT_ZOOM)
			isEditable = true;
		else
			isEditable = false;
	}
	
	public void setPosX(int x)
	{
		posX = x;
	}
	
	public void setPosY(int y)
	{
		posY = y;
	}

	public void setObjectFactor(int factor)
	{
		objectFactor = factor;
	}
}
