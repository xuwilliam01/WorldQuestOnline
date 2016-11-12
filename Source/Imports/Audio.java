package Imports;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner; 

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class Audio {

	private static GameAudio[] audioArray;
	private static Map<String, Integer> audioMap = new HashMap<String, Integer>();
	private static int numAudioClips = 0;
	
	private static boolean imported = false;
	
	public static void main(String[] args) {
		importAudio();
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
		
		//Configure storage for audio
		GameAudio[] clone = audioArray;
		audioArray = new GameAudio[numAudioClips];
		for (int i = 0; i < numAudioClips; i++)
		{
			audioArray[i] = clone[i];
			audioMap.put(audioArray[i].getName(),i);
		}
	}
	
	public static void playAudio(String name)
	{
		audioArray[audioMap.get(name)].play();
	}
	
	public static void playAudio(int index)
	{
		audioArray[index].play();
	}
	
	public static int getIndex(String name)
	{
		return audioMap.get(name);
	}
}
