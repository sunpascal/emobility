package de.unibamberg.eesys.projekt.businessobjects;

import java.util.ArrayList;
import java.util.List;

import de.unibamberg.eesys.projekt.RoadType;
import de.unibamberg.eesys.projekt.RoadType.ROAD_TYPE;

public class EcoDrivingStatistics {
	
	private List<WayPoint> wayPoints;

	private double avgVelocityHighway;				// eco-driving: avoid high speeds on the highway
	private double avgVarianceVelocityHighway;		// eco-driving: maintain constant speeds on the highway
	private double avgPosAccelerationCity;			// eco-driving: Moderate acceleration (city)
	private double avgNegAccelerationCity;			// eco-driving: Anticipate traffic (city)
	private double avgPosAccelerationHighway;		// eco-driving: Moderate acceleration (highway)
	private double avgNegAccelerationHighway;		// eco-driving: Anticipate traffic (highway)	
	
	private int countNumberWayPointsHighway = 0;
	private int countNumberWayPointsCity = 0;	
	
	public EcoDrivingStatistics(List<WayPoint> waypoints) {
		this.wayPoints = waypoints;
	}
	
	public void calcEcoDrivingStatistics() {

		double sumVelocityHighway = 0;
		double sumPositiveAccelerationCity = 0;
		double sumNegativeAccelerationCity = 0;
		double sumPositiveAccelerationHighway = 0;
		double sumNegativeAccelerationHighway = 0;		
		
		RoadType roadType = new RoadType();
		ROAD_TYPE currentRoadType = ROAD_TYPE.CITY;	
		
		ArrayList<Double> velocitiesHighway = new ArrayList<Double>(); 		// list of velocities during highway driving, used to calculate velocity variance
		
		for (WayPoint w : wayPoints) {
			
			currentRoadType = roadType.updateRoadType(w.getVelocity());
			
			if (currentRoadType == ROAD_TYPE.HIGHWAY) {
				sumVelocityHighway += w.getVelocity();
				velocitiesHighway.add(w.getVelocity());
				
				if (w.getAcceleration() > 0)
					sumPositiveAccelerationHighway += w.getAcceleration();
				else if (w.getAcceleration() < 0)
					sumNegativeAccelerationHighway += w.getAcceleration();				
				
				countNumberWayPointsHighway++;
			}
			if (currentRoadType == ROAD_TYPE.CITY) {
				
				if (w.getAcceleration() > 0)
					sumPositiveAccelerationCity += w.getAcceleration();
				else if (w.getAcceleration() < 0)
					sumNegativeAccelerationCity += w.getAcceleration();
				
				countNumberWayPointsCity++;
			}
				
		}
		
		avgVelocityHighway = sumVelocityHighway / countNumberWayPointsHighway;
		avgVelocityHighway = sumVelocityHighway / countNumberWayPointsHighway;
		
		avgPosAccelerationCity = sumPositiveAccelerationCity / countNumberWayPointsCity;
		avgNegAccelerationCity = sumNegativeAccelerationCity / countNumberWayPointsCity;
		
		avgPosAccelerationHighway = sumPositiveAccelerationHighway / countNumberWayPointsHighway;
		avgNegAccelerationHighway = sumNegativeAccelerationHighway / countNumberWayPointsHighway;		
				
		// calculate average variance of velocity
		double sum = 0;
		for (double v : velocitiesHighway) {
			sum += Math.abs( v - avgVelocityHighway); 
		}
		
		avgVarianceVelocityHighway = sum / countNumberWayPointsHighway;
		
	}	
	
	public double avgVelocityHighway() {
		return avgVelocityHighway;
	}

	public double getAvgVarianceVelocityHighway() {
		return avgVarianceVelocityHighway;
	}

	public double getAvgPosAccelerationCity() {
		return avgPosAccelerationCity;
	}

	public double getAvgNegAccelationCity() {
		return avgNegAccelerationCity;
	}

	public double getAvgNegAccelerationCity() {
		return avgNegAccelerationCity;
	}	
	
	public double getAvgPosAccelerationHighway() {
		return avgPosAccelerationHighway;
	}

	public double getAvgNegAccelerationHighway() {
		return avgNegAccelerationHighway;
	}	
	
	public int getCountNumberWayPointsHighway() {
		return countNumberWayPointsHighway;
	}

	public int getCountNumberWayPointsCity() {
		return countNumberWayPointsCity;
	}	
	
}
