package ev3Navigator;

import java.util.LinkedList;
import java.util.Queue;

import ev3Objects.Coordinate;
import lejos.hardware.Sound;

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
	 * Algorithm that generates simple search coordinates in a rectangular space defined by 2 coordinate points
	 * @param startPoint First coordinate that defines the rectangle
	 * @param endPoint Second coordinate that defines the rectangle
	 * @return A queue of coordinates that define a search pattern for the rectangular space given
	 */

	public static Queue<Coordinate> generateSimpleSearchCoordinateQueue(
			Coordinate startPoint, Coordinate endPoint) {

		Queue<Coordinate> searchCoordinateQueue = new LinkedList<Coordinate>();


		if(Math.abs(endPoint.getY() - startPoint.getY())>= Math.abs(endPoint.getX() - startPoint.getX())){

			double deltaX = endPoint.getX() - startPoint.getX();
			double deltaY = endPoint.getY() - startPoint.getY();

			int coordinateCount =1;
			boolean useStartPoint = true;
			for(int count = 1; count < 5; count++){

				double xMultiplier = count;
				Coordinate coordinate;
				if(count % 2 ==0)
					xMultiplier = count -1;

				if(useStartPoint)
				{
					coordinate = new Coordinate(startPoint.getX() +deltaX * (xMultiplier/4), startPoint.getY() + deltaY/9);
				}
				else
				{
					coordinate = new Coordinate(startPoint.getX() +deltaX * (xMultiplier/4), endPoint.getY() - deltaY/9);
				}

				coordinateCount++;

				if(coordinateCount > 1)
				{
					coordinateCount = 0;
					useStartPoint =! useStartPoint;
				}
				searchCoordinateQueue.add(coordinate);
			}

		}
		else
		{
			double deltaX = endPoint.getX() - startPoint.getX();
			double deltaY = endPoint.getY() - startPoint.getY();

			int coordinateCount =1;
			boolean useStartPoint = true;
			for(int count = 1; count < 5; count++){

				double yMultiplier = count;
				Coordinate coordinate;
				if(count % 2 ==0)
					yMultiplier = count - 1;

				if(useStartPoint)
				{
					coordinate = new Coordinate(startPoint.getX() + deltaX/9, startPoint.getY() + deltaY * (yMultiplier/4));
				}
				else
				{
					coordinate = new Coordinate(endPoint.getX() - deltaY/9, startPoint.getY() + deltaY * (yMultiplier/4));
				}

				coordinateCount++;

				if(coordinateCount > 1)
				{
					coordinateCount = 0;
					useStartPoint =! useStartPoint;
				}
				searchCoordinateQueue.add(coordinate);
			}
		}
		return searchCoordinateQueue;

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
	 * Calculates average of two angles with wrap around
	 * @param angle1 first angle
	 * @param angle2 second angle
	 * @return average of two angles
	 */
	public static double calculateAngleAverage(double angle1, double angle2)
	{
		double deltaAngle = Math.abs(angle1 - angle2);
		
		if (deltaAngle < 180)
			return ((angle1 + angle2) / 2) %360;
		else if (deltaAngle != 180)
			return (((angle1 + angle2) / 2) + 180) %360;
		else
			throw new ArithmeticException("Could not calculate average of two angles");
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

	/**
	 * Convert physical distance we want into a parameter for the motor
	 * @param radius The wheel radius
	 * @param distance The desired physical distance
	 * @return
	 */
	public static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}




}
