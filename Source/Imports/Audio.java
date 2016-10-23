package Imports;

import java.util.Scanner;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class Audio {

	public static void main(String[] args) {
		importAudio();
	}
	
	public static void importAudio()
	{
		GameAudio heartbeat = new GameAudio("heartbeat");
		GameAudio gag = new GameAudio("gag");
		Scanner scan = new Scanner (System.in);
		while (true)
		{
			String message = scan.nextLine();
			if (message.equals("play"))
			{

				heartbeat.getAudio().loop(Clip.LOOP_CONTINUOUSLY);
				System.out.println("Playing");
			}
			else if (message.equals("stop"))
			{
				heartbeat.getAudio().stop();
			}
			else if (message.equals("gag"))
			{
				gag.getAudio().start();
				System.out.println("gagged");
			}
			else if (message.equals("stopgag"))
			{
				gag.getAudio().stop();
			}
		}
	}
}
