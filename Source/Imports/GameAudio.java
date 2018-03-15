package Imports;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GameAudio {

	private String name;
	private Clip audio;
	private FloatControl gainControl;
	public static final float MAX_DISTANCE = 1080;
	public static boolean audioSupported = true;
	
	public GameAudio(String name)
	{
		this.name = name;
		
		if (!Imports.Audio.isServer)
		{
			File soundFile = new File("Audio//"+name+".wav");
			try {
				AudioInputStream sound = AudioSystem.getAudioInputStream(soundFile);
				DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
			    audio = (Clip) AudioSystem.getLine(info);
			    audio.open(sound);
				gainControl = 
					    (FloatControl) audio.getControl(FloatControl.Type.MASTER_GAIN);
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				System.out.println("Audio not working: " + name);
			} catch (IllegalArgumentException e){
				e.printStackTrace();
				System.out.println("No audio output enabled on this device");
				audioSupported = false;
			} catch (Exception e){
				e.printStackTrace();
				System.out.println("Unknown issue with audio");
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Clip getAudio() {
		return audio;
	}

	public void setAudio(Clip audio) {
		this.audio = audio;
	}

	public boolean isActive()
	{
		return audio.isActive();
	}

	public void play(float dist)
	{
		if (!audioSupported)
		{
			return;
		}
		try
		{
		gainControl.setValue(Math.max(-3-dist*Client.Client.distanceConstant,-50));
		audio.setFramePosition(0);
		audio.start();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			System.out.println("No sound file available");
			audioSupported = false;
		}
	}
	
	public void loop()
	{
		if (!audioSupported)
		{
			return;
		}
		try
		{
		audio.setFramePosition(0);
		audio.loop(99);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			System.out.println("No sound file available");
			audioSupported = false;
		}
	}
	
	public void stop()
	{
		if (!audioSupported)
		{
			return;
		}
		try
		{
			audio.setFramePosition(0);
			audio.stop();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			System.out.println("No sound file available");
			audioSupported = false;
		}
	}
	
	public boolean isRunning()
	{
		return audio.isRunning();
	}

}
