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
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;

public class EV3Launcher {

	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	// Ultrasonic sensor port connected to input S1
	// Color sensor port connected to input S2
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor leftSideUltraSoundMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor rightSideUltraSoundMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor liftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
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
		
		//Create motors object
		Motors motors = new Motors(leftMotor, rightMotor, leftSideUltraSoundMotor, rightSideUltraSoundMotor, liftMotor, WHEEL_RADIUS, TRACK);

		UltrasonicController pController = new PController(motors);

		ObstacleAvoider obstacleAvoider = new ObstacleAvoider(odometer, usPoller, pController, motors);
		ObjectDetector objectDetector = new ObjectDetector(usPoller,colorValue, colorData, odometer, obstacleAvoider);

		//Create navigator
		Navigator navigator = new Navigator(odometer, objectDetector, motors);
		
		// perform the ultrasonic localization
		USLocalizer usl = new USLocalizer(odometer, usValue, usData, USLocalizer.LocalizationType.RISING_EDGE, navigator);


		int buttonChoice;
		TextLCD t = LocalEV3.get().getTextLCD();

		LCDInfo lcd;

		do {
			// clear the display
			t.clear();

			// ask the user whether the motors should drive in a square or float
			t.drawString("< Left | Right > ", 0, 0);
			t.drawString("       |         ", 0, 1);
			t.drawString("TODO   |TODO     ", 0, 2);
			t.drawString("       |         ", 0, 3);
			t.drawString("		 |         ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice == 0 );

		switch(buttonChoice) {

		case Button.ID_LEFT :
			break;

		case Button.ID_RIGHT:
			break;

		default:

			System.exit(0);
			break;

		}
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	

	}

}
