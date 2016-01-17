package WorldCreator;
import java.io.IOException;
import javax.swing.JOptionPane;


public class StartCreator {

	public static void main(String[] args) throws NumberFormatException, IOException {

		String fileName = "";
		while(true)
		{
			fileName = (String)JOptionPane.showInputDialog("Please enter the name of the file you want to edit/create (No blank names)");
			if(fileName != null && !fileName.trim().isEmpty())
				break;
			if(fileName == null)
				System.exit(0);
		}

		CreatorFrame frame = new CreatorFrame();
		CreatorWorld world = new CreatorWorld(fileName);
		CreatorItems items = new CreatorItems(world,null);
		world.setLocation(0,0);
		items.setLocation(Client.Client.SCREEN_WIDTH,0);

		frame.add(world);
		frame.add(items);
		world.revalidate();
		items.revalidate();
		frame.setVisible(true);
		items.repaint();
	}
}
