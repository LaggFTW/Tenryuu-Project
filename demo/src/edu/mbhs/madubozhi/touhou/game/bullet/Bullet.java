package edu.mbhs.madubozhi.touhou.game.bullet;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;

import edu.mbhs.madubozhi.touhou.game.bullet.pattern.BulletPath;
import edu.mbhs.madubozhi.touhou.ui.FullGame;

/**
 * A Bullet is a basic element present in the game. They either are employed by enemies to attack the player, or by
 * the player to attack enemies.
 * @author bowenzhi
 */
public class Bullet {
/**
 * Params:
 * Bullet Path
 * Velocity
 * Acceleration
 * Friction
 * Hitbox Size
 * Sprite(s)
 * Sprite Size
 * Location
 * Parameter
 */
	protected float velocity;
	protected Point2D.Float location;
	protected Dimension hitboxSize;
	protected Dimension spriteSize;
	protected BulletShape shape;
	protected Texture sprite;
	protected BulletPath path;
	protected float q, dq;//Position-determining parameter, if needed
	protected boolean finished;
	protected boolean lockAngle;//Whether or not to alter the orientation of the bullet based on the path
	protected boolean offScreenDelete;
	protected boolean spinning;
	protected float spinAngle;
	private boolean grazed;
	
	/**
	 * Generalized constructor
	 * @param location Starting location
	 * @param velocity Starting velocity
	 * @param acceleration Acceleration
	 * @param airFric Air friction
	 * @param hitbox Hitbox dimenisons and size
	 * @param sprite Sprite (if null, just draws an oval around the size
	 * @param spriteSize Size of the sprite
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 */
	public Bullet(Point2D.Float location, float velocity, Dimension hitbox, 
			Texture sprite, Dimension spriteSize, boolean lockAngle, boolean offScreenDelete){
		this(velocity, hitbox, sprite, spriteSize, lockAngle, offScreenDelete, false);
		this.location = location;
	}

	/**
	 * Constructor for Bullets standardized according to BulletShape
	 * @param location Starting location
	 * @param velocity Starting velocity
	 * @param acceleration Acceleration
	 * @param airFric Air friction
	 * @param shape Shape of the bullet
	 * @param c Color of the bullet
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 */
	public Bullet(Point2D.Float location, float velocity, BulletShape shape, String c, boolean lockAngle, boolean offScreenDelete){
		this(location, velocity, shape.getHitbox(), shape.getSpriteTex(c), shape.getSize(), lockAngle, offScreenDelete);
	}
	
	/**
	 * Constructor for use with Bullet Patterns
	 * @param velocity Starting velocity
	 * @param acceleration Starting acceleration
	 * @param airFric Air friction
	 * @param hitbox Hitbox dimensions and size
	 * @param sprite Sprite (if null, just draws an oval around the size
	 * @param spriteSize Size of the sprite
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 */
	public Bullet(float velocity, Dimension hitbox, Texture sprite, 
			Dimension spriteSize, boolean lockAngle, boolean offScreenDelete){
		this(velocity, hitbox, sprite, spriteSize, lockAngle, offScreenDelete, false);
	}


	/**
	 * Constructor for use with Bullet Patterns that is a standardized bullet specified by BulletShape
	 * @param velocity Starting velocity
	 * @param acceleration Acceleration
	 * @param airFric Air friction
	 * @param shape Shape of the bullet
	 * @param c Color of the bullet
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 */
	public Bullet(float velocity, BulletShape shape, String c, boolean lockAngle, boolean offScreenDelete){
		this(velocity, shape.getHitbox(), shape.getSpriteTex(c), shape.getSize(), lockAngle, offScreenDelete);
	}
	
	public Bullet(float velocity, Dimension hitbox, Texture sprite, 
			Dimension spriteSize, boolean lockAngle, boolean offScreenDelete, boolean spinning){
		this.velocity = velocity;
		this.hitboxSize = hitbox;
		this.spriteSize = spriteSize;
		this.sprite = sprite;
		this.q = 0;
		this.dq = velocity / 10000.0f;
		this.finished = false;
		this.lockAngle = lockAngle;
		this.offScreenDelete = offScreenDelete;
		this.spinning = spinning;
		this.grazed = false;
	}
	
