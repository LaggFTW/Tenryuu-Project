package edu.mbhs.madubozhi.touhou.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import edu.mbhs.madubozhi.touhou.ui.FullGame;

/**
 * Implementation of a multi-threaded media player. It utilizes the JLayer Player class. 
 * @author bowenzhi
 *
 */
public class MPlayer implements Runnable{
	private Thread thread;
	private Thread t;
	private Player p;
	private File target;
	private boolean musicChangeNotifier;
	private boolean running;
	
	/**
	 * Instantiates this MPlayer to immediately begin playing the specified media file
	 * @param target media file to play
	 */
	public MPlayer(File target){
		this.target = target;
		this.musicChangeNotifier = target!=null;
		this.running = true;
		this.thread = new Thread(this);
		this.thread.start();
	}
	
	/**
	 * Instantiates this MPlayer, which initially is silent
	 */
	public MPlayer(){
		this(null);
	}

	@Override
	public void run() {
		while (running){
			if (musicChangeNotifier){
				playNew();
			}
			try {
				Thread.sleep(FullGame.MILLIS_PER_FRAME);
			} catch (InterruptedException e) {}
		}
	}
	
	/**
	 * Stops the current thread playing the media, and opens a new thread to fill memory of the old one, playing a new
	 * media file.
	 */
	private void playNew() {
		if (p != null){
			t.interrupt();
			p.close();
		}
		if (target == null){
			p = null;
		} else {
			t = new Thread(){
				public void run(){
					try {
						p = new Player(new FileInputStream(target));
						p.play();
					} catch (FileNotFoundException e) {
						System.err.println("MPlayer: 404 File Not Found");
					} catch (JavaLayerException e) {
						System.err.println("MPlayer: JLayer Exception");
					}
				}
			};
			t.start();
		}
		musicChangeNotifier = false;
	}
	
	/**
	 * @return true if media is still being played
	 */
	public boolean isPlaying(){
		return running && p != null && !p.isComplete();
	}
	
	/**
	 * Sets this MPlayer to a new media file target, and begins to play this file immediately
	 * @param f
	 */
	public void changeAudio(File f){
		target = f;
		musicChangeNotifier = true;
	}

	/**
	 * Stops the thread running this MPlayer, thus halting any and all currently streaming media
	 */
	public void terminateThread(){
		if (p != null){
			t.interrupt();
			p.close();
		}
		running = false;
		thread.interrupt();
	}

}
