package START;

import java.io.IOException;

import org.jdom2.JDOMException;

import CentralServer.CentralServer;
import ClientUDP.ClientAccountWindow;

public class StartCentralServer {
	public static void main(String[] args) throws IOException, JDOMException {
		CentralServer server = new CentralServer();
		Thread serverThread = new Thread(server);
		serverThread.start();
		System.out.println("Central server started");
//		ClientAccountWindow cl = new ClientAccountWindow(9000);
//		Thread newThread = new Thread(cl);
//		newThread.start();
	}
}
