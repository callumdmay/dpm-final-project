package ev3Launcher;

import java.io.IOException;

import ev3Localization.LightLocalizer;
import ev3Localization.USLocalizer;
import ev3Navigator.Navigator;
import ev3ObjectDetector.ObjectDetector;
import ev3ObjectDetector.ObstacleAvoider;
import ev3Objects.CaptureTheFlagGameObject;
import ev3Objects.Coordinate;
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

/**
 * A class to launch the EV3 program (main).
 */
public class EV3Launcher {

	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	// Ultrasonic sensor port connected to input S1
	// Color sensor port connected to input S2


	private static final EV3LargeRegulatedMotor rightMotor 				= new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor leftSideUltraSoundMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final EV3LargeRegulatedMotor liftMotor 				= new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final EV3LargeRegulatedMotor leftMotor 				= new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	private static final Port rearColorPort 		= LocalEV3.get().getPort("S1");		
	private static final Port forwardColorPort		= LocalEV3.get().getPort("S3");
	private static final Port rightUltraSonicPort 	= LocalEV3.get().getPort("S2");
	private static final Port leftUltraSonicPort 	= LocalEV3.get().getPort("S4");		



	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 12.05;
	private static final String SERVER_IP = "localhost";
	private static final int TEAM_NUMBER = 12;

	private static final int wifiInputString[] = {1, 2, -1, 4, 2, 6, 8, 8, 11, 1, 3, 2, 3};
	private static final int betaWifiInputString[] = {1,0,0,0,0,4,4,6,6,6,6,0,0};



	@SuppressWarnings("resource")
	public static void main(String[] args) {

		//Setup ultrasonic sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize operating mode
		// 4. Create a buffer for the sensor data
		SensorModes leftUltraSonicSensor = new EV3UltrasonicSensor(leftUltraSonicPort);
		SampleProvider leftUltraSonicSampleProvider = leftUltraSonicSensor.getMode("Distance");			// colorValue provides samples from this instance
		float[] leftUltraSonicData = new float[leftUltraSonicSampleProvider.sampleSize()];				// colorData is the buffer in which data are returned

		SensorModes rightUltraSonicSensor = new EV3UltrasonicSensor(rightUltraSonicPort);
		SampleProvider rightUltraSonicSampleProvider = rightUltraSonicSensor.getMode("Distance");			// colorValue provides samples from this instance
		float[] rightUltraSonicData = new float[rightUltraSonicSampleProvider.sampleSize()];				// colorData is the buffer in which data are returned


		//Setup color sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize operating mode
		// 4. Create a buffer for the sensor data
		SensorModes rearColorSensor = new EV3ColorSensor(rearColorPort);
		SampleProvider rearColorSensorSampleProvider = rearColorSensor.getMode("Red");			// colorValue provides samples from this instance
		float[] rearColorSensorData = new float[rearColorSensorSampleProvider.sampleSize()];			// colorData is the buffer in which data are returned

		EV3ColorSensor forwardColorSensor = new EV3ColorSensor(forwardColorPort);
		forwardColorSensor.setFloodlight(true);
		SampleProvider forwardColorSensorSampleProvider = forwardColorSensor.getColorIDMode();			// colorValue provides samples from this instance
		float[] forwardColorSensorData = new float[forwardColorSensorSampleProvider.sampleSize()];			// colorData is the buffer in which data are returned


		//Create motors object
		Motors motors = new Motors(leftMotor, rightMotor, leftSideUltraSoundMotor, liftMotor, WHEEL_RADIUS, TRACK);

		//object that provides ultrasonic distances from sensors
		UltrasonicPoller ultraSonicSampleProvider = new UltrasonicPoller(leftUltraSonicSampleProvider, leftUltraSonicData, rightUltraSonicSampleProvider, rightUltraSonicData);

		// setup the odometer and display
		Odometer odometer = new Odometer(motors);
		odometer.start();

		UltrasonicController pController = new PController(motors);

		ObstacleAvoider obstacleAvoider = new ObstacleAvoider(odometer, ultraSonicSampleProvider, pController, motors);
		ObjectDetector objectDetector = new ObjectDetector(ultraSonicSampleProvider, forwardColorSensorSampleProvider, forwardColorSensorData, odometer);

		//Create navigator
		Navigator navigator = new Navigator(odometer, objectDetector, obstacleAvoider, motors);

		//create the ultrasonic localizers
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
		} while (buttonChoice == 0 );

		switch(buttonChoice) {

		// Capture the Flag case
		case Button.ID_LEFT :
			lcd = new LCDInfo(odometer, objectDetector);

			usl.doLocalization();
			odometer.setX(-8);
			odometer.setY(-8);
			lightLocalizer.localizeDynamically();
			navigator.travelTo(0, 0);
			/*
			try {
				Transmission transmission = getWifiTransmission();
			} catch (IOException e) {
				textLCD.drawString("Connection Failed", 0, 5);
			} catch (NullPointerException e )
			{
				textLCD.drawString("Failed to read transmission", 0, 5);
			}
			*/
			//change parameter from wifiInputString to "transmission" when using real wifi input
			navigator.setGameObject(new CaptureTheFlagGameObject(betaWifiInputString));
			navigator.start();
			break;

			// Test case
		case Button.ID_RIGHT:
			lcd = new LCDInfo(odometer, objectDetector);
			
			
			break;

		default:

			System.exit(0);
			break;

		}
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	

	}

	public static Transmission getWifiTransmission() throws IOException
	{

		WifiConnection conn = null;
		try {
			conn = new WifiConnection(SERVER_IP, TEAM_NUMBER);
		} catch (IOException e) {
			throw new IOException();
		}

		if(conn.getTransmission() == null)
			throw new NullPointerException();
		
		return conn.getTransmission();
	}



}
