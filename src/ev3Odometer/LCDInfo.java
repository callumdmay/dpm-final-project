package ev3Odometer;

import ev3ObjectDetector.ObjectDetector;
import ev3WallFollower.UltrasonicPoller;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

/**
 * Creates and object responsible for the LCD display on the EV3.
 */
public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private ObjectDetector objectDetector;
	private Timer lcdTimer;
	private TextLCD LCD = LocalEV3.get().getTextLCD();;
	
	// arrays for displaying data
	private double [] pos;
	private double distTravelled;
	
	/**
	 * Object stores an Odometer and an ObjectDetector to update values
	 * @param odo The Odometer to be used
	 * @param objectDetector The ObjectDetector to be used
	 */
	public LCDInfo(Odometer odo, ObjectDetector objectDetector) {
		this.odo = odo;
		this.objectDetector = objectDetector;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		
		
		// initialize the arrays for displaying data
		pos = new double [3];
		distTravelled = 0;
		
		// start the timer
		lcdTimer.start();
	}
	/**
	 * Draw the desired values on the EV3 display.
	 */
	@Override
	public void timedOut() { 
		odo.getPosition(pos, new boolean[] { true, true, true });
		distTravelled = odo.getDistanceTravelled();
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawString((formattedDoubleToString(pos[0], 2)), 3, 0);
		LCD.drawString(formattedDoubleToString(pos[1], 2), 3, 1);
		LCD.drawString(formattedDoubleToString(pos[2], 2), 3, 2);
		

		LCD.drawString("Right US:"+objectDetector.getRightUSDistance(), 0, 3);		
		LCD.drawString("Left US:"+objectDetector.getLeftUSDistance(), 0, 4);		
		LCD.drawString("D: ", 0, 5);
		LCD.drawString(formattedDoubleToString(distTravelled, 2), 3, 5);
		
		LCD.drawString("ColorID :" + objectDetector.getColorID(), 1, 6);
		


	}
	/**
	 * Returns a formatted String from a double
	 * @param x The input double
	 * @param places The position on the LCD
	 * @return The formatted string
	 */
	private static String formattedDoubleToString(double x, int places) {
		String result = "";
		String stack = "";
		long t;
		
		// put in a minus sign as needed
		if (x < 0.0)
			result += "-";
		
		// put in a leading 0
		if (-1.0 < x && x < 1.0)
			result += "0";
		else {
			t = (long)x;
			if (t < 0)
				t = -t;
			
			while (t > 0) {
				stack = Long.toString(t % 10) + stack;
				t /= 10;
			}
			
			result += stack;
		}
		
		// put the decimal, if needed
		if (places > 0) {
			result += ".";
		
			// put the appropriate number of decimals
			for (int i = 0; i < places; i++) {
				x = Math.abs(x);
				x = x - Math.floor(x);
				x *= 10.0;
				result += Long.toString((long)x);
			}
		}
		
		return result;
	}
}
