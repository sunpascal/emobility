package de.unibamberg.eesys.projekt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
/**
 * Database Adapter to provide the SQL related methods for
 * operating on the database table "ChargeSequence".
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class DBAdapter_ChargeSequence {
	public static final String CS_ROW_ID = Table_ChargeSequence.COLUMN_ID;
	public static final String CS_FULL_ID = Table_ChargeSequence.FULL_ID;
	public static final String CS_SEQ_TYPE_ID = Table_ChargeSequence.COLUMN_SEQUENCE_TYPE_ID;
	public static final String CS_VEHICLE_TYPE_ID = Table_ChargeSequence.COLUMN_VEHICLE_TYPE_ID;
	public static final String CS_SOC_START = Table_ChargeSequence.COLUMN_SOC_START;
	public static final String CS_SOC_END = Table_ChargeSequence.COLUMN_SOC_END;
	public static final String CS_TIME_START = Table_ChargeSequence.COLUMN_TIME_START;
	public static final String CS_TIME_STOP = Table_ChargeSequence.COLUMN_TIME_STOP;
	public static final String CS_ACTIVE = Table_ChargeSequence.COLUMN_ACTIVE;
	public static final String CS_CHARGING_TYPE_ID = Table_ChargeSequence.COLUMN_CHARGING_TYPE_ID;

	public static final String VT_ROW_ID = Table_VehicleType.COLUMN_ID;
	public static final String VT_FULL_ID = Table_VehicleType.FULL_ID;

	public static final String CT_ROW_ID = Table_ChargingType.COLUMN_ID;
	public static final String CT_FULL_ID = Table_ChargingType.FULL_ID;

    private static final String TABLE_CHARGESEQ = Table_ChargeSequence.TABLE_NAME;
    private static final String TABLE_VEHICLETYPE = Table_VehicleType.TABLE_NAME;
    private static final String TABLE_CHARGINGTYPE = Table_ChargingType.TABLE_NAME;

    private SQLiteDatabase mDb;

    /**
     * Constructor - reference the provided DB
     * 
     * @param dbAdapterDB
     * 		the database opened by the DBInitializer
     */
    public DBAdapter_ChargeSequence(SQLiteDatabase dbAdapterDB) {
        this.mDb = dbAdapterDB;
    }
    
    /**
     * Create a new ChargeSequence. If the ChargeSequence is successfully created return the new
     * rowId for that ChargeSequence, otherwise return a -1 to indicate failure.
     * No sub-objects (VehicleType, ChargingType) will be considered.
     * 
	 * @param seqTypeId
	 * @param vehicleTypeId
	 * @param socStart
	 * @param socEnd
	 * @param timeStart
	 * @param timeStop
	 * @param active
     * @param chargingTypeId
     * @return rowId or -1 if failed
     */
    public long createChargeSequence(long seqTypeId, long vehicleTypeId, double socStart, double socEnd, 
    		long timeStart, long timeStop, int active, long chargingType) {
        ContentValues args = new ContentValues();
        args.put(CS_SEQ_TYPE_ID, seqTypeId);
        args.put(CS_VEHICLE_TYPE_ID, vehicleTypeId);
        args.put(CS_SOC_START, socStart);
        args.put(CS_SOC_END, socEnd);
        args.put(CS_TIME_START, timeStart);
        args.put(CS_TIME_STOP, timeStop);
        args.put(CS_ACTIVE, active);
        args.put(CS_CHARGING_TYPE_ID, chargingType);
        return this.mDb.insert(TABLE_CHARGESEQ, null, args);
    }

    /**
     * Delete the ChargeSequence with the given rowId
     * No sub-objects (VehicleType, ChargingType) will be considered.
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteChargeSequence(long rowId) {

        return this.mDb.delete(TABLE_CHARGESEQ, CS_ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all ChargeSequences including
     * the corresponding VehicleType and ChargingType.
     * 
     * @return Cursor over all ChargeSequences
     */
    public Cursor getAllChargeSequences() {
    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	
    	queryBuilder.setTables(
    			TABLE_CHARGESEQ + " LEFT OUTER JOIN " + TABLE_VEHICLETYPE + 
    			" ON " + CS_VEHICLE_TYPE_ID + " = " + VT_FULL_ID
    			 + " LEFT OUTER JOIN " + TABLE_CHARGINGTYPE + 
     			" ON " + CS_CHARGING_TYPE_ID + " = " + CT_FULL_ID
    			);
    	
    	return queryBuilder.query(mDb, null, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the ChargeSequence that matches the given rowId
     * @param rowId
     * @return Cursor positioned to matching ChargeSequence
     * 			(and the corresponding VehicleType/ChargingType) (if found) or empty cursor
     */
    public Cursor getChargeSequence(long rowId) {
    	
    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	
    	queryBuilder.setTables(
    			TABLE_CHARGESEQ + " LEFT OUTER JOIN " + TABLE_VEHICLETYPE + 
    			" ON " + CS_VEHICLE_TYPE_ID + " = " + VT_FULL_ID
    			 + " LEFT OUTER JOIN " + TABLE_CHARGINGTYPE + 
     			" ON " + CS_CHARGING_TYPE_ID + " = " + CT_FULL_ID
    			);
    	
    	Cursor mCursor = queryBuilder.query(mDb, null, CS_FULL_ID + "=" + rowId, null, null, null, null);

        return mCursor;
    }

    /**
     * Update the ChargeSequence.
     * No sub-objects (VehicleType, ChargingType) will be considered.
     * 
     * @param rowId
	 * @param seqTypeId
	 * @param vehicleTypeId
	 * @param socStart
	 * @param socEnd
	 * @param timeStart
	 * @param timeStop
	 * @param active
     * @param chargingTypeId
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateChargeSequence(long rowId, long seqTypeId, long vehicleTypeId, double socStart, double socEnd, 
    		long timeStart, long timeStop, int active, long chargingType) {
        ContentValues args = new ContentValues();
        args.put(CS_SEQ_TYPE_ID, seqTypeId);
        args.put(CS_VEHICLE_TYPE_ID, vehicleTypeId);
        args.put(CS_SOC_START, socStart);
        args.put(CS_SOC_END, socEnd);
        args.put(CS_TIME_START, timeStart);
        args.put(CS_TIME_STOP, timeStop);
        args.put(CS_ACTIVE, active);
        args.put(CS_CHARGING_TYPE_ID, chargingType);

        return this.mDb.update(TABLE_CHARGESEQ, args, CS_ROW_ID + "=" + rowId, null) >0; 
    }
}