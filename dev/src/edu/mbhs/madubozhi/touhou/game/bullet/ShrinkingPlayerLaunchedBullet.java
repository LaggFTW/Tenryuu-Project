package edu.mbhs.madubozhi.touhou.game.bullet;

import java.awt.Dimension;

import com.sun.opengl.util.texture.Texture;

public class ShrinkingPlayerLaunchedBullet extends PlayerLaunchedBullet {
	
	private Dimension inithitboxSize;
	private Dimension initspriteSize;
	private float decayConstant;
	
	public ShrinkingPlayerLaunchedBullet(float velocity, BulletShape shape,
			String c, int damage, float decay, boolean lockAngle, boolean offScreenDelete,
			boolean spinning) {
		this(velocity, shape.getHitbox(), shape.getSpriteTex(c), shape.getSize(), damage, decay, lockAngle, offScreenDelete,
				spinning);
	}
	
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
