package edu.mbhs.madubozhi.touhou.game;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.texture.Texture;

import edu.mbhs.madubozhi.touhou.audio.MPlayer;
import edu.mbhs.madubozhi.touhou.game.bullet.BarrierBullet;
import edu.mbhs.madubozhi.touhou.game.bullet.Boss;
import edu.mbhs.madubozhi.touhou.game.bullet.Bullet;
import edu.mbhs.madubozhi.touhou.game.bullet.EmbeddedBullet;
import edu.mbhs.madubozhi.touhou.game.bullet.EnemyBullet;
import edu.mbhs.madubozhi.touhou.game.bullet.PlayerLaunchedBullet;
import edu.mbhs.madubozhi.touhou.game.bullet.ShrinkingPlayerLaunchedBullet;
import edu.mbhs.madubozhi.touhou.game.parser.LevelParser;
import edu.mbhs.madubozhi.touhou.game.util.HashArray;
import edu.mbhs.madubozhi.touhou.game.util.ImageHash;
import edu.mbhs.madubozhi.touhou.game.util.InputHandler;
import edu.mbhs.madubozhi.touhou.game.util.Mode;
import edu.mbhs.madubozhi.touhou.ui.Character;
import edu.mbhs.madubozhi.touhou.ui.FullGame;
import edu.mbhs.madubozhi.touhou.ui.GameWindow;

/**
 * Final version of the Game, which implements all classes from other packages. It implements the engine of the game,
 * and contains heuristics for handling collisions, rendering, and scoring. It implements the GLCanvas class, and uses
 * OpenGL to render graphics.
 * @author bowenzhi
 *
 */
public class FinalGame extends GLCanvas {
	private static final long serialVersionUID = -8395759457708163217L;
	
	private int border = 10;
	private int fWidth = (int)(FullGame.WIDTH*0.7);
	private int fHeight = FullGame.HEIGHT;
	
	private Player player;
	private InputHandler input;
	private FPSAnimator animator;
	private GameWindow window;
	private LevelParser parser;
	
	private HashArray<Texture, Bullet> bullets = new HashArray<Texture, Bullet>(1000);
	private HashArray<Texture, Bullet> bulletsToAdd = new HashArray<Texture, Bullet>(100);
	private HashArray<Texture, PlayerLaunchedBullet> playerBullets = new HashArray<Texture, PlayerLaunchedBullet>(500);
	private BarrierBullet[] barrier= new BarrierBullet[1];
	
	private Mode mode;
	private int stage;
	private int hits, score, graze;
	
	private boolean bgm, sfx;
	private MPlayer mplayer;
	
	private boolean gameOver;
	private boolean spawning;
	
	private int deathPenalty = 0;
	
	public FinalGame(Mode m, int stage, GameWindow par, GLCapabilities glc, boolean bgm, boolean sfx) {
		super(glc);
		this.mode = m;
		this.window = par;
		this.stage = stage;
		this.bgm = bgm;
		this.sfx = sfx;
		if (bgm){
			mplayer = new MPlayer();
		}
		init();
		this.addGLEventListener(new GLEventListener(){

			@Override
			public void display(GLAutoDrawable gla) {
				GL gl = gla.getGL();
				step();
				render(gl);
			}

			@Override
			public void displayChanged(GLAutoDrawable gla, boolean modeChanged,	boolean deviceChanged) {
				
			}

			@Override
			public void init(GLAutoDrawable gla) {
				GL gl = gla.getGL();
				gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
				gl.glDisable(GL.GL_DEPTH_TEST);
				gl.glEnable(GL.GL_FRAMEBUFFER_SRGB_EXT);
				gl.glEnable(GL.GL_BLEND);
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				gl.glShadeModel(GL.GL_SMOOTH);
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
				gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
				gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
				GLU glu = new GLU();
				glu.gluOrtho2D(0, fWidth, fHeight, 0);
				ImageHash.IMG.rehash();
			}

			@Override
			public void reshape(GLAutoDrawable gla, int x, int y, int width, int height) {
				
			}
			
		});
		this.animator = new FPSAnimator(this, FullGame.FPS);
		this.animator.start();
	}
	
