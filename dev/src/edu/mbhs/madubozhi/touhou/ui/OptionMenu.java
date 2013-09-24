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


/**
 * Creates a menu that allows the user to change different options of the game,
 * for both the game and the novel sections.
 * @author Matt Du
 *
 */
public class OptionMenu extends JFrame implements Runnable{
	//class variables
	private static final long serialVersionUID = 512060905812821507L;
	private Thread thisThread;
	private FullGame parent;
	private static String DEFAULT_TITLE = "Tenryuu Project: Main Menu";
	
	//option tracking variables
	private int optX=1, optY=0;
	private int maxOption=6;
	private int[]choices;
	private int[] choiceMax = {2, 2, 2, 2, 2, 2, 4};
	
	//option variables
	private boolean SFX, GFX, BGM, voice, japanese, tsun;
	private int textSpeed;

	//image graphics storage
	private Image on, off, onhl, offhl, jp, en, jphl, enhl, spd1, spd2, spd3, spd4, spd1hl, spd2hl, spd3hl, spd4hl;
	private Image jpon, jpoff, jponhl, jpoffhl, jpjp, jpen, jpjphl, jpenhl, jpspd4, jpspd4hl;
	private Image[] labels = new Image[maxOption+1];
	private Image[] jplbls = new Image[maxOption+1];
	private Image bg, pointer, bgtsun;

	//text rendering variables
	private int labelWidth=0, jpLabelWdth=0;
	private int textX = -50;
	private int textSpacing = 65;
	private int dropShadow = 2;

	//snow graphics storage
	private Image[] snow = new Image[4];
	private ArrayList<Snow> snowflakes = new ArrayList<Snow>();
	private int numSnowflakes = 300;

