package  ev3Launcher;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class WheelCalibration {

	
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	
	private static final int ROTATE_SPEED = 150;
	
	// Constants
		public static final double WHEEL_RADIUS = 2.1;
		public static final double TRACK = 11.9;
		//Track without claw is about 11.3
		//Track with claw is 11.9
	public static void main(String[] args) {
		int buttonChoice;
		
		
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(2000);
			motor.setSpeed(ROTATE_SPEED);
		}
		
		
		final TextLCD t = LocalEV3.get().getTextLCD();
	
		do {
			// clear the display
			t.clear();

			// ask the user whether the motors should drive in a square or float
			t.drawString("< Left | Right >", 0, 0);
			t.drawString("       |        ", 0, 1);
			t.drawString("   Rotations    ", 0, 2);
			t.drawString("   2   |  5     ", 0, 3);
			t.drawString("       |        ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice == 0 );

		switch(buttonChoice) {

		case Button.ID_LEFT :

			for(int count = 0 ; count < 8; count++)
			{
				leftMotor.rotate(convertAngle(WHEEL_RADIUS, TRACK, 90), true);
				rightMotor.rotate(-convertAngle(WHEEL_RADIUS, TRACK, 90), false);
			}
			
			
			break;

		case Button.ID_RIGHT:
			
			for(int count = 0 ; count < 20; count++)
			{
				leftMotor.rotate(convertAngle(WHEEL_RADIUS, TRACK, 90), true);
				rightMotor.rotate(-convertAngle(WHEEL_RADIUS, TRACK, 90), false);
			}
			
			break;

		default:

			System.exit(0);
			break;

		}

	}
	
	
	public static int convertAngle(double radius, double width, double angle) {
		return (int) ((180.0 * Math.PI * width * angle / 360.0) / (Math.PI * radius));
	}

}
