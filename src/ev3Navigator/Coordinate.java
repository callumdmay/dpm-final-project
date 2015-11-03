package ev3Navigator;


/**
 * Creates creates an object that stores x and y coordinates.
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
	 * @return the x coordinate
	 */
	public double getX() {
		return X;
	}
	/**
	 * Get the y coordinate
	 * @return the y coordinate
	 */
	public double getY() {
		return Y;
	}
	
	

}
