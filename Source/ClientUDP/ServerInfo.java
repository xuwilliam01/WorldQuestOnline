package ClientUDP;

public class ServerInfo{
	private String name;
	private String IP;
	private int port;
	private int numPlayers;
	
	public ServerInfo(String name, String IP, int port, int numPlayers)
	{
		this.name = name;
		this.IP = IP;
		System.out.println("Server received: " + name + " " + IP + " " + port);
		this.port = port;
		this.numPlayers = numPlayers;
	}

	public String getName() {
		return name;
	}

	public String getIP() {
		return IP;
	}

	public int getPort() {
		return port;
	}
	
	public int getNumPlayers() {
		return numPlayers;
	}

	@Override
    public boolean equals(Object object)
    {
		return object != null && object instanceof ServerInfo && IP.equals(((ServerInfo)object).IP) && port == ((ServerInfo)object).port;
	}
	
	
}
