package edu.mbhs.madubozhi.touhou.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;

import edu.mbhs.madubozhi.touhou.game.Player;
import edu.mbhs.madubozhi.touhou.game.util.Mode;

/**
 * Java swing Component for displaying game info
 * @author bowenzhi
 *
 */
public class ScorePanel extends JPanel implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1501851428879200599L;
	private int border = 10;
	private int xOffset = (Toolkit.getDefaultToolkit().getScreenSize().width - FullGame.WIDTH)/2 + (int)(FullGame.WIDTH*.7) - border;
	private int yOffset = (Toolkit.getDefaultToolkit().getScreenSize().height - FullGame.HEIGHT)/2;
	private int pWidth = FullGame.WIDTH - (int)(FullGame.WIDTH*.7) + border;
	private int pHeight= (int)(FullGame.HEIGHT-2*border);
	private int hits, score, graze, stage, lives, bombs;
	private Mode mode;
	private Thread thread;
	
	private boolean stateChanged;
	
	/**
	 * Instantiates this panel with the following conditions
	 * @param m Difficulty level
	 * @param stage Stage number
	 */
	public ScorePanel(Mode m, int stage){
		this.stateChanged = true;
		this.lives = Player.PLAYER_STARTING_LIVES;
		this.bombs = Player.PLAYER_STARTING_BOMBS;
		this.mode = m;
		this.stage = stage;
		this.setSize(pWidth, pHeight);
		Dimension d = new Dimension(pWidth, pHeight);
		this.setPreferredSize(d);
		this.setMinimumSize(d);
		this.setMaximumSize(d);
		this.setFocusable(false);
		this.setBackground(Color.BLACK);
		this.thread = new Thread(this);
		this.thread.start();
	}
	
	public void paint(Graphics g){
		Image i=createImage(getWidth(), getHeight());
		render((i.getGraphics()));
		g.drawImage(i,0,0,this);
		stateChanged = false;
	}

	/**
	 * Renders this ScorePanel, drawing the background and the game information
	 * @param g Graphics object used to render this panel
	 */
	private synchronized void render(Graphics g) {
		super.paint(g);
		g.translate(xOffset, yOffset);
		drawBackground(g);
		renderGameInfo(g);
		g.translate(-xOffset, -yOffset);
	}
	
	/**
	 * Renders text displaying the stage, hits, difficulty, graze, score, lives, and bombs of the player
	 * @param g Graphics object used to render this panel
	 */
	private void renderGameInfo(Graphics g) {
		int fSize=40;
		g.setColor(Color.BLACK);
		int x = 3*border;
		g.setFont(new Font("Times New Roman", fSize, fSize));
		g.drawString("Stage: " + stage, x, 30+fSize);
		g.drawString("Hits: " + hits, x, 40+2*fSize);
		g.drawString("Difficulty: " + mode.getName(), x, 50+3*fSize);
		g.drawString("Graze: " + graze, x, 60+4*fSize);
		g.drawString("Score: " + score, x, 70+5*fSize);
		g.drawString("Lives: " + (lives<0?"NULL":lives), x, 80+6*fSize);
		g.drawString("Bombs: " + bombs, x, 90+7*fSize);
	}

	/**
	 * Draws the background image for the game. Currently is a solid color with a border
	 * @param g Graphics object of the Component that this will be drawn on
	 */
	private void drawBackground(Graphics g){
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, border, FullGame.HEIGHT);
		g.fillRect(pWidth - border, 0, border, FullGame.HEIGHT);
		g.fillRect(border, 0, pWidth - 2*border, border);
		g.fillRect(border, FullGame.HEIGHT - border, pWidth - 2*border, border);
		g.setColor(Color.WHITE);
		g.fillRect(border, border, pWidth - 2*border, pHeight);
	}
	
	/**
	 * Sets the game information to be displayed
	 * @param hits
	 * @param score
	 * @param graze
	 * @param stage
	 * @param lives
	 * @param bombs
	 */
	public void setGameInfo(int hits, int score, int graze, int stage, int lives, int bombs){
		this.hits = hits;
		this.score = score;
		this.graze = graze;
		this.stage = stage;
		this.lives = lives;
		this.bombs = bombs;
		this.stateChanged = true;
	}
	
	@Override
	public void run() {
		while (Thread.currentThread() == thread){
			if (stateChanged){
				repaint();
			}
			try {
				Thread.sleep(FullGame.MILLIS_PER_FRAME);
			} catch(Throwable t){
				System.err.println("DEBUG: Game has ended");
			}
		}
	}

	public Thread getThread() {
		return thread;
	}
}
