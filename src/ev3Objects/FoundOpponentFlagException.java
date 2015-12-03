package ev3Objects;

	/**
	 * Thrown whenever the opponent flag is found
	 */
public class FoundOpponentFlagException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public FoundOpponentFlagException() {
	}

	public FoundOpponentFlagException(String message) {
		super(message);
	}
}
