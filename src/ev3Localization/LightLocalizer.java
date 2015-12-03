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

	public static int ROTATION_SPEED = 140;
	private final int lineDetectionValue = 42;
	private final double light_SensorDistanceFromOrigin = 13.3;
	private double tileLength;
	private double tileDiagonal;

	private static final double lightLocalizationAngleOffset = 1;

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
		tileDiagonal = Math.pow(2*Math.pow(navigator.tileLength, 2),0.5);
		navigator.setLightLocalizer(this);
	}

	/**
	 * Go navigate to unoccupied corner closest to destination
	 */
	public void localizeDynamically() {

		odometer.setDistanceTravelled(Navigator.CORRECTION_DIST - tileDiagonal);

		Coordinate calibrationCoordinates[] = findOptimalLocalizationCoordinates();
		
		for (Coordinate coordinate : calibrationCoordinates) {
			// Must avoid grid gaps
			if ((coordinate.getX()+ tileLength)% (tileLength*4) == 0 || (coordinate.getY() + tileLength) % (tileLength*4) == 0 )
				continue;
			try {
				navigator.localizationTravelTo(coordinate.getX(), coordinate.getY());
			} catch (ObstacleOnCoordinateException e) {
				continue;
			}
			lightLocalize(coordinate);
			odometer.setDistanceTravelled(0);
			break;
		}

	}
	/**
	 * Travel to an intersection to localize using the light sensor
	 * @param calibrationCoordinate The coordinate to localize on
	 */
	public void lightLocalize(Coordinate calibrationCoordinate) {
		double blackLineAngles[] = new double[4];

		navigator.localizationTravelTo(calibrationCoordinate.getX(), calibrationCoordinate.getY());

		navigator.navigatorMotorCommands.setSpeed(ROTATION_SPEED);

		navigator.turnTo(Math.toRadians(45), false);

		for (int index = 0; index < blackLineAngles.length; index++) {
			// Capture the angle when we first encounter the black line
			while (!blackLineDetected())
				navigator.navigatorMotorCommands.rotateClockWise(ROTATION_SPEED);
			blackLineAngles[index] = odometer.getTheta();
			Sound.beep();

		}

		navigator.navigatorMotorCommands.stopMotors();

		// takes care of 360 wraparound
		if (blackLineAngles[0] < Math.PI) {
			blackLineAngles[0] += Math.PI * 2;
		}

		odometer.setDistanceTravelled(0);

		odometer.setX(calibrationCoordinate.getX() + fixDisplacement(blackLineAngles[1], blackLineAngles[3]));
		odometer.setY(calibrationCoordinate.getY() + fixDisplacement(blackLineAngles[0], blackLineAngles[2]));
		odometer.setTheta(fixAngle(blackLineAngles[1], blackLineAngles[3], odometer.getTheta()) + Math.toRadians(lightLocalizationAngleOffset) );

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
		return currentAngle;
	}

	/**
	 * Returns a list of coordinates for the corners of the tile the robot is
	 * in.
	 */
	private Coordinate[] findCorners() {
		double bottomLeftX = odometer.getX() - odometer.getX() % tileLength;
		double bottomLeftY = odometer.getY() - odometer.getY() % tileLength;
		Coordinate bottomLeft = new Coordinate(bottomLeftX, bottomLeftY);
		Coordinate bottomRight = new Coordinate((bottomLeft.getX() + tileLength), bottomLeft.getY());
		Coordinate topLeft = new Coordinate(bottomLeft.getX(), (bottomLeft.getY() + tileLength));
		Coordinate topRight = new Coordinate((bottomLeft.getX() + tileLength), (bottomLeft.getY() + tileLength));
		Coordinate[] corners = { bottomLeft, bottomRight, topLeft, topRight };
		return corners;
	}

	/**
	 * Returns the coordinate of the corner closest to destination coordinate
	 * 
	 * @param corners
	 *            The array of corners to choose from
	 * @param destination
	 *            The destination
	 * @return
	 */
	private Coordinate[] findOptimalLocalizationCoordinates() {
		Coordinate[] corners = findCorners();

		if (odometer.getTheta() <= Math.toRadians(90) && odometer.getTheta() >= Math.toRadians(0)) {
			return new Coordinate[] { corners[3], corners[1], corners[2], corners[0] };
		} else if (odometer.getTheta() > Math.toRadians(90) && odometer.getTheta() <= Math.toRadians(180)) {
			return new Coordinate[] { corners[2], corners[3], corners[0], corners[1] };
		} else if (odometer.getTheta() > Math.toRadians(180) && odometer.getTheta() <= Math.toRadians(270)) {
			return new Coordinate[] { corners[0], corners[2], corners[1], corners[3] };
		} else if (odometer.getTheta() > Math.toRadians(270) && odometer.getTheta() <= Math.toRadians(360)) {
			return new Coordinate[] { corners[1], corners[0], corners[3], corners[2] };
		}

		else {
			throw new NullPointerException("Could not determine optimal localization coordinates");
		}
	}

	/**
	 * Returns the angle correction for light localization
	 * @param angleA The first angle required for this calculation
	 * @param angleB The second angle required for this calculation
	 * @return The angle correction offset
	 */
	public double fixDisplacement(double angleA, double angleB) {
		return -1 * light_SensorDistanceFromOrigin * Math.cos((angleA - angleB) / 2);
	}

}
