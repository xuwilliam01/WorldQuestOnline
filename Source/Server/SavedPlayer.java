package Server;

import Server.Items.ServerItem;

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
	ServerItem bestWeapon;
	ServerItem bestArmour;
	long leaveTime;

	public SavedPlayer(String name, int money, int kills, int deaths, int totalDmg, int totalMoneySpent, int team, String hair, String skinColour, ServerItem bestWeapon, ServerItem bestArmour, long leaveTime)
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
		this.bestWeapon = bestWeapon;
		this.bestArmour = bestArmour;
		this.leaveTime = leaveTime;
	}

}
