package Client;

public class ClientPlayerScore implements Comparable<ClientPlayerScore>{
	private String name;
	private int id;
	private int kills;
	private int deaths;
	private int score;
	private int ping;
	
	public ClientPlayerScore(String name, int id, int kills, int deaths, int score, int ping)
	{
		this.name = name;
		this.id = id;
		this.kills = kills;
		this.deaths = deaths;
		this.score = score;
		this.ping = ping;
	}
	
	public String getName() {
		return name;
	}

	public int getKills() {
		return kills;
	}

	public int getDeaths() {
		return deaths;
	}
	
	public int getId()
	{
		return id;
	}

	public void setKills(int amount)
	{
		kills = amount;
	}
	
	public void addDeath()
	{
		deaths++;
	}
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getPing() {
		return ping;
	}

	public void setPing(int ping) {
		this.ping = ping;
	}

	@Override
	public int compareTo(ClientPlayerScore other) {
		if(this.kills != other.kills)
			return other.kills - kills;
		return deaths - other.deaths;
	}
	
}
