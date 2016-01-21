package WorldCreator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import Imports.Images;

/**
 * Stores the possible items that can be added to the world
 * @author Alex Raita & William Xu
 */
public class CreatorItems extends JPanel implements ActionListener{

	public static final int WIDTH = 300;
	public static final int NUM_ROWS = 3;
	private static final int NUM_COLS = 5;
	public static final int NUM_ROWS_OBJ = 3;
	public static final int NUM_COLS_OBJ = 4;
	public static final int MIN_WORLD_SIZE = 20;
	public static final int MAX_WORLD_SIZE = 100000;

	private CreatorWorld world;
	private CreatorObject[] buttons;

	//Buttons
	private JButton save = new JButton("Save");
	private JButton clear = new JButton("Clear");
	private JButton updateWidth;
	private JButton updateHeight;
	private JButton mainMenu;

	private Timer paintTimer = new Timer(10,this);

	/**
	 * Constructor
	 * @param world the world to be painted in
	 * @param menu a button go back to the main menu
	 */
	public CreatorItems(CreatorWorld world, JButton menu)
	{
		setDoubleBuffered(true);
		setBackground(Color.red);

		setFocusable(true);
		requestFocusInWindow();
		setSize(WIDTH,Client.Client.SCREEN_HEIGHT);
		setLayout(null);

		this.world = world;
		this.buttons = world.getTiles();
		addButtons();

		save.setSize(100,50);
		save.setLocation(WIDTH-125,Client.Client.SCREEN_HEIGHT-100);
		save.addActionListener(this);
		add(save);

		clear.setSize(100,50);
		clear.setLocation(WIDTH-250,Client.Client.SCREEN_HEIGHT-100);
		clear.addActionListener(this);
		add(clear);

		updateWidth = new JButton("Change Width "+"(Current: "+world.getGrid()[0].length+")");
		updateWidth.setSize(225,50);
		updateWidth.setLocation(WIDTH-250,Client.Client.SCREEN_HEIGHT-160);
		updateWidth.addActionListener(this);
		add(updateWidth);

		updateHeight = new JButton("Change Height "+"(Current: "+world.getGrid().length+")");
		updateHeight.setSize(225,50);
		updateHeight.setLocation(WIDTH-250,Client.Client.SCREEN_HEIGHT-220);
		updateHeight.addActionListener(this);
		add(updateHeight);

		if(menu != null)
		{
			mainMenu = menu;
			mainMenu.setSize(225,50);
			mainMenu.setLocation(WIDTH-250,Client.Client.SCREEN_HEIGHT-280);
			add(mainMenu);
		}

		paintTimer.start();
	}

	/**
	 * Add buttons to the world for selecting objects to add the to the world
	 */
	public void addButtons()
	{
		int tileRow = 0;
		int tileCol = 0;

		int objRow = 0;
		int objCol = 0;

		for(int button = 0; button < buttons.length;button++)
			if(buttons[button] != null)
			{
				if(buttons[button].isTile())
				{
					buttons[button].setPosition(tileRow, tileCol++ % NUM_COLS);
					tileRow = tileCol/NUM_COLS;
				}
				else
				{
					buttons[button].setPosition(objRow, objCol++ % NUM_COLS_OBJ);
					objRow = objCol/NUM_COLS_OBJ;
				}

				add(buttons[button]);
			}
		repaint();
	}

	/**
	 * Paint the background and the title
	 */
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		graphics.setColor(Color.red);
		graphics.fillRect(0, 0, WIDTH, Client.Client.HEIGHT);
		graphics.setColor(Color.BLACK);
		graphics.drawString("TILES", 140, 20);
		repaint();
	}

	@Override
	/**
	 * If we click a button
	 */
	public void actionPerformed(ActionEvent button) {

		//Save
		if(button.getSource() == save)
			try {
				world.save();
			} catch (FileNotFoundException e) {
				System.out.println("Was not able to save file");
				e.printStackTrace();
			}
		//Clear the world
		else if(button.getSource() == clear)
		{
			world.clearGrid();
		}
		//Update the width
		else if(button.getSource() == updateWidth)
		{
			while(true)
				try {
					String newWidth = (String)JOptionPane.showInputDialog(String.format("Please input the new width (larger than %d and less than %d): ",MIN_WORLD_SIZE,MAX_WORLD_SIZE));
					if(newWidth == null)
						break;
					int newWidthNum = Integer.parseInt(newWidth);
					if(newWidthNum < MIN_WORLD_SIZE|| newWidthNum > MAX_WORLD_SIZE)
						continue;
					world.setNewWidth(newWidthNum);
					updateWidth.setText("Change Width "+"(Current: "+Integer.parseInt(newWidth)+")");
					break;

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
				}
		}
		//Repaint this
		else if(button.getSource() == paintTimer)
		{
			repaint();
		}
		//Update the height
		else
		{
			while(true)
				try {
					String newHeight = (String)JOptionPane.showInputDialog(String.format("Please input the new height (larger than %d and less than %d): ",MIN_WORLD_SIZE,MAX_WORLD_SIZE));
					if(newHeight== null)
						break;
					int newHeightNum = Integer.parseInt(newHeight);
					if(newHeightNum < MIN_WORLD_SIZE || newHeightNum > MAX_WORLD_SIZE)
						continue;
					world.setNewHeight(newHeightNum);
					updateHeight.setText("Change Height "+"(Current: "+Integer.parseInt(newHeight)+")");
					break;

				} catch (NumberFormatException e) {
				} catch (HeadlessException e) {
				}
		}
	}
}
