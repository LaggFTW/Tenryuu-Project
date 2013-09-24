package edu.mbhs.madubozhi.touhou.game.enemy.pattern;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.mbhs.madubozhi.touhou.game.bullet.pattern.BulletPath;
import edu.mbhs.madubozhi.touhou.game.enemy.Enemy;
import edu.mbhs.madubozhi.touhou.game.util.RotationalDirection;

/**
 * Launches part of or a complete circle of Enemies, all of which have the same BulletPath. Capable of delayed shooting
 * between enemies (ie. shooting <i>in</i> a circle, with some delay time between each enemy launched).
 * @author bowenzhi
 */
public class EnemyMovementPattern {
/**
 * Launches Enemies in a circular arc around a point
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
	private Enemy bullet;
	private BulletPath path;
	private boolean inclusive;//include/exclude stopAngle
	private boolean finished;
	private boolean radians;//use radians for the angles
	
	private float currentAngle;
	private float dAngle;
	private int counter;
	private int bulletCounter;
	
	/**
	 * Creates an EnemyMovementPattern based on the specified variables
	 * @param origin Center of pattern
	 * @param startAngle Starting angle of the arc that this pattern will trace out
	 * @param stopAngle Ending angle of the arc that this pattern will trace out
	 * @param numBullets Number of enemies this arc will fire out
	 * @param delay Delay between shots
	 * @param radius Radius from the origin to spawn enemies
	 * @param e Enemy to spawn
	 * @param path Path the enemies will take
	 * @param inclusive Whether or not to include the stopAngle in the arc or not
	 */
	public EnemyMovementPattern(Point2D.Float origin, float startAngle, float stopAngle, int numBullets, int delay, 
			float radius, Enemy e, BulletPath path, boolean inclusive){
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
		this.bullet = e;
		this.path = path;
		this.inclusive = inclusive;
		this.currentAngle = startAngle;
		this.dAngle = (stopAngle - startAngle) / numBullets;
		this.direction = dAngle > 0 ? RotationalDirection.CLOCKWISE: RotationalDirection.COUNTERCLOCKWISE;
		this.counter = 0;
		this.radians = false;
		this.bulletCounter = this.numBullets;
	}
	
	/**
	 * Creates an EnemyMovementPattern based on the specified variables
	 * @param origin Center of pattern
	 * @param startAngle Starting angle of the arc that this pattern will trace out
	 * @param stopAngle Ending angle of the arc that this pattern will trace out
	 * @param numBullets Number of enemies this arc will fire out
	 * @param delay Delay between shots
	 * @param radius Radius from the origin to spawn enemies
	 * @param e Enemy to spawn
	 * @param path Path the enemies will take
	 * @param inclusive Whether or not to include the stopAngle in the arc or not
	 * @param radians Whether or not this pattern uses radians or degrees
	 */
	public EnemyMovementPattern(Point2D.Float origin, float startAngle, float stopAngle, int numBullets, int delay, 
			float radius, Enemy e, BulletPath path, boolean inclusive, boolean radians){
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
		this.bullet = e;
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
	public ArrayList<Enemy> step(){
		ArrayList<Enemy> arr = null;
		while (counter >= delay){
			if (bulletCounter <= 0){
				finished = true;
				return arr;
			}
			if (direction.equals(RotationalDirection.CLOCKWISE)){
				if (inclusive? currentAngle >= stopAngle: currentAngle > stopAngle){
					finished = true;
					return arr;
				}
			} else if (direction.equals(RotationalDirection.COUNTERCLOCKWISE)){
				if (inclusive? currentAngle <= stopAngle: currentAngle < stopAngle){
					finished = true;
					return arr;
				}
			}
			if (arr == null)
				arr = new ArrayList<Enemy>();
			Enemy tempE = bullet.cloneBasic();
			BulletPath tempBP = path.clone();
			tempBP.translate(origin.x + radius, origin.y);
			if (radians)
				tempBP.radRotate(origin, currentAngle);
			else
				tempBP.rotate(origin, currentAngle);
			tempE.addPath(tempBP);
			arr.add(tempE);
			bulletCounter--;
			currentAngle += dAngle;
			counter = 0;
		}
		counter++;
		return arr;
	}
	
	/**
	 * @return Angle this arc started at
	 */
	public float getStartAngle(){
		return startAngle;
	}
	
	/**
	 * @return Whether or not this pattern has been complete
	 */
	public boolean isFinished(){
		return finished?true:bulletCounter<=0;
	}
	
	/**
	 * Resets this pattern to be launched again
	 * @param newAngle Value of startAngle that will be used
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
	 * Sets the origin of the pattern
	 * @param x
	 * @param y
	 */
	public void setOrigin(float x, float y){
		if (this.origin != null){
			this.origin.x = x;
			this.origin.y = y;
		} else {
			this.origin = new Point2D.Float(x, y);
		}
	}
	
	public EnemyMovementPattern clone(){
		return new EnemyMovementPattern(this.origin!=null?new Point2D.Float(origin.x, origin.y):new Point2D.Float(),
				this.startAngle, this.stopAngle, this.numBullets, this.delay, this.radius, this.bullet.cloneBasic(),
				this.path.clone(), this.inclusive, this.radians);
	}
}
