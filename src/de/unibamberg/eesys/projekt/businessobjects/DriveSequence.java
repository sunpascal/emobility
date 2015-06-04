package de.unibamberg.eesys.projekt.businessobjects;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Business object containing a trip 
 * 
 * the properties that a trip has in common with a charging sequence are inherited from Sequence 
 * 
 * @author pascal, matthias
 *
 */
public class DriveSequence extends Sequence {

	private double coveredDistance;		// in km
	private double sumCO2;  		
	private List<WayPoint> wayPoints; 

	public List<WayPoint> getWayPoints() {
		return wayPoints;
	}
	/**
	 * sets all way points of one drive sequece
	 * @param wayPoints = List of all wayPoints of this drive sequence
	 */
	public void setWayPoints(List<WayPoint> wayPoints) {
		this.wayPoints = wayPoints;
	}

	/**
	 * 
	 * @return the total distance of this trip in km 
	 */
	public double getCoveredDistance() {
		return coveredDistance;
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
	 * Calculates total kWh for driveSequence
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
		if (coveredDistance != 0) {
			return this.calcSumkWh() / (coveredDistance * 1000);
		} else {
			// L.d("calcAveragekWhPerKm(): Cannot calculate - covered distance is 0.");
			return -1;
		}

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
		double distance = (double) getCoveredDistance();	
		
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
}
