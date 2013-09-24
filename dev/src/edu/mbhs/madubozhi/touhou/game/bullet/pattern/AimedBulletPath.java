package edu.mbhs.madubozhi.touhou.game.bullet.pattern;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

import edu.mbhs.madubozhi.touhou.game.Player;
import edu.mbhs.madubozhi.touhou.game.util.PathTraversal;
import edu.mbhs.madubozhi.touhou.game.util.RotationalDirection;

/**
 * BulletPath that shoots towards the player, wherever that may be.
 * @author bowenzhi
 */
public class AimedBulletPath extends LinearBulletPath {
/**
 * Linear shot towards the player
 */
	protected float distance;
	protected Player p;
	/**
	 * Instantiates this bullet path.
	 * @param p0 initial launch point
	 * @param distance range of shot
	 * @param p target player
	 * @param traversal how the bullet travels through the path
	 */
	public AimedBulletPath(Point2D.Float p0, float distance, Player p, PathTraversal traversal) {
		super(p0, null, traversal);
		this.p = p;
		this.distance = distance;
		calcP1();
	}
	/**
	 * Instantiates this bullet path. Defaults to the constant velocity traversal.
	 * @param p0 initial launch point
	 * @param distance range of shot
	 * @param p target player
	 */
	public AimedBulletPath(Point2D.Float p0, float distance, Player p){
		this(p0, distance, p, PathTraversal.LINEAR);
	}
	/**
	 * Calculates the destination point of the bullet based on the player's location and the range of the shot
	 */
	private void calcP1(){
		float theta = RotationalDirection.getAngleBetween(p0, new Point2D.Float(p.getX(), p.getY()));
		p1 = new Point2D.Float((float)(p0.x - distance * Math.sin(theta)), (float)(p0.y + distance * Math.cos(theta)));
	}
	public AimedBulletPath clone(){
		return new AimedBulletPath(new Float(p0.x, p0.y), distance, p, traversal);
	}
	public void translate(float x, float y){
		p0 = new Point2D.Float(p0.x + x, p0.y + y);
		calcP1();
	}
}
