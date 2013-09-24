package edu.mbhs.madubozhi.touhou.game.bullet.pattern;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

public class BulletPath {
/**
 * Stores the path the bullet travels in (generalized as an array of shapes)
 */
	protected ArrayList<Shape> path;
	protected Point2D.Float initLocation;
	public BulletPath(){
		path = new ArrayList<Shape>();
	}
	public BulletPath(Point2D.Float initLocation){
		this.initLocation = initLocation;
	}
	public BulletPath(Collection<Shape> c, Point2D.Float initLocation){
		path = new ArrayList<Shape>(c);
		this.initLocation = initLocation;
	}
	public void addShape(Shape s, Point2D.Float startLocation){
		if (path.size() == 0)
			this.initLocation = startLocation;
		path.add(s);
	}
	/**
	 * @return the location of the initial launch point
	 */
	public Point2D.Float getLocation(){
		return initLocation;
	}
	
	/**
	 * Moves the point p along the path at velocity speed. For generalized usage.
	 * @param p Point currently on the path
	 * @param velocity Speed to traverse the path at
	 * @param dt Time step (in milliseconds)
	 * @return New location after moving along the path
	 */
	public Point2D.Float step(Point2D.Float p, float velocity, float dt){
		return null;
	}
	
	/**
	 * Evaluates p(q), the path as a mathematical function. This is the parameterized version that will likely be used
	 * often with paths representative of mathematical functions.
	 * @param q Parameter that determines p
	 * @return New location after moving along the path
	 */
	public Point2D.Float step(float q){
		return null;
	}
	
	/**
	 * @return The current angle the bullet makes with the y-axis in radians
	 */
	public float getAngle(float q){
		return 0.0f;
	}
	
	/**
	 * @return Exact copy of this BulletPath
	 */
	public BulletPath clone(){
		return new BulletPath(new ArrayList<Shape>(path), new Point2D.Float(initLocation.x, initLocation.y));
	}
	
	/**
	 * Rotates the path angle degrees clockwise about a specified point
	 * @param p Point to rotate around
	 * @param angle Angle to rotate in degrees
	 */
	public void rotate(Point2D.Float p, float angle){
		
	}
	
	/**
	 * Rotates the path angle radians clockwise about a specified point
	 * @param p Point to rotate around
	 * @param angle Angle to rotate in radians
	 */
	public void radRotate(Point2D.Float p, float angle){
		
	}
	
	/**
	 * Translates the path by a specified amount
	 * @param x The new x location of the origin
	 * @param y The new y location of the origin
	 */
	public void translate(float x, float y){
		
	}
}
