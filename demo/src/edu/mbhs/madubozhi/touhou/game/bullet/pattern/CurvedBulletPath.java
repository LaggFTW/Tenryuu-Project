package edu.mbhs.madubozhi.touhou.game.bullet.pattern;

import java.awt.Shape;

public class CurvedBulletPath extends BulletPath {
/**
 * Path follows a curved path (ie. sinusoidal) around another path
 */
	private Shape path;
	private float amplitude;
	private float period;
	private float phaseShift;//eg. 0.5 = shifted half of a period ahead
}
