package edu.mbhs.madubozhi.touhou.game.bullet;

import java.awt.geom.Point2D;

import javax.media.opengl.GL;

import edu.mbhs.madubozhi.touhou.game.Player;

/**
 * Bullet that acts as a barrier around the player, buffering a few hits for the player
 * @author bowenzhi
 *
 */
public class BarrierBullet extends Bullet {

	private Player p;
	private int hp;
	private int inithp;
	private boolean hitBuffer;
	
	/**
	 * Instantiates the barrier with the following parameters
	 * @param p The player to protect
	 * @param hp How many hits this barrier can buffer
	 */
	public BarrierBullet(Player p, int hp) {
		super(new Point2D.Float(p.getX(), p.getY()), 0, BulletShape.BARRIER, "", true, false);
		this.p = p;
		this.hp = hp;
		this.inithp = hp;
		this.hitBuffer = true;
	}
	
	@Override
	public void draw(GL gl){
		float ratio = ((float)hp)/((float)inithp);
		gl.glColor4f(1.0f, 1.0f, 1.0f, ratio);
		if (sprite != null){
			sprite.enable();
			sprite.bind();
		}
		gl.glBegin(GL.GL_QUADS);
		super.drawNoBind(gl);
		gl.glEnd();
		if (sprite != null){
			sprite.disable();
		}
	}
	
	@Override
	public synchronized void step(float dt){
		this.location = new Point2D.Float(p.getX(), p.getY());
		hitBuffer = true;
	}
	
	/**
	 * Checks the collisions between this bullet and enemy bullets, updating the hit buffer count if necessary
	 * @param bullet Bullet to check collisions with
	 * @return true if a collision was detected
	 */
	public boolean checkCollision(Bullet bullet){
		boolean b = this.getHitboxArea().intersects(bullet.getHitboxArea().getBounds2D());
		if (b){
			hp -= hitBuffer?1:0;
			hitBuffer = false;
		}
		return b;
	}
	
	/**
	 * @return Whether the hit buffer for this barrier has been depleted
	 */
	public boolean isDestroyed(){
		return hp <= 0;
	}
	
}
