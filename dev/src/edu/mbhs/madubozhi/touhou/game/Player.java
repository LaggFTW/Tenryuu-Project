package edu.mbhs.madubozhi.touhou.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;

import edu.mbhs.madubozhi.touhou.audio.SoundEffect;
import edu.mbhs.madubozhi.touhou.game.bullet.BarrierBullet;
import edu.mbhs.madubozhi.touhou.game.bullet.Bullet;
import edu.mbhs.madubozhi.touhou.game.bullet.BulletShape;
import edu.mbhs.madubozhi.touhou.game.bullet.PlayerLaunchedBullet;
import edu.mbhs.madubozhi.touhou.game.bullet.ShrinkingPlayerLaunchedBullet;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.ArcBulletPattern;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.BezierBulletPath;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.LinearBulletPath;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.SpiralBulletPath;
import edu.mbhs.madubozhi.touhou.game.enemy.PlayerLaunchedEnemy;
import edu.mbhs.madubozhi.touhou.game.util.HashArray;
import edu.mbhs.madubozhi.touhou.game.util.ImageHash;
import edu.mbhs.madubozhi.touhou.game.util.InputHandler;
import edu.mbhs.madubozhi.touhou.game.util.OscillationType;
import edu.mbhs.madubozhi.touhou.game.util.Oscillator;
import edu.mbhs.madubozhi.touhou.game.util.PathTraversal;
import edu.mbhs.madubozhi.touhou.ui.Ability;
import edu.mbhs.madubozhi.touhou.ui.Character;
import edu.mbhs.madubozhi.touhou.ui.FullGame;

/**
 * The character drawn in-game that interacts with game elements, which the user can control.
 * @author bowenzhi
 */
public class Player{
	public static final int PLAYER_STARTING_LIVES = 3;
	public static final int PLAYER_STARTING_BOMBS = 3;
	protected int x, y, xVel, yVel, size, rGraze;
	private Integer xRef, yRef;
	private boolean focus;
	private int respawnX, respawnY;
	private int nSpeed, fSpeed, nSize, fSize;
	private int border, fWidth, fHeight;
	private boolean screenWrap=false;
	private int nStr, fStr;
	protected int power;
	protected int lives;
	protected int bombs;
	private boolean sfx;
	protected boolean exploding;
	protected boolean invincible;
	protected boolean timestop;
	protected boolean bombing;
	private int shotCounter;
	private int bombCounter;
	private int invincibilityCounter;
	private int explosionCounter;
	private int timestopCounter;
	private int invincibilityFrames = 240;
	private int explosionFrames = 100;
	private int timestopFrames = 180;
	private Oscillator oscillator;
	protected Character c, c2;

	/**
	 * Instantiates this Player with the given initial conditions.
	 * @param x X-position
	 * @param y Y-position
	 * @param border Border width of the game frame
	 * @param fWidth Width of the game frame
	 * @param c Primary character
	 * @param c2 Secondary character
	 * @param sfx Whether or not to play sound effects
	 */
	public Player(int x, int y, int border, int fWidth, Character c, Character c2, boolean sfx){
		this.x = x; this.y = y; this.xRef = new Integer(x); this.yRef = new Integer(y);
		this.c = c; this.c2 = c2;
		this.sfx = sfx;
		this.border = border; this.fWidth = fWidth;
		this.focus = false;
		this.xVel=0; this.yVel=0;
		this.nSpeed = 4+(int)c.getSpeed();
		this.fSpeed = 2+(int)c2.getSpeed()/2;
		this.nSize = 12-(int)c.getHitBoxSize();
		this.fSize = 6-(int)c2.getHitBoxSize()/2;
		this.nStr = (int)(5+1.5*c.getStrength());
		this.fStr = (int)(5+1.5*c2.getStrength());
		this.rGraze=30;
		this.fHeight=(int)FullGame.HEIGHT-2*border;
		this.respawnX = fWidth/2+border;
		this.respawnY = FullGame.HEIGHT - 100;
		this.lives = PLAYER_STARTING_LIVES;
		this.bombs = PLAYER_STARTING_BOMBS;
	}
	
