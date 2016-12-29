package CentralServer;

public class GameResult {
	private String name;
	private int elo;
	
	//Eventually will change to score
	private int kills;
	
	public GameResult(String name, int kills)
	{
		this.name = name;
		this.kills = kills;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getElo() {
		return elo;
	}

	public void setElo(int elo) {
		this.elo = elo;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}
	
	
	
}
