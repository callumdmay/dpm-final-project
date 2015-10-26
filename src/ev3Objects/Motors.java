package ev3Objects;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Motors {
	
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private EV3LargeRegulatedMotor leftSideUltraSoundMotor;
	private EV3LargeRegulatedMotor rightSideUltraSoundMotor;
	private EV3LargeRegulatedMotor blockLiftMotor;
	
	private double wheelRadius;
	private double axleLength;
	
	
	public Motors (EV3LargeRegulatedMotor pLeftMotor, EV3LargeRegulatedMotor pRightMotor, 
			EV3LargeRegulatedMotor pLeftSideUltraSoundMotor, EV3LargeRegulatedMotor pRightSideUltraSoundMotor, 
			EV3LargeRegulatedMotor pblockLiftMotor, double pWheelRadius, double pAxleLength)
	{
		leftMotor 					= pLeftMotor;
		rightMotor 					= pRightMotor;
		leftSideUltraSoundMotor 	= pLeftSideUltraSoundMotor;
		rightSideUltraSoundMotor	= pRightSideUltraSoundMotor;
		blockLiftMotor 				= pblockLiftMotor;
		wheelRadius 				= pWheelRadius;
		axleLength 					= pAxleLength;
	}

	public EV3LargeRegulatedMotor getLeftMotor() {
		return leftMotor;
	}

	public EV3LargeRegulatedMotor getRightMotor() {
		return rightMotor;
	}

	public EV3LargeRegulatedMotor getLeftSideUltraSoundMotor() {
		return leftSideUltraSoundMotor;
	}

	public EV3LargeRegulatedMotor getRightSideUltraSoundMotor() {
		return rightSideUltraSoundMotor;
	}
	
	public EV3LargeRegulatedMotor getBlockLiftMotor() {
		return blockLiftMotor;
	}

	public double getWheelRadius() {
		return wheelRadius;
	}

	public double getAxleLength() {
		return axleLength;
	}
	
	

}
