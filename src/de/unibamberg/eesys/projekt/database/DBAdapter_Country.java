package de.unibamberg.eesys.projekt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * Database Adapter to provide the SQL related methods for
 * operating on the database table "Country".
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class DBAdapter_Country {
	public static final String ROW_ID = Table_Country.COLUMN_ID;
	public static final String NAME = Table_Country.COLUMN_NAME;

    private static final String TABLE_COUNTRY = Table_Country.TABLE_NAME;

    private SQLiteDatabase mDb;

    /**
     * Constructor - reference the provided DB
     * 
     * @param dbAdapterDB
     * 		the database opened by the DBInitializer
     */
    public DBAdapter_Country(SQLiteDatabase dbAdapterDB) {
        this.mDb = dbAdapterDB;
    }

    /**
     * Create a new Country. If the Country is successfully created return the new
     * rowId for that Country, otherwise return a -1 to indicate failure.
     * 
     * @param name
     * @return rowId or -1 if failed
     */
    public long createCountry(String name){
        ContentValues args = new ContentValues();
        args.put(NAME, name);
        return this.mDb.insert(TABLE_COUNTRY, null, args);
    }

    /**
     * Delete the Country with the given rowId
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteCountry(long rowId) {

        return this.mDb.delete(TABLE_COUNTRY, ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all Countries in the table
     * 
     * @return Cursor over all Countries
     */
    public Cursor getAllCountries() {

        return this.mDb.query(TABLE_COUNTRY,
        		new String[] {ROW_ID, NAME},
        		null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the Country that matches the given rowId
     * @param rowId
     * @return Cursor positioned to matching Country (if found) or empty cursor
     */
    public Cursor getCountry(long rowId) {

        Cursor mCursor =

        this.mDb.query(true, TABLE_COUNTRY, 
        		new String[] {ROW_ID, NAME },
        		ROW_ID + "=" + rowId, null, null, null, null, null);

        return mCursor;
    }

    /**
     * Update the Country.
     *
     * @param rowId
     * @param name
	 * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateCountry(long rowId, String name) {
        ContentValues args = new ContentValues();
        args.put(NAME, name);

        return this.mDb.update(TABLE_COUNTRY, args, ROW_ID + "=" + rowId, null) >0; 
    }

}