package edu.mbhs.madubozhi.touhou.ui;

import java.awt.image.BufferedImage;

import com.sun.opengl.util.texture.Texture;

import edu.mbhs.madubozhi.touhou.game.util.ImageHash;

/**
 * Enum containing the four preset characters and their base properties, including
 * but not limited to: name, strength, evasiveness, gender, and speed; as well as their speical abilities
 * @author Matt Du
 *
 */
public enum Character{
	//initialize characters
	ASAMI(0, "asami", 2, 2, 1, 5, Ability.BORDERLESS), JIN(1, "jin", 5, 1, 0, 4, Ability.BARRIER), 
	MIZUKI(2, "mizuki", 0, 4, 4, 2, Ability.DODGE), KAITO(3, "kaito", 4, 2, 2, 2, Ability.TIMESTOP), 
	UNDEFINED(-1, "", 0, 0, 0, 0, null);
	
	//character properties
	protected String fName;
	protected String lName;
	private final float strength;
	private final float hitBoxSize;
	private final float charisma;
	private final float speed;
	private float effStrength;
	private float effHitboxSize;
	private float effCharisma;
	private float effSpeed;
	private String baseSpriteName;
	private Ability ability;
	protected boolean gender;

	public int charCode;
	
	/**
	 * Creates a character, given the character code and base stats
	 * @param charCode
	 * @param strength
	 * @param hit
	 * @param speed
	 * @param remilia
	 */
	private Character(int charCode, String baseSpriteName, float strength, float hit, float speed, float remilia, Ability ability){
		this.charCode = charCode;
		this.baseSpriteName = baseSpriteName;
		this.ability = ability;
		setBaseStats();
		this.strength = strength;
		this.hitBoxSize = hit;
		this.speed = speed;
		this.charisma = remilia;
		this.setStrength(strength);
		this.setHitBoxSize(hit);
		this.setSpeed(speed);
		this.setCharisma(remilia);
	}
	
	/**
	 * Modifies stats with different input additions
	 * @param strengthMod
	 * @param speedMod
	 * @param hitMod
	 * @param remiliaMod
	 */
	public void setMods(float strengthMod, float speedMod, float hitMod, float remiliaMod){
		setStrength(getBaseStrength() + strengthMod); setHitBoxSize(getBaseHitbox() + hitMod); setSpeed(getBaseSpeed() + speedMod); setCharisma(getBaseCharisma() + remiliaMod);
	}
	
	/**
	 * Gets the statistic of a character, given the stat code
	 * @param statCode
	 * @return
	 */
	public int getStat(int statCode){
		switch(statCode){
		case 0:	return (int)getBaseStrength();
		case 1: return (int)getBaseSpeed();
		case 2: return (int)getBaseHitbox();
		case 3: return (int)getBaseCharisma();
		default: return 0;
		}
	}

	/**
	 * Initialize base statistics, given a character code
	 */
	private void setBaseStats(){
		switch(charCode){
		case 0:
			fName = "Asami";
			lName = "Yukino";
			gender = true;
			break;
		case 1:
			fName = "Jin";
			lName = "Kuromi";
			gender = false;
			break;
		case 2:
			fName = "Mizuki";
			lName = "Akisora";
			gender = true;
			break;
		case 3:
			fName = "Kaito";
			lName = "Aidama";
			gender = false;
			break;
		default:
			fName = "Invalid";
			lName = "Invalid";
			statSet(new float[]{0, 0, 0, 0});
		}
	}

	/**
	 * Set the statistics of a character, given an array with the statistics
	 * @param x
	 */
	public void statSet(float [] x){
		setStrength(x[0]); setHitBoxSize(x[1]); setCharisma(x[2]);setSpeed(x[3]);
	}

	public float getSpeed() {
		return effSpeed;
	}

	public void setSpeed(float speed) {
		this.effSpeed = speed;
	}

	public float getHitBoxSize() {
		return effHitboxSize;
	}

	public void setHitBoxSize(float hitBoxSize) {
		this.effHitboxSize = hitBoxSize;
	}

	public float getStrength() {
		return effStrength;
	}

	public void setStrength(float strength) {
		this.effStrength = strength;
	}

	public float getCharisma() {
		return effCharisma;
	}

	public void setCharisma(float charisma) {
		this.effCharisma = charisma;
	}
	
	public Ability getAbility(){
		return ability;
	}
	
	public float getBaseStrength(){
		return this.strength;
	}
	
	public float getBaseSpeed(){
		return this.speed;
	}
	
	public float getBaseHitbox(){
		return this.hitBoxSize;
	}
	
	public float getBaseCharisma(){
		return this.charisma;
	}
	
	public void resetStats(){
		this.effStrength = this.strength;
		this.effSpeed = this.speed;
		this.effHitboxSize = this.hitBoxSize;
		this.effCharisma = this.charisma;
	}
	
	public BufferedImage getSprite(){
		return ImageHash.IMG.getImage(baseSpriteName);
	}
	
	public Texture getSpriteTex(){
		return ImageHash.IMG.getTex(baseSpriteName);
	}
	
}