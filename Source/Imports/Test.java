package Imports;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Test {

	public static void main(String[] args) {
		try {
			BufferedImage image = ImageIO.read(new File("Images//Creatures//" +
					"GREENSLIME.png"));
			System.out.println(image.getHeight());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
