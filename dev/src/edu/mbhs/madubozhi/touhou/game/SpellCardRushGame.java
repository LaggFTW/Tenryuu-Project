package edu.mbhs.madubozhi.touhou.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JFrame;

import edu.mbhs.madubozhi.touhou.game.bullet.pattern.BezierBulletPath;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.LinearBulletPath;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.SpiralBulletPath;
import edu.mbhs.madubozhi.touhou.game.enemy.EmbeddedEnemy;
import edu.mbhs.madubozhi.touhou.game.enemy.Enemy;
import edu.mbhs.madubozhi.touhou.game.enemy.EnemyEnemy;
import edu.mbhs.madubozhi.touhou.game.enemy.PlayerLaunchedEnemy;
import edu.mbhs.madubozhi.touhou.game.enemy.pattern.EnemyMovementPattern;
import edu.mbhs.madubozhi.touhou.game.parser.AlphaLevelParser;
import edu.mbhs.madubozhi.touhou.game.util.ImageHash;
import edu.mbhs.madubozhi.touhou.game.util.InputHandler;
import edu.mbhs.madubozhi.touhou.game.util.Mode;
import edu.mbhs.madubozhi.touhou.game.util.OscillationType;
import edu.mbhs.madubozhi.touhou.game.util.Oscillator;
import edu.mbhs.madubozhi.touhou.game.util.PathTraversal;
import edu.mbhs.madubozhi.touhou.game.util.RotationalDirection;
import edu.mbhs.madubozhi.touhou.ui.Character;
import edu.mbhs.madubozhi.touhou.ui.FullGame;

/**
 * The main Game class, which implements all other aspects of the game into it. It is the engine of the game, which is
 * a vertically scrolling dodging game with a preset number of bullet patters with increasing difficulty.
 * It extends the swing JFrame object, and also implements the Runnable interface for animating the game.
 * @author bowenzhi and mattdu
 */
public class SpellCardRushGame extends JFrame implements Runnable {
	private static final long serialVersionUID = -8395759457708163217L;
	private int stage;
	public static final String DEFAULT_TITLE = "Tenryu Project: Sanreiryuu";
	private Player player;
	private InputHandler input;
	private Thread thisThread;
	private int border = 10;
	private int fWidth = (int)(FullGame.WIDTH*.7);
	private int fHeight= (int)(FullGame.HEIGHT-2*border);
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private ArrayList<PlayerLaunchedEnemy> playerBullets = new ArrayList<PlayerLaunchedEnemy>();
	private int hits, score = 0, graze = 0;
	private long mspf = 20;

	private boolean exploding;
	private int expX, expY, rExp=0, rExpMax=100, expSpeed=2;

	private boolean invincible=false;
	private int invincibilityCounter=0;
	private int invincibilityFrames=150+rExpMax/expSpeed;
	
	private int deathPenalty = 0;
	private int enemyKillBonus = 1000;

	private Mode mode = Mode.NORMAL;

	private boolean ohko = true;//if false, the player will not die after being hit

	protected boolean gameOver=false;

	//Stuff beneath this are used as tests, will likely not be used in final code
	private float angle = 0;
	private float angleVel = 0;
	private float angleAccel = 0.05f;
	private boolean increaseOmega = true;
	private int counter = 0;
	private int counter1 = 0;
	private int counter2 = 0;
	private boolean toggle = false;
	private long timer = 0;
	private long duration = 1000;
	private long restDuration = 240;
	private int difficulty;
	private int difficultyProgression;//Spellcards per difficulty
	private int diffFactor;//(int) (difficulty*diffFactor + Math.random()*randomFactor);
	private int randomFactor;//(int) (difficulty*diffFactor + Math.random()*randomFactor);
	private int spellCardCount;
	private Oscillator oscillator;
	private AlphaLevelParser alp;
	private boolean spawned = false;
	private boolean spellcardrush = true;

	private int cardSelect;
	private int numCards = 15;
	
	private FullGame parent;

	//Sprite loaders
	private BufferedImage basicBullet;
	private BufferedImage sunBullet;

	public static void main(String [] args){new SpellCardRushGame(Mode.LUNATIC, 1, null).setVisible(true);}

