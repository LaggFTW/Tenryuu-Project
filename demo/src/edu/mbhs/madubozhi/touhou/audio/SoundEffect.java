package edu.mbhs.madubozhi.touhou.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

import edu.mbhs.madubozhi.touhou.ui.FullGame;

/**
 * Class used to manage SFX. It utilizes the Clip class in javax.sound to play the effects. Each sound effect is
 * indexed, with the index of each sound effect specified in ./audio/sfx.txt, where each listed file, included in
 * ./audio/sfx/ is given the index n - 1, where n is the line number the file is listed.
 * @author Bowen Zhi and Matt Du
 *
 */
public class SoundEffect{
	private Clip clip = null;
	private int ID;	

	/**
	 * Instantiates a SoundEffect with the default ID 0
	 */
	public SoundEffect(){
		readFile(0);	
	}

	/**
	 * Instantiates a SoundEffect with the specified ID
	 * @param ID index corresponding to a sound effect
	 */
	public SoundEffect(int ID){
		readFile(ID);
	}

	/**
	 * Reads in the specified file at the ID number
	 * @param ID index corresponding to a sound effect
	 */
	private void readFile(int ID){
		try{
			Scanner reader = new Scanner(new FileReader(new File("audio/sfx.txt")));
			for(int i = 0; reader.hasNextLine(); i++){
				if(i==ID){
					AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("audio/sfx/" + reader.nextLine()));
					clip = AudioSystem.getClip();
					clip.open(audioIn);
					break;
				}
				else {
					reader.nextLine();
				}
			}
			if(clip==null)
				JOptionPane.showMessageDialog(null, "Error: No sound effect file was found for ID " + ID + ". \nSound effect not loaded.", "Sound Effect not found", JOptionPane.ERROR_MESSAGE);
		}catch(FileNotFoundException e){
			//if the file could not be found
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: Music file not found.", "File Not Found Exception", JOptionPane.ERROR_MESSAGE);
		}catch(UnsupportedAudioFileException e){
			//if the audio type is not supported
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: Unsupported audio type.", "File Not Found Exception", JOptionPane.ERROR_MESSAGE);
		}catch(IOException e){
			//if an input/output exception occurs
			e.printStackTrace();
		}catch(LineUnavailableException e){
			//if the line is unvailable
			e.printStackTrace();
		}
	}

	/**
	 * Begins playing this sound effect. Note that to prevent memory leaks, one must call the stop() method afterwards
	 * to clear the Clip from memory.
	 */
	public void play(){
		if(!clip.isActive())
			clip.start();
	}
	
	/**
	 * Begins playing this sound effect, and continues until the sound effect file has finished playing, at which time
	 * the Clip is automatically closed, and the Thread terminated.
	 */
	public void playToStop(){
		Thread t = new Thread(){
			public void run(){
				if(!clip.isActive())
					clip.start();
				while (true){
					try {
						Thread.sleep(FullGame.MILLIS_PER_FRAME);
					} catch (InterruptedException e) {}
					if (!clip.isRunning()){
						clip.close();
						clip = null;
						break;
					}
				}
			}
		};
		t.start();
	}

	/**
	 * Closes the Clip, halting this sound effect
	 */
	public void stop(){
		clip.close();
		clip=null;
	}

	/**
	 * Reads in a new sound effect at the specified ID
	 * @param ID index corresponding to a sound effect
	 */
	public void setID(int ID){
		readFile(ID);
	}

	/**
	 * @return index corresponding to the current playing sound effect
	 */
	public int getID(){
		return this.ID;
	}
	
}