package edu.mbhs.madubozhi.touhou.game.parser;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import edu.mbhs.madubozhi.touhou.game.Player;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.AimedBulletPath;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.BezierBulletPath;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.LinearBulletPath;
import edu.mbhs.madubozhi.touhou.game.enemy.EmbeddedEnemy;
import edu.mbhs.madubozhi.touhou.game.enemy.Enemy;
import edu.mbhs.madubozhi.touhou.game.enemy.EnemyEnemy;
import edu.mbhs.madubozhi.touhou.game.enemy.pattern.EnemyMovementPattern;
import edu.mbhs.madubozhi.touhou.game.util.Mode;
import edu.mbhs.madubozhi.touhou.game.util.OscillationType;
import edu.mbhs.madubozhi.touhou.game.util.Oscillator;
import edu.mbhs.madubozhi.touhou.game.util.PathTraversal;

/**
 * Basic Level Parser. Acts as a set of preset enemies.
 * @author bowenzhi
 */
//For now, this acts as a set of presets
public class AlphaLevelParser {
	private long internaltimer;
	private int spellcard;
	private Scanner levelDataIn;
//	private Enemy enemycache;
	private int nextCueTime;
	private boolean finished;
	private Mode difficulty;
	private Player player;
//	private ArrayList<Enemy> enemiesOut;
	
