package WorldCreator;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 * Starts the world creator
 * @author Alex Raita & William Xu
 *
 */
public class StartCreator {

	public static void main(String[] args) throws NumberFormatException, IOException {

		//Get a file name
		String fileName = "";
		while(true)
		{
			fileName = (String)JOptionPane.showInputDialog("File name (new or existing) (default: WORLD)").trim();
			if(fileName != null && !fileName.isEmpty())
			{
				fileName+=".txt";
				System.exit(0);
				break;
			}		
		}

		//Create the frame and add the world and items
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
