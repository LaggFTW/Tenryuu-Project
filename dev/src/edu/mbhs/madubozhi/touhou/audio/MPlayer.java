package edu.mbhs.madubozhi.touhou.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import edu.mbhs.madubozhi.touhou.ui.FullGame;

public class MPlayer implements Runnable{
	private Thread thread;
	private Thread t;
	private Player p;
	private File target;
	private boolean musicChangeNotifier;
	private boolean running;
	
	public MPlayer(File target){
		this.target = target;
		this.musicChangeNotifier = target!=null;
		this.running = true;
		this.thread = new Thread(this);
		this.thread.start();
	}
	
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
	
	public boolean isPlaying(){
		return running && p != null && !p.isComplete();
	}
	
	public void changeAudio(File f){
		target = f;
		musicChangeNotifier = true;
	}

	public void terminateThread(){
		if (p != null){
			t.interrupt();
			p.close();
		}
		running = false;
		thread.interrupt();
	}

}
