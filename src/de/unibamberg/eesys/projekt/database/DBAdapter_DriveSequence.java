package de.unibamberg.eesys.projekt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

/**
 * Database Adapter to provide the SQL related methods for
 * operating on the database table "DriveSequence".
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class DBAdapter_DriveSequence {
	public static final String DS_ROW_ID = Table_DriveSequence.COLUMN_ID;
	public static final String DS_FULL_ID = Table_DriveSequence.FULL_ID;
	public static final String DS_SEQ_TYPE_ID = Table_DriveSequence.COLUMN_SEQUENCE_TYPE_ID;
	public static final String DS_VEHICLE_TYPE_ID = Table_DriveSequence.COLUMN_VEHICLE_TYPE_ID;
	public static final String DS_SOC_START = Table_DriveSequence.COLUMN_SOC_START;
	public static final String DS_SOC_END = Table_DriveSequence.COLUMN_SOC_END;
	public static final String DS_TIME_START = Table_DriveSequence.COLUMN_TIME_START;
	public static final String DS_TIME_STOP = Table_DriveSequence.COLUMN_TIME_STOP;
	public static final String DS_ACTIVE = Table_DriveSequence.COLUMN_ACTIVE;
	public static final String DS_COVERED_DISTANCE = Table_DriveSequence.COLUMN_COVERED_DISTANCE;
	public static final String DS_SUM_CO2 = Table_DriveSequence.COLUMN_SUM_CO2;

	public static final String VT_ROW_ID = Table_VehicleType.COLUMN_ID;
	public static final String VT_FULL_ID = Table_VehicleType.FULL_ID;
	
	private static final String TABLE_DRIVESEQ = Table_DriveSequence.TABLE_NAME;
    private static final String TABLE_VEHICLETYPE = Table_VehicleType.TABLE_NAME;
    
    private SQLiteDatabase mDb;

    /**
     * Constructor - reference the provided DB
     * 
     * @param dbAdapterDB
     * 		the database opened by the DBInitializer
     */
    public DBAdapter_DriveSequence(SQLiteDatabase dbAdapterDB) {
        this.mDb = dbAdapterDB;
    }
    
    /**
     * Create a new DriveSequence. If the DriveSequence is successfully created return the new
     * rowId for that DriveSequence, otherwise return a -1 to indicate failure.
     * No sub-objects (VehicleType) will be considered.
     *  
	 * @param seqTypeId
	 * @param vehicleTypeId
	 * @param socStart
	 * @param socEnd
	 * @param timeStart
	 * @param timeStop
	 * @param active
	 * @param coveredDistance
     * @param sumCO2
     * @return rowId or -1 if failed
     */
    public long createDriveSequence(long seqTypeId, long vehicleTypeId, double socStart, double socEnd,
    		long timeStart, long timeStop, int active, double coveredDistance, double sumCO2) {
        ContentValues args = new ContentValues();
        args.put(DS_SEQ_TYPE_ID, seqTypeId);
        args.put(DS_VEHICLE_TYPE_ID, vehicleTypeId);
        args.put(DS_SOC_START, socStart);
        args.put(DS_SOC_END, socEnd);
        args.put(DS_TIME_START, timeStart);
        args.put(DS_TIME_STOP, timeStop);
        args.put(DS_ACTIVE, active);
        args.put(DS_COVERED_DISTANCE, coveredDistance);
        args.put(DS_SUM_CO2, sumCO2);
        return this.mDb.insert(TABLE_DRIVESEQ, null, args);
    }

    /**
     * Delete the DriveSequence with the given rowId.
     * No sub-objects (VehicleType) will be considered.
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteDriveSequence(long rowId) {

        return this.mDb.delete(TABLE_DRIVESEQ, DS_ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all DriveSequences including
     * the corresponding VehicleTypes.
     * 
     * @param onlyFinishedDriveSequences (boolean)
     * @return Cursor over all DriveSequences
     */
    public Cursor getAllDriveSequences(boolean onlyFinishedDriveSequences) {
    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	Cursor mCursor = null;
    	
    	queryBuilder.setTables(
    			TABLE_DRIVESEQ + " LEFT OUTER JOIN " + TABLE_VEHICLETYPE + 
    			" ON " + DS_VEHICLE_TYPE_ID + " = " + VT_FULL_ID
    			);
    	
    	//just provide finished DriveSequences if desired
    	if(onlyFinishedDriveSequences){
        	mCursor = queryBuilder.query(
        			mDb, null, DS_ACTIVE  + "= 0 ", null, null, null, DS_ROW_ID + " DESC");
    	}
    	else {
        	mCursor = queryBuilder.query(
        			mDb, null, null, null, null, null, DS_ROW_ID + " DESC");
    	}
    	
    	return mCursor;
    }

    /**
     * Return a Cursor positioned at the DriveSequence that matches the given rowId.
     * The corresponding VehicleType will be considered.
     * 
     * @param rowId
     * @return Cursor positioned to matching DriveSequence 
     *         (and the corresponding VehicleType) (if found) or empty cursor
     */
    public Cursor getDriveSequence(long rowId) {
    	
    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	
    	queryBuilder.setTables(
    			TABLE_DRIVESEQ + " LEFT OUTER JOIN " + TABLE_VEHICLETYPE + 
    			" ON " + DS_VEHICLE_TYPE_ID + " = " + VT_FULL_ID
    			);
    	
    	Cursor mCursor = queryBuilder.query(mDb, null, DS_FULL_ID + "=" + rowId, null, null, null, null);

    	return mCursor;
    }

    /**
     * Update the DriveSequence.
     * No sub-objects (VehicleType) will be considered.
     * 
     * @param rowId
	 * @param seqTypeId
	 * @param vehicleTypeId
	 * @param socStart
	 * @param socEnd
	 * @param timeStart
	 * @param timeStop
	 * @param active
	 * @param coveredDistance
     * @param sumCO2
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateDriveSequence(long rowId, long seqTypeId, long vehicleTypeId, double socStart, double socEnd,
    		long timeStart, long timeStop, int active, double coveredDistance, double sumCO2) {
        ContentValues args = new ContentValues();
        args.put(DS_SEQ_TYPE_ID, seqTypeId);
        args.put(DS_VEHICLE_TYPE_ID, vehicleTypeId);
        args.put(DS_SOC_START, socStart);
        args.put(DS_SOC_END, socEnd);
        args.put(DS_TIME_START, timeStart);
        args.put(DS_TIME_STOP, timeStop);
        args.put(DS_ACTIVE, active);
        args.put(DS_COVERED_DISTANCE, coveredDistance);
        args.put(DS_SUM_CO2, sumCO2);

        return this.mDb.update(TABLE_DRIVESEQ, args, DS_ROW_ID + "=" + rowId, null) >0; 
    }
}