	/**
	 * Instantiates this Player with the given initial conditions.
	 * @param x X-position
	 * @param y Y-position
	 * @param border Border width of the game frame
	 * @param fWidth Width of the game frame
	 * @param c Primary character
	 * @param c2 Secondary character
	 */
	public Player(int x, int y, int border, int fWidth, Character c, Character c2){
		this(x, y, border, fWidth, c, c2, false);
	}

	public void render(GL gl){
		float sqrt2 = (float) Math.sqrt(2);
		boolean usingTextures = c.getSpriteTex() != null && c2.getSpriteTex() != null;
		Texture t1 = ImageHash.IMG.getTex("inner_basic");
		if (!usingTextures){
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			Texture t0 = ImageHash.IMG.getTex("outer_basic");
			t0.enable();
			t0.bind();
			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(0, 0);
			gl.glVertex2f(this.x - (rGraze+(exploding?explosionCounter/2:0))/sqrt2, this.y - (rGraze+(exploding?explosionCounter/2:0))/sqrt2);
			gl.glTexCoord2f(1, 0);
			gl.glVertex2f(this.x + (rGraze+(exploding?explosionCounter/2:0))/sqrt2, this.y - (rGraze+(exploding?explosionCounter/2:0))/sqrt2);
			gl.glTexCoord2f(1, 1);
			gl.glVertex2f(this.x + (rGraze+(exploding?explosionCounter/2:0))/sqrt2, this.y + (rGraze+(exploding?explosionCounter/2:0))/sqrt2);
			gl.glTexCoord2f(0, 1);
			gl.glVertex2f(this.x - (rGraze+(exploding?explosionCounter/2:0))/sqrt2, this.y + (rGraze+(exploding?explosionCounter/2:0))/sqrt2);
			gl.glEnd();
			t0.disable();
		} else {
			if (!exploding){
				gl.glColor4f(1.0f, 1.0f, 1.0f, !bombing&&invincible?(invincibilityCounter%9)/8.0f:1.0f);
				Texture t = focus?c2.getSpriteTex():c.getSpriteTex();
				t.enable();
				t.bind();
				gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2f(0, 0);
				gl.glVertex2f(this.x - rGraze/sqrt2, this.y - rGraze/sqrt2);
				gl.glTexCoord2f(1, 0);
				gl.glVertex2f(this.x + rGraze/sqrt2, this.y - rGraze/sqrt2);
				gl.glTexCoord2f(1, 1);
				gl.glVertex2f(this.x + rGraze/sqrt2, this.y + rGraze/sqrt2);
				gl.glTexCoord2f(0, 1);
				gl.glVertex2f(this.x - rGraze/sqrt2, this.y + rGraze/sqrt2);
				gl.glEnd();
				t.disable();
			} else {
				gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				Texture t0 = ImageHash.IMG.getTex("orb_red");
				t0.enable();
				t0.bind();
				gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2f(0, 0);
				gl.glVertex2f(this.x - (rGraze+(exploding?explosionCounter/2:0))/sqrt2, this.y - (rGraze+(exploding?explosionCounter/2:0))/sqrt2);
				gl.glTexCoord2f(1, 0);
				gl.glVertex2f(this.x + (rGraze+(exploding?explosionCounter/2:0))/sqrt2, this.y - (rGraze+(exploding?explosionCounter/2:0))/sqrt2);
				gl.glTexCoord2f(1, 1);
				gl.glVertex2f(this.x + (rGraze+(exploding?explosionCounter/2:0))/sqrt2, this.y + (rGraze+(exploding?explosionCounter/2:0))/sqrt2);
				gl.glTexCoord2f(0, 1);
				gl.glVertex2f(this.x - (rGraze+(exploding?explosionCounter/2:0))/sqrt2, this.y + (rGraze+(exploding?explosionCounter/2:0))/sqrt2);
				gl.glEnd();
				t0.disable();
			}
		}
		if (usingTextures && !exploding){
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			t1 = ImageHash.IMG.getTex("orb_red");
		} else {
			gl.glColor4f(invincible&&!exploding?(invincibilityCounter/5)%2==0?0.0f:1.0f:1.0f, 1.0f, exploding?0.0f:1.0f, 1.0f);
		}
		t1.enable();
		t1.bind();
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(0, 0);
		gl.glVertex2f(this.x - (size+(exploding?explosionCounter/2:0)), this.y - (size+(exploding?explosionCounter/2:0)));
		gl.glTexCoord2f(1, 0);
		gl.glVertex2f(this.x + (size+(exploding?explosionCounter/2:0)), this.y - (size+(exploding?explosionCounter/2:0)));
		gl.glTexCoord2f(1, 1);
		gl.glVertex2f(this.x + (size+(exploding?explosionCounter/2:0)), this.y + (size+(exploding?explosionCounter/2:0)));
		gl.glTexCoord2f(0, 1);
		gl.glVertex2f(this.x - (size+(exploding?explosionCounter/2:0)), this.y + (size+(exploding?explosionCounter/2:0)));
		gl.glEnd();
		t1.disable();
	}

