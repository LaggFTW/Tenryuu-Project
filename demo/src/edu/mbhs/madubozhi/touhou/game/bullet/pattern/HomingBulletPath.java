package edu.mbhs.madubozhi.touhou.game.bullet.pattern;

import java.awt.geom.Point2D;

import edu.mbhs.madubozhi.touhou.game.Player;
import edu.mbhs.madubozhi.touhou.game.util.PathTraversal;
import edu.mbhs.madubozhi.touhou.game.util.RotationalDirection;

@Deprecated
/**
 * Supposedly homes in on the player; as of current, does not function.
 * @author bowenzhi
 *
 */
public class HomingBulletPath extends LinearBulletPath {
/**
 * Path homes in on the player
 */
	private Player player;
	private float lambda;//homing scale
	private float angle;
	private float distance;
	private float qPrevious;
	private Point2D.Float previousLocation;
	private int previousLives;//keeps track in case the player dies
	public HomingBulletPath(Point2D.Float p0, Point2D.Float p1, Player player, float homingScale){
		this(p0, p1, player, homingScale, PathTraversal.LINEAR);
	}
	public HomingBulletPath(Point2D.Float p0, Point2D.Float p1, Player player, float homingScale, PathTraversal traversal){
		super(p0, p1, traversal);
		this.player = player;
		this.lambda = homingScale;
		this.previousLocation = p0;
		this.previousLives = player.getLives();
		this.qPrevious = 0;
		this.distance = (float)(p1.distance(p0));
		calcAngle();
	}
	public Point2D.Float getLocation(){
		return p0;
	}
	public Point2D.Float step(float q){
		if (q >= 0 && q <= 1 && player.getLives() == previousLives){
			previousLocation = new Point2D.Float((float)(previousLocation.x + distance * Math.sin(angle) * (q-qPrevious)), (float)(previousLocation.y + distance * Math.cos(angle) * (q-qPrevious)));
			qPrevious = q;
			calcAngle();
			return previousLocation;
		}
		return null;
	}
	public HomingBulletPath clone(){
		return new HomingBulletPath(new Point2D.Float(p0.x, p0.y), new Point2D.Float(p1.x, p1.y), this.player, this.lambda, 
				this.traversal);
	}
	public float getAngle(float q){
		return angle;
	}
	public void calcAngle(){
		if (lambda == 0 || previousLocation.equals(p0))
			angle = super.getAngle(0);
		angle = (float) ((angle + (RotationalDirection.getAngleBetween(new Point2D.Float(player.getX().floatValue(), player.getY().floatValue()), previousLocation)%(2*Math.PI)==angle?0:(RotationalDirection.getAngleBetween(new Point2D.Float(player.getX().floatValue(), player.getY().floatValue()), previousLocation)%(2*Math.PI))<angle?lambda:-lambda)) % (2*Math.PI));
	}
}
