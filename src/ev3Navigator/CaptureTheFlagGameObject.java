package ev3Navigator;

public class CaptureTheFlagGameObject {
	
	private int startingCorner;
	private Coordinate homeBaseCoordinate1, homeBaseCoordinate2;
	private Coordinate opponentBaseCoordinate1, opponentBaseCoordinate2;
	private Coordinate homeFlagDropCoordinate;
	private int homeFlagColour, opponentFlagColour;
	
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

	public int getStartingCorner() {
		return startingCorner;
	}

	public Coordinate getHomeBaseCoordinate1() {
		return homeBaseCoordinate1;
	}

	public Coordinate getHomeBaseCoordinate2() {
		return homeBaseCoordinate2;
	}

	public Coordinate getOpponentBaseCoordinate1() {
		return opponentBaseCoordinate1;
	}

	public Coordinate getOpponentBaseCoordinate2() {
		return opponentBaseCoordinate2;
	}

	public Coordinate getHomeFlagDropCoordinate() {
		return homeFlagDropCoordinate;
	}

	public int getHomeFlagColour() {
		return homeFlagColour;
	}

	public int getOpponentFlagColour() {
		return opponentFlagColour;
	}
	
	

}
