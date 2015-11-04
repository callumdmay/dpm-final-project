package ev3ObjectDetector;


import java.util.Arrays;

import ev3Odometer.Odometer;
import ev3WallFollower.UltrasonicPoller;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

/**
 * Creates an object that will detect and identify objects.
 */
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
	/**
	 * Stores an objects necessary for object detection and identification
	 * @param pUltraSonicPoller The ultrasonic sensor to be used
	 * @param pColorValue The color value to be used
	 * @param pColorData An array to store data
	 * @param pOdometer The odometer to be used
	 */
	public ObjectDetector(UltrasonicPoller pUltraSonicPoller, SampleProvider pColorValue, float[] pColorData, Odometer pOdometer)
	{
		ultraSonicPoller  = pUltraSonicPoller;
		colorValue = pColorValue;
		colorData = pColorData;
		odometer = pOdometer;
	}

	/**
	 * This method checks for obstacles in front of the robot as it is moving forward
	 * @param distance The distance to check for
	 * @return True if the ultrasonic sensor detected an object that distance
	 */
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
	/**
	 * Returns true if an object has been detected by the ultrasonic sensor
	 * @return True if objected detected
	 */
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
	/**
	 * Identifies between a blue block and a wooden obstacle
	 */
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
	/**
	 * Returns true if an object has been detected
	 * @return The boolean representing this information
	 */
	public boolean isObjectDetected()
	{
		boolean returnedValue;
		synchronized(lock)
		{
			returnedValue = objectDetected;
		}
		return returnedValue;
	}
	/**
	 * Set the status of if an object has been detected
	 * @param objectDetected The boolean representing this information
	 */
	public void setObjectDetected(boolean objectDetected) {
		synchronized(lock)
		{
			this.objectDetected = objectDetected;
		}
	}		
	/**
	 * Set the type of object detected
	 * @param pObject The Object type of the object detected
	 */
	public void setCurrentObject(OBJECT_TYPE pObject)
	{
		synchronized(lock)
		{
			currentObject = pObject;
		}	
	}
	/**
	 * Get the default obstacle distance
	 * @return The default obstacle distance
	 */
	public double getDefaultObstacleDistance() {
		return defaultObstacleDistance;
	}
	
	/**
	 * Get the current object type
	 * @return The currect object type
	 */
	public OBJECT_TYPE getCurrentObject() {
		OBJECT_TYPE returnedValue;
		synchronized(lock)
		{
			returnedValue = currentObject;
		}	
		return returnedValue;
	}

	/**
	 * Get the distance to the object
	 * @return The distance to the object
	 */
	public double getObjectDistance(){

		return ultraSonicPoller.getLeftUltraSoundSensorDistance();
	}


}
