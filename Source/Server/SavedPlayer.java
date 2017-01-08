package Server;

public class SavedPlayer {

	String name;
	int money;
	int kills;
	int deaths;
	int totalDmg;
	int totalMoney;
	int team;
	String hair;
	String skinColour;

	public SavedPlayer(String name, int money, int kills, int deaths, int totalDmg, int totalMoneySpent, int team, String hair, String skinColour)
	{
		this.name = name;
		this.money = money;
		this.kills = kills;
		this.deaths = deaths;
		this.totalDmg = totalDmg;
		this.totalMoney = totalMoneySpent;
		this.team = team;
		this.hair = hair;
		this.skinColour = skinColour;
	}

}
