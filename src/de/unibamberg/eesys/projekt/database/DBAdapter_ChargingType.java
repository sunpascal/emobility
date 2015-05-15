package de.unibamberg.eesys.projekt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * Database Adapter to provide the SQL related methods for
 * operating on the database table "ChargingType".
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class DBAdapter_ChargingType {
	public static final String ROW_ID = Table_ChargingType.COLUMN_ID;
	public static final String NAME = Table_ChargingType.COLUMN_NAME;
	public static final String DESCRIPTION = Table_ChargingType.COLUMN_DESCRIPTION;
	public static final String CHARGING_POWER = Table_ChargingType.COLUMN_CHARGING_POWER;
	
    private static final String TABLE_CHARGINGTYPE = Table_ChargingType.TABLE_NAME;

    private final SQLiteDatabase mDb;

    /**
     * Constructor - reference the provided DB
     * 
     * @param dbAdapterDB
     * 		the database opened by the DBInitializer
     */
    public DBAdapter_ChargingType(SQLiteDatabase dbAdapterDB) {
        this.mDb = dbAdapterDB;
    }
    
    /**
     * Create a new ChargingType. If the ChargingType is successfully created return the new
     * rowId for that ChargingType, otherwise return a -1 to indicate failure.
     * 
     * @param name
     * @param description
     * @param chargingPower
     * @return rowId or -1 if failed
     */
    public long createChargingType(String name, String description, double chargingPower) {
        ContentValues args = new ContentValues();
        args.put(NAME, name);
        args.put(DESCRIPTION, description);
        args.put(CHARGING_POWER, chargingPower);
        return this.mDb.insert(TABLE_CHARGINGTYPE, null, args);
    }

    /**
     * Delete the ChargingType with the given rowId
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteChargingType(long rowId) {

        return this.mDb.delete(TABLE_CHARGINGTYPE, ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all ChargingTypes in the table
     * 
     * @return Cursor over all ChargingTypes
     */
    public Cursor getAllChargingTypes() {

        return this.mDb.query(TABLE_CHARGINGTYPE,
        		new String[] {ROW_ID, NAME, DESCRIPTION, CHARGING_POWER },
        		null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the ChargingType that matches the given rowId
     * @param rowId
     * @return Cursor positioned to matching ChargingType (if found) or empty cursor
     */
    public Cursor getChargingType(long rowId) {

        Cursor mCursor =

        this.mDb.query(true, TABLE_CHARGINGTYPE, 
        		new String[] {ROW_ID, NAME, DESCRIPTION, CHARGING_POWER },
        		ROW_ID + "=" + rowId, null, null, null, null, null);

        return mCursor;
    }

    /**
     * Update the ChargingType.
     * 
     * @param rowId
     * @param name
     * @param description
     * @param chargingPower
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateChargingType(long rowId, String name, String description, double chargingPower) {
        ContentValues args = new ContentValues();
        args.put(NAME, name);
        args.put(DESCRIPTION, description);
        args.put(CHARGING_POWER, chargingPower);

        return this.mDb.update(TABLE_CHARGINGTYPE, args, ROW_ID + "=" + rowId, null) >0; 
    }

}