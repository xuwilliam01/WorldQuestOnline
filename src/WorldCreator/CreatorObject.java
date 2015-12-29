package WorldCreator;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import Imports.Images;

public class CreatorObject extends JButton implements MouseListener{

	private char reference;
	private String imageName;
	private Image image;
	private CreatorWorld world;

	public CreatorObject(char ref, String name, CreatorWorld world)
	{
		super(new ImageIcon(Images.getImage(name)));
		
		reference = ref;
		imageName = name;
		image = Images.getImage(name);
		this.world = world;
		
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusable(false);
		addMouseListener(this);
		
		setSize(image.getWidth(null),image.getHeight(null));
	}

	public char getReference() {
		return reference;
	}

	public void setReference(char reference) {
		this.reference = reference;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void deselect()
	{
		setBorder(BorderFactory.createEmptyBorder());
	}
	
	public void setPosition(int row, int col)
	{
		setLocation(col*image.getWidth(null)+(col+1)*10,row*image.getHeight(null)+row*10+50);
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
		world.setSelectedTile(reference);
		setBorder(BorderFactory.createLineBorder(Color.white));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
