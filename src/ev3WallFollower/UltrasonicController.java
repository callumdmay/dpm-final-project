package ev3WallFollower;


public interface UltrasonicController {
	
	public void processUSData(int leftUltraSonicDistance , int rightUltraSonicDistance);
	
	public int readUSDistance();
	
}
