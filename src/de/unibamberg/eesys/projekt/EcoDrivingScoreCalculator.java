package de.unibamberg.eesys.projekt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.widget.Toast;

import de.unibamberg.eesys.projekt.businessobjects.DriveSequence;
import de.unibamberg.eesys.projekt.businessobjects.EcoDrivingScore;
import de.unibamberg.eesys.projekt.businessobjects.WayPoint;
import de.unibamberg.eesys.projekt.database.DatabaseException;

/** 
 * analysis trip data to provide eco-driving scores for various eco-driving techniques
 * @author Pascal
 *
 */
public class EcoDrivingScoreCalculator {
	
	public class Technique {
		public static final String CONSTANT_SPEED_HIGHWAY = "Constant speed on the motorway";
		public static final String POS_ACCELERATION_HIGHWAY = "Low positive acceleration on motorway";
		public static final String AVOID_TOPSPEED__HIGHWAY = "Avoiding top speeds on motorway";
		public static final String ANTICIPATE_STOPS_CITY = "Anticipating stops in the city";
		public static final String MODERATE_ACCELERATION_CITY = "Moderate accelerations in the city";
	}
	
	private AppContext appContext;
	
    EcoDrivingScore[] scores = new EcoDrivingScore[] { 
    		new EcoDrivingScore(Technique.CONSTANT_SPEED_HIGHWAY, 50),
//    		new EcoDrivingScore(Technique.POS_ACCELERATION_HIGHWAY, 50),
    		new EcoDrivingScore(Technique.AVOID_TOPSPEED__HIGHWAY, 50),
    		new EcoDrivingScore(Technique.ANTICIPATE_STOPS_CITY, 50),    
    		new EcoDrivingScore(Technique.MODERATE_ACCELERATION_CITY, 50)
    };
	
	private List<WayPoint> waypoints;
	
	public EcoDrivingScoreCalculator(AppContext appContext) {
		this.appContext = appContext;
	}
	
	/** used to (re-) calculate scores 
	 *  
	 */
	public void calculateScores(DriveSequence trip) {
		
			try {
				trip.setWayPoints(appContext.getDb().getWayPoints(trip));
			} catch (DatabaseException e) {
				Toast.makeText(appContext, "Could not load trip waypoints.", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			
			if (trip.hasEcoDrivingStatistics() == false)
				trip.calcEcoDrivingStatistics();
		
			for (EcoDrivingScore score : scores ) {
				
				if (score.getTechniqueName().equals(Technique.AVOID_TOPSPEED__HIGHWAY)) {
					// high speed is based on average (top) speed
					
					// check if there were any waypoints on the highway 
					if (Double.isNaN(trip.getEcoDrivingStatistics().avgVelocityHighway()) ) {
						score.setVisible(false);
					}
					else {
						double avgVelocityHighway = trip.getEcoDrivingStatistics().avgVelocityHighway();
						double avgVelocityBad = (160/3.6); 
						double avgVelocityOk =  (110/3.6); 
						
						// calculate speed relative to interval
						double percent = ( ( (avgVelocityBad - avgVelocityHighway)   / (avgVelocityBad - avgVelocityOk) ) * 100 ); 
						int percentTrimmed = make100PercentIntervall((int) Math.round(percent));
						score.setProgress(percentTrimmed);
						score.setTechniqueName(score.getTechniqueName() + " " +
								"Ø velocity: " + Math.round(avgVelocityHighway*3.6) + "km/h " +
								percentTrimmed + "%");
					}
				}
				
				else if (score.getTechniqueName().equals(Technique.CONSTANT_SPEED_HIGHWAY)) {
					
					// check if there were any waypoints on the highway 
					if (Double.isNaN(trip.getEcoDrivingStatistics().avgVelocityHighway()) ) {
						score.setVisible(false);
					}
					
					else {
						double userValue = trip.getEcoDrivingStatistics().getAvgVarianceVelocityHighway();
						// Speed oscillations during cruising of 5 km/h increases fuel use  by 30% at 40 km/h and by 20% at 120 km/h (Waters and Laker 1980)
						// lead to an increase in energy consumption by 20% (Waters and Laker (1980))
						double limitBad = 1; 
						double limitOk = 0;	
						double percent = ( (limitBad - userValue)   / Math.abs(limitBad - limitOk) ) * 100; 
						int percentTrimmed = make100PercentIntervall((int) Math.round(percent));
						score.setProgress(percentTrimmed);
						score.setTechniqueName(score.getTechniqueName() + " " + 
								"Ø Velocity variance: " + appContext.round(userValue*3.6) + " km/h " +
								percentTrimmed +"%");
					}
				}	
			
				else if (score.getTechniqueName().equals(Technique.MODERATE_ACCELERATION_CITY)) {
				
				// check if there were any waypoints in the city
				if (trip.getEcoDrivingStatistics().getCountNumberWayPointsCity() == 0 ) {
					score.setVisible(false);
				}
				
				double userValue = trip.getEcoDrivingStatistics().getAvgPosAccelerationCity();
				// An increase in acceleration to 1.765 m/s2 (coresponding to 0-100km/h time of 15 seconds) 
				// Larsson and Ericsson (2009) define "strong accelerations" in their study 
				// as accelerations greater than 1.5 m/s2.				
				double limitBad = 1; 
				double limitOk = 0;	
				double percent = ( (limitBad - userValue)   / Math.abs(limitBad - limitOk) ) * 100; 
				int percentTrimmed = make100PercentIntervall((int) Math.round(percent));
				score.setTechniqueName(score.getTechniqueName() + " " + 
						"Ø Acceleration: " + appContext.round(userValue,2) + " " +
						percentTrimmed +"%");
				score.setProgress(percentTrimmed);
				}
				
				else if (score.getTechniqueName().equals(Technique.ANTICIPATE_STOPS_CITY)) {
					
				// check if there were any waypoints in the city
				if (trip.getEcoDrivingStatistics().getCountNumberWayPointsCity() == 0 ) {
					score.setVisible(false);
				}
					
				double userValue = trip.getEcoDrivingStatistics().getAvgNegAccelerationCity();
				double limitBad = -1; 
				double limitOk = 0;	
				double q1 = userValue - limitBad;
				double q2 = Math.abs(limitBad - limitOk); 
				double inPercent =  (q1/q2)  * 100; 
				int percentCapped = make100PercentIntervall((int) Math.round(inPercent));
				score.setTechniqueName(score.getTechniqueName() + " " + 
						"Ø Braking: " + appContext.round(userValue,2) + "m/s/s " +
						percentCapped + "%");
				
				score.setProgress(percentCapped);
				}				
			}				
			
	}
	

	public EcoDrivingScore[] getScores() {

		// sort by highest score
		Arrays.sort(scores);
		
		// Add "Your are doing well: "  & "You could improve on: "
		scores[0].setDividingText("Your are doing well: ");
		
		for (EcoDrivingScore score : scores ) {
			if (score.getProgress() <= 50) {
				score.setDividingText("You could improve on: ");
				break;
			}
		}
		
		return scores;
	}

	/** ensures that progress never exceed 100% and never drops below 0%
	 *  
	 */
	private int  make100PercentIntervall(int progress) {
		if (progress <0)
			return 0;
		else if (progress > 100)
			return 100;
		else return progress;
	}	
	
	
}
