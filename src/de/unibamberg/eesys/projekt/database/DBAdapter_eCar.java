package de.unibamberg.eesys.projekt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

/**
 * Database Adapter to provide the SQL related methods for
 * operating on the database table "eCar".
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class DBAdapter_eCar {
	public static final String EC_ROW_ID = Table_eCar.COLUMN_ID;
	public static final String EC_FULL_ID = Table_eCar.FULL_ID;
	public static final String EC_VEHICLE_TYPE_ID = Table_eCar.COLUMN_VEHICLE_TYPE_ID;
	public static final String EC_BATTERY_ID = Table_eCar.COLUMN_BATTERY_ID;
	public static final String EC_NAME = Table_eCar.COLUMN_NAME;
	public static final String EC_DESCRIPTION = Table_eCar.COLUMN_DESCRIPTION;

	public static final String VT_ROW_ID = Table_VehicleType.COLUMN_ID;
	public static final String VT_FULL_ID = Table_VehicleType.FULL_ID;

	public static final String BA_ROW_ID = Table_Battery.COLUMN_ID;
	public static final String BA_FULL_ID = Table_Battery.FULL_ID;

    private static final String TABLE_ECAR = Table_eCar.TABLE_NAME;
    private static final String TABLE_VEHICLETYPE = Table_VehicleType.TABLE_NAME;
    private static final String TABLE_BATTERY = Table_Battery.TABLE_NAME;

    private SQLiteDatabase mDb;

    /**
     * Constructor - reference the provided DB
     * 
     * @param dbAdapterDB
     * 		the database opened by the DBInitializer
     */
    public DBAdapter_eCar(SQLiteDatabase dbAdapterDB) {
        this.mDb = dbAdapterDB;
    }
    
    /**
     * - replaced by "replaceECar" -
     * Create a new eCar. If the eCar is successfully created return the new
     * rowId for that eCar, otherwise return a -1 to indicate failure.
     * No sub-objects (VehicleType, Battery) will be considered.
     * 
     * @param vehicleTypeId
     * @param batteryId
     * @param name
     * @param description
     * @return rowId or -1 if failed
     */
//    public long createECar(long vehicleTypeId, long batteryId, String name, String description) {
//        ContentValues args = new ContentValues();
//        args.put(EC_VEHICLE_TYPE_ID, vehicleTypeId);
//        args.put(EC_BATTERY_ID, batteryId);
//        args.put(EC_NAME, name);
//        args.put(EC_DESCRIPTION, description);
//        return this.mDb.insert(TABLE_ECAR, null, args);
//    }

    /**
     * Delete the eCar with the given rowId
     * No sub-objects (VehicleType, Battery) will be considered.
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteECar(long rowId) {

        return this.mDb.delete(TABLE_ECAR, EC_ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * - not needed in current implementation (but for multiple eCars) -
     * Return a Cursor over the list of all eCars including
     * the corresponding VehicleType and Battery
     * 
     * @return Cursor over all eCars
     */
//    public Cursor getAllECars() {
//    	
//    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
//    	
//    	queryBuilder.setTables(
//    			TABLE_ECAR + " LEFT OUTER JOIN " + TABLE_VEHICLETYPE + 
//    			" ON " + EC_VEHICLE_TYPE_ID + " = " + VT_FULL_ID
//    			 + " LEFT OUTER JOIN " + TABLE_BATTERY + 
//     			" ON " + EC_BATTERY_ID + " = " + BA_FULL_ID
//    			);
//    	
//    	return queryBuilder.query(mDb, null, null, null, null, null, null);
//    }

    /**
     * Return a Cursor positioned at the eCar
     * @param rowId
     * @return Cursor positioned to the eCar
     *         (including the corresponding VehicleType and Battery) (if found) or empty cursor
     */
    public Cursor getECar() {
    	
    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	
    	queryBuilder.setTables(
    			TABLE_ECAR + " LEFT OUTER JOIN " + TABLE_VEHICLETYPE + 
    			" ON " + EC_VEHICLE_TYPE_ID + " = " + VT_FULL_ID
    			 + " LEFT OUTER JOIN " + TABLE_BATTERY + 
     			" ON " + EC_BATTERY_ID + " = " + BA_FULL_ID
    			);
    	
    	Cursor mCursor = queryBuilder.query(mDb, null, EC_FULL_ID + "= 1", null, null, null, null);

        return mCursor;
    }

    /**
     * - replaced by "replaceECar" -
     * Update the eCar.
     * No sub-objects (VehicleType, Battery) will be considered.
     * 
     * @param rowId
     * @param vehicleTypeId
     * @param batteryId
     * @param name
     * @param description
     * @return true if the note was successfully updated, false otherwise
     * @throws SQLException in case of an (unexpected) error
     */
//    public boolean updateECar(long rowId, long vehicleTypeId, long batteryId, String name, String description) throws SQLException {
//        ContentValues args = new ContentValues();
//        args.put(EC_VEHICLE_TYPE_ID, vehicleTypeId);
//        args.put(EC_BATTERY_ID, batteryId);
//        args.put(EC_NAME, name);
//        args.put(EC_DESCRIPTION, description);
//
//        return this.mDb.update(TABLE_ECAR, args, EC_ROW_ID + "=" + rowId, null) >0; 
//    }

    /**
     * Replace the eCar.
     * Inserts a new eCar or updates it, if it exists.
     * No sub-objects (VehicleType, Battery) will be considered.
     * 
     * @param vehicleTypeId
     * @param batteryId
     * @param name
     * @param description
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     * @throws SQLException in case of an (unexpected) error
     */
    public long replaceECar(long vehicleTypeId, long batteryId, String name, String description) throws SQLException {
        ContentValues args = new ContentValues();
        args.put(EC_ROW_ID, 1);
        args.put(EC_VEHICLE_TYPE_ID, vehicleTypeId);
        args.put(EC_BATTERY_ID, batteryId);
        args.put(EC_NAME, name);
        args.put(EC_DESCRIPTION, description);

        return this.mDb.replace(TABLE_ECAR, null, args); 
    }
}