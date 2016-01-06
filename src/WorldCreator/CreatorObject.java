package WorldCreator;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import Imports.GameImage;
import Imports.ImageReferencePair;
import Imports.Images;

public class CreatorObject extends JButton implements MouseListener{


	private CreatorWorld world;
	private ImageReferencePair imageRef;

	public CreatorObject(char ref, String name, CreatorWorld world)
	{
		super(new ImageIcon(Images.getImage(name)));
		
		imageRef = new ImageReferencePair(ref,name);
		
		
		this.world = world;
		
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusable(false);
		addMouseListener(this);
		
		setSize(imageRef.getImage().getWidth(null),imageRef.getImage().getHeight(null));
	}

	public char getReference() {
		return imageRef.getReference();
	}

	public void setReference(char reference) {
		imageRef.setReference(reference);
	}

	public Image getImage() {
		return imageRef.getImage();
	}

	public void setImage(Image image) {
		imageRef.setImage(image);
	}

	public void deselect()
	{
		setBorder(BorderFactory.createEmptyBorder());
	}
	
	public void setPosition(int row, int col)
	{
		setLocation(col*imageRef.getImage().getWidth(null)+(col+1)*10,row*imageRef.getImage().getHeight(null)+row*10+50);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		world.setSelectedTile(imageRef.getReference());
		setBorder(BorderFactory.createLineBorder(Color.white));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public Color getColor()
	{
		return imageRef.getColor();
	}

	public void setColor(Color color)
	{
		imageRef.setColor(color);
	}
	
	
}
