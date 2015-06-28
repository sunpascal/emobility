package de.unibamberg.eesys.projekt;

public class RoadType {
	
	// road type constants and variables
	private static double MIN_SPEED_HIGHWAY = 100/3.6; 	// m/s
	private static int wayPointBuffer = 30;				// number of waypoints for which speed must exceed min. road type speed
	
	private ROAD_TYPE currentRoadType = ROAD_TYPE.CITY;
	
	public static enum ROAD_TYPE {
		CITY, HIGHWAY
	};	

	public ROAD_TYPE updateRoadType(double velocity) {
		int nThWaypoint = 0; 						// number of waypoints that fullfill requirment for new road type
		
		if (currentRoadType == ROAD_TYPE.CITY) {
			if (velocity > MIN_SPEED_HIGHWAY) {
				nThWaypoint++;
			}
			if (nThWaypoint > wayPointBuffer)
				currentRoadType = ROAD_TYPE.HIGHWAY;
		}
		// currentRoadType == ROAD_TYPE.HIGHWAY
		else  {
			if (velocity <= MIN_SPEED_HIGHWAY) {
				nThWaypoint++;
			}
			if (nThWaypoint > wayPointBuffer)
				currentRoadType = ROAD_TYPE.CITY;
		}
		
		return currentRoadType; 
	}

}
