package edu.mbhs.madubozhi.touhou.game.util;

import java.awt.geom.Point2D;

/**
 * Enum to determine the type of path that a bullet will take.
 * @author bowenzhi
 */
public enum PathType {
	LINE(0), BEZIER_CURVE(1), SPIRAL(2);
	private final int pathID;
	/**
	 * Instantiates a PathType with an indexed reference.
	 * @param pathID indexed reference
	 */
	private PathType(int pathID){
		this.pathID = pathID;
	}
	/**
	 * Calculates the point that a bullet will take on the path
	 * @param q Parameter value
	 * @param points Set of control points (number of points varies based on type)
	 * @param r0 Initial radius of a spiral type
	 * @param theta0Rad Initial angle of a spiral type
	 * @param dRadius Radius step of a spiral type
	 * @param dThetaRad Angle step of a spiral type
	 * @return Point that the bullet will exist at
	 */
	public Point2D.Float calcPoint(float q, Point2D.Float[] points, float r0, float theta0Rad, float dRadius,
			float dThetaRad){
		Point2D.Float p0 = null, p1 = null, p2 = null;
		if (points.length >= 1)
			p0 = points[0];
		if (points.length >= 2)
			p1 = points[1];
		if (points.length >= 3)
			p2 = points[2];
		if (q >= 0 && q <= 1)
			switch(this.pathID){
			case 0: 
				return new Point2D.Float((float)(1-q) * p0.x + (float)q * p1.x, (float)(1-q) * p0.y + (float)q * p1.y);
			case 1: 
				return new Point2D.Float((float)((1-q) * (1-q) * p0.x + 2 * (1-q) * q * p1.x + q * q * p2.x), 
						(float)((1-q) * (1-q) * p0.y + 2 * (1-q) * q * p1.y + q * q * p2.y));
			case 2: 
				return new Point2D.Float(p0.x + (float)((r0 + q * dRadius)*(Math.cos(theta0Rad + q * dThetaRad))), 
						p0.y + (float)((r0 + q * dRadius)*(Math.sin(theta0Rad + q * dThetaRad))));
			default: return null;
			}
		return null;
	}
}