	private int border, fWidth, fHeight;
	/**
	 * Constructor
	 * @param reader reader containing the level information
	 * @param border border of game frame
	 * @param fWidth game frame width
	 * @param fHeight game frame height
	 * @param difficulty difficulty level
	 * @param p player
	 */
	public AlphaLevelParser(FileReader reader, int border, int fWidth, int fHeight, Mode difficulty, Player p){
		levelDataIn = new Scanner(reader);
		internaltimer = 0;
		finished = false;
		iterateCueTiming();
		spellcard = 9001;
		this.border = border;
		this.fWidth = fWidth;
		this.fHeight = fHeight;
		this.difficulty = difficulty;
		this.player = p;
	}
	/**
	 * Alternate constructor
	 * @param input input stream containing level information
	 */
	public AlphaLevelParser(FileInputStream input){
		levelDataIn = new Scanner(input);
		internaltimer = 0;
		finished = false;
		iterateCueTiming();
		spellcard = 9001;
	}
	/**
	 * Moves to the next cue for enemies to appear
	 */
	private void iterateCueTiming(){
		finished = !levelDataIn.hasNextInt();
		if (!finished){
			try {
				nextCueTime = levelDataIn.nextInt();
			} catch (InputMismatchException e){
				System.err.println("Level parser error: Input syntax");
			}
		}
	}
	/**
	 * Called every time step, used to translate the level data into enemies
	 * @return ArrayList of enemies outputted by the level data
	 */
	public ArrayList<Enemy> step(){
		ArrayList<Enemy> toReturn = new ArrayList<Enemy>();
		while (!finished && internaltimer >= nextCueTime){
			try {
				switch (levelDataIn.nextInt()){
				case 0:
					Enemy e0 = new EmbeddedEnemy(-50, 1, 20, 20, Color.CYAN, border, fWidth, 25, true,
							new EnemyMovementPattern(new Point2D.Float(0, 0), 0, 360, 5, 0, 10, 
									new Enemy(0, 1, 2, 50, Color.BLUE, border, fWidth, true, true), 
									new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(fHeight*2, 0)), 
									true, false), difficulty.getBulletNumber(),
							new Oscillator(OscillationType.COSINE, 0, 0, 0.1f, 5, 30, 1080));
					e0.addPath(new BezierBulletPath(new Point2D.Float(fWidth, -10),
									new Point2D.Float(fWidth/2, fHeight*1.5f), 
									new Point2D.Float(0, -10), PathTraversal.LINEAR));
					toReturn.add(e0);
					break;
				case 1:
					Enemy e1 = new EmbeddedEnemy(-50, 1, 20, 20, Color.CYAN, border, fWidth, 25, true,
							new EnemyMovementPattern(new Point2D.Float(0, 0), 0, 360, 5, 0, 10, 
									new Enemy(0, 1, 2, 50, Color.RED, border, fWidth, true, true), 
									new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(fHeight*2, 0)), 
									true, false), difficulty.getBulletNumber(),
							new Oscillator(OscillationType.COSINE, 0, 0, 0.1f, 5, 30, 1080));
					e1.addPath(new BezierBulletPath(new Point2D.Float(0, -10),
									new Point2D.Float(fWidth/2, fHeight*1.5f), 
									new Point2D.Float(fWidth, -10), PathTraversal.LINEAR));
					toReturn.add(e1);
					break;
				case 2:
					Enemy e2 = new EmbeddedEnemy(-50, 1, 20, 250, Color.CYAN, border, fWidth, 500, true,
							new EnemyMovementPattern(new Point2D.Float(0, 0), 0, 180, 6, 0, 10, 
									new Enemy(0, 1, 3, (difficulty.getIndex()+1)*5, Color.WHITE, border, fWidth, true, true), 
									new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(player.getX()+fWidth, player.getY()+fHeight),
											 PathTraversal.ROOT), 
									true, false), difficulty.getBulletNumber(),
							null);
					e2.addPath(new LinearBulletPath(new Point2D.Float(-10, fHeight/10), new Point2D.Float(fWidth + 10, fHeight/2)));
					toReturn.add(e2);
					break;
				case 3:
					Enemy e3 = new EmbeddedEnemy(-50, 1, 20, 250, Color.CYAN, border, fWidth, 500, true,
							new EnemyMovementPattern(new Point2D.Float(0, 0), -45, 135, 6, 0, 10, 
									new Enemy(0, 1, 3, (difficulty.getIndex()+1)*5, Color.WHITE, border, fWidth, true, true), 
									new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(player.getX()+fWidth, player.getY()+fHeight),
											 PathTraversal.ROOT), 
									true, false), difficulty.getBulletNumber(),
							null);
					e3.addPath(new LinearBulletPath(new Point2D.Float(fWidth + 10, fHeight/10), new Point2D.Float(-10, fHeight/2)));
					toReturn.add(e3);
					break;
				case 4:
					Enemy e4 = new EmbeddedEnemy(-50, 1, 20, 12, Color.CYAN, border, fWidth, 1500, true,
							new EnemyMovementPattern(new Point2D.Float(0, 0), 0, 360, 5, 0, 10, 
									new Enemy(0, 1, 3, 25, Color.ORANGE, border, fWidth, true, true), 
									new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(fHeight*2, 0)), 
									true, false), difficulty.getBulletNumber()/2,
							new Oscillator(OscillationType.COSINE, 0, 0, 0.1f, 5, 50, 480));
					e4.addPath(new LinearBulletPath(new Point2D.Float(fWidth/2, -10), new Point2D.Float(fWidth/2, fHeight + 50)));
					toReturn.add(e4);
					break;
				case 5:
					EnemyEnemy e5 = new EnemyEnemy(-50, 1, 30, 10, Color.ORANGE, border, fWidth, 2000, true, true);
					e5.addPath(new LinearBulletPath(new Point2D.Float(fWidth/2, fHeight/3), new Point2D.Float(fWidth/2, fHeight/3)));
					e5.setSpellcardStatus(true);
					toReturn.add(e5);
					spellcard = 11;
					break;
				case 6:
					EnemyEnemy e6 = new EnemyEnemy(-50, 1, 30, 10, Color.RED, border, fWidth, 2000, true, true);
					e6.addPath(new LinearBulletPath(new Point2D.Float(fWidth/2, fHeight/2), new Point2D.Float(fWidth/2, fHeight/2)));
					e6.setSpellcardStatus(true);
					toReturn.add(e6);
					spellcard = 10;
					break;
				case 7:
					EnemyEnemy e7a = new EnemyEnemy(-50, 1, 30, 10, Color.RED, border, fWidth, 2000, true, true);
					e7a.addPath(new LinearBulletPath(new Point2D.Float(fWidth/2, fHeight/3), new Point2D.Float(fWidth/2, fHeight/3)));
					e7a.setSpellcardStatus(true);
					EnemyEnemy e7b = new EnemyEnemy(-50, 1, 30, 10, Color.RED, border, fWidth, 2000, true, true);
					e7b.addPath(new LinearBulletPath(new Point2D.Float(fWidth/2, fHeight/2), new Point2D.Float(fWidth/2, fHeight/2)));
					e7b.setSpellcardStatus(true);
					toReturn.add(e7a);
					toReturn.add(e7b);
					spellcard = 5;
					break;
				case 8:
					EnemyEnemy e8 = new EnemyEnemy(-50, 1, 30, 10, Color.RED, border, fWidth, 2000, true, true);
					e8.addPath(new LinearBulletPath(new Point2D.Float(fWidth/2, fHeight/3), new Point2D.Float(fWidth/2, fHeight/3)));
					e8.setSpellcardStatus(true);
					toReturn.add(e8);
					spellcard = 3;
					break;
				case 9:
					EnemyEnemy e9 = new EnemyEnemy(-50, 1, 30, 10, Color.RED, border, fWidth, 2000, true, true);
					e9.addPath(new LinearBulletPath(new Point2D.Float(fWidth/2, fHeight/2), new Point2D.Float(fWidth/2, fHeight/2)));
					e9.setSpellcardStatus(true);
					toReturn.add(e9);
					spellcard = 1;
					break;
				case 10:
					EnemyEnemy e10a = new EnemyEnemy(-50, 1, 30, 10, Color.RED, border, fWidth, 2000, true, true);
					e10a.addPath(new LinearBulletPath(new Point2D.Float(fWidth/2, fHeight/3), new Point2D.Float(fWidth/2, fHeight/3)));
					e10a.setSpellcardStatus(true);
					EnemyEnemy e10b = new EnemyEnemy(-50, 1, 30, 10, Color.RED, border, fWidth, 2000, true, true);
					e10b.addPath(new LinearBulletPath(new Point2D.Float(fWidth/4, fHeight/2), new Point2D.Float(fWidth/4, fHeight/2)));
					e10b.setSpellcardStatus(true);
					EnemyEnemy e10c = new EnemyEnemy(-50, 1, 30, 10, Color.RED, border, fWidth, 2000, true, true);
					e10c.addPath(new LinearBulletPath(new Point2D.Float(3*fWidth/4, fHeight/2), new Point2D.Float(3*fWidth/4, fHeight/2)));
					e10c.setSpellcardStatus(true);
					toReturn.add(e10a);
					toReturn.add(e10b);
					toReturn.add(e10c);
					spellcard = 0;
					break;
				case 11:
					Enemy e11 = new EmbeddedEnemy(-500, 1, 20, 35, Color.CYAN, border, fWidth, 25, true,
							new EnemyMovementPattern(new Point2D.Float(0, 0), 0, 1, 1, 0, 0, 
									new Enemy(0, 1, 2, 50, Color.BLUE, border, fWidth, true, true), 
									new AimedBulletPath(new Point2D.Float(0, 0), fHeight * 2, player, PathTraversal.ROOT), 
									true, false), difficulty.getBulletNumber(), null);
					e11.addPath(new BezierBulletPath(new Point2D.Float(10, -10),
									new Point2D.Float(10, fHeight * 3 /4 - 10),
									new Point2D.Float(-50, fHeight * 3 / 4), PathTraversal.LINEAR));
					toReturn.add(e11);
					break;
				case 12:
					Enemy e12 = new EmbeddedEnemy(-500, 1, 20, 35, Color.CYAN, border, fWidth, 25, true,
							new EnemyMovementPattern(new Point2D.Float(0, 0), 0, 1, 1, 0, 0, 
									new Enemy(0, 1, 2, 50, Color.RED, border, fWidth, true, true), 
									new AimedBulletPath(new Point2D.Float(0, 0), fHeight * 2, player, PathTraversal.ROOT), 
									true, false), difficulty.getBulletNumber(), null);
					e12.addPath(new BezierBulletPath(new Point2D.Float(fWidth - 10, -10),
									new Point2D.Float(fWidth - 10, fHeight * 3 /4 - 10),
									new Point2D.Float(fWidth + 50, fHeight * 3 / 4), PathTraversal.LINEAR));
					toReturn.add(e12);
					break;
				}
			} catch (InputMismatchException e){
				System.err.println("Level parser error: Input syntax");
			}
			iterateCueTiming();
			internaltimer = 0;
		}
		internaltimer++;
		return toReturn;
	}
	public int getSpellcard(){
		return spellcard;
	}
	public void setSpellcard(int spellcard){
		this.spellcard = spellcard;
	}
	public void immediateExecution(){
		internaltimer = 0;
		nextCueTime = nextCueTime > 150?150:nextCueTime;
	}
}
