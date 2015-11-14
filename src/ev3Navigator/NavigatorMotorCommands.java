package ev3Navigator;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Helper class to refactor out the basic motor commands of the navigator,
 * to shorten the navigator class size
 * @author Callum
 *
 */
public class NavigatorMotorCommands {
	
	EV3LargeRegulatedMotor leftMotor;
	EV3LargeRegulatedMotor rightMotor;
	
	/**
	 * Accepts the left motor and right motor, needed for the basic motor commands
	 * @param pLeftMotor
	 * @param pRightMotor
	 */
	
	public NavigatorMotorCommands(EV3LargeRegulatedMotor pLeftMotor, EV3LargeRegulatedMotor pRightMotor){
		leftMotor = pLeftMotor;
		rightMotor = pRightMotor;
		
		
		
	}
	
	/**
	 *  Stops the motors
	 */
	public void stopMotors()
	{
		leftMotor.stop();
		rightMotor.stop();
	}

	/**
	 * Set the navigator to a certain speed
	 * @param speed The speed to be set
	 */
	public void driveStraight(int speed)
	{
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);

		leftMotor.forward();
		rightMotor.forward();
	}

	/**
	 * Rotate clockwise at a certain speed
	 * @param speed The speed to be set
	 */
	public void rotateClockWise(int speed)
	{
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);

		leftMotor.forward();
		rightMotor.backward();
	}

	/**
	 * Rotate counter-clockwise at a certain speed
	 * @param speed The speed to be set
	 */
	public void rotateCounterClockWise(int speed)
	{
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);

		leftMotor.backward();
		rightMotor.forward();
	}


}
