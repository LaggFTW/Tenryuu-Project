package edu.mbhs.madubozhi.touhou.game.bullet.pattern;

import java.awt.geom.Point2D;

import edu.mbhs.madubozhi.touhou.game.util.RotationalDirection;

/**
 * Bullet path that utilizes multiple BulletPaths sequentially
 * @author bowenzhi
 *
 */
public class CompoundBulletPath extends BulletPath {
	private BulletPath[] path;
	private int index;
	private Point2D.Float position;
	private boolean cyclic;
	/**
	 * Instantiates this path
	 * @param cyclic If true, will loop back once all paths have been completed
	 * @param paths Paths to use in this path
	 */
	public CompoundBulletPath(boolean cyclic, BulletPath... paths){
		this.path = new BulletPath[paths.length];
		for (int i = 0; i < paths.length; i++){
			this.path[i] = paths[i];
		}
		this.index = 0;
		this.cyclic = cyclic;
		this.position = new Point2D.Float(paths.length>=1?paths[0]!=null?paths[0].getLocation().x:0.0f:0.0f, 
				paths.length>=1?paths[0]!=null?paths[0].getLocation().y:0.0f:0.0f);
	}
	/**
	 * Instantiates this path
	 * @param paths Paths to use in this path
	 */
	public CompoundBulletPath(BulletPath... paths){
		this(false, paths);
	}
	public Point2D.Float step(float q){
		if (!cyclic && q > (float)(path.length)){
			return null;
		} else {
			boolean indexed = false;
			if (q == 0){
				indexed = true;
			}
			if (cyclic && q > (float)(path.length)){
				q = q%path.length;
				int prevIndex = index;
				index = (int)(q);
				indexed = prevIndex != index;
			} else {
				if (q > (float)(index+1)){
					index = (int)(q);
					indexed = true;
				}
			}
			if (indexed){
				path[index].translate(position.x - path[index].getLocation().x, position.y - path[index].getLocation().y);
			}
			q -= (float)(index);
			Point2D.Float toReturn = path[index].step(q);
			position = toReturn;
			return toReturn;
		}
	}
	public CompoundBulletPath clone(){
		return new CompoundBulletPath(this.cyclic, this.getPaths());
	}
	public void translate(float x, float y){
		position.setLocation(position.x + x, position.y + y);
	}
	public void rotate(Point2D.Float p, float angle){
		position.setLocation(RotationalDirection.CLOCKWISE.rotate(angle, p, position));
		for (BulletPath bp:path){
			bp.rotate(p, angle);
		}
	}
	public void radRotate(Point2D.Float p, float angle){
		position.setLocation(RotationalDirection.CLOCKWISE.radRotate(angle, p, position));
		for (BulletPath bp:path){
			bp.radRotate(p, angle);
		}
	}
	public float getAngle(float q){
		if (index < path.length){
			return path[index].getAngle(q-index);
		}
		return 0.0f;
	}
	/**
	 * @return An array with clones of the paths
	 */
	public BulletPath[] getPaths(){
		BulletPath[] toReturn = new BulletPath[path.length];
		for (int i = 0; i < path.length; i++){
			toReturn[i] = path[i].clone();
		}
		return toReturn;
	}
}
