package edu.mbhs.madubozhi.touhou.game.enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import edu.mbhs.madubozhi.touhou.game.bullet.pattern.BulletPath;

/**
 * Basic Enemy class, which is a parallel to the Bullet class, albeit with a few functional differences. Can hit objects
 * within the game such as other enemies and players.
 * @author bowenzhi
 */
public class Enemy{
	public int initx, targetX, size;
	public float x, y;
	public Color color;
	public double yVel;
	private int sHeight= Toolkit.getDefaultToolkit().getScreenSize().height;
	protected int border;
	protected int fWidth;
	private int fHeight=(int)sHeight-2*border;
	public boolean offScreenDelete = true;
	
	protected int r = 5;
	
	//Path
	private BulletPath path;
	public float q;
	private float dq;
	private boolean finished = false;
	protected boolean lockAngle;//Whether or not to alter the orientation of the bullet based on the path
	
	//Sprites
	protected BufferedImage sprite;
	protected BufferedImage innerSprite;
	protected boolean overlay;
	
	/**
	 * Instantiates an enemy with the specified parameters and conditions
	 * @param x Initial x position
	 * @param target Target location
	 * @param size Size of enemy
	 * @param yVel Velocity of enemy
	 * @param color Color of enemy
	 * @param border Border of frame that this enemy is drawn in
	 * @param fWidth Width of frame that this enemy is drawn in
	 * @param lockAngle Whether or not this enemy's orientation changes based on its path
	 */
	public Enemy(int x, int target, int size, double yVel, Color color, int border, int fWidth, boolean lockAngle){
		this(x, target, size, yVel, color, border, fWidth, null, false, true);
	}
	
	/**
	 * Instantiates an enemy with the specified parameters and conditions
	 * @param x Initial x position
	 * @param target Target location
	 * @param size Size of enemy
	 * @param yVel Velocity of enemy
	 * @param color Color of enemy
	 * @param border Border of frame that this enemy is drawn in
	 * @param fWidth Width of frame that this enemy is drawn in
	 * @param lockAngle Whether or not this enemy's orientation changes based on its path
	 * @param offScreenDelete Whether this enemy will be considered "finished" if it is offScreen
	 */
	public Enemy(int x, int target, int size, double yVel, Color color, int border, int fWidth, boolean lockAngle, boolean offScreenDelete){
		this(x, target, size, yVel, color, border, fWidth, null, false, offScreenDelete);
	}
	
	/**
	 * Instantiates an enemy with the specified parameters and conditions
	 * @param x Initial x position
	 * @param target Target location
	 * @param size Size of enemy
	 * @param yVel Velocity of enemy
	 * @param color Color of enemy
	 * @param border Border of frame that this enemy is drawn in
	 * @param fWidth Width of frame that this enemy is drawn in
	 * @param path BulletPath that this enemy will traverse
	 * @param lockAngle Whether or not this enemy's orientation changes based on its path
	 * @param offScreenDelete Whether this enemy will be considered "finished" if it is offScreen
	 */
	public Enemy(int x, int target, int size, double yVel, Color color, int border, int fWidth, BulletPath path, boolean lockAngle, boolean offScreenDelete){
		this.x = x; initx = x; targetX = target; this.size = size; this.color = color; this.yVel = yVel;
		this.border = border; this.fWidth = fWidth;
		y=0; this.path = path; q = 0; dq = (float) (yVel / 10000);
		this.lockAngle = lockAngle;
		this.overlay = true;
		this.offScreenDelete = offScreenDelete;
	}
	
	/**
	 * Instantiates an enemy with the specified parameters and conditions
	 * @param x Initial x position
	 * @param target Target location
	 * @param size Size of enemy
	 * @param yVel Velocity of enemy
	 * @param color Color of enemy
	 * @param b Sprite of enemy
	 * @param border Border of frame that this enemy is drawn in
	 * @param fWidth Width of frame that this enemy is drawn in
	 * @param path BulletPath that this enemy will traverse
	 * @param lockAngle Whether or not this enemy's orientation changes based on its path
	 * @param offScreenDelete Whether this enemy will be considered "finished" if it is offScreen
	 */
	public Enemy(int x, int target, int size, double yVel, Color color, BufferedImage b, int border, int fWidth, BulletPath path, boolean lockAngle, boolean offScreenDelete){
		this.x = x; initx = x; targetX = target; this.size = size; this.color = color; this.yVel = yVel;
		this.border = border; this.fWidth = fWidth;
		y=0; this.path = path; q = 0; dq = (float) (yVel / 10000);
		this.lockAngle = lockAngle;
		if (this.color == null){
			sprite = b;
			this.overlay = false;
		} else
			if (b != null){
				innerSprite = b;
				RescaleOp ro = new RescaleOp(color.getComponents(null), new float[]{0f, 0f, 0f, 0f}, null);
				sprite = ro.filter(b, null);
				this.overlay = true;
			}
		this.offScreenDelete = offScreenDelete;
	}
	
