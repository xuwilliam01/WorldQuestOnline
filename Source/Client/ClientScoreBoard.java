package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.PriorityQueue;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import Server.Creatures.ServerCreature;

public class ClientScoreBoard extends JPanel{

	private PriorityQueue<ClientPlayerScore> redTeam = new PriorityQueue<ClientPlayerScore>();
	private PriorityQueue<ClientPlayerScore> blueTeam = new PriorityQueue<ClientPlayerScore>();
	
	public ClientScoreBoard()
	{
		setDoubleBuffered(true);
		setFocusable(false);
		setOpaque(false);
		setBorder(BorderFactory.createLineBorder(Color.black, 10));
		setSize(Client.SCREEN_WIDTH/2, Client.SCREEN_HEIGHT/2);
		setLocation(Client.SCREEN_WIDTH/4, Client.SCREEN_HEIGHT/4);
	}
	
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		int yPos = 100;
		int xPos = (int)(0.5*Client.SCREEN_WIDTH/8);
		graphics.setColor(Color.blue);
		graphics.setFont(ClientWorld.BIG_NORMAL_FONT);
		for(ClientPlayerScore player: blueTeam)
		{
			graphics.drawString(String.format("%20s%3d%3d",player.getName(),player.getKills(), player.getDeaths()), xPos, yPos);
			yPos += 30;
		}
		
		yPos = 100;
		xPos = (int)(Client.SCREEN_WIDTH/4);
		graphics.setColor(Color.red);
		for(ClientPlayerScore player: redTeam)
		{
			graphics.drawString(String.format("%20s%3d%3d",player.getName(),player.getKills(), player.getDeaths()), xPos, yPos);
			yPos += 30;
		}
	}
	
	public void addPlayer(String name, int id, int team)
	{
		if(team == ServerCreature.RED_TEAM)
			redTeam.add(new ClientPlayerScore(name,id));
		else
			blueTeam.add(new ClientPlayerScore(name,id));
	}
	
	public void addKill(int id, int team)
	{
		PriorityQueue<ClientPlayerScore> players;
		if(team == ServerCreature.RED_TEAM)
			players = redTeam;
		else
			players = blueTeam;
		
		ClientPlayerScore p = null;
		int kills = 0;
		for(ClientPlayerScore player : players)
			if(player.getId() == id)
			{
				p = player;
				kills = p.getKills()+1;
				p.setKills(-1);
				break;
			}
		players.remove(p);
		p.setKills(kills);
		players.add(p);
	}
	
	public void addDeath(int id, int team)
	{
		PriorityQueue<ClientPlayerScore> players;
		if(team == ServerCreature.RED_TEAM)
			players = redTeam;
		else
			players = blueTeam;
		
		ClientPlayerScore p = null;
		int kills = 0;
		for(ClientPlayerScore player : players)
			if(player.getId() == id)
			{
				p = player;
				kills = player.getKills();
				player.setKills(-1);
				break;
			}
		players.remove(p);
		p.setKills(kills);
		p.addDeath();
		players.add(p);
	}
	
	public void removePlayer(int id, int team)
	{
		PriorityQueue<ClientPlayerScore> players;
		if(team == ServerCreature.RED_TEAM)
			players = redTeam;
		else
			players = blueTeam;
		
		ClientPlayerScore toRemove = null;
		for(ClientPlayerScore player : players)
			if(player.getId() == id)
			{
				player.setKills(-1);
				toRemove = player;
			}
		players.remove(toRemove);
	}
}
