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

public class CreatorItems extends JPanel implements ActionListener{

	public static final int WIDTH = 300;
	public static final int NUM_ROWS = 3;
	private static final int NUM_COLS = 5;

	private CreatorWorld world;
	private CreatorObject[] buttons;

	//Buttons
	private JButton save = new JButton("Save");
	private JButton clear = new JButton("Clear");
	private JButton updateWidth;
	private JButton updateHeight;


	public CreatorItems(CreatorWorld world)
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
	}

	public void addButtons()
	{
		int row = 0;
		int col = 0;

		for(int button = 0; button < buttons.length;button++)
			if(buttons[button] != null)
			{
				buttons[button].setPosition(row, col++ % NUM_COLS);
				row = col/NUM_COLS;

				add(buttons[button]);
			}
		repaint();
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		graphics.drawString("TILES", 140, 20);

	}

	@Override
	public void actionPerformed(ActionEvent button) {

		if(button.getSource() == save)
			try {
				world.save();
			} catch (FileNotFoundException e) {
				System.out.println("Was not able to save file");
				e.printStackTrace();
			}
		else if(button.getSource() == clear)
		{
			world.clearGrid();
		}
		else if(button.getSource() == updateWidth)
		{
			while(true)
				try {
					String newWidth = (String)JOptionPane.showInputDialog("Please input the new width: ");
					if(newWidth == null)
						break;
					world.setNewWidth(Integer.parseInt(newWidth));
					updateWidth.setText("Change Width "+"(Current: "+Integer.parseInt(newWidth)+")");
					break;

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
				}
		}
		else
		{
			while(true)
				try {
					String newHeight = (String)JOptionPane.showInputDialog("Please input the new height: ");
					if(newHeight== null)
						break;
					world.setNewHeight(Integer.parseInt(newHeight));
					updateHeight.setText("Change Height "+"(Current: "+Integer.parseInt(newHeight)+")");
					break;

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
				}
		}
	}
}
