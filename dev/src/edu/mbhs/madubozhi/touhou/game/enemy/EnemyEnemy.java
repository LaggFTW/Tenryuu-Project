package edu.mbhs.madubozhi.touhou.game.enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.mbhs.madubozhi.touhou.game.bullet.pattern.BulletPath;

/**
 * An enemy that can be hit by other enemies. Pardon the name.
 * @author bowenzhi
 */
public class EnemyEnemy extends Enemy {

	protected int hp;
	protected int initHP;
	private boolean spellcardEnemy;//Whether or not said embedded enemy is associated with a spellcard
	
	/**
	 * Instantiates this enemy.
	 * @param x initial x
	 * @param target target location
	 * @param size size
	 * @param yVel velocity
	 * @param color color
	 * @param border border of game frame
	 * @param fWidth game frame width
	 * @param hp durability of bullet
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete determines whether or not to delete the bullet once offscreen (for optimization purposes)
	 */
	public EnemyEnemy(int x, int target, int size, double yVel, Color color,
			int border, int fWidth, int hp, boolean lockAngle, boolean offScreenDelete) {
		super(x, target, size, yVel, color, border, fWidth, lockAngle, offScreenDelete);
		this.hp = hp;
		this.initHP=hp;
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
	 * @param hp durability of bullet
	 * @param path BulletPath the traverse
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete determines whether or not to delete the bullet once offscreen (for optimization purposes)
	 */
	public EnemyEnemy(int x, int target, int size, double yVel, Color color,
			int border, int fWidth, int hp, BulletPath path, boolean lockAngle,
			boolean offScreenDelete) {
		super(x, target, size, yVel, color, border, fWidth, path, lockAngle,
				offScreenDelete);
		this.hp = hp;
		this.initHP=hp;
	}
	
	/**
	 * Checks of this enemy has been hit by a player's enemy, if so, hp deduction takes place.
	 * @param enemy enemy to check collision against
	 * @return true if a collision was detected
	 */
	public boolean checkCollision(PlayerLaunchedEnemy enemy){
		float sqrt2 = (float) Math.sqrt(2);
		boolean b = enemy.getArea().intersects(new Rectangle2D.Float(this.x - this.size/sqrt2, this.y - this.size/sqrt2, this.size * sqrt2, this.size * sqrt2));
		if (b){
			hp -= enemy.getDamage();
		}
		return b;
	}
	
	/**
	 * @return whether the enemy's hp has been depleted
	 */
	public boolean isDestroyed(){
		return hp <= 0;
	}
	
	/**
	 * Used for spellcard management
	 * @param status the new spellcard status to set
	 */
	public void setSpellcardStatus(boolean status){
		this.spellcardEnemy = status;
	}
	
	/**
	 * @return whether or not this enemy is controlling a spellcard
	 */
	public boolean getSpellcardStatus(){
		return this.spellcardEnemy;
	}
	
	/**
	 * Draws this enemy
	 * @param g Graphics object of the Component that this will be drawn on
	 */
	public void draw(Graphics g){
		if (sprite != null){
			if (overlay){
				g.drawImage(sprite, (int)(this.x-this.size)-r, (int)(this.y-this.size)-r, 
						(int)(this.x+this.size)+r, (int)(this.y+this.size)+r, 0, 0, sprite.getWidth(), sprite.getHeight(),
						null);
				g.drawImage(innerSprite, (int)(this.x-this.size), (int)this.y-this.size, 
						(int)(this.x+this.size), (int)this.y+this.size, 0, 0, innerSprite.getWidth(), innerSprite.getHeight(),
						null);
			} else
				g.drawImage(sprite, (int)(this.x-this.size), (int)(this.y-this.size), 
						(int)(this.x+this.size), (int)(this.y+this.size), 0, 0, sprite.getWidth(), sprite.getHeight(),
						null);
		} else {
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(this.color);
			g2d.fill(new Ellipse2D.Float(this.x-this.size-r, this.y-this.size-r, 2*(this.size+r), 2*(this.size+r)));
			g2d.setColor(Color.WHITE);
			g2d.fill(new Ellipse2D.Float(this.x-this.size, this.y-this.size, this.size*2, this.size*2));
		}
		float ratio = (float)hp/(float)initHP;
		int length = 2*size;
		g.setColor(Color.GREEN);	g.fillRect((int)(x-size), (int)(y-size-5), (int)(length*ratio), 3);
		g.setColor(Color.RED);	g.fillRect((int)(x-size)+(int)(length*ratio), (int)(y-size-5), length-(int)(length*ratio), 3);
	}
}
