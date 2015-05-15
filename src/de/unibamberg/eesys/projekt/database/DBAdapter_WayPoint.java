package de.unibamberg.eesys.projekt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
/**
 * Database Adapter to provide the SQL related methods for
 * operating on the database table "WayPoint".
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class DBAdapter_WayPoint {
	public static final String WP_FULL_ID = Table_WayPoint.FULL_ID;
	public static final String WP_ROW_ID = Table_WayPoint.COLUMN_ID;
	public static final String WP_DRIVE_SEQ_ID = Table_WayPoint.COLUMN_DRIVE_SEQ_ID;
	public static final String WP_GEO_ID = Table_WayPoint.COLUMN_GEO_ID;
	public static final String WP_VELOCITY = Table_WayPoint.COLUMN_VELOCITY;
	public static final String WP_ACCELERATION = Table_WayPoint.COLUMN_ACCELERATION;
	public static final String WP_TIMESTAMP = Table_WayPoint.COLUMN_TIMESTAMP;
	
	public static final String GC_FULL_ID = Table_GeoCoordinate.FULL_ID;
	public static final String GC_ROW_ID = Table_GeoCoordinate.COLUMN_ID;

	public static final String DS_FULL_ID = Table_DriveSequence.FULL_ID;
	public static final String DS_ROW_ID = Table_DriveSequence.COLUMN_ID;
	public static final String DS_VEHICLE_TYPE_ID = Table_DriveSequence.COLUMN_VEHICLE_TYPE_ID;

	public static final String VT_FULL_ID = Table_VehicleType.FULL_ID;
	public static final String VT_ROW_ID = Table_VehicleType.COLUMN_ID;

    private static final String TABLE_GEOCOORDINATE = Table_GeoCoordinate.TABLE_NAME;
    private static final String TABLE_DRIVESEQ = Table_DriveSequence.TABLE_NAME;
    private static final String TABLE_VEHICLETYPE = Table_VehicleType.TABLE_NAME;
    private static final String TABLE_WAYPOINT = Table_WayPoint.TABLE_NAME;

    private SQLiteDatabase mDb;

    /**
     * Constructor - reference the provided DB
     * 
     * @param dbAdapterDB
     * 		the database opened by the DBInitializer
     */
    public DBAdapter_WayPoint(SQLiteDatabase dbAdapterDB) {
        this.mDb = dbAdapterDB;
    }

    /**
     * Create a new new (single) WayPoint.
     * No further sub entities (e.g. GeoCoordinate) will be considered.
     * If the WayPoint is successfully created return the new
     * rowId for that WayPoint, otherwise return a -1 to indicate failure.
     * 
     * @param driveSequenceId
     * @param geoId
     * @param velocity
     * @param acceleration
     * @param timestamp
     * @return rowId or -1 if failed
     */
    public long createWayPoint(long driveSequenceId, long geoId, double velocity,
    		double acceleration, long timestamp) {
        ContentValues args = new ContentValues();
        args.put(WP_DRIVE_SEQ_ID, driveSequenceId);
        args.put(WP_GEO_ID, geoId);
        args.put(WP_VELOCITY, velocity);
        args.put(WP_ACCELERATION, acceleration);
        args.put(WP_TIMESTAMP, timestamp);
        return this.mDb.insert(TABLE_WAYPOINT, null, args);
    }

    /**
     * Delete the WayPoint with the given rowId.
     * No further sub entities (e.g. GeoCoordinate) will be considered.
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteWayPoint(long rowId) {

        return this.mDb.delete(TABLE_WAYPOINT, WP_ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all WayPoints and the corresponding GeoCoordinates and DriveSequences.
     * 
     * @return Cursor over all WayPoints and the corresponding GeoCoordinates and DriveSequences.
     */
    public Cursor getAllWayPoints() {
    	
    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	
    	queryBuilder.setTables(
    			TABLE_WAYPOINT + " LEFT OUTER JOIN " + TABLE_GEOCOORDINATE + 
    			" ON " + WP_GEO_ID + " = " + GC_FULL_ID
	   			 + " LEFT OUTER JOIN " + TABLE_DRIVESEQ + 
	    			" ON " + WP_DRIVE_SEQ_ID + " = " + DS_FULL_ID
	   			 + " LEFT OUTER JOIN " + TABLE_VEHICLETYPE + 
	    			" ON " + DS_VEHICLE_TYPE_ID + " = " + VT_FULL_ID
    			);
    	
    	return queryBuilder.query(mDb, null, null, null, null, null, null);
    }
    
    /**
     * Return a Cursor over the list of all WayPoints
     * for the specified drive sequence id.
     * No further sub entities (e.g. DriveSequence) will be considered.
     * 
     * @param driveSeqId
     * @return Cursor over all WayPoints for the specified drive sequence id
     */
    public Cursor getWayPointsByDriveSeq(long driveSeqId) {
    	
    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	
    	queryBuilder.setTables(
    			TABLE_WAYPOINT + " LEFT OUTER JOIN " + TABLE_GEOCOORDINATE + 
    			" ON " + WP_GEO_ID + " = " + GC_FULL_ID
    			);
    	
    	return queryBuilder.query(mDb, null, WP_DRIVE_SEQ_ID + "=" + driveSeqId, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the WayPoint that matches the given rowId.
     * No further sub entities (e.g. GeoCoordinate) will be considered.
     * 
     * @param rowId
     * @return Cursor positioned to matching WayPoint (if found) or empty cursor
     */
    public Cursor getWayPoint(long rowId) {
    	
    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	
    	queryBuilder.setTables(
    			TABLE_WAYPOINT + " LEFT OUTER JOIN " + TABLE_GEOCOORDINATE + 
    			" ON " + WP_GEO_ID + " = " + GC_FULL_ID
    			);
    	
    	Cursor mCursor = queryBuilder.query(mDb, null, WP_FULL_ID + "=" + rowId, null, null, null, null);

        return mCursor;
    }

    /**
     * Update the WayPoint.
     * No further sub entities (e.g. GeoCoordinate) will be considered.
     * 
     * @param rowId
     * @param driveId
     * @param geoId
     * @param timestamp
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateWayPoint(long rowId, long driveSequenceId, long geoId, double velocity,
    		double acceleration, long timestamp) {
        ContentValues cvWP = new ContentValues();
    	cvWP.put(WP_DRIVE_SEQ_ID, driveSequenceId);
    	cvWP.put(WP_GEO_ID, geoId);
    	cvWP.put(WP_VELOCITY, velocity);
    	cvWP.put(WP_ACCELERATION, acceleration);
    	cvWP.put(WP_TIMESTAMP, timestamp);

        return this.mDb.update(TABLE_WAYPOINT, cvWP, WP_ROW_ID + "=" + rowId, null) >0; 
    }

}