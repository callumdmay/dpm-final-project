package ev3Objects;

public class MissedObjectException extends RuntimeException{
	

	private static final long serialVersionUID = 3953926484313394173L;

	/**
	 * Thrown whenever we miss the object we are trying to investigate
	 */

	public MissedObjectException() {
	}

	public MissedObjectException(String message) {
		super(message);
	}

}
