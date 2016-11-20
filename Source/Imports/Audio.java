package Imports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Server.ServerEngine;

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
			//playAudio("gag",0);
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
		addToAudioArray(new GameAudio("cut"));

		//Configure storage for audio
		GameAudio[] clone = audioArray;
		audioArray = new GameAudio[numAudioClips];
		for (int i = 0; i < numAudioClips; i++)
		{
			audioArray[i] = clone[i];
			audioMap.put(audioArray[i].getName(),i);
		}
	}

//	public static void playAudio(String name, int dist)
//	{
//		audioArray[audioMap.get(name)].play(dist);
//	}

	
	public static ArrayList<GameAudio> currentlyPlaying = new ArrayList<GameAudio>();
	public static int maxConcurrentAudio = 5;
	
	public static void playAudio(int index, int dist)
	{
		ArrayList<GameAudio>toRemove = new ArrayList<GameAudio>();
		for (GameAudio audio:currentlyPlaying)
		{
			if (!audio.isActive())
			{
				toRemove.add(audio);
			}
		}
		
		for (GameAudio audio:toRemove)
		{
			currentlyPlaying.remove(audio);
		}
		
		if (currentlyPlaying.size()< maxConcurrentAudio)
		{
			if (audioArray[index].isActive())
			{
				GameAudio newAudio = new GameAudio(audioArray[index].getName());
				currentlyPlaying.add(newAudio);
				newAudio.play(dist);
			}
			else
			{
				currentlyPlaying.add(audioArray[index]);
				audioArray[index].play(dist);
			}
		
		}
	}

	public static int getIndex(String name)
	{
		return audioMap.get(name);
	}
}
