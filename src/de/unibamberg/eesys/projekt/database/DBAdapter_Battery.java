package de.unibamberg.eesys.projekt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Database Adapter to provide the SQL related methods for
 * operating on the database table "Battery".
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class DBAdapter_Battery{
	public static final String ROW_ID = Table_Battery.COLUMN_ID;
	public static final String CURRENT_SOC = Table_Battery.COLUMN_CURRENT_SOC;
	public static final String CHARGING = Table_Battery.COLUMN_CHARGING;

    private static final String TABLE_BATTERY = Table_Battery.TABLE_NAME;

    private SQLiteDatabase mDb;

    /**
     * Constructor - reference the provided DB
     * 
     * @param dbAdapterDB
     * 		the database opened by the DBInitializer
     */
    public DBAdapter_Battery(SQLiteDatabase dbAdapterDB) {
        this.mDb = dbAdapterDB;
    }

    /**
     * - replaced by "replaceBattery" -
     * Create a new Battery. If the Battery is successfully created return the new
     * rowId for that Battery, otherwise return a -1 to indicate failure.
     * 
     * @param currentSoc
     * @param charging
     * @return rowId or -1 if failed
     */
//    public long createBattery(double currentSoc, int charging) {
//        ContentValues args = new ContentValues();
//        args.put(CURRENT_SOC, currentSoc);
//        args.put(CHARGING, charging);
//        return this.mDb.insert(TABLE_BATTERY, null, args);
//    }

    /**
     * Delete the Battery with the given rowId
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteBattery(long rowId) {

        return this.mDb.delete(TABLE_BATTERY, ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * - not needed in current implementation (but for multiple eCars) -
     * Return a Cursor over the list of all Batteries in the table
     * 
     * @return Cursor over all Batteries
     */
//    public Cursor getAllBatteries() {
//
//        return this.mDb.query(TABLE_BATTERY,
//        		new String[] {ROW_ID, CURRENT_SOC, CHARGING},
//        		null, null, null, null, null);
//    }

    /**
     * Return a Cursor positioned at the Battery that matches the given rowId
     * @param rowId
     * @return Cursor positioned to matching Battery (if found) or empty cursor
     */
    public Cursor getBattery() {

        Cursor mCursor =

        this.mDb.query(true, TABLE_BATTERY, 
        		new String[] {ROW_ID, CURRENT_SOC, CHARGING },
        		ROW_ID + "= 1", null, null, null, null, null);

        return mCursor;
    }

    /**
     * - replaced by "replaceBattery" -
     * Update the Battery.
     *
     * @param rowId
     * @param currentSoc
	 * @param charging
	 * @return true if the note was successfully updated, false otherwise
     */
//    public boolean updateBattery(long rowId, double currentSoc, int charging) {
//        ContentValues args = new ContentValues();
//        args.put(CURRENT_SOC, currentSoc);
//        args.put(CHARGING, charging);
//
//        return this.mDb.update(TABLE_BATTERY, args, ROW_ID + "=" + rowId, null) >0; 
//    }

    /**
     * Replace the Battery.
     * Inserts a new Battery or updates it, if it exists.
     *
     * @param currentSoc
	 * @param charging
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long replaceBattery(double currentSoc, int charging) {
        ContentValues args = new ContentValues();
        args.put(ROW_ID, 1);
        args.put(CURRENT_SOC, currentSoc);
        args.put(CHARGING, charging);

        return this.mDb.replace(TABLE_BATTERY, null, args); 
    }

}