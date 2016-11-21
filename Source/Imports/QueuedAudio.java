package Imports;


public class QueuedAudio implements Comparable<QueuedAudio>
{
	private GameAudio audio;
	private int dist;
	public QueuedAudio(int dist, GameAudio audio)
	{
		this.audio = audio;
		this.dist = dist;
	}
	@Override
	public int compareTo(QueuedAudio audio) {
		return this.dist-audio.getDist();
	}
	public GameAudio getAudio() {
		return audio;
	}
	public void setAudio(GameAudio audio) {
		this.audio = audio;
	}
	public int getDist() {
		return dist;
	}
	public void setDist(int dist) {
		this.dist = dist;
	}
	
}
