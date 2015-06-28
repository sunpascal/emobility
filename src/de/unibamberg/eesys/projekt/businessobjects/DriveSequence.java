package de.unibamberg.eesys.projekt.businessobjects;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.unibamberg.eesys.projekt.L;
import de.unibamberg.eesys.projekt.RoadType;
import de.unibamberg.eesys.projekt.RoadType.ROAD_TYPE;

/**
 * Business object containing a trip 
 * 
 * the properties that a trip has in common with a charging sequence are inherited from Sequence 
 * 
 * @author pascal
 *
 */
public class DriveSequence extends Sequence implements Comparable {
	
	private double coveredDistance;		// in meter!! 
	private double sumCO2;  		
	private List<WayPoint> wayPoints; 
	
	private double avgVelocityHighway;				// eco-driving: avoid high speeds on the highway
	private double avgVarianceVelocityHighway;		// eco-driving: maintain constant speeds on the highway
	private double avgPosAccelerationCity;			// eco-driving: Moderate acceleration (city)
	private double avgNegAccelerationCity;			// eco-driving: Anticipate traffic (city)
	private double avgPosAccelerationHighway;		// eco-driving: Moderate acceleration (highway)
	private double avgNegAccelerationHighway;		// eco-driving: Anticipate traffic (highway)
	
	private boolean hasEcoDrivingStatistics = false; 
	
	public void calcEcoDrivingStatistics() {

		double sumVelocityHighway = 0;
		double sumPositiveAccelerationCity = 0;
		double sumNegativeAccelerationCity = 0;
		double sumPositiveAccelerationHighway = 0;
		double sumNegativeAccelerationHighway = 0;		
		
		int countNumberWayPointsHighway = 0;
		int countNumberWayPointsCity = 0;
		
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
		
		hasEcoDrivingStatistics = true; 
	}	

	public List<WayPoint> getWayPoints() {
		return wayPoints;
	}
	/**
	 * sets all way points of one drive sequence
	 * @param wayPoints = List of all wayPoints of this drive sequence
	 */
	public void setWayPoints(List<WayPoint> wayPoints) {
		this.wayPoints = wayPoints;
	}

	/**
	 * 
	 * @return the total distance of this trip in METER 
	 */
	public double getCoveredDistanceInMeters() {
		return coveredDistance;
	}
	
	/**
	 * 
	 * @return the total distance of this trip in METER 
	 */
	public double getCoveredDistanceInKm() {
		return coveredDistance / 1000;
	}	

	/** 
	 *   
	 * @param coveredDistance the total distance of this trip in km
	 */
	public void setCoveredDistance(double coveredDistance) {
		this.coveredDistance = coveredDistance;
	}
	/**
	 * Calculates Co2 Consumption per Drive Sequence
	 * @param kWh in total of this drive
	 * @param CO2perkWh 
	 * @return CO2 Consumption per Drive Sequence
	 */
	public double calcSumCO2(double kWh, double CO2perkWh) {
		return kWh * CO2perkWh;
	}
	/**
	 * 
	 * @return calculated Co2 Consumption per Drive Sequence
	 */
	public double getSumCO2() {
		return sumCO2;
	}
	/**
	 * Sets Co2 Consumption per Drive Sequence
	 * @param sumCO2
	 */
	public void setSumCO2(double sumCO2) {
		this.sumCO2 = sumCO2;
	}
	/**
	 * Calculates total energy consume in kWh for driveSequence
	 * @return total kWh
	 */
	public double calcSumkWh() {
		double sumkWh = Math.abs(getSocStart() - getSocEnd());
		return sumkWh;
	}
	/**
	 * calculates average KwH per Km
	 * @return average KwH per Km
	 */
	public double calcAveragekWhPerKm() {
		if (coveredDistance == 0) {
			 L.v("calcAveragekWhPerKm(): Cannot calculate - covered distance is 0.");
			 return 0; 
		}

		// kWh divided by km (!)
		return calcSumkWh() / (coveredDistance/1000);
	}
	
	/**
	 * calculates average KwH per 100 Km
	 * @return average KwH per 100 Km
	 */
	public double calcAveragekWhPer100Km() {
		return calcAveragekWhPerKm() * 100;
	}	
	
	/** calculates how far a driver get get with one kWh
	 *  (like "miles per gallon")
	 *  used to calculate personal range
	 * @return
	 */
	private double calcKmPerKwH() {
		if (coveredDistance == 0) {
			 L.e("calcAverageKmPerKw(): Cannot calculate - covered distance is 0.");
			 return 0; 
		}

		// kWh divided by km (!)
		return (coveredDistance/1000) / calcSumkWh() ;
	}
	
	/** calculates how far a driver can get on one battery charge 
	 * @param batterySize capacity of battery in kWh
	 * @return the distance in KM
	 */
	public double calcPersonalRange(double batterySize) {
		return calcKmPerKwH() * batterySize ;
	}	
	
	public String getTimeStartFormatted() {
		
		long timeStart = getTimeStart();
		
		/** Stores the timestamp from the SQL database.*/
		Timestamp timestamp = new Timestamp(timeStart);
		Date date = new Date(timestamp.getTime());		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.M HH:mm");
		
		/** Output of the formated date.*/
		return  dateFormatter.format(date);		
		
	}
	
	/** 
	 * used to display the trips in the log
	 */
	public String toString() {
		
		/** Time during the begin of the drive sequence.*/
		long timeStart = getTimeStart();
		
		/** Stores the timestamp from the SQL database.*/
		Timestamp timestamp = new Timestamp(timeStart);
		Date date = new Date(timestamp.getTime());		
		DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, 2);
		
		/** Output of the formated date.*/
		String showedDate = dateFormatter.format(date);
		
		// Displays the distance of the selected drive sequence.
		double distance = (double) getCoveredDistanceInMeters();	
		
		double consumedKwh;
		if(getSocEnd() < 0){
		consumedKwh = (getSocEnd() * -1)
				+ getSocStart();
		}else{
			consumedKwh =getSocStart() - getSocEnd();
		}					
		
		return  showedDate + " - " + String.format("%.2f",distance/1000) + "km"
				+ " " + String.format("%.2f", consumedKwh) + " kWh";				
	}
	@Override
	public int compareTo(Object another) {
		double tripConsumption = calcSumkWh();
		if (tripConsumption > ((DriveSequence) another).calcSumkWh())
			return 1;
		else if (tripConsumption < ((DriveSequence) another).calcSumkWh())
			return -1; 
		else return 0;
	}

	public boolean hasEcoDrivingStatistics() {
		return hasEcoDrivingStatistics;
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
	
	/**
	 * converts km/h to m/s 
	 * @param kmh
	 * @return
	 */
	public double toMs(double kmh) {
		return kmh / 3.6;
	}
	
	/**
	 * converts m/s to km/h
	 * @param ms
	 * @return
	 */
	public double toKmh(double ms) {
		return ms * 3.6;
	}

	public double getAvgPosAccelerationHighway() {
		return avgPosAccelerationHighway;
	}

	public double getAvgNegAccelerationHighway() {
		return avgNegAccelerationHighway;
	}	
}
