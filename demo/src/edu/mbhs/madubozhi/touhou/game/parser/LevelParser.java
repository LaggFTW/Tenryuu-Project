package edu.mbhs.madubozhi.touhou.game.parser;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.sun.opengl.util.texture.Texture;

import edu.mbhs.madubozhi.touhou.game.Player;
import edu.mbhs.madubozhi.touhou.game.bullet.Boss;
import edu.mbhs.madubozhi.touhou.game.bullet.Bullet;
import edu.mbhs.madubozhi.touhou.game.bullet.BulletShape;
import edu.mbhs.madubozhi.touhou.game.bullet.DarknessBullet;
import edu.mbhs.madubozhi.touhou.game.bullet.EmbeddedBullet;
import edu.mbhs.madubozhi.touhou.game.bullet.EnemyBullet;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.AimedBulletPath;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.ArcBulletPattern;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.BezierBulletPath;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.CompoundBulletPath;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.LinearBulletPath;
import edu.mbhs.madubozhi.touhou.game.bullet.pattern.SpiralBulletPath;
import edu.mbhs.madubozhi.touhou.game.util.HashArray;
import edu.mbhs.madubozhi.touhou.game.util.ImageHash;
import edu.mbhs.madubozhi.touhou.game.util.Mode;
import edu.mbhs.madubozhi.touhou.game.util.OscillationType;
import edu.mbhs.madubozhi.touhou.game.util.Oscillator;
import edu.mbhs.madubozhi.touhou.game.util.PathTraversal;
import edu.mbhs.madubozhi.touhou.game.util.RotationalDirection;
import edu.mbhs.madubozhi.touhou.ui.FullGame;

/**
 * Final Level Parser, acts as a set of preset patterns and bullets for use in level design
 * @author bowenzhi
 *
 */
public class LevelParser {
	private Scanner levelDataIn;
	private long internaltimer;
	private int nextCueTime;
	private int spellcard = 12;
	private boolean finished;
	private boolean bossSignal;
	private Player player;
	private Mode difficulty;
	private Oscillator oscillator = new Oscillator(OscillationType.COSINE, 0, 0, 0, 0, 30, 1080);
	
	private int fWidth = (int) (FullGame.WIDTH*0.7);
	private int fHeight = FullGame.HEIGHT;
	
