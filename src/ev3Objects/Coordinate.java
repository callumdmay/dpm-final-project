package ev3Objects;


/**
 * Creates an object that stores x and y coordinates.
 */
public class Coordinate {
	
	private double X;
	private double Y;
	
	/**
	 * Store coordinates in the coordinate object
	 * @param pX The x coordinate
	 * @param pY The y coordinate
	 */
	public Coordinate(double pX, double pY)
	{
		X = pX;
		Y = pY;
	}
	/**
	 * Get the x coordinate
	 * @return The x coordinate
	 */
	public double getX() {
		return X;
	}
	/**
	 * Get the y coordinate
	 * @return The y coordinate
	 */
	public double getY() {
		return Y;
	}
	
	

}
