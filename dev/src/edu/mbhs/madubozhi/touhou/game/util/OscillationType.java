package edu.mbhs.madubozhi.touhou.game.util;

/**
 * Enum for the way some value oscillates (usually used for the launch angle of certain spellcards)
 * @author bowenzhi
 */
public enum OscillationType {
	NO_OSCILLATION(0), COSINE(1), BOUNDED_OMEGA(2);
	private final int index;
	private OscillationType(int index){
		this.index = index;
	}
	public int getIndex(){
		return index;
	}
};