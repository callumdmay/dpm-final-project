package ev3Launcher;

import java.io.IOException;

import ev3Localization.LightLocalizer;
import ev3Localization.USLocalizer;
import ev3Navigator.Navigator;
import ev3ObjectDetector.ObjectDetector;
import ev3ObjectDetector.ObstacleAvoider;
import ev3Objects.CaptureTheFlagGameObject;
import ev3Objects.Coordinate;
import ev3Objects.FoundOpponentFlagException;
import ev3Objects.Motors;
import ev3Odometer.LCDInfo;
import ev3Odometer.Odometer;
import ev3WallFollower.PController;
import ev3WallFollower.UltrasonicController;
import ev3WallFollower.UltrasonicPoller;
import ev3Wifi.Transmission;
import ev3Wifi.WifiConnection;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import ev3Wifi.Transmission;
import ev3Wifi.StartCorner;
import ev3Wifi.ParseTransmission;
import ev3Wifi.WifiConnection;
import ev3Objects.ColourSensorPoller;

/**
 * A class to launch the EV3 program (main).
 */
public class EV3Launcher {

	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	// Ultrasonic sensor port connected to input S1
	// Color sensor port connected to input S2

	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor leftSideUltraSoundMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final EV3LargeRegulatedMotor liftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	private static final Port rearColorPort = LocalEV3.get().getPort("S1");
	private static final Port forwardColorPort = LocalEV3.get().getPort("S3");
	private static final Port rightUltraSonicPort = LocalEV3.get().getPort("S2");
	private static final Port leftUltraSonicPort = LocalEV3.get().getPort("S4");

	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 12;
	private static final String SERVER_IP = "192.168.10.200";
	private static final int TEAM_NUMBER = 12;

	private static final int betaWifiInputString[] = { 4, 0, 0, 0, 0, 5, 5, 7, 8, 1, 1, 0, 5 };
	

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		// Setup ultrasonic sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize
		// operating mode
		// 4. Create a buffer for the sensor data
		SensorModes leftUltraSonicSensor = new EV3UltrasonicSensor(leftUltraSonicPort);
		SampleProvider leftUltraSonicSampleProvider = leftUltraSonicSensor.getMode("Distance"); // colorValue provides samples from this instance
		float[] leftUltraSonicData = new float[leftUltraSonicSampleProvider.sampleSize()]; // colorData is the buffer in which data are returned

		SensorModes rightUltraSonicSensor = new EV3UltrasonicSensor(rightUltraSonicPort);
		SampleProvider rightUltraSonicSampleProvider = rightUltraSonicSensor.getMode("Distance"); // colorValue provides samples from this instance
		float[] rightUltraSonicData = new float[rightUltraSonicSampleProvider.sampleSize()]; // colorData is the buffer in which data are returned

		// Setup color sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize
		// operating mode
		// 4. Create a buffer for the sensor data
		SensorModes rearColorSensor = new EV3ColorSensor(rearColorPort);
		SampleProvider rearColorSensorSampleProvider = rearColorSensor.getMode("Red"); // colorValue provides samples from this instance
		float[] rearColorSensorData = new float[rearColorSensorSampleProvider.sampleSize()];  // colorData is the buffer in which data are returned
		
		EV3ColorSensor forwardColorSensor = new EV3ColorSensor(forwardColorPort);
		SampleProvider forwardColorSensorSampleProvider = forwardColorSensor.getColorIDMode(); // colorValue provides samples from this instance
		float[] forwardColorSensorData = new float[forwardColorSensorSampleProvider.sampleSize()]; // colorData is the buffer in which data are returned

		// Create motors object
		Motors motors = new Motors(leftMotor, rightMotor, leftSideUltraSoundMotor, liftMotor, WHEEL_RADIUS, TRACK);

		// object that provides ultrasonic distances from sensors
		UltrasonicPoller ultraSonicSampleProvider = new UltrasonicPoller(leftUltraSonicSampleProvider,
				leftUltraSonicData, rightUltraSonicSampleProvider, rightUltraSonicData);

		// setup the odometer and display
		Odometer odometer = new Odometer(motors);
		odometer.start();

		UltrasonicController pController = new PController(motors);
		ColourSensorPoller colourSensorPoller =  new ColourSensorPoller(forwardColorSensorSampleProvider, forwardColorSensorData);
		
		
		ObstacleAvoider obstacleAvoider = new ObstacleAvoider(odometer, ultraSonicSampleProvider, pController, motors);
		ObjectDetector objectDetector = new ObjectDetector(ultraSonicSampleProvider, odometer, colourSensorPoller);

		// Create navigator
		Navigator navigator = new Navigator(odometer, objectDetector, obstacleAvoider, motors, colourSensorPoller);

		// create the ultrasonic localizers
		USLocalizer usl = new USLocalizer(odometer, rightUltraSonicSampleProvider, rightUltraSonicData, USLocalizer.LocalizationType.RISING_EDGE, navigator);
		LightLocalizer lightLocalizer = new LightLocalizer(odometer, navigator, rearColorSensorSampleProvider, rearColorSensorData);

		int buttonChoice;
		TextLCD textLCD = LocalEV3.get().getTextLCD();
				
		LCDInfo lcd;

		do {
			// clear the display
			textLCD.clear();

			// ask the user whether the motors should drive in a square or float
			textLCD.drawString("< Left | Right > ", 0, 0);
			textLCD.drawString("       |         ", 0, 1);
			textLCD.drawString("Capture|Run      ", 0, 2);
			textLCD.drawString("the    |a Test   ", 0, 3);
			textLCD.drawString("flag   |         ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice == 0);

		switch (buttonChoice) {

		// Capture the Flag case
		case Button.ID_LEFT:
			
			int[] wifiInputString = new int[13];
			wifiInputString = getWifiGameInput(wifiInputString);
			
			lcd = new LCDInfo(odometer, objectDetector);

			performInitialLocalization(odometer, navigator, usl, lightLocalizer);
			navigator.localizationTravelTo(0, 0);
			navigator.turnTo(Math.toRadians(0), true);

			navigator.setGameObject(new CaptureTheFlagGameObject(wifiInputString));
			navigator.start();
			
			break;

		// Test case
		case Button.ID_RIGHT:
			
			lcd = new LCDInfo(odometer, objectDetector);
			
			performInitialLocalization(odometer, navigator, usl, lightLocalizer);
			navigator.localizationTravelTo(0, 0);
			navigator.turnTo(Math.toRadians(0),true);
			navigator.setGameObject( new CaptureTheFlagGameObject(betaWifiInputString));
			navigator.start();
			
			break;

		default:

			System.exit(0);
			break;

		}
		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
		System.exit(0);

	}

	private static void performInitialLocalization(Odometer odometer,
			Navigator navigator, USLocalizer usl, LightLocalizer lightLocalizer) {
		usl.doLocalization();
		odometer.setX(-10);
		odometer.setY(-10);
		lightLocalizer.lightLocalize(new Coordinate(0,0));
	}

	
	private static int[] getWifiGameInput(int[] wifiInputString) {
		WifiConnection conn = null;
		try {
			conn = new WifiConnection(SERVER_IP, TEAM_NUMBER);
		} catch (IOException e) {
			System.out.println("can't connect");
		}

		Transmission t = conn.getTransmission();
		if (t == null) {
			System.out.println("can't transmit");
		} else {
			wifiInputString = t.getTransmissionData();
		}
		return wifiInputString;
	}


}
