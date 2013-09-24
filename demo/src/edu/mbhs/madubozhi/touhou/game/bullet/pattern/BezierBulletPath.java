package edu.mbhs.madubozhi.touhou.game.bullet.pattern;

import java.awt.geom.Point2D;

import edu.mbhs.madubozhi.touhou.game.util.PathTraversal;
import edu.mbhs.madubozhi.touhou.game.util.RotationalDirection;

/**
 * BulletPath that follows a quadratic Bezier curve
 * @author bowenzhi
 */
public class BezierBulletPath extends BulletPath {
/**
 * Path follows a 2nd order bezier curve
 */
	protected Point2D.Float p0, p1, p2;
	protected PathTraversal traversal;
	/**
	 * Instantiates this bullet path. Defaults to a constant velocity traversal.
	 * @param p0 initial launch point
	 * @param p1 weight point
	 * @param p2 destination point
	 */
	public BezierBulletPath(Point2D.Float p0, Point2D.Float p1, Point2D.Float p2){
		this(p0, p1, p2, PathTraversal.LINEAR);
	}
	/**
	 * Instantiates this bullet path.
	 * @param p0 initial launch point
	 * @param p1 weight point
	 * @param p2 destination point
	 * @param traversal how the bullet travels through the path
	 */
	public BezierBulletPath(Point2D.Float p0, Point2D.Float p1, Point2D.Float p2, PathTraversal traversal){
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		this.traversal = traversal;
	}
	public Point2D.Float getLocation(){
		return p0;
	}
	public Point2D.Float step(float q){
		if (q >= 0 && q <= 1){
			float qEff = traversal.getAdjustedParam(q);
			return new Point2D.Float((float)((1-qEff) * (1-qEff) * p0.x + 2 * (1-qEff) * qEff * p1.x + qEff * qEff * p2.x), 
					(float)((1-qEff) * (1-qEff) * p0.y + 2 * (1-qEff) * qEff * p1.y + qEff * qEff * p2.y));
		}
		return null;
	}
	public BezierBulletPath clone(){
		return new BezierBulletPath(new Point2D.Float(p0.x, p0.y), new Point2D.Float(p1.x, p1.y), 
				new Point2D.Float(p2.x, p2.y), this.traversal);
	}
	public synchronized void rotate(Point2D.Float p, float angle){
		p0.setLocation(RotationalDirection.CLOCKWISE.rotate(angle, p, p0));
		p1.setLocation(RotationalDirection.CLOCKWISE.rotate(angle, p, p1));
		p2.setLocation(RotationalDirection.CLOCKWISE.rotate(angle, p, p2));
	}
	public synchronized void radRotate(Point2D.Float p, float angle){
		p0.setLocation(RotationalDirection.CLOCKWISE.radRotate(angle, p, p0));
		p1.setLocation(RotationalDirection.CLOCKWISE.radRotate(angle, p, p1));
		p2.setLocation(RotationalDirection.CLOCKWISE.radRotate(angle, p, p2));
	}
	public synchronized void translate(float x, float y){
		p0.setLocation(p0.x + x, p0.y + y);
		p1.setLocation(p1.x + x, p1.y + y);
		p2.setLocation(p2.x + x, p2.y + y);
	}
	public float getAngle(float q){
		float iComp = 2 * (1-q) * (p1.x - p0.x) + 2 * q * (p2.x - p1.x);
		float jComp = 2 * (1-q) * (p1.y - p0.y) + 2 * q * (p2.y - p1.y);
		float magnitude = (float) Math.sqrt(Math.pow(iComp, 2) + Math.pow(jComp, 2));
		float angle = (float) Math.acos(jComp / magnitude);
		return (float) ((iComp < 0)?angle:2 * Math.PI - angle);
	}
}