	/**
	 * Instantiates an enemy with the specified parameters and conditions
	 * @param x Initial x position
	 * @param target Target location
	 * @param size Size of enemy
	 * @param yVel Velocity of enemy
	 * @param b Sprite of enemy
	 * @param border Border of frame that this enemy is drawn in
	 * @param fWidth Width of frame that this enemy is drawn in
	 * @param path BulletPath that this enemy will traverse
	 * @param lockAngle Whether or not this enemy's orientation changes based on its path
	 * @param offScreenDelete Whether this enemy will be considered "finished" if it is offScreen
	 */
	public Enemy(int x, int target, int size, double yVel, BufferedImage b, int border, int fWidth, BulletPath path, boolean lockAngle, boolean offScreenDelete){
		this.x = x; initx = x; targetX = target; this.size = size; this.yVel = yVel;
		this.border = border; this.fWidth = fWidth;
		y=0; this.path = path; q = 0; dq = (float) (yVel / 10000);
		this.lockAngle = lockAngle;
		this.overlay = false;
		this.sprite = b;
		this.offScreenDelete = offScreenDelete;
	}
	
	/**
	 * Alternate constructor
	 * @param x Initial x position
	 * @param target Target location
	 * @param size Size of enemy
	 * @param yVel Velocity of enemy
	 * @param color Color of enemy
	 * @param border Border of frame that this enemy is drawn in
	 * @param fWidth Width of frame that this enemy is drawn in
	 * @param path BulletPath that this enemy will traverse
	 * @param lockAngle Whether or not this enemy's orientation changes based on its path
	 * @param offScreenDelete Whether this enemy will be considered "finished" if it is offScreen
	 */
	public Enemy(int x, int target, int size, double yVel, Color color, int border, int fWidth,	boolean lockAngle, boolean offScreenDelete, int outerRadius) {
		this(x, target, size, yVel, color, border, fWidth, lockAngle, offScreenDelete);
		this.r = outerRadius;
	}
	
	/**
	 * Alternate constructor
	 * @param x Initial x position
	 * @param target Target location
	 * @param size Size of enemy
	 * @param yVel Velocity of enemy
	 * @param color Color of enemy
	 * @param border Border of frame that this enemy is drawn in
	 * @param fWidth Width of frame that this enemy is drawn in
	 * @param path BulletPath that this enemy will traverse
	 * @param lockAngle Whether or not this enemy's orientation changes based on its path
	 * @param offScreenDelete Whether this enemy will be considered "finished" if it is offScreen
	 * @param outerRadius Border of enemy
	 */
	public Enemy(int x, int target, int size, double yVel, Color color, int border, int fWidth, BulletPath path, boolean lockAngle, boolean offScreenDelete, int outerRadius){
		this(x, target, size, yVel, color, border, fWidth, path, lockAngle, offScreenDelete);
		this.r = outerRadius;
	}

	/**
	 * Appends a BulletPath to this Enemy
	 * @param path
	 */
	public void addPath(BulletPath path){
		this.path = path; q = 0; dq = (float) (yVel / 10000);
	}
	
	/**
	 * Draws this enemy
	 * @param g Graphics object of the Component that this will be drawn on
	 */
	public void draw(Graphics g){
		if (sprite != null){
			g.translate((int)(this.x-this.size)-r, (int)(this.y-this.size)-r);
			if (overlay){
				g.drawImage(sprite, 0, 0, null);
				g.drawImage(innerSprite, 0, 0, null);
			} else
				g.drawImage(sprite, 0, 0, null);
			g.translate(-((int)(this.x-this.size)-r), -((int)(this.y-this.size)-r));
		} else {
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(this.color);
			g2d.fill(new Ellipse2D.Float(this.x-this.size-r, this.y-this.size-r, 2*(this.size+r), 2*(this.size+r)));
			g2d.setColor(Color.WHITE);
			g2d.fill(new Ellipse2D.Float(this.x-this.size, this.y-this.size, this.size*2, this.size*2));
		}
	}
	
	/**
	 * Updates the position of the enemy
	 */
	public void step(){
		if (path == null){
			y+=yVel;
			double xVel = (yVel)/sHeight*(float)(targetX-initx);
			x+=xVel;
			if(y>fHeight) finished = true;
			if(x>fWidth)x=0;
			if(x<0)x=fWidth;
		} else {
			Point2D.Float temp = path.step(q);
			if (temp == null){
				finished = true;
				return;
			}
			x = temp.x;
			y = temp.y;
			q += dq;
		}
	}
	
	public void setOuterRadius(int i){this.r = i;}
	
	/**
	 * @return Area of the enemy's hitbox
	 */
	public Area getArea(){
		return new Area(new Ellipse2D.Double(x-size, y-size, 2*size, 2*size));
	}
	
	/**
	 * @return An enemy with the exact same basic properties as this enemy
	 */
	public Enemy cloneBasic(){
		return new Enemy(this.initx, this.targetX, this.size, this.yVel, this.color, this.sprite, this.border, this.fWidth, this.path, this.lockAngle, this.offScreenDelete);
	}
	
	/**
	 * @return Whether or not the enemy has finished traversing its path
	 */
	public boolean isFinished(){
		return finished;
	}
}