	public Bullet(float velocity, BulletShape shape, String c, boolean lockAngle, boolean offScreenDelete, boolean spinning){
		this(velocity, shape.getHitbox(), shape.getSpriteTex(c), shape.getSize(), lockAngle, offScreenDelete, spinning);
	}
	
	/**
	 * Moves this Bullet over a time step
	 * @param dt Time step (milliseconds)
	 */
	public void step(float dt){
		if (dt <= 0){
			return;
		}
		if (path != null){
			Point2D.Float temp = path.step(q);
			if (temp == null){
				finished = true;
				return;
			}
			location = temp;
		}
		calcPhysics(dt);
		q += dq * (dt/FullGame.MILLIS_PER_FRAME);
		if (spinning){
			spinAngle += 1.0f * (dt/FullGame.MILLIS_PER_FRAME);
		}
	}
	
	/**
	 * Draws this Bullet
	 * @param gl GL Graphics object to draw with
	 */
	public void draw(GL gl){
		if (location == null)
			return;
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
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
	
	/**
	 * Draws this Bullet without binding the texture (for efficiency purposes, it is advantageous to bind the texture
	 * once and draw all bullets of that texture
	 * @param gl GL Graphics object to draw with
	 */
	public void drawNoBind(GL gl){
		if (location == null)
			return;
		gl.glTexCoord2f(0, 0);
		gl.glVertex2f(location.x - spriteSize.width/2, location.y - spriteSize.height/2);
		gl.glTexCoord2f(1, 0);
		gl.glVertex2f(location.x + spriteSize.width/2, location.y - spriteSize.height/2);
		gl.glTexCoord2f(1, 1);
		gl.glVertex2f(location.x + spriteSize.width/2, location.y + spriteSize.height/2);
		gl.glTexCoord2f(0, 1);
		gl.glVertex2f(location.x - spriteSize.width/2, location.y + spriteSize.height/2);
	}
	
	/**
	 * Updates velocity and acceleration, then determines dq based on said calcs
	 * @param dt
	 */
	private void calcPhysics(float dt){
		
	}
	
	/**
	 * Constrains this bullet to follow a path
	 * @param path BulletPath describing the path
	 */
	public void addPath(BulletPath path){
		this.path = path;
		this.q = 0;
		this.location = path.step(q);
	}
	
	/**
	 * @return a copy of a Bullet that is exactly the same as the current one (bar position and path)
	 */
	public Bullet cloneBasicProperties(){
		return new Bullet(this.velocity, this.hitboxSize, this.sprite,
				this.spriteSize, this.lockAngle, this.offScreenDelete, this.spinning);
	}
	
	/**
	 * @return true if the bullet is considered to have traveled its full path and will fade away
	 */
	public boolean isFinished(){
		return finished;
	}
	
	/**
	 * @return location of the bullet
	 */
	public Point2D.Float getLocation(){
		return this.location;
	}

	public Shape getHitboxArea() {
		boolean b = location==null;
		return new Area(new Rectangle2D.Float(b?-this.hitboxSize.width*2:this.location.x - this.hitboxSize.width/2, 
				b?-this.hitboxSize.height*2:this.location.y - this.hitboxSize.height/2, this.hitboxSize.width, this.hitboxSize.height));
	}
	
	public Shape getSpriteArea(){
		boolean b = location==null;
		return new Area(new Rectangle2D.Float(b?-this.spriteSize.width*2:this.location.x - this.spriteSize.width/2, 
				b?-this.spriteSize.height*2:this.location.y - this.spriteSize.height/2, this.spriteSize.width, this.spriteSize.height));
	}
	
	public boolean checkOffscreenDelete(){
		return this.q > 0.1 && this.offScreenDelete;
	}
	
	public boolean angleLocked(){
		return this.lockAngle;
	}
	
	public boolean isSpinning(){
		return this.spinning;
	}
	
	public Texture getSprite(){
		return this.sprite;
	}
	
	public boolean isGrazed(){
		return this.grazed;
	}
	
	public void graze(){
		this.grazed = true;
	}
	
}
