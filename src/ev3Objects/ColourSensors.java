package ev3Objects;

import lejos.robotics.SampleProvider;

public class ColourSensors {

	SampleProvider forwardColourSensor;
	SampleProvider rearColourSensor;
	float colourSensorSample[];

	public ColourSensors(SampleProvider pForwardColourSensor, SampleProvider pRearColourSensor, float pColourSensorSample[])
	{

		forwardColourSensor = pForwardColourSensor;
		rearColourSensor = pRearColourSensor;
		colourSensorSample = pColourSensorSample;

	}
	
	
	



}
