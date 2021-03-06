package ev3Objects;

import java.util.ArrayList;

import ev3Wifi.Transmission;
import lejos.hardware.Sound;

/**
 * Creates an object to handle the coordinates received by wifi.
 */
public class CaptureTheFlagGameObject {
	private int startingCorner;
	private Coordinate homeBaseCoordinate1, homeBaseCoordinate2;
	private Coordinate startingCoordinate;
	private Coordinate opponentBaseCoordinate_BL, opponentBaseCoordinate_TR, opponentBaseCoordinate_TL, opponentBaseCoordinate_BR, closestOpponentBaseCoordinate;
	private Coordinate homeFlagDropCoordinate;

	private ArrayList<Coordinate> preSearchLocalizationCoordinates; 
	private int homeFlagColour, opponentFlagColour;
	private double startingAngle;

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
		opponentBaseCoordinate_BL = new Coordinate(pInputArray[5]*tileLength, pInputArray[6]*tileLength);
		opponentBaseCoordinate_TR = new Coordinate(pInputArray[7]*tileLength, pInputArray[8]*tileLength);
		opponentBaseCoordinate_TL = new Coordinate(opponentBaseCoordinate_BL.getX(), opponentBaseCoordinate_TR.getY());
		opponentBaseCoordinate_BR = new Coordinate(opponentBaseCoordinate_TR.getX(), opponentBaseCoordinate_BL.getY());
		homeFlagDropCoordinate	= new Coordinate(pInputArray[9]*tileLength, pInputArray[10]*tileLength);

		homeFlagColour = pInputArray[11];
		opponentFlagColour = pInputArray[12];
		
		
		
