package Imports;

import java.awt.Color;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import WorldCreator.CreatorObject;

/**
 * Links an image with a character reference
 * 
 * @author Alex Raita & William Xu
 *
 */
public class ImageReferencePair {
	/**
	 * Store all tiles and their images
	 */
	private static ImageReferencePair[] images = new ImageReferencePair[(int)(Math.pow(2, 16))];
	private char reference;
	private String imageName;
	private Image image;
	private Color color;

	/**
	 * Constructor
	 * 
	 * @param ref
	 *            the reference
	 * @param name
	 *            the image name
	 */
	public ImageReferencePair(char ref, String name) {
		reference = ref;
		imageName = name;

		GameImage gameImage = Images.getGameImage(name);
		image = Images.getImage(name);
		color = gameImage.getCentreColor();

	}

	/**
	 * Imports the reference
	 */
	public static void importReferences() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(
				"Resources", "WorldCreator.cfg")));
		String line;
		while ((line = br.readLine()) != null) {
			String[] name = line.substring(2).split(" ");
			images[line.charAt(0)] = new ImageReferencePair(line.charAt(0),
					name[0]);
		}
		br.close();

		br = new BufferedReader(new FileReader(new File("Resources",
				"MoreTiles.cfg")));
		while ((line = br.readLine()) != null) {
			if (line.charAt(0) != '/') {
				String[] tokens = line.split(" ");
				char index = (char)(Integer.parseInt(tokens[0]));
				images[index] = new ImageReferencePair(index,
						tokens[1]);
			}
		}
		br.close();
	}

	// ///////////////////////
	// GETTERS AND SETTERS //
	// ///////////////////////
	public static ImageReferencePair[] getImages() {
		return images;
	}

	public static void setImages(ImageReferencePair[] images) {
		ImageReferencePair.images = images;
	}

	public char getReference() {
		return reference;
	}

	public void setReference(char reference) {
		this.reference = reference;
	}

	public String getImageName() {
		return imageName;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
