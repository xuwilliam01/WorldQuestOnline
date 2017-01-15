package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.PriorityQueue;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import Imports.Images;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerPlayer;

public class ClientScoreBoard extends JPanel{

	private PriorityQueue<ClientPlayerScore> redTeam = new PriorityQueue<ClientPlayerScore>();
	private PriorityQueue<ClientPlayerScore> blueTeam = new PriorityQueue<ClientPlayerScore>();
	Image scoreboardImage;
	private boolean gameover = false;
	private String winner = "Red Team";
	private int team = 0;
	private Client client;
	
	public ClientScoreBoard(Client client)
	{
		setDoubleBuffered(true);
		setFocusable(false);
		setOpaque(false);
		//setBorder(BorderFactory.createLineBorder(Color.black, 10));
		setSize((Client.SCREEN_WIDTH+ClientInventory.INVENTORY_WIDTH)/2, Client.SCREEN_HEIGHT/2);
		setLocation((Client.SCREEN_WIDTH+ClientInventory.INVENTORY_WIDTH)/4 - ClientInventory.INVENTORY_WIDTH/2, Client.SCREEN_HEIGHT/4);
		scoreboardImage = Images.getImage("scoreboard");
		this.client = client;
	}

	public void setWinner(int loser)
	{
		gameover = true;
		this.team = loser;
		if (team == ServerPlayer.RED_TEAM) 
		{
			winner = "Blue Team";
		}
		//client.getInventory().mainMenu.setLocation(Client.SCREEN_WIDTH/2-50, Client.SCREEN_HEIGHT/2+300);
		repaint();
	}
	
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		int yPos = 100;
		int xPos = (int)(0.5*Client.SCREEN_WIDTH/8);

		graphics.drawImage(scoreboardImage, 0, 0, (Client.SCREEN_WIDTH+ClientInventory.INVENTORY_WIDTH)/2, Client.SCREEN_HEIGHT/2, null);

		graphics.setFont(ClientWorld.BIG_NORMAL_FONT);
		if(gameover)
		{
			graphics.setColor(Color.red);
			if(team == ServerPlayer.RED_TEAM)
				graphics.setColor(Color.blue);
			String toDraw = String.format("%s won!", winner);
			graphics.drawString(toDraw, Client.SCREEN_WIDTH/4 - graphics.getFontMetrics().stringWidth(toDraw)/2,50);
		}
		
		graphics.setColor(Color.WHITE);
		yPos = 100;
		xPos = ClientFrame.getScaledWidth(58);
		
		for(ClientPlayerScore player: redTeam)
		{
			
			int fieldWidth = 80;
			String name = player.getName();
			int currentWidth = graphics.getFontMetrics().stringWidth("...");
			int endIndex = 0;
			for (int i=0; i<name.length(); i++){
				int letterWidth = graphics.getFontMetrics().stringWidth(name.substring(i, i+1));
				currentWidth += letterWidth;
				if (currentWidth > fieldWidth){
					name = name.substring(0, i) + "...";
					break;
				}
			}
			
			
			//graphics.drawString(String.format("%-24s%-10d%-10d%-15d%-10d",player.getName(),player.getKills(), player.getDeaths(), player.getScore(), player.getPing()), xPos, yPos);
			graphics.drawString(name, ClientFrame.getScaledWidth(58), yPos);
			graphics.drawString(String.format("%15d%15d%15d%14d",player.getKills(), player.getDeaths(), player.getScore(), player.getPing()), ClientFrame.getScaledWidth(146), yPos);
			yPos += 30;
		}

		yPos = 100;
		xPos = ClientFrame.getScaledWidth(440);
		graphics.setColor(Color.WHITE);
		for(ClientPlayerScore player: blueTeam)
		{
			int fieldWidth = 80;
			String name = player.getName();
			int currentWidth = graphics.getFontMetrics().stringWidth("...");
			int endIndex = 0;
			for (int i=0; i<name.length(); i++){
				int letterWidth = graphics.getFontMetrics().stringWidth(name.substring(i, i+1));
				currentWidth += letterWidth;
				if (currentWidth > fieldWidth){
					name = name.substring(0, i) + "...";
					break;
				}
			}
			//graphics.drawString(String.format("%-24s%-10d%-10d%-15d%-10d",player.getName(),player.getKills(), player.getDeaths(), player.getScore(), player.getPing()), xPos, yPos);
			graphics.drawString(name, ClientFrame.getScaledWidth(560), yPos);
			graphics.drawString(String.format("%15d%15d%15d%14d",player.getKills(), player.getDeaths(), player.getScore(), player.getPing()), ClientFrame.getScaledWidth(648), yPos);
			yPos += 30;
		}
	}


	public void addPlayer(String name, int id, int team, int kills, int deaths, int score, int ping)
	{
		if(team == ServerCreature.RED_TEAM)
			redTeam.add(new ClientPlayerScore(name,id, kills, deaths, score, ping));
		else
			blueTeam.add(new ClientPlayerScore(name,id, kills, deaths, score, ping));
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

	public void update(int id, int score, int ping, int team)
	{
		if(team == ServerCreature.RED_TEAM)
		{
			for(ClientPlayerScore player : redTeam)
				if(player.getId() == id)
				{
					player.setScore(score);
					player.setPing(ping);
					return;
				}
		}
		else
			for(ClientPlayerScore player : blueTeam)
				if(player.getId() == id)
				{
					player.setScore(score);
					player.setPing(ping);
					return;
				}
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
