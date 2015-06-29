package de.unibamberg.eesys.projekt.database;

import java.util.List;
import java.util.Map;

import de.unibamberg.eesys.projekt.businessobjects.*;

/**
 * The DatabaseInterface defines all methods of the DatabaseImplementation.
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public interface DatabaseInterface {
	
	/**
	 * Stores a DriveSequence in the database
	 * (Insert or Update, depending on the driveSeqId)
	 * 
	 * @param driveSequence
	 * @return primary key (id) of newly created DriveSequence or -1 if failed
	 * @throws DatabaseException 
	 */
	public long storeDriveSequence(DriveSequence driveSequence) throws DatabaseException;

	/**
	 * Returns all DriveSequences stored in database
	 * 
	 * @param onlyFinishedDriveSequences (boolean)
	 * @return List of DriveSequences including sub objects (VehicleType) or empty list if nothing found
	 * @throws DatabaseException 
	 */
	public List<DriveSequence> getDriveSequences(boolean onlyFinishedDriveSequences) throws DatabaseException; 

	/**
	 * Stores a ChargeSequence in the database
	 * 
	 * @param chargeSequence
	 * @return primary key (id) of newly created ChargeSequence or -1 if failed
	 * @throws DatabaseException 
	 */
	public long storeChargeSequence(ChargeSequence chargeSequence) throws DatabaseException;

	/**
	 * Returns all ChargeSequences stored in database
	 * (Insert or Update, depending on the chargeSeqId)
	 *  
	 * @return List of ChargeSequences including sub objects (VehicleType) or empty list if nothing found
	 * @throws DatabaseException 
	 */
	public List<ChargeSequence> getChargeSequences() throws DatabaseException; 

	/**
	 * Stores WayPoints in the database
	 * 
	 * @param waypoints (Array)
	 * @throws DatabaseException 
	 */
	public void storeWayPoints(WayPoint[] waypoints) throws DatabaseException;
	
	/**
	 * Returns all WayPoints to a corresponding DriveSequence
	 * 
	 * @param driveSequence
	 * @return List of WayPoints including sub objects (GeoCoordinate) or empty list if nothing found
	 * @throws DatabaseException 
	 */
	public List<WayPoint> getWayPoints(DriveSequence driveSequence) throws DatabaseException;

	/**
	 * Returns all WayPoints in the Database
	 * 
	 * @return List of WayPoints including sub objects (GeoCoordinate) or empty list if nothing found
	 * @throws DatabaseException 
	 */
	public List<WayPoint> getAllWayPoints() throws DatabaseException;

	/**
	 * Returns all ChargingTypes in the database
	 * 
	 * @return List of ChargingTypes or empty list if nothing found
	 * @throws DatabaseException 
	 */
	public List<ChargingType> getChargingTypes() throws DatabaseException;
	
	/**
	 * Stores a ChargingStation in the database
	 * 
	 * @param chargingStation
	 * @return primary key (id) of newly created ChargingStation or -1 if failed
	 * @throws DatabaseException 
	 */
	public long storeChargingStation(ChargingStation chargingStation) throws DatabaseException;

	/**
	 * Returns all ChargingStations in the database
	 * 
	 * @return List of ChargingStation including sub objects (ChargingType, GeoCoordinate)
	 *         or empty list if nothing found
	 * @throws DatabaseException 
	 */
	public List<ChargingStation> getChargingStations() throws DatabaseException;

	/**
	 * Returns all VehicleTypes in the database
	 * 
	 * @return List of VehicleTypes or empty list if nothing found
	 * @throws DatabaseException 
	 */
	public List<VehicleType> getVehicleTypes() throws DatabaseException;
	
	/**
	 * Stores the eCar in the database.
	 * Just one eCar can exist
	 * Insert (if not existing) or Update (if existing).
	 * 
	 * @param Ecar
	 * @throws DatabaseException 
	 */
	public void storeEcar(Ecar eCar) throws DatabaseException ;
	
	/**
	 * Returns the eCar object including VehicleType and Battery
	 * 
	 * @return eCar including sub objects (VehicleType, Battery) or empty eCar if nothing found
	 * @throws DatabaseException 
	 */
	public Ecar getEcar() throws DatabaseException;
	
	/**
	 * - Used for Reporting -
	 * Returns the DriveSequences that were feasible, not feasible or just feasible
	 * with a full charged battery for the current VehicleType (eCar database entry).
	 * Reference point for a full charged battery is the batteryCapacity of the
	 * current VehicleType.
	 * 
	 * @param onlyFinishedDriveSequences (boolean)
	 * @return int array with Feasibilities.
	 *         int[0] = feasible DriveSequences
	 *         int[1] = feasible DriveSequences (for full charged battery - SOC = 100 %)
	 *         int[2] = not feasible DriveSequences
	 *         (all values are 0 if nothing found)
	 * @throws DatabaseException 
	 */
	public int[] getReport_DriveSeqFeasibility(boolean onlyFinishedDriveSequences) throws DatabaseException;
	
	/**
	 * - Used for Reporting -
	 * Returns the startSOCs and endSOCs (in percent) and the corresponding timestamps for all 
	 * DriveSequences for the current VehicleType (eCar database entry).
	 * 
	 * @param Number of DriveSequences (2 SOC values in % per DriveSequence)
	 * @param onlyFinishedDriveSequences (boolean)
	 * @return List of Maps (timestamp, SOC value in %) or empty list if nothing found
	 * @throws DatabaseException 
	 */
	public List<Map<String, Object>> getReport_BatterySOCs(int numberOfDriveSeq, boolean onlyFinishedDriveSequences) throws DatabaseException;
	
	/**
	 * - Used for Reporting -
	 * Returns the average consumption per kilometer of the current month
	 * and the past in total for the current VehicleType (eCar db entry)
	 * 
	 * @param onlyFinishedDriveSequences (boolean)
	 * @return double array 
	 *         double[0] = average consumption (past)
	 *         double[1] = average consumption (current month)
	 *         (all values are 0 if nothing found)
	 * @throws DatabaseException 
	 */
	public double[] getReport_AverageConsumption(boolean onlyFinishedDriveSequences) throws DatabaseException;
	

	/**
	 * - Used for Reporting -
	 * Returns the number of DriveSequences per km-interval (based on all DriveSequences).
	 * The intervals are:
	 *  < 25, 25 - 50, 50 - 100, 100 - 200, 200+ km.
	 * (the lower limit is excluded, the upper limit included!) 
	 * 
	 * @param onlyFinishedDriveSequences (boolean)
	 * @return int[9][2]
	 * First Index: upper interval limit (25, 50, 100, 200, 999)
	 * Second Index: Number of DriveSequences in this interval
	 * (the number of DriveSequences for each intervall are 0 if nothing found)
	 * @throws DatabaseException 
	 */
	public int[][] getReport_DriveSeqDistances(boolean onlyFinishedDriveSequences) throws DatabaseException;

	List<Map<String, Object>> getReport_Speed(
			int numberOfDriveSeq, long driveSequenceId) throws DatabaseException;
}