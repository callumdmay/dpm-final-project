package ev3Objects;

	/**
	 * Exception that is thrown when there is an obstacle on the coordinate 
	 * the navigator is trying to move to
	 */
public class ObstacleOnCoordinateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ObstacleOnCoordinateException() {
	}

	public ObstacleOnCoordinateException(String message) {
		super(message);
	}


}
