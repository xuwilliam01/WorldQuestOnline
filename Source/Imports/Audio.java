package Imports;

import java.util.HashMap;
import java.util.Map;

public class Audio {

	private static GameAudio[] audioArray;
	private static Map<String, Integer> audioMap = new HashMap<String, Integer>();
	private static int numAudioClips = 0;

	private static boolean imported = false;


	public static void main(String[] args) throws InterruptedException {
		importAudio();
		//playAudio("Damage",0);
		for(int i =0; i < 10;i++)
		{
			playAudio("gag",0);
			Thread.sleep(500);
		}
	}

	public static void addToAudioArray(GameAudio audio)
	{
		audioArray[numAudioClips] = audio;
		numAudioClips++;
	}

	public static void importAudio()
	{
		if (imported)
			return;
		imported = true;
		audioArray = new GameAudio[1000];

		//Import all audio
		addToAudioArray(new GameAudio("heartbeat"));
		addToAudioArray(new GameAudio("gag"));
		addToAudioArray(new GameAudio("Damage"));

		//Configure storage for audio
		GameAudio[] clone = audioArray;
		audioArray = new GameAudio[numAudioClips];
		for (int i = 0; i < numAudioClips; i++)
		{
			audioArray[i] = clone[i];
			audioMap.put(audioArray[i].getName(),i);
		}
	}

	public static void playAudio(String name, int dist)
	{
		audioArray[audioMap.get(name)].play(dist);
	}

	public static void playAudio(int index, int dist)
	{
		audioArray[index].play(dist);
	}

	public static int getIndex(String name)
	{
		return audioMap.get(name);
	}
}
