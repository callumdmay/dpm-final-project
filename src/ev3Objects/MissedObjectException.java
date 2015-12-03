package ev3Objects;

/**
 * Thrown whenever we miss the object we are trying to investigate
 */
public class MissedObjectException extends RuntimeException{
	

	private static final long serialVersionUID = 3953926484313394173L;

	public MissedObjectException() {
	}

	public MissedObjectException(String message) {
		super(message);
	}

}
