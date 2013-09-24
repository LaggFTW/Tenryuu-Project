package edu.mbhs.madubozhi.touhou.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import edu.mbhs.madubozhi.touhou.audio.SoundEffect;
import edu.mbhs.madubozhi.touhou.game.util.Mode;

/**
 * JFrame which contains the main menu, and has options that link to every other screen
 * in the game. Uses GameOption for different effects, and is animated. All graphics are
 * loaded from predetermined directories.
 * @author Matt Du
 *
 */

public class Menu extends JFrame implements Runnable{
	//class variables
	private static final long serialVersionUID = 2606958446610692290L;
	private static String DEFAULT_TITLE = "Tenryuu Project: Main Menu";
	private Thread thisThread;
	private FullGame parent;
	private int frameCounter;
	
	//variables which keep track of current option
	private int opt = 0, maxOption=5;
	private int rMaxOption = 4;
	
	//graphics variables
	private ArrayList<Snow> snowflakes;
	private int numSnowflakes = 300;

	//variables for text spacing
	private int textX = 65;
	private int textSpacing = 65;
	private int dropShadow = 2;
	private double transX=0;
	
	//arrays of loaded images
	private Image [] snow = new Image[4];
	private Image [] images = new Image[maxOption+1];
	private Image [] hilite = new Image[maxOption+1];
	private Image [] jpimgs = new Image[maxOption+1];
	private Image [] jphils = new Image[maxOption+1];
	private Image mountain, bg, bgtsun, sky, cloud, pointer;
	private Image [] rImages = new Image [rMaxOption+1];
	private Image [] rHilite = new Image [rMaxOption+1];
	
	//miscellaneous variables for keeping track of transitions and menu types
	private boolean menuMode = true;
	private boolean transition=false, rTransition = false;
	private int maxX; private double speed=30;

	private boolean initFinish = false;

