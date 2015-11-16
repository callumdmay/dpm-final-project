package ev3Objects;

public class ObstacleOnCoordinateException extends RuntimeException {


	/**
	 * Exception that is thrown when there is an obstacle on the coordinate 
	 * the navigator is trying to move to
	 */
	private static final long serialVersionUID = 1L;

	public ObstacleOnCoordinateException() {
	}

	public ObstacleOnCoordinateException(String message) {
		super(message);
	}


}
