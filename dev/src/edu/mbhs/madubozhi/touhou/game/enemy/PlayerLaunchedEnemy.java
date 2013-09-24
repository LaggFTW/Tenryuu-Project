package edu.mbhs.madubozhi.touhou.game.enemy;

import java.awt.Color;

import edu.mbhs.madubozhi.touhou.game.bullet.pattern.BulletPath;

/**
 * A bullet shot by the enemy, which can deal damage to EnemyEnemys.
 * @author bowenzhi
 */
public class PlayerLaunchedEnemy extends Enemy {

	/**
	 * Constructor
	 * @param x initial position
	 * @param target target
	 * @param size size
	 * @param yVel speed
	 * @param color color
	 * @param border border of game frame
	 * @param fWidth game frame width
	 * @param damage damage/power of enemy
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete determines whether or not to delete the bullet once offscreen (for optimization purposes)
	 */
	public PlayerLaunchedEnemy(int x, int target, int size, double yVel,
			Color color, int border, int fWidth, int damage,
			boolean lockAngle, boolean offScreenDelete) {
		super(x, target, size, yVel, color, border, fWidth, lockAngle, offScreenDelete, 1);
		this.damage = damage;
	}
	
	/**
	 * Alternate constructor
	 * @param x initial x
	 * @param target target location
	 * @param size size
	 * @param yVel velocity
	 * @param color color
	 * @param border border of game frame
	 * @param fWidth game frame width
	 * @param damage damage/power of enemy
	 * @param path BulletPath the traverse
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete determines whether or not to delete the bullet once offscreen (for optimization purposes)
	 */
	public PlayerLaunchedEnemy(int x, int target, int size, double yVel,
			Color color, int border, int fWidth, int damage, BulletPath path,
			boolean lockAngle, boolean offScreenDelete) {
		super(x, target, size, yVel, color, border, fWidth, path, lockAngle,
				offScreenDelete, 1);
		this.damage = damage;
	}

	private int damage;
	
	public int getDamage(){
		return damage;
	}

}
