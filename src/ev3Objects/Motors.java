package ev3Objects;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Creates an object that stores useful motors for the EV3
 *
 */
public class Motors {
	
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private EV3LargeRegulatedMotor leftSideUltraSoundMotor;
	private EV3LargeRegulatedMotor blockLiftMotor;
	
	private double wheelRadius;
	private double axleLength;
	
	/**
	 * Stores essential objects for the EV3 to use its motors.
	 * @param pLeftMotor The left motor
	 * @param pRightMotor The right motor
	 * @param pLeftSideUltraSoundMotor The motor connected to the ultrasonic
	 * @param pblockLiftMotor The motor responsible for lifting the object
	 * @param pWheelRadius The radius of the ground wheels
	 * @param pAxleLength The distance between the ground wheels
	 */
	public Motors (EV3LargeRegulatedMotor pLeftMotor, EV3LargeRegulatedMotor pRightMotor, 
			EV3LargeRegulatedMotor pLeftSideUltraSoundMotor, EV3LargeRegulatedMotor pblockLiftMotor, 
			double pWheelRadius, double pAxleLength)
	{
		leftMotor 					= pLeftMotor;
		rightMotor 					= pRightMotor;
		leftSideUltraSoundMotor 	= pLeftSideUltraSoundMotor;
		blockLiftMotor 				= pblockLiftMotor;
		wheelRadius 				= pWheelRadius;
		axleLength 					= pAxleLength;
	}
	
	// Getters
	
	/**
	 * Get the left motor.
	 * @return The left motor
	 */
	public EV3LargeRegulatedMotor getLeftMotor() {
		return leftMotor;
	}
	/**
	 * Get the rightMotor
	 * @return The right motor
	 */
	public EV3LargeRegulatedMotor getRightMotor() {
		return rightMotor;
	}

	/**
	 * Get the motor on which is attached the ultrasonic sensor
	 * @return The left ultrasonic sensor motor
	 */
	public EV3LargeRegulatedMotor getLeftUltraSoundMotor() {
		return leftSideUltraSoundMotor;
	}

	/**
	 * Get the motor responsible for block pick-up
	 * @return The motor used to pick-up the block
	 */
	public EV3LargeRegulatedMotor getBlockLiftMotor() {
		return blockLiftMotor;
	}

	/**
	 * Get the radius of the ground wheels
	 * @return The radius of the wheels
	 */
	public double getWheelRadius() {
		return wheelRadius;
	}
	
	/**
	 * Get the distance between the wheels
	 * @return The distance between the wheels
	 */
	public double getAxleLength() {
		return axleLength;
	}

}
