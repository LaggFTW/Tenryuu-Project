package edu.mbhs.madubozhi.touhou.game.bullet;

import java.awt.Dimension;

import com.sun.opengl.util.texture.Texture;

import edu.mbhs.madubozhi.touhou.game.bullet.pattern.Spellcard;

public class Boss extends EnemyBullet {

	Spellcard spellcard;

	public Boss(float velocity, Dimension hitbox, Texture sprite, Dimension spriteSize, int hp, int scoreValue, 
			Spellcard spellcard, boolean lockAngle, boolean offScreenDelete, boolean spinning){
		super(velocity, hitbox, sprite, spriteSize, hp, scoreValue, lockAngle, offScreenDelete, spinning);
		this.spellcard = spellcard;
	}
	
	public Boss(float velocity, BulletShape shape, String c, int hp, int scoreValue, 
			Spellcard spellcard, boolean lockAngle, boolean offScreenDelete, boolean spinning){
		this(velocity, shape.getHitbox(), shape.getSpriteTex(c), shape.getSize(), hp, scoreValue, spellcard, lockAngle, 
				offScreenDelete, spinning);
	}
	
}
