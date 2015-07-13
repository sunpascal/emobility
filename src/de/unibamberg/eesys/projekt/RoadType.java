package de.unibamberg.eesys.projekt;

public class RoadType {
	
	// road type constants and variables
	private static double MIN_SPEED_HIGHWAY = 100 / 3.6; 	// m/s
	
	// number of waypoints for which speed must exceed min. road type speed
	private int wayPointThreshold = Params.NUMBER_OF_WAYPOINTS_UNTIL_NEW_ROADTYPE;
			
	
	private ROAD_TYPE currentRoadType = ROAD_TYPE.CITY;
	
	public static enum ROAD_TYPE {
		CITY, HIGHWAY
	};	

	/**
	 * overrides threshold defined in Params.NUMBER_OF_WAYPOINTS_UNTIL_NEW_ROADTYPE
	 * @param threshholdWaypoint
	 */
	public RoadType (int threshholdWaypoint) {
		this.wayPointThreshold = threshholdWaypoint;
	}
	
	/**
	 * will use threshold defined in Params.NUMBER_OF_WAYPOINTS_UNTIL_NEW_ROADTYPE
	 */
	public RoadType() {
	}

	public ROAD_TYPE updateRoadType(double velocity) {
		
		// number of waypoints that fullfill requirement for new road type		
		int nThWaypoint = 0; 						
		
		if (currentRoadType == ROAD_TYPE.CITY) {
			if (velocity > MIN_SPEED_HIGHWAY) {
				nThWaypoint++;
			}
			if (nThWaypoint > wayPointThreshold) {
				currentRoadType = ROAD_TYPE.HIGHWAY;
				nThWaypoint = 0;
			}
		}
		// currentRoadType == ROAD_TYPE.HIGHWAY
		// we are on they highway... 
		else  {
			if (velocity <= MIN_SPEED_HIGHWAY) {
				nThWaypoint++;
			}
			if (nThWaypoint > wayPointThreshold) {
				currentRoadType = ROAD_TYPE.CITY;
				nThWaypoint = 0;
			}
		}
		
		
		return currentRoadType; 
	}

}
