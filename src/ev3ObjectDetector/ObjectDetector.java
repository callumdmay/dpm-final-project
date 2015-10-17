package ev3ObjectDetector;


import ev3Odometer.Odometer;
import ev3WallFollower.UltrasonicPoller;
import lejos.robotics.SampleProvider;

public class ObjectDetector{

	SampleProvider colorValue;
	Odometer odometer;
	UltrasonicPoller ultraSonicPoller;

	private float[] colorData;
	private final int FILTER_OUT = 5;
	private int filterControl;
	private final double obstacleDistance = 20;

	private ObstacleAvoider obstacleAvoider;

	public ObjectDetector(UltrasonicPoller pUltraSonicPoller, SampleProvider pColorValue, float[] pColorData, Odometer pOdometer, ObstacleAvoider pObstacleAvoider)
	{
		ultraSonicPoller  = pUltraSonicPoller;
		colorValue = pColorValue;
		colorData = pColorData;
		odometer = pOdometer;
		obstacleAvoider = pObstacleAvoider;
	}


	//This method checks for obstacles in front of the robot as it is moving forward
	public void checkForObjects( double pX, double pY)
	{

		// rudimentary filter - checks 5 times to ensure obstacle is really ahead of robot
		if( ultraSonicPoller.getDistance() < obstacleDistance)
		{
			filterControl ++;
		}

		//We must get 5 readings of less than 25 before we initiate obstacle avoidance
		if(filterControl < FILTER_OUT)
			return;

		filterControl = 0;

		determineObject();
	}

	public void determineObject()
	{

	}


}
