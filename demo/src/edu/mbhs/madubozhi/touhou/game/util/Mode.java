package edu.mbhs.madubozhi.touhou.game.util;

/**
 * Enum for storing the game properties of different game modes
 * @author Matt Du and Bowen Zhi
 */
public enum Mode{
	//construct the different game modes with their respective properties
	EASYMODO("Easymodo", 25, 0), NORMAL("Normal", 15, 1), HARD("Hard", 10, 2), LUNATIC("Lunatic", 6, 3), EXTRA("Extra", 3, 4);
	
	//properties of different bullets
	private String name;
	private int bulletNumber;
	private int index;
	
	/**
	 * Constructor: creates a game mode with the given name, bullet density, 
	 * and index for the game to parse.
	 * @param name
	 * @param bulletNumber
	 * @param index
	 */
	private Mode(String name, int bulletNumber, int index){
		this.name = name;
		this.bulletNumber = bulletNumber;
		this.index = index;
	}
	
	/**
	 * Returns the name of the game mode
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Returns the bullet density of the game mode
	 * @return
	 */
	public int getBulletNumber(){
		return bulletNumber;
	}
	
	/**
	 * Returns the index of the game mode
	 * @return
	 */
	public int getIndex(){
		return index;
	}
	
	/**
	 * Sets the bullet density of a game mode
	 * @param bulletNumber
	 */
	public void setBulletNumber(int bulletNumber){
		this.bulletNumber = bulletNumber;
	}
	
	/**
	 * Returns the name of the game mode
	 * @return
	 */
	public String toString(){
		return name;
	}
}