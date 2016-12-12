package START;

import java.io.IOException;

import CentralServer.CentralServer;

public class StartCentralServer {
	public static void main(String[] args) throws IOException {
		CentralServer server = new CentralServer();
		Thread serverThread = new Thread(server);
		serverThread.start();
	}
}