	/**
	 * Constructor creates a new menu with a blank cursor and custom keyListener.
	 * Sets a blank cursor, and intializes all graphics variables and related objects.
	 * @param g
	 */
	public Menu(FullGame g){
		super(DEFAULT_TITLE);	
		parent = g;	snowflakes = new ArrayList<Snow>(); initImages();
		setSize(FullGame.WIDTH, FullGame.HEIGHT);
		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - FullGame.WIDTH)/2, (Toolkit.getDefaultToolkit().getScreenSize().height - FullGame.HEIGHT)/2);
		setUndecorated(true);
		//create blank cursor
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Invisimouse"); 
		setCursor(blankCursor);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.BLACK);
		
		//keyListener: checks for cursor location and menu type, then adjusts graphics variables accordingly
		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				switch(e.getKeyCode()){
				case KeyEvent.VK_LEFT:	case KeyEvent.VK_A:	case KeyEvent.VK_W:	case KeyEvent.VK_UP: 
					opt--; 
					if(opt<(menuMode?0:1))
						opt=(menuMode?maxOption:rMaxOption);
					break;
				case KeyEvent.VK_RIGHT:	case KeyEvent.VK_S:	case KeyEvent.VK_D:	case KeyEvent.VK_DOWN: 
					opt++; 
					if(opt>(menuMode?maxOption:rMaxOption))
						opt=(menuMode?0:1); 
					break;
				case KeyEvent.VK_SPACE:	case KeyEvent.VK_ENTER: case KeyEvent.VK_Z: 
					if (parent.go.SFX) {new SoundEffect(0).playToStop();}
					if(!(rTransition || transition))
						if(opt==0 && menuMode){
							menuMode=false;
							transition=true;
							opt=1;
						}
						else if(opt!=0 && menuMode){
							parent.menuSelection=opt; 
							parent.respond(); 
						}else{
							parseOptions();
							parent.menuSelection=0;
							parent.respond();
						}
					break;
				case KeyEvent.VK_ESCAPE: case KeyEvent.VK_X:
					if (parent.go.SFX) {new SoundEffect(1).playToStop();}
					if(transition || rTransition){}
					else{
						opt=0;
						if(!menuMode && !transition && !rTransition){
							menuMode=true;
							rTransition=true;
							opt=0;
						}else
							opt=maxOption; break;
					}
				}
			}
		});
		thisThread = new Thread(this);
		thisThread.start();
	}


	/**
	 * Updates the background image according to the screen resolution and game options selected
	 */
	public void updateBG(){
		float ratio = .75f;
		int newWidth = (int)((float)FullGame.HEIGHT/ratio);
		try{
			mountain=ImageIO.read(new File("img/menuimages/main/mountain" + (parent.go.tsun?"v2":"") + ".png"));
			mountain=resize(mountain, newWidth, FullGame.HEIGHT);
		}catch(IOException e){System.err.println("BG was not loaded successfully");}
	}

	/**
	 * Resizes and returns an image, given the original image, and new dimensions
	 * @param origImg
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	private Image resize(Image origImg, int newWidth, int newHeight){
		return origImg.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
	}

	/**
	 * Initializes all the images by reading them from the file and resizing them to the proper
	 * size, according to the screen resolution
	 */
	public synchronized void initImages(){
		float ratio = .5f;
		int newWidth = (int)((float)FullGame.HEIGHT/.75f);
		int diff = (FullGame.WIDTH-newWidth)/2;
		try{
			for(int i = 0; i <= maxOption; i++){
				images[i]=ImageIO.read(new File("img/menuimages/main/opt"+i+".png"));
				images[i]=resize(images[i], (int)(images[i].getWidth(null)*ratio), (int)(images[i].getHeight(null)*ratio));
				hilite[i]=ImageIO.read(new File("img/menuimages/main/hl"+i+".png"));
				hilite[i]=resize(hilite[i], (int)(hilite[i].getWidth(null)*ratio), (int)(hilite[i].getHeight(null)*ratio));
				jpimgs[i]=ImageIO.read(new File("img/menuimages/main/jp/opt"+i+".png"));
				jpimgs[i]=resize(jpimgs[i], (int)(jpimgs[i].getWidth(null)*ratio), (int)(jpimgs[i].getHeight(null)*ratio));
				jphils[i]=ImageIO.read(new File("img/menuimages/main/jp/hl"+i+".png"));
				jphils[i]=resize(jphils[i], (int)(jphils[i].getWidth(null)*ratio), (int)(jphils[i].getHeight(null)*ratio));
			}
			maxX=0;
			for(int i = 0; i < images.length; i++)
				if(images[i].getWidth(null) > maxX)
					maxX = (int)(images[i].getWidth(null));
			maxX+=2*(textX+diff);
			bgtsun=ImageIO.read(new File("img/menuimages/main/menu_bg01v2.png"));
			bgtsun=resize(bgtsun, newWidth, FullGame.HEIGHT);
			bg=ImageIO.read(new File("img/menuimages/main/menu_bg01.png"));
			bg=resize(bg, newWidth, FullGame.HEIGHT);
			pointer=ImageIO.read(new File("img/menuimages/main/pointer.png"));
			pointer=resize(pointer, (int)(pointer.getWidth(null)*ratio), (int)(pointer.getHeight(null)*ratio));
			mountain=ImageIO.read(new File("img/menuimages/main/mountain" + (parent.go.tsun?"v2":"") + ".png"));
			mountain=resize(mountain, newWidth, FullGame.HEIGHT);
			sky=ImageIO.read(new File("img/menuimages/main/skybg.png"));
			sky=resize(sky, newWidth, FullGame.HEIGHT);
			cloud=ImageIO.read(new File("img/menuimages/main/cloud.png"));
			cloud=resize(cloud, (int)(cloud.getWidth(null)*ratio), (int)(cloud.getHeight(null)*ratio));
			for(int i = 1; i < snow.length; i++){
				snow[i] = ImageIO.read(new File("img/menuimages/main/snow0.png"));
				snow[i] = resize(snow[i], i, i);
			}
			for(int i = 0; i < rMaxOption; i++){
				rImages[i]=ImageIO.read(new File("img/menuimages/modeselect/opt"+i+".png"));
				rImages[i]=resize(rImages[i], (int)(rImages[i].getWidth(null)*ratio), (int)(rImages[i].getHeight(null)*ratio));
				rHilite[i]=ImageIO.read(new File("img/menuimages/modeselect/hl"+i+".png"));
				rHilite[i]=resize(rHilite[i], (int)(rHilite[i].getWidth(null)*ratio), (int)(rHilite[i].getHeight(null)*ratio));
			}

		}catch(IOException e){
			System.err.println("Error: IOException in OptionMenu.java");
			e.printStackTrace();
		}

		//adding snowflakes
		for(int i = 0; i < numSnowflakes; i++){
			int xStart = (int)(Math.random()*((float)FullGame.HEIGHT/ratio)+diff);
			int xEnd = xStart-(int)(Math.random()*400+200);
			snowflakes.add(new Snow((float)(Math.random()*3 + 1), xStart, xEnd,  (float)(Math.random()*8)+5, ratio));
		}
		initFinish=true;
	}

	/**
	 * Double-buffers the canvas, and then calls render to draw
	 * the components of the menu.
	 * @param g
	 */
	public void paint(Graphics g){
		Image i=createImage(getWidth(), getHeight()); 
		if(initFinish)
			render((i.getGraphics()));
		g.drawImage(i,0,0,this);
	}

	/**
	 * Render method: draws the menu according to the user input and game option
	 * selections. Updates dynamically with varying amount of objects depending
	 * on game options. 
	 * @param g
	 */
	public void render(Graphics g){
		super.paint(g);
		float ratio = .75f;
		int newWidth = (int)((float)FullGame.HEIGHT/ratio);
		int diff = (FullGame.WIDTH-newWidth)/2;
		int cloudX=diff+frameCounter/2;
		if(parent.go.GFX){
			g.drawImage(sky, diff, 0, null);
			g.drawImage(cloud, cloudX, (int)(cloud.getHeight(null)*ratio-50), null);
			g.drawImage(mountain, diff, 0, null);
			g.setColor(Color.BLACK);

			for(Snow s : snowflakes)
				g.drawImage(snow[(int)s.size], (int)(s.x-s.size/2), (int)(s.y-s.size/2), null);
			if(cloudX>newWidth+diff)
				frameCounter=2*(diff-cloud.getWidth(null));
			g.fillRect(diff+newWidth, 0, diff, FullGame.HEIGHT);
			g.fillRect(0, 0, diff, FullGame.HEIGHT);
		}else
			g.drawImage((parent.go.tsun?bgtsun:bg), diff, 0, null);
		if(menuMode || transition || rTransition)
			drawMenu(g);
		if(!menuMode || transition || rTransition)
			drawModes(g);
	}

	/**
	 * Draws the menu if the menu is in state one. Only draws when not transitioning, and draws the
	 * selection according to user's keyboard inputs and game options.
	 * @param g
	 */
	public void drawMenu(Graphics g){
		float ratio = .75f;
		int newWidth = (int)((float)FullGame.HEIGHT/ratio);
		int diff = (FullGame.WIDTH-newWidth)/2;
		if(!(transition || rTransition))
			g.drawImage(!parent.go.language?hilite[opt]:jphils[opt], diff+textX+dropShadow-(int)transX, (int)(80+opt*textSpacing*ratio)+dropShadow, null);
		for(int i = 0; i <= maxOption; i++)
			g.drawImage(!parent.go.language?images[i]:jpimgs[i], diff+textX-(i==0?0:(int)transX), (int)(80+i*textSpacing*ratio), null);
		if(!(transition || rTransition))
			g.drawImage(pointer, diff+textX-pointer.getWidth(null)-5, (int)(80+opt*textSpacing*ratio)-20, null);
	}

	/**
	 * Draws the menu if the menu is in state two. Only draws when not transitioning, and draws the
	 * selection according to user's keyboard inputs and game options.
	 * @param g
	 */
	public void drawModes(Graphics g){
		float ratio = .75f;
		int newWidth = (int)((float)FullGame.HEIGHT/ratio);
		int diff = (FullGame.WIDTH-newWidth)/2;
		int modeIndent = 20;
		g.drawImage(images[0], diff+textX, 80, null);
		if(!(transition || rTransition))
			g.drawImage(rHilite[opt-1], (int)transX-diff+modeIndent+dropShadow, (int)(80+(1.1*(opt-1)+1)*textSpacing*ratio)+dropShadow, null);
		for(int i = 1; i <= rMaxOption; i++)
			g.drawImage(rImages[i-1], (int)transX-diff+modeIndent, (int)(80+(1.1*(i-1)+1)*textSpacing*ratio), null);
		if(!(transition || rTransition))
			g.drawImage(pointer, diff+textX-pointer.getWidth(null)+modeIndent, (int)(80+(1.1*(opt-1)+1)*textSpacing*ratio)-20, null);
	}


	/**
	 * Animates the menu by repainting every specified interval. Also sets the limits for
	 * the different transitions.
	 */
	public void run() {
		while(Thread.currentThread()==thisThread){
			repaint();
			if(transition){
				transX+=speed;
				if(transX>400){
					transition=false;
					transX=400;
				}
			}
			if(rTransition){
				transX-=speed;
				if(transX<0){
					rTransition=false;
					transX=0;
				}
			}
			for(Snow s: snowflakes)
				s.step();
			try{
				Thread.sleep(20);
				frameCounter++;
			}catch(Exception e){
				e.printStackTrace();
			}
		}		
	}

	/**
	 * Parses the options given the game mode.
	 */
	public void parseOptions(){
		switch(opt-1){
		case 0: parent.setMode(Mode.EASYMODO);break;
		case 1: parent.setMode(Mode.NORMAL); break;
		case 2: parent.setMode(Mode.HARD); break;
		case 3: parent.setMode(Mode.LUNATIC); break;
		}
	}
}