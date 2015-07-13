package de.unibamberg.eesys.projekt;

import de.unibamberg.eesys.projekt.RoadType.ROAD_TYPE;
import de.unibamberg.eesys.projekt.businessobjects.WayPoint;

public class ProactiveFeedback {
	
	// <b>Top speeds. </b>At 110 km/h, driving 20km/h faster consumes 25% for energy.\n\n
	private static String feedbackTopSpeed = "If you drive 20 km/h slower, you will consume 25% less energy.";
	private static String feedbackConstantSpeed = "Driving at constant speed can reduce energy consumption by 20%.";
	
	private RoadType roadType;
	
	public ProactiveFeedback () {
		// use threshold 0 because the entire trip is not being used
		roadType = new RoadType(0);
	}
	
	public String getRecommendation(WayPoint w) {
		
		String recommendation = ""; 
		
		ROAD_TYPE currentRoadType = roadType.updateRoadType(w.getVelocity());
		
		if (currentRoadType == ROAD_TYPE.HIGHWAY) {
		
			// if speed is > 130 km/h 
				recommendation = feedbackTopSpeed;
	
			// if speed variation is > ....  
	//		return feedbackConstantSpeed;	
				
		}
		
		return recommendation;
		
	}

}
