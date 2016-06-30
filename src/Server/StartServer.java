package Server;

import java.io.IOException;

import javax.swing.JOptionPane;

/**
 * Run the Server here
 * @author William Xu & Alex Raita
 *
 */
public class StartServer
{

	/**
	 * Start up the server
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		// Allow the user to enter a specific file to run the server for
		String fileName = JOptionPane
				.showInputDialog("Please enter the file you want to use for the server");
		if (fileName == null)
			return;

		// Allow the user to enter a port for the server
		int portNum;
		while (true)
		{
			String port = JOptionPane
					.showInputDialog("Please enter the port you want to use for the server");
			if (port == null)
				return;

			try
			{
				portNum = Integer.parseInt(port);
				break;
			}
			catch (NumberFormatException e)
			{
			}
		}

		// Run the server
		Server server = new Server(fileName, portNum);
		Thread serverThread = new Thread(server);
		serverThread.start();

//		int dialogResult = JOptionPane.showConfirmDialog(null,
//				"Would you like to see a minimap of the entire world?",
//				"Warning", 0);
//		if (dialogResult == JOptionPane.YES_OPTION)
//		{
//			ServerFrame myFrame = new ServerFrame();
//			ServerGUI gui = new ServerGUI(server.getEngine().getWorld(),
//					server.getEngine());
//			gui.setLocation(0, 0);
//			myFrame.add(gui);
//			gui.revalidate();
//			server.getEngine().setGui(gui);
//		}
	}
}
