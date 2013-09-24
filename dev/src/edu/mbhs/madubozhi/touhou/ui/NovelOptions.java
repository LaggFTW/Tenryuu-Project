package edu.mbhs.madubozhi.touhou.ui;

/**
 * A class containing options for the visual novel interface, such as text speed,
 * music on/off, and voice on/off
 * @author Matt Du
 *
 */
public class NovelOptions{
	//options
	public int textSpeed;
	public boolean music;
	public boolean voice;
	public boolean insta;

	/**
	 * Default constructor sets text speed to slow, with music on, voices on,
	 * and instant text off
	 */
	public NovelOptions(){
		this(1, true, true,  false);
	}
	
	/**
	 * Creates a new option set, as specified by the user
	 * @param speed
	 * @param music
	 * @param voice
	 * @param insta
	 */
	public NovelOptions(int speed, boolean music, boolean voice, boolean insta){
		this.textSpeed=speed;
		this.music = music;
		this.voice=voice;
		this.insta=insta;
	}
	
}