	/**
	 * Steps through the player's movements, applying keyboard controls. This is called every frame.
	 * @param input InputHandler object corresponding to the input from the user during this game session
	 */
	public void step(InputHandler input){
		applyKeyboardControls(input);
		size=(focus?fSize:nSize);
		power=(int)(focus?fStr:nStr);
		x += xVel; 	y += yVel;
		if(screenWrap){
			if(x+size>fWidth-border)x=size+border;
			if(x-size<border)x=fWidth-border-size;
		}else{
			if(x+size>fWidth-border)x=fWidth-border-size;
			if(x-size<border)x=size+border;
		}
		if(y+size>fHeight+border)y=fHeight+border-size;
		if(y-size<border)y=size+border;
		xRef = new Integer(x);
		yRef = new Integer(y);
	}
	
	/**
	 * Steps through the player's movements, applying keyboard controls, and then through the player's shot bullets
	 * and/or bombs.
	 * @param input InputHandler object corresponding to the input from the user during this game session
	 * @param bullets Reference for the game's master HashArray of bullets present in the game
	 * @param barrier Reference to a BarrierBullet, in the case that the player's defensive bomb utilizes it
	 */
	public void stepWithBullets(InputHandler input, HashArray<Texture, PlayerLaunchedBullet> bullets, BarrierBullet[] barrier){
		if (!exploding){
			this.applyAbilities(input, barrier);
			if (!bombing){
				this.step(input);
			}
			if (input.getShoot())
				this.bulletShot(bullets);
			this.applyBombs(input, bullets);
			shotCounter++;
			bombCounter -= bombCounter==0?0:1;
		}
		checkTimers();
	}

	/**
	 * @return The hitbox of the character
	 */
	public Rectangle2D getArea(){
		double sqrt2 = Math.sqrt(2);
		return new Rectangle2D.Double(x-(size/sqrt2), y-(size/sqrt2), 2*size/sqrt2, 2*size/sqrt2);//Note: 1.414 is approximately sqrt(2), used as an optimization
	}

	/**
	 * @return The graze hitbox of the character
	 */
	public Rectangle2D getGrazeArea(){
		return new Rectangle2D.Double(x-size-rGraze/2, y-size-rGraze/2, 2*(size+rGraze), 2*(size+rGraze));
	}

	/**
	 * Calculates and updates character position based on keyboard input.
	 * @param input Keyboard input to use
	 */
	private void applyKeyboardControls(InputHandler input){
		boolean fwd, bwd, lft, rgt;
		fwd = input.getFwd(); 	bwd = input.getBwd();
		lft = input.getLft(); 	rgt = input.getRgt();
		focus = input.getFocus();
		if (fwd && bwd) {fwd = false; bwd = false;}
		if (lft && rgt) {lft = false; rgt = false;}
		if (fwd && rgt) 		setSpeed(1, -1);
		else if (fwd && lft)	setSpeed(-1, -1);
		else if (bwd && rgt) 	setSpeed(1, 1);
		else if (bwd && lft) 	setSpeed(-1, 1);
		else if (fwd) 			setSpeed(0, -1);
		else if (bwd) 			setSpeed(0, 1);
		else if (lft) 			setSpeed(-1, 0);
		else if (rgt) 			setSpeed(1, 0);
		else 					setSpeed(0, 0);
	}

