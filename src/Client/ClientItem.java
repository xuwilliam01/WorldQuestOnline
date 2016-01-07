package Client;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import Imports.Images;

@SuppressWarnings("serial")
public class ClientItem extends JButton implements MouseListener{

	private Image image;
	private String imageName;
	private boolean selected = false;
	private ClientInventory inventory;
	private String type;
	private int amount = 1;

	public ClientItem(String imageName, String type,int row, int col, ClientInventory inventory)
	{
		super(new ImageIcon(Images.getImage(imageName.substring(0,imageName.length()-4)+"_ICON.png")));
		this.type = type;
		this.inventory = inventory;
		this.imageName = imageName;
		image = Images.getImage(imageName.substring(0,imageName.length()-4)+"_ICON.png");
		
		setSize(Images.INVENTORY_IMAGE_SIDELENGTH,Images.INVENTORY_IMAGE_SIDELENGTH);
		setLocation(col*Images.INVENTORY_IMAGE_SIDELENGTH+(col+1)*20,row*Images.INVENTORY_IMAGE_SIDELENGTH+row*20+50);
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusable(false);
		addMouseListener(this);
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

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void increaseAmount()
	{
		amount++;
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	public void decreaseAmount()
	{
		amount--;
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
		if(e.getButton()== MouseEvent.BUTTON1)
		{
			selected = true;
			
			//Do something if selected
			System.out.println("Selected this item");
		}
		else if(e.getButton() == MouseEvent.BUTTON3)
		{
			System.out.println("Dropped this item");
			
			inventory.removeItem(this);
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
