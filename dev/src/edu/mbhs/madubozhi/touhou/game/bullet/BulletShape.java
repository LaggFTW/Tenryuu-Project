package edu.mbhs.madubozhi.touhou.game.bullet;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.sun.opengl.util.texture.Texture;

import edu.mbhs.madubozhi.touhou.game.util.ImageHash;

/**
 * The game will mostly feature a set of standardized bullet types. These have preset dimensions, sprites, hitboxes,
 * and colors.
 * <p>Format for bullet sprite names is as follows: [basename]_[color].png
 * @author bowenzhi
 */
public enum BulletShape {
	ORB_SMALL("orb_small",new Dimension(4, 4),new Dimension(7, 7)),
	ORB_MED_SMALL("orb",new Dimension(6, 6),new Dimension(10, 10)),
	ORB("orb",new Dimension(12, 12),new Dimension(25, 25)),
	ORB_LARGE("orb_large",new Dimension(25, 25),new Dimension(50, 50)),
	BULLET("bullet",new Dimension(9, 9),new Dimension(26, 9)),
	BUBBLE("bubble",new Dimension(32, 32),new Dimension(100, 100)),
	BUBBLE_HOLLOW("hbubble",new Dimension(54, 54),new Dimension(100, 100)),
	BUBBLE_SQUARE("sbubble",new Dimension(32, 32),new Dimension(100, 100)),
	SAKURA("sakura",new Dimension(10, 10),new Dimension(20, 15)),
	BLADE_SMALL("blade",new Dimension(5, 5),new Dimension(25 ,7)),
	BLADE("blade",new Dimension(8, 8),new Dimension(54, 12)),
	BLADE_LARGE("blade",new Dimension(12, 12),new Dimension(72, 16)),
	SPEAR("spear",new Dimension(124, 20),new Dimension(240, 28)),
	SPEAR_ROUND("rspear",new Dimension(124, 20),new Dimension(240, 28)),
	STAR_SMALL("star",new Dimension(12, 12),new Dimension(30, 30)),
	STAR_LARGE("star",new Dimension(24, 24),new Dimension(60, 60)),
	SNOWFLAKE("snowflake",new Dimension(12, 12),new Dimension(36, 36)),
	DIAMOND("diamond",new Dimension(7, 7),new Dimension(30, 12)),
	WAVE_SMALL("wave",new Dimension(11, 11),new Dimension(27, 20)),
	WAVE_LARGE("wave",new Dimension(20, 20),new Dimension(47, 35)),
	FAMILIAR("familiar",new Dimension(10, 10),new Dimension(50, 50)),
	BARRIER("barrier",new Dimension(80, 80),new Dimension(120, 120));
	//("",null,null) are placeholders for now
	
	private final String baseSpriteName;
	private final Dimension hitbox;
	private final Dimension size;
	
	/**
	 * Instantiates a bullet shape with the following properties.
	 * @param baseSpriteName name of the sprite of the bullet to be used to read in the sprite images
	 * @param hitbox hitbox dimensions of the bullet
	 * @param size drawn dimensions of the bullet
	 */
	private BulletShape(String baseSpriteName, Dimension hitbox, Dimension size){
		this.baseSpriteName = baseSpriteName;
		this.hitbox = hitbox;
		this.size = size;
	}
	
	/**
	 * @return hitbox of this bullet shape
	 */
	public Dimension getHitbox(){
		return hitbox;
	}
	
	/**
	 * @return size of this bullet shape
	 */
	public Dimension getSize(){
		return size;
	}
	
	/**
	 * @param color Color requested
	 * @return a sprite of this bullet shape with the color requested, in the form of a BufferedImage
	 */
	public BufferedImage getSprite(String color){
		String toAppend = (color==null||color.equals(""))?(""):("_" + color);
		return ImageHash.IMG.getImage(this.baseSpriteName + toAppend);
	}
	
	/**
	 * @param color Color requested
	 * @return a sprite of this bullet shape with the color requested, in the form of a Texture
	 */
	public Texture getSpriteTex(String color){
		String toAppend = (color==null||color.equals(""))?(""):("_" + color);
		if (!toAppend.equals("") && color.equals("random")){
			ArrayList<String> colors = new ArrayList<String>();
			for (String s:ImageHash.IMG.getKeys()){
				if (s.startsWith(this.baseSpriteName)){
					colors.add(s.substring(s.lastIndexOf("_")));
				}
			}
			toAppend = colors.get((int)(Math.random()*colors.size()));
		}
		return ImageHash.IMG.getTex(this.baseSpriteName + toAppend);
	}
	
}
