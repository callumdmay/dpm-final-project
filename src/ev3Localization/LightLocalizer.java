package ev3Localization;

import ev3Navigator.Navigator;
import ev3Odometer.Odometer;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class LightLocalizer {

	public static int 		ROTATION_SPEED 		= 25;
	private final int 		lineDetectionValue = 40;
	private final double	light_SensorDistanceFromOrigin = 13.3;

	private Odometer 			odometer;
	private SampleProvider 		colorSensor;
	private float[] 			colorData;	
	private final double[]		calibrationCoordinates = {-3,-3};
	private Navigator 			navigator;


	public LightLocalizer(Odometer odo, Navigator navigator, SampleProvider colorSensor, float[] colorData) {
		this.odometer = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.navigator = navigator;
	}

	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees


		double blackLineAngles[] = new double[4];

		navigator.travelTo(calibrationCoordinates[0],calibrationCoordinates[1]);
		navigator.turnTo(Math.PI/2, ROTATION_SPEED * 2);

		for( int index = 0 ; index < blackLineAngles.length; index ++)
		{
			//Capture the angle when we first encounter the black line
			while(!blackLineDetected())
				navigator.rotateCounterClockWise(ROTATION_SPEED);

			Sound.beep();
			blackLineAngles[index]= odometer.getTheta();

			//turn off of black line so as not to capture the same line twice
			navigator.turnTo(odometer.getTheta() + 5*Math.PI/180,ROTATION_SPEED);
		}

		double deltaY = blackLineAngles[2] - blackLineAngles[0];
		double deltaX = blackLineAngles[3] - blackLineAngles[1];

		odometer.setX(-light_SensorDistanceFromOrigin * Math.cos(deltaY/2));
		odometer.setY(-light_SensorDistanceFromOrigin * Math.cos(deltaX/2));

		odometer.setTheta(odometer.getTheta() + blackLineAngles[0]+Math.toRadians(180) +deltaY/2);
		navigator.travelTo(0, 0);

		initiateFinalCalibration();

	}

	private boolean blackLineDetected()
	{
		colorSensor.fetchSample(colorData, 0);

		//if we run over a black line, calculate and update odometer values
		if((int)(colorData[0]*100) < lineDetectionValue)
			return true;
		else 
			return false;
	}



	private void initiateFinalCalibration()
	{
		navigator.turnTo(0);
		while(odometer.getTheta() <= Math.toRadians(20) && !blackLineDetected())
			navigator.rotateCounterClockWise(15);

		if(blackLineDetected()){
			Sound.beep();
			odometer.setTheta(0);
		}
		else{
			while(odometer.getTheta() >= -Math.toRadians(20) && !blackLineDetected())
				navigator.rotateClockWise(10);

			if(blackLineDetected()){
				Sound.beep();
				odometer.setTheta(0);
			}
		}
		navigator.stopMotors();
		navigator.turnTo(0);

	}


}
