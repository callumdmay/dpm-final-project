package ev3WallFollower;


public interface UltrasonicController {
	
	public void processUSData(int distance);
	
	public int readUSDistance();
	
	public void reverse();
}
