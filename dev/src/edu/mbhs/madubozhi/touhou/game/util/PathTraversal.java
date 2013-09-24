package edu.mbhs.madubozhi.touhou.game.util;

/**
 * Describes the progression pattern of the bullet along a path. There are three kinds: linear, quadratic, and root.
 * <ul>
 * <li>A linear path describes a constant acceleration of 0
 * <li>A quadratic path describes a positive acceleration
 * <li>A root path describes a negative acceleration
 * </ul>
 * @author bowenzhi
 */
public enum PathTraversal {

	LINEAR(0), QUADRATIC(1), ROOT(2);
	private final int index;
	/**
	 * Instantiates a PathTravesal with an indexed reference.
	 * @param index reference index
	 */
	private PathTraversal(int index){
		this.index = index;
	}
	/**
	 * Returns the new parameter's value based on the path traversal type
	 * @param q Parameter
	 */
	public float getAdjustedParam(float q){
		switch(this.index){
		case 0:
			return q;
		case 1:
			return (float)(Math.pow(q, 2));
		case 2:
			return (float)(Math.sqrt(q));
		default:
			return 0;
		}
	}
}
