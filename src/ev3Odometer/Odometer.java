/*
s * Odometer.java
 */

package ev3Odometer;

import ev3Objects.Motors;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Creates an objects to calculate the EV3's position and header.
 */
public class Odometer extends Thread {
	// robot position
	private double x, y, theta, distanceTravelled;
	private double last_tacho_L;
	private double last_tacho_R;
	private double wheelRadius;
	private double axleLength;
	private static EV3LargeRegulatedMotor leftMotor;
	private static EV3LargeRegulatedMotor rightMotor;


	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	/**
	 * Stores the motors essential to perform calculations
	 * @param pMotors The motors attached to wheels
	 */
	public Odometer(Motors pMotors) {
		leftMotor = pMotors.getLeftMotor();
		rightMotor = pMotors.getRightMotor();
		wheelRadius = pMotors.getWheelRadius();
		axleLength = pMotors.getAxleLength();
		x = 0.0;
		y = 0.0;
		distanceTravelled = 0.0;
		//by default the robot is pointing along the positive y axis
		theta = Math.PI/2;
		lock = new Object();
	}

	/**
	 * Runs the odometer (required for thread)
	 */
	public void run() {
		long updateStart, updateEnd;

		//reset the tacho count, and set the initial tacho counts to the last_tacho_X variables

		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();

		last_tacho_L = leftMotor.getTachoCount();
		last_tacho_R = rightMotor.getTachoCount();

		while (true) {
			updateStart = System.currentTimeMillis();

			int current_tacho_L = leftMotor.getTachoCount();
			int current_tacho_R = rightMotor.getTachoCount();

			//calculating the distance each wheel has travelled
			double distanceL = Math.PI*wheelRadius*(current_tacho_L - last_tacho_L)/180;
			double distanceR = Math.PI*wheelRadius*(current_tacho_R - last_tacho_R)/180;
			//updating the last tacho counts
			last_tacho_L = current_tacho_L;
			last_tacho_R = current_tacho_R;
			//calculating the change in angle and change in absolute displacement
			double deltaD = 0.5*(distanceL +distanceR);
			double deltaT = (distanceR-distanceL)/axleLength;


			synchronized (lock) {
				//updating the locations of the variables
				theta += deltaT;

				//maintain the bounds of theta
				if(theta < 0)
					theta += 2*Math.PI;
				if(theta > 2*Math.PI)
					theta -= 2*Math.PI;

				setDistanceTravelled(getDistanceTravelled() + deltaD);

				setX(x + deltaD *Math.cos(theta));
				setY(y + deltaD *Math.sin(theta));
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	/**
	 * Update the position
	 * @param position The array that stores position and heading
	 * @param update Whether the value should be updated or not
	 */
	public void getPosition(double[] position, boolean[] update) {
		// Ensure the value positions don't change while odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta*(180/Math.PI); //using degrees is easier on the eyes
		}
	}

	/**
	 * Get the x coordinate
	 * @return The x coordinate
	 */
	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	/**
	 * Get the y coordinate
	 * @return The y coordinate
	 */
	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	/**
	 * Get the heading angle
	 * @return The angle the EV3 is facing
	 */
	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	/**
	 * Return the distance traveled by the EV3.
	 * @return The distance traveled
	 */
	public double getDistanceTravelled() {

		double result;

		synchronized (lock) {
			result = distanceTravelled;
		}

		return result;
	}

	/**
	 * Set the distance to be traveled.
	 * @param distance The distance to be traveled.
	 */
	public void setDistanceTravelled(double distance) {
		synchronized(lock){
			this.distanceTravelled = distance;
		}
	}

	/**
	 * Set the position
	 * @param position The array that stores position and heading
	 * @param update Whether the value should be updated or not
	 */
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	/**
	 * Set the x coordinate
	 * @param x The x coordinate
	 */
	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	/**
	 * Set the y coordinate
	 * @param y The y coordinate
	 */
	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	/**
	 * Set the heading angle
	 * @param theta The heading angle 
	 */
	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}