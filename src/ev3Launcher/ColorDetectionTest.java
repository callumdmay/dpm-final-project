package ev3Launcher;

import ev3ObjectDetector.ObjectDetector;
import ev3Objects.ColourSensorPoller;
import ev3Objects.Motors;
import ev3Odometer.LCDInfo;
import ev3Odometer.Odometer;
import ev3WallFollower.UltrasonicPoller;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import ev3Odometer.LCDInfo;


public class ColorDetectionTest {


	private static final Port rearColorPort = LocalEV3.get().getPort("S1");
	private static final Port forwardColorPort = LocalEV3.get().getPort("S3");
	private static final Port rightUltraSonicPort = LocalEV3.get().getPort("S2");
	private static final Port leftUltraSonicPort = LocalEV3.get().getPort("S4");
	
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor leftSideUltraSoundMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final EV3LargeRegulatedMotor liftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	
	
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 12;
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Motors motors = new Motors(leftMotor, rightMotor, leftSideUltraSoundMotor, liftMotor, WHEEL_RADIUS, TRACK);
		
		EV3ColorSensor forwardColorSensor = new EV3ColorSensor(forwardColorPort);
		SampleProvider forwardColorSensorSampleProvider = forwardColorSensor.getColorIDMode(); // colorValue provides samples from this instance
		float[] forwardColorSensorData = new float[forwardColorSensorSampleProvider.sampleSize()]; // colorData is the buffer in which data are returned

		SensorModes leftUltraSonicSensor = new EV3UltrasonicSensor(leftUltraSonicPort);
		SampleProvider leftUltraSonicSampleProvider = leftUltraSonicSensor.getMode("Distance"); // colorValue provides samples from this instance
		float[] leftUltraSonicData = new float[leftUltraSonicSampleProvider.sampleSize()]; // colorData is the buffer in which data are returned

		SensorModes rightUltraSonicSensor = new EV3UltrasonicSensor(rightUltraSonicPort);
		SampleProvider rightUltraSonicSampleProvider = rightUltraSonicSensor.getMode("Distance"); // colorValue provides samples from this instance
		float[] rightUltraSonicData = new float[rightUltraSonicSampleProvider.sampleSize()]; // colorData is the buffer in which data are returned

		
		
		ColourSensorPoller colourSensorPoller =  new ColourSensorPoller(forwardColorSensorSampleProvider, forwardColorSensorData);
		UltrasonicPoller ultraSonicSampleProvider = new UltrasonicPoller(leftUltraSonicSampleProvider,
				leftUltraSonicData, rightUltraSonicSampleProvider, rightUltraSonicData);

		// setup the odometer and display
				Odometer odometer = new Odometer(motors);
				odometer.start();
				ObjectDetector objectDetector = new ObjectDetector(ultraSonicSampleProvider, odometer, colourSensorPoller);
		
				
		LCDInfo lcd = new LCDInfo(odometer, objectDetector);		
				
		
		colourSensorPoller.start();
	}

}
