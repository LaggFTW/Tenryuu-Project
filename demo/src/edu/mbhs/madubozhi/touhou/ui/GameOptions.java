package edu.mbhs.madubozhi.touhou.ui;

/**
 * A object which contains various options that affect different game settings,
 * inclding, but not limited to: sound effects, background music, graphics effects,
 * voices, text speed, and language.
 * @author Matt Du
 *
 */
public class GameOptions{
	/**
	 * All variables are public for quick access by any class or object in the program
	 */
	//turns sound effects on or off
	public boolean SFX;
	//turns background music on or off
	public boolean BGM;
	//turns graphics effects on or off
	public boolean GFX;
	//turns voices on or off - not implemented
	public boolean voice;
	//adjusts text speed
	public int textSpeed;
	//turns instant text on or off
	public boolean insta;
	//adjusts language to English or Japanese
	public boolean language;
	//turns tsundere on or offs
	public boolean tsun;
	
	/**
	 * Constructor: initializes all the variables and settings
	 * @param BGM
	 * @param SFX
	 * @param voice
	 * @param GFX
	 * @param textSpeed
	 * @param language
	 * @param tsun
	 */
	public GameOptions(boolean BGM, boolean SFX, boolean voice, boolean GFX, int textSpeed, boolean language, boolean tsun){
		this.SFX=SFX; this.BGM=BGM; this.GFX=GFX; this.voice=voice; this.textSpeed=textSpeed; this.language=language; this.tsun = tsun;
		if(textSpeed>5)
			insta=true;
	}
}