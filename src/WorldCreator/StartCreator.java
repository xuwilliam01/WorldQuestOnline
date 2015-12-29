package WorldCreator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class StartCreator {

	public static void main(String[] args) throws NumberFormatException, IOException {

		String fileName = "";
		while(true)
		{
			fileName = (String)JOptionPane.showInputDialog("Please enter the name of the file you want to edit/create");
			if(fileName != null && fileName != "")
				break;
		}

		CreatorFrame frame = new CreatorFrame();
		CreatorWorld world = new CreatorWorld(fileName);
		CreatorItems items = new CreatorItems(world);
		world.setLocation(0,0);
		items.setLocation(CreatorWorld.WIDTH,0);

		frame.add(world);
		frame.add(items);
		world.revalidate();
		items.revalidate();



		frame.setVisible(true);

	}
}
