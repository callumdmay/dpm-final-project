package ev3Objects;

import ev3Navigator.Coordinate;
import ev3Wifi.Transmission;

/**
 * Creates an object to handle the coordinates received by wifi.
 */
public class CaptureTheFlagGameObject {
	private int startingCorner;
	private Coordinate homeBaseCoordinate1, homeBaseCoordinate2;
	private Coordinate startingCoordinate;
	private Coordinate opponentBaseCoordinate1, opponentBaseCoordinate2, opponentBaseCoordinate3, opponentBaseCoordinate4, closestOpponentBaseCoordinate;
	private Coordinate homeFlagDropCoordinate;
	private int homeFlagColour, opponentFlagColour;
	private static final double tileLength = 30.48;


	/**
	 * Stores an input array of coordinates to be used to navigate to different locations
	 * Also determines the robots starting location and the closest opponent base coordinate to the 
	 * robots starting position
	 * @param pInputArray The array of coordinates essential for navigating in the grid.
	 */
	public CaptureTheFlagGameObject(int pInputArray[])
	{
		startingCorner = pInputArray[0];

		homeBaseCoordinate1 	= new Coordinate(pInputArray[1] * tileLength, pInputArray[2]*tileLength);
		homeBaseCoordinate2 	= new Coordinate(pInputArray[3]* tileLength, pInputArray[4]*tileLength);
		opponentBaseCoordinate1 = new Coordinate(pInputArray[5]*tileLength, pInputArray[6]*tileLength);
		opponentBaseCoordinate2 = new Coordinate(pInputArray[7]*tileLength, pInputArray[8]*tileLength);
		opponentBaseCoordinate3 = new Coordinate(opponentBaseCoordinate1.getX(), opponentBaseCoordinate2.getY());
		opponentBaseCoordinate4 = new Coordinate(opponentBaseCoordinate2.getX(), opponentBaseCoordinate1.getY());
		homeFlagDropCoordinate	= new Coordinate(pInputArray[9]*tileLength, pInputArray[10]*tileLength);

		homeFlagColour = pInputArray[11];
		opponentFlagColour = pInputArray[12];


		
		switch(startingCorner){

		case 1:
			startingCoordinate = new Coordinate(0,0);
		case 2:
			startingCoordinate = new Coordinate(10*tileLength,0);
		case 3:
			startingCoordinate = new Coordinate(10*tileLength,10*tileLength);
		case 4:
			startingCoordinate = new Coordinate(10*tileLength,10*tileLength);

		}

		determineClosestOpponentBaseCoordinate();

	}
	
	public CaptureTheFlagGameObject(Transmission pT)
	{
		this( new int[] {pT.startingCorner.getId(), pT.homeZoneBL_X, pT.homeZoneBL_Y, pT.homeZoneTR_X, pT.homeZoneTR_Y, 
				pT.opponentHomeZoneBL_X, pT.opponentHomeZoneBL_Y, pT.opponentHomeZoneTR_X, pT.opponentHomeZoneTR_Y, pT.dropZone_X, pT.dropZone_Y, pT.flagType, pT.opponentFlagType });
	}

	/**
	 * Determine the closest coordinate of the opponents base that the robot should travel to, 
	 * @return closest opponent base coordinate to robot starting location
	 */
	private void determineClosestOpponentBaseCoordinate() {

		double currentDistance	=1000;
		closestOpponentBaseCoordinate= null;
		for(Coordinate coordinate : new Coordinate[] {opponentBaseCoordinate1,opponentBaseCoordinate2,opponentBaseCoordinate3,opponentBaseCoordinate4})
		{
			//added this so the algorithm doesn't consider the coordinates against the wall
			if(coordinate.getX()<0 ||coordinate.getX()>10*tileLength || coordinate.getY() < 0 ||coordinate.getY()>10*tileLength)
				continue;

			double deltaX = Math.abs(startingCoordinate.getX() - coordinate.getX());
			double deltaY = Math.abs(startingCoordinate.getY() - coordinate.getY());

			double distance = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
			if(distance < currentDistance){
				currentDistance = distance;
				closestOpponentBaseCoordinate = coordinate;
			}
		}

		if(closestOpponentBaseCoordinate == null)
			throw new NullPointerException("Could not determine closest opponent base coordinate");

	}
	/**
	 * Get the corner of the starting position, a number from 1 to 4
	 * @return The starting corner of the square grid
	 */
	public int getStartingCorner() {
		return startingCorner;
	}


	/**
	 *  Get the robots starting coordinates
	 * @return the starting coordinate of the robot
	 */
	public Coordinate getStartingCoordinate() {
		return startingCoordinate;
	}

	/**
	 * Get the closest opponent base coordinate
	 * @return the closest opponent base coordinate
	 */
	public Coordinate getClosestOpponentBaseCoordinate() {
		return closestOpponentBaseCoordinate;
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
	 * Get the coordinate of the top left corner of the enemy base area
	 * @return The coordinate of the top left corner of the enemy base area
	 */
	public Coordinate getOpponentBaseCoordinate3() {
		return opponentBaseCoordinate3;
	}

	/**
	 * Get the coordinate of the bottom right corner of the enemy base area
	 * @return The coordinate of the bottom right corner of the enemy base area
	 */
	public Coordinate getOpponentBaseCoordinate4() {
		return opponentBaseCoordinate4;
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
