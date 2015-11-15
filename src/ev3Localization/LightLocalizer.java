package ev3Localization;

import ev3Navigator.Navigator;
import ev3Odometer.Odometer;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

/**
 * Creates an object to localize the EV3 using the light sensor.
 */
public class LightLocalizer {

	public static int ROTATION_SPEED = 150;
	private final int lineDetectionValue = 45;
	private final double light_SensorDistanceFromOrigin = 14.1;

	private Odometer odometer;
	private SampleProvider colorSensor;
	private float[] colorData;
	private double[] calibrationCoordinates;
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
	}

	/**
	 * Go navigate to unoccupied corner closest to destination
	 */
	public void localizeDynamically() {

		double blackLineAngles[] = new double[4];

		navigator.travelTo(calibrationCoordinates[0], calibrationCoordinates[1]);

		for (int index = 0; index < blackLineAngles.length; index++) {
			// Capture the angle when we first encounter the black line
			while (!blackLineDetected())
				navigator.navigatorMotorCommands.rotateClockWise(ROTATION_SPEED);

			Sound.beep();
			blackLineAngles[index] = odometer.getTheta();

		}
		
		navigator.navigatorMotorCommands.stopMotors();
		Sound.beepSequence();
		
		// takes care of 360 wraparound
		if (blackLineAngles[0] < Math.PI) {
			blackLineAngles[0] += Math.PI*2;
		}

		odometer.setX(fixDisplacement(blackLineAngles[1], blackLineAngles[3]));
		odometer.setY(fixDisplacement(blackLineAngles[0], blackLineAngles[2]));
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
	 * Set the coordinates for the robot to head to for dynamic light
	 * localization
	 * 
	 * @param position
	 *            The intersection coordinates used for light localization
	 */
	public void setCalibrationCoordinates(double[] intersectionCoordinates) {
		this.calibrationCoordinates = intersectionCoordinates;
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
	public double fixAngle(double angleA, double angleB, double currentAngle) {
		double deltaTheta;
		deltaTheta = Math.PI - (angleA - angleB) / 2 - angleB;
		currentAngle += deltaTheta;
		return currentAngle;
	}

	/**
	 * Returns the corrected displacement of the EV3
	 * 
	 * @param angleA
	 * @param angleB
	 * @return
	 */
	public double fixDisplacement(double angleA, double angleB) {
		double z;
		z = -1 * light_SensorDistanceFromOrigin * Math.cos((angleA - angleB) / 2);
		return z;
	}

}
