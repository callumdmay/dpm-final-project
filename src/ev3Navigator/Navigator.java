package ev3Navigator;

import java.util.Queue;

import ev3ObjectDetector.ObjectDetector;
import ev3Objects.Motors;
import ev3Odometer.Odometer;
import ev3WallFollower.UltrasonicController;
import ev3WallFollower.UltrasonicPoller;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigator extends Thread{

	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private EV3LargeRegulatedMotor neckMotor;

	private double wheelRadius;
	private double axleLength;

	private final double locationError = 1;
	private final double navigatingAngleError = 1;

	private final int FORWARD_SPEED = 200;
	private final int ROTATE_SPEED = 100;
	private final int SMALL_CORRECTION_SPEED =20;

	private Odometer odometer;
	private ObjectDetector objectDetector;


	private boolean isCheckingForObstacles;

	public static int coordinateCount = 0;
	private static Queue<Coordinate> coordinates;



	public Navigator(Odometer pOdometer, UltrasonicPoller pUltraSonicPoller, UltrasonicController pwallFollowerController, Motors pMotors, ObjectDetector pObjectDetector)
	{
		odometer 					= pOdometer;
		leftMotor 					= pMotors.getRightMotor();
		rightMotor 					= pMotors.getRightMotor();
		neckMotor 					= pMotors.getNeckMotor();
		wheelRadius 				= pMotors.getWheelRadius();
		axleLength 					= pMotors.getAxleLength();
		objectDetector 				= pObjectDetector;

		isCheckingForObstacles = true;


	}

	public Navigator(Odometer pOdometer, Motors pMotors)
	{
		odometer 					= pOdometer;
		leftMotor 					= pMotors.getRightMotor();
		rightMotor 					= pMotors.getRightMotor();
		neckMotor 					= pMotors.getNeckMotor();
		wheelRadius 				= pMotors.getWheelRadius();
		axleLength 					= pMotors.getAxleLength();
		neckMotor 					= null;

		isCheckingForObstacles = false;
	}

	@Override
	public void run()
	{
		resetMotors();

		//For each coordinate in the queue, 
		for( Coordinate coordinate : coordinates)
			travelTo(coordinate.getX(), coordinate.getY());


		resetMotors();
	}

	//This method takes a new x and y location, and moves to it while avoiding obstacles
	public void travelTo(double pX, double pY)
	{

		//While the robot is not at the objective coordinates, keep moving towards it 
		while(Math.abs(pX- odometer.getX()) > locationError || Math.abs(pY - odometer.getY()) > locationError)
		{
			if(isCheckingForObstacles)
				objectDetector.checkForObjects(pX, pY);

			navigateToCoordinates(pX, pY);

		}

	}

	//Turns to the absolute value theta
	public void turnTo(double pTheta)
	{

		pTheta = pTheta % Math.toRadians(360);

		double deltaTheta = pTheta - odometer.getTheta();

		double rotationAngle = 0;

		if( Math.abs(deltaTheta) <= Math.PI)
			rotationAngle = deltaTheta;

		if(deltaTheta < -Math.PI)
			rotationAngle = deltaTheta + 2*Math.PI;

		if(deltaTheta > 2*Math.PI)
			rotationAngle = deltaTheta - 2*Math.PI;

		//Basic proportional control on turning speed when
		//making a small angle correction
		if(Math.abs(deltaTheta)<= Math.toRadians(10))
		{
			leftMotor.setSpeed(SMALL_CORRECTION_SPEED);
			rightMotor.setSpeed(SMALL_CORRECTION_SPEED);
		}
		else
		{
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);
		}

		leftMotor.rotate(-NavigatorUtility.convertAngle(wheelRadius, axleLength, rotationAngle * 180/Math.PI), true);
		rightMotor.rotate(NavigatorUtility.convertAngle(wheelRadius, axleLength, rotationAngle * 180/Math.PI), false);
	}

	public void turnTo(double pTheta, int speed)
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


		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);

		leftMotor.rotate(-NavigatorUtility.convertAngle(wheelRadius, axleLength, rotationAngle * 180/Math.PI), true);
		rightMotor.rotate(NavigatorUtility.convertAngle(wheelRadius, axleLength, rotationAngle * 180/Math.PI), false);
	}


	/*
	 * This method simply navigates to the given coordinates
	 */
	private void navigateToCoordinates(double pX, double pY)
	{
		double currentX = odometer.getX();
		double currentY = odometer.getY();

		double newAngle = NavigatorUtility.calculateNewAngle(pX - currentX, pY - currentY);


		if(Math.abs(  Math.toDegrees(NavigatorUtility.calculateShortestTurningAngle(newAngle, odometer.getTheta())))  > navigatingAngleError)
			turnTo(newAngle);
		else
		{
			//Basic proportional control, when the robot gets close to 
			//required coordinates, slow down 
			if(Math.abs(pX - currentX) <= 3 || Math.abs(pY - currentY ) <= 3)
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

	//Sets the global coordinates for the navigator
	public void setCoordinates(Queue<Coordinate> pCoordinates)
	{
		coordinates = pCoordinates;
	}

	//Stops and resets the motors
	private void resetMotors()
	{
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(2000);
		}

		if(neckMotor != null)
		{		
			neckMotor.stop();
			neckMotor.setAcceleration(2000);
			neckMotor.setSpeed(50);
		}
	}

	public void stopMotors()
	{
		leftMotor.stop();
		rightMotor.stop();
	}

	public void rotateClockWise(int speed)
	{
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);

		leftMotor.forward();
		rightMotor.backward();
	}

	public void rotateCounterClockWise(int speed)
	{
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);

		leftMotor.backward();
		rightMotor.forward();
	}


}

