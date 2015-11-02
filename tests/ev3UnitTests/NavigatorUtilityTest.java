package ev3UnitTests;

import org.junit.Assert;
import org.junit.Test;

import ev3Navigator.NavigatorUtility;

public class NavigatorUtilityTest {

	
	
	@Test
	public void testNewAngleCalculator() {
		Assert.assertEquals(Math.toRadians(45), NavigatorUtility.calculateNewAngle(1, 1),  Math.toRadians(1));
		Assert.assertEquals(Math.toRadians(90), NavigatorUtility.calculateNewAngle(0, 1), Math.toRadians(1));
		Assert.assertEquals(Math.toRadians(315), NavigatorUtility.calculateNewAngle(1, -1), Math.toRadians(1));

	}
	
	@Test
	public void testCalculateShortestTurningAngle()
	{
		Assert.assertEquals(Math.toRadians(0), NavigatorUtility.calculateShortestTurningAngle(Math.toRadians(0), 0), Math.toRadians(1) );
		Assert.assertEquals(Math.toRadians(-45), NavigatorUtility.calculateShortestTurningAngle(Math.toRadians(-45), 0), Math.toRadians(1) );
		Assert.assertEquals(Math.toRadians(-60), NavigatorUtility.calculateShortestTurningAngle(Math.toRadians(-20), Math.toRadians(40)), Math.toRadians(1) );
		Assert.assertEquals(Math.toRadians(-180), NavigatorUtility.calculateShortestTurningAngle(Math.toRadians(190), Math.toRadians(10)), Math.toRadians(1) );
		Assert.assertEquals(Math.toRadians(0), NavigatorUtility.calculateShortestTurningAngle(Math.toRadians(360), Math.toRadians(0)), Math.toRadians(1) );
		Assert.assertEquals(Math.toRadians(-10), NavigatorUtility.calculateShortestTurningAngle(Math.toRadians(360), Math.toRadians(10)), Math.toRadians(1) );
	}
	
	@Test
	public void testCalculateAngleError()
	{
		Assert.assertEquals(Math.toRadians(0), NavigatorUtility.calculateAngleError(0, 0, 0), Math.toRadians(1) );
		Assert.assertEquals(Math.toRadians(0), NavigatorUtility.calculateAngleError(0, 0, Math.toRadians(45)), Math.toRadians(1) );
		Assert.assertEquals(Math.toRadians(45), NavigatorUtility.calculateAngleError(1, 1, 0), Math.toRadians(1) );
		Assert.assertEquals(Math.toRadians(-45), NavigatorUtility.calculateAngleError(1, -1, 0), Math.toRadians(1) );
		Assert.assertEquals(Math.toRadians(180), NavigatorUtility.calculateAngleError(-1, 0, 0), Math.toRadians(1) );
		Assert.assertEquals(Math.toRadians(90), NavigatorUtility.calculateAngleError(0, 500, 0), Math.toRadians(1) );
		Assert.assertEquals(Math.toRadians(-180), NavigatorUtility.calculateAngleError(1, 0, Math.toRadians(180)), Math.toRadians(1) );

	}
}
