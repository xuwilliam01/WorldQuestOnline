package CentralServer;

public class LeaderboardPlayer implements Comparable<LeaderboardPlayer>{

	private int rating = 0;
	private String name;
	
	public LeaderboardPlayer(String name, int rating)
	{
		this.name = name;
		this.rating = rating;
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
	
	

}
