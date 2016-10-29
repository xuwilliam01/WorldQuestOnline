package Client;

public class ClientPlayerScore implements Comparable<ClientPlayerScore>{
	private String name;
	private int id;
	private int kills = 0;
	private int deaths = 0;
	
	public ClientPlayerScore(String name, int id)
	{
		this.name = name;
		this.id = id;
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
	
	@Override
	public int compareTo(ClientPlayerScore other) {
		if(this.kills != other.kills)
			return other.kills - kills;
		return deaths - other.deaths;
	}
	
}
