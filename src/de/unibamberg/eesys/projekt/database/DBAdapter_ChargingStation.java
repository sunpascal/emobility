package de.unibamberg.eesys.projekt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
/**
 * Database Adapter to provide the SQL related methods for
 * operating on the database table "ChargingStation".
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class DBAdapter_ChargingStation {
	public static final String CS_ROW_ID = Table_ChargingStation.COLUMN_ID;
	public static final String CS_FULL_ID = Table_ChargingStation.FULL_ID;
	public static final String CS_CHARGING_TYPE_ID = Table_ChargingStation.COLUMN_CHARGING_TYPE_ID;
	public static final String CS_GEO_ID = Table_ChargingStation.COLUMN_GEO_ID;
	public static final String CS_NAME = Table_ChargingStation.COLUMN_NAME;
	public static final String CS_DESCRIPTION = Table_ChargingStation.COLUMN_DESCRIPTION;
	
	public static final String CT_FULL_ID = Table_ChargingType.FULL_ID;
	public static final String CT_ROW_ID = Table_ChargingType.COLUMN_ID;
	
	public static final String GC_FULL_ID = Table_GeoCoordinate.FULL_ID;
	public static final String GC_ROW_ID = Table_GeoCoordinate.COLUMN_ID;
	
    private static final String TABLE_CHARGINGSTATION = Table_ChargingStation.TABLE_NAME;
    private static final String TABLE_CHARGINGTYPE = Table_ChargingType.TABLE_NAME;
    private static final String TABLE_GEOCOORDINATE = Table_GeoCoordinate.TABLE_NAME;

    private final SQLiteDatabase mDb;

    /**
     * Constructor - reference the provided DB
     * 
     * @param dbAdapterDB
     * 		the database opened by the DBInitializer
     */
    public DBAdapter_ChargingStation(SQLiteDatabase dbAdapterDB) {
        this.mDb = dbAdapterDB;
    }
    
    /**
     * Create a new ChargingStation. If the ChargingStation is successfully created return the new
     * rowId for that ChargingStation, otherwise return a -1 to indicate failure.
     * 
     * @param chargingTypeId
     * @param geoId
     * @param name
     * @param description
     * @return rowId or -1 if failed
     */
    public long createChargingStation(long chargingTypeId, long geoId, String name, String description) {
        ContentValues args = new ContentValues();
        args.put(CS_CHARGING_TYPE_ID, chargingTypeId);
        args.put(CS_GEO_ID, geoId);
        args.put(CS_NAME, name);
        args.put(CS_DESCRIPTION, description);
        return this.mDb.insert(TABLE_CHARGINGSTATION, null, args);
    }

    /**
     * Delete the ChargingStation with the given rowId
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteChargingStation(long rowId) {

        return this.mDb.delete(TABLE_CHARGINGSTATION, CS_ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all ChargingStations in the table
     * 
     * @return Cursor over all ChargingStations
     */
    public Cursor getAllChargingStations() {

    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	
    	queryBuilder.setTables(
    			TABLE_CHARGINGSTATION +
    			" LEFT OUTER JOIN " + TABLE_CHARGINGTYPE + " ON " + CS_CHARGING_TYPE_ID + " = " + CT_FULL_ID +
    			" LEFT OUTER JOIN " + TABLE_GEOCOORDINATE + " ON " + CS_GEO_ID + " = " + GC_FULL_ID
    			);
    	
    	return queryBuilder.query(mDb, null, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the ChargingStation that matches the given rowId
     * @param rowId
     * @return Cursor positioned to matching ChargingStation (if found) or empty cursor
     */
    public Cursor getChargingStation(long rowId) {
    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	
    	queryBuilder.setTables(
    			TABLE_CHARGINGSTATION +
    			" LEFT OUTER JOIN " + TABLE_CHARGINGTYPE + " ON " + CS_CHARGING_TYPE_ID + " = " + CT_FULL_ID +
    			" LEFT OUTER JOIN " + TABLE_GEOCOORDINATE + " ON " + CS_GEO_ID + " = " + GC_FULL_ID
    			);
    	
    	Cursor mCursor = queryBuilder.query(mDb, null, CS_FULL_ID + "=" + rowId, null, null, null, null);

        return mCursor;
    }

    /**
     * Update the ChargingStation.
     * 
     * @param rowId
     * @param chargingTypeId
     * @param geoId
     * @param name
     * @param description
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateChargingStation(long rowId, long chargingTypeId, long geoId, String name, String description) {
        ContentValues args = new ContentValues();
        args.put(CS_CHARGING_TYPE_ID, chargingTypeId);
        args.put(CS_GEO_ID, geoId);
        args.put(CS_NAME, name);
        args.put(CS_DESCRIPTION, description);

        return this.mDb.update(TABLE_CHARGINGSTATION, args, CS_ROW_ID + "=" + rowId, null) >0; 
    }
}