	/**
	 * Instantiates this parser with the following properties
	 * @param reader FileReader buffered with the file to parse
	 * @param m Difficulty level
	 * @param p Player
	 */
	public LevelParser(FileReader reader, Mode m, Player p){
		levelDataIn = new Scanner(reader);
		this.player = p;
		this.difficulty = m;
		iterateCueTiming();
	}
	/**
	 * Instantiates this parser with the following properties
	 * @param input InputStream buffered with the file to parse
	 * @param m Difficulty level
	 * @param p Player
	 */
	public LevelParser(FileInputStream input, Mode m, Player p){
		levelDataIn = new Scanner(input);
		this.player = p;
		this.difficulty = m;
		iterateCueTiming();
	}
	/**
	 * Advances to the next cued timing of a pattern
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
	 * Auto-iterates the cue timing when called, overriding the original timing
	 */
	public void immediateExecution(){
		internaltimer = 0;
		nextCueTime = nextCueTime > 150?150:nextCueTime;
	}
	/**
	 * Sets the current Spellcard to null
	 */
	public void despell(){
		spellcard = Integer.MAX_VALUE;
	}
	/**
	 * Called every timestep, updates the HashArray inputted with any enemies specified by the parsed data
	 * @param bullets Reference to the master HashArray of bullets used in the engine
	 */
	public void step(HashArray<Texture, Bullet> bullets){
		if (!finished){
			stepSpellCards(bullets);
		}
		bossSignal = false;
		while (!finished && internaltimer >= nextCueTime){
			switch(levelDataIn.nextInt()){
			case 9001:
				bossSignal = true;
				break;
			case 0:
				Bullet e0 = new EmbeddedBullet(20, BulletShape.FAMILIAR, "", 50, 500, true, true, 
						new ArcBulletPattern(new Point2D.Float(0, 0), 0, 360, 5, 0, 10, 
								new Bullet(50, BulletShape.DIAMOND, "blue", false, true), 
								new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(fHeight*2, 0)), 
								true, false), difficulty.getBulletNumber(),
						new Oscillator(OscillationType.COSINE, 0, 0, 0.1f, 5, 30, 1080), true);
				e0.addPath(new BezierBulletPath(new Point2D.Float(fWidth, -10),
								new Point2D.Float(fWidth/2, fHeight*1.5f), 
								new Point2D.Float(0, -10), PathTraversal.LINEAR));
				bullets.put(e0.getSprite(), e0);
				break;
			case 1:
				Bullet e1 = new EmbeddedBullet(20, BulletShape.FAMILIAR, "", 50, 500, true, true, 
						new ArcBulletPattern(new Point2D.Float(0, 0), 0, 360, 5, 0, 10, 
								new Bullet(50, BulletShape.DIAMOND, "red", false, true), 
								new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(fHeight*2, 0)), 
								true, false), difficulty.getBulletNumber(),
						new Oscillator(OscillationType.COSINE, 0, 0, 0.1f, 5, 30, 1080), true);
				e1.addPath(new BezierBulletPath(new Point2D.Float(0, -10),
								new Point2D.Float(fWidth/2, fHeight*1.5f), 
								new Point2D.Float(fWidth, -10), PathTraversal.LINEAR));
				bullets.put(e1.getSprite(), e1);
				break;
			case 2:
				Bullet e2 = new EmbeddedBullet(250, BulletShape.WAVE_LARGE, "blue", 500, 1000, false, true,
						new ArcBulletPattern(new Point2D.Float(0, 0), 0, 180, 6, 0, 10, 
								new Bullet(3, BulletShape.ORB_MED_SMALL, "cyan", true, true), 
								new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(player.getX()+fWidth, player.getY()+fHeight),
										 PathTraversal.ROOT), 
								true, false), difficulty.getBulletNumber(),
						null);
				e2.addPath(new LinearBulletPath(new Point2D.Float(-10, fHeight/10), new Point2D.Float(fWidth + 10, fHeight/2)));
				bullets.put(e2.getSprite(), e2);
				break;
			case 3:
				Bullet e3 = new EmbeddedBullet(250, BulletShape.WAVE_LARGE, "blue", 500, 1000, false, true,
						new ArcBulletPattern(new Point2D.Float(0, 0), -45, 135, 6, 0, 10, 
								new Bullet(3, BulletShape.ORB_MED_SMALL, "cyan", true, true), 
								new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(player.getX()+fWidth, player.getY()+fHeight),
										 PathTraversal.ROOT), 
								true, false), difficulty.getBulletNumber(),
						null);
				e3.addPath(new LinearBulletPath(new Point2D.Float(fWidth + 10, fHeight/10), new Point2D.Float(-10, fHeight/2)));
				bullets.put(e3.getSprite(), e3);
				break;
			case 4:
				Bullet e4 = new EmbeddedBullet(12, BulletShape.BUBBLE_HOLLOW, "cyan", 1500, 3000, true, true,
						new ArcBulletPattern(new Point2D.Float(0, 0), 0, 360, 5, 0, 10, 
								new Bullet(25, BulletShape.ORB, "orange", true, true), 
								new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(fHeight*2, 0)), 
								true, false), difficulty.getBulletNumber()/2,
						new Oscillator(OscillationType.COSINE, 0, 0, 0.1f, 5, 50, 480));
				e4.addPath(new LinearBulletPath(new Point2D.Float(fWidth/2, -10), new Point2D.Float(fWidth/2, fHeight + 50)));
				bullets.put(e4.getSprite(), e4);
				break;
			case 5:
				Boss e5 = new Boss(5, BulletShape.BUBBLE_SQUARE, "green", 4000, 6000, null, true, false, true);
				e5.addPath(new LinearBulletPath(new Point2D.Float(fWidth/2, fHeight/3), new Point2D.Float(fWidth/2, fHeight/3)));
				spellcard = 9;
				bullets.put(e5.getSprite(), e5);
				break;
			case 6:
				Boss e7 = new Boss(5, BulletShape.BUBBLE_SQUARE, "green", 2000, 4000, null, true, false ,true);
				e7.addPath(new LinearBulletPath(new Point2D.Float(fWidth/2, fHeight/2), new Point2D.Float(fWidth/2, fHeight/2)));
				spellcard = 7;
				bullets.put(e7.getSprite(), e7);
				break;
			case 7:
				Boss e6 = new Boss(1, BulletShape.ORB_LARGE, "red", 6000, 5000, null, true, false, false);
				e6.addPath(new LinearBulletPath(new Point2D.Float(fWidth/2, fHeight/5), new Point2D.Float(fWidth/2, fHeight/5)));
				spellcard = 8;
				bullets.put(e6.getSprite(), e6);
				break;
			case 8:
				Boss e8 = new Boss(1, BulletShape.BUBBLE_SQUARE, "red", 8000, 5000, null, true, false, true);
				e8.addPath(new LinearBulletPath(new Point2D.Float(fWidth/2, fHeight/2), new Point2D.Float(fWidth/2, fHeight/2)));
				spellcard = 2;
				bullets.put(e8.getSprite(), e8);
				break;
			case 9:
				Boss e9 = new Boss(1, BulletShape.BUBBLE, "red", 8000, 5000, null, true, false, false);
				e9.addPath(new LinearBulletPath(new Point2D.Float(fWidth/2, fHeight/2), new Point2D.Float(fWidth/2, fHeight/2)));
				spellcard = 1;
				bullets.put(e9.getSprite(), e9);
				break;
			case 10:
				Boss e10a = new Boss(100, BulletShape.BUBBLE_SQUARE, "red", 16000, 15000, null, true, false, true);
				e10a.addPath(new CompoundBulletPath(true, new LinearBulletPath(new Point2D.Float(fWidth/3, fHeight/3), new Point2D.Float(2*fWidth/3, fHeight/3)), new LinearBulletPath(new Point2D.Float(2*fWidth/3, fHeight/3), new Point2D.Float(fWidth/3, fHeight/3))));
				EnemyBullet e10b = new EnemyBullet(0, BulletShape.FAMILIAR, "", 2000, 3000, true, true, true);
				e10b.addPath(new LinearBulletPath(new Point2D.Float(fWidth/4, fHeight/2), new Point2D.Float(fWidth/4, fHeight/2)));
				EnemyBullet e10c = new EnemyBullet(0, BulletShape.FAMILIAR, "", 2000, 3000, true, true, true);
				e10c.addPath(new LinearBulletPath(new Point2D.Float(3*fWidth/4, fHeight/2), new Point2D.Float(3*fWidth/4, fHeight/2)));
				spellcard = 0;
				bullets.put(e10a.getSprite(), e10a);
				bullets.put(e10b.getSprite(), e10b);
				bullets.put(e10c.getSprite(), e10c);
				break;
			case 11:
				Bullet e11 = new EmbeddedBullet(35, BulletShape.FAMILIAR, "", 25, 500, true, true,
						new ArcBulletPattern(new Point2D.Float(0, 0), 0, 1, 1, 0, 0, 
								new Bullet(50, BulletShape.BLADE_SMALL, "indigo", false, true), 
								new AimedBulletPath(new Point2D.Float(0, 0), fHeight * 2, player, PathTraversal.ROOT), 
								true, false), difficulty.getBulletNumber(), null);
				e11.addPath(new BezierBulletPath(new Point2D.Float(10, -10),
								new Point2D.Float(10, fHeight * 3 /4 - 10),
								new Point2D.Float(-50, fHeight * 3 / 4), PathTraversal.LINEAR));
				bullets.put(e11.getSprite(), e11);
				break;
			case 12:
				Bullet e12 = new EmbeddedBullet(35, BulletShape.FAMILIAR, "", 25, 500, true, true,
						new ArcBulletPattern(new Point2D.Float(0, 0), 0, 1, 1, 0, 0, 
								new Bullet(50, BulletShape.BLADE_SMALL, "red", false, true), 
								new AimedBulletPath(new Point2D.Float(0, 0), fHeight * 2, player, PathTraversal.ROOT), 
								true, false), difficulty.getBulletNumber(), null);
				e12.addPath(new BezierBulletPath(new Point2D.Float(fWidth - 10, -10),
								new Point2D.Float(fWidth - 10, fHeight * 3 /4 - 10),
								new Point2D.Float(fWidth + 50, fHeight * 3 / 4), PathTraversal.LINEAR));
				bullets.put(e12.getSprite(), e12);
				break;
			case 13:
				Boss e13 = new Boss(2, BulletShape.BUBBLE_SQUARE, "orange", 3000, 5000, null, true, false, true);
				e13.addPath(new SpiralBulletPath(fWidth/2, fHeight/4, 0, 0, 0, 0, true));
				spellcard = 10;
				bullets.put(e13.getSprite(), e13);
				break;
			case 14:
				Bullet e14 = new EmbeddedBullet(35, BulletShape.FAMILIAR, "", 25, 500, true, true,
						new ArcBulletPattern(new Point2D.Float(0, 0), 0, 1, 1, 0, 0, 
								new Bullet(50, BulletShape.BLADE_SMALL, "green", false, true), 
								new CompoundBulletPath(new AimedBulletPath(new Point2D.Float(0, 0), fHeight/2, player, PathTraversal.ROOT), 
										new AimedBulletPath(new Point2D.Float(0, 0), fHeight/2, player, PathTraversal.ROOT), 
										new AimedBulletPath(new Point2D.Float(0, 0), fHeight/2, player, PathTraversal.ROOT)), 
								true, false), difficulty.getBulletNumber(), null);
				e14.addPath(new BezierBulletPath(new Point2D.Float(-50, -10),
								new Point2D.Float(fWidth/2, fHeight/2),
								new Point2D.Float(fWidth + 50, -10), PathTraversal.LINEAR));
				bullets.put(e14.getSprite(), e14);
				break;
			case 15:
				Bullet e15 = new EmbeddedBullet(35, BulletShape.FAMILIAR, "", 50, 500, true, true, 
						new ArcBulletPattern(new Point2D.Float(0, 0), 120, 240, 4, 0, 0, 
								new Bullet(10, BulletShape.ORB_MED_SMALL, "green", true, true), 
								new SpiralBulletPath(0, 0, 0, fHeight*1.2f, 0, 720, true, PathTraversal.ROOT), 
								true, false), difficulty.getBulletNumber(), null);
				e15.addPath(new CompoundBulletPath(new AimedBulletPath(new Point2D.Float(-10, fHeight/4), fWidth/3, player),
						new AimedBulletPath(new Point2D.Float(-10, fHeight/4), -fWidth, player)));
				bullets.put(e15.getSprite(), e15);
				break;
			case 16:
				Bullet e16 = new EmbeddedBullet(35, BulletShape.FAMILIAR, "", 50, 500, true, true, 
						new ArcBulletPattern(new Point2D.Float(0, 0), 0, 120, 4, 0, 0, 
								new Bullet(10, BulletShape.ORB_MED_SMALL, "green", true, true), 
								new SpiralBulletPath(0, 0, 0, fHeight*1.2f, 0, -720, true, PathTraversal.ROOT), 
								true, false), difficulty.getBulletNumber(), null);
				e16.addPath(new CompoundBulletPath(new AimedBulletPath(new Point2D.Float(fWidth+10, fHeight/4), fWidth/3, player),
						new AimedBulletPath(new Point2D.Float(fWidth+10, fHeight/4), -fWidth, player)));
				bullets.put(e16.getSprite(), e16);
				break;
			}
			iterateCueTiming();
			internaltimer = 0;
		}
		internaltimer++;
	}
	/**
	 * Called every timestep, if a spellcard is running, manages the patterns of the current spellcard
	 * @param bullets Reference to the master HashArray of bullets used in the engine
	 */
	private void stepSpellCards(HashArray<Texture, Bullet> bullets){
		switch(spellcard){
		case 0:
			/**
			 * Phoenix Sign: "Phoenix Wings"
			 */
			if (internaltimer < 100){
				return;
			}
			if (internaltimer%(difficulty.getBulletNumber()/2)==0){
				ArcBulletPattern arc1 = new ArcBulletPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/3), oscillator.getAngle(), oscillator.getAngle() + 360, 16, 0, 0,
						new Bullet(60 - difficulty.getBulletNumber(), BulletShape.ORB_MED_SMALL, "random", true, true), 
						new LinearBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(fHeight, 0)), 
								false, false);
				for (Bullet b: arc1.step()){
					bullets.put(b.getSprite(), b);
				}
				oscillator.oscillate();
			}
			if (internaltimer%(difficulty.getBulletNumber()*.75)==0){
				Point2D.Float tempP = new Point2D.Float(fWidth/4, FullGame.HEIGHT/2);
				float theta = RotationalDirection.getAngleBetween(tempP, new Point2D.Float(player.getX(), player.getY()));
				ArcBulletPattern arc1 = new ArcBulletPattern(
						tempP, (float)(theta + Math.PI/4), (float)(theta + Math.PI), 3, 0, 0,
						new Bullet(100 - difficulty.getBulletNumber(), BulletShape.ORB_MED_SMALL, "orange", true, true), 
						new LinearBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(fHeight, 0)), 
								true, true);
				tempP = new Point2D.Float(3*fWidth/4, FullGame.HEIGHT/2);
				theta = RotationalDirection.getAngleBetween(tempP, new Point2D.Float(player.getX(), player.getY()));
				ArcBulletPattern arc2 = new ArcBulletPattern(
						tempP, (float)(theta + Math.PI/4), (float)(theta + Math.PI), 3, 0, 0,
						new Bullet(100 - difficulty.getBulletNumber(), BulletShape.ORB_MED_SMALL, "orange", true, true), 
						new LinearBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(fHeight, 0)), 
								true, true);
				for (Bullet b: arc1.step()){
					bullets.put(b.getSprite(), b);
				}
				for (Bullet b: arc2.step()){
					bullets.put(b.getSprite(), b);
				}
			}
			break;

		case 1:
			/**
			 * Stargazer Sign: "Evening Primrose"
			 */
			if (internaltimer < 100){
				return;
			}
			if (Math.random() * difficulty.getBulletNumber() < 2){
				int size = (int)(6*Math.random()+2);
				ArcBulletPattern arc = new ArcBulletPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), oscillator.getAngle(), oscillator.getAngle() + 360, 16, 0, 30,
						new Bullet((int)(6*Math.random()+2), new Dimension(size, size), BulletShape.ORB.getSpriteTex("random"), new Dimension(size + 4, size + 4), true, true), 
								new BezierBulletPath(
										new Point2D.Float(0, 0),
										new Point2D.Float(750, oscillator.omegaIncreasing()?-1250:1250), 
										new Point2D.Float(1250, oscillator.omegaIncreasing()?750:-750),
										PathTraversal.ROOT), 
										false, false);
				for (Bullet b: arc.step()){
					bullets.put(b.getSprite(), b);
				}
				oscillator.oscillate();
			}
			break;

		case 2:
			/**
			 * Kerr Sign: "Fission Whirlpool"
			 */
			if (internaltimer%(difficulty.getBulletNumber()*1.5)==3*difficulty.getBulletNumber()/4){
				ArcBulletPattern arc = new ArcBulletPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), oscillator.getAngle(), oscillator.getAngle() + 360, 16, 0, 30,
						new Bullet(16, BulletShape.ORB_MED_SMALL, "yellow", true, false), 
						new BezierBulletPath( 
								new Point2D.Float(625, oscillator.omegaIncreasing()?-75:75),
								new Point2D.Float(150, oscillator.omegaIncreasing()?125:-125),
								new Point2D.Float(0, 0)),
								false, false);
				for (Bullet b: arc.step()){
					bullets.put(b.getSprite(), b);
				}
			}
			if (internaltimer < 100){
				return;
			}
			if (internaltimer%(difficulty.getBulletNumber()*1.5)==0){
				ArcBulletPattern arc1 = new ArcBulletPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), oscillator.getAngle(), oscillator.getAngle() + 360, 16, 0, 30,
						new Bullet(12, BulletShape.ORB_MED_SMALL, "blue", true, true), 
						new BezierBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(750, oscillator.omegaIncreasing()?-125:125), 
								new Point2D.Float(1250, oscillator.omegaIncreasing()?75:-75)), 
								false, false);
				ArcBulletPattern arc2 = new ArcBulletPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), oscillator.getAngle(), oscillator.getAngle() + 360, 16, 0, 30,
						new Bullet(12
								, BulletShape.ORB_MED_SMALL, "red", true, true), 
						new BezierBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(750, oscillator.omegaIncreasing()?125:-125), 
								new Point2D.Float(1250, oscillator.omegaIncreasing()?-75:75)),
								false, false);
				for (Bullet b: arc1.step()){
					bullets.put(b.getSprite(), b);
				}
				for (Bullet b: arc2.step()){
					bullets.put(b.getSprite(), b);
				}
			}
			if (internaltimer%(difficulty.getBulletNumber()*12)==0){
				ArcBulletPattern arc = new ArcBulletPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/2), oscillator.getAngle(), oscillator.getAngle() + 360, 6, 0, 30,
						new Bullet(8, BulletShape.BUBBLE, "random", true, true), 
						new BezierBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(750, oscillator.omegaIncreasing()?125:-125), 
								new Point2D.Float(1250, oscillator.omegaIncreasing()?-75:75)),
								false, false);
				for (Bullet b: arc.step()){
					bullets.put(b.getSprite(), b);
				}
			}
			oscillator.oscillate();
			break;
		
		case 3:
			/**
			 * Darkness Sign: "Labyrinth"
			 */
			if (internaltimer%((int)((difficulty.getBulletNumber()/20 + 1) * 3))==0){
				ArcBulletPattern arc = new ArcBulletPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/3), oscillator.getAngle(), oscillator.getAngle() + 0.65f, 16, 0, 0,
						new DarknessBullet((80 - (difficulty.getBulletNumber()<15?difficulty.getBulletNumber():15 * 2))/2.5f, BulletShape.ORB, "blue", true, true, player), 
								new LinearBulletPath(
										new Point2D.Float(0, 0),
										new Point2D.Float(fHeight, 0),
										PathTraversal.ROOT), 
										false, true);
				for (Bullet b: arc.step()){
					bullets.put(b.getSprite(), b);
				}
				oscillator.oscillate();
			}
			break;

		case 4:
			/**
			 * Orbit Sign: "Blue Flare"
			 */
			if (internaltimer <= nextCueTime * 3 / 5 && (internaltimer%difficulty.getBulletNumber()*2)==0){
				ArcBulletPattern arc1 = new ArcBulletPattern(
						new Point2D.Float(player.getX(), player.getY()), oscillator.getAngle(), oscillator.getAngle() + 360, 16, 0, 0,
						new Bullet(64, BulletShape.ORB_MED_SMALL, "blue", true, false), 
						new SpiralBulletPath(0, 0, 500, 0, oscillator.getAngle(), oscillator.getAngle() + 60, true), false, false);
				ArcBulletPattern arc2 = new ArcBulletPattern(
						new Point2D.Float(player.getX(), player.getY()), oscillator.getAngle(), oscillator.getAngle() + 360, 16, 0, 15,
						new Bullet((float)(Math.random() * 32 + 4), BulletShape.ORB_MED_SMALL, "blue", true, false), 
						new SpiralBulletPath(0, 0, 800, 50, oscillator.getAngle(), oscillator.getAngle() - 360, true), false, false);
				for (Bullet b: arc1.step()){
					bullets.put(b.getSprite(), b);
				}
				for (Bullet b: arc2.step()){
					bullets.put(b.getSprite(), b);
				}
			}
			oscillator.oscillate();
			break;

		case 5:
			/**
			 * Orbit Sign: "Lunar Satellite"
			 */
			if (internaltimer <= 3*nextCueTime/4 && internaltimer%((difficulty.getBulletNumber()/10) + 50)==0){
				Point2D.Float tempP = new Point2D.Float(fWidth/2, FullGame.HEIGHT/2);
				float theta = RotationalDirection.getAngleBetween(tempP, new Point2D.Float(player.getX(), player.getY()));
				LinearBulletPath path1 = new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(1000, 0), PathTraversal.QUADRATIC);
				LinearBulletPath path2 = new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(0, 100), PathTraversal.ROOT);
				ArcBulletPattern arc = new ArcBulletPattern(
						tempP, (float) (theta+Math.PI/2), (float) (theta + Math.PI/2 + 0.001), 1, 0, 0,
						new EmbeddedBullet((float)(35+(Math.random()*35)), BulletShape.STAR_LARGE, "random", Integer.MAX_VALUE, 0, true, true, 
								new ArcBulletPattern(null, 0, 360, 8, 0, 35, 
										new Bullet(20, BulletShape.STAR_SMALL, "random", true, true, true), path2, false, false),
										difficulty.getBulletNumber()*5, null, true), path1, true, true);
				for (Bullet b: arc.step()){
					bullets.put(b.getSprite(), b);
				}
			}
			if (internaltimer%difficulty.getBulletNumber()==0){
				ArcBulletPattern arc1 = new ArcBulletPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/3), -oscillator.getAngle(), 120 - oscillator.getAngle(), 8, 0, 0,
						new Bullet(70 - difficulty.getBulletNumber(), BulletShape.ORB_SMALL, "", true, true), 
						new LinearBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(fHeight, 0),
								PathTraversal.LINEAR), 
								false, false);
				ArcBulletPattern arc2 = new ArcBulletPattern(
						new Point2D.Float(fWidth/2, FullGame.HEIGHT/3), 180 - oscillator.getAngle(), 300 - oscillator.getAngle(), 8, 0, 0,
						new Bullet(70 - difficulty.getBulletNumber(), BulletShape.ORB_SMALL, "", true, true), 
						new LinearBulletPath(
								new Point2D.Float(0, 0),
								new Point2D.Float(fHeight, 0),
								PathTraversal.LINEAR), 
								false, false);
				for (Bullet b: arc1.step()){
					bullets.put(b.getSprite(), b);
				}
				for (Bullet b: arc2.step()){
					bullets.put(b.getSprite(), b);
				}
			} else
				if (internaltimer%difficulty.getBulletNumber()==difficulty.getBulletNumber()/2){
					ArcBulletPattern arc1 = new ArcBulletPattern(
							new Point2D.Float(fWidth/2, FullGame.HEIGHT/3), -oscillator.getAngle(), 120 - oscillator.getAngle(), 8, 0, 0,
							new Bullet(70 - difficulty.getBulletNumber(), BulletShape.ORB_SMALL, "", true, true), 
							new LinearBulletPath(
									new Point2D.Float(0, 0),
									new Point2D.Float(fHeight, 0),
									PathTraversal.LINEAR), 
									false, false);
					ArcBulletPattern arc2 = new ArcBulletPattern(
							new Point2D.Float(fWidth/2, FullGame.HEIGHT/3), 180 - oscillator.getAngle(), 300 - oscillator.getAngle(), 8, 0, 0,
							new Bullet(70 - difficulty.getBulletNumber(), BulletShape.ORB_SMALL, "", true, true), 
							new LinearBulletPath(
									new Point2D.Float(0, 0),
									new Point2D.Float(fHeight, 0),
									PathTraversal.LINEAR), 
									false, false);
					for (Bullet b: arc1.step()){
						bullets.put(b.getSprite(), b);
					}
					for (Bullet b: arc2.step()){
						bullets.put(b.getSprite(), b);
					}
				}
			oscillator.oscillate();
			break;

		case 6:
			/**
			 * Orbit Sign: "Meteor Shower"
			 */
			if (internaltimer <= nextCueTime * 3 / 5 && internaltimer%(difficulty.getBulletNumber()*2)==0){
				ArcBulletPattern arc1 = new ArcBulletPattern(
						new Point2D.Float(player.getX(), player.getY()), oscillator.getAngle(), oscillator.getAngle() + 360, 16, 0, 0,
						new Bullet(64, BulletShape.ORB_MED_SMALL, "blue", true, false), 
						new SpiralBulletPath(0, 0, 500, 0, oscillator.getAngle(), oscillator.getAngle() + 60, true), false, false);
				ArcBulletPattern arc2 = new ArcBulletPattern(
						new Point2D.Float(player.getX(), player.getY()), oscillator.getAngle(), oscillator.getAngle() + 360, 16, 0, 15,
						new Bullet((float)(Math.random() * 8 + 8), BulletShape.WAVE_SMALL, "red", false, false), 
						new SpiralBulletPath(0, 0, 1000, 50, oscillator.getAngle(), oscillator.getAngle() + 120, true), false, false);
				for (Bullet b: arc1.step()){
					bullets.put(b.getSprite(), b);
				}
				for (Bullet b: arc2.step()){
					bullets.put(b.getSprite(), b);
				}
			}
			oscillator.oscillate();
			break;

		case 7:
			/**
			 * Stargazer Sign: "Stardust Trail"
			 */
			if (internaltimer%150 == 0){
				Point2D.Float tempP = new Point2D.Float(fWidth/2, FullGame.HEIGHT/2);
				float theta = oscillator.getAngle();
				SpiralBulletPath path1 = new SpiralBulletPath(0, 0, 0, fHeight, 0, 120, true, PathTraversal.QUADRATIC);
				LinearBulletPath path2 = new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(fHeight, 0), PathTraversal.LINEAR);
				ArcBulletPattern arc = new ArcBulletPattern(
						tempP, theta, (float) (theta + 2 * Math.PI), 4, 0, 0,
						new EmbeddedBullet(64, BulletShape.ORB_LARGE, "cyan", 100, 100, true, true, 
								new ArcBulletPattern(null, 0, 360, 8, 0, 0, 
										new Bullet(16, BulletShape.STAR_SMALL, "random", true, true, true), path2, false, false),
										difficulty.getBulletNumber()*3, oscillator), path1, true, true);
				for (Bullet b: arc.step()){
					bullets.put(b.getSprite(), b);
				}
			}
			break;
		case 8:
			/**
			 * Fire Sign: "Lava Plume"
			 */
			if (internaltimer%(difficulty.getBulletNumber()/3)==0){
				int size = (int)(Math.random()*15) + 10;
				ArcBulletPattern arc = new ArcBulletPattern(
						new Point2D.Float(0, 0), 0, 1, 1, 0, 0, 
						new Bullet(30, new Dimension(size, size), ImageHash.IMG.getTex("wave_red"), new Dimension((int)(size*3), (int)(size*3)), false, true, false),
						new LinearBulletPath(new Point2D.Float((float)(Math.random()*FullGame.WIDTH), -size), new Point2D.Float((float)(Math.random()*FullGame.WIDTH), FullGame.HEIGHT+size), PathTraversal.QUADRATIC),
						false, false);
				for (Bullet b:arc.step()){
					bullets.put(b.getSprite(), b);
				}
			}
			break;
		case 9:
			/**
			 * Nature Sign: "Glyph of Force"
			 */
			if (internaltimer%(difficulty.getBulletNumber()/3)==0){
				ArcBulletPattern arc = new ArcBulletPattern(
						new Point2D.Float(fWidth/2, fHeight/3), oscillator.getAngle(), oscillator.getAngle()+360, 8, 0, 20,
						new Bullet(80 - difficulty.getBulletNumber()*2, BulletShape.DIAMOND, "green", false, true), new CompoundBulletPath(new BezierBulletPath(
								new Point2D.Float(0, 0), new Point2D.Float(0, 100), new Point2D.Float(100, 100), PathTraversal.ROOT), 
								new AimedBulletPath(new Point2D.Float(0, 0), fHeight + fWidth, player, PathTraversal.LINEAR)), false, false);
				for (Bullet b:arc.step()){
					bullets.put(b.getSprite(), b);
				}
				oscillator.oscillate();
			}
			break;
		case 10:
			/**
			 * Ember Sign: "Basaltic Flow"
			 */
			if (internaltimer%(difficulty.getBulletNumber()*5)==0){
				ArcBulletPattern arc = new ArcBulletPattern(
						new Point2D.Float(fWidth/2, fHeight/4), 0, 360, 4, 0, 40,
						new Bullet(64, BulletShape.WAVE_SMALL, "orange", false, true), new CompoundBulletPath(true, 
								new AimedBulletPath(new Point2D.Float(0, 0), FullGame.WIDTH/3, player, PathTraversal.ROOT),
								new AimedBulletPath(new Point2D.Float(0, 0), FullGame.WIDTH/3, player, PathTraversal.ROOT)), false, false);
				for (Bullet b:arc.step()){
					bullets.put(b.getSprite(), b);
				}
			}
			if (Math.random()*difficulty.getBulletNumber()*5<1){
				float rand = (float)(Math.random()*360);
				ArcBulletPattern arc = new ArcBulletPattern(
						new Point2D.Float(fWidth/2, fHeight/4), rand, rand + 360, 6, 0, 40,
						new Bullet(16, BulletShape.BUBBLE, "red", true, true), 
						new LinearBulletPath(new Point2D.Float(0, 0), new Point2D.Float(fHeight, 0)), false, false);
				for (Bullet b:arc.step()){
					bullets.put(b.getSprite(), b);
				}
			}
			break;
		}
	}
	/**
	 * @return true if the level is deemed to have been completed
	 */
	public boolean isFinished(){
		return this.finished;
	}
	/**
	 * @return a signal that denotes the changing of bgm to a boss theme
	 */
	public boolean getBossSignal(){
		return this.bossSignal;
	}
}
