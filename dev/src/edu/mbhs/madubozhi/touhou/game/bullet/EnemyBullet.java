package edu.mbhs.madubozhi.touhou.game.bullet;

import java.awt.Dimension;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;

/**
 * An EnemyBullet is a bullet that can be destroyed by the player using the player's bullets.
 * @author bowenzhi
 */
public class EnemyBullet extends Bullet {
	
	protected int hp;
	protected int inithp;
	protected int scoreValue;

	/**
	 * Instantiates this bullet.
	 * @param velocity speed of bullet
	 * @param acceleration acceleration of bullet
	 * @param airFric air friction of bullet
	 * @param hitbox hitbox of bullet
	 * @param sprite sprite of bullet
	 * @param spriteSize size of bullet
	 * @param vulnerableHitbox hitbox of bullet that the bullet can be hit on
	 * @param hp durability of bullet
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 */
	public EnemyBullet(float velocity, Dimension hitbox, Texture sprite, Dimension spriteSize, 
			int hp, int scoreValue, boolean lockAngle, boolean offScreenDelete){
		this(velocity, hitbox, sprite, spriteSize, hp, scoreValue, lockAngle, offScreenDelete, false);
	}
	
	/**
	 * Instantiates this bullet as one with standardized shape.
	 * @param velocity speed of bullet
	 * @param acceleration acceleration of bullet
	 * @param airFric air friction of bullet
	 * @param shape shape of bullet
	 * @param c color of bullet
	 * @param hp durability of bullet
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 */
	public EnemyBullet(float velocity, BulletShape shape, String c, int hp, int scoreValue, boolean lockAngle, boolean offScreenDelete){
		this(velocity, shape.getHitbox(), shape.getSpriteTex(c), shape.getSize(),
				hp, scoreValue, lockAngle, offScreenDelete);
	}
	
	public EnemyBullet(float velocity, Dimension hitbox, Texture sprite, Dimension spriteSize, 
			int hp, int scoreValue, boolean lockAngle, boolean offScreenDelete, boolean spinning){
		super(velocity, hitbox, sprite, spriteSize, lockAngle, offScreenDelete, spinning);
		this.hp = hp;
		this.inithp = hp;
		this.scoreValue = scoreValue;
	}
	
	public EnemyBullet(float velocity, BulletShape shape, String c, int hp, int scoreValue, boolean lockAngle, boolean offScreenDelete, boolean spinning){
		this(velocity, shape.getHitbox(), shape.getSpriteTex(c), shape.getSize(),
				hp, scoreValue, lockAngle, offScreenDelete, spinning);
	}
	
	@Override
	public void draw(GL gl){
		gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		float ratio = ((float)hp)/((float)inithp);
		gl.glRectd(this.getLocation().x-this.spriteSize.width*ratio/2, this.getLocation().y-this.spriteSize.height/2-5, 
				this.getLocation().x+this.spriteSize.width*ratio/2, this.getLocation().y-this.spriteSize.height/2);
		super.draw(gl);
	}
	
	/**
	 * Checks of this bullet has been hit by a player's bullet, if so, hp deduction takes place.
	 * @param bullet bullet to check against
	 * @return true if a hit has been registered
	 */
	public boolean checkCollision(PlayerLaunchedBullet bullet){
		boolean b = (bullet.getDamage()<=0)?false:this.getSpriteArea().intersects(bullet.getHitboxArea().getBounds2D());
		if (b){
			takeDamage(bullet.getDamage());
		}
		return b;
	}
	
	public void takeDamage(int damage){
		hp -= damage;
	}
	
	/**
	 * @return true if the bullet has lost all of its hp
	 */
	public boolean isDestroyed(){
		return hp <= 0;
	}

	public int scoreValue() {
		return this.scoreValue;
	}
	
	@Override
	public EnemyBullet cloneBasicProperties(){
		return new EnemyBullet(this.velocity, this.hitboxSize, this.sprite, this.spriteSize, this.hp, this.scoreValue,
				this.lockAngle, this.offScreenDelete, this.spinning);
	}
	
}
