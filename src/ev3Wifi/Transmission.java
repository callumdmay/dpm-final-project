/*
* @author Sean Lawlor
* @date November 3, 2011
* @class ECSE 211 - Design Principle and Methods
* 
* Modified by F.P. Ferrie
* February 28, 2014
* Changed parameters for W2014 competition
* 
* Modified by Francois OD
* November 11, 2015
* Changed parameters for F2015 competition
*/
package ev3Wifi;

/*
 * Skeleton class to hold datatypes needed for final project
 * 
 * Simply all public variables so can be accessed with 
 * Transmission t = new Transmission();
 * int d1 = t.d1;
 * 
 * and so on...
 * 
 */

public class Transmission {
	
	public StartCorner startingCorner;
	public int homeZoneBL_X;
	public int homeZoneBL_Y;
	public int homeZoneTR_X;
	public int homeZoneTR_Y;
	public int opponentHomeZoneBL_X;
	public int opponentHomeZoneBL_Y;
	public int opponentHomeZoneTR_X;
	public int opponentHomeZoneTR_Y;
	public int dropZone_X;
	public int dropZone_Y;
	public int flagType;
	public int opponentFlagType;
	

public int[] getTransmissionData()
{
	int[] wifiInputs = new int[13];
	
	wifiInputs[0] = startingCorner.getId();
	wifiInputs[1] = homeZoneBL_X;
	wifiInputs[2] = homeZoneBL_Y;
	wifiInputs[3] = homeZoneTR_X;
	wifiInputs[4] = homeZoneTR_Y;
	wifiInputs[5] = opponentHomeZoneBL_X;
	wifiInputs[6] = opponentHomeZoneBL_Y;
	wifiInputs[7] = opponentHomeZoneTR_X;
	wifiInputs[8] = opponentHomeZoneTR_Y;
	wifiInputs[9] = dropZone_X;
	wifiInputs[10] = dropZone_Y;
	wifiInputs[11] = flagType;
	wifiInputs[12] = opponentFlagType;
	
	return wifiInputs;
}
}


