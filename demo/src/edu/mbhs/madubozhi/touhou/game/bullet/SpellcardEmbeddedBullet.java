package edu.mbhs.madubozhi.touhou.game.bullet;

import java.awt.Dimension;

import com.sun.opengl.util.texture.Texture;

import edu.mbhs.madubozhi.touhou.game.bullet.pattern.Spellcard;

/**
 * Enemy(Bullet) with a Spellcard attached to it
 * @author bowenzhi
 *
 */
public class SpellcardEmbeddedBullet extends EnemyBullet{

	private Spellcard spellcard;
	
	public SpellcardEmbeddedBullet(float velocity, Dimension hitbox, Texture sprite,
			Dimension spriteSize, int hp, int scoreValue, Spellcard spellcard, boolean lockAngle,
			boolean offScreenDelete) {
		super(velocity, hitbox, sprite, spriteSize, hp, scoreValue, lockAngle, offScreenDelete);
		this.spellcard = spellcard;
	}
}
