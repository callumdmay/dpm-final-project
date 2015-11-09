package ev3WallFollower;

import ev3Objects.Motors;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Creates an object that will control the movement of the EV3
 * while it navigates around objects
 */
public class PController implements UltrasonicController {

	private static final int bandCenter = 25; // Offset from the wall (cm)
	private static final int bandWidth = 3; // Width of dead band (cm)
	private final int motorStraight = 100, FILTER_OUT = 20;
	private final int offset = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	private int filterControl;
	
	/**
	 * Store motors essential for navigation
	 * @param pMotors The motors with wheels attached
	 */
	public PController(Motors pMotors) {
		// Default Constructor
		this.leftMotor = pMotors.getLeftMotor();
		this.rightMotor = pMotors.getRightMotor();
		filterControl = 0;
	}
	/**
	 * Move away from the object if it too close to it, and turn the other way
	 * when facing an empty area.
	 */
	@Override
	public void processUSData(int pDistance) {


		// rudimentary filter - toss out invalid samples corresponding to null signal.
		if(pDistance > 255)
		{
			//impossible, sensor can only read to 255, must be a bad value
		}
		else if (pDistance == 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the filter value
			filterControl ++;
		} else if (pDistance == 255){
			// true 255, therefore set distance to 255
			distance = pDistance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			distance = pDistance;
		}

		//calculate error
		int distanceError = distance - bandCenter;

		
		
	

		//Correct distance to wall
		if(Math.abs(distanceError) <= bandWidth)
		{
			leftMotor.setSpeed(motorStraight);					// Initalize motor rolling forward
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();
		}
		//Too far from wall
		else if (distanceError > bandWidth)
		{
			int leftSpeed =0;
			int rightSpeed =0;
			
			if (distance > 70)
			{
				leftSpeed = 150;
				rightSpeed= 150;
				leftMotor.setSpeed(leftSpeed - 50);
				rightMotor.setSpeed(rightSpeed + 150);
				
				leftMotor.forward();
				rightMotor.forward();
			}
			else
			{
			//Offset is used to smooth out curve, so vehicle doesn't turn too sharply
			 leftSpeed = motorStraight - scaledSpeedDelta(distanceError) + (int)(1.5*offset);
			 rightSpeed = motorStraight + scaledSpeedDelta(distanceError)-offset;

			leftMotor.setSpeed(leftSpeed);		
			rightMotor.setSpeed(rightSpeed);
			//start moving
			leftMotor.forward();
			rightMotor.forward();
		}
		}
		//Too close to wall
		else if (distanceError < bandWidth*(-1))
		{
			int leftSpeed =0;
			int rightSpeed =0;
			
			if ( distanceError <=10)
			{
				leftSpeed = 150;
				rightSpeed = 150;
				leftMotor.setSpeed(leftSpeed + 150);		
				rightMotor.setSpeed(rightSpeed - 100);
				//start moving
				leftMotor.forward();
				rightMotor.forward();
				
			}
			
			else {
			//Change speed according to error size, the offset is used to tweak the turning radius
			 leftSpeed = motorStraight + scaledSpeedDelta(distanceError) +2*offset;
			rightSpeed = motorStraight - scaledSpeedDelta(distanceError) - offset;
				
			leftMotor.setSpeed(leftSpeed);		
			rightMotor.setSpeed(rightSpeed);
			//start moving
			leftMotor.forward();
			rightMotor.forward();
			}
		}


	}

	public int scaledSpeedDelta(int error)
	{
		//Don't know what will happen at very large values, this if statement caps the max and min speeds
		if(error> 20||error<-20)
			error =20;

		//Simple linear error scaling, each cm off corresponds to a 5 degree increase/decrease on each wheel
		return Math.abs(error *5);
	}

	/**
	 * Get the distance recorded by the ultrasonic sensor.
	 */
	@Override
	public int readUSDistance() {
		return this.distance;
	}
	
	

}
