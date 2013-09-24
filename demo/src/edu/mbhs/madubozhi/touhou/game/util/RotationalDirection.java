package edu.mbhs.madubozhi.touhou.game.util;

import java.awt.geom.Point2D;

/**
 * Enum representing a direction of rotation
 * @author bowenzhi
 */
public enum RotationalDirection {
	COUNTERCLOCKWISE, CLOCKWISE;
	private RotationalDirection(){
	}
	
	/**
	 * Rotates a point a specified angle
	 * @param angle Angle in degrees
	 * @param origin Origin of rotation
	 * @param point Point that is rotated
	 * @return point rotated angle degrees about the origin
	 */
	public Point2D.Float rotate(float angle, Point2D.Float origin, Point2D.Float point){
		float x = point.x - origin.x;
		float y = point.y - origin.y;
		float xN, yN;
		angle = (float) ((this.equals(CLOCKWISE)?angle:-angle) * Math.PI / 180);
		xN = (float) (x * Math.cos(angle) - y * Math.sin(angle));
		yN = (float) (x * Math.sin(angle) + y * Math.cos(angle));
		return new Point2D.Float(xN + origin.x, yN + origin.y);
	}
	
	/**
	 * Rotates a point a specified angle in radians
	 * @param angle Angle in radians
	 * @param origin Origin of rotation
	 * @param point Point that is rotated
	 * @return point rotated angle degrees about the origin
	 */
	public Point2D.Float radRotate(float angle, Point2D.Float origin, Point2D.Float point){
		float x = point.x - origin.x;
		float y = point.y - origin.y;
		float xN, yN;
		angle = (float) ((this.equals(CLOCKWISE)?angle:-angle));
		xN = (float) (x * Math.cos(angle) - y * Math.sin(angle));
		yN = (float) (x * Math.sin(angle) + y * Math.cos(angle));
		return new Point2D.Float(xN + origin.x, yN + origin.y);
	}
	
	/**
	 * Calculates the angle between two points relative to the y-axis
	 * @param p0 first point
	 * @param p1 second point
	 * @return angle in radians
	 */
	public static float getAngleBetween(Point2D.Float p0, Point2D.Float p1){
		float angle = (float) Math.acos((p1.y - p0.y)/Math.sqrt(Math.pow(p1.x - p0.x, 2) + Math.pow(p1.y - p0.y, 2)));
		return (float) ((p1.x - p0.x < 0)?angle:2 * Math.PI-angle);
	}
}
