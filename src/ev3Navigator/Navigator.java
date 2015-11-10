package ev3Navigator;

import java.util.ArrayList;
import java.util.Queue;

import ev3ObjectDetector.ObjectDetector;
import ev3ObjectDetector.ObstacleAvoider;
import ev3Objects.Motors;
import ev3Odometer.Odometer;
import ev3WallFollower.UltrasonicController;
import ev3WallFollower.UltrasonicPoller;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Creates an object used to Navigate through the environment
 */
public class Navigator extends Thread{

	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private ObjectDetector objectDetector;
	private ObstacleAvoider obstacleAvoider;
	private Odometer odometer;
	private CaptureTheFlagGameObject captureTheFlagGameObject;

	private double wheelRadius;
	private double axleLength;

	private final double locationError = 1;
	private final double navigatingAngleError = 1;
	private static final double  tileLength = 30.48;

	private final int FORWARD_SPEED = 200;
	private final int ROTATE_SPEED = 100;
	private final int SMALL_CORRECTION_SPEED =20;
	private final int SMALL_ROTATION_SPEED = 25;


	public static int coordinateCount = 0;
	private static ArrayList<Coordinate> coordinates;


	/**
	 * Store useful objects to be used by the Navigator
	 * @param pOdometer The Odometer to be used by the navigator
	 * @param pObjectDetector The ObjectDetector to be used by the navigator
	 * @param pObstacleAvoider The Obstacle avoider to be used by the navigator
	 * @param pMotors The Motors to be used by the navigator
	 */
	public Navigator(Odometer pOdometer, ObjectDetector pObjectDetector, ObstacleAvoider pObstacleAvoider, Motors pMotors)
	{
		odometer 					= pOdometer;
		objectDetector 				= pObjectDetector;
		obstacleAvoider				= pObstacleAvoider;
		leftMotor 					= pMotors.getLeftMotor();
		rightMotor 					= pMotors.getRightMotor();
		wheelRadius 				= pMotors.getWheelRadius();
		axleLength 					= pMotors.getAxleLength();

		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(2000);

		}

	}

	@Override
	public void run()
	{
		Sound.beepSequenceUp();
		switch(captureTheFlagGameObject.getStartingCorner()){

		case 1:

		case 2:
			odometer.setX(10*tileLength);
		case 3:
			odometer.setX(10*tileLength);
			odometer.setY(10*tileLength);
		case 4:
			odometer.setY(10*tileLength);


			travelTo(captureTheFlagGameObject.getOpponentBaseCoordinate1().getX(), captureTheFlagGameObject.getOpponentBaseCoordinate1().getY());

		}

		stopMotors();
	}
	/**
	 * Travel to a coordinate while avoiding objects
	 * @param pX The x coordinate to travel to
	 * @param pY The y coordinate to travel to
	 */
	public void travelTo(double pX, double pY)
	{

		//While the robot is not at the objective coordinates, keep moving towards it 
		while(Math.abs(pX- odometer.getX()) > locationError || Math.abs(pY - odometer.getY()) > locationError)
		{
			if(objectDetector.detectedObject())
			{
				obstacleAvoider.avoidObstacle(pX, pY);
			}
			moveToCoordinates(pX, pY);

		}

	}
	/**
	 * Turns to absolute value theta
	 * @param pTheta The angle to turn to
	 * @param useSmallRotationSpeed Whether to turn slowly or not
	 */
	public void turnTo(double pTheta, boolean useSmallRotationSpeed)
	{

		pTheta = pTheta % Math.toRadians(360);

		double deltaTheta = pTheta - odometer.getTheta();

		double rotationAngle = 0;

		if( Math.abs(deltaTheta) <= Math.PI)
			rotationAngle = deltaTheta;

		if(deltaTheta < -Math.PI)
			rotationAngle = deltaTheta + 2*Math.PI;

		if(deltaTheta > Math.PI)
			rotationAngle = deltaTheta - 2*Math.PI;

		//Basic proportional control on turning speed when
		//making a small angle correction
		if(Math.abs(deltaTheta)<= Math.toRadians(10) || useSmallRotationSpeed)
		{
			leftMotor.setSpeed(SMALL_ROTATION_SPEED);
			rightMotor.setSpeed(SMALL_ROTATION_SPEED);
		}
		else
		{
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);
		}

		leftMotor.rotate(-NavigatorUtility.convertAngle(wheelRadius, axleLength, rotationAngle * 180/Math.PI), true);
		rightMotor.rotate(NavigatorUtility.convertAngle(wheelRadius, axleLength, rotationAngle * 180/Math.PI), false);
	}


	/**
	 * Navigates to given coordinates
	 * @param pX The x coordinate to travel to
	 * @param pY The y coordinate to travel to
	 */
	private void moveToCoordinates(double pX, double pY)
	{
		double currentX = odometer.getX();
		double currentY = odometer.getY();

		double newAngle = NavigatorUtility.calculateNewAngle(pX - currentX, pY - currentY);


		if(Math.abs(  Math.toDegrees(NavigatorUtility.calculateShortestTurningAngle(newAngle, odometer.getTheta())))  > navigatingAngleError)
			turnTo(newAngle, false);
		else
		{
			//Basic proportional control, when the robot gets close to 
			//required coordinates, slow down 
			if(Math.abs(pX - currentX) <= 3 && Math.abs(pY - currentY ) <= 3)
			{
				leftMotor.setSpeed(SMALL_CORRECTION_SPEED);
				rightMotor.setSpeed(SMALL_CORRECTION_SPEED);
			}
			else
			{
				leftMotor.setSpeed(FORWARD_SPEED);
				rightMotor.setSpeed(FORWARD_SPEED);
			}
			leftMotor.forward();
			rightMotor.forward();
		}
	}
	/**
	 * Set the global coordinates for the navigator and return the coordinates Queue
	 * @param Coordinates to be set
	 * @return The coordinatesQueue
	 */
	private static ArrayList<Coordinate> createCoordinatesList( double coordinates[][])
	{
		ArrayList<Coordinate> coordinatesQueue = new ArrayList<Coordinate>();

		for (int x = 0 ; x < coordinates.length; x++)
			coordinatesQueue.add(new Coordinate(coordinates[x][0]*tileLength,coordinates[x][1]*tileLength));

		return coordinatesQueue;
	}
	/**
	 * Set the coordinates of the navigator
	 * @param pCoordinates The coordinates to be set
	 */
	public void setCoordinates(double pCoordinates[][])
	{
		coordinates = createCoordinatesList(pCoordinates);
	}
	/**
	 * Sets the game object for the capture the flag game 
	 * @param pCaptureTheFlagGameObject
	 */

	public void setGameObject(CaptureTheFlagGameObject pCaptureTheFlagGameObject)
	{
		captureTheFlagGameObject = pCaptureTheFlagGameObject;
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