	/**
	 * Sets the speed of the character
	 * @param xS X Speed
	 * @param yS Y Speed
	 */
	private void setSpeed(int xS, int yS){
		xVel = (focus?xS*fSpeed:xS*nSpeed);
		yVel = (focus?yS*fSpeed:yS*nSpeed);
		size = (focus?fSize:nSize);
	}
	
	/**
	 * Adds bullets onto the screen if the user has shot.
	 * @param launchedBulletsIn Reference for the game's master HashArray of bullets present in the game 
	 */
	private void bulletShot(HashArray<Texture, PlayerLaunchedBullet> launchedBulletsIn){
		//XXX Temporary
		if (shotCounter >= 5){
//			if (sfx) {new SoundEffect(2).playToStop();}
			PlayerLaunchedBullet plb1 = new PlayerLaunchedBullet(100, BulletShape.ORB_SMALL, "", power, true, true);
			PlayerLaunchedBullet plb2 = new PlayerLaunchedBullet(100, BulletShape.ORB_SMALL, "", power, true, true);
			PlayerLaunchedBullet plb3 = new PlayerLaunchedBullet(100, BulletShape.ORB_SMALL, "", power, true, true);
			plb1.addPath(new BezierBulletPath(new Point2D.Float(x, y),
									new Point2D.Float(x - (focus?fWidth/20:fWidth/3), y - fHeight / 2),
									new Point2D.Float(x - (focus?fWidth/25:fWidth/4), y - fHeight)));
			plb2.addPath(new LinearBulletPath(new Point2D.Float(x, y),
									new Point2D.Float(x, y - fHeight)));
			plb3.addPath(new BezierBulletPath(new Point2D.Float(x, y),
									new Point2D.Float(x + (focus?fWidth/20:fWidth/3), y - fHeight / 2),
									new Point2D.Float(x + (focus?fWidth/25:fWidth/4), y - fHeight)));
			launchedBulletsIn.put(plb1.getSprite(), plb1);
			launchedBulletsIn.put(plb2.getSprite(), plb2);
			launchedBulletsIn.put(plb3.getSprite(), plb3);
			shotCounter = 0;
		}
		//XXX Temporary
	}
	
	private void applyAbilities(InputHandler input, BarrierBullet[] barrier){
		Character currentChar = focus?c2:c;
		if (currentChar.getAbility().equals(Ability.BORDERLESS)){
			screenWrap = true;
		} else {
			screenWrap = false;
			if (bombs >= 1 && input.defBomb()){
				boolean b = false;
				if (currentChar.getAbility().equals(Ability.BARRIER)){
					b = barrier[0]==null;
					barrier[0] = b?new BarrierBullet(this, 3):barrier[0];
				}
				if (currentChar.getAbility().equals(Ability.DODGE)){
					b = !invincible;
					if (b){
						invincible = true;
						invincibilityCounter = 0;
					}
				}
				if (currentChar.getAbility().equals(Ability.TIMESTOP)){
					b = !timestop;
					if (b){
						timestop = true;
						timestopCounter = 0;
					}
				}
				if (b){
					if (sfx) {new SoundEffect(3).playToStop();}
				}
				bombs -= b?1:0;
			}
		}
	}
	
