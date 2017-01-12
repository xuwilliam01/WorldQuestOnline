package CentralServer;

public class LeaderboardPlayer implements Comparable<LeaderboardPlayer>{

	private int rating = 0;
	private String name;
	private int wins;
	private int losses;
	
	public LeaderboardPlayer(String name, int rating, int wins, int losses)
	{
		this.name = name;
		this.rating = rating;
		this.wins = wins;
		this.losses = losses;
	}
	@Override
	public int compareTo(LeaderboardPlayer o) {
		// TODO Auto-generated method stub
		return rating - o.rating;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWins() {
		return wins;
	}
	public void setWins(int wins) {
		this.wins = wins;
	}
	public int getLosses() {
		return losses;
	}
	public void setLosses(int losses) {
		this.losses = losses;
	}
	
	

}