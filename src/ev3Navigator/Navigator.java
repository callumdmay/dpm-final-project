package ev3Navigator;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ev3Localization.LightLocalizer;
import ev3ObjectDetector.ObjectDetector;
import ev3ObjectDetector.ObstacleAvoider;
import ev3Objects.CaptureTheFlagGameObject;
import ev3Objects.ColourSensorPoller;
import ev3Objects.Coordinate;
import ev3Objects.FoundOpponentFlagException;
import ev3Objects.MissedObjectException;
import ev3Objects.Motors;
import ev3Objects.ObstacleOnCoordinateException;
import ev3Odometer.Odometer;
import ev3WallFollower.UltrasonicPoller;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Creates an object used to Navigate through the environment
 */
public class Navigator extends Thread{

	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private EV3LargeRegulatedMotor blockLiftMotor;
	private ObjectDetector objectDetector;
	private ObstacleAvoider obstacleAvoider;
	private Odometer odometer;
	private ColourSensorPoller colourSensorPoller;
	private CaptureTheFlagGameObject captureTheFlagGameObject;
	public NavigatorMotorCommands navigatorMotorCommands;
	
	private LightLocalizer lightLocalizer;

	private double wheelRadius;
	private double axleLength;
	
	
	private final double locationError = 1;
	private final double navigatingAngleError = 2;
	private final int searchOffSet = -20;
	public static final double  tileLength = 30.48;
	private final int investigateObjectDistance = 8;

	private final int FORWARD_SPEED = 300;
	private final int ROTATE_SPEED = 100;
	private final int SMALL_CORRECTION_SPEED =100;
	private final int SMALL_ROTATION_SPEED = 50;
	public final static double CORRECTION_DIST = 135;
	private final static int clawMotorAngleOffset = 100;
	


	public static int coordinateCount = 0;


	/**
	 * Store useful objects to be used by the Navigator
	 * @param pOdometer The Odometer to be used by the navigator
	 * @param pObjectDetector The ObjectDetector to be used by the navigator
	 * @param pObstacleAvoider The Obstacle avoider to be used by the navigator
	 * @param pMotors The Motors to be used by the navigator
	 */
	public Navigator(Odometer pOdometer, ObjectDetector pObjectDetector, ObstacleAvoider pObstacleAvoider, Motors pMotors, ColourSensorPoller pColourSensorPoller)
	{
		odometer 					= pOdometer;
		objectDetector 				= pObjectDetector;
		obstacleAvoider				= pObstacleAvoider;
		colourSensorPoller			= pColourSensorPoller;
		leftMotor 					= pMotors.getLeftMotor();
		rightMotor 					= pMotors.getRightMotor();
		blockLiftMotor					= pMotors.getBlockLiftMotor();
		wheelRadius 				= pMotors.getWheelRadius();
		axleLength 					= pMotors.getAxleLength();

		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(2000);

		}

