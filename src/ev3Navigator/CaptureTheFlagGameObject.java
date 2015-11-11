package ev3Navigator;

public class CaptureTheFlagGameObject {
	
	/**
	 * Creates an object to handle the coordinates received by wifi.
	 */
	private int startingCorner;
	private Coordinate homeBaseCoordinate1, homeBaseCoordinate2;
	private Coordinate opponentBaseCoordinate1, opponentBaseCoordinate2;
	private Coordinate homeFlagDropCoordinate;
	private int homeFlagColour, opponentFlagColour;
	
	/**
	 * Stores an input array of coordinates to be used to navigate to different locations
	 * @param pInputArray The array of coordinates essential for navigating in the grid.
	 */
	public CaptureTheFlagGameObject(int pInputArray[])
	{
		startingCorner = pInputArray[0];
		homeBaseCoordinate1 	= new Coordinate(pInputArray[1], pInputArray[2]);
		homeBaseCoordinate2 	= new Coordinate(pInputArray[3], pInputArray[4]);
		opponentBaseCoordinate1 = new Coordinate(pInputArray[5], pInputArray[6]);
		opponentBaseCoordinate2 = new Coordinate(pInputArray[7], pInputArray[8]);
		homeFlagDropCoordinate	= new Coordinate(pInputArray[9], pInputArray[10]);
		
		homeFlagColour = pInputArray[11];
		opponentFlagColour = pInputArray[12];
		
	}
	/**
	 * Get the corner of the starting position, a number from 1 to 4
	 * @return The starting corner of the square grid
	 */
	public int getStartingCorner() {
		return startingCorner;
	}
	
	/**
	 * Get the coordinate of the bottom left corner of the home base area
	 * @return The coordinate of the bottom left corner of the home base area
	 */
	public Coordinate getHomeBaseCoordinate1() {
		return homeBaseCoordinate1;
	}
	
	/**
	 * Get the coordinate of the top right corner of the home base area
	 * @return The coordinate of the top right corner of the home base area
	 */
	public Coordinate getHomeBaseCoordinate2() {
		return homeBaseCoordinate2;
	}
	
	/**
	 * Get the coordinate of the bottom left corner of the enemy base area
	 * @return The coordinate of the bottom left corner of the enemy base area
	 */
	public Coordinate getOpponentBaseCoordinate1() {
		return opponentBaseCoordinate1;
	}

	/**
	 * Get the coordinate of the top right corner of the enemy base area
	 * @return The coordinate of the top right corner of the enemy base area
	 */
	public Coordinate getOpponentBaseCoordinate2() {
		return opponentBaseCoordinate2;
	}

	/**
	 * Get the coordinate of the bottom left corner of the drop zone
	 * @return The coordinate of the lower left corner of the drop zone
	 */
	public Coordinate getHomeFlagDropCoordinate() {
		return homeFlagDropCoordinate;
	}

	/**
	 * Get the integer representing the home flag color
	 * @return The integer representing the home flag color
	 */
	public int getHomeFlagColour() {
		return homeFlagColour;
	}

	/**
	 * Get the integer representing the enemy flag color
	 * @return The integer representing the enemy flag color
	 */
	public int getOpponentFlagColour() {
		return opponentFlagColour;
	}
	
}
