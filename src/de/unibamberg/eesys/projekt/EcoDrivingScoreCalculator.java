package de.unibamberg.eesys.projekt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    		new EcoDrivingScore("Constant speed", 10),
    		new EcoDrivingScore("Moderate acceleration", 80),
    		new EcoDrivingScore("Avoiding high speeds", 90),
    		new EcoDrivingScore("Anticipating stops", 55)    
    };
	
	private int score_constant_speed;  			// calculated using mean variance in speed
	private int score_moderate_acceleration;	// based on average positive acceleration
	private int score_avoiding_high_speeds;		// based on average speed: ToDo: wie nur top Geschwindigkeiten berücksichtigen? (sollte nicht durch häufig Stau/Stadtfahren verringert werden)
	private int score_anticipating_stops;		// based on average negative acceleration

	private List<WayPoint> waypoints;
	
	public EcoDrivingScoreCalculator(AppContext appContext) {
		this.appContext = appContext;
	}
	
	/** used to (re-) calculate scores 
	 *  
	 */
	public void calculateScores() {
		
		List<DriveSequence> trips;
		try {
			trips = appContext.getDb().getDriveSequences(true);

			for (EcoDrivingScore score : scores ) {
				
				// Constant speed:
				for (DriveSequence trip : trips) {
					// todo: if trip does not have mean variance in speed...					
//					if (trip.getMeanVarianceInSpeed == null) ...
					
					// calculate mean variance in speed
					trip.setWayPoints(appContext.getDb().getWayPoints(trip));
					
					for (WayPoint w : trip.getWayPoints()) {
						w.getVelocity();
						
//						ToDo ... 
					}
				}
//				score.setProgress(50);			
				
			}
			
			
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

}
