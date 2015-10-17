package ev3WallFollower;

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


public class UltrasonicPoller implements TimerListener{
	private SampleProvider ultraSonicSensorSampleProvider;
	private float[] usData;
	//set to 30 as we need the distance variable to be initialized above the obstacle detection distance
	private int distance = 30;
	private Object lock;
	private Timer lcdTimer;
	public static final int LCD_REFRESH = 10;

	public UltrasonicPoller(SampleProvider us, float[] usData) {
		this.ultraSonicSensorSampleProvider = us;
		this.usData = usData;
		lock = new Object();
		
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		lcdTimer.start();
	}

	//  Sensors now return floats using a uniform protocol.
	//  Need to convert US result to an integer [0,255]

	public void timedOut() {
		while (true) {
			ultraSonicSensorSampleProvider.fetchSample(usData,0);							// acquire data

			//The poller now simply updates the distance variable, it does not influence the controller at all
			synchronized(lock){
				setDistance((int)(usData[0]*100.0));
			}
			
		}
	}


	private void setDistance(int distance) {
		synchronized (lock) {
			this.distance = distance;
		}
	}


	public int getDistance() {
		int result;
		synchronized (lock) {
			result = distance;
		}

		return result;
	}



}
