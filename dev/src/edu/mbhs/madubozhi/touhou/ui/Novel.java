package edu.mbhs.madubozhi.touhou.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.mbhs.madubozhi.touhou.audio.Music;


/**
 * A interface for a generic visual novel: has text animation, and full input parsing from
 * file, so no hard coding is needed. All input for file is preloaded in a text file, this
 * class only parses the text file and displays the resulting novel on the screen.
 * @author Matt Du
 *
 */
public class Novel extends JFrame implements Runnable{

	//class variables
	private static final long serialVersionUID = 5155537158251874970L;
	private NovelOptions opts;
	private ArrayList<String> commands = new ArrayList<String>();
	private int currentCommand=1;

	//text variables
	private int textHeight = FullGame.HEIGHT/5;
	private int speakerWidth = FullGame.WIDTH/4;
	private int speakerOrg = FullGame.HEIGHT-textHeight-(textHeight/4);
	private int textIndent = FullGame.WIDTH/15;
	private int fontSize = 22;
	private int index1 = 1;	boolean flag1;
	private int index2 = 1; boolean flag2;
	private int index3 = 1; boolean flag3;
	private int textSpacing = 15;
	private int numReveal = 1;
	private Font font;

	//novel variables
	private String speaker;
	private String textLine1;
	private String textLine2;
	private String textLine3;
	private Image background;
	private Image spriteL;
	private Image spriteM;
	private Image spriteR;
	private Music music;
	private BasicStroke border = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

	//choice variables
	private boolean cFlag = false;
	private int choice;
	private String choiceA;
	private String choiceB;
	private float choiceWidth=FullGame.WIDTH*.75f, choiceHeight=50, choiceSpacing=60, aHeight = FullGame.HEIGHT/2-choiceHeight/2-choiceSpacing/2-50, bHeight=FullGame.HEIGHT/2+choiceHeight/2+choiceSpacing/2-50;
	private Rectangle2D.Float aBox = new Rectangle2D.Float(FullGame.WIDTH/2-choiceWidth/2, aHeight, choiceWidth, choiceHeight);
	private Rectangle2D.Float bBox = new Rectangle2D.Float(FullGame.WIDTH/2-choiceWidth/2, bHeight, choiceWidth, choiceHeight);

	//threading variables
	private Thread thisThread;
	private FullGame fullgame;

	/*
	 * Commands:
	 * 
	 * Set speaker:
	 * SPEAKER speakerName
	 * 
	 * Set music:
	 * MUSIC musicFile.url
	 * 
	 * Set background:
	 * BG backgroundFile.url
	 * 
	 * Set sprites:
	 * SPRITE (L/M/R) spriteFile.url
	 * 
	 * Set text:
	 * TEXT 1 Line one text goes here
	 * TEXT 2 Line two text goes here
	 * TEXT 3 Line three text goes here
	 * 
	 * Remove sprite:
	 * SPRITE (L/M/R) null
	 * 
	 * Remove music:
	 * MUSIC pause
	 * 
	 * Remove speaker (thought mode):
	 * SPEAKER  
	 * 
	 * Wait for click
	 * WAIT
	 * 
	 * Add choices:
	 * CHOICE 1 Choice A text
	 * CHOICE 2 Choice B text
	 */

