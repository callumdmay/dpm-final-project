package ev3Navigator;

/**
 * This class contains utility/math methods that are used by the navigator class
 * These methods were refactored out of the Navigator class to reduce the size of the
 * navigator class and increase readability
 * These methods are only dependent on their parameters, hence why they are static.
 */
public class NavigatorUtility {


	/**
	 * This method calculates the new angle the robot must face, based on the delta Y and delta X
	 * @param deltaX The change in x
	 * @param deltaY The change in y
	 * @return
	 */
	public static double calculateNewAngle(double deltaX, double deltaY)
	{

		double newAngle = 0;
		
		if(deltaX >= 0 )
				newAngle = Math.atan(deltaY/deltaX);

		if(deltaX< 0 && deltaY >= 0)
			newAngle = Math.atan(deltaY/deltaX)+ Math.PI;

		if(deltaX < 0 && deltaY < 0)
			newAngle = Math.atan(deltaY/deltaX) - Math.PI;

		if(newAngle <0)
			newAngle += Math.toRadians(360);
		
		return newAngle;
		
	}
	/**
	 * This method determines how much the robot should turn, 
	 * it return the smallest turning angle possible
	 * @param newAngle The angle to turn to
	 * @param currentAngle The current angle
	 * @return
	 */
	public static double calculateShortestTurningAngle(double newAngle, double currentAngle)
	{
		double deltaTheta = newAngle - currentAngle;

		if( Math.abs(deltaTheta) <= Math.PI)
			return deltaTheta;

		if(deltaTheta < -Math.PI)
			return deltaTheta + 2*Math.PI;

		if(deltaTheta > Math.PI)
			return deltaTheta - 2*Math.PI;

		throw new ArithmeticException("Cannot calculate shortest turning angle");

	}
	/**
	 * Calculate the angle error
	 * @param deltaX The change in x
	 * @param deltaY The change in y
	 * @param theta The angle
	 * @return
	 */
	public static double calculateAngleError(double deltaX, double deltaY, double theta)
	{
		//cannot divide by zero
		if(deltaX == 0 && deltaY == 0)
			return 0;
		
		if(deltaX >= 0 )
			return calculateShortestTurningAngle(Math.atan(deltaY/deltaX), theta);

		if(deltaX< 0 && deltaY >= 0)
			return calculateShortestTurningAngle(Math.atan(deltaY/deltaX)+ Math.PI, theta);

		if(deltaX < 0 && deltaY < 0)
			return calculateShortestTurningAngle(Math.atan(deltaY/deltaX) - Math.PI, theta);

		throw new ArithmeticException("Cannot calculate angle error");
	}
	/**
	 * Convert radian angle we want into an angle the motor can turn to
	 * @param radius The wheel radius
	 * @param width The axle length
	 * @param angle The angle we want to turn
	 * @return
	 */
	public static int convertAngle(double radius, double width, double angle) {
		return (int) ((180.0 * Math.PI * width * angle / 360.0) / (Math.PI * radius));
	}
}
