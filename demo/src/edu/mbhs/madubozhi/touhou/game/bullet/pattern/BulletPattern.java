package edu.mbhs.madubozhi.touhou.game.bullet.pattern;

import java.util.ArrayList;

import edu.mbhs.madubozhi.touhou.game.bullet.Bullet;


public abstract class BulletPattern {
/**
 * Stores BulletPaths and Bullets
 */
	
	/**
	 * If the time is appropriate, launch bullets in certain paths. Call this only after one wants this pattern to begin.
	 * @return 
	 */
	public abstract ArrayList<Bullet> step();
}
