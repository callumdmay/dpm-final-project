package ev3ObjectDetector;

import ev3Localization.LCDInfo;
import ev3Localization.LightLocalizer;
import ev3Localization.USLocalizer;
import ev3Localization.USLocalizer.LocalizationType;
import ev3Navigator.Navigator;
import ev3Objects.Motors;
import ev3Odometer.Odometer;
import ev3WallFollower.PController;
import ev3WallFollower.UltrasonicController;
import ev3WallFollower.UltrasonicPoller;
import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;

public class Lab5 {

	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	// Ultrasonic sensor port connected to input S1
	// Color sensor port connected to input S2
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final Port usPort = LocalEV3.get().getPort("S1");		
	private static final Port colorPort = LocalEV3.get().getPort("S4");		


	public static final double WHEEL_RADIUS = 2.25;
	public static final double TRACK = 16.2;


	public static void main(String[] args) {

		//Setup ultrasonic sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize operating mode
		// 4. Create a buffer for the sensor data
		@SuppressWarnings("resource")							    	// Because we don't bother to close this resource
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usValue = usSensor.getMode("Distance");			// colorValue provides samples from this instance
		float[] usData = new float[usValue.sampleSize()];				// colorData is the buffer in which data are returned

		UltrasonicPoller usPoller = new UltrasonicPoller(usValue, usData);
		//Setup color sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize operating mode
		// 4. Create a buffer for the sensor data
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("Red");			// colorValue provides samples from this instance
		float[] colorData = new float[colorValue.sampleSize()];			// colorData is the buffer in which data are returned

		// setup the odometer and display
		Odometer odometer = new Odometer(WHEEL_RADIUS, TRACK, leftMotor, rightMotor);
		odometer.start();
		LCDInfo lcd = new LCDInfo(odometer);

		//Create motors object
		Motors motors = new Motors(leftMotor, rightMotor, WHEEL_RADIUS, TRACK);
		//Create navigator
		Navigator navigator = new Navigator(odometer, motors);

		UltrasonicController pController = new PController(motors);

		// perform the ultrasonic localization
		USLocalizer usl = new USLocalizer(odometer, usValue, usData, USLocalizer.LocalizationType.RISING_EDGE, navigator);
		usl.doLocalization();

		ObstacleAvoider obstacleAvoider = new ObstacleAvoider(odometer, usPoller, pController, motors);
		ObjectDetector objectDetector = new ObjectDetector(usPoller,colorValue, colorData, odometer, obstacleAvoider);


		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	

	}

}
