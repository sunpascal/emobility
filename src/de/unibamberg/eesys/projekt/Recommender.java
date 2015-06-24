package de.unibamberg.eesys.projekt;

import java.util.Arrays;
import java.util.List;

import de.unibamberg.eesys.projekt.businessobjects.DriveSequence;
import de.unibamberg.eesys.projekt.businessobjects.VehicleType;
import de.unibamberg.eesys.projekt.database.DatabaseException;

/** Analysis trip data to provide electric vehicle recommendation
 * 
 * @author Pascal
 *
 */
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

	public double calcBatterySize100PercentOfTrips() throws NoTripsException {
		if (trips == null) {
			throw new NoTripsException("Recommendation not possible because there are no trips");
		}
		DriveSequence tripWithHighestConsumption = trips[trips.length-1];
		double consumptionOfLongestTrip = tripWithHighestConsumption.calcSumkWh();  
		return consumptionOfLongestTrip;
	}
	
	public double calcBatterySize80PercentOfTrips() throws NoTripsException {
		if (trips == null) {
			throw new NoTripsException("Recommendation not possible because there are no trips");
		}
		int nthTrip =  Math.round(0.90f * trips.length);
		DriveSequence tripWithHighestConsumption = trips[nthTrip-1];
		double consumptionOfNthTrip = tripWithHighestConsumption.calcSumkWh();  
		return consumptionOfNthTrip;		
	}
	
	public VehicleType getRecommendation100PercentOfTrips() throws NoTripsException {
		double requiredBatterySize = calcBatterySize100PercentOfTrips();
		
		// check vehicle list for the first vehicle with given battery size
		// the vehicle list is sorted from smallest to largest!
		for (VehicleType v : vehicleList) {
			if (v.getBatteryCapacity() >= requiredBatterySize)
				return v;
		}
		return null; 
	}
	
	/** 
	 * gives an alternative vehicle to originalVehicle
	 * @return
	 */
	public VehicleType getAlternativeRecommendationWithTripAdaptation(VehicleType originalVehicle) throws NoTripsException {
		
		// check vehicle list for the first vehicle that has a smaller battery size than the original vehicle
		// go backwards through list, since list is sorted from smallest to largest!
		for (int i=vehicleList.length-1; i>0; i--) {
			VehicleType v = vehicleList[i];
			if (v.getBatteryCapacity() < originalVehicle.getBatteryCapacity()) {
				// determine how many trips have to done using another vehicles in order for this car
				// to be possible 
					int nAdaptations =getNumberOfTripsExceedingEnergy(v.getBatteryCapacity());
					v.setnTripAdaptationsrequired(nAdaptations);
				return v;
			}
				
		}
		return null; 
	}
	
	/**
	 * returns the number of trips that cannot be completed using the battery capacity <i>energy</i> 
	 * @param energy
	 * @return
	 */
	private int getNumberOfTripsExceedingEnergy(double energy) {
		int n = 0;
		if (trips != null) {
			for (DriveSequence trip : trips) {
				if (trip.calcSumkWh() > energy)
					n++;
			}
		}
		return n;
	}
	
	public VehicleType getRecommendation80PercentofTrips() throws NoTripsException {
		double requiredBatterySize = calcBatterySize80PercentOfTrips();
		
		// check vehicle list for the first vehicle with given battery size
		for (VehicleType v : vehicleList) {
			if (v.getBatteryCapacity() >= requiredBatterySize)
				return v;
		}
		return null; 
	}

	public VehicleType getAlternativeRecommendationWithEcoDrivingAdaptation(
			VehicleType originalVehicle) {
		
		// check vehicle list for the first vehicle that has a smaller battery size than the original vehicle
		// Todo: check if we have to go backwards
		for (int i=vehicleList.length-1; i>0; i--) {
			VehicleType v = vehicleList[i];
			if (v.getBatteryCapacity() < originalVehicle.getBatteryCapacity()) {
				// determine how many trips have to done using another vehicles in order for this car
				// to be possible 
					double targetCapacity = v.getBatteryCapacity();
					double currentConsumption = originalVehicle.getBatteryCapacity();
					double percentConsumptionReduction  = ((currentConsumption-targetCapacity)/currentConsumption)*100;
					v.setPercentConsumptionReductionRequired( "" + Math.round(percentConsumptionReduction));
				return v;
			}
		}
		return null;
	}		

}
