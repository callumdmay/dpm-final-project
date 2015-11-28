package ev3ObjectDetector;

import ev3Navigator.NavigatorUtility;
import ev3Objects.Motors;
import ev3Odometer.Odometer;
import ev3WallFollower.UltrasonicController;
import ev3WallFollower.UltrasonicPoller;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 *  Creates an object to allow the EV3 to avoid objects
 */
public class ObstacleAvoider {

	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private EV3LargeRegulatedMotor leftUltraSoundMotor;

	private Odometer odometer;
	private UltrasonicPoller ultraSonicPoller;
	private UltrasonicController wallFollowerController;

	private double wheelRadius;
	private double axleLength;

	private final int neckMotor_OFFSET = -50;
	private final double wallFollowingAngleError = 4 ;

	/**
	 * Stores objects essential to allow the EV3 to avoid objects
	 * @param pOdometer The Odometer to be used.
	 * @param pUltraSonicPoller The Ultrasonic sensor to be used.
	 * @param pwallFollowerController The Wall Follower controller to be used.
	 * @param pMotors The motors to be used.
	 */
	public ObstacleAvoider(Odometer pOdometer, UltrasonicPoller pUltraSonicPoller, UltrasonicController pwallFollowerController, Motors pMotors)
	{
		ultraSonicPoller 			= pUltraSonicPoller;
		wallFollowerController 		= pwallFollowerController;
		odometer 					= pOdometer;
		leftMotor 					= pMotors.getLeftMotor();
		rightMotor 					= pMotors.getRightMotor();
		leftUltraSoundMotor			= pMotors.getLeftUltraSoundMotor();
		wheelRadius 				= pMotors.getWheelRadius();
		axleLength 					= pMotors.getAxleLength();
		
		leftUltraSoundMotor.setSpeed(50);
		leftUltraSoundMotor.setAcceleration(1000);
	}


	/**
	 * This method runs the p-type wall-following algorithm
	 * until the robot is facing back at towards coordinates.
	 * It then tries to move towards them again
	 */
	public void avoidObstacle(double pX, double pY) {


		rightMotor.stop();
		leftMotor.stop();

		leftUltraSoundMotor.rotate(neckMotor_OFFSET, true);
		
		leftMotor.setSpeed(100);
		rightMotor.setSpeed(100);
		leftMotor.rotate(NavigatorUtility.convertAngle(wheelRadius, axleLength, 80), true);
		rightMotor.rotate(-NavigatorUtility.convertAngle(wheelRadius, axleLength, 80), false);

		double currentX;
		double currentY;
		
		double abortAngle = odometer.getTheta() + Math.toRadians(160);
		
		if(abortAngle > Math.toRadians(360))
			abortAngle-= Math.toRadians(360);

		do{
			currentX = odometer.getX();
			currentY = odometer.getY();
			wallFollowerController.processUSData(ultraSonicPoller.getLeftUltraSoundSensorDistance() , ultraSonicPoller.getRightUltraSoundSensorDistance());
			
			if(Math.abs(odometer.getTheta() - abortAngle) < Math.toRadians(wallFollowingAngleError))
				break;
			
		} while(Math.abs(NavigatorUtility.calculateAngleError(pX - currentX, pY - currentY, odometer.getTheta())*180/Math.PI) > wallFollowingAngleError);
		
		leftUltraSoundMotor.rotate(-neckMotor_OFFSET, false);


	}


}
