package ev3Launcher;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;


public class DistanceCalibration {

	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

	private static final int FORWARD_SPEED = 300;

	// Constants
	public static final double WHEEL_RADIUS = 2.1;


	public static void main(String[] args) {

		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(2000);
			motor.setSpeed(FORWARD_SPEED);

		}
		
		int buttonChoice;

		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(2000);
			motor.setSpeed(FORWARD_SPEED);
		}


		final TextLCD t = LocalEV3.get().getTextLCD();

		do {
			// clear the display
			t.clear();

			// ask the user whether the motors should drive in a square or float
			t.drawString("< Left | Right >", 0, 0);
			t.drawString("       |        ", 0, 1);
			t.drawString("   Distance     ", 0, 2);
			t.drawString("1 Tile |2 Tile  ", 0, 3);
			t.drawString("       |        ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice == 0 );

		switch(buttonChoice) {

		case Button.ID_LEFT :

			leftMotor.rotate(convertDistance(WHEEL_RADIUS, 30.48), true);
			rightMotor.rotate(convertDistance(WHEEL_RADIUS, 30.48), false);

			break;

		case Button.ID_RIGHT:


			leftMotor.rotate(convertDistance(WHEEL_RADIUS, 60.96), true);
			rightMotor.rotate(convertDistance(WHEEL_RADIUS, 60.96), false);

			break;

		default:

			System.exit(0);
			break;

		}

	}



	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}




}