	/**
	 * Instantiates this Game object. Which promptly starts the game.
	 * @param m Difficulty mode
	 * @param stage Stage number
	 */
	public SpellCardRushGame(Mode m, int stage, FullGame par){
		super(DEFAULT_TITLE);
		this.parent = par;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(FullGame.WIDTH, FullGame.HEIGHT);
		setUndecorated(true);		
		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - FullGame.WIDTH)/2, (Toolkit.getDefaultToolkit().getScreenSize().height - FullGame.HEIGHT)/2);
		this.mode = m;
		this.stage = stage;
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Invisimouse"); 
		setCursor(blankCursor);
		this.sunBullet = ImageHash.IMG.getImage("sun_small");
		this.basicBullet = ImageHash.IMG.getImage("orb");
		init();
		getContentPane().setBackground(Color.BLACK);
		repaint();
		setThread(new Thread(this));
		getThread().start();
	}

	/**
	 * Sets up initial variables of the game before it is begun.
	 */
	private void init(){
		input = new InputHandler(this);
		initGame();
	}

	/**
	 * Sets up a number of game variables. Is called in init.
	 */
	private void initGame(){
		gameOver = false;
		if (parent != null){
			player = new Player(fWidth/2, 4*fHeight/5, border, fWidth+border*2, parent.getMC(), parent.getSC());
		} else {
			player = new Player(fWidth/2, 4*fHeight/5, border, fWidth+border*2, Character.ASAMI, Character.JIN);
		}
		cardSelect = 14;
		difficulty = 5;
		diffFactor = 2;//(int) (difficulty*diffFactor + Math.random()*randomFactor);
		randomFactor = 3;//(int) (difficulty*diffFactor + Math.random()*randomFactor);
		difficultyProgression = 3;
		spellCardCount = 1;
		score = 0;
		graze = 0;
		hits = 0;
		clearBullets();
		resetCounters();
		oscillator = new Oscillator(OscillationType.COSINE, 0, 0, 0.1f, 5, 30, 1080);
		try {
			alp = new AlphaLevelParser(new FileReader(new File("level1OLD.dat")), border, fWidth ,fHeight, mode, player);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Overrides the paint method of the JFrame. Renders graphics to be displayed in the game as an image.
	 */
	public void paint(Graphics g){
		Image i=createImage(getWidth(), getHeight());
		render((i.getGraphics()));
		g.drawImage(i,0,0,this);
	}

	/**
	 * Basic method for collision detection between the player and a specified enemy
	 * @param e Enemy to check collisions against
	 * @return true if the player collides with the enemy, else false
	 */
	private synchronized boolean checkCollision(Enemy e){
		return checkRange(e, 250)?e.getArea().intersects(player.getArea()):false;
	}

	/**
	 * Checks if the graze (close to getting hit) hitbox intersects with a specified enemy
	 * @param e Enemy to check grazes against
	 * @return true if the player is close enough to the enemy for grazing to occur, else false
	 */
	private synchronized boolean checkGraze(Enemy e){
		return checkRange(e, 250)?e.getArea().intersects(player.getGrazeArea()):false;
	}
	
	/**
	 * Checks if the enemy is close enough to the player to warrant collision detection
	 * @param e Enemy
	 * @param radius Maximum radius of the area from the player
	 * @return Whether or not the enemy is in range to warrant collision detection
	 */
	private synchronized boolean checkRange(Enemy e, int radius){
		return (new Rectangle2D.Float(player.x - radius, player.y - radius, radius * 2, radius * 2).contains(e.getArea().getBounds2D()));
	}

	/**
	 * Draws all game components, including the player, enemies, the background, and the sidebar
	 * @param g Graphics object of the Component that this will be drawn on
	 */
	private synchronized void render(Graphics g){
		super.paint(g);
		drawEnemies(g);
		if(exploding)
			drawExplosion(g);
		if (!gameOver && !exploding)
			drawPlayer(g);
		drawBackground(g);
		int fSize=40;
		g.setColor(Color.BLACK);
		g.setFont(new Font("Times New Roman", fSize, fSize));
		g.drawString("Stage: " + stage, fWidth+3*border, 30+fSize);
		g.drawString("Hits: " + hits, fWidth+3*border, 40+2*fSize);
		g.drawString("Difficulty: " + mode.getName(), fWidth+3*border, 50+3*fSize);
		g.drawString("Graze: " + graze, fWidth+3*border, 60+4*fSize);
		g.drawString("Score: " + score, fWidth+3*border, 70+5*fSize);
		g.drawString("Lives: " + (player.lives<0?"NULL":player.lives), fWidth+3*border, 80+6*fSize);
		if (gameOver){
			renderGameOverText(g);
		}
	}

	/**
	 * Draws the explosion animation
	 */
	private void drawExplosion(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.YELLOW);
		g2d.fill(new Ellipse2D.Float(expX-rExp/2, expY-rExp/2, rExp, rExp));
		int rOuter=20;
		if(rExp-rOuter>0){
			g2d.setColor(Color.ORANGE);
			g2d.fill(new Ellipse2D.Float(expX-(rExp-rOuter)/2, expY-(rExp-rOuter)/2, (rExp-rOuter), (rExp-rOuter)));
			int rInner = 60;
			if(rExp-rInner>0){
				g2d.setColor(Color.RED);
				g2d.fill(new Ellipse2D.Float(expX-(rExp-rInner)/2, expY-(rExp-rInner)/2, (rExp-rInner), (rExp-rInner)));
			}
		}
	}

	/**
	 * Renders text for the game over screen
	 * @param g Graphics object of the Component that this will be drawn on
	 */
	private void renderGameOverText(Graphics g) {
		int fSize=40;
		g.setFont(new Font("Times New Roman", fSize, fSize));
		Color c = g.getColor();
		g.setColor(Color.WHITE);
		String s = "Game Over";
		String t = "Press 'R' to Retry";
		g.drawString(s, fWidth/2 - g.getFontMetrics().stringWidth(s)/2, fHeight/2 - (g.getFontMetrics().getMaxDescent()));
		g.setFont(new Font("Times New Roman", fSize*2/3, fSize*2/3));
		g.drawString(t, fWidth/2 - g.getFontMetrics().stringWidth(t)/2, fHeight/2 - (g.getFontMetrics().getMaxDescent()) + 40);
		g.setColor(c);
	}

	/**
	 * Draws the background image for the game. Currently is a solid color with a border
	 * @param g Graphics object of the Component that this will be drawn on
	 */
	private void drawBackground(Graphics g){
		g.setColor(Color.WHITE);
		g.fillRect(fWidth+2*border, border, FullGame.WIDTH-(fWidth+3*border), fHeight);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, border, FullGame.HEIGHT);
		g.fillRect(0, 0, FullGame.WIDTH, border);
		g.fillRect(0, FullGame.HEIGHT-border, FullGame.WIDTH, border);
		g.fillRect(fWidth+border, 0, border, FullGame.HEIGHT);
		g.fillRect(FullGame.WIDTH-border, 0, border, FullGame.HEIGHT);
	}

	/**
	 * Draws the Player onto the screen at its current location
	 * @param g Graphics object of the Component that this will be drawn on
	 */
	private void drawPlayer(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
		g2d.draw(new Ellipse2D.Float(player.x-player.rGraze/2, player.y-player.rGraze/2, player.rGraze, player.rGraze));
		if(invincible)
			if((int)(invincibilityCounter/5)%2==0)
				g2d.setColor(Color.WHITE);
			else
				g2d.setColor(Color.RED);
		else
			g2d.setColor(Color.RED);
		g2d.fill(new Ellipse2D.Float(player.x-player.size-4, player.y-player.size-4, player.size*2+8, player.size*2+8));
		if(invincible)
			if((int)(invincibilityCounter/5)%2==0)
				g2d.setColor(Color.CYAN);
			else
				g2d.setColor(Color.WHITE);
		else
			g2d.setColor(Color.WHITE);
		g2d.fill(new Ellipse2D.Float(player.x-player.size, player.y-player.size, player.size*2, player.size*2));
	}

	/**
	 * Draws the enemies onto the screen. Also removes any unneeded enemies for optimization
	 * @param g Graphics object of the Component that this will be drawn on
	 */
	private synchronized void drawEnemies(Graphics g){
		ArrayList<Enemy> removeMe = new ArrayList<Enemy>();
		ArrayList<PlayerLaunchedEnemy> playerRemove = new ArrayList<PlayerLaunchedEnemy>();
		for(Enemy e: enemies){
			if(e.x > -e.size*2 - border && e.y > -e.size*2 - border && e.x < fWidth + border + e.size*2 && e.y < fHeight + border + e.size*2){
				e.draw(g);
			} else
				if (e.q > 0.1 && e.offScreenDelete)
					removeMe.add(e);
		}
		for(PlayerLaunchedEnemy e: playerBullets){
			if(e.x > -e.size*2 - border && e.y > -e.size*2 - border && e.x < fWidth + border + e.size*2 && e.y < fHeight + border + e.size*2){
				e.draw(g);
			} else
				if (e.q > 0.1 && e.offScreenDelete)
					playerRemove.add(e);
		}
		for (Enemy e: removeMe)
			enemies.remove(e);
		for (PlayerLaunchedEnemy e: playerRemove)
			playerBullets.remove(e);
	}

	/**
	 * Chooses the current pattern for enemies to move in based on difficulty and current time. Updates the difficulty
	 * so that more difficult patterns appear as time approaches. Called every update.
	 */
	private void checkAndIncrementSpellCards() {
		if (cardSelect<numCards){
			if (timer>duration){
				cardSelect = numCards;
				spellCardCount++;
				resetCounters();
			}
		} else {
			if (timer>restDuration){
				cardSelect = (int) (difficulty*diffFactor + Math.random()*randomFactor);
				resetCounters();
			}
			if (timer==restDuration*2/3)
				clearBullets();
		}
		if (spellCardCount % difficultyProgression == 0){
			difficulty -= (difficulty==0?0:1);
			spellCardCount = 1;
		}
		timer++;
	}

	/**
	 * Resets the counters so that they may be used for the next spellcard or game reset
	 */
	private void resetCounters(){
		counter = 0;
		counter1 = 0;
		counter2 = 0;
		angle = 0;
		angleVel = 0;
		increaseOmega = true;
		toggle = false;
		timer = 0;
	}

	/**
	 * Clears away bullets currently on the screen, and gives the user some points for every bullet cleared if at the
	 * end of a spellcard (patterns)
	 */
	private synchronized void clearBullets(){
		score += gameOver?0:enemies.size() * 50;
		enemies = new ArrayList<Enemy>();
	}

	/**
	 * Loops through the list of all enemies, and checks collisions and graze, and manages more complex pattern 
	 * expansions
	 */
	private synchronized void updateBullets(){
		int enemyCount = 0;
		ArrayList<Enemy> removeMe = new ArrayList<Enemy>();
		ArrayList<Enemy> toAdd = new ArrayList<Enemy>();
		ArrayList<PlayerLaunchedEnemy> playerRemove = new ArrayList<PlayerLaunchedEnemy>();
		for(Enemy e:enemies){
			if(e.isFinished()){
				removeMe.add(e);
				if (e instanceof EnemyEnemy && ((EnemyEnemy)e).getSpellcardStatus()){
					alp.setSpellcard(numCards);
//					clearBullets();
				}
			}
			else {
				if(checkCollision(e) && !invincible && !gameOver){
					hits++;
					player.lives--;invincibilityCounter = 0;
					invincible = true;
					expX = player.x; expY = player.y;
					exploding=true;
					score -= score<deathPenalty?(score):deathPenalty;
					if (!(e instanceof EnemyEnemy))
							removeMe.add(e);
					if (ohko && player.lives<0){
						gameOver = true;
						if (parent != null){
							parent.setGameRunning(false);
						}
					}
				} else {
					if (e instanceof EnemyEnemy){
						for (PlayerLaunchedEnemy ple: playerBullets){
							boolean b = ((EnemyEnemy) e).checkCollision(ple);
							if (b && !playerRemove.contains(ple))
								playerRemove.add(ple);
						}
						if (((EnemyEnemy) e).isDestroyed()){
							removeMe.add(e);
							score += enemyKillBonus;
							if (((EnemyEnemy)e).getSpellcardStatus()){
								alp.setSpellcard(numCards);
//								clearBullets();
							}
							continue;
						}
						enemyCount++;
					}
					if (e instanceof EmbeddedEnemy){
						ArrayList<Enemy> tempArr = ((EmbeddedEnemy)e).launchPattern();
						if (tempArr != null)
							for(Enemy enemy: tempArr)
								toAdd.add(enemy);
					}
					e.step();
					if(checkGraze(e) && !gameOver && !exploding && !invincible){
						graze++;
						score+=10;
					}
				}
			}
		}
		for(PlayerLaunchedEnemy e: playerBullets){
			if (e.isFinished())
				playerRemove.add(e);
			else
				e.step();
		}
		for(PlayerLaunchedEnemy e: playerRemove){
			playerBullets.remove(e);
		}
		for(Enemy e:removeMe){
			enemies.remove(e);
		}
		for(Enemy e:toAdd){
			enemies.add(e);
		}
		if (enemyCount == 0){
			if (!spawned){
				alp.immediateExecution();
				spawned = true;
			}
		} else
			spawned = false;
	}

	/**
	 * Updates the player and enemy positions, as well as pattern formations. Called every frame.
	 */
	private synchronized void update(){
		if (!gameOver && !exploding){
			if (spellcardrush)
				checkAndIncrementSpellCards();
			player.stepWithEnemies(input, playerBullets);
		}
		updateBullets();
		if (!gameOver){
			if (!spellcardrush){
				cardSelect = alp.getSpellcard();
				ArrayList<Enemy> e = alp.step();
				if (e != null)
					for (Enemy enemy: e)
						enemies.add(enemy);
			}
			stepSpellCards();
		}
		checkGameOverStatus();
	}

	/**
	 * Manages how to respond to user input at the game over screen
	 */
	private void checkGameOverStatus() {
		if (gameOver){
			if (input.getRestartSignal()){
				initGame();
			}
		}
	}

	/**
	 * Main method for forming spellcards, which are enemy movement patterns. There are a preset number of spellcards,
	 * which can be run based on the selected spellcard.
	 */
	private synchronized void stepSpellCards() {
		switch (cardSelect){
		case -1:
			/**
			 * Lotus Sign: "Big Lotus"
			 */
			if (counter >= mode.getBulletNumber()){
				EnemyMovementPattern emp1 = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), angle, angle + 360, 16, 0, 0,
						new Enemy(-fWidth, 50, 2, 8, new Color(0, (int)(Math.abs(angleVel)/5 * 40), (int)(Math.abs(angleVel)/5 * 200)), border, fWidth, false, false), 
						new SpiralBulletPath(0, 0, 30, 800, angle, angle + 720, true), false);
				EnemyMovementPattern emp2 = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), angle, angle + 360, 16, 0, 0,
						new Enemy(-fWidth, 50, 2, 8, new Color((int)(Math.abs(angleVel)/5 * 200), 0, (int)(Math.abs(angleVel)/5 * 40)), border, fWidth, false, false), 
						new SpiralBulletPath(0, 0, 30, 800, angle, angle - 720, true), false);
				for (Enemy e: emp1.step()){
					enemies.add(e);
				}
				for (Enemy e: emp2.step()){
					enemies.add(e);
				}
				counter = 0;
			}
			angle += angleVel;
			if (increaseOmega)
				if (angleVel < 5)
					angleVel += angleAccel;
				else
					increaseOmega = false;
			else
				if (angleVel > -5)
					angleVel -= angleAccel;
				else
					increaseOmega = true;
			counter++;
			break;

		case -2:
			/**
			 * Lotus Sign: "Reversal"
			 */
			if (counter >= mode.getBulletNumber()){
				EnemyMovementPattern emp1 = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), angle, angle + 360, 16, 0, 0,
						new Enemy(-fWidth, 50, 2, 8, new Color(0, (int)(Math.abs(angleVel)/5 * 40), (int)(Math.abs(angleVel)/5 * 200)), border, fWidth, false, false), 
						new SpiralBulletPath(0, 0, 30, 800, angle, angle + 720, true), false);
				EnemyMovementPattern emp2 = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), angle, angle + 360, 16, 0, 0,
						new Enemy(-fWidth, 50, 2, 8, new Color((int)(Math.abs(angleVel)/5 * 200), 0, (int)(Math.abs(angleVel)/5 * 40)), border, fWidth, false, false), 
						new SpiralBulletPath(0, 0, 800, 30, angle, angle - 720, true), false);
				for (Enemy e: emp1.step()){
					enemies.add(e);
				}
				for (Enemy e: emp2.step()){
					enemies.add(e);
				}
				counter = 0;
			}
			angle += angleVel;
			if (increaseOmega)
				if (angleVel < 5)
					angleVel += angleAccel;
				else
					increaseOmega = false;
			else
				if (angleVel > -5)
					angleVel -= angleAccel;
				else
					increaseOmega = true;
			counter++;
			break;

		case 0:
			/**
			 * Phoenix Sign: "Phoenix Wings"
			 */
			if (counter >= mode.getBulletNumber() / 3){
				EnemyMovementPattern emp1 = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/3), oscillator.getAngle(), oscillator.getAngle() + 360, 16, 0, 0,
						new Enemy(-fWidth, 50, 4, 70 - mode.getBulletNumber(), new Color((int)(Math.random()*60) + 150, (int)(Math.random()*120) + 50, (int)(Math.random()*120) + 50), border, fWidth, false), 
						new LinearBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(fHeight, 0)), 
								false);
				for (Enemy e: emp1.step()){
					enemies.add(e);
				}
				oscillator.oscillate();
				counter = 0;
			}
			if (counter1 >= mode.getBulletNumber()*.75){
				Point2D.Float tempP = new Point2D.Float(fWidth/4, FullGame.HEIGHT/2);
				float theta = RotationalDirection.getAngleBetween(tempP, new Point2D.Float(player.x, player.y));
				EnemyMovementPattern emp1 = new EnemyMovementPattern(
						tempP, (float)(theta + Math.PI/4), (float)(theta + Math.PI), 3, 0, 0,
						new Enemy(-fWidth, 50, 4, 100 - mode.getBulletNumber(), Color.ORANGE, border, fWidth, false), 
						new LinearBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(fHeight, 0)), 
								true, true);
				tempP = new Point2D.Float(3*fWidth/4, FullGame.HEIGHT/2);
				theta = RotationalDirection.getAngleBetween(tempP, new Point2D.Float(player.x, player.y));
				EnemyMovementPattern emp2 = new EnemyMovementPattern(
						tempP, (float)(theta + Math.PI/4), (float)(theta + Math.PI), 3, 0, 0,
						new Enemy(-fWidth, 50, 4, 100 - mode.getBulletNumber(), Color.ORANGE, border, fWidth, false), 
						new LinearBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(fHeight, 0)), 
								true, true);
				for (Enemy e: emp1.step()){
					enemies.add(e);
				}
				for (Enemy e: emp2.step()){
					enemies.add(e);
				}
				counter1 = 0;
			}
			counter++;
			counter1++;
			break;

		case 1:
			/**
			 * Stargazer Sign: "Evening Primrose"
			 */
			if (Math.random() * mode.getBulletNumber() < 2){
				EnemyMovementPattern emp = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), angle, angle + 360, 16, 0, 30,
						new Enemy(-fWidth, 50, (int)(6*Math.random()+2), (int)(6*Math.random()+2), new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)),
								border, fWidth, null, false, true), 
								new BezierBulletPath(
										new Point2D.Float(0, 0),
										new Point2D.Float(750, increaseOmega?-1250:1250), 
										new Point2D.Float(1250, increaseOmega?750:-750),
										PathTraversal.ROOT), 
										false);
				for (Enemy e: emp.step()){
					enemies.add(e);
				}
			}
			angle += angleVel;
			if (increaseOmega)
				if (angleVel < 5)
					angleVel += 0.1;
				else
					increaseOmega = false;
			else
				if (angleVel > -5)
					angleVel -= 0.1;
				else
					increaseOmega = true;
			break;

		case 2:
			/**
			 * Kerr Sign: "Fission Whirlpool"
			 */
			if (counter >= mode.getBulletNumber()){
				EnemyMovementPattern emp1 = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), angle, angle + 360, 16, 0, 30,
						new Enemy(-fWidth, 50, 6, 64, new Color(0, (int)(Math.abs(angleVel)/5 * 40), (int)(Math.abs(angleVel)/5 * 200)), border, fWidth, false), 
						new BezierBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(750, increaseOmega?-125:125), 
								new Point2D.Float(1250, increaseOmega?75:-75)), 
								false);
				EnemyMovementPattern emp2 = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), angle, angle + 360, 16, 0, 30,
						new Enemy(-fWidth, 50, 6, 64, new Color((int)(Math.abs(angleVel)/5 * 200), 0, (int)(Math.abs(angleVel)/5 * 40)), border, fWidth, false), 
						new BezierBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(750, increaseOmega?125:-125), 
								new Point2D.Float(1250, increaseOmega?-75:75)),
								false);
				for (Enemy e: emp1.step()){
					enemies.add(e);
				}
				for (Enemy e: emp2.step()){
					enemies.add(e);
				}
				counter = 0;
			}
			if (counter1 >= mode.getBulletNumber()){
				EnemyMovementPattern emp = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), angle, angle + 360, 16, 0, 30,
						new Enemy(-fWidth, 50, 6, 64, new Color((int)(Math.abs(angleVel)/5 * 120), (int)(Math.abs(angleVel)/5 * 120), 0), border, fWidth, false, false), 
						new BezierBulletPath( 
								new Point2D.Float(625, increaseOmega?-75:75),
								new Point2D.Float(150, increaseOmega?125:-125),
								new Point2D.Float(0, 0)),
								false);
				for (Enemy e: emp.step()){
					enemies.add(e);
				}
				counter1 = 0;
			}
			if (counter2 >= mode.getBulletNumber() * 8){
				EnemyMovementPattern emp = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), angle, angle + 360, 6, 0, 30,
						new Enemy(-fWidth, 50, 30, 64+16*Math.random(), new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)), border, fWidth, false), 
						new BezierBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(750, increaseOmega?125:-125), 
								new Point2D.Float(1250, increaseOmega?-75:75)),
								false);
				for (Enemy e: emp.step()){
					enemies.add(e);
				}
				counter2 = 0;
			}
			angle += angleVel;
			if (increaseOmega)
				if (angleVel < 5)
					angleVel += angleAccel;
				else
					increaseOmega = false;
			else
				if (angleVel > -5)
					angleVel -= angleAccel;
				else
					increaseOmega = true;
			counter++;
			counter1++;
			counter2++;
			break;
		
		case 3:
			/**
			 * Darkness Sign: "Labyrinth"
			 */
			if (counter >= (mode.getBulletNumber()/20 + 1) * 6 / 3.5){
				EnemyMovementPattern emp1 = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/3), oscillator.getAngle(), oscillator.getAngle() + 0.65f, 16, 0, 0,
						new Enemy(-fWidth, 50, 8, 80 - (mode.getBulletNumber()<15?mode.getBulletNumber():15 * 2), new Color(30, 10, 30),
								border, fWidth, null, false, false), 
								new LinearBulletPath(
										new Point2D.Float(0, 0),
										new Point2D.Float(fHeight, 0),
										PathTraversal.ROOT), 
										false, true);
				for (Enemy e: emp1.step()){
					enemies.add(e);
				}
				oscillator.oscillate();
				counter = 0;
			}
			counter++;
			break;

		case 4:
			/**
			 * Orbit Sign: "Blue Flare"
			 */
			if (timer <= duration * 3 / 5 && counter >= mode.getBulletNumber() * 2){
				EnemyMovementPattern emp1 = new EnemyMovementPattern(
						new Point2D.Float(player.x, player.y), angle, angle + 360, 16, 0, 0,
						new Enemy(-fWidth, 50, 2, 64, new Color(0, (int)(Math.abs(angleVel)/5 * 40), (int)(Math.abs(angleVel)/5 * 200)), border, fWidth, false, false), 
						new SpiralBulletPath(0, 0, 500, 0, angle, angle + 60, true), false);
				EnemyMovementPattern emp2 = new EnemyMovementPattern(
						new Point2D.Float(player.x, player.y), angle, angle + 360, 16, 0, 15,
						new Enemy(-fWidth, 50, 2, Math.random() * 32 + 4, new Color((int)(Math.abs(angleVel)/5 * 40), 0, (int)(Math.abs(angleVel)/5 * 200)), border, fWidth, false, false), 
						new SpiralBulletPath(0, 0, 800, 50, angle, angle - 360, true), false);
				for (Enemy e: emp1.step()){
					enemies.add(e);
				}
				for (Enemy e: emp2.step()){
					enemies.add(e);
				}
				counter = 0;
			}
			angle += angleVel;
			if (increaseOmega)
				if (angleVel < 5)
					angleVel += angleAccel;
				else
					increaseOmega = false;
			else
				if (angleVel > -5)
					angleVel -= angleAccel;
				else
					increaseOmega = true;
			counter++;
			break;

		case 5:
			/**
			 * Orbit Sign: "Lunar Satellite"
			 */
			if (timer <= 3*duration/4 && counter >= (mode.getBulletNumber()/10) + 50){
				Point2D.Float tempP = new Point2D.Float(fWidth/2, FullGame.HEIGHT/2);
				float theta = RotationalDirection.getAngleBetween(tempP, new Point2D.Float(player.x, player.y));
				Color c = new Color((int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30);
				LinearBulletPath path1 = new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(1000, 0), PathTraversal.QUADRATIC);
				LinearBulletPath path2 = new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(0, 100), PathTraversal.ROOT);
				EnemyMovementPattern emp = new EnemyMovementPattern(
						tempP, (float) (theta+Math.PI/2), (float) (theta + Math.PI/2 + 0.001), 1, 0, 0,
						new EmbeddedEnemy(-fWidth, 0, 25, 35+(Math.random()*35), c, border, fWidth, Integer.MAX_VALUE, false, 
								new EnemyMovementPattern(null, 0, 360, 8, 0, 35, 
										new Enemy(-fWidth, 5, 4, 20, c, border, fWidth, true), path2, false, false),
										mode.getBulletNumber()*5, null), path1, true, true);
				for (Enemy e: emp.step()){
					enemies.add(e);
				}
				counter = 0;
			}
			if (counter1 >= mode.getBulletNumber()){
				EnemyMovementPattern emp1 = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/3), -oscillator.getAngle(), 120 - oscillator.getAngle(), 8, 0, 0,
						new Enemy(-fWidth, 50, 3, 70 - mode.getBulletNumber(), new Color(100, 200, 200), border, fWidth, false), 
						new LinearBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(fHeight, 0),
								PathTraversal.LINEAR), 
								false, false);
				EnemyMovementPattern emp2 = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/3), 180 - oscillator.getAngle(), 300 - oscillator.getAngle(), 8, 0, 0,
						new Enemy(-fWidth, 50, 3, 70 - mode.getBulletNumber(), new Color(100, 200, 200), border, fWidth, false), 
						new LinearBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(fHeight, 0),
								PathTraversal.LINEAR), 
								false, false);
				for (Enemy e: emp1.step()){
					enemies.add(e);
				}
				for (Enemy e: emp2.step()){
					enemies.add(e);
				}
				counter1 = 0;
			} else
				if (counter2 >= mode.getBulletNumber()){
					EnemyMovementPattern emp1 = new EnemyMovementPattern(
							new Point2D.Float(fWidth/2, FullGame.HEIGHT/3), -oscillator.getAngle(), 120 - oscillator.getAngle(), 8, 0, 0,
							new Enemy(-fWidth, 50, 3, 70 - mode.getBulletNumber(), new Color(200, 200, 100), border, fWidth, false), 
							new LinearBulletPath(
									new Point2D.Float(0, 0),
									new Point2D.Float(fHeight, 0),
									PathTraversal.LINEAR), 
									false, false);
					EnemyMovementPattern emp2 = new EnemyMovementPattern(
							new Point2D.Float(fWidth/2, FullGame.HEIGHT/3), 180 - oscillator.getAngle(), 300 - oscillator.getAngle(), 8, 0, 0,
							new Enemy(-fWidth, 50, 3, 70 - mode.getBulletNumber(), new Color(200, 200, 100), border, fWidth, false), 
							new LinearBulletPath(
									new Point2D.Float(0, 0),
									new Point2D.Float(fHeight, 0),
									PathTraversal.LINEAR), 
									false, false);
					for (Enemy e: emp1.step()){
						enemies.add(e);
					}
					for (Enemy e: emp2.step()){
						enemies.add(e);
					}
					counter2 = 0;
				}
			counter++;
			counter1++;
			counter2++;
			oscillator.oscillate();
			break;

		case 6:
			/**
			 * Orbit Sign: "Meteor Shower"
			 */
			if (timer <= duration * 3 / 5 && counter >= mode.getBulletNumber() * 2){
				EnemyMovementPattern emp1 = new EnemyMovementPattern(
						new Point2D.Float(player.x, player.y), angle, angle + 360, 16, 0, 0,
						new Enemy(-fWidth, 50, 2, 64, new Color(0, (int)(Math.abs(angleVel)/5 * 40), (int)(Math.abs(angleVel)/5 * 200)), border, fWidth, false, false), 
						new SpiralBulletPath(0, 0, 500, 0, angle, angle + 60, true), false);
				EnemyMovementPattern emp2 = new EnemyMovementPattern(
						new Point2D.Float(player.x, player.y), angle, angle + 360, 16, 0, 15,
						new Enemy(-fWidth, 50, (int)(Math.random()*2 + 2), Math.random() * 8 + 8, new Color((int)(Math.abs(angleVel)/5 * 200), 0, (int)(Math.abs(angleVel)/5 * 40)), border, fWidth, false, false), 
						new SpiralBulletPath(0, 0, 1000, 50, angle, angle + 120, true), false, false);
				for (Enemy e: emp1.step()){
					enemies.add(e);
				}
				for (Enemy e: emp2.step()){
					enemies.add(e);
				}
				counter = 0;
			}
			angle += angleVel;
			if (increaseOmega)
				if (angleVel < 5)
					angleVel += angleAccel;
				else
					increaseOmega = false;
			else
				if (angleVel > -5)
					angleVel -= angleAccel;
				else
					increaseOmega = true;
			counter++;
			break;

		case 7:
			/**
			 * Draco Sign: "Dragon's Breath"
			 */
			if (counter >= (mode.getBulletNumber()/10) + 50){
				Point2D.Float tempP = new Point2D.Float(fWidth/2, FullGame.HEIGHT/2);
				float theta = RotationalDirection.getAngleBetween(tempP, new Point2D.Float(player.x, player.y));
				Color c = new Color((int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30);
				LinearBulletPath path1 = new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(1000, 0), PathTraversal.QUADRATIC);
				LinearBulletPath path2 = new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(1000, 0), PathTraversal.ROOT);
				EnemyMovementPattern emp = new EnemyMovementPattern(
						tempP, (float) (theta+Math.PI/2), (float) (theta + Math.PI/2 + 0.001), 1, 0, 0,
						new EmbeddedEnemy(-fWidth, 50, 30, 64, c, border, fWidth, Integer.MAX_VALUE, false, 
								new EnemyMovementPattern(null, 0, 360, 8, 0, 0, 
										new Enemy(-fWidth, 5, 4, 16, c, border, fWidth, false), path2, false, false),
										mode.getBulletNumber()*3, null), path1, true, true);
				for (Enemy e: emp.step()){
					enemies.add(e);
				}
				counter = 0;
			}
			counter++;
			break;

		case 8:
			/**
			 * Kerr Sign: "Collapse"
			 */
			if (counter >= mode.getBulletNumber()){
				float radius = (float)((duration - timer))/(float)(duration);
				radius = radius*radius*radius*radius*radius*500.0f;
				EnemyMovementPattern emp = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, fHeight/2), angle, angle+360, (timer>duration*3/4)?24:(timer>duration/3?32:48), 0, 0,
								new Enemy(-fWidth, 50, 2, 128, new Color(10, 10, 10), border, fWidth, false, false), 
								new SpiralBulletPath(0, 0, 200.0f + radius, 
										35.0f + radius, angle, angle-60, 
										true, PathTraversal.ROOT), false);
				for (Enemy e: emp.step()){
					enemies.add(e);
				}
				counter = 0;
			}
			if (counter1 >= mode.getBulletNumber()*3){
				EnemyMovementPattern emp1 = new EnemyMovementPattern(
						new Point2D.Float(fWidth/4, FullGame.HEIGHT/4), oscillator.getAngle(), oscillator.getAngle() + 360, 8, 0, 0,
						new Enemy(-fWidth, 50, 3, 100 - mode.getBulletNumber(), new Color((int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30), border, fWidth, false), 
						new LinearBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(fHeight, 0),
								PathTraversal.LINEAR), 
								false, true);
				EnemyMovementPattern emp2 = new EnemyMovementPattern(
						new Point2D.Float(3*fWidth/4, FullGame.HEIGHT/4), oscillator.getAngle(), oscillator.getAngle() + 360, 8, 0, 0,
						new Enemy(-fWidth, 50, 3, 100 - mode.getBulletNumber(), new Color((int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30), border, fWidth, false), 
						new LinearBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(fHeight, 0),
								PathTraversal.LINEAR), 
								false, true);
				EnemyMovementPattern emp3 = new EnemyMovementPattern(
						new Point2D.Float(fWidth/4, 3*FullGame.HEIGHT/4), oscillator.getAngle(), oscillator.getAngle() + 360, 8, 0, 0,
						new Enemy(-fWidth, 50, 3, 100 - mode.getBulletNumber(), new Color((int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30), border, fWidth, false), 
						new LinearBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(fHeight, 0),
								PathTraversal.LINEAR), 
								false, true);
				EnemyMovementPattern emp4 = new EnemyMovementPattern(
						new Point2D.Float(3*fWidth/4, 3*FullGame.HEIGHT/4), oscillator.getAngle(), oscillator.getAngle() + 360, 8, 0, 0,
						new Enemy(-fWidth, 50, 3, 100 - mode.getBulletNumber(), new Color((int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30), border, fWidth, false), 
						new LinearBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(fHeight, 0),
								PathTraversal.LINEAR), 
								false, true);
				for (Enemy e: emp1.step()){
					enemies.add(e);
				}
				for (Enemy e: emp2.step()){
					enemies.add(e);
				}
				for (Enemy e: emp3.step()){
					enemies.add(e);
				}
				for (Enemy e: emp4.step()){
					enemies.add(e);
				}
				counter1 = 0;
			}
			counter++;
			counter1++;
			oscillator.oscillate();
			break;	

		case 9:
			/**
			 * Boa Sign: "Constriction"
			 */
			if (counter >= mode.getBulletNumber() * 2){
				EnemyMovementPattern emp1 = new EnemyMovementPattern(
						new Point2D.Float(player.x, player.y), angle, angle+360, 32, 0, 0,
						new Enemy(-fWidth, 50, 2, 64, new Color(0, (int)(Math.abs(angleVel)/5 * 40), (int)(Math.abs(angleVel)/5 * 200)), border, fWidth, false, false), 
						new SpiralBulletPath(0, 0, 150, 30, angle, angle-60, true, PathTraversal.ROOT), false);
				EnemyMovementPattern emp2 = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), angle, angle + 360, 16, 0, 15,
						new Enemy(-fWidth, 50, (int)(Math.random()*2 + 2), Math.random() * 4 + 4, new Color((int)(Math.abs(angleVel)/5 * 200), 0, (int)(Math.abs(angleVel)/5 * 40)), border, fWidth, false), 
						new BezierBulletPath(new Point2D.Float(0, 0),
								new Point2D.Float(750, increaseOmega?125:-125), 
								new Point2D.Float(1250, increaseOmega?-75:75)), false);
				for (Enemy e: emp1.step()){
					enemies.add(e);
				}
				for (Enemy e: emp2.step()){
					enemies.add(e);
				}
				counter = 0;
			}
			angle += angleVel;
			if (increaseOmega)
				if (angleVel < 5)
					angleVel += angleAccel;
				else
					increaseOmega = false;
			else
				if (angleVel > -5)
					angleVel -= angleAccel;
				else
					increaseOmega = true;
			counter++;
			break;

		case 10:
			/**
			 * Fusion Sign: "Fukushima Collapse"
			 */
			if (counter >= 75 + (mode.getBulletNumber()/10)*50){
				EnemyMovementPattern emp = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), angle, angle + 360, 8, 0, 0,
						new Enemy(-fWidth, 0, 75, 8 - (mode.getBulletNumber()/5), sunBullet, border, fWidth, null, false, false), 
						new SpiralBulletPath(0, 0, 30, 1000, angle, angle + (toggle?360:-360), true, PathTraversal.ROOT), false);
				for (Enemy e: emp.step()){
					enemies.add(e);
				}
				counter = 0;
				toggle = !toggle;
			}
			if (mode.equals(Mode.EXTRA) && counter1 >= 50){
				EnemyMovementPattern emp1 = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), angle, angle + 360, 8, 0, 0,
						new Enemy(-fWidth, 0, 10, 8, Color.ORANGE, border, fWidth, false), 
						new SpiralBulletPath(0, 0, 30, 1000, angle, angle + (toggle?-120:120), true, PathTraversal.LINEAR), false);
				for (Enemy e: emp1.step()){
					enemies.add(e);
				}
				counter1 = 0;
			}
			counter++;
			counter1++;
			break;

		case 11:
			/**
			 * Stargazer Sign: "Galaxxxy Burst"
			 */
			if (counter >= mode.getBulletNumber() / 3){
				EnemyMovementPattern emp1 = new EnemyMovementPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/3), oscillator.getAngle(), oscillator.getAngle() + 360, 16, 0, 0,
						new Enemy(-fWidth, 50, 3, 70 - mode.getBulletNumber(), new Color((int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30),
								border, fWidth, null, false, false), 
								new LinearBulletPath(
										new Point2D.Float(0, 0),
										new Point2D.Float(fHeight, 0),
										PathTraversal.LINEAR), 
										false, true);
				for (Enemy e: emp1.step()){
					enemies.add(e);
				}
				oscillator.oscillate();
				counter = 0;
			}
			counter++;
			break;

		case 12:
			/**
			 * Stargazer Sign: "Stardust Trail"
			 */
			if (timer <= duration * 3 / 4 && counter >= 150){
				Point2D.Float tempP = new Point2D.Float(fWidth/2, FullGame.HEIGHT/2);
				float theta = oscillator.getAngle();
				Color c = new Color((int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30, (int)(Math.random()*180) + 30);
				SpiralBulletPath path1 = new SpiralBulletPath(0, 0, 0, fHeight, 0, 120, true, PathTraversal.QUADRATIC);
				LinearBulletPath path2 = new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(fHeight, 0), PathTraversal.LINEAR);
				EnemyMovementPattern emp = new EnemyMovementPattern(
						tempP, theta, (float) (theta + 2 * Math.PI), 4, 0, 0,
						new EmbeddedEnemy(-fWidth, 50, 30, 64, c, border, fWidth, 100, false, 
								new EnemyMovementPattern(null, 0, 360, 8, 0, 0, 
										new Enemy(-fWidth, 5, 4, 16, c, border, fWidth, false), path2, false, false),
										mode.getBulletNumber()*3, oscillator), path1, true, true);
				for (Enemy e: emp.step()){
					enemies.add(e);
				}
				counter = 0;
			}
			oscillator.oscillate();
			counter++;
			break;

		case 13:
			/**
			 * TODO
			 */
			break;

		case 14:
			/**
			 * Mountain Sign: "Avalanche"
			 */
			if (counter >= (mode.getBulletNumber()/5 + 1)){
				double xStart = Math.random()*FullGame.WIDTH;
				double xTarget = xStart+Math.random()*400-200;
				enemies.add(new Enemy((int)xStart, (int)xTarget, (int)(Math.random()*20)+10, (Math.random()*10+5), new Color((int)(Math.random()*40), (int)(Math.random()*215) + 40, (int)(Math.random()*100) + 155), border, fWidth, true, true));
				counter = 0;
			}
			counter++;
			break;
		}
	}

	/**
	 * Is called once the game is started, which then runs the progression of this game sequentially on a single thread
	 * (forks do occur for processing optimization). The rendering of game objects is in essence looped through
	 * separately from the calculations, with the former process operating is a faster loop than the latter. Processing
	 * occurs in the following order:
	 * <ol>
	 * <li>Checks for player deaths and bullet collisions, as well as on-screen pickup items
	 * <li>Steps through player movements and actions (ie. shooting bullets, activating bombs)
	 * <li>Steps through all bullets and pickup items on screen
	 * </ol>
	 */
	public void run() {
		while(Thread.currentThread()==getThread()){
			update();
			repaint();
			if(exploding)
				rExp+=expSpeed;
			if(rExp==rExpMax){
				exploding=false;
				rExp=0;
				player.respawn();
			}
			if(invincible)
				invincibilityCounter++;
			if(invincibilityCounter>invincibilityFrames)
				invincible=false;
			try{
				Thread.sleep(mspf/2);
				if (!gameOver && !exploding && cardSelect < numCards)
					score+=50;
			}catch(Exception e){
//				e.printStackTrace();
				System.err.println("DEBUG: Game has ended");
			}
			repaint();
			try{
				Thread.sleep(mspf/2);
				if (!gameOver && !exploding && cardSelect < numCards)
					score+=50;
			}catch(Exception e){
//				e.printStackTrace();
				System.err.println("DEBUG: Game has ended");
			}
		}
	}

	/**
	 * Changes the difficulty setting
	 * @param m New game difficulty mode to set to
	 */
	public void setGameMode(Mode m){
		this.mode = m;
	}
	
	/**
	 * Called when the game ends. Disposes of the graphics allocated for the game.
	 */
	public void destroy(){
		parent=null;
		this.dispose();
	}
	
	/**
	 * @return The thread the game is run on
	 */
	public Thread getThread() {
		return thisThread;
	}

	/**
	 * Shifts the current thread running the game. This usually should not be used outside of game.
	 * @param thisThread The new thread to run the game on
	 */
	private void setThread(Thread thisThread) {
		this.thisThread = thisThread;
	}

}