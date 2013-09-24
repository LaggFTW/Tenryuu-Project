package edu.mbhs.madubozhi.touhou.ui;

import java.awt.AWTException;
import java.io.File;
import java.util.ArrayList;

import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;

import edu.mbhs.madubozhi.touhou.audio.MPlayer;
import edu.mbhs.madubozhi.touhou.audio.Music;
import edu.mbhs.madubozhi.touhou.game.util.Mode;

/**
 * Main class that is run at the start. It is an extension of the JFrame swing Component, and combines the game, novel,
 * and user interfaces.
 * @author bowenzhi
 *
 */
public class FullGame extends JFrame {
	private static final long serialVersionUID = -7325653220164008869L;
	
	/**
	 * Standard Width of the Game
	 */
	public static final int WIDTH = 1280;//Toolkit.getDefaultToolkit().getScreenSize().width;
	/**
	 * Standard Height of the Game
	 */
	public static final int HEIGHT= 720;//Toolkit.getDefaultToolkit().getScreenSize().height;
	
	/**
	 * Standard Framerate of the Game
	 */
	public static final int FPS = 60;
	/**
	 * Standard Framerate of the Game
	 */
	public static final int MILLIS_PER_FRAME = 1000/FPS;
	
	private int choiceSize = 0;
	private LoadingScreen ls = new LoadingScreen();
	private Novel currentScene;
	private int safetyDelay=150;
	private NovelOptions opts;
	private Character mc;
	private Character sc;
	private Menu menu;
	private OptionMenu om;
	private Mode gameMode;
	private boolean response = false;
	protected Music currentMusic=null;
	protected MPlayer mplayer = new MPlayer();
	private boolean gameRunning = true;
	protected int resevoir = 0;

	protected GameOptions go;
	private CharacterDesignMenu cdm = new CharacterDesignMenu(this);

	protected int menuSelection = -1;
	protected ArrayList<Integer>choices = new ArrayList<Integer>();

	public static void main(String [] args){new FullGame().setVisible(true);}

	/**
	 * Instantiates this FullGame class
	 */
	public FullGame(){
		super();
		//BGM, SFX, voice, GFX, textSpeed, language
		go = new GameOptions(true, true, true, true, 5, false, false);
		om = new  OptionMenu(this, go);
		opts = new NovelOptions(go.textSpeed, go.BGM, go.voice, go.insta);
		ls.setVisible(true);
		menu = new Menu(this);
		mplayer.changeAudio(go.BGM?new File("audio/bgm/title music.mp3"):null);
		showWindow(menu);
		parse(menuSelection);
	}
	
	public FullGame(boolean a){
		go = new GameOptions(true, true, true, true, 5, false, false);
		om = new  OptionMenu(this, go);
		opts = new NovelOptions(go.textSpeed, go.BGM, go.voice, go.insta);
	}

	/**
	 * Parses the menu selections called when the user makes a choice
	 * @param menuSelection index of the choice that the user selected
	 */
	private void parse(int menuSelection){
		switch(menuSelection){
		case 0:
			mc=null; sc=null;
			showWindow(cdm);
			if(mc==null || sc==null)
				backToMenu();
			else{
				mplayer.changeAudio(null);
				startGame();
			}
			break;
		case 1: 
			gameMode=Mode.EXTRA;
			mplayer.changeAudio(null);
			switchGame(7);
			showWindow(menu);
			parse(this.menuSelection);
			break;
		case 2: 
			//TODO Practice
			System.out.println("Practise Menu");
			break;
		case 3:
			showWindow(om);
			opts = new NovelOptions(go.textSpeed, go.BGM, go.voice, go.insta);
			backToMenu();
			break;
		case 4: 
			//TODO LIbraries
			System.out.println("Libraries List");
			break;
		case 5: 
			System.exit(0);
		}
	}

	protected void updateMenuArt(int code){
		if(code==0)
			menu.updateBG();
		else
			menu.initImages();
	}

	private void startGame(){
		currentScene = new Novel(opts, "partA.txt", this);
		switchScene();
		currentMusic.stop();
		if(choices.get(0)==0)
			switchGame(1);
		else{
			reset();
			showWindow(menu);
		}
		reset();
		showWindow(menu);
		parse(menuSelection);
	}

	private void showWindow(JFrame j){
		j.setVisible(true);
		j.repaint();
		try{Thread.sleep(safetyDelay);}catch(Exception e){}
		response=false;
		while(!response){}
		response=false;
		ls.setVisible(true);
		try{Thread.sleep(safetyDelay);}catch(Exception e){}
		j.setVisible(false);
		j.dispose(); j=null;
	}

	public void setMode(Mode m){
		gameMode = m;
	}

	/**
	 * Plays music of the currently set music
	 */
	public void playMusic(){
		if(!currentMusic.clip.isActive() && go.BGM)
			currentMusic.start();
	}
	
	/**
	 * Resets the menu
	 */
	public void reset(){
		menuSelection=-1;
		choiceSize=0;
		choices=new ArrayList<Integer>();
		gameMode = null;
	}

	/**
	 * Stops the currently running music
	 */
	public void stopMusic(){
		currentMusic.stop();
		currentMusic=null;
	}

	/**
	 * Method used to allow for communications between other UI components
	 */
	public void respond(){
		response=true;
	}

	/**
	 * Resets the menu and shows the initial main menu
	 */
	public void backToMenu(){
		reset();
		ls.setVisible(true);
		showWindow(menu);
		parse(menuSelection);
	}

	/**
	 * Utilizes the Novel class to advance scenes in the Novel
	 * @param choiceNum
	 * @param partA
	 * @param partB
	 * @return
	 */
	public Novel nextScene(int choiceNum, String partA, String partB){
		return new Novel(opts, choices.get(choiceNum)==0?partA:partB, this);
	}

	public void setChar(Character a, int charCode){
		if(charCode == 0)mc=a;
		else			sc=a;
	}

	/**
	 * Switches scenes in the Novel
	 */
	public void switchScene(){
		currentScene.setVisible(true);
		try{Thread.sleep(safetyDelay);}catch(Exception e){}
		while(choiceSize == choices.size()){}
		choiceSize++;
		try{Thread.sleep(safetyDelay);}catch(Exception e){}
		currentScene.dispose(); currentScene.kill(); currentScene=null;
	}

	/**
	 * Switches from the UI components to the game
	 * @param stageNum
	 */
	public void switchGame(int stageNum){
		GameWindow g;
		try {
			g = new GameWindow(gameMode, stageNum, this, new GLCapabilities());
			g.setVisible(true);
			try{Thread.sleep(safetyDelay);}catch(Exception e){}
			setGameRunning(true);
			while(isGameRunning()){}
			response=false;
			ls.setVisible(true);
			try{Thread.sleep(safetyDelay);}catch(Exception e){}
			g.dispose(); g.stopThreads();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public Character getMC(){
		return mc;
	}

	public Character getSC(){
		return sc;
	}

	public void optionUpdate(){
		opts = new NovelOptions(go.textSpeed, go.BGM, go.voice, go.insta);
	}

	public boolean isGameRunning() {
		return gameRunning;
	}

	public void setGameRunning(boolean gameRunning) {
		this.gameRunning = gameRunning;
	}

}