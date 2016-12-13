package ClientUDP;

public class ServerInfo{
	private String name;
	private String IP;
	private int port;
	private int numPlayers;
	private String origIP = null;
	
	public ServerInfo(String name, String IP, int port, int numPlayers)
	{
		this.name = name;
		this.IP = IP;
		//System.out.println("Server received: " + name + " " + IP + " " + port);
		this.port = port;
		this.numPlayers = numPlayers;
	}

	public ServerInfo(String name, String IP, int port, int numPlayers, String origIP)
	{
		this.name = name;
		this.IP = IP;
		//System.out.println("Server received: " + name + " " + IP + " " + port);
		this.port = port;
		this.numPlayers = numPlayers;
		this.origIP = origIP;
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

	public String getOrigIP()
	{
		return origIP;
	}
	
	@Override
    public boolean equals(Object object)
    {
		return object != null && object instanceof ServerInfo && IP.equals(((ServerInfo)object).IP) && port == ((ServerInfo)object).port;
	}
	
	
}
