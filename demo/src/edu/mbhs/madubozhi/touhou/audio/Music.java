package edu.mbhs.madubozhi.touhou.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

/**
 * Music class: creates a audio clip given a file URL, and has options
 * for playing and stopping the clip from playing.
 * @author Matt Du
 */
public class Music{
	//audio clip
	public Clip clip;

	/**
	 * Constructor: intializes the clip, given the URL of the file
	 * @param fileLocation
	 */
	public Music(String fileLocation){
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(fileLocation));
			clip = AudioSystem.getClip();
			clip.open(audioIn);
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
	 * Plays the audio clip
	 */
	public void start(){
		if(!clip.isActive())
			clip.start();
	}

	/**
	 * Stops the clip, and removes all references to it
	 */
	public void stop(){
		if (clip == null)
			return;
		clip.close();
		clip = null;
	}

}

