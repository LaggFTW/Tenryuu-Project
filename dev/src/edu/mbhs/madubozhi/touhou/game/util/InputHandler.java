package edu.mbhs.madubozhi.touhou.game.util;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles the user's keyboard inputs and translates them into inputs usable 
 * by game objects
 * @author bowenzhi and mattdu
 */
public class InputHandler implements KeyListener{
	//key signals for going forward, backwards, left, right, focusing, shooting, bombing, restarting, and pausing
	private boolean fwd, bwd, lft, rgt, focus, shoot, defBomb, offBomb, restartSignal;

	/**
	 * Initiates a new instance of itself as a KeyListener
	 * @param comp
	 */
	public InputHandler(Component comp){
		comp.addKeyListener(this);
	}

	/**
	 * Returns the fwd boolean value
	 * @return
	 */
	public boolean getFwd(){
		return fwd;
	}

	/**
	 * Returns the bwd boolean value
	 * @return
	 */
	public boolean getBwd(){
		return bwd;
	}

	/**
	 * Returns the lft boolean value
	 * @return
	 */
	public boolean getLft(){
		return lft;
	}

	/**
	 * Returns the rgt boolean value
	 * @return
	 */
	public boolean getRgt(){
		return rgt;
	}

	/**
	 * Returns the focus boolean value
	 * @return
	 */
	public boolean getFocus(){
		return focus;
	}

	/**
	 * Returns the shoot boolean value
	 * @return
	 */
	public boolean getShoot(){
		return shoot;
	}

	/**
	 * Returns the defBomb boolean value
	 * @return
	 */
	public boolean defBomb(){
		return defBomb;
	}

	/**
	 * Returns the offBomb boolean value
	 * @return
	 */
	public boolean offBomb(){
		return offBomb;
	}

	/**
	 * Returns the restartSignal boolean value
	 * @return
	 */
	public boolean getRestartSignal(){
		return restartSignal;
	}

	/**
	 * keyPressed event: adjusts different values according to which keys are pressed down
	 * Allows for simultaneous input translation
	 */
	public void keyPressed(KeyEvent e){
		//Quit
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			System.exit(0);

		//Handle movement keys
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode()==KeyEvent.VK_W)
			fwd = true;
		if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode()==KeyEvent.VK_S)
			bwd = true;
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_A)
			lft = true;
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode()==KeyEvent.VK_D)
			rgt = true;
		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
			focus = true;
		if (e.getKeyCode() == KeyEvent.VK_Z)
			shoot = true;

		//Handle game event keys
		if (e.getKeyCode() == KeyEvent.VK_X)
			defBomb = true;
		if (e.getKeyCode() == KeyEvent.VK_C)
			offBomb = true;
		if (e.getKeyCode() == KeyEvent.VK_R)
			restartSignal = true;
	}


	/**
	 * keyReleased event: unadjusts different values according to which keys are pressed down
	 * Allows for simultaneous input translation
	 */
	public void keyReleased(KeyEvent e){
		//Handle movement keys
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode()==KeyEvent.VK_W)
			fwd = false;
		if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode()==KeyEvent.VK_S)
			bwd = false;
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_A)
			lft = false;
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode()==KeyEvent.VK_D)
			rgt = false;
		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
			focus = false;
		if (e.getKeyCode() == KeyEvent.VK_Z)
			shoot = false;

		//Handle game evnet keys
		if (e.getKeyCode() == KeyEvent.VK_X)
			defBomb = false;
		if (e.getKeyCode() == KeyEvent.VK_C)
			offBomb = false;
		if (e.getKeyCode() == KeyEvent.VK_R)
			restartSignal = false;
	}

	/**
	 * keyTyped event: has no use, since keyReleased and keyPressed are both implemented
	 */
	public void keyTyped(KeyEvent e) {}

}