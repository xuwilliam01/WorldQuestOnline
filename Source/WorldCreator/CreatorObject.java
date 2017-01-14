package WorldCreator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import Client.ClientFrame;
import Imports.GameImage;
import Imports.ImageReferencePair;
import Imports.Images;

/**
 * An object that can be selected in the world creator
 * @author Alex Raita & William Xu
 *
 */
public class CreatorObject extends JButton implements MouseListener
{
	public final static double SCALE = 1.5;
	private CreatorWorld world;
	private ImageReferencePair imageRef;
	private boolean isTile;
	private String name;
	
	/**
	 * Constructor
	 * @param ref the reference
	 * @param name the image name
	 * @param isTile whether it is a tile or not
	 * @param description the description for the tool tip
	 * @param world the world it will be placed in
	 */
	public CreatorObject(char ref, String name, boolean isTile,
			String description, CreatorWorld world)
	{
		super(new ImageIcon(Images.getImage(name.substring(0,
				name.length())
				+ "_ICON")));
		setSize(ClientFrame.getScaledWidth(getIcon().getIconWidth()), ClientFrame.getScaledHeight(getIcon().getIconHeight()));


		imageRef = new ImageReferencePair(ref, name);

		this.world = world;
		this.isTile = isTile;
		this.name = name;
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusable(false);
		addMouseListener(this);
		setToolTipText(description);


	}
	
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		if(name.contains("RED"))
		{
			graphics.setColor(new Color(255, 0,0, 60));
			graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
		else if(name.contains("BLUE"))
		{
			graphics.setColor(new Color(0, 0,255, 60));
			graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
		repaint();
	}

	public String getImageName()
	{
		return imageRef.getImageName();
	}

	public char getReference()
	{
		return imageRef.getReference();
	}

	public void setReference(char reference)
	{
		imageRef.setReference(reference);
	}
	

	public Image getImage()
	{
		return imageRef.getImage();
	}

	public void setImage(Image image)
	{
		imageRef.setImage(image);
	}

	public boolean isTile()
	{
		return isTile;
	}

	public void deselect()
	{
		setBorder(BorderFactory.createEmptyBorder());
	}

	public void setPosition(int row, int col)
	{
		if (isTile)
			setLocation(ClientFrame.getScaledWidth((int)(SCALE*(col * imageRef.getImage().getWidth(null) + (col + 1)
					* 12))),ClientFrame.getScaledHeight((int)(SCALE*(row * imageRef.getImage().getHeight(null) + row * 10)
					+ 80)));
		else
			setLocation(ClientFrame.getScaledWidth((int)(SCALE*(col * getIcon().getIconWidth()/SCALE + (col + 1) * 12))), ClientFrame.getScaledHeight((int)(SCALE*(row
					* getIcon().getIconHeight()/SCALE + row * 10) + 425)));
	}

	@Override
	/**
	 * If we select this object. Highlight it
	 */
	public void mousePressed(MouseEvent e)
	{
		world.setSelectedTile(imageRef.getReference());
		setBorder(BorderFactory.createLineBorder(Color.white));
	}

	// UNUSED BUT REQUIRED METHODS
	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////
	public Color getColor()
	{
		return imageRef.getColor();
	}

	public void setColor(Color color)
	{
		imageRef.setColor(color);
	}
	
	public String getName()
	{
		return name;
	}

}
