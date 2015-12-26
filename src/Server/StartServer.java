package Server;
import javax.swing.JOptionPane;

/**
 * Run the Server here
 * @author William Xu & Alex Raita
 *
 */
public class StartServer
{

	public static void main(String[] args)
	{

		Server server = new Server();

		Thread serverThread = new Thread(server);

		serverThread.start();

		int dialogResult = JOptionPane.showConfirmDialog (null, "Would you like to see a minimap of the entire world?","Warning",0);
		if(dialogResult == JOptionPane.YES_OPTION){
		
			ServerFrame myFrame = new ServerFrame();
			ServerGUI gui = new ServerGUI(server.getEngine().getWorld());
			myFrame.add(gui);
			gui.revalidate();
			server.getEngine().setGui(gui);
		}

	}

}
