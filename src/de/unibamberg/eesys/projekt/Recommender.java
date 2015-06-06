package de.unibamberg.eesys.projekt;

import java.util.Arrays;
import java.util.List;

import de.unibamberg.eesys.projekt.businessobjects.DriveSequence;
import de.unibamberg.eesys.projekt.businessobjects.VehicleType;
import de.unibamberg.eesys.projekt.database.DatabaseException;

public class Recommender {
	private VehicleType[] vehicleList;
	private DriveSequence[] trips;

	public Recommender(AppContext appContext) {
		List<VehicleType> vehicles = null;
		try {
			vehicles = appContext.getDb().getVehicleTypes();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		vehicleList = vehicles.toArray(new VehicleType[vehicles.size()]);
		Arrays.sort(vehicleList);  

		List<DriveSequence> tripsList = null;
		try {
			tripsList = appContext.getDb().getDriveSequences(true);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		trips = tripsList.toArray(new DriveSequence[tripsList.size()]);
		Arrays.sort(trips);  
		
	}

	public double calcBatterySize100PercentOfTrips() throws Exception {
		if (trips == null) {
			throw new Exception("Recommendation not possible because there are no trips");
		}
		DriveSequence tripWithHighestConsumption = trips[trips.length-1];
		double consumptionOfLongestTrip = tripWithHighestConsumption.calcSumkWh();  
		return consumptionOfLongestTrip;
	}
	
	public double calcBatterySize95PercentOfTrips() throws Exception {
		if (trips == null) {
			throw new Exception("Recommendation not possible because there are no trips");
		}
		int nthTrip =  Math.round(0.90f * trips.length);
		DriveSequence tripWithHighestConsumption = trips[nthTrip-1];
		double consumptionOfNthTrip = tripWithHighestConsumption.calcSumkWh();  
		return consumptionOfNthTrip;		
	}
	
	public VehicleType getRecommendation100PercentOfTrips() throws Exception {
		double requiredBatterySize = calcBatterySize100PercentOfTrips();
		
		// check vehicle list for the first vehicle with given battery size
		for (VehicleType v : vehicleList) {
			if (v.getBatteryCapacity() >= requiredBatterySize)
				return v;
		}
		return null; 
	}
	
	public VehicleType getRecommendation95PercentOfTrips() throws Exception {
		double requiredBatterySize = calcBatterySize95PercentOfTrips();
		
		// check vehicle list for the first vehicle with given battery size
		for (VehicleType v : vehicleList) {
			if (v.getBatteryCapacity() >= requiredBatterySize)
				return v;
		}
		return null; 
	}	

}
