package edu.mbhs.madubozhi.touhou.game.enemy;

import java.awt.Color;
import java.util.ArrayList;

import edu.mbhs.madubozhi.touhou.game.enemy.pattern.EnemyMovementPattern;
import edu.mbhs.madubozhi.touhou.game.util.Oscillator;

/**
 * Yo dawg, I heard you like enemies, so I put an enemy in your enemy so you can shoot enemies why you shoot
 * enemies. EnemyEnemy that shoots out other bullets (in patterns, of course)
 * @author bowenzhi
 *
 */
public class EmbeddedEnemy extends EnemyEnemy {
/**
 * Enemy with other enemies and patterns embedded within it
 */
	private EnemyMovementPattern embeddedPattern;
	private Oscillator oscillator;
	private int frequency;
	private int counter;
	
	/**
	 * Instantiates this enemy with the specified variables
	 * @param x Initial x position
	 * @param target Target location
	 * @param size Size of enemy
	 * @param yVel Velocity of enemy
	 * @param color Color of enemy
	 * @param border Border of frame that this enemy is drawn in
	 * @param fWidth Width of frame that this enemy is drawn in
	 * @param lockAngle Whether or not this enemy's orientation changes based on its path
	 * @param embeddedPattern Pattern of enemies that this enemy spawns out
	 * @param frequency Frequency of spawning
	 * @param oscillator Oscillator object for oscillatory patterns
	 */
	public EmbeddedEnemy(int x, int target, int size, double yVel, Color color,
			int border, int fWidth, int hp, boolean lockAngle, EnemyMovementPattern embeddedPattern,
			int frequency, Oscillator oscillator) {
		super(x, target, size, yVel, color, border, fWidth, hp, lockAngle, true);
		this.embeddedPattern = embeddedPattern;
		this.oscillator = oscillator;
		this.frequency = frequency;
		this.counter = 0;
	}

	/**
	 * @return An ArrayList containing any enemies that were spawned by this EmbeddedEnemy this iteration.
	 */
	public ArrayList<Enemy> launchPattern(){
		ArrayList<Enemy> arr = null;
		if (counter >= frequency && embeddedPattern != null){
			embeddedPattern.setOrigin((float)this.x, (float)this.y);
			arr = new ArrayList<Enemy>();
			ArrayList<Enemy> temp = embeddedPattern.step();
			if (temp != null)
				for(Enemy b:temp)
					arr.add(b);
			embeddedPattern.reset(oscillator!=null?oscillator.getAngle():embeddedPattern.getStartAngle());
			counter = 0;
		}
		if (oscillator!=null)
			oscillator.oscillate();
		counter++;
		return arr;
	}
	
	public EmbeddedEnemy cloneBasic(){
		return new EmbeddedEnemy(this.initx, this.targetX, this.size, this.yVel, this.color, this.border, this.fWidth,
				this.hp, this.lockAngle, this.embeddedPattern.clone(), this.frequency, 
				oscillator!=null?this.oscillator.clone():null);
	}

}
