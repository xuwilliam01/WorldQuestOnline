package Imports;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GameAudio {

	private String name;
	private Clip audio;
	
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

	public void play()
	{
		if(!audio.isActive())
			audio.loop(1);
	}

	
}
