package Server;
import java.awt.Dimension;
import javax.swing.JFrame;


@SuppressWarnings("serial")
public class ServerFrame extends JFrame
{

	/**
	 * The zoom amount for the map
	 */
	public static final int FRAME_FACTOR = 2;
	
	/**
	 * Constructor for the game frame
	 */
	public ServerFrame()
	{
		setPreferredSize(new Dimension(Client.Client.SCREEN_WIDTH/FRAME_FACTOR, Client.Client.SCREEN_HEIGHT/FRAME_FACTOR));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("WorldQuest Online - Server Map");
		setLocationRelativeTo(null);
		setLayout(null);
		pack();
		setVisible(true);
	}
}