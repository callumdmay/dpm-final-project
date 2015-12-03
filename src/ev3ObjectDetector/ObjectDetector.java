package ev3ObjectDetector;


import ev3Objects.ColourSensorPoller;
import ev3Objects.FoundOpponentFlagException;
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
	private ColourSensorPoller colourSensorPoller;
	private boolean setFlag = false;


	public enum OBJECT_TYPE { flag, obstacle } 

	private final int FILTER_OUT = 5;
	private int filterControl;
	private final double defaultObstacleDistance = 18;


	private Object lock = new Object();
	
	/**
	 * Stores an objects necessary for object detection and identification
	 * @param pUltraSonicPoller The ultrasonic sensor to be used
	 * @param pColorValue The color value to be used
	 * @param pColorData An array to store data
	 * @param pOdometer The odometer to be used
	 */
	public ObjectDetector(UltrasonicPoller pUltraSonicPoller, Odometer pOdometer, ColourSensorPoller colourSensor)
	{
		colourSensorPoller = colourSensor;
		ultraSonicPoller  = pUltraSonicPoller;
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
			return true;
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

			return true;

		}

		return false;

	}

	/**
	 * Identifies between a blue block and a wooden obstacle
	 */
	public void determineIfObjectIsFlag(int flagColour)
	{

		if(colourSensorPoller.getColorID()== flagColour){

			Sound.beepSequenceUp();
			setFlagBlock(true);
			throw new FoundOpponentFlagException();
		}
		else
		{
			Sound.beep();
			Sound.beep();
			setFlagBlock(false);
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
	 * Get the distance to the object
	 * @return The distance to the object
	 */
	public double getObjectDistance(){


		if(ultraSonicPoller.getRightUltraSoundSensorDistance() < defaultObstacleDistance)
			return ultraSonicPoller.getRightUltraSoundSensorDistance();

		if(ultraSonicPoller.getLeftUltraSoundSensorDistance() < defaultObstacleDistance)
			return ultraSonicPoller.getLeftUltraSoundSensorDistance();

		return 100;
	}

	/**
	 * Get distance from the right US sensor
	 * @return The distance from the right US sensor
	 */
	public double getRightUSDistance()
	{
		return ultraSonicPoller.getRightUltraSoundSensorDistance();
	}
	
	/**
	 * Get distance from the left US sensor
	 * @return The distance from the left US sensor
	 */
	public double getLeftUSDistance()
	{
		return ultraSonicPoller.getLeftUltraSoundSensorDistance();
	}

	/**
	 * Set the setFlag boolean
	 * @param flagDetected Whether or not the block detected is the desired one
	 */
	public void setFlagBlock (boolean flagDetected)
	{
		setFlag = flagDetected;
	}
	
	/**
	 * Returns the setFlag boolean
	 * @return Whether or not the block detected is the desired one
	 */
	public boolean getFlagBlock()
	{
		return setFlag;
	}

	/**
	 * Returns the color of the block
	 * @return Returns the color of the block
	 */
	public int getColorID()
	{
		return colourSensorPoller.getColorID();
	}



}
