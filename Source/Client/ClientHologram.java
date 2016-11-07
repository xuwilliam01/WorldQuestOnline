package Client;

import java.awt.Image;

import Imports.Images;

public class ClientHologram {
	private Image image;
	private int x;
	private int y;
	
	public ClientHologram(int imageIndex, int x, int y)
	{
		this.image = Images.getImage(Images.getImageName(imageIndex));
		this.x = x;
		this.y = y;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(int imageIndex) {
		this.image = Images.getImage(Images.getImageName(imageIndex));
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	
}
