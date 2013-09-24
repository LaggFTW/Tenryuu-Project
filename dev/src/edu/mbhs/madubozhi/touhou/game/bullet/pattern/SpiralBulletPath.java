package edu.mbhs.madubozhi.touhou.game.bullet.pattern;

import java.awt.geom.Point2D;

import edu.mbhs.madubozhi.touhou.game.util.PathTraversal;

/**
 * A BulletPath in the shape of an expanding (or contracting) arc / spiral
 * @author bowenzhi
 */
public class SpiralBulletPath extends BulletPath {
/**
 * Path in the shape of an expanding arc / spiral
 */
	protected float x, y;
	protected float r0, dRadius;
	protected float theta0Rad, dThetaRad;
	protected PathTraversal traversal;
	/**
	 * Instantiates this path. Defaults to a constant velocity traversal.
	 * @param x x coordinate of rotation point
	 * @param y y coordinate of rotation point
	 * @param r0 initial radius
	 * @param rF final radius
	 * @param theta0 initial angle
	 * @param thetaF final angle
	 * @param degrees whether or not theta0 and thetaF are in degrees
	 */
	public SpiralBulletPath(float x, float y, float r0, float rF, float theta0, float thetaF, boolean degrees){
		this(x, y, r0, rF, theta0, thetaF, degrees, PathTraversal.LINEAR);
	}
	/**
	 * Instantiates this path. Defaults to a constant velocity traversal.
	 * @param x x coordinate of rotation point
	 * @param y y coordinate of rotation point
	 * @param r0 initial radius
	 * @param rF final radius
	 * @param theta0 initial angle
	 * @param thetaF final angle
	 * @param degrees whether or not theta0 and thetaF are in degrees
	 * @param traversal how the bullet travels through the path
	 */
	public SpiralBulletPath(float x, float y, float r0, float rF, float theta0, float thetaF, boolean degrees, PathTraversal traversal){
		this.x = x;
		this.y = y;
		this.r0 = r0;
		this.dRadius = rF - r0;
		this.theta0Rad = degrees? (float) (theta0 / 180 * Math.PI): theta0;
		this.dThetaRad = (degrees? (float) (thetaF / 180 * Math.PI): thetaF) - this.theta0Rad;
		this.traversal = traversal;
	}
	public Point2D.Float getLocation(){
		return new Point2D.Float(x, y);
	}
	public Point2D.Float step(float q){
		if (q >= 0 && q <= 1){
			float qEff = traversal.getAdjustedParam(q);
			return new Point2D.Float(x + (float)((r0 + qEff * dRadius)*(Math.cos(theta0Rad + qEff * dThetaRad))), 
					y + (float)((r0 + qEff * dRadius)*(Math.sin(theta0Rad + qEff * dThetaRad))));
		}
		return null;
	}
	public SpiralBulletPath clone(){
		return new SpiralBulletPath(x, y, r0, r0 + dRadius, theta0Rad, theta0Rad + dThetaRad, false, this.traversal);
	}
	public synchronized void rotate(Point2D.Float p, float angle){
		theta0Rad += angle / 180 * Math.PI;
	}
	public synchronized void radRotate(Point2D.Float p, float angle){
		theta0Rad += angle;
	}
	public synchronized void translate(float x, float y){
		this.x += x;
		this.y += y;
	}
	public float getAngle(float q){
		float iComp = (float) (dRadius * (Math.cos(theta0Rad + q * dThetaRad))
				- (r0 + q * dRadius) * (Math.sin(theta0Rad + q * dThetaRad)) * dThetaRad);
		float jComp = (float) (dRadius * (Math.sin(theta0Rad + q * dThetaRad))
				+ (r0 + q * dRadius) * (Math.cos(theta0Rad + q * dThetaRad)) * dThetaRad);
		float magnitude = (float) Math.sqrt(Math.pow(iComp, 2) + Math.pow(jComp, 2));
		float angle = (float) Math.acos(jComp / magnitude);
		float angleNormalizedPositive = (float)((iComp < 0)?angle + Math.PI:Math.PI - angle);
		float angleNormalizedNegative = (float)((iComp < 0)?angle:2*Math.PI - angle);
		return dRadius>0?angleNormalizedPositive:angleNormalizedNegative;
	}
}
