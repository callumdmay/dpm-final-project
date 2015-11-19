package ev3Localization;

import ev3Navigator.Navigator;
import ev3Objects.Coordinate;
import ev3Objects.ObstacleOnCoordinateException;
import ev3Odometer.Odometer;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

/**
 * Creates an object to localize the EV3 using the light sensor.
 */
public class LightLocalizer {

	public static int ROTATION_SPEED = 175;
	private final int lineDetectionValue = 42;
	private final double light_SensorDistanceFromOrigin = 14.1;
	private double tileLength;

	private Odometer odometer;
	private SampleProvider colorSensor;
	private float[] colorData;
	private Navigator navigator;

	/**
	 * Store an Odometer, a Navigator, a SampleProvider, and an array of floats
	 * to be used by the LightLocalizer
	 * 
	 * @param odo
	 *            The Odometer to be used
	 * @param navigator
	 *            The Navigator to be used
	 * @param colorSensor
	 *            The SampleProvider to be used
	 * @param colorData
	 *            The data array to be used.
	 */
	public LightLocalizer(Odometer odo, Navigator navigator, SampleProvider colorSensor, float[] colorData) {
		this.odometer = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.navigator = navigator;

		tileLength = navigator.tileLength;

		navigator.setLightLocalizer(this);
	}

	/**
	 * Go navigate to unoccupied corner closest to destination
	 */
	public void localizeDynamically() {

		odometer.setDistanceTravelled(0);

		Coordinate calibrationCoordinates[] = findOptimalLocalizationCoordinates();

		double blackLineAngles[] = new double[4];

		Coordinate calibrationCoordinate = null;
		
		for(Coordinate coordinate : calibrationCoordinates)
		{
			try{
				navigator.localizationTravelTo(coordinate.getX()-4, coordinate.getY()-4);
			}
			catch (ObstacleOnCoordinateException e){
				continue;
			}

			calibrationCoordinate = coordinate;
			break;
		}
		
		for (int index = 0; index < blackLineAngles.length; index++) {
			// Capture the angle when we first encounter the black line
			while (!blackLineDetected())
				navigator.navigatorMotorCommands.rotateClockWise(ROTATION_SPEED);

			Sound.beep();
			blackLineAngles[index] = odometer.getTheta();

		}

		navigator.navigatorMotorCommands.stopMotors();

		odometer.setX(calibrationCoordinate.getX() -1 * light_SensorDistanceFromOrigin * Math.cos((blackLineAngles[1] - blackLineAngles[3]) / 2));
		odometer.setY(calibrationCoordinate.getY() -1 * light_SensorDistanceFromOrigin * Math.cos((blackLineAngles[0] - blackLineAngles[2]) / 2));
		odometer.setTheta(fixAngle(blackLineAngles[1], blackLineAngles[3], odometer.getTheta()));
	}

	
	public void lightLocalize(Coordinate calibrationCoordinate)
	{
		odometer.setDistanceTravelled(0);
		double blackLineAngles[] = new double[4];
		
		navigator.localizationTravelTo(calibrationCoordinate.getX()-4, calibrationCoordinate.getY()-4);
		
		for (int index = 0; index < blackLineAngles.length; index++) {
			// Capture the angle when we first encounter the black line
			while (!blackLineDetected())
				navigator.navigatorMotorCommands.rotateClockWise(ROTATION_SPEED);

			Sound.beep();
			blackLineAngles[index] = odometer.getTheta();

		}

		navigator.navigatorMotorCommands.stopMotors();

		odometer.setX(calibrationCoordinate.getX() -1 * light_SensorDistanceFromOrigin * Math.cos((blackLineAngles[1] - blackLineAngles[3]) / 2));
		odometer.setY(calibrationCoordinate.getY() -1 * light_SensorDistanceFromOrigin * Math.cos((blackLineAngles[0] - blackLineAngles[2]) / 2));
		odometer.setTheta(fixAngle(blackLineAngles[1], blackLineAngles[3], odometer.getTheta()));
	}
	
	
	
	/**
	 * Return true if a black line is detected by the color sensor.
	 * 
	 * @return A boolean representing if the light sensor detects a line or not.
	 */
	private boolean blackLineDetected() {
		colorSensor.fetchSample(colorData, 0);

		// if we run over a black line, calculate and update odometer values
		if ((int) (colorData[0] * 100) < lineDetectionValue)
			return true;
		else
			return false;
	}


	/**
	 * Returns the corrected angle of the EV3
	 * 
	 * @param angleA
	 *            The first angle used to correct x
	 * @param angleB
	 *            The second angle used to correct y
	 * @param currentAngle
	 *            The current heading of the EV3
	 * @return The corrected heading of the EV3
	 */
	private double fixAngle(double angleA, double angleB, double currentAngle) {
		double deltaTheta;
		deltaTheta = Math.PI - (angleA - angleB) / 2 - angleB;
		currentAngle += deltaTheta;
		return currentAngle - Math.toRadians(6);
	}


	/**
	 * Returns a list of coordinates for the corners of the tile the robot is in.
	 */
	private Coordinate[] findCorners(){
		double bottomLeftX = odometer.getX() - odometer.getX() % tileLength;
		double bottomLeftY = odometer.getY() - odometer.getY() % tileLength;
		Coordinate bottomLeft = new Coordinate(bottomLeftX, bottomLeftY);
		Coordinate bottomRight = new Coordinate((bottomLeft.getX() + tileLength), bottomLeft.getY());
		Coordinate topLeft = new Coordinate(bottomLeft.getX(), (bottomLeft.getY() + tileLength));
		Coordinate topRight = new Coordinate((bottomLeft.getX() + tileLength), (bottomLeft.getY() + tileLength));
		Coordinate[] corners = {bottomLeft, bottomRight, topLeft, topRight};
		return corners;
	}

	/**
	 * Returns the coordinate of the corner closest to destination coordinate
	 * @param corners The array of corners to choose from
	 * @param destination The destination
	 * @return
	 */
	private Coordinate[] findOptimalLocalizationCoordinates(){
		Coordinate[] corners = findCorners();

		if(odometer.getTheta()<=Math.toRadians(90) && odometer.getTheta()>=Math.toRadians(0))
		{
			return new Coordinate[] {corners[3], corners[1], corners[2], corners[0]};
		}
		else if (odometer.getTheta()>Math.toRadians(90) && odometer.getTheta()<=Math.toRadians(180))
		{
			return new Coordinate[] {corners[2], corners[3], corners[0], corners[1]};
		}
		else if (odometer.getTheta()>Math.toRadians(180) && odometer.getTheta()<=Math.toRadians(270))
		{
			return new Coordinate[] {corners[0], corners[2], corners[1], corners[3]};
		}
		else if (odometer.getTheta()>Math.toRadians(270) && odometer.getTheta()<=Math.toRadians(360))
		{
			return new Coordinate[] {corners[1], corners[0], corners[3], corners[2]};
		}
		
		else{
			throw new NullPointerException("Could not determine optimal localization coordinates");
		}
	}


}