	/**
	 * Constructor: creates a new novel, given the file URL, options,
	 * and the parent window. It reads the commands from the file, then applies the
	 * commands from the file to the novel in order. Has a key listener to have the
	 * user navigate through the pages, and a mouse listener to listen for choices.
	 * @param o
	 * @param data
	 * @param parent
	 */
	public Novel(NovelOptions o, String data, FullGame parent){
		super();
		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - FullGame.WIDTH)/2, (Toolkit.getDefaultToolkit().getScreenSize().height - FullGame.HEIGHT)/2);
		opts=o; fullgame=parent;
		setUndecorated(true);
		numReveal=o.textSpeed;
		setSize(FullGame.WIDTH, FullGame.HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBackground(Color.BLACK);
		read(data);
		while(!(commands.get(currentCommand).contains("WAIT"))){
			applyCommand(currentCommand);
			currentCommand++;
		}
		//mouse listener checks for whether clicks is in a choice box
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(!cFlag)
					proceed(false);
				else{
					int x = e.getX();
					int y = e.getY();
					choice=1337;
					if(aBox.contains(new Point2D.Float((float)x, (float)y)))
						choice=0;
					if(bBox.contains(new Point2D.Float((float)x, (float)y)))
						choice=1;
					if(fullgame!=null && choice<5)
						fullgame.choices.add(choice);
				}
			}
		});
		//key listener applies appropriate commands
		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				switch(e.getKeyCode()){
				case KeyEvent.VK_ESCAPE:
					System.exit(0);
					break;
				case KeyEvent.VK_SPACE:
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_ENTER:
					if(!cFlag)
						proceed(false); break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_UP:
					lastInstanceofWait();
				}
			}
		});
		thisThread = new Thread(this);
		thisThread.start();
	}

	/**
	 * Returns the novel to the previous page, where the WAIT command was listed
	 */
	public void lastInstanceofWait(){
		currentCommand-=1;
		while(!commands.get(currentCommand).contains("WAIT")){
			currentCommand--;
		}currentCommand-=1;
		if(currentCommand <=0){
			cFlag=false;
			proceed(true);
		}else{
			while(!commands.get(currentCommand).contains("WAIT")){
				currentCommand--;
			}
			cFlag=false;
			if(currentCommand<0)currentCommand=0;
			proceed(true);
		}
	}

	/**
	 * Proceeds to the next page, given whether to have instant text
	 * or scrolling text
	 * @param skip
	 */
	public void proceed(boolean skip){
		if((flag1 && flag2 && flag3) || opts.insta){
			currentCommand++;
			if(currentCommand==commands.size())
				System.exit(0);
			while(currentCommand > 0 && !(commands.get(currentCommand).contains("WAIT"))){
				applyCommand(currentCommand);
				currentCommand++;
			}
			if(!skip){
				index1=1;index2=1;index3=1;flag1=false;flag2=false;flag3=false;
			}
			repaint();
		}else{
			flag1=true;flag2=true;flag3=true;
		}
	}

	/**
	 * Destroys the novel window
	 */
	public void kill(){
		setVisible(false);
		dispose();
	}

	/**
	 * Draws the novel, by drawing each of the respective variables
	 * in order.
	 * @param g
	 */
	public void render(Graphics g){
		super.paint(g);
		drawBG(g);
		drawSprites(g);
		drawTextFields(g);
		drawText(g);
		if(cFlag)
			choice(g);
	}

	/**
	 * Draws a choice panel
	 * @param g
	 */
	private void choice(Graphics g){
		cFlag=true;
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(new Color(255, 234, 115));
		g2d.fill(aBox);		g2d.fill(bBox);
		g2d.setColor(new Color(166, 141, 0));
		g2d.setStroke(border);
		g2d.draw(aBox);		g2d.draw(bBox);
		g.setFont(font);
		int aWidth = g.getFontMetrics(font).stringWidth(choiceA);
		int bWidth = g.getFontMetrics(font).stringWidth(choiceB);
		g.drawString(choiceA, FullGame.WIDTH/2-aWidth/2, (int)aHeight+fontSize+8);
		g.drawString(choiceB, FullGame.WIDTH/2-bWidth/2, (int)bHeight+fontSize+8);

	}

	/**
	 * Draws the text fields where the text is to be written
	 * @param g
	 */
	private void drawTextFields(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		//speaker field
		g2d.setColor(new Color(50, 50, 50, 250));
		g2d.fillRect(10, speakerOrg, speakerWidth, 3*(textHeight/4));

		//speaker field border
		g2d.setColor(new Color(255, 245, 114));
		g2d.setStroke(border);
		g2d.drawRect(10, speakerOrg, speakerWidth, 3*(textHeight/4));

		//text field
		g2d.setColor(new Color(50, 50, 50, 250));
		g2d.fillRect(textIndent, FullGame.HEIGHT-textHeight, FullGame.WIDTH-3*textIndent, textHeight-20);

		//text field border
		g2d.setColor(new Color(255, 245, 114));
		g2d.setStroke(border);
		g2d.drawRect(textIndent, FullGame.HEIGHT-textHeight, FullGame.WIDTH-3*textIndent, textHeight-20);
	}

	/**
	 * Draws the text, with scrolling, if the isntant text option is disabled.
	 * All the data is preloaded by the game.
	 * @param g
	 */
	private void drawText(Graphics g){
		g.setColor(Color.WHITE);
		g.setFont(font);
		g.drawString(speaker, 30, speakerOrg+fontSize+5);
		if(!opts.insta){
			if(index1<=textLine1.length()){
				String lineOne = textLine1.substring(0, index1);
				g.drawString(lineOne, textIndent+20, FullGame.HEIGHT-textHeight+fontSize+textSpacing);
				index1=(index1+numReveal>textLine1.length()?textLine1.length():index1+numReveal);
			}
			if(index1==textLine1.length()) flag1=true;
			if(flag1 && index2<textLine2.length()){
				String lineTwo = textLine2.substring(0, index2);
				g.drawString(textLine1, textIndent+20, FullGame.HEIGHT-textHeight+fontSize+textSpacing);
				g.drawString(lineTwo, textIndent+20, FullGame.HEIGHT-textHeight+2*(fontSize+textSpacing));
				index2=(index2+numReveal>textLine2.length()?textLine2.length():index2+numReveal);
			}
			if(index2==textLine2.length()) flag2=true;
			if(flag2 && index3<=textLine3.length()){
				String lineThree = textLine3.substring(0, index3);
				g.drawString(textLine1, textIndent+20, FullGame.HEIGHT-textHeight+fontSize+textSpacing);
				g.drawString(textLine2, textIndent+20, FullGame.HEIGHT-textHeight+2*(fontSize+textSpacing));
				g.drawString(lineThree, textIndent+20, FullGame.HEIGHT-textHeight+3*(fontSize+textSpacing));
				index3=(index3+numReveal>textLine3.length()?textLine3.length():index3+numReveal);
			}
			if(index3==textLine3.length()) flag3=true;
		}
		if(flag3 || opts.insta){
			g.drawString(textLine1, textIndent+20, FullGame.HEIGHT-textHeight+fontSize+textSpacing);
			g.drawString(textLine2, textIndent+20, FullGame.HEIGHT-textHeight+2*(fontSize+textSpacing));
			g.drawString(textLine3, textIndent+20, FullGame.HEIGHT-textHeight+3*(fontSize+textSpacing));
		}
	}

	/**
	 * Draws the background image
	 * @param g
	 */
	private void drawBG(Graphics g){
		g.drawImage(background, 0, 0, FullGame.WIDTH, FullGame.HEIGHT, null);
	}

	/**
	 * Draws the sprites, if the sprites have been loaded
	 * @param g
	 */
	private void drawSprites(Graphics g){
		if(spriteL!=null)
			g.drawImage(spriteL, -150, FullGame.HEIGHT-spriteL.getHeight(null), null);
		if(spriteM!=null)
			g.drawImage(spriteM, FullGame.WIDTH/2-spriteM.getWidth(null)/2, FullGame.HEIGHT-spriteM.getHeight(null), null);
		if(spriteR!=null)
			g.drawImage(spriteR, FullGame.WIDTH-spriteR.getWidth(null)+150, FullGame.HEIGHT-spriteR.getHeight(null), null);
	}

	/**
	 * Parses and applies commands from the text file, given the index of the command
	 * @param command
	 */
	private void applyCommand(int command){
		String t = commands.get(command);
		
		//sets the font
		if(t.substring(0, 4).equals("FONT")){
			font = new Font(t.substring(5, t.length()), Font.PLAIN, fontSize);
			return;
		}
		
		//sets the speaker name
		if(t.substring(0, 7).equals("SPEAKER")){
			if(t.contains("%")){
				speaker = nameTranslate(t.substring(8, t.length()));
				return;
			}
			speaker = t.substring(8, t.length());
			return;
		}
		
		//sets the background image
		if(t.substring(0, 2).equals("BG")){
			try{
				background = ImageIO.read(new File("img/bg/vnbackgrounds/"+t.substring(3, t.length())));
			}catch(IOException e){
				System.err.println("Error: IOException reading image at command " + currentCommand);
				e.printStackTrace();
			}
			return;
		}
		
		//sets the background music
		if(t.substring(0, 5).equals("MUSIC")){
			if(music!=null)
				music.stop();
			if(!t.contains("pause"))
				music = new Music("audio/bgm/" + t.substring(6, t.length()));
			if(fullgame.currentMusic!=null)fullgame.stopMusic();
			fullgame.currentMusic = music;
			fullgame.playMusic();
			music=null;
			return;
		}
		
		//sets the sprites
		if(t.substring(0,6).equals("SPRITE")){		
			t=t.substring(7, t.length());
			try{
				switch(t.charAt(0)){
				case 'l':
				case 'L': spriteL = (t.contains("null")?null:ImageIO.read(new File("img/vnsprites/" + nameTranslate(t.substring(2, t.length())) + ".png"))); break;
				case 'r':
				case 'R': spriteR = (t.contains("null")?null:ImageIO.read(new File("img/vnsprites/" + nameTranslate(t.substring(2, t.length())) + ".png"))); break;
				case 'm':
				case 'M': spriteM = (t.contains("null")?null:ImageIO.read(new File("img/vnsprites/" + nameTranslate(t.substring(2, t.length())) + ".png"))); break;
				default:
					System.err.println("Error: Not a valid sprite position" + " \"" + t.substring(2, t.length()) + ".png" + "\" at command " + currentCommand);
				}
			}
			catch(IOException e){
				System.err.println("Novel: sprite image not found");
			}
			return;
		}
		
		//sets line 1 of the text
		if(t.substring(0, 6).equals("TEXT 1")){
			index1=1;
			flag1=false;
			textLine1=textTranslate(t.substring(7, t.length()));
			return;
		}
		
		//sets line 2 of the text
		if(t.substring(0, 6).equals("TEXT 2")){
			index2=1;
			flag2=false;
			textLine2=textTranslate(t.substring(7, t.length()));
			return;
		}
		
		//sets line 3 of the text
		if(t.substring(0, 6).equals("TEXT 3")){
			index3=1;
			flag3=false;
			textLine3=textTranslate(t.substring(7, t.length()));
			return;
		}
		
		//sets the first choice
		if(t.substring(0, 8).equals("CHOICE 1")){
			choiceA=t.substring(9, t.length());
			return;
		}
		
		//sets the second choice
		if(t.substring(0, 8).equals("CHOICE 2")){
			choiceB=t.substring(9, t.length());
			cFlag=true;
			return;
		}
	}

	/**
	 * Translates escape characters in the command to the proper text, such
	 * as character names and other such nuances.
	 * @param orig
	 * @return
	 */
	private String textTranslate(String orig){
		if(!orig.contains("%"))
			return orig;
		int index = orig.indexOf("%");
		String firstPart = orig.substring(0, index);
		String midPart = nameTranslate(orig.substring(index, index+4));
		String secondPart = textTranslate(orig.substring(index+4, orig.length()));
		return firstPart+midPart+secondPart;
	}


	/**
	 * Translates different escape characters as different names from the characters,
	 * taken from the parent game.
	 * @param code
	 * @return
	 */
	private String nameTranslate(String code){
		if(code.contains("%MCF"))			return fullgame.getMC().fName;
		if(code.contains("%MCL"))			return fullgame.getMC().lName;
		if(code.contains("%MCR"))			return fullgame.getMC().fName + " " + fullgame.getMC().lName;
		if(code.contains("%MCK"))			return fullgame.getMC().lName + " " + fullgame.getMC().fName;
		if(code.contains("%SCF"))			return fullgame.getSC().fName;
		if(code.contains("%SCL"))			return fullgame.getSC().lName;
		if(code.contains("%SCR"))			return fullgame.getSC().fName + " " + fullgame.getSC().lName;
		if(code.contains("%SCK"))			return fullgame.getSC().lName + " " + fullgame.getSC().fName;
		return "Sir Invalid Sr. IV";

	}

	/**
	 * Reads the file, given the file name, and adds all the commands from a file
	 * to an organized ArryList
	 * @param filename
	 */
	private void read(String filename){
		try{
			Scanner reader = new Scanner(new FileReader(filename));
			while(reader.hasNextLine())
				commands.add(reader.nextLine());
			reader.close();
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: File not found. \n\nNovel data was not read.");
			System.exit(0);
		}
	}

	/**
	 * Animates the panels, so that text scrolls as specified by options.
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

	/**
	 * Paints the panel and double-buffers the entire thing.
	 */
	public void paint(Graphics g){
		Image i=createImage(getWidth(), getHeight());
		render((i.getGraphics()));
		g.drawImage(i,0,0,this);
	}
	
}