package ev3Launcher;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * A class to test claw functionality.
 */
public class ClawTest {
	private static final EV3LargeRegulatedMotor liftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		

		liftMotor.setAcceleration(100);
		liftMotor.rotate(175);
		
		liftMotor.rotate(-175);
		
	}

}
