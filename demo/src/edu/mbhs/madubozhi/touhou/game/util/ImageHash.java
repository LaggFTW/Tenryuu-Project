package edu.mbhs.madubozhi.touhou.game.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import javax.imageio.ImageIO;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

/** 
 * Loads and stores all images
 */
public enum ImageHash {
	IMG();
	
//	private HashMap<String, BufferedImage> imageHash;
	private HashMap<String, Texture> textureHash;
	private boolean textureFailed;
	/**
	 * Constructor that instantiates this ImageHash. Reads in all images in the folder ./img/ and puts them into a hash
	 * with their name as the key
	 */
	private ImageHash(){
//		imageHash = new HashMap<String, BufferedImage>();
		textureHash = new HashMap<String, Texture>();
		textureFailed = false;
		try {
			readHash(new File(System.getProperty("user.dir") + "/img"));
		} catch (IOException e) {
			System.err.println("ImageHash error");
			e.printStackTrace();
		}
	}
	/**
	 * Reads in all images from dir and puts them into a hash with their name as the key
	 * @param dir The directory to read images from
	 * @throws IOException 
	 */
	private void readHash(File dir) throws IOException{
		for (File f:dir.listFiles()){
			if (f.getName().contains("vn") || f.getName().equals("menuimages")){
				continue;
			}
			if (f.isDirectory()){
				readHash(f);
			} else {
				if (f.getName().endsWith(".png")){
					String name = f.getName().substring(0, f.getName().lastIndexOf(".")).toLowerCase(Locale.ENGLISH);
					BufferedImage i = ImageIO.read(f);
//					imageHash.put(name, i);
					if (!textureFailed){
						try{
							Texture t = TextureIO.newTexture(TextureIO.newTextureData(i, false));
							textureHash.put(name, t);
						} catch(Throwable t){
							textureFailed = true;
							System.err.println("Problems reading texture");
						}
					}
				}
			}
		}
	}
	/**
	 * @param s The name of the image to load
	 * @return BufferedImage of the requested image, if it exists (else returns a null image)
	 */
	public BufferedImage getImage(String s){
//		return imageHash.get(s);
		return null;
	}
	/**
	 * @param s The name of the texture to load
	 * @return Texture of the requested texture, if it exists (else returns a null texture)
	 */
	public Texture getTex(String s){
		return textureHash.get(s);
	}
	/**
	 * Reconstructs the ImageHash
	 */
	public void rehash(){
//		imageHash = new HashMap<String, BufferedImage>();
		textureHash = new HashMap<String, Texture>();
		textureFailed = false;
		try {
			readHash(new File(System.getProperty("user.dir") + "/img"));
		} catch (IOException e) {
			System.err.println("ImageHash error");
			e.printStackTrace();
		}
	}
	/**
	 * Gets all keyed Strings in the hash
	 */
	public Collection<String> getKeys(){
		return textureHash.keySet();
	}
}
