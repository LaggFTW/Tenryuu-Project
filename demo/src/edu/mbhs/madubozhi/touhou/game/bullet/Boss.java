package edu.mbhs.madubozhi.touhou.game.bullet;

import java.awt.Dimension;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;

import edu.mbhs.madubozhi.touhou.game.bullet.pattern.CompoundBulletPath;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.Spellcard;

/**
 * A Boss is an EnemyBullet usually with a special and complex pattern attached to it
 * @author bowenzhi
 *
 */
public class Boss extends EnemyBullet {

	Spellcard spellcard;

	/**
	 * Instantiates this Boss with the following parameters
	 * @param velocity Velocity
	 * @param hitbox Size of hitbox
	 * @param sprite Sprite texture to render
	 * @param spriteSize Size of sprite
	 * @param hp HP
	 * @param scoreValue How many points are rewarded for defeating this Boss
	 * @param spellcard Special pattern related to this Boss
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete determines whether or not to auto-delete this bullet once it is offscreen
	 * @param spinning determines whether or not to attach an angular velocity to this bullet
	 */
	public Boss(float velocity, Dimension hitbox, Texture sprite, Dimension spriteSize, int hp, int scoreValue, 
			Spellcard spellcard, boolean lockAngle, boolean offScreenDelete, boolean spinning){
		super(velocity, hitbox, sprite, spriteSize, hp, scoreValue, lockAngle, offScreenDelete, spinning);
		this.spellcard = spellcard;
	}
	
	/**
	 * Instantiates this Boss with the following parameters
	 * @param velocity Velocity
	 * @param shape Standard BulletShape
	 * @param c Color of the Bullet
	 * @param hp HP
	 * @param scoreValue How many points are rewarded for defeating this Boss
	 * @param spellcard Special pattern related to this Boss
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete determines whether or not to auto-delete this bullet once it is offscreen
	 * @param spinning determines whether or not to attach an angular velocity to this bullet
	 */
	public Boss(float velocity, BulletShape shape, String c, int hp, int scoreValue, 
			Spellcard spellcard, boolean lockAngle, boolean offScreenDelete, boolean spinning){
		this(velocity, shape.getHitbox(), shape.getSpriteTex(c), shape.getSize(), hp, scoreValue, spellcard, lockAngle, 
				offScreenDelete, spinning);
	}
	
	public void draw(GL gl){
		gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		float ratio = ((float)hp)/((float)inithp);
		gl.glRectd(this.getLocation().x-this.spriteSize.width*ratio/2, this.getLocation().y-this.spriteSize.height/2-5, 
				this.getLocation().x+this.spriteSize.width*ratio/2, this.getLocation().y-this.spriteSize.height/2);
		if (location == null)
			return;
		float alpha = 1.0f;
		if (q < dq*60.0f){
			alpha = q/(dq*60.0f);
		}
		if (path != null && !(path instanceof CompoundBulletPath) && q > 1 - dq*60.0f){
			alpha = (1 - q)/(dq*60.0f);
		}
		gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);
		if (sprite != null){
			sprite.enable();
			sprite.bind();
		}
		gl.glPushMatrix();
		gl.glTranslatef(location.x, location.y, 0.0f);
		if (!lockAngle && path!=null){
			float angle = (float)(path.getAngle(q)/Math.PI*180+90);
			gl.glRotatef(angle, 0.0f, 0.0f, 1.0f);
		} else {
			if (spinning){
				gl.glRotatef(spinAngle, 0.0f, 0.0f, 1.0f);
			}
		}
		gl.glBegin(GL.GL_QUADS);
		if (sprite != null) {gl.glTexCoord2d(0, 0);}
		gl.glVertex2f(0 - spriteSize.width/2, 0 - spriteSize.height/2);
		if (sprite != null) {gl.glTexCoord2d(1, 0);}
		gl.glVertex2f(0 + spriteSize.width/2, 0 - spriteSize.height/2);
		if (sprite != null) {gl.glTexCoord2d(1, 1);}
		gl.glVertex2f(0 + spriteSize.width/2, 0 + spriteSize.height/2);
		if (sprite != null) {gl.glTexCoord2d(0, 1);}
		gl.glVertex2f(0 - spriteSize.width/2, 0 + spriteSize.height/2);
		gl.glEnd();
		gl.glPopMatrix();
		if (sprite != null){
			sprite.disable();
		}
	}
	
	public void drawNoBind(GL gl){
		float alpha = 1.0f;
		if (q < dq*60.0f){
			alpha = q/(dq*60.0f);
		}
		if (path != null && !(path instanceof CompoundBulletPath) && q > 1 - dq*60.0f){
			alpha = (1 - q)/(dq*60.0f);
		}
		gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);
		super.drawNoBind(gl);
	}
	
}
