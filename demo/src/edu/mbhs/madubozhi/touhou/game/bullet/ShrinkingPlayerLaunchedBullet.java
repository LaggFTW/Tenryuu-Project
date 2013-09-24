package edu.mbhs.madubozhi.touhou.game.bullet;

import java.awt.Dimension;

import com.sun.opengl.util.texture.Texture;

/**
 * A bullet that shrinks as it moves
 * @author bowenzhi
 *
 */
public class ShrinkingPlayerLaunchedBullet extends PlayerLaunchedBullet {
	
	private Dimension inithitboxSize;
	private Dimension initspriteSize;
	private float decayConstant;
	
	/**
	 * Instantiates this bullet
	 * @param velocity Speed of the bullet
	 * @param shape BulletShape to use for the Bullet
	 * @param c Color of the bullet
	 * @param damage Damage the bullet deals
	 * @param decay How much this bullet decays by every timestep
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete determines whether or not to delete this bullet once offscreen
	 * @param spinning whether to attach an angular velocity to this bullet
	 */
	public ShrinkingPlayerLaunchedBullet(float velocity, BulletShape shape,
			String c, int damage, float decay, boolean lockAngle, boolean offScreenDelete,
			boolean spinning) {
		this(velocity, shape.getHitbox(), shape.getSpriteTex(c), shape.getSize(), damage, decay, lockAngle, offScreenDelete,
				spinning);
	}
	
	/**
	 * Instantiates this bullet
	 * @param velocity Speed of the bullet
	 * @param hitbox hitbox size
	 * @param sprite Sprite Texture to use
	 * @param spriteSize Sprite size
	 * @param damage Damage the bullet deals
	 * @param decay How much this bullet decays by every timestep
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete determines whether or not to delete this bullet once offscreen
	 * @param spinning whether to attach an angular velocity to this bullet
	 */
	public ShrinkingPlayerLaunchedBullet(float velocity, Dimension hitbox,
			Texture sprite, Dimension spriteSize, int damage, float decay,
			boolean lockAngle, boolean offScreenDelete, boolean spinning) {
		super(velocity, hitbox, sprite, spriteSize, damage, lockAngle, offScreenDelete,
				spinning);
		this.decayConstant = decay;
		this.inithitboxSize = new Dimension(hitbox);
		this.initspriteSize =  new Dimension(spriteSize);
	}
	
	@Override
	public void step(float dt){
		super.step(dt);
		spriteSize.setSize(new Dimension((int)(spriteSize.width * decayConstant), (int)(spriteSize.height * decayConstant)));
		hitboxSize.setSize(new Dimension((int)(hitboxSize.width * decayConstant), (int)(hitboxSize.height * decayConstant)));
		if (hitboxSize.width <= 0 || hitboxSize.height <= 0 || spriteSize.width <= 0 || spriteSize.height <= 0){
			finished = true;
		}
	}
	
	@Override
	public ShrinkingPlayerLaunchedBullet cloneBasicProperties(){
		return new ShrinkingPlayerLaunchedBullet(this.velocity, this.inithitboxSize, this.sprite, this.initspriteSize, 
				this.getDamage(), this.decayConstant, this.lockAngle, this.offScreenDelete, this.spinning);
	}
	
}
