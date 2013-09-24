package edu.mbhs.madubozhi.touhou.game.bullet;

import java.awt.Dimension;
import java.util.ArrayList;

import com.sun.opengl.util.texture.Texture;

import edu.mbhs.madubozhi.touhou.game.bullet.pattern.ArcBulletPattern;
import edu.mbhs.madubozhi.touhou.game.util.Oscillator;

/**
 * An EmbeddedBullet is an EnemyBullet that shoots out other bullets (in patterns, of course).
 * @author bowenzhi
 */
public class EmbeddedBullet extends EnemyBullet {
/**
 * Bullet with other bullets and patterns embedded within it
 */
	private ArcBulletPattern embeddedPattern;
	private Oscillator oscillator;
	private int frequency;
	private int counter;
	
	/**
	 * Instantiates the bullet using these specifications
	 * @param velocity speed of bullet
	 * @param acceleration acceleration of bullet
	 * @param airFric air friction of bullet
	 * @param hitbox hitbox of bullet
	 * @param sprite sprite of bullet
	 * @param spriteSize sprite size of bullet
	 * @param vulnerableHitbox hitbox of bullet that the bullet can be hit on
	 * @param hp durability of bullet
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param embeddedPattern pattern that this bullet shoots other bullets out with
	 * @param frequency frequency that this bullet shoots other bullets out with
	 * @param oscillator Oscillator for shooting varying the pattern that the bullets are shot out with
	 */
	public EmbeddedBullet(float velocity, Dimension hitbox, Texture sprite, Dimension spriteSize, int hp,
			int scoreValue, boolean lockAngle, boolean offScreenDelete, ArcBulletPattern embeddedPattern, int frequency, 
			Oscillator oscillator) {
		this(velocity, hitbox, sprite, spriteSize, hp, scoreValue, lockAngle, offScreenDelete, embeddedPattern, frequency, oscillator, false);
	}
	
	/**
	 * Instantiates the bullet using these specifications, for standard-shaped bullets
	 * @param velocity speed of bullet
	 * @param acceleration acceleration of bullet
	 * @param airFric air friction of bullet
	 * @param shape shape of bullet
	 * @param c color of bullet
	 * @param hp durability of bullet
	 * @param lockAngle determines whether or not to calculate the angle the bullet is moving in for sprite animations
	 * @param embeddedPattern pattern that this bullet shoots other bullets out with
	 * @param frequency frequency that this bullet shoots other bullets out with
	 * @param oscillator Oscillator for shooting varying the pattern that the bullets are shot out with
	 */
	public EmbeddedBullet(float velocity, BulletShape shape, String c, int hp, int scoreValue, boolean lockAngle, boolean offScreenDelete, ArcBulletPattern embeddedPattern, 
			int frequency, Oscillator oscillator){
		this(velocity, shape.getHitbox(), shape.getSpriteTex(c), shape.getSize(),
				hp, scoreValue, lockAngle, offScreenDelete, embeddedPattern, frequency, oscillator);
	}
	
	public EmbeddedBullet(float velocity, Dimension hitbox, Texture sprite, Dimension spriteSize, int hp,
			int scoreValue, boolean lockAngle, boolean offScreenDelete, ArcBulletPattern embeddedPattern, int frequency, 
			Oscillator oscillator, boolean spinning) {
		super(velocity, hitbox, sprite, spriteSize, hp, scoreValue, lockAngle, offScreenDelete, spinning);
		this.embeddedPattern = embeddedPattern;
		this.oscillator = oscillator;
		this.frequency = frequency;
		this.counter = 0;
	}
	
	public EmbeddedBullet(float velocity, BulletShape shape, String c, int hp, int scoreValue, boolean lockAngle, boolean offScreenDelete, ArcBulletPattern embeddedPattern, 
			int frequency, Oscillator oscillator, boolean spinning){
		this(velocity, shape.getHitbox(), shape.getSpriteTex(c), shape.getSize(),
				hp, scoreValue, lockAngle, offScreenDelete, embeddedPattern, frequency, oscillator, spinning);
	}
	
	/**
	 * Determines whether to shoot bullets or not. Called every frame.
	 * @return an ArrayList with all bullets shot by this bullet in it
	 */
	public ArrayList<Bullet> launchPattern(){
		ArrayList<Bullet> arr = null;
		if (counter >= frequency && embeddedPattern != null){
			embeddedPattern.setOrigin(this.getLocation().x, this.getLocation().y);
			arr = new ArrayList<Bullet>();
			ArrayList<Bullet> temp = embeddedPattern.step();
			if (temp != null)
				for (Bullet b:temp)
					arr.add(b);
			embeddedPattern.reset(oscillator!=null?oscillator.getAngle():embeddedPattern.getStartAngle());
			counter = 0;
		}
		if (oscillator!=null)
			oscillator.oscillate();
		counter++;
		return arr;
	}
	
	@Override
	public EmbeddedBullet cloneBasicProperties(){
		return new EmbeddedBullet(this.velocity, this.hitboxSize, this.sprite,
				this.spriteSize, this.hp, this.scoreValue, this.lockAngle, this.offScreenDelete,
				this.embeddedPattern.clone(), this.frequency, oscillator!=null?this.oscillator.clone():null, 
						this.spinning);
	}
	
}
