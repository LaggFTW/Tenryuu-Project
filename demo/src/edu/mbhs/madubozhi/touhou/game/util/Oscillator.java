package edu.mbhs.madubozhi.touhou.game.util;

/**
 * Oscillator class that helps automate and handle oscillatory motion (usually for certain spellcards).
 * @author bowenzhi
 */
public class Oscillator {
	private OscillationType type;
	private float theta;
	private float omega;
	private float alpha;
	private float maxOmega;//0 = no limit
	private boolean increaseOmega;
	private float period;
	private float amplitude;
	
	private float counter;
	
	/**
	 * Instantiates an oscillator with the following specifications.
	 * @param type Type of oscillation
	 * @param omega Used in BOUNDED_OMEGA
	 * @param alpha Used in BOUNDED_OMEGA
	 * @param maxOmega Used in BOUNDED_OMEGA
	 * @param period Used in COSINE
	 * @param amplitude Used in COSINE
	 */
	public Oscillator(OscillationType type, float thetaInit, float omega, float alpha, float maxOmega, float period, float amplitude){
		this.type = type;
		this.theta = thetaInit;
		this.omega = omega;
		this.alpha = alpha;
		this.maxOmega = maxOmega;
		this.period = period;
		this.amplitude = amplitude;
		this.counter = 0;
		this.increaseOmega = true;
	}
	
	/**
	 * Updates the variables in this oscillatory system. Call this consistently for a realistic oscillation pattern.
	 */
	public void oscillate(){
		switch(type.getIndex()){
		case 1:
			this.theta = amplitude * (float)(Math.cos(counter/period));
			break;
		case 2:
			this.theta += this.omega;
			if (this.maxOmega != 0){
				if (this.increaseOmega)
					if (this.omega < this.maxOmega)
						this.omega += this.alpha;
					else
						this.increaseOmega = false;
				else
					if (this.omega > -this.maxOmega)
						this.omega -= this.alpha;
					else
						this.increaseOmega = true;
			} else
				this.omega += this.alpha;
			break;
		default:
			break;
		}
		counter++;
	}
	
	/**
	 * @return The angle that the oscillator currently is storing
	 */
	public float getAngle(){
		return theta;
	}
	
	public boolean angleIncreasing(){
		switch(type.getIndex()){
		case 1: return (float)(Math.sin(counter/period)) < 0;
		case 2: return omega > 0;
		default: return false;
		}
	}
	
	public boolean omegaIncreasing(){
		switch(type.getIndex()){
		case 1: return (float)(Math.cos(counter/period)) < 0;
		case 2: return increaseOmega;
		default: return false;
		}
	}
	
	public float getdAngledt(){
		switch(type.getIndex()){
		case 1: return -(float)(Math.sin(counter/period));
		case 2: return omega;
		default: return 0.0f;
		}
	}
	
	public float getdOmegadt(){
		switch(type.getIndex()){
		case 1: return -(float)(Math.cos(counter/period));
		case 2: return alpha*(increaseOmega?1:-1);
		default: return 0.0f;
		}
	}
	
	public Oscillator clone(){
		return new Oscillator(this.type, this.theta, this.omega, this.alpha, this.maxOmega, this.period, this.amplitude);
	}
	
}
