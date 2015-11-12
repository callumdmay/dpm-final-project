package ev3WallFollower;

import java.util.Arrays;

import lejos.robotics.SampleProvider;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

//
//  Control of the wall follower is applied periodically by the 
//  UltrasonicPoller thread.  The while loop at the bottom executes
//  in a loop.  Assuming that the us.fetchSample, and cont.processUSData
//  methods operate in about 20mS, and that the thread sleeps for
//  10 mS at the end of each loop, then one cycle through the loop
//  is approximately 30 mS.  This corresponds to a sampling rate
//  of 1/70mS or about 33 Hz.
//

/**
 * Creates an object that gives the distance of an object using the
 * ultrasonic sensor with a polling rate of 33Hz.
 */
public class UltrasonicPoller implements TimerListener{
	private SampleProvider leftUltraSonicSensorSampleProvider;
	private SampleProvider rightUltraSonicSensorSampleProvider;
	
	private float[] leftUltraSonicData;
	private float[] rightUltraSonicData;
	//set to 30 as we need the distance variable to be initialized above the obstacle detection distance
	private int leftUltraSoundSensorDistance = 30;
	private int rightUltraSoundSensorDistance = 30;
	private Object lock;
	private Timer lcdTimer;
	public static final int LCD_REFRESH = 10;
	/**
	 * Stores objects essential for the ultrasonic sensor readings.
	 * @param pLeftUltraSonicSampleProvider The ultrasonic sensor to be used on the left side
	 * @param pLeftUltraSonicData An array for the left sensor's data
	 * @param pRightUltraSonicSampleProvider The ultrasonic sensor to be used on the right side
	 * @param pRightUltraSonicData An array for the right sensor's data
	 */
	public UltrasonicPoller(SampleProvider pLeftUltraSonicSampleProvider, float[] pLeftUltraSonicData, SampleProvider pRightUltraSonicSampleProvider, float[] pRightUltraSonicData) {
		leftUltraSonicSensorSampleProvider = pLeftUltraSonicSampleProvider;
		leftUltraSonicData = pLeftUltraSonicData;
		
		rightUltraSonicSensorSampleProvider = pRightUltraSonicSampleProvider;
		rightUltraSonicData = pRightUltraSonicData;
		
		lock = new Object();
		
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		lcdTimer.start();
	}

	/**
	 * Sensors now return floats using a uniform protocol.
	 * Need to convert US result to an integer [0,255]
	 */
	@Override
	public void timedOut() {
		while (true) {
			float leftUltraSonicSampleData[] = new float[7];
			float rightUltraSonicSampleData[] = new float[7];

			for(int index = 0 ; index < leftUltraSonicSampleData.length; index++)
			{
				leftUltraSonicSensorSampleProvider.fetchSample(leftUltraSonicData, 0);
				rightUltraSonicSensorSampleProvider.fetchSample(rightUltraSonicData, 0);
				leftUltraSonicSampleData[index] = leftUltraSonicData[0]*100;
				rightUltraSonicSampleData[index] = rightUltraSonicData[0]*100;
				
			}
			
			Arrays.sort(leftUltraSonicSampleData);
			Arrays.sort(rightUltraSonicSampleData);

			//The poller now simply updates the distance variable, it does not influence the controller at all
			synchronized(lock){
				setLeftUltraSonicDistance((Math.round(leftUltraSonicSampleData[2])));
				setRightUltraSonicDistance((Math.round(rightUltraSonicSampleData[2])));
				
			}
			
		}
	}

	/**
	 * Set right ultrasonic distance
	 * @param distance The distance to be set
	 */
	private void setRightUltraSonicDistance(int distance) {
		synchronized (lock) {
			this.rightUltraSoundSensorDistance = distance;
		}
	}
	
	/**
	 * Set left ultrasonic distance
	 * @param distance The distance to be set
	 */
	private void setLeftUltraSonicDistance(int distance) {
		synchronized (lock) {
			this.leftUltraSoundSensorDistance = distance;
		}
	}

	/**
	 * Get right ultrasonic distance
	 * @param distance The distance measured by the left ultrasonic
	 */
	public int getLeftUltraSoundSensorDistance() {
		int result;
		synchronized (lock) {
			result = leftUltraSoundSensorDistance;
		}

		return result;
	}
	
	/**
	 * Get right ultrasonic distance
	 * @param distance The distance measured by the right ultrasonic
	 */
	public int getRightUltraSoundSensorDistance() {
		int result;
		synchronized (lock) {
			result = rightUltraSoundSensorDistance;
		}

		return result;
	}




}
