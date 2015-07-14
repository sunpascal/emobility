package de.unibamberg.eesys.projekt;

import java.util.ArrayList;
import java.util.List;

import de.unibamberg.eesys.projekt.RoadType.ROAD_TYPE;
import de.unibamberg.eesys.projekt.businessobjects.EcoDrivingStatistics;
import de.unibamberg.eesys.projekt.businessobjects.WayPoint;

public class ProactiveFeedback {
	
	// <b>Top speeds. </b>At 110 km/h, driving 20km/h faster consumes 25% for energy.\n\n
	private static String feedbackTopSpeed = "If you drive 20 km/h slower, you will consume 25% less energy.";
	
	// Driving at constant speed can reduce energy consumption by 20%.";
	private static String feedbackConstantSpeed = "Try to maintain a constant speed to use 5% less energy";
	
	private RoadType roadType;
	
	private List<WayPoint> last10Waypoints = null; 
	
	public ProactiveFeedback () {
		// use threshold 0 because the entire trip is not being used
		roadType = new RoadType(0);
	}
	
	public String getRecommendation(WayPoint w) {
		
		String recommendation = "";
		
		if (last10Waypoints == null)
			last10Waypoints = new ArrayList<WayPoint>();
		
		if (last10Waypoints.size() < 30) {
			last10Waypoints.add(w);
		}
		else {
			// if there are 11 waypoints, then make sure there are only 10 waypoints
			last10Waypoints.remove(0); // remove oldest waypoint
			last10Waypoints.add(w);
		}
		
		// Todo: make sure there enough waypoints to make sensible comparissons
		// especially variance will be great if there are too few waypoints
//		if (last10Waypoints.size() > 0) { 
		
			ROAD_TYPE currentRoadType = roadType.updateRoadType(w.getVelocity());
			
			if (currentRoadType == ROAD_TYPE.HIGHWAY) {
				
				// ToDo: get list with last 10 waypoints 
				EcoDrivingStatistics stats = new EcoDrivingStatistics(last10Waypoints);
				stats.calcEcoDrivingStatistics();
				
				// if speed is > 140 km/h
				// show top speed feedback...
				if (stats.avgVelocityHighway() > (140/3.6) ) {
					// ToDo: make percentage dynamic 
					recommendation = feedbackTopSpeed;
				}
	
				// if speed variation is > ....
				// show maintain constant speed feedback
				 // Todo: check why AvgVarianceVelocityHighway is always 0!!!
				else if (stats.getAvgVarianceVelocityHighway() > (0)) {
					recommendation = feedbackConstantSpeed;			
				}
					
			}
		
		return recommendation;
		
	}

}
