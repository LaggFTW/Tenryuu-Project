package edu.mbhs.madubozhi.touhou.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

/**
 * Simple loading screen to fill up window when another window is loading
 * @author Matt Du
 */
public class LoadingScreen extends JFrame implements Runnable{

	private static final long serialVersionUID = -6488857411530162176L;
	//variables for screen width and height
	private int sWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	private int sHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
	
	private Thread thisThread;
	
	/**
	 * Constructor: creates a screen with an invisible cursor, drawn as specified in the
	 * render method
	 */
	public LoadingScreen(){
		super();
		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - sWidth)/2, (Toolkit.getDefaultToolkit().getScreenSize().height - sHeight)/2);
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Invisimouse"); 
		setCursor(blankCursor);
		setUndecorated(true);
		setFocusable(false);
		setSize(sWidth, sHeight);
		getContentPane().setBackground(Color.BLACK);
		thisThread = new Thread(this);
		thisThread.start();
	}
	
	/**
	 * Paints the screen, with double-buffering, refreshed as specified
	 * in the run method
	 * @param g
	 */
	public void paint(Graphics g){
		Image i=createImage(getWidth(), getHeight());
		render((i.getGraphics()));
		g.drawImage(i,0,0,this);
	}
	
	/**
	 * Rendering method: draws a black screen with white text that reads "Loading" in the
	 * center of the screen
	 * @param g
	 */
	public void render(Graphics g){
		super.paint(g);
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.PLAIN, 72));
		g.drawString("Loading", sWidth/2, sHeight/2);
	}

	/**
	 * Run: animates the canvas, to be implemented when animated loading screens
	 * are created
	 */
	public void run() {
		while(Thread.currentThread()==thisThread){
			repaint();
			try{
				Thread.sleep(20);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}