	/**
	 * Constructor: creates a new menu given the parent window and the set of currently enabled
	 * game options. Contains a keyListener that adjusts different options dynamically
	 * @param par
	 * @param go
	 */
	public OptionMenu(FullGame par, GameOptions go){
		super(DEFAULT_TITLE);
		initImages();
		this.parent = par;	SFX=go.SFX;	GFX=go.GFX;	BGM=go.BGM;	voice=go.voice;	textSpeed=go.textSpeed;
		choices = new int[]{(BGM?1:2), (SFX?1:2), (voice?1:2), (GFX?1:2), (tsun?1:2), (japanese?1:2), ((textSpeed+1)/2), 1234};
		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - FullGame.WIDTH)/2, (Toolkit.getDefaultToolkit().getScreenSize().height - FullGame.HEIGHT)/2);
		setUndecorated(true);
		setSize(FullGame.WIDTH, FullGame.HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.BLACK);
		//invisible mouse
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Invisimouse"); 
		setCursor(blankCursor);
		
		//key listener adjusts options according to current status
		addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e){
				switch(e.getKeyCode()){
				case KeyEvent.VK_LEFT:	case KeyEvent.VK_A:
					if(optX>1)
						optX-- ;
					break;
				case KeyEvent.VK_W:	case KeyEvent.VK_UP: 
					optY--;
					if(optY<0)
						optY=maxOption;
					if(optX > choiceMax[optY])
						optX = choiceMax[optY];
					break;
				case KeyEvent.VK_RIGHT:	case KeyEvent.VK_S:	
					if(optX<choiceMax[optY])
						optX++;
					break;
				case KeyEvent.VK_D:	case KeyEvent.VK_DOWN: 	
					optY++; 
					if(optY>maxOption)
						optY=0;
					if(optY==maxOption && optX==2)
						optX=3;
					if(optX > choiceMax[optY])
						optX = choiceMax[optY];
					break;
				case KeyEvent.VK_SPACE:	case KeyEvent.VK_ENTER: case KeyEvent.VK_Z: 
					if (SFX) {new SoundEffect(0).playToStop();}
					parse();
					break;
				case KeyEvent.VK_ESCAPE: case KeyEvent.VK_X: 
					if (SFX) {new SoundEffect(1).playToStop();}
					parent.respond();
					break;
				}
			}
		});

		thisThread = new Thread(this);
		thisThread.start();
	}

	/**
	 * Parses the current options and adjusts them dynamically when the user
	 * changes any option.
	 */
	public void parse(){
		switch(optY){
		case 0:
			if(optX==1){BGM=true;
			if (!parent.mplayer.isPlaying()){
				parent.mplayer.changeAudio(new File("audio/bgm/title music.mp3"));
			}}
			if(optX==2){BGM=false;
			if (parent.mplayer.isPlaying()){
				parent.mplayer.changeAudio(null);
			}}
			break;
		case 1:
			if(optX==1) SFX=true;
			if(optX==2) SFX=false;
			break;
		case 2:
			if(optX==1) voice=true;
			if(optX==2) voice=false;
			break;
		case 3:
			if(optX==1) GFX=true;
			if(optX==2) GFX=false;
			break;
		case 4:
			if(optX==1) tsun=true;
			if(optX==2) tsun=false;
			break;
		case 5:
			if(optX==1) japanese=true;
			if(optX==2) japanese=false;
			break;
		case 6: 
			if(optX==1) textSpeed=1;
			if(optX==2) textSpeed=3;
			if(optX==3) textSpeed=5;
			if(optX==4) textSpeed=7;
			break;
		}
		choices = new int[]{(BGM?1:2), (SFX?1:2), (voice?1:2), (GFX?1:2), (tsun?1:2), (japanese?1:2), ((textSpeed+1)/2), 1234};
		parent.go=new GameOptions(BGM, SFX, voice, GFX, textSpeed, japanese, tsun);
		if(optY==3 || optY==4)
			parent.updateMenuArt(0);
		parent.optionUpdate();
	}

	/**
	 * Initializes the images from the predetermined directories
	 */
	public synchronized void initImages(){
		float ratio = 0.5f;
		int newWidth = (int)((float)FullGame.HEIGHT/.75f);
		int diff = (FullGame.WIDTH-newWidth)/2;
		try{
			bg = ImageIO.read(new File("img/menuimages/optionmenu/bg_01.png"));
			bg = resize(bg, newWidth, FullGame.HEIGHT);
			bgtsun = ImageIO.read(new File("img/menuimages/optionmenu/bg_01v2.png"));
			bgtsun = resize(bgtsun, newWidth, FullGame.HEIGHT);
			pointer = ImageIO.read(new File("img/menuimages/optionmenu/pointer.png"));
			pointer = resize(pointer, (int)(pointer.getWidth(null)*ratio), (int)(pointer.getHeight(null)*ratio));

			on = ImageIO.read(new File("img/menuimages/optionmenu/on.png"));
			on = resize(on, (int)(on.getWidth(null)*ratio), (int)(on.getHeight(null)*ratio));
			jpon = ImageIO.read(new File("img/menuimages/optionmenu/jp/on.png"));
			jpon = resize(jpon, (int)(jpon.getWidth(null)*ratio), (int)(jpon.getHeight(null)*ratio));
			off = ImageIO.read(new File("img/menuimages/optionmenu/off.png"));
			off = resize(off, (int)(off.getWidth(null)*ratio), (int)(off.getHeight(null)*ratio));
			jpoff = ImageIO.read(new File("img/menuimages/optionmenu/jp/off.png"));
			jpoff = resize(jpoff, (int)(jpoff.getWidth(null)*ratio), (int)(jpoff.getHeight(null)*ratio));
			jp = ImageIO.read(new File("img/menuimages/optionmenu/jp.png"));
			jp = resize(jp, (int)(jp.getWidth(null)*ratio), (int)(jp.getHeight(null)*ratio));
			jpjp = ImageIO.read(new File("img/menuimages/optionmenu/jp/jp.png"));
			jpjp = resize(jpjp, (int)(jpjp.getWidth(null)*ratio), (int)(jpjp.getHeight(null)*ratio));
			en = ImageIO.read(new File("img/menuimages/optionmenu/en.png"));
			en = resize(en, (int)(en.getWidth(null)*ratio), (int)(en.getHeight(null)*ratio));
			jpen = ImageIO.read(new File("img/menuimages/optionmenu/jp/en.png"));
			jpen = resize(jpen, (int)(jpen.getWidth(null)*ratio), (int)(jpen.getHeight(null)*ratio));
			spd1 = ImageIO.read(new File("img/menuimages/optionmenu/spd1.png"));
			spd1 = resize(spd1, (int)(spd1.getWidth(null)*ratio), (int)(spd1.getHeight(null)*ratio));
			spd2 = ImageIO.read(new File("img/menuimages/optionmenu/spd2.png"));
			spd2 = resize(spd2, (int)(spd2.getWidth(null)*ratio), (int)(spd2.getHeight(null)*ratio));
			spd3 = ImageIO.read(new File("img/menuimages/optionmenu/spd3.png"));
			spd3 = resize(spd3, (int)(spd3.getWidth(null)*ratio), (int)(spd3.getHeight(null)*ratio));
			spd4 = ImageIO.read(new File("img/menuimages/optionmenu/spd4.png"));
			spd4 = resize(spd4, (int)(spd4.getWidth(null)*ratio), (int)(spd4.getHeight(null)*ratio));
			jpspd4 = ImageIO.read(new File("img/menuimages/optionmenu/jp/spd4.png"));
			jpspd4 = resize(jpspd4, (int)(jpspd4.getWidth(null)*ratio), (int)(jpspd4.getHeight(null)*ratio));

			onhl = ImageIO.read(new File("img/menuimages/optionmenu/onhl.png"));
			onhl = resize(onhl, (int)(onhl.getWidth(null)*ratio), (int)(onhl.getHeight(null)*ratio));
			jponhl = ImageIO.read(new File("img/menuimages/optionmenu/jp/onhl.png"));
			jponhl = resize(jponhl, (int)(jponhl.getWidth(null)*ratio), (int)(jponhl.getHeight(null)*ratio));
			offhl = ImageIO.read(new File("img/menuimages/optionmenu/offhl.png"));
			offhl = resize(offhl, (int)(offhl.getWidth(null)*ratio), (int)(offhl.getHeight(null)*ratio));
			jpoffhl = ImageIO.read(new File("img/menuimages/optionmenu/jp/offhl.png"));
			jpoffhl = resize(jpoffhl, (int)(jpoffhl.getWidth(null)*ratio), (int)(jpoffhl.getHeight(null)*ratio));
			jphl = ImageIO.read(new File("img/menuimages/optionmenu/jphl.png"));
			jphl = resize(jphl, (int)(jphl.getWidth(null)*ratio), (int)(jphl.getHeight(null)*ratio));
			jpjphl = ImageIO.read(new File("img/menuimages/optionmenu/jp/jphl.png"));
			jpjphl = resize(jpjphl, (int)(jpjphl.getWidth(null)*ratio), (int)(jpjphl.getHeight(null)*ratio));
			enhl = ImageIO.read(new File("img/menuimages/optionmenu/enhl.png"));
			enhl = resize(enhl, (int)(enhl.getWidth(null)*ratio), (int)(enhl.getHeight(null)*ratio));
			jpenhl = ImageIO.read(new File("img/menuimages/optionmenu/jp/enhl.png"));
			jpenhl = resize(jpenhl, (int)(jpenhl.getWidth(null)*ratio), (int)(jpenhl.getHeight(null)*ratio));
			spd1hl = ImageIO.read(new File("img/menuimages/optionmenu/spd1hl.png"));
			spd1hl = resize(spd1hl, (int)(spd1hl.getWidth(null)*ratio), (int)(spd1hl.getHeight(null)*ratio));
			spd2hl = ImageIO.read(new File("img/menuimages/optionmenu/spd2hl.png"));
			spd2hl = resize(spd2hl, (int)(spd2hl.getWidth(null)*ratio), (int)(spd2hl.getHeight(null)*ratio));
			spd3hl = ImageIO.read(new File("img/menuimages/optionmenu/spd3hl.png"));
			spd3hl = resize(spd3hl, (int)(spd3hl.getWidth(null)*ratio), (int)(spd3hl.getHeight(null)*ratio));
			spd4hl = ImageIO.read(new File("img/menuimages/optionmenu/spd4hl.png"));
			spd4hl = resize(spd4hl, (int)(spd4hl.getWidth(null)*ratio), (int)(spd4hl.getHeight(null)*ratio));
			jpspd4hl = ImageIO.read(new File("img/menuimages/optionmenu/jp/spd4hl.png"));
			jpspd4hl = resize(jpspd4hl, (int)(jpspd4hl.getWidth(null)*ratio), (int)(jpspd4hl.getHeight(null)*ratio));
			for(int i = 0; i <= maxOption; i++){
				labels[i] = ImageIO.read(new File("img/menuimages/optionmenu/label"+ (i+1) + ".png"));
				if(labels[i].getWidth(null)>labelWidth) labelWidth = labels[i].getWidth(null);
				labels[i]=resize(labels[i], (int)(labels[i].getWidth(null)*ratio), (int)(labels[i].getHeight(null)*ratio));
				
				jplbls[i] = ImageIO.read(new File("img/menuimages/optionmenu/jp/label"+ (i+1) + ".png"));
				if(jplbls[i].getWidth(null)>jpLabelWdth) jpLabelWdth = jplbls[i].getWidth(null);
				jplbls[i]=resize(jplbls[i], (int)(jplbls[i].getWidth(null)*ratio), (int)(jplbls[i].getHeight(null)*ratio));
			}
			jpLabelWdth+=(labelWidth-jpLabelWdth);
			for(int i = 1; i < snow.length; i++){
				snow[i] = ImageIO.read(new File("img/menuimages/main/snow0.png"));
				snow[i] = resize(snow[i], i, i);
			}

		}catch(IOException e){
			System.err.println("You made an IO Exception. OptionMenu image initializtion");
			e.printStackTrace();
			System.exit(0);
		}
		for(int i = 0; i < numSnowflakes; i++){
			int xStart = (int)(Math.random()*((float)FullGame.HEIGHT/ratio)+diff);
			int xEnd = xStart-(int)(Math.random()*400+200);			snowflakes.add(new Snow((float)(Math.random()*snow.length + 1), xStart, xEnd,  (float)(Math.random()*8)+5, ratio));
		}
	}

	/**
	 * Resizes and returns an image, given the original image and the new dimensions
	 * @param origImg
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	private Image resize(Image origImg, int newWidth, int newHeight){
		return origImg.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
	}

	/**
	 * Renders the OptionMenu, with varying degrees of graphics complication given the game
	 * options listed. Also draws the current selection and applies them with the input handler
	 * @param g
	 */
	public void render(Graphics g){
		super.paint(g);
		float ratio = (float)bg.getHeight(null)/(float)bg.getWidth(null);
		int newWidth = (int)((float)FullGame.HEIGHT/ratio);
		int diff = (FullGame.WIDTH-newWidth)/2;
		g.drawImage((tsun?bgtsun:bg), diff, 0, null);
		if(GFX){
			for(Snow s : snowflakes)
				g.drawImage(snow[(int)(s.size)-1], (int)(s.x-s.size/2), (int)(s.y-s.size/2), null);
			g.fillRect(diff+newWidth, 0, diff, FullGame.HEIGHT);
			g.fillRect(0, 0, diff, FullGame.HEIGHT);
		}
		for(int i = 0; i <= maxOption; i++)
			g.drawImage(parent.go.language?jplbls[i]:labels[i], diff+textX + ((parent.go.language?jpLabelWdth:labelWidth)-(int)((parent.go.language?jplbls[i]:labels[i]).getWidth(null))), (int)(80+i*textSpacing*ratio), null);

		//on off
		int c1x = 100, c2x = 220;
		for(int i = 0; i <= maxOption-2; i++){
			if(choices[i]==1)	g.drawImage(parent.go.language?jponhl:onhl, diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + c1x + dropShadow, (int)(80+i*textSpacing*ratio) + dropShadow, null);
			else g.drawImage(parent.go.language?jpoffhl:offhl, diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + c2x + dropShadow, (int)(80+i*textSpacing*ratio) + dropShadow, null);

			g.drawImage(parent.go.language?jpoff:off, diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + c2x, (int)(80+i*textSpacing*ratio), null);
			g.drawImage(parent.go.language?jpon:on, diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + c1x, (int)(80+i*textSpacing*ratio), null);
		}
		//jp en
		if(japanese)g.drawImage((parent.go.language?jpjphl:jphl), diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + c1x+2, (int)(80+(maxOption-1)*textSpacing*ratio)+dropShadow, null);
		else g.drawImage((parent.go.language?jpenhl:enhl), diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + c2x+2, (int)(80+(maxOption-1)*textSpacing*ratio)+dropShadow, null);

		g.drawImage((parent.go.language?en:en), diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + c2x, (int)(80+(maxOption-1)*textSpacing*ratio), null);
		g.drawImage((parent.go.language?jpjp:jpjp), diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + c1x, (int)(80+(maxOption-1)*textSpacing*ratio), null);

		int y = (int)(80+(maxOption)*textSpacing*ratio);
		if(textSpeed == 1)g.drawImage(spd1hl, diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + c1x + dropShadow, y+8+dropShadow, null);
		else if(textSpeed == 3)g.drawImage(spd2hl, diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + (c1x+c2x)/2 + dropShadow, y+8+dropShadow, null);
		else if(textSpeed == 5)g.drawImage(spd3hl, diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + c2x + dropShadow, y+8+dropShadow, null);
		else g.drawImage((parent.go.language?jpspd4hl:spd4hl), diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + c2x+(c2x-c1x)/2 + dropShadow, y+dropShadow, null);
		g.drawImage(spd1, diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + c1x, y+8, null);
		g.drawImage(spd2, diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + (c1x+c2x)/2, y+8, null);
		g.drawImage(spd3, diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + c2x, y+8, null);
		g.drawImage((parent.go.language?jpspd4:spd4), diff+textX + (parent.go.language?jpLabelWdth:labelWidth) + c2x+(c2x-c1x)/2, y, null);

		int pointerX= diff+textX + (parent.go.language?jpLabelWdth:labelWidth);
		if(optY!=maxOption) if(optX==1) pointerX+=c1x; else pointerX+=c2x;
		else if(optX==1) pointerX+=c1x; else if(optX==2) pointerX+=(c1x+c2x)/2; else if(optX==3) pointerX+=c2x; else pointerX+=c2x+(c2x-c1x)/2;
		pointerX-=((int)(pointer.getWidth(null))+7);
		g.drawImage(pointer, pointerX, (int)(60+optY*textSpacing*ratio), null);
	}

	/**
	 * Renders and double-buffers the image displayed
	 * @param g
	 */
	public void paint(Graphics g){
		Image i=createImage(getWidth(), getHeight());
		render((i.getGraphics()));
		g.drawImage(i,0,0,this);
	}

	/**
	 * Animates the image by constantly repainting the window at a preset rate.
	 */
	public void run() {
		while(Thread.currentThread()==thisThread){
			if(GFX)
				for(Snow s: snowflakes)
					s.step();
			repaint();
			try{
				Thread.sleep(20);
			}catch(Exception e){
				e.printStackTrace();
			}
		}		
	}
}