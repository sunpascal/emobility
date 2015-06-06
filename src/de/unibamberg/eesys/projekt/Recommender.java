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
		vehicleList = (VehicleType[]) vehicles.toArray();
		Arrays.sort(vehicleList);  // todo: nach was wird hier sortiert? ==> sollte nach battery size sein!

		List<DriveSequence> tripsList = null;
		try {
			tripsList = appContext.getDb().getDriveSequences(true);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		trips = (DriveSequence[]) tripsList.toArray();
		Arrays.sort(trips);  // todo: sollte nach trip consumption sein!
		
	}

	public double calcBatterySize100PercentOfTrips() {
		DriveSequence tripWithHighestConsumption = trips[trips.length];
		double consumptionOfLongestTrip = tripWithHighestConsumption.calcSumkWh();  
		return consumptionOfLongestTrip;
	}
	
	public double calcBatterySize80PercentOfTrips() {
		int nthTrip =  Math.round(0.8f * trips.length);
		DriveSequence tripWithHighestConsumption = trips[nthTrip];
		double consumptionOfNthTrip = tripWithHighestConsumption.calcSumkWh();  
		return consumptionOfNthTrip;		
	}
	
	public VehicleType getRecommendation100PercentOfTrips() {
		double requiredBatterySize = calcBatterySize100PercentOfTrips();
		
		// check vehicle list for the first vehicle with given battery size
		for (VehicleType v : vehicleList) {
			if (v.getBatteryCapacity() >= requiredBatterySize)
				return v;
		}
		return null; 
	}
	
	public VehicleType getRecommendation80PercentOfTrips() {
		double requiredBatterySize = calcBatterySize80PercentOfTrips();
		
		// check vehicle list for the first vehicle with given battery size
		for (VehicleType v : vehicleList) {
			if (v.getBatteryCapacity() >= requiredBatterySize)
				return v;
		}
		return null; 
	}	

}
