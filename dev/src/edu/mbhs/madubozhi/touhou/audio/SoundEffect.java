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

public class SoundEffect{
	private Clip clip = null;
	private int ID;	

	public SoundEffect(){
		readFile(0);	
	}

	public SoundEffect(int ID){
		readFile(ID);
	}

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

	public void play(){
		if(!clip.isActive())
			clip.start();
	}
	
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

	public void stop(){
		clip.close();
		clip=null;
	}

	public void setID(int ID){
		readFile(ID);
	}

	public int getID(){
		return this.ID;
	}
	
}