	private void init(){
		input = new InputHandler(this);
		initGame();
		this.setSize(fWidth, fHeight);
		Dimension d = new Dimension(fWidth, fHeight);
		this.setPreferredSize(d);
		this.setMinimumSize(d);
		this.setMaximumSize(d);
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - FullGame.WIDTH)/2, (Toolkit.getDefaultToolkit().getScreenSize().height - FullGame.HEIGHT)/2);
	}
	
	private void initGame(){
		gameOver = false;
		spawning = true;
		if (window.parent() != null){
			player = new Player(fWidth/2, 4*fHeight/5, border, fWidth, window.parent().getMC(), window.parent().getSC(), sfx);
		} else {
			player = new Player(fWidth/2, 4*fHeight/5, border, fWidth, Character.ASAMI, Character.JIN, sfx);
		}
		score = 0;
		graze = 0;
		hits = 0;
		clearBullets();
		try {
			parser = new LevelParser(new FileReader(new File("level"+stage+".dat")), mode, player);
		} catch (FileNotFoundException e) {
			System.err.println("Missing level data file: " + "level"+stage+".dat");
		}
		if (bgm){
			mplayer.changeAudio(new File("audio/bgm/level 0"+stage+"a.mp3"));
		}
	}
	
	private void advanceLevel(){
		if (stage < 6){
			stage++;
			int lives = player.lives;
			if (window.parent() != null){
				player = new Player(fWidth/2, 4*fHeight/5, border, fWidth, window.parent().getMC(), window.parent().getSC(), sfx);
			} else {
				player = new Player(fWidth/2, 4*fHeight/5, border, fWidth, Character.ASAMI, Character.JIN, sfx);
			}
			player.lives = lives;
			clearBullets();
			try {
				parser = new LevelParser(new FileReader(new File("level"+stage+".dat")), mode, player);
			} catch (FileNotFoundException e) {
				System.err.println("Missing level data file: " + "level"+stage+".dat");
			}
			if (bgm){
				mplayer.changeAudio(new File("audio/bgm/level 0"+stage+"a.mp3"));
			}
		}
	}
	
	private void step(){
		if (player.bombing){
			clearSimpleBullets();
		}
		updateBullets();
		updatePlayerBullets();
		checkParserExecution();
		bulletsToAdd = new HashArray<Texture, Bullet>(100);
		if (!gameOver){
			player.stepWithBullets(input, playerBullets, barrier);
		}
		if (!gameOver && parser != null && !player.timestop){
			parser.step(bullets);
		}
		if (bgm && parser.getBossSignal()){
			mplayer.changeAudio(new File("audio/bgm/level 0"+stage+"b.mp3"));
		}
		if (parser.isFinished()){
			advanceLevel();
		}
		this.window.scorePanel().setGameInfo(hits, score, graze, stage, player.lives, player.bombs);
	}

	private void updateBullets(){
		for (Texture t:bullets.getKeys()){
			ArrayList<Bullet> removeMe = new ArrayList<Bullet>();
			ArrayList<PlayerLaunchedBullet> playerRemove = new ArrayList<PlayerLaunchedBullet>();
			for (Bullet b:bullets.get(t)){
				if(b.isFinished()){
					removeMe.add(b);
				}
				else {
					if(barrier[0] !=null && !barrier[0].isDestroyed() && barrier[0].checkCollision(b)){
						if (b instanceof EnemyBullet){
							((EnemyBullet)(b)).takeDamage(player.power);
							if (((EnemyBullet)(b)).isDestroyed()){
								removeMe.add(b);
							}
						} else {
							removeMe.add(b);
						}
					}
					else {
						if(checkCollision(b) && !player.invincible && !gameOver){
							hits++;
							player.death();
							score -= score<deathPenalty?(score):deathPenalty;
							if (!(b instanceof EnemyBullet))
								removeMe.add(b);
							if (player.lives<0){
								gameOver = true;
								window.parent().setGameRunning(false);
							}
						} else {
							if (b instanceof EnemyBullet){
								for (PlayerLaunchedBullet plb: playerBullets.getAll()){
									boolean bool = ((EnemyBullet) b).checkCollision(plb);
									if (bool && !playerRemove.contains(plb))
										playerRemove.add(plb);
								}
								if (((EnemyBullet) b).isDestroyed()){
									removeMe.add(b);
									score += ((EnemyBullet)b).scoreValue();
									continue;
								}
							}
							if (!player.timestop && b instanceof EmbeddedBullet){
								ArrayList<Bullet> tempArr = ((EmbeddedBullet)b).launchPattern();
								if (tempArr != null)
									for (Bullet bullet: tempArr)
										bulletsToAdd.put(bullet.getSprite(), bullet);
							}
							if (!player.timestop){
								b.step(FullGame.MILLIS_PER_FRAME);
							} else {
								b.step(FullGame.MILLIS_PER_FRAME * player.getTimestopEffect());
							}
							if(!gameOver && !player.exploding && !player.invincible && !player.timestop && !b.isGrazed() && checkGraze(b)){
								b.graze();
								graze++;
								score+=10;
							}
						}
					}
				}
			}
			for (PlayerLaunchedBullet b: playerRemove){
				playerBullets.removeValue(b);
			}
			for (Bullet b:removeMe){
				if (bullets.get(t) != null){
					bullets.get(t).remove(b);
				}
			}
		}
		bullets.combine(bulletsToAdd);
	}
	
	public void updatePlayerBullets(){
		ArrayList<PlayerLaunchedBullet> playerRemove = new ArrayList<PlayerLaunchedBullet>();
		for (PlayerLaunchedBullet b: playerBullets.getAll()){
			if (b.isFinished()){
				playerRemove.add(b);
			}
			else {
				if (!player.timestop || b instanceof ShrinkingPlayerLaunchedBullet){
					b.step(FullGame.MILLIS_PER_FRAME);
				} else {
					b.step(FullGame.MILLIS_PER_FRAME * player.getTimestopEffect());
				}
			}
		}
		for (PlayerLaunchedBullet b: playerRemove){
			playerBullets.removeValue(b);
		}
		if (barrier[0] != null){
			if (barrier[0].isDestroyed()){
				barrier[0]= null;
			} else {
				barrier[0].step(FullGame.MILLIS_PER_FRAME);
			}
		}
	}

	/**
	 * Clears away bullets currently on the screen, and gives the user some points for every bullet cleared if at the
	 * end of a spellcard (patterns)
	 */
	private void clearBullets(){
		score += gameOver||player.exploding?0:bullets.size() * 50;
		bullets = new HashArray<Texture, Bullet>(500);
		playerBullets = new HashArray<Texture, PlayerLaunchedBullet>(500);
	}
	
	private void clearSimpleBullets(){
		for (Texture t:bullets.getKeys()){
			ArrayList<Bullet> bs = bullets.get(t);
			ArrayList<Bullet> toRemove = new ArrayList<Bullet>();
			for (Bullet b:bs){
				if (b instanceof EnemyBullet || b instanceof Boss){
					continue;
				}
				toRemove.add(b);
			}
			for (Bullet b:toRemove){
				bs.remove(b);
			}
		}
	}
	
	/**
	 * Basic method for collision detection between the player and a specified bullet
	 * @param e Enemy to check collisions against
	 * @return true if the player collides with the bullet, else false
	 */
	private boolean checkCollision(Bullet b){
		return checkRange(b, 250)?b.getHitboxArea().intersects(player.getArea()):false;
	}

	/**
	 * Checks if the graze (close to getting hit) hitbox intersects with a specified bullet
	 * @param e Enemy to check grazes against
	 * @return true if the player is close enough to the bullet for grazing to occur, else false
	 */
	private boolean checkGraze(Bullet b){
		return checkRange(b, 250)?b.getHitboxArea().intersects(player.getGrazeArea()):false;
	}
	
	/**
	 * Checks if the bullet is close enough to the player to warrant collision detection
	 * @param e Enemy
	 * @param radius Maximum radius of the area from the player
	 * @return Whether or not the bullet is in range to warrant collision detection
	 */
	private boolean checkRange(Bullet b, int radius){
		return (new Rectangle2D.Float(player.x - radius, player.y - radius, radius * 2, radius * 2).contains(b.getHitboxArea().getBounds2D()));
	}
	
	private void checkParserExecution() {
		int enemyCount = 0;
		boolean hasBoss = false;
		for (Bullet b:bullets.getAll()){
			if (b instanceof EnemyBullet){
				enemyCount++;
				if (!hasBoss && b instanceof Boss){
					hasBoss = true;
				}
			}
		}
		if (enemyCount == 0){
			if (!spawning){
				parser.immediateExecution();
				spawning = true;
			}
		} else {
			spawning = false;
		}
		if (!hasBoss){
			parser.despell();
		}
	}

	private void render(GL gl){
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		renderBackground(gl);
		gl.glEnable(GL.GL_TEXTURE_2D);
		player.render(gl);
		renderBullets(gl);
		gl.glDisable(GL.GL_TEXTURE_2D);
		renderBorder(gl);
	}
	
	private void renderBorder(GL gl){
		gl.glColor4f(0.75f, 0.75f, 0.75f, 1.0f);
		gl.glRectf(0, 0, fWidth, border);
		gl.glRectf(0, fHeight - border, fWidth, fHeight);
		gl.glRectf(0, border, border, fHeight - border);
		gl.glRectf(fWidth - border, border, fWidth, fHeight - border);
	}
	
	private void renderBackground(GL gl){
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		Texture t = ImageHash.IMG.getTex("level_"+stage);
		t.enable();
		t.bind();
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(0, 0);
		gl.glVertex2f(border, border);
		gl.glTexCoord2f(0, 1);
		gl.glVertex2f(border, fWidth - border);
		gl.glTexCoord2f(1, 1);
		gl.glVertex2f(fWidth - border, fWidth - border);
		gl.glTexCoord2f(1, 0);
		gl.glVertex2f(fWidth - border, border);
		gl.glEnd();
		t.disable();
		gl.glDisable(GL.GL_TEXTURE_2D);
	}
	
	/**
	 * Draws the enemies onto the screen. Also removes any unneeded enemies for optimization
	 * @param g Graphics object of the Component that this will be drawn on
	 */
	private void renderBullets(GL gl){
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		for (Texture t:bullets.getKeys()){
			ArrayList<Bullet> bs = bullets.get(t);
			if (bs.size() <= 0){
				continue;
			}
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			boolean fastRender = true;
			for (Bullet b:bs){
				if (!b.angleLocked() || b.isSpinning() || b instanceof EnemyBullet){
					fastRender = false;
					break;
				}
			}
			ArrayList<Bullet> removeMe = new ArrayList<Bullet>();
			if (fastRender){
				t.enable();
				t.bind();
				gl.glBegin(GL.GL_QUADS);
				for (Bullet b: bs){
					if (b.getSpriteArea().intersects(new Rectangle2D.Float(0, 0, fWidth, fHeight))){
						b.drawNoBind(gl);
					} else
						if (b.checkOffscreenDelete())
							removeMe.add(b);
				}
				gl.glEnd();
				t.disable();
			} else {
				for (Bullet b: bs){
					if (b.getSpriteArea().intersects(new Rectangle2D.Float(0, 0, fWidth, fHeight))){
						b.draw(gl);
					} else
						if (b.checkOffscreenDelete())
							removeMe.add(b);
				}
			}
			for (Bullet b: removeMe)
				bs.remove(b);
		}
		for (Texture t: playerBullets.getKeys()){
			ArrayList<PlayerLaunchedBullet> plbs = playerBullets.get(t);
			if (plbs.size() <= 0){
				continue;
			}
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			boolean fastRender = true;
			for (PlayerLaunchedBullet b:plbs){
				if (!b.angleLocked() || b.isSpinning()){
					fastRender = false;
					break;
				}
			}
			ArrayList<PlayerLaunchedBullet> playerRemove = new ArrayList<PlayerLaunchedBullet>();
			if (fastRender){
				t.enable();
				t.bind();
				gl.glBegin(GL.GL_QUADS);
				for (PlayerLaunchedBullet b: plbs){
					if(b.getSpriteArea().intersects(new Rectangle2D.Float(0, 0, fWidth, fHeight))){
						b.drawNoBind(gl);
					} else
						if (b.checkOffscreenDelete())
							playerRemove.add(b);
				}
				gl.glEnd();
				t.disable();
			} else {
				for (PlayerLaunchedBullet b: plbs){
					if (b.getSpriteArea().intersects(new Rectangle2D.Float(0, 0, fWidth, fHeight))){
						b.draw(gl);
					} else
						if (b.checkOffscreenDelete())
							playerRemove.add(b);
				}
			}
			for (PlayerLaunchedBullet b: playerRemove)
				plbs.remove(b);
		}
		if (barrier[0]!= null){
			barrier[0].draw(gl);
		}
	}

	public void killThread() {
		if (bgm){
			this.mplayer.terminateThread();
		}
		this.animator.stop();
	}
	
}
