package edu.mbhs.madubozhi.touhou.game.bullet.pattern;

import java.awt.geom.Point2D;

import edu.mbhs.madubozhi.touhou.game.util.PathTraversal;
import edu.mbhs.madubozhi.touhou.game.util.RotationalDirection;

/**
 * Bullet follows a line (lame, I know)
 * @author bowenzhi
 */
public class LinearBulletPath extends BulletPath {
/**
 * Path follows a line (lame, I know)
 */
	protected Point2D.Float p0, p1;
	protected PathTraversal traversal;
	/**
	 * Instantiates this path. Defaults to a constant velocity traversal.
	 * @param p0 launch point
	 * @param p1 destination
	 */
	public LinearBulletPath(Point2D.Float p0, Point2D.Float p1){
		this(p0, p1, PathTraversal.LINEAR);
	}
	/**
	 * Instantiates this path.
	 * @param p0 launch point
	 * @param p1 destination
	 * @param traversal how the bullet travels through the path
	 */
	public LinearBulletPath(Point2D.Float p0, Point2D.Float p1, PathTraversal traversal){
		this.p0 = p0;
		this.p1 = p1;
		this.traversal = traversal;
	}
	public Point2D.Float getLocation(){
		return p0;
	}
	public Point2D.Float step(float q){
		if (q >= 0 && q <= 1){
			float qEff = traversal.getAdjustedParam(q);
			return new Point2D.Float((float)((1-qEff) * p0.x + qEff * p1.x), 
					(float)((1-qEff) * p0.y + qEff * p1.y));
		}
		return null;
	}
	public LinearBulletPath clone(){
		return new LinearBulletPath(new Point2D.Float(p0.x, p0.y), new Point2D.Float(p1.x, p1.y), this.traversal);
	}
	public synchronized void rotate(Point2D.Float p, float angle){
		p0.setLocation(RotationalDirection.CLOCKWISE.rotate(angle, p, p0));
		p1.setLocation(RotationalDirection.CLOCKWISE.rotate(angle, p, p1));
	}
	public synchronized void radRotate(Point2D.Float p, float angle){
		p0.setLocation(RotationalDirection.CLOCKWISE.radRotate(angle, p, p0));
		p1.setLocation(RotationalDirection.CLOCKWISE.radRotate(angle, p, p1));
	}
	public synchronized void translate(float x, float y){
		p0.setLocation(p0.x + x, p0.y + y);
		p1.setLocation(p1.x + x, p1.y + y);
	}
	public float getAngle(float q){
		float iComp = p1.x - p0.x;
		float jComp = p1.y - p0.y;
		float magnitude = (float) Math.sqrt(Math.pow(iComp, 2) + Math.pow(jComp, 2));
		float angle = (float) Math.acos(jComp / magnitude);
		return (float) ((iComp < 0)?angle:2 * Math.PI - angle);
	}
}
