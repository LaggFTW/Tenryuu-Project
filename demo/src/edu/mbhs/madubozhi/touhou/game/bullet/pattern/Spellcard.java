package edu.mbhs.madubozhi.touhou.game.bullet.pattern;

import java.util.ArrayList;

import edu.mbhs.madubozhi.touhou.game.bullet.Bullet;
import edu.mbhs.madubozhi.touhou.game.util.Mode;
import edu.mbhs.madubozhi.touhou.game.util.Oscillator;

/**
 * A spellcard is a complex pattern consisting of multiple layers of bullet patterns.
 * @author bowenzhi
 */
public class Spellcard {
	private String name;
	private int[] durations;
	private int hp;
	private boolean timeOutOnly;
	private boolean finished;
	private ArcBulletPattern[] bp;
	private int[][] frequencies;
	private long[] delays;
	private Oscillator[] oscillators;
	private int numPatterns;
	private int difficulty;
	
	private long durationCounter;//Actual duration counter
	private int maxFrequency;//Maximum frame frequency of bullet patterns
	private int freqCounter;//Frame counter for frequencies
	
	public Spellcard(String name, int[] durations, int hp, boolean timeOutOnly, ArcBulletPattern[] bp, 
			int[][] frequencies, int[] delays, Oscillator[] oscillators, 
			int numPatterns){
		this.name = name;
		this.hp = hp;
		this.durations = new int[4];
		this.timeOutOnly = timeOutOnly;
		this.finished = false;
		this.bp = new ArcBulletPattern[numPatterns];
		this.frequencies = new int[numPatterns][4];
		this.delays = new long[numPatterns];
		this.durationCounter = 0L;
		this.freqCounter = 0;
		this.oscillators = new Oscillator[numPatterns];
		int i;
		for (i = 0; i < 4; i++){
			this.durations[i] = durations[i];
		}
		this.maxFrequency = 0;
		for (i = 0; i < numPatterns; i++){
			this.bp[i] = bp[i];
			this.delays[i] = delays[i];
			this.oscillators[i] = oscillators[i];
			for (int j = 0; j < 4; j++){
				this.frequencies[i][j] = frequencies[i][j];
				if (this.frequencies[i][j] > this.maxFrequency){
					this.maxFrequency = this.frequencies[i][j];
				}
			}
		}
		this.numPatterns = numPatterns;
		this.difficulty = 0;
	}
	
	public ArrayList<Bullet> step(float dt){
		//TODO: Implement HP
		ArrayList<Bullet> arr = new ArrayList<Bullet>();
		ArrayList<Bullet> temp;
		if (durationCounter >= durations[difficulty]){
			finished = true;
			return arr;
		}
		if (this.freqCounter >= this.maxFrequency){
			freqCounter = 0;
		}
		for (int i = 0; i < this.numPatterns; i++){
			if(durationCounter >= this.delays[i] && freqCounter % this.frequencies[i][difficulty] == 0){
				if (this.bp[i].isFinished())
					this.bp[i].reset(this.oscillators[i].getAngle());
				temp = this.bp[i].step();
				if (temp != null)
					for (Bullet b:temp){
						arr.add(b);
					}
			}
			oscillators[i].oscillate();
		}
		durationCounter += dt;
		freqCounter++;
		return arr;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean isFinished(){
		return finished;
	}
	
	public void setDifficulty(Mode difficulty){
		this.difficulty = difficulty.getIndex();
		if (this.difficulty == 4){
			this.difficulty = 3;
		}
	}
	
	public void setName(String name){
		this.name = name;
	}
	
}