		processFlagColour(opponentFlagColour);
		determineStartingCoordinate();
		determineStartingAngle();
		determineClosestOpponentBaseCoordinate();
		createPreSearchLocalizationCoordinatesArray();
	}



	/**
	 * Alternative constructor, takes in Transmission object from wifi class, will be used for final game 
	 * @param pT Transmission object from wifi package, contains necessary game parameters
	 */
	
	private void processFlagColour(int opponentFlag)
	{
	switch(opponentFlag)
	{
	case 1:
		opponentFlagColour = 6;
		break;
		
	case 2:
		opponentFlagColour = 0;
		break;
		
	case 3: 
		opponentFlagColour = 3;
		break;
	
	
	case 4:
		opponentFlagColour = 6;
		break;
	
	case 5:
		opponentFlagColour = 2;
		break;
	}	
		
		 
	}
	
	/**
	 * Decode the wifi transmission
	 * @param pT The wifi transmission
	 */
	public CaptureTheFlagGameObject(Transmission pT)
	{
		this( new int[] {pT.startingCorner.getId(), pT.homeZoneBL_X, pT.homeZoneBL_Y, pT.homeZoneTR_X, pT.homeZoneTR_Y, 
				pT.opponentHomeZoneBL_X, pT.opponentHomeZoneBL_Y, pT.opponentHomeZoneTR_X, pT.opponentHomeZoneTR_Y, pT.dropZone_X, pT.dropZone_Y, pT.flagType, pT.opponentFlagType });
	}

	/**
	 * Determines the starting coordinate of the robot based on the input starting corner
	 */
	private void determineStartingCoordinate() {
		switch(startingCorner){

		case 1:
			startingCoordinate = new Coordinate(0,0);
			break;
		case 2:
			startingCoordinate = new Coordinate(10*tileLength,0);
			break;
		case 3:
			startingCoordinate = new Coordinate(10*tileLength,10*tileLength);
			break;
		case 4:
			startingCoordinate = new Coordinate(0,10*tileLength);
			break;
		}
	}

	/**
	 * Determine the closest coordinate of the opponents base that the robot should travel to, 
	 * @return closest opponent base coordinate to robot starting location
	 */
	private void determineClosestOpponentBaseCoordinate() {

		double currentDistance	=1000;
		closestOpponentBaseCoordinate= null;
		for(Coordinate coordinate : new Coordinate[] {opponentBaseCoordinate_BL,opponentBaseCoordinate_TR,opponentBaseCoordinate_TL,opponentBaseCoordinate_BR})
		{
			//added this so the algorithm doesn't consider the coordinates close to the wall (close meaning 1 tile or less away from wall)
			if(coordinate.getX()<1*tileLength ||coordinate.getX()>9*tileLength || coordinate.getY() < 1*tileLength ||coordinate.getY()>9*tileLength)
				continue;

			double deltaX = Math.abs(coordinate.getX() - startingCoordinate.getX());
			double deltaY = Math.abs(coordinate.getY() - startingCoordinate.getY());

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
	 * Determine the starting angle based on the known corner start
	 */
	private void determineStartingAngle()
	{
		switch(startingCorner){
		
		case 1:
			startingAngle = Math.toRadians(0);
			break;
		case 2:
			startingAngle = Math.toRadians(90);
			break;
		case 3:	
			startingAngle = Math.toRadians(180);
			break;
		case 4:
			startingAngle = Math.toRadians(270);
			break;
		}
	}



	/**
	 * Fills the preSearchLocalizationCoordinates Arraylist with a list of coordinates that surround the closestOpponentBaseCoordinate
	 * These coordinates are navigated to by the wall follower to localize before searching begins
	 */
	private void createPreSearchLocalizationCoordinatesArray()
	{
		Coordinate temp0;
		Coordinate temp1;
		Coordinate temp2;
		Coordinate temp3;

		if(closestOpponentBaseCoordinate.equals(opponentBaseCoordinate_BL))
		{
			temp0 = new Coordinate(closestOpponentBaseCoordinate.getX(), closestOpponentBaseCoordinate.getY());
			temp1 = new Coordinate(closestOpponentBaseCoordinate.getX() -tileLength, closestOpponentBaseCoordinate.getY()- tileLength);
			temp2 = new Coordinate(closestOpponentBaseCoordinate.getX() -tileLength, closestOpponentBaseCoordinate.getY());
			temp3 = new Coordinate(closestOpponentBaseCoordinate.getX() , closestOpponentBaseCoordinate.getY()-tileLength);
		
		}

		else if(closestOpponentBaseCoordinate.equals(opponentBaseCoordinate_TL))
		{
			temp0 = new Coordinate(closestOpponentBaseCoordinate.getX(), closestOpponentBaseCoordinate.getY());
			temp1 = new Coordinate(closestOpponentBaseCoordinate.getX() -tileLength, closestOpponentBaseCoordinate.getY()+tileLength);
			temp2 = new Coordinate(closestOpponentBaseCoordinate.getX() -tileLength, closestOpponentBaseCoordinate.getY());
			temp3 = new Coordinate(closestOpponentBaseCoordinate.getX() , closestOpponentBaseCoordinate.getY()+tileLength);
		}

		else if(closestOpponentBaseCoordinate.equals(opponentBaseCoordinate_BR))
		{
			temp0 = new Coordinate(closestOpponentBaseCoordinate.getX(), closestOpponentBaseCoordinate.getY());
			temp1 = new Coordinate(closestOpponentBaseCoordinate.getX() +tileLength, closestOpponentBaseCoordinate.getY()-tileLength);
			temp2 = new Coordinate(closestOpponentBaseCoordinate.getX() +tileLength, closestOpponentBaseCoordinate.getY());
			temp3 = new Coordinate(closestOpponentBaseCoordinate.getX() , closestOpponentBaseCoordinate.getY()-tileLength);
		}

		else if(closestOpponentBaseCoordinate.equals(opponentBaseCoordinate_TR))
		{
			temp0 = new Coordinate(closestOpponentBaseCoordinate.getX(), closestOpponentBaseCoordinate.getY());
			temp1 = new Coordinate(closestOpponentBaseCoordinate.getX() +tileLength, closestOpponentBaseCoordinate.getY()+tileLength);
			temp2 = new Coordinate(closestOpponentBaseCoordinate.getX() +tileLength, closestOpponentBaseCoordinate.getY());
			temp3 = new Coordinate(closestOpponentBaseCoordinate.getX() , closestOpponentBaseCoordinate.getY()+tileLength);
		}
		else
		{
			throw new NullPointerException("closestOpponentBaseCoordinate did not equal any of the opponent base coordinates");
		}

		preSearchLocalizationCoordinates = new ArrayList<Coordinate>();
		preSearchLocalizationCoordinates.add(temp0);
		preSearchLocalizationCoordinates.add(temp1);
		preSearchLocalizationCoordinates.add(temp2);
		preSearchLocalizationCoordinates.add(temp3);

	}

	/**
	 * Get the furthest opponent base coordinate
	 * @return The furthest opponent base coordinate
	 */
	public Coordinate getFurthestOpponentBaseCoordinate()
	{
		for(Coordinate coordinate : new Coordinate[] {opponentBaseCoordinate_BL,opponentBaseCoordinate_TR,opponentBaseCoordinate_TL,opponentBaseCoordinate_BR})
		{
			if(closestOpponentBaseCoordinate.getX() != coordinate.getX() && closestOpponentBaseCoordinate.getY() != coordinate.getY())
				return coordinate;
		}
		
		throw new NullPointerException("Could not find opposite coordinate");
	}

	/**
	 * Get the corner of the starting position, a number from 1 to 4
	 * @return The starting corner of the square grid
	 */
	public int getStartingCorner() {
		return startingCorner;
	}
	
	/**
	 * Get the coordinate to perform light localization before search
	 * @return The coordinate to perform light localization before search
	 */
	public ArrayList<Coordinate> getPreSearchLocalizationCoordinates() {
		return preSearchLocalizationCoordinates;
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
	public Coordinate getOpponentBaseCoordinate_BL() {
		return opponentBaseCoordinate_BL;
	}

	/**
	 * Get the coordinate of the top right corner of the enemy base area
	 * @return The coordinate of the top right corner of the enemy base area
	 */
	public Coordinate getOpponentBaseCoordinate_TR() {
		return opponentBaseCoordinate_TR;
	}

	/**
	 * Get the coordinate of the top left corner of the enemy base area
	 * @return The coordinate of the top left corner of the enemy base area
	 */
	public Coordinate getOpponentBaseCoordinate_TL() {
		return opponentBaseCoordinate_TL;
	}

	/**
	 * Get the coordinate of the bottom right corner of the enemy base area
	 * @return The coordinate of the bottom right corner of the enemy base area
	 */
	public Coordinate getOpponentBaseCoordinate_BR() {
		return opponentBaseCoordinate_BR;
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

	/**
	 * Get the starting angle of the EV3 before the run
	 * @return The starting angle of the EV3 before the run
	 */
	public double getStartingAngle() {
		return startingAngle;
	}

}
