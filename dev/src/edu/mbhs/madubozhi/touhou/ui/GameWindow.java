package edu.mbhs.madubozhi.touhou.ui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;

import edu.mbhs.madubozhi.touhou.game.FinalGame;
import edu.mbhs.madubozhi.touhou.game.util.Mode;

public class GameWindow extends JFrame {
	private static final long serialVersionUID = 2845976885119981647L;
	public static final String DEFAULT_TITLE = "Tenryu Project: Sanreiryuu";
	private FullGame parent;
	private FinalGame gameCanvas;
	private ScorePanel scorePanel;
	
	public GameWindow(Mode m, int stage, FullGame par, GLCapabilities glc) throws AWTException{
		super(DEFAULT_TITLE);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
		this.setUndecorated(true);
		this.setFocusable(false);
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Invisimouse"); 
		this.setCursor(blankCursor);
		this.getContentPane().setBackground(Color.BLACK);
		this.parent = par;
		this.gameCanvas = new FinalGame(m, stage, this, glc, parent.go.BGM, parent.go.SFX);
		this.scorePanel = new ScorePanel(m, stage);
		this.add(gameCanvas);
		this.add(scorePanel);
		this.setVisible(true);
		Robot b = new Robot();
		b.mouseMove((Toolkit.getDefaultToolkit().getScreenSize().width - FullGame.WIDTH)/2 + FullGame.WIDTH/2, (Toolkit.getDefaultToolkit().getScreenSize().height - FullGame.HEIGHT)/2 + FullGame.HEIGHT/2);
		b.mousePress(InputEvent.BUTTON1_MASK);
		b.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	public FullGame parent() {
		return this.parent;
	}
	
	public FinalGame gameCanvas(){
		return this.gameCanvas;
	}
	
	public ScorePanel scorePanel(){
		return this.scorePanel;
	}

	public void stopThreads() {
		gameCanvas.killThread();
		scorePanel.getThread().interrupt();
	}
	
}