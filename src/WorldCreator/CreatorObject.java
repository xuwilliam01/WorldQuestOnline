package WorldCreator;

import java.awt.Image;

import Imports.Images;

public class CreatorObject {

	private char reference;
	private String imageName;
	private Image image;
	
	public CreatorObject(char ref, String name)
	{
		reference = ref;
		imageName = name;
		image = Images.getImage(name);
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
}
