package edu.mbhs.madubozhi.touhou.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import edu.mbhs.madubozhi.touhou.audio.SoundEffect;


/**
 * Creates a character design menu, where the user can select a team of characters,
 * and customize both character's statistics with a set number of stat points.
 * @author Matt Du
 *
 */
public class CharacterDesignMenu extends JFrame implements Runnable{
	//class variables
	private static final long serialVersionUID = 8316587745294498781L;
	private Thread thisThread;
	private FullGame parent;	
	private Character mc, sc;

	//key constants
	private final int enter = KeyEvent.VK_ENTER;	private final int up = KeyEvent.VK_UP;		private final int down = KeyEvent.VK_DOWN;		private final int left = KeyEvent.VK_LEFT;
	private final int right = KeyEvent.VK_RIGHT;	private final int esc = KeyEvent.VK_ESCAPE;	private final int w = KeyEvent.VK_W;			private final int a = KeyEvent.VK_A;
	private final int s = KeyEvent.VK_S;			private final int d = KeyEvent.VK_D;		private final int z = KeyEvent.VK_Z;			private final int x = KeyEvent.VK_X;		private final int tab = KeyEvent.VK_TAB;
	
	//choice tracking variables
	private int menuCode = 1;
	private int m1x=0, m1xMax=3;
	private int m2x=1, m2xMax=1;
	private int m3x=0, m3y=0,m3yMax=4;
	private int dropshadow=3;
	private double size=2.5;

	//menu tracking variables
	private int fontSize = 100;
	private int pChar, pMenu, frameCounter=0, timeOut=20;
	private boolean cTrans, mTrans=true;

	//point reservoir
	private int reservoir = 15;

	//image graphics storage variables
	private Image bg, bgtsun, sakura;
	private Image [] characters = new Image [4];
	private Image [] numbers = new Image [11];
	private Image [] text = new Image [4];
	private Image [] labels = new Image[5];
	private Image [] hilite = new Image[4];
	private Image [] names = new Image[4];
	private Image [] jpnames = new Image[4];
	private Image arrowA, arrowB, arrowC, arrowD, arrowE, arrowF;

	//modification tracking arrays
	private int [] mcmods = new int[4];
	private int [] mcbase = new int[4];
	private int [] scmods = new int[4];
	private int [] scbase = new int[4];

