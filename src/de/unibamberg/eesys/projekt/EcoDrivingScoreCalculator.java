package de.unibamberg.eesys.projekt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.widget.Toast;

import de.unibamberg.eesys.projekt.businessobjects.ChargingStation;
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
	
	private AppContext appContext;
	
    EcoDrivingScore[] scores = new EcoDrivingScore[] { 
    		new EcoDrivingScore("Constant speed", 50),
    		new EcoDrivingScore("Moderate acceleration", 50),
    		new EcoDrivingScore("Avoiding high speeds", 50),
    		new EcoDrivingScore("Anticipating stops", 50)    
    };
	
	private int score_constant_speed;  			// calculated using mean variance in speed
	private int score_moderate_acceleration;	// based on average positive acceleration
	private int score_avoiding_high_speeds;		// based on average speed: ToDo: wie nur top Geschwindigkeiten berücksichtigen? (sollte nicht durch häufig Stau/Stadtfahren verringert werden)
	private int score_anticipating_stops;		// based on average negative acceleration

	protected static enum ROAD_TYPE {
		CITY, COUNTRY, MOTORWAY, IGNORE
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
				Toast.makeText(appContext, "Could not load trip waypoints.", Toast.LENGTH_SHORT);
				e.printStackTrace();
			}
		
			for (EcoDrivingScore score : scores ) {
				
				if (score.getTechniqueName().equals("Avoiding high speeds")) {
					// high speed is based on average (top) speed
					
					// toDo: differentiate by road type!
					
					double sumVelocity = 0;  
					for (WayPoint w : trip.getWayPoints()) {
						sumVelocity += w.getVelocity();
					}
					double avgVelocity = sumVelocity / trip.getWayPoints().size();
					L.d("avgVelocity: " + avgVelocity); 
					
					int avgVelocityBad = 160; 
					int avgVelocityOk = 110; 
					
					// calculate speed relative to intervall
					int relativeSpeed = (int) ( (avgVelocityBad - avgVelocity)   / (avgVelocityBad - avgVelocityOk) ) * 100; 
					score.setProgress(make100PercentIntervall((relativeSpeed)));
						
				}
			}
//				score.setProgress(50);	
			
			for (EcoDrivingScore score : scores ) {
				
				if (score.getTechniqueName().equals("Avoiding high speeds"))
					for (WayPoint w : trip.getWayPoints()) {
						w.getVelocity();
						
//						ToDo ... 
					}
				}			
			// Constant speed:
			// todo: if trip does not have mean variance in speed...					
//			if (trip.getMeanVarianceInSpeed == null) ...
			
			// calculate mean variance in speed
				
		
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
		else if (progress > 0)
			return 100;
		else return progress;
	}	
	
}
