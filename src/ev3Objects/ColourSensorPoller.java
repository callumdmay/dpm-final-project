package ev3Objects;
import lejos.robotics.SampleProvider;


public class ColourSensorPoller extends Thread {
	
	private Object lock;
	private SampleProvider colorSensorProvider;
	private float[] colorSensorData;
	
	
	private int colorID = 0;
	
	public ColourSensorPoller (SampleProvider colorProvider, float[] forwardColorData )
	{
	 colorSensorProvider = colorProvider;
	 colorSensorData = forwardColorData;
	 lock = new Object();
	}
	
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
	
	public void setColorID(int ID)
	{
		colorID = ID;
	}
	
	public int getColorID()
	{
		return colorID;
	}
	
	
	
}
