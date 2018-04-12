package WorldCreator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.ToolTipManager;

import Client.ClientFrame;

/**
 * Stores the possible items that can be added to the world
 * 
 * @author Alex Raita & William Xu
 */
@SuppressWarnings("serial")
public class CreatorItems extends JPanel implements ActionListener {

	public static int WIDTH = 300;
	public static final int NUM_ROWS = 5;
	private static final int NUM_COLS = 5;
	public static final int NUM_ROWS_OBJ = 5;
	public static final int NUM_COLS_OBJ = 5;
	public static final int MIN_WORLD_SIZE = 20;
	public static final int MAX_WORLD_SIZE = 100000;

	private CreatorWorld world;
	private CreatorObject[] buttons;

	// Buttons
	private JButton save = new JButton("Save");
	private JButton clear = new JButton("Clear");
	private JButton updateWidth;
	private JButton updateHeight;
	private JButton mainMenu;
	private JButton rename;
	private JButton delete;

	private Timer paintTimer = new Timer(10, this);

	/**
	 * Constructor
	 * @param world the world to be painted in
	 * @param menu a button go back to the main menu
	 */
	public CreatorItems(CreatorWorld world, JButton menu)
	{
		ToolTipManager.sharedInstance().setInitialDelay(0);
		setDoubleBuffered(true);
		setBackground(Color.black);

		WIDTH = ClientFrame.getScaledWidth(300);
		setFocusable(true);
		requestFocusInWindow();
		setSize(300,Client.Client.SCREEN_HEIGHT);
		setLayout(null);

		this.world = world;
		this.buttons = world.getTiles();
		addButtons();

		int delta = 80;
		
		save.setSize(ClientFrame.getScaledWidth(100),ClientFrame.getScaledHeight(50));
		save.setLocation(WIDTH-ClientFrame.getScaledWidth(125),Client.Client.SCREEN_HEIGHT-ClientFrame.getScaledHeight(100+delta));
		save.addActionListener(this);
		save.setBackground(new Color(240, 240, 240));
		save.setForeground(Color.black);
		add(save);

		clear.setSize(ClientFrame.getScaledWidth(100),ClientFrame.getScaledHeight(50));
		clear.setLocation(WIDTH-ClientFrame.getScaledWidth(250),Client.Client.SCREEN_HEIGHT-ClientFrame.getScaledHeight(100+delta));
		clear.addActionListener(this);
		clear.setBackground(new Color(240, 240, 240));
		clear.setForeground(Color.black);
		add(clear);

		updateWidth = new JButton("Change Width ("+world.getGrid()[0].length+")");
		updateWidth.setSize(ClientFrame.getScaledWidth(225),ClientFrame.getScaledHeight(50));
		updateWidth.setLocation(WIDTH-ClientFrame.getScaledWidth(250),Client.Client.SCREEN_HEIGHT-ClientFrame.getScaledHeight(160+delta));
		updateWidth.addActionListener(this);
		updateWidth.setBackground(new Color(240, 240, 240));
		updateWidth.setForeground(Color.black);
		add(updateWidth);

		updateHeight = new JButton("Change Height ("+world.getGrid().length+")");
		updateHeight.setSize(ClientFrame.getScaledWidth(225),ClientFrame.getScaledHeight(50));
		updateHeight.setLocation(WIDTH-ClientFrame.getScaledWidth(250),Client.Client.SCREEN_HEIGHT-ClientFrame.getScaledHeight(220+delta));
		updateHeight.addActionListener(this);
		updateHeight.setBackground(new Color(240, 240, 240));
		updateHeight.setForeground(Color.black);
		add(updateHeight);

		if(menu != null)
		{
			mainMenu = menu;
			mainMenu.setSize(ClientFrame.getScaledWidth(225),ClientFrame.getScaledHeight(50));
			mainMenu.setLocation(WIDTH-ClientFrame.getScaledWidth(250),Client.Client.SCREEN_HEIGHT-ClientFrame.getScaledHeight(340+delta));
			mainMenu.setBackground(new Color(240, 240, 240));
			mainMenu.setForeground(Color.black);
			add(mainMenu);
		}

		rename = new JButton("Rename");
		rename.setSize(ClientFrame.getScaledWidth(100),ClientFrame.getScaledHeight(50));
		rename.setLocation(WIDTH-ClientFrame.getScaledWidth(250),Client.Client.SCREEN_HEIGHT-ClientFrame.getScaledHeight(280+delta));
		rename.addActionListener(this);
		rename.setBackground(new Color(240, 240, 240));
		rename.setForeground(Color.black);
		add(rename);

		delete = new JButton("Delete");
		delete.setSize(ClientFrame.getScaledWidth(100),ClientFrame.getScaledHeight(50));
		delete.setLocation(WIDTH-ClientFrame.getScaledWidth(125),Client.Client.SCREEN_HEIGHT-ClientFrame.getScaledHeight(280+delta));
		delete.addActionListener(this);
		delete.setBackground(new Color(240, 240, 240));
		delete.setForeground(Color.black);
		add(delete);


		paintTimer.start();
	}