	private void applyBombs(InputHandler input, HashArray<Texture, PlayerLaunchedBullet> launchedBulletsIn){
		Character currentChar = focus?c2:c;
		if (bombing){
			if (bombCounter%30==29){
				if (sfx) {new SoundEffect(4).playToStop();}
			}
			switch (currentChar.charCode){
			/**
			 * Asami: Lotus Sign "Sakura Blast"
			 */
			case 0:
				if (bombCounter >= 60 && bombCounter%5==0){
					float angle = oscillator.getAngle();
					ArcBulletPattern arc1 = new ArcBulletPattern(
							new Point2D.Float(this.x, this.y), angle, angle + 360, 16, 0, 0,
							new PlayerLaunchedBullet(8, BulletShape.SAKURA, "random", 0, false, true), 
							new SpiralBulletPath(0, 0, 30, 800, angle, angle + 720, true, PathTraversal.ROOT), false, false);
					ArcBulletPattern arc2 = new ArcBulletPattern(
							new Point2D.Float(this.x, this.y), angle, angle + 360, 16, 0, 0,
							new PlayerLaunchedBullet(8, BulletShape.SAKURA, "random", 0, false, true), 
							new SpiralBulletPath(0, 0, 30, 800, angle, angle - 720, true, PathTraversal.ROOT), false, false);
					for (Bullet b:arc1.step()){
						launchedBulletsIn.put(b.getSprite(), (PlayerLaunchedBullet)(b));
					}
					for (Bullet b:arc2.step()){
						launchedBulletsIn.put(b.getSprite(), (PlayerLaunchedBullet)(b));
					}
				}
				if (bombCounter%15==0){
					Dimension d = new Dimension((180 - bombCounter)*4, (180 - bombCounter)*4);
					PlayerLaunchedBullet plb = new PlayerLaunchedBullet(100, d, ImageHash.IMG.getTex("blank"), d, power*12, true, false);
					plb.addPath(new LinearBulletPath(new Point2D.Float(this.x, this.y), new Point2D.Float(this.x, this.y)));
					launchedBulletsIn.put(plb.getSprite(), plb);
				}
				break;
			/**
			 * Jin: Sword Sign: "Brandish"
			 */
			case 1:
				if (bombCounter%10==0){
					boolean toggle = bombCounter%20==0;
					ArcBulletPattern arc1 = new ArcBulletPattern(
							new Point2D.Float(this.x, this.y), 0, 1, 1, 0, 0,
							new PlayerLaunchedBullet(1000, new Dimension(40, 16), ImageHash.IMG.getTex("diamond_blue"), new Dimension(30, 12), power, false, true),
							new SpiralBulletPath(0, 0, 20, 20, toggle?-10:-170, toggle?-170:-10, true),
							false, false);
					ArcBulletPattern arc2 = new ArcBulletPattern(
							new Point2D.Float(this.x, this.y), 0, 1, 1, 0, 0,
							new PlayerLaunchedBullet(1000, new Dimension(40, 16), ImageHash.IMG.getTex("diamond_cyan"), new Dimension(30, 12), power, false, true),
							new SpiralBulletPath(0, 0, 40, 40, toggle?-10:-170, toggle?-170:-10, true),
							false, false);
					ArcBulletPattern arc3 = new ArcBulletPattern(
							new Point2D.Float(this.x, this.y), 0, 1, 1, 0, 0,
							new PlayerLaunchedBullet(1000, new Dimension(40, 16), ImageHash.IMG.getTex("diamond_green"), new Dimension(30, 12), power, false, true),
							new SpiralBulletPath(0, 0, 60, 60, toggle?-10:-170, toggle?-170:-10, true),
							false, false);
					ArcBulletPattern arc4 = new ArcBulletPattern(
							new Point2D.Float(this.x, this.y), 0, 1, 1, 0, 0,
							new PlayerLaunchedBullet(1000, new Dimension(40, 16), ImageHash.IMG.getTex("diamond_yellow"), new Dimension(30, 12), power, false, true),
							new SpiralBulletPath(0, 0, 80, 80, toggle?-10:-170, toggle?-170:-10, true),
							false, false);
					ArcBulletPattern arc5 = new ArcBulletPattern(
							new Point2D.Float(this.x, this.y), 0, 1, 1, 0, 0,
							new PlayerLaunchedBullet(1000, new Dimension(40, 16), ImageHash.IMG.getTex("diamond_orange"), new Dimension(30, 12), power, false, true),
							new SpiralBulletPath(0, 0, 100, 100, toggle?-10:-170, toggle?-170:-10, true),
							false, false);
					ArcBulletPattern arc6 = new ArcBulletPattern(
							new Point2D.Float(this.x, this.y), 0, 1, 1, 0, 0,
							new PlayerLaunchedBullet(1000, new Dimension(40, 16), ImageHash.IMG.getTex("diamond_red"), new Dimension(30, 12), power, false, true),
							new SpiralBulletPath(0, 0, 120, 120, toggle?-10:-170, toggle?-170:-10, true),
							false, false);
					for (Bullet b:arc1.step()){
						launchedBulletsIn.put(b.getSprite(), (PlayerLaunchedBullet)(b));
					}
					for (Bullet b:arc2.step()){
						launchedBulletsIn.put(b.getSprite(), (PlayerLaunchedBullet)(b));
					}
					for (Bullet b:arc3.step()){
						launchedBulletsIn.put(b.getSprite(), (PlayerLaunchedBullet)(b));
					}
					for (Bullet b:arc4.step()){
						launchedBulletsIn.put(b.getSprite(), (PlayerLaunchedBullet)(b));
					}
					for (Bullet b:arc5.step()){
						launchedBulletsIn.put(b.getSprite(), (PlayerLaunchedBullet)(b));
					}
					for (Bullet b:arc6.step()){
						launchedBulletsIn.put(b.getSprite(), (PlayerLaunchedBullet)(b));
					}
				}
				break;
			/**
			 * Mizuki: Shuriken Sign: "Gleaming Blades"
			 */
			case 2:
				if (bombCounter%2==0){
					float rand = (float)(Math.random()*360);
					ArcBulletPattern arc = new ArcBulletPattern(
							new Point2D.Float(this.x, this.y), rand, rand + 360, 8, 0, 30,
							new PlayerLaunchedBullet(50, BulletShape.BLADE_SMALL, "blue", power*5, false, true), 
							new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(FullGame.WIDTH, FullGame.WIDTH), PathTraversal.LINEAR), 
									false, false);
					for (Bullet b: arc.step()){
						launchedBulletsIn.put(b.getSprite(), (PlayerLaunchedBullet)(b));
					}	
				}
				break;
			/**
			 * Kaito: Savage Sign: "Mortal Blow"
			 */
			case 3:
				if (bombCounter%2==0){
					float rand = (float)(Math.random()*360);
					ArcBulletPattern arc = new ArcBulletPattern(
							new Point2D.Float(this.x, this.y), rand, rand + 360, (int)(Math.random()*4)+1, 0, 30,
							new ShrinkingPlayerLaunchedBullet(5000, BulletShape.ORB_LARGE, "orange", power, 0.9f, true, true, false),
							new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(10, 10)), false, false);
					for (Bullet b:arc.step()){
						launchedBulletsIn.put(b.getSprite(), (PlayerLaunchedBullet)(b));
					}
				}
				break;
			default: break;
			}
			if (oscillator != null){
				oscillator.oscillate();
			}
		} else {
			if (bombs >= 2 && input.offBomb()){
				bombing = true;
				bombs -= 2;
				switch (currentChar.charCode){
				/**
				 * Asami
				 */
				case 0: bombCounter = 180; oscillator = new Oscillator(OscillationType.COSINE, 0, 0, 0, 0, 30, 360);
					break;
				/**
				 * Jin
				 */
				case 1: bombCounter = 300; oscillator = null;
					break;
				/**
				 * Mizuki
				 */
				case 2: bombCounter = 120; oscillator = null;
					break;
				/**
				 * Kaito
				 */
				case 3: bombCounter = 240; oscillator = null;
					break;
				default: break;
				}
			}
		}
	}
	
	public void death() {
		if (sfx) {new SoundEffect(5).playToStop();}
		lives--;
		exploding = true;
		explosionCounter = 0;
		invincible = true;
		invincibilityCounter = 0;
		timestop = false;
		bombs = PLAYER_STARTING_BOMBS;
	}
	
	private void checkTimers(){
		if (bombing){
			if (bombCounter > 0){
				invincible = true;
				invincibilityCounter = invincibilityFrames*3/4;
			} else {
				bombing = false;
			}
		} else {
			if (invincible){
				if (invincibilityCounter < invincibilityFrames){
					invincibilityCounter++;
				} else {
					invincible = false;
				}
			}
		}
		if (exploding){
			if (explosionCounter < explosionFrames){
				explosionCounter++;
			} else {
				exploding = false;
			}
		}
		if (timestop){
			if (timestopCounter < timestopFrames){
				timestopCounter++;
			} else {
				timestop = false;
			}
		}
	}
	
	/**
	 * @return Reference to the player's x position (useful for tracking the player's movements)
	 */
	public Integer getX(){
		return xRef;
	}
	
	/**
	 * @return Reference to the player's y position (useful for tracking the player's movements)
	 */
	public Integer getY(){
		return yRef;
	}
	
	public int getR(){
		return size;
	}
	
	public int getLives(){
		return lives;
	}
	
	public boolean getFocus(){
		return focus;
	}
	
	public float getTimestopEffect(){
		if (!timestop){
			return 1.0f;
		} else {
			float intermediateFrames = timestopFrames/5.0f;
			if (timestopCounter < intermediateFrames){
				return (intermediateFrames - timestopCounter)/intermediateFrames;
			}
			if (timestopFrames - timestopCounter < intermediateFrames){
				return (intermediateFrames - (timestopFrames - timestopCounter))/intermediateFrames;
			}
			return 0.0f;
		}
	}

	/**
	 * Respawns the character at the default respawn point. Occurs after death.
	 */
	public void respawn(){
		x = respawnX;
		y = respawnY;
		xRef = new Integer(x);
		yRef = new Integer(y);
	}
	
	/**
	 * NOTE: This method is outdated. Use stepWithBullets instead.
	 * <p>Steps through the player's movements, applying keyboard controls, and then through the player's shot enemies.
	 * @param input InputHandler object corresponding to the input from the user during this game session
	 * @param bullets Reference for the game's master ArrayList of enemies present in the game
	 */
	public void stepWithEnemies(InputHandler input, ArrayList<PlayerLaunchedEnemy> enemies){
		this.step(input);
		if (input.getShoot())
			this.enemyShot(enemies);
		shotCounter++;
	}
	
	/**
	 * NOTE: This method is outdated. Use bulletShot instead.
	 * <p>Adds enemies onto the screen if the user has shot / used a bomb.
	 * @param launchedBulletsIn Referenece for the game's master ArrayList of enemies present in the game
	 */
	private void enemyShot(ArrayList<PlayerLaunchedEnemy> launchedEnemiesIn){
		if (shotCounter >= 5){
			launchedEnemiesIn.add(
					new PlayerLaunchedEnemy(
							-50, -50, 4, 150, Color.RED, border, fWidth, power,
							new BezierBulletPath(new Point2D.Float(x, y),
									new Point2D.Float(x - (focus?fWidth/20:fWidth/3), y - fHeight / 2),
									new Point2D.Float(x - (focus?fWidth/25:fWidth/4), y - fHeight)),
									true, true)
					);
			launchedEnemiesIn.add(
					new PlayerLaunchedEnemy(
							-50, -50, 4, 150, Color.RED, border, fWidth, power,
							new LinearBulletPath(new Point2D.Float(x, y),
									new Point2D.Float(x, y - fHeight)),
									true, true)
					);
			launchedEnemiesIn.add(
					new PlayerLaunchedEnemy(
							-50, -50, 4, 150, Color.RED, border, fWidth, power,
							new BezierBulletPath(new Point2D.Float(x, y),
									new Point2D.Float(x + (focus?fWidth/20:fWidth/3), y - fHeight / 2),
									new Point2D.Float(x + (focus?fWidth/25:fWidth/4), y - fHeight)),
									true, true)
					);
			shotCounter = 0;
		}
	}
	
}