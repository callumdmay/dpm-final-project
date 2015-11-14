package ev3Localization;



import java.util.Arrays;

import ev3Navigator.Navigator;
import ev3Odometer.Odometer;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.robotics.SampleProvider;

/**
 * Creates an object to self-localize using the ultra-sonic sensor.
 */
public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };


	public static int 		ROTATION_SPEED 		= 85;
	private final int 		distanceNoiseMargin = 1;
	private final int 		measuredDistance 	= 30;
	private final int 		usSensorMaxDistance = 40;
	private final double 	TILE_SIZE 			= 30.48;
	private final double	us_SensorDistanceFromOrigin = 4.3;

	private Odometer 			odometer;
	private SampleProvider		usSensor;
	private float[] 			usData;
	private LocalizationType 	locType;
	private Navigator 			navigator;




	/**
	 * Stores an Odometer, a SampleProvider, a data array of floats, 
	 * a LocalizationType, and a Navigator to be used by the USLocalizer.
	 * @param odo The odometer to be used
	 * @param usSensor The SampleProvider to be used
	 * @param usData The array of float data to be used
	 * @param locType The localization type to be performed
	 * @param navigator The navigator to be used
	 */
	public USLocalizer(Odometer odo,  SampleProvider usSensor, float[] usData, LocalizationType locType, Navigator navigator) {
		this.odometer = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.navigator = navigator;
	}

	/**
	 * Perform the ultrasonic localization.
	 */
	public void doLocalization() {
	
		double angleA, angleB;

		if (locType == LocalizationType.FALLING_EDGE) {


			if(getFilteredData(7) <= usSensorMaxDistance-3){
				// rotate the robot until it sees no wall
				while(getFilteredData(7) <= usSensorMaxDistance-3)
					navigator.navigatorMotorCommands.rotateClockWise(ROTATION_SPEED);


				//quickly turn away from wall, to ensure the same value isn't captured
				navigator.turnTo(odometer.getTheta() - 30*Math.PI/180, true);
			}

			// keep rotating until the robot sees a wall, then latch the angle
			angleA = latchFallingEdgeAngle(true);

			Sound.beep();

			// switch direction and wait until it sees no wall
			while(getFilteredData(5) <= usSensorMaxDistance-3)
				navigator.navigatorMotorCommands.rotateCounterClockWise(ROTATION_SPEED);


			// keep rotating until the robot sees a wall, then latch the angle
			angleB = latchFallingEdgeAngle(false);

			Sound.beep();


		} else {

			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */

			while(getFilteredData(5) >= measuredDistance - 10)
				navigator.navigatorMotorCommands.rotateCounterClockWise(ROTATION_SPEED+10);

			angleA = latchRisingEdgeAngle(true);
			Sound.beep();

			//turn away from wall that we just captured angle from, so as not to 
			//capture it again
			navigator.turnTo(odometer.getTheta() + Math.toRadians(45), true);

			while(getFilteredData(5) >= measuredDistance - 10)
				navigator.navigatorMotorCommands.rotateCounterClockWise(ROTATION_SPEED);

			angleB = latchRisingEdgeAngle(false);
			Sound.beep();


		}

		// angleA is clockwise from angleB, so assume the average of the
		// angles to the right of angleB is 45 degrees past 'north'
		double newTheta = odometer.getTheta() + calculateOdometerAngleAdjustment(angleA, angleB) + Math.toRadians(90);

		// update the odometer position (example to follow:)
		odometer.setPosition(new double [] {0.0, 0.0, newTheta}, new boolean [] {true, true, true});

		updateOdometerLocation();	
	}

	/**
	 * Latch the falling edge angles to be used to localization calculations.
	 * @param clockWise Whether the EV3 should turn clockwise
	 * @return The latched angle
	 */
	private double latchFallingEdgeAngle(boolean clockWise)
	{
		double fallingEdgeAngle1;
		double fallingEdgeAngle2;
		if(clockWise){
			// keep rotating until the robot sees a wall, then latch the angle
			while(getFilteredData(7)> measuredDistance+distanceNoiseMargin)
				navigator.navigatorMotorCommands.rotateClockWise(ROTATION_SPEED);

			fallingEdgeAngle1 = odometer.getTheta();

			while(getFilteredData(5)> measuredDistance-distanceNoiseMargin)
				navigator.navigatorMotorCommands.rotateClockWise(ROTATION_SPEED);

			fallingEdgeAngle2 = odometer.getTheta();

			return calculateAngleAverage(fallingEdgeAngle1, fallingEdgeAngle2);

		}

		else{

			// keep rotating until the robot sees a wall, then latch the angle
			while(getFilteredData(7)> measuredDistance+distanceNoiseMargin)
				navigator.navigatorMotorCommands.rotateCounterClockWise(ROTATION_SPEED);

			fallingEdgeAngle1 = odometer.getTheta();

			while(getFilteredData(5)> measuredDistance-distanceNoiseMargin)
				navigator.navigatorMotorCommands.rotateCounterClockWise(ROTATION_SPEED);

			fallingEdgeAngle2 = odometer.getTheta();

			return calculateAngleAverage(fallingEdgeAngle1, fallingEdgeAngle2);

		}
	}
	/**
	 * Latch the rising edge angles to be used to localization calculations.
	 * @param clockWise Whether the EV3 should turn clockwise
	 * @return The latched angle
	 */
	private double latchRisingEdgeAngle(boolean clockWise)
	{
		double risingEdgeAngle1;
		double risingEdgeAngle2;

		if(clockWise)
		{
			while(getFilteredData(5) <=measuredDistance - distanceNoiseMargin)
				navigator.navigatorMotorCommands.rotateClockWise(ROTATION_SPEED);

			risingEdgeAngle1 = odometer.getTheta();

			while(getFilteredData(5) <= measuredDistance + distanceNoiseMargin)
				navigator.navigatorMotorCommands.rotateClockWise(ROTATION_SPEED);

			risingEdgeAngle2 = odometer.getTheta();

			return calculateAngleAverage(risingEdgeAngle1, risingEdgeAngle2);
		}
		else
		{
			while(getFilteredData(5) <=measuredDistance - distanceNoiseMargin)
				navigator.navigatorMotorCommands.rotateCounterClockWise(ROTATION_SPEED);

			risingEdgeAngle1 = odometer.getTheta();

			while(getFilteredData(5) <= measuredDistance + distanceNoiseMargin)
				navigator.navigatorMotorCommands.rotateCounterClockWise(ROTATION_SPEED);

			risingEdgeAngle2 = odometer.getTheta();
			return calculateAngleAverage(risingEdgeAngle1, risingEdgeAngle2);
		}

	}
	/**
	 * Returns the average of two input angles
	 * @param angle1 The first angle
	 * @param angle2 The second angle
	 * @return The average of the first and second angles
	 */
	public static double calculateAngleAverage(double angle1, double angle2)
	{
		double x = Math.abs(angle1 -angle2);

		if (x < Math.PI) 
			return  ((angle1 + angle2) / 2) % (2 *Math.PI);
		if (x != Math.PI)
			return (((angle1 + angle2) / 2) + Math.PI) % (2 *Math.PI);

		throw new ArithmeticException("Could not calculate angle average of numbers");

	}
	/**
	 * Returns the angle to adjust to.
	 * @param angleA The first angle
	 * @param angleB The second angle
	 * @return The angle to adjust to
	 */
	private double calculateOdometerAngleAdjustment(double angleA, double angleB)
	{
		double angle1 = 45;
		double angle2 = 225;
		if(angleA <= angleB)
			return Math.toRadians(angle1) - (angleA + angleB)/2 + Math.toRadians(90) % Math.toRadians(360);

		if(angleA > angleB)
			return Math.toRadians(angle2)- (angleA + angleB)/2 + Math.toRadians(90) %Math.toRadians(360);

		throw new ArithmeticException("Could not calculate odometer angle adjustment");

	}
	/**
	 * Update the x and y coordinates using the ultrasonic sensor.
	 */
	private void updateOdometerLocation()
	{
		//face right wall and record y distance
		navigator.turnTo(Math.toRadians(270), false);
		odometer.setY(getFilteredData(11) - TILE_SIZE + us_SensorDistanceFromOrigin);

		//face back wall and record x distance
		navigator.turnTo(Math.toRadians(180), false);
		odometer.setX(getFilteredData(11) - TILE_SIZE + us_SensorDistanceFromOrigin);
		
		navigator.turnTo(Math.toRadians(0), false);
	}

	/**
	 * Overload of getFilteredData that takes a sample size, then performs median filtering
	 */
	private float getFilteredData(int sampleSize){

		float sampleData[] = new float[sampleSize];

		for(int index = 0 ; index < sampleData.length; index++)
		{
			usSensor.fetchSample(usData, 0);

			if(usData[0]*100> usSensorMaxDistance)
				usData[0] = usSensorMaxDistance;

			sampleData[index] = usData[0]*100;
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Arrays.sort(sampleData);
		LCD.drawString("Distance: "+sampleData[(int) Math.floor(sampleData.length/2)], 0, 4);
		return sampleData[(int) Math.floor(sampleData.length/2)];
	}



}
