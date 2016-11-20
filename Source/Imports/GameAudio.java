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

import Server.ServerEngine;

public class GameAudio {

	private String name;
	private Clip audio;
	private FloatControl gainControl;
	
	public GameAudio(String name)
	{
		this.name = name;
		File soundFile = new File("Audio//"+name+".wav");
		try {
			AudioInputStream sound = AudioSystem.getAudioInputStream(soundFile);
			DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
		    audio = (Clip) AudioSystem.getLine(info);
		    audio.open(sound);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		gainControl = 
			    (FloatControl) audio.getControl(FloatControl.Type.MASTER_GAIN);
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

	public void play(int dist)
	{
		gainControl.setValue(3f - Client.Client.distanceConstant*dist);
		audio.loop(1);
	}
}
