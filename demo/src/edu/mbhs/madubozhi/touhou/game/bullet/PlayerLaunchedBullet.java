package edu.mbhs.madubozhi.touhou.game.bullet;

import java.awt.Dimension;

import com.sun.opengl.util.texture.Texture;

/**
 * A Bullet shot by the player, which can deal damage to enemies.
 * @author bowenzhi
 */
public class PlayerLaunchedBullet extends Bullet {

	private int damage;
	
	/**
	 * Instantiates the bullet.
	 * @param velocity speed of bullet
	 * @param acceleration acceleration of bullet
	 * @param airFric air friction of bullet
	 * @param hitbox hitbox of bullet
	 * @param sprite sprite of bullet
	 * @param spriteSize size of bullet
	 * @param damage damage the bullet deals to enemies
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete whether not not to remove the bullet once offscreen
	 */
	public PlayerLaunchedBullet(float velocity, Dimension hitbox, Texture sprite,
			Dimension spriteSize, int damage, boolean lockAngle, boolean offScreenDelete) {
		this(velocity, hitbox, sprite, spriteSize, damage, lockAngle, offScreenDelete, false);
	}
	
	/**
	 * Instantiates the bullet, if the bullet is of standard shape.
	 * @param velocity speed of bullet
	 * @param acceleration acceleration of bullet
	 * @param airFric air friction of bullet
	 * @param shape shape of bullet
	 * @param c color of bullet
	 * @param damage damage the bullet deals to enemies
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete whether not not to remove the bullet once offscreen
	 */
	public PlayerLaunchedBullet(float velocity, BulletShape shape, String c, int damage, boolean lockAngle, 
			boolean offScreenDelete) {
		this(velocity, shape.getHitbox(), shape.getSpriteTex(c), shape.getSize(), damage, lockAngle, offScreenDelete, false);
	}
	
	/**
	 * Instantiates the bullet.
	 * @param velocity speed of bullet
	 * @param acceleration acceleration of bullet
	 * @param airFric air friction of bullet
	 * @param hitbox hitbox of bullet
	 * @param sprite sprite of bullet
	 * @param spriteSize size of bullet
	 * @param damage damage the bullet deals to enemies
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete whether not not to remove the bullet once offscreen
	 * @param spinning whether or not to attach an angular velocity to this bullet
	 */
	public PlayerLaunchedBullet(float velocity, Dimension hitbox, Texture sprite,
			Dimension spriteSize, int damage, boolean lockAngle, boolean offScreenDelete, boolean spinning) {
		super(velocity, hitbox, sprite, spriteSize, lockAngle, offScreenDelete, spinning);
		this.damage = damage;
	}
	
	/**
	 * Instantiates the bullet, if the bullet is of standard shape.
	 * @param velocity speed of bullet
	 * @param acceleration acceleration of bullet
	 * @param airFric air friction of bullet
	 * @param shape shape of bullet
	 * @param c color of bullet
	 * @param damage damage the bullet deals to enemies
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete whether not not to remove the bullet once offscreen
	 * @param spinning whether or not to attach an angular velocity to this bullet
	 */
	public PlayerLaunchedBullet(float velocity, BulletShape shape, String c, int damage, boolean lockAngle, 
			boolean offScreenDelete, boolean spinning) {
		this(velocity, shape.getHitbox(), shape.getSpriteTex(c), shape.getSize(), damage, lockAngle, offScreenDelete, spinning);
	}
	
	public int getDamage(){
		return damage;
	}
	
	@Override
	public PlayerLaunchedBullet cloneBasicProperties(){
		return new PlayerLaunchedBullet(this.velocity, this.hitboxSize, this.sprite, this.spriteSize, this.damage,
				this.lockAngle, this.offScreenDelete, this.spinning);
	}

}
