package ev3ObjectDetector;


import java.util.Arrays;

import ev3Odometer.Odometer;
import ev3WallFollower.UltrasonicPoller;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class ObjectDetector{

	private SampleProvider colorValue;
	private Odometer odometer;
	private UltrasonicPoller ultraSonicPoller;

	public enum OBJECT_TYPE { block, obstacle } 

	private float[] colorData;
	private final int FILTER_OUT = 5;
	private int filterControl;
	private final double defaultObstacleDistance = 20;
	private OBJECT_TYPE currentObject;
	private boolean objectDetected;

	private Object lock = new Object();

	public ObjectDetector(UltrasonicPoller pUltraSonicPoller, SampleProvider pColorValue, float[] pColorData, Odometer pOdometer)
	{
		ultraSonicPoller  = pUltraSonicPoller;
		colorValue = pColorValue;
		colorData = pColorData;
		odometer = pOdometer;
	}


	//This method checks for obstacles in front of the robot as it is moving forward
	public boolean detectedObject(int distance)
	{

		if( ultraSonicPoller.getLeftUltraSoundSensorDistance() < distance || ultraSonicPoller.getRightUltraSoundSensorDistance() < distance)
		{
			synchronized(lock)
			{
				setObjectDetected(true);
			}
			return true;

		}
		
		synchronized(lock)
		{
			setObjectDetected(false);
			setCurrentObject(null);
		}
		return false;
	}

	public boolean detectedObject()
	{

		if( ultraSonicPoller.getLeftUltraSoundSensorDistance() < defaultObstacleDistance || ultraSonicPoller.getRightUltraSoundSensorDistance() < defaultObstacleDistance)
		{
			synchronized(lock)
			{
				setObjectDetected(true);
			}
			return true;

		}
		
		synchronized(lock)
		{
			setObjectDetected(false);
			setCurrentObject(null);
		}
		return false;

	}

	public void processObject()
	{

		if(ultraSonicPoller.getLeftUltraSoundSensorDistance() <=8  && getCurrentObject() == null)
		{
			colorValue.fetchSample(colorData, 0);
			if(colorData[0]== 2){
				Sound.beep();
				setCurrentObject(OBJECT_TYPE.block);
			}
			else
			{
				Sound.beep();
				Sound.beep();
				setCurrentObject(OBJECT_TYPE.obstacle);
			}
		}
	}

	public boolean isObjectDetected()
	{
		boolean returnedValue;
		synchronized(lock)
		{
			returnedValue = objectDetected;
		}
		return returnedValue;
	}

	public void setObjectDetected(boolean objectDetected) {
		synchronized(lock)
		{
			this.objectDetected = objectDetected;
		}
	}		

	public void setCurrentObject(OBJECT_TYPE pObject)
	{
		synchronized(lock)
		{
			currentObject = pObject;
		}	
	}

	public double getDefaultObstacleDistance() {
		return defaultObstacleDistance;
	}

	public OBJECT_TYPE getCurrentObject() {
		OBJECT_TYPE returnedValue;
		synchronized(lock)
		{
			returnedValue = currentObject;
		}	
		return returnedValue;
	}

	public double getObjectDistance(){

		return ultraSonicPoller.getLeftUltraSoundSensorDistance();
	}


}
