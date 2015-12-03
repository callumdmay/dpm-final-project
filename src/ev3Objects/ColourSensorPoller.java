package ev3Objects;
import lejos.robotics.SampleProvider;

/**
 * A class used to obtain the color detected by the color sensor.
 *
 */
public class ColourSensorPoller extends Thread {
	
	private Object lock;
	private SampleProvider colorSensorProvider;
	private float[] colorSensorData;
	
	
	private int colorID = 0;
	/**
	 * Classes required to detect an object's color
	 * @param colorProvider The color provider to be used
	 * @param forwardColorData The array of floats where the date will be stored
	 */
	public ColourSensorPoller (SampleProvider colorProvider, float[] forwardColorData )
	{
	 colorSensorProvider = colorProvider;
	 colorSensorData = forwardColorData;
	 lock = new Object();
	}
	/**
	 * Gets the color of an object
	 */
	@Override
	public void run()
	{
		while(true)
		{
			colorSensorProvider.fetchSample(colorSensorData, 0);
			colorID = (int)colorSensorData[0];
			
			synchronized(lock)
			{
			setColorID(colorID);
			}
			
		}
		
	}
	
	/**
	 * Setting the color to be detected
	 * @param ID The number representing the color
	 */
	public void setColorID(int ID)
	{
		colorID = ID;
	}
	
	/**
	 * Get the color detected
	 * @return The number representing the color
	 */
	public int getColorID()
	{
		return colorID;
	}
	
	
	
}
