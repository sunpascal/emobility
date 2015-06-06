package de.unibamberg.eesys.projekt.businessobjects;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import de.unibamberg.eesys.projekt.L;

/**
 * Business object containing a trip 
 * 
 * the properties that a trip has in common with a charging sequence are inherited from Sequence 
 * 
 * @author pascal, matthias
 *
 */
public class DriveSequence extends Sequence implements Comparable {

	private double coveredDistance;		// in meter!! 
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
			 L.e("calcAveragekWhPerKm(): Cannot calculate - covered distance is 0.");
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
}