	/**
	 * Constructor: creates a new character design menu, given the parent
	 * window. Handles all inputs with the handleInput method.
	 * @param parent
	 */
	public CharacterDesignMenu(FullGame parent){
		super();
		initImages();
		this.parent = parent;
		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - FullGame.WIDTH)/2, (Toolkit.getDefaultToolkit().getScreenSize().height - FullGame.HEIGHT)/2);
		setUndecorated(true);
		setSize(FullGame.WIDTH, FullGame.HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.BLACK);
		//invisible mouse
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Invisimouse"); 
		setCursor(blankCursor);
		addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e){
				if(!mTrans)
					handleInput(e.getKeyCode());
			}
		});
		thisThread = new Thread(this);
		thisThread.start();
	}

	/**
	 * Renders and double-buffers the window
	 * @param g
	 */
	public void paint(Graphics g){
		Image i=createImage(getWidth(), getHeight()); 
		render(i.getGraphics());
		g.drawImage(i,0,0,this);
	}

	/**
	 * Divides the rendering jobs into different methods and calls the appropriate
	 * one according to the user inputs
	 * @param g
	 */
	public void render(Graphics g){
		super.paint(g);
		float ratio = (float)bg.getHeight(null)/(float)bg.getWidth(null);
		int newWidth = (int)((float)FullGame.HEIGHT/ratio);
		int diff = (FullGame.WIDTH-newWidth)/2;
		g.drawImage((parent.go.tsun?bgtsun:bg), diff, 0, null);
		g.setColor(new Color(0, 0, 0, 200));
		g.fillRect(0, 0, FullGame.WIDTH, FullGame.HEIGHT);
		if(mTrans)
			calcTrans(g);
		else
			//switch the menuCode and draws the appropriate menu
			switch(menuCode){
			case 1:
				drawMenuOne(g, 0);
				break;
			case 2:
				drawMenuTwo(g, 0);
				break;
			case 3:
				drawMenuThree(g, 0);
				break;
			}
		g.setColor(Color.black);
		g.fillRect(diff+newWidth, 0, diff, FullGame.HEIGHT);
		g.fillRect(0, 0, diff, FullGame.HEIGHT);
	}

	/**
	 * First transition method transitions from between menus one and two. 
	 * Called by calcTrans method
	 * @param g
	 */
	public void transOne(Graphics g){
		float ratio = (float)bg.getHeight(null)/(float)bg.getWidth(null);
		int newWidth = (int)((float)FullGame.HEIGHT/ratio);
		float speed = newWidth/timeOut;
		if(pMenu==1){
			drawMenuOne(g, -(int)speed*frameCounter);
			drawMenuTwo(g, -(int)speed*frameCounter+newWidth);
		}else{
			drawMenuTwo(g, (int)speed*frameCounter);
			drawMenuOne(g, (int)speed*frameCounter-newWidth);
		}
	}

	/**
	 * Second transition method transitions between menus two and three
	 * Called by calcTrans method
	 * @param g
	 */
	public void transTwo(Graphics g){
		float ratio = (float)bg.getHeight(null)/(float)bg.getWidth(null);
		int newWidth = (int)((float)FullGame.HEIGHT/ratio);
		float speed = newWidth/timeOut;
		if(pMenu==2){
			drawMenuTwo(g, -(int)speed*frameCounter);
			drawMenuThree(g, -(int)speed*frameCounter+newWidth);
		}else{
			drawMenuThree(g, (int)speed*frameCounter);
			drawMenuTwo(g, (int)speed*frameCounter-newWidth);
		}
	}

	/**
	 * Draws the first menu, where the user selects the primary character
	 * to be used in the game
	 * @param g
	 * @param x
	 */
	public void drawMenuOne(Graphics g, int x){
		float ratio = (float)bg.getHeight(null)/(float)bg.getWidth(null);
		int newWidth = (int)((float)FullGame.HEIGHT/ratio);
		int diff = (FullGame.WIDTH-newWidth)/2;
		int gap = (int)(130.0*ratio);
		int baseY = FullGame.HEIGHT/4+30;
		g.drawImage(labels[2], x+FullGame.WIDTH/2-labels[2].getWidth(null)/2, FullGame.HEIGHT/4-gap, null);
		g.drawImage(labels[4], x+FullGame.WIDTH/2-labels[2].getWidth(null)/2-labels[4].getWidth(null)-15, FullGame.HEIGHT/4-gap-3, null);

		g.drawImage(characters[m1x], x+FullGame.WIDTH/2-characters[m1x].getWidth(null), FullGame.HEIGHT/4, null);
		g.drawImage(names[m1x], x+FullGame.WIDTH/2-40, baseY, null);
		g.drawImage(arrowE, (int)(1.2*diff), FullGame.HEIGHT/2-arrowE.getHeight(null)/2, null);
		g.drawImage(arrowF, FullGame.WIDTH-(int)(1.2*diff)-arrowF.getWidth(null), FullGame.HEIGHT/2-arrowF.getHeight(null)/2, null);
		for(int i = 0; i < text.length; i++){
			g.drawImage(resize(text[i], (int)(text[i].getWidth(null)*.8), (int)(text[i].getHeight(null)*.8)), x+FullGame.WIDTH/2-40, baseY+i*gap+gap, null);
			for(int j = 0; j < getCharacter(m1x).getStat(i); j++)
				g.drawImage(sakura, x+FullGame.WIDTH/2-20 + text[0].getWidth(null) + j*(sakura.getWidth(null)+(sakura.getWidth(null)/4)), baseY+i*gap+gap, null);
		}
	}

	/**
	 * Draws the second menu, where the user selects the secondary character
	 * to be used alongside the first character. The second character is the opposite
	 * gender of the first character
	 * @param g
	 * @param x
	 */
	public void drawMenuTwo(Graphics g, int x){
		float ratio = (float)bg.getHeight(null)/(float)bg.getWidth(null);
		int newWidth = (int)((float)FullGame.HEIGHT/ratio);
		int diff = (FullGame.WIDTH-newWidth)/2;
		int gap = (int)(130.0*ratio);
		int baseY = FullGame.HEIGHT/4+30;
		g.drawImage(labels[3], x+FullGame.WIDTH/2-labels[3].getWidth(null)/2, FullGame.HEIGHT/4-gap, null);
		g.drawImage(labels[4], x+FullGame.WIDTH/2-labels[3].getWidth(null)/2-labels[4].getWidth(null)-15, FullGame.HEIGHT/4-gap-3, null);
		g.drawImage(characters[m2x+(mc.gender?2:0)], x+FullGame.WIDTH/2-characters[m2x+(mc.gender?2:0)].getWidth(null), FullGame.HEIGHT/4, null);
		g.drawImage(names[m2x+(mc.gender?2:0)], x+FullGame.WIDTH/2-40, baseY, null);
		if(!(mTrans && pMenu+menuCode==3)){
			g.drawImage(arrowE, x+(int)(1.2*diff), FullGame.HEIGHT/2-arrowE.getHeight(null)/2, null);
			g.drawImage(arrowF, x+FullGame.WIDTH-(int)(1.2*diff)-arrowF.getWidth(null), FullGame.HEIGHT/2-arrowF.getHeight(null)/2, null);
		}
		for(int i = 0; i < text.length; i++){
			g.drawImage(resize(text[i], (int)(text[i].getWidth(null)*.8), (int)(text[i].getHeight(null)*.8)), x+FullGame.WIDTH/2-40, baseY+i*gap+gap, null);
			for(int j = 0; j < getCharacter(m2x+(mc.gender?2:0)).getStat(i); j++)
				g.drawImage(sakura, x+FullGame.WIDTH/2-20 + text[0].getWidth(null) + j*(sakura.getWidth(null)+(sakura.getWidth(null)/4)), baseY+i*gap+gap, null);
		}
	}

	/**
	 * Draws the stat selection string, where the user can adjust the statistics of the previously
	 * selected characters in preparation for the game. The screen is divided into two parts, and the
	 * user can use the arrow keys to modify each statistic individually before continuing
	 * @param g
	 * @param x
	 */
	public void drawMenuThree(Graphics g, int x){
		g.setColor(Color.WHITE);
		float ratio = (float)bg.getHeight(null)/(float)bg.getWidth(null);
		int newWidth = (int)((float)FullGame.HEIGHT/ratio);
		int diff = (FullGame.WIDTH-newWidth)/2;
		int gap = (int)(120.0*ratio);
		int indent = (FullGame.WIDTH/2+(diff+text[0].getWidth(null)))/2;
		g.setFont(new Font("Just The Way You Are", Font.ITALIC, (int)(fontSize*.6f)));
		int stringLength = g.getFontMetrics().stringWidth("Select Mods");
		g.drawString("Select Mods", x+FullGame.WIDTH/2 - stringLength/2, (int)(fontSize*.6f));
		g.drawLine(x+FullGame.WIDTH/2, FullGame.HEIGHT/4+20, x+FullGame.WIDTH/2, FullGame.HEIGHT);
		drawNumber(reservoir, x+FullGame.WIDTH/2, FullGame.HEIGHT/4, g);
		g.drawImage(labels[2], x+40+diff, FullGame.HEIGHT/4-gap, null);
		g.drawImage(labels[3], x+FullGame.WIDTH-diff-labels[3].getWidth(null)-40, FullGame.HEIGHT/4-gap, null);
		g.drawImage(names[m1x], x+40+diff, FullGame.HEIGHT/4, null);
		g.drawImage(names[m2x+(mc.gender?2:0)], x+FullGame.WIDTH-diff-names[m2x+(mc.gender?2:0)].getWidth(null)-40, FullGame.HEIGHT/4, null);
		g.setColor(new Color(255, 200, 200, 110));
		if(m3y!=4)
			g.fillRoundRect(x+((m3x/2)*newWidth/2 + (int)(.8*indent))-40, m3y*gap+FullGame.HEIGHT/4+gap-(numbers[0].getHeight(null)-arrowA.getHeight(null))/2, ((int)(1.2*indent) + arrowA.getWidth(null))-((int)(.8*indent)) + 80, numbers[0].getHeight(null), numbers[0].getHeight(null)/2, numbers[0].getHeight(null)/2);
		if(m3y==4)
			if(m3x>1)	g.drawImage(hilite[3], x+FullGame.WIDTH/2+hilite[2].getWidth(null)/2+dropshadow, 4*gap+FullGame.HEIGHT/4+gap+dropshadow, null);
			else		g.drawImage(hilite[2], x+FullGame.WIDTH/2-hilite[3].getWidth(null)+dropshadow, 4*gap+FullGame.HEIGHT/4+gap+dropshadow, null);
		for(int i = 0; i < 2; i++)
			for(int j = 0; j < 4; j++){				
				g.drawImage(text[j], x+(i*(newWidth/2))+40+diff, (j*gap)+(FullGame.HEIGHT/4)+gap, null);
				g.drawImage(numbers[(i==0?(mcmods[j]+mcbase[j]):(scmods[j]+scbase[j]))], x+i*newWidth/2+indent, (j*gap)+(FullGame.HEIGHT/4)+gap, null);
				g.drawImage((i==0?(mcmods[j]==0?arrowC:arrowA):(scmods[j]==0?arrowC:arrowA)), x+(i*newWidth/2 + (int)(.8*indent)), j*gap+FullGame.HEIGHT/4+gap, null);
				g.drawImage((i==0?(mcbase[j]+mcmods[j]==10?arrowD:arrowB):(scbase[j]+scmods[j]==10?arrowD:arrowB)), x+(i*newWidth/2 + (int)(1.2*indent)), j*gap+FullGame.HEIGHT/4+gap, null);
			}
		g.drawImage(labels[0], x+FullGame.WIDTH/2 - labels[1].getWidth(null), 4*gap+FullGame.HEIGHT/4+gap, null);
		g.drawImage(labels[1], x+FullGame.WIDTH/2+labels[0].getWidth(null)/2, 4*gap+FullGame.HEIGHT/4+gap, null);
	}

	/**
	 * Divides the handling of the user input into 3 different methods, 
	 * according to which menu the user is on
	 * @param keyCode
	 */
	public void handleInput(int keyCode){
		switch(menuCode){
		case 1: oneInput(keyCode); break;
		case 2: twoInput(keyCode); break;
		case 3: threeInput(keyCode); break;
		}
	}

	/**
	 * Resets the base statistics of the character, given which character
	 * to reset.
	 * @param isMC
	 */
	public void reset(boolean isMC){
		int readd = 0;
		if(isMC)
			for(int i = 0; i < mcmods.length; i++){
				readd+=mcmods[i];
				mcmods[i]=0;
			}
		else
			for(int i = 0; i < scmods.length; i++){
				readd+=scmods[i];
				scmods[i]=0;
			}
		reservoir+=readd;
	}

	/**
	 * Handles the input for menu one. Left and right changes which character
	 * is selected, enter proceeds, and escape returns to the main menu
	 * @param keyCode
	 */
	private void oneInput(int keyCode){
		switch(keyCode){
		case enter: case z:
			if (parent.go.SFX) {new SoundEffect(0).playToStop();}
			mc = getCharacter(m1x);
			pMenu = menuCode;
			menuCode++;
			mTrans=true;
			initBaseStats(mc, 0);
			reset(true);
			break;
		case a: case left: case up: case w:
			m1x=(m1x-1<0?m1xMax:m1x-1);
			break;
		case d: case right: case down: case s:
			m1x=(m1x+1>m1xMax?0:m1x+1);
			break;
		case esc: case x:
			if (parent.go.SFX) {new SoundEffect(1).playToStop();}
			if(parent==null)
				System.exit(0);
			parent.respond();
			break;
		}
	}

	/**
	 * Handles the input for menu two. Left and right changes which character
	 * is selected, enter proceeds to menu three, and escape returns to menu one
	 * @param keyCode
	 */
	private void twoInput(int keyCode){
		int add;
		if(mc.gender)	add=2;else add=0;
		switch(keyCode){
		case enter: case z:
			if (parent.go.SFX) {new SoundEffect(0).playToStop();}
			sc = getCharacter(m2x+add);
			pMenu = menuCode;
			menuCode++;
			mTrans=true;
			initBaseStats(sc, 1);
			reset(false);
			break;
		case a: case left: case up: case w:
			m2x=(m2x-1<0?m2xMax:m2x-1);
			break;
		case d: case right: case down: case s:
			m2x=(m2x+1>m2xMax?0:m2x+1);
			break;
		case esc: case x:
			if (parent.go.SFX) {new SoundEffect(1).playToStop();}
			pMenu=menuCode;
			menuCode--;
			mTrans=true;
			break;
		}
	}
	
	/**
	 * Handles the input for menu three. Left, right, up, and down move the cursor
	 * to different statistics, enter switches to the next column or proceeds, depending
	 * on the cursor location, and escape returns to menu two.
	 * @param keyCode
	 */
	private void threeInput(int keyCode){
		switch(keyCode){
		case enter:	case z:
			if (parent.go.SFX) {new SoundEffect(0).playToStop();}
			if(m3y == 4 && m3x>1){
				if(parent==null)
					System.exit(0);
				parse(); 
				parent.resevoir=reservoir;
				parent.respond();
			}else if(m3y==4 && m3x<2){reset(true); reset(false);}
		case tab:
			if(m3y<4)
				m3x=(m3x+2)%4;
			break;
		case a:case left:
			if(m3y==4)	m3x=(m3x+2)%4;
			else		calculateMods(m3x, m3y);
			break;
		case w:case up:
			m3y=(m3y-1<0?m3yMax:m3y-1);
			break;
		case d:case right:
			if(m3y==4)	m3x=(m3x+2)%4;
			else		calculateMods(m3x+1, m3y);
			break;
		case s:case down:
			m3y=(m3y+1>m3yMax?0:m3y+1);
			break;
		case esc: case x:
			if (parent.go.SFX) {new SoundEffect(1).playToStop();}
			pMenu=menuCode;
			menuCode--;
			mTrans=true;
		}
	}

	/**
	 * Calculates which transition to call and calls the corresponding method
	 * @param g
	 */
	public void calcTrans(Graphics g){
		switch(menuCode+pMenu){
		case 3:	transOne(g); break;
		case 5: transTwo(g); break;
		}
	}

	/**
	 * Initializes all the image graphics from the predetermined file
	 */
	private synchronized void initImages(){
		float ratio = 0.45f;
		int newWidth = (int)((float)FullGame.HEIGHT/.75f);
		//int diff = (FullGame.WIDTH-newWidth)/2;
		try{
			bg = ImageIO.read(new File("img/menuimages/main/menu_bg01.png"));
			bg = resize(bg, newWidth, FullGame.HEIGHT);
			bgtsun = ImageIO.read(new File("img/menuimages/main/menu_bg01v2.png"));
			bgtsun = resize(bgtsun, newWidth, FullGame.HEIGHT);
			for(int i = 0; i < 4; i++){
				characters[i]=ImageIO.read(new File("img/menuimages/charactermenu/char"+i+".png"));
				characters[i]=resize(characters[i], (int)(characters[i].getWidth(null)), (int)(characters[i].getHeight(null)));
				text[i]=ImageIO.read(new File("img/menuimages/charactermenu/stat"+i+".png"));
				text[i]=resize(text[i], (int)(text[i].getWidth(null)*ratio), (int)(text[i].getHeight(null)*ratio));
				hilite[i]=ImageIO.read(new File("img/menuimages/charactermenu/hl"+i+".png"));
				hilite[i]=resize(hilite[i], (int)(hilite[i].getWidth(null)*ratio), (int)(hilite[i].getHeight(null)*ratio));
				names[i]=ImageIO.read(new File("img/menuimages/charactermenu/name"+i+".png"));
				names[i]=resize(names[i], (int)(names[i].getWidth(null)*ratio), (int)(names[i].getHeight(null)*ratio));
				jpnames[i]=ImageIO.read(new File("img/menuimages/charactermenu/jpname"+i+".png"));
				jpnames[i]=resize(jpnames[i], (int)(jpnames[i].getWidth(null)*ratio), (int)(jpnames[i].getHeight(null)*ratio));
			}
			for(int i = 0; i < 11; i++){
				numbers[i]=ImageIO.read(new File("img/menuimages/charactermenu/"+i+".png"));
				numbers[i]=resize(numbers[i], (int)(numbers[i].getWidth(null)*ratio), (int)(numbers[i].getHeight(null)*ratio));
			}
			for(int i = 0; i < labels.length; i++){
				labels[i]=ImageIO.read(new File("img/menuimages/charactermenu/label"+i+".png"));
				labels[i]=resize(labels[i], (int)(labels[i].getWidth(null)*ratio), (int)(labels[i].getHeight(null)*ratio));
			}
			arrowA=ImageIO.read(new File("img/menuimages/charactermenu/left.png"));
			arrowA=resize(arrowA, (int)(arrowA.getWidth(null)*ratio), (int)(arrowA.getHeight(null)*ratio));
			arrowB=ImageIO.read(new File("img/menuimages/charactermenu/right.png"));
			arrowB=resize(arrowB, (int)(arrowB.getWidth(null)*ratio), (int)(arrowB.getHeight(null)*ratio));
			arrowC=ImageIO.read(new File("img/menuimages/charactermenu/xleft.png"));
			arrowC=resize(arrowC, (int)(arrowC.getWidth(null)*ratio), (int)(arrowC.getHeight(null)*ratio));
			arrowD=ImageIO.read(new File("img/menuimages/charactermenu/xright.png"));
			arrowD=resize(arrowD, (int)(arrowD.getWidth(null)*ratio), (int)(arrowD.getHeight(null)*ratio));
			arrowE=ImageIO.read(new File("img/menuimages/charactermenu/arrowE.png"));
			arrowE=resize(arrowE, (int)(arrowE.getWidth(null)*ratio/2), (int)(arrowE.getHeight(null)*ratio/2));
			arrowF=ImageIO.read(new File("img/menuimages/charactermenu/arrowF.png"));
			arrowF=resize(arrowF, (int)(arrowF.getWidth(null)*ratio/2), (int)(arrowF.getHeight(null)*ratio/2));
			sakura=ImageIO.read(new File("img/menuimages/charactermenu/sakura.png"));
			sakura=resize(sakura, (int)(sakura.getWidth(null)*ratio), (int)(sakura.getHeight(null)*ratio));
		}catch(IOException e){
			System.err.println("Error reading images in Character Design Menu");
			e.printStackTrace();
		}
	}

	/**
	 * Returns a character, given the character code
	 * @param charCode
	 * @return
	 */
	private Character getCharacter(int charCode){
		switch(charCode){
		case 0: return Character.ASAMI;
		case 1: return Character.MIZUKI;
		case 2: return Character.JIN;
		case 3: return Character.KAITO;
		default: return Character.UNDEFINED;
		}
	}

	
	/**
	 * Initializes a character's base statistics, given the character
	 * and the character code
	 * @param c
	 * @param code
	 */
	private void initBaseStats(Character c, int code){
		if(code == 0){
			mcbase[0] = (int)c.getBaseStrength();
			mcbase[1] = (int)c.getBaseSpeed();
			mcbase[2] = (int)c.getBaseHitbox();
			mcbase[3] = (int)c.getBaseCharisma();
		}
		else{
			scbase[0] = (int)c.getBaseStrength();
			scbase[1] = (int)c.getBaseSpeed();
			scbase[2] = (int)c.getBaseHitbox();
			scbase[3] = (int)c.getBaseCharisma();
		}
	}

	/**
	 * Calculates modifications to statistics, given the user inputs
	 * @param x
	 * @param y
	 */
	private void calculateMods(int x, int y){
		switch(x){
		case 0:
			if(mcmods[y]>0){
				mcmods[y]--;
				reservoir++;
			}break;
		case 1:
			if(mcmods[y]+mcbase[y]<10 && reservoir > 0){
				mcmods[y]++;
				reservoir--;
			}break;
		case 2:
			if(scmods[y]>0){
				scmods[y]--;
				reservoir++;
			}break;
		case 3:
			if(scmods[y]+scbase[y]<10 && reservoir >0){
				scmods[y]++;
				reservoir--;
			}
		}
	}
	
	/**
	 * Draws a number, using the digit images, given the x and y to
	 * draw the number at
	 * @param num
	 * @param xCent
	 * @param y
	 * @param g
	 */
	private void drawNumber(int num, int xCent, int y, Graphics g){
		int ten = (int)(num/10);
		int one = num-(ten*10);
		int width = numbers[ten].getWidth(null)+numbers[one].getWidth(null)+10;
		g.drawImage(numbers[ten], xCent-width/2, y, null);
		g.drawImage(numbers[one], xCent-width/2+numbers[ten].getWidth(null)+10, y, null);
	}

	/**
	 * Sets the game stats to match the selections in the character menu
	 */
	private void parse(){
		mc.setMods(mcmods[0], mcmods[1], mcmods[2], mcmods[3]);
		sc.setMods(scmods[0], scmods[1], scmods[2], scmods[3]);
		parent.setChar(mc, 0);
		parent.setChar(sc, 1);
	}

	/**
	 * Animates the menu and the transitions and 20ms between frames
	 */
	public void run() {
		while(Thread.currentThread()==thisThread){
			if(mTrans){
				frameCounter++;
				if(frameCounter>timeOut){
					mTrans=false;
					frameCounter=0;
				}			
			}
			repaint();
			try{
				Thread.sleep(20);
			}catch(Exception e){
				e.printStackTrace();
			}
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
}