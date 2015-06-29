package de.unibamberg.eesys.projekt.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;
import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.businessobjects.Battery;
import de.unibamberg.eesys.projekt.businessobjects.ChargeSequence;
import de.unibamberg.eesys.projekt.businessobjects.ChargingStation;
import de.unibamberg.eesys.projekt.businessobjects.ChargingType;
import de.unibamberg.eesys.projekt.businessobjects.DriveSequence;
import de.unibamberg.eesys.projekt.businessobjects.Ecar;
import de.unibamberg.eesys.projekt.businessobjects.GeoCoordinate;
import de.unibamberg.eesys.projekt.businessobjects.Sequence.SequenceType;
import de.unibamberg.eesys.projekt.businessobjects.VehicleType;
import de.unibamberg.eesys.projekt.businessobjects.WayPoint;

/**
 * Implementation of the DatabaseInterface. 
 * The DatabaseImplementation is a class to abstract SQL related functions
 * and allows to process application transactions instead of SQL methods.
 * It is instantiated in the application context and provides all database
 * related functions for the application.
 * 
 * - For method comments please see DatabaseInterface -
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class DBImplementation implements DatabaseInterface {
	SQLiteDatabase db;
	DBInitializer dbInit;
	AppContext appContext;

	/***
	 * Constructor - 
	 * 		Call the DBInitializer to create a writable DB
	 * 
	 * @param ctx
	 * 
     * @throws SQLException
     *      if the database could be neither opened nor created
  	 */
	public DBImplementation(AppContext appContext) throws SQLException{
		dbInit = new DBInitializer(appContext);
		db = dbInit.open().db;
		this.appContext = appContext;
	}
	
	/***
	 * "Destructor" - 
	 * 		Close the DB before destroying the DBImplementation object
	 * 
	 * @throws Throwable 
	 * 		everything that could went wrong - needed for the finalize statement
	 */
	protected void finalize() throws Throwable
	{
		if(db != null) 
		{
			dbInit.close();
		}
		super.finalize();  
	}

	@Override
	public void storeWayPoints(WayPoint[] waypoints) throws DatabaseException{

		//working variables
		long wayPointId = -1;
		long geoId = -1;
		
		//store all WayPoints in DB
		for (WayPoint wayPoint : waypoints) {
			DBAdapter_WayPoint dbWayPoint = new DBAdapter_WayPoint(db);
			DBAdapter_GeoCoordinate dbGeo = new DBAdapter_GeoCoordinate(db);

			geoId = dbGeo.createGeoCoordinate(
					wayPoint.getGeoCoordinate().getLongitude(),
					wayPoint.getGeoCoordinate().getLatitude(),
					wayPoint.getGeoCoordinate().getAltitude()
			);
			
			//if GeoCoordinate could not be created, abort storing the WayPoint
			if (geoId > 0) {
				wayPointId = dbWayPoint.createWayPoint(
						wayPoint.getDriveSequence().getId(),
						geoId,
						wayPoint.getVelocity(),
						wayPoint.getAcceleration(),
						wayPoint.getTimestamp()
				);
			}
			else {
				throw new DatabaseException("DBImplementation; storeWayPoints; Could not create GeoCoordinate. Creation of corresponding WayPoint aborted. WayPoint ID: " + wayPoint.getId());
			}
			if (wayPointId < 1) {
				throw new DatabaseException("DBImplementation; storeWayPoints; Could not create WayPoint " + wayPoint.getId());
			}
		}
	}
	
	@Override
	public List<WayPoint> getWayPoints(DriveSequence driveSequence) throws DatabaseException{
		// working variables
		List<WayPoint> wayPoints = new ArrayList<WayPoint>();
		WayPoint wayPoint;
		GeoCoordinate geoCoord;
		Cursor cWayPoints;
        
        //DBAdapters
		DBAdapter_WayPoint dbWayPoint = new DBAdapter_WayPoint(db);

		//get all WayPoints for the corresponding drive sequence
		cWayPoints = dbWayPoint.getWayPointsByDriveSeq(driveSequence.getId());
		
		if(cWayPoints != null) {
			if (cWayPoints.moveToFirst()) {
				do {
					geoCoord = new GeoCoordinate();
					wayPoint = new WayPoint();
					
					//WayPoint variables
					wayPoint.setId(cWayPoints.getLong(0)); 				//0 = _id (int)
					wayPoint.setDriveSequence(driveSequence); 			//1 = driveSequence_ID (int)
					wayPoint.setVelocity(cWayPoints.getDouble(3)); 		//3 = velocity (real)
					wayPoint.setAcceleration(cWayPoints.getDouble(4));	//4 = acceleration (real)
					wayPoint.setTimestamp(cWayPoints.getLong(5));		//5 = timestamp (int)
					
					//Determine corresponding GeoCorrdinate
					geoCoord.setId(cWayPoints.getLong(6));				//6 = _id (int)
					geoCoord.setLongitude(cWayPoints.getDouble(7));		//7 = longitude (real)
					geoCoord.setLatitude(cWayPoints.getDouble(8));		//8 = latitude (real)
					geoCoord.setAltitude(cWayPoints.getDouble(9)); 		//9 = altitude (real)
					wayPoint.setGeoCoordinate(geoCoord);
	
					wayPoints.add(wayPoint);
				} while(cWayPoints.moveToNext());
			}
//			else {
//				cWayPoints.close();
//				throw new DatabaseException("DBImplementation; getWayPoints; Could not find WayPoints for this DriveSequence in DB");
//			}
			cWayPoints.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getWayPoints; Could not receive valid cursor.");
		}
		
		//empty if nothing found
		return wayPoints;
	}

	@Override
	public List<WayPoint> getAllWayPoints() throws DatabaseException{
		// working variables
		List<WayPoint> wayPoints = new ArrayList<WayPoint>();
		WayPoint wayPoint;
		GeoCoordinate geoCoord;
		DriveSequence driveSequence;
		VehicleType vehicleType;
		boolean active;
		
		Cursor cWayPoints;
        
        //DBAdapters
		DBAdapter_WayPoint dbWayPoint = new DBAdapter_WayPoint(db);

		//get all WayPoints for the corresponding drive sequence
		cWayPoints = dbWayPoint.getAllWayPoints();
		
		if(cWayPoints != null) {
			if (cWayPoints.moveToFirst()) {
				do {
					geoCoord = new GeoCoordinate();
					wayPoint = new WayPoint();
					vehicleType = new VehicleType();
					driveSequence = new DriveSequence();
					
					//VehicleType variables
					vehicleType.setId(cWayPoints.getLong(20)); 						//20 = _id (int)
					vehicleType.setName(cWayPoints.getString(21));					//21 = name (text)
					vehicleType.setDescription(cWayPoints.getString(22));			//22 = description (text)
					vehicleType.setBatteryCapacity(cWayPoints.getDouble(23));		//23 = batteryCapacity (real)
					vehicleType.setEnergyConsumption_perKM(cWayPoints.getDouble(24));//24 = energyConsumption_perKM (real)
					vehicleType.setRecuperationEfficiency(cWayPoints.getDouble(25));//25 = recuperationEfficiency (real)
					vehicleType.setMass(cWayPoints.getDouble(26));					//26 = mass (real)
					vehicleType.setFrontArea(cWayPoints.getDouble(27));				//27 = frontArea (real)
					vehicleType.setPrice(cWayPoints.getString(28));
					
					//DriveSequence variables
					active = (cWayPoints.getInt(17)==1) ? true : false;
					driveSequence.setId(cWayPoints.getLong(10));				//10 = _id (int)
					driveSequence.setSequenceType(SequenceType.values()[cWayPoints.getInt(11)-1]);//11 = sequenceType_ID (int)
					driveSequence.setVehicleType(vehicleType);					//12 = vehicleType_ID (int)
					driveSequence.setSocStart(cWayPoints.getDouble(13));		//13 = socStart (real)
					driveSequence.setSocEnd(cWayPoints.getDouble(14));			//14 = socEnd (real)
					driveSequence.setTimeStart(cWayPoints.getLong(15));			//15 = timeStart (int)
					driveSequence.setTimeStop(cWayPoints.getLong(16));			//16 = timeStop (int)
					driveSequence.setActive(active);							//17 = active (int)
					driveSequence.setCoveredDistance(cWayPoints.getDouble(18));	//18 = coveredDistance (real)
					driveSequence.setSumCO2(cWayPoints.getDouble(19));			//19 = sumCO2 (real)

					//Determine corresponding GeoCorrdinate
					geoCoord.setId(cWayPoints.getLong(6));				//6 = _id (int)
					geoCoord.setLongitude(cWayPoints.getDouble(7));		//7 = longitude (real)
					geoCoord.setLatitude(cWayPoints.getDouble(8));		//8 = latitude (real)
					geoCoord.setAltitude(cWayPoints.getDouble(9)); 		//9 = altitude (real)
					
					//WayPoint variables
					wayPoint.setId(cWayPoints.getLong(0)); 				//0 = _id (int)
					wayPoint.setDriveSequence(driveSequence); 			//1 = driveSequence_ID (int)
					wayPoint.setGeoCoordinate(geoCoord); 				//2 = geo_ID (int)
					wayPoint.setVelocity(cWayPoints.getDouble(3)); 		//3 = velocity (real)
					wayPoint.setAcceleration(cWayPoints.getDouble(4));	//4 = acceleration (real)
					wayPoint.setTimestamp(cWayPoints.getLong(5));		//5 = timestamp (int)
					
					wayPoints.add(wayPoint);
				} while(cWayPoints.moveToNext());
			}
//			else {
//				cWayPoints.close();
//				throw new DatabaseException("DBImplementation; getAllWayPoints; Could not find WayPoints in DB");
//			}
			cWayPoints.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getAllWayPoints; Could not receive valid cursor.");
		}
		//empty if nothing found
		return wayPoints;
	}

	@Override
	public long storeDriveSequence(DriveSequence driveSequence) throws DatabaseException{
		//working variables
		long dsReturn = -1;
		int active = driveSequence.isActive() ? 1 : 0;
		boolean updateNecessary = false;
		boolean driveSeqUpdated = false;
		Cursor cDriveSeq = null;
		
		//DBAdapter
		DBAdapter_DriveSequence dbDriveSequence = new DBAdapter_DriveSequence(db);
		
		//check whether the DriveSequenceId is set AND there is an existing database record
		//if true --> update
		if(driveSequence.getId()>0){
			cDriveSeq = dbDriveSequence.getDriveSequence(driveSequence.getId());
			if(cDriveSeq != null) {
				if (cDriveSeq.moveToFirst()) {
					updateNecessary = true;
					cDriveSeq.close();
				}
			}
		}
		
		//do update or create
		if (updateNecessary){
			driveSeqUpdated = dbDriveSequence.updateDriveSequence(
					driveSequence.getId(),
					driveSequence.getSequenceType().ordinal()+1,
					driveSequence.getVehicleType().getId(),
					driveSequence.getSocStart(),
					driveSequence.getSocEnd(),
					driveSequence.getTimeStart(),
					driveSequence.getTimeStop(),
					active,
					driveSequence.getCoveredDistanceInMeters(),
					driveSequence.getSumCO2()
					);
			
			//set DriveSeqenceId or -1 if update failed
			dsReturn = driveSeqUpdated ? driveSequence.getId() : -1;
		}
		else {
			dsReturn = dbDriveSequence.createDriveSequence(
					driveSequence.getSequenceType().ordinal()+1,
					driveSequence.getVehicleType().getId(),
					driveSequence.getSocStart(),
					driveSequence.getSocEnd(),
					driveSequence.getTimeStart(),
					driveSequence.getTimeStop(),
					active,
					driveSequence.getCoveredDistanceInMeters(),
					driveSequence.getSumCO2()
					);
		}
		
		if (dsReturn < 1) {
			throw new DatabaseException("DBImplementation; storeDriveSequence; Could not create or update DriveSequence " + driveSequence.getId());
		}

		//rowId or -1 if fails
		return dsReturn;
	}
	
	@Override
	public List<DriveSequence> getDriveSequences(boolean onlyFinishedDriveSequences) throws DatabaseException{
		
		// working variables
		List<DriveSequence> driveSequences = new ArrayList<DriveSequence>();
		DriveSequence driveSequence;
		VehicleType vehicleType;
		Cursor cDriveSeq;
		boolean active;
		
        //DBAdapters
		DBAdapter_DriveSequence dbDriveSeq = new DBAdapter_DriveSequence(db);

		//get all DriveSequences
		if(onlyFinishedDriveSequences) {
			cDriveSeq = dbDriveSeq.getAllDriveSequences(true);
		}
		else {
			cDriveSeq = dbDriveSeq.getAllDriveSequences(false);
		}
		
		if(cDriveSeq != null) {
			if (cDriveSeq.moveToFirst()) {
				do {
					driveSequence = new DriveSequence();
					vehicleType = new VehicleType();
					
					//VehicleType variables
					vehicleType.setId(cDriveSeq.getLong(10)); 						//10 = _id (int)
					vehicleType.setName(cDriveSeq.getString(11));					//11 = name (text)
					vehicleType.setDescription(cDriveSeq.getString(12));			//12 = description (text)
					vehicleType.setBatteryCapacity(cDriveSeq.getDouble(13));		//13 = batteryCapacity (real)
					vehicleType.setEnergyConsumption_perKM(cDriveSeq.getDouble(14));//14 = energyConsumption_perKM (real)
					vehicleType.setRecuperationEfficiency(cDriveSeq.getDouble(15));	//15 = recuperationEfficiency (real)
					vehicleType.setMass(cDriveSeq.getDouble(16));					//16 = mass (real)
					vehicleType.setFrontArea(cDriveSeq.getDouble(17));				//17 = frontArea (real)
	
					//DriveSequence variables
					active = (cDriveSeq.getInt(7)==1) ? true : false;
					driveSequence.setId(cDriveSeq.getLong(0));				//0 = _id (int)
					driveSequence.setSequenceType(SequenceType.values()[cDriveSeq.getInt(1)-1]);//1 = sequenceType_ID (int)
					driveSequence.setVehicleType(vehicleType);				//2 = vehicleType_ID (int)
					driveSequence.setSocStart(cDriveSeq.getDouble(3));		//3 = socStart (real)
					driveSequence.setSocEnd(cDriveSeq.getDouble(4));		//4 = socEnd (real)
					driveSequence.setTimeStart(cDriveSeq.getLong(5));		//5 = timeStart (int)
					driveSequence.setTimeStop(cDriveSeq.getLong(6));			//6 = timeStop (int)
					driveSequence.setActive(active);						//7 = active (int)
					driveSequence.setCoveredDistance(cDriveSeq.getDouble(8));//8 = coveredDistance (real)
					driveSequence.setSumCO2(cDriveSeq.getDouble(9));		//9 = sumCO2 (real)
	
					driveSequences.add(driveSequence);
				} while(cDriveSeq.moveToNext());
			}
//			else {
//				cDriveSeq.close();
//				throw new DatabaseException("DBImplementation; getDriveSequences; Could not find DriveSequences in DB");
//			}
			cDriveSeq.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getDriveSequences; Could not receive valid cursor.");
		}
		//empty if nothing found
		return driveSequences;
	}


	@Override
	public long storeChargeSequence(ChargeSequence chargeSequence) throws DatabaseException{
		//working variables
		long csReturn = -1;
		int active = chargeSequence.isActive() ? 1 : 0;
		boolean updateNecessary = false;
		boolean chargeSeqUpdated = false;
		Cursor cChargeSeq;
		
		//DBAdapter
		DBAdapter_ChargeSequence dbChargeSequence = new DBAdapter_ChargeSequence(db);
		
		//check whether the ChargeSequenceId is set AND there is an existing database record
		//if true --> update
		if(chargeSequence.getId()>0){
			cChargeSeq = dbChargeSequence.getChargeSequence(chargeSequence.getId());
			if(cChargeSeq != null) {
				if (cChargeSeq.moveToFirst()) {
					updateNecessary = true;
					cChargeSeq.close();
				}
			}
		}
		
		//do update or create
		if (updateNecessary){
			chargeSeqUpdated = dbChargeSequence.updateChargeSequence(
					chargeSequence.getId(),
					chargeSequence.getSequenceType().ordinal()+1,
					chargeSequence.getVehicleType().getId(),
					chargeSequence.getSocStart(),
					chargeSequence.getSocEnd(),
					chargeSequence.getTimeStart(),
					chargeSequence.getTimeStop(),
					active,
					chargeSequence.getChargingType().getId()
					);
			
			//set ChargeSeqenceId or -1 if update failed
			csReturn = chargeSeqUpdated ? chargeSequence.getId() : -1;
		}
		else{
			csReturn = dbChargeSequence.createChargeSequence(
					chargeSequence.getSequenceType().ordinal()+1,
					chargeSequence.getVehicleType().getId(),
					chargeSequence.getSocStart(),
					chargeSequence.getSocEnd(),
					chargeSequence.getTimeStart(),
					chargeSequence.getTimeStop(),
					active,
					chargeSequence.getChargingType().getId()
					);
		}

		if (csReturn < 1) {
			throw new DatabaseException("DBImplementation; storeChargeSequence; Could not create ChargeSequence " + chargeSequence.getId());
		}
		
		//rowId or -1 if fails
		return csReturn;
	}
	
	@Override
	public List<ChargeSequence> getChargeSequences() throws DatabaseException{
		
		// working variables
		List<ChargeSequence> chargeSequences = new ArrayList<ChargeSequence>();
		ChargeSequence chargeSequence;
		VehicleType vehicleType;
		ChargingType chargingType;
		Cursor cChargeSeq;
		boolean active;
		
        //DBAdapters
		DBAdapter_ChargeSequence dbChargeSeq = new DBAdapter_ChargeSequence(db);

		//get all ChargeSequences
		cChargeSeq = dbChargeSeq.getAllChargeSequences();
		
		if(cChargeSeq != null) {
			if (cChargeSeq.moveToFirst()) {
				do {
					chargeSequence = new ChargeSequence();
					chargingType = new ChargingType();
					vehicleType = new VehicleType();
					
					//VehicleType variables
					vehicleType.setId(cChargeSeq.getLong(9)); 						//9 = _id (int)
					vehicleType.setName(cChargeSeq.getString(10));					//10 = name (text)
					vehicleType.setDescription(cChargeSeq.getString(11));			//11 = description (text)
					vehicleType.setBatteryCapacity(cChargeSeq.getDouble(12));		//12 = batteryCapacity (real)
					vehicleType.setEnergyConsumption_perKM(cChargeSeq.getDouble(13));//13 = energyConsumption_perKM (real)
					vehicleType.setRecuperationEfficiency(cChargeSeq.getDouble(14));//14 = recuperationEfficiency (real)
					vehicleType.setMass(cChargeSeq.getDouble(15));					//15 = mass (real)
					vehicleType.setFrontArea(cChargeSeq.getDouble(16));				//16 = frontArea (real)

					//ChargingType variables
					chargingType.setId(cChargeSeq.getLong(17));						//17 = _id (int) 
					chargingType.setName(cChargeSeq.getString(18));					//18 = name (text)
					chargingType.setDescription(cChargeSeq.getString(19));			//19 = description (text)
					chargingType.setChargingPower(cChargeSeq.getDouble(20));		//20 = chargingPower (real)
					
					//ChargeSequence variables
					active = (cChargeSeq.getInt(7)==1) ? true : false;
					chargeSequence.setId(cChargeSeq.getLong(0));				//0 = _id (int)
					chargeSequence.setSequenceType(SequenceType.values()[cChargeSeq.getInt(1)-1]);//1 = sequenceType_ID (int)
					chargeSequence.setVehicleType(vehicleType);					//2 = vehicleType_ID (int)
					chargeSequence.setSocStart(cChargeSeq.getDouble(3));		//3 = socStart (real)
					chargeSequence.setSocEnd(cChargeSeq.getDouble(4));			//4 = socEnd (real)
					chargeSequence.setTimeStart(cChargeSeq.getLong(5));			//5 = timeStart (int)
					chargeSequence.setTimeStop(cChargeSeq.getLong(6));			//6 = timeStop (int)
					chargeSequence.setActive(active);							//7 = active (int)
					chargeSequence.setChargingType(chargingType);				//8 = chargingType (int)
	
					chargeSequences.add(chargeSequence);
				} while(cChargeSeq.moveToNext());
			}
//			else {
//				cChargeSeq.close();
//				throw new DatabaseException("DBImplementation; getChargeSequences; Could not find ChargeSequences in DB");
//			}
			cChargeSeq.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getChargeSequences; Could not receive valid cursor.");
		}
		//empty if nothing found
		return chargeSequences;
	}

	@Override
	public List<ChargingType> getChargingTypes() throws DatabaseException{
		//working variables
		Cursor cChargingType;
		ChargingType chargingType;
		List<ChargingType> chargingTypes = new ArrayList<ChargingType>();
		
		//DBAdapter
		DBAdapter_ChargingType dbChargingType = new DBAdapter_ChargingType(db);
		
		//retrieve all ChargingTypes in DB
		cChargingType = dbChargingType.getAllChargingTypes(); 
		
		if(cChargingType != null) {
			if (cChargingType.moveToFirst()) {
				do {
					chargingType = new ChargingType();
					
					chargingType.setId(cChargingType.getLong(0));				//0 = _id (int)
					chargingType.setName(cChargingType.getString(1));			//1 = name (text)
					chargingType.setDescription(cChargingType.getString(2));	//2 = description (text)
					chargingType.setChargingPower(cChargingType.getDouble(3));	//3 = chargingPower (real)
	
					chargingTypes.add(chargingType);
				} while(cChargingType.moveToNext());
			}
//			else {
//				cChargingType.close();
//				throw new DatabaseException("DBImplementation; getChargingTypes; Could not find ChargingTypes in DB");
//			}
			cChargingType.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getChargingTypes; Could not receive valid cursor.");
		}
		//empty if nothing found
		return chargingTypes;
	}

	@Override
	public List<ChargingStation> getChargingStations() throws DatabaseException{
		// working variables
		List<ChargingStation> chargingStations = new ArrayList<ChargingStation>();
		ChargingStation chargingStation;
		ChargingType chargingType;
		GeoCoordinate geoCoordinate;

		Cursor cChargingStation;
		
        //DBAdapters
		DBAdapter_ChargingStation dbChargingStation = new DBAdapter_ChargingStation(db);

		//get all DriveSequences
		cChargingStation = dbChargingStation.getAllChargingStations();
		
		if(cChargingStation != null) {
			if (cChargingStation.moveToFirst()) {
				do {
					chargingStation = new ChargingStation();
					chargingType = new ChargingType();
					geoCoordinate = new GeoCoordinate();
					
					//ChargingType variables
					chargingType.setId(cChargingStation.getLong(5));				//5 = _id (int)
					chargingType.setName(cChargingStation.getString(6));			//6 = name (text)
					chargingType.setDescription(cChargingStation.getString(7));		//7 = description (text)
					chargingType.setChargingPower(cChargingStation.getDouble(8));	//8 = chargingPower (real)
					
					//GeoCoordinate variables
					geoCoordinate.setId(cChargingStation.getLong(9));				//9 = _id (int)
					geoCoordinate.setLongitude(cChargingStation.getDouble(10));		//10 = longitude (real)
					geoCoordinate.setLatitude(cChargingStation.getDouble(11));		//11 = latitude (real)
					geoCoordinate.setAltitude(cChargingStation.getDouble(12));		//12 = altitude (real)
					
					//ChargingStation variables
					chargingStation.setId(cChargingStation.getLong(0));				//0 = _id (int)
					chargingStation.setChargingType(chargingType);					//1 = chargingType_ID (int)
					chargingStation.setGeoCoordinate(geoCoordinate);				//2 = geo_ID (int)
					chargingStation.setName(cChargingStation.getString(3));			//3 = name (text)
					chargingStation.setDescription(cChargingStation.getString(4));	//4 = description (text)
					
					chargingStations.add(chargingStation);
				} while(cChargingStation.moveToNext());
			}
//			else {
//				cChargingStation.close();
//				throw new DatabaseException("DBImplementation; getChargingStations; Could not find ChargingStations in DB");
//			}
			cChargingStation.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getChargingStations; Could not receive valid cursor.");
		}
		//empty if nothing found
		return chargingStations;
	}

	@Override
	public long storeChargingStation(ChargingStation chargingStation) throws DatabaseException{
		//working variables
		long chargingStationId = -1;
		long geoId;
		
		//DBAdapter
		DBAdapter_ChargingStation dbCS = new DBAdapter_ChargingStation(db);
		DBAdapter_GeoCoordinate dbGeo = new DBAdapter_GeoCoordinate(db);
		
		geoId = dbGeo.createGeoCoordinate(
				chargingStation.getGeoCoordinate().getLatitude(),
				chargingStation.getGeoCoordinate().getLongitude(),
				chargingStation.getGeoCoordinate().getAltitude()
		);
		
		//if GeoCoordinate could not be created, abort storing the ChargingStation
		if (geoId > 0) {
			chargingStationId = dbCS.createChargingStation(
					chargingStation.getChargingType().getId(), 
					geoId, 
					chargingStation.getName(), 
					chargingStation.getDescription()
			);
			if (chargingStationId < 1) {
				throw new DatabaseException("DBImplementation; storeChargingStation; Could not create ChargingStation. ChargingStation: " + chargingStation.getId());
			}
		}
		else {
			throw new DatabaseException("DBImplementation; storeChargingStation; Could not create GeoCoordinate. Creation of corresponding ChargingStation aborted. ChargingStation: " + chargingStation.getId());
		}
		//rowId  or -1 if fails
		return chargingStationId;
	}

	@Override
	public List<VehicleType> getVehicleTypes() throws DatabaseException{
		//working variables
		Cursor cVehicleType;
		VehicleType vehicleType;
		List<VehicleType> vehicleTypes = new ArrayList<VehicleType>();
		
		//DBAdapter
		DBAdapter_VehicleType dbVehicleType = new DBAdapter_VehicleType(db);
		
		//retrieve all VehicleTypes in DB
		cVehicleType = dbVehicleType.getAllVehicleTypes(); 
		
		if(cVehicleType != null) {
			if (cVehicleType.moveToFirst()) {
				do {
					vehicleType = new VehicleType();
					
					vehicleType.setId(cVehicleType.getLong(0));							//0 = _id (int)
					vehicleType.setName(cVehicleType.getString(1));						//1 = name (text)
					vehicleType.setDescription(cVehicleType.getString(2));				//2 = description (text)
					vehicleType.setBatteryCapacity(cVehicleType.getDouble(3));			//3 = batteryCapacity (real)
					vehicleType.setEnergyConsumption_perKM(cVehicleType.getDouble(4));	//4 = energyConsumption_perKM (real)
					vehicleType.setRecuperationEfficiency(cVehicleType.getDouble(5));	//5 = recuperationEfficiency (real)
					vehicleType.setMass(cVehicleType.getDouble(6));						//6 = mass (real)
					vehicleType.setFrontArea(cVehicleType.getDouble(7));				//7 = frontArea (real)
					vehicleType.setPrice(cVehicleType.getString(8));					//8 = price
	
					vehicleTypes.add(vehicleType);
				} while(cVehicleType.moveToNext());
			}
//			else {
//				cVehicleType.close();
//				throw new DatabaseException("DBImplementation; getVehicleTypes; Could not find VehicleTypes in DB");
//			}
			cVehicleType.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getVehicleTypes; Could not receive valid cursor.");
		}
		//empty if nothing found
		return vehicleTypes;
	}

	@Override
	public void storeEcar(Ecar eCar) throws DatabaseException{
		//working variables
		long batteryId;
		long eCarId;
		int charging = eCar.getBattery().isCharging() ? 1 : 0;;
		
		//DBAdapter
		DBAdapter_eCar dbEcar = new DBAdapter_eCar(db);
		DBAdapter_Battery dbBattery = new DBAdapter_Battery(db);
		
		batteryId = dbBattery.replaceBattery(eCar.getBattery().getCurrentSoc(), charging);
		
		//if Battery could not be created/updated, abort storing the eCar
		if (batteryId > 0) {
			eCarId = dbEcar.replaceECar(
					eCar.getVehicleType().getId(), 
					batteryId, 
					eCar.getName(), 
					eCar.getDescription()
			);
			if (eCarId < 1) {
				throw new DatabaseException("DBImplementation; storeEcar; Could not create or update eCar.");
			}
		}
		else {
			throw new DatabaseException("DBImplementation; storeEcar; Could not create or update Battery. Creation/Update of corresponding eCar aborted.");
		}
	}

	@Override
	public Ecar getEcar() throws DatabaseException{
		// working variables
		Ecar eCar = new Ecar(appContext);
		Battery battery = new Battery();
		VehicleType vehicleType = new VehicleType();
		boolean charging;

		Cursor cEcar;
		
        //DBAdapters
		DBAdapter_eCar dbEcar = new DBAdapter_eCar(db);

		//get the eCar
		cEcar = dbEcar.getECar();
		
		if(cEcar != null) {
			if (cEcar.moveToFirst()) {
				//VehicleType variables
				vehicleType.setId(cEcar.getLong(5));						//5  = _id (int)
				vehicleType.setName(cEcar.getString(6));					//6  = name (text)
				vehicleType.setDescription(cEcar.getString(7));				//7  = description (text)
				vehicleType.setBatteryCapacity(cEcar.getDouble(8));			//8  = batteryCapacity (real)
				vehicleType.setEnergyConsumption_perKM(cEcar.getDouble(9));	//9  = energyConsumption_perKM (real)
				vehicleType.setRecuperationEfficiency(cEcar.getDouble(10));	//10 = recuperationEfficiency (real)
				vehicleType.setMass(cEcar.getDouble(11));					//11 = mass (real)
				vehicleType.setFrontArea(cEcar.getDouble(12));				//12 = frontArea (real)
				
				//Battery variables
				charging = (cEcar.getInt(15)==1) ? true : false;
				battery.setId(cEcar.getLong(13));				//13 = _id (int)
				battery.setCurrentSoc(cEcar.getDouble(14));		//14 = currentSoc (real)
				battery.setCharging(charging);					//15 = charging (int)
				
				//eCar variables
				eCar.setId(cEcar.getLong(0));					//0  = _id (int)
				eCar.setVehicleType(vehicleType);				//1  = vehicleType_ID (int)
				eCar.setBattery(battery);						//2  = battery_ID (int)
				eCar.setName(cEcar.getString(3));				//3  = name (text)
				eCar.setDescription(cEcar.getString(4));		//4  = description (text)
			}
//			else {
//				cEcar.close();
//				throw new DatabaseException("DBImplementation; getEcar; Could not find eCar in DB");
//			}
			cEcar.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getEcar; Could not receive valid cursor.");
		}
		//empty if nothing found
		return eCar;
	}

	@Override
	public int[] getReport_DriveSeqFeasibility(boolean onlyFinishedDriveSequences) throws DatabaseException {
		
		//working variables
		Cursor cDriveSeq;
		Cursor cEcar;
		int[] DriveSeqFeasibility = new int[3];
		int feasableDrives = 0;
		int feasableDrivesFullBattery = 0;
		int notFeasableDrives = 0;
		long vehicleTypeId = 0;
		double batteryCapacity = 0;
		double requiredCapacity = 0;
		
        //DBAdapters
		DBAdapter_DriveSequence dbDriveSeq = new DBAdapter_DriveSequence(db);
		DBAdapter_eCar dbEcar = new DBAdapter_eCar(db);

		//get the eCar
		cEcar = dbEcar.getECar();
		
		if(cEcar != null) {
			if (cEcar.moveToFirst()) {
				vehicleTypeId = cEcar.getLong(5);				//5  = _id (int)
				batteryCapacity = cEcar.getDouble(8);			//8  = batteryCapacity (real)
			}
//			else {
//				cEcar.close();
//				throw new DatabaseException("DBImplementation; getReport_DriveSeqFeasibility; Could not find eCar in DB");
//			}
			cEcar.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getReport_DriveSeqFeasibility; Could not receive valid cursor (cEcar).");
		}

		//get all DriveSequences
		if(onlyFinishedDriveSequences) {
			cDriveSeq = dbDriveSeq.getAllDriveSequences(true);
		}
		else {
			cDriveSeq = dbDriveSeq.getAllDriveSequences(false);
		}
		
		if(cDriveSeq != null) {
			if (cDriveSeq.moveToFirst()) {
				do {
					if (vehicleTypeId == cDriveSeq.getLong(10)){	//10 = vehicleTypeId (real)
						//calculate the total energy consumed by DriveSequence
						requiredCapacity = cDriveSeq.getDouble(3) - cDriveSeq.getDouble(4);	 //3 = socStart (real), 4 = socEnd (real)
						
						//count the feasible/not feasible drives
						if(cDriveSeq.getDouble(4) >=0) {
							feasableDrives++;
						}
						else {
							if(requiredCapacity <= batteryCapacity) {
								feasableDrivesFullBattery++;
							}
							else {
								notFeasableDrives++;
							}
						}
					}
					//stop if no more data records or if the vehicle type changes (because of different battery capacities)
				} while(cDriveSeq.moveToNext());
			}
//			else {
//				cDriveSeq.close();
//				throw new DatabaseException("DBImplementation; getReport_DriveSeqFeasibility; Could not find DriveSequences in DB");
//			}
			cDriveSeq.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getReport_DriveSeqFeasibility; Could not receive valid cursor (cDriveSeq).");
		}
		
		//calculate the exclusive values
		DriveSeqFeasibility[0] = feasableDrives;
		DriveSeqFeasibility[1] = feasableDrivesFullBattery;
		DriveSeqFeasibility[2] = notFeasableDrives;
		
		return DriveSeqFeasibility;
	}

	@Override
	public List<Map<String, Object>> getReport_BatterySOCs(int numberOfDriveSeq,
			boolean onlyFinishedDriveSequences) throws DatabaseException{
		//working variables
		Cursor cDriveSeq;
		Cursor cEcar;
		long vehicleTypeId = 0;
		double batteryCapacity = 0;
		List<Map<String, Object>> returnVals = new ArrayList<Map<String,Object>>();;
		Map<String, Object> listItem;
		
		//DBAdapters
		DBAdapter_DriveSequence dbDriveSeq = new DBAdapter_DriveSequence(db);
		DBAdapter_eCar dbEcar = new DBAdapter_eCar(db);

		//get the eCar
		cEcar = dbEcar.getECar();
		
		if(cEcar != null) {
			if (cEcar.moveToFirst()) {
				vehicleTypeId = cEcar.getLong(5);				//5  = _id (int)
				batteryCapacity = cEcar.getDouble(8);			//8  = batteryCapacity (real)
			}
//			else {
//				cEcar.close();
//				throw new DatabaseException("DBImplementation; getReport_BatterySOCs; Could not find eCar in DB");
//			}
			cEcar.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getReport_BatterySOCs; Could not receive valid cursor (cEcar).");
		}

		//get the DriveSequences
		if(onlyFinishedDriveSequences) {
			cDriveSeq = dbDriveSeq.getAllDriveSequences(true);
		}
		else {
			cDriveSeq = dbDriveSeq.getAllDriveSequences(false);
		}
		
		if(cDriveSeq != null) {
			if (cDriveSeq.moveToFirst()) {
				do {
					if (vehicleTypeId == cDriveSeq.getLong(10)){	//10 = vehicleTypeId (real)

						//start of DriveSequence
						listItem = new HashMap<String, Object>();
						listItem.put("timestamp", cDriveSeq.getLong(5));	//5 = timeStart (int)
						listItem.put("soc",
								(cDriveSeq.getDouble(3)/batteryCapacity)*100);	//3 = socStart (real)
						returnVals.add(listItem);
						
						//end of DriveSequence
						listItem = new HashMap<String, Object>();
						listItem.put("timestamp", cDriveSeq.getLong(6));	//6 = timeStop (int)
						listItem.put("soc",
								(cDriveSeq.getDouble(4)/batteryCapacity)*100);	//4 = socEnd (real)
						returnVals.add(listItem);
					}
				} while(cDriveSeq.moveToNext());
			}
//			else {
//				cDriveSeq.close();
//				throw new DatabaseException("DBImplementation; getReport_BatterySOCs; Could not find DriveSequences in DB");
//			}
			cDriveSeq.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getReport_BatterySOCs; Could not receive valid cursor (cDriveSeq).");
		}
		
		return returnVals;
	}
	
	@Override
	public List<Map<String, Object>> getReport_Speed(int numberOfDriveSeq,
			long driveSeqId) throws DatabaseException{
		//working variables
		Cursor cWaypoints;
		Cursor cEcar;
		long vehicleTypeId = 0;
		double batteryCapacity = 0;
		List<Map<String, Object>> returnVals = new ArrayList<Map<String,Object>>();;
		Map<String, Object> listItem;
		
		//DBAdapters
		DBAdapter_WayPoint dbwaypoint = new DBAdapter_WayPoint(db);

		//get the DriveSequences
			// todo: get last instead of first trip
		DriveSequence lastDrive = this.getDriveSequences(true).get(0);
		cWaypoints = dbwaypoint.getWayPointsByDriveSeq(driveSeqId);
		
		if(cWaypoints != null) {
			if (cWaypoints.moveToFirst()) {
				int i = 0;
				do {
						// todo: get speed!!!!!!!!!!
						
						//start of DriveSequence
						listItem = new HashMap<String, Object>();
						listItem.put("timestamp", cWaypoints.getLong(5));	//5 = timeStart (int)
						listItem.put("soc",
								(cWaypoints.getDouble(3)*3.6));	//3 = velocity
						returnVals.add(listItem);
						i++;
				} while(cWaypoints.moveToNext() & i<numberOfDriveSeq);
			}
			cWaypoints.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getReport_BatterySOCs; Could not receive valid cursor (cDriveSeq).");
		}
		
		return returnVals;
	}	
	
	@Override
	public List<Map<String, Object>> getReport_Acceleration(int numberOfDriveSeq,
			long driveSeqId) throws DatabaseException{
		//working variables
		Cursor cWaypoints;
		Cursor cEcar;
		long vehicleTypeId = 0;
		double batteryCapacity = 0;
		List<Map<String, Object>> returnVals = new ArrayList<Map<String,Object>>();;
		Map<String, Object> listItem;
		
		//DBAdapters
		DBAdapter_WayPoint dbwaypoint = new DBAdapter_WayPoint(db);

		//get the DriveSequences
			// todo: get last instead of first trip
		DriveSequence lastDrive = this.getDriveSequences(true).get(0);
		cWaypoints = dbwaypoint.getWayPointsByDriveSeq(driveSeqId);
		
		if(cWaypoints != null) {
			if (cWaypoints.moveToFirst()) {
				int i = 0;
				do {
						listItem = new HashMap<String, Object>();
						listItem.put("timestamp", cWaypoints.getLong(5));	//5 = timeStart (int)
						listItem.put("soc",
								(cWaypoints.getDouble(4)));	//4 = acceleration, in m/s/s!!!!
						returnVals.add(listItem);
						i++;
				} while(cWaypoints.moveToNext() & i<numberOfDriveSeq);
			}
			cWaypoints.close();
		}
		else {
			throw new DatabaseException("DBImplementation; Could not receive report.");
		}
		
		return returnVals;
	}		

	@Override
	public double[] getReport_AverageConsumption(boolean onlyFinishedDriveSequences) throws DatabaseException{
		
		//working variables
		Cursor cDriveSeq;
		Cursor cEcar;
		long vehicleTypeId = 0;
		double[] avgConsumption = new double[2];
		double kmCurrent = 0;
		double kwCurrent = 0;
		double kmPast = 0;
		double kwPast = 0;

		//get first day of the current month (date)
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		// get start of the month
		cal.set(Calendar.DAY_OF_MONTH, 1);
		
        //DBAdapters
		DBAdapter_DriveSequence dbDriveSeq = new DBAdapter_DriveSequence(db);
		DBAdapter_eCar dbEcar = new DBAdapter_eCar(db);

		//get the eCar
		cEcar = dbEcar.getECar();
		
		if(cEcar != null) {
			if (cEcar.moveToFirst()) {
				vehicleTypeId = cEcar.getLong(5);				//5  = _id (int)
			}
//			else {
//				cEcar.close();
//				throw new DatabaseException("DBImplementation; getReport_AverageConsumption; Could not find eCar in DB");
//			}
			cEcar.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getReport_AverageConsumption; Could not receive valid cursor (cEcar).");
		}

		//get all DriveSequences
		if(onlyFinishedDriveSequences) {
			cDriveSeq = dbDriveSeq.getAllDriveSequences(true);
		}
		else {
			cDriveSeq = dbDriveSeq.getAllDriveSequences(false);
		}
		
		if(cDriveSeq != null) {
			if (cDriveSeq.moveToFirst()) {
				do {
					if (vehicleTypeId == cDriveSeq.getLong(10)){	//10 = vehicleTypeId (real)

						//calculate the total km and kW values for the past and the current month
						if(cDriveSeq.getLong(5) < cal.getTimeInMillis()) {		//5 = timeStart (int)
							kwPast = kwPast + (cDriveSeq.getDouble(3) - cDriveSeq.getDouble(4)); //3 = socStart (real), 4 = socEnd (real)
							kmPast = kmPast + (cDriveSeq.getDouble(8) / 1000);	//8 = coveredDistance (real) in meter 
						}
						else {
							kwCurrent = kwCurrent + (cDriveSeq.getDouble(3) - cDriveSeq.getDouble(4)); //3 = socStart (real), 4 = socEnd (real)
							kmCurrent = kmCurrent + (cDriveSeq.getDouble(8) / 1000);	//8 = coveredDistance (real) in meter
						}
					}
				} while(cDriveSeq.moveToNext());
			}
//			else {
//				cDriveSeq.close();
//				throw new DatabaseException("DBImplementation; getReport_AverageConsumption; Could not find DriveSequences in DB");
//			}
			cDriveSeq.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getReport_AverageConsumption; Could not receive valid cursor (cDriveSeq).");
		}
		
		//calculate the exclusive values
		if(kmPast==0){
			avgConsumption[0] = 0;
		}
		else {
			avgConsumption[0] = (kwPast/kmPast) * 100;
		}

		//Consumption for current month
		if(kmCurrent==0){
			avgConsumption[1] = 0;
		}
		else {
			avgConsumption[1] = (kwCurrent/kmCurrent) * 100;
		}
		
		return avgConsumption;
	}

	@Override
	public int[][] getReport_DriveSeqDistances(boolean onlyFinishedDriveSequences) throws DatabaseException{
		
		// working variables
		
		Cursor cDriveSeq;
		// Array for counting the DriveSequences
		// First Index (Intervals): < 25, 50, 100, 200, 200+ km)
		// Second Index: Number of DriveSequences in this interval
		int[][] returnVals = new int[5][2];
		
		// Setting the interval limits
		returnVals[0][0] = 25;
		returnVals[1][0] = 50;
		returnVals[2][0] = 100;
		returnVals[3][0] = 200;
		returnVals[4][0] = 999;

        //DBAdapters
		DBAdapter_DriveSequence dbDriveSeq = new DBAdapter_DriveSequence(db);

		//get all DriveSequences
		if(onlyFinishedDriveSequences) {
			cDriveSeq = dbDriveSeq.getAllDriveSequences(true);
		}
		else {
			cDriveSeq = dbDriveSeq.getAllDriveSequences(false);
		}
		
		if(cDriveSeq != null) {
			if (cDriveSeq.moveToFirst()) {
				do {  //8 = coveredDistance (real)
					
					//Count DriveSequences depending on the covered distance
					if(cDriveSeq.getDouble(8) <= 25000) {   //5000 m = 5 km
						returnVals[0][1]++;
					}
					else {
						if(cDriveSeq.getDouble(8) <= 50000) {
							returnVals[1][1]++;
						}
						else {
							if(cDriveSeq.getDouble(8) <= 100000) {
								returnVals[2][1]++;
							}
							else {
								if(cDriveSeq.getDouble(8) <= 200000) {
									returnVals[3][1]++;
								}
								else {
									returnVals[4][1]++;
								}
							}
						}
					}
				} while(cDriveSeq.moveToNext());
			}
//			else {
//				cDriveSeq.close();
//				throw new DatabaseException("DBImplementation; getReport_DriveSeqDistances; Could not find DriveSequences in DB");
//			}
			cDriveSeq.close();
		}
		else {
			throw new DatabaseException("DBImplementation; getReport_DriveSeqDistances; Could not receive valid cursor.");
		}

		return returnVals;
	}

	public void importDb() {
		File src = new File(Environment.getExternalStorageDirectory(), "eCarSimulation.db");		
		File dst = appContext.getDatabasePath("eCarSimulation.db");  // make sure database name includes .db!
		copyFile(src, dst);		
	}
	
	public void exportDb() {
		File src = appContext.getDatabasePath("eCarSimulation.db");  // make sure database name includes .db!
		File dst = new File(Environment.getExternalStorageDirectory(), "eCarSimulation.db");		
		copyFile(src, dst);		
		
	}	
	
public void copyFile(File src, File dst)  {
	    
		try {
			FileInputStream inStream = new FileInputStream(src);
		    FileOutputStream outStream = new FileOutputStream(dst);
		    FileChannel inChannel = inStream.getChannel();
		    FileChannel outChannel = outStream.getChannel();
		    inChannel.transferTo(0, inChannel.size(), outChannel);
		    inStream.close();
		    outStream.close();
		} catch (FileNotFoundException e) {
			Toast.makeText(
					appContext.getApplicationContext(),
					"Database could not be written: File not found.",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		 catch (IOException e) {
				Toast.makeText(
						appContext.getApplicationContext(),
						"Database could not be written.",
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
	}	

}