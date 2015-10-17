package ev3Navigator;



/*
 * 
 * This class contains utility/math methods that are used by the navigator class
 * These methods were refactored out of the Navigator class to reduce the size of the
 * navigator class and increase readability
 * These methods are only dependent on their parameters, hence why they are static.
 * 
 */
public class NavigatorUtility {



	//This method calculates the new angle the robot must face, based on the delta Y and delta X
	public static double calculateNewAngle(double deltaX, double deltaY)
	{

		if(deltaX >= 0 )
			return Math.atan(deltaY/deltaX);

		if(deltaX< 0 && deltaY >= 0)
			return Math.atan(deltaY/deltaX)+ Math.PI;

		if(deltaX < 0 && deltaY < 0)
			return Math.atan(deltaY/deltaX) - Math.PI;

		throw new ArithmeticException("Cannot calculate new angle");
	}

	//This method determines how much the robot should turn, it return the smallest turning angle possible
	public static double calculateShortestTurningAngle(double newAngle, double currentAngle)
	{
		double deltaTheta = newAngle - currentAngle;

		if( Math.abs(deltaTheta) <= Math.PI)
			return deltaTheta;

		if(deltaTheta < -Math.PI)
			return deltaTheta + 2*Math.PI;

		if(deltaTheta > Math.PI)
			return deltaTheta - 2*Math.PI;

		throw new ArithmeticException("Cannot calculate angle error");

	}
	
	public static double calculateAngleError(double deltaX, double deltaY, double theta)
	{
		if(deltaX >= 0 )
			return calculateShortestTurningAngle(Math.atan(deltaY/deltaX), theta);

		if(deltaX< 0 && deltaY >= 0)
			return calculateShortestTurningAngle(Math.atan(deltaY/deltaX)+ Math.PI, theta);

		if(deltaX < 0 && deltaY < 0)
			return calculateShortestTurningAngle(Math.atan(deltaY/deltaX) - Math.PI, theta);

		throw new ArithmeticException("Cannot calculate new angle");
	}

	//Convert radian angle we want into an angle the motor can turn to
	public static int convertAngle(double radius, double width, double angle) {
		return (int) ((180.0 * Math.PI * width * angle / 360.0) / (Math.PI * radius));
	}
}
