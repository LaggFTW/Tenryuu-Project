package edu.mbhs.madubozhi.touhou.ui;


/**
 * A white circle object with an x and a y, which falls through the menu screens
 * if the fancy graphcis option is enabled
 * @author Matt Du
 *
 */
public class Snow{
	//snow properties
	public float x, y, size, initX, targetX;
	public float dX, dY;
	private int fEnd, fBegin;
	
	/**
	 * Constructor: creates a snow given the size of the snow, it's starting position,
	 * stopping position, and y velocity
	 * @param size
	 * @param xStart
	 * @param xEnd
	 * @param yVel
	 * @param ratio
	 */
	public Snow(float size, int xStart, int xEnd, float yVel, float ratio){
		this.size=size; this.initX=xStart; this.targetX = xEnd; this.dY=yVel;
		this.y=0;
		int newWidth = (int)((float)FullGame.HEIGHT/ratio);
		fBegin = (FullGame.WIDTH-newWidth)/2; fEnd = fBegin+newWidth;
		x=initX;	y=(float)(Math.random()*FullGame.HEIGHT);
	}
	
	/**
	 * Increments the snow location
	 */
	public void step(){
		y+=dY;
		dX = (dY)/(float)FullGame.HEIGHT*(targetX-initX);
		x+=dX;
		if(y>FullGame.HEIGHT) y=0f;
		if(x-size>fEnd)x=fBegin-size;
		if(x+size<fBegin)x=fEnd+size;
	}

}