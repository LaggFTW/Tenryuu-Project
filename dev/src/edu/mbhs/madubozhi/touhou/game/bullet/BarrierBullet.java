package edu.mbhs.madubozhi.touhou.game.bullet;

import java.awt.geom.Point2D;

import javax.media.opengl.GL;

import edu.mbhs.madubozhi.touhou.game.Player;

public class BarrierBullet extends Bullet {

	private Player p;
	private int hp;
	private int inithp;
	private boolean hitBuffer;
	
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
	
	public boolean checkCollision(Bullet bullet){
		boolean b = this.getHitboxArea().intersects(bullet.getHitboxArea().getBounds2D());
		if (b){
			hp -= hitBuffer?1:0;
			hitBuffer = false;
		}
		return b;
	}
	
	public boolean isDestroyed(){
		return hp <= 0;
	}
	
}