		navigatorMotorCommands = new NavigatorMotorCommands(leftMotor, rightMotor);

	}

	/**
	 * Run the game
	 */
	@Override
	public void run()
	{
		Sound.beepSequenceUp();

		odometer.setX(captureTheFlagGameObject.getStartingCoordinate().getX());
		odometer.setY(captureTheFlagGameObject.getStartingCoordinate().getY());
		odometer.setTheta(captureTheFlagGameObject.getStartingAngle());

		for(Coordinate coordinate : captureTheFlagGameObject.getPreSearchLocalizationCoordinates())
		{
			try{
				travelTo(coordinate.getX(), coordinate.getY());
			}
			catch (ObstacleOnCoordinateException e){
				continue;
			}
			// Don't localize if you're on a grid gap
			if (!((coordinate.getX()+ tileLength)% (tileLength*4) == 0 || (coordinate.getY() + tileLength) % (tileLength*4) == 0 )){
				lightLocalizer.lightLocalize(coordinate);
			}
			break;
		}

		Sound.beepSequenceUp();


		try{
			searchForFlag(captureTheFlagGameObject.getClosestOpponentBaseCoordinate(), captureTheFlagGameObject.getFurthestOpponentBaseCoordinate());
		}
		catch(FoundOpponentFlagException e)
		{
			Sound.beepSequenceUp();
			pickUpFlag();
			lightLocalizer.localizeDynamically();
			travelTo(captureTheFlagGameObject.getHomeFlagDropCoordinate().getX(), captureTheFlagGameObject.getHomeFlagDropCoordinate().getY());
		}

		navigatorMotorCommands.stopMotors();
		dropFlag();
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
			moveToCoordinates(pX, pY);
			if(objectDetector.detectedObject())
			{
				determineIfObjectIsOnDestinationCoordinate(pX, pY);
				if(objectIsInTheWay(pX, pY))
				{
					obstacleAvoider.avoidObstacle(pX, pY);
					if(!objectDetector.getFlagBlock())
						lightLocalizer.localizeDynamically();
				}

			}
			if (odometer.getDistanceTravelled() > CORRECTION_DIST && !objectDetector.getFlagBlock()){
				lightLocalizer.localizeDynamically();
			}
		}
		navigatorMotorCommands.stopMotors();
	}

	/**
	 * Travel to a coordinate without dynamic localization, but checking for obstacles
	 * @param pX The x coordinate to travel to
	 * @param pY The y coordinate to travel to
	 */
	public void localizationTravelTo(double pX, double pY)
	{
		//While the robot is not at the objective coordinates, keep moving towards it 
		while(Math.abs(pX- odometer.getX()) > locationError || Math.abs(pY - odometer.getY()) > locationError)
		{
			if(objectIsInTheWay(pX, pY))
				throw new ObstacleOnCoordinateException();

			moveToCoordinates(pX, pY);
		}
		navigatorMotorCommands.stopMotors();
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
			if(Math.abs(pX - currentX) <= 5 && Math.abs(pY - currentY ) <= 5)
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
	 *  Method that determines if there is an object where the robot is trying to go. If so, 
	 *  throws an exception that must be handled by calling method
	 * @param pX The target location x coordinate
	 * @param pY The target location y coordinate
	 */

	private void determineIfObjectIsOnDestinationCoordinate(double pX, double pY){
		double objectX = odometer.getX() + Math.cos(odometer.getTheta()) * objectDetector.getObjectDistance();
		double objectY = odometer.getY() + Math.sin(odometer.getTheta()) * objectDetector.getObjectDistance();
		if(Math.abs(objectX-pX)<13 &&Math.abs(objectY-pY)<13)
			throw new ObstacleOnCoordinateException();

	}

	/**
	 * Determines if the detected object is between the robot and the 
	 * coordinate it is trying to travel to
	 * @param pX The target location x coordinate
	 * @param pY The target location y coordinate
	 * @return
	 */

	private boolean objectIsInTheWay(double pX, double pY) {
		double deltaX = pX - odometer.getX();
		double deltaY = pY - odometer.getY();
		double distanceToCoordinate = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

		if(distanceToCoordinate > objectDetector.getObjectDistance())
			return true;
		else
			return false;
	}

	/**
	 * This method searches a given area of tile width 2 and unlimited tile length, to find the flag
	 * @param startPoint search area start point
	 * @param endPoint search area endpoint
	 */
	public void searchForFlag(Coordinate startPoint, Coordinate endPoint)
	{
		colourSensorPoller.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Queue<Coordinate> searchCoordinateQueue = NavigatorUtility.generateSimpleSearchCoordinateQueue(startPoint, endPoint);

		while(true){
			for(Coordinate coordinate : searchCoordinateQueue){
				while(Math.abs(coordinate.getX()- odometer.getX()) > locationError || Math.abs(coordinate.getY() - odometer.getY()) > locationError)
				{
					moveToCoordinates(coordinate.getX(), coordinate.getY());
					if(objectDetector.detectedObject(investigateObjectDistance))
					{
						Sound.beep();
						navigatorMotorCommands.stopMotors();
						try{
						investigateObject();
						disposeFlag();
						}
						catch(MissedObjectException e)
						{
						
						}
					}
				}

			}
			lightLocalizer.localizeDynamically();
		}
	}

	/**
	 * Sequence to pick up the flag
	 */
	private void pickUpFlag()
	{

		navigatorMotorCommands.setSpeed(100);
		leftMotor.rotate(NavigatorUtility.convertDistance(wheelRadius, -10), true);
		rightMotor.rotate(NavigatorUtility.convertDistance(wheelRadius, -10),false);

		turnTo(odometer.getTheta()+ Math.toRadians(180), false);

		leftMotor.rotate(NavigatorUtility.convertDistance(wheelRadius, -11), true);
		rightMotor.rotate(NavigatorUtility.convertDistance(wheelRadius, -11),false);

		blockLiftMotor.setSpeed(30);
		blockLiftMotor.setAcceleration(100);
		blockLiftMotor.rotate(NavigatorUtility.convertAngle(wheelRadius, axleLength, -clawMotorAngleOffset), false);
	}

	/**
	 * Sequence to toss the flag
	 */
	private void disposeFlag()
	{
		pickUpFlag();
		turnTo(odometer.getTheta()+ Math.toRadians(180), false);
		blockLiftMotor.rotate(NavigatorUtility.convertAngle(wheelRadius, axleLength, clawMotorAngleOffset), false);
	}
	/**
	 * Sequence to lower the flag
	 */
	private void dropFlag()
	{
		leftMotor.rotate(NavigatorUtility.convertDistance(wheelRadius, 10), true);
		rightMotor.rotate(NavigatorUtility.convertDistance(wheelRadius, 10),false);

		blockLiftMotor.setSpeed(30);
		blockLiftMotor.setAcceleration(100);
		blockLiftMotor.rotate(NavigatorUtility.convertAngle(wheelRadius, axleLength, clawMotorAngleOffset), false);
	}


	/**
	 * Determine what the object is when in range
	 */
	private void investigateObject()
	{
		double angle1 = 0; 
		double angle2 = 0;

		if(objectDetector.getRightUSDistance() <investigateObjectDistance)
		{
			while(objectDetector.getRightUSDistance()<investigateObjectDistance + 2)
				navigatorMotorCommands.rotateClockWise(60);
		}

		if(objectDetector.getRightUSDistance() > investigateObjectDistance)
		{
			while(objectDetector.getRightUSDistance()>investigateObjectDistance )
				navigatorMotorCommands.rotateCounterClockWise(60);
			Sound.beep();
			angle1 = odometer.getTheta();
			
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			while(objectDetector.getRightUSDistance()<investigateObjectDistance -1)
				navigatorMotorCommands.rotateCounterClockWise(60);
			Sound.beep();
			angle2 = odometer.getTheta();
		}	

		turnTo(NavigatorUtility.calculateAngleAverage(angle1, angle2) + Math.toRadians(searchOffSet), true);

		odometer.setDistanceTravelled(0);
		while(objectDetector.getColorID() != 3 && objectDetector.getColorID() != 6 && objectDetector.getColorID() != 0 && objectDetector.getColorID() != 2)
		{
			navigatorMotorCommands.driveStraight(30);
			if(odometer.getDistanceTravelled()> investigateObjectDistance+3)
				throw new MissedObjectException();
		}
		objectDetector.determineIfObjectIsFlag(captureTheFlagGameObject.getOpponentFlagColour());


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
	 * Sets the light localizer to be used by the navigator
	 * @param lightLocalizer The navigator to be used
	 */
	public void setLightLocalizer(LightLocalizer LSLocalizer)
	{
		this.lightLocalizer = LSLocalizer;
	}
}

