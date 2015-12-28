package Client;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import Imports.Images;

public class ClientItem extends JButton implements ActionListener{

	private Image image;
	private String imageName;
	private boolean selected = false;

	public ClientItem(String imageName, int row, int col)
	{
		super(new ImageIcon(Images.getImage(imageName.substring(0,imageName.length()-4)+"_INVENTORY.png")));
		
		this.imageName = imageName;
		image = Images.getImage(imageName.substring(0,imageName.length()-4)+"_INVENTORY.png");
		
		setSize(Images.INVENTORY_IMAGE_SIZE,Images.INVENTORY_IMAGE_SIZE);
		setLocation(col*Images.INVENTORY_IMAGE_SIZE+(col+1)*20,row*Images.INVENTORY_IMAGE_SIZE+row*20+50);
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		addActionListener(this);
		setFocusable(false);
	}
	
	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void actionPerformed(ActionEvent e) {
		selected = true;
		
	}

	
}
