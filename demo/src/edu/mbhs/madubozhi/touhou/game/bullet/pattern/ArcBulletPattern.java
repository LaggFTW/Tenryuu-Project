package edu.mbhs.madubozhi.touhou.game.bullet.pattern;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.mbhs.madubozhi.touhou.game.bullet.Bullet;
import edu.mbhs.madubozhi.touhou.game.util.RotationalDirection;

/**
 * Launches part of or a complete circle of Bullets, all of which have the same BulletPath. Capable of delayed shooting
 * between bullets (ie. shooting <i>in</i> a circle, with some delay time between each bullet launched).
 * @author bowenzhi
 */
public class ArcBulletPattern extends BulletPattern {
/**
 * Launches Bullets in a circular arc around a point
 * Params:
 * Origin (X, Y)
 * Start Angle
 * Stop Angle
 * Number of Bullets
 * Delay Between Bullets
 * Direction of Rotation
 * Starting Radius
 * Bullet Path
 */
	private Point2D.Float origin;
	private float startAngle;
	private float stopAngle;
	private int numBullets;
	private int delay;
	private RotationalDirection direction;
	private float radius;
	private Bullet bullet;
	private BulletPath path;
	private boolean inclusive;//include/exclude stopAngle
	private boolean finished;
	private boolean radians;//use radians for the angles
	
	private float currentAngle;
	private float dAngle;
	private int counter;
	private int bulletCounter;
	
	/**
	 * Instantiates this pattern.
	 * @param origin location of shooting
	 * @param startAngle starting angle; orientation of 0 depends on the BulletPath (as it is rotated)
	 * @param stopAngle end angle; orientation of 0 depends on the BulletPath (as it is rotated)
	 * @param numBullets number of bullets shot
	 * @param delay delay between bullets
	 * @param radius radius a which the bullets are initially launched from the origin
	 * @param b bullet to shoot
	 * @param path path the bullets will take
	 * @param inclusive whether to end at stopAngle or stopAngle +/- epsilon
	 * @param radians whether or not startAngle and stopAngle are in radians
	 */
	public ArcBulletPattern(Point2D.Float origin, float startAngle, float stopAngle, int numBullets, int delay, 
			float radius, Bullet b, BulletPath path, boolean inclusive, boolean radians){
		if (numBullets == 0){
			System.err.println("ArcBulletPattern: Nothing was shot");
			return;
		}
		this.origin = origin;
		this.startAngle = startAngle;
		this.stopAngle = stopAngle;
		this.numBullets = numBullets;
		this.delay = delay;
		this.radius = radius;
		this.bullet = b;
		this.path = path;
		this.inclusive = inclusive;
		this.currentAngle = startAngle;
		this.dAngle = (stopAngle - startAngle) / numBullets;
		this.direction = dAngle > 0 ? RotationalDirection.CLOCKWISE: RotationalDirection.COUNTERCLOCKWISE;
		this.counter = 0;
		this.radians = radians;
		this.bulletCounter = this.numBullets;
	}
	
	/**
	 * If bullets were launched, return pointers to those bullets, else returns null.
	 */
	public ArrayList<Bullet> step(){
		ArrayList<Bullet> arr = null;
		while (counter >= delay){
			if (bulletCounter <= 0){
				finished = true;
				return arr;
			}
			if (direction.equals(RotationalDirection.CLOCKWISE)){
				if (inclusive? currentAngle > stopAngle: currentAngle >= stopAngle){
					finished = true;
					return arr;
				}
			} else if (direction.equals(RotationalDirection.COUNTERCLOCKWISE)){
				if (inclusive? currentAngle < stopAngle: currentAngle <= stopAngle){
					finished = true;
					return arr;
				}
			}
			if (arr == null)
				arr = new ArrayList<Bullet>();
			Bullet tempB = bullet.cloneBasicProperties();
			BulletPath tempBP = path.clone();
			tempBP.translate(origin.x + radius, origin.y);
			if (radians)
				tempBP.radRotate(origin, currentAngle);
			else
				tempBP.rotate(origin, currentAngle);
			tempB.addPath(tempBP);
			arr.add(tempB);
			bulletCounter--;
			currentAngle += dAngle;
			counter = 0;
		}
		counter++;
		return arr;
	}
	
	/**
	 * @return initial angle of launch
	 */
	public float getStartAngle(){
		return startAngle;
	}
	
	/**
	 * @return whether or not the last bullet was fired
	 */
	public boolean isFinished(){
		return finished?true:bulletCounter<=0;
	}
	
	/**
	 * Resets the pattern for repeated launch, with the given starting angle.
	 * @param newAngle starting angle
	 */
	public void reset(float newAngle){
		finished = false;
		stopAngle = (stopAngle - startAngle) + newAngle;
		startAngle = newAngle;
		currentAngle = startAngle;
		counter = 0;
		bulletCounter = numBullets;
	}
	
	/**
	 * Shifts the launch origin.
	 * @param x new X value
	 * @param y new Y value
	 */
	public void setOrigin(float x, float y){
		if (this.origin != null){
			this.origin.x = x;
			this.origin.y = y;
		} else {
			this.origin = new Point2D.Float(x, y);
		}
	}
	
	public ArcBulletPattern clone(){
		return new ArcBulletPattern(this.origin!=null?new Point2D.Float(origin.x, origin.y):new Point2D.Float(),
				this.startAngle, this.stopAngle, this.numBullets, this.delay, this.radius,
				this.bullet.cloneBasicProperties(), this.path.clone(), this.inclusive, this.radians);
	}
	
}
