package ev3Objects;

import lejos.robotics.SampleProvider;

/**
 * Creates an object to store useful color sensor objects
 */
public class ColourSensors {

	SampleProvider forwardColourSensor;
	SampleProvider rearColourSensor;
	float colourSensorSample[];

	/**
	 * Stores objects essential objects for color detection.
	 * @param pForwardColourSensor The front color sensor
	 * @param pRearColourSensor The rear color sensor
	 * @param pColourSensorSample A an array for the data
	 */
	public ColourSensors(SampleProvider pForwardColourSensor, SampleProvider pRearColourSensor, float pColourSensorSample[])
	{

		forwardColourSensor = pForwardColourSensor;
		rearColourSensor = pRearColourSensor;
		colourSensorSample = pColourSensorSample;
		
	}
	
	
	



}
