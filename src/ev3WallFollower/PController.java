package ev3WallFollower;

import ev3Objects.Motors;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {

	private static final int bandCenter = 25; // Offset from the wall (cm)
	private static final int bandWidth = 3; // Width of dead band (cm)
	private final int motorStraight = 150, FILTER_OUT = 20, motorVeryHigh = 300, motorVeryLow = 50, motorHigh = 250;
	private final int offset = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	private int filterControl;

	public PController(Motors pMotors) {
		// Default Constructor
		this.leftMotor = pMotors.getLeftMotor();
		this.rightMotor = pMotors.getRightMotor();
		filterControl = 0;
	}

	@Override
	public void processUSData(int leftUltraSonicSensor, int rightUltraSonicSensor) {

		// rudimentary filter - toss out invalid samples corresponding to null
		// signal.
		if (leftUltraSonicSensor > 255) {
			// impossible, sensor can only read to 255, must be a bad value
		} else if (leftUltraSonicSensor == 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} else if (leftUltraSonicSensor == 255) {
			// true 255, therefore set distance to 255
			distance = leftUltraSonicSensor;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			distance = leftUltraSonicSensor;
		}

		// calculate error
		int distanceError = distance - bandCenter;

		// calculate the proportion gain with the distance error
		int proportion = -(distanceError * 15);

		// Correct distance to wall
		if (Math.abs(distanceError) <= bandWidth) {
			leftMotor.setSpeed(motorStraight); // Initalize motor rolling
												// forward
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();
		}
		
		// Too close to the wall on the left or on the right side 
		else if (distanceError > 0 || rightUltraSonicSensor < 25) {
			// threshold if the distance of the wall is too close.
			if (distanceError >= 13) {
				rightMotor.setSpeed(motorVeryLow);
				leftMotor.setSpeed(motorHigh);
				leftMotor.forward();
				rightMotor.forward();
			} else {
				// motors will turn right by proportion
				rightMotor.setSpeed(motorStraight + proportion);
				leftMotor.setSpeed(motorStraight - proportion);
				leftMotor.forward();
				rightMotor.forward();

			}
		}
		
		
		
		// Too far from the wall
		else if (distanceError < 0 ) {
			// threshold if the proportion is way to high in case of damaging
			// the motors if the set speed is too high.
			if (proportion >= 200) {
				rightMotor.setSpeed(motorVeryHigh);
				leftMotor.setSpeed(motorStraight);
				leftMotor.forward();
				rightMotor.forward();
			}

			else {
				// motors will turn left by proportion
				rightMotor.setSpeed(motorStraight + proportion);
				leftMotor.setSpeed(motorStraight - proportion);
				leftMotor.forward();
				rightMotor.forward();
			}

		}


	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
