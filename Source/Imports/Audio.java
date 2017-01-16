package Imports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import Menu.MainMenu;
import Server.ServerEngine;

public class Audio {

	private static GameAudio[] audioArray;
	private static Map<String, Integer> audioMap = new HashMap<String, Integer>();
	private static int numAudioClips = 0;

	private static boolean imported = false;
	
	private static GameAudio bgm;
	
	public static boolean isServer = false;


	public static void main(String[] args) throws InterruptedException {
		importAudio(false);
		//playAudio("Damage",0);
		for(int i =0; i < 10;i++)
		{
			//playAudio("gag",0);
			Thread.sleep(500);
		}
	}
	
	private static class bgmClass implements Runnable
	{
		@Override
		public void run() {
			while (true)
			{
				if (isServer)
				{
					break;
				}
				
				if (!GameAudio.audioSupported)
				{
					break;
				}

				if (Client.Client.inGame && bgm!=null)
				{
					if (bgm == audioArray[audioMap.get("bgm_menu")])
					{
						bgm.stop();
						int random = (int)(Math.random()*5);
						bgm = audioArray[audioMap.get("bgm_game"+random)];
						bgm.play(0);
					}
					else if (!bgm.isRunning())
					{
						bgm.stop();
						int random = (int)(Math.random()*5);
						bgm = audioArray[audioMap.get("bgm_game"+random)];
						bgm.play(0);
					}
				}
				else
				{
					if (bgm != audioArray[audioMap.get("bgm_menu")])
					{
						if (bgm!=null)
						{
							bgm.stop();
						}
						bgm = audioArray[audioMap.get("bgm_menu")];
						bgm.loop();
					}
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void addToAudioArray(GameAudio audio)
	{
		audioArray[numAudioClips] = audio;
		numAudioClips++;
	}

	public static void importAudio(boolean isClient)
	{
		if (imported)
			return;
		imported = true;
		
		if (isClient && GameAudio.audioSupported)
		{
			int willImport = JOptionPane.showConfirmDialog(null, "Import audio? Some clients may not work with it.", "Wanna hear music or not", JOptionPane.YES_NO_OPTION);
			if (willImport != JOptionPane.YES_OPTION)
			{
				GameAudio.audioSupported = false;
				return;
			}
		}
		
		audioArray = new GameAudio[1000];

		//Import all audio
		addToAudioArray(new GameAudio("heartbeat"));
		addToAudioArray(new GameAudio("ice_break"));
		addToAudioArray(new GameAudio("gag"));
		addToAudioArray(new GameAudio("cut"));
		addToAudioArray(new GameAudio("fireball"));
		addToAudioArray(new GameAudio("megabow"));
		addToAudioArray(new GameAudio("fire_explode"));
		
		for (int no = 0; no < 7; no++)
		{
			addToAudioArray(new GameAudio("cut_air"+no));
		}
		
		addToAudioArray(new GameAudio("bgm_menu"));
		
		for (int no = 0; no < 5; no++)
		{
			addToAudioArray(new GameAudio("bgm_game"+no));
		}
		

		//Configure storage for audio
		GameAudio[] clone = audioArray;
		audioArray = new GameAudio[numAudioClips];
		for (int i = 0; i < numAudioClips; i++)
		{
			audioArray[i] = clone[i];
			audioMap.put(audioArray[i].getName(),i);
		}
		
		Thread bgmThread = new Thread(new bgmClass());
		bgmThread.start();
	}

//	public static void playAudio(String name, int dist)
//	{
//		audioArray[audioMap.get(name)].play(dist);
//	}

	private static long cooldownStart = 0;
	
	public static ArrayList<QueuedAudio> currentlyPlaying = new ArrayList<QueuedAudio>();
	public final static int maxConcurrentAudio = 5;
	
	// Make an exception for closer sounds (compared to the farthest sound currently playing)
	public final static int MIN_EXCEPTION_DIST = 32;
	
	public synchronized static void playAudio(int index, float dist)
	{
		if (!GameAudio.audioSupported)
		{
			return;
		}
		audioArray[index].play(dist);
		
//		ArrayList<QueuedAudio>toRemove = new ArrayList<QueuedAudio>();
//		for (QueuedAudio audio:currentlyPlaying)
//		{
//			if (!audio.getAudio().isActive())
//			{
//				toRemove.add(audio);
//			}
//		}
//		
//		for (QueuedAudio audio:toRemove)
//		{
//			currentlyPlaying.remove(audio);
//		}
//		
//		if (System.currentTimeMillis()-cooldownStart>=ServerEngine.UPDATE_RATE && (currentlyPlaying.size()< maxConcurrentAudio || (dist+MIN_EXCEPTION_DIST<currentlyPlaying.get(maxConcurrentAudio-1).getDist())))
//		{
//			if (audioArray[index].isActive())
//			{
//				QueuedAudio newAudio = new QueuedAudio(dist,new GameAudio(audioArray[index].getName()));
//				currentlyPlaying.add(newAudio);
//				newAudio.getAudio().play(dist);
//			}
//			else
//			{
//				currentlyPlaying.add(new QueuedAudio(dist,audioArray[index]));
//				audioArray[index].play(dist);
//			}
//			cooldownStart = System.currentTimeMillis();
//		
//		}
	}
	
	
	
	public static int getIndex(String name)
	{
		return audioMap.get(name);
	}
}