	/**
	 * Add buttons to the world for selecting objects to add the to the world
	 */
	public void addButtons() {
		int tileRow = 0;
		int tileCol = 0;

		int objRow = 0;
		int objCol = 0;

		for (int button = 0; button < buttons.length; button++)
			if (buttons[button] != null) {
				if (buttons[button].isTile()) {
					buttons[button].setPosition(tileRow, tileCol++ % NUM_COLS);
					tileRow = tileCol / NUM_COLS;
				} else {
					buttons[button].setPosition(objRow, objCol++ % NUM_COLS_OBJ);
					objRow = objCol / NUM_COLS_OBJ;
				}

				add(buttons[button]);
			}
		repaint();
	}

	/**
	 * Paint the background and the title
	 */
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		graphics.setColor(Color.BLACK);
		//graphics.fillRect(Client.Client.SCREEN_WIDTH, 0, Client.ClientInventory.INVENTORY_WIDTH, Client.Client.SCREEN_HEIGHT);
		graphics.drawString("TILES", ClientFrame.getScaledWidth(140), ClientFrame.getScaledHeight(20));

		repaint();
	}

	@Override
	/**
	 * If we click a button
	 */
	public void actionPerformed(ActionEvent button) {

		// Save
		if (button.getSource() == save)

			try {
				world.save();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Was not able to save file");
				e.printStackTrace();
			}

		// Clear the world
		else if (button.getSource() == clear) {
			world.clearGrid();
		}
		// Update the width
		else if (button.getSource() == updateWidth) {
			while (true)
				try {
					String newWidth = (String) JOptionPane.showInputDialog(world,
							String.format("Please input the new width (larger than %d and less than %d): ",
									MIN_WORLD_SIZE, MAX_WORLD_SIZE), "", JOptionPane.PLAIN_MESSAGE);
					if (newWidth == null)
						break;
					int newWidthNum = Integer.parseInt(newWidth);
					if (newWidthNum < MIN_WORLD_SIZE || newWidthNum > MAX_WORLD_SIZE)
						continue;
					world.setNewWidth(newWidthNum);
					updateWidth.setText("Change Width (" + Integer.parseInt(newWidth) + ")");
					break;

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
				}
		}
		// Repaint this
		else if (button.getSource() == paintTimer) {
			repaint();
		}
		// Get a name
		else if(button.getSource() == rename)
		{
			String newName = JOptionPane.showInputDialog(world, "Please enter a new name", "", JOptionPane.PLAIN_MESSAGE);
			if(newName != null)
			{
				try {
					Files.deleteIfExists(new File("Resources",world.getFileName()).toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try{
					BufferedReader maps = new BufferedReader (new FileReader(new File("Resources","Maps")));
					int numMaps = Integer.parseInt(maps.readLine());
					ArrayList<String> mapNames = new ArrayList<String>();
					for(int i =0; i < numMaps;i++)
					{
						String mapName = maps.readLine().toLowerCase();
						if(!mapName.equals(world.getFileName()))
							mapNames.add(mapName);
						else
							mapNames.add(newName);
					}
					maps.close();

					//If the file doesn't already exist	
					PrintWriter mapWriter = new PrintWriter(new File("Resources","Maps"));
					mapWriter.println(numMaps);
					for(String map : mapNames)
						mapWriter.println(map);
					mapWriter.close();
				}
				catch(Exception E)
				{
					E.printStackTrace();
				}

				world.setFileName(newName);
				try {
					world.save();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else if(button.getSource() == delete)
		{
			int result = JOptionPane.showConfirmDialog(world, "Do you want to delete this map?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if(result == JOptionPane.YES_OPTION)
			{
				world.setJustSaved(true);
				try {
					Files.deleteIfExists(new File("Resources",world.getFileName()).toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try{
					BufferedReader maps = new BufferedReader (new FileReader(new File("Resources","Maps")));
					int numMaps = Integer.parseInt(maps.readLine());
					ArrayList<String> mapNames = new ArrayList<String>();
					for(int i =0; i < numMaps;i++)
					{
						String mapName = maps.readLine().toLowerCase();
						if(!mapName.equals(world.getFileName()))
							mapNames.add(mapName);
					}
					maps.close();

					PrintWriter mapWriter = new PrintWriter(new File("Resources","Maps"));
					mapWriter.println(numMaps-1);
					for(String map : mapNames)
						mapWriter.println(map);
					mapWriter.close();
				}
				catch(Exception E)
				{
					E.printStackTrace();
				}
				mainMenu.doClick();
			}
		}
		// Update the height
		else {
			while (true)
				try {
					String newHeight = (String) JOptionPane.showInputDialog(world,
							String.format("Please input the new height (larger than %d and less than %d): ",
									MIN_WORLD_SIZE, MAX_WORLD_SIZE), "", JOptionPane.PLAIN_MESSAGE);
					if (newHeight == null)
						break;
					int newHeightNum = Integer.parseInt(newHeight);
					if (newHeightNum < MIN_WORLD_SIZE || newHeightNum > MAX_WORLD_SIZE)
						continue;
					world.setNewHeight(newHeightNum);
					updateHeight.setText("Change Height (" + Integer.parseInt(newHeight) + ")");
					break;

				} catch (NumberFormatException e) {
				} catch (HeadlessException e) {
				}
		}
	}
}
