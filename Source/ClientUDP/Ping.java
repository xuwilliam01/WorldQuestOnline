package ClientUDP;

public class Ping {
	private String IP;
	private int port;
	private long start;
	
	public Ping(String IP, int port, long start)
	{
		this.IP = IP;
		this.port = port;
		this.start = start;
	}

	public String getIP() {
		return IP;
	}

	public int getPort() {
		return port;
	}

	public long getStart() {
		return start;
	}
	
	
}
