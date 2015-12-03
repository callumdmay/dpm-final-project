/*
* @author Sean Lawlor
* @date November 3, 2011
* @class ECSE 211 - Design Principle and Methods
* 
* Modified by F.P. Ferrie
* February 28, 2014
* Changed parameters for W2014 competition

*/
package ev3Wifi;

public enum StartCorner {
	BOTTOM_LEFT(1,0,0, "BL"),
	BOTTOM_RIGHT(2,300,0, "BR"),
	TOP_RIGHT(3,300,300, "TR"),
	TOP_LEFT(4,0,300, "TL"),
	NULL(0,0,0, "NULL");
	
	private int id, x, y;
	private String name;
	
	/**
	 * Returns the start corner
	 * @param id The corner ID
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param name The name
	 */
	private StartCorner(int id, int x, int y, String name) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.name = name;
	}
	
	/**
	 * Returns the name
	 */
	public String toString() {
		return this.name;
	}
	
	/**
	 * Get the x and y coordinates
	 * @return An int array holding the x and y coordinates
	 */
	public int[] getCooridinates() {
		return new int[] {this.x, this.y};
	}
	
	/**
	 * Get the X coordinate
	 * @return The X coordinate
	 */
	public int getX() {
		return this.x;
	}
	
	/**
	 * Get the Y coordinate
	 * @return The Y coordinate
	 */
	public int getY() {
		return this.y;
	}
	
	/**
	 * Get the cornerID
	 * @return The cornerID
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Computes the starting corner for the game
	 * @param cornerId The corner ID from the transmission
	 * @return The starting corner for the game
	 */
	public static StartCorner lookupCorner(int cornerId) {
		for (StartCorner corner : StartCorner.values())
			if (corner.id == cornerId)
				return corner;
		return NULL;
	}
}
