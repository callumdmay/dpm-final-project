package ev3Objects;

public class FoundOpponentFlagException extends RuntimeException{

	/**
	 * Thrown whenever the opponent flag is found
	 */
	private static final long serialVersionUID = 1L;

	public FoundOpponentFlagException() {
	}

	public FoundOpponentFlagException(String message) {
		super(message);
	}
}
