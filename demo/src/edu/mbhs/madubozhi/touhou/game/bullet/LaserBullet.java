package edu.mbhs.madubozhi.touhou.game.bullet;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import com.sun.opengl.util.texture.Texture;

public class LaserBullet extends Bullet {
/** 
 * Uses an array of hitboxes, functions as a continuous trail of bullets, interpolates between the array to obtain a full
 * hitbox
 */
	public LaserBullet(float velocity, Dimension hitbox, Texture sprite, Dimension spriteSize, boolean lockAngle, boolean offScreenDelete) {
		super(velocity, hitbox, sprite, spriteSize, lockAngle, offScreenDelete);
	}
	public LaserBullet(Point2D.Float location, float velocity,
			Dimension hitbox, Texture sprite, Dimension spriteSize, boolean lockAngle, boolean offScreenDelete) {
		super(location, velocity, hitbox, sprite, spriteSize, lockAngle, offScreenDelete);
	}

}
