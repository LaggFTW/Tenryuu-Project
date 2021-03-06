package edu.mbhs.madubozhi.touhou.game.bullet;

import java.awt.Dimension;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;

import edu.mbhs.madubozhi.touhou.game.Player;

/**
 * Bullet that fades away from view when the player is focused
 * @author bowenzhi
 *
 */
public class DarknessBullet extends Bullet {
	
	private Player p;
	private float alpha;

	/**
	 * Instantiates this bullet
	 * @param velocity speed of bullet
	 * @param hitbox hitbox of bullet
	 * @param sprite sprite of bullet
	 * @param spriteSize size of bullet
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete determines whether or not to delete this bullet once offscreen
	 * @param spinning whether to attach an angular velocity to this bullet
	 * @param p Player reference
	 */
	public DarknessBullet(float velocity, Dimension hitbox, Texture sprite,
			Dimension spriteSize, boolean lockAngle, boolean offScreenDelete, boolean spinning, Player p) {
		this(velocity, hitbox, sprite, spriteSize, lockAngle, offScreenDelete, spinning, p, 1.0f);
	}
	
	/**
	 * Instantiates this bullet
	 * @param velocity speed of bullet
	 * @param shape BulletShape to use
	 * @param c Color of this bullet
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete determines whether or not to delete this bullet once offscreen
	 * @param spinning whether to attach an angular velocity to this bullet
	 * @param p Player reference
	 */
	public DarknessBullet(float velocity, BulletShape shape, String c,
			boolean lockAngle, boolean offScreenDelete, boolean spinning, Player p) {
		this(velocity, shape.getHitbox(), shape.getSpriteTex(c), shape.getSize(), lockAngle, offScreenDelete, spinning, p);
	}
	
	/**
	 * Instantiates this bullet
	 * @param velocity speed of bullet
	 * @param hitbox hitbox of bullet
	 * @param sprite sprite of bullet
	 * @param spriteSize size of bullet
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete determines whether or not to delete this bullet once offscreen
	 * @param p Player reference
	 */
	public DarknessBullet(float velocity, Dimension hitbox, Texture sprite,
			Dimension spriteSize, boolean lockAngle, boolean offScreenDelete, Player p) {
		this(velocity, hitbox, sprite, spriteSize, lockAngle, offScreenDelete, false, p);
	}
	
	/**
	 * Instantiates this bullet
	 * @param velocity speed of bullet
	 * @param shape BulletShape to use
	 * @param c Color of this bullet
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete determines whether or not to delete this bullet once offscreen
	 * @param p Player reference
	 */
	public DarknessBullet(float velocity, BulletShape shape, String c,
			boolean lockAngle, boolean offScreenDelete, Player p) {
		this(velocity, shape.getHitbox(), shape.getSpriteTex(c), shape.getSize(), lockAngle, offScreenDelete, false, p);
	}
	
	/**
	 * Instantiates this bullet, for use in cloning
	 * @param velocity speed of bullet
	 * @param hitbox hitbox of bullet
	 * @param sprite sprite of bullet
	 * @param spriteSize size of bullet
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param offScreenDelete determines whether or not to delete this bullet once offscreen
	 * @param spinning whether to attach an angular velocity to this bullet
	 * @param p Player reference
	 * @param alpha initial alpha value to use
	 */
	private DarknessBullet(float velocity, Dimension hitbox, Texture sprite,
			Dimension spriteSize, boolean lockAngle, boolean offScreenDelete, boolean spinning, Player p, float alpha) {
		super(velocity, hitbox, sprite, spriteSize, lockAngle, offScreenDelete, spinning);
		this.p = p;
		this.alpha = alpha;
	}
	
	@Override
	public void step(float dt){
		super.step(dt);
		if (p.getFocus()){
			alpha -= alpha<0.2f?alpha:0.2f;
		} else {
			alpha += alpha>0.8f?1-alpha:0.2f;
		}
	}
	
	@Override
	public void draw(GL gl){
		if (location == null)
			return;
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
	
	@Override
	public void drawNoBind(GL gl){
		gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);
		super.drawNoBind(gl);
	}
	
	@Override
	public DarknessBullet cloneBasicProperties(){
		return new DarknessBullet(this.velocity, this.hitboxSize, this.sprite, this.spriteSize, this.lockAngle, 
				this.offScreenDelete, this.spinning, this.p, this.alpha);
	}
	
}
